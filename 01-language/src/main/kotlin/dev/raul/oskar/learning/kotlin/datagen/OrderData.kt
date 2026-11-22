package dev.raul.oskar.learning.kotlin.datagen

import java.util.Random

// Órdenes sintéticas para el libro de órdenes del Mercado (docs/Casos.md). No se guardan en un archivo: las pruebas
// las piden al vuelo con una semilla fija (misma semilla, misma secuencia en cualquier JDK). Los casos chicos y
// puntuales se escriben a mano en cada prueba; esto es para lo que necesita volumen (propiedades, carga, el ⚡).

enum class OrderSide { BUY, SELL }

/** Un comando para el libro, en el orden en que llega. Precios en centavos (tick de 0.01). */
sealed interface OrderCommand {
    val orderId: String

    /** `limitCents == null` es una orden a mercado. */
    data class Place(
        override val orderId: String,
        val ticker: String,
        val side: OrderSide,
        val quantity: Long,
        val limitCents: Long?,
    ) : OrderCommand

    /** Cancela una orden colocada antes (puede que ya se haya ejecutado: el libro decide qué hacer). */
    data class Cancel(override val orderId: String) : OrderCommand
}

/** Proporciones de cada tipo de comando, en porcentaje. El resto son órdenes con límite. */
object OrderMix {
    const val MARKET_PERCENT = 15
    const val CANCEL_PERCENT = 15
    const val SPREAD_CENTS = 50      // los límites caen a ±50 centavos del precio medio: compras y ventas se cruzan
}

/**
 * Una secuencia perezosa de [count] comandos para un ticker. Las cancelaciones apuntan siempre a una orden con límite
 * colocada antes en la misma secuencia; las cantidades son múltiplos de 10 (de 10 a 200), así hay ejecuciones parciales.
 */
fun orderStream(seed: Long = DATA_SEED, count: Int = 1_000, ticker: String = "ACME", midCents: Long = 10_000): Sequence<OrderCommand> =
    sequence {
        val rnd = Random(seed + 30)
        val placedLimits = mutableListOf<String>()
        var next = 1
        repeat(count) {
            val roll = rnd.nextInt(100)
            if (roll < OrderMix.CANCEL_PERCENT && placedLimits.isNotEmpty()) {
                yield(OrderCommand.Cancel(placedLimits.removeAt(rnd.nextInt(placedLimits.size))))
            } else {
                val id = "ORD-%06d".format(next++)
                val side = if (rnd.nextBoolean()) OrderSide.BUY else OrderSide.SELL
                val quantity = (1 + rnd.nextInt(20)) * 10L
                val market = roll < OrderMix.CANCEL_PERCENT + OrderMix.MARKET_PERCENT
                val limit = if (market) null else midCents - OrderMix.SPREAD_CENTS + rnd.nextInt(2 * OrderMix.SPREAD_CENTS + 1)
                if (limit != null) placedLimits += id
                yield(OrderCommand.Place(id, ticker, side, quantity, limit))
            }
        }
    }
