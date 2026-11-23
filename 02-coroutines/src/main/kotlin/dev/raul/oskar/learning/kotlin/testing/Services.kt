package dev.raul.oskar.learning.kotlin.testing

import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Consulta la tasa USD→EUR cada [every] y publica la última en un `StateFlow`. Recibe el scope **y** el dispatcher: en
 * producción, `Dispatchers.IO`; en la prueba, un `StandardTestDispatcher` que comparte el reloj virtual de `runTest`.
 * Gracias a eso, una hora de sondeo se prueba en milisegundos.
 */
class RateMonitor(
    private val scope: CoroutineScope,
    private val fx: FxRateClient,
    private val every: Duration = 1.minutes,
    private val io: CoroutineDispatcher = Dispatchers.IO,
) {
    private val latestRate = MutableStateFlow<BigDecimal?>(null)
    val latest: StateFlow<BigDecimal?> = latestRate.asStateFlow()
    var polls = 0
        private set
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch(io) {
            while (isActive) {
                latestRate.value = fx.rate(Money.USD, Money.EUR)
                polls++
                delay(every)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}

/**
 * La versión que no se puede probar en tiempo virtual: el `withContext(Dispatchers.IO)` fijo saca el `delay` del reloj
 * de la prueba y espera **de verdad**.
 */
class HardcodedIoQuoter(private val latency: Duration) {
    suspend fun quote(): Money = withContext(Dispatchers.IO) {
        delay(latency)
        Money.of("100.00")
    }
}

class TransferTimedOut(limit: Duration, cause: Throwable) : RuntimeException("la transferencia no terminó en $limit", cause)

/** El tiempo límite de una transferencia del Banco (30 s), con su error de dominio (como en `cancellation`). */
suspend fun <T> withTransferDeadline(limit: Duration = 30.seconds, block: suspend () -> T): T =
    try {
        withTimeout(limit) { block() }
    } catch (e: TimeoutCancellationException) {
        throw TransferTimedOut(limit, e)
    }

/**
 * En qué orden corren dos corrutinas lanzadas con [dispatcher] respecto de quien las lanza: con
 * `StandardTestDispatcher`, se encolan y corren cuando la prueba cede; con `UnconfinedTestDispatcher`, arrancan en el
 * acto, dentro del `launch`.
 */
fun CoroutineScope.launchOrder(dispatcher: CoroutineDispatcher, log: MutableList<String>) {
    log += "antes"
    launch(dispatcher) { log += "hijo 1" }
    launch(dispatcher) { log += "hijo 2" }
    log += "después"
}
