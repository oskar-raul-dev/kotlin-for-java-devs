# Kotlin para quien viene de Java

Curso de Kotlin para un dev Java senior, con miras a **backend web** (Spring, WebFlux, Ktor) y **móvil** (Android,
Kotlin Multiplatform). La base es **Kotlin 2.4**; aparte hay **tracks legacy** que corren el compilador real de
1.4, 1.6 y 1.9, porque mucho código en producción sigue ahí. Cada lección tiene ejemplos que corren, pruebas y
ejercicios graduados **sin soluciones**. Varios ejercicios siguen un caso de punta a punta con pedidos de una tienda
(provisional: lo va a reemplazar un caso de finanzas).

> **Estado:** fase A cerrada (monorepo, Kotlin 2.4.21, track 1.9 con su contenedor). Hay 14 lecciones en
> `01-language` y `04-data`; el resto de las secciones se escribe en la fase B. El avance está en
> [`CHECKLIST.md`](CHECKLIST.md) y la hoja de ruta en [`Plan-Migracion-Monorepo.md`](Plan-Migracion-Monorepo.md).

**Salto rápido:** [Documentos](#documentos) · [Requisitos](#requisitos) · [Cómo se usa](#cómo-se-usa) ·
[Índice de secciones](#índice-de-secciones) · [Versiones](#versiones) · [Lecciones](#lecciones) ·
[Ejercicios](#ejercicios) · [Ruta prioritaria](#ruta-prioritaria) · [Bases de datos](#bases-de-datos-con-docker) ·
[Tracks legacy](#tracks-legacy) · [Estructura](#estructura) · [Trampas](#trampas-encontradas-al-armarlo) ·
[Convenciones](#convenciones) · [Agregar una lección](#agregar-una-lección)

## Documentos

| Documento | Para qué |
|---|---|
| [`CHECKLIST.md`](CHECKLIST.md) | Avance por sección y por lección, y la bitácora con fechas |
| [`Plan-Migracion-Monorepo.md`](Plan-Migracion-Monorepo.md) | Hoja de ruta: decisiones, secciones, fases A y B, preguntas abiertas |
| [`docs/Versions.md`](docs/Versions.md) | Todas las versiones fijadas (base, secciones, tracks, imágenes) y cómo actualizarlas |
| [`docs/LegacyKotlin.md`](docs/LegacyKotlin.md) | Qué trajo cada versión de 1.0 a 2.x, los tres tracks legacy y cómo correrlos |
| [`docs/Casos.md`](docs/Casos.md) | Los dos casos de estudio, Banco y Mercado: entidades, datos y qué lección usa cada uno |
| [`docs/Temario.md`](docs/Temario.md) | Temario por lección: mapeo Java → Kotlin y trampas (se reescribe en la fase B0) |
| [`22-legacy-kotlin19/docs/k2-notes.md`](22-legacy-kotlin19/docs/k2-notes.md) | Qué cambió de verdad al pasar este repo de Kotlin 1.9.22 (K1) a 2.4.21 (K2) |

## Requisitos

- **JDK 21.** Con SDKMAN, `sdk env install` en la raíz lee [`.sdkmanrc`](.sdkmanrc). Kotlin no se instala: Gradle
  baja el compilador de cada proyecto.
- No hace falta instalar Gradle: la raíz trae Gradle Wrapper 9.9.1 y cada track legacy, el suyo.
- Opcional: **Docker**, para PostgreSQL y MySQL (sin ellos esas pruebas salen `SKIPPED`) y para correr los tracks
  legacy sin instalar JDK viejos.

## Cómo se usa

**En el IDE:**

- **IntelliJ IDEA**: abrir la carpeta raíz. Importa el *composite build* (las secciones de la base). Los tracks
  legacy se abren aparte, cada uno como su propio proyecto.
- **VS Code**: un `kotlin-for-java-devs.code-workspace` propio (no se versiona; es configuración personal).
- Cada lección se corre dando play a su `Main.kt`, pero lo que vale está en sus pruebas.

**En la terminal** (desde la raíz):

```bash
sdk env                                                         # JDK 21
./gradlew test                                                  # todas las secciones de la base
./gradlew -p 01-language test                                   # una sección
./gradlew -p 01-language test --tests '*.strings.*'             # una lección (incluye su práctica)
./gradlew -p 01-language test --tests '*.strings.practice.*'    # solo tus ejercicios de esa lección
./gradlew -p 01-language run -Plesson=basics                    # el Main de una lección
./gradlew -p 01-language run -Plesson=io --console=plain        # la parte de consola lee de la terminal
./gradlew -p 04-data run -Plesson=jdbc.postgres
./gradlew -p 04-data run -Plesson=jpa --args=postgres
cd 22-legacy-kotlin19 && ./gradlew test                         # track legacy Kotlin 1.9, en nativo
./scripts/legacy.sh 19                                          # el mismo track, en su contenedor con JDK 21
```

- El repo es un **monorepo**: cada sección (`01-language`, `04-data`…) es un proyecto Gradle autocontenido. La
  raíz es un *composite build* que solo las agrega; `./gradlew test` en la raíz corre todas, y `-p <sección>` entra
  a una para filtrar pruebas o correr un `Main`.
- `-Plesson=<subpaquete>` elige qué `Main.kt` corre: el build arma `dev.raul.oskar.learning.kotlin.<lesson>.MainKt`.
  Sin `-Plesson`, corre `basics` en `01-language` y `jdbc.sqlite` en `04-data`.
- `--tests` filtra por nombre de clase con comodines.
- `--args` pasa argumentos al `main(args)`.
- `--console=plain` quita la barra de progreso de Gradle, que se mezcla con lo que se escribe en la consola.

Estado actual: **123 pruebas** en la base (85 en `01-language` y 38 en `04-data`), todas en verde, más **4 de humo**
en `22-legacy-kotlin19`. Las 18 de PostgreSQL y MySQL salen `SKIPPED` si sus contenedores no están arriba.

## Índice de secciones

El número del directorio es el orden de estudio. El nivel indica qué tan complejo es el build: 0 sin build ·
1 Gradle simple · 2 Gradle con librerías (y Docker) · 3 framework · 4 multiplataforma · 5 herramientas de build ·
6 legacy en contenedor. El detalle de cada lección está en la sección 5 del
[plan](Plan-Migracion-Monorepo.md#5-secciones-y-lecciones).

| Sección | Tema | Kotlin | Build | Lecciones | Estado |
|---|---|---|---|---|---|
| [`tutorials/`](tutorials/) | Guías de la terminal | 2.4 | 0 | `install`, `cli`, `repl`, `decompile`, `debug` | 🟡 `cli` y `repl` listos |
| [`01-language/`](01-language/) | El lenguaje y su stdlib desde Java | 2.4 | 1 | `basics`, `control`, `strings`, `oop`, `io`, `collections`, `functional`, `json`, `datetime`, `exceptions` · luego `generics`, `inline`, `interop`, `delegation`, `dsl`, `kotlin2` | 🟡 10 lecciones |
| [`02-coroutines/`](02-coroutines/) | Corrutinas, `Flow` y concurrencia | 2.4 | 1 | `fundamentals`, `cancellation`, `dispatchers`, `channels`, `flow`, `hotflows`, `sync`, `internals`, `testing`, `loom` | Fase B3 |
| [`03-reactive/`](03-reactive/) | Reactor, RxJava y puentes con corrutinas | 2.4 | 2 | `reactor`, `rxjava`, `bridges`, `backpressure` | Fase B3 |
| [`04-data/`](04-data/) | Bases de datos relacionales | 2.4 | 2 | `jdbc` (SQLite, PostgreSQL, MySQL), `jpa` · luego `exposed`, `jooq`, `r2dbc`, `sqldelight` | 🟡 4 lecciones |
| [`05-testing/`](05-testing/) | Pruebas | 2.4 | 2 | `kotest`, `mockk`, `assertions`, `testcontainers` | Fase B8 |
| [`06-compiler/`](06-compiler/) | Bytecode, reflexión, KSP y plugins del compilador | 2.4 | 2 | `bytecode`, `reflection`, `ksp`, `compilerplugins` | Fase B8 |
| [`07-design/`](07-design/) | Idioms, patrones, FP y arquitectura | 2.4 | 1 | `idioms`, `gof`, `fp`, `architecture` | Fase B9 |
| [`08-build/`](08-build/) | Gradle, calidad, docs y empaquetado | 2.4 | 5 | `gradle`, `quality`, `docs`, `packaging` | Fase B9 |
| [`09-spring/`](09-spring/) | Spring Boot y WebFlux | 2.4 | 3 | `boot`, `springdata`, `webflux`, `springtesting` | Fase B6 |
| [`10-ktor/`](10-ktor/) | Ktor | 2.4 | 3 | `server`, `client`, `ktortesting` | Fase B6 |
| [`11-multiplatform/`](11-multiplatform/) | Kotlin Multiplatform y Compose | 2.4 | 4 | `kmp`, `composemp`, `shared`, `web` | Fase B7 |
| [`12-android/`](12-android/) | Android | 2.4 | 4 (fuera del composite) | `android`, `compose`, `architecture`, `androiddata`, `di`, `background`, `androidtesting` | Fase B7 |
| [`20-legacy-kotlin14/`](20-legacy-kotlin14/) | Legacy: Android clásico y Boot 2.3–2.4 | 1.4.32 | 6 | `language14`, `coroutines14`, `android14`, `migration14` | Fase B5 |
| [`21-legacy-kotlin16/`](21-legacy-kotlin16/) | Legacy: Spring Boot 2.7 con `javax` | 1.6.21 | 6 | `language16`, `boot27`, `migration16` | Fase B5 |
| [`22-legacy-kotlin19/`](22-legacy-kotlin19/) | Legacy: el último K1 y la migración a K2 | 1.9.22 | 6 | `language19`, `k2migration`, `compat` | 🟡 build, humo y notas |
| [`25-interview/`](25-interview/) | Simulacros y revisión de PRs | 2.4 | 1 | `livecoding`, `prreview` | Fase B9 |
| [`26-kotlin-next/`](26-kotlin-next/) | Radar de la siguiente versión | 2.5 | 1 (fuera del composite) | `kotlin25` | Fase B10 |

Las secciones sin build tienen solo un `.gitkeep`. La numeración y los cortes legacy siguen abiertos a revisión
(sección 10 del plan).

## Versiones

Fijadas a propósito; el detalle, los tracks legacy y cómo actualizarlas están en [`docs/Versions.md`](docs/Versions.md):

| Pieza | Versión |
|---|---|
| Kotlin (plugins `jvm`, `serialization`, `jpa`, `allopen`) | 2.4.21 |
| kotlinx-serialization-json | 1.13.0 |
| Kermit (logging) | 2.2.0 |
| JDK (toolchain) | 21 |
| Gradle (wrapper) | 9.9.1 |

Agregado para las lecciones de bases de datos y pruebas: sqlite-jdbc 3.54.1.0, PostgreSQL JDBC 42.7.14,
mysql-connector-j 26.10.0, Hibernate ORM 7.4.15.Final, H2 2.5.252, slf4j-simple 2.0.20 y el BOM de JUnit 5.14.4.

## Lecciones

Paquete raíz: `dev.raul.oskar.learning.kotlin`. Cada subpaquete tiene su `Main.kt` y su `Exercises.md` en
`<sección>/src/main/kotlin/…/<subpaquete>/`, y sus pruebas en el mismo paquete bajo `<sección>/src/test/kotlin`.
Las lecciones 1–9 y 14 están en `01-language`; las de bases de datos (10–13), en `04-data`.

| # | Subpaquete | Qué repasa | Ejercicios | 🎯 |
|---|---|---|---|---|
| 1 | `basics` | Tipos numéricos, conversiones explícitas, overflow, `BigDecimal` con operadores, `Char`, `==` frente a `===`, `Any`/`Unit`/`Nothing`, smart casts | [25](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Exercises.md) | 5 |
| 2 | `control` | `if`/`when` como expresiones, rangos (`..`, `until`, `downTo`, `step`), bucles, etiquetas `break@`, algoritmos de pizarra (gcd, primos, búsqueda binaria, two-sum, paréntesis) | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/control/Exercises.md) | 4 |
| 3 | `strings` | Plantillas, raw strings, `buildString`, `substringBefore/After`, regex con grupos con nombre, `replace` con lambda. **La trampa de `split("\\/")`** | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/Exercises.md) | 3 |
| 4 | `oop` | Constructores, `init`, setters privados, `open`/`abstract`, interfaces con default, `sealed` + `when` exhaustivo, enums con `companion` y `of`, `object`, `data class`, `value class`, operadores, delegación `by`, `lazy` | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/Exercises.md) | 4 |
| 5 | `io` | `Path` + `kotlin.io.path`, `useLines`, `use {}`, recursos del classpath, consola testeable, Kermit | [23](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/io/Exercises.md) | 3 |
| 6 | `collections` | Solo lectura frente a mutable, listas, sets, mapas (`getOrPut`), `TreeMap`, `ArrayDeque`, `PriorityQueue`, arrays | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/Exercises.md) | 5 |
| 7 | `functional` | Tipos función, closures, composición, lambdas con receptor, `reified`, `Result`, scope functions, `fun interface`, pipelines (`groupBy`, `groupingBy`, `partition`), secuencias perezosas | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/Exercises.md) | 6 |
| 8 | `json` | kotlinx-serialization: `@SerialName`, `explicitNulls`, `ignoreUnknownKeys`, defaults, sealed polimórfico, serializadores para `BigDecimal`/`LocalDate`, árbol `JsonElement` | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/Exercises.md) | 4 |
| 9 | `datetime` | `java.time`: parseo estricto `MM/dd/uuuu`, edad con `Period`, `Clock` inyectado, epoch millis y zonas, ventanas de días, horario de verano | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/Exercises.md) | 5 |
| 10 | `jdbc.sqlite` | Repositorio JDBC común + dialecto SQLite (sin servidor) | [25, compartidos](04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/Exercises.md) | — |
| 11 | `jdbc.postgres` | Mismo repositorio en PostgreSQL + `RETURNING`, `= ANY(array)`, `jsonb` | ↑ | — |
| 12 | `jdbc.mysql` | Mismo repositorio en MySQL + `ON DUPLICATE KEY`, `LAST_INSERT_ID()`, aislamiento por defecto | ↑ | — |
| 13 | `jpa` | Hibernate 7 sin Spring: entidades en Kotlin, cascade, `orphanRemoval`, N+1 y `join fetch`, dirty checking, `LazyInitializationException`, `@Version`, UPDATE masivo | [24](04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/Exercises.md) | — |
| 14 | `exceptions` | Sin checked exceptions, `try`/`throw` como expresiones, `require`/`check`/`error`, sin multi-catch (`when` en el catch), jerarquía sellada con `cause`, `Result`/`runCatching` (y su trampa con `CancellationException`), validación que acumula errores, `use` y suppressed, `@Throws` para Java | [24](01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/Exercises.md) | 4 |

