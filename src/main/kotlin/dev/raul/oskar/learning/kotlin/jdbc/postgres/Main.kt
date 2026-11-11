package dev.raul.oskar.learning.kotlin.jdbc.postgres

import dev.raul.oskar.learning.kotlin.jdbc.mapRows
import dev.raul.oskar.learning.kotlin.jdbc.runDemo

// docker compose -f docker/postgres/compose.yaml up -d --wait
// ./gradlew run -Plesson=jdbc.postgres

fun main() {
    postgresConfig.connect().use { conn ->
        runDemo(conn, PostgresDialect)

        println("\n== Solo PostgreSQL ==")
        // RETURNING: la fila modificada vuelve en la misma sentencia.
        conn.prepareStatement("UPDATE product SET price = price * 1.10 WHERE sku = ? RETURNING sku, price").use { ps ->
            ps.setString(1, "LAP-1")
            println("RETURNING -> ${ps.executeQuery().mapRows { "${it.getString(1)}=${it.getBigDecimal(2)}" }}")
        }
        // Arrays nativos: = ANY(?) con un java.sql.Array en vez de armar un IN (?, ?, ?).
        conn.prepareStatement("SELECT sku FROM product WHERE sku = ANY(?) ORDER BY sku").use { ps ->
            ps.setArray(1, conn.createArrayOf("varchar", arrayOf("LAP-1", "KEY-1", "NOPE")))
            println("= ANY(array) -> ${ps.executeQuery().mapRows { it.getString(1) }}")
        }
        // JSONB y generate_series
        conn.createStatement().use { st ->
            val rs = st.executeQuery("""SELECT jsonb_build_object('sku', sku, 'stock', stock)::text FROM product ORDER BY sku LIMIT 2""")
            println("jsonb -> ${rs.mapRows { it.getString(1) }}")
            println("generate_series -> ${st.executeQuery("SELECT g FROM generate_series(1, 5) g").mapRows { it.getInt(1) }}")
        }
    }
}
