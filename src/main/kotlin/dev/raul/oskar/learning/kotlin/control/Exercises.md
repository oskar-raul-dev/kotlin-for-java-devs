# Ejercicios: condicionales, bucles y algoritmos

> **Lección:** `control` · **Dónde se resuelven:** `control/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../control/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew test --tests '*.control.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 11.

## 🟢 Fáciles

**01 🟢 🎯 Tramo de monto.** Clasificar un pedido por monto con `when` y rangos: `< 1000` → `SMALL`, `1000..9999` → `MEDIUM`,
`10000..99999` → `LARGE`, más → `JUMBO`; negativo → excepción.
- Firma: `fun bracket(amount: Int): String`
- Casos: los bordes 999, 1000, 9999, 10000, 99999, 100000, −1.

**02 🟢 🎯 Tramo de edad.** Edad a categoría `"18-25"`, `"26-35"`, `"36-50"`, `"51-65"`, `"65+"`;
menor de 18 o `null` → `"unknown"`.
- Firma: `fun ageBucket(age: Int?): String`
- Casos: todos los bordes, `null`, 17, 200.

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

## 🟡 Medios

**08 🟡 🎯 Primer índice que cumple.** Devolver el índice del primer elemento mayor que un umbral, o −1, usando
`for ((i, x) in xs.withIndex())` y `return` temprano. Luego reescríbelo con `indexOfFirst`.
- Firma: `fun firstAbove(xs: List<Double>, threshold: Double): Int`

**09 🟡 Matriz en espiral.** Recorrer una `Array<IntArray>` en espiral.
- Firma: `fun spiral(m: Array<IntArray>): List<Int>`
- Casos: 3×3, 3×4, 1×N, N×1, vacía.

**10 🟡 Búsqueda del primer y último.** En un `IntArray` ordenado con repetidos, el primer y último índice de un valor, en O(log n).
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

## 🟠 Difíciles

**16 🟠 Merge sort y quick sort.** Implementar ambos sobre `IntArray`; probar con 1000 arreglos aleatorios
(semilla fija) contra `sorted()`.
- Firmas: `fun mergeSort(a: IntArray): IntArray`, `fun quickSort(a: IntArray)` (en sitio)

**17 🟠 Subarreglo de suma máxima.** Kadane: la suma y los índices.
- Firma: `fun kadane(xs: IntArray): Triple<Long, Int, Int>`
- Casos: todos negativos, uno solo, mezcla.

**18 🟠 Islas.** Contar islas de `1` en una grilla (4 vecinos) con BFS usando `ArrayDeque`.
- Firma: `fun islands(grid: Array<IntArray>): Int`

**19 🟠 Rachas en días.** Dada una lista de días (`Int`) con eventos, la racha más larga de días consecutivos.
- Firma: `fun longestStreak(days: List<Int>): Int`
- Casos: repetidos, desordenados, vacía. Hazlo en O(n) con un `HashSet`.

**20 🟠 Torres de Hanói.** Lista de movimientos recursiva; verifica que son `2^n − 1` y que nunca se pone uno grande sobre uno chico.
- Firma: `fun hanoi(n: Int): List<Pair<Char, Char>>`

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
