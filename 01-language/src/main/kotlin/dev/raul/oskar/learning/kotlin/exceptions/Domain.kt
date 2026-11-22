package dev.raul.oskar.learning.kotlin.exceptions

import dev.raul.oskar.learning.kotlin.basics.Money
import java.io.IOException
import java.math.BigDecimal
import java.util.Currency

// Excepciones propias del Banco: jerarquía sellada para que quien captura sepa qué puede llegar,
// y siempre conservar la causa (cause) al envolver. Money viene de la lección basics.

sealed class BankException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class InsufficientFunds(val accountId: String, val requested: Money, val available: Money) :
    BankException("fondos insuficientes en $accountId: pedido $requested, disponible $available")

class AccountFrozen(val accountId: String) : BankException("cuenta congelada: $accountId")

class CurrencyMismatch(val expected: Currency, val actual: Currency) :
    BankException("moneda $actual en una cuenta en $expected")

class DuplicateTransfer(val idempotencyKey: String) : BankException("transferencia repetida: $idempotencyKey")

class UnbalancedEntry(val debits: Money, val credits: Money) :
    BankException("asiento desbalanceado: débitos $debits, créditos $credits")

class FxUnavailable(cause: Throwable) : BankException("servicio de tasas de cambio no disponible", cause)

/** Simula un cliente externo de tasas de cambio que falla con una excepción de bajo nivel. */
fun interface FxRateClient {
    fun rate(from: Currency, to: Currency): BigDecimal
}

/**
 * Transferencias entre cuentas en memoria. Versión de la lección: rechaza una clave repetida con
 * [DuplicateTransfer]; la versión idempotente de verdad (misma clave → mismo resultado) es el ⚡.
 */
class TransferService(
    private val fx: FxRateClient,
    balances: Map<String, Money>,
    private val frozen: Set<String> = emptySet(),
) {
    private val balances = balances.toMutableMap()
    private val done = mutableSetOf<String>()

    fun balanceOf(accountId: String): Money =
        requireNotNull(balances[accountId]) { "cuenta inexistente: $accountId" }

    /** Devuelve el saldo nuevo de la cuenta de origen. */
    fun transfer(idempotencyKey: String, from: String, to: String, amount: Money): Money {
        require(!amount.isNegative && !amount.isZero) { "monto debe ser positivo: $amount" }   // culpa del cliente
        require(from != to) { "origen y destino iguales: $from" }
        if (idempotencyKey in done) throw DuplicateTransfer(idempotencyKey)
        if (from in frozen) throw AccountFrozen(from)
        val source = balanceOf(from)
        val target = balanceOf(to)
        if (amount.currency != source.currency) throw CurrencyMismatch(source.currency, amount.currency)
        if (source < amount) throw InsufficientFunds(from, amount, source)
        val credited = if (target.currency == amount.currency) amount else convert(amount, target.currency)
        balances[from] = source - amount
        balances[to] = target + credited
        done += idempotencyKey
        check(!balanceOf(from).isNegative) { "saldo negativo en $from" }                      // culpa nuestra
        return balanceOf(from)
    }

    private fun convert(amount: Money, to: Currency): Money = try {
        Money.of(amount.amount * fx.rate(amount.currency, to), to)
    } catch (e: IOException) {
        // Traducir a una excepción del dominio SIN perder la original: va como cause.
        throw FxUnavailable(e)
    }
}

/** Invariante del ledger: en cada asiento, débitos = créditos. */
fun checkBalanced(debits: List<Money>, credits: List<Money>) {
    val d = debits.fold(Money.zero()) { acc, m -> acc + m }
    val c = credits.fold(Money.zero()) { acc, m -> acc + m }
    if (d.compareTo(c) != 0) throw UnbalancedEntry(d, c)
}

/** when exhaustivo sobre la jerarquía sellada: sin else, y no compila si falta un caso. */
fun httpStatusFor(e: BankException): Int = when (e) {
    is CurrencyMismatch -> 400
    is DuplicateTransfer -> 409
    is InsufficientFunds -> 422
    is AccountFrozen -> 423
    is UnbalancedEntry -> 500        // un asiento desbalanceado es un bug nuestro, no del cliente
    is FxUnavailable -> 503
}

/** Recorre la cadena de causas (útil para logs y para buscar la raíz). */
fun causeChain(t: Throwable): List<String> =
    generateSequence(t) { it.cause }.map { "${it::class.simpleName}: ${it.message}" }.toList()

fun rootCause(t: Throwable): Throwable = generateSequence(t) { it.cause }.last()
