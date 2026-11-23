package dev.raul.oskar.learning.kotlin.dispatchers

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTime

fun main() = runBlocking {
    println("== Hilos de cada dispatcher ==")
    println("Default -> ${threadNameOn(Dispatchers.Default)}")
    println("IO      -> ${threadNameOn(Dispatchers.IO)}")
    println("db      -> ${threadNameOn(dbDispatcher(4))}")
    virtualThreadDispatcher().use { println("virtual -> '${threadNameOn(it)}' (los hilos virtuales no tienen nombre)") }

    println("\n== 200 llamadas bloqueantes de 10 ms ==")
    val accounts = List(200) { "ACC-%08d-%02d".format(it, it % 97) }
    suspend fun run(name: String, dispatcher: CoroutineDispatcher) {
        val repository = BlockingLedgerRepository(latencyMillis = 10)
        val time = measureTime { LedgerService(repository, dispatcher).balances(accounts) }
        println("%-24s %6d ms, máximo simultáneo %3d".format(name, time.inWholeMilliseconds, repository.meter.max))
    }
    run("Default", Dispatchers.Default)
    run("IO", Dispatchers.IO)
    run("IO.limitedParallelism(4)", dbDispatcher(4))
    virtualThreadDispatcher().use { run("hilos virtuales", it) }

    println("\n== ThreadLocal y withContext ==")
    println("sin propagar: ${correlationIdAfterSwitch("req-42", propagate = false)}")
    println("asContextElement: ${correlationIdAfterSwitch("req-42", propagate = true)}")

    println("\n== Unconfined ==")
    println("antes y después del delay: ${unconfinedThreads()}")

    println("\n== Estado confinado con limitedParallelism(1) ==")
    println("100 corrutinas × 1000 incrementos = ${confinedCounter(100, 1_000)}")

    println("\n== Velas en Default ==")
    val ticks = listOf(
        Tick("ACME", 1_000, Money.of("100.00")), Tick("ACME", 30_000, Money.of("101.50")),
        Tick("ACME", 59_000, Money.of("99.80")), Tick("ACME", 61_000, Money.of("100.20")),
    )
    candles(ticks).forEach(::println)
}
