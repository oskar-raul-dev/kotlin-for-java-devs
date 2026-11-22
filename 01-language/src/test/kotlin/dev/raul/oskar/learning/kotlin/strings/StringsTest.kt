package dev.raul.oskar.learning.kotlin.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StringsTest {

    @Test
    fun `templates and raw strings`() {
        assertEquals("Hola, Ana: tienes 1 orden abierta", greeting("Ana", 1))
        assertEquals("Hola, Ana: tienes 2 órdenes abiertas", greeting("Ana", 2))
        assertEquals("Reporte\n  ticker:  ACME\n  volumen: 7", report("ACME", 7))
        assertEquals("NUBE......  1234", row("NUBE", 1234))
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
        assertEquals(listOf("35=D|55=ACME"), splitLiteral("35=D|55=ACME", "\\|"))   // FIX: la misma trampa
        assertEquals(listOf("35=D", "55=ACME"), splitLiteral("35=D|55=ACME", "|"))
    }

    @Test
    fun `regex validation and extraction`() {
        assertTrue(isEmail("a.b+c@mail.example.com"))
        assertFalse(isEmail("no-es@email"))
        assertTrue(isAccountFormat("ACC-00001234-82"))
        assertFalse(isAccountFormat("ACC-1234-82"))
        assertFalse(isAccountFormat("cuenta ACC-00001234-82"))        // matches: toda la cadena
        assertTrue(mentionsAccount("cuenta ACC-00001234-82"))         // containsMatchIn: en cualquier parte
        assertEquals(listOf("ACC-00001234-82", "ACC-00005678-39"), allAccounts("de ACC-00001234-82 a ACC-00005678-39, ref ACC-12"))
    }

    @Test
    fun `named groups and destructuring`() {
        assertEquals("00001234" to "82", accountParts("ACC-00001234-82"))
        assertNull(accountParts("ACC-00001234"))                      // formato equivocado
        assertEquals("00005678", accountNumber("ACC-00005678-39"))
    }

    @Test
    fun `replace with regex`() {
        assertEquals("tel ###-####", maskDigits("tel 555-1234"))
        assertEquals("ACC-****1234-82", maskAccount("ACC-00001234-82"))
        assertEquals("a b c", normalizeSpaces("  a   b    c "))
        assertEquals(mapOf(35 to "D", 55 to "ACME", 54 to "1", 38 to "10", 44 to "100.00"), parseFix("35=D|55=ACME|54=1|38=10|44=100.00"))
    }
}
