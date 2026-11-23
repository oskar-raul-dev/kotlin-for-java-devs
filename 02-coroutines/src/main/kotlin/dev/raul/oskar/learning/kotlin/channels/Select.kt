package dev.raul.oskar.learning.kotlin.channels

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlin.time.Duration

/**
 * `select` espera a la vez varias fuentes y se queda con la primera que esté lista, como un `Selector` de NIO pero
 * para canales. Si las dos están listas, gana la que se escribió primero (sesgado; `selectUnbiased` elige al azar).
 */
suspend fun firstQuote(primary: ReceiveChannel<String>, backup: ReceiveChannel<String>): String = select {
    primary.onReceive { "principal: $it" }
    backup.onReceive { "respaldo: $it" }
}

/**
 * `select` con tiempo límite. `onTimeout` pide `@ExperimentalCoroutinesApi` en 1.11.0 (medido); `withTimeoutOrNull`
 * alrededor de un `receive` hace lo mismo sin opt-in.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun quoteOrTimeout(source: ReceiveChannel<String>, limit: Duration): String = select {
    source.onReceive { it }
    onTimeout(limit) { "sin cotización en $limit" }
}

/**
 * Mezcla dos canales hasta que los dos se cierren, con `onReceiveCatching` (que no lanza al cerrarse). Devuelve los
 * mensajes en el orden en que llegaron.
 */
suspend fun mergeUntilClosed(a: ReceiveChannel<String>, b: ReceiveChannel<String>): List<String> {
    val out = mutableListOf<String>()
    var aOpen = true
    var bOpen = true
    while (aOpen || bOpen) {
        select {
            if (aOpen) a.onReceiveCatching { r -> r.getOrNull()?.let(out::add) ?: run { aOpen = false } }
            if (bOpen) b.onReceiveCatching { r -> r.getOrNull()?.let(out::add) ?: run { bOpen = false } }
        }
    }
    return out
}
