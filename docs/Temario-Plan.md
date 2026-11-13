# Plan de trabajo del temario (paso B0)

Cómo se escribe [`docs/Temario.md`](Temario.md) completo: el formato de cada lección, las tandas en que se reparte el
trabajo, lo que tiene que cumplir cada tanda y su estado. Cada tanda se ejecuta con el prompt maestro de
[`docs/prompts/temario-tanda.md`](prompts/temario-tanda.md), rellenando sus parámetros con la fila de la tanda.

Escrito el **13 de noviembre de 2026**. El paso B0 está en el [plan](../Plan-Migracion-Monorepo.md#8-fase-b-generar-el-resto)
("cada lección con mapeo Java → Kotlin, trampas, etiquetas de versión y su ⚡") y termina con una revisión juntos
antes de escribir el código de las fases B2 a B10.

**Salto rápido:** [Alcance](#alcance) · [Plantilla](#plantilla-de-una-lección) · [Reglas](#reglas-para-todas-las-tandas) ·
[Tandas](#tandas) · [Versiones medidas](#referencia-de-versiones-medida) · [Estado](#estado)

## Alcance

| | Hoy | Al terminar B0 |
|---|---|---|
| Lecciones | 14 (las que tienen código) | **88 entradas**: 5 tutoriales + 83 lecciones en 17 secciones, según la [sección 5 del plan](../Plan-Migracion-Monorepo.md#5-secciones-y-lecciones) |
| Por lección | Mapeo Java → Kotlin y trampas | Además: objetivo, caso (Banco o Mercado), etiquetas de versión, su ⚡ y los temas 🎤 |
| Etiquetas de versión | 0 | Todas sacadas de la [referencia medida](#referencia-de-versiones-medida) |
| ⚡ | 0 | Uno por lección (los tutoriales solo `decompile`) |
| Casos | Pedidos de tienda (provisional) | Banco y Mercado ([`Casos.md`](Casos.md)) |
| "Huecos" y "Para la discusión" | Preguntas de antes del plan | Quitadas: las responde el plan (cada hueco quedó como lección) |

El temario es un documento: **no** se escribe código de lecciones en B0. Lo que sí se escribe y se corre son las pruebas
de compilación que fijan las etiquetas de versión (en el scratchpad, no en el repo).

## Plantilla de una lección

```markdown
### <n>. `<lección>` — <título corto>

**Objetivo:** <qué sabe hacer al terminar, 1–2 líneas>.
**Sección:** `<sección>` · **Build:** nivel <0–6> · **Caso:** Banco | Mercado | Ambos | — · **Estado:** existe | nueva

| Java | Kotlin | Nota |
|---|---|---|
| <construcción de Java> | <equivalente en Kotlin> `[versión]` | <diferencia que importa> |
(4 a 8 filas; cuando no hay equivalente en Java, la columna Java dice "—")

**Trampas:** <2 a 5 viñetas; cada una, una frase con la causa>.

**Caso:** <qué parte de Banco o Mercado usa, con nombres de Casos.md: Money, Tick, OrderBook…>.

**🎤 Entrevista:** <2 a 3 preguntas típicas que la lección prepara>.

**⚡ <nombre>:** <el miniproyecto en 1–2 líneas> · **Se verifica con:** <criterio comprobable: prueba, benchmark, salida>.
```

- Los tutoriales llevan una versión corta (objetivo, qué cubre, enlace a su `README.md`), porque ya están escritos.
- Una lección que ya existe conserva lo que tiene hoy en el temario, pasado a esta plantilla y a los casos nuevos.

## Reglas para todas las tandas

1. **Fuentes, en este orden:** la sección 5 del plan (qué lecciones hay y su ⚡ de sección), [`Casos.md`](Casos.md)
   (qué caso usa cada lección), [`Versions.md`](Versions.md), la [referencia de versiones](#referencia-de-versiones-medida)
   y, para las lecciones que ya existen, [`archive/Temario-v1.md`](archive/Temario-v1.md) (tablas, trampas y la
   sección "Huecos") junto con su código y su `Exercises.md`.
2. **Etiquetas de versión solo medidas.** Una etiqueta `[x.y]` sale de la referencia de este documento. Si una tanda
   necesita una que no está, la agrega a [`scripts/version-probes.sh`](../scripts/version-probes.sh), la mide y suma la
   fila a la referencia en la misma tanda. Lo que no se pueda medir lleva ⚠️.
3. **Librerías con versión, no con etiqueta de lenguaje:** kotlinx-coroutines, Spring, Ktor, Exposed… se citan con
   la versión de [`Versions.md`](Versions.md) o, si aún no está fijada, con ⚠️ ("se fija al escribir la sección").
4. **Español en el texto, inglés en el código** (identificadores, nombres de lección). Repo público: nada de
   empresas ni procesos de selección; datos y entidades ficticios.
5. **Cada tanda reemplaza sus bloques `⬜️ pendiente` del temario** y no toca los de otras tandas, salvo el mapa
   general y la trazabilidad (tanda 10).
6. **Al cerrar una tanda:** revisar enlaces y anclas, hacer el escaneo de referencias externas, actualizar la tabla
   [Estado](#estado) de este documento y la bitácora de `CHECKLIST.md`. Commit solo si el usuario lo pide.

## Tandas

| Tanda | Contenido | Entradas | Depende de | Nota |
|---|---|---|---|---|
| **0** ✅ | Base: esqueleto nuevo de `Temario.md` (encabezado, leyenda, mapa general, un bloque `⬜️ pendiente` por lección), migración de la chuleta, los idioms y las trampas consolidadas, retiro de "Huecos" y "Para la discusión" (el temario anterior queda en [`archive/Temario-v1.md`](archive/Temario-v1.md)); referencia de versiones con `kotlinc` 2.2 y 2.3 | — | — | Es la que habilita el resto |
| **1** | `tutorials` (5, versión corta) y `01-language` existentes I: `basics`, `control`, `strings`, `oop`, `io` | 10 | 0 | Pasar a los casos y a la plantilla |
| **2** | `01-language` existentes II: `collections`, `functional`, `json`, `datetime`, `exceptions` | 5 | 0 | Ídem; aquí vive la ruta 🎯 del Mercado |
| **3** | `01-language` nuevas: `generics`, `inline`, `interop`, `delegation`, `dsl`, `kotlin2` | 6 | 0 | `kotlin2` usa toda la referencia de 2.x |
| **4** | `02-coroutines` (10) | 10 | 0 | La sección más grande y la más pedida |
| **5** | `03-reactive` (4) y `04-data` (6: `jdbc`, `jpa`, `exposed`, `jooq`, `r2dbc`, `sqldelight`) | 10 | 4 | `r2dbc` y los puentes reactivos usan lo de corrutinas |
| **6** | `05-testing` (4), `06-compiler` (4), `07-design` (4) | 12 | 0 | `06-compiler` se apoya en el tutorial `decompile` |
| **7** | `08-build` (4), `09-spring` (4), `10-ktor` (3) | 11 | 4, 5 | Backend web |
| **8** | `11-multiplatform` (4), `12-android` (7) | 11 | 4 | Móvil; alcance según Android SDK y Xcode (pregunta abierta del plan) |
| **9** | Tracks `20` (4), `21` (3), `22` (3), `25-interview` (2), `26-kotlin-next` (1) y los dos 🐉 | 13 | 0–8 | `22` usa `k2-notes.md`; los 🐉 cruzan secciones |
| **10** | Cierre: mapa general y trazabilidad (lección → caso → ⚡ → ejercicios), revisión de consistencia (etiquetas contra la referencia, enlaces), README, plan y `CHECKLIST.md` | — | 1–9 | Termina con la revisión juntos |

Total: 88 entradas en 9 tandas de contenido, más la base y el cierre. Las tandas 1, 2, 3, 4 y 6 no dependen entre sí
(solo de la 0) y se pueden sacar en cualquier orden.

## Referencia de versiones (medida)

Medida el 13 de noviembre de 2026 compilando un ejemplo mínimo de cada construcción, sin opciones (salvo donde se
indica), con los `kotlinc` instalados con SDKMAN: 1.4.31, 1.5.31, 1.6.21, 1.7.21, 1.8.20, 1.9.22, 2.0.21, 2.1.21,
2.2.21, 2.3.21 y 2.4.21 (SDKMAN no tiene 1.4.32 ni 1.8.22; los 1.4 no arrancan en JDK 21 y se corren con JDK 11).
**ok** = compila sin avisos · **exp** = pide habilitarla (`-X…`) u opt-in · **dep** = compila con aviso de
deprecación · **dep!** = deprecación con error · **no** = no existe o no compila; **aviso**/**error** cuando el
resultado es un diagnóstico propio de la construcción. `[2.2]` quiere decir "estable a más tardar en 2.2.21 y no
estable en la versión anterior medida"; `[≤ 1.4]` = ya estable en la versión más vieja medida.

| Construcción | 1.4 | 1.5 | 1.6 | 1.7 | 1.8 | 1.9 | 2.0 | 2.1 | 2.2 | 2.3 | 2.4 | Etiqueta |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `fun interface` | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[≤ 1.4]` |
| `ArrayDeque` de `kotlin.collections` | ok | dep | dep | dep | dep | ok | ok | ok | ok | ok | ok | `[≤ 1.4]` (el `dep` de 1.5 a 1.8 es el choque con `addLast` de `SequencedCollection` en JDK 21) |
| `sumOf { }` | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[≤ 1.4]` |
| `runningFold` | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[≤ 1.4]` |
| Contratos propios (`contract { returns() implies … }`) | exp | exp | exp | exp | exp | exp | exp | exp | exp | exp | exp | `[exp 2.4]` (experimental desde 1.3) |
| `UInt`, `ULong` (`1u + 2u`) | exp | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `Int.mod` | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `Char.code` | exp | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `lowercase()`, `replaceFirstChar` | exp | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `capitalize()` | ok | dep | dep | dep | dep | dep | dep | dep | dep | dep | dep | `[dep 1.5]` (en 2.4 sigue siendo aviso) |
| `toLowerCase()` | ok | dep | dep | dep | dep | dep | dep | dep! | dep! | dep! | dep! | `[dep 1.5]`, error desde 2.1 |
| `sealed interface` | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `@JvmInline value class` | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `inline class` | exp | dep | dep | dep | dep | dep | dep | dep | dep | dep | dep | `[dep 1.5]` |
| `@JvmRecord` (con `-jvm-target 16`) | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `Result<T>` como tipo de retorno | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` |
| `kotlin.io.path` (`readText`) | exp | ok | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.5]` (hasta 1.7 en `kotlin-stdlib-jdk7`, que `kotlinc` no agrega solo; se midió con ese jar) |
| `readln()` | no | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.6]` |
| `buildList { }` | exp | exp | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.6]` |
| `kotlin.time.Duration` (`5.minutes`) | no | no | ok | ok | ok | ok | ok | ok | ok | ok | ok | `[1.6]` |
| `T & Any` | no | no | exp | ok | ok | ok | ok | ok | ok | ok | ok | `[1.7]` |
| `min()`/`max()` no nulos | no | no | no | ok | ok | ok | ok | ok | ok | ok | ok | `[1.7]` (deprecados en 1.4, ocultos en 1.5 y 1.6) |
| `when` no exhaustivo sobre `sealed` como sentencia | ok | ok | aviso | error | error | error | error | error | error | error | error | `[1.7]` (desde 1.7 es error) |
| `Regex.matchAt` | no | exp | exp | ok | ok | ok | ok | ok | ok | ok | ok | `[1.7]` |
| `data object` | no | no | no | exp | exp | ok | ok | ok | ok | ok | ok | `[1.9]` |
| `Enum.entries` | no | no | no | no | exp | ok | ok | ok | ok | ok | ok | `[1.9]` |
| `..<` (rango abierto) | no | no | no | exp | exp | ok | ok | ok | ok | ok | ok | `[1.9]` |
| `a..<b` sobre un `Comparable` (`LocalDate`) | no | no | no | exp | exp | ok | ok | ok | ok | ok | ok | `[1.9]` |
| `measureTime { }` | exp | exp | exp | exp | exp | ok | ok | ok | ok | ok | ok | `[1.9]` |
| `enumEntries<T>()` | no | no | no | no | no | exp | ok | ok | ok | ok | ok | `[2.0]` |
| `copy()` de una `data class` con constructor `private` | ok | ok | ok | ok | ok | ok | aviso | aviso | aviso | aviso | aviso | `[2.0]` avisa que `copy()` expone el constructor; `@ConsistentCopyVisibility` lo alinea |
| `@SubclassOptInRequired` | no | no | no | no | ok | ok | exp | ok | ok | ok | ok | `[2.1]` (1.8 y 1.9 la aceptaban sin opt-in; 2.0 pidió `@ExperimentalSubclassOptIn`) |
| `kotlin.io.path.walk` | no | no | no | no | exp | exp | exp | ok | ok | ok | ok | `[2.1]` |
| `break`/`continue` en lambdas inline | no | no | no | no | exp | exp | exp | exp | ok | ok | ok | `[2.2]` |
| `when` con guardas (`is Int if x > 0 ->`) | no | no | no | no | no | no | exp | exp | ok | ok | ok | `[2.2]` |
| `$$"…"` (plantillas con varios `$`) | no | no | no | no | no | no | exp | exp | ok | ok | ok | `[2.2]` |
| `kotlin.io.encoding.Base64` | no | no | no | no | exp | exp | exp | exp | ok | ok | ok | `[2.2]` |
| `HexFormat` (`toHexString()`) | no | no | no | no | no | exp | exp | exp | ok | ok | ok | `[2.2]` |
| Alias de tipo anidados | no | no | no | no | no | no | no | no | exp | ok | ok | `[2.3]` |
| `kotlin.time.Instant` y `Clock` | no | no | no | no | no | no | no | exp | exp | ok | ok | `[2.3]` |
| *Context parameters* (`context(log: …) fun`) | no | no | no | no | no | no | no | exp | exp | exp | ok | `[2.4]` |
| *Explicit backing fields* (`val x: List<T> field = …`) | no | no | no | no | no | no | exp | exp | exp | exp | ok | `[2.4]` |
| `kotlin.uuid.Uuid` | no | no | no | no | no | no | exp | exp | exp | exp | ok | `[2.4]` |
| *Context receivers* (`context(Log) fun`, sin nombre) | no | no | exp | exp | exp | exp | exp | exp | exp | exp | no | `[rm 2.4]` ("context parameters must be named") |
| Atómicos de la stdlib (`kotlin.concurrent.atomics`) | no | no | no | no | no | no | no | exp | exp | exp | exp | `[exp 2.4]` |
| `kotlin.io.path.deleteRecursively` | no | no | no | no | exp | exp | exp | exp | exp | exp | exp | `[exp 2.4]` (pide `@OptIn(ExperimentalPathApi::class)`) |
| `@JvmExposeBoxed` | no | no | no | no | no | no | no | no | exp | exp | exp | `[exp 2.4]` |
| Lambdas con `invokedynamic` | — | — | — | — | — | clase | — | — | — | — | indy | `[2.0]` (tutorial `decompile`, `k2-notes`) |
| `-language-version 1.9` en un `kotlinc` 2.4 | — | — | — | — | — | — | — | — | — | — | rechazado | (tutorial `cli`) |
| REPL de `kotlinc` | — | — | — | — | — | ok | — | deprecado, lenguaje 1.9 | — | — | no arranca | (tutorial `repl`) |
| `MutableList.removeFirst()` compilado contra JDK 21 | — | — | — | — | — | `List.removeFirst` | — | — | — | — | `List.removeFirst` | Sin aviso: llama al método de Java 21 (`javap`; lección `collections`) |

Lo medido en los tutoriales y en `k2-notes.md` (smart casts de K2, `typeSwitch` con target 21, avisos nuevos de K2)
también vale como referencia. La tabla sale de [`scripts/version-probes.sh`](../scripts/version-probes.sh): un ejemplo de
una línea por construcción, compilado con cada `kotlinc` instalado. Para medir una construcción nueva, se agrega una
línea al script y se corre `scripts/version-probes.sh <nombre>`.

## Estado

| Tanda | Estado | Fecha | Notas |
|---|---|---|---|
| 0 | ✅ | 2026-11-13 | Referencia medida con cinco `kotlinc` (1.9 a 2.4); esqueleto de 88 entradas pendientes; temario anterior en `archive/Temario-v1.md`; chuleta, idioms y trampas migradas y puestas al día |
| 1 | ✅ | 2026-11-13 | 5 tutoriales (versión corta) y `basics`, `control`, `strings`, `oop`, `io`. Referencia ampliada a once `kotlinc` (1.4.31 a 2.4.21) con 27 construcciones nuevas; `path.walk` pasa a `[2.1]` y `T & Any`, `enumEntries` y `@SubclassOptInRequired` quedan sin ⚠️. ⚡ nuevos (no están en la sección 5 del plan): `control` matcher por búsqueda binaria, `strings` códec estilo FIX, `io` conciliador de extracto |
| 2 | ⬜️ | | |
| 3 | ⬜️ | | |
| 4 | ⬜️ | | |
| 5 | ⬜️ | | |
| 6 | ⬜️ | | |
| 7 | ⬜️ | | |
| 8 | ⬜️ | | |
| 9 | ⬜️ | | |
| 10 | ⬜️ | | |
