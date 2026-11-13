// Funciones: nivel superior, parámetros por defecto, extensiones, lambdas, inline, plantillas y chequeos de null.
@file:JvmName("Functions")

package dev.raul.oskar.learning.kotlin.tutorials.decompile

import java.math.BigDecimal

/** Parámetro por defecto: un método fee(BigDecimal, BigDecimal) y otro sintético fee$default con una máscara de bits. */
fun fee(amount: BigDecimal, rate: BigDecimal = BigDecimal("0.005")): BigDecimal = amount * rate

/** @JvmOverloads: además genera las sobrecargas para Java, una por cada parámetro por defecto. */
@JvmOverloads
fun feeForJava(amount: BigDecimal, rate: BigDecimal = BigDecimal("0.005")): BigDecimal = amount * rate

/** Extensión: un método estático cuyo primer parámetro es el receptor. */
fun String.isTicker(): Boolean = length in 1..5 && all { it.isUpperCase() }

/** Parámetro no nullable: kotlinc agrega Intrinsics.checkNotNullParameter al entrar. */
fun describe(account: Account): String = "Cuenta ${account.id}: ${account.balance}"

/** Lambda pasada a una función inline (filter): se copia en el método y no queda ningún objeto lambda. */
fun pricesAbove(prices: List<Double>, limit: Double): List<Double> = prices.filter { it > limit }

/** Lambda que escapa (se devuelve): en Kotlin 2.x, invokedynamic con LambdaMetafactory, sin clase propia. */
fun above(limit: Double): (Double) -> Boolean = { it > limit }

/** inline: el cuerpo (y la lambda) se copian en quien llama; no hay invocación a measure en el bytecode del llamador. */
inline fun <T> measure(label: String, block: () -> T): T {
    val start = System.nanoTime()
    return block().also { println("$label: ${System.nanoTime() - start} ns") }
}

fun total(prices: List<Double>): Double = measure("total") { prices.sum() }

/** reified: solo posible en inline; el tipo real se conoce en cada llamada. */
inline fun <reified T> countOf(items: List<Any>): Int = items.count { it is T }

fun countStrings(items: List<Any>): Int = countOf<String>(items)
