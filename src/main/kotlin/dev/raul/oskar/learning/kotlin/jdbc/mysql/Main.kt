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
            // Sale mayor que el número de filas: cada upsert que terminó en UPDATE también gastó un id.
            st.executeUpdate("INSERT INTO product (sku, name, price, stock) VALUES ('CAB-1', 'Cable USB-C', 9.90, 100)")
            println("LAST_INSERT_ID() -> ${st.executeQuery("SELECT LAST_INSERT_ID()").mapRows { it.getLong(1) }}")
            // Nivel de aislamiento por defecto de InnoDB: REPEATABLE READ (en PostgreSQL es READ COMMITTED).
            println("aislamiento -> ${st.executeQuery("SELECT @@transaction_isolation").mapRows { it.getString(1) }}")
            // GROUP_CONCAT: el string_agg de PostgreSQL.
            println("GROUP_CONCAT -> ${st.executeQuery("SELECT GROUP_CONCAT(sku ORDER BY sku SEPARATOR '|') FROM product").mapRows { it.getString(1) }}")
        }
    }
}
