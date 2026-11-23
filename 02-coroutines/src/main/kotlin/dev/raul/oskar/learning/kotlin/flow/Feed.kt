package dev.raul.oskar.learning.kotlin.flow

import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.atomic.AtomicInteger

/**
 * El feed del Mercado como `Flow`: emite cada tick en su instante (`atMillis` relativo al primero), así que en
 * `runTest` los tiempos son exactos. Es **frío**: no hace nada hasta que alguien llama a `collect`, y cada `collect`
 * vuelve a correr el bloque desde el principio, como un `Stream` que se puede recorrer muchas veces. [opened] cuenta
 * cuántas veces se abrió la "fuente".
 */
class TickFeed(private val ticks: List<Tick>) {
    val opened = AtomicInteger()

    fun flow(): Flow<Tick> = flow {
        opened.incrementAndGet()
        var last = ticks.firstOrNull()?.atMillis ?: 0
        for (tick in ticks) {
            delay(tick.atMillis - last)                 // emit suspende: si el consumidor es lento, el productor espera
            last = tick.atMillis
            emit(tick)
        }
    }
}
