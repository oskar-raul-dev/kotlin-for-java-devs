package dev.raul.oskar.learning.kotlin.cancellation

import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.math.BigDecimal
import java.util.Currency
import kotlin.time.Duration

/** Error de dominio: el servicio de tasas no contestó a tiempo. Es una falla, no una cancelación. */
class FxTimeout(val limit: Duration, cause: Throwable) : RuntimeException("sin tasa en $limit", cause)

/** `withTimeoutOrNull`: `null` si no llega a tiempo, como un `future.get(timeout)` que no lanza. */
suspend fun rateOrNull(fx: FxRateClient, from: Currency, to: Currency, limit: Duration): BigDecimal? =
    withTimeoutOrNull(limit) { fx.rate(from, to) }

/**
 * `withTimeout` lanza `TimeoutCancellationException`, que **es** una `CancellationException`: si nadie la convierte,
 * la corrutina que la recibe termina "cancelada" en silencio en vez de fallar. Aquí se traduce a un error de dominio.
 */
suspend fun rateOrFail(fx: FxRateClient, from: Currency, to: Currency, limit: Duration): BigDecimal =
    try {
        withTimeout(limit) { fx.rate(from, to) }
    } catch (e: TimeoutCancellationException) {
        throw FxTimeout(limit, e)
    }
