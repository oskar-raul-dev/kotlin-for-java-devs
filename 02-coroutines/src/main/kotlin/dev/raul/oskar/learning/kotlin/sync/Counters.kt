package dev.raul.oskar.learning.kotlin.sync

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

/** Cuenta operaciones del Banco desde muchas corrutinas en `Dispatchers.Default` (hilos de verdad). */
interface OperationCounter {
    fun increment()
    val count: Long
}

/**
 * `@Volatile` da **visibilidad** (cada hilo ve el último valor escrito), no atomicidad: `count++` es leer, sumar y
 * escribir, y dos hilos pueden leer el mismo valor. El modelo de memoria es el de Java (JMM): `@Volatile` compila a un
 * campo `volatile`.
 */
class VolatileCounter : OperationCounter {
    @Volatile
    private var value = 0L
    override fun increment() { value++ }
    override val count: Long get() = value
}

/** `AtomicLong` de Java, igual que en Java. */
class JavaAtomicCounter : OperationCounter {
    private val value = AtomicLong()
    override fun increment() { value.incrementAndGet() }
    override val count: Long get() = value.get()
}

/**
 * atomicfu: la misma API en JVM, JS y Native (importa en multiplataforma, `11-multiplatform`). En la JVM, el plugin de
 * Gradle reescribe `atomic(0L)` a un campo `volatile long` con `AtomicLongFieldUpdater`: sin objeto extra por contador.
 */
class AtomicfuCounter : OperationCounter {
    private val value = atomic(0L)
    override fun increment() { value.incrementAndGet() }
    override val count: Long get() = value.value
}

/**
 * Los atómicos de la stdlib (`kotlin.concurrent.atomics`), experimentales en 2.4.21: el opt-in es **obligatorio**
 * (error, no warning; medido). En la JVM, `kotlin.concurrent.atomics.AtomicLong` es la misma clase
 * `java.util.concurrent.atomic.AtomicLong`, con extensiones como `incrementAndFetch` que hay que importar.
 */
@OptIn(ExperimentalAtomicApi::class)
class StdlibAtomicCounter : OperationCounter {
    private val value = kotlin.concurrent.atomics.AtomicLong(0)
    override fun increment() { value.incrementAndFetch() }
    override val count: Long get() = value.load()
}

/** Un `Mutex` alrededor de un `var` común: correcto, pero cada incremento toma y suelta el lock. */
class MutexCounter {
    private val mutex = Mutex()
    private var value = 0L
    suspend fun increment() = mutex.withLock { value++ }
    val count: Long get() = value
}

/** [coroutines] corrutinas en `Dispatchers.Default`, cada una incrementa [each] veces. */
suspend fun hammer(counter: OperationCounter, coroutines: Int = 100, each: Int = 1_000): Long {
    withContext(Dispatchers.Default) {
        repeat(coroutines) { launch { repeat(each) { counter.increment() } } }
    }
    return counter.count
}

suspend fun hammer(counter: MutexCounter, coroutines: Int = 100, each: Int = 1_000): Long {
    withContext(Dispatchers.Default) {
        repeat(coroutines) { launch { repeat(each) { counter.increment() } } }
    }
    return counter.count
}
