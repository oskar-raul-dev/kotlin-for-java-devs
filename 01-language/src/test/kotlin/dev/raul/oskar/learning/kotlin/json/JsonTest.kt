package dev.raul.oskar.learning.kotlin.json

import dev.raul.oskar.learning.kotlin.basics.Money
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonTest {

    /** Tres ticks con las imperfecciones del feed: cotización y clave desconocida, precio nulo, volumen ausente. */
    private val sample = """
        [
          {"seq":1,"ticker":"ACME","ts":1793021495500,"price":"99.84","volume":420,"bid-price":"99.83","ask-price":"99.85","venue":"SIM-1"},
          {"seq":2,"ticker":"ORBX","ts":1793021496000,"price":null,"volume":310},
          {"seq":3,"ticker":"ACME","ts":1793021497000,"price":"99.86"}
        ]
    """.trimIndent()

    @Test
    fun `lenient json reads ticks with gaps`() {
        val ticks = loadTicks(sample)
        assertEquals(3, ticks.size)
        assertEquals(BigDecimal("99.83"), ticks[0].bidPrice)      // "bid-price" con @SerialName
        assertNull(ticks[1].price)                                // null explícito
        assertNull(ticks[2].volume)                               // la clave falta y explicitNulls = false
        assertNull(ticks[2].askPrice)
    }

    @Test
    fun `strict json rejects unknown keys`() {
        assertFailsWith<SerializationException> { strictJson.decodeFromString<List<Tick>>(sample) }
    }

    @Test
    fun `missing nullable key is an error without explicitNulls false`() {
        val noVolume = """{"seq":3,"ticker":"ACME","ts":1,"price":"99.86","bid-price":null,"ask-price":null}"""
        assertFailsWith<SerializationException> { strictJson.decodeFromString<Tick>(noVolume) }
    }

    @Test
    fun `defaults are skipped unless encodeDefaults`() {
        assertEquals("""{"name":"feed"}""", toJson(Settings("feed")))
        assertEquals("""{"name":"feed","retries":3,"tags":[]}""", toJson(Settings("feed"), lenientJson))
        assertEquals(Settings("feed", retries = 3), settingsFrom("""{"name":"feed","retries":null}""", lenientJson))
    }

    @Test
    fun `sealed polymorphism round trip`() {
        val events = listOf(
            Trade("ACME", 1, BigDecimal("99.84"), 420),
            Quote("ACME", 2, BigDecimal("99.83"), BigDecimal("99.85")),
            Halt("LUMA", 3, "volatilidad"),
        )
        val encoded = encodeEvents(events)
        assertTrue(""""type":"trade"""" in encoded)
        assertTrue(""""price":"99.84"""" in encoded)             // el dinero viaja como cadena
        assertEquals(events, decodeEvents(encoded))
    }

    @Test
    fun `custom serializers for java types`() {
        val close = DailyClose("ACME", LocalDate.of(2026, 10, 26), BigDecimal("100.40"))
        val encoded = strictJson.encodeToString(close)
        assertEquals("""{"ticker":"ACME","date":"2026-10-26","close":"100.40"}""", encoded)
        assertEquals(close, strictJson.decodeFromString<DailyClose>(encoded))
    }

    @Test
    fun `money travels as a string with its currency`() {
        val fee = Fee("ORD-1", Money.of("1.00"))
        val encoded = strictJson.encodeToString(fee)
        assertEquals("""{"orderId":"ORD-1","amount":"1.00 USD"}""", encoded)
        assertEquals(fee, strictJson.decodeFromString<Fee>(encoded))
        assertFailsWith<IllegalArgumentException> {             // escala equivocada: error, no redondeo silencioso
            strictJson.decodeFromString<Fee>("""{"orderId":"ORD-1","amount":"1.5 USD"}""")
        }
    }

    @Test
    fun `json tree navigation`() {
        assertEquals(730, totalVolume(sample))
        assertEquals(setOf("ACME", "ORBX"), tickersIn(sample))
        assertEquals(2, countOf(buildSummary(2, setOf("ACME"))))
    }

    @Test
    fun `the generated feed only loads with the lenient configuration`() {
        val text = javaClass.getResource("/data/ticks.json")!!.readText()
        val ticks = loadTicks(text)
        assertEquals(setOf("ACME", "ORBX", "LUMA", "KAPA", "NUBE"), ticks.map { it.ticker }.toSet())
        assertTrue(ticks.any { it.price == null } && ticks.any { it.volume == null })
        assertFailsWith<SerializationException> { strictJson.decodeFromString<List<Tick>>(text) }
    }
}
