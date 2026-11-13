package dev.raul.oskar.learning.kotlin.legacy19

fun main() {
    println("Kotlin (stdlib)   = ${kotlinVersion()}")
    println("data object       = ${Status.Pending}, ${Status.Done}")
    println("entries           = ${levelNames()}")
    println("0..<3             = ${firstIndices(3)}")
    println("lambda.toString() = ${lambdaDescription()}   <- con K2 (2.x) sería Legacy19Kt\$\$Lambda/0x…")
}
