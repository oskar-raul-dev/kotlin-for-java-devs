# 06 · `collections` — Solo lectura frente a mutable, estructuras

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.collections` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--collections--solo-lectura-frente-a-mutable-estructuras) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/collections/CollectionsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Elegir la colección correcta (lista, mapa ordenado, cola, montículo, arreglo primitivo), entender qué significa "solo
lectura" en Kotlin (y por qué no es "inmutable") y usar las estructuras del JDK sin perder la ergonomía de la stdlib.

## Teoría

### 1. Interfaces de solo lectura sobre las clases de Java

Kotlin no tiene colecciones propias en la JVM: `List`, `Set` y `Map` de Kotlin se compilan a `java.util.List`, `Set` y
`Map`. Lo que agrega es una división **en el sistema de tipos**: `List<T>` no tiene `add` ni `set` (solo lectura) y
`MutableList<T>` sí. Java no tiene esa distinción: `java.util.List` siempre ofrece `add`, y la inmutabilidad se descubre
en runtime con una `UnsupportedOperationException`. Medido con 2.4.21, debajo de cada constructor hay una clase de
Java conocida: `listOf(1, 2)` es un `java.util.Arrays$ArrayList`, `listOf(1)` un `Collections$SingletonList`,
`mutableListOf()` un `java.util.ArrayList`, y `mapOf`/`setOf` un `LinkedHashMap`/`LinkedHashSet` (orden de inserción).
→ [Collections overview][collections], [Collections in Java and Kotlin][j2k-collections]

### 2. Solo lectura no es inmutable

`List` promete que **tú** no vas a modificar la lista a través de esa referencia, no que nadie pueda. Si otro código
guarda la `MutableList` original, lo que ves como `List` cambia por debajo (`readOnlyIsNotImmutable`); la defensa es
copiar con `toList()`. Y el tipo se puede saltar: medido, `(listOf(1, 2) as MutableList<Int>)[0] = 9` **funciona**
(un `Arrays$ArrayList` admite `set`), aunque `add` lanza. El `List.of` de Java 9, en cambio, es inmutable de verdad y
lanza en las dos. Para inmutabilidad real (y versiones nuevas que comparten estructura) existe la librería
`kotlinx.collections.immutable`.

### 3. Varianza: `List<Int>` es un `List<Number>`

Como `List` no tiene métodos que reciban un `T`, Kotlin la declara **covariante** (`List<out E>`): un `List<Int>` se
pasa donde se pide un `List<Number>` sin comodines (medido). En Java eso exige `List<? extends Number>` en cada firma.
`MutableList<E>` es invariante, porque permitiría meter un `Double` en una lista de `Int`. Es la misma idea que
`01 · generics` desarrolla a fondo.

### 4. Estructuras del JDK con la ergonomía de Kotlin

`TreeMap`, `PriorityQueue`, `ConcurrentHashMap` o `LinkedHashMap` se usan tal cual, y la stdlib les suma operadores y
extensiones: `heap += x`, `map.getOrPut(k) { … }` (el `computeIfAbsent`), `for ((k, v) in map)`. La lección los usa
para el Mercado: cierres por fecha en un `TreeMap` con `headMap` y `floorEntry` (`closesBefore`, `closeAtOrBefore`), y
el libro de órdenes con niveles de precio en un `TreeMap` (las compras con `Comparator.reverseOrder()`) y una
`ArrayDeque` FIFO por nivel (`OrderBook`). `firstEntry()` devuelve `null` si está vacío; `firstKey()` lanzaría.

### 5. `ArrayDeque` y la trampa de las colecciones secuenciadas de Java 21

Kotlin tiene su propia `ArrayDeque` (en `kotlin.collections`) para pilas y colas. La trampa viene de Java 21: JEP 431
agregó `SequencedCollection` con `removeFirst()`/`removeLast()` a `java.util.List`. Compilado contra JDK 21, un
`list.removeFirst()` sobre una `MutableList` llama a ese método **de Java**, que no existe en JDK 17 ni en Android: el
programa compila y falla en runtime con `NoSuchMethodError` (medido con `javap` en el temario). Se usa
`removeFirstOrNull()` o `removeAt(0)`. → [JEP 431][jep431]

### 6. Arreglos: `IntArray` frente a `Array<Int>`

`IntArray` es un `int[]` (sin boxing); `Array<Int>` es un `Integer[]`. Los arreglos son invariantes y, como en Java,
`==` entre ellos compara referencias (desde 2.0 el compilador lo avisa): se usa `contentEquals` y `contentToString`.
En código de dominio casi siempre conviene `List`; los arreglos primitivos quedan para algoritmos y rendimiento.

### 7. Elegir la estructura: lo que cuesta cada operación

Las colecciones son las del JDK, así que sus costos también:

| Estructura | Acceso por índice | Buscar | Insertar / quitar | Orden | Para qué en el curso |
|---|---|---|---|---|---|
| `ArrayList` (`mutableListOf`) | O(1) | O(n) | O(1) al final, O(n) en el medio | Inserción | Listas de ticks, movimientos |
| `ArrayDeque` | O(1) | O(n) | O(1) en los dos extremos | Inserción | Pilas, colas, FIFO de un nivel del libro |
| `HashMap` / `LinkedHashMap` (`mutableMapOf`) | — | O(1) promedio | O(1) promedio | Ninguno / inserción | Conteos, índices por clave |
| `TreeMap` | — | O(log n) | O(log n) | Por clave | Cierres por fecha, niveles de precio |
| `PriorityQueue` | — | O(n) | O(log n) | El mínimo arriba | Alertas, top-k |

`LinkedList` casi nunca conviene (cada nodo es un objeto y recorrerla es lento por la memoria); para colas, `ArrayDeque`.

### 8. `equals`, `hashCode` y las claves mutables

Un `HashMap` o `HashSet` ubica cada elemento por su `hashCode`. Si una clave cambia **después** de entrar, queda en el
casillero viejo y deja de encontrarse. Medido: una `data class Key(var ticker: String)` agregada a un `HashSet` y luego
modificada da `contains(k) == false`, aunque el set sigue teniendo un elemento. Por eso las claves son `val` (y una
`data class` con `var` como clave es un hallazgo típico de revisión, `25 · prreview`). El `TreeMap` tiene la misma trampa
con `compareTo`.

### 9. Mapas: leer sin sorpresas

`map[k]` devuelve `V?` (null si falta), `map.getValue(k)` lanza `NoSuchElementException` si falta y
`map.getOrElse(k) { … }` calcula una alternativa. La trampa es `withDefault`: medido, `mapOf("ACME" to 1).withDefault { 0 }`
devuelve `null` con `map["NUBE"]` y `0` solo con `getValue("NUBE")`. Para construir, `buildMap { put(…) }` (como
`buildList`) entrega al final un mapa de solo lectura (su implementación es `kotlin.collections.builders.MapBuilder`, medido),
y `associateBy`, `associateWith` y `groupBy` convierten listas en mapas sin bucles. → [Map-specific operations][map-ops]

### 10. Ordenar

`sorted()`, `sortedBy { it.price }`, `sortedDescending()` y `sortedWith(compareBy({ it.ticker }, { it.day }))` devuelven una
lista nueva; `sort()` ordena una `MutableList` en el lugar. El ordenamiento es **estable** (medido: los elementos con la
misma clave conservan su orden original), igual que el de Java para objetos, así que ordenar primero por día y después
por ticker deja los días ordenados dentro de cada ticker. `compareBy`, `thenBy` y `reversed()` arman un `Comparator` de
Java, así que sirven también para un `TreeMap` o una `PriorityQueue`. → [Ordering][ordering]

### 11. Concurrencia: las colecciones no son seguras entre hilos

`mutableListOf()` y `mutableMapOf()` son un `ArrayList` y un `LinkedHashMap`: modificarlos desde dos hilos (o dos
corrutinas en `Dispatchers.Default`) corrompe la estructura o lanza `ConcurrentModificationException`, igual que en Java.
Las salidas también son las de Java: `ConcurrentHashMap`, confinar la colección a un solo hilo o actor (`02 · channels`), o
compartir solo colecciones que nadie modifica (una copia con `toList()` es una foto segura de leer). Que la interfaz sea
de solo lectura no la hace segura si alguien la modifica por otro lado.

### 12. Interoperabilidad con Java

Una `List` de Kotlin pasada a un método Java llega como `java.util.List`: Java puede llamar `add` sobre ella (y lanzar o,
peor, modificarla). Y lo que viene de Java llega como *platform type*: un método Java que devuelve `List<String>` se ve en
Kotlin como `(Mutable)List<String!>`, ni nullable ni no nullable, ni de solo lectura ni mutable; el que recibe decide y
asume el riesgo (`01 · interop`). En una frontera con Java conviene copiar al entrar y al salir.

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=collections
./gradlew -p 01-language test --tests '*.collections.*'
```

