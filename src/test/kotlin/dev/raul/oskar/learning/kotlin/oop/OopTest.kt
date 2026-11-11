package dev.raul.oskar.learning.kotlin.oop

import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OopTest {

    @BeforeTest
    fun setUp() = IdGenerator.reset()

    @Test
    fun `init block validates constructor arguments`() {
        assertFailsWith<IllegalArgumentException> { LockedAccount(" ") }
        assertFailsWith<IllegalArgumentException> {
            SavingsAccount("S", BigDecimal("-1"), BigDecimal.ZERO)
        }
    }

    @Test
    fun `overridden overdraft changes withdraw behaviour`() {
        val savings = SavingsAccount("S", BigDecimal("100"), BigDecimal("0.1"))
        val checking = CheckingAccount("C", BigDecimal("100"), BigDecimal("50"))
        assertFailsWith<IllegalStateException> { savings.withdraw(BigDecimal("101")) }
        checking.withdraw(BigDecimal("150"))
        assertEquals(BigDecimal("-50"), checking.balance)
    }

    @Test
    fun `polymorphic transfer`() {
        val from = CheckingAccount("C", BigDecimal("10"), BigDecimal("100"))
        val to = SavingsAccount("S", BigDecimal.ZERO, BigDecimal("0.1"))
        assertTrue(to.isEmpty)
        transfer(from, to, BigDecimal("60"))
        assertEquals(BigDecimal("60"), to.balance)
        assertFailsWith<UnsupportedOperationException> { transfer(LockedAccount("L"), to, BigDecimal.ONE) }
    }

    @Test
    fun `sealed hierarchy and default methods`() {
        assertEquals(6.0, Rectangle(2.0, 3.0).area())
        assertEquals("Soy rectángulo", Rectangle(1.0, 1.0).describe())
        assertEquals("Soy un triángulo de base 4.0", Triangle(4.0, 1.0).describe())
        assertEquals(16.0, totalArea(listOf(Rectangle(2.0, 3.0), Triangle(4.0, 5.0))))
    }

    @Test
    fun `enum factory in companion`() {
        assertEquals(ShippingMethod.EXPRESS, ShippingMethod.of("EXPRESS"))
        assertNull(ShippingMethod.of(null))
        assertNull(ShippingMethod.of("drone"))
        assertFailsWith<IllegalArgumentException> { ShippingMethod.ofOrThrow("drone") }
        assertEquals(0.35, ShippingMethod.FREIGHT.costFactor())
        assertEquals(1.0, ShippingMethod.EXPRESS.costFactor())
        assertEquals(Direction.NORTH, Direction.WEST.turnRight())
    }

    @Test
    fun `data class equality ignores body properties`() {
        val a = Customer("1", "Ana").apply { visits = 5 }
        val b = Customer("1", "Ana")
        assertEquals(a, b)
        assertNotSame(a, b)
        assertEquals("ana@x.com", a.copy(email = "ana@x.com").email)
    }

    @Test
    fun `object singleton and private constructor factory`() {
        assertEquals("order-1", IdGenerator.next("order"))
        assertEquals("order-2", IdGenerator.next("order"))
        assertEquals("ana@example.com", Email.parse(" ANA@Example.com ")?.value)
        assertNull(Email.parse("sin arroba"))
    }

    @Test
    fun `operators and value classes`() {
        assertEquals("59.98", (Cents(1999) * 3 + Cents(1)).toString())
        assertEquals(Cents(5998), Cents(1999) * 3 + Cents(1))
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
        assertEquals("uva", maxOf3("pera", "uva", "manzana"))
    }
}
