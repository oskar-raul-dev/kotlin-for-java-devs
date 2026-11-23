package dev.raul.oskar.learning.kotlin.loom

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * El mismo trabajo (notificar un precio a [clients] clientes, cada notificación espera [latencyMillis] como una
 * escritura de red) en tres modelos. Devuelve cuánto tardó; [delivered] cuenta las entregas.
 */
interface BroadcastModel {
    val name: String
    fun broadcast(clients: Int, latencyMillis: Long, delivered: AtomicInteger): Duration
}

/** Hilos de plataforma de un pool fijo: el modelo de siempre. Con más clientes que hilos, esperan en cola. */
class PlatformThreadModel(private val threads: Int = 200) : BroadcastModel {
    override val name = "hilos de plataforma ($threads)"
    override fun broadcast(clients: Int, latencyMillis: Long, delivered: AtomicInteger): Duration = measureTime {
        val pool = Executors.newFixedThreadPool(threads)
        repeat(clients) { pool.execute { Thread.sleep(latencyMillis); delivered.incrementAndGet() } }
        pool.shutdown()
        pool.awaitTermination(1, TimeUnit.MINUTES)
    }
}

/**
 * Hilos virtuales (JEP 444, finales en el JDK 21): un hilo por cliente, código **bloqueante** de siempre
 * (`Thread.sleep`), y el JDK desmonta el hilo virtual de su hilo portador mientras espera.
 */
class VirtualThreadModel : BroadcastModel {
    override val name = "hilos virtuales"
    override fun broadcast(clients: Int, latencyMillis: Long, delivered: AtomicInteger): Duration = measureTime {
        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            repeat(clients) { executor.execute { Thread.sleep(latencyMillis); delivered.incrementAndGet() } }
        }                                                       // close() espera a que terminen todas
    }
}

/** Corrutinas: una por cliente, código que **suspende** (`delay`), sobre los hilos de `Dispatchers.Default`. */
class CoroutineModel : BroadcastModel {
    override val name = "corrutinas"
    override fun broadcast(clients: Int, latencyMillis: Long, delivered: AtomicInteger): Duration = measureTime {
        runBlocking(Dispatchers.Default) {
            repeat(clients) { launch { delay(latencyMillis); delivered.incrementAndGet() } }
        }
    }
}

/**
 * Los dos modelos juntos: corrutinas (estructura, cancelación, `Flow`) sobre un dispatcher de hilos virtuales, para
 * llamar a código bloqueante sin agotar `Dispatchers.IO`.
 */
class CoroutinesOnVirtualThreadsModel : BroadcastModel {
    override val name = "corrutinas sobre hilos virtuales"
    override fun broadcast(clients: Int, latencyMillis: Long, delivered: AtomicInteger): Duration = measureTime {
        Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher().use { virtual ->
            runBlocking {
                withContext(virtual) {
                    repeat(clients) { launch { Thread.sleep(latencyMillis); delivered.incrementAndGet() } }
                }
            }
        }
    }
}
