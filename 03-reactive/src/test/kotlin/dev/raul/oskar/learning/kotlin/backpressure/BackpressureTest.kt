// currentTime pide opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver 02-coroutines/testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.backpressure

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.exceptions.MissingBackpressureException
import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import reactor.core.Exceptions
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BackpressureTest {

    // Reactor: la fuente empuja 100 precios de golpe; el suscriptor pide 5 y después 10.

    @Test
    fun `Reactor bounded buffer delivers what it kept and then fails`() {
        val got = eagerReactorSource(100).withStrategy(Strategy.BUFFER).consumeSlowly(5, 10)
        assertEquals((1..9).toList(), got.values)               // 5 pedidos + los 4 que guardó el buffer…
        assertTrue(Exceptions.isOverflow(got.error!!))          // …y recién después el error
    }

    @Test
    fun `Reactor drop keeps the first ones`() {
        val got = eagerReactorSource(100).withStrategy(Strategy.DROP).consumeSlowly(5, 10)
        assertEquals((1..5).toList(), got.values)               // el resto se descartó mientras nadie pedía
        assertNull(got.error)
    }

    @Test
    fun `Reactor latest keeps the last one`() {
        assertEquals(listOf(1, 2, 3, 4, 5, 100), eagerReactorSource(100).withStrategy(Strategy.LATEST).consumeSlowly(5, 10).values)
    }

    @Test
    fun `Reactor interval without demand overflows`() {
        StepVerifier.withVirtualTime({ Flux.interval(Duration.ofSeconds(1)) }, 0)
            .thenAwait(Duration.ofSeconds(1))
            .expectErrorMatches { Exceptions.isOverflow(it) }   // un reloj no puede esperar a que le pidan
            .verify()
    }

    // RxJava: lo mismo con Flowable.

    @Test
    fun `RxJava bounded buffer fails right away`() {
        val got = eagerRxSource(100).withStrategy(Strategy.BUFFER).consumeSlowly(5, 10)
        assertEquals((1..5).toList(), got.values)               // a diferencia de Reactor, no entrega lo del buffer
        assertIs<MissingBackpressureException>(got.error)
    }

    @Test
    fun `RxJava drop and latest behave like Reactor`() {
        assertEquals((1..5).toList(), eagerRxSource(100).withStrategy(Strategy.DROP).consumeSlowly(5, 10).values)
        assertEquals(listOf(1, 2, 3, 4, 5, 100), eagerRxSource(100).withStrategy(Strategy.LATEST).consumeSlowly(5, 10).values)
    }

    @Test
    fun `an Observable picks its strategy when it becomes a Flowable`() {
        assertEquals(listOf(1, 2, 3, 100), Observable.range(1, 100).toFlowableWith(Strategy.LATEST).consumeSlowly(3, 1).values)
        assertEquals(listOf(1, 2, 3), Observable.range(1, 100).toFlowableWith(Strategy.DROP).consumeSlowly(3, 1).values)
        assertEquals((1..4).toList(), Observable.range(1, 100).toFlowableWith(Strategy.BUFFER).consumeSlowly(3, 1).values)  // sin límite: no pierde, guarda todo
    }

    @Test
    fun `RxJava interval without demand fails too`() {
        val scheduler = TestScheduler()
        val test = Flowable.interval(1, TimeUnit.SECONDS, scheduler).test(0)
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertError(MissingBackpressureException::class.java)
    }

    // Flow: el productor espera; las pérdidas solo aparecen si se eligen.

    @Test
    fun `Flow buffer waits and loses nothing`() = runTest {
        assertEquals((1..20).toList(), flowWithStrategy(20, Strategy.BUFFER, consumerMillis = 10))
        assertEquals(200, currentTime)                          // marca el ritmo el consumidor
    }

    @Test
    fun `Flow drop latest keeps what fits`() = runTest {
        assertEquals(listOf(1, 2, 3, 4, 5), flowWithStrategy(20, Strategy.DROP, consumerMillis = 10))   // 1 en curso + 4 en el buffer
    }

    @Test
    fun `Flow conflate keeps the first and the last`() = runTest {
        assertEquals(listOf(1, 20), flowWithStrategy(20, Strategy.LATEST, consumerMillis = 10))
    }

    @Test
    fun `without a buffer the Flow producer emits only as fast as the consumer`() = runTest {
        val produced = mutableListOf<Int>()
        val received = flowWithStrategy(10, Strategy.BUFFER, consumerMillis = 10, bufferSize = 0, produced = produced)
        assertEquals(produced, received)
        assertTrue(currentTime >= 100)
    }
}
