package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import dev.raul.oskar.learning.kotlin.basics.Money
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Currency

// Tipos de Java (BigDecimal, LocalDate...) NO son @Serializable: hace falta un KSerializer.

object BigDecimalAsString : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeString(value.toPlainString())
    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeString())
}

object LocalDateIso : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

/**
 * Money (de la lección basics) como cadena "100.00 USD": nunca como número, que en muchos clientes pasa por Double.
 * Al leer usa el constructor, que exige la escala de la moneda: "1.5 USD" es un error, no se redondea en silencio.
 */
object MoneyAsString : KSerializer<Money> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Money", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Money) =
        encoder.encodeString("${value.amount.toPlainString()} ${value.currency.currencyCode}")

    override fun deserialize(decoder: Decoder): Money {
        val parts = decoder.decodeString().split(' ')
        require(parts.size == 2) { "se esperaba \"<monto> <moneda>\": $parts" }
        return Money(BigDecimal(parts[0]), Currency.getInstance(parts[1]))
    }
}

/** Comisión cobrada por una orden. */
@Serializable
data class Fee(val orderId: String, @Serializable(with = MoneyAsString::class) val amount: Money)

/** Cierre diario de un ticker: dos tipos de Java (LocalDate y BigDecimal), cada uno con su serializador. */
@Serializable
data class DailyClose(
    val ticker: String,
    @Serializable(with = LocalDateIso::class) val date: LocalDate,
    @Serializable(with = BigDecimalAsString::class) val close: BigDecimal,
)
