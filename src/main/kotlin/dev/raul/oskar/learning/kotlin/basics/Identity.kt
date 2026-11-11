package dev.raul.oskar.learning.kotlin.basics

// == en Kotlin llama a equals() (y maneja null); === compara referencias (el == de Java).

data class Point(val x: Int, val y: Int)

fun structurallyEqual(a: Any?, b: Any?): Boolean = a == b
fun sameReference(a: Any?, b: Any?): Boolean = a === b

/**
 * Any  = Object de Java (raíz de todo lo no nullable).
 * Unit = void, pero es un valor real (un objeto singleton).
 * Nothing = "esta expresión nunca termina normalmente" (throw, bucle infinito).
 */
fun fail(message: String): Nothing = throw IllegalStateException(message)

fun describe(value: Any?): String = when (value) {
    null -> "null"
    is Int -> "Int($value)"
    is Long -> "Long($value)"
    is Double -> "Double($value)"
    is String -> "String(len=${value.length})"   // smart cast: aquí value ya es String
    is Boolean -> "Boolean($value)"
    is Char -> "Char($value)"
    else -> "Otro(${value::class.simpleName})"
}

/** Tipos sin signo (UInt, ULong...) existen, poco usados en backend. */
fun unsignedMax(): UInt = UInt.MAX_VALUE
