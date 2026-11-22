package dev.raul.oskar.learning.kotlin.basics

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BasicsTest {

    @Test
    fun `integer division truncates toward zero`() {
        assertEquals(3, integerDivision(7, 2))
        assertEquals(-3, integerDivision(-7, 2))
        assertEquals(3.5, realDivision(7, 2))
    }

    @Test
    fun `int overflows silently but addExact throws`() {
        assertEquals(Int.MIN_VALUE, overflowingAdd(Int.MAX_VALUE, 1))
        assertFailsWith<ArithmeticException> { checkedAdd(Int.MAX_VALUE, 1) }
    }

    @Test
    fun `toIntOrNull avoids NumberFormatException`() {
        assertEquals(12, parseOrDefault(" 12 "))
        assertEquals(-1, parseOrDefault("doce", -1))
    }

    @Test
    fun `BigDecimal equals compares scale, compareTo does not`() {
        assertNotEquals(BigDecimal("1.0"), BigDecimal("1.00"))
        assertTrue(sameAmount(BigDecimal("1.0"), BigDecimal("1.00")))
        assertEquals(BigDecimal("0.3"), fromText("0.1") + fromText("0.2"))
        assertNotEquals(0.3, 0.1 + 0.2)
    }

    @Test
    fun `BigDecimal from a double carries the binary error`() {
        assertNotEquals(BigDecimal("0.1"), fromDouble(0.1))
        assertTrue(fromDouble(0.1).toPlainString().startsWith("0.1000000000000000055511"))
    }

    @Test
    fun `commission uses banker rounding by default`() {
        // 0.125 -> 0.12 con HALF_EVEN (el 2 es par) y 0.13 con HALF_UP
        assertEquals(BigDecimal("0.12"), commission(BigDecimal("125"), BigDecimal("0.001")))
        assertEquals(BigDecimal("0.13"), commission(BigDecimal("125"), BigDecimal("0.001"), RoundingMode.HALF_UP))
    }

    @Test
    fun `money factories normalize the scale and the constructor validates it`() {
        assertEquals(Money.of("1.00"), Money.of("1.0"))               // data class segura: misma escala siempre
        assertEquals("19.99 USD", Money.ofCents(1999).toString())
        assertEquals(Money.of("2.34"), Money.of("2.345"))             // HALF_EVEN
        assertEquals(Money.of("2.35"), Money.of("2.345", mode = RoundingMode.HALF_UP))
        assertFailsWith<IllegalArgumentException> { Money(BigDecimal("1.5"), Money.USD) }
    }

    @Test
    fun `money operators require the same currency`() {
        val price = Money.of("100.00")
        assertEquals(Money.of("1001.00"), price * 10 + Money.ofCents(100))
        assertEquals(Money.of("-100.00"), -price)
        assertEquals(Money.of("4.32"), Money.of("1234.56").times(BigDecimal("0.0035")))   // 4.32096 -> 4.32
        assertTrue(Money.of("99.99") < price)
        assertTrue((Money.zero() - price).isNegative)
        assertFailsWith<IllegalArgumentException> { price + Money.of("1", Money.EUR) }
        assertFailsWith<IllegalArgumentException> { price < Money.of("1", Money.EUR) }
    }

    @Test
    fun `double equals is structural and triple equals is identity`() {
        val a = Point(1, 2)
        val b = Point(1, 2)
        assertTrue(structurallyEqual(a, b))
        assertFalse(sameReference(a, b))
        assertTrue(sameReference(a, a))
    }

    @Test
    fun `describe uses smart casts`() {
        assertEquals("null", describe(null))
        assertEquals("String(len=4)", describe("hola"))
        assertEquals("Long(2)", describe(2L))
        assertEquals("Otro(Point)", describe(Point(0, 0)))
    }

    @Test
    fun `chars are not numbers`() {
        assertEquals('b', nextChar('a'))
        assertEquals(97, codeOf('a'))
    }

    @Test
    fun `nothing-returning function always throws`() {
        assertFailsWith<IllegalStateException> { fail("boom") }
    }
}
