package dev.raul.oskar.learning.kotlin.jdbc.mysql

import dev.raul.oskar.learning.kotlin.jdbc.ProductRepositoryContract
import java.sql.Connection

/** Requiere docker/mysql/compose.yaml arriba; si no, las pruebas quedan SKIPPED. */
class MysqlRepositoryTest : ProductRepositoryContract() {
    override val dialect = MysqlDialect
    override fun openConnection(): Connection? = if (mysqlConfig.isReachable()) mysqlConfig.connect() else null
}
