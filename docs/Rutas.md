# Rutas de estudio

Recorridos por el curso según el objetivo: qué lecciones hacer, en qué orden y qué ejercicios de cada una. No hace
falta hacer el curso entero: elige una ruta, y cuando termines sigue por la que la continúa.

- El contenido de cada lección está en [`Temario.md`](Temario.md); las lecciones con código tienen su guía en
  [`lessons/`](lessons/README.md). Qué existe hoy y en qué fase llega lo demás: [`CHECKLIST.md`](../CHECKLIST.md#fase-b).
- La **ruta corta** (los ejercicios 🎯) vive en el [README](../README.md#ruta-prioritaria); aquí solo se ubica entre las demás.
- Las lecciones marcadas **(Bn)** todavía no existen: llegan en esa fase. Una ruta se puede empezar igual con lo que
  ya está.

**Salto rápido:** [Cómo leer una ruta](#cómo-leer-una-ruta) · [Mapa](#mapa) · [1 · Corta](#1--corta-crash) ·
[2 · Lenguaje](#2--lenguaje-completo) · [3 · Backend sin Spring](#3--backend-sin-spring-boot-ktor) ·
[4 · Spring reactivo](#4--spring-reactivo) · [5 · Spring MVC](#5--spring-mvc-clásico) · [6 · Móvil](#6--móvil-android-y-multiplataforma) ·
[7 · Concurrencia](#7--concurrencia-a-fondo) · [8 · Legacy](#8--mantener-código-legacy) · [9 · Entrevista](#9--entrevista) ·
[10 · Completa](#10--completa-full)

## Cómo leer una ruta

En cada paso se dice qué hacer en la lección con estas medidas, de menos a más:

| Medida | Qué incluye | Cuánto es, en una lección de 40 + ⚡ |
|---|---|---|
| **🎯** | Solo los ejercicios prioritarios (la ruta corta) | De 0 a 6 |
| **Lectura** | La guía de la lección y su `Main.kt`, sin ejercicios | — |
| **Núcleo** | Los 🟢 y 🟡, más todos los 🎤 | Unos 28 |
| **Núcleo + ⚡** | Lo anterior y el miniproyecto que la cierra | Unos 29 |
| **Todo** | Los 40 y el ⚡ | 41 |

Las lecciones de un paso se hacen en el orden en que aparecen. En `jdbc`, los ejercicios marcados con un motor
(`[PG]`, `[MySQL]`) se pueden dejar para cuando se necesite ese motor.

## Mapa

| Ruta | Para quién | Secciones | Hoy |
|---|---|---|---|
| [1 · Corta](#1--corta-crash) | Quien necesita leer y escribir Kotlin ya | `tutorials`, `01-language` | ✅ completa |
| [2 · Lenguaje completo](#2--lenguaje-completo) | La base de todas las demás | `tutorials`, `01-language` | 🟡 10 de 16 lecciones |
| [3 · Backend sin Spring Boot](#3--backend-sin-spring-boot-ktor) | Servicios con Ktor y librerías sueltas | `01`, `02`, `04`, `05`, `10`, `07`, `08` | 🟡 lenguaje, JDBC y 4 de 5 de corrutinas |
| [4 · Spring reactivo](#4--spring-reactivo) | WebFlux con corrutinas y R2DBC | `01`, `02`, `03`, `04`, `05`, `09`, `13` | 🟡 lenguaje, JDBC y 5 de 6 de corrutinas |
| [5 · Spring MVC clásico](#5--spring-mvc-clásico) | Spring Boot bloqueante con JPA | `01`, `04`, `05`, `09`, `13` | 🟡 lenguaje, JDBC y JPA |
| [6 · Móvil](#6--móvil-android-y-multiplataforma) | Android y Kotlin Multiplatform | `01`, `02`, `04`, `05`, `11`, `12` | 🟡 lenguaje y 5 de 6 de corrutinas |
| [7 · Concurrencia a fondo](#7--concurrencia-a-fondo) | Corrutinas, `Flow`, reactivo y el JMM | `01`, `02`, `03`, `06` | 🟡 lenguaje y 6 de 10 de corrutinas |
| [8 · Mantener código legacy](#8--mantener-código-legacy) | Proyectos en Kotlin 1.4 a 1.9 o con Java mezclado | `01`, `06`, `20`–`22` | 🟡 lenguaje y track 19 (humo) |
| [9 · Entrevista](#9--entrevista) | Preparar una entrevista de Kotlin | `01`, `02`, `04`, `07`, `25` | 🟡 los 🎤 de 18 lecciones |
| [10 · Completa](#10--completa-full) | Todo el curso | Todas | 🟡 B3 en curso (tanda 2 de 4) |

Las rutas 3 a 6 comparten el [bloque común](#bloque-común-de-lenguaje); la 2 lo contiene entero.

---

## 1 · Corta (crash)

Lo que se puede hacer **hoy** de punta a punta: la ruta prioritaria del curso desde su primera versión (43
ejercicios 🎯, 15 de ellos ★), ahora sobre los casos Banco y Mercado. Lleva el feed del Mercado de leer el JSON a
sacar estadísticas, y termina con el manejo de errores de una transferencia del Banco.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [`tutorials/01-install`](../tutorials/01-install/) y [`02-cli`](../tutorials/02-cli/) | Hasta correr una prueba y un `Main` |
| 2 | Los 15 ejercicios ★ de la [ruta prioritaria](../README.md#ruta-prioritaria), en el orden de sus bloques | ★ |
| 3 | El resto de los 43 🎯, bloque por bloque | 🎯 |
| 4 | Las guías de las lecciones que tocaste: `json`, `io`, `oop`, `basics`, `strings`, `datetime`, `control`, `functional`, `collections`, `exceptions` | Lectura |

**Al terminar:** lees Kotlin idiomático, conoces las trampas de nulos, `split`, fechas y `BigDecimal`, y sabes
revisar el manejo de errores de un PR. **Sigue con:** la [ruta 2](#2--lenguaje-completo).

## 2 · Lenguaje completo

El lenguaje y su stdlib a fondo; es la base de todas las demás rutas.

| Paso | Qué | Medida |
|---|---|---|
| 1 | Los 5 tutoriales (`01-install` … `05-debug`) | Todos sus ejercicios |
| 2 | `basics`, `control`, `strings` | Núcleo + ⚡ |
| 3 | `oop`, `collections`, `functional` | Núcleo + ⚡ |
| 4 | `io`, `json`, `datetime`, `exceptions` | Núcleo + ⚡ |
| 5 | `generics`, `inline`, `interop` **(B4)** | Núcleo + ⚡ |
| 6 | `delegation`, `dsl`, `kotlin2` **(B4)** | Núcleo + ⚡ |

**Al terminar:** puedes escribir una librería en Kotlin y llamarla desde Java. **Sigue con:** la ruta de tu
objetivo (3 a 9).

### Bloque común de lenguaje

Lo mínimo del lenguaje para las rutas 3 a 6, si no hiciste la 2: `basics`, `control`, `strings`, `oop`,
`collections`, `functional` y `exceptions` en **núcleo**, más `generics` y `interop` **(B4)** en **núcleo**. Con la
ruta 1 hecha, se puede bajar a 🟢 y 🎤.

## 3 · Backend sin Spring Boot (Ktor)

Un servicio HTTP con Ktor, corrutinas y acceso a datos con librerías de Kotlin, sin el contenedor de Spring.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Bloque común](#bloque-común-de-lenguaje), más `json` y `datetime` | Núcleo |
| 2 | `02 · fundamentals`, `cancellation`, `dispatchers`, `flow`; `testing` **(B3)** | Núcleo |
| 3 | `04 · jdbc` | Núcleo + ⚡ (el ledger concurrente) |
| 4 | `04 · exposed` y `jooq` **(B8)** | Núcleo + ⚡ de una de las dos |
| 5 | `05 · kotest`, `mockk`, `testcontainers` **(B8)** | Núcleo |
| 6 | `10 · server`, `client`, `ktortesting` **(B6)** | Núcleo + ⚡ |
| 7 | `07 · architecture` (hexagonal sobre el Mercado) **(B9)** | Núcleo |
| 8 | `08 · gradle` y `packaging` (fat jar, GraalVM) **(B9)** | Núcleo |

**Al terminar:** un servicio del Mercado con Ktor, probado con contenedores y empaquetado. **Sigue con:** el 🐉 2
(producto full-stack) con Ktor de backend.

## 4 · Spring reactivo

Spring Boot con WebFlux, corrutinas en los controladores, `Flow` de punta a punta y R2DBC.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Bloque común](#bloque-común-de-lenguaje), más `json` | Núcleo |
| 2 | `02 · fundamentals`, `cancellation`, `dispatchers`, `flow`, `hotflows`; `testing` **(B3)** | Núcleo |
| 3 | `03 · reactor`, `bridges`, `backpressure` **(B3)** | Núcleo |
| 4 | `04 · jdbc` (para entender lo que R2DBC cambia) | 🟢 y 🎤 |
| 5 | `04 · r2dbc` **(B6)** | Núcleo + ⚡ |
| 6 | `05 · mockk` y `testcontainers` **(B8)** | Núcleo |
| 7 | `09 · boot`, `webflux`, `springtesting` **(B6)** | Núcleo + ⚡ de `webflux` |
| 8 | `13-spring-api`, sabor `reactive` **(B6′)** | Todo |

**Al terminar:** la API de transferencias del Banco en WebFlux, con seguridad, caché asíncrona y pruebas con
contenedores. **Sigue con:** la [ruta 7](#7--concurrencia-a-fondo) o el sabor `mvc` para comparar.

## 5 · Spring MVC clásico

Spring Boot bloqueante con JPA: lo más común en servicios que vienen de Java.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Bloque común](#bloque-común-de-lenguaje), más `json` y `datetime` | Núcleo |
| 2 | `04 · jdbc` y `jpa` | Núcleo + ⚡ de `jpa` |
| 3 | `05 · mockk`, `assertions`, `testcontainers` **(B8)** | Núcleo |
| 4 | `09 · boot`, `springdata`, `springtesting` **(B6)** | Núcleo + ⚡ |
| 5 | `13-spring-api`, sabor `mvc` **(B6′)** | Todo |
| 6 | `08 · quality` (detekt, ktlint, Kover) **(B9)** | Núcleo |

**Al terminar:** la API de transferencias en Spring MVC con JPA, EhCache y pruebas con contenedores. **Sigue con:**
la [ruta 4](#4--spring-reactivo) para el sabor reactivo, o la [ruta 8](#8--mantener-código-legacy) si el proyecto
real está en Boot 2.

## 6 · Móvil (Android y multiplataforma)

Android con Compose y una capa compartida con Kotlin Multiplatform.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Bloque común](#bloque-común-de-lenguaje), más `json`, `datetime`, `delegation` y `dsl` **(B4)** | Núcleo |
| 2 | `02 · fundamentals`, `cancellation`, `dispatchers`, `flow`, `hotflows`; `testing` **(B3)** | Núcleo |
| 3 | `04 · sqldelight` **(B8)** | Núcleo |
| 4 | `05 · kotest` y `mockk` **(B8)** | 🟢 y 🎤 |
| 5 | `11 · kmp`, `shared`, `composemp` **(B7)** | Núcleo + ⚡ |
| 6 | `12 · android`, `compose`, `architecture`, `androiddata`, `di`, `background`, `androidtesting` **(B7)** | Núcleo + ⚡ de `architecture` |
| 7 | `11 · web` (Kotlin/JS y Wasm) **(B7)** | Opcional |

**Al terminar:** una app de los casos en Android y desktop, con la capa de datos compartida y el estado en `StateFlow`. **Sigue con:** el 🐉 2 con el cliente móvil.

## 7 · Concurrencia a fondo

Para quien escribe código concurrente: corrutinas por dentro, reactivo, el modelo de memoria y los hilos virtuales.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Ruta 2](#2--lenguaje-completo) hasta `inline` | Núcleo |
| 2 | Las 10 lecciones de `02-coroutines`, en orden: `fundamentals`, `cancellation`, `dispatchers`, `channels`, `flow`, `hotflows`; el resto **(B3)** | Todo |
| 3 | Las 4 lecciones de `03-reactive` **(B3)** | Núcleo + ⚡ |
| 4 | `06 · bytecode` (la máquina de estados decompilada) **(B8)** | Núcleo |
| 5 | `04 · jdbc`, ejercicios de concurrencia (16, 17, 18, 37 y el ⚡) | Esos |

**Al terminar:** el 🐉 1 (un mini `kotlinx.coroutines`) está a tu alcance.

## 8 · Mantener código legacy

Para proyectos que siguen en Kotlin 1.4, 1.6 o 1.9, o que mezclan Java y Kotlin.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Bloque común](#bloque-común-de-lenguaje) y [`LegacyKotlin.md`](LegacyKotlin.md) | Núcleo y lectura |
| 2 | `01 · interop` y `kotlin2` **(B4)** | Núcleo + ⚡ |
| 3 | `22-legacy-kotlin19`: `language19`, `k2migration`, `compat` **(B5)**, con [`k2-notes.md`](../22-legacy-kotlin19/docs/k2-notes.md) | Núcleo |
| 4 | `21-legacy-kotlin16`: `language16`, `boot27`, `migration16` **(B5)** | Núcleo |
| 5 | `20-legacy-kotlin14`: `language14`, `coroutines14`, `android14`, `migration14` **(B5)** | Núcleo |
| 6 | `06 · bytecode` y `compilerplugins` **(B8)** | 🟢 y 🎤 |

**Al terminar:** sabes migrar un proyecto de 1.4 a 2.4 (y de Boot 2.7 a 3) paso a paso, con los compiladores reales.

## 9 · Entrevista

Las preguntas típicas, los algoritmos de pizarra y la revisión de PRs.

| Paso | Qué | Medida |
|---|---|---|
| 1 | [Ruta 1](#1--corta-crash) | 🎯 |
| 2 | Los 🎤 de las 18 lecciones con código (cada `Exercises.md` los lista al inicio) | 🎤 (108) |
| 3 | `control`: los algoritmos de pizarra (búsqueda binaria, ventana deslizante, Kadane, comprar y vender) | 🟡 y 🟠 |
| 4 | `02 · fundamentals`, `cancellation`; `sync` **(B3)** | 🎤 |
| 5 | `07 · idioms` y `gof` **(B9)** | 🎤 |
| 6 | `25 · livecoding` y `prreview` **(B9)** | Todo |

**Al terminar:** respuestas con código detrás para las preguntas de Kotlin que más salen.

## 10 · Completa (full)

Todo el curso, en un orden que respeta las dependencias entre secciones (no el número de la sección).

| Paso | Qué | Medida |
|---|---|---|
| 1 | `tutorials` y las 16 lecciones de `01-language` (la [ruta 2](#2--lenguaje-completo)) | Todo |
| 2 | `05-testing` **(B8)**: conviene temprano, para escribir mejores pruebas en lo que sigue | Todo |
| 3 | `02-coroutines` (6 de 10 lecciones) y `03-reactive` **(B3)** | Todo |
| 4 | `04-data`: `jdbc`, `jpa`, `exposed`, `jooq`, `r2dbc`, `sqldelight` | Todo |
| 5 | `06-compiler` **(B8)** y `07-design` **(B9)** | Todo |
| 6 | `08-build` **(B9)** | Todo |
| 7 | `09-spring` y `10-ktor` **(B6)**, y `13-spring-api` en sus dos sabores **(B6′)** | Todo |
| 8 | `11-multiplatform` y `12-android` **(B7)** | Todo |
| 9 | Tracks legacy `22`, `21` y `20` **(B5)** | Todo |
| 10 | `25-interview` **(B9)** y `26-kotlin-next` **(B10)** | Todo |
| 11 | Los dos 🐉: mini `kotlinx.coroutines` y producto full-stack **(B10)** | Todo |

**Al terminar:** el curso completo, de 88 entradas y los dos 🐉.
