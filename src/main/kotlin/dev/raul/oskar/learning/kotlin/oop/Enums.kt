package dev.raul.oskar.learning.kotlin.oop

// Enums con propiedades, métodos, abstractos por constante y fábrica en companion object.

enum class ShippingMethod(val code: String, val maxDays: Int) {
    STANDARD("standard", 7),
    EXPRESS("express", 2),
    FREIGHT("freight", 30) {
        override fun costFactor() = 0.35          // cuerpo propio por constante
    };                                            // ; obligatorio antes de miembros

    open fun costFactor(): Double = 1.0

    // companion object = miembros "static". ShippingMethod.of(...) desde Kotlin.
    companion object {
        private val byCode = entries.associateBy { it.code }   // entries: Kotlin 1.9 (antes values())

        fun of(code: String?): ShippingMethod? = code?.lowercase()?.let { byCode[it] }

        fun ofOrThrow(code: String): ShippingMethod =
            of(code) ?: throw IllegalArgumentException("método de envío desconocido: $code")
    }
}

enum class Direction { NORTH, EAST, SOUTH, WEST;

    fun turnRight(): Direction = entries[(ordinal + 1) % entries.size]
}

/** when sobre enum también es exhaustivo. */
fun label(method: ShippingMethod): String = when (method) {
    ShippingMethod.STANDARD -> "Envío estándar"
    ShippingMethod.EXPRESS -> "Envío exprés"
    ShippingMethod.FREIGHT -> "Carga pesada"
}
