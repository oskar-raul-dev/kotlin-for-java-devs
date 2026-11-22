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
    fun `cascade persists entries and postings`() {
        emf.inTransaction { em ->
            val q = LedgerQueries(em)
            val entries = q.entriesWithPostings()
            assertEquals(listOf(2, 2), entries.map { it.postings.size })
            assertTrue(entries.all { it.isBalanced() })
            assertEquals(0, BigDecimal("940.00").compareTo(q.findAccount(ANA)!!.balance))
            assertNull(q.findAccount("ACC-00000000-00"))
        }
    }

    @Test
    fun `aggregate projection`() {
        val net = emf.inTransaction { em -> LedgerQueries(em).netMovementByAccount() }
        assertEquals(setOf(ANA, BEA), net.keys)                          // CYD no tiene movimientos
        assertEquals(0, BigDecimal("-60.00").compareTo(net.getValue(ANA)))  // recibió 40, envió 100
        assertEquals(0, BigDecimal("60.00").compareTo(net.getValue(BEA)))
    }

    @Test
    fun `pagination`() {
        emf.inTransaction { em ->
            val q = LedgerQueries(em)
            assertEquals(listOf(ANA, BEA), q.accountsPage(0, 2).map { it.number })
            assertEquals(listOf(CYD), q.accountsPage(1, 2).map { it.number })
        }
    }

    @Test
    fun `join fetch avoids N+1`() {
        emf.resetStatistics()
        emf.inTransaction { em -> LedgerQueries(em).entriesNaive().forEach { it.postings.size } }
        val naive = emf.statementCount()

        emf.resetStatistics()
        emf.inTransaction { em -> LedgerQueries(em).entriesWithPostings().forEach { it.postings.size } }
        val fetched = emf.statementCount()

        assertEquals(1, fetched)
        assertTrue(naive > fetched, "naive=$naive fetched=$fetched")
    }

    @Test
    fun `dirty checking writes on commit`() {
        val before = emf.inTransaction { em ->
            LedgerQueries(em).findAccount(BEA)!!.also { it.status = AccountStatus.FROZEN }.version!!   // sin save ni merge
        }
        val reloaded = emf.inTransaction { em -> LedgerQueries(em).findAccount(BEA)!! }
        assertEquals(AccountStatus.FROZEN, reloaded.status)
        assertEquals(before + 1, reloaded.version)                      // el commit hizo un UPDATE con la versión nueva
    }

    @Test
    fun `lazy collection outside transaction throws`() {
        val ana = emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!! }
        assertFailsWith<LazyInitializationException> { ana.postings.size }
    }

    @Test
    fun `optimistic locking detects concurrent update`() {
        val stale = emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!! }
        emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!!.balance += BigDecimal("1.23") }

        stale.balance -= BigDecimal("10.00")
        assertFailsWith<OptimisticLockException> { emf.inTransaction { em -> em.merge(stale) } }
    }

    @Test
    fun `orphan removal deletes postings`() {
        // Solo para mostrar orphanRemoval: un asiento real nunca se edita, se corrige con otro asiento.
        val entryId = emf.inTransaction { em ->
            val entry = LedgerQueries(em).entriesWithPostings().first()
            entry.postings.removeIf { it.direction == Direction.CREDIT }
            entry.id!!
        }
        emf.inTransaction { em ->
            val entry = em.find(JournalEntry::class.java, entryId)
            assertEquals(listOf(Direction.DEBIT), entry.postings.map { it.direction })
        }
    }

    @Test
    fun `unique constraint rolls back`() {
        assertFailsWith<PersistenceException> {
            emf.inTransaction { em -> em.persist(Account(ANA, "USD", BigDecimal("1.00"))) }
        }
        // count() devuelve java.lang.Long. Long::class.java sería el primitivo long; javaObjectType da la clase en caja.
        // Antes se escribía java.lang.Long::class.java, que en K2 avisa "not recommended for use in Kotlin".
        emf.inTransaction { em -> assertEquals(3, em.createQuery("select count(a) from Account a", Long::class.javaObjectType).singleResult.toInt()) }
    }

    @Test
    fun `bulk update bypasses the persistence context`() {
        emf.inTransaction { em ->
            val cyd = LedgerQueries(em).findAccount(CYD)!!
            assertEquals(1, LedgerQueries(em).freezeEmptyAccounts())
            assertEquals(AccountStatus.OPEN, cyd.status)                // la entidad cargada no se enteró
            em.refresh(cyd)
            assertEquals(AccountStatus.FROZEN, cyd.status)
        }
    }

    @Test
    fun `entities use id based equality`() {
        val a = emf.inTransaction { em -> LedgerQueries(em).findAccount(BEA) }
        val b = emf.inTransaction { em -> LedgerQueries(em).findAccount(BEA) }
        assertNotNull(a)
        assertEquals(a, b)                                              // instancias distintas, mismo id
        assertTrue(Account("x", "USD", BigDecimal.ONE) != Account("x", "USD", BigDecimal.ONE))   // transitorias: nunca iguales
    }
}
