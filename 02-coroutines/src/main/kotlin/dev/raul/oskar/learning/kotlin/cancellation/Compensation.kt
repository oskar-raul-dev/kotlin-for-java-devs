package dev.raul.oskar.learning.kotlin.cancellation

import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Currency
import kotlin.time.Duration

/**
 * Cuentas del Banco con operaciones remotas simuladas (`suspend`, cada una tarda [latency]). El [log] guarda cada
 * operación aplicada, en orden.
 */
class RemoteAccounts(initial: Map<String, Money>, private val latency: Duration = Duration.ZERO) {
    private val balances = initial.toMutableMap()
    val log = mutableListOf<String>()

    fun balanceOf(account: String): Money = balances.getValue(account)

    suspend fun debit(account: String, amount: Money) {
        delay(latency)
        balances[account] = balanceOf(account) - amount
        log += "debit $account $amount"
    }

    suspend fun credit(account: String, amount: Money) {
        delay(latency)
        balances[account] = (balances[account] ?: Money.zero(amount.currency)) + amount
        log += "credit $account $amount"
    }
}

/**
 * Transferencia multimoneda con tiempo límite y compensación: débito, tasa con `withTimeout`, crédito. Si algo falla
 * **o la cancelan** después del débito, el `finally` revierte el débito.
 *
 * La reversión es `suspend` (otra llamada remota). En una corrutina cancelada, cualquier `suspend` dentro de
 * `finally` lanza de inmediato: por eso va en `withContext(NonCancellable)`. Con [nonCancellable] = `false` se ve la
 * trampa: la reversión no corre y el dinero desaparece.
 */
class CompensatingTransfer(
    private val accounts: RemoteAccounts,
    private val fx: FxRateClient,
    private val fxTimeout: Duration,
    private val nonCancellable: Boolean = true,
) {
    suspend fun transfer(from: String, to: String, amount: Money, target: Currency): Money {
        accounts.debit(from, amount)
        var credited = false
        try {
            val rate = rateOrFail(fx, amount.currency, target, fxTimeout)
            val converted = amount.convert(rate, target)
            accounts.credit(to, converted)
            credited = true
            return converted
        } finally {
            if (!credited) {
                if (nonCancellable) withContext(NonCancellable) { accounts.credit(from, amount) }
                else accounts.credit(from, amount)
            }
        }
    }
}
