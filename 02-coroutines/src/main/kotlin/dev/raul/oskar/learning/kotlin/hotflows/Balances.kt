package dev.raul.oskar.learning.kotlin.hotflows

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap

/** Un asiento del ledger: monto con signo (positivo = crédito) sobre una cuenta. */
data class Posting(val account: String, val amount: Money)

/**
 * El saldo de cada cuenta del Banco como `StateFlow` que se actualiza con cada asiento. `update { }` hace que dos
 * asientos concurrentes sobre la misma cuenta no se pisen. Una pantalla (o un WebSocket) recolecta el `StateFlow` y
 * siempre arranca con el saldo actual.
 */
class LiveBalances {
    private val balances = ConcurrentHashMap<String, MutableStateFlow<Money>>()

    private fun flowOf(account: String): MutableStateFlow<Money> =
        balances.computeIfAbsent(account) { MutableStateFlow(Money.zero()) }

    fun balance(account: String): StateFlow<Money> = flowOf(account).asStateFlow()

    fun post(posting: Posting) = flowOf(posting.account).update { it + posting.amount }

    /** Un asiento de doble partida: dos postings que suman cero. */
    fun transfer(from: String, to: String, amount: Money) {
        post(Posting(from, Money.zero(amount.currency) - amount))
        post(Posting(to, amount))
    }
}
