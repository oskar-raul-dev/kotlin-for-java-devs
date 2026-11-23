package dev.raul.oskar.learning.kotlin.sync

import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.math.BigDecimal
import java.util.Currency
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Un `Semaphore` limita cuántas corrutinas entran a la vez, como el de Java pero suspendiendo: el servicio de tasas
 * acepta [permits] llamadas simultáneas. [peak] guarda el máximo observado.
 */
class LimitedFxClient(private val delegate: FxRateClient, permits: Int) : FxRateClient {
    private val semaphore = Semaphore(permits)
    private val inFlight = AtomicInteger()
    private val maxInFlight = AtomicInteger()
    val peak: Int get() = maxInFlight.get()

    override suspend fun rate(from: Currency, to: Currency): BigDecimal = semaphore.withPermit {
        maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), ::maxOf)
        try {
            delegate.rate(from, to)
        } finally {
            inFlight.decrementAndGet()
        }
    }
}

/**
 * `getOrPut` de Kotlin sobre un `ConcurrentHashMap` no pisa un valor existente, pero puede **calcularlo dos veces**: lee,
 * no encuentra, calcula y recién ahí hace `putIfAbsent`. `computeIfAbsent` de Java lo calcula una sola vez (con la
 * entrada bloqueada). Si la lambda cobra una comisión o abre una conexión, ese efecto se repite.
 */
class FeeCache {
    val map = ConcurrentHashMap<String, BigDecimal>()
    val computations = AtomicInteger()

    fun feeWithGetOrPut(account: String, compute: () -> BigDecimal): BigDecimal =
        map.getOrPut(account) { computations.incrementAndGet(); compute() }

    fun feeWithComputeIfAbsent(account: String, compute: () -> BigDecimal): BigDecimal =
        map.computeIfAbsent(account) { computations.incrementAndGet(); compute() }
}
