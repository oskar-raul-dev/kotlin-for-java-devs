package dev.raul.oskar.learning.kotlin.oop

import kotlin.math.PI

// Interfaces con implementación por defecto, sealed y when exhaustivo.

interface Named {
    val name: String
    fun describe(): String = "Soy $name"       // método por defecto (como default en Java 8)
}

/**
 * sealed: la jerarquía es cerrada (todas las subclases en el mismo paquete/módulo).
 * Ventaja: un `when` sobre ella es exhaustivo y no necesita `else`.
 */
sealed interface Shape : Named {
    fun area(): Double
}

data class Circle(val radius: Double) : Shape {
    override val name = "círculo"
    override fun area() = PI * radius * radius
}

data class Rectangle(val width: Double, val height: Double) : Shape {
    override val name = "rectángulo"
    override fun area() = width * height
}

data class Triangle(val base: Double, val height: Double) : Shape {
    override val name = "triángulo"
    override fun area() = base * height / 2
    override fun describe() = "Soy un triángulo de base $base"
}

/** Si mañana se agrega un Shape y no se cubre aquí, NO compila. */
fun perimeterHint(shape: Shape): String = when (shape) {
    is Circle -> "2πr = ${"%.2f".format(2 * PI * shape.radius)}"
    is Rectangle -> "2(w+h) = ${2 * (shape.width + shape.height)}"
    is Triangle -> "depende de los lados"
}

fun totalArea(shapes: List<Shape>): Double = shapes.sumOf { it.area() }
