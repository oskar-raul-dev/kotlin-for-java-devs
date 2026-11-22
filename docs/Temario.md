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

**Objetivo:** dejar listo el entorno del curso (JDK 21, Kotlin 2.4.21 de línea de comandos, Gradle por wrapper) sin
romper otros proyectos, y saber qué versión de qué está corriendo.
**Sección:** `tutorials` · **Build:** 0 · **Caso:** — · **Estado:** existe · **Tutorial:** [`tutorials/install/`](../tutorials/install/README.md) (12 ejercicios)

**Cubre:** SDKMAN y `.sdkmanrc` por carpeta (`sdk env`), la alternativa sin SDKMAN (brew, instaladores), por qué
Gradle no se instala (wrapper), toolchains de Gradle, JDK del IDE frente al de la terminal, Docker para las bases y
los tracks legacy. **Trampa principal:** `./gradlew --version` informa el Kotlin embebido de Gradle, no el del
proyecto.

### T · `cli` — Kotlin desde la línea de comandos

**Objetivo:** compilar, empaquetar y correr Kotlin sin Gradle ni IDE, y entender qué hace el build por debajo.
**Sección:** `tutorials` · **Build:** 0 · **Caso:** Ambos (ejemplos con `Money`) · **Estado:** existe · **Tutorial:** [`tutorials/cli/`](../tutorials/cli/README.md) (15 ejercicios)

**Cubre:** `kotlinc` frente a `javac` y `kotlin` frente a `java`, la clase de fachada `HelloKt`, jars con y sin
`-include-runtime`, classpath y paquetes, Java consumiendo un jar de Kotlin (`@file:JvmName`), opciones del
compilador (`-jvm-target`, `-Xjdk-release`, `-language-version`, `-Werror`). **Trampa principal:** `kotlinc` a mano
compila para Java 8 y `-language-version 1.9` ya no se acepta en 2.4.

### T · `repl` — REPL, expresiones y scripts

**Objetivo:** probar Kotlin sin crear un proyecto y reemplazar scripts de bash o Python por scripts tipados.
**Sección:** `tutorials` · **Build:** 0 · **Caso:** Ambos (ejemplos) · **Estado:** existe · **Tutorial:** [`tutorials/repl/`](../tutorials/repl/README.md) (15 ejercicios)

**Cubre:** qué pasó con el REPL de `kotlinc` (funciona en 1.9, deprecado en 2.1, no arranca en 2.4), expresiones con
`-e`, scripts `.kts` y `.main.kts` con `@file:DependsOn`/`@file:Import`, su caché, scratch files y Kotlin Notebook
en IntelliJ. **Trampa principal:** buscar un `jshell` de Kotlin: en 2.4 la respuesta es `.main.kts`.

### T · `decompile` — Decompilar Kotlin

**Objetivo:** leer el bytecode que genera `kotlinc` con `javap` y CFR para saber cuánto cuesta cada construcción y
cómo la ve Java.
**Sección:** `tutorials` · **Build:** 0 · **Caso:** Ambos (`Order`, ticks en los ejercicios) · **Estado:** existe · **Tutorial:** [`tutorials/decompile/`](../tutorials/decompile/README.md) (16 ejercicios, incluido el ⚡)

**Cubre:** *Show Kotlin Bytecode*, `javap -c -p`, CFR 0.152, clases (`data`, `object`, `companion`, `lateinit`),
funciones (extensiones, defaults, `inline`, lambdas con `invokedynamic` `[2.0]`), tipos (`value class`, nulabilidad),
cómo cambia el bytecode con el `jvmTarget` (`typeSwitch`, `makeConcatWithConstants`, `PermittedSubclasses`) y
`@Metadata` con `kotlin-metadata-jvm` 2.4.21. **Trampa principal:** el campo de un `lateinit` es `public`.

**⚡ Catálogo "qué genera kotlinc":** 20 construcciones, cada una con fuente mínimo, fragmento de `javap`, Java de
CFR y su costo, con la versión en que apareció · **Se verifica con:** un `check.sh` propio que compila los fuentes
con `-jvm-target 21` y comprueba con `javap` cada afirmación del catálogo.

### T · `debug` — Depurar Kotlin

**Objetivo:** depurar Kotlin con las herramientas de la JVM y saber qué cambia frente a Java: `inline`, corrutinas y
nombres sintéticos.
**Sección:** `tutorials` · **Build:** 0 · **Caso:** Ambos (ejemplos) · **Estado:** existe · **Tutorial:** [`tutorials/debug/`](../tutorials/debug/README.md) (14 ejercicios)

**Cubre:** qué información de depuración deja `kotlinc`, `jdb`, depuración remota con JDWP, funciones `inline` y
SMAP, corrutinas (kotlinx-coroutines 1.11.0, `DebugProbes`, el depurador de corrutinas de IntelliJ) y el depurador
de IntelliJ. **Trampa principal:** `print` de `jdb` no entiende operadores de Kotlin, y `DebugProbes.install()` puede
no adjuntarse.

---

## 01-language

El lenguaje y su stdlib desde Java. **Build:** 1 · **Caso:** Ambos.

### 01 · `basics` — Tipos, números, identidad

**Objetivo:** saber dónde quedaron los primitivos, el boxing y `null`, y manejar dinero con `BigDecimal` sin las
trampas de escala, redondeo y `Double`.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** existe (en el Banco desde B0′, tanda 3)

> **B0′, tanda 3 (hecho):** `Money` de [`Casos.md`](Casos.md#money) en `Money.kt` (lo importan `oop`, `exceptions` y
> `json`); `Numbers.kt` conserva `BigDecimal` suelto para las trampas (`fromDouble`, `commission`, `sameAmount`). Los
> 25 ejercicios actuales se conservan y se completan. Guía: [`01-basics.md`](lessons/01-language/01-basics.md).

| Java | Kotlin | Nota |
|---|---|---|
| `int` / `Integer` | `Int` / `Int?` | Primitivo salvo que sea nullable o argumento genérico (`List<Int>` guarda `Integer`) |
| `long x = i;` (widening implícito) | `i.toLong()` | **No hay conversiones implícitas** entre tipos numéricos |
| `Integer.parseInt` + `try/catch` | `raw.toIntOrNull() ?: default` | Sin excepciones; idiom `OrNull` + Elvis |
| `a.add(b)`, `a.compareTo(b) > 0` | `a + b`, `a * rate`, `a > b` | Operadores de la stdlib sobre `BigDecimal`; `==` sigue siendo `equals` |
| `a.equals(b)` / `a == b` | `a == b` / `a === b` | `==` maneja `null`; `===` es identidad (K2 avisa si es sobre `Int?`) |
| `Object`, `void`, — | `Any`/`Any?`, `Unit`, `Nothing` | `Nothing` es el tipo de `throw` y de `fail()`: hace compilar `?: fail(…)` |
| `Math.floorMod(a, b)`, `(int) c` | `a.mod(b)` `[1.5]`, `c.code` `[1.5]` | `%` es `rem` (signo del dividendo); `Char` no es número |
| `Integer.toUnsignedString`, `Byte.toUnsignedInt` | `UInt`, `ULong`, `1u + 2u` `[1.5]` | Existen; poco usados fuera de bytes y protocolos |

**Trampas:**
- `BigDecimal("1.0") == BigDecimal("1.00")` es `false`, porque `==` llama a `equals` y `equals` compara la escala;
  `Money` normaliza la escala en el `init` y las comparaciones de orden usan `compareTo`.
- `BigDecimal(0.1)` arrastra el error binario del `Double`: el dinero se construye desde `String` o desde centavos
  `Long`, nunca desde `Double`.
- El overflow de `Int` es silencioso, igual que en Java (`Math.addExact` lo detecta) y `Math.abs(Int.MIN_VALUE)` es
  negativo.
- `===` entre dos `Int?` iguales depende de la caché de `Integer` (−128..127), por el boxing; 2.4 lo marca con un
  aviso.
- `-3 % 2 == -1` en los dos lenguajes; para un residuo siempre positivo se usa `mod`.

**Caso:** `Money(amount, currency)` del Banco: escala de la moneda (`Currency.defaultFractionDigits`), `HALF_EVEN`
por defecto y `HALF_UP` para comparar, `require` si las monedas no coinciden, reparto de un monto en partes sin
perder centavos (comisiones y prorrateos). **Ruta 🎯:** bloque 2 (parseo seguro de montos, reparto, modos de
redondeo, `Nothing`) y bloque 5 (`require` en `Money`).

**🎤 Entrevista:** ¿por qué `BigDecimal("1.0") == BigDecimal("1.00")` es `false` y cómo se compara dinero? · Si en
Kotlin todo parece objeto, ¿dónde quedan los primitivos y cuándo hay boxing? · ¿Qué es `Nothing` y para qué sirve?

**⚡ Calculadora de precisión arbitraria:** enteros de tamaño arbitrario sobre un `IntArray` de dígitos (suma,
resta, multiplicación y división con `n` decimales y redondeo `HALF_EVEN`), es decir, un `BigInteger` propio · **Se
verifica con:** pruebas contra `java.math.BigInteger`/`BigDecimal` sobre 10 000 pares aleatorios con semilla fija,
más los bordes (cero, negativos, divisor cero con `ArithmeticException`).

### 01 · `control` — `if`/`when` como expresiones, rangos, bucles

**Objetivo:** escribir el control de flujo como expresiones, recorrer rangos sin `for (;;)` y resolver los
algoritmos de pizarra (búsqueda binaria, two-sum, ordenamientos) en Kotlin idiomático.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (en los casos desde B0′, tanda 4)

> **B0′, tanda 4 (hecho):** los tramos de edad pasaron a tramos de comisión por volumen (`commissionRate`, con `..<`
> sobre `Long`) y la búsqueda binaria se prueba sobre precios en centavos. Los algoritmos de pizarra se quedan. Guía:
> [`02-control.md`](lessons/01-language/02-control.md).

| Java | Kotlin | Nota |
|---|---|---|
| `c ? a : b` | `if (c) a else b` | `if` es expresión; no hay ternario |
| `switch` con patrones y `case Integer i when i > 0` (21) | `when (x) { is Int if x > 0 -> … }` `[2.2]` | Sin fall-through; varios valores y rangos por rama (`in 0..<100`) |
| Cadena `if / else if` | `when { cond -> … }` | `when` sin argumento |
| `for (int i = 0; i < n; i++)` | `for (i in 0..<n)` `[1.9]`, `0 until n`, `indices` | `..` es inclusivo; `downTo` y `step` para lo demás |
| `for` con índice | `for ((i, x) in xs.withIndex())` | Destructuring |
| `outer: … break outer;` | `break@outer`; `break`/`continue` dentro de `forEach { }` `[2.2]` | Etiquetas; desde 2.2 también en lambdas inline |
| Recursión de cola a mano | `tailrec fun` | El compilador la convierte en bucle |
| `Deque` / `Stack` | `ArrayDeque` `[≤ 1.4]` | `addLast`, `removeLastOrNull` |

**Trampas:**
- `0..n` incluye `n`: al portar `i < n` sale el off-by-one; se usa `0..<n` o `indices`.
- `return` dentro de `forEach { }` sale de la función que lo contiene, no de la iteración (`return@forEach` es el
  `continue`).
- `10..0` es un rango vacío (no cuenta hacia atrás) y `step` no acepta negativos: hace falta `downTo`.
- Un `when` usado como sentencia sobre un `sealed` o un `enum` sin todas las ramas es error desde
  1.7 `[1.7]` (en 1.6 era un aviso).
- `(lo + hi) / 2` desborda con índices grandes, igual que en Java: `(lo + hi) ushr 1`.

**Caso:** tramos de comisión por volumen operado (Mercado) con `when` y rangos; la búsqueda binaria del precio en un
arreglo ordenado de precios en centavos; el arbitraje y los grafos quedan para ejercicios 🔴. **Ruta 🎯:** bloque 3
(tramos de comisión) y bloque 4 (primer índice que cumple, ventana deslizante).

**🎤 Entrevista:** ¿por qué Kotlin no tiene ternario ni `for (;;)`? · ¿Qué hace `return` dentro de un `forEach`? ·
Escribe una búsqueda binaria sin overflow.

**⚡ Matcher por búsqueda binaria:** el libro de órdenes de un ticker sobre arreglos ordenados (precios en centavos y
colas de cantidades): cada orden entra con búsqueda binaria e inserción, y se empareja por precio-tiempo con bucles y
`when` (en `collections` se rehace con `TreeMap` + `ArrayDeque`) · **Se verifica con:** una secuencia fija de órdenes
cuyas ejecuciones coinciden con una lista esperada, y una comprobación después de cada orden de que el libro nunca
queda cruzado.

### 01 · `strings` — Plantillas, extensiones de `String`, regex

**Objetivo:** reemplazar `String.format`, `StringBuilder`, `StringUtils` y `Pattern` por la stdlib de Kotlin, y
parsear texto con regex sin caer en las diferencias con Java.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (en los casos desde B0′, tanda 4)

> **B0′, tanda 4 (hecho):** la fecha con grupos, el zip y la tarjeta pasaron a la forma de las cuentas `ACC-…`
> (`isAccountFormat`, `accountParts`, `maskAccount`) y a `parseFix`; los dígitos de control mod 97 quedan como
> ejercicio. La trampa de `split("\\/")` se queda, ahora también con FIX. Guía: [`03-strings.md`](lessons/01-language/03-strings.md).

| Java | Kotlin | Nota |
|---|---|---|
| `"Hola " + name`, `String.format`, `formatted` (15) | `"Hola $name, ${x + 1}"`, `"%.2f".format(x)` | Plantillas; `format` sigue para decimales |
| Text blocks (15) | `"""…""".trimIndent()`; `$$"""…"""` `[2.2]` | Raw strings sin escapes; con `$$` el `$` suelto es literal |
| `StringBuilder` | `buildString { append(…) }` | Lambda con receptor |
| `s.substring(s.indexOf('=') + 1)` | `substringAfter('=')`, `substringBefore`, `…Last` | Con valor por defecto si falta el delimitador |
| `toLowerCase()`, `StringUtils.capitalize` | `lowercase()`, `replaceFirstChar { it.uppercase() }` `[1.5]` | `toLowerCase()` y `capitalize()` están deprecados `[dep 1.5]` |
| `Pattern`, `Matcher`, `m.group("year")` | `Regex`, `matchEntire`, `findAll`, `groups["year"]`, `destructured` | `findAll` es una `Sequence` |
| `matcher.region(i, n).lookingAt()` | `regex.matchAt(text, i)` `[1.7]` | Coincidencia anclada en una posición (tokenizers) |
| `s.split("\\|")` (regex) | `s.split("|")` (literal) / `s.split(Regex("…"))` | **Ver trampa** |

**Trampas:**
- `split(String)` es **literal**: `"35=D|55=ACME".split("\\|")` devuelve la cadena entera y `"03/14/1985".split("\\/")`
  también; es el error típico al portar Java.
- `toLowerCase()` de Java usa el locale por defecto (la "i" turca); `lowercase()` usa `Locale.ROOT`, y desde 2.1
  `toLowerCase()` ya no compila (deprecación con error).
- En una plantilla, `"$price"` interpola: para un `$` literal hace falta `\$` o `$$"…"` `[2.2]`.
- `matches` exige que coincida toda la cadena y `containsMatchIn` busca en cualquier parte, igual que
  `matches()`/`find()` de Java pero con otros nombres.
- Un `Regex(…)` dentro de una función se compila en cada llamada: va en un `val` de nivel superior o en un `companion`.

**Caso:** validar y generar números de cuenta `ACC-00001234-82` (dígitos de control mod 97, como el IBAN); parsear
mensajes estilo FIX (`35=D|55=ACME|54=1|38=10|44=100.00`) con regex y `associate`; formatear montos y tickers con
`padStart`/`padEnd`. **Ruta 🎯:** bloque 2 (mensajes FIX y código de cuenta).

**🎤 Entrevista:** ¿`split` acepta regex en Kotlin? · ¿Por qué `toLowerCase()` sin locale es un bug? · ¿Cómo se
escribe un `$` literal en una plantilla?

**⚡ Códec estilo FIX:** parsear y generar mensajes `8=FIX.4.4|9=…|35=D|…|10=…` con longitud (tag 9) y checksum (tag
10: suma de bytes mod 256, tres dígitos), y errores con el tag y la posición del problema · **Se verifica con:** ida
y vuelta sobre un juego de mensajes válidos, y mensajes corruptos (checksum malo, tag repetido, `44=abc`, longitud
falsa) que dan exactamente el error y la posición esperados.

### 01 · `oop` — Clases, herencia, `sealed`, enums, objetos

**Objetivo:** modelar dominio con clases, `data class`, jerarquías selladas, enums y objetos, y saber qué cambia
frente a Java en herencia, visibilidad e igualdad.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (en los casos desde B0′, tanda 3)

> **B0′, tanda 3 (hecho):** `ShippingMethod`, `Customer`, `Email`, `Cents` y las formas geométricas pasaron a
> `AccountType`/`AccountStatus`/`Side`, `Position`, `Ticker`, `TransferId` y `Order` sellado; las cuentas usan `Money`.
> Guía: [`04-oop.md`](lessons/01-language/04-oop.md).

| Java | Kotlin | Nota |
|---|---|---|
| Campos + constructor + getters | `class Account(val id: String, …)` + `init { require(…) }` | Constructor primario = propiedades; `private set` para el setter |
| Clases no-`final` | **`final` por defecto**; `open` para heredar | Rompe proxies: plugins `allopen`, `kotlin-spring`, `jpa` |
| `record` (16) | `data class` + `copy(…)`; `@JvmRecord` `[1.5]` | `@JvmRecord` exige `jvmTarget` 16 o más |
| `sealed interface … permits` (17) + `switch` | `sealed interface Order` `[1.5]` + `when` exhaustivo | Sin `permits`: las subclases van en el mismo módulo y paquete |
| `enum` + `values()`, `valueOf` | `enum class` + `entries` `[1.9]`, `companion object { fun of(…) }` | `entries` es una lista; `values()` copia un arreglo en cada llamada |
| Singleton, `static` | `object`, `companion object`, `data object` `[1.9]` | `data object` da `toString` legible en jerarquías selladas |
| Clase que envuelve un `long` | `@JvmInline value class` `[1.5]` | Sin objeto en runtime en la mayoría de los usos; `inline class` está deprecada `[dep 1.5]` |
| Decorator escrito a mano, `BigDecimal.add` | `class X(inner: Y) : Y by inner`; `operator fun plus` | Delegación de interfaz; operadores con nombre fijo |

**Trampas:**
- Las propiedades declaradas en el **cuerpo** de una `data class` no entran en `equals`, `hashCode` ni `toString`.
- Una `data class` con constructor `private` lo expone por `copy()`: desde 2.0 el compilador avisa `[2.0]`, y
  `@ConsistentCopyVisibility` hace `copy()` tan privado como el constructor.
- `by` no delega `toString`, `equals` ni `hashCode`.
- `protected` no incluye el paquete y no hay package-private: lo más cercano es `internal` (visible en el módulo).
- `value class Money` no puede llevar la moneda: una `value class` envuelve una sola propiedad.

**Caso:** `Order` sellado (`LimitOrder`, `MarketOrder`) con `Side` (`BUY`/`SELL`); `AccountType` y `AccountStatus`
como `enum` con `of`; `Money` como `data class` con `plus`, `minus`, `times`, `unaryMinus` y `compareTo`; `Account`
con estado y `private set`; identificadores tipados (`TransferId`) con `value class`. **Ruta 🎯:** bloque 1
(`data class Tick`) y bloque 3 (enum con `of`).

**🎤 Entrevista:** `data class` frente a `record` y Lombok · ¿Por qué las clases son `final` por defecto y qué rompe
en Spring e Hibernate? · ¿Por qué `Money` no puede ser `value class`? · ¿`sealed` o `enum`?

**⚡ Evaluador de expresiones con `sealed`:** un AST sellado (`Num`, `Var`, `Neg`, `Bin`, `Call`) para fórmulas de
comisión como `max(1.00, 0.001 * notional)`, con evaluación sobre `BigDecimal`, simplificación y vuelta a texto ·
**Se verifica con:** pruebas de evaluación, de simplificación (`x * 1 → x`, `0 + x → x`), de ida y vuelta
texto → AST → texto, y una prueba que documenta que agregar un nodo sin su rama en `when` no compila.

### 01 · `io` — Archivos, recursos, consola, logging

**Objetivo:** leer y escribir archivos con `kotlin.io.path` sin ceremonia, procesar archivos grandes de forma
perezosa, cargar recursos del classpath y escribir código de consola que se pueda probar.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** existe (en el Banco desde B0′, tanda 2)

> **B0′, tanda 2 (hecho):** `sales.csv` y `Sale` pasaron a `bank-statement.csv` y `StatementLine`, con
> `ledger-movements.csv` como contraparte y una conciliación exacta (`reconcileExact`); las tolerancias son el ⚡.
> Guía: [`05-io.md`](lessons/01-language/05-io.md).

| Java | Kotlin | Nota |
|---|---|---|
| `Files.readString`, `Files.writeString` | `path.readText()`, `writeText`, `appendText` `[1.5]` | Extensiones de `kotlin.io.path`, UTF-8 por defecto |
| `Files.lines(p)` + try-with-resources | `path.useLines { lines -> … }` | `Sequence` perezosa que cierra el archivo al salir |
| `try (var w = …) { }` | `w.use { }` | Mismo manejo de suprimidas |
| `Files.walk`, `DirectoryStream` | `path.walk()` `[2.1]`, `listDirectoryEntries("*.csv")` | `walk` es una `Sequence` |
| Borrar un árbol (no hay API) | `path.deleteRecursively()` `[exp 2.4]` | Pide `@OptIn(ExperimentalPathApi::class)` |
| `getClass().getResource(…)` | `object {}.javaClass.getResource("/data/…")?.readText()` | El objeto anónimo da una clase en funciones de nivel superior |
| `Scanner(System.in)`, `System.out.println` | `readln()`, `readlnOrNull()` `[1.6]`, `println` | Mejor: recibir `BufferedReader` y `Appendable` |
| SLF4J, Log4j | Kermit 2.2.0: `Logger.withTag("io").i { … }` | El mensaje en lambda solo se construye si se loguea |

**Trampas:**
- La `Sequence` de `useLines` solo vale **dentro** del bloque: si se devuelve sin consumir, se lee de un archivo ya
  cerrado.
- `readLines()` y `readText()` cargan todo el archivo; para el extracto completo se usa `useLines`.
- `line.split(",")` rompe las descripciones con comas entre comillas del extracto: hace falta un lector CSV que
  respete las comillas.
- `Class.getResource("data/x")` sin `/` inicial es relativo al paquete; con `ClassLoader.getResource` no lleva `/`.
- `readLine()` devuelve `String?` y `readln()` lanza al final de la entrada: elegir según quién deba manejar el EOF.

**Caso:** leer `bank-statement.csv` del classpath como `StatementLine` (fechas con desfase, centavos de diferencia,
descripciones con comas), escribir el reporte de conciliación y registrar con Kermit lo que no se pudo parsear.
**Ruta 🎯:** bloque 1 (recurso del classpath, Kermit, CSV a objetos).

**🎤 Entrevista:** ¿cómo se procesa un archivo de 10 GB sin cargarlo en memoria? · ¿Cómo se prueba código que lee de
la consola? · `use` frente a try-with-resources: ¿qué pasa con la excepción de `close()`?

**⚡ Conciliador de extracto:** una CLI que lee `bank-statement.csv` y los asientos del ledger, empareja con
tolerancias (±1 día, ±0.01 USD) y escribe el reporte de diferencias (conciliadas, solo en el extracto, solo en el
ledger, monto distinto) · **Se verifica con:** la salida idéntica a un archivo dorado, y una prueba con un extracto
sintético de un millón de líneas que corre con el heap limitado (lectura por streaming con `useLines`).

### 01 · `collections` — Solo lectura frente a mutable, estructuras

**Objetivo:** elegir la colección correcta (lista, mapa ordenado, cola, montículo, arreglo primitivo), entender qué
significa "solo lectura" en Kotlin y usar las estructuras del JDK sin perder la ergonomía de la stdlib.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** existe (en el Mercado desde B0′, tanda 4)

> **B0′, tanda 4 (hecho):** `OrderBook` (`TreeMap` por precio + `ArrayDeque` FIFO, sin emparejar) y cierres por fecha
> en un `TreeMap` (`closesBefore`, `closeAtOrBefore`); los ejemplos usan tickers. Guía:
> [`06-collections.md`](lessons/01-language/06-collections.md).

| Java | Kotlin | Nota |
|---|---|---|
| `List.of(…)` (inmutable) | `listOf(…)` (**solo lectura**) | La interfaz `List` no tiene escritura, pero el objeto de abajo puede cambiar |
| `new ArrayList<>()` + `add` | `mutableListOf()`, `buildList { add(…) }` `[1.6]` | `buildList` entrega una `List` de solo lectura al terminar |
| `list.get(i)`, `Collections.min` | `getOrNull(i)`, `firstOrNull()`, `min()` `[1.7]` / `minOrNull()` | `min()` lanza con lista vacía; `minOrNull()` no |
| `computeIfAbsent(k, …).add(v)`, `merge(k, 1, Integer::sum)` | `getOrPut(k) { mutableListOf() } += v`, `groupingBy { }.eachCount()` | |
| `for (Map.Entry<K, V> e : m.entrySet())` | `for ((k, v) in m)` | Destructuring |
| `TreeMap.headMap`, `floorEntry`, `PriorityQueue` | Los mismos de `java.util`; `heap += x` | `plusAssign` funciona sobre cualquier `MutableCollection` |
| `ArrayDeque` (`java.util`), `list.removeFirst()` (21) | `ArrayDeque` de `kotlin.collections`, `removeFirstOrNull()` | **Ver trampa** de `removeFirst` |
| `int[]`, `Integer[]`, `Arrays.equals` | `IntArray`, `Array<Int>`, `contentEquals`, `contentToString` | `IntArray` no hace boxing |

**Trampas:**
- Solo lectura ≠ inmutable: quien guardó la `MutableList` original puede cambiar lo que tú ves como `List`; la
  defensa es copiar con `toList()`.
- `removeFirst()`/`removeLast()` sobre una `MutableList`, compilados contra JDK 21, llaman a
  `java.util.List.removeFirst` (de `SequencedCollection`): `NoSuchMethodError` en JDK 17 o en Android. Ni 1.9.22 ni
  2.4.21 avisan (medido con `javap`); se usa `removeFirstOrNull()` o `removeAt(0)`.
- `mutableMapOf()` es un `LinkedHashMap` (orden de inserción) y `HashMap()` no garantiza orden: una prueba que
  dependa del orden pasa con uno y falla con el otro.
- `==` entre arreglos compara referencias (K2 lo avisa); hace falta `contentEquals`.
- `min()` y `max()` cambiaron de significado: deprecados (nullables) en 1.4 y de vuelta no nulos desde 1.7 `[1.7]`.

**Caso:** el `OrderBook` del Mercado: compras en `TreeMap<Long, ArrayDeque<LimitOrder>>` de mayor a menor precio,
ventas de menor a mayor, FIFO dentro de cada precio; ventana de 7 días de cierres con `subMap`; `PriorityQueue` de
alertas de precio; volumen por ticker y día. **Ruta 🎯:** bloque 3 (contador, solo lectura, conteo por ticker y día)
y bloque 4 (ventana de 7 días con `TreeMap`).

**🎤 Entrevista:** ¿`listOf` es inmutable? · ¿Qué estructura usarías para un libro de órdenes y por qué? · ¿Cómo
funciona un `HashMap` por dentro y qué pasa con un `hashCode` malo?

**⚡ `HashMap` propio:** un mapa de direccionamiento abierto (o de listas, a elegir) con redimensionamiento, que
implementa `MutableMap` y se usa con claves del caso (tickers y `AccountId`) · **Se verifica con:** una prueba
diferencial contra `java.util.HashMap` (100 000 operaciones aleatorias con semilla fija, mismo resultado en cada
paso), una prueba con claves de `hashCode` constante, y una medición con `measureTime` `[1.9]` frente al del
JDK.

### 01 · `functional` — Funciones, lambdas, scope functions, pipelines

**Objetivo:** reemplazar `Function<>`, Streams y `Collectors` por tipos función y operaciones de colección, saber
cuándo conviene `Sequence`, y usar las scope functions sin anidarlas.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** existe (en el Mercado desde B0′, tanda 4)

> **B0′, tanda 4 (hecho):** `Txn` y el `Order` mutable pasaron a `Tick` limpio y `OrderTicket`; los pipelines agrupan
> por ticker y `candleOf` arma una vela OHLC con `fold`. Los enunciados ya usan `Tick`. Guía:
> [`07-functional.md`](lessons/01-language/07-functional.md).

