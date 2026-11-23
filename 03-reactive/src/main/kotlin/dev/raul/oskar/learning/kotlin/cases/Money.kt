package dev.raul.oskar.learning.kotlin.cases

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

/**
 * Copia mínima del `Money` de `01-language/basics` (docs/Casos.md): monto + moneda, escala de la moneda, HALF_EVEN.
 *
 * Cada sección es un proyecto independiente (sin dependencias entre secciones), así que `03-reactive` trae su
 * propia versión reducida: solo lo que usan sus lecciones. El original, con su explicación completa, está en
 * `01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Money.kt`.
 */
data class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money> {

    init {
        require(amount.scale() == currency.defaultFractionDigits) {
            "escala ${amount.scale()} en $amount; $currency usa ${currency.defaultFractionDigits} (construir con Money.of)"
        }
    }

    operator fun plus(other: Money): Money = Money(amount + same(other).amount, currency)

    operator fun minus(other: Money): Money = Money(amount - same(other).amount, currency)

    operator fun times(quantity: Long): Money = Money(amount * quantity.toBigDecimal(), currency)

    /** Conversión por una tasa: el resultado queda en [target], redondeado a su escala. */
    fun convert(rate: BigDecimal, target: Currency): Money = of(amount * rate, target)

    override fun compareTo(other: Money): Int = amount.compareTo(same(other).amount)

    val isNegative: Boolean get() = amount.signum() < 0

    override fun toString(): String = "${amount.toPlainString()} ${currency.currencyCode}"

    private fun same(other: Money): Money {
        require(other.currency == currency) { "monedas distintas: $currency y ${other.currency}" }
        return other
    }

    companion object {
        val USD: Currency = Currency.getInstance("USD")
        val EUR: Currency = Currency.getInstance("EUR")

        fun of(amount: BigDecimal, currency: Currency = USD): Money =
            Money(amount.setScale(currency.defaultFractionDigits, RoundingMode.HALF_EVEN), currency)

        fun of(text: String, currency: Currency = USD): Money = of(BigDecimal(text), currency)

        fun zero(currency: Currency = USD): Money = of(BigDecimal.ZERO, currency)
    }
}

/** Suma de una colección de montos de la misma moneda. */
fun Iterable<Money>.sum(currency: Currency = Money.USD): Money = fold(Money.zero(currency), Money::plus)
