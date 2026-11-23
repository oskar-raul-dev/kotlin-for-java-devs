// currentTime y runCurrent piden opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.internals

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.BigDecimal
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class InternalsTest {

    /** Un cliente de callbacks controlado por la prueba: guarda los callbacks y cuenta las cancelaciones. */
    private class FakeClient : CallbackQuoteClient {
        val pending = mutableMapOf<String, Pair<(Money) -> Unit, (Throwable) -> Unit>>()
        var cancelled = 0
        override fun request(ticker: String, onSuccess: (Money) -> Unit, onError: (Throwable) -> Unit): Cancellable {
            pending[ticker] = onSuccess to onError
            return Cancellable { cancelled++; pending.remove(ticker) }
        }
    }

    @Test
    fun `a suspend function returns its value or the suspended marker`() {
        assertEquals(42, callWithContinuation({ 42 }, RecordingContinuation()))
        val parking = Parking<Int>()
        val continuation = RecordingContinuation<Int>()
        assertSame(COROUTINE_SUSPENDED, callWithContinuation({ parking.park() + 1 }, continuation))
        assertNull(continuation.result)                         // todavía no terminó
        parking.parked!!.resumeWith(Result.success(41))
        assertEquals(Result.success(42), continuation.result)   // siguió donde estaba, en este mismo hilo
    }

    @Test
    fun `startCoroutine runs a coroutine without kotlinx`() {
        val finished = startBare { "sin dispatcher" }
        assertEquals(Result.success("sin dispatcher"), finished.result)
        val failed = startBare<Int> { throw IOException("feed caído") }
        assertTrue(failed.result!!.exceptionOrNull() is IOException)
    }

    @Test
    fun `live locals become fields of the state machine`() {
        val fields = stateMachineFields {
            val price = 100
            delay(1)
            println(price)                                      // price sigue viva después del delay
        }
        assertTrue("label" in fields)                           // el estado actual
        assertTrue(fields.any { it.startsWith("I$") })          // el Int que sobrevive a la suspensión
    }

    @Test
    fun `a callback API becomes a suspend function`() = runTest {
        val client = FakeClient()
        val quote = async { client.quote("ACME") }
        runCurrent()
        client.pending.getValue("ACME").first(Money.of("100.25"))
        assertEquals(Money.of("100.25"), quote.await())
    }

    @Test
    fun `callback errors arrive as exceptions`() = runTest {
        val client = FakeClient()
        val quote = async { runCatching { client.quote("ORBX") } }
        runCurrent()
        client.pending.getValue("ORBX").second(IOException("timeout del proveedor"))
        assertEquals("timeout del proveedor", quote.await().exceptionOrNull()?.message)
    }

    @Test
    fun `cancelling the coroutine cancels the request`() = runTest {
        val client = FakeClient()
        val job = launch { client.quote("LUMA") }
        runCurrent()
        job.cancel()
        runCurrent()
        assertEquals(1, client.cancelled)
        assertTrue(client.pending.isEmpty())
    }

    @Test
    fun `suspendCoroutine does not hear about cancellation`() = runTest {
        val client = FakeClient()
        val job = launch { client.quoteIgnoringCancellation("KAPA") }
        runCurrent()
        job.cancel()
        runCurrent()
        assertEquals(0, client.cancelled)                       // el pedido sigue vivo en el cliente
        assertFalse(job.isActive)                               // está cancelándose (Cancelling)…
        assertFalse(job.isCompleted)                            // …pero no puede terminar: espera el callback
        client.pending.getValue("KAPA").first(Money.of("250.00"))
        job.join()
        assertTrue(job.isCancelled)                             // recién al llegar el callback termina, cancelada
    }

    @Test
    fun `resuming twice throws`() = runTest {
        val e = assertFailsWith<IllegalStateException> { resumeTwice() }
        assertEquals("Already resumed", e.message)
    }

    @Test
    fun `sequence is lazy and needs no threads`() {
        val generated = mutableListOf<Money>()
        val walk = priceWalk(Money.of("100.00"), BigDecimal("0.01"), generated)
        assertTrue(generated.isEmpty())                         // nada corre hasta pedir
        assertEquals(listOf("100.00", "101.00", "99.99"), walk.take(3).map { it.amount.toPlainString() }.toList())
        assertEquals(3, generated.size)
        assertEquals(listOf("NEW", "PARTIAL 30", "PARTIAL 80", "FILLED 80"), orderLifecycle(listOf(30, 50)).toList())
    }

    @Test
    fun `context elements travel with the coroutine`() = runTest {
        assertNull(currentRequestId())
        assertEquals("req-1", withContext(QuoteRequestId("req-1")) { currentRequestId() })
        val child = withContext(QuoteRequestId("req-2")) { async { delay(10); currentRequestId() }.await() }
        assertEquals("req-2", child)                            // el hijo lo heredó
    }
}
