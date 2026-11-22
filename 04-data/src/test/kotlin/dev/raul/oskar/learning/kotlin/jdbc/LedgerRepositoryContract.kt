package dev.raul.oskar.learning.kotlin.jdbc

import java.math.BigDecimal
import java.sql.Connection
import java.sql.SQLException
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Las mismas pruebas para los tres motores. Cada subclase solo dice cómo conectarse.
 * Si devuelve null (base no disponible), la prueba se marca como SKIPPED, no como fallida.
 * openConnection() puede llamarse más de una vez: la segunda conexión tiene que ver la MISMA base.
 */
abstract class LedgerRepositoryContract {

    protected abstract val dialect: Dialect
    protected abstract fun openConnection(): Connection?

    private lateinit var conn: Connection
    private lateinit var repo: LedgerRepository
    private val extra = mutableListOf<Connection>()

    private val day = LocalDate.of(2026, 10, 7)
    private val ana = "ACC-00001234-82"
    private val bea = "ACC-00005678-39"

    @BeforeTest
    fun setUp() {
        val c = openConnection()
        org.junit.jupiter.api.Assumptions.assumeTrue(c != null, "${dialect.name} no disponible: levanta su docker compose")
        conn = c!!
        repo = LedgerRepository(conn, dialect).also { it.recreateSchema() }
    }

    @AfterTest
    fun tearDown() {
        extra.forEach { it.close() }
        if (::conn.isInitialized) conn.close()
    }

    private fun second(): Connection = openConnection()!!.also { extra += it }

    private fun twoAccounts() = repo.openAccounts(
        listOf(AccountRow(ana, "USD", money("1000.00")), AccountRow(bea, "USD", money("250.00"))),
    )

    @Test
    fun `transfer returns the generated entry id`() {
        twoAccounts()
        val first = repo.transfer("k-1", ana, bea, money("10.00"), "Pago 1", day)
        val second = repo.transfer("k-2", ana, bea, money("20.00"), "Pago 2", day)
        assertTrue(second > first)
        assertEquals(listOf(first, second), repo.movements(bea).map { it.entryId })
    }

    @Test
    fun `batch insert and queries`() {
        twoAccounts()
        assertEquals(listOf(ana, bea), repo.accounts().map { it.id })
        assertEquals(AccountRow(ana, "USD", money("1000.00")), repo.findAccount(ana))
        assertNull(repo.findAccount("ACC-00000000-00"))
        assertEquals(money("1250.00"), repo.totalBalance("USD"))
    }

    @Test
    fun `sum over no rows is zero not null`() {
        assertEquals(money("0"), repo.totalBalance("USD"))
    }

    @Test
    fun `parameters are not sql injection`() {
        twoAccounts()
        assertNull(repo.findAccount("x' OR '1'='1"))
        assertEquals(2, repo.accounts().size)
    }

    @Test
    fun `a repeated idempotency key is rejected and changes nothing`() {
        twoAccounts()
        repo.transfer("k-1", ana, bea, money("10.00"), "Pago", day)
        assertFailsWith<SQLException> { repo.transfer("k-1", ana, bea, money("10.00"), "Pago otra vez", day) }
        assertEquals(money("990.00"), repo.findAccount(ana)?.balance)
        assertEquals(1, repo.movements(ana).size)
    }

    @Test
    fun `upsert inserts then updates`() {
        repo.upsertRate("EUR", BigDecimal("0.9215"))
        repo.upsertRate("EUR", BigDecimal("0.9208"))
        assertEquals(1, repo.rateCount())
        assertEquals(BigDecimal("0.9208"), repo.rate("EUR"))
    }

    @Test
    fun `update and delete report affected rows`() {
        twoAccounts()
        assertTrue(repo.freeze(bea))
        assertFalse(repo.freeze("ACC-00000000-00"))
        assertTrue(repo.deleteAccount(bea))
        assertFalse(repo.deleteAccount(bea))
    }

    @Test
    fun `a transfer writes the entry, both postings and both balances`() {
        twoAccounts()
        repo.transfer("k-1", ana, bea, money("100.00"), "Transferencia a $bea", day)
        assertEquals(money("900.00"), repo.findAccount(ana)?.balance)
        assertEquals(money("350.00"), repo.findAccount(bea)?.balance)
        assertEquals(listOf('C'), repo.movements(ana).map { it.direction })
        assertEquals(listOf('D'), repo.movements(bea).map { it.direction })
        assertTrue(repo.unbalancedEntries().isEmpty())                 // débitos = créditos
        assertEquals(listOf("Transferencia a $bea"), repo.entriesMatching("TRANSFER"))
        assertFailsWith<SQLException> { repo.deleteAccount(ana) }     // con postings, la foreign key lo impide
    }

    @Test
    fun `check constraint failure rolls back the whole transfer`() {
        twoAccounts()
        assertFailsWith<SQLException> { repo.transfer("k-1", bea, ana, money("500.00"), "Sin fondos", day) }
        assertEquals(money("250.00"), repo.findAccount(bea)?.balance)
        assertEquals(money("1000.00"), repo.findAccount(ana)?.balance)  // el crédito de ana también se revirtió
        assertTrue(repo.movements(bea).isEmpty())                        // y el asiento con sus postings
        assertTrue(conn.autoCommit)
    }

    @Test
    fun `a frozen account cannot send`() {
        twoAccounts()
        repo.freeze(ana)
        assertFailsWith<SQLException> { repo.transfer("k-1", ana, bea, money("1.00"), "Congelada", day) }
        assertEquals(money("1000.00"), repo.findAccount(ana)?.balance)
    }

    @Test
    fun `the default isolation level depends on the engine`() {
        assertEquals(dialect.defaultIsolation, conn.transactionIsolation)
    }

    @Test
    fun `read-modify-write loses an update unless the engine refuses it`() {
        twoAccounts()
        val other = second()
        val otherRepo = LedgerRepository(other, dialect)
        conn.autoCommit = false
        other.autoCommit = false
        // Dos transacciones leen el mismo saldo (1000) y cada una escribe "su" resta, calculada en la app.
        val mine = repo.findAccount(ana)!!.balance
        val theirs = otherRepo.findAccount(ana)!!.balance
        val outcome = runCatching {
            repo.overwriteBalance(ana, mine - money("30.00"))
            conn.commit()
            otherRepo.overwriteBalance(ana, theirs - money("50.00"))
            other.commit()
        }
        runCatching { conn.rollback() }
        runCatching { other.rollback() }
        conn.autoCommit = true
        other.autoCommit = true
        if (dialect.refusesLostUpdate) {
            assertTrue(outcome.isFailure, "${dialect.name} debía rechazar una de las dos escrituras")
        } else {
            outcome.getOrThrow()
            assertEquals(money("950.00"), repo.findAccount(ana)?.balance)   // se perdió la resta de 30
        }
    }

    @Test
    fun `relative updates keep both withdrawals`() {
        twoAccounts()
        val otherRepo = LedgerRepository(second(), dialect)
        repo.transfer("k-1", ana, bea, money("30.00"), "Primera", day)
        otherRepo.transfer("k-2", ana, bea, money("50.00"), "Segunda", day)
        assertEquals(money("920.00"), repo.findAccount(ana)?.balance)
    }
}
