package dev.raul.oskar.learning.kotlin.oop

import java.math.BigDecimal

fun main() {
    println("== Herencia y polimorfismo ==")
    val savings = SavingsAccount("S-1", BigDecimal("1000"), BigDecimal("0.05"))
    val checking = CheckingAccount("C-1", BigDecimal("100"), BigDecimal("200"))
    val accounts: List<Account> = listOf(savings, checking, LockedAccount("L-1"))
    accounts.forEach { println(it) }
    transfer(checking, savings, BigDecimal("250"))      // usa el sobregiro
    println("después de transferir 250: $checking, $savings, interés=${savings.interest()}")
    runCatching { transfer(accounts[2], savings, BigDecimal.ONE) }
        .onFailure { println("error esperado: ${it.message}") }

    println("\n== Sealed + interfaces ==")
    val shapes = listOf(Circle(1.0), Rectangle(2.0, 3.0), Triangle(4.0, 5.0))
    shapes.forEach { println("${it.describe()} | área=${"%.2f".format(it.area())} | ${perimeterHint(it)}") }
    println("área total = ${"%.2f".format(totalArea(shapes))}")

    println("\n== Enums ==")
    println("ShippingMethod.of(\"EXPRESS\") = ${ShippingMethod.of("EXPRESS")}, of(\"x\") = ${ShippingMethod.of("x")}")
    ShippingMethod.entries.forEach { println("${it.name} ${it.code} ${it.maxDays} ${it.costFactor()} ${label(it)}") }
    println("NORTH.turnRight() = ${Direction.NORTH.turnRight()}, WEST.turnRight() = ${Direction.WEST.turnRight()}")

    println("\n== object / data / value ==")
    println(IdGenerator.next("order") + " " + IdGenerator.next("order"))
    val c1 = Customer("1", "Ana")
    val c2 = c1.copy(email = "ana@example.com")
    val (id, name, email) = c2
    println("$c1 | $c2 | destructuring: $id $name $email")
    println("Email.parse(\" ANA@Example.com \") = ${Email.parse(" ANA@Example.com ")}, inválido = ${Email.parse("x")}")
    println("Cents(1999) * 3 + Cents(1) = ${Cents(1999) * 3 + Cents(1)}")
    println("Vector2(1,2) + Vector2(3,4) = ${Vector2(1, 2) + Vector2(3, 4)}, -v = ${-Vector2(1, 2)}")
    val list = CountingList<String>()
    list.add("a"); list.add("b"); list.remove("a")
    println("CountingList = $list, adds=${list.adds}")
    println("maxOf3 = ${maxOf3("pera", "uva", "manzana")}")
}
