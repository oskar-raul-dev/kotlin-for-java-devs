package dev.raul.oskar.learning.kotlin.collections

import java.util.TreeMap

// El libro de órdenes de un ticker del Mercado (docs/Casos.md): TreeMap por precio + ArrayDeque por llegada.
// Solo guarda y consulta; emparejar órdenes (precio-tiempo) es el ⚡ de control y sus ejercicios.

data class RestingOrder(val id: String, val quantity: Long) {
    init {
        require(quantity > 0) { "cantidad debe ser positiva: $quantity" }
    }
}

class OrderBook {
    // Precios en centavos (Long): comparar y usar de clave es exacto, sin escalas de BigDecimal.
    // Compras de mayor a menor (la mejor compra es la más alta), ventas de menor a mayor.
    private val bids = TreeMap<Long, ArrayDeque<RestingOrder>>(Comparator.reverseOrder())
    private val asks = TreeMap<Long, ArrayDeque<RestingOrder>>()

    fun addBid(priceCents: Long, order: RestingOrder) = add(bids, priceCents, order)
    fun addAsk(priceCents: Long, order: RestingOrder) = add(asks, priceCents, order)

    /** getOrPut (computeIfAbsent de Java) + addLast: FIFO dentro de cada precio. */
    private fun add(side: TreeMap<Long, ArrayDeque<RestingOrder>>, priceCents: Long, order: RestingOrder) {
        require(priceCents > 0) { "precio debe ser positivo: $priceCents" }
        side.getOrPut(priceCents) { ArrayDeque() }.addLast(order)
    }

    /** firstEntry() devuelve null si está vacío; firstKey() lanzaría NoSuchElementException. */
    fun bestBid(): Long? = bids.firstEntry()?.key
    fun bestAsk(): Long? = asks.firstEntry()?.key

    /** La orden que se ejecutaría primero en un precio: la que llegó antes. */
    fun firstAt(priceCents: Long): RestingOrder? = (bids[priceCents] ?: asks[priceCents])?.firstOrNull()

    /** Cruzado = la mejor compra paga lo mismo o más que la mejor venta: un motor correcto nunca lo deja así. */
    fun isCrossed(): Boolean {
        val bid = bestBid() ?: return false
        val ask = bestAsk() ?: return false
        return bid >= ask
    }

    /** Profundidad: cantidad total por nivel, los primeros [levels] de cada lado, en orden de prioridad. */
    fun depth(levels: Int): Pair<List<Pair<Long, Long>>, List<Pair<Long, Long>>> {
        fun side(book: TreeMap<Long, ArrayDeque<RestingOrder>>) =
            book.entries.take(levels).map { (price, queue) -> price to queue.sumOf { it.quantity } }
        return side(bids) to side(asks)
    }

    /** removeIf sobre la cola y, si quedó vacía, se borra el nivel. */
    fun cancel(id: String): Boolean {
        for (book in listOf(bids, asks)) {
            val level = book.entries.firstOrNull { (_, queue) -> queue.any { it.id == id } } ?: continue
            level.value.removeIf { it.id == id }
            if (level.value.isEmpty()) book.remove(level.key)
            return true
        }
        return false
    }
}
