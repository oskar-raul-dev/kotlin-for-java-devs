// Clases: qué genera kotlinc para data class, propiedades, companion, object, lateinit y const.
package dev.raul.oskar.learning.kotlin.tutorials.decompile

import java.math.BigDecimal

/** data class: equals/hashCode/toString, componentN y copy (con copy$default para los parámetros por defecto). */
data class Account(val id: String, var balance: BigDecimal = BigDecimal.ZERO)

/** Propiedad con getter calculado (sin campo) y propiedad con setter privado. */
class Position(val ticker: String, quantity: Int) {
    var quantity: Int = quantity
        private set

    val isEmpty: Boolean
        get() = quantity == 0

    /** lateinit: campo nullable en el bytecode y un chequeo que lanza UninitializedPropertyAccessException. */
    lateinit var lastTrade: String

    fun buy(n: Int) {
        quantity += n
    }

    /** companion object: una clase anidada Position$Companion y un campo estático Companion. */
    companion object {
        const val MAX_QUANTITY = 1_000_000   // const: se copia en cada uso, sin getter
        val DEFAULT_TICKER = "ACME"            // val: campo privado estático + getter en Companion

        @JvmStatic
        fun empty(ticker: String) = Position(ticker, 0)   // @JvmStatic: además, un método estático en Position
    }
}

/** object: singleton con un campo estático INSTANCE y constructor privado. */
object Market {
    fun open(): Boolean = true
}
