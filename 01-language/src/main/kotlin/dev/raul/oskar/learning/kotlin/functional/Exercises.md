# Ejercicios: programación funcional y pipelines

> **Lección:** `functional` · **Dónde se resuelven:** `functional/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../functional/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.functional.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 09, 10, 16.

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

**07 🟢 Scope functions.** Reescribir un bloque con variables temporales usando `apply`, `also` y `let`, y explicar en un comentario cuál devuelve qué.

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

**15 🟡 Secuencias.** Encontrar el primer día con más de `k` ticks en una secuencia infinita de días generada; demostrar con
un contador que no se evaluó de más.

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

**20 🟠 Transductor manual.** Escribe tu propia versión de `map`, `filter` y `take` como extensiones `inline` sobre `Iterable` y como extensiones
sobre `Sequence` (con `sequence { yield(…) }`). Prueba que la versión `Sequence` es perezosa.

## 🔴 Muy difíciles

**21 🔴 Either propio.** `sealed class Either<out L, out R>` con `map`, `flatMap`, `fold` y un `either { }` que permita escribir validaciones
encadenadas sin anidar. Úsalo para validar un tick crudo del feed (`ticks.json`) cortando en el primer error.

**22 🔴 Validación aplicativa.** Como el anterior pero acumulando **todos** los errores (`Validated`), y compara con `Either` en el mismo caso.

**23 🔴 Parser combinators.** `Parser<T> = (String, Int) -> Result?` con `char`, `many`, `seq`, `or`, `map`; con ellos, parsea fechas
`MM/dd/yyyy` y expresiones aritméticas.

**24 🔴 Inmutabilidad y rendimiento.** Compara (JMH no está: usa `measureTimeMillis` con calentamiento) un pipeline con listas intermedias,
uno con `Sequence` y uno con bucle mutable sobre 10 millones de elementos. Escribe en un comentario cuándo elegirías cada uno en un PR.
