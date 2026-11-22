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
        conn.prepareStatement("UPDATE account SET balance = balance + ? WHERE id = ? RETURNING id, balance").use { ps ->
            ps.setBigDecimal(1, java.math.BigDecimal("1.23"))
            ps.setString(2, "ACC-00001234-82")
            println("RETURNING (intereses) -> ${ps.executeQuery().mapRows { "${it.getString(1)}=${it.getBigDecimal(2)}" }}")
        }
        // Arrays nativos: = ANY(?) con un java.sql.Array en vez de armar un IN (?, ?, ?).
        conn.prepareStatement("SELECT id FROM account WHERE id = ANY(?) ORDER BY id").use { ps ->
            ps.setArray(1, conn.createArrayOf("varchar", arrayOf("ACC-00001234-82", "ACC-00005678-39", "ACC-00000000-00")))
            println("= ANY(array) -> ${ps.executeQuery().mapRows { it.getString(1) }}")
        }
        // JSONB y generate_series
        conn.createStatement().use { st ->
            val rs = st.executeQuery("""SELECT jsonb_build_object('id', id, 'balance', balance)::text FROM account ORDER BY id""")
            println("jsonb -> ${rs.mapRows { it.getString(1) }}")
            println("generate_series -> ${st.executeQuery("SELECT g FROM generate_series(1, 5) g").mapRows { it.getInt(1) }}")
        }
    }
}
