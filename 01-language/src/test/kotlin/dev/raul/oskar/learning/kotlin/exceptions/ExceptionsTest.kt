package dev.raul.oskar.learning.kotlin.exceptions

import java.io.IOException
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
        assertEquals(30, parseAge(" 30 "))
        assertEquals("edad nula", assertFailsWith<IllegalArgumentException> { parseAge(null) }.message)
        assertFailsWith<IllegalArgumentException> { parseAge("doce") }
        assertFailsWith<IllegalArgumentException> { parseAge("200") }
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

    @Test
    fun `domain exceptions keep the cause`() {
        val service = OrderService({ throw IOException("connection refused") })
        val e = assertFailsWith<InventoryUnavailable> { service.canPlace("sku-1", 10) }
        assertIs<IOException>(e.cause)
        assertEquals(503, httpStatusFor(e))
        assertEquals("connection refused", rootCause(e).message)
        assertEquals(2, causeChain(e).size)
    }

    @Test
    fun `sealed hierarchy maps to status codes`() {
        val service = OrderService({ 700 }, mapOf("O-1" to 5000))
        val bad = assertFailsWith<InvalidOrderRequest> { service.canPlace("sku", -1) }
        assertEquals("quantity", bad.field)
        assertEquals(400, httpStatusFor(bad))
        assertEquals(404, httpStatusFor(assertFailsWith<OrderNotFound> { service.totalOf("O-9") }))
        assertEquals(5000, service.totalOf("O-1"))
        assertTrue(service.canPlace("sku", 1))
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
        assertEquals("ok: Ana", describe(validate("Ana", 30, "ana@example.com")))
        val v = validate("", 12, "sin-arroba")
        assertIs<Validation.Invalid>(v)
        assertEquals(3, v.errors.size)
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