## Recorrido sugerido

1. `Collections.kt`, de arriba abajo: solo lectura no es inmutable (`readOnlyIsNotImmutable`), listas
   (`listBasics`), sets (`setOps`), mapas (`wordCount`, `indexByFirstLetter`, `describeMap`), `TreeMap` de cierres por fecha
   con consultas de rango (`closesBefore`, `closeAtOrBefore`: el último cierre conocido un sábado), `ArrayDeque` como cola y pila (`queueAndStack`), `PriorityQueue`
   (`topK`) y arreglos (`arrays`).
2. `OrderBook.kt`: el libro de órdenes de un ticker: `TreeMap` por precio en centavos (compras con
   `Comparator.reverseOrder()`), `ArrayDeque` FIFO por nivel con `getOrPut`, `firstEntry()` en vez de `firstKey()`,
   profundidad y cancelación con `removeIf`. Solo guarda y consulta: emparejar es el ⚡ de `control`.
3. `CollectionsTest.kt`: incluye la trampa de `==` entre arreglos (con `@Suppress` a propósito).
4. `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Collections.kt` | Colecciones de Kotlin y estructuras del JDK | `readOnlyIsNotImmutable`, `listBasics`, `setOps`, `wordCount`, `indexByFirstLetter`, `describeMap`, `closesBefore`, `closeAtOrBefore`, `queueAndStack`, `topK`, `arrays` | `CollectionsTest` (una prueba por bloque, de `read-only view…` a `arrays need contentEquals`) |
| `OrderBook.kt` | Libro de órdenes con estructuras del JDK | `RestingOrder`, `OrderBook` (`addBid`, `addAsk`, `bestBid`, `bestAsk`, `firstAt`, `isCrossed`, `depth`, `cancel`) | `CollectionsTest` (`order book keeps price priority and arrival order`) |

