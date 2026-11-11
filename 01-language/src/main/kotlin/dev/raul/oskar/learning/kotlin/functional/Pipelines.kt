package dev.raul.oskar.learning.kotlin.functional

// Pipelines sobre colecciones: el "Stream API" de Kotlin, pero directo sobre List.
// Ojo: List.map/filter son ANSIOSOS (crean lista intermedia en cada paso).
// asSequence() los vuelve perezosos, como un Stream de Java.

data class Txn(val user: String, val state: String, val amount: Int, val day: Int)

val sampleTxns = listOf(
    Txn("ana", "CA", 120, 1), Txn("ana", "CA", 80, 2), Txn("ana", "TX", 900, 3),
    Txn("bob", "NY", 50, 1), Txn("bob", "NY", 60, 1), Txn("bob", "NY", 70, 1),
    Txn("eva", "FL", 300, 2),
)

/** groupBy + mapValues: el Collectors.groupingBy(..., summingInt) de Java. */
fun totalByUser(txns: List<Txn>): Map<String, Int> =
    txns.groupBy { it.user }.mapValues { (_, list) -> list.sumOf { it.amount } }

/** groupingBy + eachCount: conteo por clave. */
fun countByState(txns: List<Txn>): Map<String, Int> = txns.groupingBy { it.state }.eachCount()

/** associate / associateBy / associateWith: listas a mapas. */
fun maxTxnByUser(txns: List<Txn>): Map<String, Txn> =
    txns.groupBy { it.user }.mapValues { (_, l) -> l.maxBy { it.amount } }

/** partition: dos listas en una pasada. */
fun splitLarge(txns: List<Txn>, threshold: Int): Pair<List<Txn>, List<Txn>> = txns.partition { it.amount >= threshold }

/** flatMap: de usuario a sus estados distintos, aplanado. */
fun distinctStatesPerUser(txns: List<Txn>): Map<String, Set<String>> =
    txns.groupBy({ it.user }, { it.state }).mapValues { it.value.toSet() }

/** Usuarios "sospechosos": muchas transacciones el mismo día o en más de un estado. */
fun suspiciousUsers(txns: List<Txn>, maxPerDay: Int = 2): List<String> =
    txns.groupBy { it.user }
        .filter { (_, list) ->
            list.groupingBy { it.day }.eachCount().values.any { it > maxPerDay } ||
                list.map { it.state }.distinct().size > 1
        }
        .keys.sorted()

/** zipWithNext: diferencias entre elementos consecutivos. */
fun deltas(values: List<Int>): List<Int> = values.zipWithNext { a, b -> b - a }

/** Secuencia perezosa e infinita: solo se calcula lo que take pide. */
fun firstSquaresAbove(limit: Int, count: Int, trace: MutableList<Int>): List<Int> =
    generateSequence(1) { it + 1 }
        .onEach { trace += it }
        .map { it * it }
        .filter { it > limit }
        .take(count)
        .toList()
