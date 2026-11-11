package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.hibernate.SessionFactory

// Sin Spring: EntityManagerFactory a mano desde META-INF/persistence.xml.
// La EMF es cara y se crea una vez; el EntityManager es barato, uno por unidad de trabajo.

/**
 * Hibernate registra con JBoss Logging, que por defecto cae en java.util.logging (INFO, muy ruidoso).
 * Se le pide usar SLF4J para que respete src/main/resources/simplelogger.properties (solo WARN).
 * Debe fijarse antes de que se cargue la primera clase de Hibernate.
 */
private fun useSlf4jLogging() {
    System.setProperty("org.jboss.logging.provider", "slf4j")
}

fun h2Factory(name: String = "learning"): EntityManagerFactory {
    useSlf4jLogging()
    // Cada fábrica con su propia base en memoria, para aislar pruebas.
    return Persistence.createEntityManagerFactory(
        "learning",
        mapOf("jakarta.persistence.jdbc.url" to "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"),
    )
}

/** Misma unidad de persistencia contra el PostgreSQL de docker/postgres/compose.yaml. */
fun postgresFactory(): EntityManagerFactory {
    useSlf4jLogging()
    return Persistence.createEntityManagerFactory(
        "learning",
        mapOf(
            "jakarta.persistence.jdbc.url" to (System.getenv("PG_URL") ?: "jdbc:postgresql://localhost:15432/learning"),
            "jakarta.persistence.jdbc.user" to (System.getenv("PG_USER") ?: "learning"),
            "jakarta.persistence.jdbc.password" to (System.getenv("PG_PASSWORD") ?: "learning"),
        ),
    )
}

/** Abre un EntityManager con transacción; commit al terminar, rollback si lanza. */
fun <T> EntityManagerFactory.inTransaction(block: (EntityManager) -> T): T {
    val em = createEntityManager()
    val tx = em.transaction
    try {
        tx.begin()
        val result = block(em)
        tx.commit()
        return result
    } catch (e: Throwable) {
        if (tx.isActive) tx.rollback()
        throw e
    } finally {
        em.close()
    }
}

/** Cuántas sentencias SQL preparó Hibernate (para ver el problema N+1). */
fun EntityManagerFactory.statementCount(): Long = unwrap(SessionFactory::class.java).statistics.prepareStatementCount

fun EntityManagerFactory.resetStatistics() = unwrap(SessionFactory::class.java).statistics.clear()
