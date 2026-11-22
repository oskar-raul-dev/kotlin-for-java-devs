package dev.raul.oskar.learning.kotlin.jdbc.postgres

import dev.raul.oskar.learning.kotlin.jdbc.LedgerRepositoryContract
import java.sql.Connection

/** Requiere docker/postgres/compose.yaml arriba; si no, las pruebas quedan SKIPPED. */
class PostgresLedgerTest : LedgerRepositoryContract() {
    override val dialect = PostgresDialect
    override fun openConnection(): Connection? = if (postgresConfig.isReachable()) postgresConfig.connect() else null
}
