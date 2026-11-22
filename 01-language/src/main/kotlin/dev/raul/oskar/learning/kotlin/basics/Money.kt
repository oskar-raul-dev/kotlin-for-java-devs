package dev.raul.oskar.learning.kotlin.basics

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

/**
 * Dinero del Banco (docs/Casos.md): monto + moneda.
 *
 * - `data class` y no `value class`: una value class envuelve UNA sola propiedad y aquí son dos.
 * - La escala siempre es la de la moneda (2 para USD y EUR). Un `init` no puede reasignar un `val`, así que el
 *   constructor VALIDA la escala y las fábricas de [Companion] la NORMALIZAN. Con la escala fija, el `equals`
 *   generado es seguro: nunca se comparan "1.0" y "1.00".
 * - Redondeo HALF_EVEN (bancario) por defecto.
 * - Se construye desde String o desde centavos Long, nunca desde Double.
 */
data class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    init {
        require(amount.scale() == currency.defaultFractionDigits) {
            "escala ${amount.scale()} en $amount; $currency usa ${currency.defaultFractionDigits} (construir con Money.of)"
        }
    }

    operator fun plus(other: Money): Money = Money(amount + sameCurrency(other).amount, currency)

    operator fun minus(other: Money): Money = Money(amount - sameCurrency(other).amount, currency)

    /** Por una cantidad entera (10 títulos): la escala no cambia, no hace falta redondear. */
    operator fun times(quantity: Long): Money = Money(amount * quantity.toBigDecimal(), currency)

    /** Por una tasa (comisión, interés): el resultado se redondea a la escala de la moneda. */
    fun times(rate: BigDecimal, mode: RoundingMode = RoundingMode.HALF_EVEN): Money = of(amount * rate, currency, mode)

    operator fun unaryMinus(): Money = Money(amount.negate(), currency)

    /** compareTo de BigDecimal ignora la escala; igual se exige la misma moneda. */
    override fun compareTo(other: Money): Int = amount.compareTo(sameCurrency(other).amount)

    val isZero: Boolean get() = amount.signum() == 0
    val isNegative: Boolean get() = amount.signum() < 0

    override fun toString(): String = "${amount.toPlainString()} ${currency.currencyCode}"

    private fun sameCurrency(other: Money): Money {
        require(other.currency == currency) { "monedas distintas: $currency y ${other.currency}" }
        return other
    }

    companion object {
        val USD: Currency = Currency.getInstance("USD")
        val EUR: Currency = Currency.getInstance("EUR")

        fun of(amount: BigDecimal, currency: Currency = USD, mode: RoundingMode = RoundingMode.HALF_EVEN): Money =
            Money(amount.setScale(currency.defaultFractionDigits, mode), currency)

        fun of(text: String, currency: Currency = USD, mode: RoundingMode = RoundingMode.HALF_EVEN): Money =
            of(BigDecimal(text), currency, mode)

        /** 1999 centavos -> 19.99: exacto, sin pasar por decimales binarios. */
        fun ofCents(cents: Long, currency: Currency = USD): Money =
            Money(BigDecimal.valueOf(cents, currency.defaultFractionDigits), currency)

        fun zero(currency: Currency = USD): Money = ofCents(0, currency)
    }
}
