package dev.raul.oskar.learning.kotlin.testing

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Lo que deja una sesión de mercado: cuántas velas de un minuto, cuántas alertas y el cierre de cada ticker. */
data class SessionSummary(val candles: Int, val alerts: Int, val close: Map<String, Money>)

/** El feed de una sesión de [minutes] minutos, un tick por ticker y segundo, emitido en su instante. */
fun sessionFeed(seed: Long, minutes: Int): Flow<Tick> {
    val ticks = tickFeed(seed = seed, steps = minutes * 60, stepMillis = 1_000)
    return flow {
        var now = 0L
        for (tick in ticks) {
            delay(tick.atMillis - now)
            now = tick.atMillis
            emit(tick)
        }
    }
}

/**
 * Procesa una sesión: cuenta velas de un minuto por ticker y alertas cuando un precio se mueve 1 % desde la última
 * alerta. Con el feed en tiempo real tardaría lo que dura la sesión; en `runTest`, milisegundos.
 */
suspend fun summarize(feed: Flow<Tick>): SessionSummary {
    val minutesSeen = TICKERS.associateWith { mutableSetOf<Long>() }
    val reference = mutableMapOf<String, Money>()
    val close = mutableMapOf<String, Money>()
    var alerts = 0
    feed.collect { tick ->
        minutesSeen.getValue(tick.ticker) += tick.atMillis / 60_000
        close[tick.ticker] = tick.price
        val ref = reference.getOrPut(tick.ticker) { tick.price }
        if ((tick.price - ref).amount.abs() * 100.toBigDecimal() >= ref.amount) {
            alerts++
            reference[tick.ticker] = tick.price
        }
    }
    return SessionSummary(minutesSeen.values.sumOf { it.size }, alerts, close)
}
