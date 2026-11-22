package dev.raul.oskar.learning.kotlin.oop

import dev.raul.oskar.learning.kotlin.basics.Money
import java.util.concurrent.atomic.AtomicLong

// object, companion, data class, value class, delegación y sobrecarga de operadores.

/** object = singleton seguro para hilos, sin el patrón getInstance(). */
object IdGenerator {
    private val counter = AtomicLong()
    fun next(prefix: String = "id"): String = "$prefix-${counter.incrementAndGet()}"
    fun reset() = counter.set(0)
}

/** data class: equals/hashCode/toString/copy/componentN generados. Posición de un cliente en un ticker. */
data class Position(val ticker: String, val quantity: Long, val averageCost: Money) {
    // Las propiedades del cuerpo NO entran en equals/hashCode/toString/copy.
    var lastPrice: Money? = null
}

/** Constructor privado + fábrica en companion: el único camino crea objetos válidos. */
class Ticker private constructor(val symbol: String) {
    companion object {
        private val SYMBOL = Regex("[A-Z]{1,5}")

        fun parse(raw: String): Ticker? = raw.trim().uppercase().takeIf { SYMBOL.matches(it) }?.let(::Ticker)
    }

    override fun toString() = symbol
}

/**
 * value class: un tipo distinto en compilación, sin objeto extra en ejecución. Una función que recibe un
 * TransferId no acepta por error una clave de idempotencia u otro String cualquiera.
 */
@JvmInline
value class TransferId(val value: String) {
    init {
        require(PATTERN.matches(value)) { "id de transferencia inválido: $value" }
    }

    companion object {
        private val PATTERN = Regex("""TRF-\d{6}""")   // en el companion: se compila una vez, no en cada init

        fun of(number: Long): TransferId = TransferId("TRF-%06d".format(number))
    }
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