## Para profundizar

**Documentación oficial**

- [Collections overview][collections]: interfaces de solo lectura y mutables, tipos y constructores.
- [Collections in Java and Kotlin][j2k-collections]: la guía oficial para quien viene de Java, operación por operación.
- [Constructing collections][constructing] y [Collection operations][operations]: `buildList`, copias, operaciones.
- [Arrays][arrays]: arreglos primitivos, `contentEquals`, cuándo usar arreglos.
- [Map-specific operations][map-ops]: `getValue`, `getOrElse`, `withDefault`, filtros y escrituras sobre mapas.
- [Ordering][ordering]: orden natural, `compareBy`, `sortedWith`, estabilidad.
- [JEP 431: Sequenced Collections][jep431]: el cambio de Java 21 detrás de la trampa de `removeFirst`.
- [`kotlinx.collections.immutable`][kx-immutable]: colecciones inmutables y persistentes.

**Lecturas**

- *Effective Java*, 3.ª edición (J. Bloch), ítem 50 ("Make defensive copies when needed"): la copia defensiva que
  muestra `readOnlyIsNotImmutable`.
- [Capacitación de Kotlin para programadores 2: conceptos básicos][bootcamp-basics] (Google) *(es)*: listas, arreglos y
  bucles en un codelab.

**Videos**

- [Colecciones en Kotlin: List, Set y Map][colecciones-es] *(es)*: las tres interfaces y sus versiones mutables.
- [Immutable and Persistent Lists][typealias-clip] (TypeAlias Show): `kotlinx.collections.immutable` y por qué solo
  lectura no alcanza.

## Estado

- **Hecho:** código en el caso Mercado (B0′ tanda 4) y 9 pruebas; 24 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ `HashMap` propio).

[collections]: https://kotlinlang.org/docs/collections-overview.html
[j2k-collections]: https://kotlinlang.org/docs/java-to-kotlin-collections-guide.html
[constructing]: https://kotlinlang.org/docs/constructing-collections.html
[operations]: https://kotlinlang.org/docs/collection-operations.html
[arrays]: https://kotlinlang.org/docs/arrays.html
[jep431]: https://openjdk.org/jeps/431
[kx-immutable]: https://github.com/Kotlin/kotlinx.collections.immutable
[bootcamp-basics]: https://developers.google.com/codelabs/kotlin-bootcamp-basics?hl=es-419
[colecciones-es]: https://www.youtube.com/watch?v=WenqxF-RIyI
[typealias-clip]: https://www.youtube.com/watch?v=sGjeknOcqEo
[map-ops]: https://kotlinlang.org/docs/map-operations.html
[ordering]: https://kotlinlang.org/docs/collection-ordering.html
