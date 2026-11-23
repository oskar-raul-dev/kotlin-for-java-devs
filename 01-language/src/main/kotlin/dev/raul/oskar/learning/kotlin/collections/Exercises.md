# Ejercicios: colecciones

> **Lección:** `collections` · **Dónde se resuelven:** `collections/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../collections/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.collections.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.7]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 08, 09, 10. **Entrevista (🎤):** 02, 11, 18, 25, 30, 34.

> Datos de apoyo para varios ejercicios: `data class Trade(val account: String, val ticker: String, val day: LocalDate, val volume: Long)`
> (operaciones de las cuentas de clientes en el Mercado).

## 🟢 Fáciles

**01 🟢 🎯 Contador con mapa.** Contar operaciones por ticker con un `MutableMap` y `getOrDefault`; luego con `getOrPut`;
luego con `groupingBy { }.eachCount()`. Las tres deben dar lo mismo.
- Firma: `fun countByTicker(trades: List<Trade>): Map<String, Int>`

**02 🟢 🎯 🎤 Solo lectura no es inmutable.** Una clase que expone `val items: List<String>` respaldada por un `MutableList`
privado: demuestra con una prueba que un cast a `MutableList` la modifica desde fuera, y arréglalo con `toList()`.

**03 🟢 Acceso seguro.** `getOrNull`, `firstOrNull { }`, `getOrElse`: el tercer elemento, el primero par y el índice 10 con valor por defecto.

**04 🟢 Sets.** Tickers que operó una cuenta, tickers en común entre dos cuentas y tickers nuevos de hoy respecto al histórico.
- Firma: `fun newTickers(history: List<Trade>, today: List<Trade>, account: String): Set<String>`

**05 🟢 Ordenar por varios campos.** Eventos por día ascendente y monto descendente con `sortedWith(compareBy<Event> { … }.thenByDescending { … })`.

**06 🟢 Arrays frente a listas.** `IntArray` contra `List<Int>`: igualdad (`==` contra `contentEquals`), `toString` y conversión en ambos sentidos.

**07 🟢 Mapa ordenado.** Pasar un `Map<LocalDate, Int>` a `TreeMap` y obtener el primer y último día, y los días de una semana con `subMap`.

**25 🟢 🎤 `removeFirst` y el JDK 21.** Vaciar una cola de órdenes pendientes en orden de llegada.
- Firma: `fun drain(pending: MutableList<String>): List<String>` (con `removeFirstOrNull()` o `removeAt(0)`, no con
  `removeFirst()`)
- Casos: lista con tres órdenes, vacía; la lista original queda vacía. Explica en un comentario por qué
  `removeFirst()` compilado contra JDK 21 da `NoSuchMethodError` en JDK 17 o en Android, y cómo lo verías con `javap -c`.

**26 🟢 `[1.6]` `buildList` y `buildMap`.** Un resumen de operaciones con líneas condicionales y el volumen por ticker.
- Firmas: `fun tradeSummary(trades: List<Trade>): List<String>` (con `buildList`) y
  `fun volumeByTicker(trades: List<Trade>): Map<String, Long>` (con `buildMap`)
- Casos: lista vacía (solo el encabezado), varios tickers; el resultado es de solo lectura: hacerle cast a
  `MutableList` y agregar lanza `UnsupportedOperationException`.

**27 🟢 `[1.7]` `min()` frente a `minOrNull()`.** El ticker con el cierre más bajo.
- Firma: `fun cheapestTicker(closes: Map<String, BigDecimal>): String?` (con `minByOrNull`)
- Casos: varios, empate (el primero), vacío → `null`; demuestra que `closes.values.min()` con un mapa vacío lanza
  `NoSuchElementException`. Explica en un comentario por qué `min()` cambió de significado entre 1.4 y 1.7.

**28 🟢 El orden de los mapas.** Los tickers en el orden en que aparecen, con `mutableMapOf`, `HashMap` y `TreeMap`.
- Firma: `fun firstSeen(trades: List<Trade>, kind: String): List<String>` (`kind` en `"linked"`, `"hash"`, `"tree"`)
- Casos: `mutableMapOf<String, Int>() is LinkedHashMap`; `"linked"` respeta el orden de llegada y `"tree"` el
  alfabético; para `"hash"` la prueba solo compara como conjunto. Explica en un comentario qué prueba se vuelve frágil.

**29 🟢 `+` y `-` sobre colecciones.** Actualizar una watchlist sin mutar la original.
- Firma: `fun updated(watchlist: Set<String>, added: List<String>, removed: List<String>): Set<String>`
- Casos: agregar uno que ya estaba, quitar uno que no estaba, la original no cambia. Deja comentado en la prueba por
  qué `watchlist += "ACME"` es ambiguo si `watchlist` es un `var` de tipo `MutableSet`.

## 🟡 Medios

**08 🟡 🎯 Historial por cuenta.** De una lista plana a `Map<String, List<Trade>>` ordenado por día dentro de cada cuenta.
- Firma: `fun historyByAccount(trades: List<Trade>): Map<String, List<Trade>>`

**09 🟡 🎯 Conteo por ticker y día.** `Map<String, Map<LocalDate, Int>>`: cuántas operaciones tuvo cada ticker cada día,
con los días sin operaciones **ausentes** (no en cero). Luego una función que pregunte el conteo de un día devolviendo 0 si falta.
- Firmas: `fun dailyCounts(trades: List<Trade>): Map<String, Map<LocalDate, Int>>`, `fun countOn(...): Int`

**10 🟡 🎯 Ventana de 7 días con `TreeMap`.** Para un ticker y un día `d`, la suma de volumen en `[d − 7, d)` usando
`subMap(from, true, to, false)`.
- Firma: `fun windowSum(volumes: TreeMap<LocalDate, Long>, day: LocalDate, days: Long = 7): Long`
- Casos: ventana vacía, día justo en el borde inferior (entra), el propio `d` (no entra).

**11 🟡 🎤 LRU.** Una caché LRU de capacidad `k` con `LinkedHashMap(accessOrder = true)` y `removeEldestEntry`.
- Firma: `class Lru<K, V>(capacity: Int)` con `get`/`put`

**12 🟡 Cola de procesamiento.** `ArrayDeque` como cola FIFO de tareas con reintentos: una tarea que falla vuelve al final hasta 3 veces.

**13 🟡 Top-k por cuenta.** Las `k` operaciones de mayor volumen de cada cuenta con una `PriorityQueue` por cuenta.

**14 🟡 Agrupar en rangos.** Montos a cubetas `[0, 1000)`, `[1000, 5000)`, … con `TreeMap.floorKey`.
- Firma: `fun histogram(amounts: List<Int>, edges: List<Int>): Map<Int, Int>`

**15 🟡 Diferencia de mapas.** Dadas dos versiones de un `Map<String, Int>`, devolver claves agregadas, borradas y cambiadas (con valor viejo y nuevo).

**30 🟡 🎤 Niveles de precio.** Un lado del libro como `TreeMap<Long, Long>` (precio en centavos → cantidad total):
para las compras, de mayor a menor con `reverseOrder()`.
- Firma: `class PriceLevels(descending: Boolean)` con `add(price: Long, qty: Long)`, `remove(price: Long, qty: Long)`,
  `best(): Long?`, `top(n: Int): List<Pair<Long, Long>>` y `quantityUpTo(price: Long): Long` (lo que se puede ejecutar
  hasta ese precio)
- Casos: agregar y quitar hasta que un nivel queda en cero (desaparece), quitar más de lo que hay →
  `IllegalArgumentException`, `top` con menos niveles que `n`. Explica en un comentario por qué `TreeMap` y no
  `PriorityQueue` ni un arreglo ordenado (el ⚡ de `control`).

**31 🟡 `windowed` y `chunked`.** Media móvil de cierres y lotes de órdenes.
- Firmas: `fun movingAverage(closes: List<BigDecimal>, n: Int): List<BigDecimal>` (escala 4, `HALF_EVEN`) y
  `fun batches(orderIds: List<String>, size: Int): List<List<String>>`
- Casos: `n` igual al tamaño (un valor), `n` mayor (vacío), `n <= 0` → `IllegalArgumentException`; 250 órdenes en
  lotes de 100 → `[100, 100, 50]`. Compara `windowed(n, partialWindows = true)`.

**32 🟡 `associate` y sus trampas.** Indexar operaciones por cuenta.
- Firmas: `fun lastTradeByAccount(trades: List<Trade>): Map<String, Trade>` (con `associateBy`) y
  `fun tradesByAccount(trades: List<Trade>): Map<String, List<Trade>>` (con `groupBy`)
- Casos: claves repetidas (en `associateBy` gana la última: pruébalo), `associateWith` para el volumen de cada
  ticker de una lista, `zip` de dos listas de distinto tamaño (se corta en la más corta).

**33 🟡 `EnumMap`.** Volumen por lado (`Side` de `oop`) con un `EnumMap`, que itera en el orden de declaración.
- Firma: `fun volumeBySide(orders: List<Pair<Side, Long>>): Map<Side, Long>`
- Casos: sin ventas (aparece con 0 o no aparece: decide y documenta), el orden de iteración, comparación con
  `groupBy` + `sumOf`. Explica en un comentario qué ganas con `EnumMap` y `EnumSet` frente a `HashMap` y `HashSet`.

**34 🟡 🎤 Un `hashCode` malo.** `class BadKey(val ticker: String)` con `hashCode() = 42` y `equals` correcto.
- Firma: `fun timeLookups(keys: List<Any>): Duration` (meter las claves en un `HashMap` y buscarlas todas)
- Casos: con 20 000 claves, `BadKey` da resultados correctos pero tarda mucho más que `String` (la prueba imprime los
  tiempos y solo afirma que los resultados son correctos); una `BadComparableKey` que además implementa `Comparable`
  vuelve a ser rápida. Explica en un comentario cómo funciona un `HashMap` por dentro y por qué ayuda `Comparable`
  (los bins en árbol desde Java 8).

**35 🟡 Vistas.** `subList`, `keys` y `asReversed()` no copian: cambiar la vista cambia el original.
- Firma: `fun keepLast(trades: MutableList<Trade>, n: Int)` (con `subList(0, k).clear()`)
- Casos: el original queda con las últimas `n`; quitar una clave de `map.keys` quita la entrada; cambiar el original
  después de crear un `subList` y usar la vista → `ConcurrentModificationException`.

## 🟠 Difíciles

**16 🟠 Multimapa inverso.** De `Map<String, Set<String>>` (cuenta → tickers) a `Map<String, Set<String>>` (ticker → cuentas), sin mutables a la vista.

**17 🟠 Intervalos.** Unir intervalos superpuestos `[(1,3), (2,6), (8,10)]` → `[(1,6), (8,10)]` y consultar si un punto cae en alguno con `TreeMap`.

**18 🟠 🎤 Mediana en flujo.** Dos `PriorityQueue` (máx. y mín.) para la mediana de montos a medida que llegan.
- Firma: `class RunningMedian { fun add(x: Int); fun median(): Double }`

**19 🟠 Conteo con decaimiento.** Contador por clave que "olvida": cada día los conteos se multiplican por 0.9. Diseña la estructura para no recorrer todo cada día.

**20 🟠 Índice invertido.** De documentos a `Map<String, SortedSet<Int>>` (palabra → ids) y consulta `AND`/`OR` de varias palabras.

**36 🟠 El matcher con `TreeMap`.** Rehacer el ⚡ de `control` (el libro sobre arreglos ordenados) con
`TreeMap<Long, ArrayDeque<…>>` por lado.
- Firma: `class TreeOrderBook(val ticker: String)` con la misma API que `ArrayOrderBook`
- Casos: las mismas pruebas que el ⚡ de `control` pasan sin cambios (si no lo hiciste, escribe una secuencia de 15
  órdenes con sus ejecuciones esperadas), y una prueba diferencial contra `ArrayOrderBook` con
  `datagen.orderStream(seed, 10_000)`. Explica en un comentario la complejidad de cada operación en las dos versiones.

**37 🟠 Máximo de una ventana.** El precio más alto de los últimos `n` ticks, en O(1) amortizado por tick, con una
`ArrayDeque` monótona.
- Firma: `class SlidingMax(val window: Int)` con `fun add(price: Long): Long` (devuelve el máximo actual)
- Casos: precios crecientes, decrecientes, repetidos, `window = 1`; contra la versión ingenua (`takeLast(n).max()`)
  en 100 000 ticks con semilla fija.

**38 🟠 Cuentas del mismo cliente.** Unir cuentas que comparten una referencia (un teléfono, una dirección) con
unión-búsqueda sobre un `IntArray` (compresión de caminos y unión por rango).
- Firma: `fun clusters(links: List<Pair<String, String>>): List<Set<String>>` (grupos ordenados por tamaño y luego
  alfabéticamente)
- Casos: sin enlaces, una cadena `A-B, B-C`, dos grupos, enlaces repetidos y lazos `A-A`, 100 000 enlaces aleatorios
  (contra un BFS sobre el mismo grafo).

**39 🟠 Días operados con `BitSet`.** Los días del año en que operó cada cuenta como un `java.util.BitSet`.
- Firmas: `fun tradingDays(trades: List<Trade>, year: Int): Map<String, BitSet>`, `fun commonDays(a: BitSet, b: BitSet): Int`,
  `fun longestStreak(days: BitSet): Int`
- Casos: año bisiesto (día 366), sin operaciones, todos los días; compara la memoria aproximada con un
  `Set<LocalDate>` para 1000 cuentas y explica la diferencia en un comentario.

**40 🟠 Multiconjunto ordenado.** Los precios de un día con repeticiones, sobre `TreeMap<Long, Int>`.
- Firma: `class SortedMultiset` con `add(x: Long)`, `remove(x: Long): Boolean`, `count(x: Long): Int`, `size`,
  `first()`, `last()` y `percentile(p: Double): Long` (el valor en la posición `ceil(p · size)`)
- Casos: repetidos, quitar el último de un valor (la clave desaparece), `percentile(0.5)` contra ordenar una lista,
  vacío → `NoSuchElementException`, `p` fuera de `(0, 1]` → `IllegalArgumentException`.

## 🔴 Muy difíciles

**21 🔴 Ventana deslizante eficiente.** Para cada día de un año y cada ticker, la suma de volumen de los 30 días anteriores en **O(días × tickers)**
total, no O(días × 30): sumas acumuladas (prefix sums) por ticker. Compara con la versión ingenua en una prueba.

**22 🔴 Colección inmutable persistente.** Una lista enlazada inmutable con `prepend`, `head`, `tail`, `map` y compartición estructural;
prueba que versiones viejas no cambian.

**23 🔴 Mapa con expiración.** `ExpiringMap<K, V>(ttl: Duration, clock: Clock)` que expira entradas de forma perezosa en `get` y activa
con una `PriorityQueue` por vencimiento; seguro entre hilos.

**24 🔴 Trie con autocompletado.** Insertar 100 000 ciudades, autocompletar por prefijo devolviendo las `k` más frecuentes.

## ⚡ Saiyajin

**41 ⚡ `HashMap` propio.** Un mapa de direccionamiento abierto (sondeo lineal) o de listas por cubeta, a elegir, con
redimensionamiento al pasar un factor de carga, que implementa `MutableMap<K, V>` y se usa con claves del caso
(tickers `String` y `TransferId` de `oop`).
- Firma: `class OpenHashMap<K, V>(initialCapacity: Int = 16, val loadFactor: Double = 0.75) : MutableMap<K, V>`
  (puedes partir de `AbstractMutableMap`); `entries`, `keys` y `values` deben reflejar el mapa y permitir quitar.
- Bordes: claves `null` (decide y documenta), borrar en direccionamiento abierto sin romper las cadenas de sondeo
  (lápidas o reubicación), redimensionar a mitad de una iteración (→ `ConcurrentModificationException`).
- **Se verifica con:** una prueba diferencial contra `java.util.HashMap` con 100 000 operaciones aleatorias
  (`put`, `get`, `remove`, `containsKey`, iteración) y semilla fija, comparando el resultado de cada paso; una prueba
  con claves de `hashCode` constante (correcta aunque lenta); `equals`/`hashCode` del mapa iguales a los de un
  `HashMap` con el mismo contenido; y una medición con `measureTime` `[1.9]` frente al del JDK, impresa sin afirmar
  tiempos.
