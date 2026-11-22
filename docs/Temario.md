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
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** existe (pasa al Banco en B0′, tanda 5)

> **Hoy → B0′:** el repositorio de productos (`ProductRepository`, `ProductRepositoryContract`) pasa a
> `LedgerRepository` con cuentas, asientos y postings, la transferencia en una transacción, la actualización perdida
> y `SELECT … FOR UPDATE`. Guía: [`01-jdbc.md`](lessons/04-data/01-jdbc.md). Versiones: sqlite-jdbc 3.54.1.0,
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
**Sección:** `04-data` · **Build:** nivel 2 · **Caso:** Banco · **Estado:** existe (pasa al Banco en B0′, tanda 5)

> **Hoy → B0′:** `Customer`, `PurchaseOrder` y `OrderLine` pasan a `Account` con `@Version` y `JournalEntry` con
> `Posting` (cascade, `orphanRemoval`), con el N+1 al listar movimientos. Guía: [`02-jpa.md`](lessons/04-data/02-jpa.md).
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
