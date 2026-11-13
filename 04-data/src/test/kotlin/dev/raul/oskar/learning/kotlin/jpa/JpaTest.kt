package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.OptimisticLockException
import jakarta.persistence.PersistenceException
import org.hibernate.LazyInitializationException
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JpaTest {

    private lateinit var emf: EntityManagerFactory

    @BeforeTest
    fun setUp() {
        emf = h2Factory("test-${UUID.randomUUID()}")     // base nueva por prueba
        seed(emf)
    }

    @AfterTest
    fun tearDown() = emf.close()

    @Test
    fun `cascade persists orders and lines`() {
        emf.inTransaction { em ->
            val ana = OrderRepository(em).findCustomerByEmail("ana@example.com")!!
            assertEquals(2, ana.orders.size)
            assertEquals(BigDecimal("1251.00"), ana.orders.first().total())
            assertNull(OrderRepository(em).findCustomerByEmail("nadie@example.com"))
        }
    }

    @Test
    fun `aggregate projection`() {
        val totals = emf.inTransaction { em -> OrderRepository(em).totalsByCustomer() }
        assertEquals(listOf("Ana", "Bob"), totals.map { it.first })
        assertEquals(0, BigDecimal("1561.00").compareTo(totals[0].second))
    }

    @Test
    fun `pagination`() {
        emf.inTransaction { em ->
            val repo = OrderRepository(em)
            assertEquals(listOf("Ana", "Bob"), repo.customersPage(0, 2).map { it.name })
            assertEquals(listOf("Eva"), repo.customersPage(1, 2).map { it.name })
        }
    }

    @Test
    fun `join fetch avoids N+1`() {
        emf.resetStatistics()
        emf.inTransaction { em -> OrderRepository(em).allOrdersNaive().forEach { it.customer!!.name } }
        val naive = emf.statementCount()

        emf.resetStatistics()
        emf.inTransaction { em -> OrderRepository(em).allOrdersWithCustomer().forEach { it.customer!!.name } }
        val fetched = emf.statementCount()

        assertEquals(1, fetched)
        assertTrue(naive > fetched, "naive=$naive fetched=$fetched")
    }

    @Test
    fun `dirty checking writes on commit`() {
        val id = emf.inTransaction { em ->
            OrderRepository(em).ordersByStatus(OrderStatus.NEW).single().also { it.status = OrderStatus.PAID }.id!!
        }
        val reloaded = emf.inTransaction { em -> em.find(PurchaseOrder::class.java, id) }
        assertEquals(OrderStatus.PAID, reloaded.status)
        assertEquals(1L, reloaded.version)
    }

    @Test
    fun `lazy collection outside transaction throws`() {
        val ana = emf.inTransaction { em -> OrderRepository(em).findCustomerByEmail("ana@example.com")!! }
        assertFailsWith<LazyInitializationException> { ana.orders.size }
    }

    @Test
    fun `optimistic locking detects concurrent update`() {
        val id = emf.inTransaction { em -> OrderRepository(em).ordersByStatus(OrderStatus.PAID).single().id!! }
        val stale = emf.inTransaction { em -> em.find(PurchaseOrder::class.java, id) }
        emf.inTransaction { em -> em.find(PurchaseOrder::class.java, id).status = OrderStatus.SHIPPED }

        stale.status = OrderStatus.CANCELLED
        assertFailsWith<OptimisticLockException> { emf.inTransaction { em -> em.merge(stale) } }
    }

    @Test
    fun `orphan removal deletes lines`() {
        val orderId = emf.inTransaction { em ->
            val order = OrderRepository(em).ordersByStatus(OrderStatus.NEW).single()
            order.lines.removeIf { it.product == "mouse" }
            order.id!!
        }
        emf.inTransaction { em ->
            val order = em.find(PurchaseOrder::class.java, orderId)
            assertEquals(listOf("laptop"), order.lines.map { it.product })
        }
    }

    @Test
    fun `unique constraint rolls back`() {
        assertFailsWith<PersistenceException> {
            emf.inTransaction { em -> em.persist(Customer("Otra Ana", "ana@example.com")) }
        }
        // count() devuelve java.lang.Long. Long::class.java sería el primitivo long; javaObjectType da la clase en caja.
        // Antes se escribía java.lang.Long::class.java, que en K2 avisa "not recommended for use in Kotlin".
        emf.inTransaction { em -> assertEquals(3, em.createQuery("select count(c) from Customer c", Long::class.javaObjectType).singleResult.toInt()) }
    }

    @Test
    fun `bulk update bypasses the persistence context`() {
        emf.inTransaction { em ->
            val order = OrderRepository(em).ordersByStatus(OrderStatus.NEW).single()
            assertEquals(1, OrderRepository(em).cancelAllNew())
            assertEquals(OrderStatus.NEW, order.status)           // la entidad cargada no se enteró
            em.refresh(order)
            assertEquals(OrderStatus.CANCELLED, order.status)
        }
    }

    @Test
    fun `entities use id based equality`() {
        val a = emf.inTransaction { em -> OrderRepository(em).findCustomerByEmail("bob@example.com") }
        val b = emf.inTransaction { em -> OrderRepository(em).findCustomerByEmail("bob@example.com") }
        assertNotNull(a)
        assertEquals(a, b)                                          // instancias distintas, mismo id
        assertTrue(Customer("x", "y") != Customer("x", "y"))       // transitorias: nunca iguales
    }
}
