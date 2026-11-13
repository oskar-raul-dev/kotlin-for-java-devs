// explicitNulls era experimental en kotlinx-serialization 1.6.x y pedía
// @file:OptIn(ExperimentalSerializationApi::class); en 1.13 ya es estable y no hace falta.
package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.add

// Json es inmutable y caro de construir: se crea una vez y se reutiliza.

/** Estricto (por defecto): claves desconocidas -> excepción. */
val strictJson = Json

/** Tolerante: ignora claves desconocidas, nulls implícitos, y escribe los defaults. */
val lenientJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false        // un campo nullable que falta = null
    encodeDefaults = true
    coerceInputValues = true     // null en un campo no-null con default -> usa el default
}

val prettyJson = Json(from = lenientJson) { prettyPrint = true }

fun loadOrderEvents(text: String): List<OrderEvent> = lenientJson.decodeFromString(text)

fun toJson(settings: Settings, json: Json = strictJson): String = json.encodeToString(settings)
fun settingsFrom(text: String, json: Json = strictJson): Settings = json.decodeFromString(text)

fun encodePayments(payments: List<Payment>): String = strictJson.encodeToString(payments)
fun decodePayments(text: String): List<Payment> = strictJson.decodeFromString(text)

/** Árbol JSON sin clases: útil cuando el esquema es variable. */
fun sumOrderTotals(text: String): Int =
    Json.parseToJsonElement(text).jsonArray
        .mapNotNull { it.jsonObject["order-total"]?.jsonPrimitive?.intOrNull }
        .sum()

fun statesIn(text: String): Set<String> =
    Json.parseToJsonElement(text).jsonArray
        .mapNotNull { (it.jsonObject["address"] as? JsonObject)?.get("state")?.jsonPrimitive?.content }
        .toSet()

/** Construir JSON a mano con el DSL. */
fun buildSummary(count: Int, states: Set<String>): JsonObject = buildJsonObject {
    put("count", count)
    putJsonArray("states") { states.sorted().forEach { add(it) } }
}

fun countOf(summary: JsonObject): Int = summary["count"]!!.jsonPrimitive.int
