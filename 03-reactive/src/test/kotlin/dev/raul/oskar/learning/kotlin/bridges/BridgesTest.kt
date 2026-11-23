package dev.raul.oskar.learning.kotlin.bridges

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import reactor.util.context.Context
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

// runBlocking y no runTest: los Mono y Flux usan los schedulers de Reactor (tiempo real), que el reloj virtual de
// kotlinx-coroutines-test no controla. Las esperas son de milisegundos.
class BridgesTest {

    private val client = ReactivePriceClient(mapOf("ACME" to Money.of("100.00")))

    @Test
    fun `awaitSingle suspends for the value and fails on empty`() = runBlocking {
        assertEquals(Money.of("100.00"), client.priceOf("ACME"))
        assertFailsWith<NoSuchElementException> { client.priceOf("ZZZZ") }
        assertNull(client.priceOrNull("ZZZZ"))                  // vacío → null, sin excepción
    }

    @Test
    fun `a Flux becomes a Flow`() = runBlocking {
        assertEquals(tickFeed(steps = 10), client.feedAsFlow(10).toList())
    }

    @Test
    fun `mono and asFlux offer coroutine code to Reactor`() {
        quoteMono("ACME").test().expectNext(Money.of("100.00")).verifyComplete()
        val ticks = tickFeed(steps = 2)
        ticks.asFlow().toFlux().test().expectNextSequence(ticks).verifyComplete()
    }

    @Test
    fun `the Reactor context reaches the coroutine`() {
        correlationIdSeenByCoroutine()
            .contextWrite(Context.of("correlationId", "req-41"))
            .test().expectNext("req-41").verifyComplete()
        correlationIdSeenByCoroutine().test().expectNext("sin id").verifyComplete()
    }

    @Test
    fun `the coroutine context reaches Reactor`() = runBlocking {
        assertEquals("req-42", correlationIdSeenByReactor("req-42"))
    }

    @Test
    fun `cancelling the Flow cancels the Reactor subscription`() = runBlocking {
        val cancelled = AtomicBoolean()
        assertEquals(listOf(0L, 1L, 2L), endlessReactorFeed(cancelled).asFlow().take(3).toList())
        assertTrue(cancelled.get())
    }

    @Test
    fun `cancelling the subscription cancels the coroutine`() {
        val stopped = AtomicBoolean()
        StepVerifier.create(endlessCoroutineFeed(stopped))
            .expectNext(0, 1, 2)
            .thenCancel()
            .verify(Duration.ofSeconds(2))
        val deadline = System.nanoTime() + 2_000_000_000
        while (!stopped.get() && System.nanoTime() < deadline) Thread.onSpinWait()
        assertTrue(stopped.get())                               // el finally de la corrutina corrió
    }

    @Test
    fun `backpressure crosses the bridge`() {
        val produced = AtomicInteger()
        StepVerifier.create(countingFlowAsFlux(produced), 0)
            .thenRequest(5)
            .expectNextCount(5)
            .thenCancel()
            .verify(Duration.ofSeconds(2))
        assertTrue(produced.get() <= 6, "el Flow produjo ${produced.get()} con una demanda de 5")
    }
}
