package dev.raul.oskar.learning.kotlin.jdbc

import java.math.BigDecimal
import java.sql.Connection
import java.sql.SQLException
import java.time.LocalDate

/** El mismo recorrido para los tres motores: cada Main solo cambia la conexión y el dialecto. */
fun runDemo(conn: Connection, dialect: Dialect) {
    val meta = conn.metaData
    println("== ${dialect.name}: ${meta.databaseProductName} ${meta.databaseProductVersion} (driver ${meta.driverVersion}) ==")

    val repo = LedgerRepository(conn, dialect)
    repo.recreateSchema()
    val ana = "ACC-00001234-82"
    val bea = "ACC-00005678-39"
    val day = LocalDate.of(2026, 10, 7)

    repo.openAccounts(listOf(AccountRow(ana, "USD", money("1000.00")), AccountRow(bea, "USD", money("250.00"))))
    println("openAccounts (batch) -> ${repo.accounts()}")

    val entry = repo.transfer("k-1", ana, bea, money("100.00"), "Transferencia a $bea", day)
    println("transfer -> asiento $entry; saldos ${repo.accounts().map { "${it.id}=${it.balance}" }}")
    println("movimientos de $bea -> ${repo.movements(bea)}")
    println("asientos desbalanceados -> ${repo.unbalancedEntries()}  (débitos = créditos)")

    try {
        repo.transfer("k-1", ana, bea, money("1.00"), "Repetida", day)
    } catch (e: SQLException) {
        println("clave de idempotencia repetida -> ${e::class.simpleName}, nada cambió: ${repo.findAccount(ana)?.balance}")
    }
    try {
        repo.transfer("k-2", bea, ana, money("5000.00"), "Sin fondos", day)
    } catch (e: SQLException) {
        println("sin fondos -> el CHECK revierte todo (${e::class.simpleName}): $bea=${repo.findAccount(bea)?.balance}")
    }

    repo.upsertRate("EUR", BigDecimal("0.9215"))
    repo.upsertRate("EUR", BigDecimal("0.9208"))
    println("upsert de tasa (1 insert + 1 update) -> EUR=${repo.rate("EUR")}, filas=${repo.rateCount()}")
    println("entriesMatching(\"transfer\") -> ${repo.entriesMatching("transfer")}")
    println("saldo total USD -> ${repo.totalBalance("USD")}")
    println("freeze $bea -> ${repo.freeze(bea)}; borrar $bea con movimientos -> " +
        runCatching { repo.deleteAccount(bea) }.fold({ "$it" }, { "falla por la foreign key (${it::class.simpleName})" }))
}
