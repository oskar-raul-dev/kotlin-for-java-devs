package dev.raul.oskar.learning.kotlin.exceptions

// Excepciones propias: jerarquía sellada para que quien captura sepa qué puede llegar,
// y siempre conservar la causa (cause) al envolver.

sealed class OrderException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidOrderRequest(val field: String, message: String) : OrderException("$field: $message")

class OrderNotFound(val orderId: String) : OrderException("pedido no encontrado: $orderId")

class InventoryUnavailable(cause: Throwable) : OrderException("servicio de inventario no disponible", cause)

/** Simula un cliente externo que falla con una excepción de bajo nivel. */
fun interface InventoryClient {
    fun available(sku: String): Int
}

class OrderService(private val inventory: InventoryClient, private val orders: Map<String, Long> = emptyMap()) {

    fun totalOf(orderId: String): Long = orders[orderId] ?: throw OrderNotFound(orderId)

    fun canPlace(sku: String, quantity: Int): Boolean {
        if (sku.isBlank()) throw InvalidOrderRequest("sku", "vacío")
        if (quantity <= 0) throw InvalidOrderRequest("quantity", "debe ser positiva")
        val stock = try {
            inventory.available(sku)
        } catch (e: java.io.IOException) {
            // Traducir a una excepción del dominio SIN perder la original: va como cause.
            throw InventoryUnavailable(e)
        }
        return stock >= quantity
    }
}

/** when exhaustivo sobre la jerarquía sellada: sin else, y no compila si falta un caso. */
fun httpStatusFor(e: OrderException): Int = when (e) {
    is InvalidOrderRequest -> 400
    is OrderNotFound -> 404
    is InventoryUnavailable -> 503
}

/** Recorre la cadena de causas (útil para logs y para buscar la raíz). */
fun causeChain(t: Throwable): List<String> =
    generateSequence(t) { it.cause }.map { "${it::class.simpleName}: ${it.message}" }.toList()

fun rootCause(t: Throwable): Throwable = generateSequence(t) { it.cause }.last()
