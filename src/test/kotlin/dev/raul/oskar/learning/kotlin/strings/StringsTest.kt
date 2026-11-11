package dev.raul.oskar.learning.kotlin.strings

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StringsTest {

    @Test
    fun `templates and raw strings`() {
        assertEquals("Hola, Ana: tienes 1 artículo", greeting("Ana", 1))
        assertEquals("Reporte\n  cliente: X\n  total:   7", report("X", 7))
        assertEquals("peras.....  1234", row("peras", 1234))
    }

    @Test
    fun `substring helpers`() {
        assertEquals("example.com", domainOf("a@example.com"))
        assertEquals("", domainOf("sin-arroba"))
        assertEquals("a", userOf("a@example.com"))
    }

    @Test
    fun `string algorithms`() {
        assertEquals("tres dos uno", reverseWords("  uno dos   tres "))
        assertTrue(isPalindrome("Anita lava la tina"))
        assertFalse(isPalindrome("Kotlin"))
        assertEquals(mapOf('b' to 1, 'a' to 3, 'n' to 2), charFrequency("Banana"))
        assertEquals("Hola Mundo", capitalizeWords("hola mundo"))
        assertTrue(sameIgnoringCase("CA", "ca"))
    }

    @Test
    fun `split with string is literal, not regex`() {
        assertEquals(listOf("03/14/1985"), splitLiteral("03/14/1985", "\\/"))
        assertEquals(listOf("03", "14", "1985"), splitLiteral("03/14/1985", "/"))
        assertEquals(listOf("03", "14", "1985"), splitRegex("03/14/1985", "\\/"))
    }

    @Test
    fun `regex validation and extraction`() {
        assertTrue(isEmail("a.b+c@mail.example.com"))
        assertFalse(isEmail("no-es@email"))
        assertTrue(hasZip("Austin TX 73301"))
        assertEquals(listOf("33101", "73301"), allZips("Miami FL 33101, Austin TX 73301, ref 123456"))
    }

    @Test
    fun `named groups and destructuring`() {
        assertEquals(LocalDate.of(1985, 3, 14), parseUsDate("03/14/1985"))
        assertNull(parseUsDate("02/30/2020"))   // fecha imposible
        assertNull(parseUsDate("1985-03-14"))   // formato equivocado
        assertEquals(1985, yearByName("03/14/1985"))
    }

    @Test
    fun `replace with regex`() {
        assertEquals("tel ###-####", maskDigits("tel 555-1234"))
        assertEquals("************1234", maskCard("4111 1111 1111 1234"))
        assertEquals("a b c", normalizeSpaces("  a   b    c "))
        assertEquals(mapOf("host" to "db", "port" to "5432", "user" to "app"), parseKeyValues("host=db; port=5432; user = app"))
    }
}
