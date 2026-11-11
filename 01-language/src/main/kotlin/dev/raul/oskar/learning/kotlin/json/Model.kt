package dev.raul.oskar.learning.kotlin.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Claves con guiones en el JSON -> @SerialName.
// Los campos nullable sin valor por defecto se vuelven OBLIGATORIOS en el JSON
// salvo que Json { explicitNulls = false } (ver Codec.kt).

@Serializable
data class OrderEvent(
    val timestamp: Long,
    @SerialName("customer-email") val customerEmail: String?,
    val address: Address?,
    @SerialName("payment-method") val paymentMethod: String?,
    @SerialName("order-total") val orderTotal: Int?,
    @SerialName("item-count") val itemCount: Int?,
)

@Serializable
data class Address(
    val city: String,
    val state: String,
    @SerialName("zipcode") val zipCode: String,
)

/** Valores por defecto: si la clave falta, se usa el default (y por defecto NO se escriben). */
@Serializable
data class Settings(
    val name: String,
    val retries: Int = 3,
    val tags: List<String> = emptyList(),
)

/** Jerarquía sellada: el JSON lleva un discriminador "type" con el @SerialName de cada subclase. */
@Serializable
sealed class Payment {
    abstract val amount: Long
}

@Serializable
@SerialName("card")
data class CardPayment(override val amount: Long, val last4: String) : Payment()

@Serializable
@SerialName("transfer")
data class TransferPayment(override val amount: Long, val iban: String) : Payment()
