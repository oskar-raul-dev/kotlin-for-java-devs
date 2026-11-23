package dev.raul.oskar.learning.kotlin.rxjava

import dev.raul.oskar.learning.kotlin.cases.Candle
import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.Tick
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.rx3.rxSingle
import java.util.concurrent.TimeUnit

/** El cliente de cotizaciones viejo, en RxJava. No se toca: es la frontera. */
class LegacyQuoteClient {
    fun quote(ticker: String): Single<Money> = Single.just(Money.of("100.00")).delay(50, TimeUnit.MILLISECONDS)
}

/**
 * Migración incremental, paso 1: el código nuevo es `suspend` y consume la API vieja con `await()`
 * (`kotlinx-coroutines-rx3`). La suscripción se cancela si la corrutina se cancela.
 */
suspend fun quoteNew(client: LegacyQuoteClient, ticker: String): Money = client.quote(ticker).await()

/** Paso 2: los flujos viejos entran al código nuevo como `Flow` con `asFlow()`. */
fun candlesAsFlow(ticks: Observable<Tick>): Flow<Candle> = ticks.cleaned().candles().asFlow()

/**
 * Paso 3: lo que se reescribió en corrutinas se sigue ofreciendo a los llamadores viejos como `Single` con
 * `rxSingle { }`, hasta que todos migren.
 */
fun quoteForLegacyCallers(ticker: String): Single<Money> = rxSingle {
    delay(50)
    Money.of(if (ticker == "ACME") "100.00" else "42.50")
}
