package dev.raul.oskar.learning.kotlin.hotflows

import dev.raul.oskar.learning.kotlin.cases.Money
import dev.raul.oskar.learning.kotlin.cases.TICKERS
import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** Alerta del tablero: el precio de un ticker se movió más que el umbral desde la última alerta. */
data class MoveAlert(val ticker: String, val from: Money, val to: Money)

/**
 * Tablero de precios del Mercado con flujos **calientes**: existen y tienen estado aunque nadie los recolecte.
 *
 * - El último precio de cada ticker es un `StateFlow` (siempre tiene valor; quien llega tarde recibe el actual, como
 *   un `BehaviorSubject`). Se expone de solo lectura con `asStateFlow()`.
 * - Las alertas son un `SharedFlow` sin `replay` (eventos: quien llega tarde no ve las pasadas, como un
 *   `PublishSubject`), con un buffer extra para que un suscriptor lento no frene al feed de inmediato.
 */
class PriceBoard(tickers: List<String> = TICKERS, private val alertMove: Money = Money.of("1.00")) {
    private val prices = tickers.associateWith { MutableStateFlow<Money?>(null) }
    private val lastAlerted = mutableMapOf<String, Money>()
    private val alertsFlow = MutableSharedFlow<MoveAlert>(extraBufferCapacity = 64)

    val alerts: SharedFlow<MoveAlert> = alertsFlow.asSharedFlow()

    fun price(ticker: String): StateFlow<Money?> = prices.getValue(ticker).asStateFlow()

    suspend fun onTick(tick: Tick) {
        prices.getValue(tick.ticker).value = tick.price
        val reference = lastAlerted.getOrPut(tick.ticker) { tick.price }
        val move = (tick.price - reference).amount.abs()
        if (move >= alertMove.amount) {
            lastAlerted[tick.ticker] = tick.price
            alertsFlow.emit(MoveAlert(tick.ticker, reference, tick.price))
        }
    }
}
