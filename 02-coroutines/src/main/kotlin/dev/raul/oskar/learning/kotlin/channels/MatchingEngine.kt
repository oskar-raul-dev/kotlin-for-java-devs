package dev.raul.oskar.learning.kotlin.channels

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.util.TreeMap

enum class Side { BUY, SELL }

/** Orden limitada del Mercado. Precios en centavos (como el `OrderBook` de `01-language/collections`). */
data class LimitOrder(val id: String, val side: Side, val priceCents: Long, val quantity: Long) {
    init {
        require(priceCents > 0) { "precio debe ser positivo: $priceCents" }
        require(quantity > 0) { "cantidad debe ser positiva: $quantity" }
    }
}

/** Una ejecución: la orden que llegó (*taker*) contra una que esperaba en el libro (*maker*), al precio del maker. */
data class Execution(val takerId: String, val makerId: String, val priceCents: Long, val quantity: Long)

/** Foto del libro: niveles (precio, cantidad total) de compras (mejor primero) y de ventas (mejor primero). */
data class BookSnapshot(val bids: List<Pair<Long, Long>>, val asks: List<Pair<Long, Long>>) {
    val isCrossed: Boolean
        get() = bids.isNotEmpty() && asks.isNotEmpty() && bids.first().first >= asks.first().first
    val totalQuantity: Long get() = bids.sumOf { it.second } + asks.sumOf { it.second }
}

/**
 * El libro con emparejamiento por precio-tiempo y ejecuciones parciales. **No** es seguro entre hilos a propósito:
 * solo lo toca la corrutina del actor ([MatchingEngine]), así que no necesita locks.
 */
class MatchingBook {
    private class Resting(val id: String, var quantity: Long)

    private val bids = TreeMap<Long, ArrayDeque<Resting>>(Comparator.reverseOrder())
    private val asks = TreeMap<Long, ArrayDeque<Resting>>()

    fun place(order: LimitOrder): List<Execution> {
        val (opposite, own) = if (order.side == Side.BUY) asks to bids else bids to asks
        val crosses: (Long) -> Boolean =
            if (order.side == Side.BUY) { best -> best <= order.priceCents } else { best -> best >= order.priceCents }
        var remaining = order.quantity
        val executions = mutableListOf<Execution>()
        while (remaining > 0) {
            val level = opposite.firstEntry() ?: break
            if (!crosses(level.key)) break
            val queue = level.value
            val maker = queue.first()
            val filled = minOf(remaining, maker.quantity)
            executions += Execution(order.id, maker.id, level.key, filled)
            remaining -= filled
            maker.quantity -= filled
            if (maker.quantity == 0L) queue.removeFirst()
            if (queue.isEmpty()) opposite.remove(level.key)
        }
        if (remaining > 0) own.getOrPut(order.priceCents) { ArrayDeque() }.addLast(Resting(order.id, remaining))
        return executions
    }

    fun cancel(id: String): Boolean {
        for (side in listOf(bids, asks)) {
            val level = side.entries.firstOrNull { (_, q) -> q.any { it.id == id } } ?: continue
            level.value.removeIf { it.id == id }
            if (level.value.isEmpty()) side.remove(level.key)
            return true
        }
        return false
    }

    fun snapshot(): BookSnapshot {
        fun levels(side: TreeMap<Long, ArrayDeque<Resting>>) = side.map { (price, q) -> price to q.sumOf { it.quantity } }
        return BookSnapshot(levels(bids), levels(asks))
    }
}

/** Los mensajes que entiende el actor. La respuesta viaja en un `CompletableDeferred` (el "sobre de respuesta"). */
sealed interface Command {
    data class Place(val order: LimitOrder) : Command
    data class Cancel(val id: String, val reply: CompletableDeferred<Boolean>) : Command
    data class Snapshot(val reply: CompletableDeferred<BookSnapshot>) : Command
}

/**
 * El libro de órdenes como **actor**: un canal de comandos y una sola corrutina que lo consume y es la única dueña del
 * [MatchingBook]. Cien corrutinas pueden enviar órdenes a la vez sin locks, porque el estado nunca se comparte: se le
 * piden cosas por mensaje. Las ejecuciones salen por otro canal (hacia la liquidación del Banco).
 *
 * `actor { }` de kotlinx.coroutines es `@ObsoleteCoroutinesApi` en 1.11.0 (medido): el patrón se escribe a mano, así.
 */
class MatchingEngine(scope: CoroutineScope, capacity: Int = 64) {
    private val commands = Channel<Command>(capacity)
    private val executionsChannel = Channel<Execution>(Channel.UNLIMITED)
    val executions: ReceiveChannel<Execution> get() = executionsChannel
    val inbox: SendChannel<Command> get() = commands

    val job: Job = scope.launch {
        val book = MatchingBook()
        try {
            for (command in commands) {
                when (command) {
                    is Command.Place -> book.place(command.order).forEach { executionsChannel.send(it) }
                    is Command.Cancel -> command.reply.complete(book.cancel(command.id))
                    is Command.Snapshot -> command.reply.complete(book.snapshot())
                }
            }
        } finally {
            executionsChannel.close()
        }
    }

    suspend fun place(order: LimitOrder) = commands.send(Command.Place(order))

    suspend fun cancel(id: String): Boolean =
        CompletableDeferred<Boolean>().also { commands.send(Command.Cancel(id, it)) }.await()

    suspend fun snapshot(): BookSnapshot =
        CompletableDeferred<BookSnapshot>().also { commands.send(Command.Snapshot(it)) }.await()

    /** Cierra la entrada: el actor procesa lo que quedaba y termina, y cierra el canal de ejecuciones. */
    fun close() {
        commands.close()
    }
}
