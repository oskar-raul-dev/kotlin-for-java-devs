package dev.raul.oskar.learning.kotlin.jdbc.postgres

import dev.raul.oskar.learning.kotlin.jdbc.DbConfig
import dev.raul.oskar.learning.kotlin.jdbc.Dialect
import java.sql.Connection

object PostgresDialect : Dialect {
    override val name = "PostgreSQL"

    // IDENTITY es el autoincremento estándar SQL (preferible a SERIAL). NUMERIC(14,2) es exacto.
    override val createTables = listOf(
        """
        CREATE TABLE account (
            id       VARCHAR(20)   PRIMARY KEY,
            currency CHAR(3)       NOT NULL,
            balance  NUMERIC(14,2) NOT NULL CHECK (balance >= 0),
            status   VARCHAR(10)   NOT NULL DEFAULT 'OPEN'
        )
        """,
        """
        CREATE TABLE journal_entry (
            id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            idempotency_key VARCHAR(64)  NOT NULL UNIQUE,
            description     VARCHAR(200) NOT NULL,
            booked_on       DATE         NOT NULL
        )
        """,
        """
        CREATE TABLE posting (
            id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            entry_id   BIGINT        NOT NULL,
            account_id VARCHAR(20)   NOT NULL,
            direction  CHAR(1)       NOT NULL CHECK (direction IN ('D', 'C')),
            amount     NUMERIC(14,2) NOT NULL CHECK (amount > 0),
            FOREIGN KEY (entry_id) REFERENCES journal_entry (id),
            FOREIGN KEY (account_id) REFERENCES account (id)
        )
        """,
        "CREATE TABLE fx_rate (currency CHAR(3) PRIMARY KEY, rate NUMERIC(12,4) NOT NULL)",
    ).map { it.trimIndent() }

    override val upsertRate = """
        INSERT INTO fx_rate (currency, rate) VALUES (?, ?)
        ON CONFLICT (currency) DO UPDATE SET rate = EXCLUDED.rate
    """.trimIndent()

    override val defaultIsolation = Connection.TRANSACTION_READ_COMMITTED
    override val refusesLostUpdate = false
}

/** Valores por defecto = docker/postgres/compose.yaml. Se cambian con PG_URL, PG_USER, PG_PASSWORD. */
val postgresConfig = DbConfig.fromEnv("PG", "jdbc:postgresql://localhost:25432/learning", "learning", "learning")
