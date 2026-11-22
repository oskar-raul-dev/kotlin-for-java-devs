package dev.raul.oskar.learning.kotlin.functional

fun main() {
    println("== Funciones ==")
    val triple = multiplier(3)
    println("applyTwice(triple, 2) = ${applyTwice(triple, 2)}")
    val incThenTriple = { x: Int -> x + 1 } andThen triple
    println("(x+1) andThen *3 de 4 = ${incThenTriple(4)}")
    println("add(2)(3) = ${add(2)(3)}")
    println("referencia a función: ${listOf("1", "22", "333").map(String::length)}")
    println(html { append("<p>"); append("hola"); append("</p>") })
    println("filterIsType<String> = ${filterIsType<String>(listOf(1, "a", 2.0, "b"))}")
    var calls = 0
    val slowSquare = memoize<Int, Int> { calls++; it * it }
    println("memo: ${slowSquare(9)} ${slowSquare(9)} llamadas reales=$calls")
    println("parseAll = ${parseAll(listOf("1", "dos", " 3 ", "4x"))}")
    val log = mutableListOf<String>()
    println(summary(scopeFunctions(log)) + "  log=$log")
    println("validador: ${(notBlank and shortText).validate("hola")} ${(notBlank and shortText).validate("demasiado largo")}")

    println("\n== Pipelines ==")
    println("volumeByTicker    = ${volumeByTicker(sampleTicks)}")
    println("ticksPerTicker    = ${ticksPerTicker(sampleTicks)}")
    println("highestByTicker   = ${highestByTicker(sampleTicks).mapValues { it.value.price }}")
    println("splitLarge(1000)  = ${splitLarge(sampleTicks, 1_000).let { (a, b) -> a.size to b.size }}")
    println("daysPerTicker     = ${daysPerTicker(sampleTicks)}")
    println("vela ACME día 1   = ${candleOf(sampleTicks.filter { it.ticker == "ACME" && it.day == 1 })}")
    println("deltas (centavos) = ${deltas(listOf(10_000, 10_040, 9_980, 10_110))}")
    val trace = mutableListOf<Int>()
    println("cuadrados > 50    = ${firstSquaresAbove(50, 3, trace)}  (generados: $trace)")
}
