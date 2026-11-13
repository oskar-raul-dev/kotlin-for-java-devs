# Kotlin legacy: de 1.0 a 2.x y cómo correr los tracks

Guía de los tracks legacy (`20-legacy-kotlin14`, `21-legacy-kotlin16`, `22-legacy-kotlin19`): qué cambió entre
versiones de Kotlin, por qué se eligieron esos tres cortes y cómo se corre cada uno, en contenedor o en nativo.
Escrita el **13 de noviembre de 2026**; lo marcado con ⚠️ se verifica al desarrollar el track (fase B5).

**Salto rápido:** [Por qué](#por-qué-tracks-con-el-compilador-real) · [Versiones](#qué-trajo-cada-versión) ·
[Tracks](#los-tres-tracks) · [Correr](#cómo-se-corren) · [Trampas](#trampas-de-los-builds-viejos) ·
[Pendiente](#pendiente)

## Por qué tracks con el compilador real

Mucho código Kotlin en producción no está en la última versión: apps Android de 2019–2021, servicios Spring Boot 2.x
que nadie migró, librerías internas compiladas con K1. Leerlo y migrarlo exige saber qué no existía todavía y qué se
comportaba distinto.

Un compilador moderno con `-language-version` no sirve para eso: K2 acepta pocas versiones hacia atrás ⚠️, y no
reproduce el bytecode, el backend JVM viejo ni el plugin de Gradle de la época. Por eso cada track es un proyecto
aparte, **fuera del composite**, con su propio wrapper de Gradle, su versión de Kotlin y su JDK.

## Qué trajo cada versión

| Versión | Año | Qué trajo o quitó | ¿Aparece en código vivo? |
|---|---|---|---|
| 1.0–1.2 | 2016–17 | Lenguaje inicial, corrutinas experimentales | Casi no: se migró todo |
| 1.3 | 2018 | Corrutinas estables, `Result`, contratos e inline classes experimentales | Poco; quedó absorbido por 1.4 |
| **1.4** | 2020 | `fun interface`, explicit API mode, nueva inferencia, coma final; **jvmTarget por defecto 1.6**, backend JVM viejo, `kotlin-android-extensions` (synthetics) y kapt | **Sí**: Android de 2019–2021 y servicios Spring Boot 2.3–2.4 |
| 1.5 | 2021 | Backend JVM IR por defecto, `value class` y `sealed interface` estables, `@JvmRecord`, `jvmToolchain` (1.5.30) | Transición |
| **1.6–1.8** | 2021–22 | `when` exhaustivo obligatorio, `Duration` estable, `T & Any`, opt-in estable, inferencia de builders | **Sí, mucho**: Spring Boot 2.7 (javax, Kotlin 1.6.21) |
| **1.9** | 2023 | `data object`, `entries`, `..<`; último compilador K1 | **Sí**: el último paso antes de K2 |
| 2.0–2.3 | 2024–25 | K2, smart casts mejorados, lambdas con `invokedynamic`, guard conditions, `break`/`continue` no locales, `$$` en plantillas | Base del curso, con etiquetas `[2.0]`, `[2.2]`… |
| 2.4 | 2026 | Context parameters y explicit backing fields estables ⚠️ | **Base del curso** (2.4.21) |

Lo que cambió de verdad al pasar este repo de 1.9.22 a 2.4.21 (avisos nuevos de K2, `toString()` de las lambdas,
opt-ins que desaparecieron) está medido en [`22-legacy-kotlin19/docs/k2-notes.md`](../22-legacy-kotlin19/docs/k2-notes.md).

## Los tres tracks

| Track | Kotlin | Representa | Gradle | JDK | Contenedor | Estado |
|---|---|---|---|---|---|---|
| `20-legacy-kotlin14` | 1.4.32 | Android clásico y Boot 2.3–2.4 | 6.8.x ⚠️ | 8 y 11 | `k14` (Temurin 11) | Fase B5 |
| `21-legacy-kotlin16` | 1.6.21 | Spring Boot 2.7 con `javax` | 7.6.x ⚠️ | 11 y 17 | `k16` (Temurin 17) | Fase B5 |
| `22-legacy-kotlin19` | 1.9.22 | Último K1 | 9.2.1 | 21 | `k19` (Temurin 21) | ✅ Build y prueba de humo (A6), contenedor (A8) |

Fuera, a propósito: 1.0–1.3 (se cuentan en la tabla de arriba) y 1.5, 1.7 y 1.8 (los cubren los tracks vecinos con
etiquetas de versión; un track por versión menor sería repetir casi todo).

## Cómo se corren

Ninguno entra en `./gradlew test` de la raíz. Hay dos caminos y dan el mismo resultado.

**En contenedor** (no hace falta tener el JDK viejo instalado):

```bash
./scripts/legacy.sh 19                       # ./gradlew test en 22-legacy-kotlin19, con JDK 21
./scripts/legacy.sh 19 run --console=plain   # su Main
./scripts/legacy.sh 19 shell                 # bash dentro del contenedor
```

Imágenes, digests, caché de Gradle y detalles en [`docker/legacy-kotlin/README.md`](../docker/legacy-kotlin/README.md).

**En nativo** (cada track trae su `.sdkmanrc`):

```bash
cd 22-legacy-kotlin19
sdk env                                      # el JDK del track (con SDKMAN)
./gradlew test                               # con el wrapper propio del track, no el de la raíz
```

`./gradlew -p 22-legacy-kotlin19 test` desde la raíz también funciona, pero usa el Gradle 9.9.1 de la raíz en lugar
del 9.2.1 del track, con avisos de deprecación del plugin 1.9.22.

**En IntelliJ:** abrir la carpeta del track como **proyecto aparte** (no se importa con el composite de la raíz).

## Trampas de los builds viejos

- **Gradle 10 y el plugin 1.9.22.** Corre en Gradle 9.9 con deprecaciones que serán errores en Gradle 10: el track 19
  se queda en Gradle 9.x.
- **JUnit 5 con Kotlin 1.9.22 en Gradle 9.** `kotlin-test-junit5` 1.9.22 trae JUnit 5.6.3 y Gradle 9 ya no inyecta el
  launcher: sin `platform("org.junit:junit-bom:5.10.3")` y `junit-platform-launcher`, *Failed to load JUnit Platform*.
- **`jvmToolchain` llegó en 1.5.30.** El track 14 no puede pedirle a Gradle un JDK: el JDK lo pone el contenedor o
  SDKMAN, y no es opcional ⚠️.
- **`kotlin-test-junit5` para 1.4** ⚠️: si no existe, el track 14 usa JUnit 4.
- **Gradle viejo en IntelliJ actual** ⚠️: si IntelliJ no importa bien Gradle 6.8, el track 14 se trabaja por CLI y
  contenedor.

## Pendiente

- [ ] Track 14: Gradle exacto para KGP 1.4.32 (6.8.x), JUnit 4 o 5, y prueba en `k14` (Temurin 11 en arm64).
- [ ] Track 16: Gradle 7.6.x, Spring Boot 2.7 con `javax`, y acceso a PostgreSQL y MySQL desde `k16`.
- [ ] Lecciones del track 19 (`language19`, `k2migration`, `compat`) a partir de `k2-notes.md`.
- [ ] Probar `scripts/legacy.sh` en Linux (permisos de `build/` y del volumen `gradle-home`).
