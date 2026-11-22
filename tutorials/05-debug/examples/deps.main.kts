// Baja kotlinx-coroutines-debug 1.11.0 y sus dependencias (core, ByteBuddy) a ~/.m2/repository, e imprime el
// classpath para compilar y correr Feed.kt con kotlinc y java. Ver el tutorial debug, sección 5.
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.11.0")

val m2 = System.getProperty("user.home") + "/.m2/repository"
val jars = listOf(
    "org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.11.0/kotlinx-coroutines-core-jvm-1.11.0.jar",
    "org/jetbrains/kotlinx/kotlinx-coroutines-debug/1.11.0/kotlinx-coroutines-debug-1.11.0.jar",
    "net/bytebuddy/byte-buddy/1.17.6/byte-buddy-1.17.6.jar",
    "net/bytebuddy/byte-buddy-agent/1.17.6/byte-buddy-agent-1.17.6.jar",
).map { "$m2/$it" }
jars.filterNot { java.io.File(it).exists() }.forEach { error("falta $it") }
println(jars.joinToString(":"))
