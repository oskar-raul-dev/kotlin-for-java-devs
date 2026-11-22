// Lee la anotación @Metadata de clases compiladas por kotlinc y muestra lo que Java no ve: qué es data class,
// qué propiedades hay, cuáles son nullable, y el nombre Kotlin de las funciones con nombre mutilado.
//   kotlin metadata.main.kts <carpeta de clases> <clase> [<clase>…]
@file:DependsOn("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.21")

import java.io.File
import java.net.URLClassLoader
import kotlin.metadata.isData
import kotlin.metadata.isNullable
import kotlin.metadata.isSuspend
import kotlin.metadata.isValue
import kotlin.metadata.isVar
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.signature
import kotlin.metadata.kind

require(args.size >= 2) { "uso: kotlin metadata.main.kts <carpeta de clases> <clase> [<clase>…]" }
val loader = URLClassLoader(arrayOf(File(args[0]).toURI().toURL()), Metadata::class.java.classLoader)

for (name in args.drop(1)) {
    val metadata = loader.loadClass(name).getAnnotation(Metadata::class.java) ?: error("$name no tiene @Metadata")
    println("== $name  (k=${metadata.kind}, mv=${metadata.metadataVersion.joinToString(".")})")
    when (val km = KotlinClassMetadata.readStrict(metadata)) {
        is KotlinClassMetadata.Class -> {
            val c = km.kmClass
            println("clase: ${c.name}  kind=${c.kind}  data=${c.isData}  value=${c.isValue}")
            c.properties.forEach { p -> println("  ${if (p.isVar) "var" else "val"} ${p.name}: ${p.returnType.classifier}${if (p.returnType.isNullable) "?" else ""}") }
            c.functions.forEach { f -> println("  ${if (f.isSuspend) "suspend " else ""}fun ${f.name}  ->  JVM ${f.signature}") }
        }
        is KotlinClassMetadata.FileFacade -> {
            km.kmPackage.functions.forEach { f -> println("  ${if (f.isSuspend) "suspend " else ""}fun ${f.name}  ->  JVM ${f.signature}") }
        }
        else -> println("otro tipo de metadato: ${km::class.simpleName}")
    }
}
