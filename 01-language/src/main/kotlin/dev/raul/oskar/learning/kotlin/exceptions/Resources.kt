package dev.raul.oskar.learning.kotlin.exceptions

// use {} es el try-with-resources de Kotlin: cierra aunque falle, y si close() también lanza,
// esa segunda excepción queda como "suppressed" de la primera (igual que en Java).

class FakeResource(
    val name: String,
    private val log: MutableList<String>,
    private val failOnClose: Boolean = false,
) : AutoCloseable {
    init {
        log += "open $name"
    }

    fun work(fail: Boolean) {
        log += "work $name"
        if (fail) throw IllegalStateException("falla en work de $name")
    }

    override fun close() {
        log += "close $name"
        if (failOnClose) throw IllegalStateException("falla en close de $name")
    }
}

/** Dos recursos anidados: se cierran en orden inverso, como en Java. */
fun useTwo(log: MutableList<String>, failWork: Boolean = false, failClose: Boolean = false) {
    FakeResource("a", log).use { a ->
        FakeResource("b", log, failOnClose = failClose).use { b ->
            a.work(fail = false)
            b.work(fail = failWork)
        }
    }
}

/** Interop con Java: sin @Throws, Java no sabe que el método lanza una checked y no puede capturarla. */
@Throws(java.io.IOException::class)
fun loadConfigForJava(path: String): String {
    if (!path.endsWith(".json")) throw java.io.IOException("no es json: $path")
    return "{}"
}
