package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.time.LocalDate

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

/** Cierre diario de un ticker: dos tipos de Java (LocalDate y BigDecimal), cada uno con su serializador. */
@Serializable
data class DailyClose(
    val ticker: String,
    @Serializable(with = LocalDateIso::class) val date: LocalDate,
    @Serializable(with = BigDecimalAsString::class) val close: BigDecimal,
)
