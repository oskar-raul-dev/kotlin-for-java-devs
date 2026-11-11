// Versiones fijadas a propósito (la migración a Kotlin 2.x va aparte):
// Kotlin 1.9.22, kotlinx-serialization 1.6.2, Kermit 2.0.4, JDK 21, Gradle 9.2.1.
plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    // plugin.jpa = no-arg para @Entity/@Embeddable/@MappedSuperclass (JPA exige constructor vacío)
    kotlin("plugin.jpa") version "1.9.22"
    // all-open: Hibernate necesita clases y propiedades no final para los proxies lazy
    kotlin("plugin.allopen") version "1.9.22"
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

    // JDBC
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.mysql:mysql-connector-j:9.1.0")

    // JPA con Hibernate 6 (jakarta.persistence) + H2 en memoria para las pruebas
    implementation("org.hibernate.orm:hibernate-core:6.6.3.Final")
    implementation("com.h2database:h2:2.3.232")
    // Hibernate y sqlite-jdbc registran por SLF4J; sin binding imprimen un aviso
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
    // kotlin-test-junit5 1.9.22 trae JUnit 5.6.3 y Gradle 9 ya no inyecta el launcher:
    // sin estas dos líneas, "Failed to load JUnit Platform".
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// Cada lección tiene su propio Main.kt; se elige con -Plesson=<subpaquete>:
//   ./gradlew run -Plesson=basics
//   ./gradlew run -Plesson=jdbc.sqlite
val lesson: String = (findProperty("lesson") as String?) ?: "basics"

application {
    mainClass.set("dev.raul.oskar.learning.kotlin.$lesson.MainKt")
}

tasks.named<JavaExec>("run") {
    // Para la lección io: que la consola funcione con ./gradlew run
    standardInput = System.`in`
    workingDir = rootDir
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
