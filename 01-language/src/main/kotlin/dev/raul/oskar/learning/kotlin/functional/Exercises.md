# Ejercicios: programación funcional y pipelines

> **Lección:** `functional` · **Dónde se resuelven:** `functional/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../functional/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.functional.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.4]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 08, 09, 10, 16. **Entrevista (🎤):** 07, 15, 20, 25, 26, 30.

> Varios ejercicios usan `Tick` y `sampleTicks` de `Pipelines.kt` (ticks ya limpios del Mercado), o el feed
> `ticks.json` que lee la lección `json`.
> Regla para todos: **sin `var` ni bucles** salvo que el enunciado lo pida.

## 🟢 Fáciles

**01 🟢 🎯 `map`, `filter`, `sumOf`.** El volumen total de un ticker en los ticks con precio mayor que un umbral, en una sola
expresión.
- Firma: `fun bigVolume(ticks: List<Tick>, ticker: String, minPrice: BigDecimal): Long`

**02 🟢 🎯 `mapNotNull`.** De `List<String?>` con fechas de liquidación `MM/dd/yyyy` a la lista de años válidos, descartando
nulos y basura.
- Firma: `fun years(dates: List<String?>): List<Int>`

**03 🟢 Referencias a funciones.** Reescribir tres lambdas como referencias (`::isPrime`, `String::length`, `Tick::volume`).

**04 🟢 `any`, `all`, `none`, `count`.** ¿Algún volumen cero? ¿Todos del mismo ticker? ¿Cuántos el día 1?

**05 🟢 `associateBy` y `associateWith`.** El último tick de cada ticker; cada ticker con su volumen total.

**06 🟢 `zip` y `zipWithNext`.** Pares (día, precio) y la diferencia entre precios consecutivos de un ticker.

**07 🟢 🎤 Scope functions.** Reescribir un bloque con variables temporales usando `apply`, `also` y `let`, y explicar en un comentario cuál devuelve qué.

**25 🟢 🎤 `List`, `Sequence` y `Stream`.** El mismo pipeline (filtrar por ticker, mapear al volumen, tomar 2) de tres
formas, registrando en una lista cada paso que se ejecuta.
- Firmas: `fun withList(ticks: List<Tick>, log: MutableList<String>): List<Long>`, `fun withSequence(…)` y
  `fun withStream(…)` (con `ticks.stream()`)
- Casos: las tres dan lo mismo; el registro de la lista hace todos los `filter` antes de los `map`, el de la
  secuencia va elemento por elemento y se detiene al tener 2. Explica en un comentario cuándo eliges cada una (tamaño,
  paralelismo, interoperar con Java).

**26 🟢 🎤 Lambda con receptor.** Armar una fila CSV con un bloque cuyo `this` es la lista de campos.
- Firma: `fun csvRow(block: MutableList<String>.() -> Unit): String`
- Casos: `csvRow { add("ACME"); add("10"); if (true) add("100.00") }` → `"ACME,10,100.00"`, un campo con coma va entre
  comillas, bloque vacío → `""`. Explica en un comentario la diferencia entre `(T) -> Unit` y `T.() -> Unit` y por qué
  `apply` y `buildString` tienen la segunda forma.

**27 🟢 `sumOf` que desborda.** Sumar volúmenes que caben en `Int` pero cuya suma no.
- Firmas: `fun totalNaive(volumes: List<Int>): Int` (con `sumOf { it }`) y `fun total(volumes: List<Int>): Long`
- Casos: `[Int.MAX_VALUE, 1]` → la ingenua da negativo y la buena `2147483648`; lista vacía → 0. Explica en un
  comentario cómo elige `sumOf` la sobrecarga.

**28 🟢 `groupingBy` frente a `groupBy`.** Ticks y volumen por ticker sin crear una lista por grupo.
- Firmas: `fun countByTicker(ticks: List<Tick>): Map<String, Int>` (con `groupingBy { }.eachCount()`) y
  `fun volumeByTickerFold(ticks: List<Tick>): Map<String, Long>` (con `groupingBy { }.fold(0L) { … }`)
- Casos: iguales a los de `groupBy` + `mapValues` sobre `sampleTicks`; lista vacía → mapa vacío.

**29 🟢 `takeWhile` y `dropWhile`.** Precios hasta la primera caída, y lo que viene después.
- Firma: `fun untilFirstDrop(prices: List<BigDecimal>): Pair<List<BigDecimal>, List<BigDecimal>>`
- Casos: siempre subiendo (todo en la primera), cae en el segundo, vacía, precios iguales (no es caída). Pista:
  `zipWithNext` para encontrar el punto de corte.

## 🟡 Medios

**08 🟡 🎯 `groupBy` + `mapValues`.** Precio promedio por ticker, redondeado a 2 decimales con `BigDecimal` (`HALF_EVEN`).
- Firma: `fun averagePriceByTicker(ticks: List<Tick>): Map<String, BigDecimal>`

**09 🟡 🎯 `flatMap`.** Cada tick genera varias etiquetas (`"ticker:ACME"`, `"size:large"` o `"size:small"` según el volumen,
`"band:100-110"` según el precio en tramos de 10); producir todos los pares (etiqueta, día) y contarlos.
- Firma: `fun tagDayCounts(ticks: List<Tick>): Map<Pair<String, Int>, Int>`

**10 🟡 🎯 `fold` con acumulador rico.** En una sola pasada: mínimo, máximo, suma y cantidad (`data class Stats`).
- Firma: `fun stats(xs: List<Int>): Stats?` (lista vacía → `null`)

**11 🟡 Composición.** `compose(vararg fs: (String) -> String)` que aplica `trim`, `lowercase` y quita tildes en orden; prueba el orden.

**12 🟡 Currying y aplicación parcial.** Una regla `(minVolume: Long) -> (Tick) -> Boolean` y una lista de reglas configuradas.

**13 🟡 Memoización recursiva.** Fibonacci memoizado con un `HashMap` capturado; compara llamadas reales contra la versión ingenua.

**14 🟡 `Result` encadenado.** Parsear, validar y convertir una línea `ticker,day,price,volume` en un `Tick` con `runCatching`,
`mapCatching` y `recover`;
devolver la lista de buenos y el mensaje de cada malo.

**15 🟡 🎤 Secuencias.** Encontrar el primer día con más de `k` ticks en una secuencia infinita de días generada; demostrar con
un contador que no se evaluó de más.

**30 🟡 🎤 Lambdas que capturan una `var`.** En Kotlin no hay "effectively final": una lambda puede capturar y cambiar
una `var`.
- Firmas: `fun counterPair(): Pair<() -> Int, () -> Int>` (dos lambdas que incrementan y leen la misma `var`) y
  `fun lambdasInLoop(): List<() -> Int>` (una por vuelta de `for (i in 0..2)`)
- Casos: las dos lambdas comparten el estado; las del bucle devuelven `0, 1, 2`; tres lambdas que capturan una sola
  `var` cambiada después del bucle devuelven todas el último valor. Explica en un comentario qué es `Ref.IntRef`
  (míralo con `javap`) y por qué una lambda así no es segura entre hilos.

**31 🟡 VWAP.** El precio promedio ponderado por volumen de cada ticker: `Σ(precio × volumen) / Σ volumen`, escala 4,
`HALF_EVEN`, en una pasada con `fold`.
- Firma: `fun vwap(ticks: List<Tick>): Map<String, BigDecimal>`
- Casos: un ticker con un solo tick (su precio), dos ticks con volúmenes distintos, un ticker con volumen total cero
  (no aparece), contra calcularlo a mano para `sampleTicks`.

**32 🟡 `[1.4]` `runningFold` y la caída máxima.** El saldo después de cada movimiento y la mayor caída desde un máximo.
- Firmas: `fun runningBalance(opening: BigDecimal, movements: List<BigDecimal>): List<BigDecimal>` y
  `fun maxDrawdown(equity: List<BigDecimal>): BigDecimal`
- Casos: `runningBalance(100, [10, -30, 5])` → `[100, 110, 80, 85]`; `maxDrawdown([100, 120, 90, 130, 100])` → `30`;
  siempre subiendo → `0`; lista vacía. Compara `runningFold` con `scan` y `runningReduce`.

**33 🟡 Media móvil con huecos.** La media de 7 días de calendario, no de 7 elementos: si faltan días, la ventana
tiene menos datos.
- Firma: `fun movingAverage7(closes: Map<Int, BigDecimal>): Map<Int, BigDecimal>` (día → media de los cierres en
  `[día − 6, día]`, escala 4)
- Casos: 10 días seguidos (igual que `windowed(7)` desde el día 7), días con huecos (difiere de `windowed`: la prueba
  lo muestra), un solo día. Decide y documenta si hay media cuando la ventana tiene menos de 7 días.

**34 🟡 Decoradores de funciones.** Funciones que reciben una función y devuelven otra con algo más.
- Firmas: `fun <A, R> timed(clock: () -> Long, f: (A) -> R): (A) -> Pair<R, Long>` y
  `fun <A, R> logged(log: MutableList<String>, name: String, f: (A) -> R): (A) -> R`
- Casos: con un reloj falso que avanza 5 en cada llamada, `timed` mide 5; `logged(timed(…))` y `timed(logged(…))`
  registran lo mismo pero miden distinto; una excepción dentro de `f` se registra y se relanza.

**35 🟡 `return` no local.** El primer tick con volumen cero, con `forEach` y `return` (sale de la función), y con un
`myForEach` propio sin `inline`.
- Firmas: `fun firstZeroVolume(ticks: List<Tick>): Tick?` y
  `fun <T> Iterable<T>.myForEach(action: (T) -> Unit)` (no `inline`)
- Casos: lo encuentra, no hay, lista vacía. Deja comentado en la prueba el `return` que no compila dentro de
  `myForEach` y explica en un comentario por qué `inline` lo permite (la lección `inline` lo profundiza).

## 🟠 Difíciles

**16 🟠 🎯 Estadística en una pasada.** Media y desviación estándar con el algoritmo de Welford dentro de un `fold`, sin
guardar la lista ni restar números grandes.
- Firma: `fun stats(xs: Sequence<Double>): Stats` (con `count`, `mean` y `stdDev`)
- Casos: vacía, un elemento (desviación 0), `[2, 4, 4, 4, 5, 5, 7, 9]` → media 5 y desviación 2, y valores `1e9 + x` sin la
  pérdida de precisión de la fórmula ingenua.

**17 🟠 Días atípicos.** Con lo anterior: para cada ticker, marca los días cuyo volumen se aleja más de `k` desviaciones
de la media de los 5 días de mercado anteriores. Todo con `groupBy`, `windowed` o mapas, sin `var`.
- Firma: `fun outliers(volumes: Map<String, Map<LocalDate, Long>>, k: Double): List<Outlier>`

**18 🟠 Pipeline perezoso con efectos.** Reescribir el 17 con `asSequence()` y medir (con contadores) cuántos cálculos ahorra cuando se pide solo la primera alerta.

**19 🟠 `fun interface` combinables.** `Rule` con `and`, `or`, `not` como funciones infix y un DSL: `rule { volumeAbove(1_000) and not(tickerIn("NUBE")) }`.

**20 🟠 🎤 Transductor manual.** Escribe tu propia versión de `map`, `filter` y `take` como extensiones `inline` sobre `Iterable` y como extensiones
sobre `Sequence` (con `sequence { yield(…) }`). Prueba que la versión `Sequence` es perezosa.

**36 🟠 Cruce de medias.** Señales de compra cuando la media corta cruza la larga hacia arriba, y de venta hacia
abajo, sin `var` (con `windowed` y `zipWithNext`).
- Firma: `fun crossovers(closes: List<BigDecimal>, short: Int, long: Int): List<Pair<Int, Side>>` (índice del cierre
  en que se produce el cruce, y el lado)
- Casos: una serie que sube y baja con dos cruces conocidos, medias que se tocan sin cruzar (no hay señal), menos
  cierres que `long` → vacía, `short >= long` → `IllegalArgumentException`.

**37 🟠 P&L con lotes FIFO.** Ganancia realizada y no realizada de un portafolio: cada venta consume los lotes de
compra más viejos. Todo con `fold` sobre estructuras inmutables (un `Map<String, List<Lot>>`).
- Firma: `fun pnl(fills: List<Fill>, marks: Map<String, BigDecimal>): PnL` con
  `data class Fill(val ticker: String, val side: Side, val quantity: Long, val price: BigDecimal)`,
  `data class PnL(val realized: BigDecimal, val unrealized: BigDecimal)`
- Casos: comprar 10 a 100 y 10 a 110, vender 15 a 120 → realizada `250` (10 × 20 + 5 × 10), no realizada con marca 115
  → `25`; vender más de lo que hay → `IllegalArgumentException`; varios tickers; sin operaciones → ceros.

**38 🟠 Lentes.** Cambiar un campo muy anidado de `data class` (`Bank → Branch → Account → balance`) con cadenas de
`copy`, y luego con una lente propia.
- Firma: `data class Lens<S, A>(val get: (S) -> A, val set: (S, A) -> S)` con `infix fun <B> then(other: Lens<A, B>): Lens<S, B>`
  y `fun modify(s: S, f: (A) -> A): S`
- Casos: las dos formas dan el mismo resultado; el original no cambia; las leyes de las lentes (`set(s, get(s)) == s`,
  `get(set(s, a)) == a`) para tres lentes compuestas.

**39 🟠 Trampolín.** Recursión mutua (`isEven`/`isOdd`) de un millón de niveles sin desbordar la pila: `tailrec` no
sirve entre dos funciones.
- Firma: `sealed interface Trampoline<out T>` con `Done(value)` y `More(next: () -> Trampoline<T>)`, y
  `fun <T> Trampoline<T>.run(): T`
- Casos: `isEven(1_000_000).run() == true`, `isOdd(7)`, la versión recursiva directa lanza `StackOverflowError`; un
  `flatMap` para encadenar y una suma recursiva de una lista de 100 000 elementos.

**40 🟠 Generadores.** Secuencias infinitas con `sequence { }`, `yieldAll` y `generateSequence`.
- Firmas: `fun randomWalk(seed: Long, startCents: Long, tickCents: Long): Sequence<Long>`,
  `fun <T> interleave(a: Sequence<T>, b: Sequence<T>): Sequence<T>` y
  `fun <T> Sequence<T>.dedupConsecutive(): Sequence<T>`
- Casos: `randomWalk` con la misma semilla da lo mismo y nunca baja de un tick; `interleave` de dos de distinto
  largo sigue con la que queda; `dedupConsecutive` sobre una secuencia infinita funciona con `take`; un contador
  prueba que no se genera nada antes de consumir.

## 🔴 Muy difíciles

**21 🔴 Either propio.** `sealed class Either<out L, out R>` con `map`, `flatMap`, `fold` y un `either { }` que permita escribir validaciones
encadenadas sin anidar. Úsalo para validar un tick crudo del feed (`ticks.json`) cortando en el primer error.

**22 🔴 Validación aplicativa.** Como el anterior pero acumulando **todos** los errores (`Validated`), y compara con `Either` en el mismo caso.

**23 🔴 Parser combinators.** `Parser<T> = (String, Int) -> Result?` con `char`, `many`, `seq`, `or`, `map`; con ellos, parsea fechas
`MM/dd/yyyy` y expresiones aritméticas.

**24 🔴 Inmutabilidad y rendimiento.** Compara (JMH no está: usa `measureTimeMillis` con calentamiento) un pipeline con listas intermedias,
uno con `Sequence` y uno con bucle mutable sobre 10 millones de elementos. Escribe en un comentario cuándo elegirías cada uno en un PR.

## ⚡ Saiyajin

**41 ⚡ Librería de operadores de `Sequence`.** Operadores propios y perezosos, escritos con `sequence { }`, para un
backtest simple sobre los ticks de `ticks.json` (léelos con lo de la lección `json`).
- Firmas: `fun <T> Sequence<T>.windowedBy(width: Long, key: (T) -> Long): Sequence<List<T>>` (ventanas por tiempo, no
  por cantidad), `fun <T> Sequence<T>.chunkedWhile(sameChunk: (T, T) -> Boolean): Sequence<List<T>>`,
  `fun <T, R> Sequence<T>.scanWhile(initial: R, op: (R, T) -> R, keepGoing: (R) -> Boolean): Sequence<R>`,
  `fun <T : Comparable<T>> mergeSorted(vararg sources: Sequence<T>): Sequence<T>`, y los indicadores
  `fun Sequence<BigDecimal>.sma(n: Int): Sequence<BigDecimal>` y `fun crossSignals(closes: Sequence<BigDecimal>, short: Int, long: Int): Sequence<Pair<Int, Side>>`.
- Con ellos, un `backtest(ticks, short, long)` que compra y vende en cada cruce y devuelve el P&L.
- **Se verifica con:** pruebas que comparan las velas por minuto y las medias con las de una versión ansiosa
  (`groupBy`, `windowed`) sobre `ticks.json`; un contador que demuestra cuántos elementos consume cada operador
  (pereza: `take(3)` no lee el feed entero); `mergeSorted` de tres secuencias infinitas ordenadas, con `take`; y
  el resultado del backtest fijado para una configuración.
