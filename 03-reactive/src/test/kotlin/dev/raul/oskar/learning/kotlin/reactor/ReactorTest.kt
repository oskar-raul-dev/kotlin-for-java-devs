package dev.raul.oskar.learning.kotlin.reactor

import dev.raul.oskar.learning.kotlin.cases.Candle
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.candlesReference
import dev.raul.oskar.learning.kotlin.cases.cleanReference
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.test.test
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReactorTest {

    private fun tick(ticker: String, at: Long, price: String) = Tick(ticker, at, Money.of(price))

    @Test
    fun `nothing happens until subscribe and each subscription runs again`() {
        val calls = AtomicInteger()
        val quote = lazyQuote(calls)
        assertEquals(0, calls.get())                            // armar el Mono no ejecutó nada
        quote.test().expectNext(Money.of("100.25")).verifyComplete()
        quote.test().expectNextCount(1).verifyComplete()
        assertEquals(2, calls.get())
    }

    @Test
    fun `empty is not null`() {
        priceOrEmpty(null).test().verifyComplete()              // vacío: completa sin elementos
        priceOrEmpty(Money.of("1.00")).test().expectNext(Money.of("1.00")).verifyComplete()
        // Reactor 3.9 trae anotaciones JSpecify: Mono.just(nullable) y un map que devuelve null NO compilan desde
        // Kotlin (medido). block() sobre un vacío devuelve null, tipado como nullable.
        val nothing: Money? = priceOrEmpty(null).block()
        assertNull(nothing)
        assertEquals(listOf("x"), Flux.just(1, 2).mapNotNull<String> { if (it == 2) null else "x" }.collectList().block())
    }

    @Test
    fun `block inside a non blocking thread is refused`() {
        val e = blockInsideParallel()
        assertTrue(e is IllegalStateException)
        assertTrue(e.message!!.contains("not supported in thread parallel-"))
    }

    @Test
    fun `flatMap concurrency limits the inner subscriptions`() {
        val tickers = listOf("ACME", "ORBX", "LUMA", "KAPA", "NUBE")
        val slow = { _: String -> Mono.just(Money.of("1.00")).delayElement(Duration.ofMillis(20)) }
        val unlimited = AtomicInteger()
        quoteAll(tickers, 256, unlimited, slow).blockLast()
        assertEquals(5, unlimited.get())                        // todas a la vez
        val limited = AtomicInteger()
        quoteAll(tickers, 2, limited, slow).blockLast()
        assertEquals(2, limited.get())
    }

    @Test
    fun `Mono of Void only signals completion`() {
        val log = mutableListOf<String>()
        settle(log).test().verifyComplete()
        assertEquals(listOf("liquidado"), log)
    }

    @Test
    fun `cleaning matches the reference and keeps state per subscriber`() {
        val raw = withFlaws(tickFeed(steps = 30))
        val cleaned = raw.toFlux().cleaned()
        assertEquals(cleanReference(raw), cleaned.collectList().block())
        assertEquals(cleanReference(raw), cleaned.collectList().block())   // la segunda suscripción empieza de cero
    }

    @Test
    fun `candles equal the plain collection reference`() {
        val raw = withFlaws(tickFeed(steps = 180))
        val got = raw.toFlux().cleaned().candles().collectList().block()!!
            .sortedWith(compareBy<Candle>({ it.ticker }, { it.windowStart }))
        assertEquals(candlesReference(cleanReference(raw)), got)
        assertEquals(15, got.size)                              // 3 minutos × 5 tickers
    }

    @Test
    fun `crossings fire on each crossing`() {
        val ticks = listOf(tick("ACME", 0, "99.90"), tick("ACME", 1, "100.10"), tick("ORBX", 2, "42.00"), tick("ACME", 3, "99.95"))
        ticks.toFlux().crossings("ACME", Money.of("100.00")).test()
            .expectNext("1: sube a 100.10 USD", "3: baja a 99.95 USD")
            .verifyComplete()
    }

    @Test
    fun `the timed feed runs in virtual time`() {
        val ticks = tickFeed(steps = 3, stepMillis = 60_000)
        StepVerifier.withVirtualTime { timedFeed(ticks) }
            .expectNextCount(5)                                 // los cinco del instante 0
            .expectNoEvent(Duration.ofSeconds(59))
            .thenAwait(Duration.ofSeconds(1))
            .expectNextCount(5)
            .thenAwait(Duration.ofMinutes(1))
            .expectNextCount(5)
            .verifyComplete()                                   // dos minutos de feed, sin esperar
    }
}
