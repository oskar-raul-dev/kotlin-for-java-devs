package dev.raul.oskar.learning.kotlin.jdbc

import java.math.BigDecimal
import java.math.RoundingMode

/** Fila de la tabla product. id es null hasta que la base lo genera. */
data class Product(
    val sku: String,
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val id: Long? = null,
) {
    init {
        require(stock >= 0) { "stock negativo" }
    }

    companion object {
        fun money(text: String): BigDecimal = BigDecimal(text).setScale(2, RoundingMode.HALF_EVEN)
    }
}
