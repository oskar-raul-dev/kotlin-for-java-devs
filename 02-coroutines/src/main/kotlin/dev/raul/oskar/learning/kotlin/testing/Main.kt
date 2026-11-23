package dev.raul.oskar.learning.kotlin.testing

import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

// Esta lección vive en sus pruebas (TestingTest). La demo solo muestra lo que cuesta sin tiempo virtual.
fun main() = runBlocking {
    println("Una sesión de 1 minuto con delay real (runBlocking): tarda lo que dura.")
    val (summary, time) = measureTimedValue { summarize(sessionFeed(seed = 42, minutes = 1)) }
    println("$summary en $time")
    println("En TestingTest, la misma función procesa una hora de sesión en milisegundos con runTest.")
}
