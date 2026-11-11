package dev.raul.oskar.learning.kotlin.strings

import java.time.LocalDate

// Regex de Kotlin envuelve java.util.regex.Pattern. Con raw strings no hay que doblar las \.

private val EMAIL = Regex("""^[\w.+-]+@[\w-]+(\.[\w-]+)+$""")
private val US_DATE = Regex("""^(?<month>\d{2})/(?<day>\d{2})/(?<year>\d{4})$""")
private val ZIP = Regex("""\b\d{5}\b""")
private val MULTI_SPACE = Regex("""\s{2,}""")

/** matches: toda la cadena debe cumplir el patrón. */
fun isEmail(text: String): Boolean = EMAIL.matches(text)

/** containsMatchIn: basta con que aparezca en algún lugar. */
fun hasZip(text: String): Boolean = ZIP.containsMatchIn(text)

/** findAll: todas las coincidencias, como while (m.find()) en Java. */
fun allZips(text: String): List<String> = ZIP.findAll(text).map { it.value }.toList()

/** Grupos con nombre. Parsea fechas MM/dd/yyyy (formato de EE. UU.). */
fun parseUsDate(text: String): LocalDate? {
    val m = US_DATE.matchEntire(text) ?: return null
    val (month, day, year) = m.destructured          // destructuring de los grupos en orden
    return runCatching { LocalDate.of(year.toInt(), month.toInt(), day.toInt()) }.getOrNull()
}

fun yearByName(text: String): Int? = US_DATE.matchEntire(text)?.groups?.get("year")?.value?.toInt()

/** replace con lambda: transformar cada coincidencia. */
fun maskDigits(text: String): String = Regex("""\d""").replace(text) { "#" }

fun maskCard(card: String): String =
    Regex("""\d(?=\d{4})""").replace(card.filter { it.isDigit() }, "*")

fun normalizeSpaces(text: String): String = MULTI_SPACE.replace(text.trim(), " ")

/** Extraer pares clave=valor. */
fun parseKeyValues(text: String): Map<String, String> =
    Regex("""(\w+)\s*=\s*([^;]*)""").findAll(text).associate { it.groupValues[1] to it.groupValues[2].trim() }
