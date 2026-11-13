// 22-legacy-kotlin19: el último compilador K1 (Kotlin 1.9.22), como estaba la base antes del paso A7.
// Proyecto autocontenido y FUERA del composite de la raíz: tiene su propio wrapper (Gradle 9.2.1) y se corre
// desde su carpeta:
//   cd 22-legacy-kotlin19 && ./gradlew test
// Versiones fijadas a propósito y que NO se actualizan: el plugin de Kotlin 1.9.22 usa APIs de Gradle que
// desaparecen en Gradle 10, así que este track se queda en Gradle 9.x (ver docs/k2-notes.md).
plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // kotlin-test-junit5 1.9.22 trae JUnit 5.6.3 y Gradle 9 ya no inyecta el launcher:
    // sin estas dos líneas, "Failed to load JUnit Platform". Es una de las trampas de este track.
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

// Cada lección tendrá su Main.kt; se elige con -Plesson=<subpaquete> (por defecto: legacy19):
//   ./gradlew run -Plesson=legacy19
val lesson: String = (findProperty("lesson") as String?) ?: "legacy19"

application {
    mainClass.set("dev.raul.oskar.learning.kotlin.$lesson.MainKt")
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
