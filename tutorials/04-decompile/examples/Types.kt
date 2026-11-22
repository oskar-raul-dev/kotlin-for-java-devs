// Tipos: sealed + when, value class y suspend.
package dev.raul.oskar.learning.kotlin.tutorials.decompile

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** sealed: clase abstracta con subclases conocidas; el when exhaustivo no necesita else. */
sealed interface Order {
    val quantity: Int
}

data class LimitOrder(override val quantity: Int, val price: Double) : Order

data class MarketOrder(override val quantity: Int) : Order

fun kind(order: Order): String = when (order) {
    is LimitOrder -> "límite a ${order.price}"
    is MarketOrder -> "a mercado"
}

/** value class: en el bytecode desaparece donde puede (se pasa el Long), y los nombres de función se "mutilan". */
@JvmInline
value class Cents(val value: Long)

fun addCents(a: Cents, b: Cents): Cents = Cents(a.value + b.value)

/** suspend: un parámetro extra Continuation y un retorno Object (puede devolver COROUTINE_SUSPENDED). */
suspend fun fetchPrice(ticker: String): Double = suspendCoroutine { continuation ->
    continuation.resume(if (ticker == "ACME") 100.0 else 0.0)
}
