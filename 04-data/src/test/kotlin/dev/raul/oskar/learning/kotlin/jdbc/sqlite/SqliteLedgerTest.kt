package dev.raul.oskar.learning.kotlin.jdbc.sqlite

import dev.raul.oskar.learning.kotlin.jdbc.LedgerRepositoryContract
import java.sql.Connection
import java.sql.DriverManager
import kotlin.io.path.createTempFile

/**
 * SQLite en un archivo temporal: siempre corre, no necesita Docker. Un archivo y no :memory: porque el contrato abre
 * una segunda conexión que tiene que ver la misma base (cada conexión a :memory: es una base distinta).
 */
class SqliteLedgerTest : LedgerRepositoryContract() {
    private val file = createTempFile("ledger", ".db").also { it.toFile().deleteOnExit() }

    override val dialect = SqliteDialect
    override fun openConnection(): Connection = DriverManager.getConnection("jdbc:sqlite:$file")
}
