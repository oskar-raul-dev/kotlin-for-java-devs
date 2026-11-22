package dev.raul.oskar.learning.kotlin.jdbc

/**
 * Lo que cambia entre motores. Todo lo demás (PreparedStatement, ResultSet, transacciones)
 * es JDBC estándar y vive en LedgerRepository.
 */
interface Dialect {
    val name: String

    /** DDL de las tablas, en orden de creación: autoincremento, tipos decimales y CHECK cambian por motor. */
    val createTables: List<String>

    /** Upsert de una tasa de cambio: ninguno de los tres usa el MERGE estándar con la misma sintaxis. */
    val upsertRate: String

    /** Aislamiento por defecto de una conexión nueva (constantes Connection.TRANSACTION_*). */
    val defaultIsolation: Int

    /**
     * Si el motor rechaza por sí solo el "leer saldo, restar en la app, escribir" de dos transacciones a la vez.
     * PostgreSQL (READ COMMITTED) y MySQL (REPEATABLE READ) no: la segunda escritura pisa a la primera.
     */
    val refusesLostUpdate: Boolean

    /** Sentencias que hay que correr en cada conexión nueva (SQLite: activar las foreign keys). */
    val sessionSetup: List<String> get() = emptyList()

    /** En orden inverso a las foreign keys. */
    val dropTables: List<String>
        get() = listOf("posting", "journal_entry", "account", "fx_rate").map { "DROP TABLE IF EXISTS $it" }
}
