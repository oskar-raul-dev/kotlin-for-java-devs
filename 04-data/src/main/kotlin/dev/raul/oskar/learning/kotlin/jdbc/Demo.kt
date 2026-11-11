package dev.raul.oskar.learning.kotlin.jdbc

import java.sql.Connection
import java.sql.SQLException

/** El mismo recorrido para los tres motores: cada Main solo cambia la conexión y el dialecto. */
fun runDemo(conn: Connection, dialect: Dialect) {
    val meta = conn.metaData
    println("== ${dialect.name}: ${meta.databaseProductName} ${meta.databaseProductVersion} (driver ${meta.driverVersion}) ==")

    val repo = ProductRepository(conn, dialect)
    repo.recreateSchema()

    val laptop = repo.insert(Product("LAP-1", "Laptop 14", Product.money("1200"), 5))
    println("insert -> id generado ${laptop.id}")

    repo.insertAll(
        listOf(
            Product("MOU-1", "Mouse inalámbrico", Product.money("25.50"), 40),
            Product("KEY-1", "Teclado mecánico", Product.money("80"), 10),
        ),
    )
    println("insertAll (batch) -> total ${repo.count()} filas")

    repo.upsert(Product("MOU-1", "Mouse inalámbrico", Product.money("22.90"), 50))
    repo.upsert(Product("MON-1", "Monitor 27", Product.money("310"), 3))
    println("upsert (1 update + 1 insert) -> ${repo.findAll().map { "${it.sku}:${it.price}x${it.stock}" }}")

    println("searchByName(\"mouse\") -> ${repo.searchByName("mouse").map { it.name }}")
    println("valor del inventario -> ${repo.inventoryValue()}")

    repo.moveStock("KEY-1", "LAP-1", 4)
    println("moveStock 4 -> KEY-1=${repo.findBySku("KEY-1")?.stock}, LAP-1=${repo.findBySku("LAP-1")?.stock}")
    try {
        repo.moveStock("KEY-1", "LAP-1", 100)
    } catch (e: SQLException) {
        println("moveStock 100 falla y se revierte (${e::class.simpleName}): KEY-1=${repo.findBySku("KEY-1")?.stock}, LAP-1=${repo.findBySku("LAP-1")?.stock}")
    }

    println("delete MON-1 -> ${repo.delete("MON-1")}, de nuevo -> ${repo.delete("MON-1")}")
}
