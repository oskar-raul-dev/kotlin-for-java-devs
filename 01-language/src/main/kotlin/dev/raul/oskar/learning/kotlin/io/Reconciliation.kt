package dev.raul.oskar.learning.kotlin.io

// Conciliación EXACTA del extracto contra el ledger: misma fecha y mismo monto, y cada línea se usa una sola vez.
// Con los datos generados deja fuera las diferencias a propósito (fechas con un día de desfase, centavos de
// diferencia, líneas sin pareja). Emparejar con tolerancias es el ⚡ conciliador de la lección (Exercises.md).

data class Reconciliation(
    val matched: List<Pair<LedgerLine, StatementLine>>,
    val onlyInLedger: List<LedgerLine>,
    val onlyInStatement: List<StatementLine>,
)

fun reconcileExact(ledger: List<LedgerLine>, statement: List<StatementLine>): Reconciliation {
    val pending = statement.toMutableList()
    val matched = mutableListOf<Pair<LedgerLine, StatementLine>>()
    val onlyInLedger = mutableListOf<LedgerLine>()
    for (movement in ledger) {
        // compareTo y no ==: BigDecimal("1.0") == BigDecimal("1.00") es false.
        val i = pending.indexOfFirst { it.date == movement.date && it.amount.compareTo(movement.amount) == 0 }
        if (i >= 0) matched += movement to pending.removeAt(i) else onlyInLedger += movement
    }
    return Reconciliation(matched, onlyInLedger, pending)
}

/** El reporte en texto: un resumen y las líneas que quedaron sin pareja. */
fun reconciliationReport(r: Reconciliation): String = buildString {
    appendLine("conciliadas:       ${r.matched.size}")
    appendLine("solo en el ledger: ${r.onlyInLedger.size}")
    r.onlyInLedger.forEach { appendLine("  %-10s %s %10s  %s".format(it.id, it.date, it.amount.toPlainString(), it.description)) }
    appendLine("solo en el banco:  ${r.onlyInStatement.size}")
    r.onlyInStatement.forEach { appendLine("  %-10s %s %10s  %s".format(it.reference, it.date, it.amount.toPlainString(), it.description)) }
}
