package dev.raul.oskar.learning.kotlin.exceptions

import dev.raul.oskar.learning.kotlin.basics.Money
import java.io.IOException
import java.math.BigDecimal
import java.nio.file.Path

fun main() {
    println("== Básico ==")
    println("readOrNull(no existe) = ${readOrNull(Path.of("no-existe.txt"))}")
    println("portFrom(PORT=9000)   = ${portFrom(mapOf("PORT" to "9000"))}")
    println("classify(portFrom({}))= ${classify { portFrom(emptyMap()) }}")
    println("classify(toInt)       = ${classify { "x".toInt() }}")
    println("classify(1/0)         = ${classify { divide(1, 0) }}")
    val wallet = Wallet(100)
    println("classify(withdraw 500)= ${classify { wallet.withdraw(500) }}")
    wallet.close()
    println("classify(cerrada)     = ${classify { wallet.withdraw(10) }}")
    println("classify(monto -1)    = ${classify { wallet.withdraw(-1) }}")
    println("classify(parseAmount) = ${classify { parseAmount(" diez ") }}")
    val log = mutableListOf<String>()
    println("withFinally(fail)     = ${withFinally(log, fail = true)} log=$log")

    println("\n== Dominio ==")
    val usd = "ACC-00001234-82"
    val eur = "ACC-00005678-39"
    val down = TransferService({ _, _ -> throw IOException("connection refused: fx:8443") },
        mapOf(usd to Money.of("1000"), eur to Money.of("0", Money.EUR)))
    try {
        down.transfer("k-1", usd, eur, Money.of("100"))
    } catch (e: BankException) {
        println("HTTP ${httpStatusFor(e)}")
        causeChain(e).forEach { println("  causa: $it") }
        println("  raíz: ${rootCause(e).message}")
    }
    val service = TransferService({ _, _ -> BigDecimal("0.92") },
        mapOf(usd to Money.of("1000"), eur to Money.of("0", Money.EUR)), frozen = setOf("ACC-00009999-99"))
    println("transfer 100 USD -> EUR: origen queda en ${service.transfer("k-1", usd, eur, Money.of("100"))}, destino ${service.balanceOf(eur)}")
    listOf<() -> Any>(
        { service.transfer("k-1", usd, eur, Money.of("1")) },
        { service.transfer("k-2", usd, eur, Money.of("5000")) },
        { service.transfer("k-3", "ACC-00009999-99", usd, Money.of("1")) },
        { checkBalanced(listOf(Money.of("1001")), listOf(Money.of("1000"))) },
    ).forEach { call ->
        try {
            call()
        } catch (e: BankException) {
            println("HTTP ${httpStatusFor(e)}: ${e.message}")
        }
    }

    println("\n== Result ==")
    listOf("8443", " 80 ", "abc", "70000").forEach { raw ->
        println("'$raw' -> ${portOrExplain(raw)}, default=${portOrDefault(raw)}, recover=${portRecoveringFormat(raw)}, doble=${doubledPort(raw)}")
    }

    println("\n== Validación acumulada ==")
    println(describe(validate("ACC-00001234-82", "ACC-00005678-39", "250.00")))
    println(describe(validate("ACC-1234", "ACC-1234", "-5")))

    println("\n== use y suppressed ==")
    val log2 = mutableListOf<String>()
    try {
        useTwo(log2, failWork = true, failClose = true)
    } catch (e: IllegalStateException) {
        println("principal: ${e.message}")
        e.suppressed.forEach { println("suppressed: ${it.message}") }
    }
    println("orden: $log2")
}
