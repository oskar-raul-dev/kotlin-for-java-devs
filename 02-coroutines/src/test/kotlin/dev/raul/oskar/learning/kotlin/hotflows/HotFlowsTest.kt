// currentTime, advanceTimeBy y runCurrent piden opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.hotflows

import app.cash.turbine.test
import dev.raul.oskar.learning.kotlin.cases.ACC_EUR
import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HotFlowsTest {

    private fun tick(ticker: String, price: String) = Tick(ticker, 0, Money.of(price))

    @Test
    fun `a late subscriber gets the current price at once`() = runTest {
        val board = PriceBoard()
        board.onTick(tick("ACME", "100.00"))
        board.onTick(tick("ACME", "100.40"))
        board.price("ACME").test {
            assertEquals(Money.of("100.40"), awaitItem())       // el actual, no la historia
            board.onTick(tick("ACME", "100.50"))
            assertEquals(Money.of("100.50"), awaitItem())
        }
    }

    @Test
    fun `alerts are events that late subscribers do not see`() = runTest {
        val board = PriceBoard(alertMove = Money.of("1.00"))
        board.onTick(tick("ACME", "100.00"))
        board.onTick(tick("ACME", "101.00"))                    // alerta sin nadie escuchando: se pierde
        board.alerts.test {
            board.onTick(tick("ACME", "99.90"))
            assertEquals(MoveAlert("ACME", Money.of("101.00"), Money.of("99.90")), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `StateFlow only emits when the value changes`() = runTest {
        val state = MutableStateFlow(0)
        val seen = mutableListOf<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { state.toList(seen) }
        sameValueDoesNotEmit(state)
        assertEquals(listOf(0, 1, 2), seen)                     // el segundo 1 no se emitió
        job.cancel()
    }

    @Test
    fun `mutating the same list in place notifies nobody`() = runTest {
        val state = MutableStateFlow(mutableListOf<String>())
        val seen = mutableListOf<List<String>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { state.collect { seen += it.toList() } }
        mutatingInPlaceDoesNotEmit(state)
        assertEquals(listOf(emptyList<String>()), seen)         // solo el valor inicial
        state.value = (state.value + "ORBX").toMutableList()    // una lista nueva sí avisa
        assertEquals(listOf("ACME", "ORBX"), seen.last())
        job.cancel()
    }

    @Test
    fun `update is atomic across threads`() = runTest {
        assertEquals(100_000, concurrentIncrements(100, 1_000, atomic = true))
    }

    @Test
    fun `a SharedFlow without subscribers and replay loses its events`() {
        assertEquals(listOf(true, true, true) to emptyList<Int>(), emitWithoutSubscribers(replay = 0, count = 3))
        assertEquals(listOf(true, true, true) to listOf(1, 2), emitWithoutSubscribers(replay = 2, count = 3))
    }

    @Test
    fun `a slow subscriber with a full buffer suspends the emitter`() = runTest {
        val bus = alertsBus(extraBuffer = 1)
        backgroundScope.launch { bus.collect { delay(100) } }
        runCurrent()                                            // el suscriptor ya está suscrito
        repeat(4) { bus.emit(it) }
        assertTrue(currentTime >= 100, "emit esperó al suscriptor: $currentTime ms")

        val dropping = alertsBus(extraBuffer = 1, overflow = BufferOverflow.DROP_OLDEST)
        backgroundScope.launch { dropping.collect { delay(100) } }
        runCurrent()
        val before = currentTime
        repeat(4) { dropping.emit(it) }
        assertEquals(before, currentTime)                       // con DROP_OLDEST, emit nunca suspende
    }

    @Test
    fun `WhileSubscribed stops the source exactly five seconds after the last subscriber`() = runTest {
        val source = flow { var i = 0; while (true) { emit(i++); delay(1_000) } }
        val shared = SharedLatest(backgroundScope, source)
        assertEquals(0, shared.starts)                          // nadie suscrito: la fuente no arrancó

        val subscriber = launch { shared.latest.collect { } }
        runCurrent()
        assertEquals(1, shared.starts)
        advanceTimeBy(3_000)
        subscriber.cancel()

        advanceTimeBy(4_999)
        assertEquals(0, shared.stops)                           // sigue viva durante el período de gracia
        advanceTimeBy(2)
        assertEquals(1, shared.stops)                           // y se detiene a los 5 s exactos

        // Durante la gracia la fuente siguió emitiendo (un valor por segundo); el último queda guardado en el StateFlow.
        assertTrue(shared.latest.value!! >= 7, "último valor: ${shared.latest.value}")
        assertEquals(1, shared.starts)
    }

    @Test
    fun `balances update with each posting and keep double entry`() = runTest {
        val balances = LiveBalances()
        balances.balance(ACC_USD).test {
            assertEquals(Money.zero(), awaitItem())
            balances.post(Posting(ACC_USD, Money.of("1000.00")))
            assertEquals(Money.of("1000.00"), awaitItem())
            balances.transfer(ACC_USD, ACC_EUR, Money.of("250.00"))
            assertEquals(Money.of("750.00"), awaitItem())
        }
        assertEquals(Money.of("250.00"), balances.balance(ACC_EUR).value)
    }
}
