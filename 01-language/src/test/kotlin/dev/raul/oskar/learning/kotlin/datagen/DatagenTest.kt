package dev.raul.oskar.learning.kotlin.datagen

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatagenTest {

    private fun resource(name: String): String = javaClass.getResource("/data/$name")!!.readText()

    private val ticks: List<JsonObject> by lazy {
        Json.parseToJsonElement(resource("ticks.json")).jsonArray.map { it.jsonObject }
    }

    private fun JsonObject.marketTime() =
        Instant.ofEpochMilli(getValue("ts").jsonPrimitive.long).atZone(MARKET_ZONE)

    @Test
    fun `generation is deterministic`() {
        DATASETS.values.forEach { generate -> assertEquals(generate(DATA_SEED), generate(DATA_SEED)) }
    }

    @Test
    fun `resource files match the generator`() {
        // Si esta prueba falla, se cambió el generador sin regenerar: ./gradlew -p 01-language run -Plesson=datagen
        DATASETS.forEach { (name, generate) -> assertEquals(generate(DATA_SEED), resource(name), name) }
    }

    @Test
    fun `ticks carry the planned flaws`() {
        val clean = cleanTicks()
        assertEquals(clean.size + TickFlaws.DUPLICATES, ticks.size)
        assertEquals(clean.size, ticks.map { it.getValue("seq").jsonPrimitive.long }.distinct().size)
        assertTrue(ticks.zipWithNext().any { (a, b) -> a.getValue("ts").jsonPrimitive.long > b.getValue("ts").jsonPrimitive.long })
        assertTrue(ticks.any { it["price"] == JsonNull })
        assertTrue(ticks.any { "volume" !in it })
        assertTrue(ticks.any { "venue" in it })
        assertEquals(TICKERS.keys, ticks.map { it.getValue("ticker").jsonPrimitive.content }.toSet())
    }

    @Test
    fun `one tick falls outside the session and one ticker has a gap`() {
        val outside = ticks.filter {
            val t = it.marketTime().toLocalTime()
            t.isBefore(SESSION_OPEN) || !t.isBefore(SESSION_CLOSE)
        }
        assertEquals(listOf(TickFlaws.OFF_HOURS_TICKER), outside.map { it.getValue("ticker").jsonPrimitive.content })
        val inGap = ticks.filter {
            val t = it.marketTime()
            it.getValue("ticker").jsonPrimitive.content == TickFlaws.GAP_TICKER &&
                t.toLocalDate() == TickFlaws.GAP_DAY && !t.toLocalTime().isBefore(TickFlaws.GAP_FROM) &&
                t.toLocalTime().isBefore(TickFlaws.GAP_TO)
        }
        assertTrue(inGap.isEmpty())
    }

    @Test
    fun `the session opens at a different UTC hour after the daylight saving change`() {
        fun firstUtcHour(day: LocalDate) =
            ticks.filter { it.marketTime().toLocalDate() == day }.minOf { it.marketTime().toInstant() }
                .atZone(java.time.ZoneOffset.UTC).hour
        assertEquals(13, firstUtcHour(LocalDate.of(2026, 10, 30)))   // 09:30 EDT = 13:30 UTC
        assertEquals(14, firstUtcHour(LocalDate.of(2026, 11, 2)))    // 09:30 EST = 14:30 UTC
        assertEquals(LocalTime.of(9, 30), SESSION_OPEN)
    }

    @Test
    fun `statement differs from the ledger exactly as planned`() {
        val ledger = ledgerMovements()
        val statement = statementRows()
        assertEquals(ledger.size - StatementFlaws.ONLY_IN_LEDGER + StatementFlaws.ONLY_IN_STATEMENT, statement.size)
        val exact = statement.count { s -> ledger.any { it.date == s.date && it.amount == s.amount } }
        val flawed = StatementFlaws.DATE_SHIFTED + StatementFlaws.CENT_DIFFERENCE + StatementFlaws.ONLY_IN_STATEMENT
        assertEquals(statement.size - flawed, exact)
        assertTrue(statementCsv().lines().any { it.contains("\"\"LUZ DEL VALLE\"\"") })   // comillas escapadas
        assertTrue(statement.all { it.reference.startsWith("BNK-") })
    }

    @Test
    fun `fx rates skip one business day`() {
        val dates = Json.parseToJsonElement(resource("fx-rates.json")).jsonArray
            .map { it.jsonObject.getValue("date").jsonPrimitive.content }
        assertEquals(21, dates.size)                     // 22 días hábiles en octubre de 2026, menos uno
        assertTrue(StatementFlaws.MISSING_FX_DAY !in dates)
    }
}
