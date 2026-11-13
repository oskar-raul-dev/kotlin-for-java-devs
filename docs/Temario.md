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
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** existe (pasa al Banco en B0′)

> **Hoy → B0′:** el código usa `BigDecimal` suelto (`money`, `addTax`, `sameAmount`) y ejemplos genéricos; con el
> reemplazo pasa a `Money` de [`Casos.md`](Casos.md#money). Los 25 ejercicios actuales se conservan y se completan.

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
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (pasa a los casos en B0′)

> **Hoy → B0′:** los tramos de monto y de edad pasan a tramos de comisión por volumen; la búsqueda binaria busca un
> precio en un libro ordenado. Los algoritmos de pizarra se quedan.

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
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (pasa a los casos en B0′)

> **Hoy → B0′:** la fecha MM/dd/yyyy, el zip y el código de estado pasan a validar cuentas `ACC-…` con mod 97 y a
> parsear mensajes estilo FIX. La trampa de `split("\\/")` se queda tal cual.

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
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Ambos · **Estado:** existe (pasa a los casos en B0′)

> **Hoy → B0′:** `ShippingMethod`, `Customer` y las formas geométricas pasan a `Order` sellado, `AccountType`,
> `Money` y `Account`. Las cuentas de ahorro y de cheques de hoy ya están cerca del Banco.

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
**Sección:** `01-language` · **Build:** nivel 1 · **Caso:** Banco · **Estado:** existe (pasa al Banco en B0′)

> **Hoy → B0′:** `sales.csv` y `Sale` pasan a `bank-statement.csv` y `StatementLine`; el reporte de ventas pasa a
> reporte de conciliación.

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
