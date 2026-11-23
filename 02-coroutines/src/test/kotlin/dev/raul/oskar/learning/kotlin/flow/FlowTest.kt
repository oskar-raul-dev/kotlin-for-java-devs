// currentTime pide opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.flow

import app.cash.turbine.test
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FlowTest {

    private fun tick(ticker: String, at: Long, price: String) = Tick(ticker, at, Money.of(price))

    @Test
    fun `a cold flow runs again for every collector`() = runTest {
        val feed = TickFeed(tickFeed(steps = 3))
        assertEquals(0, feed.opened.get())                      // crear el Flow no hace nada
        feed.flow().count()
        feed.flow().count()
        assertEquals(2, feed.opened.get())
    }

    @Test
    fun `the feed emits each tick at its instant`() = runTest {
        TickFeed(tickFeed(steps = 3, stepMillis = 1_000)).flow().test {
            repeat(5) { assertEquals(0, awaitItem().atMillis) }
            assertEquals(1_000, awaitItem().atMillis)
            assertEquals(1_000, currentTime)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cleaning drops duplicates and late ticks per ticker`() = runTest {
        val raw = listOf(
            tick("ACME", 0, "100.00"), tick("ACME", 0, "100.00"),       // duplicado
            tick("ORBX", 0, "42.50"),
            tick("ACME", 2_000, "100.20"), tick("ACME", 1_000, "100.10"), // llega tarde
            tick("ORBX", 1_000, "42.60"),
        )
        val clean = flowOf(*raw.toTypedArray()).cleaned().toList()
        assertEquals(listOf(raw[0], raw[2], raw[3], raw[5]), clean)
        val flawed = withFlaws(tickFeed(steps = 20))
        assertTrue(flowOf(*flawed.toTypedArray()).cleaned().toList().zipWithNext().none { (a, b) -> a == b })
    }

    @Test
    fun `candles close when the next window starts`() = runTest {
        val ticks = listOf(
            tick("ACME", 0, "100.00"), tick("ACME", 20_000, "101.50"), tick("ACME", 59_000, "99.80"),
            tick("ACME", 61_000, "100.20"), tick("ORBX", 5_000, "42.50"),
        )
        val candles = flowOf(*ticks.toTypedArray()).candles().toList()
        assertEquals(
            Candle("ACME", 0, Money.of("100.00"), Money.of("101.50"), Money.of("99.80"), Money.of("99.80"), 3),
            candles.first(),                                     // emitida al llegar el tick de 61 s
        )
        assertEquals(listOf("ACME" to 0L, "ACME" to 60_000L, "ORBX" to 0L), candles.map { it.ticker to it.windowStart })
    }

    @Test
    fun `alerts fire on each crossing and keep state per collector`() = runTest {
        val ticks = listOf(tick("ACME", 0, "99.90"), tick("ACME", 1, "100.10"), tick("ORBX", 2, "200.00"), tick("ACME", 3, "99.95"))
        val alerts = flowOf(*ticks.toTypedArray()).alerts("ACME", Money.of("100.00"))
        val expected = listOf(PriceAlert("ACME", 1, Money.of("100.10"), true), PriceAlert("ACME", 3, Money.of("99.95"), false))
        assertEquals(expected, alerts.toList())
        assertEquals(expected, alerts.toList())                  // la segunda colección no hereda el estado de la primera
    }

    @Test
    fun `flowOn moves only the upstream`() = runBlocking {
        val (producer, collector) = upstreamAndCollectorThreads(Dispatchers.IO)
        assertTrue(producer.startsWith("DefaultDispatcher-worker-"))
        assertNotEquals(producer, collector)
        assertEquals(Thread.currentThread().name.substringBefore(" @"), collector)
    }

    @Test
    fun `withContext inside flow violates the context invariant`() = runTest {
        val e = assertFailsWith<IllegalStateException> { brokenFlow(StandardTestDispatcher(testScheduler)).toList() }
        assertTrue(e.message!!.startsWith("Flow invariant is violated"))
    }

    @Test
    fun `catch only sees the upstream`() = runTest {
        assertEquals(listOf("ACME 100.00", "error: feed caído"), catchUpstreamOnly(failInProducer = true))
        assertEquals(listOf("ACME 100.00", "ACME 100.10"), catchUpstreamOnly(failInProducer = false))
        var caught = false
        assertFailsWith<IllegalStateException> {
            flowOf(1, 2).catch { caught = true }.collect { error("falla en el colector") }
        }
        assertFalse(caught)                                      // catch no vio la falla del colector
    }

    private class FakeClient : PriceListenerClient {
        val listeners = mutableMapOf<String, (String) -> Unit>()
        var unregistered = 0
        override fun register(ticker: String, onPrice: (String) -> Unit, onDone: () -> Unit) { listeners[ticker] = onPrice }
        override fun unregister(ticker: String) { listeners.remove(ticker); unregistered++ }
    }

    @Test
    fun `callbackFlow unregisters when the collector goes away`() = runTest {
        val client = FakeClient()
        val got = mutableListOf<String>()
        val job = launch { client.prices("ACME").take(2).toList(got) }
        runCurrent()
        client.listeners.getValue("ACME")("100.00")
        client.listeners.getValue("ACME")("100.10")
        job.join()
        assertEquals(listOf("100.00", "100.10"), got)
        assertEquals(1, client.unregistered)                    // take(2) canceló el flujo y awaitClose quitó el listener
        assertTrue(client.listeners.isEmpty())
    }

    private suspend fun TestScope.run(strategy: Strategy): Pair<List<Int>, Long> {
        val emitted = mutableListOf<Long>()
        val got = slowConsumer(fastPrices(10, everyMillis = 10, emitted) { currentTime }, consumerMillis = 30, strategy = strategy)
        return got to currentTime
    }

    @Test
    fun `without a buffer the producer waits for the consumer`() = runTest {
        val (got, time) = run(Strategy.NONE)
        assertEquals((0..9).toList(), got)
        assertEquals(10 * (10 + 30L), time)                     // se suman: 400 ms
    }

    @Test
    fun `a buffer decouples producer and consumer`() = runTest {
        val (got, time) = run(Strategy.BUFFER)
        assertEquals((0..9).toList(), got)
        assertEquals(10 + 10 * 30L, time)                       // el consumidor marca el ritmo: 310 ms
    }

    @Test
    fun `conflate keeps only the latest value`() = runTest {
        val (got, time) = run(Strategy.CONFLATE)
        assertEquals(0, got.first())
        assertEquals(9, got.last())                             // el último siempre llega…
        assertTrue(got.size < 10)                               // …los intermedios no
        assertTrue(time < 310)
    }
}
