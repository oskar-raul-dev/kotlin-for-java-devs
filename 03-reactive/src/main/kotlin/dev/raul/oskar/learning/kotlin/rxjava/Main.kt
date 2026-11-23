package dev.raul.oskar.learning.kotlin.rxjava

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import dev.raul.oskar.learning.kotlin.cases.withFlaws
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking

fun main() {
    println("== Error sin onError ==")
    println(errorWithoutOnError())

    println("\n== null desde una API de Java ==")
    println(nullFromJava())

    println("\n== subscribeOn y observeOn ==")
    println(threadsWith(Schedulers.io(), Schedulers.computation()))

    println("\n== El pipeline del feed en RxJava ==")
    val raw = withFlaws(tickFeed(steps = 180))
    println("${raw.toObservable().cleaned().candles().count().blockingGet()} velas")
    raw.toObservable().cleaned().crossings("ACME", Money.of("100.00")).take(3).blockingForEach(::println)

    println("\n== Migración a corrutinas ==")
    runBlocking {
        println("await(): ${quoteNew(LegacyQuoteClient(), "ACME")}")
        println("asFlow(): ${candlesAsFlow(Observable.fromIterable(raw)).count()} velas")
    }
    println("rxSingle para los llamadores viejos: ${quoteForLegacyCallers("ORBX").blockingGet()}")
}
