package dev.raul.oskar.learning.kotlin.exceptions

import kotlin.coroutines.cancellation.CancellationException

// Errores como valores: Result (runCatching) y tipos sellados propios.

/** runCatching envuelve en Result.success / Result.failure. */
fun parsePort(raw: String): Result<Int> = runCatching {
    raw.trim().toInt().also { require(it in 1..65535) { "puerto fuera de rango: $it" } }
}

fun portOrDefault(raw: String): Int = parsePort(raw).getOrDefault(8080)

fun portOrExplain(raw: String): String = parsePort(raw).fold(
    onSuccess = { "puerto $it" },
    onFailure = { "inválido (${it::class.simpleName})" },
)

/** recover: convertir ciertos fallos en éxito; los demás siguen siendo fallo. */
fun portRecoveringFormat(raw: String): Result<Int> =
    parsePort(raw).recoverCatching { e -> if (e is NumberFormatException) 8080 else throw e }

/** map frente a mapCatching: si la transformación lanza, map propaga la excepción; mapCatching la guarda. */
fun doubledPort(raw: String): Result<Int> = parsePort(raw).mapCatching { p -> check(p < 40000) { "muy alto" }; p * 2 }

/**
 * TRAMPA: runCatching captura Throwable, incluidas CancellationException (corrutinas) y Errors.
 * En código con corrutinas hay que relanzar la cancelación, o se rompe la cancelación estructurada.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {          // tampoco se tragan los Error (OutOfMemoryError, StackOverflowError)
    Result.failure(e)
}

/** Alternativa sin excepciones: un resultado sellado de dominio, explícito en la firma. */
sealed interface Validation<out T> {
    data class Valid<T>(val value: T) : Validation<T>
    data class Invalid(val errors: List<String>) : Validation<Nothing>
}

data class Applicant(val name: String, val age: Int, val email: String)

/** Acumula TODOS los errores, cosa que con excepciones (que cortan en el primero) no se puede. */
fun validate(name: String, age: Int, email: String): Validation<Applicant> {
    val errors = buildList {
        if (name.isBlank()) add("nombre vacío")
        if (age !in 18..99) add("edad fuera de rango: $age")
        if ('@' !in email) add("email inválido: $email")
    }
    return if (errors.isEmpty()) Validation.Valid(Applicant(name, age, email)) else Validation.Invalid(errors)
}

fun describe(v: Validation<Applicant>): String = when (v) {
    is Validation.Valid -> "ok: ${v.value.name}"
    is Validation.Invalid -> "errores: ${v.errors.joinToString("; ")}"
}