`jdbc/` (sin subpaquete) tiene lo compartido: `ProductRepository`, la interfaz `Dialect`, las extensiones
`mapRows` e `inTransaction`, y el recorrido `runDemo`. Las tres bases pasan **la misma batería de pruebas**
(`ProductRepositoryContract`), así que las diferencias entre motores quedan aisladas en cada `*Dialect`. Por eso
los tres motores comparten un solo `jdbc/Exercises.md`, con cada ejercicio etiquetado por motor (`[todos]`,
`[SQLite]`, `[PG]`, `[MySQL]`).

## Ejercicios

**289 ejercicios** en 12 archivos `Exercises.md`, de 23 a 25 por lección, sin soluciones.

**Semáforo:**

| | Nivel | Qué esperar |
|---|---|---|
| 🟢 | Fácil | Una función, una idea de la lección. 5–15 min |
| 🟡 | Medio | Combina dos o tres ideas o tiene bordes delicados. 15–30 min |
| 🟠 | Difícil | Diseño, algoritmo no trivial o comportamiento que hay que demostrar. 30–90 min |
| 🔴 | Muy difícil | Mini-proyecto o experimento con medición. Una sesión o más |
| 🎯 | Prioritario | Ruta recomendada (ver [Ruta prioritaria](#ruta-prioritaria)). El resto queda para después |

Los tiempos son una estimación, no una medición. En la fase B llegan las marcas de `java-champion-review`:
⚡ **Saiyajin** (miniproyecto que cierra la lección), 🐉 **jefe final** (cruza secciones), 🎤 pregunta típica de
entrevista y etiquetas de versión (`[2.0]`, `[exp N]`, `[dep N]`, `[rm N]`), con 25 a 40 ejercicios por lección.

**Convención para resolverlos:**

- Código en el subpaquete `practice` de la lección: `<sección>/src/main/kotlin/…/<lección>/practice/ExNN<Nombre>.kt`.
- Prueba en el espejo: `<sección>/src/test/kotlin/…/<lección>/practice/ExNN<Nombre>Test.kt`, con `kotlin.test`.
- Cada enunciado trae una **firma sugerida** y los **casos mínimos** que la prueba debe cubrir.
- Si dos ejercicios de la misma lección usan el mismo nombre de función (`describe`, `parse`…), envuelve cada uno
  en un `object ExNN { … }` para que no choquen dentro del paquete `practice`.
- Los ejercicios que dicen "explica en un comentario" se responden en la prueba, junto al caso que lo demuestra.

Ejemplo, para el ejercicio 02 de `basics`:

```kotlin
// 01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/practice/Ex02SafeInt.kt
package dev.raul.oskar.learning.kotlin.basics.practice

fun safeInt(raw: String?): Int? = TODO("ejercicio 02")
```

```kotlin
// 01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/basics/practice/Ex02SafeIntTest.kt
package dev.raul.oskar.learning.kotlin.basics.practice

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Ex02SafeIntTest {
    @Test
    fun `parses trimmed numbers and rejects garbage`() {
        assertEquals(42, safeInt(" 42 "))
        assertNull(safeInt("4x"))
        assertNull(safeInt(null))
    }
}
```

```bash
./gradlew -p 01-language test --tests '*.basics.practice.*'
```

- Mientras la función tenga `TODO(...)`, la prueba falla con `NotImplementedError`: así se sabe qué falta.
- Gradle no compila los `Exercises.md` aunque estén en `src/main/kotlin`, porque solo toma los `.kt`.

## Ruta prioritaria

43 ejercicios 🎯, ordenados para seguir un caso de punta a punta con pedidos de una tienda: leer el JSON, limpiar
nulos y fechas, agrupar, contar por día, armar una ventana de días, sacar estadísticas y revisar el manejo de
errores. Si el tiempo no alcanza, haz primero los marcados con **★**: son 15 y cubren el recorrido completo.

**Bloque 1: leer los datos**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `json` | 02 | 🟢 Claves con guiones (`@SerialName`) |
| ★ | `json` | 03 | 🟢 Campo que falta (`explicitNulls`) |
| | `json` | 01 | 🟢 Ida y vuelta |
| | `json` | 08 | 🟡 Cargar con tolerancia |
| | `io` | 01 | 🟢 Recurso del classpath |
| | `io` | 07 | 🟢 Kermit |
| | `io` | 08 | 🟡 CSV a objetos |
| | `oop` | 01 | 🟢 `data class` de pedidos |

**Bloque 2: nulos, cadenas y fechas**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `basics` | 09 | 🟡 Cadena de nulos |
| ★ | `strings` | 01 | 🟢 Fecha MM/dd/yyyy (sin la trampa de `split`) |
| ★ | `datetime` | 02 | 🟢 Epoch a fecha |
| ★ | `datetime` | 03 | 🟢 Edad |
| | `basics` | 02 | 🟢 Parseo seguro |
| | `basics` | 05 | 🟢 Dividir la cuenta |
| | `basics` | 14 | 🟡 Modos de redondeo |
| | `basics` | 16 | 🟡 `Nothing` útil |
| | `strings` | 08 | 🟡 Código de estado |
| | `strings` | 09 | 🟡 Zip de EE. UU. |
| | `datetime` | 01 | 🟢 Fecha estricta |

**Bloque 3: categorías y conteos**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `control` | 02 | 🟢 Tramo de edad |
| ★ | `oop` | 02 | 🟢 Enum con `of` |
| ★ | `functional` | 09 | 🟡 `flatMap` de categorías |
| ★ | `collections` | 09 | 🟡 Conteo por categoría y día |
| | `control` | 01 | 🟢 Tramo de monto |
| | `collections` | 01 | 🟢 Contador con mapa |
| | `collections` | 02 | 🟢 Solo lectura no es inmutable |
| | `collections` | 08 | 🟡 Historial por usuario |
| | `functional` | 01 | 🟢 `map`, `filter`, `sumOf` |
| | `functional` | 02 | 🟢 `mapNotNull` |
| | `functional` | 08 | 🟡 `groupBy` + `mapValues` |
| | `functional` | 10 | 🟡 `fold` con acumulador rico |
| | `datetime` | 09 | 🟡 Agrupar por día local |

**Bloque 4: ventanas de tiempo y estadística**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `datetime` | 08 | 🟡 Ventana de 7 días (¿incluye el día dado?) |
| ★ | `collections` | 10 | 🟡 Ventana de 7 días con `TreeMap` |
| ★ | `functional` | 16 | 🟠 Estadística en una pasada |
| | `control` | 08 | 🟡 Primer índice que cumple |
| | `control` | 11 | 🟡 Ventana deslizante |

**Bloque 5: errores y diseño (lo que más se comenta en una revisión de código)**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `exceptions` | 02 | 🟢 `!!` en un PR |
| ★ | `exceptions` | 01 | 🟢 `require` y `check` |
| | `exceptions` | 08 | 🟡 Envolver sin perder la causa |
| | `exceptions` | 09 | 🟡 Jerarquía sellada a HTTP |
| | `oop` | 08 | 🟡 `sealed` para resultados |
| | `oop` | 09 | 🟡 Reglas polimórficas |

**Después:** todo lo 🟠 y 🔴 salvo la estadística en una pasada, y las lecciones `jdbc` y `jpa`. Entre los difíciles,
los que cierran el caso son `functional` 17 (días atípicos), `collections` 21 (ventana con sumas acumuladas), `jdbc` 24 (la
misma ventana con funciones de ventana SQL), `oop` 24 y `exceptions` 24 (rediseño y revisión de un `main`
monolítico).

## Bases de datos con Docker

```bash
docker compose -f docker/postgres/compose.yaml up -d --wait
docker compose -f docker/mysql/compose.yaml up -d --wait
./gradlew test
docker compose -f docker/postgres/compose.yaml down -v
docker compose -f docker/mysql/compose.yaml down -v
```

- `up -d --wait` arranca en segundo plano y espera a que el `healthcheck` diga que la base acepta conexiones.
- `down -v` borra el contenedor **y su volumen**: la próxima vez la base arranca vacía.
- PostgreSQL 17.2 escucha en `127.0.0.1:15432` y MySQL 8.4.3 en `127.0.0.1:13306`. Son puertos altos para no
  chocar con otras bases en 5432/3306. Base, usuario y clave: `learning`.
- Para usar otra base están las variables `PG_URL`/`PG_USER`/`PG_PASSWORD` y `MYSQL_URL`/`MYSQL_USER`/`MYSQL_PASSWORD`.
- **Si un contenedor no está arriba, sus pruebas salen `SKIPPED`**, no fallidas: `./gradlew test` siempre
  se puede correr. SQLite (en memoria) y JPA (H2 en memoria) no necesitan Docker.
- `./gradlew -p 04-data run -Plesson=jpa --args=postgres` recrea las tablas de JPA en el PostgreSQL del compose
  (`drop-and-create`). No toca la tabla `product` de JDBC.

## Tracks legacy

Proyectos aparte, **fuera del composite**, cada uno con su wrapper de Gradle, su versión de Kotlin y su `.sdkmanrc`.
`./gradlew test` en la raíz no los incluye. Hoy existe `22-legacy-kotlin19` (Kotlin 1.9.22, el último K1); los de
1.4 y 1.6 llegan en la fase B5. Guía completa en [`docs/LegacyKotlin.md`](docs/LegacyKotlin.md).

```bash
cd 22-legacy-kotlin19 && sdk env && ./gradlew test   # en nativo, con el wrapper del track (Gradle 9.2.1)
./scripts/legacy.sh 19                               # en contenedor: eclipse-temurin:21-jdk (arm64 nativo)
./scripts/legacy.sh 19 run --console=plain           # su Main
./scripts/legacy.sh 19 shell                         # bash dentro del contenedor
```

Las imágenes van fijadas por digest y comparten la caché de Gradle en un volumen: la primera corrida tarda ~1 minuto,
las siguientes ~4 segundos ([`docker/legacy-kotlin/README.md`](docker/legacy-kotlin/README.md)).

## Estructura

```text
kotlin-for-java-devs/
├── README.md, CHECKLIST.md, CLAUDE.md, Plan-Migracion-Monorepo.md
├── settings.gradle.kts         # composite build: includeBuild de las secciones de la base
├── build.gradle.kts            # tareas agregadas build/test/check/clean
├── gradlew, gradle/wrapper/    # Gradle 9.9.1 para todo el composite
├── .sdkmanrc                   # JDK 21 (Zulu) para `sdk env`
├── docs/
│   ├── Temario.md              # temario por lección
│   ├── Versions.md             # versiones fijadas y cómo actualizarlas
│   └── LegacyKotlin.md         # Kotlin 1.0 → 2.x y los tracks legacy
├── docker/
│   ├── postgres/compose.yaml   # PostgreSQL 17.2 en 15432
│   ├── mysql/compose.yaml      # MySQL 8.4.3 en 13306
│   └── legacy-kotlin/          # compose.yaml (k14, k16, k19) y README: el JDK de cada track legacy
├── scripts/legacy.sh           # ./scripts/legacy.sh <14|16|19> [tareas de Gradle | shell]
├── 01-language/                # basics, control, strings, oop, collections, functional, exceptions, io, datetime, json
│   ├── settings.gradle.kts, build.gradle.kts   # Kotlin jvm + serialization, Kermit
│   └── src/
│       ├── main/kotlin/dev/raul/oskar/learning/kotlin/<lección>/Main.kt, *.kt, Exercises.md
│       ├── main/kotlin/…/<lección>/practice/   # tus soluciones (se crea al resolver)
│       ├── main/resources/data/sales.csv, orders.json
│       └── test/kotlin/…/<lección>/<Lección>Test.kt y practice/
├── 04-data/                    # jdbc (sqlite, postgres, mysql) y jpa
│   ├── settings.gradle.kts, build.gradle.kts   # Kotlin jvm + jpa + allopen, drivers, Hibernate, H2
│   └── src/
│       ├── main/kotlin/…/jdbc/{sqlite,postgres,mysql}/, jpa/
│       ├── main/resources/META-INF/persistence.xml   # unidad JPA "learning" (H2 por defecto)
│       ├── main/resources/simplelogger.properties    # slf4j-simple solo en WARN
│       └── test/kotlin/…/jdbc/, jpa/
├── 22-legacy-kotlin19/         # Kotlin 1.9.22: wrapper propio (Gradle 9.2.1), .sdkmanrc, prueba de humo, docs/k2-notes.md
└── tutorials/, 02-coroutines/ … 21-legacy-kotlin16/, 25-interview/, 26-kotlin-next/   # (vacías) fase B
```

## Trampas encontradas al armarlo

- **Gradle 9 + Kotlin 1.9.22 no corre pruebas tal cual.** `kotlin("test")` 1.9.22 trae JUnit 5.6.3 y Gradle 9 ya
  no inyecta el launcher, así que falla con `Failed to load JUnit Platform`. Se arreglaba con
  `platform("org.junit:junit-bom:…")` y `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`. Con Kotlin
  2.4 ya no hace falta, pero las dos líneas se quedan: el BOM fija la versión de JUnit y Gradle recomienda declarar el
  launcher. Importa para el track `22-legacy-kotlin19`.
- **`split(String)` en Kotlin es literal.** `"03/14/1985".split("\\/")` devuelve la cadena entera; en Java,
  `split` siempre usa regex. Es un error fácil al portar código de Java. Ver `strings/Strings.kt` y su prueba.
- **`explicitNulls` era experimental en kotlinx-serialization 1.6.2** y pedía
  `@file:OptIn(ExperimentalSerializationApi::class)`; en 1.13 ya es estable (`json/Codec.kt`).
- **K2 (Kotlin 2.x) avisa de lo que K1 dejaba pasar:** `!!` innecesario después de `assertNotNull`,
  `java.lang.Long::class.java` (mejor `Long::class.javaObjectType`) y `==` entre arrays. Detalle en
  [`22-legacy-kotlin19/docs/k2-notes.md`](22-legacy-kotlin19/docs/k2-notes.md).
- **Hibernate registra por `java.util.logging`** aunque haya SLF4J: se fuerza con la propiedad de sistema
  `org.jboss.logging.provider=slf4j` (`jpa/Jpa.kt`).
- **Entidades JPA en Kotlin:** nada de `data class`; hacen falta los plugins `jpa` (constructor vacío) y
  `allopen` (proxies lazy).
- **MySQL gasta ids en los upserts:** cada `ON DUPLICATE KEY UPDATE` que termina en UPDATE consume un valor de
  `AUTO_INCREMENT` (`jdbc/mysql/Main.kt`).

## Convenciones

- **Código en inglés, comentarios en español.** Identificadores, paquetes, directorios y nombres de prueba en inglés;
  comentarios, KDoc, `Exercises.md` y documentos en español.
- Paquete raíz `dev.raul.oskar.learning.kotlin`; cada lección es un subpaquete (`…kotlin.strings`, `…kotlin.jdbc.mysql`).
- Cada sección es autocontenida (sin parent común): las versiones se repiten a propósito en cada `build.gradle.kts`
  y se mantienen en sincronía con [`docs/Versions.md`](docs/Versions.md).
- Cada lección tiene un `Main.kt` (recorrido corto) y la lógica en archivos aparte, probada en `<Lección>Test.kt`.
- Al terminar algo se actualizan juntos: los conteos de este README, [`CHECKLIST.md`](CHECKLIST.md) (estado y
  bitácora) y, si cambia una versión, `docs/Versions.md`.

## Agregar una lección

1. En la sección que corresponda, crear `src/main/kotlin/dev/raul/oskar/learning/kotlin/<tema>/Main.kt` con `package …<tema>` y `fun main()`.
2. Poner la lógica en funciones o clases aparte (no en `main`) para poder probarla.
3. Crear `src/test/kotlin/dev/raul/oskar/learning/kotlin/<tema>/<Tema>Test.kt` con `kotlin.test`.
4. Escribir `<tema>/Exercises.md` con el mismo encabezado que los demás: 20–30 ejercicios con semáforo, firma
   sugerida y casos mínimos, y 🎯 en los que tocan lo que se está preparando.
5. Correr con `./gradlew -p <sección> run -Plesson=<tema>` y `./gradlew -p <sección> test --tests '*.<tema>.*'`.
6. Agregar la fila a la tabla de lecciones y actualizar el índice de secciones y `CHECKLIST.md`.
