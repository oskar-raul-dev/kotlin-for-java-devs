package dev.raul.oskar.learning.kotlin.jdbc.sqlite

import dev.raul.oskar.learning.kotlin.jdbc.ProductRepositoryContract
import java.sql.Connection
import java.sql.DriverManager

/** SQLite en memoria: siempre corre, no necesita Docker. */
class SqliteRepositoryTest : ProductRepositoryContract() {
    override val dialect = SqliteDialect
    override fun openConnection(): Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
}
