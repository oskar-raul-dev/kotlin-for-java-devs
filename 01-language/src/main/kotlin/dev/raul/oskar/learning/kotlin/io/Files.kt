package dev.raul.oskar.learning.kotlin.io

import java.math.BigDecimal
import java.nio.file.Path
import java.time.LocalDate
import kotlin.io.path.appendText
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.io.path.useLines
import kotlin.io.path.writeText

// Archivos con java.nio.file.Path + las extensiones de kotlin.io.path.
// Regla: para archivos chicos readText/readLines; para grandes useLines (lee perezosamente y cierra).

/**
 * Parte una línea CSV respetando comillas (RFC 4180): `"PAGO TARJETA, FARMACIA SOL"` es UN campo, y `""` dentro de
 * comillas es una comilla literal. `line.split(",")` no sirve: parte también las comas de la descripción.
 */
fun splitCsvLine(line: String): List<String> {
    val fields = mutableListOf<String>()
    val current = StringBuilder()
    var quoted = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        when {
            quoted && c == '"' && line.getOrNull(i + 1) == '"' -> { current.append('"'); i++ }
            c == '"' -> quoted = !quoted
            c == ',' && !quoted -> { fields += current.toString(); current.clear() }
            else -> current.append(c)
        }
        i++
    }
    fields += current.toString()
    return fields
}

/** Lo inverso: comillas solo si el campo tiene comas o comillas. */
fun csvField(text: String): String =
    if (text.any { it == ',' || it == '"' }) "\"" + text.replace("\"", "\"\"") + "\"" else text

/** Una línea del extracto externo (data/bank-statement.csv). */
data class StatementLine(val date: LocalDate, val description: String, val amount: BigDecimal, val reference: String) {

    fun toCsv(): String = listOf(date.toString(), csvField(description), amount.toPlainString(), reference).joinToString(",")

    companion object {
        const val HEADER = "date,description,amount,reference"

        /** Parsea una línea; devuelve null si está mal formada (en vez de lanzar). */
        fun fromCsv(line: String): StatementLine? {
            val f = splitCsvLine(line).map { it.trim() }
            if (f.size != 4) return null
            val date = runCatching { LocalDate.parse(f[0]) }.getOrNull() ?: return null
            val amount = f[2].toBigDecimalOrNull() ?: return null
            return StatementLine(date, f[1], amount, f[3])
        }
    }
}

/** Un movimiento del ledger de la misma cuenta (data/ledger-movements.csv): la contraparte para conciliar. */
data class LedgerLine(val id: String, val date: LocalDate, val description: String, val amount: BigDecimal) {
    companion object {
        fun fromCsv(line: String): LedgerLine? {
            val f = splitCsvLine(line).map { it.trim() }
            if (f.size != 4) return null
            val date = runCatching { LocalDate.parse(f[1]) }.getOrNull() ?: return null
            val amount = f[3].toBigDecimalOrNull() ?: return null
            return LedgerLine(f[0], date, f[2], amount)
        }
    }
}

/** Texto completo y escritura completa (sobrescribe). */
fun writeAll(path: Path, text: String) {
    path.parent?.createDirectories()
    path.writeText(text)
}

fun readAll(path: Path): String = path.readText()

fun appendLine(path: Path, line: String) = path.appendText(line + System.lineSeparator())

/** useLines: Sequence perezosa; el archivo se cierra al salir del bloque, así que se consume DENTRO (toList). */
fun readStatement(path: Path): List<StatementLine> =
    path.useLines { lines -> lines.drop(1).filter { it.isNotBlank() }.mapNotNull(StatementLine::fromCsv).toList() }

fun countLines(path: Path): Int = if (path.exists()) path.readLines().size else 0

/** use {} = try-with-resources: cierra el writer aunque haya excepción. */
fun writeStatement(path: Path, lines: List<StatementLine>) {
    path.parent?.createDirectories()
    path.bufferedWriter().use { w ->
        w.write(StatementLine.HEADER)
        w.newLine()
        lines.forEach { w.write(it.toCsv()); w.newLine() }
    }
}

/** Recursos del classpath (src/main/resources). */
fun readResource(name: String): String =
    object {}.javaClass.getResource(name)?.readText()
        ?: throw IllegalArgumentException("recurso no encontrado: $name")

fun statementFromResource(name: String = "/data/bank-statement.csv"): List<StatementLine> =
    readResource(name).lineSequence().drop(1).filter { it.isNotBlank() }.mapNotNull(StatementLine::fromCsv).toList()

fun ledgerFromResource(name: String = "/data/ledger-movements.csv"): List<LedgerLine> =
    readResource(name).lineSequence().drop(1).filter { it.isNotBlank() }.mapNotNull(LedgerLine::fromCsv).toList()
