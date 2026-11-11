// 01-language: el lenguaje y su stdlib (basics … json). Proyecto autocontenido (sin parent común);
// se construye solo (./gradlew -p 01-language test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 1.9.22, kotlinx-serialization 1.6.2, Kermit 2.0.4, JDK 21.
// La migración a Kotlin 2.x va en un paso aparte (ver Plan-Migracion-Monorepo.md).
plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Serialización y logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("co.touchlab:kermit:2.0.4")

    testImplementation(kotlin("test"))
    // kotlin-test-junit5 1.9.22 trae JUnit 5.6.3 y Gradle 9 ya no inyecta el launcher:
    // sin estas dos líneas, "Failed to load JUnit Platform".
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

// Cada lección tiene su propio Main.kt; se elige con -Plesson=<subpaquete> (por defecto: basics):
//   ./gradlew -p 01-language run -Plesson=strings
val lesson: String = (findProperty("lesson") as String?) ?: "basics"

application {
    mainClass.set("dev.raul.oskar.learning.kotlin.$lesson.MainKt")
}

tasks.named<JavaExec>("run") {
    // Para la lección io: que la consola funcione con ./gradlew run
    standardInput = System.`in`
    workingDir = projectDir
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
