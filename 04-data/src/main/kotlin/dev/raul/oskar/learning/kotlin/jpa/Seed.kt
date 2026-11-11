package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManagerFactory
import java.math.BigDecimal

/** Tres clientes con pedidos. persist del cliente basta: cascade = ALL lleva pedidos y líneas. */
fun seed(emf: EntityManagerFactory) = emf.inTransaction { em ->
    val ana = Customer("Ana", "ana@example.com").apply {
        placeOrder(PurchaseOrder().addLine("laptop", 1, BigDecimal("1200.00")).addLine("mouse", 2, BigDecimal("25.50")))
        placeOrder(PurchaseOrder(status = OrderStatus.PAID).addLine("monitor", 1, BigDecimal("310.00")))
    }
    val bob = Customer("Bob", "bob@example.com").apply {
        placeOrder(PurchaseOrder(status = OrderStatus.SHIPPED).addLine("keyboard", 1, BigDecimal("80.00")))
    }
    val eva = Customer("Eva", "eva@example.com")
    listOf(ana, bob, eva).forEach(em::persist)
}
