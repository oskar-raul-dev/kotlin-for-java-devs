package dev.raul.oskar.learning.kotlin.jdbc.sqlite

import dev.raul.oskar.learning.kotlin.jdbc.Dialect

object SqliteDialect : Dialect {
    override val name = "SQLite"

    // SQLite: tipado dinámico ("afinidad"). No hay DECIMAL: el precio se guarda como TEXT
    // para no perder exactitud (un REAL convertiría 0.1 en 0.1000000000000000055...).
    override val createTable = """
        CREATE TABLE product (
            id    INTEGER PRIMARY KEY AUTOINCREMENT,
            sku   TEXT    NOT NULL UNIQUE,
            name  TEXT    NOT NULL,
            price TEXT    NOT NULL,
            stock INTEGER NOT NULL CHECK (stock >= 0)
        )
    """.trimIndent()

    // ON CONFLICT ... DO UPDATE (desde SQLite 3.24, misma idea que PostgreSQL); excluded = la fila propuesta
    override val upsert = """
        INSERT INTO product (sku, name, price, stock) VALUES (?, ?, ?, ?)
        ON CONFLICT (sku) DO UPDATE SET name = excluded.name, price = excluded.price, stock = excluded.stock
    """.trimIndent()
}
