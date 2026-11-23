package dev.raul.oskar.learning.kotlin.sync

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    println("== Contadores: 100 corrutinas × 1000 incrementos en Default ==")
    for (counter in listOf(VolatileCounter(), JavaAtomicCounter(), AtomicfuCounter(), StdlibAtomicCounter())) {
        println("%-20s %,d".format(counter::class.simpleName, hammer(counter)))
    }
    println("%-20s %,d".format("MutexCounter", hammer(MutexCounter())))

    println("\n== 10 000 transferencias entre 50 cuentas ==")
    val accounts = (1..50).associate { "ACC-%08d".format(it) to Money.of("1000.00") }
    for (bank in listOf(UnsafeBank(accounts), OrderedLockBank(accounts), ConfinedBank(accounts))) {
        val random = Random(42)
        withContext(Dispatchers.Default) {
            coroutineScope {
                repeat(100) {
                    val seed = random.nextLong()
                    launch {
                        val r = Random(seed)
                        repeat(100) {
                            val (a, b) = accounts.keys.shuffled(r).take(2)
                            try {
                                bank.transfer(a, b, Money.of(r.nextInt(1, 200).toString()))
                            } catch (_: InsufficientFunds) { /* se rechaza y sigue */ }
                        }
                    }
                }
            }
        }
        println("%-16s total %s (debería ser 50000.00 USD)".format(bank::class.simpleName, bank.total()))
    }

    println("\n== Deadlock: A→B y B→A con locks en orden distinto ==")
    val naive = NaiveLockBank(accounts)
    val (a, b) = accounts.keys.take(2)
    val finished = withTimeoutOrNull(2.seconds) {
        coroutineScope {
            launch { naive.transfer(a, b, Money.of("1.00")) }
            launch { naive.transfer(b, a, Money.of("1.00")) }
        }
    }
    println(if (finished == null) "colgado: se cortó a los 2 s" else "terminó")
}
