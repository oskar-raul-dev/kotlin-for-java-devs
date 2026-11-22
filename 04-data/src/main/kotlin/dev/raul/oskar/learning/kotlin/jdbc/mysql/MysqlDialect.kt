package dev.raul.oskar.learning.kotlin.jdbc.mysql

import dev.raul.oskar.learning.kotlin.jdbc.DbConfig
import dev.raul.oskar.learning.kotlin.jdbc.Dialect
import java.sql.Connection

object MysqlDialect : Dialect {
    override val name = "MySQL"

    // AUTO_INCREMENT; DECIMAL(14,2) exacto. Los CHECK se respetan desde MySQL 8.0.16 (antes se ignoraban).
    // InnoDB es obligatorio para transacciones y foreign keys (es el motor por defecto).
    // Ojo: MySQL IGNORA un REFERENCES escrito en la columna; la FOREIGN KEY tiene que ir a nivel de tabla.
    override val createTables = listOf(
        """
        CREATE TABLE account (
            id       VARCHAR(20)   PRIMARY KEY,
            currency CHAR(3)       NOT NULL,
            balance  DECIMAL(14,2) NOT NULL CHECK (balance >= 0),
            status   VARCHAR(10)   NOT NULL DEFAULT 'OPEN'
        ) ENGINE = InnoDB
        """,
        """
        CREATE TABLE journal_entry (
            id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
            idempotency_key VARCHAR(64)  NOT NULL UNIQUE,
            description     VARCHAR(200) NOT NULL,
            booked_on       DATE         NOT NULL
        ) ENGINE = InnoDB
        """,
        """
        CREATE TABLE posting (
            id         BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
            entry_id   BIGINT        NOT NULL,
            account_id VARCHAR(20)   NOT NULL,
            direction  CHAR(1)       NOT NULL CHECK (direction IN ('D', 'C')),
            amount     DECIMAL(14,2) NOT NULL CHECK (amount > 0),
            FOREIGN KEY (entry_id) REFERENCES journal_entry (id),
            FOREIGN KEY (account_id) REFERENCES account (id)
        ) ENGINE = InnoDB
        """,
        "CREATE TABLE fx_rate (currency CHAR(3) PRIMARY KEY, rate DECIMAL(12,4) NOT NULL) ENGINE = InnoDB",
    ).map { it.trimIndent() }

    // ON DUPLICATE KEY UPDATE con alias de fila (8.0.19+); VALUES(col) está obsoleto.
    override val upsertRate = """
        INSERT INTO fx_rate (currency, rate) VALUES (?, ?) AS new
        ON DUPLICATE KEY UPDATE rate = new.rate
    """.trimIndent()

    override val defaultIsolation = Connection.TRANSACTION_REPEATABLE_READ
    override val refusesLostUpdate = false
}

/** Valores por defecto = docker/mysql/compose.yaml. Se cambian con MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD. */
val mysqlConfig = DbConfig.fromEnv("MYSQL", "jdbc:mysql://localhost:23306/learning", "learning", "learning")
