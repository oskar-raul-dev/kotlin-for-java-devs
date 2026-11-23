package dev.raul.oskar.learning.kotlin.cancellation

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

/**
 * Procesa un lote de movimientos. Sin puntos de suspensión ni `ensureActive()`, la corrutina no se entera de que la
 * cancelaron y termina el lote entero: la cancelación es **cooperativa**, como `Thread.interrupt()`, que solo marca
 * una bandera que el código tiene que mirar.
 *
 * [onItem] es un gancho para las pruebas (y para el `Main`): ahí se cancela a mitad del lote.
 */
suspend fun processBatch(items: List<Int>, cooperative: Boolean, processed: MutableList<Int>, onItem: (Int) -> Unit = {}) {
    for (item in items) {
        if (cooperative) currentCoroutineContext().ensureActive()   // lanza CancellationException si ya la cancelaron
        processed += item
        onItem(item)
    }
}

/**
 * La trampa: `catch (e: Exception)` atrapa también la `CancellationException`. La corrutina sigue haciendo trabajo
 * después de que la cancelaron, hasta el siguiente punto de suspensión (que vuelve a lanzar, porque el `Job` ya está
 * cancelado).
 */
suspend fun swallowingCancellation(log: MutableList<String>) {
    try {
        delay(1_000)
    } catch (e: Exception) {
        log += "atrapada: ${e::class.simpleName}"
    }
    log += "siguió trabajando"           // corre aunque la corrutina esté cancelada
    delay(1_000)                         // aquí vuelve a saltar la cancelación
    log += "nunca llega"
}

/** Lo correcto: atrapar lo que se sabe manejar y relanzar la cancelación (o atrapar algo más específico). */
suspend fun rethrowingCancellation(log: MutableList<String>) {
    try {
        delay(1_000)
    } catch (e: CancellationException) {
        log += "relanzada"
        throw e
    } catch (e: Exception) {
        log += "error real: ${e.message}"
    }
    log += "nunca llega"
}

/**
 * `runCatching` atrapa `Throwable`, cancelación incluida. Esta variante (la misma de `01-language/exceptions`) la deja
 * pasar.
 */
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
