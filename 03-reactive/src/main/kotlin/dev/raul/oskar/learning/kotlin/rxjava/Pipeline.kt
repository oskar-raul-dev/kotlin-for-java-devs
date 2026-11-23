package dev.raul.oskar.learning.kotlin.rxjava

import dev.raul.oskar.learning.kotlin.cases.Candle
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.toCandle
import io.reactivex.rxjava3.core.Observable

/** El mismo pipeline del feed que `reactor`, en RxJava 3. El estado, por suscripción con `defer`. */
fun Observable<Tick>.cleaned(): Observable<Tick> = Observable.defer {
    val last = mutableMapOf<String, Long>()
    filter { tick ->
        val previous = last[tick.ticker]
        (previous == null || tick.atMillis > previous).also { keep -> if (keep) last[tick.ticker] = tick.atMillis }
    }
}

/**
 * Velas: RxJava no tiene `bufferUntilChanged`, así que se agrupa por (ticker, ventana) y cada grupo se junta con
 * `toList()`. Sirve para un feed finito; en uno infinito, los grupos de ventanas viejas no se cerrarían nunca.
 */
fun Observable<Tick>.candles(windowMillis: Long = 60_000): Observable<Candle> =
    groupBy { it.ticker to it.atMillis / windowMillis * windowMillis }
        .flatMapSingle { group -> group.toList().map { ticks -> ticks.toCandle(group.key!!.first, group.key!!.second) } }

/** Cruces de umbral, como en `reactor`. */
fun Observable<Tick>.crossings(ticker: String, threshold: Money): Observable<String> = Observable.defer {
    var above: Boolean? = null
    filter { it.ticker == ticker }.concatMap { tick ->
        val nowAbove = tick.price >= threshold
        val crossed = above != null && nowAbove != above
        above = nowAbove
        if (crossed) Observable.just("${tick.atMillis}: ${if (nowAbove) "sube" else "baja"} a ${tick.price}") else Observable.empty()
    }
}
