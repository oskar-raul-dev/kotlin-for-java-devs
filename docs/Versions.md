# Versiones: Kotlin de 1.4 a 2.5

Versiones fijadas en el curso y de dónde salen. Revisada el **13 de noviembre de 2026** contra Maven Central y
services.gradle.org; lo marcado con ⚠️ está por verificar al desarrollar.

**Salto rápido:** [Base](#versiones-fijadas-en-la-base) · [Por sección](#por-sección) ·
[Por qué estas](#por-qué-estas-versiones) · [Tracks legacy](#tracks-legacy) · [Cómo actualizar](#cómo-actualizar)

## Versiones fijadas en la base

| Pieza | Versión | Notas |
|---|---|---|
| Kotlin (plugins `jvm`, `serialization`, `jpa`, `allopen`) | **2.4.21** | Última estable. 2.5.0 está en RC; se espera para diciembre de 2026 |
| JDK (toolchain) | 21 | Zulu 21.0.8 en la máquina (`.sdkmanrc`) |
| Kotlin de línea de comandos | 2.4.21 | Para los tutoriales (`kotlinc`, `kotlin`, scripts); `.sdkmanrc` de la raíz. Gradle no lo usa |
| Herramientas de los tutoriales | — | CFR 0.152, `kotlin-metadata-jvm` 2.4.21, kotlinx-coroutines (core y debug) 1.11.0 con ByteBuddy 1.17.6; se bajan solas al correr cada `check.sh` |
| Gradle (wrapper) | **9.9.1** | Un solo wrapper en la raíz para todo el composite |
| kotlinx-serialization-json | 1.13.0 | `explicitNulls` y `prettyPrintIndent` ya son estables (en 1.6.2 pedían `@OptIn`) |
| Kermit (logging) | 2.2.0 | Compilado con Kotlin 2.3 |
| JUnit (BOM) | 5.14.4 | Última de la línea 5; `kotlin-test-junit5` 2.4.21 trae 5.10.1 y el BOM la sube |

## Por sección

| Sección | Librerías |
|---|---|
| `01-language` | kotlinx-serialization-json 1.13.0, Kermit 2.2.0 |
| `04-data` | sqlite-jdbc 3.54.1.0, PostgreSQL JDBC 42.7.14, mysql-connector-j 26.10.0, Hibernate ORM 7.4.15.Final, H2 2.5.252, slf4j-simple 2.0.20 |

Bases de datos en Docker: PostgreSQL 17.2 y MySQL 8.4.3 (sin cambios en A7).

## Por qué estas versiones

- **JUnit 5 y no 6.** JUnit 6.1.3 ya existe, pero `kotlin-test-junit5` está hecho para la línea 5. El paso a JUnit 6 se
  ve en `05-testing`.
- **Hibernate 7 y no 6.6.** El salto 6.6.3 → 7.4.15 (JPA 3.2) no rompió nada en `jpa`: mismas pruebas y demos. Solo cambió
  el texto de `LazyInitializationException` (`Cannot lazily initialize collection of role '…' with key '1' (no session)`).
- **mysql-connector-j 26.x.** El conector cambió de numeración después de 9.7.0 (9.7.0 → 26.7.0 → 26.10.0). Funciona
  contra MySQL 8.4.3 sin cambios.

## Tracks legacy

| Track | Kotlin | Gradle | JDK | Estado |
|---|---|---|---|---|
| `20-legacy-kotlin14` | 1.4.32 | 6.8.x ⚠️ | 8 y 11 | Fase B5 |
| `21-legacy-kotlin16` | 1.6.21 | 7.6.x ⚠️ | 11 y 17 | Fase B5 |
| `22-legacy-kotlin19` | 1.9.22 | **9.2.1** (wrapper propio; no llega a 10) | 21 | Build y prueba de humo listos (A6); notas en [`22-legacy-kotlin19/docs/k2-notes.md`](../22-legacy-kotlin19/docs/k2-notes.md) |

Contenedores (`docker/legacy-kotlin/compose.yaml`, fijados por digest): `k14` Temurin 11.0.33, `k16` Temurin 17.0.21,
`k19` Temurin 21.0.13, todos sobre Ubuntu 26.04 y con variante `arm64`.

Los tracks legacy están **fuera del composite** y traen su propio wrapper y `.sdkmanrc`: se corren desde su carpeta
(`cd 22-legacy-kotlin19 && ./gradlew test`). El plugin de Gradle de Kotlin 1.9.22 corre en Gradle 9.9.1 con avisos de
deprecación que serán errores en Gradle 10, así que el track 1.9 se queda en Gradle 9.2.1 (el que usaba la base antes
de A7). Su BOM de JUnit también queda en 5.10.3.

## Cómo actualizar

1. Consultar la última estable en Maven Central (`maven-metadata.xml` del artefacto) y en
   `https://services.gradle.org/versions/current`.
2. Cambiar la versión en **cada** `build.gradle.kts` de sección (no hay parent común) y en esta tabla.
3. `./gradlew clean test` con PostgreSQL y MySQL arriba: mismas pruebas, sin SKIPPED.
4. Correr los `Main` de las lecciones y comparar la salida con la versión anterior.
5. Actualizar README ("Versiones"), CLAUDE.md y `CHECKLIST.md` (bitácora) en el mismo cambio.

## Fuentes

- Maven Central: `org.jetbrains.kotlin:kotlin-gradle-plugin`, `org.jetbrains.kotlinx:kotlinx-serialization-json`,
  `co.touchlab:kermit`, `org.hibernate.orm:hibernate-core`, `org.junit:junit-bom` y los drivers JDBC.
- Gradle: `https://services.gradle.org/versions/current` (9.9.1, 10 de noviembre de 2026).
