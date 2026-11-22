package dev.raul.oskar.learning.kotlin.io

import java.math.BigDecimal
import java.time.LocalDate
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalPathApi::class)
class IoTest {

    private val dir = createTempDirectory("kotlin-io-test")

    @AfterTest
    fun cleanUp() = dir.deleteRecursively()     // solo el temporal que creó esta prueba

    @Test
    fun `split with comma breaks quoted fields, the csv splitter does not`() {
        val line = "2026-10-02,\"PAGO TARJETA, LIBRERÍA NORTE\",-154.30,BNK-70002"
        assertEquals(5, line.split(",").size)                     // la trampa: la coma de la descripción también parte
        assertEquals(listOf("2026-10-02", "PAGO TARJETA, LIBRERÍA NORTE", "-154.30", "BNK-70002"), splitCsvLine(line))
        assertEquals(listOf("a \"b\", c"), splitCsvLine(csvField("a \"b\", c")))   // ida y vuelta con comillas
    }

    @Test
    fun `csv line parsing tolerates bad lines`() {
        val line = StatementLine.fromCsv("2026-10-02, \"PAGO TARJETA, LIBRERÍA NORTE\" ,-154.30,BNK-70002")!!
        assertEquals("PAGO TARJETA, LIBRERÍA NORTE", line.description)
        assertEquals(BigDecimal("-154.30"), line.amount)
        assertNull(StatementLine.fromCsv("demasiado,corta"))
        assertNull(StatementLine.fromCsv("2026-10-32,x,1.00,BNK-1"))   // fecha imposible
        assertNull(StatementLine.fromCsv("2026-10-02,x,diez,BNK-1"))
    }

    @Test
    fun `write read and append text`() {
        val file = dir.resolve("sub/notes.txt")
        writeAll(file, "uno\n")
        appendLine(file, "dos")
        assertTrue(file.exists())
        assertEquals(2, countLines(file))
        assertTrue(readAll(file).startsWith("uno"))
    }

    @Test
    fun `statement round trip through a file`() {
        val statement = statementFromResource()
        val file = dir.resolve("out.csv")
        writeStatement(file, statement)
        assertEquals(statement, readStatement(file))
        assertEquals(statement.size + 1, countLines(file))
    }

    @Test
    fun `classpath resources`() {
        assertEquals(27, statementFromResource().size)
        assertEquals(27, ledgerFromResource().size)
        assertFailsWith<IllegalArgumentException> { readResource("/no-existe.txt") }
    }

    @Test
    fun `exact reconciliation uses each line once and compares amounts with compareTo`() {
        val day = LocalDate.of(2026, 10, 1)
        val ledger = listOf(LedgerLine("JE-1", day, "a", BigDecimal("10.0")), LedgerLine("JE-2", day, "b", BigDecimal("10.00")))
        val statement = listOf(StatementLine(day, "A", BigDecimal("10.00"), "BNK-1"))
        val r = reconcileExact(ledger, statement)
        assertEquals(listOf("JE-1"), r.matched.map { it.first.id })   // 10.0 y 10.00 son el mismo monto
        assertEquals(listOf("JE-2"), r.onlyInLedger.map { it.id })    // la línea del banco ya se usó
        assertTrue(r.onlyInStatement.isEmpty())
    }

    @Test
    fun `exact reconciliation of the generated month leaves the planned differences`() {
        val r = reconcileExact(ledgerFromResource(), statementFromResource())
        // 3 fechas corridas + 2 montos con un centavo de diferencia + 1 movimiento que el banco no reporta (ledger)
        // y las mismas 5 + 1 cargo que el ledger no tiene (banco): eso es lo que resuelve el ⚡ con tolerancias.
        assertEquals(21, r.matched.size)
        assertEquals(6, r.onlyInLedger.size)
        assertEquals(6, r.onlyInStatement.size)
        assertTrue("solo en el banco:  6" in reconciliationReport(r))
    }

    @Test
    fun `console logic with fake input`() {
        val input = "10\nx\n5\nfin\n99\n".reader().buffered()
        val out = StringBuilder()
        assertEquals(15, sumNumbers(input, out))
        assertTrue("'x' no es un número" in out)
        assertTrue(out.endsWith("total = 15\n"))
    }

    @Test
    fun `console stops at end of input`() {
        assertEquals(3, sumNumbers("1\n2\n".reader().buffered(), StringBuilder()))   // sin "fin": corta en EOF
    }

    @Test
    fun `table is aligned`() {
        val table = formatTable(statementFromResource().take(3)).lines().filter { it.isNotEmpty() }
        assertEquals(1, table.map { it.length }.distinct().size)
    }
}
