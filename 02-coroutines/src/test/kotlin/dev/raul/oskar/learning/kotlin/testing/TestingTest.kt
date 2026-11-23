// En kotlinx-coroutines-test 1.11.0, currentTime, runCurrent, advanceTimeBy, advanceUntilIdle, setMain, resetMain y
// UnconfinedTestDispatcher piden este opt-in (medido; StandardTestDispatcher y runTest no): se usan a diario, pero su
// API todavía puede cambiar.
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.testing

import app.cash.turbine.test
import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class TestingTest {

    @Test
    fun `runTest skips delays with virtual time`() {
        val real = measureTime {
            runTest {
                delay(5.minutes)
                assertEquals(5.minutes.inWholeMilliseconds, currentTime)
            }
        }
        assertTrue(real < 1.seconds, "tardó $real de reloj real")
    }

    @Test
    fun `runBlocking uses real time`() {
        val real = measureTime { runBlocking { delay(200) } }
        assertTrue(real >= 200.milliseconds)
    }

    private val fx = FxRateClient { _, _ -> delay(100); BigDecimal("0.92") }

    @Test
    fun `an injected test dispatcher keeps the service in virtual time`() = runTest {
        val monitor = RateMonitor(backgroundScope, fx, every = 1.minutes, io = StandardTestDispatcher(testScheduler))
        monitor.start()
        advanceTimeBy(1.hours)
        assertEquals(60, monitor.polls)                         // una hora de sondeo, en milisegundos
        assertEquals(BigDecimal("0.92"), monitor.latest.value)
        monitor.stop()
        advanceTimeBy(1.hours)
        assertEquals(60, monitor.polls)                         // después de stop no hubo más
    }

    @Test
    fun `advanceTimeBy stops just before the instant and runCurrent runs what is due`() = runTest {
        val monitor = RateMonitor(backgroundScope, fx, every = 1.minutes, io = StandardTestDispatcher(testScheduler))
        monitor.start()
        assertNull(monitor.latest.value)                        // StandardTestDispatcher: nada corrió todavía
        advanceTimeBy(99)
        assertNull(monitor.latest.value)                        // la tasa llega a los 100 ms…
        advanceTimeBy(1)
        runCurrent()
        assertEquals(BigDecimal("0.92"), monitor.latest.value)  // …y no antes
    }

    @Test
    fun `a hardcoded IO dispatcher escapes virtual time`() = runTest {
        val (quote, real) = measureTimedValue { HardcodedIoQuoter(300.milliseconds).quote() }
        assertEquals(Money.of("100.00"), quote)
        assertEquals(0, currentTime)                            // el reloj virtual no se movió…
        assertTrue(real >= 300.milliseconds)                    // …y la prueba esperó de verdad
    }

    @Test
    fun `the transfer deadline fires at the exact instant`() = runTest {
        val e = assertFailsWith<TransferTimedOut> { withTransferDeadline { delay(1.minutes) } }
        assertIs<TimeoutCancellationException>(e.cause)
        assertEquals(30_000, currentTime)
        assertEquals("ok", withTransferDeadline { delay(29.seconds); "ok" })
    }

    @Test
    fun `standard and unconfined test dispatchers order work differently`() = runTest {
        val standard = mutableListOf<String>()
        launchOrder(StandardTestDispatcher(testScheduler), standard)
        assertEquals(listOf("antes", "después"), standard)      // los hijos esperan a que la prueba ceda
        advanceUntilIdle()
        assertEquals(listOf("antes", "después", "hijo 1", "hijo 2"), standard)

        val unconfined = mutableListOf<String>()
        launchOrder(UnconfinedTestDispatcher(testScheduler), unconfined)
        assertEquals(listOf("antes", "hijo 1", "hijo 2", "después"), unconfined)   // arrancan en el acto
    }

    @Test
    fun `backgroundScope is cancelled when the test ends`() = runTest {
        var ticks = 0
        backgroundScope.launch { while (true) { delay(1.seconds); ticks++ } }   // en launch normal, la prueba no terminaría
        advanceTimeBy(10.seconds + 1.milliseconds)
        assertEquals(10, ticks)
    }

    @Test
    fun `Turbine checks emissions one by one`() = runTest {
        val monitor = RateMonitor(backgroundScope, fx, every = 1.minutes, io = StandardTestDispatcher(testScheduler))
        monitor.latest.test {
            assertNull(awaitItem())
            monitor.start()
            assertEquals(BigDecimal("0.92"), awaitItem())
            expectNoEvents()                                    // la misma tasa otra vez no se emite (StateFlow)
            monitor.stop()
        }
    }

    @Test
    fun `a StateFlow conflates for a collector that is not running`() = runTest {
        val state = MutableStateFlow(0)
        state.test {                                            // Turbine recolecta sin confinar (Unconfined)…
            assertEquals(0, awaitItem())
            state.value = 1
            state.value = 2
            assertEquals(1, awaitItem())                        // …así que ve cada valor que la prueba asigna
            assertEquals(2, awaitItem())
        }

        val seen = mutableListOf<Int>()
        backgroundScope.launch(StandardTestDispatcher(testScheduler)) { state.toList(seen) }
        runCurrent()
        state.value = 3
        state.value = 4                                         // el colector no corrió entre los dos
        runCurrent()
        assertEquals(listOf(2, 4), seen)                        // el 3 se perdió: StateFlow es conflated
    }

    @Test
    fun `setMain replaces Dispatchers Main in tests`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            assertEquals("main", withContext(Dispatchers.Main) { delay(1.seconds); "main" })
            assertEquals(1_000, currentTime)                    // Main también está en el reloj virtual
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `an hour of market session in milliseconds and always the same result`() {
        val results = mutableSetOf<SessionSummary>()
        val real = measureTime {
            repeat(3) { results += runTestReturning { summarize(sessionFeed(seed = 42, minutes = 60)) } }
        }
        assertEquals(1, results.size)                           // determinista: tres corridas, un solo resultado
        assertEquals(5 * 60, results.single().candles)          // 60 velas por ticker
        assertTrue(real < 10.seconds, "tardó $real")
    }

    private fun <T> runTestReturning(block: suspend () -> T): T {
        var result: T? = null
        runTest { result = block() }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}
