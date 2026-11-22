package dev.raul.oskar.learning.kotlin.oop

import dev.raul.oskar.learning.kotlin.basics.Money

// Interfaces con implementación por defecto, sealed y when exhaustivo, sobre las órdenes del Mercado.

interface Described {
    val ticker: String
    fun describe(): String = "orden de $ticker"       // método por defecto (como default en Java 8)
}

/**
 * sealed: la jerarquía es cerrada (todas las subclases en el mismo paquete/módulo).
 * Ventaja: un `when` sobre ella es exhaustivo y no necesita `else`.
 */
sealed interface Order : Described {
    val id: String
    val side: Side
    val quantity: Long

    /** Monto comprometido; una orden a mercado no lo conoce hasta ejecutarse. */
    fun notional(): Money?
}

data class LimitOrder(
    override val id: String,
    override val ticker: String,
    override val side: Side,
    override val quantity: Long,
    val limit: Money,
) : Order {
    init {
        require(quantity > 0) { "cantidad debe ser positiva: $quantity" }
        require(!limit.isNegative && !limit.isZero) { "precio límite debe ser positivo: $limit" }
    }

    override fun notional(): Money = limit * quantity
}

data class MarketOrder(
    override val id: String,
    override val ticker: String,
    override val side: Side,
    override val quantity: Long,
) : Order {
    init {
        require(quantity > 0) { "cantidad debe ser positiva: $quantity" }
    }

    override fun notional(): Money? = null
    override fun describe() = "orden a mercado de $ticker"
}

/** Si mañana se agrega un tipo de orden y no se cubre aquí, NO compila. */
fun priceHint(order: Order): String = when (order) {
    is LimitOrder -> "${order.side} ${order.quantity} a ${order.limit} o mejor"
    is MarketOrder -> "${order.side} ${order.quantity} al precio que haya"
}

/** Lo comprometido por las órdenes con límite (las de mercado no suman). */
fun committed(orders: List<Order>): Money = orders.mapNotNull { it.notional() }.fold(Money.zero()) { acc, m -> acc + m }
