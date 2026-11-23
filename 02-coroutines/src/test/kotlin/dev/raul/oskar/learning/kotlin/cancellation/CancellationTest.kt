// currentTime, runCurrent y advanceTimeBy piden opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.cancellation

import dev.raul.oskar.learning.kotlin.cases.ACC_EUR
import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import java.math.BigDecimal
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class CancellationTest {

    private fun batchCancelledAtThree(cooperative: Boolean): List<Int> {
        val processed = mutableListOf<Int>()
        runTest {
            lateinit var job: Job
            job = launch { processBatch((1..10).toList(), cooperative, processed) { if (it == 3) job.cancel() } }
            job.join()
            assertTrue(job.isCancelled)                         // en los dos casos el Job quedó cancelado…
        }
        return processed
    }

    @Test
    fun `a loop without checks ignores cancellation`() {
        assertEquals((1..10).toList(), batchCancelledAtThree(cooperative = false))  // …pero este hizo todo el lote
    }

    @Test
    fun `ensureActive makes the loop cooperative`() {
        assertEquals(listOf(1, 2, 3), batchCancelledAtThree(cooperative = true))
    }

    @Test
    fun `catching Exception swallows the cancellation until the next suspension`() = runTest {
        val log = mutableListOf<String>()
        val job = launch { swallowingCancellation(log) }
        runCurrent()
        job.cancelAndJoin()
        assertEquals(listOf("atrapada: JobCancellationException", "siguió trabajando"), log)
    }

    @Test
    fun `rethrowing keeps cancellation working`() = runTest {
        val log = mutableListOf<String>()
        val job = launch { rethrowingCancellation(log) }
        runCurrent()
        job.cancelAndJoin()
        assertEquals(listOf("relanzada"), log)
    }

    @Test
    fun `runCatching swallows cancellation and the cancellable variant does not`() {
        assertTrue(runCatching { throw CancellationException("x") }.isFailure)
        assertFailsWith<CancellationException> { runCatchingCancellable { throw CancellationException("x") } }
        assertEquals("boom", runCatchingCancellable { error("boom") }.exceptionOrNull()?.message)
    }

    private val slowFx = FxRateClient { _, _ -> delay(500); BigDecimal("0.92") }

    @Test
    fun `withTimeoutOrNull returns null exactly at the limit`() = runTest {
        assertNull(rateOrNull(slowFx, Money.USD, Money.EUR, 100.milliseconds))
        assertEquals(100, currentTime)
        assertEquals(BigDecimal("0.92"), rateOrNull(slowFx, Money.USD, Money.EUR, 600.milliseconds))
    }

    @Test
    fun `withTimeout is a cancellation, so the domain error wraps it`() = runTest {
        val e = assertFailsWith<FxTimeout> { rateOrFail(slowFx, Money.USD, Money.EUR, 100.milliseconds) }
        assertIs<TimeoutCancellationException>(e.cause)
        assertIs<CancellationException>(e.cause)
    }

    @Test
    fun `an uncaught timeout ends a launch silently`() = runTest {
        val job = launch { withTimeout(10) { delay(100) } }
        job.join()
        assertTrue(job.isCancelled)                             // cancelado, no fallido: runTest no se entera
    }

    private fun accounts() = RemoteAccounts(mapOf(ACC_USD to Money.of("1000.00")), latency = 20.milliseconds)

    @Test
    fun `a successful transfer debits and credits`() = runTest {
        val accounts = accounts()
        val fx = FxRateClient { _, _ -> delay(50); BigDecimal("0.92") }
        val got = CompensatingTransfer(accounts, fx, 1_000.milliseconds).transfer(ACC_USD, ACC_EUR, Money.of("100.00"), Money.EUR)
        assertEquals(Money.of("92.00", Money.EUR), got)
        assertEquals(Money.of("900.00"), accounts.balanceOf(ACC_USD))
        assertEquals(90, currentTime)                           // 20 + 50 + 20
    }

    @Test
    fun `a timeout reverts the debit`() = runTest {
        val accounts = accounts()
        assertFailsWith<FxTimeout> {
            CompensatingTransfer(accounts, slowFx, 100.milliseconds).transfer(ACC_USD, ACC_EUR, Money.of("100.00"), Money.EUR)
        }
        assertEquals(Money.of("1000.00"), accounts.balanceOf(ACC_USD))
        assertEquals(listOf("debit $ACC_USD 100.00 USD", "credit $ACC_USD 100.00 USD"), accounts.log)
    }

    private suspend fun TestScope.cancelDuringFx(nonCancellable: Boolean): RemoteAccounts {
        val accounts = accounts()
        val transfer = CompensatingTransfer(accounts, slowFx, 1_000.milliseconds, nonCancellable)
        val job = launch { transfer.transfer(ACC_USD, ACC_EUR, Money.of("100.00"), Money.EUR) }
        advanceTimeBy(100)                                      // ya debitó (20 ms) y espera la tasa
        job.cancelAndJoin()
        return accounts
    }

    @Test
    fun `cancellation after the debit is compensated under NonCancellable`() = runTest {
        assertEquals(Money.of("1000.00"), cancelDuringFx(nonCancellable = true).balanceOf(ACC_USD))
    }

    @Test
    fun `without NonCancellable the compensation never runs`() = runTest {
        val accounts = cancelDuringFx(nonCancellable = false)
        assertEquals(Money.of("900.00"), accounts.balanceOf(ACC_USD))   // el dinero desapareció
        assertEquals(listOf("debit $ACC_USD 100.00 USD"), accounts.log)
    }

    private val follow: suspend (String) -> Int = { t -> delay(50); if (t == "LUMA") error("feed caído: $t") else t.length }

    @Test
    fun `supervisorScope keeps the siblings alive`() = runTest {
        val results = followEach(TICKERS, follow)
        assertEquals(4, results.values.count { it.isSuccess })
        assertEquals("feed caído: LUMA", results.getValue("LUMA").exceptionOrNull()?.message)
    }

    @Test
    fun `coroutineScope is all or nothing`() = runTest {
        val e = assertFailsWith<IllegalStateException> { followAllOrNothing(TICKERS, follow) }
        assertEquals("feed caído: LUMA", e.message)
    }

    @Test
    fun `the exception handler only works on the root coroutine`() = runTest {
        val seen = mutableListOf<String>()
        val scope = CoroutineScope(SupervisorJob() + coroutineContext.minusKey(Job))
        scope.launchWithHandler(seen) { error("falla en la raíz") }.join()
        assertEquals(listOf("handler: falla en la raíz"), seen)

        val rootSeen = mutableListOf<String>()
        val withRootHandler = CoroutineScope(SupervisorJob() + coroutineContext.minusKey(Job) +
            CoroutineExceptionHandler { _, e -> rootSeen += "raíz: ${e.message}" })
        val childSeen = mutableListOf<String>()
        withRootHandler.handlerOnChild(childSeen).join()
        assertTrue(childSeen.isEmpty())                         // el handler del hijo nunca corrió
        assertEquals(listOf("raíz: falla del hijo"), rootSeen)  // la excepción subió a la raíz
    }

    @Test
    fun `launch with a SupervisorJob leaves the hierarchy`() = runTest {
        val log = mutableListOf<String>()
        val detached = detachedBySupervisorJob(log)
        assertEquals(0, currentTime)                            // coroutineScope no la esperó
        assertTrue(log.isEmpty())
        detached.join()
        assertEquals(listOf("terminó por su cuenta"), log)
    }
}
