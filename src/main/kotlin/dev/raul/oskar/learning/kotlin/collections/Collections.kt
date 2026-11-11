package dev.raul.oskar.learning.kotlin.collections

import java.util.PriorityQueue
import java.util.TreeMap

// List/Set/Map son interfaces de SOLO LECTURA; MutableList/MutableSet/MutableMap permiten cambiar.
// Por debajo son las mismas clases de Java: listOf -> Arrays$ArrayList/ArrayList, mapOf -> LinkedHashMap.

/** Solo lectura no es inmutable: si alguien tiene la referencia mutable, la lista cambia. */
fun readOnlyIsNotImmutable(): Pair<List<Int>, List<Int>> {
    val mutable = mutableListOf(1, 2)
    val view: List<Int> = mutable
    val snapshot = mutable.toList()           // copia defensiva
    mutable += 3
    return view to snapshot
}

/** Listas: acceso seguro y operaciones comunes. */
fun listBasics(xs: List<Int>): Map<String, Any?> = mapOf(
    "first" to xs.firstOrNull(),
    "last" to xs.lastOrNull(),
    "atTen" to xs.getOrNull(10),             // xs[10] lanzaría IndexOutOfBounds
    "sorted" to xs.sorted(),
    "desc" to xs.sortedDescending(),
    "distinct" to xs.distinct(),
    "slice" to xs.take(2) + xs.takeLast(1),
    "chunks" to xs.chunked(2),
    "windows" to xs.windowed(3),
)

/** Sets: operaciones de conjuntos con infix. */
fun setOps(a: Set<String>, b: Set<String>): Triple<Set<String>, Set<String>, Set<String>> =
    Triple(a union b, a intersect b, a subtract b)

/** Mapas: getOrDefault, getOrPut, contadores e iteración con destructuring. */
fun wordCount(text: String): Map<String, Int> {
    val counts = mutableMapOf<String, Int>()
    for (w in text.lowercase().split(Regex("\\W+")).filter { it.isNotEmpty() }) {
        counts[w] = (counts[w] ?: 0) + 1             // equivalente a merge(w, 1, Integer::sum)
    }
    return counts
}

fun indexByFirstLetter(words: List<String>): Map<Char, MutableList<String>> {
    val index = mutableMapOf<Char, MutableList<String>>()
    for (w in words) index.getOrPut(w.first()) { mutableListOf() } += w   // computeIfAbsent
    return index
}

fun describeMap(m: Map<String, Int>): List<String> = m.map { (k, v) -> "$k=$v" }

/** TreeMap: ordenado por clave, con consultas de rango (headMap, floorKey...). */
fun eventsBefore(events: TreeMap<Long, String>, ts: Long): List<String> = events.headMap(ts).values.toList()
fun lastEventAtOrBefore(events: TreeMap<Long, String>, ts: Long): String? = events.floorEntry(ts)?.value

/** ArrayDeque de Kotlin: sirve de cola (FIFO) y de pila (LIFO). */
fun queueAndStack(items: List<Int>): Pair<List<Int>, List<Int>> {
    val queue = ArrayDeque(items)
    val stack = ArrayDeque(items)
    val fifo = buildList { while (queue.isNotEmpty()) add(queue.removeFirst()) }
    val lifo = buildList { while (stack.isNotEmpty()) add(stack.removeLast()) }
    return fifo to lifo
}

/** PriorityQueue (java.util): top-k de mayores con un min-heap de tamaño k. */
fun topK(nums: List<Int>, k: Int): List<Int> {
    val heap = PriorityQueue<Int>()
    for (n in nums) {
        heap += n
        if (heap.size > k) heap.poll()
    }
    return heap.sortedDescending()
}

/** Arrays: IntArray es int[]; Array<Int> es Integer[]. */
fun arrays(): Triple<IntArray, Array<String>, Array<IntArray>> {
    val ints = IntArray(5) { it * it }                // [0,1,4,9,16]
    val names = arrayOf("b", "a", "c").apply { sort() }
    val grid = Array(3) { IntArray(3) }               // int[3][3]
    grid[1][1] = 9
    return Triple(ints, names, grid)
}
