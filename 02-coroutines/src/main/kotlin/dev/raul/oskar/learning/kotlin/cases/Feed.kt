package dev.raul.oskar.learning.kotlin.cases

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Random

/**
 * Feed sintético del Mercado, generado en código con semilla fija (no hay `ticks.json` en esta sección: las secciones
 * no dependen entre sí). Cada ticker arranca en su precio de referencia y hace una caminata aleatoria de ±0.5 % por
 * paso; todos los tickers emiten un tick cada [stepMillis], en el orden de [TICKERS].
 *
 * `java.util.Random` con semilla da la misma secuencia en cualquier JVM (su algoritmo está especificado), así que las
 * pruebas pueden fijar valores exactos.
 */
fun tickFeed(seed: Long = 42, steps: Int = 120, stepMillis: Long = 1_000, tickers: List<String> = TICKERS): List<Tick> {
    val random = Random(seed)
    val prices = tickers.associateWith { REFERENCE_PRICES.getValue(it).amount }.toMutableMap()
    return buildList {
        for (step in 0 until steps) {
            for (ticker in tickers) {
                val change = BigDecimal.valueOf(random.nextInt(101) - 50L, 4)        // −0.0050 … +0.0050
                val next = (prices.getValue(ticker) * (BigDecimal.ONE + change)).setScale(2, RoundingMode.HALF_EVEN)
                prices[ticker] = next
                add(Tick(ticker, step * stepMillis, Money.of(next)))
            }
        }
    }
}

/**
 * El mismo feed con los defectos de un feed real: cada [every]-ésimo tick sale duplicado y, cada [every] posiciones
 * (con desfase), un tick llega después que el siguiente del **mismo ticker** (se intercambian dos ticks separados por
 * [stride], el número de tickers del feed). Sirve para los operadores de limpieza de `flow`.
 */
fun withFlaws(ticks: List<Tick>, every: Int = 10, stride: Int = TICKERS.size): List<Tick> {
    val out = ticks.toMutableList()
    var i = every / 2
    while (i + stride < out.size) {                    // desorden dentro de un ticker
        out[i] = out[i + stride].also { out[i + stride] = out[i] }
        i += every
    }
    return buildList {
        out.forEachIndexed { index, tick ->
            add(tick)
            if (index % every == 0) add(tick)          // duplicado exacto
        }
    }
}
