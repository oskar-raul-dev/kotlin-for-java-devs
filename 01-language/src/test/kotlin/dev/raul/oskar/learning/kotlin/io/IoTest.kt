package dev.raul.oskar.learning.kotlin.io

import java.math.BigDecimal
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.ExperimentalPathApi
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
    fun `csv line parsing tolerates bad lines`() {
        val sale = Sale.fromCsv("2026-10-01, Miami ,FL,laptop,2,10.50")!!
        assertEquals("Miami", sale.city)
        assertEquals(BigDecimal("21.00"), sale.total)
        assertNull(Sale.fromCsv("demasiado,corta"))
        assertNull(Sale.fromCsv("d,c,s,p,dos,1.0"))
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
    fun `sales round trip through a file`() {
        val sales = salesFromResource()
        val file = dir.resolve("out.csv")
        writeSales(file, sales)
        assertEquals(sales, readSales(file))
        assertEquals(sales.size + 1, countLines(file))
    }

    @Test
    fun `classpath resource`() {
        assertEquals(6, salesFromResource().size)
        assertFailsWith<IllegalArgumentException> { readResource("/no-existe.txt") }
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
        val table = formatTable(salesFromResource().take(1)).lines()
        assertEquals(table[0].length, table[1].length)
    }
}
