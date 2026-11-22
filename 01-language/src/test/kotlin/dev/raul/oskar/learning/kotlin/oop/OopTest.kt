package dev.raul.oskar.learning.kotlin.oop

import dev.raul.oskar.learning.kotlin.basics.Money
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OopTest {

    @BeforeTest
    fun setUp() = IdGenerator.reset()

    @Test
    fun `init block validates constructor arguments`() {
        assertFailsWith<IllegalArgumentException> { EscrowAccount(" ") }
        assertFailsWith<IllegalArgumentException> {
            SavingsAccount("S", Money.of("-1"), BigDecimal.ZERO)
        }
    }

    @Test
    fun `overridden overdraft changes withdraw behaviour`() {
        val savings = SavingsAccount("S", Money.of("100"), BigDecimal("0.01"))
        val checking = CheckingAccount("C", Money.of("100"), Money.of("50"))
        assertFailsWith<IllegalStateException> { savings.withdraw(Money.of("101")) }
        checking.withdraw(Money.of("150"))
        assertEquals(Money.of("-50"), checking.balance)
        assertEquals(Money.of("1.00"), savings.interest())
    }

    @Test
    fun `polymorphic transfer and account status`() {
        val from = CheckingAccount("C", Money.of("10"), Money.of("100"))
        val to = SavingsAccount("S", Money.zero(), BigDecimal("0.01"))
        assertTrue(to.isEmpty)
        transfer(from, to, Money.of("60"))
        assertEquals(Money.of("60"), to.balance)
        assertFailsWith<UnsupportedOperationException> { transfer(EscrowAccount("E"), to, Money.of("1")) }
        to.freeze()
        to.deposit(Money.of("1"))                                   // congelada: recibe pero no paga
        assertFailsWith<IllegalStateException> { to.withdraw(Money.of("1")) }
    }

    @Test
    fun `sealed hierarchy and default methods`() {
        val limit = LimitOrder("ORD-1", "ACME", Side.BUY, 10, Money.of("100.00"))
        val market = MarketOrder("ORD-2", "ORBX", Side.SELL, 5)
        assertEquals(Money.of("1000.00"), limit.notional())
        assertEquals("orden de ACME", limit.describe())             // método por defecto de la interfaz
        assertEquals("orden a mercado de ORBX", market.describe())  // sobrescrito
        assertEquals("SELL 5 al precio que haya", priceHint(market))
        assertEquals(Money.of("1000.00"), committed(listOf(limit, market)))
        assertFailsWith<IllegalArgumentException> { MarketOrder("ORD-3", "ACME", Side.BUY, 0) }
    }

    @Test
    fun `enum factory in companion and bodies per constant`() {
        assertEquals(AccountType.LIABILITY, AccountType.of("LIABILITY"))
        assertNull(AccountType.of(null))
        assertNull(AccountType.of("equity"))
        assertFailsWith<IllegalArgumentException> { AccountType.ofOrThrow("equity") }
        assertTrue(AccountType.INCOME.closesAtMonthEnd())
        assertFalse(AccountType.ASSET.closesAtMonthEnd())
        assertEquals(Side.SELL, Side.BUY.opposite())
        assertTrue(AccountStatus.FROZEN.acceptsDeposits && !AccountStatus.FROZEN.acceptsWithdrawals)
    }

    @Test
    fun `data class equality ignores body properties`() {
        val a = Position("ACME", 10, Money.of("100.10")).apply { lastPrice = Money.of("101.00") }
        val b = Position("ACME", 10, Money.of("100.10"))
        assertEquals(a, b)
        assertNotSame(a, b)
        assertNull(a.copy(quantity = 15).lastPrice)                 // copy tampoco lleva lo del cuerpo
    }

    @Test
    fun `object singleton and private constructor factory`() {
        assertEquals("TRF-1", IdGenerator.next("TRF"))
        assertEquals("TRF-2", IdGenerator.next("TRF"))
        assertEquals("ACME", Ticker.parse(" acme ")?.symbol)
        assertNull(Ticker.parse("ACME1"))
        assertNull(Ticker.parse("TOOLONG"))
    }

    @Test
    fun `operators and value classes`() {
        assertEquals("TRF-000042", TransferId.of(42).value)
        assertEquals(TransferId("TRF-000042"), TransferId.of(42))
        assertFailsWith<IllegalArgumentException> { TransferId("42") }
        assertEquals(Vector2(4, 6), Vector2(1, 2) + Vector2(3, 4))
        assertEquals(2, Vector2(1, 2)[1])
        assertTrue(Vector2(1, 9) < Vector2(2, 0))
    }

    @Test
    fun `delegation with by and lazy`() {
        val list = CountingList<Int>()
        list.add(1); list.add(2); list.remove(1)
        assertEquals(listOf(2), list)
        assertEquals(2, list.adds)

        val config = Config { mapOf("a" to "1") }
        assertEquals(0, config.loads)
        config.values; config.values
        assertEquals(1, config.loads)
    }

    @Test
    fun `generic with upper bound`() {
        assertEquals(9, maxOf3(3, 9, 1))
        assertEquals("ORBX", maxOf3("ORBX", "ACME", "NUBE"))
    }
}
