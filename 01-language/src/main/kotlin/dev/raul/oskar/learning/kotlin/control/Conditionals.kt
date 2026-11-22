package dev.raul.oskar.learning.kotlin.control

import java.math.BigDecimal

// if y when son EXPRESIONES: devuelven valor. Por eso Kotlin no tiene operador ternario.

fun sign(n: Int): String = if (n > 0) "positive" else if (n < 0) "negative" else "zero"

/**
 * when con rangos e in: el switch de Java pero sin fall-through ni break.
 * Tramos de comisión del Mercado según los títulos operados en el mes: a más volumen, menor tasa.
 */
fun commissionRate(monthlyShares: Long): BigDecimal = when (monthlyShares) {
    in Long.MIN_VALUE..-1 -> throw IllegalArgumentException("volumen negativo: $monthlyShares")
    in 0L..<10_000L -> BigDecimal("0.0010")           // ..< es el rango semiabierto (Kotlin 1.9)
    in 10_000L..<100_000L -> BigDecimal("0.0005")
    else -> BigDecimal("0.0002")
}

/** Varios valores en una rama, separados por coma. */
fun isWeekend(day: String): Boolean = when (day.lowercase()) {
    "saturday", "sunday" -> true
    else -> false
}

/** when sin argumento = cadena de if/else if. La primera rama verdadera gana. */
fun fizzBuzz(n: Int): String = when {
    n % 15 == 0 -> "FizzBuzz"
    n % 3 == 0 -> "Fizz"
    n % 5 == 0 -> "Buzz"
    else -> n.toString()
}

/** Elvis + return/throw: salir temprano de una función. */
fun firstWordLength(text: String?): Int {
    val t = text?.trim()?.takeIf { it.isNotEmpty() } ?: return 0
    return t.substringBefore(' ').length
}
