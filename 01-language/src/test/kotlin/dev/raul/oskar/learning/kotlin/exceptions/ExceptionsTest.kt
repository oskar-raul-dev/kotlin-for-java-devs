package dev.raul.oskar.learning.kotlin.exceptions

import dev.raul.oskar.learning.kotlin.basics.Money
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Path
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExceptionsTest {

    @Test
    fun `try is an expression and checked exceptions need no declaration`() {
        assertNull(readOrNull(Path.of("no-existe-${System.nanoTime()}.txt")))
    }

    @Test
    fun `throw fits in an elvis`() {
        assertEquals(9000, portFrom(mapOf("PORT" to "9000")))
        val e = assertFailsWith<IllegalArgumentException> { portFrom(mapOf("PORT" to "x")) }
        assertEquals("PORT ausente o no numérico", e.message)
    }

    @Test
    fun `require check and error map to different exceptions`() {
        val w = Wallet(100)
        assertFailsWith<IllegalArgumentException> { w.withdraw(0) }
        assertFailsWith<IllegalStateException> { w.withdraw(500) }
        assertEquals(60, w.withdraw(40))
        w.close()
        val e = assertFailsWith<IllegalStateException> { w.withdraw(1) }
        assertEquals("billetera cerrada", e.message)
    }

    @Test
    fun `requireNotNull smart casts`() {
        assertEquals(BigDecimal("250.00"), parseAmount(" 250.00 "))
        assertEquals("monto nulo", assertFailsWith<IllegalArgumentException> { parseAmount(null) }.message)
        assertFailsWith<IllegalArgumentException> { parseAmount("diez") }
        assertFailsWith<IllegalArgumentException> { parseAmount("-5") }
        assertFailsWith<IllegalArgumentException> { parseAmount("1.005") }
    }

    @Test
    fun `catch the parent and dispatch with when`() {
        assertEquals("número inválido", classify { "x".toInt() })
        assertEquals("aritmética", classify { divide(1, 0) })
        assertEquals("ok", classify { })
        assertFailsWith<UnsupportedOperationException> { classify { throw UnsupportedOperationException() } }
    }

    @Test
    fun `finally always runs but does not change the result`() {
        val log = mutableListOf<String>()
        assertEquals(1, withFinally(log, fail = false))
        assertEquals(listOf("try", "finally"), log)
        log.clear()
        assertEquals(-1, withFinally(log, fail = true))
        assertEquals(listOf("try", "catch", "finally"), log)
    }

    private val usd = "ACC-00001234-82"
    private val eur = "ACC-00005678-39"
    private fun service(fx: FxRateClient = FxRateClient { _, _ -> BigDecimal("0.92") }) =
        TransferService(fx, mapOf(usd to Money.of("1000"), eur to Money.zero(Money.EUR)), frozen = setOf("ACC-00009999-99"))

    @Test
    fun `domain exceptions keep the cause`() {
        val down = service { _, _ -> throw IOException("connection refused") }
        val e = assertFailsWith<FxUnavailable> { down.transfer("k-1", usd, eur, Money.of("10")) }
        assertIs<IOException>(e.cause)
        assertEquals(503, httpStatusFor(e))
        assertEquals("connection refused", rootCause(e).message)
        assertEquals(2, causeChain(e).size)
        assertEquals(Money.of("1000"), down.balanceOf(usd))          // la falla no dejó la transferencia a medias
    }

    @Test
    fun `transfer converts currency and rejects a reused key`() {
        val s = service()
        assertEquals(Money.of("900"), s.transfer("k-1", usd, eur, Money.of("100")))
        assertEquals(Money.of("92.00", Money.EUR), s.balanceOf(eur))
        assertEquals(409, httpStatusFor(assertFailsWith<DuplicateTransfer> { s.transfer("k-1", usd, eur, Money.of("1")) }))
    }

    @Test
    fun `sealed hierarchy maps to status codes`() {
        val s = service()
        val poor = assertFailsWith<InsufficientFunds> { s.transfer("k-2", usd, eur, Money.of("5000")) }
        assertEquals(Money.of("1000"), poor.available)
        assertEquals(422, httpStatusFor(poor))
        assertEquals(423, httpStatusFor(assertFailsWith<AccountFrozen> { s.transfer("k-3", "ACC-00009999-99", usd, Money.of("1")) }))
        assertEquals(400, httpStatusFor(assertFailsWith<CurrencyMismatch> { s.transfer("k-4", usd, eur, Money.of("1", Money.EUR)) }))
        assertEquals(500, httpStatusFor(assertFailsWith<UnbalancedEntry> { checkBalanced(listOf(Money.of("1001")), listOf(Money.of("1000"))) }))
        checkBalanced(listOf(Money.of("1000"), Money.of("1")), listOf(Money.of("1001")))   // débitos = créditos
    }

    @Test
    fun `require and check separate client errors from our bugs`() {
        val s = service()
        assertFailsWith<IllegalArgumentException> { s.transfer("k-5", usd, eur, Money.of("-1")) }   // 400: el cliente
        assertFailsWith<IllegalArgumentException> { s.transfer("k-6", usd, usd, Money.of("1")) }
        assertFailsWith<IllegalArgumentException> { s.balanceOf("ACC-00000000-00") }
    }

    @Test
    fun `result helpers`() {
        assertEquals(8443, parsePort("8443").getOrThrow())
        assertIs<NumberFormatException>(parsePort("abc").exceptionOrNull())
        assertIs<IllegalArgumentException>(parsePort("70000").exceptionOrNull())
        assertEquals(8080, portOrDefault("abc"))
        assertEquals("inválido (NumberFormatException)", portOrExplain("abc"))
        assertEquals(8080, portRecoveringFormat("abc").getOrThrow())
        assertTrue(portRecoveringFormat("70000").isFailure)       // ese error no se recupera
        assertEquals(160, doubledPort("80").getOrThrow())
        assertIs<IllegalStateException>(doubledPort("50000").exceptionOrNull())
    }

    @Test
    fun `runCatching swallows cancellation, the safe variant rethrows it`() {
        assertTrue(runCatching { throw CancellationException("cancelado") }.isFailure)
        assertFailsWith<CancellationException> { runCatchingCancellable { throw CancellationException("cancelado") } }
        assertTrue(runCatchingCancellable { "x".toInt() }.isFailure)
    }

    @Test
    fun `validation accumulates every error`() {
        assertEquals("ok: 250.00 USD de $usd a $eur", describe(validate(usd, eur, "250")))
        val v = validate("ACC-1234", "ACC-1234", "-5")
        assertIs<Validation.Invalid>(v)
        assertEquals(4, v.errors.size)                              // las cuatro reglas a la vez, no solo la primera
    }

    @Test
    fun `use closes in reverse order and keeps suppressed exceptions`() {
        val log = mutableListOf<String>()
        val e = assertFailsWith<IllegalStateException> { useTwo(log, failWork = true, failClose = true) }
        assertEquals("falla en work de b", e.message)
        assertEquals(listOf("falla en close de b"), e.suppressed.map { it.message })
        assertEquals(listOf("open a", "open b", "work a", "work b", "close b", "close a"), log)
    }

    @Test
    fun `close failure alone propagates`() {
        val e = assertFailsWith<IllegalStateException> { useTwo(mutableListOf(), failClose = true) }
        assertEquals("falla en close de b", e.message)
    }

    @Test
    fun `throws annotation is only for java callers`() {
        assertEquals("{}", loadConfigForJava("a.json"))
        assertFailsWith<IOException> { loadConfigForJava("a.yaml") }
    }
}
