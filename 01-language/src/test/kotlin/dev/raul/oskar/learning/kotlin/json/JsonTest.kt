package dev.raul.oskar.learning.kotlin.json

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

    private val text = javaClass.getResource("/data/orders.json")!!.readText()

    @Test
    fun `lenient json reads order events`() {
        val events = loadOrderEvents(text)
        assertEquals(3, events.size)
        assertEquals("CO", events[0].address?.state)
        assertNull(events[1].address)
        assertNull(events[2].orderTotal)        // la clave falta y explicitNulls = false
    }

    @Test
    fun `strict json rejects unknown keys`() {
        assertFailsWith<SerializationException> { strictJson.decodeFromString<List<OrderEvent>>(text) }
    }

    @Test
    fun `missing nullable key is an error without explicitNulls false`() {
        assertFailsWith<SerializationException> {
            strictJson.decodeFromString<Address>("""{"city":"Denver","state":"CO"}""")
        }
    }

    @Test
    fun `defaults are skipped unless encodeDefaults`() {
        assertEquals("""{"name":"app"}""", toJson(Settings("app")))
        assertEquals("""{"name":"app","retries":3,"tags":[]}""", toJson(Settings("app"), lenientJson))
        assertEquals(Settings("app", retries = 3), settingsFrom("""{"name":"app","retries":null}""", lenientJson))
    }

    @Test
    fun `sealed polymorphism round trip`() {
        val payments = listOf(CardPayment(1000, "4242"), TransferPayment(5000, "ES12"))
        val encoded = encodePayments(payments)
        assertTrue(""""type":"card"""" in encoded)
        assertEquals(payments, decodePayments(encoded))
    }

    @Test
    fun `custom serializers for java types`() {
        val invoice = Invoice("F-1", LocalDate.of(2026, 11, 3), BigDecimal("1234.50"))
        val encoded = strictJson.encodeToString(invoice)
        assertEquals("""{"number":"F-1","issued":"2026-11-03","total":"1234.50"}""", encoded)
        assertEquals(invoice, strictJson.decodeFromString<Invoice>(encoded))
    }

    @Test
    fun `json tree navigation`() {
        assertEquals(570, sumOrderTotals(text))
        assertEquals(setOf("CO", "WA"), statesIn(text))
        assertEquals(2, countOf(buildSummary(2, setOf("CO"))))
    }
}
