package dev.raul.oskar.learning.kotlin.collections

import java.math.BigDecimal
import java.time.LocalDate
import java.util.TreeMap
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        val (u, i, d) = setOps(setOf("ACME", "ORBX"), setOf("ORBX", "NUBE"))
        assertEquals(setOf("ACME", "ORBX", "NUBE"), u)
        assertEquals(setOf("ORBX"), i)
        assertEquals(setOf("ACME"), d)
    }

    @Test
    fun `maps count and index`() {
        assertEquals(mapOf("el" to 3, "perro" to 1, "y" to 2, "gato" to 1, "loro" to 1),
            wordCount("el perro y el gato, y el loro"))
        assertEquals<List<String>?>(listOf("ACME", "AURA"), indexByFirstLetter(listOf("ACME", "ORBX", "AURA"))['A'])
        assertEquals(listOf("a=1", "b=2"), describeMap(mapOf("a" to 1, "b" to 2)))
    }

    @Test
    fun `mapOf keeps insertion order`() {
        assertEquals(listOf("z", "a", "m"), mapOf("z" to 1, "a" to 2, "m" to 3).keys.toList())
    }

    @Test
    fun `tree map range queries`() {
        val closes = TreeMap(mapOf(
            LocalDate.of(2026, 10, 30) to BigDecimal("101.20"),
            LocalDate.of(2026, 10, 28) to BigDecimal("100.10"),
            LocalDate.of(2026, 10, 29) to BigDecimal("100.80"),
        ))
        assertEquals(listOf(BigDecimal("100.10"), BigDecimal("100.80")), closesBefore(closes, LocalDate.of(2026, 10, 30)))
        assertEquals(BigDecimal("101.20"), closeAtOrBefore(closes, LocalDate.of(2026, 11, 1)))   // domingo
        assertNull(closeAtOrBefore(closes, LocalDate.of(2026, 10, 1)))
    }

    @Test
    fun `order book keeps price priority and arrival order`() {
        val book = OrderBook()
        assertNull(book.bestBid())                                    // vacío: null, no NoSuchElementException
        book.addBid(10_000, RestingOrder("ORD-1", 10))
        book.addBid(10_000, RestingOrder("ORD-2", 5))
        book.addBid(10_010, RestingOrder("ORD-3", 1))
        book.addAsk(10_030, RestingOrder("ORD-4", 7))
        book.addAsk(10_020, RestingOrder("ORD-5", 3))
        assertEquals(10_010, book.bestBid())                          // la compra más alta
        assertEquals(10_020, book.bestAsk())                          // la venta más baja
        assertEquals("ORD-1", book.firstAt(10_000)?.id)               // FIFO dentro del precio
        val (bidDepth, askDepth) = book.depth(2)
        assertEquals(listOf(10_010L to 1L, 10_000L to 15L), bidDepth)  // ORD-1 + ORD-2 en el mismo nivel
        assertEquals(listOf(10_020L to 3L, 10_030L to 7L), askDepth)
        assertFalse(book.isCrossed())
        assertTrue(book.cancel("ORD-3"))
        assertEquals(10_000, book.bestBid())                          // el nivel vacío desaparece
        assertFalse(book.cancel("ORD-3"))
        book.addBid(10_025, RestingOrder("ORD-6", 1))
        assertTrue(book.isCrossed())                                  // sin motor de emparejamiento, el libro se cruza
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
