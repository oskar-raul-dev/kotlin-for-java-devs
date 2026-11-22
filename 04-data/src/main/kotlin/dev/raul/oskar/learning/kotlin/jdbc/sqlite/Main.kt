package dev.raul.oskar.learning.kotlin.jdbc.sqlite

import dev.raul.oskar.learning.kotlin.jdbc.runDemo
import java.nio.file.Files
import java.nio.file.Path
import java.sql.DriverManager

// SQLite no necesita servidor: la base es un archivo (o memoria con jdbc:sqlite::memory:).
// ./gradlew run -Plesson=jdbc.sqlite

fun main() {
    val file = Path.of("build/sqlite/learning.db")
    Files.createDirectories(file.parent)
    DriverManager.getConnection("jdbc:sqlite:$file").use { conn ->
        // Las foreign keys de SQLite vienen apagadas en cada conexión: el dialecto las activa (sessionSetup).
        runDemo(conn, SqliteDialect)
    }
    println("archivo: ${file.toAbsolutePath()}  (ábrelo con: sqlite3 ${file})")
}
