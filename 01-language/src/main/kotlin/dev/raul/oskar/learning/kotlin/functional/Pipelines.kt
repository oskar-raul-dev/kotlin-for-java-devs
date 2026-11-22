package dev.raul.oskar.learning.kotlin.functional

import java.math.BigDecimal

// Pipelines sobre colecciones: el "Stream API" de Kotlin, pero directo sobre List.
// Ojo: List.map/filter son ANSIOSOS (crean lista intermedia en cada paso).
// asSequence() los vuelve perezosos, como un Stream de Java.

/** Un tick ya limpio del Mercado (la lección json lee el feed crudo). `day` es el día de mercado, 1 a 10. */
data class Tick(val ticker: String, val day: Int, val price: BigDecimal, val volume: Long)

val sampleTicks = listOf(
    Tick("ACME", 1, BigDecimal("100.00"), 300), Tick("ACME", 1, BigDecimal("100.40"), 200),
    Tick("ACME", 1, BigDecimal("99.80"), 500), Tick("ACME", 2, BigDecimal("101.10"), 400),
    Tick("ORBX", 1, BigDecimal("42.50"), 1_000), Tick("ORBX", 2, BigDecimal("42.10"), 1_200),
    Tick("NUBE", 2, BigDecimal("7.35"), 5_000),
)

/** groupBy + mapValues: el Collectors.groupingBy(..., summingLong) de Java. sumOf con Long: no desborda como Int. */
fun volumeByTicker(ticks: List<Tick>): Map<String, Long> =
    ticks.groupBy { it.ticker }.mapValues { (_, list) -> list.sumOf { it.volume } }

/** groupingBy + eachCount: conteo por clave, sin materializar las listas de cada grupo. */
fun ticksPerTicker(ticks: List<Tick>): Map<String, Int> = ticks.groupingBy { it.ticker }.eachCount()

/** maxBy dentro de cada grupo. */
fun highestByTicker(ticks: List<Tick>): Map<String, Tick> =
    ticks.groupBy { it.ticker }.mapValues { (_, l) -> l.maxBy { it.price } }

/** partition: dos listas en una pasada. */
fun splitLarge(ticks: List<Tick>, minVolume: Long): Pair<List<Tick>, List<Tick>> = ticks.partition { it.volume >= minVolume }

/** groupBy con selector de clave y de valor: los días en que operó cada ticker. */
fun daysPerTicker(ticks: List<Tick>): Map<String, Set<Int>> =
    ticks.groupBy({ it.ticker }, { it.day }).mapValues { it.value.toSet() }

/** Vela OHLC: apertura, máximo, mínimo, cierre y volumen. */
data class Candle(val open: BigDecimal, val high: BigDecimal, val low: BigDecimal, val close: BigDecimal, val volume: Long)

/**
 * Una vela en UNA pasada con fold: el acumulador lleva todo el estado. Supone los ticks en orden de llegada.
 * Velas por día o por minuto (groupBy + esto) son ejercicios de la lección.
 */
fun candleOf(ticks: List<Tick>): Candle? {
    val first = ticks.firstOrNull() ?: return null
    return ticks.drop(1).fold(Candle(first.price, first.price, first.price, first.price, first.volume)) { c, t ->
        Candle(c.open, c.high.max(t.price), c.low.min(t.price), t.price, c.volume + t.volume)
    }
}

/** zipWithNext: diferencias entre elementos consecutivos (cambio de precio tick a tick). */
fun deltas(values: List<Int>): List<Int> = values.zipWithNext { a, b -> b - a }

/** Secuencia perezosa e infinita: solo se calcula lo que take pide. */
fun firstSquaresAbove(limit: Int, count: Int, trace: MutableList<Int>): List<Int> =
    generateSequence(1) { it + 1 }
        .onEach { trace += it }
        .map { it * it }
        .filter { it > limit }
        .take(count)
        .toList()
