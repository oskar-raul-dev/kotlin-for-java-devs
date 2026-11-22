# 07 · `functional` — Funciones, lambdas, scope functions, pipelines

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.functional` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--functional--funciones-lambdas-scope-functions-pipelines) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/functional/FunctionalTest.kt)

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

## Estado

- **Hecho:** código en el caso Mercado (B0′ tanda 4) y 9 pruebas; enunciados ya sobre `Tick`; 24 ejercicios (6 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ librería de operadores de `Sequence`).
