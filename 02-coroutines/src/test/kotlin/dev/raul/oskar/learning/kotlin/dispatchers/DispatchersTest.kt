package dev.raul.oskar.learning.kotlin.dispatchers

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

// Estas pruebas usan hilos de verdad (runBlocking), porque lo que se mide son los hilos; cada una tarda milisegundos.
class DispatchersTest {

    private val accounts = List(100) { "ACC-%08d-%02d".format(it, it % 97) }
    private val cores = Runtime.getRuntime().availableProcessors()

    private fun peakOn(dispatcher: CoroutineDispatcher, latencyMillis: Long = 10): Int = runBlocking {
        val repository = BlockingLedgerRepository(latencyMillis)
        val balances = LedgerService(repository, dispatcher).balances(accounts)
        assertEquals(accounts.size, balances.size)
        repository.meter.max
    }

    @Test
    fun `limitedParallelism never exceeds the pool size`() {
        val peak = peakOn(dbDispatcher(4))
        assertTrue(peak <= 4, "pico $peak")
        assertTrue(peak >= 2, "pico $peak: debería usar el paralelismo que tiene")
    }

    @Test
    fun `blocking calls on Default are capped by the number of cores`() {
        assertTrue(peakOn(Dispatchers.Default) <= cores)        // los demás núcleos quedan esperando: nada más corre
    }

    @Test
    fun `IO lends more threads than there are cores`() {
        val peak = peakOn(Dispatchers.IO, latencyMillis = 30)
        assertTrue(peak > cores || cores >= 64, "pico $peak con $cores núcleos")
        assertTrue(peak <= maxOf(64, cores))                    // 64 hilos o tantos como núcleos, lo que sea mayor
    }

    @Test
    fun `virtual threads run every blocking call at once`() {
        val peak = virtualThreadDispatcher().use { peakOn(it, latencyMillis = 30) }
        assertTrue(peak > 64, "pico $peak")
    }

    @Test
    fun `the db name shows in toString but not in the thread name`() = runBlocking {
        assertEquals("db", dbDispatcher(4).toString())
        assertTrue(threadNameOn(dbDispatcher(4)).startsWith("DefaultDispatcher-worker-"))
    }

    @Test
    fun `IO and Default share the same worker threads`() = runBlocking {
        assertTrue(threadNameOn(Dispatchers.IO).startsWith("DefaultDispatcher-worker-"))
        assertTrue(threadNameOn(Dispatchers.Default).startsWith("DefaultDispatcher-worker-"))
    }

    @Test
    fun `a ThreadLocal is lost after withContext unless it travels as a context element`() = runBlocking {
        assertNull(correlationIdAfterSwitch("req-42", propagate = false))
        assertEquals("req-42", correlationIdAfterSwitch("req-42", propagate = true))
        assertNull(correlationId.get())                         // y no queda pegado en el hilo de la prueba
    }

    @Test
    fun `Unconfined resumes on whatever thread resumed it`() = runBlocking {
        val (before, after) = unconfinedThreads()
        assertEquals(threadName(), before)
        assertNotEquals(before, after)
    }

    @Test
    fun `limitedParallelism(1) confines state so no increment is lost`() = runBlocking {
        assertEquals(100_000, confinedCounter(100, 1_000))
    }

    @Test
    fun `candles take an injected dispatcher`() = runTest {
        val ticks = listOf(
            Tick("ACME", 1_000, Money.of("100.00")), Tick("ACME", 30_000, Money.of("101.50")),
            Tick("ACME", 59_000, Money.of("99.80")), Tick("ACME", 61_000, Money.of("100.20")),
            Tick("ORBX", 2_000, Money.of("42.50")),
        )
        val got = candles(ticks, StandardTestDispatcher(testScheduler))
        assertEquals(3, got.size)
        assertEquals(Candle("ACME", 0, Money.of("100.00"), Money.of("101.50"), Money.of("99.80"), Money.of("99.80")), got[0])
        assertEquals(listOf("ACME", "ORBX", "ACME"), got.map { it.ticker })
    }
}
