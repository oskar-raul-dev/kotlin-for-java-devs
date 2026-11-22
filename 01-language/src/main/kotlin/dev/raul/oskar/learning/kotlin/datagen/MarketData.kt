package dev.raul.oskar.learning.kotlin.datagen

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Random

// Datos sintéticos del Mercado (docs/Casos.md, caso B). Todo es inventado: tickers, precios y volúmenes.
//
// Se usa java.util.Random y no kotlin.random.Random a propósito: el algoritmo de java.util.Random está fijado por la
// especificación de Java, así que la misma semilla da los mismos números en cualquier JDK. kotlin.random.Random(seed)
// solo garantiza la misma secuencia dentro de una misma versión de la stdlib.

val MARKET_ZONE: ZoneId = ZoneId.of("America/New_York")
val SESSION_OPEN: LocalTime = LocalTime.of(9, 30)
val SESSION_CLOSE: LocalTime = LocalTime.of(16, 0)

/** Precio inicial de cada ticker inventado. */
val TICKERS: Map<String, BigDecimal> = linkedMapOf(
    "ACME" to BigDecimal("100.00"),
    "ORBX" to BigDecimal("42.50"),
    "LUMA" to BigDecimal("18.20"),
    "KAPA" to BigDecimal("250.00"),
    "NUBE" to BigDecimal("7.35"),
)

/** 10 días hábiles que cruzan el fin del horario de verano en Nueva York (domingo 1 de noviembre de 2026). */
val MARKET_DAYS: List<LocalDate> =
    generateSequence(LocalDate.of(2026, 10, 26)) { it.plusDays(1) }
        .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
        .take(10)
        .toList()

const val TICKS_PER_DAY = 40

/** Las imperfecciones a propósito, para que las pruebas y los ejercicios sepan qué buscar. */
object TickFlaws {
    const val DUPLICATES = 10           // copias exactas (mismo seq) justo después del original
    const val SWAPS = 15                // pares vecinos intercambiados: el archivo no viene ordenado
    const val NULL_PRICE_PERCENT = 1    // "price": null
    const val MISSING_VOLUME_PERCENT = 1 // sin la clave "volume"
    const val VENUE_PERCENT = 2         // clave desconocida "venue"
    const val QUOTE_PERCENT = 30        // con "bid-price" y "ask-price"
    val GAP_TICKER = "LUMA"             // hueco de una hora sin ticks
    val GAP_DAY: LocalDate = LocalDate.of(2026, 10, 29)
    val GAP_FROM: LocalTime = LocalTime.of(11, 0)
    val GAP_TO: LocalTime = LocalTime.of(12, 0)
    val OFF_HOURS_TICKER = "KAPA"       // un tick después del cierre
    val OFF_HOURS_DAY: LocalDate = LocalDate.of(2026, 11, 3)
    val OFF_HOURS_TIME: LocalTime = LocalTime.of(17, 5)
}

/** Un tick tal como se escribe en ticks.json (antes de ensuciarlo). */
data class RawTick(
    val ticker: String,
    val epochMillis: Long,
    val price: BigDecimal?,
    val volume: Int?,
    val quote: Boolean,
    val venue: Boolean,
) {
    var seq: Int = 0

    fun toJson(): String = buildString {
        append("""{"seq":$seq,"ticker":"$ticker","ts":$epochMillis,"price":""")
        append(if (price == null) "null" else "\"${price.toPlainString()}\"")
        if (volume != null) append(""","volume":$volume""")
        if (quote && price != null) {
            val bid = (price - CENT).max(CENT).toPlainString()
            val ask = (price + CENT).toPlainString()
            append(",\"bid-price\":\"$bid\",\"ask-price\":\"$ask\"")
        }
        if (venue) append(",\"venue\":\"SIM-1\"")
        append('}')
    }

    private companion object {
        val CENT = BigDecimal("0.01")
    }
}

/** Genera los ticks limpios y ordenados, con seq creciente. */
fun cleanTicks(seed: Long = DATA_SEED): List<RawTick> {
    val rnd = Random(seed)
    val ticks = mutableListOf<RawTick>()
    for ((ticker, start) in TICKERS) {
        var price = start
        for (day in MARKET_DAYS) {
            val open = day.atTime(SESSION_OPEN).atZone(MARKET_ZONE).toInstant().toEpochMilli()
            val sessionMillis = 6L * 3600_000 + 30 * 60_000
            val times = List(TICKS_PER_DAY) { open + (rnd.nextDouble() * sessionMillis).toLong() }.sorted()
            for (ts in times) {
                // Paseo aleatorio: ±0.2 % por tick, redondeado al centavo (tick de precio de 0.01).
                price = (price * BigDecimal.valueOf(1 + rnd.nextGaussian() * 0.002))
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .max(BigDecimal("0.01"))
                val nullPrice = rnd.nextInt(100) < TickFlaws.NULL_PRICE_PERCENT
                val missingVolume = rnd.nextInt(100) < TickFlaws.MISSING_VOLUME_PERCENT
                ticks += RawTick(
                    ticker = ticker,
                    epochMillis = ts,
                    price = if (nullPrice) null else price,
                    volume = if (missingVolume) null else (1 + rnd.nextInt(50)) * 10,
                    quote = rnd.nextInt(100) < TickFlaws.QUOTE_PERCENT,
                    venue = rnd.nextInt(100) < TickFlaws.VENUE_PERCENT,
                )
            }
        }
    }
    val gapFrom = TickFlaws.GAP_DAY.atTime(TickFlaws.GAP_FROM).atZone(MARKET_ZONE).toInstant().toEpochMilli()
    val gapTo = TickFlaws.GAP_DAY.atTime(TickFlaws.GAP_TO).atZone(MARKET_ZONE).toInstant().toEpochMilli()
    ticks.removeAll { it.ticker == TickFlaws.GAP_TICKER && it.epochMillis in gapFrom..<gapTo }
    ticks += RawTick(
        ticker = TickFlaws.OFF_HOURS_TICKER,
        epochMillis = TickFlaws.OFF_HOURS_DAY.atTime(TickFlaws.OFF_HOURS_TIME).atZone(MARKET_ZONE).toInstant().toEpochMilli(),
        price = BigDecimal("251.10"),
        volume = 30,
        quote = false,
        venue = false,
    )
    return ticks.sortedWith(compareBy({ it.epochMillis }, { it.ticker }))
        .onEachIndexed { i, t -> t.seq = i + 1 }
}

/** Ensucia la lista limpia: duplicados y desorden. */
fun flawedTicks(seed: Long = DATA_SEED): List<RawTick> {
    val rnd = Random(seed + 1)
    val ticks = cleanTicks(seed).toMutableList()
    repeat(TickFlaws.DUPLICATES) {
        val i = rnd.nextInt(ticks.size)
        ticks.add(i + 1, ticks[i].copy().also { it.seq = ticks[i].seq })
    }
    repeat(TickFlaws.SWAPS) {
        val i = rnd.nextInt(ticks.size - 1)
        ticks[i] = ticks[i + 1].also { ticks[i + 1] = ticks[i] }
    }
    return ticks
}

/** El contenido de ticks.json: un arreglo con un tick por línea, fácil de leer y de comparar en git. */
fun ticksJson(seed: Long = DATA_SEED): String =
    flawedTicks(seed).joinToString(separator = ",\n", prefix = "[\n", postfix = "\n]\n") { "  " + it.toJson() }
