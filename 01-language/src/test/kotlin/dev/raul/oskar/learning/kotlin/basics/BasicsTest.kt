package dev.raul.oskar.learning.kotlin.basics

import java.math.BigDecimal
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
        assertEquals(BigDecimal("0.30"), money("0.1") + money("0.2"))
        assertNotEquals(0.3, 0.1 + 0.2)
    }

    @Test
    fun `tax uses banker rounding`() {
        assertEquals(BigDecimal("119.00"), addTax(money("100"), BigDecimal("0.19")))
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
