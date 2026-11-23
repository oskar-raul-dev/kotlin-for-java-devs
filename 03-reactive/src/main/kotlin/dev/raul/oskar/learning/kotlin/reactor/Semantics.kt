package dev.raul.oskar.learning.kotlin.reactor

import dev.raul.oskar.learning.kotlin.cases.Money
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.atomic.AtomicInteger

/**
 * Reactor es **perezoso**: armar un `Mono` no ejecuta nada; el trabajo corre al suscribirse, y otra vez en cada
 * suscripción (como un `Flow` frío). [calls] cuenta las ejecuciones. El error clásico al portar código imperativo es
 * crear el `Mono` y no suscribirlo (ni devolverlo para que alguien lo haga).
 */
fun lazyQuote(calls: AtomicInteger): Mono<Money> = Mono.fromCallable {
    calls.incrementAndGet()
    Money.of("100.25")
}

/**
 * Reactor prohíbe `null` como elemento (lo dice la especificación de Reactive Streams): `Mono.just(null)` lanza
 * `NullPointerException`. Para "puede no haber", `Mono.justOrEmpty` da un `Mono` **vacío**, que no es lo mismo que
 * `null`. Desde Kotlin, un `Money?` se traduce así.
 */
fun priceOrEmpty(price: Money?): Mono<Money> = Mono.justOrEmpty(price)

/**
 * `block()` en un hilo de Reactor marcado como no bloqueante (los de `Schedulers.parallel()`, o los de Netty en WebFlux)
 * lanza `IllegalStateException`: bloquear ahí frenaría todo el servidor. Devuelve lo que pasó.
 */
fun blockInsideParallel(): Throwable? =
    Mono.fromCallable { runCatching { Mono.just(1).delayElement(java.time.Duration.ofMillis(10)).block() }.exceptionOrNull() }
        .subscribeOn(Schedulers.parallel())
        .block()

/**
 * `flatMap` lanza todas las suscripciones internas a la vez (por defecto, hasta 256). Con el segundo parámetro se
 * limita la concurrencia. [peak] guarda el máximo de suscripciones internas activas.
 */
fun quoteAll(tickers: List<String>, concurrency: Int, peak: AtomicInteger, quote: (String) -> Mono<Money>): Flux<Money> {
    val active = AtomicInteger()
    return Flux.fromIterable(tickers).flatMap({ ticker ->
        quote(ticker)
            .doOnSubscribe { peak.accumulateAndGet(active.incrementAndGet(), ::maxOf) }
            .doOnTerminate { active.decrementAndGet() }   // antes de avisar a flatMap (doFinally llegaría tarde)
            .doOnCancel { active.decrementAndGet() }
    }, concurrency)
}

/** `Mono<Void>` no tiene valores: `then()` descarta el resultado y solo señala que terminó (o falló). */
fun settle(log: MutableList<String>): Mono<Void> = Mono.fromRunnable<Void> { log += "liquidado" }.then()
