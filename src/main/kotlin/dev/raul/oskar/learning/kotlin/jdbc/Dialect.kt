package dev.raul.oskar.learning.kotlin.jdbc

/**
 * Lo que cambia entre motores. Todo lo demás (PreparedStatement, ResultSet, transacciones)
 * es JDBC estándar y vive en ProductRepository.
 */
interface Dialect {
    val name: String

    /** DDL de la tabla: autoincremento, tipos decimales y CHECK cambian por motor. */
    val createTable: String

    /** Upsert por sku: ninguno de los tres usa el MERGE estándar con la misma sintaxis. */
    val upsert: String

    val dropTable: String get() = "DROP TABLE IF EXISTS product"
}
