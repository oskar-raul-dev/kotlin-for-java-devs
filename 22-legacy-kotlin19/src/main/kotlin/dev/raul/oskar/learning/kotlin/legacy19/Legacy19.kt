package dev.raul.oskar.learning.kotlin.legacy19

// Prueba de humo del track: lo justo para demostrar que esto compila y corre con el compilador K1 de Kotlin 1.9.22.
// Las lecciones del track (language19, k2migration, compat) se escriben en la fase B5.

/** Versión de la biblioteca estándar con la que se corre (en este track, 1.9.22). */
fun kotlinVersion(): String = KotlinVersion.CURRENT.toString()

/** `data object` (estable desde 1.9): `toString()` legible, sin el `Status$Done@1b6d3586` de un `object` común. */
sealed interface Status {
    data object Pending : Status
    data object Done : Status
}

/** `entries` (estable desde 1.9) en lugar de `values()`: no crea un arreglo nuevo en cada llamada. */
enum class Level { LOW, MEDIUM, HIGH }

fun levelNames(): List<String> = Level.entries.map { it.name }

/** `..<` (estable desde 1.9): rango abierto por la derecha, lo mismo que `until`. */
fun firstIndices(n: Int): List<Int> = (0..<n).toList()

/**
 * Cómo se ve una lambda con `toString()`. Con K1 (1.9) cada lambda se compila como una clase que hereda de
 * `kotlin.jvm.internal.Lambda`, y su `toString()` describe la firma: sin `kotlin-reflect` en el classpath da
 * `Function1<java.lang.Integer, java.lang.Integer>`.
 * Desde Kotlin 2.0 las lambdas se generan con `invokedynamic` (`LambdaMetafactory`, como en Java) y `toString()`
 * es el de la JVM: `Legacy19Kt$$Lambda/0x…@…` (medido con 2.4.21). Cambia el comportamiento en ejecución, no solo
 * la compilación: cuidado con logs o pruebas que dependan de ese texto.
 */
fun lambdaDescription(): String {
    val inc: (Int) -> Int = { x -> x + 1 }
    return inc.toString()
}
