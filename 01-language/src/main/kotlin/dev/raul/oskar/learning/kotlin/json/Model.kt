package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

// Claves con guiones en el JSON -> @SerialName.
// Los campos nullable sin valor por defecto se vuelven OBLIGATORIOS en el JSON
// salvo que Json { explicitNulls = false } (ver Codec.kt).

/**
 * Un tick del feed del Mercado (data/ticks.json). El precio viaja como cadena ("100.25") para no pasar por Double;
 * BigDecimal no es @Serializable, así que lleva un serializador propio (Serializers.kt).
 */
@Serializable
data class Tick(
    val seq: Long,
    val ticker: String,
    @SerialName("ts") val epochMillis: Long,
    @Serializable(with = BigDecimalAsString::class) val price: BigDecimal?,
    val volume: Int?,
    @SerialName("bid-price") @Serializable(with = BigDecimalAsString::class) val bidPrice: BigDecimal?,
    @SerialName("ask-price") @Serializable(with = BigDecimalAsString::class) val askPrice: BigDecimal?,
)

/** Valores por defecto: si la clave falta, se usa el default (y por defecto NO se escriben). */
@Serializable
data class Settings(
    val name: String,
    val retries: Int = 3,
    val tags: List<String> = emptyList(),
)

/**
 * Jerarquía sellada: el JSON lleva un discriminador "type" con el @SerialName de cada subclase.
 * Por eso ninguna subclase puede tener una propiedad llamada `type`.
 */
@Serializable
sealed interface MarketEvent {
    val ticker: String
    val ts: Long
}

@Serializable
@SerialName("trade")
data class Trade(
    override val ticker: String,
    override val ts: Long,
    @Serializable(with = BigDecimalAsString::class) val price: BigDecimal,
    val quantity: Long,
) : MarketEvent

@Serializable
@SerialName("quote")
data class Quote(
    override val ticker: String,
    override val ts: Long,
    @Serializable(with = BigDecimalAsString::class) val bid: BigDecimal,
    @Serializable(with = BigDecimalAsString::class) val ask: BigDecimal,
) : MarketEvent

@Serializable
@SerialName("halt")
data class Halt(override val ticker: String, override val ts: Long, val reason: String) : MarketEvent
