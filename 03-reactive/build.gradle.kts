// 03-reactive: Reactor, RxJava y los puentes con corrutinas (reactor, rxjava, bridges, backpressure). Proyecto
// autocontenido (sin parent común); se construye solo (./gradlew -p 03-reactive test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 2.4.21, Reactor BOM 2026.0.0 (reactor-core 3.9.0), reactor-kotlin-extensions
// 1.3.3, RxJava 3.1.12, RxKotlin 3.0.1, kotlinx-coroutines 1.11.0, JDK 21. Se repiten en cada sección (sin parent
// común): mantenerlas en sincronía con docs/Versions.md.
plugins {
    kotlin("jvm") version "2.4.21"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Reactor: el BOM alinea reactor-core, reactor-test y reactor-kotlin-extensions
    implementation(platform("io.projectreactor:reactor-bom:2026.0.0"))
    implementation("io.projectreactor:reactor-core")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.3.3")

    // RxJava 3 y sus extensiones de Kotlin
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Corrutinas y los puentes (awaitSingle, asFlow, asFlux, mono { }, rxSingle { }, ReactorContext)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")

    testImplementation(kotlin("test"))
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

// Cada lección tiene su propio Main.kt; se elige con -Plesson=<subpaquete> (por defecto: reactor):
//   ./gradlew -p 03-reactive run -Plesson=bridges
val lesson: String = (findProperty("lesson") as String?) ?: "reactor"

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
