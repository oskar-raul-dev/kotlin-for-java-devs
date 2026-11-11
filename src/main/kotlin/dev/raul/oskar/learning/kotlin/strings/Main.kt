package dev.raul.oskar.learning.kotlin.strings

fun main() {
    println("== Strings ==")
    println(greeting("Oskar", 1))
    println(greeting("Oskar", 3))
    println(report("ACME", 1500))
    println(row("manzanas", 12))
    println(row("peras", 1234))
    println("domainOf     = ${domainOf("oskar@example.com")}")
    println("reverseWords = ${reverseWords("  uno dos   tres ")}")
    println("palíndromo   = ${isPalindrome("Anita lava la tina")}")
    println("frecuencia   = ${charFrequency("Banana")}")
    println("capitalize   = ${capitalizeWords("hola mundo kotlin")}")
    println("split literal \"\\\\/\" = ${splitLiteral("03/14/1985", "\\/")}   <- ¡no partió!")
    println("split literal \"/\"   = ${splitLiteral("03/14/1985", "/")}")
    println("split regex   \"\\\\/\" = ${splitRegex("03/14/1985", "\\/")}")

    println("\n== Regex ==")
    println("isEmail      = ${isEmail("a.b+c@mail.example.com")} / ${isEmail("no-es@email")}")
    println("allZips      = ${allZips("Miami FL 33101, Austin TX 73301, ref 123456")}")
    println("parseUsDate  = ${parseUsDate("03/14/1985")} / ${parseUsDate("02/30/2020")}")
    println("yearByName   = ${yearByName("03/14/1985")}")
    println("maskDigits   = ${maskDigits("tel 555-1234")}")
    println("maskCard     = ${maskCard("4111 1111 1111 1234")}")
    println("normalize    = '${normalizeSpaces("  a   b    c ")}'")
    println("keyValues    = ${parseKeyValues("host=db; port=5432; user = app")}")
}
