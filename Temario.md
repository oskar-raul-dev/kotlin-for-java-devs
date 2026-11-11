# Temario: Kotlin para quien viene de Java

Qué cubre el tutorial, lección por lección, con el **mapeo Java → Kotlin**, los **idioms** que se practican y
las **trampas** que se demuestran. Al final hay una sección de **huecos**: lo que un dev Java senior esperaría
encontrar y todavía no está.

- Fuente de verdad de conteos y plan del día: [`README.md`](README.md).
- Rutas de código relativas a `src/main/kotlin/dev/raul/oskar/learning/kotlin/`.
- Leyenda de cobertura: **L** = en el código de la lección (demo + prueba) · **E** = solo en `Exercises.md`.

**Salto rápido:** [Mapa general](#mapa-general) · [Lecciones](#1-basics--tipos-números-identidad) ·
[Chuleta Java → Kotlin](#chuleta-java--kotlin) · [Idioms transversales](#idioms-transversales) ·
[Trampas](#trampas-consolidadas) · [Huecos](#huecos-lo-que-falta-para-un-java-senior)

---

## Mapa general

| # | Lección | Eje | Pregunta que responde |
|---|---|---|---|
| 1 | `basics` | Sistema de tipos | ¿Dónde están los primitivos, el boxing y `null`? |
| 2 | `control` | Expresiones | ¿Por qué no hay ternario ni `for(;;)`? |
| 3 | `strings` | stdlib | ¿Qué cambia de `String` y `Pattern`? |
| 4 | `oop` | Modelado | ¿Cómo se escriben clases, jerarquías y value objects? |
| 5 | `io` | stdlib + JDK | ¿Cómo uso `java.nio` sin ceremonia? |
| 6 | `collections` | stdlib | ¿Qué es "solo lectura" y qué estructura uso? |
| 7 | `functional` | Funciones | ¿Qué reemplaza a `Function<>`, Streams y `Collectors`? |
| 8 | `json` | Ecosistema | ¿Cómo serializo sin Jackson ni reflexión? |
| 9 | `datetime` | JDK | `java.time` correcto y testeable |
| 10–12 | `jdbc.*` | Persistencia | JDBC idiomático y portable entre motores |
| 13 | `jpa` | Persistencia | Hibernate con clases Kotlin (que son `final`) |
| 14 | `exceptions` | Errores | Vivir sin checked exceptions; errores como valores |

---

## 1. `basics` — tipos, números, identidad

Archivos: `basics/Numbers.kt`, `basics/Identity.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `int`, `long`, `double` | `Int`, `Long`, `Double` | Se compilan a primitivos si no son nullables |
| `Integer` (boxing) | `Int?` | Nullable ⇒ boxing |
| `long x = intValue;` (widening implícito) | `i.toLong()` | **No hay conversiones implícitas** |
| `Integer.parseInt` + `try/catch` | `toIntOrNull() ?: default` | Sin excepciones |
| `a.add(b)` con `BigDecimal` | `a + b`, `a * rate`, `a > b` | Sobrecarga de operadores de la stdlib |
| `a.equals(b)` | `a == b` | `==` llama a `equals` y maneja `null` |
| `a == b` (referencias) | `a === b` | Identidad |
| `Object` | `Any` / `Any?` | Raíz no nullable / nullable |
| `void` | `Unit` | Es un valor real (singleton) |
| — | `Nothing` | Tipo de `throw` y de funciones que nunca vuelven |
| `instanceof` + cast | `is` + smart cast | El compilador hace el cast |
| `(char)(c + 1)` | `c + 1` → `Char`; `c.code` → `Int` | `Char` no es número |
| `1_000_000`, `0xFF`, `0b1010`, `3L` | Igual | `UInt`/`ULong` existen (poco usados) |

**Idioms:** `toXxxOrNull()` + Elvis, `BigDecimal` siempre desde `String`, `compareTo` (no `equals`) para montos,
`when (x) { is T -> … }` como "pattern matching" ligero.

**Trampas:** overflow silencioso (igual que Java; `Math.addExact` para detectarlo), `BigDecimal("1.0") != BigDecimal("1.00")`,
`Math.abs(Int.MIN_VALUE)` negativo (E), caché de `Integer` −128..127 y `===` (E).

---

## 2. `control` — `if`/`when` como expresiones, rangos, bucles

Archivos: `control/Conditionals.kt`, `control/Loops.kt`, `control/Algorithms.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `c ? a : b` | `if (c) a else b` | `if` es expresión; no hay ternario |
| `switch` / `switch` expression (14+) | `when (x) { … }` | Sin fall-through; rangos (`in 0..17`), varios valores por rama |
| cadena `if / else if` | `when { cond -> … }` | `when` sin argumento |
| `for (int i = 0; i < n; i++)` | `for (i in 0 until n)` | `..` inclusivo, `until` exclusivo |
| `for (int i = n; i >= 0; i -= s)` | `for (i in n downTo 0 step s)` | |
| índice + valor | `for ((i, x) in xs.withIndex())` | Destructuring |
| `outer: … break outer;` | `outer@ … break@outer` | Etiquetas |
| recursión | `tailrec fun` | El compilador la convierte en bucle |
| `Stack` / `Deque` | `kotlin.collections.ArrayDeque` | `addLast`/`removeLastOrNull` |
| `(lo + hi) >>> 1` | `(lo + hi) ushr 1` | Operadores de bits con nombre: `shl`, `shr`, `ushr`, `and`, `or`, `xor` |

**Idioms:** retorno temprano con `?: return 0`, `takeIf { … }`, `repeat(n) { }`, `var (a, b) = 0L to 1L`
(destructuring de `Pair`), `require(…)` como guarda.

**Algoritmos de pizarra (L):** gcd, primos, Fibonacci, búsqueda binaria, insertion sort, two-sum con `HashMap`,
paréntesis balanceados.

---

## 3. `strings` — plantillas, extensiones de `String`, regex

Archivos: `strings/Strings.kt`, `strings/Regexes.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `"Hola " + name` / `String.format` | `"Hola $name, ${x + 1}"` | Plantillas; `"%.2f".format(x)` sigue existiendo |
| Text blocks `"""…"""` (15+) | Raw strings `"""…""".trimIndent()` | Sin escapes: ideales para regex y SQL |
| `StringBuilder` | `buildString { append(…) }` | Lambda con receptor |
| `s.substring(s.indexOf('@') + 1)` | `s.substringAfter('@')` | También `substringBefore`, `…Last` |
| `equalsIgnoreCase` | `equals(other, ignoreCase = true)` | Argumentos con nombre |
| `Pattern.compile` / `Matcher` | `Regex("…")`, `matches`, `find`, `findAll` | `findAll` = `while (m.find())` |
| `m.group("year")` | `m.groups["year"]?.value` / `m.destructured` | Grupos con nombre y destructuring |
| `Matcher.replaceAll(Function)` | `regex.replace(text) { "#" }` | |
| `s.split("\\/")` (regex) | `s.split("/")` (literal) / `s.split(Regex(…))` | **Ver trampa** |

**Idioms:** `padStart`/`padEnd`, `replaceFirstChar { it.uppercase() }` (reemplaza a `capitalize()`, deprecado),
`groupingBy { it }.eachCount()` para frecuencias, `joinToString(" ") { … }`.

**Trampa estrella:** `split(String)` es **literal** en Kotlin. `"03/14/1985".split("\\/")` devuelve la cadena entera;
es un error típico al portar código de Java.

---

## 4. `oop` — clases, herencia, sealed, enums, objetos

Archivos: `oop/Accounts.kt`, `oop/Shapes.kt`, `oop/Enums.kt`, `oop/Objects.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| Campos + constructor + getters/setters | `class A(val id: String, var x: Int)` | Constructor primario = propiedades |
| Cuerpo del constructor | `init { require(…) }` | |
| Constructor sobrecargado | `constructor(id: String) : this(id, ZERO)` / parámetros por defecto | Lo idiomático es el default |
| Setter privado | `var balance … private set` | |
| Getter calculado | `val isEmpty: Boolean get() = …` | Sin campo de respaldo |
| Clases no-`final` por defecto | **`final` por defecto**; `open` para heredar | Afecta a proxies (Spring, Hibernate) |
| `protected` (visible en el paquete) | `protected` (solo subclases) | No incluye el paquete |
| `default` en interfaces | Métodos con cuerpo en `interface` | También propiedades abstractas |
| `sealed` (17+) + `switch` | `sealed interface` / `sealed class` + `when` exhaustivo | Sin `else`; no compila si falta un caso |
| `enum` con campos y `valueOf` | `enum class(val code: String)` + `companion object { fun of(…) }` | `entries` (1.9) en vez de `values()` |
| `static` | `companion object` / funciones top-level | |
| Singleton (`getInstance`) | `object IdGenerator` | Thread-safe, inicialización perezosa |
| `record` (16+) / Lombok `@Value` | `data class` | `equals/hashCode/toString/copy/componentN` |
| Wrapper para tipar un `long` | `@JvmInline value class Cents(val value: Long)` | Sin objeto extra en runtime (en la mayoría de los usos) |
| Constructor privado + fábrica | `class Email private constructor(…)` + `companion { fun parse() }` | |
| `Comparable` + `compareTo` a mano | `compareValuesBy(this, other, { it.x }, { it.y })` | |
| Decorator escribiendo cada método | `class CountingList<T>(inner) : MutableList<T> by inner` | Delegación de interfaz |
| Inicialización perezosa (doble check) | `val values by lazy { … }` | Propiedad delegada, sincronizada por defecto |
| `<T extends Comparable<T>>` | `<T : Comparable<T>>` | |

**Idioms:** parámetros por defecto y con nombre en vez de builders/telescoping; `copy(...)` para "withers";
`sealed` para resultados de dominio; operadores (`plus`, `unaryMinus`, `get`) solo cuando el significado es obvio.

**Trampas:** propiedades del cuerpo de una `data class` **no** entran en `equals`; `by` no delega
`toString`/`equals`/`hashCode`; `;` obligatorio en un enum antes de sus miembros.

**Solo en ejercicios (E):** `Delegates.observable`/`vetoable`, varianza `out`/`in` (`Source<out T>`, `Sink<in T>`).

---

## 5. `io` — archivos, recursos, consola, logging

Archivos: `io/Files.kt`, `io/Console.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `Files.readString(path)` | `path.readText()` | Extensiones de `kotlin.io.path` |
| `Files.writeString`, `Files.lines` | `writeText`, `appendText`, `readLines`, `useLines { }` | `useLines` = `Sequence` perezosa que cierra el archivo |
| `try (var w = …) { }` | `w.use { }` | try-with-resources |
| `getClass().getResource(…)` | `object {}.javaClass.getResource(…)?.readText()` | Truco del objeto anónimo en funciones top-level |
| `System.out.println`, `Scanner` | `println`, `readln()`, `readlnOrNull()` | |
| Lógica acoplada a `System.in/out` | Recibir `BufferedReader` y `Appendable` | Consola testeable |
| SLF4J/Log4j | Kermit `Logger.withTag("io").i { … }` | Mensaje en lambda: solo se construye si se loguea |

**Idioms:** parseo que devuelve `null` en vez de lanzar (`Sale.fromCsv`), `const val` en `companion`,
`path.parent?.createDirectories()`.

---

## 6. `collections` — solo lectura vs mutable, estructuras

Archivo: `collections/Collections.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `List.of(…)` (inmutable) | `listOf(…)` (**solo lectura**, no inmutable) | Interfaz `List` sin métodos de escritura |
| `new ArrayList<>()` | `mutableListOf()` / `ArrayList()` | `MutableList` |
| `Collections.unmodifiableList(copy)` | `toList()` (copia defensiva) | |
| `list.get(10)` con riesgo | `getOrNull(10)`, `firstOrNull()`, `lastOrNull()` | |
| `map.computeIfAbsent(k, …).add(v)` | `map.getOrPut(k) { mutableListOf() } += v` | |
| `map.merge(w, 1, Integer::sum)` | `counts[w] = (counts[w] ?: 0) + 1` | O `groupingBy().eachCount()` |
| `for (Map.Entry e : m.entrySet())` | `for ((k, v) in m)` / `m.map { (k, v) -> … }` | Destructuring |
| `retainAll`, `addAll`, `removeAll` | `a intersect b`, `a union b`, `a subtract b` | Funciones infix |
| `TreeMap.headMap`, `floorEntry` | Igual (`java.util.TreeMap`) | Ventanas por timestamp |
| `ArrayDeque` (java.util) | `ArrayDeque` (kotlin.collections) | Cola y pila |
| `PriorityQueue` | Igual (`java.util`) | `heap += n` funciona por `plusAssign` |
| `int[]` / `Integer[]` / `int[][]` | `IntArray` / `Array<Int>` / `Array(3) { IntArray(3) }` | `contentToString()`, `contentDeepToString()` |

**Idioms:** `chunked`, `windowed`, `take`/`takeLast`, `buildList { }`, `sortedDescending()`.

**Trampa:** una referencia `List<T>` puede cambiar si alguien guarda la `MutableList` original.

---

## 7. `functional` — funciones, lambdas, scope functions, pipelines

Archivos: `functional/Functional.kt`, `functional/Pipelines.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `Function<Integer, Integer>` | `(Int) -> Int` | Tipos función estructurales |
| `f.andThen(g)` | No viene en la stdlib; se define como extensión `infix` | (L) |
| `String::length` | `String::length` | Referencias a función y constructor (`::Email`) |
| `@FunctionalInterface` | `fun interface Validator<T>` | SAM con conversión desde lambda |
| Genérico borrado (`Class<T>` como parámetro) | `inline fun <reified T>` | El tipo existe en runtime |
| `stream().map().filter().collect(toList())` | `map { }.filter { }` directo sobre `List` | **Ansioso**: lista intermedia en cada paso |
| `Stream` perezoso / `Stream.iterate` | `asSequence()`, `generateSequence` | Perezoso e infinito |
| `Collectors.groupingBy(f, summingInt(g))` | `groupBy { }.mapValues { (_, l) -> l.sumOf { } }` | |
| `groupingBy(f, counting())` | `groupingBy { }.eachCount()` | `Grouping` no materializa listas |
| `Collectors.partitioningBy` | `partition { }` → `Pair` | |
| `toMap` | `associate`, `associateBy`, `associateWith` | |
| `reduce(identity, op)` | `fold(initial) { acc, x -> … }` | `reduce` falla con lista vacía |
| — | `zipWithNext`, `windowed`, `flatMap`, `mapNotNull` | |
| `Optional.map/orElse` | `?.let { }`, `?:` | |
| `try/catch` local | `runCatching { }` → `Result<T>` | |

**Scope functions (L):**

| Función | Receptor | Devuelve | Uso típico |
|---|---|---|---|
| `apply` | `this` | el objeto | Configurar |
| `also` | `it` | el objeto | Efecto lateral (log) |
| `let` | `it` | resultado de la lambda | Transformar, `?.let` |
| `run` | `this` | resultado de la lambda | Calcular con el objeto |
| `with(x)` | `this` | resultado de la lambda | Agrupar llamadas sobre `x` |

**Otros temas (L):** closures, currying, memoización, lambdas con receptor (`StringBuilder.() -> Unit`, base de
los DSL), `tailrec`.

---

## 8. `json` — kotlinx-serialization

Archivos: `json/Model.kt`, `json/Codec.kt`, `json/Serializers.kt`.

| Java (Jackson) | Kotlin (kotlinx-serialization) | Nota |
|---|---|---|
| `ObjectMapper` (reflexión) | `Json` + plugin del compilador | Sin reflexión; se construye una vez |
| `@JsonProperty("order-total")` | `@SerialName("order-total")` | |
| `FAIL_ON_UNKNOWN_PROPERTIES = false` | `ignoreUnknownKeys = true` | Por defecto es estricto |
| Campo ausente → `null` | `explicitNulls = false` (experimental en 1.6.2) | Si no, un `T?` sin default es **obligatorio** |
| `@JsonInclude(NON_DEFAULT)` | Comportamiento por defecto; `encodeDefaults = true` para escribirlos | |
| `null` en campo con default | `coerceInputValues = true` | |
| `@JsonTypeInfo` + `@JsonSubTypes` | `@Serializable sealed class` + `@SerialName` por subclase | Discriminador `"type"` |
| `JsonSerializer<BigDecimal>` | `object : KSerializer<BigDecimal>` + `@Serializable(with = …)` | Tipos de Java no son serializables solos |
| `JsonNode` | `JsonElement`, `jsonObject`, `jsonPrimitive.intOrNull` | Árbol sin clases |
| `ObjectNode.put` | `buildJsonObject { put(…); putJsonArray(…) { } }` | DSL |
| — | `Json(from = base) { prettyPrint = true }` | Derivar configuraciones |

**Trampas:** `@file:OptIn(ExperimentalSerializationApi::class)` para `explicitNulls`; nullable ≠ opcional.

---

## 9. `datetime` — `java.time` correcto

Archivo: `datetime/DateTimes.kt`. Aquí no hay mapeo de lenguaje: se usa `java.time` tal cual
(`kotlinx-datetime` solo aporta en multiplataforma). Lo que se repasa es **uso correcto**:

- Modelo mental: `Instant` (se guarda) · `LocalDate/Time` (calendario) · `ZonedDateTime` (se muestra) ·
  `Duration` (exacto) vs `Period` (calendario).
- `DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(STRICT)`: `uuuu`, no `yyyy`, para que STRICT funcione.
- Edad con `Period.between`, no días/365.
- `Clock` inyectado como parámetro por defecto: `fun age(birth, clock: Clock = Clock.systemDefaultZone())`.
- Epoch millis → `LocalDate` con zona explícita.
- Ventana `[día−90, día)` como `ClosedRange<LocalDate>` (`a..b` funciona con cualquier `Comparable`).
- `generateSequence(from) { it.plusDays(1) }` para iterar días.
- Horario de verano: un día puede durar 23 o 25 h; `plusMonths(1)` sobre el 31 de enero.

---

## 10–12. `jdbc` — JDBC idiomático, tres motores

Archivos: `jdbc/Jdbc.kt`, `jdbc/ProductRepository.kt`, `jdbc/Dialect.kt`, `jdbc/{sqlite,postgres,mysql}/*Dialect.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| `try (PreparedStatement ps = …)` | `prepareStatement(sql).use { ps -> }` | |
| `while (rs.next()) list.add(map(rs))` | `inline fun <T> ResultSet.mapRows(mapper)` | Extensión propia |
| `setAutoCommit(false)`/`commit`/`rollback` | `inline fun <T> Connection.inTransaction(block: Connection.() -> T)` | Lambda con receptor: dentro, `this` es la conexión |
| Interfaz Strategy | `interface Dialect` + `object PostgresDialect : Dialect` | `object` como estrategia sin estado |
| Clase de config + builder | `data class DbConfig` + `companion { fun fromEnv(…) }` | |
| Suite de pruebas abstracta | `abstract class ProductRepositoryContract` + `assumeTrue` | Pruebas de contrato |

**Temas SQL:** `RETURN_GENERATED_KEYS`, batch, upsert por motor (`ON CONFLICT` / `ON DUPLICATE KEY … AS new`),
`IDENTITY` vs `AUTO_INCREMENT`, `NUMERIC` vs `TEXT` en SQLite, `COALESCE(SUM…)`, `CHECK` que dispara rollback,
`LIKE` con el `%` en el parámetro. En ejercicios: `RETURNING`, `= ANY(array)`, `jsonb`, funciones de ventana.

---

## 13. `jpa` — Hibernate 6 sin Spring

Archivos: `jpa/Entities.kt`, `jpa/Jpa.kt`, `jpa/OrderRepository.kt`.

**Lo específico de Kotlin:**

| Problema | Solución |
|---|---|
| JPA exige constructor sin argumentos | Plugin `kotlin("plugin.jpa")` (no-arg) |
| Clases `final` impiden proxies lazy | Plugin `kotlin("plugin.allopen")` para `@Entity`/`@MappedSuperclass`/`@Embeddable` |
| `data class` rompe `equals/hashCode` con colecciones lazy e id cambiante | Clase normal; `equals` por id, `hashCode` constante por clase |
| `id` antes de persistir | `var id: Long? = null` |
| `cascade = {CascadeType.ALL}` | `cascade = [CascadeType.ALL]` (arrays en anotaciones) |
| `Customer.class` | `Customer::class.java` |
| `Object[]` en proyecciones | `Array<Any>::class.java` + `row[0] as String` |
| `toString` con colecciones lazy | `toString` a mano sin colecciones |

**Temas JPA:** `mappedBy` y sincronizar ambos lados, `orphanRemoval`, `FetchType.LAZY` en `@ManyToOne`,
N+1 medido con estadísticas y resuelto con `join fetch`, `@Version` (bloqueo optimista), `EnumType.STRING`,
paginación, UPDATE masivo que no refresca el contexto, `getSingleResult` vs `firstOrNull()`.

---

## 14. `exceptions` — sin checked, errores como valores

Archivos: `exceptions/Basics.kt`, `exceptions/Domain.kt`, `exceptions/Results.kt`, `exceptions/Resources.kt`.

| Java | Kotlin | Nota |
|---|---|---|
| Checked exceptions / `throws` | No existen | `Files.readString` compila sin `try` |
| `try` como sentencia | `val x = try { … } catch (…) { … }` | Expresión |
| `throw` como sentencia | `?: throw …`, rama de `when` | Expresión de tipo `Nothing` |
| `Objects.requireNonNull`, `Preconditions.checkArgument` | `require`, `requireNotNull`, `check`, `checkNotNull`, `error` | IAE vs ISE; mensaje en lambda |
| `catch (A \| B e)` | `catch (e: Exception) { when (e) { is A -> …; else -> throw e } }` | Sin multi-catch |
| Jerarquía de excepciones | `sealed class OrderException : RuntimeException` | `when` exhaustivo → código HTTP |
| `new X(msg, cause)` | Igual; recorrer con `generateSequence(t) { it.cause }` | |
| try-with-resources + suppressed | `use { }` (mismo comportamiento) | |
| `throws IOException` para llamadores Java | `@Throws(IOException::class)` | Interop |
| `Optional`/Either de Vavr | `Result<T>`, `runCatching`, `fold`, `recoverCatching`, `mapCatching` | |
| Validación que corta al primer error | `sealed interface Validation<out T>` con `Valid`/`Invalid(errors)` | Acumula todos |

**Trampa:** `runCatching` captura `Throwable`, incluida `CancellationException` (rompe la cancelación de corrutinas)
y `Error`. Se muestra `runCatchingCancellable` como alternativa.

---

## Chuleta Java → Kotlin

Lo transversal, en una sola tabla.

| Java | Kotlin |
|---|---|
| `final var x = …` / `var x = …` | `val x = …` / `var x = …` |
| `@Nullable String` | `String?` — `?.`, `?:`, `!!`, `let` |
| Package-private por defecto | `public` por defecto; `internal` = visible en el módulo; no hay package-private |
| `static` / clase utilitaria | Funciones top-level, `object`, `companion object` |
| `x instanceof Foo f` | `x is Foo` (smart cast) / `x as? Foo` |
| `switch` | `when` |
| `cond ? a : b` | `if (cond) a else b` |
| `for (;;)` | `for (i in a until b step s)`, `repeat(n)` |
| Getters/setters | Propiedades (`val`, `var`, `get()`, `private set`) |
| `record` / Lombok | `data class` |
| Builder | Parámetros por defecto + con nombre, o `apply { }` |
| Utilidades `StringUtils.x(s)` | Funciones de extensión `s.x()` |
| `Function`, `Supplier`, `Consumer` | `(A) -> B`, `() -> T`, `(T) -> Unit` |
| Streams + Collectors | Operaciones directas en colecciones / `Sequence` |
| `Optional<T>` | `T?` |
| `try-with-resources` | `use { }` |
| `throws` | Nada (o `@Throws` para Java) |
| `Foo.class` | `Foo::class` (`KClass`) / `Foo::class.java` |
| `String.format` | Plantillas `"$x"` o `"%.2f".format(x)` |
| `Objects.equals(a, b)` | `a == b` |

---

## Idioms transversales

1. **Expresiones por todos lados:** `if`, `when`, `try` y `throw` producen valores; menos variables mutables.
2. **Nulos en el tipo:** `String?` + `?.` + `?:` + retorno temprano (`?: return`). `!!` se justifica o no se usa.
3. **`OrNull` antes que excepciones:** `toIntOrNull`, `firstOrNull`, `getOrNull`, `matchEntire(…) ?: return null`.
4. **Defaults y argumentos con nombre** en vez de sobrecargas y builders.
5. **Inmutabilidad por defecto:** `val`, `List`, `data class` + `copy`.
6. **Modelar con `sealed` + `when` exhaustivo** (formas, pagos, errores, validaciones).
7. **Extensiones para quitar ceremonia** a APIs de Java (`mapRows`, `inTransaction`, `kotlin.io.path`).
8. **Lambdas con receptor** para DSLs y bloques de contexto (`buildString`, `buildJsonObject`, `inTransaction`).
9. **Dependencias de tiempo e I/O inyectadas** (`Clock`, `BufferedReader`, `Appendable`) para probar.
10. **Preconditions de la stdlib** (`require`/`check`/`error`) en vez de `if … throw`.

---

## Trampas consolidadas

| Trampa | Dónde |
|---|---|
| `split(String)` es literal, no regex | `strings` |
| Solo lectura ≠ inmutable | `collections` |
| `BigDecimal.equals` compara escala | `basics` |
| Overflow silencioso de `Int` | `basics` |
| `List.map/filter` son ansiosos | `functional` |
| Propiedades del cuerpo fuera del `equals` de una `data class` | `oop` |
| `by` no delega `toString/equals/hashCode` | `oop` |
| Nullable sin default = clave obligatoria en JSON | `json` |
| `yyyy` vs `uuuu` con `ResolverStyle.STRICT` | `datetime` |
| `data class` como entidad JPA | `jpa` |
| `@ManyToOne` EAGER por defecto; N+1 | `jpa` |
| UPDATE masivo no refresca entidades cargadas | `jpa` |
| `runCatching` se traga `CancellationException` | `exceptions` |
| MySQL gasta `AUTO_INCREMENT` en upserts | `jdbc.mysql` |
| Gradle 9 + Kotlin 1.9.22 sin JUnit launcher | `build.gradle.kts` |

---

## Huecos: lo que falta para un Java senior

El tutorial cubre bien el **lenguaje del día a día** y la **persistencia**. Lo que sigue es lo que un dev Java
senior usa en producción (o le van a preguntar en un PR) y aquí no aparece, o aparece solo de pasada. Es la
lista para discutir; el orden dentro de cada bloque es una propuesta de prioridad.

### A. Concurrencia (el hueco más grande)

| Tema | Estado | Por qué importa a un Java senior |
|---|---|---|
| Corrutinas: `suspend`, `launch`/`async`, `CoroutineScope`, structured concurrency | ❌ | Es *el* modelo de concurrencia de Kotlin; reemplaza a `CompletableFuture`/ejecutores |
| Cancelación cooperativa, `withTimeout`, `SupervisorJob` | ❌ | Solo se menciona como trampa de `runCatching` |
| Dispatchers (`IO`, `Default`), `withContext` y bloqueo de hilos | ❌ | Equivalente a elegir el pool correcto |
| `Flow` (frío), `StateFlow`/`SharedFlow` (caliente) | ❌ | Equivalente a Reactor/RxJava |
| Corrutinas vs virtual threads de Java 21 (Loom) | ❌ | Decisión de arquitectura actual con JDK 21 en el toolchain |
| `Mutex`, `Channel`, `@Volatile`, `synchronized`, `AtomicXxx` desde Kotlin | ❌ / solo `AtomicLong` | Memoria compartida y JMM |
| Pruebas: `runTest`, tiempo virtual | ❌ | |

> Nota: corrutinas requiere agregar `kotlinx-coroutines-core`, que hoy no está entre las dependencias del proyecto.

### B. Interop con Java (lo que aparece en cualquier code base mixto)

| Tema | Estado |
|---|---|
| Platform types (`String!`) y anotaciones de nulabilidad (JSpecify, JetBrains, `@NonNull`) | ❌ |
| `@JvmStatic`, `@JvmOverloads`, `@JvmField`, `@JvmName`, `@file:JvmName` | ❌ |
| Cómo ve Java una clase Kotlin: `FileKt`, `Companion`, getters `isX`, `value class` con nombres mangleados | ❌ |
| SAM conversion de interfaces Java vs `fun interface` | Parcial |
| Colecciones Kotlin vistas desde Java (`List` es mutable para Java) | ❌ |
| `@Throws` | ✅ |
| Llamar Kotlin desde Java en un mismo módulo (compilación mixta) | ❌ |

### C. Sistema de tipos y genéricos avanzados

| Tema | Estado |
|---|---|
| Varianza en declaración (`out`/`in`) y en uso (`Array<out T>`), star projection `<*>` | E (oop 17) |
| `reified` | ✅ |
| `inline`, `noinline`, `crossinline`, non-local returns | Parcial (solo `inline`) |
| `typealias` | ❌ |
| Tipos definitivamente no nulos `T & Any`, `Nothing?` | ❌ |
| `contract { returns() implies … }` (cómo `require` hace smart cast) | ❌ |
| `inner class` vs clase anidada (estática por defecto, al revés que Java) | ❌ |
| `object` expressions (clases anónimas) y captura de variables | Parcial |
| `lateinit` vs `lazy` vs nullable | Solo en pruebas |
| `data object` (1.9), `sealed` con `object` | ❌ |
| Visibilidad `internal` y módulos | ❌ |

### D. Diseño idiomático y API design

| Tema | Estado |
|---|---|
| Funciones de extensión: resolución estática, cuándo sí/no, extensiones en `companion` | Parcial |
| DSLs type-safe: `@DslMarker`, builders anidados | Parcial (lambdas con receptor) |
| Propiedades delegadas propias (`ReadOnlyProperty`, `by map`) | E (observable/vetoable) |
| Operadores: `invoke`, `contains`, `rangeTo`, `compareTo`, destructuring propio | Parcial |
| Convenciones de estilo (Kotlin coding conventions, detekt, ktlint) | ❌ |
| Errores: `Result` vs `sealed` vs excepciones vs Arrow `Either` — guía de decisión | Parcial |
| Colecciones persistentes (`kotlinx.collections.immutable`) | ❌ |

### E. Ecosistema y producción

| Tema | Estado |
|---|---|
| Spring Boot con Kotlin (`kotlin-spring`, constructor injection, `@ConfigurationProperties`, WebFlux + corrutinas) | ❌ |
| Testing idiomático: MockK, Kotest/AssertK, nombres con backticks, `@TestInstance(PER_CLASS)` | Parcial (`kotlin.test`) |
| Gradle Kotlin DSL a fondo: version catalogs, convention plugins, multi-módulo | Parcial |
| KSP vs kapt (procesadores de anotaciones: MapStruct, Room, etc.) | ❌ |
| Exposed / jOOQ / Spring Data con Kotlin (alternativas a JPA) | ❌ |
| Ktor (servidor/cliente HTTP) | ❌ |
| Reflexión: `KClass`, `KProperty`, `kotlin-reflect` y su costo | ❌ |

### F. Rendimiento y JVM

| Tema | Estado |
|---|---|
| Qué genera el compilador: bytecode de lambdas (`invokedynamic` desde 2.0), `inline`, `value class`, `companion` | ❌ |
| Boxing oculto: genéricos con `Int`, `Int?`, `value class` en colecciones | Parcial |
| `Sequence` vs `List` vs `Stream`: cuándo conviene cada uno (benchmarks con JMH) | Parcial |
| Arrays primitivos vs `List<Int>` | ✅ |

### G. Versiones del lenguaje

El proyecto está fijado en **Kotlin 1.9.22** a propósito. Un senior debería conocer, aunque no pueda usar aquí,
lo que trae **Kotlin 2.x**: compilador K2, *guard conditions* en `when`, *non-local break/continue*,
*context parameters*, `@SubclassOptInRequired`, smart casts mejorados, y el cambio de `invokedynamic` para lambdas.

---

### Para la discusión

Preguntas para decidir qué agregar:

1. ¿Corrutinas como lección propia? (Propuesta: sí, como lección 15, con su dependencia aislada.)
2. ¿Interop con Java como lección propia (`interop`) con un par de clases `.java` en `src/main/java`?
3. ¿Genéricos avanzados + `inline`/`contracts` como lección `types`, o se reparten en `oop` y `functional`?
4. ¿Spring Boot dentro de este repo o en uno aparte (para no contaminar las versiones fijadas)?
5. ¿Una lección de "leer un PR en Kotlin" con code smells típicos (`!!`, `lateinit` abusado, scope functions
   anidadas, `data class` mutables, `runCatching` que se traga todo)?
