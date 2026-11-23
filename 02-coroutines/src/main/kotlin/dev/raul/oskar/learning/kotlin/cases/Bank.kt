package dev.raul.oskar.learning.kotlin.cases

import java.math.BigDecimal
import java.util.Currency

/**
 * Lo mínimo del Banco (docs/Casos.md, caso A) que usan las lecciones de `02-coroutines`. Las cuentas siguen el
 * formato `ACC-XXXXXXXX-YY` del caso; todas son inventadas.
 */
const val ACC_USD = "ACC-00001234-82"
const val ACC_EUR = "ACC-00005678-39"
const val ACC_OPS = "ACC-00009012-28"

/** Tasa de cambio, como el `FxRateClient` de `01-language/exceptions`, pero `suspend`. */
fun interface FxRateClient {
    suspend fun rate(from: Currency, to: Currency): BigDecimal
}

/** Saldo de una cuenta, servido por un cliente remoto (simulado en las pruebas). */
fun interface BalanceClient {
    suspend fun balance(account: String): Money
}
