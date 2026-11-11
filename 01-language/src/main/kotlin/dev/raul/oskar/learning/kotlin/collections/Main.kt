package dev.raul.oskar.learning.kotlin.collections

import java.util.TreeMap

fun main() {
    val (view, snapshot) = readOnlyIsNotImmutable()
    println("solo lectura ve el cambio: $view, la copia no: $snapshot")

    listBasics(listOf(5, 3, 8, 3, 1)).forEach { (k, v) -> println("%-9s %s".format(k, v)) }

    val (u, i, d) = setOps(setOf("CA", "TX", "FL"), setOf("TX", "NY"))
    println("union=$u intersect=$i subtract=$d")

    println("wordCount = ${wordCount("el perro y el gato, y el loro")}")
    println("index     = ${indexByFirstLetter(listOf("ana", "bea", "alba", "carlos", "beto"))}")

    val events = TreeMap(mapOf(300L to "c", 100L to "a", 200L to "b"))
    println("TreeMap ordenado = $events, antes de 250 = ${eventsBefore(events, 250)}, floor(250) = ${lastEventAtOrBefore(events, 250)}")

    val (fifo, lifo) = queueAndStack(listOf(1, 2, 3))
    println("cola=$fifo pila=$lifo")
    println("topK(3) = ${topK(listOf(5, 1, 9, 3, 7, 2), 3)}")

    val (ints, names, grid) = arrays()
    println("IntArray=${ints.contentToString()} names=${names.contentToString()} grid=${grid.contentDeepToString()}")
}
