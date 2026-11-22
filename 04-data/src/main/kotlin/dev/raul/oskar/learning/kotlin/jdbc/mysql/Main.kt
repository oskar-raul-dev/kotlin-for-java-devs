package dev.raul.oskar.learning.kotlin.jdbc.mysql

import dev.raul.oskar.learning.kotlin.jdbc.mapRows
import dev.raul.oskar.learning.kotlin.jdbc.runDemo

// docker compose -f docker/mysql/compose.yaml up -d --wait
// ./gradlew run -Plesson=jdbc.mysql

fun main() {
    mysqlConfig.connect().use { conn ->
        runDemo(conn, MysqlDialect)

        println("\n== Solo MySQL ==")
        conn.createStatement().use { st ->
            // LAST_INSERT_ID(): la última clave generada en ESTA conexión.
            // Las transferencias fallidas de la demo también gastaron ids: el siguiente no es "filas + 1".
            st.executeUpdate("INSERT INTO journal_entry (idempotency_key, description, booked_on) VALUES ('k-x', 'Ajuste', '2026-10-08')")
            println("LAST_INSERT_ID() -> ${st.executeQuery("SELECT LAST_INSERT_ID()").mapRows { it.getLong(1) }}")
            // Nivel de aislamiento por defecto de InnoDB: REPEATABLE READ (en PostgreSQL es READ COMMITTED).
            println("aislamiento -> ${st.executeQuery("SELECT @@transaction_isolation").mapRows { it.getString(1) }}")
            // GROUP_CONCAT: el string_agg de PostgreSQL.
            println("GROUP_CONCAT -> ${st.executeQuery("SELECT GROUP_CONCAT(id ORDER BY id SEPARATOR '|') FROM account").mapRows { it.getString(1) }}")
        }
    }
}
