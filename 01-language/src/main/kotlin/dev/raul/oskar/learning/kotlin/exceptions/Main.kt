package dev.raul.oskar.learning.kotlin.exceptions

import java.io.IOException
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
    println("classify(parseAge)    = ${classify { parseAge(" doce ") }}")
    val log = mutableListOf<String>()
    println("withFinally(fail)     = ${withFinally(log, fail = true)} log=$log")

    println("\n== Dominio ==")
    val down = OrderService({ throw IOException("connection refused: inventory:8443") })
    try {
        down.canPlace("sku-1", 5)
    } catch (e: OrderException) {
        println("HTTP ${httpStatusFor(e)}")
        causeChain(e).forEach { println("  causa: $it") }
        println("  raíz: ${rootCause(e).message}")
    }
    val ok = OrderService({ sku -> if (sku == "sku-1") 10 else 0 }, mapOf("O-1" to 5000))
    println("canPlace(sku-1) = ${ok.canPlace("sku-1", 5)}, canPlace(sku-2) = ${ok.canPlace("sku-2", 5)}")
    listOf<() -> Any>({ ok.canPlace(" ", 1) }, { ok.totalOf("O-9") }).forEach { call ->
        try {
            call()
        } catch (e: OrderException) {
            println("HTTP ${httpStatusFor(e)}: ${e.message}")
        }
    }

    println("\n== Result ==")
    listOf("8443", " 80 ", "abc", "70000").forEach { raw ->
        println("'$raw' -> ${portOrExplain(raw)}, default=${portOrDefault(raw)}, recover=${portRecoveringFormat(raw)}, doble=${doubledPort(raw)}")
    }

    println("\n== Validación acumulada ==")
    println(describe(validate("Ana", 30, "ana@example.com")))
    println(describe(validate("", 12, "ana.example.com")))

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
