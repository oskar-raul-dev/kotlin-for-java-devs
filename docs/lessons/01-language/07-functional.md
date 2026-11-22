# 07 · `functional` — Funciones, lambdas, scope functions, pipelines

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.functional` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--functional--funciones-lambdas-scope-functions-pipelines) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/functional/FunctionalTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Reemplazar `Function<>`, Streams y `Collectors` por tipos función y operaciones de colección, entender qué cuesta una
lambda y qué arregla `inline`, saber cuándo conviene `Sequence`, y usar las scope functions sin anidarlas.

## Teoría

### 1. Tipos función en vez de interfaces funcionales

En Java una lambda necesita una interfaz destino (`Function<Integer, Integer>`, `Supplier<T>`, `Consumer<T>`, o una
propia con `@FunctionalInterface`). En Kotlin las funciones tienen tipo propio: `(Int) -> Int`, `() -> T`,
`(T) -> Unit`, y se pasan y devuelven como cualquier valor (`applyTwice`, `multiplier`). Para lo que sí necesita una
interfaz con nombre (una regla de validación, una API para Java) está `fun interface`, que acepta lambdas igual que una
interfaz SAM de Java (`Validator`). La composición (`andThen`) no viene en la stdlib: se escribe como extensión `infix`.
→ [Higher-order functions and lambdas][lambdas], [Functional (SAM) interfaces][fun-interfaces]

### 2. Las lambdas capturan variables, también las `var`

Java solo deja capturar variables *effectively final*. Kotlin deja capturar una `var` y modificarla desde la lambda
(`memoize` guarda un mapa; los contadores de las pruebas usan `var calls`). El compilador envuelve la variable en un
objeto mutable (`Ref.IntRef` y compañía): funciona, pero la lambda ve el valor del momento en que **corre**, no el del
momento en que se creó, y compartir esa `var` entre hilos es una condición de carrera.

### 3. `inline`: por qué `map` y `filter` no cuestan una lambda

Una lambda que se guarda es un objeto (desde 2.0, creado con `invokedynamic`, como en Java). Pero `map`, `filter`,
`forEach`, `let`, `apply` y casi todas las funciones de orden superior de la stdlib son `inline`: el compilador copia su
cuerpo y el de la lambda en el llamador, así que no hay objeto ni llamada virtual. Por eso un `return` dentro de un
`forEach` sale de la función que lo contiene (`02 · control`), y por eso `reified` funciona (`filterIsType`): dentro de
una función `inline` el tipo genérico se conoce. → [Inline functions][inline]

### 4. Colecciones ansiosas, `Sequence` perezosa, `Stream` de Java

`list.map { }.filter { }` sobre una `List` es **ansioso**: cada paso crea una lista intermedia completa. Es lo más rápido
para colecciones chicas y lo más fácil de depurar. `asSequence()` lo vuelve **perezoso**: cada elemento pasa por toda la
cadena antes del siguiente, y lo que no se pide no se calcula (`firstSquaresAbove` lo demuestra con un contador). La
comparación con `Stream` de Java:

| | `List` (Kotlin) | `Sequence` (Kotlin) | `Stream` (Java) |
|---|---|---|---|
| Evaluación | Ansiosa | Perezosa | Perezosa |
| Paralelismo | No | No | `parallel()` |
| Terminal obligatoria | No (cada paso es un resultado) | Sí (`toList`, `sum`, `first`…) | Sí (`collect`…) |
| Para qué | El caso normal | Cadenas largas, datos grandes, infinitos | Lo mismo en Java, y paralelo |

`groupBy { }` materializa una lista por grupo; para contar o sumar alcanza `groupingBy { }.eachCount()` o `fold`, que no
la crean (`ticksPerTicker`). → [Sequences][sequences], [Grouping][grouping]

### 5. `fold`: estado acumulado sin `var`

`fold(inicial) { acc, x -> … }` reduce una colección con un acumulador; a diferencia de `reduce`, funciona con una
colección vacía. Con un acumulador rico se calcula todo en una pasada: `candleOf` arma una vela OHLC (apertura, máximo,
mínimo, cierre, volumen) recorriendo los ticks una vez. Es el reemplazo idiomático de un bucle con varias `var`, y la base
de la estadística en una pasada de la ruta 🎯.

### 6. Scope functions: cuál devuelve qué

Las cinco hacen lo mismo (ejecutar un bloque con un objeto) y se distinguen por dos preguntas: ¿el objeto es `this` o
`it`?, ¿devuelven el objeto o el resultado del bloque? (`scopeFunctions`, `summary`).

| | `this` | `it` |
|---|---|---|
| **Devuelve el objeto** | `apply` (configurar) | `also` (efecto lateral: log, validación) |
| **Devuelve el resultado** | `run`, `with` (calcular con el objeto) | `let` (transformar; `?.let` para nulos) |

La trampa no es elegir mal una: es anidarlas. Con dos niveles, `this` e `it` ya apuntan a objetos distintos según dónde
se lea, y es de lo primero que se marca en una revisión de código. → [Scope functions][scope]

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=functional
./gradlew -p 01-language test --tests '*.functional.*'
```

