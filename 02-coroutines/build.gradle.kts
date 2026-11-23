// 02-coroutines: corrutinas, Flow y concurrencia (fundamentals … loom). Proyecto autocontenido (sin parent común);
// se construye solo (./gradlew -p 02-coroutines test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 2.4.21, kotlinx-coroutines 1.11.0, Turbine 1.2.1, atomicfu 0.33.0, JDK 21.
// Se repiten en cada sección (sin parent común): mantenerlas en sincronía con docs/Versions.md.
plugins {
    kotlin("jvm") version "2.4.21"
    // atomicfu (lección sync): el plugin reescribe atomic(…) a campos atómicos de la JVM al compilar
    id("org.jetbrains.kotlinx.atomicfu") version "0.33.0"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")

    testImplementation(kotlin("test"))
    // runTest, tiempo virtual y TestDispatcher
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    // flow.test { awaitItem() }
    testImplementation("app.cash.turbine:turbine:1.2.1")
    // El BOM alinea JUnit y el launcher se declara explícito, como en las otras secciones.
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

// Cada lección tiene su propio Main.kt; se elige con -Plesson=<subpaquete> (por defecto: fundamentals):
//   ./gradlew -p 02-coroutines run -Plesson=cancellation
val lesson: String = (findProperty("lesson") as String?) ?: "fundamentals"

application {
    mainClass.set("dev.raul.oskar.learning.kotlin.$lesson.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    workingDir = projectDir
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
