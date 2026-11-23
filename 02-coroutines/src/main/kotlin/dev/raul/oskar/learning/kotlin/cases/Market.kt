package dev.raul.oskar.learning.kotlin.cases

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Lo mínimo del Mercado (docs/Casos.md, caso B) que usan las lecciones de `02-coroutines`. Tickers y precios
 * inventados, los mismos cinco que genera `01-language/datagen`.
 */
val TICKERS: List<String> = listOf("ACME", "ORBX", "LUMA", "KAPA", "NUBE")

/** Precio de referencia de cada ticker: el precio inicial de `datagen` (inventado). */
val REFERENCE_PRICES: Map<String, Money> = mapOf(
    "ACME" to Money.of("100.00"),
    "ORBX" to Money.of("42.50"),
    "LUMA" to Money.of("18.20"),
    "KAPA" to Money.of("250.00"),
    "NUBE" to Money.of("7.35"),
)

/** Latencia simulada de cada fuente de precios: fija, para que las pruebas en tiempo virtual sean exactas. */
val REFERENCE_LATENCIES: Map<String, Duration> = mapOf(
    "ACME" to 120.milliseconds,
    "ORBX" to 80.milliseconds,
    "LUMA" to 300.milliseconds,
    "KAPA" to 50.milliseconds,
    "NUBE" to 150.milliseconds,
)

/** Un precio observado en un instante (milisegundos desde el inicio de la sesión). */
data class Tick(val ticker: String, val atMillis: Long, val price: Money)
