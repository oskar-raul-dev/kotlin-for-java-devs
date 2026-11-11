package dev.raul.oskar.learning.kotlin.io

import java.math.BigDecimal
import java.nio.file.Path
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

data class Sale(
    val date: String,
    val city: String,
    val state: String,
    val product: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
) {
    val total: BigDecimal get() = unitPrice * quantity.toBigDecimal()

    fun toCsv(): String = listOf(date, city, state, product, quantity, unitPrice).joinToString(",")

    companion object {
        const val HEADER = "date,city,state,product,quantity,unit_price"

        /** Parsea una línea; devuelve null si está mal formada (en vez de lanzar). */
        fun fromCsv(line: String): Sale? {
            val f = line.split(",").map { it.trim() }
            if (f.size != 6) return null
            val qty = f[4].toIntOrNull() ?: return null
            val price = f[5].toBigDecimalOrNull() ?: return null
            return Sale(f[0], f[1], f[2], f[3], qty, price)
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

/** useLines: Sequence perezosa; el archivo se cierra al salir del bloque. */
fun readSales(path: Path): List<Sale> =
    path.useLines { lines -> lines.drop(1).filter { it.isNotBlank() }.mapNotNull(Sale::fromCsv).toList() }

fun countLines(path: Path): Int = if (path.exists()) path.readLines().size else 0

/** use {} = try-with-resources: cierra el writer aunque haya excepción. */
fun writeSales(path: Path, sales: List<Sale>) {
    path.parent?.createDirectories()
    path.bufferedWriter().use { w ->
        w.write(Sale.HEADER)
        w.newLine()
        sales.forEach { w.write(it.toCsv()); w.newLine() }
    }
}

/** Recursos del classpath (src/main/resources). */
fun readResource(name: String): String =
    object {}.javaClass.getResource(name)?.readText()
        ?: throw IllegalArgumentException("recurso no encontrado: $name")

fun salesFromResource(name: String = "/data/sales.csv"): List<Sale> =
    readResource(name).lineSequence().drop(1).filter { it.isNotBlank() }.mapNotNull(Sale::fromCsv).toList()
