package dev.raul.oskar.learning.kotlin.cancellation

import dev.raul.oskar.learning.kotlin.cases.ACC_EUR
import dev.raul.oskar.learning.kotlin.cases.ACC_USD
import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.time.Duration.Companion.milliseconds

fun main() = runBlocking {
    println("== Cancelación cooperativa ==")
    for (cooperative in listOf(false, true)) {
        val processed = mutableListOf<Int>()
        lateinit var job: Job
        job = launch { processBatch((1..10).toList(), cooperative, processed) { if (it == 3) job.cancel() } }
        job.join()
        println("cooperative=$cooperative -> procesó $processed")
    }

    println("\n== Atrapar la cancelación ==")
    for (body in listOf(::swallowingCancellation, ::rethrowingCancellation)) {
        val log = mutableListOf<String>()
        val job = launch { body(log) }
        delay(10)
        job.cancelAndJoin()
        println("$log")
    }

    println("\n== Tiempos límite ==")
    val slow = FxRateClient { _, _ -> delay(500); BigDecimal("0.92") }
    println("rateOrNull(100 ms) = ${rateOrNull(slow, Money.USD, Money.EUR, 100.milliseconds)}")
    println("rateOrFail(100 ms) -> ${runCatching { rateOrFail(slow, Money.USD, Money.EUR, 100.milliseconds) }.exceptionOrNull()}")

    println("\n== Compensación ==")
    for (nonCancellable in listOf(true, false)) {
        val accounts = RemoteAccounts(mapOf(ACC_USD to Money.of("1000.00")), latency = 20.milliseconds)
        val transfer = CompensatingTransfer(accounts, slow, fxTimeout = 1_000.milliseconds, nonCancellable)
        val job = launch { transfer.transfer(ACC_USD, ACC_EUR, Money.of("100.00"), Money.EUR) }
        delay(100)                       // ya debitó; está esperando la tasa
        job.cancelAndJoin()
        println("NonCancellable=$nonCancellable -> saldo ${accounts.balanceOf(ACC_USD)}, log ${accounts.log}")
    }

    println("\n== supervisorScope frente a coroutineScope ==")
    val follow: suspend (String) -> Int = { t -> delay(50); if (t == "LUMA") error("feed caído: $t") else t.length }
    println("followEach: ${followEach(TICKERS, follow)}")
    println("followAllOrNothing: ${runCatching { followAllOrNothing(TICKERS, follow) }.exceptionOrNull()?.message}")

    println("\n== CoroutineExceptionHandler ==")
    val seen = mutableListOf<String>()
    val scope = CoroutineScope(SupervisorJob() + coroutineContext.minusKey(Job))
    scope.launchWithHandler(seen) { error("falla en la raíz") }.join()
    println(seen)
}