| Java | Kotlin | Nota |
|---|---|---|
| `Function<A, B>`, `Supplier<T>`, `Consumer<T>` | `(A) -> B`, `() -> T`, `(T) -> Unit` | Tipos función estructurales; `andThen` no viene, se escribe como extensión `infix` |
| `@FunctionalInterface` | `fun interface Rule` `[≤ 1.4]` | Conversión SAM desde lambda, también para interfaces Kotlin |
| `String::length`, `Tick::new` | `String::length`, `::Tick` | Referencias a función, propiedad y constructor |
| `stream().map().filter().collect(toList())` | `map { }.filter { }` sobre la `List` | **Ansioso**: una lista intermedia por paso; `asSequence()` para lo perezoso |
| `groupingBy(f, summingLong(g))`, `partitioningBy` | `groupBy { }.mapValues { }`, `groupingBy { }.fold(…)`, `partition { }` | `Grouping` no materializa las listas |
| `reduce(identity, op)`, `Stream.iterate` | `fold`, `runningFold` `[≤ 1.4]`, `generateSequence`, `sequence { yield(…) }` | `reduce` falla con colección vacía |
| `mapToLong(…).sum()`, `summaryStatistics()` | `sumOf { }` `[≤ 1.4]`; estadística con un `fold` | No hay `summaryStatistics`: se escribe en una pasada |
| — (en Java 21 no hay ventanas) | `windowed(7)`, `chunked`, `zipWithNext` | Medias móviles y deltas sin índices |

**Scope functions:** `apply` (configurar; devuelve el objeto), `also` (efecto lateral; devuelve el objeto), `let`
(transformar; `?.let` para nulos), `run` y `with` (calcular con el objeto como `this`).

**Trampas:**
- `map`/`filter` sobre `List` son ansiosos: en un pipeline largo sobre todo el feed, cada paso crea una lista; con
  `asSequence()` no, pero para listas chicas `Sequence` es más lenta.
- `sumOf { it.quantity }` con un lambda `Int` suma en `Int` y desborda en silencio: el volumen se suma en `Long`.
- `groupBy` materializa una lista por grupo; para contar o sumar alcanza `groupingBy { }.eachCount()` o `fold`.
- Scope functions anidadas sombrean `it` y `this`: más de dos niveles ya no se leen (es un clásico de revisión de PR).
- Una lambda que captura una `var` la envuelve en un `Ref` mutable (no hay "effectively final"): el valor que ve es
  el del momento en que corre.

**Caso:** el pipeline de limpieza del feed (`mapNotNull`, `distinctBy`, `sortedBy`), velas OHLC con `groupBy` +
`fold`, VWAP, media móvil de 7 días con `windowed`, volatilidad en una pasada y P&L del portafolio con `fold`.
**Ruta 🎯:** bloque 3 (`map`/`filter`/`sumOf`, `mapNotNull`, `groupBy`, `flatMap`, `fold`) y bloque 4 (estadística en
una pasada).

**🎤 Entrevista:** `Sequence` frente a `Stream` frente a `List`: ¿cuándo cada una? · ¿Cuándo usar `let`, `apply`,
`also`, `run`? · ¿Qué es una lambda con receptor y para qué sirve?

**⚡ Librería de operadores de `Sequence`:** operadores propios y perezosos con `sequence { }`: `windowedBy` (ventanas
por tiempo), `chunkedWhile`, `scanWhile`, `mergeSorted` de varias secuencias ordenadas, más indicadores (media móvil,
cruce de medias) para un backtest simple · **Se verifica con:** pruebas que comparan las velas y medias con las de
la versión ansiosa sobre `ticks.json`, un contador que demuestra cuántos elementos se consumen (pereza), y una
secuencia infinita que funciona con `take`.

### 01 · `json` — kotlinx-serialization

**Objetivo:** leer y escribir JSON sin Jackson ni reflexión, con la configuración tolerante que pide un feed real,
jerarquías selladas y serializadores propios.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** existe (en el Mercado desde B0′, tanda 2)

> **B0′, tanda 2 (hecho):** `OrderEvent`, `Address` y `Payment` pasaron a `Tick` y a `MarketEvent` sellado (`Trade`,
> `Quote`, `Halt`), leídos de `ticks.json`; el serializador de `Money` (`MoneyAsString`) llegó en la tanda 3. Versión: kotlinx-serialization
> **1.13.0** ([`Versions.md`](Versions.md)). Guía: [`08-json.md`](lessons/01-language/08-json.md).

| Java (Jackson) | Kotlin (kotlinx-serialization 1.13.0) | Nota |
|---|---|---|
| `ObjectMapper` (reflexión) | `Json { … }` + plugin `serialization` del compilador | Serializadores generados al compilar; un `Json` por configuración, no uno por llamada |
| `@JsonProperty("trade-id")` | `@SerialName("trade-id")` | `namingStrategy = SnakeCase` para todo el modelo (estable en 1.13) |
| `FAIL_ON_UNKNOWN_PROPERTIES = false` | `ignoreUnknownKeys = true` | Por defecto es estricto |
| Campo ausente → `null` | `explicitNulls = false` | Estable en 1.13 (en 1.6.2 pedía `@OptIn`) |
| `@JsonTypeInfo` + `@JsonSubTypes` | `@Serializable sealed interface MarketEvent` + `@SerialName` por subclase | Discriminador `"type"`; `classDiscriminatorMode` todavía pide opt-in |
| `JsonSerializer<BigDecimal>` | `object MoneySerializer : KSerializer<Money>` + `@Serializable(with = …)` | Los tipos de Java (`BigDecimal`, `Currency`, `Instant`) no son serializables solos |
| `JsonNode`, `ObjectNode.put` | `JsonElement`, `jsonPrimitive.longOrNull`, `buildJsonObject { }` | Árbol sin clases y un DSL para construirlo |
| `MappingIterator` (streaming) | `Json.decodeToSequence<Tick>(input)` | Pide `@OptIn(ExperimentalSerializationApi::class)` en 1.13 |

**Trampas:**
- Nullable no es opcional: un `price: Long?` sin valor por defecto es una clave **obligatoria**, salvo con
  `explicitNulls = false`.
- Los valores por defecto no se escriben (`encodeDefaults = false`): el que consume el JSON ve campos ausentes.
- Un `Money` serializado como número pasa por `Double` en muchos clientes: se escribe como cadena (`"100.00"`).
- Una propiedad que se llame `type` choca con el discriminador por defecto de las jerarquías selladas.
- Construir `Json { … }` en cada llamada recrea la configuración y sus cachés: va en un `val`.

**Caso:** `Tick` con `@SerialName` y `explicitNulls`; `MarketEvent` sellado; `Money` con serializador propio;
cargar `ticks.json` con nulos, claves desconocidas y duplicados, y descartar lo inválido con un reporte. **Ruta 🎯:**
bloque 1 (★ claves con guiones y campo que falta; ida y vuelta; cargar con tolerancia).

**🎤 Entrevista:** Jackson frente a kotlinx-serialization: ¿reflexión o plugin, y qué cambia? · ¿Cómo se serializa
una jerarquía sellada? · ¿Qué diferencia hay entre un campo nullable y uno opcional?

**⚡ Serializador para un formato binario:** un `Encoder`/`Decoder` propio para un formato compacto (enteros con
*varint*, cadenas con prefijo de longitud, sin nombres de campo) que sirve para cualquier clase `@Serializable` ·
**Se verifica con:** ida y vuelta de `Tick`, `MarketEvent` y listas; bytes exactos para un caso fijo; y una prueba
que mide que `ticks.json` en binario ocupa menos que en JSON.

### 01 · `datetime` — `java.time` correcto

**Objetivo:** usar `java.time` sin los errores clásicos (zonas, horario de verano, formatos estrictos, "ahora" sin
inyectar) y saber qué aporta la stdlib de Kotlin (`kotlin.time`).
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (en los casos desde B0′, tanda 4)

> **B0′, tanda 4 (hecho):** la edad, el epoch y la ventana de 90 días pasaron al calendario del Mercado de Nueva York
> (`marketDateOf`, `sessionBounds`, `isMarketOpen`, `HOLIDAYS_2026` ficticios), `Clock` inyectado (`isOpenNow`), ventana
> de 7 días y cierre de mes (`monthClose`). Guía: [`09-datetime.md`](lessons/01-language/09-datetime.md).

| Java | Kotlin | Nota |
|---|---|---|
| `Instant.now()` dentro de la lógica | `fun isOpen(clock: Clock = Clock.systemUTC())` | `Clock` como parámetro por defecto: se prueba con `Clock.fixed` |
| `Duration.ofMinutes(5)` | `5.minutes` `[1.6]` (`kotlin.time.Duration`), `toJavaDuration()` | Dos `Duration` con el mismo nombre: cuidado con los imports |
| `System.nanoTime()` y restar | `measureTime { }` `[1.9]`, `TimeSource.Monotonic` | Tiempo monotónico, no de reloj |
| `Instant`, `Clock` de `java.time` | `kotlin.time.Instant`, `Clock.System.now()` `[2.3]` | En la stdlib; útil en multiplataforma, en la JVM se convierte |
| `!d.isBefore(a) && d.isBefore(b)` | `d in a..<b` `[1.9]` | Rangos sobre cualquier `Comparable` |
| `Stream.iterate(d, x -> x.plusDays(1))` | `generateSequence(d) { it.plusDays(1) }.takeWhile { … }` | Días como `Sequence` |
| `ofPattern("MM/dd/yyyy")` | `ofPattern("MM/dd/uuuu").withResolverStyle(STRICT)` | Igual que Java: `uuuu` para que `STRICT` funcione |
| Joda-Time / `java.time` | kotlinx-datetime ⚠️ (se fija en `11-multiplatform`) | En la JVM no hace falta |

**Trampas:**
- El día de mercado no es el día UTC: un tick de las 20:30 en Nueva York es del día siguiente en UTC; se agrupa por
  fecha en `America/New_York`.
- Con horario de verano un día dura 23 o 25 h: `plusHours(24)` no es `plusDays(1)`, y la sesión de 09:30 a 16:00 cae
  a distinta hora UTC en marzo y en noviembre.
- `yyyy` con `ResolverStyle.STRICT` falla porque es "año de la era": hace falta `uuuu`.
- `plusMonths(1)` sobre el 31 de enero da el 28 o 29 de febrero, y volver con `minusMonths(1)` no regresa al 31.
- `LocalDateTime` no es un instante: guardar un tick como `LocalDateTime` pierde la zona y la hora de verano.

**Caso:** horario del mercado (09:30–16:00 en `America/New_York`, de lunes a viernes, con feriados ficticios), día
hábil de un tick, ventana de 7 días de cierres, cierre de mes del Banco, todo con `Clock` inyectado. **Ruta 🎯:**
bloque 2 (instante del tick en la zona del mercado, día hábil, fecha estricta), bloque 3 (agrupar por día de mercado)
y bloque 4 (★ ventana de 7 días).

**🎤 Entrevista:** `Instant`, `LocalDateTime` y `ZonedDateTime`: ¿cuál se guarda y cuál se muestra? · ¿Cómo se
prueba código que depende de "ahora"? · `Duration` frente a `Period`.

**⚡ Velas por sesión:** agrupar los ticks en velas de 1 minuto y de sesión (09:30–16:00 en Nueva York) respetando la
zona y el horario de verano, con los ticks fuera de horario descartados y reportados · **Se verifica con:** pruebas
con ticks de los días de cambio de horario de marzo y noviembre (las dos sesiones duran 6.5 h y una vela por día UTC
daría otro resultado) y con el tick fuera de horario de `ticks.json`.

### 01 · `exceptions` — Sin checked exceptions, errores como valores

**Objetivo:** manejar errores sin checked exceptions: precondiciones de la stdlib, jerarquías selladas, `Result` y
errores como valores, y saber cuándo usar cada uno.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** existe (en el Banco desde B0′, tanda 3)

> **B0′, tanda 3 (hecho):** `OrderException`, `OrderService` y el cliente de inventario pasaron a `BankException`
> sellada, `TransferService` y `FxRateClient`; la validación de `Applicant` pasó a `TransferRequest`. Guía:
> [`10-exceptions.md`](lessons/01-language/10-exceptions.md).

| Java | Kotlin | Nota |
|---|---|---|
| Checked exceptions, `throws` | No existen; `@Throws(IOException::class)` para quien llama desde Java | `Files.readString` compila sin `try` |
| `try` como sentencia | `val x = try { … } catch (e: E) { … }` | `try` es expresión |
| `throw` como sentencia | `?: throw …`, `?: error("…")` | `throw` es expresión de tipo `Nothing` |
| `Objects.requireNonNull`, `Preconditions.checkArgument` | `require`, `requireNotNull`, `check`, `checkNotNull`, `error` | `require` → `IllegalArgumentException`, `check` → `IllegalStateException`; hacen smart cast |
| `catch (A \| B e)` | `catch (e: Exception) { when (e) { is A, is B -> …; else -> throw e } }` | No hay multi-catch |
| Jerarquía de excepciones | `sealed class BankException : RuntimeException` + `when` exhaustivo | Traducción a HTTP sin `else` |
| try-with-resources + suprimidas | `use { }` | Mismo comportamiento con `addSuppressed` |
| `Optional`, `Either` de Vavr | `Result<T>` `[1.5]` (`runCatching`, `getOrElse`, `mapCatching`) o un `sealed` propio | Arrow (`Either`, `Raise`) en `07 · fp` |

**Trampas:**
- `runCatching` atrapa `Throwable`: se traga la `CancellationException` de una corrutina (y los `Error`); en código
  con corrutinas hace falta relanzarla (`runCatchingCancellable` de la lección).
- `require` frente a `check`: confundirlos cambia `IllegalArgumentException` (400, culpa del cliente) por
  `IllegalStateException` (500, culpa nuestra).
- Desde Java, un método Kotlin que lanza `IOException` sin `@Throws` no se puede atrapar: `javac` dice que el `catch`
  es inalcanzable.
- Un `return` en `finally` se traga la excepción, igual que en Java.
- Devolver `Result<T>` de una función no compilaba antes de 1.5; en código viejo aparece envuelto.

**Caso:** `BankException` sellada (`InsufficientFunds`, `AccountFrozen`, `CurrencyMismatch`, `DuplicateTransfer`,
`UnbalancedEntry`), `TransferService` con `require`/`check`, validación de una `Transfer` que acumula errores, causa
encadenada al envolver la falla de un servicio externo, y su traducción a códigos HTTP. **Ruta 🎯:** bloque 5 (★ `!!`
en un PR y `require`/`check`; envolver sin perder la causa; jerarquía sellada a HTTP).

**🎤 Entrevista:** ¿por qué Kotlin no tiene checked exceptions y qué se pierde? · Excepciones, `Result` o `sealed`:
¿cuándo cada uno? · ¿Qué pasa con `runCatching` dentro de una corrutina cancelada?

**⚡ `TransferService` completo:** transferencias entre cuentas con `BankException` sellada, validación que acumula
todos los errores de una solicitud, idempotencia por clave (la misma clave devuelve el mismo resultado) y traducción
a HTTP · **Se verifica con:** una prueba parametrizada de tabla (solicitud → resultado o excepción → código HTTP), un
reintento con la misma clave que no duplica asientos, y una comprobación de que ningún saldo queda negativo.

### 01 · `generics` — Varianza, `*`, `reified`, `T & Any`, `typealias`

**Objetivo:** escribir APIs genéricas que acepten lo que deben (varianza en la declaración y en el uso), saber qué se
borra en runtime y cuándo `reified` lo evita, y leer las firmas genéricas de la stdlib sin adivinar.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** nueva

| Java | Kotlin | Nota |
|---|---|---|
| `List<? extends MarketEvent>` (en cada uso) | `interface Source<out T>` (en la declaración) o `List<out T>` (en el uso) | Varianza en la declaración: se escribe una vez, no en cada firma |
| `Consumer<? super Trade>` | `fun interface Handler<in T>` | PECS sin repetir el comodín: un `Handler<MarketEvent>` sirve donde se pide `Handler<Trade>` |
| Tipo crudo `List`, `List<?>` | `List<*>` `[≤ 1.4]` | Proyección estrella: se lee como `Any?`, no se escribe |
| `<T extends Comparable<T> & Serializable>` | `fun <T> f() where T : Comparable<T>, T : java.io.Serializable` | Varias cotas con `where` |
| `Class<T> type` como parámetro extra | `inline fun <reified T> decode(…)` `[≤ 1.4]` | El tipo existe en runtime dentro de la función inline; no se llama desde Java |
| `@NonNull T` (sin efecto en el compilador) | `T & Any` `[1.7]`, `T : Any` | `T` sin cota admite `null`; `T & Any` lo excluye en un uso puntual |
| `Collections.<String, Integer>emptyMap()` | `pick<_, Int>(m)` `[1.7]` | Guion bajo: se infiere uno y se escribe el otro |
| — (no hay alias de tipo) | `typealias Prices = Map<String, Money>` `[≤ 1.4]`; anidado en una clase `[2.3]` | Solo un nombre: no crea un tipo nuevo (para eso, `value class`) |

**Trampas:**
- El borrado es el mismo que en Java: `x is List<String>` no compila y `List<Trade>` y `List<Quote>` son la misma clase
  en runtime; `reified` solo existe dentro de funciones `inline`.
- `Array<T>` es invariante, a diferencia del arreglo covariante de Java (que acepta la asignación y lanza
  `ArrayStoreException` al escribir); `Array<Trade>` no se pasa como `Array<MarketEvent>`.
- `MutableList<*>` no acepta `add` de nada y `List<Any?>` sí acepta cualquier cosa: no son lo mismo.
- Un `typealias` no protege: `typealias AccountId = String` acepta cualquier `String`, incluido un ticker.
- Desde Java, `List<out T>` en un parámetro se ve como `List<? extends T>` y en un retorno sin comodín; si una API
  Java lo necesita distinto hace falta `@JvmSuppressWildcards` o `@JvmWildcard`.

**Caso:** la jerarquía `MarketEvent` (`Trade`, `Quote`, `Halt`) de `json` con un `Source<out MarketEvent>` del feed y
`Handler<in Trade>` para los suscriptores; `typealias Prices = Map<String, Money>`; un `decode<T>()` con `reified` sobre
`ticks.json`; un `OrderBook<O : Order>` genérico sobre las órdenes de `oop`. **Ruta 🎯:** no entra (la ruta cubre las
10 lecciones base).

**🎤 Entrevista:** ¿qué significan `out` e `in` y cómo se relacionan con `? extends`/`? super`? · ¿Por qué `reified`
exige `inline`? · ¿Por qué `List` es covariante y `MutableList` no?

**⚡ Colecciones persistentes covariantes:** `PersistentList<out T>` y `PersistentMap<K, out V>` inmutables con
compartición estructural (cada `plus` devuelve una versión nueva sin copiar todo) · **Se verifica con:** una prueba
diferencial contra `List`/`Map` de la stdlib (10 000 operaciones aleatorias con semilla fija), una prueba de que una
versión vieja no cambia después de un `plus`, y una que asigna un `PersistentList<Trade>` a un
`PersistentList<MarketEvent>` (varianza) y documenta en un comentario por qué la versión mutable no compilaría.

### 01 · `inline` — `noinline`, `crossinline`, contratos, `value class`

**Objetivo:** saber qué hace `inline` con el bytecode y cuándo vale la pena, controlar los `return` dentro de lambdas,
y tipar valores (`AccountId`, cantidades, centavos) con `value class` sin pagar un objeto por valor.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** nueva

| Java | Kotlin | Nota |
|---|---|---|
| Lambda → objeto `Function` (o `invokedynamic`) en cada llamada | `inline fun <T> measured(block: () -> T)` | El cuerpo y la lambda se copian en el llamador: sin objeto, sin llamada virtual |
| `return` en una lambda sale solo de la lambda | `return` en la lambda de una `inline` sale de la función que llama | *Non-local return*; `return@forEach` es el local |
| Pasar la lambda a otro hilo o guardarla | `crossinline` `[≤ 1.4]` / `noinline` | `crossinline` prohíbe el `return` no local; `noinline` deja esa lambda como objeto |
| `Objects.requireNonNull(x)` (el compilador no aprende nada) | `contract { returns() implies (x != null) }` `[exp 2.4]` | Experimental desde 1.3; así funcionan los smart casts después de `require` |
| Clase que envuelve un `long` (un objeto por valor) | `@JvmInline value class Cents(val v: Long)` `[1.5]`; genérica `[1.8]` | En runtime es un `long` salvo cuando se usa como nullable, genérico o interfaz |
| — | `break`/`continue` dentro de una lambda inline `[2.2]` | Antes había que usar etiquetas o `return@` |
| — | `@PublishedApi internal` | Una `public inline fun` no puede usar miembros privados: se copia en el código del cliente |

