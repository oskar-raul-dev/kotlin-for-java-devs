package dev.raul.oskar.learning.kotlin.basics

import java.math.BigDecimal
import java.math.RoundingMode

// Kotlin no tiene primitivos "visibles": Int, Long, Double... son clases,
// pero el compilador los baja a int, long, double cuando no son nullables.
// Un Int? sí se vuelve java.lang.Integer (boxing).

/** Sin conversión implícita: un Int NO se asigna a un Long. Hay que llamar a toLong(). */
fun widen(i: Int): Long = i.toLong()

/** División entera como en Java: trunca hacia cero. */
fun integerDivision(a: Int, b: Int): Int = a / b

/** Para obtener decimales, al menos un operando debe ser Double. */
fun realDivision(a: Int, b: Int): Double = a.toDouble() / b

/** Int desborda en silencio igual que en Java; Math.addExact lanza ArithmeticException. */
fun overflowingAdd(a: Int, b: Int): Int = a + b
fun checkedAdd(a: Int, b: Int): Int = Math.addExact(a, b)

/** String -> número sin excepción: toIntOrNull devuelve null si no se puede. */
fun parseOrDefault(text: String, default: Int = 0): Int = text.trim().toIntOrNull() ?: default

/** BigDecimal desde Double arrastra el error binario: BigDecimal(0.1) = 0.1000000000000000055511151231257827… */
fun fromDouble(d: Double): BigDecimal = BigDecimal(d)

/** Desde String es exacto. Para dinero, ver Money.kt (que nunca acepta Double). */
fun fromText(text: String): BigDecimal = BigDecimal(text)

/** En Kotlin los operadores +, -, *, / funcionan con BigDecimal (operator overloading). */
fun commission(notional: BigDecimal, rate: BigDecimal, mode: RoundingMode = RoundingMode.HALF_EVEN): BigDecimal =
    (notional * rate).setScale(2, mode)

/** BigDecimal.equals compara escala ("1.0" != "1.00"); compareTo no. */
fun sameAmount(a: BigDecimal, b: BigDecimal): Boolean = a.compareTo(b) == 0

/** Char no es un número: 'a' + 1 da Char, y .code da el Int. */
fun nextChar(c: Char): Char = c + 1
fun codeOf(c: Char): Int = c.code

/** Literales: guiones bajos, hex, binario, sufijos L y f. */
val million: Int = 1_000_000
val mask: Int = 0xFF
val bits: Int = 0b1010
val big: Long = 3_000_000_000L
val ratio: Float = 0.5f
