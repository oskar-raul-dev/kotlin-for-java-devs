package dev.raul.oskar.learning.kotlin.jdbc

import java.sql.Connection
import java.sql.SQLException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Las mismas pruebas para los tres motores. Cada subclase solo dice cómo conectarse.
 * Si devuelve null (base no disponible), la prueba se marca como SKIPPED, no como fallida.
 */
abstract class ProductRepositoryContract {

    protected abstract val dialect: Dialect
    protected abstract fun openConnection(): Connection?

    private lateinit var conn: Connection
    private lateinit var repo: ProductRepository

    @BeforeTest
    fun setUp() {
        val c = openConnection()
        org.junit.jupiter.api.Assumptions.assumeTrue(c != null, "${dialect.name} no disponible: levanta su docker compose")
        conn = c!!
        repo = ProductRepository(conn, dialect).also { it.recreateSchema() }
    }

    @AfterTest
    fun tearDown() {
        if (::conn.isInitialized) conn.close()
    }

    private fun sample() = listOf(
        Product("A-1", "Alpha mouse", Product.money("10.00"), 5),
        Product("B-1", "Beta keyboard", Product.money("20.50"), 2),
    )

    @Test
    fun `insert returns generated id`() {
        val first = repo.insert(sample()[0])
        val second = repo.insert(sample()[1])
        assertNotNull(first.id)
        assertTrue(second.id!! > first.id!!)
        assertEquals(first, repo.findBySku("A-1"))
    }

    @Test
    fun `batch insert and queries`() {
        repo.insertAll(sample())
        assertEquals(2, repo.count())
        assertEquals(listOf("A-1", "B-1"), repo.findAll().map { it.sku })
        assertEquals(listOf("B-1"), repo.searchByName("KEYB").map { it.sku })
        assertNull(repo.findBySku("nope"))
        assertEquals(Product.money("91.00"), repo.inventoryValue())
    }

    @Test
    fun `sum over empty table is zero not null`() {
        assertEquals(Product.money("0"), repo.inventoryValue())
    }

    @Test
    fun `parameters are not sql injection`() {
        repo.insertAll(sample())
        assertNull(repo.findBySku("x' OR '1'='1"))
        assertEquals(2, repo.count())
    }

    @Test
    fun `unique sku is enforced`() {
        repo.insert(sample()[0])
        assertFailsWith<SQLException> { repo.insert(sample()[0]) }
    }

    @Test
    fun `upsert inserts then updates`() {
        repo.upsert(sample()[0])
        repo.upsert(sample()[0].copy(price = Product.money("12.34"), stock = 9))
        val p = repo.findBySku("A-1")!!
        assertEquals(1, repo.count())
        assertEquals(Product.money("12.34"), p.price)
        assertEquals(9, p.stock)
    }

    @Test
    fun `update and delete report affected rows`() {
        repo.insertAll(sample())
        assertTrue(repo.updatePrice("A-1", Product.money("11")))
        assertFalse(repo.updatePrice("nope", Product.money("11")))
        assertTrue(repo.delete("A-1"))
        assertFalse(repo.delete("A-1"))
    }

    @Test
    fun `transaction commits both updates`() {
        repo.insertAll(sample())
        repo.moveStock("A-1", "B-1", 3)
        assertEquals(2, repo.findBySku("A-1")!!.stock)
        assertEquals(5, repo.findBySku("B-1")!!.stock)
    }

    @Test
    fun `check constraint failure rolls back the whole transaction`() {
        repo.insertAll(sample())
        assertFailsWith<SQLException> { repo.moveStock("A-1", "B-1", 50) }
        assertEquals(5, repo.findBySku("A-1")!!.stock)
        assertEquals(2, repo.findBySku("B-1")!!.stock)   // el primer UPDATE también se revirtió
        assertTrue(conn.autoCommit)
    }
}
