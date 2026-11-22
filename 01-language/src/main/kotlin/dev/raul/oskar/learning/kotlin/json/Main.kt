package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import java.math.BigDecimal
import java.time.LocalDate

fun main() {
    val text = object {}.javaClass.getResource("/data/ticks.json")!!.readText()

    println("== Deserializar el feed (tolerante) ==")
    val ticks = loadTicks(text)
    println("${ticks.size} ticks; los primeros:")
    ticks.take(3).forEach(::println)
    println("sin precio: ${ticks.count { it.price == null }}, sin volumen: ${ticks.count { it.volume == null }}")

    println("\n== Estricto falla con la clave desconocida \"venue\" ==")
    try {
        strictJson.decodeFromString<List<Tick>>(text)
    } catch (e: SerializationException) {
        println("${e::class.simpleName}: ${e.message?.lineSequence()?.first()}")
    }

    println("\n== Defaults ==")
    println("strict  : ${toJson(Settings("feed"))}")
    println("lenient : ${toJson(Settings("feed"), lenientJson)}")
    println("leído   : ${settingsFrom("""{"name":"feed","retries":null}""", lenientJson)}")

    println("\n== Polimorfismo sellado ==")
    val events = listOf(
        Trade("ACME", 1793021495500, BigDecimal("99.84"), 420),
        Quote("ACME", 1793021495600, BigDecimal("99.83"), BigDecimal("99.85")),
        Halt("LUMA", 1793030000000, "volatilidad"),
    )
    val encoded = encodeEvents(events)
    println(encoded)
    println(decodeEvents(encoded))

    println("\n== Serializadores propios ==")
    println(prettyJson.encodeToString(DailyClose("ACME", LocalDate.of(2026, 10, 26), BigDecimal("100.42"))))

    println("\n== Árbol JSON ==")
    println("volumen total = ${totalVolume(text)}")
    println("tickers       = ${tickersIn(text)}")
    println("resumen       = ${buildSummary(ticks.size, tickersIn(text))}")
}
