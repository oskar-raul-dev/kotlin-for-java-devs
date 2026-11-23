package dev.raul.oskar.learning.kotlin.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow

enum class Strategy { NONE, BUFFER, CONFLATE }

/** Un productor que emite [count] precios, uno cada [everyMillis]; anota el instante de cada emisión en [emittedAt]. */
fun fastPrices(count: Int, everyMillis: Long, emittedAt: MutableList<Long>, clock: () -> Long): Flow<Int> = flow {
    repeat(count) {
        delay(everyMillis)
        emittedAt += clock()
        emit(it)
    }
}

/**
 * Un consumidor lento ([consumerMillis] por elemento) con cada estrategia:
 * - `NONE`: el productor espera al consumidor en cada `emit` (backpressure por suspensión, sin `request(n)`); el tiempo
 *   total es la suma de los dos.
 * - `BUFFER`: una cola acotada entre los dos (corren en corrutinas distintas); se recibe todo, en menos tiempo.
 * - `CONFLATE`: se queda solo con el último valor pendiente; sirve para precios, nunca para ejecuciones.
 */
suspend fun slowConsumer(source: Flow<Int>, consumerMillis: Long, strategy: Strategy, bufferSize: Int = 64): List<Int> {
    val received = mutableListOf<Int>()
    val shaped = when (strategy) {
        Strategy.NONE -> source
        Strategy.BUFFER -> source.buffer(bufferSize)
        Strategy.CONFLATE -> source.conflate()
    }
    shaped.collect {
        delay(consumerMillis)
        received += it
    }
    return received
}