## Recorrido sugerido

1. `Functional.kt`: tipos función (`applyTwice`), closures (`multiplier`), composición `infix` (`andThen`), currying
   (`add`), lambda con receptor (`html`), `reified` (`filterIsType`), `memoize`, `fold` (`countIf`), `Result`
   (`parseAll`), las cinco scope functions sobre un boleto de orden mutable (`OrderTicket`, `scopeFunctions`, `summary`), `tailrec`
   (`digitSum`) y `fun interface` (`Validator`).
2. `Pipelines.kt`: sobre ticks limpios (`Tick`, `sampleTicks`): `groupBy` con `sumOf` en `Long`, `groupingBy`,
   `maxBy`, `partition`, una vela OHLC en una pasada con `fold` (`candleOf`), `zipWithNext` y secuencias perezosas.
3. `FunctionalTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Functional.kt` | Funciones de orden superior y scope functions | `applyTwice`, `multiplier`, `andThen`, `add`, `html`, `filterIsType`, `memoize`, `countIf`, `parseAll`, `OrderTicket`, `scopeFunctions`, `summary`, `digitSum`, `Validator`, `notBlank`, `shortText` | `FunctionalTest` (`higher order…`, `reified generics`, `memoize…`, `fold and result`, `scope functions`, `fun interface composition`) |
| `Pipelines.kt` | Pipelines sobre colecciones | `Tick`, `sampleTicks`, `volumeByTicker`, `ticksPerTicker`, `highestByTicker`, `splitLarge`, `daysPerTicker`, `Candle`, `candleOf`, `deltas`, `firstSquaresAbove` | `FunctionalTest` (`grouping pipelines`, `a candle in one pass with fold`, `sequences are lazy`) |

## Para profundizar

**Documentación oficial**

- [Higher-order functions and lambdas][lambdas]: tipos función, lambdas, referencias, lambdas con receptor.
- [Inline functions][inline]: qué se copia, `noinline`, `crossinline`, `reified`.
- [Functional (SAM) interfaces][fun-interfaces]: `fun interface` y la conversión de lambdas.
- [Scope functions][scope]: la tabla oficial de las cinco y cuándo usar cada una.
- [Sequences][sequences]: evaluación perezosa y cuándo conviene.
- [Grouping][grouping]: `groupBy`, `groupingBy`, `eachCount`, `fold`.
- [`java.util.stream`][streams] (Java SE 21): el modelo de Java, para comparar.

**Lecturas**

- [Capacitación de Kotlin para programadores 6: manipulación funcional][bootcamp-functional] (Google) *(es)*: lambdas,
  funciones de orden superior y SAM.

**Videos**

- [Kotlin Scope Functions for Expressiveness: let, run, with, apply][scope-video] (no oficial): las scope functions con
  ejemplos.
- [KOTLIN Funciones de Alcance (Let, Run, With, Also, Apply)][scope-es] *(es)*: las cinco, en español.

## Estado

- **Hecho:** código en el caso Mercado (B0′ tanda 4) y 9 pruebas; enunciados ya sobre `Tick`; 24 ejercicios (6 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ librería de operadores de `Sequence`).

[lambdas]: https://kotlinlang.org/docs/lambdas.html
[inline]: https://kotlinlang.org/docs/inline-functions.html
[fun-interfaces]: https://kotlinlang.org/docs/fun-interfaces.html
[scope]: https://kotlinlang.org/docs/scope-functions.html
[sequences]: https://kotlinlang.org/docs/sequences.html
[grouping]: https://kotlinlang.org/docs/collection-grouping.html
[streams]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html
[bootcamp-functional]: https://developers.google.com/codelabs/kotlin-bootcamp-sams?hl=es-419
[scope-video]: https://www.youtube.com/watch?v=d0BYBs4JSsE
[scope-es]: https://www.youtube.com/watch?v=KJdvDejcIdw
