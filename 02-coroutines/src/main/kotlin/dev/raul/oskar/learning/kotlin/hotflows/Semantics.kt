package dev.raul.oskar.learning.kotlin.hotflows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `StateFlow` compara con `equals` y solo avisa cuando el valor **cambia**: asignar el mismo valor no emite. Devuelve
 * lo que vio un suscriptor que recolecta todo (en `runTest` con dispatcher sin confinar para no perder nada).
 */
fun sameValueDoesNotEmit(state: MutableStateFlow<Int>) {
    state.value = 1
    state.value = 1          // igual: no se emite
    state.value = 2
}

/**
 * La trampa del objeto mutable: mutar la lista y volver a asignar **la misma** referencia no avisa a nadie (es
 * `equals` consigo misma). Hay que asignar una lista nueva.
 */
fun mutatingInPlaceDoesNotEmit(state: MutableStateFlow<MutableList<String>>) {
    state.value.add("ACME")
    state.value = state.value          // misma referencia: no se emite
}

/**
 * `update { }` hace el ciclo leer-calcular-`compareAndSet` como un `AtomicReference.updateAndGet`: 100 corrutinas en
 * `Default` no pierden incrementos. Con `value = value + 1` sí los perderían.
 */
suspend fun concurrentIncrements(coroutines: Int, each: Int, atomic: Boolean): Int {
    val counter = MutableStateFlow(0)
    withContext(Dispatchers.Default) {
        repeat(coroutines) {
            launch {
                repeat(each) {
                    if (atomic) counter.update { it + 1 } else counter.value = counter.value + 1
                }
            }
        }
    }
    return counter.value
}

/**
 * Un `SharedFlow` con `replay = 0` y sin suscriptores **pierde** lo que se emite: `tryEmit` devuelve `true` y el valor
 * no queda en ningún lado. Devuelve cuántos valores quedaron en el `replayCache` después de emitir [count].
 */
fun emitWithoutSubscribers(replay: Int, count: Int): Pair<List<Boolean>, List<Int>> {
    val events = MutableSharedFlow<Int>(replay = replay)
    val accepted = List(count) { events.tryEmit(it) }
    return accepted to events.replayCache
}

/**
 * Un suscriptor lento con el buffer lleno hace **suspender** a quien emite (backpressure en un flujo caliente), salvo
 * que `onBufferOverflow` diga qué descartar. Devuelve el `SharedFlow` configurado.
 */
fun alertsBus(extraBuffer: Int, overflow: BufferOverflow = BufferOverflow.SUSPEND): MutableSharedFlow<Int> =
    MutableSharedFlow(replay = 0, extraBufferCapacity = extraBuffer, onBufferOverflow = overflow)

/**
 * Convierte un `Flow` frío (el feed, caro de abrir) en un `StateFlow` compartido: la fuente arranca con el primer
 * suscriptor y se detiene [stopTimeoutMillis] después del último (`WhileSubscribed(5000)`, como `autoConnect` con
 * gracia). [starts] y [stops] cuentan cuántas veces se abrió y se cerró la fuente.
 */
class SharedLatest(scope: CoroutineScope, source: Flow<Int>, stopTimeoutMillis: Long = 5_000) {
    var starts = 0
        private set
    var stops = 0
        private set

    val latest: StateFlow<Int?> = source
        .onStart { starts++ }
        .onCompletion { stops++ }
        .stateIn(scope, SharingStarted.WhileSubscribed(stopTimeoutMillis), initialValue = null)
}
