// currentTime, runCurrent y advanceTimeBy piden opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.fundamentals

import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FundamentalsTest {

    // runTest usa tiempo virtual: delay(300) avanza el reloj de la prueba al instante, sin esperar.

    @Test
    fun `parallel quotes take as long as the slowest source`() = runTest {
        val prices = quoteInParallel(TICKERS, SimulatedPriceSource())
        assertEquals(5, prices.size)
        assertEquals(300, currentTime)                         // LUMA, la más lenta
    }

    @Test
    fun `sequential quotes add up every latency`() = runTest {
        quoteSequentially(TICKERS, SimulatedPriceSource())
        assertEquals(120L + 80 + 300 + 50 + 150, currentTime)
    }

    @Test
    fun `portfolio value multiplies prices by quantities`() = runTest {
        val value = portfolioValue(mapOf("ACME" to 10L, "ORBX" to 25L, "NUBE" to 100L), SimulatedPriceSource())
        assertEquals(Money.of("2797.50"), value)               // 1000.00 + 1062.50 + 735.00
    }

    @Test
    fun `one failing source cancels its siblings`() = runTest {
        val source = SimulatedPriceSource(failing = setOf("ORBX"))
        val e = assertFailsWith<QuoteUnavailable> { quoteInParallel(TICKERS, source) }
        assertEquals("ORBX", e.ticker)
        assertEquals(80, currentTime)                          // falló a los 80 ms y no esperó a nadie más
        assertEquals(TICKERS.toSet(), source.started.toSet())  // todas empezaron…
        assertEquals(listOf("KAPA"), source.finished.toList()) // …y solo terminó la de 50 ms
    }

    @Test
    fun `the bank asks for balance and rate at the same time`() = runTest {
        val quote = prepareTransfer(
            ACC_USD, Money.USD, Money.EUR,
            balances = { delay(200); Money.of("1500.00") },
            fx = { _, _ -> delay(150); BigDecimal("0.92") },
        )
        assertEquals(TransferQuote(Money.of("1500.00"), BigDecimal("0.92")), quote)
        assertEquals(200, currentTime)
    }

    @Test
    fun `suspend does not mean another thread`() = runBlocking {
        assertEquals(Thread.currentThread().name, currentThreadName())
    }

    @Test
    fun `coroutineScope waits for launch and async`() = runTest {
        val log = mutableListOf<String>()
        assertEquals(42, launchVersusAsync(log))
        assertEquals(listOf("las dos lanzadas (job activo: true)", "async terminó", "launch terminó"), log)
        assertEquals(100, currentTime)                         // esperó también al launch, que nadie esperó a mano
    }

    @Test
    fun `an async failure cancels the scope even without await`() = runTest {
        val log = mutableListOf<String>()
        val e = assertFailsWith<IllegalStateException> { asyncFailureWithoutAwait(log) }
        assertEquals("la fuente falló", e.message)
        assertTrue(log.isEmpty())                              // el hermano quedó cancelado
        assertEquals(10, currentTime)
    }

    @Test
    fun `cancelling the parent cancels every child`() = runTest {
        assertEquals(listOf(true, true), cancelParentCancelsChildren())
        assertEquals(0, currentTime)                           // nadie esperó los 10 s
    }

    @Test
    fun `a hundred thousand coroutines are cheap`() = runTest {
        assertEquals(100_000, manyCoroutines(100_000))
        assertEquals(1_000, currentTime)                       // todas esperaron a la vez
    }

    @Test
    fun `GlobalScope leaks while an owned scope does not`() = runTest {
        val release = CompletableDeferred<Unit>()
        val leaked = coroutineScope { leakWithGlobalScope(release) }
        assertTrue(leaked.isActive)                            // el scope terminó y la corrutina sigue viva
        release.complete(Unit)
        leaked.join()

        val owned = CompletableDeferred<Unit>()
        val job = coroutineScope {
            val j = ownedLaunch(owned)
            owned.complete(Unit)                               // sin esto, coroutineScope esperaría para siempre
            j
        }
        assertFalse(job.isActive)                              // coroutineScope no volvió hasta que su hijo terminó
    }
}
