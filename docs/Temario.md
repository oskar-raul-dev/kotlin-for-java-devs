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
