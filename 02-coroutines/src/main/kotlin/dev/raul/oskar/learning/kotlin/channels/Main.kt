package dev.raul.oskar.learning.kotlin.channels

import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds

fun main() = runBlocking {
    println("== Rendezvous ==")
    rendezvousLog().forEach(::println)

    println("\n== Capacidad del canal: envíos antes de que llegue el consumidor ==")
    for (capacity in listOf(Channel.RENDEZVOUS, 3, Channel.UNLIMITED)) {
        println("capacidad $capacity -> ${sendsBeforeConsumer(capacity)} de 10")
    }
    println("trySend en un canal de 2: ${trySendIntoFull(2, 4)}")

    println("\n== Fan-out: tres trabajadores se reparten 30 ticks ==")
    fanOut(tickFeed(steps = 6), workers = 3).forEach { (w, ticks) -> println("trabajador $w: ${ticks.size} ticks") }

    println("\n== select ==")
    coroutineScope {
        val primary = Channel<String>()
        val backup = Channel<String>()
        launch { delay(80); primary.send("ACME 100.10") }
        launch { delay(30); backup.send("ACME 100.12") }
        println(firstQuote(primary, backup))
        coroutineContext.cancelChildren()
    }
    println(quoteOrTimeout(Channel(), 50.milliseconds))

    println("\n== Motor de emparejamiento como actor ==")
    coroutineScope {
        val engine = MatchingEngine(this)
        engine.place(LimitOrder("S1", Side.SELL, 10_050, 100))
        engine.place(LimitOrder("S2", Side.SELL, 10_040, 50))
        engine.place(LimitOrder("B1", Side.BUY, 10_050, 120))
        println("libro: ${engine.snapshot()}")
        engine.close()
        for (execution in engine.executions) println("ejecución: $execution")
    }
}
