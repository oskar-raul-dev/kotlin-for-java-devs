package dev.raul.oskar.learning.kotlin.fundamentals

import dev.raul.oskar.learning.kotlin.cases.BalanceClient
import dev.raul.oskar.learning.kotlin.cases.FxRateClient
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.REFERENCE_LATENCIES
import dev.raul.oskar.learning.kotlin.cases.REFERENCE_PRICES
import dev.raul.oskar.learning.kotlin.cases.sum
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.util.Currency
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

/**
 * Una fuente de precios del Mercado. `suspend` quiere decir "puede pausarse sin bloquear el hilo", no "corre en otro
 * hilo": quien llama decide dónde corre (eso es `dispatchers`).
 */
fun interface PriceSource {
    suspend fun price(ticker: String): Money
}

class QuoteUnavailable(val ticker: String) : RuntimeException("sin precio para $ticker")

/**
 * Fuente simulada: tarda la latencia fija de cada ticker con `delay` (que suspende, no bloquea) y devuelve el precio
 * de referencia. Anota qué consultas empezaron y cuáles terminaron, para que las pruebas vean la cancelación: una
 * consulta cancelada aparece en [started] y no en [finished].
 */
class SimulatedPriceSource(
    private val latencies: Map<String, Duration> = REFERENCE_LATENCIES,
    private val prices: Map<String, Money> = REFERENCE_PRICES,
    private val failing: Set<String> = emptySet(),
) : PriceSource {
    val started = ConcurrentLinkedQueue<String>()
    val finished = ConcurrentLinkedQueue<String>()

    override suspend fun price(ticker: String): Money {
        started += ticker
        delay(latencies.getValue(ticker))
        if (ticker in failing) throw QuoteUnavailable(ticker)
        finished += ticker
        return prices.getValue(ticker)
    }
}

/** Una tras otra: el tiempo total es la **suma** de las latencias. Así se lee el código síncrono de siempre. */
suspend fun quoteSequentially(tickers: Collection<String>, source: PriceSource): Map<String, Money> =
    tickers.associateWith { source.price(it) }

/**
 * En paralelo con `async` dentro de `coroutineScope`: el tiempo total es el de la fuente **más lenta**. Si una falla,
 * el scope cancela a las demás y relanza la excepción; nada queda corriendo cuando la función vuelve (structured
 * concurrency). El equivalente Java sería `StructuredTaskScope.ShutdownOnFailure`, en vista previa en el JDK 21.
 */
suspend fun quoteInParallel(tickers: Collection<String>, source: PriceSource): Map<String, Money> = coroutineScope {
    tickers.map { ticker -> async { ticker to source.price(ticker) } }.awaitAll().toMap()
}

/** Valor del portafolio (ticker → cantidad de títulos) con los precios consultados en paralelo. */
suspend fun portfolioValue(holdings: Map<String, Long>, source: PriceSource): Money {
    val prices = quoteInParallel(holdings.keys, source)
    return holdings.map { (ticker, quantity) -> prices.getValue(ticker) * quantity }.sum()
}

/** Lo que el Banco necesita antes de una transferencia multimoneda. */
data class TransferQuote(val balance: Money, val rate: BigDecimal)

/**
 * El Banco pide el saldo y la tasa a la vez: dos `async` y dos `await`. Equivale a dos `CompletableFuture` con
 * `thenCombine`, pero se lee en orden y una falla cancela a la otra llamada.
 */
suspend fun prepareTransfer(
    account: String,
    from: Currency,
    to: Currency,
    balances: BalanceClient,
    fx: FxRateClient,
): TransferQuote = coroutineScope {
    val balance = async { balances.balance(account) }
    val rate = async { fx.rate(from, to) }
    TransferQuote(balance.await(), rate.await())
}
