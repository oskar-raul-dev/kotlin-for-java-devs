package dev.raul.oskar.learning.kotlin.fundamentals

import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.time.measureTimedValue

// runBlocking solo en main y en pruebas: une el mundo bloqueante (main) con el de las corrutinas.
fun main() = runBlocking {
    println("== suspend no cambia de hilo ==")
    println("main corre en ${Thread.currentThread().name}; currentThreadName() = ${currentThreadName()}")

    println("\n== Cotizaciones: en serie frente a en paralelo ==")
    val (serial, serialTime) = measureTimedValue { quoteSequentially(TICKERS, SimulatedPriceSource()) }
    println("en serie:    $serial en $serialTime (suma de latencias)")
    val (parallel, parallelTime) = measureTimedValue { quoteInParallel(TICKERS, SimulatedPriceSource()) }
    println("en paralelo: $parallel en $parallelTime (la más lenta)")
    val holdings = mapOf("ACME" to 10L, "ORBX" to 25L, "NUBE" to 100L)
    println("portafolio $holdings = ${portfolioValue(holdings, SimulatedPriceSource())}")

    println("\n== Una fuente falla: las demás se cancelan ==")
    val failing = SimulatedPriceSource(failing = setOf("ORBX"))
    try {
        quoteInParallel(TICKERS, failing)
    } catch (e: QuoteUnavailable) {
        println("${e.message}; empezaron ${failing.started.toList()}, terminaron ${failing.finished.toList()}")
    }

    println("\n== Banco: saldo y tasa a la vez ==")
    val (quote, quoteTime) = measureTimedValue {
        prepareTransfer(
            ACC_USD, Money.USD, Money.EUR,
            balances = { delay(200); Money.of("1500.00") },
            fx = { _, _ -> delay(150); BigDecimal("0.92") },
        )
    }
    println("$quote en $quoteTime (≈200 ms, no 350)")

    println("\n== launch frente a async ==")
    val log = mutableListOf<String>()
    println("async devolvió ${launchVersusAsync(log)}; log = $log")

    println("\n== Cancelar al padre cancela a los hijos ==")
    println("hijos cancelados: ${cancelParentCancelsChildren()}")

    println("\n== 100 000 corrutinas ==")
    val (count, time) = measureTimedValue { manyCoroutines(100_000) }
    println("$count corrutinas de 1 s cada una en $time")
}
