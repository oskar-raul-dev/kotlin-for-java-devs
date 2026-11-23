# Ejercicios: condicionales, bucles y algoritmos

> **Lección:** `control` · **Dónde se resuelven:** `control/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../control/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.control.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[2.2]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 08, 11. **Entrevista (🎤):** 10, 17, 26, 27, 31, 36.

## 🟢 Fáciles

**01 🟢 🎯 Tramo de monto.** Clasificar una transferencia del Banco por monto con `when` y rangos: `< 1000` → `SMALL`, `1000..9999` → `MEDIUM`,
`10000..99999` → `LARGE`, más → `JUMBO`; negativo → excepción.
- Firma: `fun bracket(amount: Int): String`
- Casos: los bordes 999, 1000, 9999, 10000, 99999, 100000, −1.

**02 🟢 🎯 Tramo de volumen.** Títulos operados en el mes a tramo de comisión: `"T1"` (menos de 10 000), `"T2"` (menos de
100 000), `"T3"` (menos de 1 000 000), `"T4"` (el resto); negativo o `null` → `"unknown"`.
- Firma: `fun volumeTier(shares: Long?): String`
- Casos: todos los bordes, `null`, −1, 0.

**03 🟢 Máximo sin `max`.** El mayor de una lista con un `for`; lista vacía → `null`.
- Firma: `fun largest(xs: List<Int>): Int?`

**04 🟢 Tabla de multiplicar.** Devolver las líneas `"7 x 1 = 7"` … `"7 x 10 = 70"` con `for` y `..`.
- Firma: `fun table(n: Int): List<String>`

**05 🟢 Pares en un rango con paso.** Todos los múltiplos de `k` entre `a` y `b` (incluidos), también si `a > b` (en descenso).
- Firma: `fun multiples(a: Int, b: Int, k: Int): List<Int>`
- Casos: `(1, 10, 3)` → `[3, 6, 9]`, `(10, 1, 3)` → `[9, 6, 3]`. Usa `step` y `downTo`.

**06 🟢 Contar dígitos y sumarlos.** Con `while`, para cualquier `Long`, incluso negativo.
- Firma: `fun digitStats(n: Long): Pair<Int, Int>` (cantidad, suma)

**07 🟢 Año bisiesto.** Con un solo `when` sin argumento.
- Firma: `fun isLeap(year: Int): Boolean`
- Casos: 1900 (no), 2000 (sí), 2024 (sí), 2026 (no).

**25 🟢 `[1.9]` El off-by-one al portar.** En Java, `for (int i = 1; i < n; i++)`. Las variaciones de precio entre
ticks consecutivos (en centavos), con `1..<size`.
- Firma: `fun priceChanges(prices: LongArray): LongArray`
- Casos: `[10000, 10001, 9999]` → `[1, -2]`, vacío y un solo precio → vacío. Agrega una prueba que demuestre que
  recorrer `0..prices.size` lanza `ArrayIndexOutOfBoundsException`.

**26 🟢 🎤 Sin ternario.** Describir el estado del libro con `if` como expresión, sin variables mutables.
- Firma: `fun bookState(bidCents: Long?, askCents: Long?): String`
- Casos: `(9998, 10002)` → `"spread 4"`, `(10002, 9998)` y `(10000, 10000)` → `"cruzado"`, falta uno o los dos →
  `"sin precio"`. Reescríbelo con `when` sin argumento. Explica en un comentario por qué Kotlin no tiene `?:` ternario
  (pista: `?:` ya es el Elvis y `if` devuelve valor).

**27 🟢 🎤 `return` dentro de `forEach`.** Dos funciones sobre saldos diarios en centavos:
- Firmas: `fun firstOverdraft(balances: List<Long>): Int?` (índice del primer saldo negativo, con `forEachIndexed` y
  `return`) y `fun sumSkippingNegatives(xs: List<Long>): Long` (con `return@forEach`)
- Casos: sin descubiertos → `null`, el primero negativo → 0, lista vacía. Explica en un comentario a dónde salta cada
  `return` y qué pasaría si `forEach` no fuera `inline`.

**28 🟢 Lado de la orden.** El tag 54 de FIX a texto, con un `when` que tenga varios valores en una rama.
- Firma: `fun side(tag54: String): String`
- Casos: `"1"` → `"BUY"`, `"2"` → `"SELL"`, `"5"` y `"6"` → `"SELL_SHORT"`, cualquier otro → `IllegalArgumentException`
  con el valor en el mensaje (el `else` lanza: el `when` sigue siendo una expresión de tipo `String`).

**29 🟢 Niveles de precio.** Los precios entre dos límites, en centavos, con un tick, usando `step` sobre `Long`.
- Firma: `fun priceLevels(lowCents: Long, highCents: Long, tickCents: Long): List<Long>`
- Casos: `(10000, 10100, 25)` → 5 niveles, `(10000, 10010, 25)` → `[10000]`, `low > high` → vacío (como `10..0`),
  `tick <= 0` → `IllegalArgumentException` (comprueba que `step` ya la lanza).

## 🟡 Medios

**08 🟡 🎯 Primer índice que cumple.** Devolver el índice del primer elemento mayor que un umbral, o −1, usando
`for ((i, x) in xs.withIndex())` y `return` temprano. Luego reescríbelo con `indexOfFirst`.
- Firma: `fun firstAbove(xs: List<Double>, threshold: Double): Int`

**09 🟡 Matriz en espiral.** Recorrer una `Array<IntArray>` en espiral.
- Firma: `fun spiral(m: Array<IntArray>): List<Int>`
- Casos: 3×3, 3×4, 1×N, N×1, vacía.

**10 🟡 🎤 Búsqueda del primer y último.** En un `IntArray` ordenado con repetidos, el primer y último índice de un valor, en O(log n).
- Firma: `fun range(sorted: IntArray, target: Int): IntRange?`

**11 🟡 🎯 Ventana deslizante.** La mayor suma de `k` elementos consecutivos, en O(n).
- Firma: `fun maxWindowSum(xs: IntArray, k: Int): Long`
- Casos: `k` igual al tamaño, `k > size` → excepción, negativos.

**12 🟡 Criba de Eratóstenes.** Primos hasta `n` con un `BooleanArray`.
- Firma: `fun sieve(n: Int): List<Int>`
- Casos: `sieve(30)`, `sieve(1)`, que `sieve(100_000).size == 9592`.

**13 🟡 Anagramas.** ¿Son anagramas dos cadenas? Con un `IntArray(26)` de conteos.
- Firma: `fun isAnagram(a: String, b: String): Boolean`

**14 🟡 Etiquetas.** Encontrar en una matriz la primera celda con valor negativo y salir de los dos bucles con `break@outer`.
Luego hazlo sin etiquetas, con una función y `return`. ¿Cuál se lee mejor?
- Firma: `fun firstNegative(m: Array<IntArray>): Pair<Int, Int>?`

**15 🟡 Merge de ordenados.** Mezclar dos `IntArray` ordenados en uno ordenado, en O(n + m).
- Firma: `fun merge(a: IntArray, b: IntArray): IntArray`

**30 🟡 `[2.2]` Guardas en `when`.** Clasificar un campo leído de un extracto (puede venir como número, como texto o
faltar) con `is … if …`.
- Firma: `fun classify(field: Any?): String`
- Casos: `-500L` → `"debit"`, `500L` → `"credit"`, `0L` → `"zero"`, `"123"` → `"numeric text"`, `"abc"` → `"text"`,
  `null` → `"missing"`, `1.5` → `"other"`. Escríbelo también sin guardas y compara cuál se lee mejor.

**31 🟡 🎤 Búsqueda binaria sin desborde.** El primer índice cuyo precio es mayor o igual que el buscado (el punto de
inserción), con el medio calculado como `(lo + hi) ushr 1`.
- Firmas: `fun lowerBound(sorted: LongArray, target: Long): Int`, `fun mid(lo: Int, hi: Int): Int`
- Casos: vacío → 0, todos menores → `size`, repetidos → el primero, el valor exacto, entre dos valores;
  `mid(Int.MAX_VALUE - 1, Int.MAX_VALUE)` no es negativo (con `(lo + hi) / 2` sí: pruébalo). Compara con
  `java.util.Arrays.binarySearch` y su `-(punto de inserción) - 1`.

**32 🟡 Dos punteros.** En una lista ordenada de movimientos (centavos), los dos cuya suma más se acerca al monto
de una línea del extracto.
- Firma: `fun closestPair(sorted: LongArray, target: Long): Pair<Int, Int>?`
- Casos: suma exacta, sin suma exacta (la más cercana), menos de dos elementos → `null`, empate (el par con el menor
  primer índice). Debe ser O(n).

**33 🟡 `[2.2]` `break` y `continue` en una lambda.** Sumar montos con `forEach` saltando los negativos y parando antes
de pasar un límite, con `continue` y `break` dentro de la lambda (posible desde 2.2 en lambdas `inline`).
- Firma: `fun sumUntilLimit(amounts: List<Long>, limit: Long): Long`
- Casos: `([100, -5, 200, 300], 350)` → 300, límite 0, lista vacía. Escribe la versión de antes de 2.2 (con
  `run` y etiquetas) y compara.

**34 🟡 `tailrec`.** Pasos de Collatz con recursión de cola, y una recursión que no es de cola para comparar.
- Firmas: `tailrec fun collatzSteps(n: Long, steps: Int = 0): Int`, `fun depth(n: Int): Int` (sin `tailrec`)
- Casos: `collatzSteps(27) == 111`, `n <= 0` → `IllegalArgumentException`; `depth(1_000_000)` lanza
  `StackOverflowError`. Explica en un comentario qué genera el compilador con `tailrec` (míralo con `javap`) y por qué
  `1 + depth(n - 1)` no es de cola.

**35 🟡 Velas por cantidad de ticks.** Agrupar precios de `size` en `size` y sacar apertura, máximo, mínimo y cierre
de cada grupo, con bucles y `step` (sin `chunked`).
- Firma: `fun candles(prices: LongArray, size: Int): List<LongArray>` (cada vela es `[open, high, low, close]`)
- Casos: 10 precios de a 4 → 3 velas (la última parcial), `size` mayor que el arreglo → una vela, vacío → ninguna,
  `size <= 0` → `IllegalArgumentException`.

## 🟠 Difíciles

**16 🟠 Merge sort y quick sort.** Implementar ambos sobre `IntArray`; probar con 1000 arreglos aleatorios
(semilla fija) contra `sorted()`.
- Firmas: `fun mergeSort(a: IntArray): IntArray`, `fun quickSort(a: IntArray)` (en sitio)

**17 🟠 🎤 Subarreglo de suma máxima.** Kadane: la suma y los índices.
- Firma: `fun kadane(xs: IntArray): Triple<Long, Int, Int>`
- Casos: todos negativos, uno solo, mezcla.

**18 🟠 Islas.** Contar islas de `1` en una grilla (4 vecinos) con BFS usando `ArrayDeque`.
- Firma: `fun islands(grid: Array<IntArray>): Int`

**19 🟠 Rachas en días.** Dada una lista de días (`Int`) con eventos, la racha más larga de días consecutivos.
- Firma: `fun longestStreak(days: List<Int>): Int`
- Casos: repetidos, desordenados, vacía. Hazlo en O(n) con un `HashSet`.

**20 🟠 Torres de Hanói.** Lista de movimientos recursiva; verifica que son `2^n − 1` y que nunca se pone uno grande sobre uno chico.
- Firma: `fun hanoi(n: Int): List<Pair<Char, Char>>`

**36 🟠 🎤 Comprar y vender.** Con precios diarios, la mayor ganancia con una sola compra y venta, y con tantas como
se quiera (sin tener dos posiciones a la vez). Las dos en O(n).
- Firmas: `fun maxProfit(prices: LongArray): Long`, `fun maxProfitMany(prices: LongArray): Long`
- Casos: `[7, 1, 5, 3, 6, 4]` → 5 y 7, precios en descenso → 0 y 0, vacío y uno solo → 0.

**37 🟠 Hasta un precio mayor.** Para cada día, cuántos días faltan para un precio más alto (0 si no llega), en O(n)
con una pila monótona sobre `ArrayDeque`.
- Firma: `fun daysUntilHigher(prices: LongArray): IntArray`
- Casos: `[73, 74, 75, 71, 69, 72, 76, 73]` → `[1, 1, 4, 2, 1, 1, 0, 0]`, precios iguales (no cuentan como más
  altos), en descenso → todos 0.

**38 🟠 Fusionar ventanas.** Ventanas de mantenimiento del Banco (minutos del día) que se solapan o se tocan se unen.
- Firma: `fun mergeWindows(windows: List<IntRange>): List<IntRange>`
- Casos: desordenadas, una dentro de otra, `[1..3, 4..5]` → `[1..5]` (se tocan), `[1..3, 5..6]` sin cambios,
  vacía, rangos vacíos (`5..1`) se ignoran.

**39 🟠 Transferencias en círculo.** ¿Hay un ciclo en el grafo de transferencias entre cuentas? Devolver uno, con DFS
**iterativo** (una `ArrayDeque` como pila y tres estados por nodo), sin recursión.
- Firma: `fun findCycle(transfers: List<Pair<String, String>>): List<String>?` (`["A", "B", "C", "A"]` o `null`)
- Casos: sin ciclo, un ciclo de tres, un lazo `A → A`, dos componentes con el ciclo en la segunda, un grafo de
  100 000 cuentas en cadena (la versión recursiva desbordaría la pila).

## 🔴 Muy difíciles

**21 🔴 Mochila 0/1.** Programación dinámica con un solo `IntArray`. Devuelve el valor y qué objetos.
- Firma: `fun knapsack(weights: IntArray, values: IntArray, capacity: Int): Pair<Int, List<Int>>`

**22 🔴 N reinas.** Contar soluciones con backtracking y bits.
- Firma: `fun nQueens(n: Int): Int`
- Casos: 1 → 1, 4 → 2, 8 → 92, 10 → 724.

**23 🔴 Dijkstra.** Camino más corto en un grafo con pesos usando `PriorityQueue`, devolviendo distancia y ruta.
- Firma: `fun dijkstra(graph: Map<String, List<Pair<String, Int>>>, from: String, to: String): Pair<Int, List<String>>?`

**24 🔴 Evaluador de expresiones.** `"3 + 4 * (2 - 1)"` → 7 con shunting-yard (dos pilas), con `+ - * /`, paréntesis y menos unario.
- Firma: `fun eval(expr: String): Long`
- Casos: precedencia, asociatividad (`8 - 3 - 2`), paréntesis anidados, división por cero, expresión mal formada.

**40 🔴 Arbitraje de divisas.** Con tipos de cambio entre monedas, encontrar un ciclo cuyo producto sea mayor que 1
(USD → EUR → JPY → USD deja ganancia), con Bellman-Ford sobre los pesos `-ln(tasa)`.
- Firma: `fun findArbitrage(rates: Map<Pair<String, String>, Double>): List<String>?`
- Casos: sin arbitraje → `null`, un ciclo de tres con producto `1.0005` → ese ciclo (empezando en cualquiera de sus
  monedas), un ciclo de dos, tasas que se cancelan exactamente (producto `1.0`, con tolerancia `1e-12`) → `null`.

## ⚡ Saiyajin

**41 ⚡ Matcher por búsqueda binaria.** El libro de órdenes de un ticker sobre arreglos ordenados: los precios de cada
lado en un `LongArray` (compras de mayor a menor, ventas de menor a mayor) y, en cada nivel, una cola de órdenes en
orden de llegada. Cada orden con límite busca su nivel con búsqueda binaria e inserta si no existe; se empareja por
precio-tiempo con bucles y `when`. Las órdenes a mercado consumen lo que haya y el resto se descarta; las
cancelaciones de órdenes ya ejecutadas se ignoran. (En `collections` se rehace con `TreeMap` + `ArrayDeque`.)
- Firma: `class ArrayOrderBook(val ticker: String)` con `fun submit(command: OrderCommand): List<Fill>`,
  `fun bestBid(): Long?`, `fun bestAsk(): Long?` y `data class Fill(val buyId: String, val sellId: String,
  val priceCents: Long, val quantity: Long)`; usa `OrderCommand` de `datagen` (el precio de la ejecución es el de la
  orden que ya estaba en el libro).
- **Se verifica con:** una secuencia fija escrita a mano (unas 15 órdenes con ejecuciones parciales, mercado y
  cancelaciones) cuyas ejecuciones coinciden con una lista esperada; y con `datagen.orderStream(seed, 10_000)`,
  comprobando después de cada comando que el libro nunca queda cruzado (`bestBid < bestAsk`), que los precios de cada
  lado siguen ordenados y que la cantidad ejecutada más la que queda en el libro más la descartada es la que entró.
