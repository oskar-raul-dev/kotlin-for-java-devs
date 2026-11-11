// 04-data: persistencia (jdbc con SQLite, PostgreSQL y MySQL; jpa con Hibernate 6). Proyecto autocontenido
// (sin parent común); se construye solo (./gradlew -p 04-data test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 1.9.22, JDK 21. La migración a Kotlin 2.x va en un paso aparte.
plugins {
    kotlin("jvm") version "1.9.22"
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

// Cada lección tiene su propio Main.kt; se elige con -Plesson=<subpaquete> (por defecto: jdbc.sqlite):
//   ./gradlew -p 04-data run -Plesson=jdbc.postgres
//   ./gradlew -p 04-data run -Plesson=jpa --args=postgres
val lesson: String = (findProperty("lesson") as String?) ?: "jdbc.sqlite"

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
