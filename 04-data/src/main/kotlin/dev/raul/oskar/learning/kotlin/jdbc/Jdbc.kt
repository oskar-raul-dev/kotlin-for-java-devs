package dev.raul.oskar.learning.kotlin.jdbc

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

// Pequeñas extensiones que hacen JDBC menos verboso en Kotlin.

/** Recorre un ResultSet y mapea cada fila. `use` cierra el ResultSet al terminar. */
inline fun <T> ResultSet.mapRows(mapper: (ResultSet) -> T): List<T> = use {
    buildList { while (next()) add(mapper(this@mapRows)) }
}

/** Transacción: commit si el bloque termina, rollback si lanza. Restaura el autoCommit. */
inline fun <T> Connection.inTransaction(block: Connection.() -> T): T {
    val previous = autoCommit
    autoCommit = false
    try {
        val result = block()
        commit()
        return result
    } catch (e: Throwable) {
        rollback()
        throw e
    } finally {
        autoCommit = previous
    }
}

/** Datos de conexión leídos de variables de entorno, con valores por defecto del docker/compose. */
data class DbConfig(val url: String, val user: String?, val password: String?) {
    fun connect(): Connection = DriverManager.getConnection(url, user, password)

    /** ¿Hay alguien escuchando? Sirve para saltar pruebas si el contenedor no está arriba. */
    fun isReachable(): Boolean = try {
        connect().use { it.isValid(2) }
    } catch (e: SQLException) {
        false
    }

    companion object {
        fun fromEnv(prefix: String, defaultUrl: String, defaultUser: String, defaultPassword: String) = DbConfig(
            url = System.getenv("${prefix}_URL") ?: defaultUrl,
            user = System.getenv("${prefix}_USER") ?: defaultUser,
            password = System.getenv("${prefix}_PASSWORD") ?: defaultPassword,
        )
    }
}
