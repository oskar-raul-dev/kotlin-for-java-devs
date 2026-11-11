package dev.raul.oskar.learning.kotlin.io

import co.touchlab.kermit.Logger
import kotlin.io.path.Path
import kotlin.io.path.absolute

// Ejecutar con: ./gradlew run -Plesson=io --console=plain
// (el build conecta System.in, así que la parte interactiva funciona; Ctrl+D o "fin" termina).

fun main(args: Array<String>) {
    // Kermit: logger multiplataforma (JVM, Android, iOS).
    val log = Logger.withTag("io")

    val sales = salesFromResource()
    log.i { "Leídas ${sales.size} ventas del classpath" }
    print(formatTable(sales))

    val out = Path("build/io-demo/sales-miami.csv")
    writeSales(out, sales.filter { it.city == "Miami" })
    appendLine(out, "2026-10-04,Miami,FL,mouse,2,25.50")
    log.i { "Escrito ${out.absolute()} (${countLines(out)} líneas)" }
    println(readAll(out))
    println("Releídas: ${readSales(out).map { it.product }}")

    if ("--no-console" !in args) {
        println("\n== Consola ==")
        sumNumbers(System.`in`.bufferedReader(), System.out)
    }
}
