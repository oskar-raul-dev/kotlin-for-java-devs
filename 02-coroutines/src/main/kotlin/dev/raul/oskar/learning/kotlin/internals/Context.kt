package dev.raul.oskar.learning.kotlin.internals

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Un elemento propio del `CoroutineContext`: el id de correlación de la consulta. El contexto es un mapa inmutable
 * indexado por `Key` que viaja con la `Continuation` (no con el hilo), así que lo leen todas las funciones `suspend`
 * que corren en la corrutina, como un `ThreadLocal` que no se pierde al cambiar de hilo.
 */
data class QuoteRequestId(val value: String) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<QuoteRequestId>
}

/** Lee el id del contexto de la corrutina actual (o `null`). */
suspend fun currentRequestId(): String? = coroutineContext[QuoteRequestId]?.value
