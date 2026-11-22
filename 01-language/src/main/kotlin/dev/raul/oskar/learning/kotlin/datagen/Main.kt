package dev.raul.oskar.learning.kotlin.datagen

import kotlin.io.path.Path
import kotlin.io.path.absolute

// Regenera los datos sintéticos de los casos Banco y Mercado:
//   ./gradlew -p 01-language run -Plesson=datagen
// `run` usa el directorio de la sección como directorio de trabajo, así que la ruta relativa cae en
// 01-language/src/main/resources/data. DatagenTest falla si los archivos y el generador no coinciden.

fun main(args: Array<String>) {
    val dir = Path(args.firstOrNull() ?: "src/main/resources/data")
    writeDatasets(dir).forEach { (name, bytes) -> println("%-22s %,9d bytes".format(name, bytes)) }
    println("-> ${dir.absolute().normalize()}")
}
