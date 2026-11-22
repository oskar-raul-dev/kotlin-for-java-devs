package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.EntityManagerFactory
import java.math.BigDecimal
import java.time.LocalDate

const val ANA = "ACC-00001234-82"
const val BEA = "ACC-00005678-39"
const val CYD = "ACC-00009012-28"

/**
 * Tres cuentas y dos transferencias. Se siembra en código (no hay ledger-seed.json): son pocos datos y así cada prueba
 * arranca con lo mismo. persist del asiento basta para sus líneas: cascade = ALL.
 * Saldos al final: ANA 940.00, BEA 310.00, CYD 0.00.
 */
fun seed(emf: EntityManagerFactory) = emf.inTransaction { em ->
    val ana = Account(ANA, "USD", BigDecimal("1000.00"))
    val bea = Account(BEA, "USD", BigDecimal("250.00"))
    val cyd = Account(CYD, "USD", BigDecimal("0.00"))
    listOf(ana, bea, cyd).forEach(em::persist)
    em.persist(transferEntry(ana, bea, BigDecimal("100.00"), "Transferencia a $BEA", LocalDate.of(2026, 10, 7)))
    em.persist(transferEntry(bea, ana, BigDecimal("40.00"), "Reembolso", LocalDate.of(2026, 10, 9)))
}
