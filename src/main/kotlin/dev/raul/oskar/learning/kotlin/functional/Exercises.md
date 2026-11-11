# Ejercicios: programación funcional y pipelines

> **Lección:** `functional` · **Dónde se resuelven:** `functional/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../functional/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew test --tests '*.functional.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 09, 10, 16.

> Varios ejercicios usan `Txn` y `sampleTxns` de `Pipelines.kt`, o los `OrderEvent` de la lección `json`.
> Regla para todos: **sin `var` ni bucles** salvo que el enunciado lo pida.

## 🟢 Fáciles

**01 🟢 🎯 `map`, `filter`, `sumOf`.** El total de montos de un usuario mayores que un umbral, en una sola expresión.
- Firma: `fun bigTotal(txns: List<Txn>, user: String, threshold: Int): Int`

**02 🟢 🎯 `mapNotNull`.** De `List<String?>` con fechas `MM/dd/yyyy` a la lista de años válidos, descartando nulos y basura.
- Firma: `fun years(dobs: List<String?>): List<Int>`

**03 🟢 Referencias a funciones.** Reescribir tres lambdas como referencias (`::isPrime`, `String::length`, `Txn::amount`).

**04 🟢 `any`, `all`, `none`, `count`.** ¿Algún monto negativo? ¿Todos del mismo estado? ¿Cuántos el día 1?

**05 🟢 `associateBy` y `associateWith`.** Txn por id; cada usuario con su total.

**06 🟢 `zip` y `zipWithNext`.** Pares (día, monto) y la diferencia entre montos consecutivos de un usuario.

**07 🟢 Scope functions.** Reescribir un bloque con variables temporales usando `apply`, `also` y `let`, y explicar en un comentario cuál devuelve qué.

## 🟡 Medios

**08 🟡 🎯 `groupBy` + `mapValues`.** Promedio de monto por estado, redondeado a 2 decimales con `BigDecimal`.
- Firma: `fun averageByState(txns: List<Txn>): Map<String, BigDecimal>`

**09 🟡 🎯 `flatMap`.** Cada evento genera varias categorías (`"state:CA"`, `"type:auto"`, `"age:26-35"`); producir todos los pares
(categoría, día) y contarlos.
- Firma: `fun categoryDayCounts(events: List<Ev>): Map<Pair<String, Int>, Int>`

**10 🟡 🎯 `fold` con acumulador rico.** En una sola pasada: mínimo, máximo, suma y cantidad (`data class Stats`).
- Firma: `fun stats(xs: List<Int>): Stats?` (lista vacía → `null`)

**11 🟡 Composición.** `compose(vararg fs: (String) -> String)` que aplica `trim`, `lowercase` y quita tildes en orden; prueba el orden.

**12 🟡 Currying y aplicación parcial.** Una regla `(threshold: Int) -> (Txn) -> Boolean` y una lista de reglas configuradas.

**13 🟡 Memoización recursiva.** Fibonacci memoizado con un `HashMap` capturado; compara llamadas reales contra la versión ingenua.

**14 🟡 `Result` encadenado.** Parsear, validar y convertir una línea en un `Txn` con `runCatching`, `mapCatching` y `recover`;
devolver la lista de buenos y el mensaje de cada malo.

**15 🟡 Secuencias.** Encontrar el primer día con más de `k` transacciones en una secuencia infinita de días generada; demostrar con
un contador que no se evaluó de más.

## 🟠 Difíciles

**16 🟠 🎯 Estadística en una pasada.** Media y desviación estándar con el algoritmo de Welford dentro de un `fold`, sin
guardar la lista ni restar números grandes.
- Firma: `fun stats(xs: Sequence<Double>): Stats` (con `count`, `mean` y `stdDev`)
- Casos: vacía, un elemento (desviación 0), `[2, 4, 4, 4, 5, 5, 7, 9]` → media 5 y desviación 2, y valores `1e9 + x` sin la
  pérdida de precisión de la fórmula ingenua.

**17 🟠 Días atípicos.** Con lo anterior: para cada estado, marca los días cuyas ventas se alejan más de `k` desviaciones
de la media de los 30 días anteriores. Todo con `groupBy`, `windowed` o mapas, sin `var`.
- Firma: `fun outliers(sales: Map<String, Map<LocalDate, Int>>, k: Double): List<Outlier>`

**18 🟠 Pipeline perezoso con efectos.** Reescribir el 17 con `asSequence()` y medir (con contadores) cuántos cálculos ahorra cuando se pide solo la primera alerta.

**19 🟠 `fun interface` combinables.** `Rule` con `and`, `or`, `not` como funciones infix y un DSL: `rule { amountAbove(1000) and not(stateIn("CA")) }`.

**20 🟠 Transductor manual.** Escribe tu propia versión de `map`, `filter` y `take` como extensiones `inline` sobre `Iterable` y como extensiones
sobre `Sequence` (con `sequence { yield(…) }`). Prueba que la versión `Sequence` es perezosa.

## 🔴 Muy difíciles

**21 🔴 Either propio.** `sealed class Either<out L, out R>` con `map`, `flatMap`, `fold` y un `either { }` que permita escribir validaciones
encadenadas sin anidar. Úsalo para validar un `OrderEvent` acumulando el primer error.

**22 🔴 Validación aplicativa.** Como el anterior pero acumulando **todos** los errores (`Validated`), y compara con `Either` en el mismo caso.

**23 🔴 Parser combinators.** `Parser<T> = (String, Int) -> Result?` con `char`, `many`, `seq`, `or`, `map`; con ellos, parsea fechas
`MM/dd/yyyy` y expresiones aritméticas.

**24 🔴 Inmutabilidad y rendimiento.** Compara (JMH no está: usa `measureTimeMillis` con calentamiento) un pipeline con listas intermedias,
uno con `Sequence` y uno con bucle mutable sobre 10 millones de elementos. Escribe en un comentario cuándo elegirías cada uno en un PR.
