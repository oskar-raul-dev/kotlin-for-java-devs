package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.OptimisticLockException
import org.hibernate.LazyInitializationException

// ./gradlew run -Plesson=jpa                    -> H2 en memoria
// ./gradlew run -Plesson=jpa --args=postgres    -> PostgreSQL de docker/postgres/compose.yaml

fun main(args: Array<String>) {
    val emf = if ("postgres" in args) postgresFactory() else h2Factory()
    emf.use {
        seed(emf)

        println("== Consultas ==")
        emf.inTransaction { em ->
            val repo = OrderRepository(em)
            println("findCustomerByEmail -> ${repo.findCustomerByEmail("ana@example.com")}")
            println("PAID -> ${repo.ordersByStatus(OrderStatus.PAID)}")
            println("totales -> ${repo.totalsByCustomer()}")
            println("página 0 (2) -> ${repo.customersPage(0, 2)}, página 1 -> ${repo.customersPage(1, 2)}")
        }

        println("\n== N+1 ==")
        emf.resetStatistics()
        emf.inTransaction { em -> OrderRepository(em).allOrdersNaive().forEach { it.customer!!.name } }
        println("sin fetch   -> ${emf.statementCount()} sentencias")
        emf.resetStatistics()
        emf.inTransaction { em -> OrderRepository(em).allOrdersWithCustomer().forEach { it.customer!!.name } }
        println("join fetch  -> ${emf.statementCount()} sentencias")

        println("\n== Dirty checking ==")
        val id = emf.inTransaction { em ->
            val order = OrderRepository(em).ordersByStatus(OrderStatus.NEW).first()
            order.status = OrderStatus.PAID            // sin em.merge ni save: el commit hace el UPDATE
            order.id!!
        }
        emf.inTransaction { em -> println("después del commit -> ${em.find(PurchaseOrder::class.java, id)}") }

        println("\n== Lazy fuera de la transacción ==")
        val detached = emf.inTransaction { em -> em.find(Customer::class.java, 1L) }
        try {
            detached.orders.size
        } catch (e: LazyInitializationException) {
            println("LazyInitializationException: ${e.message}")
        }

        println("\n== Bloqueo optimista ==")
        val stale = emf.inTransaction { em -> em.find(PurchaseOrder::class.java, id) }
        emf.inTransaction { em -> em.find(PurchaseOrder::class.java, id).status = OrderStatus.SHIPPED }
        try {
            emf.inTransaction { em ->
                stale.status = OrderStatus.CANCELLED
                em.merge(stale)                          // version vieja -> falla al hacer flush
            }
        } catch (e: OptimisticLockException) {
            println("OptimisticLockException: alguien modificó el pedido $id antes")
        }

        println("\n== UPDATE masivo ==")
        emf.inTransaction { em ->
            // Cliente gestionado + cascade: el pedido nuevo se inserta en el commit sin persist explícito.
            OrderRepository(em).findCustomerByEmail("eva@example.com")!!.placeOrder(PurchaseOrder())
        }
        emf.inTransaction { em -> println("cancelados -> ${OrderRepository(em).cancelAllNew()}") }
    }
}
