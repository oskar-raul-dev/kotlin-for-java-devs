package dev.raul.oskar.learning.kotlin.backpressure

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    fun show(strategy: Strategy, received: Received) = println(
        "${strategy.name.padEnd(7)} recibió ${received.values}" + (received.error?.let { "  error: ${it.javaClass.simpleName}" } ?: ""),
    )

    println("== Reactor: 100 precios de golpe; el consumidor pide 5 y después 10 ==")
    Strategy.entries.forEach { show(it, eagerReactorSource(100).withStrategy(it).consumeSlowly(5, 10)) }

    println("\n== RxJava (Flowable), igual ==")
    Strategy.entries.forEach { show(it, eagerRxSource(100).withStrategy(it).consumeSlowly(5, 10)) }

    println("\n== Flow: 20 precios, consumidor de 10 ms ==")
    Strategy.entries.forEach { println("${it.name.padEnd(7)} recibió ${flowWithStrategy(20, it, consumerMillis = 10)}") }
}
