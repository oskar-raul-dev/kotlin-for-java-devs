package dev.raul.oskar.learning.kotlin.jdbc

import java.math.RoundingMode
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * Repositorio JDBC "a mano", el mismo para SQLite, PostgreSQL y MySQL.
 * Reglas que se repasan aquí:
 *  - Siempre PreparedStatement con ? (nunca concatenar: inyección SQL).
 *  - `use {}` en cada Statement/ResultSet (try-with-resources).
 *  - Claves generadas con RETURN_GENERATED_KEYS.
 *  - Transacciones explícitas cuando hay más de una escritura que debe ser atómica.
 */
class ProductRepository(private val conn: Connection, private val dialect: Dialect) {

    fun recreateSchema() {
        conn.createStatement().use { st ->
            st.execute(dialect.dropTable)
            st.execute(dialect.createTable)
        }
    }

    fun insert(p: Product): Product {
        val sql = "INSERT INTO product (sku, name, price, stock) VALUES (?, ?, ?, ?)"
        conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { ps ->
            ps.setString(1, p.sku)
            ps.setString(2, p.name)
            ps.setBigDecimal(3, p.price)
            ps.setInt(4, p.stock)
            ps.executeUpdate()
            ps.generatedKeys.use { keys ->
                check(keys.next()) { "la base no devolvió la clave generada" }
                return p.copy(id = keys.getLong(1))
            }
        }
    }

    /** Lote: un viaje de red para muchas filas, dentro de una transacción. */
    fun insertAll(products: List<Product>): IntArray = conn.inTransaction {
        prepareStatement("INSERT INTO product (sku, name, price, stock) VALUES (?, ?, ?, ?)").use { ps ->
            for (p in products) {
                ps.setString(1, p.sku)
                ps.setString(2, p.name)
                ps.setBigDecimal(3, p.price)
                ps.setInt(4, p.stock)
                ps.addBatch()
            }
            ps.executeBatch()
        }
    }

    fun findBySku(sku: String): Product? =
        conn.prepareStatement("SELECT id, sku, name, price, stock FROM product WHERE sku = ?").use { ps ->
            ps.setString(1, sku)
            ps.executeQuery().mapRows(::toProduct).firstOrNull()
        }

    fun findAll(): List<Product> =
        conn.prepareStatement("SELECT id, sku, name, price, stock FROM product ORDER BY sku").use { ps ->
            ps.executeQuery().mapRows(::toProduct)
        }

    /** LIKE con parámetro: el % va en el valor, no en el SQL. */
    fun searchByName(fragment: String): List<Product> =
        conn.prepareStatement("SELECT id, sku, name, price, stock FROM product WHERE LOWER(name) LIKE ? ORDER BY sku").use { ps ->
            ps.setString(1, "%${fragment.lowercase()}%")
            ps.executeQuery().mapRows(::toProduct)
        }

    fun updatePrice(sku: String, price: java.math.BigDecimal): Boolean =
        conn.prepareStatement("UPDATE product SET price = ? WHERE sku = ?").use { ps ->
            ps.setBigDecimal(1, price)
            ps.setString(2, sku)
            ps.executeUpdate() == 1
        }

    fun delete(sku: String): Boolean =
        conn.prepareStatement("DELETE FROM product WHERE sku = ?").use { ps ->
            ps.setString(1, sku)
            ps.executeUpdate() == 1
        }

    /** Inserta o actualiza por sku: la sintaxis la pone el dialecto. */
    fun upsert(p: Product) {
        conn.prepareStatement(dialect.upsert).use { ps ->
            ps.setString(1, p.sku)
            ps.setString(2, p.name)
            ps.setBigDecimal(3, p.price)
            ps.setInt(4, p.stock)
            ps.executeUpdate()
        }
    }

    fun count(): Int = conn.createStatement().use { st ->
        st.executeQuery("SELECT COUNT(*) FROM product").mapRows { it.getInt(1) }.single()
    }

    /** Agregado: valor del inventario. COALESCE porque SUM de cero filas es NULL. */
    fun inventoryValue(): java.math.BigDecimal = conn.createStatement().use { st ->
        st.executeQuery("SELECT COALESCE(SUM(price * stock), 0) FROM product")
            .mapRows { java.math.BigDecimal(it.getString(1)).setScale(2, RoundingMode.HALF_EVEN) }
            .single()
    }

    /**
     * Mover stock entre dos productos: las dos UPDATE o ninguna.
     * Si el origen queda negativo, el CHECK (stock >= 0) de la tabla falla y todo se revierte.
     */
    fun moveStock(fromSku: String, toSku: String, qty: Int) = conn.inTransaction {
        prepareStatement("UPDATE product SET stock = stock + ? WHERE sku = ?").use { ps ->
            ps.setInt(1, qty); ps.setString(2, toSku); ps.executeUpdate()
            ps.setInt(1, -qty); ps.setString(2, fromSku)
            if (ps.executeUpdate() != 1) throw SQLException("sku inexistente: $fromSku")
        }
    }

    private fun toProduct(rs: ResultSet) = Product(
        id = rs.getLong("id"),
        sku = rs.getString("sku"),
        name = rs.getString("name"),
        // getString + BigDecimal: igual en los tres motores (SQLite no tiene tipo decimal real)
        price = java.math.BigDecimal(rs.getString("price")).setScale(2, RoundingMode.HALF_EVEN),
        stock = rs.getInt("stock"),
    )
}
