package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManager
import java.math.BigDecimal

/** Consultas JPQL típicas. JPQL habla de entidades y propiedades, no de tablas y columnas. */
class OrderRepository(private val em: EntityManager) {

    fun findCustomerByEmail(email: String): Customer? =
        em.createQuery("select c from Customer c where c.email = :email", Customer::class.java)
            .setParameter("email", email)
            .resultList
            .firstOrNull()                       // getSingleResult lanzaría NoResultException

    /** Sin fetch: acceder a o.customer dispara una consulta por pedido (N+1). */
    fun allOrdersNaive(): List<PurchaseOrder> =
        em.createQuery("select o from PurchaseOrder o order by o.id", PurchaseOrder::class.java).resultList

    /** join fetch: pedidos y clientes en una sola consulta. */
    fun allOrdersWithCustomer(): List<PurchaseOrder> =
        em.createQuery("select o from PurchaseOrder o join fetch o.customer order by o.id", PurchaseOrder::class.java)
            .resultList

    fun ordersByStatus(status: OrderStatus): List<PurchaseOrder> =
        em.createQuery("select o from PurchaseOrder o where o.status = :s order by o.id", PurchaseOrder::class.java)
            .setParameter("s", status)
            .resultList

    /** Proyección con agregados: Array<Any> por fila (Object[] en Java), mapeado a un par. */
    fun totalsByCustomer(): List<Pair<String, BigDecimal>> =
        em.createQuery(
            """
            select c.name, sum(l.unitPrice * l.quantity)
            from OrderLine l join l.order o join o.customer c
            group by c.name
            order by c.name
            """.trimIndent(),
            Array<Any>::class.java,
        ).resultList.map { row -> row[0] as String to (row[1] as BigDecimal) }

    /** Paginación: setFirstResult/setMaxResults (OFFSET/LIMIT). */
    fun customersPage(page: Int, size: Int): List<Customer> =
        em.createQuery("select c from Customer c order by c.name", Customer::class.java)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList

    /** UPDATE masivo: va directo a la base y NO actualiza las entidades ya cargadas. */
    fun cancelAllNew(): Int =
        em.createQuery("update PurchaseOrder o set o.status = :c where o.status = :n")
            .setParameter("c", OrderStatus.CANCELLED)
            .setParameter("n", OrderStatus.NEW)
            .executeUpdate()
}
