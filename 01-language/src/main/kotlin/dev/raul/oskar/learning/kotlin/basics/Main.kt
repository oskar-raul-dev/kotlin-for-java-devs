package dev.raul.oskar.learning.kotlin.basics

import java.math.BigDecimal
import java.math.RoundingMode

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
    println("fromDouble(0.1)          = ${fromDouble(0.1)}")
    println("fromText(\"0.1\")          = ${fromText("0.1")}")
    println("commission(1001.25, 0.1%) = ${commission(BigDecimal("1001.25"), BigDecimal("0.001"))} (HALF_EVEN)")
    println("literales                = $million $mask $bits $big $ratio")
    println("nextChar('a')            = ${nextChar('a')}, code = ${codeOf('a')}")

    println("\n== Money ==")
    val price = Money.of("100.00")
    val fee = Money.ofCents(100)
    println("10 ACME a $price + comisión $fee = ${price * 10 + fee}")
    println("Money.of(\"2.345\")          = ${Money.of("2.345")} (HALF_EVEN) / ${Money.of("2.345", mode = RoundingMode.HALF_UP)} (HALF_UP)")
    println("Money.of(\"1.0\") == of(\"1.00\") = ${Money.of("1.0") == Money.of("1.00")}  (la escala ya está normalizada)")
    println("interés 0.35% de 1234.56   = ${Money.of("1234.56").times(BigDecimal("0.0035"))}")
    runCatching { Money(BigDecimal("1.5"), Money.USD) }.onFailure { println("constructor con escala 1: ${it.message}") }
    runCatching { price + Money.of("1", Money.EUR) }.onFailure { println("USD + EUR: ${it.message}") }

    println("\n== Identidad ==")
    val p1 = Point(1, 2)
    val p2 = Point(1, 2)
    println("p1 == p2  -> ${structurallyEqual(p1, p2)}")
    println("p1 === p2 -> ${sameReference(p1, p2)}")

    println("\n== describe ==")
    listOf<Any?>(null, 1, 2L, 3.0, "hola", true, 'z', p1).forEach { println(describe(it)) }
    println("UInt.MAX = ${unsignedMax()}")
}
