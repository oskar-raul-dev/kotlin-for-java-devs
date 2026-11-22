package dev.raul.oskar.learning.kotlin.control

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ControlTest {

    @Test
    fun `when with ranges`() {
        assertEquals(BigDecimal("0.0010"), commissionRate(9_999))
        assertEquals(BigDecimal("0.0005"), commissionRate(10_000))   // ..< excluye el borde superior del tramo
        assertEquals(BigDecimal("0.0002"), commissionRate(100_000))
        assertFailsWith<IllegalArgumentException> { commissionRate(-1) }
    }

    @Test
    fun `when without subject`() {
        assertEquals("FizzBuzz", fizzBuzz(30))
        assertEquals("Fizz", fizzBuzz(9))
        assertEquals("Buzz", fizzBuzz(10))
        assertEquals("7", fizzBuzz(7))
        assertTrue(isWeekend("Sunday"))
        assertFalse(isWeekend("monday"))
    }

    @Test
    fun `elvis return exits early`() {
        assertEquals(0, firstWordLength(null))
        assertEquals(0, firstWordLength("   "))
        assertEquals(4, firstWordLength(" hola mundo"))
    }

    @Test
    fun `ranges until downTo and step`() {
        assertEquals(5050, sumTo(100))
        assertEquals(listOf(0, 1, 2), indicesUntil(3))
        assertEquals(listOf(10, 7, 4, 1), countdown(10, 3))
        assertEquals(listOf("0:a", "1:b"), indexed(listOf("a", "b")))
        assertEquals(1, digits(0))
        assertEquals(5, digits(-12345))
    }

    @Test
    fun `labeled break leaves outer loop`() {
        assertEquals(3 to 8, firstPairWithSum(listOf(3, 8, 1, 7, 4), 11))
        assertNull(firstPairWithSum(listOf(1, 2), 100))
    }

    @Test
    fun `classic algorithms`() {
        assertEquals(12L, gcd(84, 36))
        assertEquals(listOf(2, 3, 5, 7, 11, 13), (1..13).filter(::isPrime))
        assertEquals(55L, fibonacci(10))
        assertEquals(12586269025L, fibonacci(50))
        val prices = intArrayOf(9_950, 9_980, 10_000, 10_020, 10_050)          // centavos
        assertContentEquals(prices, insertionSort(intArrayOf(10_020, 9_950, 10_050, 9_980, 10_000)))
        assertEquals(3, binarySearch(prices, 10_020))
        assertEquals(-1, binarySearch(prices, 10_010))
        assertEquals(0 to 1, twoSum(intArrayOf(2, 7, 11, 15), 9))
        assertTrue(balanced("{[()()]}"))
        assertFalse(balanced("(]"))
        assertFalse(balanced("(("))
    }
}
