package dev.raul.oskar.learning.kotlin.internals

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

fun main() {
    println("== CPS: una función suspend devuelve el resultado o COROUTINE_SUSPENDED ==")
    val done = RecordingContinuation<Int>()
    println("sin suspender: ${callWithContinuation({ 42 }, done)}")
    val parking = Parking<Int>()
    val waiting = RecordingContinuation<Int>()
    val r = callWithContinuation({ parking.park() + 1 }, waiting)
    println("suspendida: ${r === COROUTINE_SUSPENDED}; resultado todavía: ${waiting.result}")
    parking.parked!!.resumeWith(Result.success(41))
    println("después de reanudar a mano: ${waiting.result}")

    println("\n== La máquina de estados por dentro ==")
    println("campos de una lambda suspend: ${stateMachineFields { val x = 1; delay(1); println(x) }}")

    println("\n== Callback -> suspend ==")
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    val client = object : CallbackQuoteClient {
        override fun request(ticker: String, onSuccess: (Money) -> Unit, onError: (Throwable) -> Unit): Cancellable {
            val f = scheduler.schedule({ onSuccess(Money.of("100.25")) }, 50, TimeUnit.MILLISECONDS)
            return Cancellable { f.cancel(false) }
        }
    }
    runBlocking {
        println("ACME = ${withContext(QuoteRequestId("req-7")) { client.quote("ACME") }}")
        println("id en el contexto: ${withContext(QuoteRequestId("req-8")) { currentRequestId() }}")
    }
    scheduler.shutdown()

    println("\n== sequence: generadores sin hilos ==")
    val generated = mutableListOf<Money>()
    println(priceWalk(Money.of("100.00"), BigDecimal("0.01"), generated).take(4).toList())
    println("generados: ${generated.size} (perezoso: solo los que se pidieron)")
    println(orderLifecycle(listOf(30, 50, 20)).toList())
}
