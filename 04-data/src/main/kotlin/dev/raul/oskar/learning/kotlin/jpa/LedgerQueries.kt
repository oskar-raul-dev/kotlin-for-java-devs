package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManager
import java.math.BigDecimal

/** Consultas JPQL típicas sobre el ledger. JPQL habla de entidades y propiedades, no de tablas y columnas. */
class LedgerQueries(private val em: EntityManager) {

    fun findAccount(number: String): Account? =
        em.createQuery("select a from Account a where a.number = :n", Account::class.java)
            .setParameter("n", number)
            .resultList
            .firstOrNull()                       // getSingleResult lanzaría NoResultException

    /** Sin fetch: recorrer e.postings dispara una consulta por asiento (N+1). */
    fun entriesNaive(): List<JournalEntry> =
        em.createQuery("select e from JournalEntry e order by e.id", JournalEntry::class.java).resultList

    /** join fetch: asientos y líneas en una sola consulta. */
    fun entriesWithPostings(): List<JournalEntry> =
        em.createQuery("select distinct e from JournalEntry e join fetch e.postings order by e.id", JournalEntry::class.java)
            .resultList

    fun accountsByStatus(status: AccountStatus): List<Account> =
        em.createQuery("select a from Account a where a.status = :s order by a.number", Account::class.java)
            .setParameter("s", status)
            .resultList

    /**
     * Proyección con agregados: Array<Any> por fila (Object[] en Java). Suma débitos y créditos por cuenta y devuelve
     * el movimiento neto (débitos − créditos) de cada una.
     */
    fun netMovementByAccount(): Map<String, BigDecimal> =
        em.createQuery(
            """
            select a.number, p.direction, sum(p.amount)
            from Posting p join p.account a
            group by a.number, p.direction
            order by a.number
            """.trimIndent(),
            Array<Any>::class.java,
        ).resultList
            .groupBy({ it[0] as String }, { row -> (row[2] as BigDecimal).let { if (row[1] == Direction.DEBIT) it else it.negate() } })
            .mapValues { (_, amounts) -> amounts.fold(BigDecimal.ZERO, BigDecimal::add) }

    /** Paginación: setFirstResult/setMaxResults (OFFSET/LIMIT). */
    fun accountsPage(page: Int, size: Int): List<Account> =
        em.createQuery("select a from Account a order by a.number", Account::class.java)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList

    /** UPDATE masivo: va directo a la base y NO actualiza las entidades ya cargadas. */
    fun freezeEmptyAccounts(): Int =
        em.createQuery("update Account a set a.status = :frozen where a.balance = 0 and a.status = :open")
            .setParameter("frozen", AccountStatus.FROZEN)
            .setParameter("open", AccountStatus.OPEN)
            .executeUpdate()
}
