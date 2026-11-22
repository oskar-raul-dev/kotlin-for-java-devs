package dev.raul.oskar.learning.kotlin.jdbc

import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.time.LocalDate

/**
 * El ledger del Banco con JDBC "a mano", el mismo para SQLite, PostgreSQL y MySQL.
 * Reglas que se repasan aquí:
 *  - Siempre PreparedStatement con ? (nunca concatenar: inyección SQL).
 *  - `use {}` en cada Statement/ResultSet (try-with-resources).
 *  - Claves generadas con RETURN_GENERATED_KEYS.
 *  - Transacciones explícitas cuando hay más de una escritura que debe ser atómica.
 *  - Actualizaciones RELATIVAS (balance = balance - ?) en vez de "leer, restar en la app, escribir".
 */
class LedgerRepository(private val conn: Connection, private val dialect: Dialect) {

    init {
        conn.createStatement().use { st -> dialect.sessionSetup.forEach(st::execute) }
    }

    fun recreateSchema() {
        conn.createStatement().use { st ->
            dialect.dropTables.forEach(st::execute)
            dialect.createTables.forEach(st::execute)
        }
    }

    fun openAccount(account: AccountRow) {
        conn.prepareStatement("INSERT INTO account (id, currency, balance, status) VALUES (?, ?, ?, ?)").use { ps ->
            bindAccount(ps, account)
            ps.executeUpdate()
        }
    }

    /** Lote: un viaje de red para muchas filas, dentro de una transacción. */
    fun openAccounts(accounts: List<AccountRow>): IntArray = conn.inTransaction {
        prepareStatement("INSERT INTO account (id, currency, balance, status) VALUES (?, ?, ?, ?)").use { ps ->
            for (a in accounts) {
                bindAccount(ps, a)
                ps.addBatch()
            }
            ps.executeBatch()
        }
    }

    fun findAccount(id: String): AccountRow? =
        conn.prepareStatement("SELECT id, currency, balance, status FROM account WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().mapRows(::toAccount).firstOrNull()
        }

    fun accounts(): List<AccountRow> =
        conn.prepareStatement("SELECT id, currency, balance, status FROM account ORDER BY id").use { ps ->
            ps.executeQuery().mapRows(::toAccount)
        }

    /** Agregado: saldo total en una moneda. COALESCE porque SUM de cero filas es NULL. */
    fun totalBalance(currency: String): BigDecimal =
        conn.prepareStatement("SELECT COALESCE(SUM(balance), 0) FROM account WHERE currency = ?").use { ps ->
            ps.setString(1, currency)
            ps.executeQuery().mapRows { amount(it.getString(1)) }.single()
        }

    /**
     * Transferencia como UN asiento de doble partida: el asiento, sus dos postings y los dos saldos, todo o nada.
     * Si el origen queda negativo, el CHECK (balance >= 0) falla y se revierte todo, también el asiento ya insertado.
     * La clave de idempotencia es UNIQUE: repetirla lanza SQLException (la idempotencia de verdad es un ejercicio).
     * Devuelve el id generado del asiento.
     */
    fun transfer(key: String, from: String, to: String, amount: BigDecimal, description: String, bookedOn: LocalDate): Long =
        conn.inTransaction {
            val entryId = prepareStatement(
                "INSERT INTO journal_entry (idempotency_key, description, booked_on) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS,
            ).use { ps ->
                ps.setString(1, key)
                ps.setString(2, description)
                ps.setDate(3, Date.valueOf(bookedOn))
                ps.executeUpdate()
                ps.generatedKeys.use { keys ->
                    check(keys.next()) { "la base no devolvió la clave generada" }
                    keys.getLong(1)
                }
            }
            prepareStatement("INSERT INTO posting (entry_id, account_id, direction, amount) VALUES (?, ?, ?, ?)").use { ps ->
                for ((account, direction) in listOf(from to "C", to to "D")) {
                    ps.setLong(1, entryId)
                    ps.setString(2, account)
                    ps.setString(3, direction)
                    ps.setBigDecimal(4, amount)
                    ps.addBatch()
                }
                ps.executeBatch()
            }
            // Relativa: la base resta sobre el valor actual de la fila, que bloquea hasta el commit.
            // (En SQLite los montos son TEXT y esta resta se calcula en REAL; amount() redondea al leer.)
            prepareStatement("UPDATE account SET balance = balance + ? WHERE id = ? AND status = 'OPEN'").use { ps ->
                ps.setBigDecimal(1, amount.negate()); ps.setString(2, from)
                if (ps.executeUpdate() != 1) throw SQLException("cuenta de origen inexistente o no abierta: $from")
                ps.setBigDecimal(1, amount); ps.setString(2, to)
                if (ps.executeUpdate() != 1) throw SQLException("cuenta de destino inexistente o no abierta: $to")
            }
            entryId
        }

