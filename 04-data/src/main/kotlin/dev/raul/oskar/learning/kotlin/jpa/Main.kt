package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.OptimisticLockException
import org.hibernate.LazyInitializationException
import java.math.BigDecimal

// ./gradlew run -Plesson=jpa                    -> H2 en memoria
// ./gradlew run -Plesson=jpa --args=postgres    -> PostgreSQL de docker/postgres/compose.yaml

fun main(args: Array<String>) {
    val emf = if ("postgres" in args) postgresFactory() else h2Factory()
    emf.use {
        seed(emf)

        println("== Consultas ==")
        emf.inTransaction { em ->
            val q = LedgerQueries(em)
            println("findAccount -> ${q.findAccount(ANA)}")
            println("OPEN -> ${q.accountsByStatus(AccountStatus.OPEN)}")
            println("movimiento neto -> ${q.netMovementByAccount()}")
            println("página 0 (2) -> ${q.accountsPage(0, 2)}, página 1 -> ${q.accountsPage(1, 2)}")
        }

        println("\n== N+1 ==")
        emf.resetStatistics()
        emf.inTransaction { em -> LedgerQueries(em).entriesNaive().forEach { it.postings.size } }
        println("sin fetch   -> ${emf.statementCount()} sentencias")
        emf.resetStatistics()
        emf.inTransaction { em -> LedgerQueries(em).entriesWithPostings().forEach { it.postings.size } }
        println("join fetch  -> ${emf.statementCount()} sentencias")

        println("\n== Dirty checking ==")
        emf.inTransaction { em -> LedgerQueries(em).findAccount(BEA)!!.status = AccountStatus.FROZEN }   // sin save: el commit hace el UPDATE
        emf.inTransaction { em -> println("después del commit -> ${LedgerQueries(em).findAccount(BEA)}") }

        println("\n== Lazy fuera de la transacción ==")
        val detached = emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!! }
        try {
            detached.postings.size
        } catch (e: LazyInitializationException) {
            println("LazyInitializationException: ${e.message}")
        }

        println("\n== Bloqueo optimista ==")
        val stale = emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!! }
        emf.inTransaction { em -> LedgerQueries(em).findAccount(ANA)!!.balance += BigDecimal("1.23") }   // intereses
        try {
            emf.inTransaction { em ->
                stale.balance -= BigDecimal("10.00")
                em.merge(stale)                          // version vieja -> falla al hacer flush
            }
        } catch (e: OptimisticLockException) {
            println("OptimisticLockException: alguien modificó la cuenta $ANA antes")
        }

        println("\n== UPDATE masivo ==")
        emf.inTransaction { em -> println("congeladas (saldo 0) -> ${LedgerQueries(em).freezeEmptyAccounts()}") }
    }
}
