package dev.raul.oskar.learning.kotlin.loom

import org.junit.jupiter.api.Assumptions.assumeTrue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

// Pruebas con hilos y tiempos reales: comprueban órdenes de magnitud con márgenes amplios, no números finos.
class LoomTest {

    private val clients = 2_000
    private val latency = 50L

    private fun run(model: BroadcastModel): Long {
        val delivered = AtomicInteger()
        val time = model.broadcast(clients, latency, delivered)
        assertEquals(clients, delivered.get(), model.name)
        return time.inWholeMilliseconds
    }

    @Test
    fun `virtual threads make blocking cheap`() {
        // 2 000 esperas de 50 ms: con hilos virtuales, todas a la vez (unos 50 ms, lejos de los 100 000 en serie).
        assertTrue(run(VirtualThreadModel()) < 1_000)
    }

    @Test
    fun `coroutines get the same with suspension`() {
        assertTrue(run(CoroutineModel()) < 1_000)
    }

    @Test
    fun `a fixed platform pool queues what does not fit`() {
        // 2 000 tareas en 200 hilos: al menos 10 tandas de 50 ms.
        assertTrue(run(PlatformThreadModel(threads = 200)) >= 10 * latency)
    }

    @Test
    fun `coroutines can run blocking code on virtual threads`() {
        assertTrue(run(CoroutinesOnVirtualThreadsModel()) < 1_000)
    }

    @Test
    fun `blocking inside synchronized pins the carrier on JDK 21`() {
        assumeTrue(jdkFeatureVersion < 24, "JEP 491 (JDK 24) quitó el pinning de synchronized")
        val carriers = Runtime.getRuntime().availableProcessors()
        val tasks = carriers * 8
        val pinned = blockingInsideLocks(tasks, sleepMillis = 50, useSynchronized = true)
        val unpinned = blockingInsideLocks(tasks, sleepMillis = 50, useSynchronized = false)
        assertTrue(pinned >= (50 * 6).milliseconds, "con synchronized: $pinned")      // ~8 tandas de 50 ms
        assertTrue(unpinned < pinned / 2, "ReentrantLock $unpinned frente a synchronized $pinned")
    }
}
