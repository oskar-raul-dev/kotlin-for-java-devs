# 01 · `basics` — Tipos, números, identidad

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.basics` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#01--basics--tipos-números-identidad) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/basics/BasicsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=basics
./gradlew -p 01-language test --tests '*.basics.*'
```

## Recorrido sugerido

1. `Numbers.kt`: conversiones explícitas (`widen`), división entera (`integerDivision`, `realDivision`), overflow
   (`overflowingAdd`, `checkedAdd`), `toIntOrNull` (`parseOrDefault`), `BigDecimal` desde `Double` frente a desde
   `String` (`fromDouble`, `fromText`), operadores y redondeo (`commission`), escala (`sameAmount`), `Char` y
   literales.
2. `Money.kt`: el dinero del Banco. `data class` (no `value class`: son dos propiedades), escala de la moneda
   validada en el `init` y normalizada en las fábricas (`of`, `ofCents`, `zero`), `HALF_EVEN` por defecto,
   operadores (`plus`, `minus`, `times`, `unaryMinus`) y `compareTo` que exigen la misma moneda.
3. `Identity.kt`: `==` frente a `===`, `Nothing` (`fail`), `when` por tipo con smart cast (`describe`), `UInt`.
4. `BasicsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Numbers.kt` | Números sin conversiones implícitas, overflow, `BigDecimal` | `widen`, `integerDivision`, `realDivision`, `overflowingAdd`, `checkedAdd`, `parseOrDefault`, `fromDouble`, `fromText`, `commission`, `sameAmount`, `nextChar`, `codeOf` | `BasicsTest` (`integer division…`, `int overflows…`, `toIntOrNull…`, `BigDecimal equals…`, `BigDecimal from a double…`, `commission uses banker rounding…`, `chars are not numbers`) |
| `Money.kt` | Dinero con moneda y escala fija | `Money`, `USD`, `EUR`, `of`, `ofCents`, `zero`, `isZero`, `isNegative` | `BasicsTest` (`money factories normalize the scale…`, `money operators require the same currency`) |
| `Identity.kt` | Igualdad, `Any`/`Unit`/`Nothing`, smart casts | `Point`, `structurallyEqual`, `sameReference`, `fail`, `describe`, `unsignedMax` | `BasicsTest` (`double equals…`, `describe uses smart casts`, `nothing-returning…`) |

`Money` es el tipo de dinero de todo `01-language`: `oop`, `exceptions` y `json` lo importan de aquí.

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 3) y 12 pruebas; 25 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 6 (enunciados al caso); B2 (ejercicios a la escala nueva, 🎤 y ⚡ calculadora de precisión
  arbitraria).
