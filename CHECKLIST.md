# Checklist del curso

Bitácora de avance: qué está creado y qué falta, sección por sección y lección por lección. Se actualiza cada vez
que se termina algo. El porqué y el orden de los pasos están en [`Plan-Migracion-Monorepo.md`](Plan-Migracion-Monorepo.md).

**Leyenda:** ✅ listo · 🟡 en progreso · ⬜️ pendiente · ➖ no aplica

**Estados:** `PENDIENTE` · `EN CREACIÓN` · `LISTO`

**Salto rápido:** [Infraestructura](#infraestructura-del-repo) · [Migración](#migración-fase-a) ·
[Secciones](#secciones) · [Lecciones](#lecciones) · [Bitácora](#bitácora)

## Infraestructura del repo

| Pieza | Listo | Estado | Notas |
|---|---|---|---|
| Repositorio git | ✅ | `LISTO` | Rama `main`, usuario local; remoto `origin` en GitHub; tags `baseline-1.9.22`, `monorepo-1.9.22`, `monorepo-2.4.21` y `legacy-kotlin19` |
| `.gitignore` del ecosistema Java/JVM | ✅ | `LISTO` | El de `java-champion-review`, con `.idea/` completo ignorado |
| Gradle Wrapper 9.9.1 + composite build | ✅ | `LISTO` | `01-language` y `04-data`; `./gradlew test` en verde (123 pruebas) |
| Kotlin 2.x en la base | ✅ | `LISTO` | Kotlin 2.4.21, Gradle 9.9.1 (A7) |
| `.sdkmanrc` | 🟡 | `EN CREACIÓN` | Raíz y `22-legacy-kotlin19` (Zulu 21.0.8) listos; faltan los tracks 20 y 21 |
| Proyecto de IntelliJ (`.idea/`) | ✅ | `LISTO` | Ignorado por git; composite importado con `01-language` y `04-data` como builds incluidos |
| Workspace de VS Code | ✅ | `LISTO` | `kotlin-for-java-devs.code-workspace` (ignorado por git) |
| `README.md` (índice general) | 🟡 | `EN CREACIÓN` | Comandos y rutas del monorepo al día; falta el índice de secciones (A9) |
| `CHECKLIST.md` | ✅ | `LISTO` | Este archivo |
| `Plan-Migracion-Monorepo.md` | ✅ | `LISTO` | Secciones, versiones, contenedores, fases A y B, ideas del caso de finanzas |
| `docs/Temario.md` | 🟡 | `EN CREACIÓN` | El de las 14 lecciones actuales; se reescribe completo en B0 |
| `docs/Versions.md` | ✅ | `LISTO` | Versiones de la base, por sección y de los tracks; cómo actualizar |
| `docs/LegacyKotlin.md` | ⬜️ | `PENDIENTE` | Con A8 |
| `docs/JavaToKotlin.md`, `docs/EffectiveKotlin.md` | ⬜️ | `PENDIENTE` | Fase B |
| `CLAUDE.md` | ✅ | `LISTO` | Al día con el monorepo; se revisa en A9 |
| Compose de PostgreSQL 17.2 y MySQL 8.4.3 | ✅ | `LISTO` | `docker/postgres`, `docker/mysql` |
| Contenedores de los tracks legacy (`docker/legacy-kotlin/`, `scripts/legacy.sh`) | ⬜️ | `PENDIENTE` | Paso A8; carpetas creadas |

## Migración (fase A)

| Paso | Listo | Notas |
|---|---|---|
| A0 · Línea base en git | ✅ | Tag `baseline-1.9.22` sobre `c33e8d1` |
| A1 · Repo público (sin referencias externas) | ✅ | Escaneo con `grep` antes de cada commit; caso de ejemplo provisional de pedidos |
| A2 · Esqueleto del monorepo | ✅ | `b5b5214` |
| A3 · `01-language` | ✅ | `0d734cd`, 85 pruebas |
| A4 · `04-data` | ✅ | `0d734cd`, 38 pruebas (18 de PostgreSQL y MySQL) |
| A5 · Cierre del movimiento | ✅ | Composite importado en IntelliJ; 123 pruebas en verde sin SKIPPED; tag `monorepo-1.9.22` |
| A7 · Base a Kotlin 2.x | ✅ | 4 subpasos, 123 pruebas en verde; notas en `22-legacy-kotlin19/docs/k2-notes.md` |
| A6 · Track `22-legacy-kotlin19` | ✅ | Wrapper propio 9.2.1, Kotlin 1.9.22, 4 pruebas de humo; fuera del composite |
| A8 · Contenedores legacy | ⬜️ | **Siguiente** |
| A9 · Documentación | ⬜️ | README con índice de secciones, CLAUDE.md reescrito |
| B0′ · Caso de finanzas | ⬜️ | Elegir de la sección 11 del plan y reemplazar el dominio de pedidos |

## Secciones

| Sección | Estructura | Build base | Código | Pruebas | Ejercicios | Tutorial / docs | Estado |
|---|---|---|---|---|---|---|---|
| [`tutorials`](#tutorials) | ✅ | ➖ | ➖ | ➖ | ⬜️ | ⬜️ | `PENDIENTE` |
| [`01-language`](#01-language) | ✅ | ✅ Gradle (2.4.21) | 🟡 | 🟡 | 🟡 | ➖ | `EN CREACIÓN` |
| [`02-coroutines`](#02-coroutines) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`03-reactive`](#03-reactive) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`04-data`](#04-data) | ✅ | ✅ Gradle (2.4.21) | 🟡 | 🟡 | 🟡 | ➖ | `EN CREACIÓN` |
| [`05-testing`](#05-testing) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`06-compiler`](#06-compiler) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`07-design`](#07-design) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`08-build`](#08-build) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`09-spring`](#09-spring) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`10-ktor`](#10-ktor) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`11-multiplatform`](#11-multiplatform) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`12-android`](#12-android) | ✅ | ⬜️ AGP | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`20-legacy-kotlin14`](#20-legacy-kotlin14) | ✅ | ⬜️ Gradle 6.8 | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`21-legacy-kotlin16`](#21-legacy-kotlin16) | ✅ | ⬜️ Gradle 7.6 | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`22-legacy-kotlin19`](#22-legacy-kotlin19) | ✅ | ✅ Gradle 9.2.1 (1.9.22) | 🟡 humo | 🟡 4 | ⬜️ | 🟡 `k2-notes.md` | `EN CREACIÓN` |
| [`25-interview`](#25-interview) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |
| [`26-kotlin-next`](#26-kotlin-next) | ✅ | ⬜️ | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |

## Lecciones

Las lecciones que ya existen tienen código y pruebas, pero quedan 🟡 hasta: cambiar el
dominio provisional por el caso de finanzas (B0′) y llevar los ejercicios a la escala de `java-champion-review`
(mínimo 25, marcas 🎤 y su ⚡, fase B2).

### tutorials

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `install` | ➖ | ➖ | ⬜️ | ➖ | `PENDIENTE` |
| — | `cli` | ➖ | ➖ | ⬜️ | ➖ | `PENDIENTE` |
| — | `repl` | ➖ | ➖ | ⬜️ | ➖ | `PENDIENTE` |
| — | `decompile` | ➖ | ➖ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `debug` | ➖ | ➖ | ⬜️ | ➖ | `PENDIENTE` |

### 01-language

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| 1 | `basics` | ✅ | ✅ | 🟡 25 | ⬜️ | `EN CREACIÓN` |
| 2 | `control` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 3 | `strings` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 4 | `oop` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 5 | `io` | ✅ | ✅ | 🟡 23 | ⬜️ | `EN CREACIÓN` |
| 6 | `collections` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 7 | `functional` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 8 | `json` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 9 | `datetime` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| 14 | `exceptions` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| — | `generics` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `inline` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `interop` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `delegation` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `dsl` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `kotlin2` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 02-coroutines

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `fundamentals` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `cancellation` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `dispatchers` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `channels` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `flow` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `hotflows` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `sync` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `internals` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `testing` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `loom` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 03-reactive

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `reactor` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `rxjava` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `bridges` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `backpressure` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 04-data

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| 10–12 | `jdbc` (SQLite, PostgreSQL, MySQL) | ✅ | ✅ | 🟡 25 | ⬜️ | `EN CREACIÓN` |
| 13 | `jpa` | ✅ | ✅ | 🟡 24 | ⬜️ | `EN CREACIÓN` |
| — | `exposed` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `jooq` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `r2dbc` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `sqldelight` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 05-testing

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `kotest` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `mockk` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `assertions` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `testcontainers` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 06-compiler

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `bytecode` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `reflection` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `ksp` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `compilerplugins` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 07-design

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `idioms` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `gof` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `fp` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `architecture` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 08-build

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `gradle` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `quality` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `docs` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `packaging` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 09-spring

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `boot` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `springdata` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `webflux` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `springtesting` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 10-ktor

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `server` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `client` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `ktortesting` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 11-multiplatform

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `kmp` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `composemp` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `shared` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `web` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 12-android

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `android` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `compose` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `architecture` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `androiddata` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `di` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `background` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `androidtesting` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 20-legacy-kotlin14

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `language14` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `coroutines14` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `android14` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `migration14` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 21-legacy-kotlin16

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `language16` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `boot27` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `migration16` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 22-legacy-kotlin19

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `language19` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `k2migration` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `compat` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 25-interview

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `livecoding` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |
| — | `prreview` | ⬜️ | ⬜️ | ⬜️ | ⬜️ | `PENDIENTE` |

### 26-kotlin-next

| # | Lección | Código | Pruebas | Ejercicios | ⚡ | Estado |
|---|---|---|---|---|---|---|
| — | `kotlin25` | ⬜️ | ⬜️ | ⬜️ | ➖ | `PENDIENTE` |

### 🐉 Jefes finales

| Proyecto | Estado |
|---|---|
| Mini `kotlinx.coroutines` (`02` + `06`) | `PENDIENTE` |
| Producto full-stack de finanzas: backend + cliente KMP (`04` + `09`/`10` + `11`/`12` + `08`) | `PENDIENTE` |

## Bitácora

- **2026-11-10** · Repo git creado (`a45fc23`, `c33e8d1`) con el curso v1: 14 lecciones, 123 pruebas, 289 ejercicios,
  sin referencias externas. Dominio de ejemplo provisional: pedidos de tienda.
- **2026-11-10** · `Plan-Migracion-Monorepo.md` (`003ce5d`): secciones, tracks legacy 1.4 / 1.6 / 1.9, estrategia de
  contenedores, fases A y B, y 10 ideas de caso de finanzas.
- **2026-11-10** · Estructura del monorepo (`b5b5214`) y movimiento de las lecciones a `01-language` y `04-data`
  (`0d734cd`): composite build en la raíz, todo sigue en Kotlin 1.9.22; 123 pruebas en verde con PostgreSQL y MySQL.
  Siguiente sesión: A7, migrar la base a Kotlin 2.x.
- **2026-11-12** · Cierre de A0 y A5: tag `baseline-1.9.22` sobre `c33e8d1`; composite importado en IntelliJ
  (`01-language` y `04-data` como builds incluidos); `./gradlew clean test` en la raíz con 123 pruebas en verde y
  0 SKIPPED (PostgreSQL y MySQL arriba). Tag `monorepo-1.9.22` sobre el commit de cierre. Siguiente: A7.
- **2026-11-13** · A7: base a **Kotlin 2.4.21** en 4 subpasos, cada uno con las 123 pruebas en verde y 0 SKIPPED:
  Gradle 9.9.1; Kotlin 2.4.21 + kotlinx-serialization 1.13.0 + Kermit 2.2.0; drivers, H2, slf4j y JUnit 5.14.4;
  Hibernate 7.4.15. Salida de los 14 `Main` comparada con `monorepo-1.9.22`: solo cambian mensajes de Hibernate y de
  serialization. 3 avisos nuevos de K2 corregidos; `explicitNulls` ya no pide opt-in. Nuevos: `.sdkmanrc`,
  `docs/Versions.md`, `22-legacy-kotlin19/docs/k2-notes.md`. Siguiente: A6.
- **2026-11-13** · A6: `22-legacy-kotlin19` con wrapper propio (Gradle 9.2.1), Kotlin 1.9.22, `.sdkmanrc` y 4 pruebas de
  humo (`data object`, `entries`, `..<`, y el `toString()` de una lambda, que delata si corre K1 o K2). Fuera del
  composite: `cd 22-legacy-kotlin19 && ./gradlew test`. Remoto `origin` en GitHub. Siguiente: A8.
