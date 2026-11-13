package dev.raul.oskar.learning.kotlin.collections

import java.util.TreeMap
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CollectionsTest {

    @Test
    fun `read-only view sees mutations, copy does not`() {
        val (view, snapshot) = readOnlyIsNotImmutable()
        assertEquals(listOf(1, 2, 3), view)
        assertEquals(listOf(1, 2), snapshot)
    }

    @Test
    fun `list helpers`() {
        val r = listBasics(listOf(5, 3, 8, 3, 1))
        assertEquals(5, r["first"])
        assertNull(r["atTen"])
        assertEquals(listOf(1, 3, 3, 5, 8), r["sorted"])
        assertEquals(listOf(5, 3, 8, 1), r["distinct"])
        assertEquals(listOf(5, 3, 1), r["slice"])
        assertEquals(listOf(listOf(5, 3), listOf(8, 3), listOf(1)), r["chunks"])
        assertEquals(3, (r["windows"] as List<*>).size)
        assertFailsWith<IndexOutOfBoundsException> { listOf(1)[5] }
    }

    @Test
    fun `set operations`() {
        val (u, i, d) = setOps(setOf("CA", "TX"), setOf("TX", "NY"))
        assertEquals(setOf("CA", "TX", "NY"), u)
        assertEquals(setOf("TX"), i)
        assertEquals(setOf("CA"), d)
    }

    @Test
    fun `maps count and index`() {
        assertEquals(mapOf("el" to 3, "perro" to 1, "y" to 2, "gato" to 1, "loro" to 1),
            wordCount("el perro y el gato, y el loro"))
        assertEquals<List<String>?>(listOf("ana", "alba"), indexByFirstLetter(listOf("ana", "bea", "alba"))['a'])
        assertEquals(listOf("a=1", "b=2"), describeMap(mapOf("a" to 1, "b" to 2)))
    }

    @Test
    fun `mapOf keeps insertion order`() {
        assertEquals(listOf("z", "a", "m"), mapOf("z" to 1, "a" to 2, "m" to 3).keys.toList())
    }

    @Test
    fun `tree map range queries`() {
        val events = TreeMap(mapOf(300L to "c", 100L to "a", 200L to "b"))
        assertEquals(listOf("a", "b"), eventsBefore(events, 250))
        assertEquals("b", lastEventAtOrBefore(events, 250))
        assertNull(lastEventAtOrBefore(events, 50))
    }

    @Test
    fun `queue stack and heap`() {
        assertEquals(listOf(1, 2, 3) to listOf(3, 2, 1), queueAndStack(listOf(1, 2, 3)))
        assertEquals(listOf(9, 7, 5), topK(listOf(5, 1, 9, 3, 7, 2), 3))
    }

    @Test
    fun `arrays need contentEquals`() {
        val (ints, names, grid) = arrays()
        assertContentEquals(intArrayOf(0, 1, 4, 9, 16), ints)
        assertContentEquals(arrayOf("a", "b", "c"), names)
        assertEquals(9, grid[1][1])
        // == en arrays compara referencias, como en Java. Desde K2 (Kotlin 2.0) el compilador avisa de esta trampa;
        // aquí se suprime porque la línea existe justo para demostrarla.
        @Suppress("ARRAY_EQUALITY_OPERATOR_CAN_BE_REPLACED_WITH_CONTENT_EQUALS")
        assertEquals(false, intArrayOf(1) == intArrayOf(1))
        assertEquals(true, intArrayOf(1) contentEquals intArrayOf(1))
    }
}
