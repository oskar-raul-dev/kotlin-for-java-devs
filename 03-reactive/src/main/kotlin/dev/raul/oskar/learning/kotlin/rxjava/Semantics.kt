package dev.raul.oskar.learning.kotlin.rxjava

import dev.raul.oskar.learning.kotlin.cases.Money
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Un `subscribe()` sin `onError`: el error no tiene a dónde ir y termina en el manejador global de RxJava envuelto en
 * `OnErrorNotImplementedException` (en Android, eso cierra la app). Devuelve lo que recibió el manejador global.
 */
fun errorWithoutOnError(): Throwable? {
    var seen: Throwable? = null
    RxJavaPlugins.setErrorHandler { seen = it }
    try {
        Observable.error<Int>(IllegalStateException("feed caído")).subscribe { }
    } finally {
        RxJavaPlugins.reset()
    }
    return seen
}

/**
 * Un observador de precios que guarda sus suscripciones en un `CompositeDisposable` (`addTo` de RxKotlin) y usa
 * `subscribeBy` con argumentos con nombre, así no se confunde el callback de error con el de valor. Sin `dispose()`, la
 * suscripción sigue viva y retiene lo que capturó.
 */
class PriceWatcher(private val scheduler: Scheduler) {
    private val disposables = CompositeDisposable()
    val prices = mutableListOf<Money>()
    val errors = mutableListOf<String>()

    fun watch(source: Observable<Money>) {
        source.subscribeOn(scheduler)
            .subscribeBy(onNext = { prices += it }, onError = { errors += it.message.orEmpty() })
            .addTo(disposables)
    }

    fun stop() = disposables.clear()      // clear() deja reusar el contenedor; dispose() lo cierra para siempre

    val active: Int get() = disposables.size()
}

/** Un precio cada segundo según el [scheduler] (un `TestScheduler` en las pruebas: tiempo virtual de RxJava). */
fun pricesEverySecond(scheduler: Scheduler): Observable<Money> =
    Observable.interval(1, TimeUnit.SECONDS, scheduler).map { Money.of((100 + it).toString()) }

/**
 * `subscribeOn` decide dónde corre la **fuente** (no importa dónde se escriba); `observeOn` cambia el hilo de lo que
 * viene **después**. Devuelve (hilo de la fuente, hilo del suscriptor).
 */
fun threadsWith(subscribeOn: Scheduler, observeOn: Scheduler): Pair<String, String> {
    var source = ""
    val subscriber = Observable.fromCallable { source = Thread.currentThread().name; 1 }
        .subscribeOn(subscribeOn)
        .observeOn(observeOn)
        .map { Thread.currentThread().name }
        .blockingFirst()
    return source to subscriber
}

/**
 * RxJava 3 prohíbe `null` y Kotlin lo sabe: `map { null }` no compila (Kotlin reconoce las anotaciones de RxJava 3,
 * medido). El `null` se cuela por un **platform type** de una API Java sin anotar, como `System.getProperty`: compila y
 * revienta en ejecución con `NullPointerException`.
 */
fun nullFromJava(): Throwable? =
    runCatching { Observable.just("fx.url").map { System.getProperty("no-existe.$it") }.blockingFirst() }.exceptionOrNull()

/** El scheduler de E/S de RxJava, el equivalente de `Dispatchers.IO`. */
val io: Scheduler get() = Schedulers.io()
