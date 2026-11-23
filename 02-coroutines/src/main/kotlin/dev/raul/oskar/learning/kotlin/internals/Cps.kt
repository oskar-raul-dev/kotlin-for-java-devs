package dev.raul.oskar.learning.kotlin.internals

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

/**
 * *Continuation-passing style*: para la JVM, `suspend fun f(): T` es `f(Continuation<T>): Any?`. El compilador agrega
 * el parámetro, y el valor de retorno es el resultado **o** la marca `COROUTINE_SUSPENDED` ("me suspendí; te aviso por
 * la continuación"). Desde Kotlin se puede ver con un cast de la lambda `suspend` a función de un parámetro.
 */
@Suppress("UNCHECKED_CAST")
fun <T> callWithContinuation(block: suspend () -> T, continuation: Continuation<T>): Any? =
    (block as (Continuation<T>) -> Any?).invoke(continuation)

/** Una función `suspend` que se suspende de verdad y guarda su continuación para que alguien la reanude después. */
class Parking<T> {
    var parked: Continuation<T>? = null
        private set

    suspend fun park(): T = suspendCoroutineUninterceptedOrReturn { continuation ->
        parked = continuation
        COROUTINE_SUSPENDED
    }
}

/** Una `Continuation` que anota cómo terminó la corrutina: es todo lo que hace falta para arrancar una sin kotlinx. */
class RecordingContinuation<T>(override val context: CoroutineContext = EmptyCoroutineContext) : Continuation<T> {
    var result: Result<T>? = null
        private set

    override fun resumeWith(result: Result<T>) {
        this.result = result
    }
}

/**
 * Arrancar una corrutina con la stdlib sola (`startCoroutine`), sin `kotlinx.coroutines`: no hay dispatcher, ni
 * `Job`, ni scope. La corrutina corre en el hilo que la arranca hasta su primer punto de suspensión, y sigue en el hilo
 * de quien la reanude. Sobre esto construye kotlinx todo lo demás.
 */
fun <T> startBare(block: suspend () -> T): RecordingContinuation<T> =
    RecordingContinuation<T>().also { block.startCoroutine(it) }

/**
 * Las variables locales que siguen vivas después de un punto de suspensión pasan a ser **campos** del objeto de la
 * máquina de estados; el estado actual va en el campo `label`. Devuelve los nombres de los campos de la lambda
 * `suspend` (que es su propia máquina de estados).
 */
fun stateMachineFields(block: suspend () -> Unit): List<String> = block.javaClass.declaredFields.map { it.name }.sorted()
