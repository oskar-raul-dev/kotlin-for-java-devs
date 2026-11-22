package dev.raul.oskar.learning.kotlin.functional

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FunctionalTest {

    @Test
    fun `higher order functions and closures`() {
        assertEquals(18, applyTwice(multiplier(3), 2))
        assertEquals(15, ({ x: Int -> x + 1 } andThen multiplier(3))(4))
        assertEquals(5, add(2)(3))
        assertEquals("<b>x</b>", html { append("<b>x</b>") })
    }

    @Test
    fun `reified generics`() {
        assertEquals(listOf("a", "b"), filterIsType<String>(listOf(1, "a", 2.0, "b")))
        assertEquals(listOf(1), filterIsType<Int>(listOf(1, "a")))
    }

    @Test
    fun `memoize calls the function once per key`() {
        var calls = 0
        val f = memoize<Int, Int> { calls++; it * 2 }
        f(1); f(1); f(2)
        assertEquals(2, calls)
    }

    @Test
    fun `fold and result`() {
        assertEquals(2, countIf(listOf(1, 2, 3, 4)) { it % 2 == 0 })
        assertEquals(0, countIf(emptyList<Int>()) { true })
        assertEquals(listOf(1, 3) to listOf("dos", "4x"), parseAll(listOf("1", "dos", " 3 ", "4x")))
    }

    @Test
    fun `scope functions`() {
        val log = mutableListOf<String>()
        val ticket = scopeFunctions(log)
        assertEquals(listOf("creada ORD-1"), log)
        assertEquals(15, ticket.quantity)
        assertTrue(ticket.submitted)
        assertEquals("ORD-1: 15 ACME enviada=true", summary(ticket))
    }

    @Test
    fun `fun interface composition`() {
        val v = notBlank and shortText
        assertTrue(v.validate("hola"))
        assertFalse(v.validate("  "))
        assertFalse(v.validate("demasiado largo"))
        assertEquals(10L, digitSum(1234))
    }

    @Test
    fun `grouping pipelines`() {
        assertEquals(mapOf("ACME" to 1_400L, "ORBX" to 2_200L, "NUBE" to 5_000L), volumeByTicker(sampleTicks))
        assertEquals(4, ticksPerTicker(sampleTicks)["ACME"])
        assertEquals(BigDecimal("101.10"), highestByTicker(sampleTicks).getValue("ACME").price)
        val (large, small) = splitLarge(sampleTicks, 1_000)
        assertEquals(3, large.size)
        assertEquals(4, small.size)
        assertEquals(setOf(1, 2), daysPerTicker(sampleTicks)["ACME"])
    }

    @Test
    fun `a candle in one pass with fold`() {
        val day1 = sampleTicks.filter { it.ticker == "ACME" && it.day == 1 }
        assertEquals(
            Candle(BigDecimal("100.00"), BigDecimal("100.40"), BigDecimal("99.80"), BigDecimal("99.80"), 1_000),
            candleOf(day1),
        )
        assertNull(candleOf(emptyList()))
    }

    @Test
    fun `sequences are lazy`() {
        val trace = mutableListOf<Int>()
        assertEquals(listOf(64, 81, 100), firstSquaresAbove(50, 3, trace))
        assertEquals((1..10).toList(), trace)   // no generó más de lo necesario
        assertEquals(listOf(3, -4, 11), deltas(listOf(10, 13, 9, 20)))
    }
}
