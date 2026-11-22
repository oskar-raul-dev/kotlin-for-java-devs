package dev.raul.oskar.learning.kotlin.io

import co.touchlab.kermit.Logger
import kotlin.io.path.Path
import kotlin.io.path.absolute

// Ejecutar con: ./gradlew -p 01-language run -Plesson=io --console=plain
// (el build conecta System.in, así que la parte interactiva funciona; Ctrl+D o "fin" termina).

fun main(args: Array<String>) {
    // Kermit: logger multiplataforma (JVM, Android, iOS).
    val log = Logger.withTag("io")

    val statement = statementFromResource()
    val ledger = ledgerFromResource()
    log.i { "Leídas ${statement.size} líneas del extracto y ${ledger.size} movimientos del ledger" }
    print(formatTable(statement.take(8)))

    val dir = Path("build/io-demo")
    val copy = dir.resolve("statement-copy.csv")
    writeStatement(copy, statement)
    log.i { "Escrito ${copy.absolute()} (${countLines(copy)} líneas); releídas ${readStatement(copy).size}" }

    val report = dir.resolve("reconciliation.txt")
    writeAll(report, reconciliationReport(reconcileExact(ledger, statement)))
    appendLine(report, "(conciliación exacta: sin tolerancias de fecha ni de monto)")
    println("\n" + readAll(report))

    if ("--no-console" !in args) {
        println("== Consola ==")
        sumNumbers(System.`in`.bufferedReader(), System.out)
    }
}
