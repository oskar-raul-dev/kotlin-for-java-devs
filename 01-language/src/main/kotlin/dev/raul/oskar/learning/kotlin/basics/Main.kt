package dev.raul.oskar.learning.kotlin.basics

fun main() {
    println("== Números ==")
    println("widen(42)                = ${widen(42)}")
    println("integerDivision(7, 2)    = ${integerDivision(7, 2)}")
    println("integerDivision(-7, 2)   = ${integerDivision(-7, 2)}")
    println("realDivision(7, 2)       = ${realDivision(7, 2)}")
    println("overflowingAdd(MAX, 1)   = ${overflowingAdd(Int.MAX_VALUE, 1)}")
    println("parseOrDefault(\" 12 \")   = ${parseOrDefault(" 12 ")}")
    println("parseOrDefault(\"x\", -1)  = ${parseOrDefault("x", -1)}")
    println("0.1 + 0.2 (Double)       = ${0.1 + 0.2}")
    println("money(0.1)+money(0.2)    = ${money("0.1") + money("0.2")}")
    println("addTax(100.00, 0.19)     = ${addTax(money("100"), "0.19".toBigDecimal())}")
    println("literales                = $million $mask $bits $big $ratio")
    println("nextChar('a')            = ${nextChar('a')}, code = ${codeOf('a')}")

    println("\n== Identidad ==")
    val p1 = Point(1, 2)
    val p2 = Point(1, 2)
    println("p1 == p2  -> ${structurallyEqual(p1, p2)}")
    println("p1 === p2 -> ${sameReference(p1, p2)}")

    println("\n== describe ==")
    listOf<Any?>(null, 1, 2L, 3.0, "hola", true, 'z', p1).forEach { println(describe(it)) }
    println("UInt.MAX = ${unsignedMax()}")
}
