package dev.raul.oskar.learning.kotlin.datagen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class OrderDataTest {

    private val orders = orderStream().toList()
    private val places = orders.filterIsInstance<OrderCommand.Place>()

    @Test
    fun `the same seed gives the same orders and another seed does not`() {
        assertEquals(orders, orderStream().toList())
        assertNotEquals(orders, orderStream(seed = 1).toList())
        assertEquals(1_000, orders.size)
        assertEquals(50, orderStream(count = 50).count())        // perezosa: solo genera lo que se pide
    }

    @Test
    fun `the stream mixes limit and market orders and cancellations`() {
        assertTrue(places.any { it.limitCents == null })
        assertTrue(places.any { it.limitCents != null })
        assertTrue(orders.any { it is OrderCommand.Cancel })
        assertTrue(places.all { it.quantity in 10..200 && it.quantity % 10 == 0L })
        assertEquals(places.size, places.map { it.orderId }.toSet().size)   // ids únicos
    }

    @Test
    fun `cancellations point to limit orders placed earlier`() {
        val seen = mutableSetOf<String>()
        for (command in orders) {
            when (command) {
                is OrderCommand.Place -> if (command.limitCents != null) seen += command.orderId
                is OrderCommand.Cancel -> assertTrue(command.orderId in seen, "cancela algo que no existía: ${command.orderId}")
            }
        }
    }

    @Test
    fun `limit prices cross around the mid price`() {
        val buys = places.filter { it.side == OrderSide.BUY }.mapNotNull { it.limitCents }
        val sells = places.filter { it.side == OrderSide.SELL }.mapNotNull { it.limitCents }
        assertTrue(buys.max() >= sells.min(), "alguna compra tiene que pagar lo que pide una venta")
        assertTrue((buys + sells).all { it in 9_950..10_050 })
    }
}
