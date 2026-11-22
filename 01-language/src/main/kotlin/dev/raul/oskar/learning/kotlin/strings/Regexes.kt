package dev.raul.oskar.learning.kotlin.strings

// Regex de Kotlin envuelve java.util.regex.Pattern. Con raw strings no hay que doblar las \.
// Cada Regex va en un val de nivel superior: dentro de una función se compilaría en cada llamada.

private val EMAIL = Regex("""^[\w.+-]+@[\w-]+(\.[\w-]+)+$""")
private val ACCOUNT = Regex("""ACC-(?<number>\d{8})-(?<check>\d{2})""")   // cuentas del Banco
private val FIX_FIELD = Regex("""(\d+)=([^|]*)""")
private val MULTI_SPACE = Regex("""\s{2,}""")
private val DIGIT = Regex("""\d""")
private val ACCOUNT_HEAD = Regex("""(?<=ACC-)\d{4}""")   // lookbehind: 4 dígitos precedidos por ACC-

/** matches: toda la cadena debe cumplir el patrón. */
fun isEmail(text: String): Boolean = EMAIL.matches(text)

/** Solo la FORMA ACC-8 dígitos-2 dígitos; validar los dígitos de control (mod 97) es un ejercicio de la lección. */
fun isAccountFormat(text: String): Boolean = ACCOUNT.matches(text)

/** containsMatchIn: basta con que aparezca en algún lugar. */
fun mentionsAccount(text: String): Boolean = ACCOUNT.containsMatchIn(text)

/** findAll: todas las coincidencias, como while (m.find()) en Java. */
fun allAccounts(text: String): List<String> = ACCOUNT.findAll(text).map { it.value }.toList()

/** Grupos con nombre + destructuring de los grupos en orden: (número, control). */
fun accountParts(text: String): Pair<String, String>? {
    val m = ACCOUNT.matchEntire(text) ?: return null
    val (number, check) = m.destructured
    return number to check
}

fun accountNumber(text: String): String? = ACCOUNT.matchEntire(text)?.groups?.get("number")?.value

/** replace con lambda: transformar cada coincidencia. */
fun maskDigits(text: String): String = DIGIT.replace(text) { "#" }

/** Tapa los primeros 4 dígitos del número de cuenta. */
fun maskAccount(account: String): String = ACCOUNT_HEAD.replace(account, "****")

fun normalizeSpaces(text: String): String = MULTI_SPACE.replace(text.trim(), " ")

/** Mensaje estilo FIX (tag=valor separados por |) a mapa: findAll + associate. 35=tipo, 55=ticker, 54=lado, 38=cantidad, 44=precio. */
fun parseFix(message: String): Map<Int, String> =
    FIX_FIELD.findAll(message).associate { it.groupValues[1].toInt() to it.groupValues[2] }
