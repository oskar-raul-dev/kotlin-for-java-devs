package dev.raul.oskar.learning.kotlin.functional

// Funciones de primera clase, lambdas, funciones de orden superior, scope functions,
// composición, currying, inmutabilidad y Result.

/** Tipo función: (Int) -> Int. Es como Function<Integer,Integer> pero sin interfaz. */
fun applyTwice(f: (Int) -> Int, x: Int): Int = f(f(x))

/** Devolver funciones: un "fabricante" con closure. */
fun multiplier(factor: Int): (Int) -> Int = { it * factor }

/** Composición: g ∘ f. Kotlin no la trae en la stdlib; se escribe con infix. */
infix fun <A, B, C> ((A) -> B).andThen(g: (B) -> C): (A) -> C = { a -> g(this(a)) }

/** Currying manual. */
fun add(a: Int): (Int) -> Int = { b -> a + b }

/** Lambda con receptor: el `this` dentro del bloque es un StringBuilder (así funciona buildString). */
fun html(block: StringBuilder.() -> Unit): String = StringBuilder().apply(block).toString()

/** inline + reified: el tipo genérico se conserva en ejecución (imposible en Java). */
inline fun <reified T> filterIsType(items: List<Any>): List<T> = items.filterIsInstance<T>()

/** Memoización con closure sobre un mapa. */
fun <K, V> memoize(f: (K) -> V): (K) -> V {
    val cache = HashMap<K, V>()
    return { k -> cache.getOrPut(k) { f(k) } }
}

/** fold: reduce con acumulador inicial (funciona con lista vacía, reduce no). */
fun <T> countIf(items: List<T>, p: (T) -> Boolean): Int = items.fold(0) { acc, x -> if (p(x)) acc + 1 else acc }

/** Result/runCatching: errores como valores en vez de try/catch. */
fun parseAll(raw: List<String>): Pair<List<Int>, List<String>> {
    val results = raw.map { s -> runCatching { s.trim().toInt() }.map { it to s } }
    val ok = results.mapNotNull { it.getOrNull()?.first }
    val errors = results.zip(raw).filter { (r, _) -> r.isFailure }.map { (_, s) -> s }
    return ok to errors
}

/** Scope functions: let, run, with, apply, also. Cuál devuelve qué. */
data class Order(var id: String = "", var items: MutableList<String> = mutableListOf(), var paid: Boolean = false)

fun scopeFunctions(log: MutableList<String>): Order =
    Order().apply {                       // apply: this = objeto, devuelve el objeto (configurar)
        id = "o-1"
        items += "libro"
    }.also {                              // also: it = objeto, devuelve el objeto (efecto lateral)
        log += "creada ${it.id}"
    }.let { order ->                      // let: it = objeto, devuelve el resultado de la lambda
        order.copy(items = (order.items + "lápiz").toMutableList())
    }.run {                               // run: this = objeto, devuelve el resultado
        paid = items.size > 1
        this
    }

fun summary(order: Order): String = with(order) { "$id: ${items.joinToString()} pagada=$paid" }

/** Recursión de cola funcional: suma de dígitos. */
tailrec fun digitSum(n: Long, acc: Long = 0): Long = if (n == 0L) acc else digitSum(n / 10, acc + n % 10)

/** Interfaz funcional (SAM) estilo Java: `fun interface`. */
fun interface Validator<T> {
    fun validate(value: T): Boolean

    infix fun and(other: Validator<T>): Validator<T> = Validator { validate(it) && other.validate(it) }
}

val notBlank = Validator<String> { it.isNotBlank() }
val shortText = Validator<String> { it.length <= 10 }
