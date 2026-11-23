package dev.raul.oskar.learning.kotlin.fundamentals

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.time.Duration.Companion.seconds

/**
 * Una función `suspend` sin puntos de suspensión corre entera en el hilo de quien la llama: `suspend` no crea hilos
 * ni cambia de hilo por sí sola.
 */
@Suppress("RedundantSuspendModifier")
suspend fun currentThreadName(): String = Thread.currentThread().name

/**
 * `launch` devuelve un `Job` (sin resultado: "dispara y espera si quieres"); `async`, un `Deferred<T>` (un `Job`
 * con resultado). Las dos corrutinas son hijas del scope: `coroutineScope` no vuelve hasta que terminan.
 */
suspend fun launchVersusAsync(log: MutableList<String>): Int = coroutineScope {
    val job: Job = launch {
        delay(100)
        log += "launch terminó"
    }
    val deferred = async {
        delay(50)
        log += "async terminó"
        42
    }
    log += "las dos lanzadas (job activo: ${job.isActive})"
    deferred.await()
}

/**
 * Una excepción dentro de `async` cancela al scope **aunque nadie llame a `await`**: el `Job` del hijo falla y su
 * padre se entera. Por eso el `launch` hermano no llega a terminar.
 */
suspend fun asyncFailureWithoutAwait(log: MutableList<String>) {
    coroutineScope {
        async<Unit> {
            delay(10)
            throw IllegalStateException("la fuente falló")
        }
        launch {
            delay(1_000)
            log += "el hermano terminó"
        }
    }
}

/**
 * Cancelar un `Job` cancela a todos sus hijos: devuelve el estado de los dos hijos después de cancelar al padre.
 * `future.cancel(true)` en Java no sabe nada de las tareas que ese future lanzó.
 */
suspend fun cancelParentCancelsChildren(): List<Boolean> = coroutineScope {
    lateinit var a: Job
    lateinit var b: Job
    val parent = launch {
        a = launch { delay(10.seconds) }
        b = launch { delay(10.seconds) }
    }
    yield()            // deja que el padre arranque y cree a sus hijos
    parent.cancel()
    parent.join()
    listOf(a.isCancelled, b.isCancelled)
}

/**
 * Cien mil corrutinas que esperan un segundo cada una. Con cien mil hilos de plataforma la JVM se queda sin memoria;
 * una corrutina suspendida es un objeto chico en el heap.
 */
suspend fun manyCoroutines(count: Int): Int = coroutineScope {
    val jobs = List(count) { launch { delay(1.seconds) } }
    jobs.forEach { it.join() }
    jobs.size
}

/**
 * La trampa de `GlobalScope`: la corrutina no tiene dueño. El scope de quien llama termina y ella sigue viva; nadie
 * la cancela ni la espera. Por eso en 1.11.0 pide `@OptIn(DelicateCoroutinesApi::class)` (medido).
 */
@OptIn(DelicateCoroutinesApi::class)
fun leakWithGlobalScope(release: CompletableDeferred<Unit>): Job = GlobalScope.launch {
    release.await()
}

/** Lo correcto: la corrutina se lanza en un scope que alguien maneja (y cancela). */
fun CoroutineScope.ownedLaunch(release: CompletableDeferred<Unit>): Job = launch { release.await() }