    /** Los movimientos de una cuenta, con JOIN al asiento. */
    fun movements(accountId: String): List<MovementRow> =
        conn.prepareStatement(
            """
            SELECT e.id, e.description, p.direction, p.amount
            FROM posting p JOIN journal_entry e ON e.id = p.entry_id
            WHERE p.account_id = ? ORDER BY e.id
            """.trimIndent(),
        ).use { ps ->
            ps.setString(1, accountId)
            ps.executeQuery().mapRows { MovementRow(it.getLong(1), it.getString(2), it.getString(3).single(), amount(it.getString(4))) }
        }

    /** LIKE con parámetro: el % va en el valor, no en el SQL. */
    fun entriesMatching(fragment: String): List<String> =
        conn.prepareStatement("SELECT description FROM journal_entry WHERE LOWER(description) LIKE ? ORDER BY id").use { ps ->
            ps.setString(1, "%${fragment.lowercase()}%")
            ps.executeQuery().mapRows { it.getString(1) }
        }

    /** Invariante de la doble partida: los asientos cuyos débitos no igualan a sus créditos (debería estar vacía). */
    fun unbalancedEntries(): List<Long> =
        conn.createStatement().use { st ->
            st.executeQuery(
                """
                SELECT entry_id FROM posting GROUP BY entry_id
                HAVING SUM(CASE WHEN direction = 'D' THEN amount ELSE 0 END)
                    <> SUM(CASE WHEN direction = 'C' THEN amount ELSE 0 END)
                """.trimIndent(),
            ).mapRows { it.getLong(1) }
        }

    fun freeze(id: String): Boolean =
        conn.prepareStatement("UPDATE account SET status = 'FROZEN' WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeUpdate() == 1
        }

    /** Solo se puede borrar una cuenta sin movimientos: con postings, la foreign key lo impide (SQLException). */
    fun deleteAccount(id: String): Boolean =
        conn.prepareStatement("DELETE FROM account WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeUpdate() == 1
        }

    /** Inserta o actualiza la tasa de una moneda: la sintaxis la pone el dialecto. */
    fun upsertRate(currency: String, rate: BigDecimal) {
        conn.prepareStatement(dialect.upsertRate).use { ps ->
            ps.setString(1, currency)
            ps.setBigDecimal(2, rate)
            ps.executeUpdate()
        }
    }

    fun rate(currency: String): BigDecimal? =
        conn.prepareStatement("SELECT rate FROM fx_rate WHERE currency = ?").use { ps ->
            ps.setString(1, currency)
            ps.executeQuery().mapRows { BigDecimal(it.getString(1)).setScale(4, RoundingMode.HALF_EVEN) }.firstOrNull()
        }

    fun rateCount(): Int = conn.createStatement().use { st ->
        st.executeQuery("SELECT COUNT(*) FROM fx_rate").mapRows { it.getInt(1) }.single()
    }

    /** Escritura ABSOLUTA del saldo: solo para mostrar la actualización perdida (ver el contrato de pruebas). */
    fun overwriteBalance(id: String, balance: BigDecimal) {
        conn.prepareStatement("UPDATE account SET balance = ? WHERE id = ?").use { ps ->
            ps.setBigDecimal(1, balance)
            ps.setString(2, id)
            ps.executeUpdate()
        }
    }

    private fun bindAccount(ps: java.sql.PreparedStatement, a: AccountRow) {
        ps.setString(1, a.id)
        ps.setString(2, a.currency)
        ps.setBigDecimal(3, a.balance)
        ps.setString(4, a.status)
    }

    // getString + BigDecimal: igual en los tres motores (SQLite no tiene tipo decimal real).
    private fun amount(text: String): BigDecimal = BigDecimal(text).setScale(2, RoundingMode.HALF_EVEN)

    private fun toAccount(rs: ResultSet) = AccountRow(
        id = rs.getString("id"),
        currency = rs.getString("currency").trim(),
        balance = amount(rs.getString("balance")),
        status = rs.getString("status"),
    )
}
