package dev.raul.oskar.learning.kotlin.oop

// Enums con propiedades, métodos, cuerpo por constante y fábrica en companion object.

/** Tipos de cuenta del ledger de doble partida (docs/Casos.md). */
enum class AccountType(val code: String, val increasesWithDebit: Boolean) {
    ASSET("asset", true),
    LIABILITY("liability", false),
    INCOME("income", false) {
        override fun closesAtMonthEnd() = true          // cuerpo propio por constante
    },
    EXPENSE("expense", true) {
        override fun closesAtMonthEnd() = true
    };                                                  // ; obligatorio antes de miembros

    /** Las cuentas de resultados (ingresos y gastos) se saldan en el cierre de mes; las de balance, no. */
    open fun closesAtMonthEnd(): Boolean = false

    // companion object = miembros "static". AccountType.of(...) desde Kotlin.
    companion object {
        private val byCode = entries.associateBy { it.code }   // entries: Kotlin 1.9 (antes values())

        fun of(code: String?): AccountType? = code?.lowercase()?.let { byCode[it] }

        fun ofOrThrow(code: String): AccountType =
            of(code) ?: throw IllegalArgumentException("tipo de cuenta desconocido: $code")
    }
}

enum class AccountStatus {
    OPEN, FROZEN, CLOSED;

    val acceptsDeposits: Boolean get() = this != CLOSED
    val acceptsWithdrawals: Boolean get() = this == OPEN
}

enum class Side {
    BUY, SELL;

    fun opposite(): Side = when (this) {
        BUY -> SELL
        SELL -> BUY
    }
}

/** when sobre enum también es exhaustivo. */
fun label(type: AccountType): String = when (type) {
    AccountType.ASSET -> "Activo"
    AccountType.LIABILITY -> "Pasivo"
    AccountType.INCOME -> "Ingreso"
    AccountType.EXPENSE -> "Gasto"
}
