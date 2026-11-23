package dev.raul.oskar.learning.kotlin.bridges

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import dev.raul.oskar.learning.kotlin.cases.tickFeed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.coroutineContext

/** Un cliente de precios del Mercado con API de Reactor (como uno hecho con `WebClient`). */
class ReactivePriceClient(private val prices: Map<String, Money>) {
    fun price(ticker: String): Mono<Money> = Mono.justOrEmpty(prices[ticker]).delayElement(Duration.ofMillis(10))
    fun feed(steps: Int): Flux<Tick> = Flux.fromIterable(tickFeed(steps = steps))
}

/**
 * `awaitSingle()` suspende hasta que el `Mono` emite (como `block()`, pero sin bloquear el hilo). Sobre un `Mono` vacío
 * lanza `NoSuchElementException`; para "puede no haber" está `awaitSingleOrNull()`, que traduce el vacío a `null`.
 */
suspend fun ReactivePriceClient.priceOf(ticker: String): Money = price(ticker).awaitSingle()

suspend fun ReactivePriceClient.priceOrNull(ticker: String): Money? = price(ticker).awaitSingleOrNull()

/** Un `Flux` como `Flow`: la demanda de Reactor (`request(n)`) se traduce a la suspensión del colector. */
fun ReactivePriceClient.feedAsFlow(steps: Int): Flow<Tick> = feed(steps).asFlow()

/**
 * Lo escrito con corrutinas, ofrecido como Reactor: `mono { }` corre una corrutina por suscripción; `asFlux()` convierte
 * un `Flow`. Es lo que hace Spring WebFlux por detrás con un controlador `suspend` (`09-spring`).
 */
fun quoteMono(ticker: String): Mono<Money> = mono {
    delay(10)
    Money.of(if (ticker == "ACME") "100.00" else "42.50")
}

fun Flow<Tick>.toFlux(): Flux<Tick> = asFlux()

/**
 * El contexto de Reactor cruza el puente: dentro de `mono { }`, lo que el suscriptor puso con `contextWrite` está en
 * `coroutineContext[ReactorContext]`. Así llega, por ejemplo, el contexto de seguridad de Spring a una función `suspend`.
 */
fun correlationIdSeenByCoroutine(): Mono<String> = mono {
    coroutineContext[ReactorContext]?.context?.getOrDefault("correlationId", "sin id") ?: "sin contexto"
}

/** Y al revés: una corrutina pasa contexto a una cadena de Reactor con `Context.asCoroutineContext()`. */
suspend fun correlationIdSeenByReactor(id: String): String =
    withContext(Context.of("correlationId", id).asCoroutineContext()) {
        Mono.deferContextual { ctx -> Mono.just(ctx.getOrDefault("correlationId", "sin id")!!) }.awaitSingle()
    }

/** Un feed infinito de Reactor que anota si alguien canceló su suscripción. */
fun endlessReactorFeed(cancelled: AtomicBoolean): Flux<Long> =
    Flux.interval(Duration.ofMillis(5)).doOnCancel { cancelled.set(true) }

/** Un feed infinito escrito con corrutinas (`flux { }`) que anota si su corrutina terminó por cancelación. */
fun endlessCoroutineFeed(stopped: AtomicBoolean): Flux<Int> = flux {
    try {
        var i = 0
        while (true) {
            send(i++)
            delay(5)
        }
    } finally {
        stopped.set(true)
    }
}

/**
 * Un `Flow` que cuenta cuántos elementos llegó a producir, convertido a `Flux`: el productor solo avanza cuando
 * Reactor pide (`request(n)`); la backpressure cruza el puente.
 */
fun countingFlowAsFlux(produced: AtomicInteger): Flux<Int> = flow {
    repeat(1_000) {
        produced.incrementAndGet()
        emit(it)
    }
}.asFlux()
