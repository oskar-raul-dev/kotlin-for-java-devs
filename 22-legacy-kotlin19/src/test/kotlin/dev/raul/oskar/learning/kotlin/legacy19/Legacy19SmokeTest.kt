package dev.raul.oskar.learning.kotlin.legacy19

import kotlin.test.Test
import kotlin.test.assertEquals

class Legacy19SmokeTest {

    @Test
    fun `runs on Kotlin 1_9_22`() {
        // Si alguien "actualiza" este track por error, esta prueba lo detecta.
        assertEquals("1.9.22", kotlinVersion())
    }

    @Test
    fun `data object has readable toString`() {
        assertEquals("Pending", Status.Pending.toString())
        assertEquals("Done", Status.Done.toString())
    }

    @Test
    fun `entries and open-ended range`() {
        assertEquals(listOf("LOW", "MEDIUM", "HIGH"), levelNames())
        assertEquals(listOf(0, 1, 2), firstIndices(3))
    }

    @Test
    fun `K1 compiles lambdas as classes`() {
        // Con K1 la lambda es una clase y toString() da la firma (sin kotlin-reflect, en forma de Function1<…>).
        // Con K2 (Kotlin 2.0+) sale de invokedynamic y toString() es el de la JVM ("Legacy19Kt$$Lambda/0x…@…"),
        // así que esta aserción fallaría: es la marca de que corre el backend de K1.
        assertEquals("Function1<java.lang.Integer, java.lang.Integer>", lambdaDescription())
    }
}
