package dev.raul.oskar.learning.kotlin.collections

import java.math.BigDecimal
import java.time.LocalDate
import java.util.TreeMap

fun main() {
    val (view, snapshot) = readOnlyIsNotImmutable()
    println("solo lectura ve el cambio: $view, la copia no: $snapshot")

    listBasics(listOf(5, 3, 8, 3, 1)).forEach { (k, v) -> println("%-9s %s".format(k, v)) }

    val (u, i, d) = setOps(setOf("ACME", "ORBX", "LUMA"), setOf("ORBX", "NUBE"))
    println("union=$u intersect=$i subtract=$d")

    println("wordCount = ${wordCount("el perro y el gato, y el loro")}")
    println("index     = ${indexByFirstLetter(listOf("ACME", "ORBX", "AURA", "KAPA", "OLMO"))}")

    val closes = TreeMap(mapOf(
        LocalDate.of(2026, 10, 30) to BigDecimal("101.20"),
        LocalDate.of(2026, 10, 28) to BigDecimal("100.10"),
        LocalDate.of(2026, 10, 29) to BigDecimal("100.80"),
    ))
    val saturday = LocalDate.of(2026, 10, 31)
    println("TreeMap ordenado = $closes")
    println("antes del 30 = ${closesBefore(closes, LocalDate.of(2026, 10, 30))}, cierre al sábado 31 = ${closeAtOrBefore(closes, saturday)}")

    val book = OrderBook()
    book.addBid(10_000, RestingOrder("ORD-1", 10))
    book.addBid(10_000, RestingOrder("ORD-2", 5))
    book.addBid(9_990, RestingOrder("ORD-3", 20))
    book.addAsk(10_010, RestingOrder("ORD-4", 7))
    println("libro: mejor compra=${book.bestBid()} mejor venta=${book.bestAsk()} cruzado=${book.isCrossed()}")
    println("profundidad = ${book.depth(2)}, primera en 10000 = ${book.firstAt(10_000)}")

    val (fifo, lifo) = queueAndStack(listOf(1, 2, 3))
    println("cola=$fifo pila=$lifo")
    println("topK(3) = ${topK(listOf(5, 1, 9, 3, 7, 2), 3)}")

    val (ints, names, grid) = arrays()
    println("IntArray=${ints.contentToString()} names=${names.contentToString()} grid=${grid.contentDeepToString()}")
}
