// Mini librería del caso Banco (docs/Casos.md): se compila sola a money.jar y la usan Kotlin y Java.
// @file:JvmName cambia el nombre de la clase que agrupa las funciones de nivel superior de este archivo:
// sin él se llamaría MoneyKt.
@file:JvmName("Monies")

package dev.raul.oskar.learning.kotlin.tutorials.cli.money

import java.math.BigDecimal
import java.math.RoundingMode

/** Monto en USD con dos decimales. Simplificado: el Money completo (con moneda) está en docs/Casos.md. */
data class Money(val amount: BigDecimal) {
    init {
        require(amount.scale() <= 2) { "máximo 2 decimales: $amount" }
    }

    operator fun plus(other: Money) = Money(amount + other.amount)

    override fun toString() = "USD " + amount.setScale(2, RoundingMode.HALF_EVEN)
}

/** Función de nivel superior: desde Java se llama Monies.usd("10.50"). */
fun usd(raw: String): Money = Money(BigDecimal(raw))
