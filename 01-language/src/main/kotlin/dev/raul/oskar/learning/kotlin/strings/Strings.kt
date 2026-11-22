package dev.raul.oskar.learning.kotlin.strings

// String es java.lang.String; Kotlin le agrega muchas funciones de extensión.

fun greeting(name: String, openOrders: Int): String =
    "Hola, $name: tienes $openOrders ${if (openOrders == 1) "orden abierta" else "órdenes abiertas"}"   // plantillas

/** Raw string: sin escapes, multilínea. trimIndent quita la sangría común. */
fun report(ticker: String, volume: Int): String = """
    Reporte
      ticker:  $ticker
      volumen: $volume
""".trimIndent()

/** padStart/padEnd para alinear columnas. */
fun row(label: String, value: Int): String = label.padEnd(10, '.') + value.toString().padStart(6)

/** substringBefore/After evitan los indexOf + substring de Java. */
fun domainOf(email: String): String = email.substringAfter('@', missingDelimiterValue = "")
fun userOf(email: String): String = email.substringBefore('@')

/** buildString = StringBuilder con apply. */
fun reverseWords(text: String): String = buildString {
    text.trim().split(Regex("\\s+")).asReversed().forEachIndexed { i, w ->
        if (i > 0) append(' ')
        append(w)
    }
}

fun isPalindrome(text: String): Boolean {
    val clean = text.lowercase().filter { it.isLetterOrDigit() }
    return clean == clean.reversed()
}

/** Contar caracteres: groupingBy + eachCount. */
fun charFrequency(text: String): Map<Char, Int> =
    text.lowercase().filter { it.isLetter() }.groupingBy { it }.eachCount()

fun capitalizeWords(text: String): String =
    text.split(' ').joinToString(" ") { w -> w.replaceFirstChar { it.uppercase() } }

/** Comparación sin mayúsculas: equals(other, ignoreCase = true). */
fun sameIgnoringCase(a: String, b: String): Boolean = a.equals(b, ignoreCase = true)

/**
 * TRAMPA para ojos de Java: en Kotlin, split(String) es LITERAL, no regex.
 * "03/14/1985".split("\\/") busca el texto \/ y no lo encuentra: devuelve la cadena entera.
 * En Java, "03/14/1985".split("\\/") sí parte, porque Java siempre usa regex. Lo mismo con un mensaje estilo FIX:
 * "35=D|55=ACME".split("\\|") no parte; split("|") sí.
 */
fun splitLiteral(text: String, delimiter: String): List<String> = text.split(delimiter)
fun splitRegex(text: String, pattern: String): List<String> = text.split(Regex(pattern))
