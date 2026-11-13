// 01-language: el lenguaje y su stdlib (basics … json). Proyecto autocontenido (sin parent común);
// se construye solo (./gradlew -p 01-language test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 2.4.21, kotlinx-serialization 1.13.0, Kermit 2.2.0, JDK 21.
// Se repiten en cada sección (sin parent común): mantenerlas en sincronía con docs/Versions.md.
plugins {
    kotlin("jvm") version "2.4.21"
    kotlin("plugin.serialization") version "2.4.21"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Serialización y logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.13.0")
    implementation("co.touchlab:kermit:2.2.0")

    testImplementation(kotlin("test"))
    // El BOM alinea JUnit (kotlin-test-junit5 2.4.21 trae 5.10.1) y el launcher se declara explícito, como pide
    // Gradle 9. Con Kotlin 1.9.22 (que traía JUnit 5.6.3) faltar cualquiera de las dos daba "Failed to load JUnit Platform".
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
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
