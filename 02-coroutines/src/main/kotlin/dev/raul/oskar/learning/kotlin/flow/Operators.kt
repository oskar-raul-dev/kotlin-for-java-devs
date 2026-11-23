package dev.raul.oskar.learning.kotlin.flow

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow

/**
 * Limpieza del feed: descarta duplicados exactos y ticks que llegan fuera de orden dentro de su ticker. Un operador
 * propio es una extensión sobre `Flow` que devuelve otro `Flow`; el estado (lo último visto por ticker) vive dentro de
 * cada colección, así que dos `collect` no se pisan.
 */
fun Flow<Tick>.cleaned(): Flow<Tick> = flow {
    val lastSeen = mutableMapOf<String, Long>()
    collect { tick ->
        val previous = lastSeen[tick.ticker]
        if (previous == null || tick.atMillis > previous) {
            lastSeen[tick.ticker] = tick.atMillis
            emit(tick)
        }
    }
}

/** Vela OHLC de un ticker en una ventana de tiempo. */
data class Candle(val ticker: String, val windowStart: Long, val open: Money, val high: Money, val low: Money, val close: Money, val ticks: Int)

/**
 * Velas de [windowMillis] por ticker. Cada vela se emite **cuando llega el primer tick de la ventana siguiente** (o al
 * terminar el flujo): un operador con estado, escrito con `flow { collect { } }`.
 */
fun Flow<Tick>.candles(windowMillis: Long = 60_000): Flow<Candle> = flow {
    val open = mutableMapOf<String, Candle>()
    collect { tick ->
        val start = tick.atMillis / windowMillis * windowMillis
        val current = open[tick.ticker]
        if (current != null && current.windowStart != start) {
            emit(current)
            open.remove(tick.ticker)
        }
        val candle = open[tick.ticker]
        open[tick.ticker] = if (candle == null) {
            Candle(tick.ticker, start, tick.price, tick.price, tick.price, tick.price, 1)
        } else {
            candle.copy(high = maxOf(candle.high, tick.price), low = minOf(candle.low, tick.price), close = tick.price, ticks = candle.ticks + 1)
        }
    }
    open.values.sortedBy { it.ticker }.forEach { emit(it) }
}

/** Alerta de precio: un ticker cruzó un umbral hacia arriba o hacia abajo. */
data class PriceAlert(val ticker: String, val atMillis: Long, val price: Money, val crossedUp: Boolean)

/**
 * Alertas cuando el precio de [ticker] cruza [threshold]. El estado (de qué lado estaba) se declara **dentro** del
 * `flow { }`: si viviera afuera, dos `collect` del mismo `Flow` lo compartirían y se pisarían.
 */
fun Flow<Tick>.alerts(ticker: String, threshold: Money): Flow<PriceAlert> = flow {
    var above: Boolean? = null
    filter { it.ticker == ticker }.collect { tick ->
        val nowAbove = tick.price >= threshold
        if (above != null && nowAbove != above) emit(PriceAlert(ticker, tick.atMillis, tick.price, nowAbove))
        above = nowAbove
    }
}
