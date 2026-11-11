package dev.raul.oskar.learning.kotlin.jdbc.postgres

import dev.raul.oskar.learning.kotlin.jdbc.DbConfig
import dev.raul.oskar.learning.kotlin.jdbc.Dialect

object PostgresDialect : Dialect {
    override val name = "PostgreSQL"

    // IDENTITY es el autoincremento estándar SQL (preferible a SERIAL). NUMERIC(12,2) es exacto.
    override val createTable = """
        CREATE TABLE product (
            id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            sku   VARCHAR(32)   NOT NULL UNIQUE,
            name  VARCHAR(200)  NOT NULL,
            price NUMERIC(12,2) NOT NULL,
            stock INTEGER       NOT NULL CHECK (stock >= 0)
        )
    """.trimIndent()

    override val upsert = """
        INSERT INTO product (sku, name, price, stock) VALUES (?, ?, ?, ?)
        ON CONFLICT (sku) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, stock = EXCLUDED.stock
    """.trimIndent()
}

/** Valores por defecto = docker/postgres/compose.yaml. Se cambian con PG_URL, PG_USER, PG_PASSWORD. */
val postgresConfig = DbConfig.fromEnv("PG", "jdbc:postgresql://localhost:15432/learning", "learning", "learning")
