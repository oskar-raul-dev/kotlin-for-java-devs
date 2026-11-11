package dev.raul.oskar.learning.kotlin.exceptions

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

// Kotlin NO tiene excepciones verificadas (checked): nadie obliga a capturar ni a declarar throws.
// Files.readString lanza IOException (checked en Java) y aquí compila sin try ni throws.

fun readOrNull(path: Path): String? = try {
    Files.readString(path)               // try es una EXPRESIÓN: su valor es el de la última línea
} catch (e: IOException) {
    null                                 // ...o el del catch
}

/** throw también es una expresión, de tipo Nothing: cabe en un Elvis o en una rama de when. */
fun portFrom(env: Map<String, String>): Int =
    env["PORT"]?.toIntOrNull() ?: throw IllegalArgumentException("PORT ausente o no numérico")

/**
 * Precondiciones de la stdlib (el mensaje es una lambda: solo se arma si falla):
 *   require(cond)        -> IllegalArgumentException  (argumento inválido)
 *   requireNotNull(x)    -> IllegalArgumentException, y devuelve x no nulo
 *   check(cond)          -> IllegalStateException     (estado inválido del objeto)
 *   checkNotNull(x)      -> IllegalStateException
 *   error("msg")         -> IllegalStateException, devuelve Nothing
 */
class Wallet(private var balance: Long) {
    var closed = false
        private set

    fun withdraw(amount: Long): Long {
        require(amount > 0) { "monto debe ser positivo: $amount" }
        check(!closed) { "billetera cerrada" }
        if (amount > balance) error("fondos insuficientes: saldo=$balance, pedido=$amount")
        balance -= amount
        return balance
    }

    fun close() {
        closed = true
    }
}

fun parseAge(raw: String?): Int {
    val text = requireNotNull(raw) { "edad nula" }        // desde aquí text es String
    val age = requireNotNull(text.trim().toIntOrNull()) { "edad no numérica: '$text'" }
    require(age in 0..150) { "edad fuera de rango: $age" }
    return age
}

/** No hay multi-catch (catch (A | B e)): se captura el padre y se distingue con when. */
fun classify(block: () -> Unit): String = try {
    block()
    "ok"
} catch (e: Exception) {
    when (e) {
        is NumberFormatException -> "número inválido"          // va antes: es subclase de IAE
        is IllegalArgumentException -> "argumento: ${e.message}"
        is IllegalStateException -> "estado: ${e.message}"
        is ArithmeticException -> "aritmética"
        else -> throw e                                          // lo que no se sabe manejar, se relanza
    }
}

/** finally corre siempre; su valor NO cuenta como resultado del try. */
fun withFinally(log: MutableList<String>, fail: Boolean): Int = try {
    log += "try"
    if (fail) throw IllegalStateException("falla")
    1
} catch (e: IllegalStateException) {
    log += "catch"
    -1
} finally {
    log += "finally"
}

/** Int / 0 lanza ArithmeticException (Double / 0 da Infinity, sin excepción). */
fun divide(a: Int, b: Int): Int = a / b
