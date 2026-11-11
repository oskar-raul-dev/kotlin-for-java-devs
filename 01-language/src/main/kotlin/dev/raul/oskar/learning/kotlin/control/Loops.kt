package dev.raul.oskar.learning.kotlin.control

// No existe el for(;;) de Java. Se itera sobre rangos, colecciones o con while.

fun sumTo(n: Int): Int {
    var total = 0
    for (i in 1..n) total += i          // 1..n incluye n
    return total
}

fun indicesUntil(n: Int): List<Int> {
    val out = mutableListOf<Int>()
    for (i in 0 until n) out += i       // until excluye n (como i < n)
    return out
}

fun countdown(from: Int, step: Int = 1): List<Int> {
    val out = mutableListOf<Int>()
    for (i in from downTo 0 step step) out += i
    return out
}

/** withIndex: índice y valor a la vez (como un for con contador). */
fun indexed(items: List<String>): List<String> {
    val out = mutableListOf<String>()
    for ((i, item) in items.withIndex()) out += "$i:$item"
    return out
}

/** while y do-while funcionan igual que en Java. */
fun digits(n: Int): Int {
    var x = Math.abs(n)
    var count = 0
    do {
        count++
        x /= 10
    } while (x > 0)
    return count
}

/** Etiquetas: break@outer sale del bucle externo (equivalente al label: de Java). */
fun firstPairWithSum(nums: List<Int>, target: Int): Pair<Int, Int>? {
    var result: Pair<Int, Int>? = null
    outer@ for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] + nums[j] == target) {
                result = nums[i] to nums[j]
                break@outer
            }
        }
    }
    return result
}

/** repeat(n) { } cuando no importa el índice. */
fun stars(n: Int): String = buildString { repeat(n) { append('*') } }
