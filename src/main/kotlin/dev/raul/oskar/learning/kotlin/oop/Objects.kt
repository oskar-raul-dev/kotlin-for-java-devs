package dev.raul.oskar.learning.kotlin.oop

import java.util.concurrent.atomic.AtomicLong

// object, companion, data class, value class, delegación y sobrecarga de operadores.

/** object = singleton seguro para hilos, sin el patrón getInstance(). */
object IdGenerator {
    private val counter = AtomicLong()
    fun next(prefix: String = "id"): String = "$prefix-${counter.incrementAndGet()}"
    fun reset() = counter.set(0)
}

/** data class: equals/hashCode/toString/copy/componentN generados. */
data class Customer(val id: String, val name: String, val email: String? = null) {
    // Las propiedades del cuerpo NO entran en equals/hashCode.
    var visits: Int = 0
}

/** Constructor privado + fábrica en companion: el único camino crea objetos válidos. */
class Email private constructor(val value: String) {
    companion object {
        fun parse(raw: String): Email? =
            raw.trim().lowercase().takeIf { it.matches(Regex("""[^@\s]+@[^@\s]+\.\w+""")) }?.let(::Email)
    }

    override fun toString() = value
}

/** value class: un tipo distinto en compilación, sin objeto extra en ejecución. */
@JvmInline
value class Cents(val value: Long) {
    operator fun plus(other: Cents) = Cents(value + other.value)
    operator fun times(n: Int) = Cents(value * n)
    override fun toString() = "%d.%02d".format(value / 100, value % 100)
}

/** Operadores: plus, minus, compareTo, get, contains... con la palabra `operator`. */
data class Vector2(val x: Int, val y: Int) : Comparable<Vector2> {
    operator fun plus(o: Vector2) = Vector2(x + o.x, y + o.y)
    operator fun unaryMinus() = Vector2(-x, -y)
    operator fun get(i: Int) = when (i) { 0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException(i) }
    override fun compareTo(other: Vector2) = compareValuesBy(this, other, { it.x }, { it.y })
}

/** Delegación de interfaz con `by`: composición sin escribir los métodos a mano. */
class CountingList<T>(private val inner: MutableList<T> = mutableListOf()) : MutableList<T> by inner {
    var adds = 0
        private set

    override fun add(element: T): Boolean {
        adds++
        return inner.add(element)
    }

    // `by` delega solo los miembros de la interfaz: toString/equals/hashCode NO se delegan.
    override fun toString(): String = inner.toString()
}

/** Propiedades delegadas: lazy se calcula una sola vez, en el primer acceso. */
class Config(private val loader: () -> Map<String, String>) {
    var loads = 0
        private set

    val values: Map<String, String> by lazy {
        loads++
        loader()
    }
}

/** Genéricos con restricción: T debe ser Comparable. */
fun <T : Comparable<T>> maxOf3(a: T, b: T, c: T): T = maxOf(a, maxOf(b, c))
