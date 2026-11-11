package dev.raul.oskar.learning.kotlin.control

// if y when son EXPRESIONES: devuelven valor. Por eso Kotlin no tiene operador ternario.

fun sign(n: Int): String = if (n > 0) "positive" else if (n < 0) "negative" else "zero"

/** when con rangos e in: el switch de Java pero sin fall-through ni break. */
fun ageGroup(age: Int): String = when (age) {
    in Int.MIN_VALUE..-1 -> throw IllegalArgumentException("age < 0: $age")
    in 0..17 -> "minor"
    in 18..64 -> "adult"
    else -> "senior"
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
