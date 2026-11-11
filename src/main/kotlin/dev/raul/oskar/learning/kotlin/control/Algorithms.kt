package dev.raul.oskar.learning.kotlin.control

// Algoritmos clásicos de pizarra, escritos al estilo Kotlin.

/** Máximo común divisor, recursivo. tailrec => el compilador lo convierte en bucle. */
tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) Math.abs(a) else gcd(b, a % b)

fun isPrime(n: Int): Boolean {
    if (n < 2) return false
    if (n < 4) return true
    if (n % 2 == 0) return false
    var d = 3
    while (d.toLong() * d <= n) {
        if (n % d == 0) return false
        d += 2
    }
    return true
}

/** Fibonacci iterativo con destructuring de Pair. */
fun fibonacci(n: Int): Long {
    require(n >= 0) { "n debe ser >= 0" }
    var (a, b) = 0L to 1L
    repeat(n) {
        val next = a + b
        a = b
        b = next
    }
    return a
}

/** Búsqueda binaria sobre IntArray ordenado. Devuelve el índice o -1. */
fun binarySearch(sorted: IntArray, target: Int): Int {
    var lo = 0
    var hi = sorted.size - 1
    while (lo <= hi) {
        val mid = (lo + hi) ushr 1          // evita overflow de (lo + hi) / 2
        when {
            sorted[mid] == target -> return mid
            sorted[mid] < target -> lo = mid + 1
            else -> hi = mid - 1
        }
    }
    return -1
}

/** Insertion sort en sitio, sobre IntArray (int[] de Java). */
fun insertionSort(a: IntArray): IntArray {
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0 && a[j] > key) {
            a[j + 1] = a[j]
            j--
        }
        a[j + 1] = key
    }
    return a
}

/** Two-sum en O(n) con HashMap: el clásico de entrevista. */
fun twoSum(nums: IntArray, target: Int): Pair<Int, Int>? {
    val seen = HashMap<Int, Int>()               // valor -> índice
    for ((i, n) in nums.withIndex()) {
        seen[target - n]?.let { return it to i }
        seen[n] = i
    }
    return null
}

/** ¿Paréntesis balanceados? Pila con ArrayDeque (de Kotlin, no la de java.util). */
fun balanced(s: String): Boolean {
    val pairs = mapOf(')' to '(', ']' to '[', '}' to '{')
    val stack = ArrayDeque<Char>()
    for (c in s) {
        when (c) {
            '(', '[', '{' -> stack.addLast(c)
            ')', ']', '}' -> if (stack.removeLastOrNull() != pairs[c]) return false
        }
    }
    return stack.isEmpty()
}
