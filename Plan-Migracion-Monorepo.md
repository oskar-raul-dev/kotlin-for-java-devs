# Plan: de proyecto único a monorepo de Kotlin

Propuesta para revisar antes de mover código. El repo pasa de un proyecto único a un
curso general de Kotlin para quien viene de Java, con miras a **backend web** (Spring, WebFlux, Ktor) y **móvil**
(Android, Kotlin Multiplatform). Sigue la arquitectura de `java-champion-review` y `groovy-for-java-devs`:
**monorepo de proyectos independientes, cada uno con el build mínimo que necesita**.

Redactado el **10 de noviembre de 2026**. Lo marcado con ⚠️ se verifica al ejecutar el paso correspondiente.

**Salto rápido:** [Estado](#estado-y-próximos-pasos) · [Decisiones](#1-decisiones) · [Versiones](#2-versiones-base-y-tracks-legacy) ·
[Contenedores](#3-estrategia-de-contenedores) · [Arquitectura](#4-arquitectura-del-monorepo) ·
[Secciones](#5-secciones-y-lecciones) · [Ejercicios](#6-ejercicios-niveles-de-java-champion) ·
[Fase A: migrar](#7-fase-a-migrar-lo-que-hay) · [Fase B: contenido](#8-fase-b-generar-el-resto) ·
[Riesgos](#9-riesgos-y-pendientes) · [Para decidir](#10-para-decidir-en-la-revisión) ·
[Caso de punta a punta](#11-caso-de-punta-a-punta-ideas-de-reemplazo) ·
[Guías de lección](#12-guías-de-lección-docslessons)

---

## Estado y próximos pasos

Actualizado el **22 de noviembre de 2026**. La fase A está cerrada y la fase B va en curso: **B1 cerrado, B0 y B0′ a
medias**. El avance por sección y lección está en [`CHECKLIST.md`](CHECKLIST.md).

### Dónde vamos

| Paso | Estado | Qué falta |
|---|---|---|
| Fase A (A0–A9) | ✅ tag `phase-a` | — |
| B1 · Tutoriales | ✅ | — (5 tutoriales, 72 ejercicios, 136 comprobaciones en sus `check.sh`) |
| B0 · Temario | ✅ | — (88 entradas, los dos 🐉 y la trazabilidad; revisado con el usuario: [`docs/Temario-Plan.md`](docs/Temario-Plan.md#estado)) |
| B0′ · Casos Banco y Mercado | ✅ | — (las 6 tandas del [reemplazo](docs/Casos.md#plan-de-reemplazo): el dominio de pedidos ya no existe) |
| B0″ · Guías de lección | ✅ (mapa) · 🟡 guías de estudio: 8 de 12 (faltan `datetime`, `exceptions`, `jdbc`, `jpa`) ([sección 13](#13-guías-de-estudio-teoría-y-fuentes-propuesta)) | — (índice, plantilla y 12 guías en [`docs/lessons/`](docs/lessons/README.md); se mantienen con cada cambio de código) |
| B2–B10 | ⬜️ | Todo |

### La base del curso v1, verificada en Kotlin 2.x

**Verificado el 22 de noviembre de 2026**, desde cero (`./gradlew clean test --rerun-tasks --warning-mode=all`):

- `01-language` y `04-data` compilan con **Kotlin 2.4.21**, JDK 21 (toolchain) y Gradle 9.9.1, **sin avisos** del
  compilador ni deprecaciones de Gradle.
- **123 pruebas, 0 fallidas**: 85 de 85 en `01-language`; 38 en `04-data`, con las 18 de PostgreSQL y MySQL `SKIPPED`
  ese día porque los puertos 15432 y 13306 estaban tomados por contenedores de otro proyecto. Esas 18 pasaron con
  2.4.21 el 13 de noviembre (A7, 0 SKIPPED) y se repiten antes del próximo commit con código.
- Los 12 `Main` corren: `basics`, `control`, `strings`, `oop`, `io`, `collections`, `functional`, `exceptions`,
  `datetime`, `json`, `jdbc.sqlite` y `jpa`.
- `22-legacy-kotlin19` sigue en verde con Kotlin 1.9.22 (4 pruebas de humo).

**Qué significa "migrado":** el código del curso v1 compila y se comporta igual en K2 (A7 comparó la salida de los
`Main` con la de 1.9.22) y se corrigieron los 3 avisos nuevos de K2
([`k2-notes.md`](22-legacy-kotlin19/docs/k2-notes.md)). **No** significa que el código ya enseñe lo de 2.x: eso llega
con el temario (etiquetas `[2.0]`…`[2.4]`), la lección `kotlin2` (B4) y los ejercicios nuevos (B2). El dominio sigue
siendo el provisional de pedidos hasta B0′.

### Historial de la fase A

| Commit | Qué |
|---|---|
| `a45fc23` Initial commit | Solo `.gitignore` |
| `c33e8d1` Add Kotlin course v1 | El curso en un solo proyecto (Kotlin 1.9.22), sin referencias externas y con el dominio provisional de pedidos |
| `003ce5d` Add monorepo migration plan | Este documento |
| `b5b5214` Create monorepo directory structure | Las 17 secciones, `docs/`, `scripts/` y `docker/legacy-kotlin/` con `.gitkeep` |
| `0d734cd` Move current lessons into 01-language and 04-data | Composite build en la raíz; 123 pruebas en verde (85 + 38) con PostgreSQL y MySQL |
| `cb023d3` Close monorepo move (A0-A5) and add progress checklist | `CHECKLIST.md`; importación en IntelliJ comprobada. Tag `monorepo-1.9.22` |
| `d2f6b35` Migrate base to Kotlin 2.4.21 | A7. Tag `monorepo-2.4.21` |
| `98a5125` Add Kotlin 1.9 legacy track | A6: `22-legacy-kotlin19` con wrapper propio (Gradle 9.2.1), Kotlin 1.9.22 y 4 pruebas de humo. Tag `legacy-kotlin19` |
| `afa4379` Add containers for the legacy Kotlin tracks | A8: `docker/legacy-kotlin/`, `scripts/legacy.sh`, `docs/LegacyKotlin.md`. Tag `legacy-containers` |
| Close phase A: README index and CLAUDE.md rewrite | A9: README con documentos, requisitos, índice de secciones, tracks legacy y convenciones; CLAUDE.md reescrito. Tag `phase-a` |

**A7 (13 de noviembre de 2026):** la base pasó a **Kotlin 2.4.21**, Gradle 9.9.1, Hibernate 7.4.15 y librerías al
día, por subpasos y sin romper nada: mismas 123 pruebas en verde y misma salida en los `Main` salvo mensajes de
librerías. K2 dio 3 avisos nuevos, ya corregidos. Versiones en [`docs/Versions.md`](docs/Versions.md); lo que cambió, en
[`22-legacy-kotlin19/docs/k2-notes.md`](22-legacy-kotlin19/docs/k2-notes.md).

**A6 (13 de noviembre de 2026):** `22-legacy-kotlin19` es un proyecto aparte, **fuera del composite**, con su wrapper
(Gradle 9.2.1), Kotlin 1.9.22 y `.sdkmanrc`. Se corre con `cd 22-legacy-kotlin19 && ./gradlew test`. La prueba de humo
falla a propósito si el track deja de correr con K1: comprueba la versión y el `toString()` de una lambda, que con K2
cambia por `invokedynamic`.

**A8 (13 de noviembre de 2026):** `./scripts/legacy.sh 19` corre el track 19 en `eclipse-temurin:21-jdk` (fijada por
digest, `arm64` nativo en Apple Silicon) con las mismas 4 pruebas que en nativo. Las imágenes de los tracks 14
(Temurin 11) y 16 (Temurin 17) también están fijadas y corren en `arm64`; sus tracks se crean en B5.

Tags anotados: `baseline-1.9.22` (curso v1 de proyecto único), `monorepo-1.9.22` (fin de A5), `monorepo-2.4.21`
(fin de A7), `legacy-kotlin19` (fin de A6), `legacy-containers` (fin de A8) y `phase-a` (fin de A9 y de la fase A).

### Historial de la fase B

| Commit | Qué |
|---|---|
| `017c9d3` Add cli and repl tutorials | B1: `cli` y `repl` con sus `check.sh` |
| `3919d39` Add install, decompile and debug tutorials | B1 cerrado: 5 tutoriales |
| `890e295` Plan the syllabus rewrite (B0 batch 0) and measure version tags | B0: `docs/Temario-Plan.md`, prompt por tanda, `scripts/version-probes.sh`, tanda 0 |
| `b1e3280` Write syllabus batch 1 | B0, tanda 1: tutoriales y `basics`, `control`, `strings`, `oop`, `io` |
| `984b5d0` Write syllabus batch 2 | B0, tanda 2: `collections`, `functional`, `json`, `datetime`, `exceptions` |
| `edc5e49` Review phase B status and propose lesson guides (B0″) | Revisión de estado; tag `b0-status-review` |
| `cdfd9b2` Add lesson guides (B0″) | B0″: `docs/lessons/` con índice, plantilla y 12 guías; regla de sincronía en CLAUDE.md y README |
| `237c387` Generate the case data (B0′ batch 1) | B0′, tanda 1: paquete `datagen` y los cuatro archivos de datos; 130 pruebas |
| `9ac0342` Move json and io to the cases (B0′ batch 2) | B0′, tanda 2: `json` sobre `ticks.json` (`Tick`, `MarketEvent`), `io` sobre el extracto con conciliación exacta; 134 pruebas |
| `7e66694` Move basics, oop and exceptions to the Bank (B0′ batch 3) | B0′, tanda 3: `Money`, `Order` sellado, `AccountType`, `BankException`, `TransferService`, `MoneyAsString`; 140 pruebas |
| `abc737d` Move the rest of 01-language to the cases (B0′ batch 4) | B0′, tanda 4: `control`, `strings`, `collections`, `functional`, `datetime`; 141 pruebas |
| `5298bfe` Write syllabus batch 3 | B0, tanda 3: `generics`, `inline`, `interop`, `delegation`, `dsl`, `kotlin2`; 12 etiquetas medidas más JSpecify y `default` |
| `5089107` Write syllabus batch 4 | B0, tanda 4: `02-coroutines` (10 entradas); opt-in de kotlinx-coroutines 1.11.0 medidos |
| `26dff26` Write syllabus batch 6 | B0, tanda 6: `05-testing`, `06-compiler`, `07-design` (12 entradas) |
| `9ede67e` Write syllabus batch 5 | B0, tanda 5: `03-reactive` y `04-data` (10 entradas) |
| `fdbb8c1` Move 04-data to the Bank ledger (B0′ batch 5) | B0′, tanda 5: `LedgerRepository` y su contrato en los tres motores, ledger en JPA; 153 pruebas |
| `1f36e73` Rewrite the exercises for the cases (B0′ batch 6) | B0′ cerrado: enunciados y ruta 🎯 en los casos, `orders.json` y `sales.csv` borrados |
| `44ecba4` Write syllabus batch 7 | B0, tanda 7: `08-build`, `09-spring`, `10-ktor` (11 entradas) |
| `338690c` Write syllabus batch 8 | B0, tanda 8: `11-multiplatform`, `12-android` (11 entradas) |
| `48e17ef` Write syllabus batch 9 | B0, tanda 9: tracks legacy, `25-interview`, `26-kotlin-next` y los dos 🐉 |
| `0debc16` Close the syllabus mechanically (B0 batch 10) | B0, tanda 10 sin la revisión: trazabilidad y consistencia |
| `77120bb` Close B0 and record the review decisions | B0 cerrado con la revisión; preguntas 1 a 3 respondidas; órdenes con generador |
| `c1c4e5b` Add the order generator to datagen | `orderStream`: comandos `Place`/`Cancel` con semilla, sin archivo; 157 pruebas |
| `b6e9b60` Number the tutorials | `tutorials/01-install` … `05-debug`; 136 comprobaciones en verde |
| `6247429` Propose study guides with theory and sources | Sección 13 del plan y la guía de `basics` como muestra |
| `1aed5c7` Adopt the study guide format | Reglas, plantilla y `scripts/check-guide-links.sh` |
| `d0a14f1` Write study guides 1: control, strings, oop | Guías de estudio de `control`, `strings` y `oop` |
| `b32d9cc` Move the database ports to the 20000 range | PostgreSQL 25432 y MySQL 23306 |
| Write study guides 2: io, collections, functional, json | Guías de estudio de `io`, `collections`, `functional` y `json` |

**Remoto:** `origin` en GitHub desde el 13 de noviembre de 2026. Antes de cada push se repite el escaneo de
referencias externas sobre `git log -p --all`, y se suben los tags con `git push origin main --follow-tags`.

### Pendiente, en el orden en que se va a hacer

Orden decidido el 22 de noviembre de 2026: B0′ se adelanta, porque sus tandas 1 a 4 reescriben lecciones cuyo
temario ya está escrito (tandas 1 y 2 de B0), y así el código deja antes el dominio de pedidos.

1. ~~**B0″ · Guías de lección**~~ ✅ (22 de noviembre de 2026): índice, plantilla y 12 guías en `docs/lessons/`.
2. ~~**B0′ · tandas 1 a 4**~~ ✅ (22 de noviembre de 2026): datos y generador, `json`/`io`, `basics`/`oop`/`exceptions` y
   el resto de `01-language`; el código de `01-language` ya está en los casos.
3. **B0 · tandas 3, 4 y 6** (no dependen de otras) y luego la **5** (espera a la 4). Tandas 3 a 6 ✅ (22 de
   noviembre de 2026).
4. ~~**B0′ · tandas 5 y 6**~~ ✅ (22 de noviembre de 2026): `04-data` con el ledger (se apoya en la tanda 5 de B0); ejercicios, ruta 🎯 y borrado de
   `orders.json`/`sales.csv`. Termina antes de B2.
5. ~~**B0 · tandas 7, 8, 9 y 10**, con la revisión juntos al final~~ ✅ (22 de noviembre de 2026: revisión aprobada).
6. **B2–B10** según la [sección 8](#8-fase-b-generar-el-resto).

**Preguntas abiertas:** las de la [sección 10](#10-para-decidir-en-la-revisión) quedaron respondidas el 22 de noviembre de 2026;
la propuesta de guías de estudio ([sección 13](#13-guías-de-estudio-teoría-y-fuentes-propuesta)) quedó aprobada.

---

## 1. Decisiones

| # | Decisión | Estado |
|---|---|---|
| 1 | Un solo repo git con varios proyectos (monorepo), como `java-champion-review` y `groovy-for-java-devs` | ✅ acordado |
| 2 | **Repo público**: sin referencias a empresas ni procesos de selección; 🎯 pasa a significar "prioritario" | ✅ hecho en el curso v1 |
| 3 | Ejercicios con la escala de `java-champion-review`: 🟢 🟡 🟠 🔴 ⚡ 🐉 🎤 🎯, de 25 a 40 por lección más su ⚡ | ✅ acordado |
| 4 | Proyecto base en la **última Kotlin estable** y tracks legacy aparte, cada uno con su compilador real | ✅ acordado |
| 5 | Los tracks legacy corren en **contenedores** (idea de `groovy-for-java-devs/docs/LegacyJdks.md`) y también en nativo | ✅ probado con el track 19 (A8), en contenedor y en nativo |
| 6 | El repo se sigue llamando `kotlin-for-java-devs`, igual que `groovy-for-java-devs` | ✅ acordado |
| 7 | Primero se **mueve sin cambiar comportamiento** (Kotlin 1.9.22) y se prueba, corrigiendo solo lo necesario; la migración a 2.x va en otra sesión | ✅ acordado |
| 8 | **Código en inglés, comentarios y documentos en español**, como en `java-champion-review` | ✅ revisado en el curso v1 |
| 9 | Git con `user.name` y `user.email` locales del repo; mensajes de commit en inglés | ✅ hecho |
| 10 | `kotlin-for-java-devs.code-workspace` copiado de `java-champion-review` e ignorado por git (ajustes personales de VS Code) | ✅ hecho |
| 11 | El caso de punta a punta pasa a **dos casos de finanzas con un puente**: Banco (ledger de doble partida, transferencias, conciliación) y Mercado (feed de precios, velas, libro de órdenes, P&L); cada ejecución se liquida en el ledger. USD como base, sin derivados ni cortos | ✅ acordado el 13 nov 2026; diseño en [`docs/Casos.md`](docs/Casos.md) |

El punto 7 importa: si mover y actualizar van juntos, cuando algo falle no se sabe si fue el movimiento o la
versión. Además, la lista de lo que se rompe al pasar de 1.9.22 a 2.x sirve de contenido para el track de 1.9.

---

## 2. Versiones: base y tracks legacy

### Base

| Pieza | Versión | Nota |
|---|---|---|
| Kotlin | **2.4.21** ✅ | Última estable, confirmada en Maven Central el 13 nov 2026. 2.5.0 está en RC y se espera para diciembre de 2026 |
| JDK | 21 (toolchain) | Igual que los repos de Java y Groovy. Gradle 9 exige JDK 17+ para correr |
| Gradle | **9.9.1** ✅ | Última estable al hacer A7 (los repos hermanos están en 9.9.0); KGP 2.4.21 corre sin avisos |
| kotlinx-serialization, Kermit, Hibernate, drivers, JUnit | Fijadas en A7 ✅ | serialization 1.13.0, Kermit 2.2.0, Hibernate 7.4.15, JUnit 5.14.4…; detalle en `docs/Versions.md` |

### Qué grupos de versiones vale la pena ver en legacy

Repaso de las versiones de Kotlin y cuáles se encuentran todavía en código real:

| Versión | Año | Qué trajo o quitó | ¿Aparece en código vivo? |
|---|---|---|---|
| 1.0–1.2 | 2016–17 | Lenguaje inicial, corrutinas experimentales | Casi no: se migró todo |
| 1.3 | 2018 | Corrutinas estables, `Result`, contratos e inline classes experimentales | Poco; quedó absorbido por 1.4 |
| **1.4** | 2020 | `fun interface`, explicit API mode, nueva inferencia, coma final; **jvmTarget por defecto 1.6**, backend JVM viejo, `kotlin-android-extensions` (synthetics) y kapt | **Sí**: Android de 2019–2021 y servicios Spring Boot 2.3–2.4 que nadie actualizó |
| 1.5 | 2021 | Backend JVM IR por defecto, `value class` y `sealed interface` estables, `@JvmRecord` | Transición |
| **1.6–1.8** | 2021–22 | `when` exhaustivo obligatorio, `Duration` estable, `T & Any`, opt-in estable, inferencia de builders | **Sí, mucho**: Spring Boot 2.7 (javax, Kotlin 1.6.21) es la base de muchísimos backends corporativos |
| **1.9** | 2023 | `data object`, `entries`, `..<`, último compilador K1 | **Sí**: el último paso antes de K2 y el estado actual de este repo |
| 2.0–2.3 | 2024–25 | K2, smart casts mejorados, lambdas con `invokedynamic`, guard conditions, `break`/`continue` no locales, `$$` en plantillas | Se cubre en el **base** con etiquetas `[2.0]`, `[2.2]`… |
| 2.4 | 2026 | Context parameters y explicit backing fields estables ⚠️ | Base |

**Propuesta: tres tracks legacy**, uno por cada corte que se encuentra en proyectos reales:

| Track | Kotlin | Representa | Gradle | JDK | Por qué este corte |
|---|---|---|---|---|---|
| `20-legacy-kotlin14` | **1.4.32** | Android clásico y Boot 2.3–2.4 | 6.8.x ⚠️ | 8 y 11 | Antes de `value class`, `sealed interface`, JVM IR y `jvmToolchain` (llegó en 1.5.30). Synthetics, kapt, `BroadcastChannel` antes de `StateFlow` |
| `21-legacy-kotlin16` | **1.6.21** | Spring Boot 2.7 con `javax` | 7.6.x ⚠️ | 11 y 17 | La versión que trae el BOM de Boot 2.7, el legacy más común en backend. Aquí vive la migración `javax` → `jakarta` |
| `22-legacy-kotlin19` | **1.9.22** | Último K1 | 9.2.1 (el actual) | 21 | Es lo que hay hoy. Su lección central: **migrar a K2** con lo que se rompió en el paso A7 |

Lo que se deja fuera, a propósito:

- **1.0–1.3**: no vale la pena un proyecto; las diferencias se cuentan en `docs/LegacyKotlin.md`.
- **1.5, 1.7 y 1.8**: los cubren los tracks vecinos con etiquetas de versión. Un track por versión menor sería
  repetir casi todo.
- **`-language-version` en un compilador moderno**: medido con 2.4.21, el mínimo es `2.0` (y deprecado); `1.9` da
  `language version 1.9 is no longer supported`. Además no reproduce
  el bytecode, el backend viejo ni el plugin de Gradle de la época. Por eso los tracks usan el compilador real.

---

## 3. Estrategia de contenedores

Viene de `groovy-for-java-devs/docs/LegacyJdks.md`, con una ventaja: los JDK que necesita Kotlin legacy (8, 11 y
17) tienen **imágenes multi-arquitectura** (`eclipse-temurin`, `azul/zulu-openjdk`) que corren nativas en Apple
Silicon. El problema de JDK 6 y 7, que solo existen para `amd64`, no aparece aquí.

**Qué se conteneriza:** solo el JDK de la época. Gradle llega con el wrapper de cada track y el compilador de Kotlin
con su plugin. La imagen no necesita nada más.

```text
docker/
├── postgres/            ya existe: PostgreSQL 17.2 en 127.0.0.1:25432
├── mysql/               ya existe: MySQL 8.4.3 en 127.0.0.1:23306
└── legacy-kotlin/
    ├── README.md        imágenes, digests, cómo correr cada track y cómo hacerlo en nativo
    └── compose.yaml     servicios k14 (JDK 11), k16 (JDK 17), k19 (JDK 21), con el repo montado en /work
                         y un volumen con nombre para ~/.gradle (caché compartida entre corridas)
scripts/
└── legacy.sh            ./scripts/legacy.sh 14 test  →  docker compose run --rm k14 ./gradlew test
```

- Las imágenes se fijan por **digest** (`@sha256:…`), como en el plan de Groovy, para que no cambien solas.
- Las pruebas de JDBC del track 16 (Boot 2.7) hablan con los contenedores de `docker/postgres` y `docker/mysql` por
  la red de Docker, no por `127.0.0.1`.
- **En nativo**: cada track lleva su `.sdkmanrc` (`java=11.x-zulu`, etc.). `sdk env` y `./gradlew test`.
- Los tracks 16 y 19 también pueden usar `jvmToolchain` de Gradle; el 14 no, porque su plugin es anterior a 1.5.30.
  Ahí el contenedor o SDKMAN no son opcionales.
- Probado en A8 con el track 19: imagen `arm64` nativa, volumen `gradle-home` (primera corrida ~1 min, siguientes ~4 s)
  y `build/` compartido entre contenedor y nativo sin limpiar.
- ⚠️ Pasa a B5: Gradle 6.8 dentro de Temurin 11 en arm64, IntelliJ 2026 importando un proyecto con Gradle 6.8,
  la caché compartida entre wrappers de Gradle distintos y el acceso a las bases desde `k16`.

---

## 4. Arquitectura del monorepo

```text
kotlin-for-java-devs/
├── README.md, CHECKLIST.md, CLAUDE.md
├── kotlin-for-java-devs.code-workspace   VS Code (ignorado por git, como en java-champion-review)
├── docs/
│   ├── Temario.md         temario completo: secciones, lecciones, ⚡, trazabilidad
│   ├── Versions.md        matriz Kotlin × Gradle × JDK × librerías, por sección
│   ├── LegacyKotlin.md    qué cambia de 1.0 a 2.x, cómo correr los tracks (contenedor y nativo)
│   ├── JavaToKotlin.md    chuleta Java → Kotlin (sale del Temario actual)
│   └── EffectiveKotlin.md track transversal, como EffectiveJava.md
├── settings.gradle.kts    composite build: includeBuild de las secciones en la versión base
├── build.gradle.kts       tareas agregadas build/test/check/clean
├── gradlew, gradle/       un wrapper en la raíz (el de la base)
├── docker/, scripts/
├── tutorials/             nivel 0: solo Markdown y scripts .main.kts
├── 01-language/ … 11-multiplatform/    en el composite
├── 12-android/            FUERA: su versión de AGP exige su propio wrapper
├── 20-legacy-kotlin14/    FUERA: wrapper y JDK propios, en contenedor
├── 21-legacy-kotlin16/    FUERA
├── 22-legacy-kotlin19/    FUERA
├── 25-interview/          en el composite
└── 26-kotlin-next/        FUERA hasta que 2.5 salga estable: radar de la siguiente versión
```

**Reglas** (las mismas de los repos de Java y Groovy):

- Cada sección tiene su `settings.gradle.kts` y su `build.gradle.kts`, **sin padre común**. Las versiones se
  repiten a propósito; una prueba o tarea del root revisa que coincidan (⚠️ todavía no existe: va con `08-build`,
  fase B9; mientras tanto, `docs/Versions.md` y CLAUDE.md piden cambiarlas juntas).
- Correr una sección sola: `./gradlew -p 01-language test`. Una lección: `./gradlew -p 01-language run -Plesson=strings`.
- Todo: `./gradlew test` en la raíz (solo el composite). Los tracks legacy y Android se corren aparte.
- Paquete raíz `dev.raul.oskar.learning.kotlin`; cada lección es un subpaquete, igual que hoy.
- **Código en inglés, comentarios y documentos en español**: identificadores, paquetes, directorios, módulos y
  nombres de prueba en inglés; comentarios, KDoc, tutoriales, `Exercises.md` y documentos en español.

**Niveles de build:**

| Nivel | Qué lleva | Secciones |
|---|---|---|
| 0 · Sin build | `.md`, scripts `.main.kts`, `kotlinc` a mano | `tutorials` |
| 1 · Gradle simple | Kotlin JVM + `kotlin.test`, a lo más un plugin | `01`, `02`, `07`, `25` |
| 2 · Gradle con deps | Nivel 1 + librerías del tema (y Docker para BD) | `03`, `04`, `05`, `06` |
| 3 · Framework | Spring Boot o Ktor con sus plugins | `09`, `10` |
| 4 · Multi-módulo y plataformas | KMP, Compose, Android (AGP) | `11`, `12` |
| 5 · Herramientas de build | Convention plugins, detekt, Kover, Dokka, native image | `08` |
| 6 · Legacy en contenedor | Wrapper viejo + JDK de la época | `20`, `21`, `22` |

---

## 5. Secciones y lecciones

✅ = existe hoy (se mueve) · 🆕 = nueva. El detalle por lección (mapeo Java → Kotlin, trampas, versión de cada
concepto) va en `docs/Temario.md`, que se reescribe en el paso B0.

| Sección | Lecciones | ⚡ Saiyajin (miniproyecto que cierra la lección) |
|---|---|---|
| `tutorials/` | 🆕 `install` (SDKMAN, brew, toolchains, IntelliJ), `cli` (`kotlinc`, `kotlin`, `-include-runtime`, jar ejecutable), `repl` (REPL, scripts `.main.kts`, Kotlin Notebook), `decompile` (*Show Kotlin Bytecode*, `javap`, CFR, metadatos `@Metadata`), `debug` (IntelliJ, depurador de corrutinas, `DebugProbes`, `jdb`) | `decompile`: catálogo "qué genera kotlinc" de 20 construcciones con bytecode y decompilado, verificado con pruebas |
| `01-language` | ✅ `basics`, `control`, `strings`, `oop`, `collections`, `functional`, `exceptions`, `io`, `datetime`, `json` · 🆕 `generics` (varianza, `*`, `reified`, `T & Any`, `typealias`), `inline` (`noinline`, `crossinline`, contratos, `value class`), `interop` (platform types, `@Jvm*`, Java y Kotlin en el mismo módulo), `delegation` (delegados propios, `by map`, delegación de clase), `dsl` (`@DslMarker`, builders), `kotlin2` (lo de 2.0 a 2.4 con su etiqueta) | `basics`: calculadora de precisión arbitraria · `oop`: evaluador de expresiones con `sealed` · `collections`: `HashMap` propio comparado con el del JDK · `functional`: librería de operadores de `Sequence` · `json`: serializador propio para un formato binario · `dsl`: DSL de HTML type-safe |
| `02-coroutines` | 🆕 `fundamentals` (`suspend`, structured concurrency, `Job`), `cancellation` (cooperativa, `withTimeout`, excepciones, `SupervisorJob`), `dispatchers` (`withContext`, `limitedParallelism`, bloquear hilos), `channels` (`Channel`, `select`, productores), `flow` (operadores, `flowOn`, buffer, conflate), `hotflows` (`StateFlow`, `SharedFlow`), `sync` (`Mutex`, `Semaphore`, atomicfu, `@Volatile` y el JMM), `internals` (CPS, máquina de estados decompilada, `Continuation` a mano), `testing` (`runTest`, tiempo virtual, Turbine), `loom` (corrutinas frente a hilos virtuales) | `internals`: scheduler de corrutinas propio sobre `createCoroutine` · `flow`: operadores propios con backpressure · `loom`: servidor de chat con hilos de plataforma, virtuales y corrutinas bajo carga |
| `03-reactive` | 🆕 `reactor` (`Mono`/`Flux` desde Kotlin, extensiones), `rxjava`, `bridges` (`kotlinx-coroutines-reactor`, `awaitSingle`, `asFlow`, `asFlux`), `backpressure` | Mismo pipeline en Reactor, RxJava y Flow, con medición de throughput y memoria |
| `04-data` | ✅ `jdbc` (SQLite, PostgreSQL, MySQL), `jpa` · 🆕 `exposed`, `jooq`, `r2dbc`, `sqldelight` | La misma app con JDBC, JPA, Exposed, jOOQ y R2DBC: SQL generado y benchmark |
| `05-testing` | 🆕 `kotest` (estilos, property-based), `mockk` (incluido `coEvery`), `assertions` (Kotest, AssertK, power-assert), `testcontainers` | Mini MockK: proxies, `every`/`verify` y matchers, para entender cómo graba las llamadas |
| `06-compiler` | 🆕 `bytecode` (lambdas, `companion`, `value class`, propiedades), `reflection` (`KClass`, `KProperty`, `kotlin-reflect` y su costo), `ksp` (KSP frente a kapt), `compilerplugins` (all-open, no-arg, serialization, power-assert, uno propio con FIR) | `ksp`: generador de builders y mappers con errores de compilación propios |
| `07-design` | 🆕 `idioms` (Effective Kotlin), `gof` (qué patrones desaparecen en Kotlin), `fp` (Arrow: `Either`, `Raise`, optics), `architecture` (hexagonal + DDD) | Rescate de legado: una clase dios portada de Java, con pruebas de caracterización y refactor idiomático |
| `08-build` | 🆕 `gradle` (Kotlin DSL, version catalogs, convention plugins en `build-logic`), `quality` (detekt, ktlint, Kover), `docs` (Dokka), `packaging` (fat jar, jlink, GraalVM native image) | Convention plugin del repo que genera el índice de lecciones y ejercicios del README |
| `09-spring` | 🆕 `boot` (plugin `kotlin-spring`, constructor injection, `@ConfigurationProperties`, validación), `springdata`, `webflux` (corrutinas, `Flow`, `coRouter`, `WebClient`), `springtesting` (MockMvc DSL, `WebTestClient`, SpringMockK) | API reactiva de punta a punta con WebFlux + R2DBC + corrutinas, con prueba de carga frente a la versión MVC |
| `10-ktor` | 🆕 `server` (routing, plugins, auth, WebSockets), `client`, `ktortesting` | Chat con `Flow` sobre WebSockets |
| `11-multiplatform` | 🆕 `kmp` (`expect`/`actual`, source sets, targets), `composemp` (desktop, wasm, iOS), `shared` (Ktor client + SQLDelight + kotlinx-datetime), `web` (Kotlin/JS y Wasm) | Módulo compartido con la lógica de negocio, consumido desde desktop y web |
| `12-android` | 🆕 `android` (Activity, ciclo de vida), `compose`, `architecture` (ViewModel, `StateFlow`, navegación), `androiddata` (Room, DataStore), `di` (Hilt frente a Koin), `background` (WorkManager), `androidtesting` (pruebas de Compose, Robolectric) | App offline-first con sincronización |
| `20-legacy-kotlin14` | 🆕 `language14` (lo que no existía todavía), `coroutines14` (`BroadcastChannel`, `ConflatedBroadcastChannel`), `android14` (synthetics y kapt, solo lectura y demo JVM), `migration14` (de 1.4 a 1.6) | Migrar una mini app 1.4 a 2.x paso a paso, con un commit por salto de versión |
| `21-legacy-kotlin16` | 🆕 `language16`, `boot27` (Spring Boot 2.7 con `javax`, WebFlux + corrutinas de la época), `migration16` (Boot 2.7 → 3, `javax` → `jakarta`) | La API de `09-spring` portada a Boot 2.7 y de vuelta |
| `22-legacy-kotlin19` | 🆕 `language19`, `k2migration` (lo que se rompió en A7 y por qué), `compat` (`apiVersion`, `languageVersion`, compatibilidad binaria y `@Metadata`) | Matriz de compatibilidad: una librería compilada en 1.9 consumida desde 2.4, y al revés |
| `25-interview` | 🆕 `livecoding` (simulacros en Kotlin), `prreview` (leer un PR: `!!`, `lateinit` abusado, scope functions anidadas, `data class` mutables, `runCatching` que se traga la cancelación) | Cinco simulacros cronometrados de 35 minutos |
| `26-kotlin-next` | 🆕 `kotlin25` (lo nuevo de 2.5 cuando salga estable) | — |

**🐉 Jefes finales** (cruzan secciones):

1. **Mini `kotlinx.coroutines`**: dispatcher, `Job` con jerarquía, cancelación, `Channel` y un `Flow` frío, con las
   mismas pruebas que la librería real (`02` + `06`).
2. **Producto full-stack**: backend (WebFlux o Ktor) + cliente KMP (Android, desktop y web) con sincronización
   offline, CI y empaquetado nativo (`04` + `09`/`10` + `11`/`12` + `08`).

**Lo que viene de `java-champion-review`** (revisado con su Temario): los tutoriales de CLI, REPL, decompilación y
depuración (`tutorials`) · metaprogramación → `06-compiler` · `16-testing` → `05-testing` · `17-quality`,
`19-gradle`, `21-docs` y `06-packaging` → `08-build` · `15-design` → `07-design` · `03-concurrency` → `02-coroutines`
(JMM, sincronización, problemas clásicos) · `23-legacy-jdks` → tracks `20`–`22` · `25-interview` → `25-interview` ·
*Effective Java* → `docs/EffectiveKotlin.md`. **No se trae**: GUI (AWT, Swing, JavaFX), sonido, imágenes, Ant,
Jenkins, JPMS y JVM internals a fondo (GC, JIT): ya están en el repo de Java y no cambian con Kotlin.

---

## 6. Ejercicios: niveles de java-champion

| Marca | Significado |
|---|---|
| 🟢 | Fácil: una idea de la lección. 5–15 min |
| 🟡 | Medio: combina dos o tres ideas o tiene bordes delicados. 15–30 min |
| 🟠 | Difícil: diseño, algoritmo no trivial o comportamiento que hay que demostrar. 30–90 min |
| 🔴 | Muy difícil: experimento con medición o diseño grande. Una sesión |
| ⚡ | **Saiyajin**: miniproyecto que cierra la lección. Varias sesiones |
| 🐉 | **Jefe final**: proyecto que cruza varias secciones |
| 🎤 | Pregunta típica de entrevista |
| 🎯 | Prioritario: ruta mínima recomendada |
| `[1.4]` `[1.9]` `[2.0]` `[2.4]` | Versión de Kotlin en que aparece (estable) |
| `[exp N]` / `[dep N]` / `[rm N]` | Experimental, deprecado o removido en la versión N |

- **De 25 a 40 ejercicios por lección más su ⚡.** Las lecciones actuales tienen de 23 a 25: se completan hasta
  25 como mínimo, se marcan los 🎤 y se agrega el ⚡.
- Cada enunciado conserva **firma sugerida** y **casos mínimos**.
- Se mantiene la convención actual: solución en `<lección>/practice/ExNN<Nombre>.kt`, prueba en el espejo, stubs con
  `TODO("ejercicio NN")`, `object ExNN { … }` si dos ejercicios chocan de nombre.
- Distribución orientativa por lección: ~30 % 🟢, ~35 % 🟡, ~25 % 🟠, ~10 % 🔴, más 1 ⚡.

---

## 7. Fase A: migrar lo que hay

Objetivo: **las mismas 123 pruebas en verde** (18 `SKIPPED` sin Docker), ahora repartidas en proyectos, antes de
escribir contenido nuevo. Cada paso termina en un commit.

| Paso | Qué | Verificación |
|---|---|---|
| **A0 · Línea base** ✅ | `git init` con usuario local; commit 1 solo con `.gitignore` (el del ecosistema JVM de `java-champion-review`); commit 2 con el curso v1; commit 3 con este plan. Tag `baseline-1.9.22` sobre el commit 2 | `./gradlew test` → 123 pruebas |
| **A1 · Repo público** ✅ | Ver la lista de abajo. Se hizo **antes** de mover, para que los diffs del movimiento sean solo renombres | Pruebas en verde; `grep` de referencias externas vacío |
| **A2 · Esqueleto** ✅ (`CHECKLIST.md` creado al cierre de la sesión) | `docs/`, `CHECKLIST.md`, `settings.gradle.kts` del root como composite, `build.gradle.kts` del root con tareas agregadas, carpetas vacías de las secciones nuevas. `Temario.md` → `docs/Temario.md` | `./gradlew help` en la raíz |
| **A3 · `01-language`** ✅ 85 pruebas | `git mv` de `basics`, `control`, `strings`, `oop`, `collections`, `functional`, `exceptions`, `io`, `datetime`, `json` (main y test) y de `resources/data/`. Build propio: `jvm` + `serialization` + Kermit, `run -Plesson`, `standardInput` | `./gradlew -p 01-language test` |
| **A4 · `04-data`** ✅ 38 pruebas | `git mv` de `jdbc/**` y `jpa`, más `META-INF/persistence.xml` y `simplelogger.properties`. Build propio: `jvm` + `jpa` + `allopen`, drivers, Hibernate, H2, slf4j, JUnit BOM | `./gradlew -p 04-data test`, con y sin Docker |
| **A5 · Cierre del movimiento** ✅ tag `monorepo-1.9.22` | Borrar `src/` y el build del root viejo. Corregir comandos y rutas en README, CLAUDE.md, `docs/Temario.md` y los `Exercises.md`. Importar en IntelliJ | `./gradlew test` en la raíz: 123 pruebas, mismos nombres que la línea base |
| **A6 · Track 1.9** ✅ tag `legacy-kotlin19` | `22-legacy-kotlin19/` con wrapper propio (9.2.1), build en 1.9.22, `.sdkmanrc` y una prueba de humo. Primer track porque su build ya se sabe que funciona | `cd 22-legacy-kotlin19 && ./gradlew test` en nativo: 4 pruebas. (`./gradlew -p 22-legacy-kotlin19 test` desde la raíz también pasa, pero con el Gradle 9.9.1 de la raíz y sus avisos) |
| **A7 · Base a 2.x** ✅ | `01` y `04` a Kotlin 2.4.21, Gradle 9.9.1 y librerías al día (Hibernate 7.4.15), en 4 subpasos. Cada cosa que se rompe o cambia de comportamiento se anota en `22-legacy-kotlin19/docs/k2-notes.md` | 123 pruebas en verde en 2.4 |
| **A8 · Contenedores** ✅ tag `legacy-containers` | `docker/legacy-kotlin/` (compose + README), `scripts/legacy.sh`, `docs/LegacyKotlin.md`. Probar con el track 19 en Apple Silicon | `./scripts/legacy.sh 19 test` igual que en nativo |
| **A9 · Documentación** ✅ tag `phase-a` | README nuevo (índice de secciones como el de Groovy), CLAUDE.md reescrito (versiones, arquitectura, niveles, leyenda, reglas de sincronía). `docs/Versions.md` (A7) y `CHECKLIST.md` ya existen | Revisión juntos |

**Lo que se limpió en el curso v1 (A1)**, porque el repo va a ser público:

- Ninguna referencia a empresas, proyectos externos ni procesos de selección en documentos, código, pruebas ni
  ejercicios. Verificado con `grep` antes de cada commit.
- La sección "Plan de hoy" del README pasó a **Ruta prioritaria** y 🎯 pasó a significar "prioritario".
- Los ejercicios que partían de código externo ahora parten de código del propio repo (`json/Model.kt`, un `main`
  monolítico que se escribe en `oop` 24).
- **Lo que se queda**: las trampas en sí (`split("\\/")` literal, `explicitNulls` con opt-in, `BigDecimal` desde
  `String`) como trampas generales.
- **Caso de punta a punta reemplazado**: el caso anterior se quitó por completo (datos, modelo, ejercicios y fechas de
  ejemplo). Quedó un dominio **provisional** de pedidos de tienda (`data/orders.json`, `OrderEvent`, `ShippingMethod`,
  `OrderService`) para que las lecciones sigan corriendo, hasta elegir uno de los casos de la
  [sección 11](#11-caso-de-punta-a-punta-ideas-de-reemplazo).
- **Convención revisada**: identificadores, paquetes, nombres de prueba y directorios en inglés; comentarios, KDoc,
  `Exercises.md` y documentos en español. El código ya la cumplía.

---

## 8. Fase B: generar el resto

Orden propuesto: primero lo pedido explícitamente (corrutinas, reactivo, legacy, web y móvil), y al final lo
transversal.

| Fase | Contenido | Entregable |
|---|---|---|
| **B0′** ✅ | Elegir el caso de finanzas (sección 11) y reemplazar el dominio provisional de pedidos en datos, lecciones y ejercicios 🎯. Caso elegido y diseñado en [`docs/Casos.md`](docs/Casos.md); falta el reemplazo en 6 tandas | Ruta prioritaria del README rehecha |
| **B0″** ✅ | Guías de lección en `docs/lessons/` ([sección 12](#12-guías-de-lección-docslessons)): plantilla, índice y una guía por cada lección con código | 12 guías (10 de `01-language`, `jdbc`, `jpa`); desde ahí, cada lección nueva nace con la suya |
| **B0** ✅ | `docs/Temario.md` completo: cada lección con mapeo Java → Kotlin, trampas, etiquetas de versión y su ⚡. Se hace por tandas según [`docs/Temario-Plan.md`](docs/Temario-Plan.md) | Revisión juntos antes de escribir código |
| **B1** ✅ | `tutorials/`: `install`, `cli`, `repl`, `decompile`, `debug` | 5 tutoriales con sus scripts y un `check.sh` cada uno (136 comprobaciones, 72 ejercicios) |
| **B2** | Ejercicios de las 12 lecciones actuales llevados a la nueva escala: mínimo 25, 🎤, ⚡ | ~300 → ~330 ejercicios + 12 ⚡ |
| **B3** | `02-coroutines` (10 lecciones) y `03-reactive` (4) | Código, pruebas, `Exercises.md` |
| **B4** | Lecciones nuevas de `01-language` (`generics`, `inline`, `interop`, `delegation`, `dsl`, `kotlin2`) | |
| **B5** | Tracks `20` y `21` en contenedor; contenido de `22` con las notas de A7 | Los tres tracks corriendo con `scripts/legacy.sh` |
| **B6** | `09-spring` (con WebFlux) y `10-ktor` | |
| **B7** | `11-multiplatform` y `12-android` | Requiere Android SDK; iOS requiere Xcode |
| **B8** | `04-data` nuevo (Exposed, jOOQ, R2DBC, SQLDelight), `05-testing`, `06-compiler` | |
| **B9** | `07-design`, `08-build`, `25-interview`, `docs/EffectiveKotlin.md` | |
| **B10** | Los dos 🐉 y `26-kotlin-next` cuando 2.5 salga estable | |

En cada fase se actualizan en el mismo cambio `CHECKLIST.md` (estado y bitácora), el índice del README, los
conteos de pruebas y ejercicios y, si cambia una versión, `docs/Versions.md`.

**Tamaño estimado:** unas 60 lecciones en 18 secciones más los tutoriales, del orden de 1,800 ejercicios a 30 por
lección. Es parecido a `java-champion-review` (79 lecciones). Por eso va por fases y con revisión en B0.

---

## 9. Riesgos y pendientes

- ⚠️ **Versiones sin confirmar**: Gradle exacto para KGP 1.4.32 y 1.6.21, y si `kotlin-test-junit5` existe para 1.4 (si no, JUnit 4 en ese track).
- **IntelliJ y Gradle viejo**: puede que un IntelliJ actual no importe bien un proyecto con Gradle 6.8. Alternativa:
  abrir el track como proyecto aparte o trabajarlo solo por CLI y contenedor.
- **Android** necesita Android SDK y emulador, y su AGP fija la versión de Gradle (AGP 9.0 exige Gradle 9.1+).
  **iOS** necesita Xcode; sin él, KMP se hace con targets desktop y wasm.
- ~~**Hibernate 6 → 7** en A7 puede romper la lección `jpa`.~~ Se hizo como subpaso A7.4 y no rompió nada.
- **El composite usa el Gradle de la raíz** para todas las secciones incluidas. Si una sección necesita otro Gradle,
  sale del composite (como `12-android`).

---

## 10. Para decidir en la revisión

1. ~~¿Los tres tracks legacy (**1.4.32**, **1.6.21** con Boot 2.7, **1.9.22**) o prefieres otro corte?~~ Los tres (22 de noviembre de 2026).
2. ~~¿Numeración y nombres de secciones como están arriba?~~ Como están, con los huecos 13–19 (22 de noviembre de 2026).
3. ~~¿`io`, `datetime` y `json` se quedan en `01-language`, como `02-language` del repo de Java, o van a una sección
   `02-stdlib` aparte?~~ Se quedan en `01-language` (22 de noviembre de 2026).
4. ~~¿Tienes Android SDK y Xcode instalados? Define el alcance de B7.~~ Sí (medido el 22 de noviembre de 2026: Android
   SDK con emulador en `~/Library/Android/sdk` y Xcode 27.2): el alcance incluye iOS.
5. ~~¿Qué caso de la sección 11 reemplaza al dominio provisional?~~ Dos casos, Banco y Mercado, con puente: [`docs/Casos.md`](docs/Casos.md).
6. ~~¿Arranco con A2–A5?~~ Hecho el 10 de noviembre de 2026.
7. ~~**Guías de lección**: ¿carpetas por sección o lista plana? ¿Antes de B0′ o dentro de cada tanda?~~ Carpetas por
   sección (`docs/lessons/01-language/01-basics.md`), escritas antes de B0′ (22 de noviembre de 2026).

---

## 11. Caso de punta a punta: ideas de reemplazo

El hilo conductor de los ejercicios 🎯 (leer datos, limpiar nulos y fechas, agrupar, ventanas de tiempo, estadística,
errores) necesita un dominio propio. La propuesta es **finanzas**, porque obliga a cuidar `BigDecimal`, fechas y zonas,
concurrencia y consistencia. Cada idea indica qué lecciones ejercita; se puede elegir una o combinar dos.

| # | Idea | Qué se practica | Lecciones que toca |
|---|---|---|---|
| 1 | **Ráfaga de precios de una acción** (*tick feed*): un flujo de cotizaciones con ráfagas, velas OHLC por minuto, VWAP y media móvil | `Flow` con `buffer`/`conflate`/`sample`, backpressure, ventanas de tiempo, `Instant` y zonas de mercado | `json`, `datetime`, `functional`, `02-coroutines`, `03-reactive`, `10-ktor` (WebSocket) |
| 2 | **Libro mayor de doble partida** (*ledger*): asientos que siempre cuadran, saldos por cuenta, cierre de mes | `value class Money`, `BigDecimal` y redondeo, invariantes con `require`, inmutabilidad, transacciones | `basics`, `oop`, `exceptions`, `04-data` (JDBC/JPA/Exposed), `07-design` (DDD) |
| 3 | **Libro de órdenes y motor de emparejamiento** (*order book*): órdenes límite y de mercado, ejecuciones parciales, prioridad precio-tiempo | `TreeMap`/`PriorityQueue`, `sealed` para tipos de orden, single-writer con `Channel` | `collections`, `oop`, `02-coroutines` (actor), `05-testing` (property-based) |
| 4 | **Portafolio y P&L**: posiciones, costo promedio frente a FIFO, ganancia realizada y no realizada, varias monedas | Agregaciones con `groupBy`/`fold`, `Money` con moneda, reportes | `functional`, `collections`, `json`, `09-spring` (API de reportes) |
| 5 | **Conciliación bancaria**: cruzar el extracto del banco (CSV) con el ledger interno, con tolerancias de fecha y monto | Parseo de CSV, emparejamiento aproximado, reporte de diferencias, errores acumulados | `io`, `strings`, `datetime`, `exceptions`, `04-data` |
| 6 | **Tipos de cambio y arbitraje**: feed de tasas con caché y expiración, conversión con redondeo bancario, arbitraje triangular como ciclo negativo en un grafo | Grafos (Bellman-Ford), caché con TTL, `Clock` inyectado | `control`, `collections`, `datetime`, `02-coroutines` |
| 7 | **Billetera y pagos P2P**: transferencias con clave de idempotencia, saldo que nunca queda negativo, patrón outbox | `Mutex` y concurrencia, transacciones, reintentos, eventos | `02-coroutines`, `04-data`, `09-spring`/`10-ktor`, `07-design` |
| 8 | **Indicadores técnicos y backtesting**: RSI, MACD y bandas de Bollinger sobre precios históricos, y una estrategia simple evaluada contra ellos | `Sequence` y `windowed`, estadística en una pasada, benchmarks | `functional`, `collections`, `06-compiler` (JMH) |
| 9 | **Presupuesto personal**: categorías, presupuesto mensual, avisos al acercarse al límite, mes calendario y cambio de horario | Ventanas de calendario, `YearMonth`, reglas configurables, DSL | `datetime`, `oop`, `dsl`, `11-multiplatform`/`12-android` (app) |
| 10 | **Alertas de precio por suscripción**: cada usuario define umbrales sobre un ticker y recibe avisos en vivo en la app | `StateFlow`/`SharedFlow`, WebSocket, notificaciones | `02-coroutines`, `10-ktor`, `11-multiplatform`, `12-android` |

**Decidido (13 nov 2026):** dos casos, **Banco** (ideas 2, 5, 7 y parte de 6) y **Mercado** (1, 3, 4, 8 y 10), con un puente de liquidación; el diseño está en [`docs/Casos.md`](docs/Casos.md). La recomendación original era combinar **1 (ráfaga de precios)** y **2 (ledger)**. La ráfaga cubre lo asíncrono y reactivo
(corrutinas, `Flow`, WebFlux, Ktor, móvil), y el ledger cubre lo transaccional y de dominio (`BigDecimal`, JDBC/JPA,
DDD). Juntas forman el 🐉 full-stack: un backend que recibe precios y registra operaciones, y un cliente KMP que las
muestra. Las ideas 3 y 6 quedan como ⚡ de `collections` y `control`.

Fuentes de versiones: [Kotlin 2.4.20 Released](https://blog.jetbrains.com/kotlin/2026/09/kotlin-2-4-20-released/) ·
[What's new in Kotlin 2.4.0](https://kotlinlang.org/docs/whatsnew24.html) ·
[Kotlin 2.5 release date / 2.4.21](https://censuus.com/blog/for-developers/kotlin-2-4-20-whats-new-kotlin-2-5-release-date) ·
[Configure a Gradle project](https://kotlinlang.org/docs/gradle-configure-project.html) ·
[Gradle compatibility](https://docs.gradle.org/current/userguide/compatibility.html) ·
[About AGP](https://developer.android.com/build/releases/about-agp)

---

## 12. Guías de lección (`docs/lessons/`)

**Propuesta del 22 de noviembre de 2026, aprobada el mismo día** (pregunta 7 de la [sección 10](#10-para-decidir-en-la-revisión)): carpetas por sección y escritas antes de B0′.

### Para qué

Hoy, para entender una lección hay que abrir su paquete y adivinar por dónde empezar: `Main.kt` es solo una demo, la
lógica está repartida en varios `.kt`, las pruebas viven en otro árbol (`src/test`) y el estado está en
`CHECKLIST.md`. La guía es **el mapa del código de una lección**: qué archivo leer primero, qué muestra cada uno, qué
prueba lo demuestra, cómo correrlo, y qué está hecho y qué falta. No repite el temario:

| Documento | Responde |
|---|---|
| `docs/Temario.md` | **Qué** se aprende: mapeo Java → Kotlin, trampas, caso, 🎤 y ⚡ |
| `<lección>/Exercises.md` | **Qué** se practica: enunciados graduados |
| `docs/lessons/…` (nuevo) | **Dónde** está y **por dónde** leerlo: archivos, símbolos, pruebas, comandos, estado |
| `CHECKLIST.md` | **Cuánto** falta, en una tabla para todo el curso |

### Estructura de directorios (aprobada)

Una carpeta por sección, con **el mismo nombre que la sección en la raíz**, y dentro un archivo por lección numerado
con dos dígitos en el orden de estudio del temario. Así el árbol de `docs/lessons/` se lee de arriba abajo en el orden
del curso, igual que el de la raíz:

```text
docs/lessons/
├── README.md                       # índice: todas las guías en orden, con enlace a temario, código y ejercicios
├── _template.md                    # plantilla de una guía
├── 01-language/
│   ├── 01-basics.md
│   ├── 02-control.md
│   ├── 03-strings.md
│   ├── 04-oop.md
│   ├── 05-io.md
│   ├── 06-collections.md
│   ├── 07-functional.md
│   ├── 08-json.md
│   ├── 09-datetime.md
│   ├── 10-exceptions.md
│   └── 11-generics.md …  16-kotlin2.md        # al escribir cada lección (B4)
├── 02-coroutines/01-fundamentals.md … 10-loom.md                 # B3
├── 04-data/
│   ├── 01-jdbc.md                  # una guía para los tres motores: código compartido + dialectos
│   ├── 02-jpa.md
│   └── 03-exposed.md … 06-sqldelight.md                          # B8
├── 22-legacy-kotlin19/01-language19.md …                         # B5
└── …                               # el resto de secciones, cuando tengan código
```

Reglas:

- **Sección = nombre exacto del directorio de la raíz** (`01-language`, `04-data`, `22-legacy-kotlin19`), con su número
  y sus huecos (no hay `13`–`19`). Los tutoriales **no** llevan guía: su `README.md` ya es el recorrido; el índice los
  enlaza en primer lugar.
- **Lección = `NN-<subpaquete>.md`**, con `NN` en el orden del temario dentro de la sección (dos dígitos alcanzan: la
  sección más grande, `01-language`, tiene 16). El nombre después del número es el del subpaquete del código
  (`basics`, `jdbc`), para que buscar un archivo encuentre los dos.
- **El número solo vive en `docs/`**: paquetes, directorios de código y nombres de prueba no cambian.
- Si se inserta una lección, se renumera solo dentro de su sección (`git mv`), y se corrige el índice.
- Una lección sin código no tiene guía: la guía nace con el código, no con el temario.

**Alternativa descartada:** lista plana con numeración global de tres dígitos (`docs/lessons/006-basics.md`, …,
`088-kotlin25.md`). Se ve el orden completo en una sola carpeta, pero insertar una lección obliga a renumerar todas las
siguientes (o a dejar huecos), el número no dice a qué sección pertenece y 80 archivos en una carpeta se navegan peor
que 17 carpetas. Si se prefiere, se adopta con huecos de 10 en 10 por sección (`0110-basics`, `0120-control`…).

### Plantilla de una guía

```markdown
# 01 · `basics` — Tipos, números, identidad

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.basics` · **Caso:** pedidos (provisional) → Banco
**Enlaces:** [temario](../../Temario.md#01--basics--tipos-números-identidad) · [ejercicios](…/basics/Exercises.md) · [código](…) · [pruebas](…)

## Cómo correrla
./gradlew -p 01-language run -Plesson=basics
./gradlew -p 01-language test --tests '*.basics.*'

## Recorrido sugerido
1. `Numbers.kt`: conversiones explícitas, división, overflow, `toIntOrNull`, `BigDecimal` (`widen`, `checkedAdd`, `money`, `sameAmount`).
2. `Identity.kt`: `==` frente a `===`, `Nothing` (`fail`), `when` por tipo (`describe`), `UInt`.
3. `BasicsTest.kt`: cada trampa del temario con la prueba que la demuestra.
4. `Main.kt`: la demo que imprime todo junto.

## Mapa del código
| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Numbers.kt` | … | `widen`, `parseOrDefault`, `money`, `addTax`… | `BasicsTest` (…) |

## Estado
- **Hecho:** código y 9 pruebas; 25 ejercicios (escala vieja); 0 resueltos en `practice/`.
- **Falta:** B0′ (pasa a `Money` del Banco), B2 (ejercicios a la escala nueva, 🎤 y ⚡ "calculadora de precisión arbitraria").
```

Criterios: se citan **símbolos, no números de línea** (las líneas cambian con cualquier edición); cada fila del mapa
apunta a una prueba existente; "Estado" se limita a lo de la lección (el total sigue en `CHECKLIST.md`); máximo una
pantalla y media.

### Cuándo y cómo se escriben

1. **B0″** (antes de B0′): `docs/lessons/README.md`, `_template.md` y las **12 guías** de lo que ya tiene código (10 de
   `01-language`, `jdbc` y `jpa`), describiendo el código tal como está hoy. Sirven de mapa de partida para el
   reemplazo de B0′, y se comprueba que cada símbolo citado existe (`grep`).
2. **B0′ y siguientes:** cada tanda que cambia el código de una lección actualiza su guía en el mismo cambio, igual
   que hoy actualiza `CHECKLIST.md`.
3. **Regla de sincronía nueva** (para `CLAUDE.md` y "Agregar una lección" del README, al aprobarse): lección nueva →
   guía nueva y fila en el índice; archivo nuevo, renombrado o borrado en una lección → su guía.

---

## 13. Guías de estudio: teoría y fuentes (propuesta)

**Propuesta del 22 de noviembre de 2026, aprobada el mismo día** con estos ajustes: más profundidad en la teoría y en la
comparación con Java donde el modelo cambia de verdad (`oop`, `io`, corrutinas, reactivo); fuentes en inglés y en
español, con prioridad al inglés; las 11 guías del código que ya existe en una primera tanda, y desde B2 cada lección
nace con código y guía juntos. Muestra escrita:
[`docs/lessons/01-language/01-basics.md`](docs/lessons/01-language/01-basics.md).

### Qué cambia

Las guías de B0″ son un **mapa del código** (qué archivo leer, qué símbolo muestra qué, qué prueba lo demuestra). La
propuesta las convierte en **guías de estudio**: la teoría que hay detrás de la lección, explicada para quien viene de
Java, con las fuentes para profundizar. El mapa del código se queda, en el medio.

| Sección de la guía | Qué lleva | Hoy |
|---|---|---|
| Qué vas a aprender | 2 o 3 líneas, del objetivo del temario | — |
| **Teoría** | 4 a 8 apartados; cada uno: el concepto, el contraste con Java 21, **por qué** Kotlin lo diseñó así, el símbolo de la lección que lo muestra y el enlace a la fuente | — |
| Cómo correrla, recorrido, mapa del código | Igual que hoy | ✅ |
| **Para profundizar** | Tres bloques: documentación oficial, lecturas, videos; 3 a 8 enlaces por bloque, cada uno con una línea de qué mirar ahí | — |
| Estado | Igual que hoy | ✅ |

La guía pasa de una pantalla y media a unas cuatro o cinco. El temario sigue siendo el resumen (mapeo, trampas,
⚡); la guía es lo que se lee para aprender.

### Reglas de las fuentes

1. **Primero lo oficial:** la documentación de kotlinlang.org (docs, API de la stdlib, especificación, KEEP), la de
   Java SE 21 y la de cada librería **en la versión fijada** en `Versions.md`.
2. **Después, lecturas reconocidas:** libros (*Kotlin in Action*, 2.ª edición; *Effective Kotlin*; *Effective Java*),
   el blog de JetBrains y artículos técnicos con ejemplos (Baeldung, Kotlin Academy). Un libro de pago se cita, pero
   nunca como única fuente de un apartado.
3. **Videos:** charlas de KotlinConf y del canal *Kotlin by JetBrains* primero; Android Developers para `12-android`;
   cursos de terceros solo si cubren justo la lección, y marcados como no oficiales.
4. **Idioma:** la mayoría del material bueno está en inglés; se agrega en español cuando existe con la misma calidad.
5. **Verificado, no recordado:** cada enlace se comprueba al escribir la guía (en la muestra, 13 de 14 respondieron 200
   a `curl`; Baeldung devuelve 403 a clientes automáticos y se revisó a mano). La guía dice la fecha de revisión.
6. **Lo que la teoría afirma y no está en las fuentes, se mide**, igual que las etiquetas del temario. Ejemplo de la
   muestra: la división `/` de Kotlin sobre `BigDecimal` redondea con `HALF_EVEN` a la escala del dividendo
   (`10 / 3 = 3`), algo que no estaba ni en el temario ni en el código.

### Cómo se hace

- **Primera tanda:** las 11 guías del código hecho (la de `basics` ya está), en tres entregas con su investigación
  (páginas oficiales de cada tema, charlas y artículos) y su commit.
- **Desde ahí, cada lección nueva de B2 a B10 nace con su guía completa**, igual que hoy nace con su mapa.
- **Script de enlaces** `scripts/check-guide-links.sh` (hecho): recorre las guías, separa los 401/403/429 de sitios que
  bloquean clientes automáticos (se revisan a mano) y sale con error si hay enlaces rotos.
- Cambian con la aprobación: la plantilla `docs/lessons/_template.md`, las reglas de `docs/lessons/README.md` (la de
  "máximo una pantalla y media") y la regla de sincronía de CLAUDE.md.

### Para decidir

1. ~~¿El formato de la muestra sirve?~~ Sí; con más profundidad donde el tema lo pide.
2. ~~¿Videos solo en inglés?~~ Inglés y español, con prioridad al inglés.
3. ~~¿Las 12 guías antes de B2?~~ Las del código hecho en una primera tanda; las nuevas, con su código.
