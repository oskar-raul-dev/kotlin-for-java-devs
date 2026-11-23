package dev.raul.oskar.learning.kotlin.hotflows

import dev.raul.oskar.learning.kotlin.cases.ACC_EUR
import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() = runBlocking {
    println("== Tablero de precios ==")
    val board = PriceBoard()
    val alerts = launch { board.alerts.take(3).collect { println("alerta: $it") } }
    yield()
    tickFeed(steps = 120).forEach { board.onTick(it) }
    alerts.join()            // el buffer de 64 guardó las alertas; el colector las procesa ahora y termina con take(3)
    println("último ACME: ${board.price("ACME").value}; último NUBE: ${board.price("NUBE").value}")

    println("\n== StateFlow y equals ==")
    val list = MutableStateFlow(mutableListOf<String>())
    val before = list.value
    mutatingInPlaceDoesNotEmit(list)
    println("misma referencia después de mutar: ${list.value === before} (nadie fue avisado)")

    println("\n== update frente a value = value + 1 (100 corrutinas × 1000) ==")
    println("update: ${concurrentIncrements(100, 1_000, atomic = true)}")
    println("value = value + 1: ${concurrentIncrements(100, 1_000, atomic = false)} (se pierden incrementos)")

    println("\n== SharedFlow sin suscriptores ==")
    println("replay 0: ${emitWithoutSubscribers(0, 3)}")
    println("replay 2: ${emitWithoutSubscribers(2, 3)}")

    println("\n== Saldos en vivo ==")
    val balances = LiveBalances()
    balances.post(Posting(ACC_USD, Money.of("1000.00")))
    balances.transfer(ACC_USD, ACC_EUR, Money.of("250.00"))
    println("$ACC_USD = ${balances.balance(ACC_USD).value}, $ACC_EUR = ${balances.balance(ACC_EUR).value}")
}
