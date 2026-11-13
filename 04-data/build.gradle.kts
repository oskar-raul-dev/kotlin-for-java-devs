// 04-data: persistencia (jdbc con SQLite, PostgreSQL y MySQL; jpa con Hibernate 7). Proyecto autocontenido
// (sin parent común); se construye solo (./gradlew -p 04-data test) o desde el agregador de la raíz.
// Versiones fijadas a propósito: Kotlin 2.4.21, Hibernate 7.4, JDK 21. Se repiten en cada sección (sin parent
// común): mantenerlas en sincronía con docs/Versions.md.
plugins {
    kotlin("jvm") version "2.4.21"
    // plugin.jpa = no-arg para @Entity/@Embeddable/@MappedSuperclass (JPA exige constructor vacío)
    kotlin("plugin.jpa") version "2.4.21"
    // all-open: Hibernate necesita clases y propiedades no final para los proxies lazy
    kotlin("plugin.allopen") version "2.4.21"
    application
}

group = "dev.raul.oskar.learning.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // JDBC
    implementation("org.xerial:sqlite-jdbc:3.54.1.0")
    implementation("org.postgresql:postgresql:42.7.14")
    implementation("com.mysql:mysql-connector-j:26.10.0")

    // JPA con Hibernate 7 (jakarta.persistence 3.2) + H2 en memoria para las pruebas
    implementation("org.hibernate.orm:hibernate-core:7.4.15.Final")
    implementation("com.h2database:h2:2.5.252")
    // Hibernate y sqlite-jdbc registran por SLF4J; sin binding imprimen un aviso
    runtimeOnly("org.slf4j:slf4j-simple:2.0.20")

    testImplementation(kotlin("test"))
    // El BOM alinea JUnit (kotlin-test-junit5 2.4.21 trae 5.10.1) y el launcher se declara explícito, como pide
    // Gradle 9. Con Kotlin 1.9.22 (que traía JUnit 5.6.3) faltar cualquiera de las dos daba "Failed to load JUnit Platform".
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
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
