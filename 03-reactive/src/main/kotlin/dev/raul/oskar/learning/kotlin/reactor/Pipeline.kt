package dev.raul.oskar.learning.kotlin.reactor

import dev.raul.oskar.learning.kotlin.cases.Candle
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.toCandle
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

/**
 * El feed del Mercado como `Flux<Tick>`, cada tick en su instante (relativo al primero). `concatMap` + `delayElement`
 * respeta el orden; con `StepVerifier.withVirtualTime` los tiempos son exactos y la prueba no espera.
 */
fun timedFeed(ticks: List<Tick>): Flux<Tick> {
    val start = ticks.firstOrNull()?.atMillis ?: 0
    var previous = start
    return ticks.toFlux().concatMap { tick ->
        val gap = tick.atMillis - previous
        previous = tick.atMillis
        if (gap == 0L) Mono.just(tick) else Mono.just(tick).delayElement(Duration.ofMillis(gap))
    }
}

/**
 * Limpieza: sin duplicados ni ticks que retroceden en su ticker. El estado se crea **por suscripción** con
 * `Flux.defer`: si viviera afuera, dos suscriptores se lo compartirían (la misma trampa que `alerts` en `02 · flow`).
 */
fun Flux<Tick>.cleaned(): Flux<Tick> = Flux.defer {
    val last = mutableMapOf<String, Long>()
    filter { tick ->
        val previous = last[tick.ticker]
        (previous == null || tick.atMillis > previous).also { keep -> if (keep) last[tick.ticker] = tick.atMillis }
    }
}

/**
 * Velas de [windowMillis] por ticker: `groupBy` por ticker y, dentro de cada grupo, `bufferUntilChanged` por ventana. Los
 * grupos corren intercalados, así que las velas de tickers distintos salen en cualquier orden entre sí.
 */
fun Flux<Tick>.candles(windowMillis: Long = 60_000): Flux<Candle> =
    groupBy { it.ticker }
        .flatMap { group ->
            group.bufferUntilChanged { it.atMillis / windowMillis }
                .map { ticks -> ticks.toCandle(group.key(), ticks.first().atMillis / windowMillis * windowMillis) }
        }

/** Alertas cuando el precio de [ticker] cruza [threshold]; el estado, otra vez, por suscripción. */
fun Flux<Tick>.crossings(ticker: String, threshold: Money): Flux<String> = Flux.defer {
    var above: Boolean? = null
    filter { it.ticker == ticker }.handle { tick, sink ->
        val nowAbove = tick.price >= threshold
        if (above != null && nowAbove != above) sink.next("${tick.atMillis}: ${if (nowAbove) "sube" else "baja"} a ${tick.price}")
        above = nowAbove
    }
}
