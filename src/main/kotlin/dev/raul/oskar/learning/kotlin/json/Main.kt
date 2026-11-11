package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import java.math.BigDecimal
import java.time.LocalDate

fun main() {
    val text = object {}.javaClass.getResource("/data/orders.json")!!.readText()

    println("== Deserializar (tolerante) ==")
    loadOrderEvents(text).forEach(::println)

    println("\n== Estricto falla con la clave desconocida \"channel\" ==")
    try {
        strictJson.decodeFromString<List<OrderEvent>>(text)
    } catch (e: SerializationException) {
        println("${e::class.simpleName}: ${e.message?.lineSequence()?.first()}")
    }

    println("\n== Defaults ==")
    println("strict  : ${toJson(Settings("app"))}")
    println("lenient : ${toJson(Settings("app"), lenientJson)}")
    println("leído   : ${settingsFrom("""{"name":"app","retries":null}""", lenientJson)}")

    println("\n== Polimorfismo sellado ==")
    val payments = listOf(CardPayment(1000, "4242"), TransferPayment(5000, "ES12"))
    val encoded = encodePayments(payments)
    println(encoded)
    println(decodePayments(encoded))

    println("\n== Serializadores propios ==")
    val invoice = Invoice("F-1", LocalDate.of(2026, 11, 3), BigDecimal("1234.50"))
    println(prettyJson.encodeToString(invoice))

    println("\n== Árbol JSON ==")
    println("suma order-total = ${sumOrderTotals(text)}")
    println("estados          = ${statesIn(text)}")
    println("resumen          = ${buildSummary(3, statesIn(text))}")
}
