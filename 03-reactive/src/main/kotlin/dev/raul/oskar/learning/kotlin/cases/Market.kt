package dev.raul.oskar.learning.kotlin.cases

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Random

/**
 * Lo mínimo del Mercado (docs/Casos.md, caso B) para `03-reactive`: los cinco tickers con su precio inicial, el tick, el
 * feed sintético con semilla y sus defectos, y las velas calculadas con colecciones comunes (la **referencia** contra
 * la que se comparan los pipelines de Reactor, RxJava y `Flow`). Es la misma generación que `02-coroutines/cases`.
 */
val TICKERS: List<String> = listOf("ACME", "ORBX", "LUMA", "KAPA", "NUBE")

val REFERENCE_PRICES: Map<String, Money> = mapOf(
    "ACME" to Money.of("100.00"),
    "ORBX" to Money.of("42.50"),
    "LUMA" to Money.of("18.20"),
    "KAPA" to Money.of("250.00"),
    "NUBE" to Money.of("7.35"),
)

/** Un precio observado en un instante (milisegundos desde el inicio de la sesión). */
data class Tick(val ticker: String, val atMillis: Long, val price: Money)

/** Feed con semilla: caminata de ±0.5 % por paso, un tick por ticker cada [stepMillis]. */
fun tickFeed(seed: Long = 42, steps: Int = 120, stepMillis: Long = 1_000, tickers: List<String> = TICKERS): List<Tick> {
    val random = Random(seed)
    val prices = tickers.associateWith { REFERENCE_PRICES.getValue(it).amount }.toMutableMap()
    return buildList {
        for (step in 0 until steps) {
            for (ticker in tickers) {
                val change = BigDecimal.valueOf(random.nextInt(101) - 50L, 4)
                val next = (prices.getValue(ticker) * (BigDecimal.ONE + change)).setScale(2, RoundingMode.HALF_EVEN)
                prices[ticker] = next
                add(Tick(ticker, step * stepMillis, Money.of(next)))
            }
        }
    }
}

/** Defectos de un feed real: duplicados exactos y ticks que llegan después del siguiente de su mismo ticker. */
fun withFlaws(ticks: List<Tick>, every: Int = 10, stride: Int = TICKERS.size): List<Tick> {
    val out = ticks.toMutableList()
    var i = every / 2
    while (i + stride < out.size) {
        out[i] = out[i + stride].also { out[i + stride] = out[i] }
        i += every
    }
    return buildList {
        out.forEachIndexed { index, tick ->
            add(tick)
            if (index % every == 0) add(tick)
        }
    }
}

/** Vela OHLC de un ticker en una ventana. */
data class Candle(val ticker: String, val windowStart: Long, val open: Money, val high: Money, val low: Money, val close: Money)

/** Limpieza de referencia: sin duplicados y sin ticks que retroceden dentro de su ticker. */
fun cleanReference(ticks: List<Tick>): List<Tick> {
    val last = mutableMapOf<String, Long>()
    return ticks.filter { t -> (last[t.ticker]?.let { t.atMillis > it } ?: true).also { ok -> if (ok) last[t.ticker] = t.atMillis } }
}

/** Velas de referencia con colecciones comunes, ordenadas por ticker y ventana. */
fun candlesReference(ticks: List<Tick>, windowMillis: Long = 60_000): List<Candle> =
    ticks.groupBy { it.ticker to it.atMillis / windowMillis * windowMillis }
        .map { (key, group) -> group.toCandle(key.first, key.second) }
        .sortedWith(compareBy({ it.ticker }, { it.windowStart }))

/** Arma una vela con ticks de una misma ventana, en orden de llegada. */
fun List<Tick>.toCandle(ticker: String, windowStart: Long): Candle =
    Candle(ticker, windowStart, first().price, maxOf { it.price }, minOf { it.price }, last().price)
