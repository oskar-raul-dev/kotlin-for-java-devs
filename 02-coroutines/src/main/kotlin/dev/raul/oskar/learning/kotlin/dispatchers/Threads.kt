package dev.raul.oskar.learning.kotlin.dispatchers

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * Nombre del hilo actual sin el sufijo ` @coroutine#N`, que kotlinx.coroutines agrega en modo debug (activo con
 * `-ea`, como en las pruebas de Gradle, o con `-Dkotlinx.coroutines.debug`).
 */
fun threadName(): String = Thread.currentThread().name.substringBefore(" @")

/** En qué hilo corre un bloque sobre [dispatcher]. */
suspend fun threadNameOn(dispatcher: CoroutineDispatcher): String = withContext(dispatcher) { threadName() }

/**
 * Corrutinas sobre hilos virtuales del JDK 21: compila sin opt-in (medido). Hay que cerrarlo (`use`), como cualquier
 * `ExecutorService`. Se compara con los dispatchers normales en `loom`.
 */
fun virtualThreadDispatcher(): ExecutorCoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

/** El id de correlación del log, como el MDC de SLF4J: un `ThreadLocal`. */
val correlationId: ThreadLocal<String?> = ThreadLocal()

/**
 * Un `ThreadLocal` vive en el hilo, no en la corrutina: después de un `withContext` a otro dispatcher, el valor
 * desaparece. Con `asContextElement` viaja con la corrutina y se restaura en cada hilo donde ella corre (es lo que hace
 * `MDCContext` de `kotlinx-coroutines-slf4j`).
 */
suspend fun correlationIdAfterSwitch(id: String, propagate: Boolean): String? {
    correlationId.set(id)
    try {
        val context = if (propagate) Dispatchers.IO + correlationId.asContextElement(id) else Dispatchers.IO
        return withContext(context) { correlationId.get() }
    } finally {
        correlationId.remove()
    }
}

/**
 * `Dispatchers.Unconfined` arranca en el hilo de quien lanza y, después de suspender, sigue en el hilo que la
 * reanudó: el orden y el hilo dejan de ser predecibles. Devuelve el hilo antes y después de un `delay`.
 */
suspend fun unconfinedThreads(): Pair<String, String> = coroutineScope {
    lateinit var before: String
    lateinit var after: String
    launch(Dispatchers.Unconfined) {
        before = threadName()
        delay(10)
        after = threadName()
    }.join()
    before to after
}

/**
 * Estado confinado a un solo hilo lógico: `limitedParallelism(1)` sobre `Default` serializa las corrutinas, así que
 * un `var` común no pierde incrementos. Es el `newSingleThreadExecutor()` sin pool propio (adelanto de `sync`).
 */
suspend fun confinedCounter(coroutines: Int, incrementsEach: Int): Int {
    val confined = Dispatchers.Default.limitedParallelism(1)
    var counter = 0
    coroutineScope {
        repeat(coroutines) {
            launch(confined) { repeat(incrementsEach) { counter++ } }
        }
    }
    return counter
}

/** Vela OHLC de un ticker en un minuto. */
data class Candle(val ticker: String, val minute: Long, val open: Money, val high: Money, val low: Money, val close: Money)

/**
 * Las velas del Mercado son cálculo puro (CPU): van en `Dispatchers.Default`, que tiene tantos hilos como núcleos. El
 * dispatcher se inyecta para poder probarlo con el de la prueba.
 */
suspend fun candles(ticks: List<Tick>, dispatcher: CoroutineDispatcher = Dispatchers.Default): List<Candle> =
    withContext(dispatcher) {
        ticks.groupBy { it.ticker to it.atMillis / 60_000 }
            .map { (key, group) ->
                val sorted = group.sortedBy { it.atMillis }
                Candle(
                    ticker = key.first,
                    minute = key.second,
                    open = sorted.first().price,
                    high = sorted.maxOf { it.price },
                    low = sorted.minOf { it.price },
                    close = sorted.last().price,
                )
            }
            .sortedWith(compareBy({ it.minute }, { it.ticker }))
    }
