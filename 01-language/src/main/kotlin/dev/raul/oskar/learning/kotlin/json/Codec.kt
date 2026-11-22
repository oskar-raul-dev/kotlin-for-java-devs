// explicitNulls era experimental en kotlinx-serialization 1.6.x y pedía
// @file:OptIn(ExperimentalSerializationApi::class); en 1.13 ya es estable y no hace falta.
package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

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

/** El feed real trae claves que el modelo no conoce ("venue") y campos que faltan: hace falta el tolerante. */
fun loadTicks(text: String): List<Tick> = lenientJson.decodeFromString(text)

fun toJson(settings: Settings, json: Json = strictJson): String = json.encodeToString(settings)
fun settingsFrom(text: String, json: Json = strictJson): Settings = json.decodeFromString(text)

fun encodeEvents(events: List<MarketEvent>): String = strictJson.encodeToString(events)
fun decodeEvents(text: String): List<MarketEvent> = strictJson.decodeFromString(text)

/** Árbol JSON sin clases: útil cuando el esquema es variable o solo interesa un campo. */
fun totalVolume(text: String): Long =
    Json.parseToJsonElement(text).jsonArray
        .mapNotNull { it.jsonObject["volume"]?.jsonPrimitive?.longOrNull }
        .sum()

fun tickersIn(text: String): Set<String> =
    Json.parseToJsonElement(text).jsonArray
        .mapNotNull { it.jsonObject["ticker"]?.jsonPrimitive?.content }
        .toSet()

/** Construir JSON a mano con el DSL. */
fun buildSummary(count: Int, tickers: Set<String>): JsonObject = buildJsonObject {
    put("count", count)
    putJsonArray("tickers") { tickers.sorted().forEach { add(it) } }
}

fun countOf(summary: JsonObject): Int = summary["count"]!!.jsonPrimitive.int
