package dev.raul.oskar.learning.kotlin.jdbc.sqlite

import dev.raul.oskar.learning.kotlin.jdbc.Dialect
import java.sql.Connection

object SqliteDialect : Dialect {
    override val name = "SQLite"

    // SQLite: tipado dinámico ("afinidad"). No hay DECIMAL: los montos se guardan como TEXT
    // para no perder exactitud (un REAL convertiría 0.1 en 0.1000000000000000055...).
    // El CHECK compara como número gracias a CAST.
    override val createTables = listOf(
        """
        CREATE TABLE account (
            id       TEXT PRIMARY KEY,
            currency TEXT NOT NULL,
            balance  TEXT NOT NULL CHECK (CAST(balance AS REAL) >= 0),
            status   TEXT NOT NULL DEFAULT 'OPEN'
        )
        """,
        """
        CREATE TABLE journal_entry (
            id              INTEGER PRIMARY KEY AUTOINCREMENT,
            idempotency_key TEXT NOT NULL UNIQUE,
            description     TEXT NOT NULL,
            booked_on       TEXT NOT NULL
        )
        """,
        """
        CREATE TABLE posting (
            id         INTEGER PRIMARY KEY AUTOINCREMENT,
            entry_id   INTEGER NOT NULL,
            account_id TEXT    NOT NULL,
            direction  TEXT    NOT NULL CHECK (direction IN ('D', 'C')),
            amount     TEXT    NOT NULL CHECK (CAST(amount AS REAL) > 0),
            FOREIGN KEY (entry_id) REFERENCES journal_entry (id),
            FOREIGN KEY (account_id) REFERENCES account (id)
        )
        """,
        "CREATE TABLE fx_rate (currency TEXT PRIMARY KEY, rate TEXT NOT NULL)",
    ).map { it.trimIndent() }

    // ON CONFLICT ... DO UPDATE (desde SQLite 3.24, misma idea que PostgreSQL); excluded = la fila propuesta
    override val upsertRate = """
        INSERT INTO fx_rate (currency, rate) VALUES (?, ?)
        ON CONFLICT (currency) DO UPDATE SET rate = excluded.rate
    """.trimIndent()

    override val defaultIsolation = Connection.TRANSACTION_SERIALIZABLE

    // Las transacciones de SQLite son serializables: de dos escrituras sobre la misma foto, una falla (SQLITE_BUSY).
    override val refusesLostUpdate = true

    // En SQLite las foreign keys vienen APAGADAS en cada conexión: hay que activarlas.
    override val sessionSetup = listOf("PRAGMA foreign_keys = ON")
}
