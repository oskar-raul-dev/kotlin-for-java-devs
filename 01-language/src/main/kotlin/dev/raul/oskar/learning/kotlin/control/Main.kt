package dev.raul.oskar.learning.kotlin.control

fun main() {
    println("== Condicionales ==")
    println(listOf(-3, 0, 5).map(::sign))
    println(listOf(10, 30, 70).map(::ageGroup))
    println((1..15).joinToString(" ") { fizzBuzz(it) })
    println("firstWordLength(\"  hola mundo\") = ${firstWordLength("  hola mundo")}")
    println("firstWordLength(null)         = ${firstWordLength(null)}")

    println("\n== Bucles ==")
    println("sumTo(100)          = ${sumTo(100)}")
    println("indicesUntil(5)     = ${indicesUntil(5)}")
    println("countdown(10, 3)    = ${countdown(10, 3)}")
    println("indexed             = ${indexed(listOf("a", "b", "c"))}")
    println("digits(-12345)      = ${digits(-12345)}")
    println("firstPairWithSum    = ${firstPairWithSum(listOf(3, 8, 1, 7, 4), 11)}")
    println("stars(5)            = ${stars(5)}")

    println("\n== Algoritmos ==")
    println("gcd(84, 36)         = ${gcd(84, 36)}")
    println("primos < 30         = ${(1..30).filter(::isPrime)}")
    println("fibonacci(0..10)    = ${(0..10).map(::fibonacci)}")
    val sorted = insertionSort(intArrayOf(5, 2, 9, 1, 7))
    println("insertionSort       = ${sorted.contentToString()}")
    println("binarySearch(7)     = ${binarySearch(sorted, 7)}")
    println("twoSum([2,7,11,15],9) = ${twoSum(intArrayOf(2, 7, 11, 15), 9)}")
    println("balanced(\"{[()]}\")  = ${balanced("{[()]}")}, balanced(\"(]\") = ${balanced("(]")}")
}
