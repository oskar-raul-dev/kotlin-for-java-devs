package dev.raul.oskar.learning.kotlin.dispatchers

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/** Cuenta cuántas llamadas hay en curso a la vez y guarda el máximo. */
class ConcurrencyMeter {
    private val current = AtomicInteger()
    private val peak = AtomicInteger()
    val max: Int get() = peak.get()

    fun <T> measure(block: () -> T): T {
        val now = current.incrementAndGet()
        peak.accumulateAndGet(now, ::maxOf)
        try {
            return block()
        } finally {
            current.decrementAndGet()
        }
    }
}

/**
 * El `LedgerRepository` JDBC de `04-data`, simulado: cada llamada **bloquea** su hilo [latencyMillis] (como esperar a
 * la base). Las secciones son independientes, así que aquí no hay JDBC de verdad; para el dispatcher da lo mismo.
 */
class BlockingLedgerRepository(
    private val latencyMillis: Long = 10,
    val meter: ConcurrencyMeter = ConcurrencyMeter(),
) {
    fun balance(account: String): Money = meter.measure {
        Thread.sleep(latencyMillis)
        Money.of(account.takeLast(2))
    }
}

/**
 * Un dispatcher para la base del tamaño del pool de conexiones: una **vista** de `Dispatchers.IO` que nunca corre más
 * de [poolSize] tareas a la vez. Es el `Executors.newFixedThreadPool(n)` dedicado, sin crear ni cerrar un pool. El
 * nombre (`"db"`) compila sin opt-in en 1.11.0 y sale en el `toString()` del dispatcher y del contexto, pero **no** en
 * el nombre del hilo, que sigue siendo `DefaultDispatcher-worker-N` (medido): los hilos son los de `IO`.
 */
fun dbDispatcher(poolSize: Int): CoroutineDispatcher = Dispatchers.IO.limitedParallelism(poolSize, "db")

/**
 * El servicio llama al repositorio bloqueante con `withContext(db)`: la corrutina se muda a un hilo del dispatcher de
 * la base, bloquea **ese** hilo y vuelve con el resultado. Quien llama no se entera.
 */
class LedgerService(private val repository: BlockingLedgerRepository, private val db: CoroutineDispatcher) {
    suspend fun balance(account: String): Money = withContext(db) { repository.balance(account) }

    suspend fun balances(accounts: List<String>): List<Money> = coroutineScope {
        accounts.map { async { balance(it) } }.awaitAll()
    }
}
