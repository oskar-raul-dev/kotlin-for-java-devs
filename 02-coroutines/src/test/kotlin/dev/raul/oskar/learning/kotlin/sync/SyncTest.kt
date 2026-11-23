// currentTime pide opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.sync

import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class SyncTest {

    @Test
    fun `atomic counters never lose increments`() = runBlocking {
        for (counter in listOf(JavaAtomicCounter(), AtomicfuCounter(), StdlibAtomicCounter())) {
            assertEquals(100_000, hammer(counter), counter::class.simpleName)
        }
        assertEquals(100_000, hammer(MutexCounter()))
    }

    @Test
    fun `a volatile counter is visible but not atomic`() = runBlocking {
        // Con hilos de verdad, perder incrementos es casi seguro pero no determinista: se repite hasta verlo.
        val lost = (1..20).any { hammer(VolatileCounter(), coroutines = 100, each = 10_000) < 1_000_000 }
        assertTrue(lost, "en 20 corridas ningún incremento se perdió (muy improbable con varios núcleos)")
    }

    private val accounts = (1..50).associate { "ACC-%08d".format(it) to Money.of("1000.00") }

    private suspend fun randomTransfers(bank: Bank, coroutines: Int = 100, each: Int = 100) {
        val random = Random(42)
        withContext(Dispatchers.Default) {
            repeat(coroutines) {
                val seed = random.nextLong()
                launch {
                    val r = Random(seed)
                    repeat(each) {
                        val (a, b) = accounts.keys.shuffled(r).take(2)
                        try {
                            bank.transfer(a, b, Money.of(r.nextInt(1, 200).toString()))
                        } catch (_: InsufficientFunds) {
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `ordered locks keep the total and no balance goes negative`() = runBlocking {
        val bank = OrderedLockBank(accounts)
        randomTransfers(bank)
        assertEquals(Money.of("50000.00"), bank.total())
        assertTrue(accounts.keys.none { bank.balanceOf(it).isNegative })
    }

    @Test
    fun `confined state gives the same guarantees without locks`() = runBlocking {
        val bank = ConfinedBank(accounts)
        randomTransfers(bank)
        assertEquals(Money.of("50000.00"), bank.total())
        assertTrue(accounts.keys.none { bank.balanceOf(it).isNegative })
    }

    @Test
    fun `confinement does not protect across a suspension point`() = runTest {
        val bank = ConfinedBank(
            mapOf("A" to Money.of("100.00"), "B" to Money.of("0.00"), "C" to Money.of("0.00")),
            base = StandardTestDispatcher(testScheduler),
            suspendInside = true,
        )
        coroutineScope {
            launch { bank.transfer("A", "B", Money.of("100.00")) }
            launch { bank.transfer("A", "C", Money.of("100.00")) }
        }
        assertTrue(bank.balanceOf("A").isNegative)              // un solo hilo lógico y aun así se gastó dos veces
    }

    @Test
    fun `without protection money appears or disappears`() = runTest {
        // En runTest (un solo hilo) el yield entre leer y escribir basta para intercalar dos transferencias.
        val bank = UnsafeBank(mapOf("A" to Money.of("100.00"), "B" to Money.of("0.00"), "C" to Money.of("0.00")))
        coroutineScope {
            launch { bank.transfer("A", "B", Money.of("100.00")) }
            launch { bank.transfer("A", "C", Money.of("100.00")) }
        }
        assertEquals(Money.of("200.00"), bank.balanceOf("B") + bank.balanceOf("C"))   // se gastaron 100 dos veces
        assertTrue(bank.balanceOf("A").isNegative)
    }

    @Test
    fun `crossed transfers deadlock with naive lock order`() = runTest {
        val bank = NaiveLockBank(mapOf("A" to Money.of("100.00"), "B" to Money.of("100.00")))
        val done = withTimeoutOrNull(10.seconds) {
            coroutineScope {
                launch { bank.transfer("A", "B", Money.of("1.00")) }
                launch { bank.transfer("B", "A", Money.of("1.00")) }
            }
        }
        assertNull(done)                                        // nunca terminaron: cada una espera el lock de la otra
        assertEquals(10_000, currentTime)
    }

    @Test
    fun `the same crossed transfers finish with ordered locks`() = runTest {
        val bank = OrderedLockBank(mapOf("A" to Money.of("100.00"), "B" to Money.of("100.00")))
        coroutineScope {
            launch { bank.transfer("A", "B", Money.of("1.00")) }
            launch { bank.transfer("B", "A", Money.of("1.00")) }
        }
        assertEquals(Money.of("100.00"), bank.balanceOf("A"))
    }

    @Test
    fun `Mutex is not reentrant`() = runTest {
        val mutex = Mutex()
        assertNull(withTimeoutOrNull(5.seconds) { nestedWithLock(mutex) })
        assertFalse(mutex.isLocked)                             // al cancelar, withLock soltó el lock
    }

    @Test
    fun `a semaphore caps concurrent calls`() = runTest {
        val slow = FxRateClient { _, _ -> delay(100); BigDecimal("0.92") }
        val limited = LimitedFxClient(slow, permits = 3)
        coroutineScope { repeat(10) { launch { limited.rate(Money.USD, Money.EUR) } } }
        assertEquals(3, limited.peak)
        assertEquals(400, currentTime)                          // 10 llamadas de 100 ms de a 3: cuatro tandas
    }

    @Test
    fun `getOrPut on a ConcurrentHashMap may compute twice`() {
        val cache = FeeCache()
        val bothInside = CountDownLatch(2)
        val compute = { bothInside.countDown(); bothInside.await(1, TimeUnit.SECONDS); BigDecimal("2.50") }
        val threads = List(2) { thread { cache.feeWithGetOrPut("ACC-1", compute) } }
        threads.forEach { it.join() }
        assertEquals(2, cache.computations.get())               // los dos hilos calcularon…
        assertEquals(1, cache.map.size)                         // …y quedó un solo valor
    }

    @Test
    fun `computeIfAbsent computes once`() {
        val cache = FeeCache()
        val start = CountDownLatch(1)
        val threads = List(8) { thread { start.await(); cache.feeWithComputeIfAbsent("ACC-1") { Thread.sleep(20); BigDecimal("2.50") } } }
        start.countDown()
        threads.forEach { it.join() }
        assertEquals(1, cache.computations.get())
    }
}
