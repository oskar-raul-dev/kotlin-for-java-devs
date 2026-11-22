package dev.raul.oskar.learning.kotlin.oop

import dev.raul.oskar.learning.kotlin.basics.Money
import java.math.BigDecimal

fun main() {
    println("== Herencia y polimorfismo ==")
    val savings = SavingsAccount("ACC-00001234-82", Money.of("1000"), BigDecimal("0.004"))
    val checking = CheckingAccount("ACC-00005678-39", Money.of("100"), Money.of("200"))
    val accounts: List<Account> = listOf(savings, checking, EscrowAccount("ESC-1"))
    accounts.forEach { println(it) }
    transfer(checking, savings, Money.of("250"))      // usa el sobregiro
    println("después de transferir 250: $checking, $savings, interés del mes=${savings.interest()}")
    runCatching { transfer(accounts[2], savings, Money.of("1")) }
        .onFailure { println("error esperado: ${it.message}") }
    checking.freeze()
    runCatching { checking.withdraw(Money.of("1")) }.onFailure { println("error esperado: ${it.message}") }

    println("\n== Sealed + interfaces ==")
    val orders = listOf(
        LimitOrder("ORD-1", "ACME", Side.BUY, 10, Money.of("100.00")),
        MarketOrder("ORD-2", "ORBX", Side.SELL, 5),
    )
    orders.forEach { println("${it.describe()} | ${priceHint(it)} | nocional=${it.notional()}") }
    println("comprometido = ${committed(orders)}")

    println("\n== Enums ==")
    println("AccountType.of(\"ASSET\") = ${AccountType.of("ASSET")}, of(\"x\") = ${AccountType.of("x")}")
    AccountType.entries.forEach { println("${it.name} ${it.code} débito-suma=${it.increasesWithDebit} cierra=${it.closesAtMonthEnd()} ${label(it)}") }
    println("BUY.opposite() = ${Side.BUY.opposite()}, FROZEN acepta depósitos = ${AccountStatus.FROZEN.acceptsDeposits}")

    println("\n== object / data / value ==")
    println(IdGenerator.next("TRF") + " " + IdGenerator.next("TRF"))
    val p1 = Position("ACME", 10, Money.of("100.10"))
    val p2 = p1.copy(quantity = 15)
    val (ticker, quantity, cost) = p2
    println("$p1 | $p2 | destructuring: $ticker $quantity $cost")
    println("Ticker.parse(\" acme \") = ${Ticker.parse(" acme ")}, inválido = ${Ticker.parse("ACME1")}")
    println("TransferId.of(42) = ${TransferId.of(42)}")
    println("Vector2(1,2) + Vector2(3,4) = ${Vector2(1, 2) + Vector2(3, 4)}, -v = ${-Vector2(1, 2)}")
    val list = CountingList<String>()
    list.add("ACME"); list.add("ORBX"); list.remove("ACME")
    println("CountingList = $list, adds=${list.adds}")
    println("maxOf3 = ${maxOf3("ORBX", "ACME", "NUBE")}")
}
