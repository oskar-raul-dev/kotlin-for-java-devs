# Ejercicios: colecciones

> **Lección:** `collections` · **Dónde se resuelven:** `collections/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../collections/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.collections.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 09, 10.

> Datos de apoyo para varios ejercicios: `data class Trade(val account: String, val ticker: String, val day: LocalDate, val volume: Long)`
> (operaciones de las cuentas de clientes en el Mercado).

## 🟢 Fáciles

**01 🟢 🎯 Contador con mapa.** Contar operaciones por ticker con un `MutableMap` y `getOrDefault`; luego con `getOrPut`;
luego con `groupingBy { }.eachCount()`. Las tres deben dar lo mismo.
- Firma: `fun countByTicker(trades: List<Trade>): Map<String, Int>`

**02 🟢 🎯 Solo lectura no es inmutable.** Una clase que expone `val items: List<String>` respaldada por un `MutableList`
privado: demuestra con una prueba que un cast a `MutableList` la modifica desde fuera, y arréglalo con `toList()`.

**03 🟢 Acceso seguro.** `getOrNull`, `firstOrNull { }`, `getOrElse`: el tercer elemento, el primero par y el índice 10 con valor por defecto.

**04 🟢 Sets.** Tickers que operó una cuenta, tickers en común entre dos cuentas y tickers nuevos de hoy respecto al histórico.
- Firma: `fun newTickers(history: List<Trade>, today: List<Trade>, account: String): Set<String>`

**05 🟢 Ordenar por varios campos.** Eventos por día ascendente y monto descendente con `sortedWith(compareBy<Event> { … }.thenByDescending { … })`.

**06 🟢 Arrays frente a listas.** `IntArray` contra `List<Int>`: igualdad (`==` contra `contentEquals`), `toString` y conversión en ambos sentidos.

**07 🟢 Mapa ordenado.** Pasar un `Map<LocalDate, Int>` a `TreeMap` y obtener el primer y último día, y los días de una semana con `subMap`.

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

**11 🟡 LRU.** Una caché LRU de capacidad `k` con `LinkedHashMap(accessOrder = true)` y `removeEldestEntry`.
- Firma: `class Lru<K, V>(capacity: Int)` con `get`/`put`

**12 🟡 Cola de procesamiento.** `ArrayDeque` como cola FIFO de tareas con reintentos: una tarea que falla vuelve al final hasta 3 veces.

**13 🟡 Top-k por cuenta.** Las `k` operaciones de mayor volumen de cada cuenta con una `PriorityQueue` por cuenta.

**14 🟡 Agrupar en rangos.** Montos a cubetas `[0, 1000)`, `[1000, 5000)`, … con `TreeMap.floorKey`.
- Firma: `fun histogram(amounts: List<Int>, edges: List<Int>): Map<Int, Int>`

**15 🟡 Diferencia de mapas.** Dadas dos versiones de un `Map<String, Int>`, devolver claves agregadas, borradas y cambiadas (con valor viejo y nuevo).

## 🟠 Difíciles

**16 🟠 Multimapa inverso.** De `Map<String, Set<String>>` (cuenta → tickers) a `Map<String, Set<String>>` (ticker → cuentas), sin mutables a la vista.

**17 🟠 Intervalos.** Unir intervalos superpuestos `[(1,3), (2,6), (8,10)]` → `[(1,6), (8,10)]` y consultar si un punto cae en alguno con `TreeMap`.

**18 🟠 Mediana en flujo.** Dos `PriorityQueue` (máx. y mín.) para la mediana de montos a medida que llegan.
- Firma: `class RunningMedian { fun add(x: Int); fun median(): Double }`

**19 🟠 Conteo con decaimiento.** Contador por clave que "olvida": cada día los conteos se multiplican por 0.9. Diseña la estructura para no recorrer todo cada día.

**20 🟠 Índice invertido.** De documentos a `Map<String, SortedSet<Int>>` (palabra → ids) y consulta `AND`/`OR` de varias palabras.

## 🔴 Muy difíciles

**21 🔴 Ventana deslizante eficiente.** Para cada día de un año y cada ticker, la suma de volumen de los 30 días anteriores en **O(días × tickers)**
total, no O(días × 30): sumas acumuladas (prefix sums) por ticker. Compara con la versión ingenua en una prueba.

**22 🔴 Colección inmutable persistente.** Una lista enlazada inmutable con `prepend`, `head`, `tail`, `map` y compartición estructural;
prueba que versiones viejas no cambian.

**23 🔴 Mapa con expiración.** `ExpiringMap<K, V>(ttl: Duration, clock: Clock)` que expira entradas de forma perezosa en `get` y activa
con una `PriorityQueue` por vencimiento; seguro entre hilos.

**24 🔴 Trie con autocompletado.** Insertar 100 000 ciudades, autocompletar por prefijo devolviendo las `k` más frecuentes.
