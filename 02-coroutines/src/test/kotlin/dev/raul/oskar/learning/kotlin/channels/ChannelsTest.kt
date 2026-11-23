// currentTime pide opt-in en kotlinx-coroutines-test 1.11.0 (medido; ver la lección testing).
@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.raul.oskar.learning.kotlin.channels

import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ChannelsTest {

    @Test
    fun `a rendezvous send waits for the receiver`() = runTest {
        val log = rendezvousLog()
        assertEquals("send 1…", log[0])
        assertEquals("recibí 1", log[1])                       // el primer send no terminó antes de que alguien recibiera
        assertEquals(setOf("send 1 listo", "send 2…", "send 2 listo", "recibí 2"), log.drop(2).toSet())
    }

    @Test
    fun `capacity decides how far the producer runs ahead`() = runTest {
        assertEquals(0, sendsBeforeConsumer(Channel.RENDEZVOUS))
        assertEquals(3, sendsBeforeConsumer(3))
        assertEquals(10, sendsBeforeConsumer(Channel.UNLIMITED))
    }

    @Test
    fun `trySend on a full channel fails without throwing`() {
        assertEquals(listOf(true, true, false, false), trySendIntoFull(capacity = 2, total = 4))
    }

    @Test
    fun `produce closes the channel when the block ends`() = runTest {
        val ticks = tickFeed(steps = 3)
        assertEquals(ticks, produceTicks(ticks).toList())       // toList termina: el canal se cerró solo
    }

    @Test
    fun `consumers of one channel share the elements`() = runTest {
        val ticks = tickFeed(steps = 6)
        val received = fanOut(ticks, workers = 3)
        val all = received.values.flatten()
        assertEquals(ticks.size, all.size)                      // nadie recibió un tick dos veces…
        assertEquals(ticks.toSet(), all.toSet())                // …y no se perdió ninguno
        assertTrue(received.values.all { it.isNotEmpty() })     // los tres trabajaron
    }

    @Test
    fun `fan-in interleaves several producers`() = runTest {
        val got = fanIn(mapOf("ACME" to listOf(10, 30), "ORBX" to listOf(20)))
        assertEquals(listOf("ACME:10", "ORBX:20", "ACME:30"), got)
    }

    @Test
    fun `select takes whichever source is ready first`() = runTest {
        coroutineScope {
            val primary = Channel<String>()
            val backup = Channel<String>()
            launch { delay(80); primary.send("100.10") }
            launch { delay(30); backup.send("100.12") }
            assertEquals("respaldo: 100.12", firstQuote(primary, backup))
            assertEquals(30, currentTime)
            coroutineContext.cancelChildren()
        }
    }

    @Test
    fun `select can time out`() = runTest {
        assertEquals("sin cotización en 50ms", quoteOrTimeout(Channel(), 50.milliseconds))
        assertEquals(50, currentTime)
    }

    @Test
    fun `merge reads both channels until both close`() = runTest {
        coroutineScope {
            val a = Channel<String>()
            val b = Channel<String>()
            launch { a.send("a1"); delay(20); a.send("a2"); a.close() }
            launch { delay(10); b.send("b1"); b.close() }
            assertEquals(listOf("a1", "b1", "a2"), mergeUntilClosed(a, b))
        }
    }

    @Test
    fun `the book matches by price then time with partial fills`() {
        val book = MatchingBook()
        assertTrue(book.place(LimitOrder("S1", Side.SELL, 10_050, 100)).isEmpty())
        assertTrue(book.place(LimitOrder("S2", Side.SELL, 10_040, 50)).isEmpty())
        assertTrue(book.place(LimitOrder("S3", Side.SELL, 10_050, 40)).isEmpty())
        val executions = book.place(LimitOrder("B1", Side.BUY, 10_050, 120))
        assertEquals(
            listOf(Execution("B1", "S2", 10_040, 50), Execution("B1", "S1", 10_050, 70)),   // mejor precio, luego el más antiguo
            executions,
        )
        assertEquals(BookSnapshot(bids = emptyList(), asks = listOf(10_050L to 70L)), book.snapshot())
        assertTrue(book.cancel("S3"))
        assertFalse(book.cancel("S3"))
    }

    @Test
    fun `the actor answers through CompletableDeferred and closes its executions`() = runTest {
        val engine = MatchingEngine(this)
        engine.place(LimitOrder("S1", Side.SELL, 10_000, 10))
        engine.place(LimitOrder("B1", Side.BUY, 9_900, 5))
        assertEquals(BookSnapshot(listOf(9_900L to 5L), listOf(10_000L to 10L)), engine.snapshot())
        assertTrue(engine.cancel("B1"))
        engine.place(LimitOrder("B2", Side.BUY, 10_000, 4))
        engine.close()
        assertEquals(listOf(Execution("B2", "S1", 10_000, 4)), engine.executions.toList())
        engine.job.join()
    }

    @Test
    fun `a hundred senders never leave the book crossed and keep the quantity`() = runBlocking {
        withContext(Dispatchers.Default) {
            val engine = MatchingEngine(this)
            val random = Random(7)
            val orders = List(1_000) {
                LimitOrder("O$it", if (random.nextBoolean()) Side.BUY else Side.SELL, 9_950L + random.nextInt(100), 1L + random.nextInt(20))
            }
            coroutineScope {
                orders.chunked(10).forEach { batch -> launch { batch.forEach { engine.place(it) } } }   // 100 corrutinas
            }
            val snapshot = engine.snapshot()
            engine.close()
            val filled = engine.executions.toList().sumOf { it.quantity }
            assertFalse(snapshot.isCrossed)
            assertEquals(orders.sumOf { it.quantity }, snapshot.totalQuantity + 2 * filled)   // cada ejecución consume dos lados
        }
    }
}
