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
[Caso de punta a punta](#11-caso-de-punta-a-punta-ideas-de-reemplazo)

---

## Estado y próximos pasos

Actualizado el **12 de noviembre de 2026**, al cerrar A0 y A5. El avance detallado por sección y lección está en
[`CHECKLIST.md`](CHECKLIST.md).

**Hecho (fase A, pasos A0–A5):**

| Commit | Qué |
|---|---|
| `a45fc23` Initial commit | Solo `.gitignore` |
| `c33e8d1` Add Kotlin course v1 | El curso en un solo proyecto (Kotlin 1.9.22), sin referencias externas y con el dominio provisional de pedidos |
| `003ce5d` Add monorepo migration plan | Este documento |
| `b5b5214` Create monorepo directory structure | Las 17 secciones, `docs/`, `scripts/` y `docker/legacy-kotlin/` con `.gitkeep` |
| `0d734cd` Move current lessons into 01-language and 04-data | Composite build en la raíz; 123 pruebas en verde (85 + 38) con PostgreSQL y MySQL |

Tags: `baseline-1.9.22` (sobre `c33e8d1`, el curso v1 de proyecto único) y `monorepo-1.9.22` (sobre el commit
que cierra A5: monorepo completo, todavía en Kotlin 1.9.22).

Sin remoto todavía. Antes del primer push: repetir el escaneo de referencias externas sobre `git log -p --all`.

**Pendiente, en el orden en que se va a hacer:**

1. **A7 · Migrar la base a Kotlin 2.x** (próxima sesión). Verificar la última estable al empezar (2.4.21 vista; 2.5.0
   esperada para diciembre de 2026), Gradle 9.9.0 y librerías. Anotar cada cosa que se rompa para el track 1.9.
2. **A6 · Track `22-legacy-kotlin19`** con wrapper propio en 1.9.22. Se hace después de A7 para reutilizar las notas
   de lo que cambió.
3. **A8 · Contenedores**: `docker/legacy-kotlin/`, `scripts/legacy.sh`, `docs/LegacyKotlin.md`, probados en Apple Silicon.
4. **A9 · Documentación**: README con índice de secciones, CLAUDE.md, `docs/Versions.md`. (`CHECKLIST.md` ya existe.)
5. **B0′ · Caso de finanzas**: elegir de la [sección 11](#11-caso-de-punta-a-punta-ideas-de-reemplazo) y reemplazar el
   dominio provisional de pedidos.
6. **Fase B (B0–B10)**: temario completo, tutoriales, ejercicios con la escala de `java-champion-review`, corrutinas y
   reactivo, tracks legacy, web, móvil y el resto de secciones.

**Preguntas abiertas** ([sección 10](#10-para-decidir-en-la-revisión)): cortes legacy, numeración de secciones, dónde
van `io`/`datetime`/`json`, Android SDK y Xcode, y qué caso de finanzas se usa.

---

## 1. Decisiones

| # | Decisión | Estado |
|---|---|---|
| 1 | Un solo repo git con varios proyectos (monorepo), como `java-champion-review` y `groovy-for-java-devs` | ✅ acordado |
| 2 | **Repo público**: sin referencias a empresas ni procesos de selección; 🎯 pasa a significar "prioritario" | ✅ hecho en el curso v1 |
| 3 | Ejercicios con la escala de `java-champion-review`: 🟢 🟡 🟠 🔴 ⚡ 🐉 🎤 🎯, de 25 a 40 por lección más su ⚡ | ✅ acordado |
| 4 | Proyecto base en la **última Kotlin estable** y tracks legacy aparte, cada uno con su compilador real | ✅ acordado |
| 5 | Los tracks legacy corren en **contenedores** (idea de `groovy-for-java-devs/docs/LegacyJdks.md`) y también en nativo | ✅ acordado; falta probarlo |
| 6 | El repo se sigue llamando `kotlin-for-java-devs`, igual que `groovy-for-java-devs` | ✅ acordado |
| 7 | Primero se **mueve sin cambiar comportamiento** (Kotlin 1.9.22) y se prueba, corrigiendo solo lo necesario; la migración a 2.x va en otra sesión | ✅ acordado |
| 8 | **Código en inglés, comentarios y documentos en español**, como en `java-champion-review` | ✅ revisado en el curso v1 |
| 9 | Git con `user.name` y `user.email` locales del repo; mensajes de commit en inglés | ✅ hecho |
| 10 | `kotlin-for-java-devs.code-workspace` copiado de `java-champion-review` e ignorado por git (ajustes personales de VS Code) | ✅ hecho |
| 11 | El caso de punta a punta pasa a un dominio de **finanzas** propio (trading, ledger…); mientras tanto, pedidos de tienda | Elegir en la [sección 11](#11-caso-de-punta-a-punta-ideas-de-reemplazo) |

El punto 7 importa: si mover y actualizar van juntos, cuando algo falle no se sabe si fue el movimiento o la
versión. Además, la lista de lo que se rompe al pasar de 1.9.22 a 2.x sirve de contenido para el track de 1.9.

---

## 2. Versiones: base y tracks legacy

### Base

| Pieza | Versión | Nota |
|---|---|---|
| Kotlin | **2.4.21** ⚠️ | Última estable encontrada (8 oct 2026). 2.5.0 está en beta y se espera para diciembre de 2026 |
| JDK | 21 (toolchain) | Igual que los repos de Java y Groovy. Gradle 9 exige JDK 17+ para correr |
| Gradle | 9.9.0 ⚠️ | El mismo wrapper que `java-champion-review` y `groovy-for-java-devs`; verificar compatibilidad con KGP 2.4 |
| kotlinx-serialization, Kermit, Hibernate, drivers, JUnit | Las últimas compatibles ⚠️ | Se fijan en el paso A7 y quedan en `docs/Versions.md` |

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
- **`-language-version` en un compilador moderno**: K2 solo acepta pocas versiones hacia atrás ⚠️, y no reproduce
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
├── postgres/            ya existe: PostgreSQL 17.2 en 127.0.0.1:15432
├── mysql/               ya existe: MySQL 8.4.3 en 127.0.0.1:13306
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
- ⚠️ A probar en A8: Gradle 6.8 dentro de Temurin 11 en arm64, IntelliJ 2026 importando un proyecto con Gradle 6.8,
  y la caché compartida entre wrappers de Gradle distintos.

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
  repiten a propósito; una prueba o tarea del root revisa que coincidan.
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
| **A6 · Track 1.9** ⬜️ (después de A7) | `22-legacy-kotlin19/` con wrapper propio (9.2.1), build en 1.9.22, `.sdkmanrc` y una prueba de humo. Primer track porque su build ya se sabe que funciona | `./gradlew -p 22-legacy-kotlin19 test` en nativo |
| **A7 · Base a 2.x** ⬜️ siguiente | `01` y `04` a Kotlin 2.4.21 ⚠️, Gradle 9.9.0 ⚠️ y librerías al día. Cada cosa que se rompe o cambia de comportamiento se anota en `22-legacy-kotlin19/docs/k2-notes.md` | 123 pruebas en verde en 2.4 |
| **A8 · Contenedores** ⬜️ | `docker/legacy-kotlin/` (compose + README), `scripts/legacy.sh`, `docs/LegacyKotlin.md`. Probar con el track 19 en Apple Silicon | `./scripts/legacy.sh 19 test` igual que en nativo |
| **A9 · Documentación** ⬜️ | README nuevo (índice de secciones como el de Groovy), CLAUDE.md reescrito (versiones, arquitectura, niveles, leyenda, reglas de sincronía), `docs/Versions.md`, `CHECKLIST.md` con bitácora | Revisión juntos |

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
| **B0′** | Elegir el caso de finanzas (sección 11) y reemplazar el dominio provisional de pedidos en datos, lecciones y ejercicios 🎯 | Ruta prioritaria del README rehecha |
| **B0** | `docs/Temario.md` completo: cada lección con mapeo Java → Kotlin, trampas, etiquetas de versión y su ⚡ | Revisión juntos antes de escribir código |
| **B1** | `tutorials/`: `install`, `cli`, `repl`, `decompile`, `debug` | 5 tutoriales con sus scripts |
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

- ⚠️ **Versiones sin confirmar**: Kotlin 2.4.21 como última estable, compatibilidad de KGP 2.4 con Gradle 9.9.0,
  Gradle exacto para KGP 1.4.32 y 1.6.21, y si `kotlin-test-junit5` existe para 1.4 (si no, JUnit 4 en ese track).
- **IntelliJ y Gradle viejo**: puede que un IntelliJ actual no importe bien un proyecto con Gradle 6.8. Alternativa:
  abrir el track como proyecto aparte o trabajarlo solo por CLI y contenedor.
- **Android** necesita Android SDK y emulador, y su AGP fija la versión de Gradle (AGP 9.0 exige Gradle 9.1+).
  **iOS** necesita Xcode; sin él, KMP se hace con targets desktop y wasm.
- **Hibernate 6 → 7** en A7 puede romper la lección `jpa`. Si pasa, se separa en un paso propio.
- **El composite usa el Gradle de la raíz** para todas las secciones incluidas. Si una sección necesita otro Gradle,
  sale del composite (como `12-android`).

---

## 10. Para decidir en la revisión

1. ¿Los tres tracks legacy (**1.4.32**, **1.6.21** con Boot 2.7, **1.9.22**) o prefieres otro corte?
2. ¿Numeración y nombres de secciones como están arriba?
3. ¿`io`, `datetime` y `json` se quedan en `01-language`, como `02-language` del repo de Java, o van a una sección
   `02-stdlib` aparte?
4. ¿Tienes Android SDK y Xcode instalados? Define el alcance de B7.
5. ¿Qué caso de la [sección 11](#11-caso-de-punta-a-punta-ideas-de-reemplazo) (o qué combinación) reemplaza al dominio provisional?
6. ~~¿Arranco con A2–A5?~~ Hecho el 10 de noviembre de 2026.

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

**Recomendación:** combinar **1 (ráfaga de precios)** y **2 (ledger)**. La ráfaga cubre lo asíncrono y reactivo
(corrutinas, `Flow`, WebFlux, Ktor, móvil), y el ledger cubre lo transaccional y de dominio (`BigDecimal`, JDBC/JPA,
DDD). Juntas forman el 🐉 full-stack: un backend que recibe precios y registra operaciones, y un cliente KMP que las
muestra. Las ideas 3 y 6 quedan como ⚡ de `collections` y `control`.

Fuentes de versiones: [Kotlin 2.4.20 Released](https://blog.jetbrains.com/kotlin/2026/09/kotlin-2-4-20-released/) ·
[What's new in Kotlin 2.4.0](https://kotlinlang.org/docs/whatsnew24.html) ·
[Kotlin 2.5 release date / 2.4.21](https://censuus.com/blog/for-developers/kotlin-2-4-20-whats-new-kotlin-2-5-release-date) ·
[Configure a Gradle project](https://kotlinlang.org/docs/gradle-configure-project.html) ·
[Gradle compatibility](https://docs.gradle.org/current/userguide/compatibility.html) ·
[About AGP](https://developer.android.com/build/releases/about-agp)
