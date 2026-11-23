package dev.raul.oskar.learning.kotlin.cancellation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException

/**
 * El Mercado sigue a cada ticker en un hijo de `supervisorScope`: la falla de uno **no** cancela a los demás. Cada
 * resultado se devuelve como `Result` (sin tragarse la cancelación).
 */
suspend fun followEach(tickers: List<String>, follow: suspend (String) -> Int): Map<String, Result<Int>> =
    supervisorScope {
        val children = tickers.associateWith { async { follow(it) } }
        children.mapValues { (_, child) ->
            try {
                Result.success(child.await())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

/** Lo mismo con `coroutineScope`: todo o nada. La primera falla cancela al resto y se relanza. */
suspend fun followAllOrNothing(tickers: List<String>, follow: suspend (String) -> Int): Map<String, Int> =
    coroutineScope {
        tickers.associateWith { async { follow(it) } }.mapValues { it.value.await() }
    }

/**
 * `CoroutineExceptionHandler` es el `UncaughtExceptionHandler` de las corrutinas, y solo se usa en la corrutina
 * **raíz** (un `launch` directo del scope). Devuelve el `Job` lanzado; [seen] recibe lo que el handler vio.
 */
fun CoroutineScope.launchWithHandler(seen: MutableList<String>, block: suspend () -> Unit): Job {
    val handler = CoroutineExceptionHandler { _, e -> seen += "handler: ${e.message}" }
    return launch(handler) { block() }
}

/**
 * La trampa: un handler puesto en un hijo no hace nada; la excepción sube al padre igual. Devuelve el `Job` raíz.
 */
fun CoroutineScope.handlerOnChild(seen: MutableList<String>): Job = launch {
    val ignored = CoroutineExceptionHandler { _, e -> seen += "handler del hijo: ${e.message}" }
    launch(ignored) { throw IllegalStateException("falla del hijo") }
}

/**
 * La trampa de `launch(SupervisorJob())`: el `Job` nuevo pasa a ser el padre de la corrutina, que deja de ser hija del
 * scope. `coroutineScope` vuelve sin esperarla y nadie la cancela. En 1.11.0 esa sobrecarga de `launch` ya está
 * deprecada (warning, medido): "Passing a Job to coroutine builders breaks structured concurrency"; avisa que pasará a
 * error. Se suprime el aviso solo para mostrar la trampa.
 */
@Suppress("DEPRECATION")
suspend fun detachedBySupervisorJob(log: MutableList<String>): Job = coroutineScope {
    launch(SupervisorJob()) {
        delay(100)
        log += "terminó por su cuenta"
    }
}
