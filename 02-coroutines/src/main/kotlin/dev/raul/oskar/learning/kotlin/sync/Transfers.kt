package dev.raul.oskar.learning.kotlin.sync

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class InsufficientFunds(account: String) : RuntimeException("fondos insuficientes en $account")

/** Las transferencias del Banco, en cuatro versiones con la misma interfaz. */
interface Bank {
    suspend fun transfer(from: String, to: String, amount: Money)
    fun balanceOf(account: String): Money
    fun total(): Money
}

/** Base común: el mapa de saldos y la operación sin protección. */
abstract class MapBank(initial: Map<String, Money>) : Bank {
    protected val balances = HashMap(initial)
    override fun balanceOf(account: String): Money = balances.getValue(account)
    override fun total(): Money = balances.values.fold(Money.zero(), Money::plus)

    /**
     * Leer, validar, `yield` (como si consultara algo remoto) y escribir: en el punto de suspensión puede entrar otra
     * transferencia. Con [suspendInside] = `false`, la sección crítica no tiene puntos de suspensión.
     */
    protected suspend fun move(from: String, to: String, amount: Money, suspendInside: Boolean = true) {
        val source = balances.getValue(from)
        if (source < amount) throw InsufficientFunds(from)
        if (suspendInside) yield()
        balances[from] = balances.getValue(from) - amount
        balances[to] = balances.getValue(to) + amount
    }
}

/** Sin ninguna protección: con corrutinas concurrentes se pierde (o aparece) dinero y un saldo puede quedar negativo. */
class UnsafeBank(initial: Map<String, Money>) : MapBank(initial) {
    override suspend fun transfer(from: String, to: String, amount: Money) = move(from, to, amount)
}

/**
 * Un `Mutex` por cuenta, tomados **siempre en el mismo orden** (por id de cuenta): A→B y B→A piden primero el menor, así
 * que nunca quedan esperándose. `withLock` suspende en vez de bloquear el hilo. Ojo: `Mutex` **no es reentrante**.
 */
class OrderedLockBank(initial: Map<String, Money>) : MapBank(initial) {
    private val locks = initial.keys.associateWith { Mutex() }

    override suspend fun transfer(from: String, to: String, amount: Money) {
        require(from != to) { "misma cuenta: $from" }
        val (first, second) = if (from < to) Pair(from, to) else Pair(to, from)
        locks.getValue(first).withLock {
            locks.getValue(second).withLock { move(from, to, amount) }
        }
    }
}

/**
 * La versión con *deadlock*: toma primero el lock de origen y después el de destino. Si A→B y B→A corren a la vez, cada
 * una tiene un lock y espera el otro para siempre. El `yield()` entre los dos `withLock` hace el cruce determinista.
 */
class NaiveLockBank(initial: Map<String, Money>) : MapBank(initial) {
    private val locks = initial.keys.associateWith { Mutex() }

    override suspend fun transfer(from: String, to: String, amount: Money) {
        locks.getValue(from).withLock {
            yield()
            locks.getValue(to).withLock { move(from, to, amount) }
        }
    }
}

/**
 * Sin locks: todo el estado confinado a un dispatcher de un solo hilo lógico (`limitedParallelism(1)`). Como nada corre
 * en paralelo sobre el mapa, no hay carreras de datos, y tampoco puede haber deadlock. Es la idea del actor de
 * `channels`, más corta.
 *
 * La trampa ([suspendInside] = `true`): confinar evita el **paralelismo**, no la **intercalación**. Si la sección
 * crítica tiene un punto de suspensión entre validar y escribir, otra corrutina del mismo dispatcher entra ahí y se
 * gasta el mismo saldo dos veces. Un `Mutex` sí se mantiene tomado a través de la suspensión.
 */
class ConfinedBank(
    initial: Map<String, Money>,
    base: CoroutineDispatcher = Dispatchers.Default,
    private val suspendInside: Boolean = false,
) : MapBank(initial) {
    private val confined = base.limitedParallelism(1)
    override suspend fun transfer(from: String, to: String, amount: Money) =
        withContext(confined) { move(from, to, amount, suspendInside) }
}

/** `Mutex` no es reentrante: un `withLock` anidado sobre el mismo `Mutex` en la misma corrutina espera para siempre. */
suspend fun nestedWithLock(mutex: Mutex): Nothing {
    mutex.withLock {
        mutex.withLock { error("no se llega") }
    }
}
