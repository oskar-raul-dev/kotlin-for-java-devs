// La aplicación: usa la librería money. Se compila con money.jar en el classpath (-cp).
package dev.raul.oskar.learning.kotlin.tutorials.cli.app

import dev.raul.oskar.learning.kotlin.tutorials.cli.money.usd

fun main() {
    val total = listOf("10.50", "0.25", "4").map(::usd).reduce { a, b -> a + b }
    println("total = $total")
}
