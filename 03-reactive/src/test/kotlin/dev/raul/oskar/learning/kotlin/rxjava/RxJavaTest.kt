package dev.raul.oskar.learning.kotlin.rxjava

import dev.raul.oskar.learning.kotlin.cases.Candle
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.candlesReference
import dev.raul.oskar.learning.kotlin.cases.cleanReference
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RxJavaTest {

    @Test
    fun `an error without onError goes to the global handler`() {
        val seen = errorWithoutOnError()
        assertIs<OnErrorNotImplementedException>(seen)
        assertEquals("feed caído", seen.cause?.message)
    }

    @Test
    fun `a null from an unannotated Java API passes the compiler and fails at runtime`() {
        val e = nullFromJava()
        assertIs<NullPointerException>(e)
        assertEquals("The mapper function returned a null value.", e.message)
    }

    @Test
    fun `subscribeOn picks the source thread and observeOn what comes after`() {
        val (source, subscriber) = threadsWith(Schedulers.io(), Schedulers.computation())
        assertTrue(source.startsWith("RxCachedThreadScheduler"))
        assertTrue(subscriber.startsWith("RxComputationThreadPool"))
    }

    @Test
    fun `a TestScheduler gives RxJava virtual time and disposing stops the stream`() {
        val scheduler = TestScheduler()
        val watcher = PriceWatcher(scheduler)
        watcher.watch(pricesEverySecond(scheduler))
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS)
        assertEquals(listOf("100", "101", "102").map(Money::of), watcher.prices)
        assertEquals(1, watcher.active)
        watcher.stop()
        scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        assertEquals(3, watcher.prices.size)                    // después de dispose no llega nada más
        assertEquals(0, watcher.active)
    }

    @Test
    fun `subscribeBy routes errors to the named callback`() {
        val watcher = PriceWatcher(Schedulers.trampoline())
        watcher.watch(Observable.error(IllegalStateException("sin precios")))
        assertEquals(listOf("sin precios"), watcher.errors)
    }

    @Test
    fun `TestObserver checks the pipeline`() {
        val raw = withFlaws(tickFeed(steps = 30))
        raw.toObservable().cleaned().test().assertValueSequence(cleanReference(raw)).assertComplete()
    }

    @Test
    fun `candles equal the reference and the Reactor version`() {
        val raw = withFlaws(tickFeed(steps = 180))
        val got = raw.toObservable().cleaned().candles().toList().blockingGet()
            .sortedWith(compareBy<Candle>({ it.ticker }, { it.windowStart }))
        assertEquals(candlesReference(cleanReference(raw)), got)
    }

    @Test
    fun `crossings match the Reactor version`() {
        val raw = withFlaws(tickFeed(steps = 180))
        raw.toObservable().cleaned().crossings("ACME", Money.of("100.00")).take(3).test()
            .assertValues("2000: sube a 100.09 USD", "40000: baja a 99.98 USD", "48000: sube a 100.21 USD")
    }

    @Test
    fun `await and asFlow bring RxJava into coroutines`() = runTest {
        assertEquals(Money.of("100.00"), quoteNew(LegacyQuoteClient(), "ACME"))
        val raw = withFlaws(tickFeed(steps = 120))
        assertEquals(10, candlesAsFlow(raw.toObservable()).toList().size)
    }

    @Test
    fun `rxSingle offers suspend code to old callers`() {
        quoteForLegacyCallers("ORBX").test().awaitDone(1, TimeUnit.SECONDS).assertValue(Money.of("42.50"))
    }
}
