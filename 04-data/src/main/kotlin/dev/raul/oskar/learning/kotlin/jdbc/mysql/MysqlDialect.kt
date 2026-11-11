package dev.raul.oskar.learning.kotlin.jdbc.mysql

import dev.raul.oskar.learning.kotlin.jdbc.DbConfig
import dev.raul.oskar.learning.kotlin.jdbc.Dialect

object MysqlDialect : Dialect {
    override val name = "MySQL"

    // AUTO_INCREMENT; DECIMAL(12,2) exacto. Los CHECK se respetan desde MySQL 8.0.16 (antes se ignoraban).
    // InnoDB es obligatorio para transacciones (es el motor por defecto).
    override val createTable = """
        CREATE TABLE product (
            id    BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
            sku   VARCHAR(32)   NOT NULL UNIQUE,
            name  VARCHAR(200)  NOT NULL,
            price DECIMAL(12,2) NOT NULL,
            stock INT           NOT NULL CHECK (stock >= 0)
        ) ENGINE = InnoDB
    """.trimIndent()

    // ON DUPLICATE KEY UPDATE con alias de fila (8.0.19+); VALUES(col) está obsoleto.
    override val upsert = """
        INSERT INTO product (sku, name, price, stock) VALUES (?, ?, ?, ?) AS new
        ON DUPLICATE KEY UPDATE name = new.name, price = new.price, stock = new.stock
    """.trimIndent()
}

/** Valores por defecto = docker/mysql/compose.yaml. Se cambian con MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD. */
val mysqlConfig = DbConfig.fromEnv("MYSQL", "jdbc:mysql://localhost:13306/learning", "learning", "learning")
