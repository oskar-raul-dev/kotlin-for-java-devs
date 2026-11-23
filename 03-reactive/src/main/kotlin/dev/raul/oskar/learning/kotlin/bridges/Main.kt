package dev.raul.oskar.learning.kotlin.bridges

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import reactor.util.context.Context
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

fun main() = runBlocking {
    val client = ReactivePriceClient(mapOf("ACME" to Money.of("100.00")))
    println("== Reactor -> corrutinas ==")
    println("awaitSingle(ACME) = ${client.priceOf("ACME")}")
    println("awaitSingleOrNull(ZZZZ) = ${client.priceOrNull("ZZZZ")}")
    println("awaitSingle(ZZZZ) -> ${runCatching { client.priceOf("ZZZZ") }.exceptionOrNull()}")
    println("feed como Flow: ${client.feedAsFlow(10).count()} ticks")

    println("\n== Corrutinas -> Reactor ==")
    println("mono { } = ${quoteMono("ORBX").block()}")

    println("\n== Contexto en los dos sentidos ==")
    println("Reactor -> corrutina: ${correlationIdSeenByCoroutine().contextWrite(Context.of("correlationId", "req-41")).block()}")
    println("corrutina -> Reactor: ${correlationIdSeenByReactor("req-42")}")

    println("\n== Cancelación en los dos sentidos ==")
    val cancelled = AtomicBoolean()
    println("take(3) del Flow: ${endlessReactorFeed(cancelled).asFlow().take(3).toList()}; suscripción cancelada: $cancelled")
    val stopped = AtomicBoolean()
    println("take(3) del Flux: ${endlessCoroutineFeed(stopped).take(3).collectList().block()}")
    Thread.sleep(50)
    println("corrutina detenida: $stopped")

    println("\n== Backpressure a través del puente ==")
    val produced = AtomicInteger()
    countingFlowAsFlux(produced).limitRate(10).take(25).blockLast()
    println("se pidieron 25; el Flow produjo ${produced.get()} de 1000")
}
