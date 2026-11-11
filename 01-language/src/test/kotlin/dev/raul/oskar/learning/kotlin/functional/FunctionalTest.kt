package dev.raul.oskar.learning.kotlin.functional

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val order = scopeFunctions(log)
        assertEquals(listOf("creada o-1"), log)
        assertEquals(listOf("libro", "lápiz"), order.items)
        assertTrue(order.paid)
        assertEquals("o-1: libro, lápiz pagada=true", summary(order))
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
        assertEquals(mapOf("ana" to 1100, "bob" to 180, "eva" to 300), totalByUser(sampleTxns))
        assertEquals(3, countByState(sampleTxns)["NY"])
        assertEquals(900, maxTxnByUser(sampleTxns).getValue("ana").amount)
        val (large, small) = splitLarge(sampleTxns, 200)
        assertEquals(2, large.size)
        assertEquals(5, small.size)
        assertEquals(setOf("CA", "TX"), distinctStatesPerUser(sampleTxns)["ana"])
    }

    @Test
    fun `suspicious users rule`() {
        // ana: dos estados; bob: tres transacciones el mismo día; eva: normal
        assertEquals(listOf("ana", "bob"), suspiciousUsers(sampleTxns))
    }

    @Test
    fun `sequences are lazy`() {
        val trace = mutableListOf<Int>()
        assertEquals(listOf(64, 81, 100), firstSquaresAbove(50, 3, trace))
        assertEquals((1..10).toList(), trace)   // no generó más de lo necesario
        assertEquals(listOf(3, -4, 11), deltas(listOf(10, 13, 9, 20)))
    }
}
