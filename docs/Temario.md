# Temario: Kotlin para quien viene de Java

Qué enseña el curso, lección por lección: el **mapeo Java → Kotlin**, las **trampas**, la **versión** de cada
construcción, el **caso** (Banco o Mercado) y el **⚡** que la cierra. Cubre las 18 secciones del
[plan](../Plan-Migracion-Monorepo.md#5-secciones-y-lecciones): 5 tutoriales y 83 lecciones.

> **Estado:** en construcción por tandas, según [`Temario-Plan.md`](Temario-Plan.md) (paso B0). Cada lección marcada
> **⬜️ pendiente** se completa en la tanda que indica. El temario anterior de las 14 lecciones originales está en
> [`archive/Temario-v1.md`](archive/Temario-v1.md).

- Fuente de verdad de conteos y de la ruta prioritaria: [`README.md`](../README.md). Casos: [`Casos.md`](Casos.md).
  Versiones fijadas: [`Versions.md`](Versions.md).
- Este documento describe **qué** se enseña; el código, las pruebas y los ejercicios se escriben de B2 en adelante.

**Salto rápido:** [Leyenda](#leyenda) · [Mapa general](#mapa-general) ·
[`tutorials`](#tutorials) · [`01-language`](#01-language) · [`02-coroutines`](#02-coroutines) · [`03-reactive`](#03-reactive) · [`04-data`](#04-data) · [`05-testing`](#05-testing) · [`06-compiler`](#06-compiler) · [`07-design`](#07-design) · [`08-build`](#08-build) · [`09-spring`](#09-spring) · [`10-ktor`](#10-ktor) · [`11-multiplatform`](#11-multiplatform) · [`12-android`](#12-android) · [`20-legacy-kotlin14`](#20-legacy-kotlin14) · [`21-legacy-kotlin16`](#21-legacy-kotlin16) · [`22-legacy-kotlin19`](#22-legacy-kotlin19) · [`25-interview`](#25-interview) · [`26-kotlin-next`](#26-kotlin-next) ·
[🐉 Jefes finales](#-jefes-finales) · [Chuleta Java → Kotlin](#chuleta-java--kotlin) · [Idioms](#idioms-transversales) ·
[Trampas](#trampas-consolidadas) · [Trazabilidad](#trazabilidad)

---

## Leyenda

| Marca | Significado |
|---|---|
| 🟢 🟡 🟠 🔴 | Dificultad de los ejercicios: fácil, medio, difícil, muy difícil |
| ⚡ | **Saiyajin**: miniproyecto que cierra la lección, con un criterio verificable |
| 🐉 | **Jefe final**: proyecto que cruza secciones |
| 🎤 | Pregunta típica de entrevista |
| 🎯 | Prioritario: ruta mínima recomendada |
| `[1.9]` `[2.2]` | Versión de Kotlin en que la construcción es estable, **medida** (tabla en [`Temario-Plan.md`](Temario-Plan.md#referencia-de-versiones-medida)) |
| `[exp N]` / `[dep N]` / `[rm N]` | Experimental, deprecada o retirada en la versión N |
| **Build 0–6** | Complejidad del build: 0 sin build · 1 Gradle simple · 2 con librerías (y Docker) · 3 framework · 4 multiplataforma · 5 herramientas de build · 6 legacy en contenedor |
| ⚠️ | Por verificar al escribir la sección |

---

## Mapa general

| Sección | Tema | Build | Caso | Lecciones | Tanda |
|---|---|---|---|---|---|
| [`tutorials`](#tutorials) | Guías de la terminal y del entorno | 0 | Ambos (ejemplos) | 5 | 1 |
| [`01-language`](#01-language) | El lenguaje y su stdlib desde Java | 1 | Ambos | 16 | 1, 2, 3 |
| [`02-coroutines`](#02-coroutines) | Corrutinas, `Flow` y concurrencia | 1 | Mercado (y Banco en `sync`) | 10 | 4 |
| [`03-reactive`](#03-reactive) | Reactor, RxJava y puentes con corrutinas | 2 | Mercado | 4 | 5 |
| [`04-data`](#04-data) | Bases de datos relacionales | 2 | Banco | 6 | 5 |
| [`05-testing`](#05-testing) | Pruebas | 2 | Ambos | 4 | 6 |
| [`06-compiler`](#06-compiler) | Bytecode, reflexión, KSP y plugins del compilador | 2 | — | 4 | 6 |
| [`07-design`](#07-design) | Idioms, patrones, FP y arquitectura | 1 | Banco (DDD) y Mercado (hexagonal) | 4 | 6 |
| [`08-build`](#08-build) | Gradle, calidad, docs y empaquetado | 5 | — | 4 | 7 |
| [`09-spring`](#09-spring) | Spring Boot y WebFlux | 3 | Banco | 4 | 7 |
| [`10-ktor`](#10-ktor) | Ktor | 3 | Mercado | 3 | 7 |
| [`11-multiplatform`](#11-multiplatform) | Kotlin Multiplatform y Compose | 4 | Ambos | 4 | 8 |
| [`12-android`](#12-android) | Android | 4 (fuera del composite) | Ambos | 7 | 8 |
| [`20-legacy-kotlin14`](#20-legacy-kotlin14) | Legacy: Android clásico y Boot 2.3–2.4 (Kotlin 1.4.32) | 6 | — | 4 | 9 |
| [`21-legacy-kotlin16`](#21-legacy-kotlin16) | Legacy: Spring Boot 2.7 con `javax` (Kotlin 1.6.21) | 6 | Banco (la API de `09-spring` portada) | 3 | 9 |
| [`22-legacy-kotlin19`](#22-legacy-kotlin19) | Legacy: el último K1 y la migración a K2 (Kotlin 1.9.22) | 6 | — | 3 | 9 |
| [`25-interview`](#25-interview) | Simulacros y revisión de PRs | 1 | Ambos | 2 | 9 |
| [`26-kotlin-next`](#26-kotlin-next) | Radar de la siguiente versión | 1 (fuera del composite) | — | 1 | 9 |

**Total:** 88 entradas.

---

## tutorials

Guías de la terminal y del entorno. **Build:** 0 · **Caso:** Ambos (ejemplos).

### T · `install` — Instalar y preparar el entorno

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: tutorial escrito en [`tutorials/install/`](../tutorials/install/README.md). Cubre: SDKMAN, brew, toolchains, IntelliJ.

### T · `cli` — Kotlin desde la línea de comandos

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: tutorial escrito en [`tutorials/cli/`](../tutorials/cli/README.md). Cubre: `kotlinc`, `kotlin`, `-include-runtime`, jar ejecutable.

### T · `repl` — REPL, expresiones y scripts

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: tutorial escrito en [`tutorials/repl/`](../tutorials/repl/README.md). Cubre: REPL, scripts `.main.kts`, Kotlin Notebook.

### T · `decompile` — Decompilar Kotlin

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: tutorial escrito en [`tutorials/decompile/`](../tutorials/decompile/README.md). Cubre: *Show Kotlin Bytecode*, `javap`, CFR, `@Metadata`; ⚡ catálogo "qué genera kotlinc".

### T · `debug` — Depurar Kotlin

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: tutorial escrito en [`tutorials/debug/`](../tutorials/debug/README.md). Cubre: IntelliJ, depurador de corrutinas, `DebugProbes`, `jdb`.

---

## 01-language

El lenguaje y su stdlib desde Java. **Build:** 1 · **Caso:** Ambos.

### 01 · `basics` — Tipos, números, identidad

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `control` — `if`/`when` como expresiones, rangos, bucles

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `strings` — Plantillas, extensiones de `String`, regex

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `oop` — Clases, herencia, `sealed`, enums, objetos

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `io` — Archivos, recursos, consola, logging

> ⬜️ **Pendiente (tanda 1).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `collections` — Solo lectura frente a mutable, estructuras

> ⬜️ **Pendiente (tanda 2).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `functional` — Funciones, lambdas, scope functions, pipelines

> ⬜️ **Pendiente (tanda 2).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `json` — kotlinx-serialization

> ⬜️ **Pendiente (tanda 2).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `datetime` — `java.time` correcto

> ⬜️ **Pendiente (tanda 2).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `exceptions` — Sin checked exceptions, errores como valores

> ⬜️ **Pendiente (tanda 2).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 01 · `generics` — Varianza, `*`, `reified`, `T & Any`, `typealias`

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

### 01 · `inline` — `noinline`, `crossinline`, contratos, `value class`

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

### 01 · `interop` — Platform types, `@Jvm*`, Java y Kotlin en el mismo módulo

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

### 01 · `delegation` — Delegados propios, `by map`, delegación de clase

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

### 01 · `dsl` — `@DslMarker`, builders

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

### 01 · `kotlin2` — Lo de 2.0 a 2.4, con su etiqueta

> ⬜️ **Pendiente (tanda 3).** Estado: nueva. Fuente: plan, sección 5.

---

## 02-coroutines

Corrutinas, `Flow` y concurrencia. **Build:** 1 · **Caso:** Mercado (y Banco en `sync`).

### 02 · `fundamentals` — `suspend`, structured concurrency, `Job`

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `cancellation` — Cooperativa, `withTimeout`, excepciones, `SupervisorJob`

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `dispatchers` — `withContext`, `limitedParallelism`, bloquear hilos

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `channels` — `Channel`, `select`, productores

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `flow` — Operadores, `flowOn`, buffer, conflate

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `hotflows` — `StateFlow`, `SharedFlow`

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `sync` — `Mutex`, `Semaphore`, atomicfu, `@Volatile` y el JMM

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `internals` — CPS, máquina de estados decompilada, `Continuation` a mano

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `testing` — `runTest`, tiempo virtual, Turbine

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

### 02 · `loom` — Corrutinas frente a hilos virtuales

> ⬜️ **Pendiente (tanda 4).** Estado: nueva. Fuente: plan, sección 5.

---

## 03-reactive

Reactor, RxJava y puentes con corrutinas. **Build:** 2 · **Caso:** Mercado.

### 03 · `reactor` — `Mono`/`Flux` desde Kotlin, extensiones

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 03 · `rxjava` — RxJava desde Kotlin

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 03 · `bridges` — `kotlinx-coroutines-reactor`, `awaitSingle`, `asFlow`, `asFlux`

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 03 · `backpressure` — Backpressure en los tres modelos

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

---

## 04-data

Bases de datos relacionales. **Build:** 2 · **Caso:** Banco.

### 04 · `jdbc` — JDBC idiomático, tres motores (SQLite, PostgreSQL, MySQL)

> ⬜️ **Pendiente (tanda 5).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 04 · `jpa` — Hibernate 7 sin Spring

> ⬜️ **Pendiente (tanda 5).** Estado: existe. Fuente: contenido anterior en [`archive/Temario-v1.md`](archive/Temario-v1.md) y el código de la lección.

### 04 · `exposed` — Exposed (DSL y DAO)

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 04 · `jooq` — jOOQ desde Kotlin

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 04 · `r2dbc` — R2DBC con corrutinas

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

### 04 · `sqldelight` — SQLDelight

> ⬜️ **Pendiente (tanda 5).** Estado: nueva. Fuente: plan, sección 5.

---

## 05-testing

Pruebas. **Build:** 2 · **Caso:** Ambos.

### 05 · `kotest` — Estilos, property-based

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 05 · `mockk` — MockK, incluido `coEvery`

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 05 · `assertions` — Kotest, AssertK, power-assert

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 05 · `testcontainers` — Testcontainers

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

---

## 06-compiler

Bytecode, reflexión, KSP y plugins del compilador. **Build:** 2 · **Caso:** —.

### 06 · `bytecode` — Lambdas, `companion`, `value class`, propiedades

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 06 · `reflection` — `KClass`, `KProperty`, `kotlin-reflect` y su costo

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 06 · `ksp` — KSP frente a kapt

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 06 · `compilerplugins` — all-open, no-arg, serialization, power-assert, uno propio con FIR

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

---

## 07-design

Idioms, patrones, FP y arquitectura. **Build:** 1 · **Caso:** Banco (DDD) y Mercado (hexagonal).

### 07 · `idioms` — Effective Kotlin

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 07 · `gof` — Qué patrones desaparecen en Kotlin

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 07 · `fp` — Arrow: `Either`, `Raise`, optics

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

### 07 · `architecture` — Hexagonal + DDD

> ⬜️ **Pendiente (tanda 6).** Estado: nueva. Fuente: plan, sección 5.

---

## 08-build

Gradle, calidad, docs y empaquetado. **Build:** 5 · **Caso:** —.

### 08 · `gradle` — Kotlin DSL, version catalogs, convention plugins en `build-logic`

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 08 · `quality` — detekt, ktlint, Kover

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 08 · `docs` — Dokka

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 08 · `packaging` — Fat jar, jlink, GraalVM native image

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

---

## 09-spring

Spring Boot y WebFlux. **Build:** 3 · **Caso:** Banco.

### 09 · `boot` — Plugin `kotlin-spring`, constructor injection, `@ConfigurationProperties`, validación

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 09 · `springdata` — Spring Data desde Kotlin

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 09 · `webflux` — Corrutinas, `Flow`, `coRouter`, `WebClient`

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 09 · `springtesting` — MockMvc DSL, `WebTestClient`, SpringMockK

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

---

## 10-ktor

Ktor. **Build:** 3 · **Caso:** Mercado.

### 10 · `server` — Routing, plugins, auth, WebSockets

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 10 · `client` — Cliente HTTP de Ktor

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

### 10 · `ktortesting` — Pruebas de Ktor

> ⬜️ **Pendiente (tanda 7).** Estado: nueva. Fuente: plan, sección 5.

---

## 11-multiplatform

Kotlin Multiplatform y Compose. **Build:** 4 · **Caso:** Ambos.

### 11 · `kmp` — `expect`/`actual`, source sets, targets

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 11 · `composemp` — Compose Multiplatform: desktop, wasm, iOS

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 11 · `shared` — Ktor client + SQLDelight + kotlinx-datetime

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 11 · `web` — Kotlin/JS y Wasm

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

---

## 12-android

Android. **Build:** 4 (fuera del composite) · **Caso:** Ambos.

### 12 · `android` — Activity, ciclo de vida

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `compose` — Jetpack Compose

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `architecture` — ViewModel, `StateFlow`, navegación

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `androiddata` — Room, DataStore

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `di` — Hilt frente a Koin

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `background` — WorkManager

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

### 12 · `androidtesting` — Pruebas de Compose, Robolectric

> ⬜️ **Pendiente (tanda 8).** Estado: nueva. Fuente: plan, sección 5.

---

## 20-legacy-kotlin14

Legacy: Android clásico y Boot 2.3–2.4 (Kotlin 1.4.32). **Build:** 6 · **Caso:** —.

### 20 · `language14` — Lo que no existía todavía

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 20 · `coroutines14` — `BroadcastChannel`, `ConflatedBroadcastChannel`

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 20 · `android14` — Synthetics y kapt (solo lectura y demo JVM)

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 20 · `migration14` — De 1.4 a 1.6

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

---

## 21-legacy-kotlin16

Legacy: Spring Boot 2.7 con `javax` (Kotlin 1.6.21). **Build:** 6 · **Caso:** Banco (la API de `09-spring` portada).

### 21 · `language16` — Kotlin 1.6 frente a 2.4

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 21 · `boot27` — Spring Boot 2.7 con `javax`, WebFlux + corrutinas de la época

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 21 · `migration16` — Boot 2.7 → 3, `javax` → `jakarta`

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

---

## 22-legacy-kotlin19

Legacy: el último K1 y la migración a K2 (Kotlin 1.9.22). **Build:** 6 · **Caso:** —.

### 22 · `language19` — Kotlin 1.9 frente a 2.4

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 22 · `k2migration` — Lo que cambió en el paso A7 y por qué (`k2-notes.md`)

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 22 · `compat` — `apiVersion`, `languageVersion`, compatibilidad binaria y `@Metadata`

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

---

## 25-interview

Simulacros y revisión de PRs. **Build:** 1 · **Caso:** Ambos.

### 25 · `livecoding` — Simulacros en Kotlin

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

### 25 · `prreview` — Leer un PR: `!!`, `lateinit` abusado, scope functions anidadas, `data class` mutables, `runCatching`

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

---

## 26-kotlin-next

Radar de la siguiente versión. **Build:** 1 (fuera del composite) · **Caso:** —.

### 26 · `kotlin25` — Lo nuevo de 2.5 cuando salga estable

> ⬜️ **Pendiente (tanda 9).** Estado: nueva. Fuente: plan, sección 5.

---

## 🐉 Jefes finales

> ⬜️ **Pendiente (tanda 9).** Fuente: plan, sección 5: el mini `kotlinx.coroutines` (`02` + `06`) y el producto
> full-stack con el puente de liquidación de [`Casos.md`](Casos.md#el-puente-liquidación) (`04` + `09`/`10` + `11`/`12` + `08`).

---

## Chuleta Java → Kotlin

Lo transversal, en una sola tabla (Java 21 a la izquierda). El detalle está en cada lección.

| Java | Kotlin |
|---|---|
| `final var x = …` / `var x = …` | `val x = …` / `var x = …` |
| `@Nullable String` | `String?` — `?.`, `?:`, `!!`, `let` |
| Package-private por defecto | `public` por defecto; `internal` = visible en el módulo; no hay package-private |
| `static` / clase utilitaria | Funciones de nivel superior, `object`, `companion object` |
| `x instanceof Foo f` | `x is Foo` (smart cast) / `x as? Foo` |
| `switch` con patrones y `when` (21) | `when (x) { is Foo -> … }`; con guardas `is Foo if x.ok ->` `[2.2]` |
| `cond ? a : b` | `if (cond) a else b` |
| `for (;;)` | `for (i in 0..<n)` `[1.9]`, `for (i in n downTo 0 step 2)`, `repeat(n)` |
| Getters/setters | Propiedades (`val`, `var`, `get()`, `private set`) |
| `record` / Lombok | `data class`; `@JvmRecord` si Java necesita un `record` |
| `sealed interface … permits` (17) | `sealed interface` (con target 17+ lleva `PermittedSubclasses`) |
| Builder | Parámetros por defecto + con nombre, o `apply { }` |
| Utilidades `StringUtils.x(s)` | Funciones de extensión `s.x()` |
| `Function`, `Supplier`, `Consumer` | `(A) -> B`, `() -> T`, `(T) -> Unit` |
| Streams + Collectors | Operaciones directas en colecciones / `Sequence` |
| `Optional<T>` | `T?` |
| `try-with-resources` | `use { }` |
| `throws` | Nada (o `@Throws` para Java) |
| `Foo.class` | `Foo::class` (`KClass`) / `Foo::class.java` |
| `String.format`, text blocks | Plantillas `"$x"`, raw strings `"""…"""`; `$$"…"` para un `$` literal `[2.2]` |
| `Objects.equals(a, b)` | `a == b` |
| `CompletableFuture`, ejecutores | Corrutinas: `suspend`, `launch`/`async`, structured concurrency |
| Hilos virtuales (21) | Corrutinas, o hilos virtuales desde Kotlin (lección `02 · loom`) |
| `jshell`, `java Script.java` | Scripts `.main.kts` (el REPL de `kotlinc` no arranca en 2.4) |
| `UUID.randomUUID()`, `Instant.now()` | Igual desde Kotlin; o `Uuid.random()` `[2.4]` y `Clock.System.now()` `[2.3]` de la stdlib |

---

## Idioms transversales

1. **Expresiones por todos lados:** `if`, `when`, `try` y `throw` producen valores; menos variables mutables.
2. **Nulos en el tipo:** `String?` + `?.` + `?:` + retorno temprano (`?: return`). `!!` se justifica o no se usa.
3. **`OrNull` antes que excepciones:** `toIntOrNull`, `firstOrNull`, `getOrNull`, `matchEntire(…) ?: return null`.
4. **Defaults y argumentos con nombre** en vez de sobrecargas y builders.
5. **Inmutabilidad por defecto:** `val`, `List`, `data class` + `copy`.
6. **Modelar con `sealed` + `when` exhaustivo** (órdenes, movimientos, errores, validaciones).
7. **Extensiones para quitar ceremonia** a APIs de Java (`mapRows`, `inTransaction`, `kotlin.io.path`).
8. **Lambdas con receptor** para DSLs y bloques de contexto (`buildString`, `buildJsonObject`, `inTransaction`).
9. **Dependencias de tiempo e I/O inyectadas** (`Clock`, `BufferedReader`, `Appendable`) para probar.
10. **Precondiciones de la stdlib** (`require`/`check`/`error`) en vez de `if … throw`.
11. **Dinero con `BigDecimal` normalizado**, nunca desde `Double` (`Money` de [`Casos.md`](Casos.md#money)).
12. **Concurrencia estructurada:** toda corrutina vive en un *scope* que la cancela; nada de `GlobalScope`.
13. **El `jvmTarget` igual al JDK donde corre:** con target viejo se pierden `typeSwitch`, `makeConcatWithConstants`
    y `PermittedSubclasses` ([`tutorials/decompile`](../tutorials/decompile/README.md)).

---

## Trampas consolidadas

| Trampa | Dónde |
|---|---|
| `split(String)` es literal, no regex | `01 · strings` |
| Solo lectura ≠ inmutable | `01 · collections` |
| `BigDecimal.equals` compara escala; `BigDecimal(0.1)` desde `Double` | `01 · basics` |
| Overflow silencioso de `Int` | `01 · basics` |
| `List.map/filter` son ansiosos | `01 · functional` |
| Propiedades del cuerpo fuera del `equals` de una `data class` | `01 · oop` |
| `by` no delega `toString/equals/hashCode` | `01 · oop`, `01 · delegation` |
| Nullable sin default = clave obligatoria en JSON | `01 · json` |
| `yyyy` vs `uuuu` con `ResolverStyle.STRICT`; el día de mercado no es el día UTC | `01 · datetime` |
| `runCatching` se traga `CancellationException` | `01 · exceptions`, `02 · cancellation` |
| `data class` como entidad JPA | `04 · jpa` |
| `@ManyToOne` EAGER por defecto; N+1 | `04 · jpa` |
| UPDATE masivo no refresca entidades cargadas | `04 · jpa` |
| MySQL gasta `AUTO_INCREMENT` en upserts | `04 · jdbc` |
| El campo de un `lateinit` es `public` | `06 · bytecode`, [`tutorials/decompile`](../tutorials/decompile/README.md) |
| `filter { }` no crea lambda (es `inline`); una lambda que escapa sí (`invokedynamic`) | `01 · inline`, `06 · bytecode` |
| `kotlinc` a mano compila para Java 8; `-include-runtime` mete también `kotlin-reflect` | [`tutorials/cli`](../tutorials/cli/README.md) |
| `-language-version 1.9` ya no se acepta en un `kotlinc` 2.4: no sirve para emular legacy | [`tutorials/cli`](../tutorials/cli/README.md), `22 · compat` |
| El REPL de `kotlinc` no arranca en 2.4 (en 2.1 corría con lenguaje 1.9) | [`tutorials/repl`](../tutorials/repl/README.md) |
| Los *context receivers* `context(Log)` sin nombre no compilan en 2.4 `[rm 2.4]` | `01 · kotlin2` |
| `print` de `jdb` no entiende operadores de Kotlin; `DebugProbes.install()` puede no adjuntarse | [`tutorials/debug`](../tutorials/debug/README.md) |
| Gradle 9 + Kotlin 1.9.22 sin JUnit launcher; el plugin 1.9.22 no llega a Gradle 10 | `22 · language19`, [`docs/LegacyKotlin.md`](LegacyKotlin.md) |
| K2 avisa de lo que K1 dejaba pasar (`!!` tras `assertNotNull`, `java.lang.Long::class`, `==` en arrays) | `22 · k2migration` |

---

## Trazabilidad

> ⬜️ **Pendiente (tanda 10).** Matriz lección → caso → ⚡ → ejercicios, y revisión de consistencia (etiquetas contra
> la referencia medida, enlaces).
