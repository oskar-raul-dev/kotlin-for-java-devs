package dev.raul.oskar.learning.kotlin.backpressure

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux

/**
 * Qué hacer cuando el productor va más rápido que el consumidor:
 * - `BUFFER`: una cola acotada. En Reactor y RxJava, si se llena, el flujo **falla** (no se pierde nada en silencio);
 *   en `Flow`, el productor **espera**.
 * - `DROP`: descartar lo que llega cuando no hay lugar (los primeros sobreviven).
 * - `LATEST`: quedarse con el último (los intermedios se pierden).
 */
enum class Strategy { BUFFER, DROP, LATEST }

/** Una fuente de Reactor que empuja 1..[total] de golpe, sin mirar la demanda (como un feed de mercado). */
fun eagerReactorSource(total: Int): Flux<Int> =
    Flux.push { sink -> (1..total).forEach { sink.next(it) }; sink.complete() }

/**
 * Reactor: el suscriptor pide con `request(n)`; si la fuente no puede esperar, un `onBackpressure…` decide.
 * `onBackpressureBuffer(n)` falla con `OverflowException` al desbordar.
 */
fun Flux<Int>.withStrategy(strategy: Strategy, bufferSize: Int = 4): Flux<Int> = when (strategy) {
    Strategy.BUFFER -> onBackpressureBuffer(bufferSize)
    Strategy.DROP -> onBackpressureDrop()
    Strategy.LATEST -> onBackpressureLatest()
}

/** La misma fuente en RxJava: un `Flowable.create` con `MISSING` (no hace nada con la demanda). */
fun eagerRxSource(total: Int): Flowable<Int> = Flowable.create({ emitter ->
    (1..total).forEach { emitter.onNext(it) }
    emitter.onComplete()
}, BackpressureStrategy.MISSING)

/**
 * RxJava: solo `Flowable` tiene backpressure, con los mismos tres operadores; `onBackpressureBuffer(n)` falla con
 * `MissingBackpressureException` al desbordar.
 */
fun Flowable<Int>.withStrategy(strategy: Strategy, bufferSize: Int = 4): Flowable<Int> = when (strategy) {
    Strategy.BUFFER -> onBackpressureBuffer(bufferSize)
    Strategy.DROP -> onBackpressureDrop()
    Strategy.LATEST -> onBackpressureLatest()
}

/**
 * `Observable` **no** tiene backpressure: `toFlowable` obliga a elegir estrategia al cruzar a un mundo que sí la tiene.
 * Es el lugar donde una app RxJava declara qué hacer con un productor rápido.
 */
fun Observable<Int>.toFlowableWith(strategy: Strategy): Flowable<Int> = toFlowable(
    when (strategy) {
        Strategy.BUFFER -> BackpressureStrategy.BUFFER        // sin límite: cambia el problema por memoria
        Strategy.DROP -> BackpressureStrategy.DROP
        Strategy.LATEST -> BackpressureStrategy.LATEST
    },
)

/**
 * `Flow`: no hay `request(n)`; el productor espera en `emit`. Las estrategias aparecen al poner un buffer entre los
 * dos: `buffer(n)` (espera cuando se llena: no pierde nada), `buffer(n, DROP_LATEST)` (descarta lo nuevo) o
 * `conflate()` (se queda con el último). [produced] anota los instantes en que el productor emitió; el consumidor tarda
 * [consumerMillis] por elemento. Devuelve lo que recibió el consumidor.
 */
suspend fun flowWithStrategy(
    total: Int,
    strategy: Strategy,
    consumerMillis: Long,
    bufferSize: Int = 4,
    produced: MutableList<Int> = mutableListOf(),
): List<Int> {
    val source: Flow<Int> = (1..total).asFlow().onEach { produced += it }
    val shaped = when (strategy) {
        Strategy.BUFFER -> source.buffer(bufferSize)
        Strategy.DROP -> source.buffer(bufferSize, BufferOverflow.DROP_LATEST)
        Strategy.LATEST -> source.conflate()
    }
    val received = mutableListOf<Int>()
    shaped.collect { delay(consumerMillis); received += it }
    return received
}

/** Lo que vio un consumidor lento: los valores en orden y, si hubo, el error. */
data class Received(val values: List<Int>, val error: Throwable?)

/**
 * Un consumidor lento de Reactor: pide [first] al suscribirse (mientras la fuente empuja todo) y [then] después. Así se
 * ve qué deja pasar cada estrategia.
 */
fun Flux<Int>.consumeSlowly(first: Long, then: Long): Received {
    val values = mutableListOf<Int>()
    var error: Throwable? = null
    val subscriber = object : BaseSubscriber<Int>() {
        override fun hookOnSubscribe(subscription: Subscription) = request(first)
        override fun hookOnNext(value: Int) { values += value }
        override fun hookOnError(throwable: Throwable) { error = throwable }
    }
    subscribe(subscriber)
    subscriber.request(then)
    return Received(values, error)
}

/** Lo mismo para un `Flowable` de RxJava. */
fun Flowable<Int>.consumeSlowly(first: Long, then: Long): Received {
    val values = mutableListOf<Int>()
    var error: Throwable? = null
    val subscriber = object : DisposableSubscriber<Int>() {
        override fun onStart() = request(first)
        override fun onNext(value: Int) { values += value }
        override fun onError(throwable: Throwable) { error = throwable }
        override fun onComplete() {}
        fun more(n: Long) = request(n)
    }
    subscribe(subscriber)
    subscriber.more(then)
    return Received(values, error)
}
