package dev.raul.oskar.learning.kotlin.exceptions

import dev.raul.oskar.learning.kotlin.basics.Money
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

/** Una solicitud de transferencia ya validada. */
data class TransferRequest(val from: String, val to: String, val amount: Money)

/** Acumula TODOS los errores de la solicitud, cosa que con excepciones (que cortan en el primero) no se puede. */
fun validate(from: String, to: String, amount: String): Validation<TransferRequest> {
    val parsed = amount.trim().toBigDecimalOrNull()
    val errors = buildList {
        if (!ACCOUNT.matches(from)) add("cuenta de origen inválida: '$from'")
        if (!ACCOUNT.matches(to)) add("cuenta de destino inválida: '$to'")
        if (from == to) add("origen y destino iguales")
        if (parsed == null || parsed.signum() <= 0) add("monto inválido: '$amount'")
    }
    // parsed es un val local: el compilador lo da por no nulo dentro de la rama (smart cast), sin `!!`.
    return if (errors.isEmpty() && parsed != null) Validation.Valid(TransferRequest(from, to, Money.of(parsed)))
    else Validation.Invalid(errors)
}

/** Solo la forma ACC-8 dígitos-2 dígitos; validar los dígitos de control (mod 97) es ejercicio de strings. */
private val ACCOUNT = Regex("""ACC-\d{8}-\d{2}""")

fun describe(v: Validation<TransferRequest>): String = when (v) {
    is Validation.Valid -> "ok: ${v.value.amount} de ${v.value.from} a ${v.value.to}"
    is Validation.Invalid -> "errores: ${v.errors.joinToString("; ")}"
}
