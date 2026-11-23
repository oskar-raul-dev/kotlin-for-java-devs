package dev.raul.oskar.learning.kotlin.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

private fun threadName(): String = Thread.currentThread().name.substringBefore(" @")

/**
 * `flowOn` cambia el contexto **aguas arriba** (el productor y los operadores anteriores), como `subscribeOn` de
 * Reactor; el `collect` sigue en el contexto de quien recolecta. Devuelve (hilo del productor, hilo del colector).
 */
suspend fun upstreamAndCollectorThreads(io: CoroutineDispatcher): Pair<String, String> {
    var producer = ""
    var collector = ""
    flow { producer = threadName(); emit(1) }
        .flowOn(io)
        .collect { collector = threadName() }
    return producer to collector
}

/**
 * La trampa: `withContext` dentro de `flow { }` rompe la preservación del contexto (el `emit` ocurriría en otro
 * contexto que el del colector). Falla con `IllegalStateException` "Flow invariant is violated" (medido).
 */
fun brokenFlow(io: CoroutineDispatcher): Flow<Int> = flow {
    withContext(io) { emit(1) }
}

/**
 * `catch` solo atrapa lo que pasa **aguas arriba**: la falla del productor se convierte en un valor, pero una excepción
 * dentro del `collect` sigue de largo. Devuelve lo recolectado.
 */
suspend fun catchUpstreamOnly(failInProducer: Boolean): List<String> =
    flow {
        emit("ACME 100.00")
        if (failInProducer) error("feed caído")
        emit("ACME 100.10")
    }
        .catch { emit("error: ${it.message}") }
        .toList()

/** Un cliente de precios con la API de callbacks de siempre: registrar un listener y quitarlo. */
interface PriceListenerClient {
    fun register(ticker: String, onPrice: (String) -> Unit, onDone: () -> Unit)
    fun unregister(ticker: String)
}

/**
 * `callbackFlow` adapta una API de callbacks a un `Flow` frío: `trySend` desde el callback y `awaitClose` para quitar
 * el listener cuando el colector se cancela o el flujo termina (sin eso, el listener queda colgado). Compila sin
 * opt-in en 1.11.0 (medido).
 */
fun PriceListenerClient.prices(ticker: String): Flow<String> = callbackFlow {
    register(ticker, onPrice = { trySend(it) }, onDone = { channel.close() })
    awaitClose { unregister(ticker) }
}

/** `map` + `onEach` para registrar el pipeline sin cambiarlo. */
fun Flow<Candle>.describe(log: MutableList<String>): Flow<String> =
    map { "${it.ticker}@${it.windowStart / 1000}s ${it.open}→${it.close}" }.onEach { log += it }
