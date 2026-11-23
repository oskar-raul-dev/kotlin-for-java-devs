package dev.raul.oskar.learning.kotlin.internals

import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/** Lo que devuelve una API de callbacks para poder cancelar el pedido. */
fun interface Cancellable {
    fun cancel()
}

/** Un cliente de cotizaciones del Mercado con la API de callbacks de siempre (como un cliente HTTP asíncrono de Java). */
interface CallbackQuoteClient {
    fun request(ticker: String, onSuccess: (Money) -> Unit, onError: (Throwable) -> Unit): Cancellable
}

/**
 * Callback → `suspend` con `suspendCancellableCoroutine`: la corrutina se suspende, el callback la reanuda con
 * `resume` (o `resumeWithException`), y si la corrutina se cancela mientras espera, `invokeOnCancellation` cancela el
 * pedido en el cliente. Así se envuelve cualquier API asíncrona de Java.
 */
suspend fun CallbackQuoteClient.quote(ticker: String): Money = suspendCancellableCoroutine { continuation ->
    val handle = request(
        ticker,
        onSuccess = { continuation.resume(it) },
        onError = { continuation.resumeWithException(it) },
    )
    continuation.invokeOnCancellation { handle.cancel() }
}

/**
 * La versión con `suspendCoroutine` (de la stdlib, sin kotlinx): funciona, pero **no se entera de la cancelación**: si
 * la corrutina se cancela, el pedido sigue vivo en el cliente y la corrutina espera hasta que el callback llegue.
 */
suspend fun CallbackQuoteClient.quoteIgnoringCancellation(ticker: String): Money = suspendCoroutine { continuation ->
    request(ticker, onSuccess = { continuation.resume(it) }, onError = { continuation.resumeWithException(it) })
}

/** Reanudar dos veces la misma `Continuation` lanza `IllegalStateException`: un callback que llega dos veces rompe. */
suspend fun resumeTwice(): Int = suspendCoroutine { continuation ->
    continuation.resume(1)
    continuation.resume(2)
}
