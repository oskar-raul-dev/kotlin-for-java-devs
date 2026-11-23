package dev.raul.oskar.learning.kotlin.reactor

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    println("== Perezoso ==")
    val calls = AtomicInteger()
    val quote = lazyQuote(calls)
    println("armado: ${calls.get()} ejecuciones")
    println("block(): ${quote.block()}; otra vez: ${quote.block()}; ejecuciones: ${calls.get()}")

    println("\n== null frente a vacío ==")
    println("justOrEmpty(null).block() = ${priceOrEmpty(null).block()}")

    println("\n== block() en un hilo no bloqueante ==")
    println(blockInsideParallel()?.message)

    println("\n== Pipeline del feed: limpieza, velas y cruces ==")
    val raw = withFlaws(tickFeed(steps = 180))
    val candles = raw.toFlux().cleaned().candles().collectList().block()!!
    println("${raw.size} ticks crudos -> ${candles.size} velas de 1 minuto")
    candles.filter { it.ticker == "ACME" }.forEach(::println)
    raw.toFlux().cleaned().crossings("ACME", Money.of("100.00")).take(3).doOnNext(::println).blockLast()

    println("\n== flatMap con y sin límite ==")
    for (concurrency in listOf(256, 2)) {
        val peak = AtomicInteger()
        quoteAll(listOf("ACME", "ORBX", "LUMA", "KAPA", "NUBE"), concurrency, peak) {
            Mono.just(Money.of("1.00")).delayElement(Duration.ofMillis(20))
        }.blockLast()
        println("concurrencia $concurrency -> pico de suscripciones internas ${peak.get()}")
    }
}
