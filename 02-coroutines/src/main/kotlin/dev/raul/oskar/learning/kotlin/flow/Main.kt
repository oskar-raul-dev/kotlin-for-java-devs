package dev.raul.oskar.learning.kotlin.flow

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

fun main() = runBlocking {
    // 2 minutos de feed comprimidos: un tick por ticker cada 10 ms (para que la demo tarde poco en tiempo real).
    val raw = withFlaws(tickFeed(steps = 120, stepMillis = 10))
    val feed = TickFeed(raw)

    println("== Frío: cada collect abre la fuente de nuevo ==")
    println("ticks crudos: ${feed.flow().count()}, limpios: ${feed.flow().cleaned().count()}; fuente abierta ${feed.opened} veces")

    println("\n== Velas de 300 ms (en la demo) ==")
    val log = mutableListOf<String>()
    feed.flow().cleaned().candles(windowMillis = 300).describe(log).take(5).toList()
    log.forEach(::println)

    println("\n== Alertas de ACME al cruzar 100.00 ==")
    feed.flow().cleaned().alerts("ACME", Money.of("100.00")).take(3).toList().forEach(::println)

    println("\n== flowOn ==")
    println("productor y colector: ${upstreamAndCollectorThreads(Dispatchers.IO)}")
    println("withContext dentro de flow: ${runCatching { brokenFlow(Dispatchers.IO).toList() }.exceptionOrNull()?.message?.lineSequence()?.first()}")

    println("\n== catch ==")
    println(catchUpstreamOnly(failInProducer = true))

    println("\n== Consumidor lento: 20 precios cada 10 ms, consumidor de 30 ms ==")
    for (strategy in Strategy.entries) {
        val (got, time) = measureTimedValue {
            slowConsumer(fastPrices(20, 10, mutableListOf()) { 0 }, consumerMillis = 30, strategy = strategy)
        }
        println("%-8s recibió %2d en %s".format(strategy, got.size, time))
    }
}
