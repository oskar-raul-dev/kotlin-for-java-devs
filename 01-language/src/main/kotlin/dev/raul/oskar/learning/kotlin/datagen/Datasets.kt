package dev.raul.oskar.learning.kotlin.datagen

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

/** Semilla fija: los archivos se regeneran idénticos byte a byte. Cambiarla cambia todos los datos. */
const val DATA_SEED = 20261026L

/** Cada archivo de src/main/resources/data con la función que lo genera. */
val DATASETS: Map<String, (Long) -> String> = linkedMapOf(
    "ticks.json" to ::ticksJson,
    "bank-statement.csv" to ::statementCsv,
    "ledger-movements.csv" to ::ledgerCsv,
    "fx-rates.json" to ::fxRatesJson,
)

/** Escribe todos los archivos en [dir] y devuelve cuántos bytes tiene cada uno. */
fun writeDatasets(dir: Path, seed: Long = DATA_SEED): Map<String, Int> {
    dir.createDirectories()
    return DATASETS.mapValues { (name, generate) ->
        val text = generate(seed)
        dir.resolve(name).writeText(text)
        text.toByteArray().size
    }
}