**Trampas:**
- `inline` en una función que no recibe lambdas solo agranda el bytecode; el compilador avisa ("expected performance
  impact … is insignificant").
- `value class` se boxea en `List<Cents>`, en `Cents?` y al pasarla como interfaz; además las funciones que la
  reciben llevan un nombre alterado (`credit-abc12`) que Java no puede llamar (`@JvmExposeBoxed` es `[exp 2.4]`).
- `inline class` está deprecada `[dep 1.5]`: hoy se escribe `@JvmInline value class`.
- El compilador no verifica los contratos: uno falso (`returns() implies (x != null)` sin comprobarlo) produce smart
  casts falsos y un NPE donde el código parecía seguro.
- Una lambda `crossinline` que se ejecuta en otro hilo ya no está dentro del `try` del llamador: sus excepciones no
  las atrapa quien llamó a la función inline.

**Caso:** `AccountId`, `TransferId` y `Cents` como `value class` del Banco; `inline fun <T> Connection.inTransaction`
(ya está en `jdbc`) y un `retrying` para el `FxRateClient` de `exceptions`, con `crossinline` para el registro de cada
intento; un `requireBalanced` con contrato para el invariante de `JournalEntry`. **Ruta 🎯:** no entra.

**🎤 Entrevista:** ¿qué hace `inline` y cuándo no conviene? · ¿Qué es un *non-local return* y por qué existe
`crossinline`? · ¿Cuándo una `value class` deja de ser gratis?

**⚡ Utilidades sin asignaciones:** `measured`, `retrying` y `withLock` como funciones `inline` y `Cents`/`AccountId`
como `value class`, usadas por un servicio de transferencias en memoria · **Se verifica con:** `javap -c` sobre la
clase que las usa: cero `invokedynamic` y cero clases `$lambda` para las lambdas inline, `Cents` como `long` en las
firmas; una prueba de que el `return` no local sale de la función llamadora; y una de que la lambda `noinline`
guardada se puede invocar después.

### 01 · `interop` — Platform types, `@Jvm*`, Java y Kotlin en el mismo módulo

**Objetivo:** llamar a Java desde Kotlin sin NPE escondidos y escribir APIs Kotlin que Java use con naturalidad, en un
módulo que mezcla los dos lenguajes.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** nueva

| Java | Kotlin | Nota |
|---|---|---|
| Método sin anotar que puede devolver `null` | Platform type `String!` | Kotlin no sabe si es nullable: decide quien lo recibe |
| `@Nullable` de JSpecify | `String?` real: error al usarlo sin `?.` `[2.1]` | Medido: ignorado en 1.7, aviso de 1.8 a 2.0, error desde 2.1 |
| `static` en una clase | `companion object` + `@JvmStatic`; funciones de nivel superior → `ArchivoKt` | Sin `@JvmStatic`, Java escribe `Rates.Companion.of(…)` |
| Sobrecargas para "parámetros opcionales" | Parámetros por defecto + `@JvmOverloads` `[≤ 1.4]` | Genera las sobrecargas quitando parámetros desde el final |
| `public static final` / campo público | `const val`, `@JvmField` | Sin ellos Java ve un getter |
| `throws IOException` | `@Throws(IOException::class)` | Sin ella, `javac` no deja atrapar la excepción ("never thrown") |
| `record` | `@JvmRecord data class` `[1.5]` | Exige `jvmTarget` 16 o más |
| Método `default` en una interfaz | Método con cuerpo en una interfaz Kotlin → `default` de Java `[2.2]` | Medido con `javap`: hasta 2.1 era abstracto con el cuerpo en `DefaultImpls` |

**Trampas:**
- Asignar un platform type a un `String` no comprueba nada: el NPE salta en el primer uso, lejos de la llamada a
  Java; se declara el tipo (`String?`) en el borde.
- Subir de compilador puede romper el build: el mismo `Rates.find(code).length` sobre un `@Nullable` de JSpecify pasa
  de aviso (2.0) a error (2.1).
- Renombrar `Rates.kt` cambia la clase `RatesKt` que ven los llamadores Java; `@file:JvmName("Rates")` fija el nombre.
- Una `value class` o una función `suspend` en una API pública es casi inutilizable desde Java (nombre alterado,
  `Continuation` como último parámetro).
- Las colecciones de solo lectura de Kotlin son `java.util.List` mutables para Java: un `add` desde Java las cambia
  (o lanza `UnsupportedOperationException`, según la implementación).

**Caso:** un cliente Java heredado de tasas de cambio (`LegacyFxClient`, con JSpecify) envuelto por el `FxRateClient`
de `exceptions`, y la API Kotlin del ledger (`Money`, `TransferService`) consumida desde una prueba escrita en Java.
**Ruta 🎯:** no entra.

**🎤 Entrevista:** ¿qué es un platform type y por qué existe? · ¿Qué anotaciones pones para que Java use bien tu API
Kotlin? · ¿Cómo se ve un `companion object` desde Java?

**⚡ Módulo mixto Java + Kotlin:** `src/main/java` con el cliente heredado y `src/main/kotlin` con la API nueva, en el
mismo módulo de Gradle · **Se verifica con:** una prueba JUnit escrita en Java que usa la API Kotlin sin
`Companion`, sin `INSTANCE` y con parámetros opcionales; el build con `-Werror` y JSpecify en modo estricto; y una
salida de `javap` guardada en la prueba que muestra los métodos `static` y las sobrecargas generadas.

### 01 · `delegation` — Delegados propios, `by map`, delegación de clase

**Objetivo:** reemplazar decoradores, holders perezosos y listeners escritos a mano por delegación de clase y de
propiedades, y escribir delegados propios (`getValue`/`setValue`, `provideDelegate`).
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** nueva

| Java | Kotlin | Nota |
|---|---|---|
| Decorator que reenvía cada método a mano (o `@Delegate` de Lombok) | `class AuditedLedger(inner: Ledger) : Ledger by inner` | Se sobrescribe solo lo que cambia |
| *Holder* perezoso, doble chequeo con `volatile` | `val rates by lazy { load() }` | `SYNCHRONIZED` por defecto; `LazyThreadSafetyMode` para otras políticas |
| `PropertyChangeSupport` y listeners | `Delegates.observable`, `Delegates.vetoable` | `observable` avisa después del cambio; `vetoable` decide antes |
| Configuración leída de un `Map` con casts | `class Limits(m: Map<String, Any?>) { val daily: Long by m }` `[≤ 1.4]` | La clave es el nombre de la propiedad |
| Un campo que refleja a otro | `var b by this::a` `[≤ 1.4]` | Útil al renombrar una propiedad sin romper a los llamadores |
| — | `operator fun getValue`/`setValue`, `ReadOnlyProperty`, `provideDelegate` | `provideDelegate` corre al crear el objeto: valida antes del primer acceso |
| Campo privado mutable + getter de solo lectura | `val orders: List<Order> field = mutableListOf()` `[2.4]` | *Explicit backing field*: sin la pareja `_orders`/`orders` |

**Trampas:**
- La delegación de clase no es herencia: dentro de `inner`, `this` es `inner`, así que sus llamadas internas no pasan
  por lo que sobrescribió la clase que delega.
- El delegado se fija al construir: si se delega en un parámetro y después se quiere cambiar la instancia, no cambia.
- `by map` busca la clave en cada acceso y lanza `NoSuchElementException` si falta; con `Map<String, Any?>` el tipo se
  comprueba recién al usar el valor (`ClassCastException` tardía).
- Una inicialización de `lazy` que lanza no se guarda: el siguiente acceso vuelve a intentar.
- `Delegates.notNull()` y `lateinit` fallan en runtime, no en compilación, y `lateinit` no admite tipos primitivos.

**Caso:** `AuditedLedger : Ledger by inner` que registra cada asiento del Banco; `AccountLimits` leídos de un `Map`
con `by map` y validados con `vetoable` (un límite diario nunca negativo); el historial de estados de una `Account`
con `observable`; un delegado propio `setting<T>()` que valida la clave en `provideDelegate`. **Ruta 🎯:** no entra.

**🎤 Entrevista:** ¿qué diferencia hay entre delegar con `by` y heredar? · ¿Es seguro `by lazy` entre hilos y qué
cuesta? · ¿Cómo se escribe un delegado de propiedad propio?

**⚡ Configuración tipada con delegados:** una clase `BankSettings` cuyas propiedades salen de un `Map` (o de variables
de entorno) con `by setting<Int>("retries", default = 3)`, con validación, valores por defecto y un registro de
cambios · **Se verifica con:** pruebas de que una clave mal escrita falla **al construir** el objeto (`provideDelegate`)
y no en el primer acceso, de que un valor fuera de rango se rechaza con `vetoable`, y de que el registro de cambios
queda en orden.

### 01 · `dsl` — `@DslMarker`, builders

**Objetivo:** diseñar DSLs type-safe con lambdas con receptor, evitar que se mezclen los receptores de bloques
anidados y saber cuándo un DSL ayuda y cuándo estorba.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java | Kotlin | Nota |
|---|---|---|
| Builder con `withX(…).withY(…).build()` | `journalEntry { debit(…); credit(…) }` | Lambda con receptor (`JournalEntryBuilder.() -> Unit`) |
| Builders anidados que dejan llamar al de afuera | `@DslMarker annotation class LedgerDsl` `[≤ 1.4]` | Dentro de un bloque marcado no se ve el receptor implícito del bloque externo |
| `StringBuilder` + `append` | `buildString { }`, `buildList { }` `[1.6]` | El builder no sale del bloque; afuera queda un valor de solo lectura |
| Plantillas de texto con `$` escapado | `$$"""…$${x}…"""` `[2.2]` | En plantillas de HTML o JSON, el `$` suelto es literal |
| Pasar el contexto (logger, transacción) a mano | `context(tx: Transaction) fun post(…)` `[2.4]` | *Context parameters*; los *context receivers* sin nombre se retiraron `[rm 2.4]` |
| Gradle en Groovy | `build.gradle.kts` | El DSL más usado de Kotlin; se ve a fondo en `08-build` |

**Trampas:**
- Sin `@DslMarker`, dentro de `tr { }` se puede llamar al `table()` del bloque de afuera sin darse cuenta: compila y
  arma un HTML equivocado.
- Si el builder se escapa del bloque (se guarda en una variable de afuera), se puede modificar después; el DSL debe
  devolver un valor inmutable al terminar.
- `operator fun String.unaryPlus()` para agregar texto es un truco conocido pero oscuro para quien lee: mejor una
  función con nombre (`text("…")`).
- El código experimental con `context(Log)` (sin nombre) de 1.6 a 2.3 ya no compila en 2.4: hay que pasarlo a
  `context(log: Log)`.
- Un DSL para algo que se usa una vez cuesta más de leer que una llamada con argumentos con nombre.

**Caso:** `journalEntry { }` del Banco que construye un `JournalEntry` y rechaza al terminar un asiento desbalanceado;
el reporte HTML del portafolio del Mercado (posiciones, P&L) como ⚡. **Ruta 🎯:** no entra.

**🎤 Entrevista:** ¿qué es una lambda con receptor? · ¿Para qué sirve `@DslMarker`? · ¿Context parameters o
receptores de extensión: cuándo cada uno?

**⚡ DSL de HTML type-safe:** `html { body { table { tr { td("ACME") } } } }` que genera el reporte del portafolio,
con escape de texto y atributos tipados · **Se verifica con:** la salida idéntica a un HTML dorado, una prueba de que
`<script>` en el nombre de un ticker sale escapado, y un comentario con el código que **no** compila gracias a
`@DslMarker` (llamar a `table` dentro de `td`).

### 01 · `kotlin2` — Lo de 2.0 a 2.4, con su etiqueta

**Objetivo:** saber qué trajo cada versión de 2.0 a 2.4 (lenguaje, stdlib y compilador), qué cambió de
comportamiento al pasar de K1 a K2 y cómo se nota en código que se migra.
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| `case Integer i when i > 0 ->` | `is Int if x > 0 ->` `[2.2]` | Guardas en `when` |
| Text block con `$` literal | `$$"…"` `[2.2]` | Plantillas con varios `$` |
| `java.time.Instant`, `UUID`, `Base64`, `HexFormat` | `kotlin.time.Instant`/`Clock` `[2.3]`, `kotlin.uuid.Uuid` `[2.4]`, `kotlin.io.encoding.Base64`, `toHexString()` `[2.2]` | En la stdlib, útiles en multiplataforma |
| `ScopedValue` (vista previa en 21) o pasar el contexto | *Context parameters* `[2.4]` | Reemplazan a los *context receivers* `[rm 2.4]` |
| Campo privado + getter de otro tipo | *Explicit backing fields* `[2.4]` | `val x: List<T> field = mutableListOf()` |
| — | Smart cast a supertipo común con `x is B \|\| x is C` `[2.0]`; `break`/`continue` en lambdas inline `[2.2]` | K2 infiere más: aparecen avisos nuevos (`!!` innecesario) |
| — | `enumEntries<T>()` `[2.0]`, `@SubclassOptInRequired` `[2.1]`, alias de tipo anidados `[2.3]` | Ver la [referencia medida](Temario-Plan.md#referencia-de-versiones-medida) |
| Lambdas con `invokedynamic` | Lambdas con `invokedynamic` desde 2.0 | `toString()` distinto; `@JvmSerializableLambda` `[1.8]` o `-Xlambdas=class` |

**Trampas:**
- K2 es otro compilador, no otra versión del mismo: cambia la inferencia y los smart casts, y código que compilaba
  puede dar avisos o errores nuevos (los de [`k2-notes.md`](../22-legacy-kotlin19/docs/k2-notes.md)).
- El `toString()` de una lambda cambió en 2.0 (`Function1<…>` → `…$$Lambda/0x…`): cualquier log o prueba que lo
  compare se rompe.
- Un `kotlinc` 2.4 rechaza `-language-version 1.9` (tutorial `cli`): para compilar como 1.9 hace falta otro
  compilador, no una opción.
- Desde 2.1, los `@Nullable` de JSpecify son errores; desde 2.2, los métodos con cuerpo en interfaces se compilan como
  `default` de Java: dos cambios que se notan sin tocar el código propio.
- Los *context receivers* experimentales ya no compilan en 2.4 `[rm 2.4]`.

**Caso:** cada construcción con un ejemplo de los casos: guardas sobre `MarketEvent`, `Uuid` como clave de
idempotencia de una transferencia, `kotlin.time.Instant` en los ticks, *context parameters* para la transacción del
ledger, *explicit backing field* en el `OrderBook`. **Ruta 🎯:** no entra.

**🎤 Entrevista:** ¿qué cambió con K2 y cómo se nota al migrar? · ¿Qué son los *context parameters* y qué
reemplazaron? · ¿Por qué cambió el `toString()` de las lambdas?

**⚡ Catálogo ejecutable de 2.x:** una prueba por construcción de 2.0 a 2.4, con su etiqueta en el nombre, y los
mismos ejemplos como pruebas del script de versiones · **Se verifica con:** `./gradlew test` en verde con 2.4.21 y
`scripts/version-probes.sh` (con los ejemplos agregados) reproduciendo exactamente las etiquetas de la referencia,
incluido que cada ejemplo **no** compila con la versión anterior a su etiqueta.

---

## 02-coroutines

Corrutinas, `Flow` y concurrencia. **Build:** 1 · **Caso:** Mercado (y Banco en `sync`).

### 02 · `fundamentals` — `suspend`, structured concurrency, `Job`

**Objetivo:** escribir código concurrente que se lee como secuencial, con corrutinas que viven dentro de un scope y
nunca quedan huérfanas, y saber qué es (y qué no es) una función `suspend`.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `CompletableFuture.thenCompose(…).thenApply(…)` | Llamadas `suspend` una tras otra | El código asíncrono se escribe en orden, sin callbacks |
| `executor.submit(…)` + `future.get()` | `async { }` + `await()` | `await` suspende la corrutina, no bloquea el hilo |
| `StructuredTaskScope` (vista previa en 21) | `coroutineScope { }` | Estable: el bloque no termina hasta que terminan sus hijos |
| `CountDownLatch`, `invokeAll` | `awaitAll(…)`, `joinAll(…)` | Si un hijo falla, cancela a sus hermanos |
| `future.cancel(true)` | `job.cancel()` | Cancela también a los hijos del `Job` |
| Un hilo nuevo por tarea | `launch { }` dentro de un scope | Miles de corrutinas cuestan lo que unos pocos hilos |
| `main` que espera al pool | `runBlocking { }` | Solo en `main` y en pruebas, nunca dentro de una corrutina |

**Trampas:**
- `GlobalScope.launch` crea corrutinas sin dueño (nadie las cancela ni espera): en 1.11.0 pide
  `@DelicateCoroutinesApi` (medido).
- `suspend` no quiere decir "en otro hilo": la función corre en el hilo de quien la llama hasta su primer punto de
  suspensión, y si adentro hay JDBC o `Thread.sleep`, bloquea igual.
- Una excepción dentro de `async` cancela al scope padre aunque nadie haya llamado a `await`.
- `runBlocking` dentro de una corrutina bloquea el hilo; sobre un dispatcher de un solo hilo, se cuelga.
- `launch` devuelve un `Job` sin resultado: para un valor hace falta `async`.

**Caso:** el Mercado consulta los precios de los cinco tickers en paralelo con `async` y arma el valor del
portafolio; el Banco pide en paralelo el saldo y la tasa de cambio antes de una transferencia multimoneda.

**🎤 Entrevista:** ¿qué es structured concurrency y qué problema resuelve? · `launch` frente a `async` · ¿Una
corrutina es un hilo?

**⚡ Fan-out de cotizaciones:** cinco fuentes de precios simuladas con latencias fijas, consultadas en paralelo dentro
de un `coroutineScope` · **Se verifica con:** una prueba en tiempo virtual (`runTest`) donde el tiempo total es el de
la fuente más lenta y no la suma, y otra donde una fuente falla y se comprueba que las demás quedaron canceladas (no
terminaron).

### 02 · `cancellation` — Cooperativa, `withTimeout`, excepciones, `SupervisorJob`

**Objetivo:** cancelar sin dejar trabajo a medias, poner tiempos límite, y decidir cuándo una falla debe tumbar a las
corrutinas hermanas y cuándo no.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `Thread.interrupt()` + `isInterrupted()` | Cancelación cooperativa: `ensureActive()`, `isActive`, `yield()` | Cada `suspend` de la librería ya comprueba la cancelación |
| `InterruptedException` | `CancellationException` | Es el mecanismo normal de cancelar: no se trata como error |
| `future.get(2, SECONDS)` | `withTimeout(2.seconds)` / `withTimeoutOrNull` | El segundo devuelve `null` en vez de lanzar |
| `finally` que libera recursos | `finally { withContext(NonCancellable) { … } }` | Sin `NonCancellable`, un `suspend` dentro de `finally` de una corrutina cancelada no corre |
| Un pool donde una tarea falla sola | `supervisorScope { }`, `SupervisorJob()` | La falla de un hijo no cancela a los demás |
| `UncaughtExceptionHandler` | `CoroutineExceptionHandler` | Solo sirve en la corrutina raíz (`launch` de nivel superior) |

**Trampas:**
- Un bucle de cálculo sin puntos de suspensión no se entera de que lo cancelaron.
- `catch (e: Exception)` y `runCatching` atrapan la `CancellationException`: si no se relanza, la corrutina sigue
  viva después de cancelarse (ver `runCatchingCancellable` en `exceptions`).
- `withTimeout` lanza `TimeoutCancellationException`, que es una cancelación: si nadie la atrapa, la corrutina termina
  "en silencio" en vez de fallar.
- `launch(SupervisorJob()) { }` saca a la corrutina de la jerarquía: ya no es hija del scope y nadie la espera.
- `CoroutineExceptionHandler` en un `async` o en un hijo no hace nada.

**Caso:** el Banco pide la tasa al `FxRateClient` con tiempo límite y, si la transferencia se cancela a mitad, revierte
el débito en un `finally` con `NonCancellable`; el Mercado sigue a cada ticker en un hijo de `supervisorScope`, así la
caída de uno no corta a los otros.

**🎤 Entrevista:** ¿por qué la cancelación es cooperativa? · ¿Qué pasa si atrapas `CancellationException`? ·
`coroutineScope` frente a `supervisorScope`.

**⚡ Transferencia con tiempo límite y compensación:** débito, consulta de tasa con `withTimeout` y crédito, con
reversión si algo falla o se cancela · **Se verifica con:** pruebas en tiempo virtual: el tiempo límite salta en el
instante exacto, la compensación corre una sola vez aunque la cancelación llegue durante la compensación, y la
cancelación del padre se propaga (no se traga).

### 02 · `dispatchers` — `withContext`, `limitedParallelism`, bloquear hilos

**Objetivo:** elegir en qué hilos corre cada cosa (CPU, E/S bloqueante, UI), limitar el paralelismo sin crear pools a
mano y no agotar los hilos con llamadas bloqueantes.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `ForkJoinPool.commonPool()` para cálculo | `Dispatchers.Default` | Tantos hilos como núcleos |
| Un pool grande para E/S bloqueante | `Dispatchers.IO` | 64 hilos (o más si hay más núcleos), compartidos con `Default` |
| `Executors.newFixedThreadPool(4)` dedicado | `Dispatchers.IO.limitedParallelism(4, "db")` | Medido: el nombre compila sin opt-in en 1.11.0 |
| `executor.submit { }` y esperar | `withContext(Dispatchers.IO) { }` | Cambia de hilo y vuelve con el resultado |
| `Executors.newVirtualThreadPerTaskExecutor()` | `.asCoroutineDispatcher()` | Compila sin opt-in; se compara en `loom` |
| `Executors.newSingleThreadExecutor()` | `limitedParallelism(1)`; `newSingleThreadContext` | El segundo pide `@ExperimentalCoroutinesApi` (medido) y hay que cerrarlo |
| `ThreadLocal` (MDC del logger) | `ThreadLocal.asContextElement()`, `MDCContext` | El valor viaja con la corrutina, no con el hilo |

**Trampas:**
- JDBC o `Thread.sleep` en `Default` ocupa un núcleo entero mientras espera: con unas pocas llamadas, todo lo demás se
  frena.
- `limitedParallelism` sobre `IO` es una vista elástica (puede sumar hilos); sobre `Default` no agrega hilos, solo
  limita.
- Un `ThreadLocal` se pierde al cambiar de hilo: el MDC del log aparece vacío después de un `withContext`.
- `Dispatchers.Unconfined` sigue en el hilo que reanudó la corrutina: el orden de ejecución deja de ser predecible.
- Un `newSingleThreadContext` que no se cierra deja un hilo vivo que impide terminar la JVM.

**Caso:** el `LedgerRepository` JDBC de `04-data` llamado con `withContext(db)`, donde
`db = Dispatchers.IO.limitedParallelism(poolSize)` coincide con el pool de conexiones; las velas del Mercado se
calculan en `Default`.

**🎤 Entrevista:** ¿`Default` o `IO`, y por qué? · ¿Qué pasa si llamas a JDBC desde `Default`? · ¿Cómo limitas a N las
llamadas simultáneas a la base?

**⚡ Dimensionar dispatchers:** 1 000 llamadas bloqueantes simuladas (10 ms cada una) sobre `Default`, `IO`,
`limitedParallelism(10)` y un dispatcher de hilos virtuales · **Se verifica con:** una prueba con un contador de
llamadas simultáneas que demuestra que `limitedParallelism(10)` nunca pasa de 10, y la tabla de tiempos de cada
variante guardada junto a la prueba (con calentamiento).

### 02 · `channels` — `Channel`, `select`, productores

**Objetivo:** comunicar corrutinas con canales (el `BlockingQueue` que suspende en vez de bloquear), esperar varias
fuentes a la vez con `select` y escribir un actor de un solo escritor.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `BlockingQueue.put` / `take` | `channel.send` / `receive` | Suspenden en vez de bloquear |
| `ArrayBlockingQueue(n)`, `SynchronousQueue` | `Channel(n)`, `Channel(Channel.RENDEZVOUS)` | Por defecto es *rendezvous*: `send` espera a que alguien reciba |
| Hilo productor que llena una cola | `produce { send(…) }` | Pide `@ExperimentalCoroutinesApi` (medido) |
| Esperar la primera de varias colas | `select { a.onReceive { }; b.onReceive { } }` | Estable (compila sin opt-in) |
| Un hilo dueño del estado + cola de comandos | `Channel<Command>` + un `launch` que lo consume | `actor { }` es `@ObsoleteCoroutinesApi` (medido): se escribe a mano |
| `ScheduledExecutorService` | Un `flow { while (true) { emit(…); delay(…) } }` | `ticker` también es `@ObsoleteCoroutinesApi` (medido) |
| *Poison pill* para terminar | `channel.close()` + `for (x in channel)` | El `for` termina cuando el canal se cierra y se vacía |

**Trampas:**
- Un `send` a un canal *rendezvous* sin nadie que reciba suspende para siempre.
- Si el productor no cierra el canal, el `for` del consumidor no termina nunca.
- `Channel.UNLIMITED` esconde la falta de backpressure hasta que se acaba la memoria.
- `trySend` sobre un canal lleno no lanza: devuelve un resultado fallido que hay que mirar.
- Varios consumidores de un canal se **reparten** los elementos; para que todos reciban todo hace falta un
  `SharedFlow`.

**Caso:** el `OrderBook` del Mercado como actor: un `Channel` de comandos (`Place`, `Cancel`) y una sola corrutina que
modifica el libro, sin locks; las ejecuciones salen por otro canal hacia la liquidación del Banco.

**🎤 Entrevista:** `Channel` frente a `BlockingQueue` · ¿Qué es un actor y por qué evita los locks? · ¿Qué hace
`select`?

**⚡ Motor de emparejamiento como actor:** el libro de órdenes de `collections` detrás de un canal de comandos, con
precio-tiempo y ejecuciones parciales · **Se verifica con:** la misma secuencia de órdenes que el ⚡ de `control` da
las mismas ejecuciones, y una prueba con 100 corrutinas que envían órdenes a la vez donde el libro nunca queda cruzado
y la cantidad total se conserva.

### 02 · `flow` — Operadores, `flowOn`, buffer, conflate

**Objetivo:** procesar flujos de datos asíncronos con `Flow` (frío, con backpressure por suspensión), cambiar de
contexto bien y elegir entre `buffer`, `conflate` y los operadores de tiempo.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `Stream` (síncrono, de un solo uso) | `Flow` (asíncrono, frío: se puede recolectar muchas veces) | Cada `collect` vuelve a correr el productor |
| `Flow.Publisher` / `Flux` | `Flow`; los puentes están en `03-reactive` | Sin `request(n)`: el productor se frena porque `emit` suspende |
| `subscribeOn` / `publishOn` | `flowOn(Dispatchers.IO)` | Cambia el contexto **aguas arriba** del operador |
| Callbacks de un cliente | `callbackFlow { trySend(…); awaitClose { } }` | Compila sin opt-in |
| Cola entre productor y consumidor | `buffer(64)`, `conflate()` | `conflate` se queda solo con el último valor |
| `debounce`, `sample` de Reactor | `debounce(…)`, `sample(…)` | Piden `@FlowPreview` (medido) |
| `flatMap` de Reactor, `buffer(n)` | `flatMapMerge`, `flatMapLatest`, `chunked(n)` | Piden `@ExperimentalCoroutinesApi` (medido) |

**Trampas:**
- `withContext` dentro de `flow { }` rompe la preservación del contexto: lanza `IllegalStateException` ("Flow
  invariant is violated", medido); se usa `flowOn` o `channelFlow`.
- Dos `collect` del mismo `Flow` frío abren dos veces la fuente (dos conexiones, dos lecturas del archivo).
- `catch` solo atrapa lo que pasa aguas arriba; una excepción dentro de `collect` sigue de largo.
- `conflate` pierde valores intermedios: sirve para el último precio, no para ejecuciones que hay que liquidar todas.
- Los operadores con opt-in pueden cambiar de firma entre versiones de la librería.

**Caso:** el feed de `ticks.json` del Mercado como `Flow` con ráfagas: limpieza (duplicados, desorden), velas de un
minuto, alertas de precio y un consumidor lento que obliga a elegir entre `buffer` y `conflate`.

**🎤 Entrevista:** `Flow` frío frente a caliente · ¿Cómo funciona la backpressure en `Flow`? · ¿Qué hace `flowOn` y por
qué no se usa `withContext` adentro?

**⚡ Operadores propios con backpressure:** `windowedByTime`, `throttleLatest` y `batch(size, timeout)` escritos con
`flow { }` y `channelFlow { }` · **Se verifica con:** pruebas en tiempo virtual con las emisiones exactas en los
instantes exactos, una prueba con un consumidor lento que demuestra que el productor se frena (buffer acotado), y la
comparación con `sample` de la librería donde hacen lo mismo.

### 02 · `hotflows` — `StateFlow`, `SharedFlow`

**Objetivo:** publicar estado y eventos a varios suscriptores con flujos calientes, convertir uno frío en caliente sin
fugas y saber qué recibe un suscriptor que llega tarde.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `AtomicReference` + listeners, `BehaviorSubject` | `MutableStateFlow(valor)` | Siempre tiene valor; el que llega tarde recibe el actual |
| Bus de eventos, `PublishSubject` | `MutableSharedFlow(replay, extraBufferCapacity)` | Sin valor actual; `replay` decide cuántos ve el que llega tarde |
| Bucle con `compareAndSet` | `state.update { it + 1 }` | Compila sin opt-in |
| `ConnectableFlux.autoConnect()` | `stateIn` / `shareIn` con `SharingStarted.WhileSubscribed(5000)` | La fuente se detiene 5 s después del último suscriptor |
| `LiveData` (Android) | `StateFlow` | Se ve en `12-android` |

**Trampas:**
- `StateFlow` compara con `equals`: volver a asignar un valor igual, o mutar un objeto y reasignarlo, no avisa a
  nadie.
- Un `MutableSharedFlow()` con `replay = 0` y sin suscriptores pierde lo que se emite.
- `stateIn(scope, SharingStarted.Eagerly, …)` en un scope de larga vida mantiene la fuente activa para siempre.
- Un suscriptor lento de un `SharedFlow` con buffer lleno hace suspender a quien emite (salvo `onBufferOverflow`).
- `collect` sobre un `StateFlow` nunca termina: en una prueba hace falta `first()`, `take(n)` o Turbine.

**Caso:** el último precio de cada ticker como `StateFlow` y las alertas como `SharedFlow` (Mercado); el saldo de una
cuenta como `StateFlow` que se actualiza con cada asiento (Banco).

**🎤 Entrevista:** `StateFlow` frente a `SharedFlow` · ¿Qué recibe un suscriptor que llega tarde a cada uno? · ¿Para
qué sirve `WhileSubscribed(5000)`?

**⚡ Tablero de precios en vivo:** `StateFlow` por ticker alimentado por el feed y un `SharedFlow` de alertas · **Se
verifica con:** pruebas con Turbine en tiempo virtual: el suscriptor tardío recibe el último precio en el acto, las
alertas no se repiten para quien llega tarde, y la fuente se detiene exactamente 5 s después del último suscriptor.

### 02 · `sync` — `Mutex`, `Semaphore`, atomicfu, `@Volatile` y el JMM

**Objetivo:** proteger estado compartido entre corrutinas sin bloquear hilos, y saber cuándo un lock sobra porque se
puede confinar el estado.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| `synchronized`, `ReentrantLock` | `mutex.withLock { }` | Suspende en vez de bloquear; **no** es reentrante |
| `java.util.concurrent.Semaphore` | `semaphore.withPermit { }` | Limita cuántas corrutinas entran a la vez |
| `AtomicLong`, `AtomicReference` | Los mismos de `java.util.concurrent.atomic`; atomicfu ⚠️; `kotlin.concurrent.atomics` `[exp 2.4]` | atomicfu y la stdlib importan en multiplataforma |
| `volatile` | `@Volatile` | Visibilidad, no atomicidad; mismo modelo de memoria (JMM) |
| `ConcurrentHashMap` | El mismo | `getOrPut` no pisa un valor existente pero puede calcularlo de más; `computeIfAbsent` lo calcula una vez |
| Un hilo dueño del estado | Confinamiento: un actor (`channels`) o `limitedParallelism(1)` | Muchas veces más simple que un lock |

**Trampas:**
- Llamar a una función `suspend` dentro de `synchronized` no compila ("suspension point is inside a critical section",
  medido): se usa `Mutex`.
- `Mutex` no es reentrante: un `withLock` anidado sobre el mismo `Mutex` en la misma corrutina se queda esperando para
  siempre.
- `count++` sobre un `@Volatile` no es atómico: dos corrutinas en hilos distintos pierden incrementos.
- Bloquear dos cuentas en orden distinto (A→B y B→A a la vez) es un deadlock: hay que bloquear siempre en el mismo
  orden (por id).
- `getOrPut` de Kotlin sobre un `ConcurrentHashMap` puede ejecutar la lambda dos veces (se queda con un solo
  resultado): si la lambda abre una conexión o cobra una comisión, ese efecto se repite.

**Caso:** transferencias concurrentes del Banco con un `Mutex` por cuenta, tomadas en orden de `AccountId` para evitar
el deadlock; un `Semaphore` que limita las llamadas simultáneas al `FxRateClient`.

**🎤 Entrevista:** `Mutex` frente a `synchronized` · ¿Por qué `@Volatile` no basta para un contador? · ¿Cómo evitas un
deadlock al transferir entre dos cuentas?

**⚡ Transferencias concurrentes sin perder saldo:** 10 000 transferencias aleatorias desde 100 corrutinas en
`Dispatchers.Default` sobre 50 cuentas · **Se verifica con:** una prueba de que el total de dinero se conserva y ningún
saldo queda negativo, otra con transferencias cruzadas A→B y B→A que termina dentro de un `withTimeout` (sin deadlock),
y una variante con el mismo estado confinado a un actor que da el mismo resultado.

### 02 · `internals` — CPS, máquina de estados decompilada, `Continuation` a mano

**Objetivo:** entender qué genera el compilador para una función `suspend`, adaptar APIs de callbacks a `suspend` y
saber qué cuesta cada punto de suspensión.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| Callback `onSuccess`/`onError` | `suspendCancellableCoroutine { cont -> … cont.resume(x) }` | Adapta un callback a `suspend` |
| `CompletableFuture<T>` como retorno | `suspend fun f(): T` → `f(Continuation<T>): Any?` | *Continuation-passing style*: el compilador agrega el parámetro |
| — (Java no tiene corrutinas en el lenguaje) | Máquina de estados con `label` y `COROUTINE_SUSPENDED` | Un estado por cada punto de suspensión |
| — | `createCoroutine` / `startCoroutine` (stdlib, sin kotlinx) | La base sobre la que kotlinx construye todo |
| Generadores (no hay) | `sequence { yield(x) }` | Suspensión restringida: corre sin hilos ni dispatcher |
| `ThreadLocal` | Elementos del `CoroutineContext` | El contexto viaja con la `Continuation` |

**Trampas:**
- Reanudar dos veces la misma `Continuation` lanza `IllegalStateException`.
- `suspendCoroutine` no se entera de la cancelación: con una API que se puede cancelar se usa
  `suspendCancellableCoroutine` e `invokeOnCancellation`.
- Las variables locales que siguen vivas después de un punto de suspensión pasan a ser campos del objeto de la
  máquina de estados (más memoria por corrutina suspendida).
- Las pilas de excepciones salen cortadas en los puntos de suspensión; `kotlinx-coroutines-debug` (tutorial
  `debug`) las reconstruye.
- Desde Java, una función `suspend` pide un `Continuation` como último parámetro: no se puede llamar de forma natural.

**Caso:** un cliente de cotizaciones del Mercado que entrega precios por callback, adaptado a `suspend fun quote(…)`
con cancelación.

**🎤 Entrevista:** ¿cómo compila Kotlin una función `suspend`? · ¿Qué es una `Continuation`? · ¿Por qué las
corrutinas son "baratas"?

**⚡ Scheduler de corrutinas propio sobre `createCoroutine`:** un planificador de un solo hilo, con su propio `yield`
y `delay`, sin kotlinx · **Se verifica con:** una prueba del intercalado exacto de N corrutinas (salida esperada fija),
y la máquina de estados decompilada (tutorial `decompile`) guardada en la lección, con tantos estados como puntos de
suspensión más uno.

### 02 · `testing` — `runTest`, tiempo virtual, Turbine

**Objetivo:** probar código con corrutinas, tiempos y flujos de forma determinista y en milisegundos, sin `sleep` ni
pruebas que fallan de vez en cuando.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines-test 1.11.0 ⚠️, Turbine ⚠️) | Nota |
|---|---|---|
| `Thread.sleep` en la prueba, Awaitility | `runTest { }` con tiempo virtual | `delay` avanza el reloj virtual al instante |
| Reloj falso hecho a mano | `advanceTimeBy`, `advanceUntilIdle`, `currentTime` | El planificador de la prueba controla el tiempo |
| Inyectar un `Executor` | Inyectar un `CoroutineDispatcher` (`StandardTestDispatcher`) | Sin inyección, el código escapa del tiempo virtual |
| `TestObserver` de RxJava | Turbine: `flow.test { awaitItem() }` | Comprueba emisiones una por una |
| Hilo de fondo que la prueba ignora | `backgroundScope.launch { }` | Se cancela solo al terminar la prueba |
| UI thread en pruebas de Android | `Dispatchers.setMain(StandardTestDispatcher())` | Se ve en `12-android` |

**Trampas:**
- Un `withContext(Dispatchers.IO)` fijo dentro del código probado sale del tiempo virtual: sus `delay` duran de verdad.
- `StandardTestDispatcher` no corre nada hasta que la prueba avanza el tiempo o cede (`advanceUntilIdle`, `yield`);
  `UnconfinedTestDispatcher` corre de inmediato: cambiar uno por otro cambia el orden.
- Una corrutina lanzada en un scope propio que nunca termina cuelga o hace fallar la prueba; para eso está
  `backgroundScope`.
- Un `StateFlow` es *conflated*: Turbine puede no ver los valores intermedios si se emiten sin suspender.
- `runBlocking` en una prueba usa tiempo real.

**Caso:** las velas de un minuto del feed del Mercado y el tiempo límite de una transferencia del Banco, probados en
tiempo virtual.

**🎤 Entrevista:** ¿cómo se prueba un `delay(5.minutes)` sin esperar? · `StandardTestDispatcher` frente a
`UnconfinedTestDispatcher` · ¿Cómo pruebas un `Flow`?

**⚡ Una sesión de mercado en un segundo:** la sesión completa (6.5 h de ticks simulados) procesada en tiempo virtual
· **Se verifica con:** `measureTime` que muestra menos de 1 s de reloj real, el mismo resultado en 100 corridas
seguidas, y pruebas con Turbine del orden de las velas y las alertas.

### 02 · `loom` — Corrutinas frente a hilos virtuales

**Objetivo:** comparar corrutinas e hilos virtuales de Java 21 (qué resuelve cada uno, qué cuesta y cuándo conviene
mezclarlos) con mediciones propias y no con opiniones.
**Sección:** `02-coroutines` · **Build:** nivel 1 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (kotlinx-coroutines 1.11.0 ⚠️) | Nota |
|---|---|---|
| Hilos virtuales (JEP 444, finales en 21) | Corrutinas | Los dos hacen barata la concurrencia; Loom sin cambiar el código bloqueante |
| `Executors.newVirtualThreadPerTaskExecutor()` | `.asCoroutineDispatcher()` | Corrutinas sobre hilos virtuales: compila sin opt-in |
| `StructuredTaskScope` (vista previa en 21) | `coroutineScope { }` | Estable en Kotlin desde hace años |
| `ScopedValue` (vista previa en 21) | `CoroutineContext` | Contexto que viaja con la tarea |
| JDBC bloqueante en un hilo virtual | `withContext(Dispatchers.IO)` | Con Loom bloquear es barato; con corrutinas hay que mover el bloqueo |
| `synchronized` con bloqueo adentro | `Mutex` | Ver la trampa de *pinning* |

**Trampas:**
- En JDK 21, un hilo virtual que bloquea dentro de `synchronized` queda fijado a su hilo portador (*pinning*) y deja
  de ser barato; JDK 24 lo corrigió (JEP 491) ⚠️, pero el curso usa 21.
- Los hilos virtuales no traen cancelación estructurada, `Flow` ni backpressure: eso sigue siendo de las corrutinas.
- Un `ThreadLocal` pesado en millones de hilos virtuales multiplica la memoria.
- Un benchmark sin calentamiento ni repeticiones (JIT, GC) mide el arranque, no el modelo.
- Mezclar los dos modelos sin necesidad duplica las herramientas de diagnóstico (volcados de hilos, depurador).

**Caso:** un servidor de difusión de precios del Mercado (cada cliente se suscribe a tickers y recibe actualizaciones),
el "chat" del ⚡ con los datos del caso.

**🎤 Entrevista:** ¿los hilos virtuales hacen innecesarias las corrutinas? · ¿Qué es el *pinning*? · ¿Cuándo usarías
cada uno en un backend Spring?

**⚡ Servidor de difusión con tres modelos:** el mismo servidor de suscripciones (tipo chat) con hilos de plataforma,
hilos virtuales y corrutinas, bajo 10 000 clientes simulados · **Se verifica con:** una tabla de throughput, latencia
p99 y memoria de cada modelo guardada en la lección (con calentamiento y repeticiones), y una prueba que reproduce el
*pinning* en JDK 21 con un `synchronized` y lo resuelve con `ReentrantLock` o `Mutex`.

---

## 03-reactive

Reactor, RxJava y puentes con corrutinas. **Build:** 2 · **Caso:** Mercado.

### 03 · `reactor` — `Mono`/`Flux` desde Kotlin, extensiones

**Objetivo:** leer y escribir código Reactor desde Kotlin (el de WebFlux y de muchas librerías Java), con las
extensiones de `reactor-kotlin-extensions` y sin pelear con los tipos nullables.
**Sección:** `03-reactive` · **Build:** nivel 2 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (Reactor ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| `Mono.just(x)`, `Flux.fromIterable(list)` | `x.toMono()`, `list.toFlux()` | Extensiones de `reactor-kotlin-extensions` |
| `flux.map(t -> …).filter(…)` | `flux.map { … }.filter { … }` | Lambdas finales fuera del paréntesis |
| `Mono<Void>` | `Mono<Void>` / `Mono<Unit>` con cuidado | `Void` no tiene valores; `Unit` sí: no se mezclan |
| `Mono.empty()` como "no hay" | `Mono<T>` vacío; `awaitSingleOrNull()` lo convierte en `null` | El vacío no es `null`: Reactor no admite `null` dentro |
| `StepVerifier.create(flux)` | `flux.test().expectNext(…).verifyComplete()` | `reactor-test` con extensión de Kotlin |
| `subscribeOn` / `publishOn` | Los mismos | Equivalen a elegir dispatcher, pero por operador |

**Trampas:**
- Nada pasa hasta `subscribe()`: un `Mono` que se crea y no se suscribe no ejecuta nada (el error clásico al portar
  código imperativo).
- Reactor prohíbe `null` como elemento: `Mono.just(maybeNull)` lanza; desde Kotlin hay que usar `justOrEmpty` o
  `toMono()` sobre un nullable.
- `block()` dentro de un flujo reactivo (o en un hilo de Netty) se queda bloqueado o lanza
  `IllegalStateException`.
- Los tipos de Reactor son Java: un `Flux<Tick>` puede traer platform types en las firmas de librerías viejas.
- Un `flatMap` sin límite de concurrencia lanza todas las suscripciones internas a la vez.

**Caso:** el feed de ticks del Mercado como `Flux<Tick>`: limpieza, velas de un minuto y alertas, el mismo pipeline
que `02 · flow` escrito en Reactor.

**🎤 Entrevista:** ¿qué significa que Reactor es *lazy*? · `map` frente a `flatMap` en Reactor · ¿Por qué Reactor no
admite `null`?

**⚡ Pipeline del feed en Reactor:** limpieza, velas y alertas del feed sobre `Flux` · **Se verifica con:** pruebas
con `StepVerifier` en tiempo virtual (`withVirtualTime`) que dan las mismas velas que la versión `Flow` de
`02 · flow` para el mismo `ticks.json`.

### 03 · `rxjava` — RxJava desde Kotlin

**Objetivo:** mantener código RxJava (muy común en Android y en servicios viejos) desde Kotlin y saber cómo migrarlo
a corrutinas.
**Sección:** `03-reactive` · **Build:** nivel 2 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (RxJava 3 ⚠️) | Nota |
|---|---|---|
| `Observable`, `Flowable`, `Single`, `Maybe`, `Completable` | Los mismos | `Flowable` es el único con backpressure |
| `observable.subscribe(onNext, onError)` | `subscribeBy(onNext = { }, onError = { })` (RxKotlin ⚠️) | Argumentos con nombre: no se confunden los callbacks |
| `CompositeDisposable.add(d)` | `d.addTo(disposables)` | Hay que liberar las suscripciones |
| `Schedulers.io()`, `observeOn` | Los mismos | Similares a `Dispatchers.IO` |
| `TestObserver` | `observable.test().assertValues(…)` | Igual que en Java |
| — | `kotlinx-coroutines-rx3`: `await()`, `asFlow()`, `rxSingle { }` | El camino de migración a corrutinas |

**Trampas:**
- Un `subscribe()` sin `onError` lanza `OnErrorNotImplementedException` al primer error, que termina en el handler
  global (y en Android cierra la app).
- `Observable` no tiene backpressure: un productor rápido llena la memoria; para eso está `Flowable`.
- Una suscripción que nadie libera (`dispose`) sigue viva y retiene lo que capturó (fugas de memoria en Android).
- RxJava tampoco admite `null`: un `map` que devuelve `null` desde Kotlin lanza `NullPointerException`.
- Mezclar `observeOn` y `subscribeOn` sin entender cuál afecta a qué deja el trabajo en el hilo equivocado.

**Caso:** el mismo pipeline del feed del Mercado, ahora en RxJava, y su migración paso a paso a `Flow`.

**🎤 Entrevista:** `Observable` frente a `Flowable` · ¿Qué pasa con un error sin `onError`? · ¿Cómo migrarías RxJava a
corrutinas sin reescribir todo de golpe?

**⚡ Migración incremental:** el pipeline en RxJava migrado a `Flow` por partes, con puentes en las fronteras ·
**Se verifica con:** la misma batería de pruebas en verde en cada paso de la migración (un commit por paso), y una
prueba final sin dependencias de RxJava en el núcleo.

### 03 · `bridges` — `kotlinx-coroutines-reactor`, `awaitSingle`, `asFlow`, `asFlux`

**Objetivo:** cruzar entre corrutinas y Reactor/RxJava sin perder la cancelación, el contexto ni la backpressure, que
es lo que pide un backend WebFlux escrito en Kotlin.
**Sección:** `03-reactive` · **Build:** nivel 2 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (`kotlinx-coroutines-reactor` ⚠️, la versión de coroutines de la sección) | Nota |
|---|---|---|
| `mono.block()` | `mono.awaitSingle()`, `awaitSingleOrNull()` | Suspende en vez de bloquear |
| `flux.toIterable()` | `flux.asFlow()` | La backpressure de Reactor se mapea a la suspensión |
| Crear un `Flux` desde un productor | `flow.asFlux()`, `flux { send(…) }` | De `Flow` a Reactor |
| `Mono.fromCallable(…)` | `mono { suspendingCall() }` | Corre una corrutina como `Mono` |
| Contexto de Reactor (`contextWrite`) | `ReactorContext` en el `CoroutineContext` | El contexto cruza el puente en los dos sentidos |
| Controlador WebFlux que devuelve `Mono<T>` | `suspend fun` en el controlador | Spring hace el puente solo (`09-spring`) |

**Trampas:**
- `awaitSingle()` sobre un `Mono` vacío lanza `NoSuchElementException`: para "puede no haber" se usa
  `awaitSingleOrNull()`.
- Cancelar la corrutina cancela la suscripción, y viceversa; un `GlobalScope` en el puente rompe esa cadena.
- `asFlow()` sobre un `Flux` caliente no lo vuelve frío: varios `collect` comparten la fuente.
- El `ReactorContext` (por ejemplo, el de Spring Security) solo llega si se usa el puente; un `launch` aparte lo pierde.
- Un `runBlocking` en un controlador WebFlux bloquea un hilo de Netty: todo el servidor se frena.

**Caso:** un servicio del Mercado que lee precios de un cliente Reactor y los expone como `Flow` a la lógica escrita
con corrutinas.

**🎤 Entrevista:** ¿cómo se llama a una API Reactor desde una corrutina? · ¿Se conserva la cancelación al cruzar el
puente? · ¿Qué pasa con el contexto de Reactor?

**⚡ Puente con cancelación y contexto:** un adaptador `Flow` ↔ `Flux` para el feed, con un id de correlación en el
contexto · **Se verifica con:** pruebas de que cancelar del lado `Flow` cancela la suscripción de Reactor (y al
revés), de que el id de correlación llega a los dos lados, y de que la backpressure frena al productor.

### 03 · `backpressure` — Backpressure en los tres modelos

**Objetivo:** comparar cómo manejan un productor más rápido que el consumidor Reactor (`request(n)`), RxJava
(`Flowable`) y `Flow` (suspensión), y elegir estrategia (buffer, descarte, último valor) según el dato.
**Sección:** `03-reactive` · **Build:** nivel 2 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| `Flow.Subscription.request(n)` (`java.util.concurrent.Flow`) | `emit` que suspende | En `Flow` no hay `request`: el productor espera al consumidor |
| `onBackpressureBuffer(n)` | `buffer(n)` | Cola acotada entre los dos |
| `onBackpressureDrop()` | `buffer(n, BufferOverflow.DROP_LATEST)` | Descarta lo nuevo |
| `onBackpressureLatest()` | `conflate()` | Se queda con el último |
| `Flowable` frente a `Observable` | Todo `Flow` tiene backpressure | En RxJava hay que elegir el tipo correcto |
| `limitRate(n)` | `buffer(n)` + `flowOn` | Prefetch entre hilos |

**Trampas:**
- Un buffer sin límite no es backpressure: solo cambia cuándo se acaba la memoria.
- Descartar o quedarse con el último sirve para precios, nunca para ejecuciones u órdenes, que hay que procesar todas.
- `flowOn` agrega un buffer implícito (de 64 por defecto) entre los dos contextos.
- Un consumidor lento en un `SharedFlow` puede frenar a todos los demás suscriptores.
- Medir sin carga sostenida (unos segundos) no muestra el problema: la memoria crece de a poco.

**Caso:** una ráfaga del feed del Mercado (diez veces el ritmo normal) contra un consumidor lento: precios con
`conflate`, ejecuciones con `buffer` acotado.

**🎤 Entrevista:** ¿qué es la backpressure y cómo la resuelve cada modelo? · ¿Cuándo descartas datos y cuándo no? ·
¿Por qué `Observable` no la tiene?

**⚡ El mismo pipeline en tres modelos:** el pipeline del feed en Reactor, RxJava y `Flow`, bajo la misma carga ·
**Se verifica con:** una tabla de throughput y memoria máxima de cada uno (con calentamiento, carga sostenida de un
minuto) guardada en la lección, y una prueba por estrategia que muestra qué elementos llegan (todos, los últimos, los
primeros).

---

## 04-data

Bases de datos relacionales. **Build:** 2 · **Caso:** Banco.

### 04 · `jdbc` — JDBC idiomático, tres motores (SQLite, PostgreSQL, MySQL)

**Objetivo:** escribir JDBC sin ceremonia con extensiones de Kotlin, aislar las diferencias entre motores en un
dialecto y probar todo con una sola batería de contrato.
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** existe (en el Banco desde B0′, tanda 5)

> **B0′, tanda 5 (hecho):** el repositorio de productos pasó a `LedgerRepository` (cuentas, asientos y postings, la
> transferencia en una transacción con UPDATE relativos) y su contrato `LedgerRepositoryContract` (13 pruebas por
> motor, incluidas el aislamiento por defecto y la actualización perdida con dos conexiones); `FOR UPDATE` queda para
> el ⚡. Guía: [`01-jdbc.md`](lessons/04-data/01-jdbc.md). Versiones: sqlite-jdbc 3.54.1.0,
> PostgreSQL JDBC 42.7.14, mysql-connector-j 26.10.0; PostgreSQL 17.2 y MySQL 8.4.3 en Docker
> ([`Versions.md`](Versions.md)).

| Java 21 | Kotlin | Nota |
|---|---|---|
| `try (PreparedStatement ps = …)` | `conn.prepareStatement(sql).use { ps -> }` | `use` cierra igual que try-with-resources |
| `while (rs.next()) list.add(map(rs))` | `inline fun <T> ResultSet.mapRows(mapper)` | Extensión propia de la lección |
| `setAutoCommit(false)` / `commit` / `rollback` | `inline fun <T> Connection.inTransaction(block: Connection.() -> T)` | Lambda con receptor: adentro, `this` es la conexión |
| Interfaz Strategy por motor | `interface Dialect` + `object PostgresDialect : Dialect` | `object` como estrategia sin estado |
| Clase de configuración + builder | `data class DbConfig` + `companion { fun fromEnv(…) }` | Variables `PG_URL`, `MYSQL_URL` |
| Suite abstracta de pruebas | `abstract class …Contract` + `assumeTrue` | La misma batería en los tres motores; sin contenedor, `SKIPPED` |
| `rs.getInt` + `wasNull()` | `rs.getObject("x", Long::class.javaObjectType)` → `Long?` | El nulo de SQL como nulo de Kotlin |

**Trampas:**
- `getInt` sobre un `NULL` devuelve `0`: sin `wasNull()` (o `getObject`), un saldo nulo parece cero.
- `COALESCE(SUM(amount), 0)` hace falta porque `SUM` sobre cero filas devuelve `NULL`, no `0`.
- El aislamiento por defecto cambia por motor: `READ COMMITTED` en PostgreSQL, `REPEATABLE READ` en MySQL (InnoDB);
  un "leer saldo, restar, escribir" pierde actualizaciones en los dos sin `SELECT … FOR UPDATE` o una versión.
- El upsert no es estándar: `ON CONFLICT` en PostgreSQL y SQLite, `ON DUPLICATE KEY UPDATE` en MySQL, que además
  consume valores de `AUTO_INCREMENT` aunque actualice.
- SQLite guarda los `NUMERIC` con afinidad flexible: un monto puede volver como `Double` si no se cuida el tipo.

**Caso:** `LedgerRepository` del Banco: cuentas, asientos (`JournalEntry`) y postings; la transferencia como un asiento
en una transacción que valida débitos = créditos; la actualización perdida reproducida y evitada con `FOR UPDATE`; las
diferencias de aislamiento entre motores en la misma prueba de contrato.

**🎤 Entrevista:** ¿qué aislamiento usa por defecto cada motor y qué anomalías permite? · ¿Cómo evitas la
actualización perdida en un saldo? · ¿Por qué un contrato de pruebas para varios motores?

**⚡ Ledger concurrente en tres motores:** el `LedgerRepository` con transferencias concurrentes (hilos o corrutinas
sobre un pool) en SQLite, PostgreSQL y MySQL · **Se verifica con:** el contrato en los tres motores, una prueba que
muestra la actualización perdida sin bloqueo y otra que la evita con `FOR UPDATE` (o con una columna de versión), y
la suma de todos los saldos igual antes y después.

### 04 · `jpa` — Hibernate 7 sin Spring

**Objetivo:** usar JPA desde Kotlin sin caer en sus trampas (clases `final`, `data class`, ids nulos), entender el
contexto de persistencia y medir el N+1.
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** existe (en el Banco desde B0′, tanda 5)

> **B0′, tanda 5 (hecho):** `Customer`, `PurchaseOrder` y `OrderLine` pasaron a `Account` con `@Version` y
> `JournalEntry` con `Posting` (cascade, `orphanRemoval`), con el N+1 al recorrer las líneas de los asientos. Guía: [`02-jpa.md`](lessons/04-data/02-jpa.md).
> Versiones: Hibernate ORM 7.4.15 (JPA 3.2), H2 2.5.252 ([`Versions.md`](Versions.md)).

| Java 21 | Kotlin | Nota |
|---|---|---|
| Constructor sin argumentos para JPA | Plugin `kotlin("plugin.jpa")` (no-arg) | Lo genera el compilador; Kotlin no lo puede llamar |
| Clases y métodos no `final` para los proxies | Plugin `allopen` para `@Entity`, `@MappedSuperclass`, `@Embeddable` | Sin él, `LAZY` no funciona |
| Entidad con `equals`/`hashCode` de Lombok | Clase normal; `equals` por id y `hashCode` constante por clase | **Nunca** `data class` |
| `Long id` nulo antes de persistir | `var id: Long? = null` | |
| `cascade = {CascadeType.ALL}` | `cascade = [CascadeType.ALL]` | Arreglos en anotaciones |
| `Account.class` | `Account::class.java` | |
| `getSingleResult()` y `NoResultException` | `resultList.firstOrNull()` | Sin excepción para "no hay" |
| `@Version long version` | `@Version var version: Long = 0` | Bloqueo optimista |

**Trampas:**
- Una entidad `data class` usa todos los campos en `equals`/`hashCode` (también las colecciones lazy, y el id que
  cambia al persistir): se rompen los `Set` y se disparan cargas inesperadas.
- `toString` que imprime una colección lazy fuera de la transacción lanza `LazyInitializationException` (o carga toda
  la relación dentro de ella).
- Un UPDATE masivo con JPQL no actualiza las entidades que ya están en el contexto de persistencia: hay que limpiarlo.
- Las propiedades no nullables de Kotlin no impiden que Hibernate cargue un `null` desde la base: la columna tiene que
  ser `NOT NULL` de verdad.
- Sin sincronizar los dos lados de una relación (`addPosting` que setea el padre), `mappedBy` ignora el cambio.

**Caso:** `Account` del Banco con `@Version` para el bloqueo optimista en transferencias concurrentes, `JournalEntry`
con sus `Posting` en cascada, y el N+1 al listar los movimientos de un mes medido con las estadísticas de Hibernate.

**🎤 Entrevista:** ¿por qué una entidad no debe ser `data class`? · ¿Qué es el N+1 y cómo lo resuelves? · Bloqueo
optimista frente a pesimista.

**⚡ Ledger en JPA con concurrencia:** el ledger con `@Version`, reintento ante `OptimisticLockException` y consultas
sin N+1 · **Se verifica con:** una prueba de dos transacciones que modifican la misma cuenta (una falla y se
reintenta), el número exacto de sentencias SQL de cada consulta (con `statementCount()`), y la suma de saldos intacta.

### 04 · `exposed` — Exposed (DSL y DAO)

**Objetivo:** acceder a SQL con Exposed (el framework SQL de JetBrains), comparar su DSL type-safe con su capa DAO y
con JDBC/JPA.
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Exposed ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| SQL en cadenas con JDBC | `Accounts.selectAll().where { Accounts.status eq OPEN }` | DSL type-safe: las columnas son objetos |
| `CREATE TABLE` a mano | `object Accounts : LongIdTable() { val balance = decimal(…) }` | El esquema en Kotlin; `SchemaUtils` lo crea |
| Entidades de JPA | DAO: `class Account(id) : LongEntity(id)` | Más liviano que JPA, sin proxies |
| `@Transactional` | `transaction { }` | Bloque con la transacción como receptor |
| `inTransaction` propio (lección `jdbc`) | `newSuspendedTransaction { }` | Versión con corrutinas |

**Trampas:**
- Fuera de un bloque `transaction { }` no hay conexión: una consulta perezosa usada afuera lanza.
- La capa DAO cachea entidades dentro de la transacción: un UPDATE con el DSL no se ve en una entidad ya cargada.
- Exposed cambió bastante su API entre versiones (por ejemplo, `select` frente a `selectAll().where`): los ejemplos de
  internet suelen ser viejos.
- El DSL no protege del N+1: recorrer referencias desde la capa DAO hace una consulta por elemento.
- Un `decimal(precision, scale)` mal dimensionado redondea montos en silencio según el motor.

**Caso:** el ledger del Banco (cuentas, asientos, postings) con el DSL y con la capa DAO, y la misma transferencia
que en `jdbc`.

**🎤 Entrevista:** Exposed frente a JPA · DSL frente a DAO · ¿Cómo maneja Exposed las transacciones?

**⚡ El ledger en Exposed:** el mismo contrato del `LedgerRepository` implementado con el DSL de Exposed · **Se verifica
con:** el contrato de `jdbc` en verde en PostgreSQL y MySQL, y el SQL que genera cada operación guardado y revisado
(sin N+1).

### 04 · `jooq` — jOOQ desde Kotlin

**Objetivo:** escribir SQL complejo (ventanas, CTE, upserts) con el DSL type-safe de jOOQ generado desde el esquema,
desde Kotlin.
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (jOOQ ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| `ctx.select(ACCOUNT.ID).from(ACCOUNT).where(…)` | Lo mismo, con lambdas finales y sin `;` | jOOQ es Java: desde Kotlin se usa igual |
| Clases generadas desde el esquema | Generación con el plugin de Gradle de jOOQ | Las tablas y columnas tipadas salen de la base |
| `Record` a POJO | `fetchInto(AccountRow::class.java)`, `mapping(::AccountRow)` | Mapeo a `data class` por constructor |
| Funciones de ventana en SQL crudo | `avg(TICK.PRICE).over(partitionBy(…).orderBy(…))` | El fuerte de jOOQ |
| `MERGE`/upsert por motor | `insertInto(…).onConflict(…).doUpdate()` | jOOQ traduce al dialecto |

**Trampas:**
- La generación de código necesita una base con el esquema (o los DDL): el build depende de eso.
- Los tipos generados son Java: una columna nullable aparece como platform type salvo que se configure la generación
  para Kotlin.
- La edición gratuita de jOOQ cubre bases de código abierto (PostgreSQL, MySQL, SQLite); otras piden licencia.
- `fetchOne()` lanza si hay más de una fila; `fetchAny()` no: elegir según lo que garantiza la consulta.
- Escribir SQL "a la jOOQ" no lo hace portable: las funciones propias de un motor siguen siendo de ese motor.

**Caso:** velas del Mercado guardadas en tablas y consultadas con funciones de ventana (media móvil, VWAP por día);
el ledger del Banco para comparar con Exposed.

**🎤 Entrevista:** jOOQ frente a JPA: ¿cuándo cada uno? · ¿Qué gana generar código desde el esquema? · ¿Cómo
escribirías una media móvil en SQL?

**⚡ Analítica del Mercado en SQL:** velas, media móvil de 7 días y VWAP diario calculados en la base con jOOQ ·
**Se verifica con:** los mismos resultados que el cálculo en memoria de `functional` sobre `ticks.json` (prueba que
compara uno a uno), en PostgreSQL y MySQL.

### 04 · `r2dbc` — R2DBC con corrutinas

**Objetivo:** acceder a la base sin bloquear hilos con R2DBC y corrutinas, y saber cuándo eso mejora algo y cuándo
solo complica frente a JDBC (o JDBC con hilos virtuales).
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (R2DBC ⚠️, drivers de PostgreSQL y MySQL) | Nota |
|---|---|---|
| `Connection` y `PreparedStatement` de JDBC | `ConnectionFactory`, `Statement` de R2DBC | API reactiva: devuelve `Publisher` |
| `Mono`/`Flux` de Reactor | `awaitSingle()`, `asFlow()` (`03 · bridges`) | Desde corrutinas se escribe en estilo directo |
| Pool de conexiones JDBC (HikariCP) | `r2dbc-pool` | Otro pool, otra configuración |
| `@Transactional` | Transacciones de R2DBC (o de Spring Data R2DBC en `09-spring`) | La transacción viaja en el contexto, no en el hilo |
| JDBC sobre hilos virtuales (Java 21) | La alternativa sin reescribir | Comparar con medición |

**Trampas:**
- R2DBC no tiene JPA: no hay carga perezosa, ni cascadas, ni contexto de persistencia.
- Un `Flux` de filas sin consumir mantiene la conexión ocupada: hay que consumirlo entero o cancelarlo.
- Mezclar R2DBC y JDBC en la misma transacción no se puede: son conexiones distintas.
- Con hilos virtuales (Java 21), JDBC bloqueante ya es barato: R2DBC solo gana con mucha concurrencia y streaming.
- Los drivers R2DBC no siempre soportan lo mismo que su par JDBC (tipos, `RETURNING`, lotes).

**Caso:** el ledger del Banco: la consulta de movimientos como `Flow` y la transferencia en una transacción reactiva.

**🎤 Entrevista:** ¿qué problema resuelve R2DBC? · ¿R2DBC o JDBC con hilos virtuales? · ¿Cómo maneja R2DBC las
transacciones?

**⚡ La misma app en cinco accesos:** el ledger con JDBC, JPA, Exposed, jOOQ y R2DBC detrás del mismo puerto ·
**Se verifica con:** el contrato del `LedgerRepository` en verde en las cinco, el SQL generado por cada una guardado
para comparar, y un benchmark de throughput con carga concurrente (calentamiento y repeticiones) con su tabla.

### 04 · `sqldelight` — SQLDelight

**Objetivo:** escribir el SQL a mano en archivos `.sq` y obtener APIs Kotlin type-safe generadas, que es el acceso a
datos más usado en Kotlin Multiplatform y Android.
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (SQLDelight ⚠️) | Nota |
|---|---|---|
| SQL en cadenas dentro del código | Archivos `.sq` con las consultas con nombre | El SQL es la fuente: se valida al compilar |
| DAOs escritos a mano | Interfaces generadas (`ledgerQueries.selectByAccount(id)`) | Tipos de Kotlin, nullables incluidos |
| Migraciones con Flyway | Archivos `.sqm` de SQLDelight | Verificación de migraciones al compilar |
| Room (Android, con anotaciones) | SQLDelight (SQL primero) | Los dos generan código; Room con KSP |
| Driver JDBC | Drivers por plataforma (JDBC, Android, nativo) | La misma capa en JVM, Android e iOS |

**Trampas:**
- Un error de SQL en un `.sq` rompe la compilación: bueno, pero el mensaje sale del plugin de Gradle, no del IDE de
  Kotlin.
- El dialecto se configura en el build: escribir SQL de PostgreSQL con el dialecto de SQLite no compila.
- Las consultas reactivas (`asFlow()`) vuelven a correr ante cualquier cambio en la tabla, no solo en las filas que
  importan.
- Las migraciones son responsabilidad propia: un `.sqm` faltante deja bases viejas sin la columna nueva.
- En multiplataforma, cada plataforma necesita su driver y su forma de crear la base.

**Caso:** el saldo y los movimientos de una cuenta del Banco y la lista de seguimiento de tickers del Mercado, como la
capa local de la app de `11-multiplatform`.

**🎤 Entrevista:** SQL primero (SQLDelight) frente a anotaciones (Room) · ¿Qué valida SQLDelight al compilar? · ¿Cómo
se migra el esquema?

**⚡ Capa local compartida:** las consultas `.sq` de saldo, movimientos y lista de seguimiento con su migración ·
**Se verifica con:** pruebas en la JVM con SQLite en memoria, una migración `.sqm` aplicada sobre una base de la
versión anterior con los datos intactos, y la misma capa usada luego por `11-multiplatform` sin cambios.

---

## 05-testing

Pruebas. **Build:** 2 · **Caso:** Ambos.

### 05 · `kotest` — Estilos, property-based

**Objetivo:** escribir pruebas con Kotest (estilos de especificación, pruebas guiadas por datos) y, sobre todo,
pruebas basadas en propiedades que encuentran los casos borde que uno no pensó.
**Sección:** `05-testing` · **Build:** nivel 2 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (Kotest ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| Clase JUnit con métodos `@Test` | `class BookSpec : FunSpec({ test("…") { } })` | También `StringSpec`, `DescribeSpec`, `BehaviorSpec` |
| `@ParameterizedTest` + `@MethodSource` | `withData(…)` | Cada dato es una prueba con nombre propio |
| jqwik (`@Property`, `@ForAll`) | `checkAll(Arb.long(1..1_000)) { q -> … }` | Generadores (`Arb`) y *shrinking* al mínimo caso que falla |
| `assertEquals` | `x shouldBe y` | Matchers infix; se ven en `assertions` |
| `@BeforeEach` | `beforeTest { }`, extensiones | Ojo con el modo de aislamiento (trampa) |
| JUnit Platform | `kotest-runner-junit5` sobre JUnit Platform | Convive con `kotlin.test` en el mismo módulo |

**Trampas:**
- El modo de aislamiento por defecto de Kotest es una sola instancia de la especificación para todas sus pruebas: un
  `val` mutable de la clase se comparte entre pruebas, al revés que en JUnit (una instancia por método); se confirma
  con la versión que se fije ⚠️.
- Una propiedad que falla con datos aleatorios solo se reproduce con la semilla que Kotest imprime: hay que fijarla
  en la prueba al investigar.
- `shouldBe` sobre `BigDecimal` usa `equals`: `1.0` y `1.00` no son iguales (por eso `Money` normaliza la escala).
- Una propiedad trivial ("no lanza") pasa siempre y no prueba nada: la propiedad tiene que decir algo del dominio.
- Sin `useJUnitPlatform()` en Gradle, las especificaciones de Kotest no corren y el build sale verde igual.

**Caso:** propiedades del `OrderBook` del Mercado ("nunca queda cruzado", "la cantidad total se conserva", "el orden
de llegada se respeta dentro de un precio") y de `Money` del Banco (`a + b - b == a`, `ofCents` ida y vuelta).

**🎤 Entrevista:** ¿qué es una prueba basada en propiedades y qué encuentra que una de ejemplos no? · ¿Qué es el
*shrinking*? · Kotest frente a JUnit 5.

**⚡ Propiedades del motor de emparejamiento:** generadores de secuencias de órdenes (`Arb` propio) contra el motor de
`02 · channels` · **Se verifica con:** las tres propiedades del caso con 10 000 secuencias cada una, y un error
sembrado a propósito en el motor que Kotest encuentra y reduce a una secuencia de dos o tres órdenes.

### 05 · `mockk` — MockK, incluido `coEvery`

**Objetivo:** aislar una unidad de sus dependencias con MockK (incluidas funciones `suspend`, `object` y funciones de
extensión), y saber cuándo un *fake* escrito a mano es mejor que un mock.
**Sección:** `05-testing` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (MockK ⚠️) | Nota |
|---|---|---|
| `when(fx.rate(…)).thenReturn(…)` (Mockito) | `every { fx.rate(USD, EUR) } returns BigDecimal("0.92")` | En Kotlin `when` es palabra reservada: Mockito pide comillas invertidas |
| — (Mockito no conoce `suspend`) | `coEvery { client.quote("ACME") } returns …`, `coVerify` | `every` no acepta una llamada `suspend` |
| `verify(repo).save(…)`, `ArgumentCaptor` | `verify(exactly = 1) { repo.save(capture(slot)) }` | `slot<T>()` captura el argumento |
| `mockStatic` | `mockkStatic(…)`, `mockkObject(IdGenerator)` | Funciones de nivel superior y `object` |
| `@Mock(answer = RETURNS_DEFAULTS)` | `mockk(relaxed = true)` | Devuelve valores vacíos sin configurar nada |
| Clases `final` (Mockito 5 usa *inline mock maker*) | Todas las clases son `final` y MockK las mockea igual | Sin `open` solo para probar |

**Trampas:**
- `any()` de Mockito devuelve `null`: en un parámetro no nullable de Kotlin lanza `NullPointerException` antes de
  llegar al mock (MockK no tiene ese problema).
- `mockkStatic` sobre una función de extensión de nivel superior pide el nombre de la clase del archivo
  (`"dev.….MoneyKt"`): renombrar el archivo rompe la prueba sin error de compilación.
- `mockkObject` cambia un singleton para toda la JVM: sin `unmockkAll()` al final, contamina otras pruebas.
- Un mock `relaxed` responde a todo: una llamada que falta configurar pasa en silencio con un valor vacío.
- Mockear un tipo que no es nuestro (un `ResultSet`, un cliente HTTP) da pruebas que pasan y producción que falla:
  mejor un *fake* o una prueba de integración.

**Caso:** `TransferService` del Banco probado con un `FxRateClient` y un repositorio mockeados: la transferencia en
otra moneda pide la tasa una sola vez, una falla del cliente se traduce en `FxUnavailable`, y nada se guarda si falla.

**🎤 Entrevista:** ¿mock, stub o fake? · ¿Cómo se mockea una función `suspend` o un `object`? · ¿Por qué Mockito
incomoda en Kotlin?

**⚡ Mini MockK:** una librería mínima con `mock<T>()`, `every { } returns`, `verify { }` y *matchers*, sobre proxies
dinámicos de Java para interfaces · **Se verifica con:** pruebas que usan la mini librería para probar el
`TransferService` (mismos casos que con MockK) y una prueba de que la grabación de llamadas dentro de `every { }` no
ejecuta el código real.

### 05 · `assertions` — Kotest, AssertK, power-assert

**Objetivo:** escribir aserciones que dicen qué falló sin tener que depurar, y elegir entre `kotlin.test`, los
matchers de Kotest, AssertK y el plugin power-assert del compilador.
**Sección:** `05-testing` · **Build:** nivel 2 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| `assertEquals(expected, actual)` | `assertEquals` de `kotlin.test` (lo que usa el curso hoy) | Mismo orden de argumentos que JUnit |
| AssertJ: `assertThat(x).isEqualTo(y)` | AssertK: `assertThat(x).isEqualTo(y)` ⚠️ | Pensada para Kotlin: tipos nullables y smart casts |
| Hamcrest / AssertJ encadenado | Kotest: `x shouldBe y`, `list shouldContainExactly …` ⚠️ | Infix y `assertSoftly { }` |
| `assert` de Java (`-ea`) | Plugin power-assert de `kotlinc`, incluido desde 2.0 | Medido en 2.4.21: dibuja cada subexpresión de un `require` o `assert` que falla |
| `assertNotNull(x)` y luego `x.get()` | `assertNotNull(x)` de `kotlin.test` hace smart cast | Contrato `returns() implies (actual != null)` (ver `k2-notes`) |
| `SoftAssertions` | `assertSoftly { }` (Kotest), `assertAll` (AssertK) | Reportan todas las fallas juntas |

**Trampas:**
- Invertir `expected` y `actual` en `assertEquals` da mensajes al revés ("esperaba X y fue Y" con X e Y cambiados).
- AssertJ funciona desde Kotlin, pero `isNotNull()` no le dice nada al compilador: después hace falta `!!`.
- El plugin power-assert solo transforma las funciones que se le configuran (`kotlin.assert`, `require`,
  `assertTrue`…); el resto sigue con mensajes planos.
- Comparar `Money` o `BigDecimal` con un matcher de igualdad tropieza con la escala; con números de punto flotante
  hace falta tolerancia.
- Una aserción que solo comprueba "no es null" o "no lanza" pasa con casi cualquier implementación.

**Caso:** las mismas pruebas del `TransferService` del Banco y de las velas del Mercado escritas en los tres estilos,
para comparar los mensajes de error.

**🎤 Entrevista:** ¿qué hace bueno el mensaje de una aserción? · ¿Qué es power-assert? · ¿Por qué AssertK y no
AssertJ en Kotlin?

**⚡ Mensajes de falla comparados:** el mismo conjunto de pruebas que fallan a propósito, escrito con `kotlin.test`,
Kotest, AssertK y power-assert · **Se verifica con:** los mensajes de cada estilo capturados en archivos dorados
(una prueba que corre las demás y compara su salida), y una tabla en la lección con cuál ubica la causa en una lectura.

### 05 · `testcontainers` — Testcontainers

**Objetivo:** probar contra bases reales y desechables desde la prueba, sin depender de contenedores levantados a
mano, y mantener la suite rápida.
**Sección:** `05-testing` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Testcontainers ⚠️) | Nota |
|---|---|---|
| `docker compose up` antes de las pruebas (lo de hoy en `04-data`) | `PostgreSQLContainer("postgres:17.2")` en la prueba | La prueba levanta y tira su base |
| `@Testcontainers` + `@Container static` | `companion object` con el contenedor | Ver la trampa del campo estático |
| Puertos fijos (15432, 13306) | `container.jdbcUrl` | Puerto aleatorio del host: nunca se escribe a mano |
| Un contenedor por clase de prueba | Patrón de contenedor único (un `object` que lo arranca una vez) | Una sola base para toda la suite |
| `@DynamicPropertySource` (Spring) | Igual, se ve en `09-spring` | |

**Trampas:**
- Un `@Container` en una propiedad de instancia arranca un contenedor por prueba, porque JUnit crea una instancia por
  método; compartirlo pide que la anotación quede en un campo estático (`companion object` con `@JvmField`) ⚠️.
- Sin Docker, las pruebas fallan o se saltan según la configuración: hay que decidir cuál (hoy `04-data` las salta).
- La primera corrida baja imágenes y tarda minutos: fijar la imagen por versión (como `docker/postgres/compose.yaml`)
  evita sorpresas y comparte la caché.
- Pruebas que comparten la base sin limpiar dependen del orden; cada prueba prepara y borra sus datos, o usa una
  transacción que revierte.
- En Apple Silicon, una imagen sin variante `arm64` corre emulada y mucho más lenta.

**Caso:** el contrato `LedgerRepositoryContract` de `04-data` (tanda 5 de B0′) corrido contra PostgreSQL y MySQL de
Testcontainers, incluida la diferencia de aislamiento entre motores en una transferencia concurrente.

**🎤 Entrevista:** ¿por qué no usar H2 para probar código que corre en PostgreSQL? · ¿Cómo se mantiene rápida una suite
con contenedores? · ¿Qué ventaja tiene sobre un `docker compose` previo?

**⚡ El contrato del ledger con contenedores:** el mismo contrato de `04-data` sobre PostgreSQL y MySQL de
Testcontainers, con un contenedor único por motor · **Se verifica con:** la suite en verde sin ningún `docker compose`
previo, el tiempo total medido con y sin contenedor único, y una prueba que reproduce la actualización perdida en un
saldo en un motor y la evita con `SELECT … FOR UPDATE`.

---

## 06-compiler

Bytecode, reflexión, KSP y plugins del compilador. **Build:** 2 · **Caso:** —.

### 06 · `bytecode` — Lambdas, `companion`, `value class`, propiedades

**Objetivo:** leer el bytecode que genera Kotlin para sus construcciones y saber qué cuesta cada una y cómo la ve
Java.
**Sección:** `06-compiler` · **Build:** nivel 2 · **Caso:** — · **Estado:** nueva

| Java 21 | Lo que genera Kotlin | Nota |
|---|---|---|
| Campo privado + getter/setter | Propiedad: campo privado + `getX`/`setX` | `@JvmField` deja el campo público, sin métodos |
| `static final` | `const val` → constante en línea; `companion object` → clase anidada `Companion` + campo estático | Sin `@JvmStatic`, los métodos viven en `Companion` |
| Método estático en una clase de utilidades | Función de nivel superior → `ArchivoKt.f()` | `@file:JvmName` cambia el nombre de la clase |
| Lambda (`invokedynamic`) | `invokedynamic` desde 2.0 (antes, una clase por lambda) | Medido en `k2-notes` y en el tutorial `decompile` |
| Sobrecargas | Parámetros por defecto → método sintético `f$default` con una máscara de bits | Java no lo ve sin `@JvmOverloads` |
| `record` | `data class` → `equals`, `hashCode`, `toString`, `componentN`, `copy` | `@JvmRecord` genera un `record` de verdad |
| Clase envoltorio | `value class` → el tipo de adentro y nombres alterados (`f-abc12`) | Se boxea al usarse como nullable o genérica |
| `switch` sobre `String` | `when` sobre `String` → `hashCode()` + `equals` | Igual que `javac` |

**Trampas:**
- Cada función pública con parámetros no nullables comprueba sus argumentos con `Intrinsics.checkNotNullParameter`:
  el NPE que llega desde Java nombra el parámetro, pero es una llamada más por método.
- Un `object` se inicializa en la carga de la clase (seguro entre hilos) y una excepción en su `init` deja la clase
  inutilizable (`NoClassDefFoundError` en los siguientes usos).
- Una función `inline` grande copiada en muchos llamadores infla el tamaño del bytecode.
- `lateinit` agrega una comprobación en cada lectura y lanza `UninitializedPropertyAccessException`, no NPE.
- La anotación `@Metadata` lleva la información de Kotlin (nulabilidad, propiedades): herramientas que la borran
  (R8 sin reglas) rompen la reflexión de Kotlin.

**Caso:** las construcciones del propio curso (`Money`, `TransferId`, `OrderBook`, las lambdas de `functional`)
decompiladas con las herramientas del tutorial `decompile`.

**🎤 Entrevista:** ¿cómo ve Java un `companion object`? · ¿Qué genera un parámetro por defecto? · ¿Una `value class` es
siempre gratis?

**⚡ Catálogo de bytecode con archivos dorados:** una construcción por archivo y su salida de `javap -p` (o del
decompilador) guardada como dorada · **Se verifica con:** una prueba que compila cada ejemplo, corre `javap` con
`ToolProvider` y compara con su dorado, de modo que un cambio de compilador que altera el bytecode hace fallar la
prueba y obliga a documentarlo.

### 06 · `reflection` — `KClass`, `KProperty`, `kotlin-reflect` y su costo

**Objetivo:** usar reflexión de Kotlin cuando hace falta (mappers, serialización, frameworks), saber qué funciona sin
`kotlin-reflect` y medir cuánto cuesta.
**Sección:** `06-compiler` · **Build:** nivel 2 · **Caso:** — · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| `Tick.class` | `Tick::class` (`KClass`), `Tick::class.java` | `KClass` sabe de nulabilidad y propiedades |
| `getDeclaredFields()` | `Tick::class.memberProperties` | Necesita `kotlin-reflect` (≈ 3.8 MB en 2.4.21) |
| `getSimpleName()` | `Tick::class.simpleName`, `qualifiedName` | Medido: funcionan **sin** `kotlin-reflect` |
| `Method` / `MethodHandle` | `Tick::price` (`KProperty1`), `::validate` (`KFunction`) | Las referencias funcionan sin `kotlin-reflect` (medido: `name` y `get`) |
| Constructor por reflexión | `Tick::class.primaryConstructor!!.callBy(args)` | `callBy` respeta los valores por defecto |
| `TypeToken`/`ParameterizedType` | `typeOf<List<Tick>>()` con `reified` | El tipo genérico completo en runtime |

**Trampas:**
- Sin `kotlin-reflect` en el classpath, `Tick::class.members` lanza `KotlinReflectionNotSupportedError` en runtime
  (medido); compila igual.
- La primera llamada a la reflexión completa de Kotlin es lenta (lee y arma la metadata): en un camino caliente se
  guarda el resultado.
- La reflexión de Java sobre una clase Kotlin ve getters, campos privados y `Companion`, no propiedades ni nulabilidad.
- `T::class` sobre un genérico no compila sin `reified`; sobre un valor nullable hace falta comprobar antes.
- En Android, R8 sin reglas de conservación borra la metadata y la reflexión de Kotlin falla en producción, no en las
  pruebas.

**Caso:** un mapper genérico de filas (`Map<String, Any?>`) a `data class` para los asientos del ledger y los ticks.

**🎤 Entrevista:** ¿qué aporta `KClass` sobre `Class`? · ¿Qué necesita `kotlin-reflect` y qué no? · ¿Cómo se evita
el costo de la reflexión en un camino caliente?

**⚡ Mapper por reflexión frente a uno escrito a mano:** el mapper genérico (constructor primario, nombres, valores
por defecto con `callBy`, nullables) y su versión a mano · **Se verifica con:** pruebas de valores por defecto, nulos
y claves faltantes, y una medición con calentamiento (un millón de filas) guardada en la lección, con y sin caché de
la metadata.

### 06 · `ksp` — KSP frente a kapt

**Objetivo:** generar código en tiempo de compilación con KSP (el reemplazo de kapt), entender por qué kapt es lento y
escribir un procesador con errores propios.
**Sección:** `06-compiler` · **Build:** nivel 2 · **Caso:** — · **Estado:** nueva

| Java 21 | Kotlin (KSP ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| `javax.annotation.processing.Processor` | `SymbolProcessor` + `SymbolProcessorProvider` | Lee símbolos de Kotlin, no stubs de Java |
| Procesadores de Java desde Kotlin | kapt: genera stubs Java de todo el módulo y corre APT | Lento; en mantenimiento |
| Registro con `@AutoService` / `META-INF/services` | `META-INF/services/…SymbolProcessorProvider` | Mismo mecanismo de `ServiceLoader` |
| `Messager.printMessage(ERROR, …, element)` | `logger.error("…", symbol)` | El error apunta al símbolo en el código del usuario |
| MapStruct, Dagger, Room con APT | Sus versiones KSP (Room, Moshi, Dagger) | Hay que confirmar soporte KSP por librería |

**Trampas:**
- KSP solo **genera** archivos nuevos: no modifica clases existentes (para eso hace falta un plugin del compilador).
- Declarar mal las dependencias de un archivo generado (`aggregating` frente a `isolating`) rompe el build incremental:
  cambios que no regeneran o regeneraciones de más.
- El código generado tiene que estar en los *source sets* para que el IDE lo vea; si no, compila en Gradle y se ve rojo
  en IntelliJ.
- Un procesador que lanza una excepción en vez de reportar con `logger.error` da un error ilegible.
- Mezclar kapt y KSP en un módulo paga los dos costos.

**Caso:** builders y mappers generados para las entidades del curso (`Tick`, `JournalEntry`, `Transfer`).

**🎤 Entrevista:** ¿por qué KSP es más rápido que kapt? · ¿Qué puede y qué no puede hacer un procesador KSP? · ¿Qué es
el procesamiento incremental?

**⚡ Generador de builders y mappers:** un procesador KSP que, para cada clase anotada, genera un builder y un mapper
desde `Map<String, Any?>`, con errores de compilación propios (constructor privado, tipo no soportado) · **Se verifica
con:** pruebas de compilación en memoria donde el código generado compila y funciona, y donde cada uso incorrecto da
exactamente el error esperado, apuntando al símbolo.

### 06 · `compilerplugins` — all-open, no-arg, serialization, power-assert, uno propio con FIR

**Objetivo:** saber qué plugins del compilador usa todo proyecto Kotlin de backend (y por qué), cuáles vienen con
`kotlinc`, y escribir uno propio sobre el frontend K2 (FIR).
**Sección:** `06-compiler` · **Build:** nivel 2 · **Caso:** — · **Estado:** nueva

| Java 21 | Kotlin (plugins de `kotlinc` 2.4.21) | Nota |
|---|---|---|
| Clases no `final` por defecto (proxies de Spring) | `allopen` / `kotlin-spring` | Abre las clases anotadas; sin él, Spring no puede crear el proxy |
| Constructor sin argumentos para JPA | `noarg` / `kotlin-jpa` | Lo usa `04-data` (`jpa`) |
| Jackson por reflexión | `kotlinx-serialization` | Genera los serializadores al compilar (lección `json`) |
| `assert` con mensaje plano | `power-assert` | En `kotlinc/lib` desde 2.0 (medido) |
| Lombok | Plugin `lombok` de Kotlin | Para que Kotlin vea lo que Lombok genera en clases Java del mismo módulo |
| — | `compose-compiler` | En `kotlinc/lib` desde 2.0 (medido); se ve en `11-multiplatform` |
| Procesador que modifica el AST (truco de Lombok) | Plugin propio con FIR (análisis) e IR (generación) | API inestable: atado a una versión exacta de Kotlin ⚠️ |

**Trampas:**
- Sin `kotlin-spring`, un `@Service` con `@Transactional` es `final` y Spring falla al crear el proxy (o, en algunos
  casos, la anotación no se aplica sin avisar).
- El constructor de `noarg` no corre los inicializadores de las propiedades salvo la opción `invokeInitializers`: una
  entidad cargada por JPA puede tener `null` donde Kotlin prometía otra cosa.
- `allopen` abre también los métodos: una `val` de una entidad abierta puede sobrescribirse en un proxy y devolver otra
  cosa.
- Un plugin propio compilado contra 2.4.21 casi seguro no carga en 2.5: la API del compilador no es estable.
- Que un plugin venga en `kotlinc/lib` no quiere decir que Gradle lo active: cada uno se declara en el build.

**Caso:** un diagnóstico propio para el Banco: error de compilación al construir `BigDecimal` o `Money` desde un
`Double`, y aviso al declarar una entidad JPA como `data class`.

**🎤 Entrevista:** ¿por qué existen `kotlin-spring` y `kotlin-jpa`? · ¿Qué es FIR? · Plugin del compilador frente a
procesador KSP.

**⚡ Plugin propio con FIR:** el diagnóstico del caso como plugin de K2, empaquetado y usado por un módulo de ejemplo
· **Se verifica con:** pruebas del compilador con los diagnósticos esperados (código, línea y columna) sobre archivos
de ejemplo, y una prueba de que el código válido del curso compila sin avisos con el plugin activo.

---

## 07-design

Idioms, patrones, FP y arquitectura. **Build:** 1 · **Caso:** Banco (DDD) y Mercado (hexagonal).

### 07 · `idioms` — Effective Kotlin

**Objetivo:** escribir Kotlin que un equipo Kotlin aprueba en una revisión: inmutable por defecto, null-safety sin
`!!`, el tipo justo para cada cosa, y la stdlib en vez de utilidades propias.
**Sección:** `07-design` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| Clase de utilidades con `static` | Funciones de nivel superior y de extensión | `Money.format()` en vez de `MoneyUtils.format(m)` |
| Builder o constructores telescópicos | Parámetros con nombre y por defecto | El builder solo si hay validación en pasos |
| `Optional<T>` | `T?` con `?.`, `?:` y `let` | `Optional` en Kotlin es ruido |
| Getters/setters y `equals` escritos | Propiedades y `data class` | Pero no para entidades JPA |
| Banderas `boolean`/`int` de estado | `sealed` o `enum` | El `when` exhaustivo obliga a cubrir todo |
| Validaciones con `if` + `throw` | `require`, `check`, `requireNotNull` | Distinguen culpa del cliente de culpa nuestra |
| `final` donde se acuerda uno | `val`, colecciones de solo lectura, clases `final` por defecto | Lo inmutable es lo normal |

**Trampas:**
- Las funciones de extensión se resuelven por el tipo **declarado**, no por el real: no son polimórficas y una
  extensión no puede "sobrescribir" un miembro.
- `!!` en un PR casi siempre esconde una decisión de diseño pendiente (¿puede ser null o no?).
- Exponer una `MutableList` como `List` sin copiar deja que el dueño la cambie por debajo del que la lee.
- Encadenar más de dos scope functions se vuelve ilegible; `apply`/`also` para configurar y efectos, no para todo.
- Copiar el estilo de Java (getters explícitos, `Optional`, utilidades estáticas) compila pero no pasa una revisión.

**Caso:** revisión de PRs sobre código del Banco y del Mercado con errores típicos sembrados (`!!`, `data class` como
entidad, `var` innecesarios, `MutableList` expuesta).

**🎤 Entrevista:** ¿cuándo usarías una función de extensión y cuándo un miembro? · ¿Qué le dirías a un PR lleno de `!!`?
· `data class`: ¿cuándo sí y cuándo no?

**⚡ Rescate de legado:** una clase dios portada tal cual de Java (servicio de transferencias de 600 líneas), cubierta
primero con pruebas de caracterización y refactorizada a Kotlin idiomático · **Se verifica con:** las pruebas de
caracterización (un *golden master* sobre 1 000 entradas generadas con semilla fija) en verde antes y después, y
cero `!!` y menos `var` medidos al final.

### 07 · `gof` — Qué patrones desaparecen en Kotlin

**Objetivo:** reconocer qué patrones de diseño clásicos ya trae el lenguaje (y se escriben en una línea), cuáles
cambian de forma y cuáles siguen igual.
**Sección:** `07-design` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| Singleton con `getInstance()` | `object` | Seguro entre hilos; difícil de reemplazar en pruebas |
| Builder | Parámetros por defecto y con nombre, o un DSL (`dsl`) | |
| Strategy con interfaz y clases | Una lambda o `fun interface` | `CommissionRule = (Order) -> Money` |
| Decorator | Delegación de clase `by` (`delegation`) | |
| Observer | `Flow`, `StateFlow` (`02-coroutines`) | |
| Visitor | `sealed` + `when` exhaustivo | Sin doble despacho |
| Factory method | `companion object { fun of(…) }`, `operator fun invoke` | |
| Template method | Función de orden superior | El "hueco" es un parámetro función |

**Trampas:**
- Un `object` como singleton es estado global: las pruebas no lo pueden reemplazar si no se inyecta.
- `sealed` + `when` facilita agregar operaciones pero obliga a tocar todos los `when` al agregar un tipo (el clásico
  problema de la expresión, al revés que el Visitor abierto).
- Un `operator fun invoke` en el `companion` hace que una fábrica parezca un constructor: confunde al que lee.
- Escribir el patrón de Java completo en Kotlin "por si acaso" agrega clases que el lenguaje ya resolvía.
- Una lambda como Strategy pierde el nombre en los logs y en el depurador: si la estrategia tiene identidad, mejor un
  `enum` o un `object` con nombre.

**Caso:** reglas de comisión del Mercado como Strategy (lambdas), los comandos del `OrderBook` como Command sellado,
`AuditedLedger` como Decorator del Banco y las alertas de precio como Observer.

**🎤 Entrevista:** ¿qué patrones desaparecen en Kotlin y por qué? · ¿Cómo se escribe un Visitor en Kotlin? · ¿Es
`object` un buen singleton?

**⚡ Catálogo antes y después:** ocho patrones con su versión Java (en `src/main/java`) y su versión Kotlin, en el mismo
módulo · **Se verifica con:** la misma batería de pruebas parametrizada corriendo contra las dos implementaciones de
cada patrón, y una tabla de líneas y clases de cada versión.

### 07 · `fp` — Arrow: `Either`, `Raise`, optics

**Objetivo:** manejar errores como valores con Arrow (`Either`, el DSL `Raise`, acumulación de errores) y modificar
estructuras inmutables anidadas con optics, sabiendo cuándo esto ayuda y cuándo complica.
**Sección:** `07-design` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Arrow ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| `Either` de Vavr | `Either<BankError, Transfer>` | El error es parte de la firma |
| Cadena de `flatMap` anidados | `either { val a = parse(x).bind(); ensure(a > 0) { … } }` | DSL `Raise`: código directo, cortocircuito en el primer error |
| `Validation` de Vavr | `zipOrAccumulate`, `mapOrAccumulate` | Acumula todos los errores |
| `Optional` | `T?` (no `Option`) | Arrow recomienda los nullables de Kotlin |
| `record` + "withers" anidados | Optics (`@optics` + KSP): `Account.limits.daily.modify(…)` | Modificar en profundidad sin `copy` anidados |
| `List` que no puede estar vacía | `NonEmptyList` | Los errores acumulados nunca son una lista vacía |

**Trampas:**
- Arrow cambió mucho entre versiones mayores (`Validated` dejó paso a las funciones de acumulación): los ejemplos de
  internet suelen ser de una API vieja.
- Mezclar excepciones y `Either` sin una frontera clara termina en las dos cosas: una excepción que escapa de un
  `either { }` no se convierte sola en `Left`.
- `bind()` fuera de un bloque `either { }` (o de un `Raise`) no compila: el DSL vive en su receptor.
- Optics pide KSP y anotar las clases: en un dominio chico, `copy` anidado se lee mejor.
- Llenar de `Either` el código de infraestructura (controladores, repositorios) obliga a desenvolver en cada capa.

**Caso:** la validación de una `Transfer` del Banco acumulando errores, y `TransferService` devolviendo
`Either<BankError, TransferReceipt>` en vez de lanzar `BankException`.

**🎤 Entrevista:** excepciones, `Result` o `Either`: ¿cuándo cada uno? · ¿Qué hace `Raise` y por qué no es "solo
`flatMap`"? · ¿Cómo acumulas errores de validación?

**⚡ `TransferService` funcional:** el ⚡ de `exceptions` reescrito con `Either` y `Raise` · **Se verifica con:** la
misma tabla de casos del ⚡ de `exceptions` (solicitud → resultado → código HTTP) pasando con la versión funcional,
y una prueba basada en propiedades de que la validación devuelve **todos** los errores de una solicitud inválida.

### 07 · `architecture` — Hexagonal + DDD

**Objetivo:** separar el dominio de la infraestructura (puertos y adaptadores), modelar agregados con invariantes y
eventos de dominio, y publicar eventos sin perderlos ni duplicarlos (outbox).
**Sección:** `07-design` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| Capas de Spring (`@Service` → `@Repository`) | Módulo de dominio sin dependencias + adaptadores | El dominio no importa Spring ni JPA |
| Interfaz de repositorio en el paquete de infraestructura | Puerto (`interface Ledger`) en el dominio | El adaptador JDBC o JPA lo implementa |
| Entidad JPA como modelo de dominio | Agregado (`Account`) separado de su entidad de persistencia | El mapeo vive en el adaptador |
| Eventos con jerarquía de clases | `sealed interface AccountEvent` | `when` exhaustivo en los consumidores |
| Visibilidad de paquete para esconder detalles | `internal` (visible en el módulo de Gradle) | No hay visibilidad de paquete en Kotlin |
| IDs como `Long` o `String` | `value class AccountId` | El compilador no deja mezclar ids |

**Trampas:**
- `internal` es por módulo de Gradle, no por paquete: sin módulos separados, todo el proyecto ve lo "interno".
- Un dominio de `data class` con todo público y servicios que lo manipulan es un modelo anémico con otro nombre.
- Una anotación de JPA o de Spring en el dominio ata el dominio a la infraestructura (y a sus plugins `allopen`).
- Un outbox sin consumidor idempotente duplica efectos cuando reintenta.
- Una transacción que modifica dos agregados a la vez esconde una regla de negocio que debería ser un evento.

**Caso:** el ledger del Banco como agregado `Account` con eventos (`Debited`, `Credited`, `Frozen`) y outbox; el
motor de emparejamiento del Mercado como núcleo hexagonal sin dependencias, con adaptadores en memoria y JDBC.

**🎤 Entrevista:** ¿qué es una arquitectura hexagonal y qué gana? · ¿Qué es un agregado y dónde viven sus invariantes?
· ¿Qué problema resuelve el patrón outbox?

**⚡ Núcleo hexagonal con ledger DDD:** módulos de Gradle `domain` (sin dependencias), `adapters` y `app`, con el
motor de emparejamiento y el ledger · **Se verifica con:** una prueba que falla si `domain` importa algo de Spring,
JPA o JDBC; el mismo contrato de puertos pasando con el adaptador en memoria y con el JDBC; y una prueba de que el
outbox entrega cada evento una sola vez a un consumidor falso aunque se reintente.

---

## 08-build

Gradle, calidad, docs y empaquetado. **Build:** 5 · **Caso:** —.

### 08 · `gradle` — Kotlin DSL, version catalogs, convention plugins en `build-logic`

**Objetivo:** leer y escribir builds de Gradle en Kotlin DSL, centralizar versiones con un catálogo y compartir
configuración entre módulos con plugins de convención en vez de copiar bloques.
**Sección:** `08-build` · **Build:** nivel 5 · **Caso:** — · **Estado:** nueva

| Java 21 (Maven / Gradle Groovy) | Kotlin DSL (Gradle 9.9.1) | Nota |
|---|---|---|
| `build.gradle` en Groovy | `build.gradle.kts` | Tipado: el IDE completa y el compilador avisa |
| `<dependencyManagement>`, propiedades de versión | `gradle/libs.versions.toml` + `libs.kotlinx.coroutines.core` | Catálogo de versiones: un solo lugar, accesores tipados |
| POM padre, `allprojects { }` | Plugins de convención en `build-logic` (`kotlin-dsl`) | `id("course.kotlin-library")` en cada módulo |
| `buildSrc` | `includeBuild("build-logic")` en `pluginManagement` | Un build incluido no invalida todo al cambiar |
| `JAVA_HOME` del que corre | `kotlin { jvmToolchain(21) }` | El JDK del proyecto, independiente del de Gradle |
| Perfiles de Maven | Propiedades (`-Plesson=…`) y tareas propias | Este repo elige el `Main` con `-Plesson` |

**Trampas:**
- `./gradlew --version` muestra el Kotlin **embebido** de Gradle (el de los scripts), no el del proyecto: el del
  proyecto es el del plugin `kotlin("jvm")`.
- El plugin de Kotlin y Gradle tienen compatibilidad cruzada: el plugin de Kotlin 1.9.22 usa APIs que Gradle 10
  quita, por eso el track 19 de este repo se queda en Gradle 9.
- `allprojects { }` y `subprojects { }` acoplan los módulos y rompen la *configuration cache*; la configuración
  compartida va en plugins de convención.
- Los accesores tipados (`libs.…`, `implementation(…)` de un plugin) solo existen para lo que se aplica en
  `plugins { }`; con `apply(plugin = …)` no se generan.
- Un nombre del catálogo con guiones (`kotlinx-coroutines-core`) se usa con puntos (`libs.kotlinx.coroutines.core`).

**Caso:** el propio repo: hoy cada sección repite sus versiones a propósito; esta lección muestra cómo sería con un
catálogo y plugins de convención, y qué se gana y qué se pierde.

**🎤 Entrevista:** ¿`buildSrc` o `build-logic`? · ¿Qué es un version catalog? · ¿Qué es la *configuration cache* y qué
la rompe?

**⚡ Plugin de convención que genera el índice del README:** un plugin en `build-logic` que recorre las lecciones y
sus `Exercises.md` y genera la tabla de lecciones y los conteos del README · **Se verifica con:** pruebas del plugin
con Gradle TestKit, y una tarea `check` que falla si el README no coincide con lo generado.

### 08 · `quality` — detekt, ktlint, Kover

**Objetivo:** automatizar el análisis estático, el formato y la cobertura de un proyecto Kotlin, y hacer que fallen
el build y no la revisión de código.
**Sección:** `08-build` · **Build:** nivel 5 · **Caso:** — · **Estado:** nueva

| Java 21 | Kotlin (detekt, ktlint, Kover ⚠️ se fijan al escribir la sección) | Nota |
|---|---|---|
| Checkstyle, PMD, SpotBugs | detekt | Reglas pensadas para Kotlin (complejidad, `!!`, estilo) |
| google-java-format, Spotless | ktlint (solo o con Spotless) | Formato según `.editorconfig` |
| JaCoCo | Kover | Cobertura que entiende funciones `inline`, `data class` y corrutinas |
| Umbral de cobertura en el plugin | `kover { verify { rule { minBound(80) } } }` | Falla `check` si baja |
| Supresión con anotaciones | `@Suppress("MagicNumber")`, archivo *baseline* | La baseline congela la deuda vieja |

**Trampas:**
- detekt trae su propio compilador de Kotlin: una versión de detekt que no conoce el Kotlin del proyecto falla o no
  analiza bien.
- Una *baseline* generada una vez y olvidada esconde los problemas para siempre.
- El formato de ktlint y el del IDE se pelean si no comparten `.editorconfig`: commits que solo cambian espacios.
- La cobertura de líneas alta no dice si las pruebas comprueban algo (una prueba sin aserciones cubre igual).
- Algunas reglas de detekt necesitan resolución de tipos (`detektMain`); con la tarea simple no se aplican.

**Caso:** el código del curso (`01-language`, `04-data`) bajo detekt, ktlint y Kover, con las reglas ajustadas a lo
que la lección muestra a propósito (las trampas que se demuestran no son errores).

**🎤 Entrevista:** ¿qué mira detekt que no mire el compilador? · ¿Qué umbral de cobertura pondrías y por qué? · ¿Qué
problema tiene una baseline?

**⚡ Calidad del repo en el build:** detekt, ktlint y Kover configurados con un plugin de convención y aplicados a las
secciones · **Se verifica con:** `./gradlew check` en verde con la baseline vacía, y tres errores sembrados (un `!!`,
un archivo mal formateado y una función sin pruebas) que hacen fallar `check` cada uno con su mensaje.

### 08 · `docs` — Dokka

**Objetivo:** documentar APIs con KDoc y generar la documentación con Dokka, incluidos ejemplos que compilan.
**Sección:** `08-build` · **Build:** nivel 5 · **Caso:** — · **Estado:** nueva

| Java 21 | Kotlin (Dokka ⚠️) | Nota |
|---|---|---|
| Javadoc con HTML | KDoc con Markdown | `**negrita**`, listas y bloques de código |
| `{@link Money#of}` | `[Money.of]` | Enlaces con corchetes |
| `@param`, `@return`, `@throws` | Los mismos (`@throws` para quien llama desde Java) | |
| Ejemplo copiado en el comentario | `@sample dev.….MoneySamples.ofCents` | El ejemplo es código que compila |
| `javadoc` | Dokka (HTML, y Javadoc para publicar en Maven Central) | Un formato por salida |

**Trampas:**
- KDoc no interpreta etiquetas HTML como Javadoc: `<p>` sale tal cual.
- Un `@sample` que apunta a una función que no existe solo da un aviso: el ejemplo desaparece sin romper el build
  (salvo que los avisos fallen).
- Las declaraciones `internal` y `private` no aparecen: lo que se documenta es la API pública.
- Publicar en Maven Central exige un jar de Javadoc: hace falta el formato Javadoc de Dokka, no el HTML.
- La documentación del módulo y de los paquetes va en un archivo Markdown aparte (`Module.md`), no en el código.

**Caso:** la API de `Money`, `LedgerRepository` y el feed documentados, con ejemplos que son pruebas del curso.

**🎤 Entrevista:** KDoc frente a Javadoc · ¿Cómo mantienes los ejemplos de la documentación al día? · ¿Qué hace falta
para publicar una librería Kotlin?

**⚡ Sitio de documentación del curso:** Dokka sobre `01-language` y `04-data`, con `Module.md` por sección y ejemplos
con `@sample` · **Se verifica con:** la generación sin avisos con `failOnWarning`, todos los `@sample` resueltos, y una
prueba de que cada declaración pública de `basics` y `jdbc` tiene KDoc.

### 08 · `packaging` — Fat jar, jlink, GraalVM native image

**Objetivo:** empaquetar una aplicación Kotlin para distribuirla (jar con dependencias, runtime propio con jlink,
binario nativo con GraalVM) y medir qué cambia en tamaño y arranque.
**Sección:** `08-build` · **Build:** nivel 5 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (plugins ⚠️ se fijan al escribir la sección) | Nota |
|---|---|---|
| `maven-shade-plugin` | Plugin Shadow (fat jar) | La stdlib de Kotlin va adentro |
| `maven-assembly`, scripts de arranque | Plugin `application` (`installDist`, `distZip`) | Lo que usa este repo para `run` |
| `jlink` | `jlink` con los módulos que hacen falta | Un JRE recortado junto a la app |
| `jpackage` | `jpackage` | Instalador por sistema operativo |
| GraalVM `native-image` | Plugin de GraalVM Native Build Tools | Arranque en milisegundos, sin JIT |

**Trampas:**
- Un fat jar que junta varias librerías pisa los archivos de `META-INF/services`: hay que fusionarlos o el
  `ServiceLoader` (por ejemplo, los drivers JDBC) no encuentra nada.
- `jlink` no acepta módulos automáticos: una dependencia sin `module-info` obliga a otra estrategia.
- `native-image` necesita conocer de antemano la reflexión: `kotlin-reflect` y librerías basadas en reflexión piden
  configuración; kotlinx-serialization no, porque genera el código al compilar.
- Un binario nativo arranca rápido pero puede rendir menos en un proceso largo (sin JIT): depende de la carga.
- El binario nativo es por plataforma: hay que construirlo en cada sistema operativo y arquitectura.

**Caso:** el conciliador de extracto del Banco (el ⚡ de `io`) como herramienta de línea de comandos.

**🎤 Entrevista:** fat jar, jlink o native image: ¿cuándo cada uno? · ¿Qué complica GraalVM en una app Kotlin? · ¿Por
qué kotlinx-serialization es amigable con native image?

**⚡ La misma CLI en cuatro paquetes:** el conciliador como fat jar, distribución de `application`, imagen de jlink y
binario nativo · **Se verifica con:** una prueba de humo que corre cada artefacto sobre `bank-statement.csv` y compara
la salida con el archivo dorado, y una tabla de tamaño y tiempo de arranque de cada uno.

---

## 09-spring

Spring Boot y WebFlux. **Build:** 3 · **Caso:** Banco.

### 09 · `boot` — Plugin `kotlin-spring`, constructor injection, `@ConfigurationProperties`, validación

**Objetivo:** escribir una aplicación Spring Boot en Kotlin idiomático, sabiendo qué hacen los plugins del compilador
y dónde se rompe la validación y la nulabilidad.
**Sección:** `09-spring` · **Build:** nivel 3 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Spring Boot ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| `public static void main` + `SpringApplication.run` | `runApplication<BankApp>(*args)` | Función con `reified` |
| `@Autowired` en campos | Constructor primario: `class TransferService(private val repo: Ledger)` | Inyección por constructor sin anotación |
| Clases no `final` para los proxies | Plugin `kotlin-spring` (all-open para `@Component`, `@Transactional`…) | Sin él, Spring no puede crear el proxy |
| `@ConfigurationProperties` con setters | `data class BankProperties(val maxTransfer: BigDecimal)` | Enlace por constructor |
| `@NotBlank` en un campo | `@field:NotBlank val alias: String` | El *use-site target* decide dónde va la anotación |
| Jackson con constructor vacío | `jackson-module-kotlin` | Entiende constructores con parámetros y nulabilidad |

**Trampas:**
- `@NotBlank` en un parámetro del constructor sin `@field:` (o `@param:` según la versión) queda donde Bean
  Validation no mira: la validación no corre y no hay error.
- Sin `kotlin-spring`, un `@Service` con `@Transactional` es `final`: Spring falla al crear el proxy, o la anotación
  no tiene efecto.
- Sin `jackson-module-kotlin`, deserializar una `data class` falla por falta de constructor vacío.
- `lateinit var` con `@Value` falla en runtime, no al compilar, si la propiedad no existe.
- Las APIs de Spring anotadas con nulabilidad se ven como tipos de Kotlin; si están anotadas con JSpecify, desde Kotlin
  2.1 un mal uso es error de compilación (medido en `01 · interop`).

**Caso:** la API REST de transferencias del Banco: `POST /transfers` con validación, límites en
`@ConfigurationProperties` y `BankException` traducida a `ProblemDetail`.

**🎤 Entrevista:** ¿por qué existe `kotlin-spring`? · ¿Cómo se valida un `data class` con Bean Validation? · Inyección
por constructor en Kotlin.

**⚡ API de transferencias con idempotencia:** `POST /transfers` con cabecera `Idempotency-Key`, validación que
acumula errores y errores de dominio a HTTP · **Se verifica con:** pruebas MockMvc de que la misma clave devuelve la
misma respuesta sin duplicar el asiento, de que una solicitud inválida devuelve todos sus errores en un 400, y de que
sin fondos da 422 con `ProblemDetail`.

### 09 · `springdata` — Spring Data desde Kotlin

**Objetivo:** usar Spring Data (JPA, JDBC y R2DBC con corrutinas) desde Kotlin sin `Optional` ni sorpresas de
transacciones.
**Sección:** `09-spring` · **Build:** nivel 3 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Spring Data ⚠️) | Nota |
|---|---|---|
| `repo.findById(id).orElse(null)` | `repo.findByIdOrNull(id)` | Extensión de Spring Data para Kotlin |
| `interface AccountRepository extends JpaRepository<…>` | `interface AccountRepository : JpaRepository<Account, Long>` | Las reglas de entidades de `04 · jpa` siguen valiendo |
| `ReactiveCrudRepository` con `Mono`/`Flux` | `CoroutineCrudRepository` con `suspend` y `Flow` | Spring Data R2DBC |
| `JdbcTemplate` | `JdbcClient` / Spring Data JDBC | Sin contexto de persistencia |
| `@Transactional` en el servicio | Igual (con `kotlin-spring` para abrir la clase) | No aplica a métodos privados ni llamados desde la misma clase |

**Trampas:**
- `@Transactional` en un método llamado desde la misma clase no pasa por el proxy: no hay transacción.
- *Open session in view* viene activado por defecto en Spring Boot: la carga perezosa "funciona" en el controlador y
  esconde el N+1 hasta producción.
- Un método de consulta derivada con un nombre mal escrito falla al arrancar la aplicación, no al compilar.
- `save()` sobre una entidad con id asignado (no generado) hace un `merge` con un `SELECT` previo.
- Mezclar Spring Data JPA y R2DBC en una aplicación duplica la configuración y las transacciones no se cruzan.

**Caso:** el ledger del Banco con Spring Data JPA (cuentas con `@Version`, asientos) y la consulta de movimientos con
proyecciones.

**🎤 Entrevista:** ¿por qué falla `@Transactional` en una llamada interna? · ¿Qué es *open session in view* y por qué
desactivarlo? · Spring Data JPA frente a JDBC.

**⚡ Ledger con Spring Data:** repositorios del ledger, reintento ante `OptimisticLockingFailureException` y consultas
sin N+1 · **Se verifica con:** pruebas con Testcontainers donde cada endpoint ejecuta un número fijo de sentencias
(contadas), una transferencia concurrente que se reintenta y no pierde saldo, y *open session in view* desactivado.

### 09 · `webflux` — Corrutinas, `Flow`, `coRouter`, `WebClient`

**Objetivo:** escribir un backend reactivo con Spring WebFlux usando corrutinas (`suspend` y `Flow`) en vez de
`Mono`/`Flux`, y saber cuándo conviene frente a Spring MVC.
**Sección:** `09-spring` · **Build:** nivel 3 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (Spring WebFlux ⚠️) | Nota |
|---|---|---|
| Controlador que devuelve `Mono<Account>` | `suspend fun account(id: String): Account` | Spring hace el puente (`03 · bridges`) |
| `Flux<Tick>` en el controlador | `fun ticks(): Flow<Tick>` | *Streaming* (por ejemplo, SSE) |
| `RouterFunctions.route()` | `coRouter { GET("/accounts/{id}", handler::get) }` | DSL de rutas con corrutinas |
| `WebClient …retrieve().bodyToMono()` | `webClient.get().uri(…).retrieve().awaitBody<Quote>()` | Extensiones para corrutinas |
| Spring MVC sobre hilos (o hilos virtuales) | WebFlux sobre Netty | Comparar con medición |

**Trampas:**
- Una llamada bloqueante (JDBC, `Thread.sleep`) en un handler de WebFlux ocupa un hilo del *event loop* y frena todo
  el servidor.
- Tener los dos starters (MVC y WebFlux) hace que gane MVC: la aplicación no es reactiva aunque se escriba como tal.
- El contexto (seguridad, MDC) solo viaja si se usan los puentes de corrutinas; un `launch` aparte lo pierde.
- Un `Flow` devuelto sin `content-type` de *streaming* se junta entero antes de responder.
- Con hilos virtuales, Spring MVC bloqueante ya escala bien: WebFlux gana en *streaming* y con muchas conexiones
  lentas, no siempre.

**Caso:** la API de transferencias del Banco y el feed de precios del Mercado como `Flow` con SSE.

**🎤 Entrevista:** WebFlux con corrutinas frente a MVC con hilos virtuales · ¿Qué pasa si bloqueas en WebFlux? ·
¿Cómo devuelves un *stream* de precios?

**⚡ API reactiva de punta a punta:** WebFlux + R2DBC + corrutinas para el ledger y el feed, y la misma API en MVC
con JDBC · **Se verifica con:** el mismo conjunto de pruebas de contrato en las dos versiones, y una prueba de carga
(1 000 clientes concurrentes, latencia de base simulada) con una tabla de throughput, p99 y hilos usados.

### 09 · `springtesting` — MockMvc DSL, `WebTestClient`, SpringMockK

**Objetivo:** probar una aplicación Spring en Kotlin a cada nivel (unidad, *slice*, aplicación completa) con los DSL
de Kotlin y sin que la suite tarde minutos.
**Sección:** `09-spring` · **Build:** nivel 3 · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Spring Boot Test, SpringMockK ⚠️) | Nota |
|---|---|---|
| `mockMvc.perform(get("/x")).andExpect(status().isOk())` | `mockMvc.get("/x").andExpect { status { isOk() } }` | DSL de Kotlin de MockMvc |
| `webTestClient…expectBody(Account.class)` | `expectBody<Account>()` | Extensión con `reified` |
| `@MockBean` / `@MockitoBean` | `@MockkBean` (SpringMockK) | MockK en vez de Mockito (`05 · mockk`) |
| `@SpringBootTest` para todo | *Slices*: `@WebMvcTest`, `@DataJpaTest`, `@WebFluxTest` | Contextos chicos y rápidos |
| Base de pruebas a mano | Testcontainers con `@ServiceConnection` | La conexión la arma Spring |

**Trampas:**
- Cada combinación distinta de beans mockeados crea otro contexto de Spring: la caché de contextos deja de servir y la
  suite se vuelve lenta.
- Una prueba `@Transactional` revierte al final y nunca hace *flush*: errores de constraints que en producción
  saltan al commit aquí no aparecen.
- `@SpringBootTest` sin `webEnvironment` no levanta servidor: un `WebTestClient` contra un puerto real falla.
- Un *slice* no carga todo: un bean que falta da un error de contexto que parece del código y no de la prueba.
- `any()` de Mockito en parámetros no nullables de Kotlin lanza NPE: otra razón para SpringMockK.

**Caso:** la API de transferencias del Banco probada en tres niveles, con la base en Testcontainers.

**🎤 Entrevista:** ¿cuándo `@SpringBootTest` y cuándo un *slice*? · ¿Qué rompe la caché de contextos? · ¿Por qué una
prueba `@Transactional` puede esconder un bug?

**⚡ Pirámide de pruebas de la API:** pruebas de unidad, *slices* y aplicación completa con Testcontainers para la API
de transferencias · **Se verifica con:** la cantidad de contextos de Spring creados (registrada y acotada), el tiempo
de cada nivel en una tabla, y tres errores sembrados (validación, consulta, configuración) que atrapa cada uno el
nivel que corresponde.

---

## 10-ktor

Ktor. **Build:** 3 · **Caso:** Mercado.

### 10 · `server` — Routing, plugins, auth, WebSockets

**Objetivo:** escribir un servidor HTTP y WebSocket con Ktor: rutas con DSL, plugins, autenticación y streaming con
corrutinas, sin la magia de Spring.
**Sección:** `10-ktor` · **Build:** nivel 3 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (Ktor ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| Javalin, Spark, Servlets | `embeddedServer(Netty) { routing { get("/quotes/{ticker}") { … } } }` | Todo es código: sin escaneo de clases |
| Filtros, interceptores | Plugins: `install(ContentNegotiation) { json() }` | `StatusPages`, `CallLogging`, `Authentication` |
| `@RequestBody` | `call.receive<OrderRequest>()` | Con kotlinx-serialization |
| Spring Security con JWT | `install(Authentication) { jwt { … } }` + `authenticate { }` | Rutas protegidas por bloque |
| WebSocket de Jakarta | `webSocket("/prices") { … }` | Una sesión es una corrutina |
| `application.properties` | `application.conf` / YAML o configuración en código | `EngineMain` frente a `embeddedServer` |

**Trampas:**
- Un handler que bloquea (JDBC sin `withContext(Dispatchers.IO)`) frena a todos los que comparten el hilo.
- `call.receive` se puede llamar una sola vez: el cuerpo ya se consumió en la segunda.
- Sin `StatusPages`, una excepción en una ruta sale como 500 sin cuerpo útil.
- El orden en que se instalan los plugins importa (por ejemplo, autenticación antes de las rutas que la usan).
- Una sesión de WebSocket que termina deja corriendo las corrutinas que lanzó si no se lanzaron en su scope.

**Caso:** el servidor de precios del Mercado: REST para cotizaciones y un WebSocket donde cada cliente se suscribe a
tickers y recibe precios y alertas.

**🎤 Entrevista:** Ktor frente a Spring Boot · ¿Cómo funciona un plugin de Ktor? · ¿Cómo manejas un cliente WebSocket
lento?

**⚡ Precios y alertas por WebSocket:** el "chat" del plan con los datos del caso: suscripción por ticker, difusión con
`SharedFlow` y alertas de precio · **Se verifica con:** pruebas con `testApplication` de que cada cliente recibe solo
sus tickers, de que un cliente lento no frena a los demás (último valor) y de que al desconectarse no quedan
corrutinas vivas.

### 10 · `client` — Cliente HTTP de Ktor

**Objetivo:** consumir APIs HTTP con el cliente de Ktor (en la JVM y en multiplataforma), con serialización,
reintentos y tiempos límite.
**Sección:** `10-ktor` · **Build:** nivel 3 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (Ktor client ⚠️) | Nota |
|---|---|---|
| `java.net.http.HttpClient` | `HttpClient(CIO)` (o `Java`, `OkHttp`, `Darwin` en iOS) | El motor cambia por plataforma |
| `HttpResponse.BodyHandlers` + Jackson | `client.get(url).body<Quote>()` | `ContentNegotiation` con kotlinx-serialization |
| Reintentos a mano, Resilience4j | `install(HttpRequestRetry) { retryOnServerErrors(3); exponentialDelay() }` | |
| `HttpRequest.timeout` | `install(HttpTimeout) { requestTimeoutMillis = … }` | |
| Excepción en 4xx/5xx según la librería | `expectSuccess = true` | Por defecto **no** lanza en errores HTTP |

**Trampas:**
- Crear un `HttpClient` por pedido abre conexiones y hilos que no se reutilizan: se crea uno y se cierra al final.
- Por defecto un 404 o un 500 no lanza: sin `expectSuccess` o sin mirar el estado, se deserializa un cuerpo de error.
- Reintentar un `POST` sin clave de idempotencia puede duplicar una transferencia.
- Cada motor soporta cosas distintas (WebSockets, HTTP/2, proxies): el que funciona en la JVM puede no estar en iOS.
- Un tiempo límite muy alto con reintentos multiplica la espera total.

**Caso:** el cliente de cotizaciones del Mercado contra el servidor de `server`, y un cliente de la API de
transferencias del Banco con clave de idempotencia en los reintentos.

**🎤 Entrevista:** ¿por qué se reutiliza el `HttpClient`? · ¿Qué reintentarías y qué no? · ¿Cómo elige Ktor el motor
en multiplataforma?

**⚡ Cliente resistente de cotizaciones:** reintentos con espera exponencial, tiempo límite y un corte tras N fallas
seguidas · **Se verifica con:** pruebas con `MockEngine` que cuentan los intentos, verifican que el tiempo límite salta
y que un `POST` reintentado lleva siempre la misma clave de idempotencia.

### 10 · `ktortesting` — Pruebas de Ktor

**Objetivo:** probar un servidor Ktor sin abrir puertos y un cliente sin red, de forma rápida y determinista.
**Sección:** `10-ktor` · **Build:** nivel 3 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (ktor-server-test-host ⚠️) | Nota |
|---|---|---|
| Levantar el servidor en un puerto y llamarlo | `testApplication { client.get("/quotes/ACME") }` | Sin red: el cliente habla en memoria |
| WireMock para el cliente | `MockEngine { request -> respond(…) }` | Respuestas por pedido, sin servidor |
| Pruebas de WebSocket a mano | `client.webSocket("/prices") { … }` dentro de `testApplication` | |
| Configuración de pruebas | `environment { config = … }` en `testApplication` | |

**Trampas:**
- `testApplication` carga la configuración del módulo: si el módulo lee `application.conf`, la prueba también.
- Un cliente de la prueba sin `ContentNegotiation` recibe el JSON como texto y la aserción compara mal.
- Las pruebas de WebSocket que no cierran la sesión dejan la prueba colgada.
- `MockEngine` responde lo que se le dice: no prueba que el servidor real responda así (eso lo cubre una prueba de
  contrato).
- Mezclar tiempo real (`delay` del servidor) en `testApplication` hace lentas las pruebas.

**Caso:** el servidor de precios y su cliente del Mercado probados entre sí.

**🎤 Entrevista:** ¿cómo pruebas un servidor Ktor sin abrir un puerto? · `MockEngine` frente a WireMock · ¿Cómo pruebas
un WebSocket?

**⚡ Contrato servidor-cliente:** el servidor de `server` y el cliente de `client` probados juntos en
`testApplication`, con el modelo serializable compartido · **Se verifica con:** la suite completa sin red ni puertos,
en menos de unos segundos, y una prueba que rompe el contrato a propósito (un campo renombrado) y falla en los dos
lados.

---

## 11-multiplatform

Kotlin Multiplatform y Compose. **Build:** 4 · **Caso:** Ambos.

### 11 · `kmp` — `expect`/`actual`, source sets, targets

**Objetivo:** compartir código Kotlin entre JVM, Android, iOS y web con Kotlin Multiplatform: *source sets*,
`expect`/`actual` y qué de la JVM no existe en el código común.
**Sección:** `11-multiplatform` · **Build:** nivel 4 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin Multiplatform (plugin `kotlin("multiplatform")` 2.4.21) | Nota |
|---|---|---|
| Un módulo por plataforma con interfaces | *Source sets*: `commonMain`, `jvmMain`, `iosMain`, `wasmJsMain` | La jerarquía por defecto los arma sola |
| `ServiceLoader` para la implementación por plataforma | `expect fun platformName(): String` + `actual` en cada plataforma | Las funciones `expect` son estables |
| Clase abstracta con una implementación por plataforma | `expect class` / `actual class` | Medido: en Beta todavía en 2.4.21 (aviso; `-Xexpect-actual-classes`) |
| `java.math.BigDecimal`, `java.time` | No existen en `commonMain` | Montos en centavos `Long` o una librería decimal multiplataforma ⚠️ |
| `java.time.Instant` | `kotlin.time.Instant` `[2.3]`, kotlinx-datetime ⚠️ | |
| Librería Java para iOS (no hay) | Framework de Kotlin/Native para Swift | `suspend` llega como *completion handler* o `async` |

**Trampas:**
- El `Money` de `01-language` usa `java.math.BigDecimal`: no compila en `commonMain`. El dominio común necesita otra
  representación (centavos `Long`) o una librería.
- Preferir interfaces e inyección a `expect class`: las clases `expect` siguen en Beta y atan la API a cada plataforma.
- Desde Swift, una `sealed class` de Kotlin no es un `enum` exhaustivo, y los genéricos se pierden en parte.
- Una prueba en `commonTest` corre en cada target: puede pasar en la JVM y fallar en iOS por una diferencia de
  plataforma (formato de números, zona horaria).
- La sincronización de Gradle de un proyecto multiplataforma es lenta: conviene compilar solo los targets que se usan.

**Caso:** el dominio común del Banco y el Mercado (`Tick`, `OrderBook`, montos en centavos, validación de cuentas
`ACC-…`) en `commonMain`, usado por las apps de `composemp` y de `12-android`.

**🎤 Entrevista:** ¿qué comparte KMP y qué no? · `expect`/`actual` frente a interfaces · ¿Cómo se ve una función
`suspend` desde Swift?

**⚡ Dominio común en cuatro plataformas:** el dominio de los casos en `commonMain` con sus pruebas en `commonTest` ·
**Se verifica con:** las pruebas en verde en `jvm`, `iosSimulatorArm64`, `js` y `wasmJs`, y una comprobación que falla
si `commonMain` importa algo de `java.*`.

### 11 · `composemp` — Compose Multiplatform: desktop, wasm, iOS

**Objetivo:** construir una interfaz con Compose Multiplatform que corre en escritorio, iOS y navegador, y entender
el modelo de estado y recomposición.
**Sección:** `11-multiplatform` · **Build:** nivel 4 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (Compose Multiplatform ⚠️ se fija al escribir la sección) | Nota |
|---|---|---|
| Swing, JavaFX | `@Composable fun Portfolio(state: PortfolioState)` | UI como función del estado |
| `PropertyChangeListener` y repintar | `mutableStateOf`, `remember`, recomposición | Compose vuelve a dibujar solo lo que leyó el estado |
| Una app por plataforma | Escritorio (JVM), iOS y web (Wasm) desde el mismo código | Madurez distinta por plataforma ⚠️ |
| `ResourceBundle`, recursos del classpath | Recursos de Compose (`Res.string.…`) | Generados y tipados |
| Pruebas de Swing con robots | `runComposeUiTest { onNodeWithText(…) }` | Pruebas de UI en la JVM |

**Trampas:**
- Leer un estado en un scope más amplio que el necesario recompone de más (toda la lista en vez de una fila).
- Un efecto lateral (red, log) dentro de una función `@Composable` corre en cada recomposición: va en
  `LaunchedEffect`.
- `remember` se pierde al rotar o al recrear la pantalla; para eso existe `rememberSaveable`.
- Parámetros inestables (una `List` mutable, una clase sin `@Immutable`) impiden saltear recomposiciones.
- La versión web (Wasm) necesita un navegador con soporte de GC de Wasm: no todos los navegadores viejos sirven.

**Caso:** el tablero del portafolio del Mercado (posiciones, último precio, P&L) en escritorio, iOS y navegador.

**🎤 Entrevista:** ¿qué es la recomposición? · `remember` frente a `rememberSaveable` · ¿Qué hace estable a un
parámetro?

**⚡ Tablero de portafolio en tres plataformas:** la misma UI de Compose en escritorio, iOS y Wasm, alimentada por un
`StateFlow` de precios · **Se verifica con:** pruebas de UI con `runComposeUiTest` (filas, orden, resaltado al cambiar
el precio) y los tres targets compilando en una tarea de verificación.

### 11 · `shared` — Ktor client + SQLDelight + kotlinx-datetime

**Objetivo:** armar el módulo compartido de una app real: red con el cliente de Ktor, base local con SQLDelight y
fechas con kotlinx-datetime, todo en código común.
**Sección:** `11-multiplatform` · **Build:** nivel 4 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (Ktor client, SQLDelight, kotlinx-datetime ⚠️) | Nota |
|---|---|---|
| `java.net.http.HttpClient` | `HttpClient` de Ktor con un motor por plataforma (`10 · client`) | CIO o OkHttp en Android, Darwin en iOS |
| JDBC / Room | SQLDelight con un driver por plataforma (`04 · sqldelight`) | El mismo `.sq` en todas |
| `java.time` | kotlinx-datetime / `kotlin.time.Instant` | El día de mercado en `America/New_York` también en iOS |
| Inyección con Spring | Koin u objetos armados a mano ⚠️ | Sin reflexión en Kotlin/Native |
| Repositorio que oculta red y base | `class PortfolioRepository(api, db)` en `commonMain` | La lógica no sabe de plataformas |

**Trampas:**
- Cada plataforma necesita su motor de Ktor y su driver de SQLDelight: olvidar uno compila en la JVM y falla al
  enlazar en iOS.
- Las zonas horarias dependen de la base de datos de zonas de cada sistema: probar el horario de verano en todas las
  plataformas.
- Las corrutinas en iOS: lo que se expone a Swift tiene que reanudar en el hilo principal para tocar la UI.
- Un `Flow` de SQLDelight se vuelve a emitir con cualquier cambio de la tabla: hace falta `distinctUntilChanged`.
- Compartir demasiado (también la UI y la navegación) puede costar más que lo que ahorra.

**Caso:** el saldo y los movimientos de la cuenta del Banco y el portafolio con precios del Mercado, con caché local.

**🎤 Entrevista:** ¿qué pondrías en el módulo compartido y qué no? · ¿Cómo manejas la red y la base en KMP? · ¿Qué
cambia con las fechas en multiplataforma?

**⚡ Módulo compartido consumido desde escritorio y web:** el módulo con red, base y fechas usado por una app de
escritorio y una web (Wasm) · **Se verifica con:** pruebas del módulo con `MockEngine` y SQLDelight en memoria, y las
dos apps compilando y mostrando los mismos datos de una fuente falsa.

### 11 · `web` — Kotlin/JS y Wasm

**Objetivo:** llevar Kotlin al navegador con Kotlin/JS y Kotlin/Wasm, interoperar con JavaScript y medir qué cuesta
cada uno.
**Sección:** `11-multiplatform` · **Build:** nivel 4 · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (targets `js` y `wasmJs` de 2.4.21 ⚠️ estado de Wasm) | Nota |
|---|---|---|
| GWT, TeaVM, J2CL | Kotlin/JS (compilador IR) | Genera JavaScript; usa paquetes npm |
| — | Kotlin/Wasm (`wasmJs`) | WebAssembly con GC del navegador |
| Llamar a JavaScript desde Java (no hay) | `external fun`, `external class` | Declaraciones de APIs de JS |
| Exponer Java a JavaScript (no hay) | `@JsExport` | Lo que JavaScript puede llamar |
| Tipado dinámico | `dynamic` (solo JS) | Sin comprobación del compilador |

**Trampas:**
- `Long` en Kotlin/JS no es un `number` de JavaScript: se emula y es lento; los precios en centavos conviene tenerlos
  como `Int` o `Double` en la frontera.
- `@JsExport` no exporta todo (genéricos, `suspend`, algunas colecciones): la API exportada se diseña aparte.
- `dynamic` apaga el sistema de tipos: un error de nombre aparece en el navegador, no al compilar.
- El tamaño del bundle crece con la stdlib y las dependencias: medirlo y activar la eliminación de código muerto.
- Wasm necesita navegadores recientes; la versión JS llega a más navegadores.

**Caso:** el motor de comisiones del Mercado (tramos de `control`) corriendo en el navegador.

**🎤 Entrevista:** Kotlin/JS frente a Kotlin/Wasm · ¿Cómo llamas a una librería de npm desde Kotlin? · ¿Qué cuesta un
`Long` en JS?

**⚡ Calculadora de comisiones en el navegador:** el mismo motor compilado a JS y a Wasm, con una API `@JsExport` ·
**Se verifica con:** pruebas que corren en los dos targets, una prueba en JavaScript que llama a la API exportada, y
una tabla con el tamaño del bundle de cada uno.

---

## 12-android

Android. **Build:** 4 (fuera del composite) · **Caso:** Ambos.

### 12 · `android` — Activity, ciclo de vida

**Objetivo:** entender las piezas base de una app Android (Activity, ciclo de vida, `Context`) y los problemas que
vienen de ellas: rotaciones, muerte del proceso y trabajo en el hilo principal.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (Android Gradle Plugin ⚠️; Android SDK instalado) | Nota |
|---|---|---|
| `main` y un proceso que vive lo que uno quiere | `ComponentActivity` con `onCreate`, `onStart`, `onStop`… | El sistema decide cuándo crear y destruir |
| Estado en campos | `SavedStateHandle`, `rememberSaveable` | Sobrevive a la rotación y a la muerte del proceso |
| Hilos y `ExecutorService` | `lifecycleScope`, `viewModelScope` | Corrutinas atadas al ciclo de vida |
| `System.getProperty`, recursos del classpath | `Context`, `R.string.…` | El `Context` da acceso a casi todo |
| Permisos del sistema operativo (no hay) | `AndroidManifest.xml` + permisos en runtime | |

**Trampas:**
- Rotar la pantalla destruye y recrea la `Activity`: lo que estaba en sus campos se pierde.
- Guardar una `Activity` (o su `Context`) en un singleton la retiene en memoria después de destruida (fuga).
- Trabajo largo en el hilo principal congela la UI y, a los pocos segundos, el sistema muestra un ANR.
- El proceso puede morir en segundo plano: al volver, Android recrea la pantalla pero no la memoria.
- El Android Gradle Plugin fija su propia versión de Gradle: por eso `12-android` vive fuera del composite.

**Caso:** la app del cliente: saldo y movimientos de la cuenta (Banco), portafolio y precios (Mercado), con el
módulo compartido de `11 · shared`.

**🎤 Entrevista:** ¿qué pasa al rotar la pantalla? · ¿Cómo sobrevive el estado a la muerte del proceso? · ¿Por qué no
guardar un `Context` en un singleton?

**⚡ Pantalla a prueba de rotación:** la pantalla de transferencia con el formulario a medio llenar · **Se verifica
con:** pruebas (Robolectric o en el emulador) que rotan y simulan la muerte del proceso, y comprueban que el
formulario y el ticker elegido sobreviven.

### 12 · `compose` — Jetpack Compose

**Objetivo:** construir pantallas con Jetpack Compose: estado elevado, listas perezosas, modificadores y efectos, sin
caer en recomposiciones de más.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Mercado · **Estado:** nueva

| Java 21 | Kotlin (Jetpack Compose ⚠️) | Nota |
|---|---|---|
| Layouts XML + `findViewById` | `@Composable fun TickerRow(tick: Tick, onClick: () -> Unit)` | UI declarativa en Kotlin |
| `RecyclerView` + `Adapter` | `LazyColumn { items(ticks, key = { it.ticker }) { … } }` | La `key` evita recomponer y animar de más |
| Estado en la `View` | Estado elevado (*state hoisting*) | La función recibe el estado y avisa los eventos |
| `AsyncTask`, callbacks | `LaunchedEffect(key) { … }` | Efectos atados a la composición |
| Estilos XML | `MaterialTheme` y `Modifier` | El orden de los modificadores importa |

**Trampas:**
- El orden de los `Modifier` cambia el resultado: `padding` antes o después de `background` no es lo mismo.
- Sin `key` en un `LazyColumn`, reordenar la lista recompone todas las filas y pierde su estado.
- Leer un `State` en la función padre en vez de en la fila hace que cambie toda la pantalla por un precio.
- `LaunchedEffect(Unit)` corre una sola vez: si depende de un parámetro, ese parámetro va como clave.
- Calcular algo caro dentro de la composición lo repite en cada recomposición: `remember(key) { … }`.

**Caso:** la lista de tickers con precio en vivo y el detalle con la vela del día (Mercado).

**🎤 Entrevista:** ¿qué es *state hoisting*? · ¿Para qué sirve la `key` en una lista perezosa? · ¿Cuándo usas
`LaunchedEffect`?

**⚡ Pantalla de precios en Compose:** lista de tickers con precio que se actualiza, resaltado al subir o bajar y
detalle con la vela · **Se verifica con:** pruebas de UI de Compose (orden, resaltado, navegación al detalle) y un
contador de recomposiciones que muestra que al cambiar un precio solo se recompone su fila.

### 12 · `architecture` — ViewModel, `StateFlow`, navegación

**Objetivo:** organizar una app Android con flujo de datos unidireccional: `ViewModel` con `StateFlow`, eventos de UI y
navegación, sin fugas ni estado duplicado.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (AndroidX Lifecycle, Navigation ⚠️) | Nota |
|---|---|---|
| Presentador con referencia a la vista (MVP) | `ViewModel` que expone `StateFlow<UiState>` | La vista observa; el `ViewModel` no conoce la vista |
| `LiveData` | `StateFlow` + `collectAsStateWithLifecycle()` | Deja de recolectar cuando la pantalla no se ve |
| Callbacks de la vista | Eventos como funciones (`onTransfer(amount)`) | Flujo unidireccional |
| `Intent` con extras | Navigation Compose con rutas tipadas | Argumentos serializables |
| Singletons de estado | Estado en el `ViewModel`, datos en repositorios | Una sola fuente de verdad |

**Trampas:**
- Exponer un `MutableStateFlow` deja que la vista cambie el estado por su cuenta: se expone `StateFlow`.
- `collectAsState()` sin ciclo de vida sigue recolectando en segundo plano y gasta batería.
- Un `ViewModel` que guarda un `Context` de `Activity` la retiene (fuga): si hace falta, `Application`.
- Un evento de una sola vez (mostrar un mensaje) guardado como estado se repite al rotar.
- Pasar objetos grandes como argumentos de navegación supera el límite del `Bundle`.

**Caso:** el flujo de transferencia del Banco: formulario, validación (las reglas de `exceptions`), confirmación y
resultado, contra el módulo compartido.

**🎤 Entrevista:** ¿qué es flujo unidireccional? · `StateFlow` frente a `LiveData` · ¿Cómo manejas un evento de una
sola vez?

**⚡ Flujo de transferencia con UDF:** `ViewModel`, estados (`Editing`, `Confirming`, `Done`, `Failed`) y navegación ·
**Se verifica con:** pruebas del `ViewModel` con `runTest` y Turbine (la secuencia exacta de estados, también ante un
error de red) y una prueba de navegación.

### 12 · `androiddata` — Room, DataStore

**Objetivo:** guardar datos locales en Android con Room y DataStore, y hacer de la base local la fuente de verdad de
una app que funciona sin red.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Room, DataStore ⚠️) | Nota |
|---|---|---|
| `SQLiteOpenHelper` | Room: `@Entity`, `@Dao` con `suspend` y `Flow` | Generado con KSP (`06 · ksp`) |
| `SharedPreferences` | DataStore (Preferences o con tipo) | Asíncrono, con `Flow` |
| Migraciones a mano | `@Database(version = …)` + migraciones probadas | Esquema exportado a JSON |
| Caché de red ad hoc | Base local como fuente de verdad + sincronización | *Offline-first* |

**Trampas:**
- Una consulta de Room en el hilo principal lanza una excepción: tiene que ser `suspend` o devolver `Flow`.
- Subir la versión de la base sin migración borra los datos (o falla), según la configuración.
- Dos instancias de DataStore sobre el mismo archivo lanzan una excepción: una por archivo, como singleton.
- Room con kapt en vez de KSP hace mucho más lento el build.
- Un `Flow` de Room emite ante cualquier cambio de la tabla: hace falta `distinctUntilChanged` en la UI.

**Caso:** los movimientos de la cuenta del Banco guardados en Room y las transferencias hechas sin red, en cola hasta
sincronizar.

**🎤 Entrevista:** Room frente a SQLDelight · ¿Qué es una app *offline-first*? · ¿Cómo pruebas una migración de Room?

**⚡ App offline-first con sincronización:** movimientos y transferencias pendientes en Room como fuente de verdad, y
sincronización con el servidor cuando hay red · **Se verifica con:** pruebas de que una transferencia hecha sin red
queda pendiente y se envía una sola vez al volver la red (servidor falso), una regla de conflicto probada, y una
migración de esquema con datos.

### 12 · `di` — Hilt frente a Koin

**Objetivo:** inyectar dependencias en una app Android con Hilt (en compilación) y con Koin (en runtime), y elegir
según lo que cuesta cada uno.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (Hilt, Koin ⚠️) | Nota |
|---|---|---|
| Dagger | Hilt (`@HiltAndroidApp`, `@Inject constructor`, `@Module`) | El grafo se valida al compilar |
| Spring con escaneo | Koin: `module { single { Ledger(get()) } }` | DSL en runtime; sin generación de código |
| `@Singleton`, scopes de Dagger | Scopes de Hilt atados a componentes de Android | `ViewModelScoped`, `ActivityRetained`… |
| Reemplazos en pruebas | `@TestInstallIn` (Hilt), `loadKoinModules` (Koin) | |

**Trampas:**
- Una dependencia que falta en Koin explota al ejecutar, no al compilar.
- Hilt con kapt alarga mucho el build; con KSP mejora ⚠️.
- Un scope equivocado (algo de pantalla como singleton) retiene objetos que deberían morir con la pantalla.
- Inyectar un `Context` de `Activity` en algo de vida más larga es una fuga.
- Para una app chica, armar el grafo a mano puede ser más claro que cualquier framework.

**Caso:** el grafo de la app del cliente: repositorios del módulo compartido, cliente de red, base local y
`ViewModel`.

**🎤 Entrevista:** Hilt frente a Koin · ¿Qué valida Hilt al compilar? · ¿Cuándo no usarías un framework de inyección?

**⚡ La misma app con Hilt y con Koin:** las dos variantes del grafo para la misma app · **Se verifica con:** las mismas
pruebas en las dos, una dependencia faltante que en Hilt rompe la compilación y en Koin falla una prueba en runtime, y
una tabla con el tiempo de build y de arranque de cada una.

### 12 · `background` — WorkManager

**Objetivo:** hacer trabajo en segundo plano confiable en Android con WorkManager (restricciones, reintentos,
periodicidad) dentro de los límites del sistema.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Banco · **Estado:** nueva

| Java 21 | Kotlin (WorkManager ⚠️) | Nota |
|---|---|---|
| `ScheduledExecutorService` | `PeriodicWorkRequestBuilder<SyncWorker>(15, MINUTES)` | Sobrevive al reinicio del dispositivo |
| Hilo con reintentos a mano | `CoroutineWorker` + `Result.retry()` + `setBackoffCriteria` | |
| Condiciones de red, batería | `Constraints(requiredNetworkType = CONNECTED)` | Corre cuando se cumplen |
| Tarea única | `enqueueUniqueWork(name, KEEP, …)` | Evita duplicados |

**Trampas:**
- El trabajo periódico tiene un mínimo de 15 minutos: no sirve para "cada minuto".
- El modo de ahorro de energía (Doze) y las restricciones de algunos fabricantes demoran el trabajo.
- Sin nombre único, cada arranque de la app encola otra copia del mismo trabajo.
- Un `Worker` que lanza una excepción se da por fallado, no se reintenta: hay que devolver `Result.retry()`.
- El trabajo corre fuera de la pantalla: no puede depender de un `ViewModel`.

**Caso:** la sincronización de transferencias pendientes del Banco y el refresco de precios del Mercado.

**🎤 Entrevista:** WorkManager frente a un servicio en primer plano · ¿Cómo evitas trabajos duplicados? · ¿Qué
limita el trabajo en segundo plano en Android?

**⚡ Sincronización periódica confiable:** el `SyncWorker` de las transferencias pendientes con restricciones,
reintentos y nombre único · **Se verifica con:** pruebas con las herramientas de prueba de WorkManager (restricciones
simuladas, reintento con espera, trabajo único que no se duplica) y la integración con el ⚡ de `androiddata`.

### 12 · `androidtesting` — Pruebas de Compose, Robolectric

**Objetivo:** probar una app Android en la JVM (unidad y Robolectric), con pruebas de UI de Compose y en el emulador,
sabiendo qué atrapa cada nivel.
**Sección:** `12-android` · **Build:** nivel 4 (fuera del composite) · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin (AndroidX Test, Robolectric, Compose UI Test ⚠️) | Nota |
|---|---|---|
| JUnit en la JVM | Pruebas de unidad de `ViewModel` y repositorios | Con `Dispatchers.setMain(StandardTestDispatcher())` |
| Pruebas de Android en el dispositivo | Robolectric (en la JVM) | Rápidas, pero no es un dispositivo real |
| Espresso | `createComposeRule()`, `onNodeWithText`, `testTag` | Pruebas de UI de Compose |
| Pruebas instrumentadas | `connectedAndroidTest` en el emulador | Las más lentas y las más reales |

**Trampas:**
- Sin reemplazar `Dispatchers.Main`, una prueba de `ViewModel` en la JVM falla porque no hay hilo principal.
- Esperas con `Thread.sleep` en pruebas de UI dan pruebas que fallan de vez en cuando: Compose espera solo a que la UI
  quede quieta.
- Robolectric simula Android: algo que pasa en Robolectric puede fallar en un dispositivo (y al revés).
- Buscar nodos por texto rompe las pruebas al traducir la app: `testTag` para lo estructural.
- El emulador en CI necesita aceleración por hardware; sin ella, las pruebas instrumentadas tardan muchísimo.

**Caso:** la app del cliente (saldo, transferencia, precios) probada en todos los niveles.

**🎤 Entrevista:** ¿qué pruebas en la JVM y qué en el dispositivo? · Robolectric frente a pruebas instrumentadas ·
¿Cómo pruebas una pantalla de Compose?

**⚡ Pirámide de pruebas de la app:** pruebas de unidad, Robolectric, UI de Compose e instrumentadas para la app del
cliente · **Se verifica con:** una tabla con la cantidad y el tiempo de cada nivel, y cuatro errores sembrados, uno
por nivel, cada uno atrapado por el nivel que corresponde.

---

## 20-legacy-kotlin14

Legacy: Android clásico y Boot 2.3–2.4 (Kotlin 1.4.32). **Build:** 6 · **Caso:** —.

### 20 · `language14` — Lo que no existía todavía

**Objetivo:** leer y mantener código Kotlin 1.4 (Android de 2019–2021, Spring Boot 2.3–2.4) sabiendo qué del Kotlin
actual todavía no existía y qué se escribía distinto.
**Sección:** `20-legacy-kotlin14` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Kotlin 2.4 (base) | Kotlin 1.4.32 | Nota |
|---|---|---|
| `sealed interface`, `@JvmInline value class` | No existen (`inline class` experimental) | `[1.5]` en la referencia |
| `fun f(): Result<T>` | No compila como tipo de retorno | `[1.5]` |
| `lowercase()`, `Char.code` | Experimentales; se usa `toLowerCase()` y `toInt()` | `toLowerCase()` es error desde 2.1 |
| `buildList { }`, `kotlin.time.Duration` | Experimentales | `[1.6]` |
| `data object`, `entries`, `..<` | No existen | `[1.9]` |
| `kotlin.io.path.readText()` | Experimental (y en `kotlin-stdlib-jdk7`) | `[1.5]` |
| `jvmToolchain(21)` | No existe (llegó en 1.5.30): el JDK lo pone el entorno | El contenedor `k14` usa Temurin 11 |

**Trampas:**
- El `jvmTarget` por defecto de 1.4 es 1.6: sin cambiarlo, el bytecode no usa métodos `default` ni `invokedynamic`.
- Código que compila en 1.4 puede no compilar hoy (`toLowerCase()`, `capitalize()`): la migración no es solo subir el
  número.
- El backend JVM de 1.4 no es el IR: el bytecode (y los nombres sintéticos) difieren del de las versiones nuevas.
- `kotlin-test-junit5` para 1.4 puede no existir: el track usaría JUnit 4 ⚠️.
- Un IntelliJ actual puede no importar bien Gradle 6.8: el track se trabaja por CLI y contenedor ⚠️.

**Caso:** — (código de la época: una mini app de cotizaciones escrita como en 2020).

**🎤 Entrevista:** ¿qué cambió en Kotlin desde 1.4? · ¿Qué cuidarías al tocar un servicio en Kotlin 1.4? · ¿Por qué
`toLowerCase()` ya no compila?

**⚡ Inventario de lo que no existía:** cada construcción de la referencia con etiqueta `[1.5]` o posterior escrita
como se escribía en 1.4 · **Se verifica con:** las pruebas del track en verde con Kotlin 1.4.32 en el contenedor
`k14`, y `scripts/version-probes.sh` confirmando que la versión moderna de cada ejemplo no compila en 1.4.

### 20 · `coroutines14` — `BroadcastChannel`, `ConflatedBroadcastChannel`

**Objetivo:** reconocer las APIs de corrutinas de la época de 1.4 (`BroadcastChannel`, `ConflatedBroadcastChannel`) y
reemplazarlas por `SharedFlow` y `StateFlow` sin cambiar el comportamiento.
**Sección:** `20-legacy-kotlin14` · **Build:** nivel 6 · **Caso:** Mercado · **Estado:** nueva

| Kotlin 2.4 (kotlinx-coroutines 1.11.0) | Kotlin 1.4.32 (kotlinx-coroutines de la época ⚠️) | Nota |
|---|---|---|
| `MutableSharedFlow(replay, extraBufferCapacity)` | `BroadcastChannel(capacity)` + `openSubscription()` | Medido: en 1.11.0 es deprecación con **error** |
| `MutableStateFlow(valor)` | `ConflatedBroadcastChannel(valor)` + `.value` | Medido: deprecación con error en 1.11.0 |
| `stateIn` / `shareIn` | `broadcastIn` | |
| `channelFlow`, `callbackFlow` | Los mismos (ya existían) | |
| `Flow` estable | `Flow` estable desde la 1.3.x | Los operadores con opt-in cambiaron mucho desde entonces |

**Trampas:**
- Una suscripción de `BroadcastChannel` (`openSubscription()`) que no se cancela queda abierta y retiene memoria.
- `ConflatedBroadcastChannel` y `StateFlow` no son iguales: `StateFlow` descarta valores repetidos (`equals`), el canal
  no.
- Al migrar a `SharedFlow` hay que decidir `replay` y el buffer: con los valores por defecto se pierden emisiones sin
  suscriptores.
- El código de la época compila contra su versión de la librería; con la actual, ni siquiera compila (error).
- Mezclar el `actor` de entonces con `SharedFlow` duplica el estado.

**Caso:** el último precio de cada ticker y las alertas del Mercado, escritos como en 2020 y migrados.

**🎤 Entrevista:** ¿por qué se deprecó `BroadcastChannel`? · `ConflatedBroadcastChannel` frente a `StateFlow` · ¿Qué
cuidarías al migrar a `SharedFlow`?

**⚡ Migración del feed de la época:** el tablero de precios con `BroadcastChannel` y `ConflatedBroadcastChannel`
migrado a `SharedFlow` y `StateFlow` · **Se verifica con:** la misma batería de pruebas (en tiempo virtual) en verde
antes (track 14) y después (base 2.4), incluida la diferencia de valores repetidos documentada en una prueba.

### 20 · `android14` — Synthetics y kapt (solo lectura y demo JVM)

**Objetivo:** reconocer en código Android viejo los *synthetics* de `kotlin-android-extensions` y kapt, y saber por qué
se reemplazaron (ViewBinding, KSP), sin necesidad de compilar Android en el track.
**Sección:** `20-legacy-kotlin14` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Kotlin 2.4 (base) | Kotlin 1.4.32 | Nota |
|---|---|---|
| ViewBinding o Compose | Synthetics: `import kotlinx.android.synthetic.main.activity_main.*` | El plugin `kotlin-android-extensions` se deprecó y luego se quitó ⚠️ |
| KSP | kapt | kapt genera stubs Java de todo el módulo (`06 · ksp`) |
| `kotlin-parcelize` | `@Parcelize` dentro de `kotlin-android-extensions` | Cambió de plugin |
| Corrutinas con `lifecycleScope` | `AsyncTask` o RxJava (`03 · rxjava`) | |

**Trampas:**
- Los synthetics en un `Fragment` devuelven una vista nula después de `onDestroyView`: un NPE frecuente en esas apps.
- Los synthetics de todos los layouts comparten un espacio de nombres: dos vistas con el mismo id en layouts distintos
  se confunden.
- kapt hace lento el build y no entiende bien algunos tipos de Kotlin.
- Lo que el track muestra es **lectura** de código y una demo en la JVM: sin el plugin de Android no se compila una app.
- Migrar synthetics a ViewBinding toca cada pantalla: conviene hacerlo por pantalla, no de golpe.

**Caso:** — (fragmentos de una app vieja de saldo, como se escribían en 2020).

**🎤 Entrevista:** ¿qué eran los synthetics y por qué se quitaron? · kapt frente a KSP · ¿Cómo migrarías una app con
synthetics?

**⚡ Guía de migración de una pantalla:** una pantalla con synthetics y kapt, su versión con ViewBinding y KSP, y la
explicación del cambio · **Se verifica con:** la versión nueva compilando en `12-android` y una prueba de Robolectric que
pasa con las dos versiones de la pantalla.

### 20 · `migration14` — De 1.4 a 1.6

**Objetivo:** subir un proyecto de Kotlin 1.4 a 1.6 (y de ahí a 2.x) por saltos chicos, sabiendo qué rompe cada salto.
**Sección:** `20-legacy-kotlin14` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Kotlin 2.4 (base) | Kotlin 1.4 → 1.6 | Nota |
|---|---|---|
| Backend JVM IR | 1.5 lo hace el backend por defecto | Bytecode distinto; algunos plugins viejos fallan |
| `when` sobre `sealed` o `enum` exhaustivo como sentencia | Aviso en 1.6, error desde 1.7 | Medido en la referencia |
| `-opt-in=…` | `-Xopt-in=…` | El opt-in se estabilizó en el camino |
| `lowercase()`, `uppercase()` | `toLowerCase()` se depreca en 1.5 | Error desde 2.1 |
| `kotlin.io.path` en la stdlib | En `kotlin-stdlib-jdk7` hasta 1.7 | Ojo con las dependencias |

**Trampas:**
- Saltar de 1.4 a 2.4 de una vez mezcla todos los cambios: un error ya no dice de qué salto viene.
- Subir Kotlin sin subir las librerías (coroutines, serialization) da errores de metadata incompatible.
- El plugin de Gradle de cada versión pide un rango de Gradle: subir Kotlin puede obligar a subir Gradle.
- Las deprecaciones avisan en un salto y rompen en el siguiente: hay que limpiarlas en cada paso, no al final.
- Un `when` que era aviso en 1.6 es error en 1.7: código que "compilaba con avisos" deja de compilar.

**Caso:** — (la mini app de cotizaciones de `language14`).

**🎤 Entrevista:** ¿cómo migrarías un proyecto Kotlin viejo? · ¿Qué rompe más al subir de versión? · ¿Por qué saltos
chicos?

**⚡ Migración paso a paso:** la mini app de 1.4 subida a 1.6, 1.9 y 2.4, con un commit por salto de versión · **Se
verifica con:** las mismas pruebas en verde en cada commit (con el compilador real de cada versión, en su contenedor),
y un registro por salto con lo que se rompió y cómo se arregló.

---

## 21-legacy-kotlin16

Legacy: Spring Boot 2.7 con `javax` (Kotlin 1.6.21). **Build:** 6 · **Caso:** Banco (la API de `09-spring` portada).

### 21 · `language16` — Kotlin 1.6 frente a 2.4

**Objetivo:** trabajar en código Kotlin 1.6 (el de Spring Boot 2.7, todavía muy común) sabiendo qué falta respecto de la
base y qué cambió de comportamiento.
**Sección:** `21-legacy-kotlin16` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Kotlin 2.4 (base) | Kotlin 1.6.21 | Nota |
|---|---|---|
| `when` exhaustivo como sentencia: error | Aviso | `[1.7]` en la referencia |
| `min()`, `max()` no nulos | Ocultos; se usa `minOrNull()` | `[1.7]` |
| `T & Any` | Experimental | `[1.7]` |
| `data object`, `entries`, `..<` | No existen | `[1.9]` |
| K2 (smart casts, `invokedynamic`) | K1 | `[2.0]` |
| `buildList`, `Duration`, `readln()` | Recién estables | `[1.6]` |

**Trampas:**
- `xs.min()` no compila en 1.6 (estaba oculto): el código de la época usa `minOrNull()`, que hoy sigue valiendo.
- Un `when` incompleto que en 1.6 solo avisa rompe el build al subir a 1.7.
- Spring Boot 2.7 fija la versión de Kotlin por su gestión de dependencias: subir Kotlin a mano puede chocar con ella.
- Las inferencias de K1 y K2 difieren: código ambiguo en 1.6 puede elegir otra sobrecarga en 2.x.
- El track corre en JDK 17 (contenedor `k16`): algunas librerías de la época no corren en 21.

**Caso:** — (el código de `boot27`).

**🎤 Entrevista:** ¿qué le falta a Kotlin 1.6 que usarías hoy? · ¿Qué se rompe al subir de 1.6 a 1.7? · ¿Por qué
tanto código sigue en 1.6?

**⚡ Inventario de 1.6:** las construcciones `[1.7]` a `[2.4]` escritas como en 1.6 · **Se verifica con:** pruebas del
track en verde con Kotlin 1.6.21 en `k16`, y `version-probes.sh` confirmando que cada versión moderna no compila en
1.6.

### 21 · `boot27` — Spring Boot 2.7 con `javax`, WebFlux + corrutinas de la época

**Objetivo:** mantener una aplicación Spring Boot 2.7 en Kotlin 1.6 (`javax.*`, WebFlux con las corrutinas de la época),
que es lo que se encuentra en muchos servicios en producción.
**Sección:** `21-legacy-kotlin16` · **Build:** nivel 6 · **Caso:** Banco · **Estado:** nueva

| Kotlin 2.4 + Spring Boot 3 (base) | Kotlin 1.6.21 + Spring Boot 2.7 ⚠️ | Nota |
|---|---|---|
| `jakarta.persistence.*`, `jakarta.validation.*` | `javax.persistence.*`, `javax.validation.*` | El cambio de paquete es el corazón de la migración |
| `@ConfigurationProperties` con constructor, sin anotación extra | `@ConstructorBinding` necesario para las inmutables | |
| Hibernate 6 / 7 | Hibernate 5.6 | Otras APIs y otro dialecto |
| Spring Security 6 (`SecurityFilterChain`) | Spring Security 5.7 (`WebSecurityConfigurerAdapter` deprecado) | |
| Corrutinas actuales en WebFlux | Las de la época (`kotlinx-coroutines-reactor` viejo) | |

**Trampas:**
- Sin `@ConstructorBinding`, una `data class` de `@ConfigurationProperties` no se enlaza en Boot 2.x (falla al arrancar).
- Mezclar dependencias `jakarta` en una app Boot 2.7 da `ClassNotFoundException` en runtime, no al compilar.
- Las librerías nuevas ya no publican versiones para `javax`: actualizar una dependencia puede ser imposible sin migrar.
- Spring Boot 2.7 está fuera de soporte gratuito: los parches de seguridad son un motivo concreto para migrar ⚠️.
- La base de datos del track se accede desde el contenedor `k16`: la URL no es `localhost` sino el host de Docker ⚠️.

**Caso:** la API de transferencias del Banco de `09 · boot`, escrita para Boot 2.7.

**🎤 Entrevista:** ¿qué cambia entre Boot 2.7 y 3? · ¿Qué es `javax` → `jakarta` y por qué duele? · ¿Cómo mantienes un
servicio que no puedes migrar todavía?

**⚡ La API de transferencias en Boot 2.7:** la API de `09 · boot` con `javax`, `@ConstructorBinding` y Hibernate 5.6 ·
**Se verifica con:** las mismas pruebas de contrato de la API de `09-spring` en verde contra esta versión, en el
contenedor `k16` con PostgreSQL.

### 21 · `migration16` — Boot 2.7 → 3, `javax` → `jakarta`

**Objetivo:** migrar una aplicación de Spring Boot 2.7 (Kotlin 1.6, `javax`) a Boot 3 (Kotlin 2.x, `jakarta`) con un
plan, herramientas y pruebas que protejan el comportamiento.
**Sección:** `21-legacy-kotlin16` · **Build:** nivel 6 · **Caso:** Banco · **Estado:** nueva

| Boot 3 (destino) | Boot 2.7 (origen) | Nota |
|---|---|---|
| `jakarta.*` | `javax.*` | OpenRewrite automatiza buena parte del cambio ⚠️ |
| JDK 17 como mínimo | JDK 8 o 11 posibles | Subir el JDK antes que Spring |
| Kotlin moderno | Kotlin 1.6 | Boot 3 exige un Kotlin más nuevo ⚠️ |
| Hibernate 6+ | Hibernate 5.6 | Cambian consultas y tipos |
| `SecurityFilterChain` | `WebSecurityConfigurerAdapter` | |

**Trampas:**
- Migrar Spring, Kotlin, Hibernate y el JDK en un solo paso hace imposible saber qué rompió qué.
- Un reemplazo de texto `javax` → `jakarta` toca también `javax.crypto` o `javax.sql`, que **no** cambian.
- Hibernate 6 cambia el SQL generado y algunos tipos: las pruebas tienen que correr contra la base real, no H2.
- Las librerías sin versión `jakarta` bloquean la migración: hay que inventariarlas al principio.
- Sin pruebas de contrato antes de empezar, no hay forma de saber si la API sigue igual.

**Caso:** la API de transferencias del Banco de `boot27`, llevada de vuelta a la versión de `09 · boot`.

**🎤 Entrevista:** ¿cómo planificarías una migración a Boot 3? · ¿Qué no debe cambiar en `javax` → `jakarta`? · ¿Qué
pruebas pondrías antes de empezar?

**⚡ Ida y vuelta de la API:** la API de `09-spring` portada a Boot 2.7 (`boot27`) y migrada de nuevo a Boot 3 por
pasos · **Se verifica con:** las mismas pruebas de contrato en verde en Boot 2.7, en cada paso intermedio y en Boot 3,
con un commit por paso.

---

## 22-legacy-kotlin19

Legacy: el último K1 y la migración a K2 (Kotlin 1.9.22). **Build:** 6 · **Caso:** —.

### 22 · `language19` — Kotlin 1.9 frente a 2.4

**Objetivo:** trabajar en código Kotlin 1.9 (el último K1) sabiendo qué trae ya de lo moderno y qué cambia al pasar a
K2.
**Sección:** `22-legacy-kotlin19` · **Build:** nivel 6 · **Caso:** — · **Estado:** existe (track con prueba de humo)

| Kotlin 2.4 (base) | Kotlin 1.9.22 | Nota |
|---|---|---|
| `data object`, `entries`, `..<` | Ya existen | `[1.9]` |
| Guardas en `when`, `$$"…"`, `break` en lambdas inline | No existen | `[2.2]` |
| Smart cast con `x is B \|\| x is C` | No (K1: "unresolved reference") | `[2.0]`, medido |
| Lambdas con `invokedynamic` | Una clase por lambda | `Legacy19SmokeTest` lo fija |
| *Context parameters* | *Context receivers* experimentales | `[2.4]` y `[rm 2.4]` |
| `kotlinc` 2.4 con `-language-version 1.9` | — | Medido: rechazado; por eso el track usa el compilador real |

**Trampas:**
- El `toString()` de una lambda cambia al pasar a K2: el track lo usa a propósito en su prueba de humo.
- El plugin de Gradle de 1.9.22 usa APIs que Gradle 10 quita: el track se queda en Gradle 9.2.1.
- `kotlin-test-junit5` 1.9.22 en Gradle 9 necesita el BOM de JUnit y el launcher (gotcha de CLAUDE.md).
- Código que en 1.9 compila sin avisos puede dar avisos nuevos en K2 (`!!` innecesario por los smart casts nuevos).
- Los *context receivers* experimentales de 1.9 no compilan en 2.4.

**Caso:** — (el propio track y su prueba de humo).

**🎤 Entrevista:** ¿qué tiene 1.9 que no tenía 1.6? · ¿Qué cambia K2? · ¿Por qué un track con el compilador real y no
`-language-version`?

**⚡ Lo de 2.x que 1.9 no compila:** el catálogo de `01 · kotlin2` corrido en el track · **Se verifica con:** cada
ejemplo de 2.x fallando al compilar en el track (prueba que espera el error) y su versión 1.9 equivalente pasando.

### 22 · `k2migration` — Lo que cambió en el paso A7 y por qué (`k2-notes.md`)

**Objetivo:** entender qué cambió de verdad al pasar este repo de Kotlin 1.9.22 a 2.4.21 y aplicar lo aprendido a otro
proyecto.
**Sección:** `22-legacy-kotlin19` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Kotlin 2.4.21 (después) | Kotlin 1.9.22 (antes) | Nota ([`k2-notes.md`](../22-legacy-kotlin19/docs/k2-notes.md)) |
|---|---|---|
| Aviso "Unnecessary non-null assertion" | Sin aviso | K2 hace smart cast después de `assertNotNull` |
| Aviso sobre `java.lang.Long::class.java` | Sin aviso | Se usa `Long::class.javaObjectType` |
| Aviso de `==` entre arreglos | Sin aviso | La prueba de `collections` lo suprime a propósito |
| `…$$Lambda/0x…` | `Function1<…>` | `toString()` de las lambdas |
| Opt-ins que ya no hacen falta | `@OptIn` necesarios | Por ejemplo, `explicitNulls` de kotlinx-serialization |

**Trampas:**
- Migrar el compilador y las librerías a la vez esconde de dónde viene cada cambio: este repo lo hizo por subpasos.
- Los avisos nuevos de K2 parecen ruido pero suelen señalar código muerto o comprobaciones redundantes.
- Lo que no cubren las pruebas (un `toString()` en un log) cambia sin que nadie se entere.
- Compilar con `-Werror` hace que un aviso nuevo de K2 rompa el build.
- Las herramientas que leen bytecode (agentes, ofuscadores) pueden no entender las lambdas `invokedynamic`.

**Caso:** — (este repo: el paso A7).

**🎤 Entrevista:** ¿qué rompe K2 en un proyecto real? · ¿Cómo planificarías el paso a K2? · ¿Por qué cambian los avisos?

**⚡ Bitácora de migración de otro proyecto:** un proyecto chico de 1.9 migrado a 2.4 siguiendo los pasos de A7 ·
**Se verifica con:** las pruebas en verde antes y después, cada aviso nuevo de K2 resuelto y anotado en una tabla como
la de `k2-notes.md`, y la salida de los `Main` comparada antes y después.

### 22 · `compat` — `apiVersion`, `languageVersion`, compatibilidad binaria y `@Metadata`

**Objetivo:** controlar la compatibilidad entre versiones de Kotlin (`-language-version`, `-api-version`, versión de
metadata) para publicar y consumir librerías sin romper a nadie.
**Sección:** `22-legacy-kotlin19` · **Build:** nivel 6 · **Caso:** — · **Estado:** nueva

| Situación | Resultado (medido) | Nota |
|---|---|---|
| `kotlinc` 2.4.21 con `-language-version 1.9` | Error: "no longer supported; use version 2.0 or greater" | El mismo con `-api-version 1.9` |
| `-language-version 2.0` o `2.1` | Aviso de deprecación | 2.2 a 2.4 sin aviso; 2.5 experimental |
| Librería compilada con 2.4.21, usada desde 2.3.21 | Compila | Un compilador lee la metadata de **una** versión más nueva |
| La misma librería desde 2.2.21, 2.0.21 o 1.9.22 | Error: "incompatible version of Kotlin… metadata is 2.4.0" | |
| Librería compilada con 1.9.22, usada desde 2.4.21 | Compila | Hacia atrás no hay problema |

**Trampas:**
- Publicar una librería compilada con el último Kotlin rompe a los usuarios que están dos versiones atrás: para eso
  está `-api-version`/`-language-version` (dentro del rango que el compilador acepta).
- `-language-version` no convierte al compilador nuevo en el viejo: no reproduce K1 ni sus errores.
- La versión de la stdlib en runtime tiene que ser al menos la de la `-api-version`: una stdlib vieja en el classpath
  da `NoSuchMethodError`.
- `@Metadata` y `META-INF/*.kotlin_module` llevan la información de Kotlin: herramientas que los borran rompen el
  consumo desde Kotlin (aunque Java siga funcionando).
- La compatibilidad binaria de la API pública (firmas, parámetros por defecto) se rompe sin querer: hace falta una
  herramienta de validación de API ⚠️.

**Caso:** — (las mediciones de esta entrada, hechas con los `kotlinc` instalados).

**🎤 Entrevista:** ¿qué hacen `-language-version` y `-api-version`? · ¿Qué versión de Kotlin elegirías para compilar
una librería pública? · ¿Qué es la versión de metadata?

**⚡ Matriz de compatibilidad:** una librería chica compilada con 1.9, 2.0, 2.2 y 2.4 y consumida desde cada uno de
esos compiladores · **Se verifica con:** un script que reproduce la matriz completa (compila y usa en cada
combinación) y su tabla de resultados, que coincide con la medida aquí.

---

## 25-interview

Simulacros y revisión de PRs. **Build:** 1 · **Caso:** Ambos.

### 25 · `livecoding` — Simulacros en Kotlin

**Objetivo:** resolver problemas en vivo en Kotlin con soltura (estructura, casos borde, explicación en voz alta) en
el tiempo de una entrevista.
**Sección:** `25-interview` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Java 21 | Kotlin | Nota |
|---|---|---|
| `Map<K, List<V>>` con `computeIfAbsent` | `groupBy`, `getOrPut` | Menos líneas en la pizarra |
| `for (int i = 0; …)` | `for (i in xs.indices)`, `withIndex()`, `windowed` | |
| `record` para el resultado | `data class` o `Pair` | Un `Pair` con nombres pobres confunde: mejor una `data class` |
| `Deque`, `PriorityQueue` | `ArrayDeque`, `PriorityQueue` de `java.util` | |
| Validación con `if`/`throw` | `require`, `check` | |

**Trampas:**
- Una solución de una línea con cinco operadores encadenados es difícil de explicar y de depurar en vivo.
- Olvidar los casos borde (lista vacía, un elemento, empates) es lo que más se nota en una entrevista.
- `Int` desborda igual que en Java: sumas de montos o de volumen van en `Long`.
- Usar `!!` para ir rápido deja una mala impresión: mejor `requireNotNull` con un mensaje.
- No hablar mientras se escribe: el entrevistador evalúa también el razonamiento.

**Caso:** problemas con los datos de los casos (emparejar órdenes, conciliar movimientos, ventanas de precios, validar
cuentas).

**🎤 Entrevista:** — (la lección entera es práctica de entrevista).

**⚡ Cinco simulacros cronometrados:** cinco problemas de 35 minutos cada uno, con enunciado, pruebas ocultas y una
rúbrica · **Se verifica con:** las pruebas de cada simulacro (casos normales y borde) y el tiempo registrado de cada
intento.

### 25 · `prreview` — Leer un PR: `!!`, `lateinit` abusado, scope functions anidadas, `data class` mutables, `runCatching`

**Objetivo:** revisar un PR de Kotlin y encontrar lo que un revisor con experiencia marca: `!!`, `lateinit` abusado,
scope functions anidadas, `data class` mutables y `runCatching` que se traga la cancelación.
**Sección:** `25-interview` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** nueva

| Lo que aparece en el PR | Lo que se pide | Lección |
|---|---|---|
| `user!!.account!!.balance` | Decidir la nulabilidad: `?.`, `?:`, `requireNotNull` | `basics`, `exceptions` |
| `lateinit var` en todo | Constructor o `by lazy`; `lateinit` solo para inyección o pruebas | `oop`, `delegation` |
| `apply { also { let { … } } }` | Máximo un nivel, o variables con nombre | `functional`, `idioms` |
| `data class` con `var` usada como clave de un mapa | `val`; claves inmutables | `oop`, `collections` |
| `runCatching { suspendCall() }` | Relanzar `CancellationException` | `exceptions`, `02 · cancellation` |
| Entidad JPA como `data class` | Clase normal con `equals` por id | `04 · jpa` |

**Trampas:**
- Una `data class` con `var` en un `HashSet` "desaparece" del set al cambiar un campo (cambia su `hashCode`).
- `lateinit` en un campo que puede no inicializarse convierte un error de diseño en una excepción en runtime.
- `runCatching` alrededor de código con corrutinas rompe la cancelación estructurada sin que se note en las pruebas
  felices.
- Comentar estilo y no comportamiento: lo que importa en la revisión son los errores, no los espacios.
- Pedir cambios sin decir por qué: un buen comentario de PR explica el riesgo concreto.

**Caso:** PRs sobre el `TransferService` del Banco y el feed del Mercado con errores sembrados.

**🎤 Entrevista:** ¿qué buscas primero en un PR de Kotlin? · ¿Qué tiene de malo `runCatching` en una corrutina? ·
¿Cuándo está bien `lateinit`?

**⚡ Diez PRs para revisar:** diez PRs con errores sembrados, cada uno con pruebas que fallan mientras el error siga ·
**Se verifica con:** las pruebas de cada PR (por ejemplo, una prueba de cancelación que falla con `runCatching`) que
pasan después de la corrección, y comentarios de revisión escritos como en un PR real.

---

## 26-kotlin-next

Radar de la siguiente versión. **Build:** 1 (fuera del composite) · **Caso:** —.

### 26 · `kotlin25` — Lo nuevo de 2.5 cuando salga estable

**Objetivo:** seguir lo que trae Kotlin 2.5 cuando salga estable (lenguaje, stdlib, compilador) y medirlo con la
misma referencia que el resto del curso.
**Sección:** `26-kotlin-next` · **Build:** nivel 1 (fuera del composite) · **Caso:** — · **Estado:** nueva

| Kotlin 2.4 (base) | Kotlin 2.5 | Nota |
|---|---|---|
| `-language-version 2.5` en `kotlinc` 2.4.21 | Medido: aviso "experimental, no backwards compatibility guarantees" | Se puede probar antes de que salga |
| Lo experimental en 2.4 (`[exp 2.4]`: contratos, atómicos, `deleteRecursively`…) | ⚠️ por medir en 2.5 | La referencia se amplía con su columna |
| Clases `expect`/`actual` en Beta | ⚠️ | |

**Trampas:**
- Lo experimental de una versión puede cambiar o desaparecer en la siguiente (como los *context receivers*).
- `-language-version 2.5` con un compilador 2.4 no es Kotlin 2.5: activa lo que 2.4 ya implementa de 2.5.
- La sección vive fuera del composite para no arrastrar a la base a una versión no estable.
- Subir la base a 2.5 es una decisión del plan (y de `Versions.md`), no un efecto de esta lección.

**Caso:** — (los ejemplos de los casos que use cada novedad).

**🎤 Entrevista:** ¿cómo sigues la evolución de un lenguaje? · ¿Usarías una característica experimental en producción?

**⚡** — El plan no le asigna miniproyecto: es un radar. Al salir 2.5, la tarea es medir sus construcciones con
`version-probes.sh` y sumar la columna 2.5 a la referencia.

---

## 🐉 Jefes finales

Proyectos que cruzan secciones; se hacen al final, cuando las lecciones que combinan ya están hechas.

### 🐉 1 · Mini `kotlinx.coroutines` (`02` + `06`)

**Qué es:** una librería de corrutinas propia sobre la stdlib (`createCoroutine`, `Continuation`), sin kotlinx:
dispatcher, `Job` con jerarquía padre-hijo, cancelación cooperativa, `Channel` y un `Flow` frío. Extiende el ⚡ de
`02 · internals` (scheduler propio) y usa lo de `06 · bytecode` para mostrar la máquina de estados que genera.

**Caso:** el feed del Mercado (productor de ticks, consumidores de velas) corriendo sobre la mini librería en vez de
kotlinx.

**Se verifica con:** un subconjunto de las pruebas de la librería real reescritas contra la mini (estructura de
`Job`, cancelación que se propaga, `Channel` *rendezvous* y con buffer, `Flow` que no corre hasta `collect`), el
pipeline del feed dando las mismas velas que con kotlinx, y la máquina de estados decompilada de una función de la
librería comentada en la lección.

### 🐉 2 · Producto full-stack (`04` + `09`/`10` + `11`/`12` + `08`)

**Qué es:** el producto de los dos casos unidos por el [puente de liquidación](Casos.md#el-puente-liquidación): un
backend (WebFlux o Ktor) que recibe órdenes del Mercado, las empareja y liquida cada ejecución como asiento en el
ledger del Banco, y un cliente KMP (Android, escritorio y web) que opera, ve su saldo y su portafolio, funciona sin red
y sincroniza. Con CI y empaquetado nativo del backend.

**Caso:** los dos: la compra exige saldo (el chequeo que cruza los casos), cada ejecución genera su `JournalEntry` y
la app muestra precios en vivo por WebSocket.

**Se verifica con:** una prueba de punta a punta (backend con Testcontainers + cliente con su repositorio compartido)
donde una compra sin saldo se rechaza antes de entrar al libro, una compra con saldo termina en un asiento balanceado
y una posición; la suma de saldos del ledger se conserva en una prueba de carga; la app sincroniza una operación hecha
sin red exactamente una vez; y el pipeline de CI construye el binario nativo del backend y corre todas las pruebas.

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
