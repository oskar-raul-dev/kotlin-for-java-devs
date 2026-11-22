# 01 · `basics` — Tipos, números, identidad

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.basics` · **Caso:** pedidos (provisional) → Banco
**Enlaces:** [temario](../../Temario.md#01--basics--tipos-números-identidad) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/basics/BasicsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=basics
./gradlew -p 01-language test --tests '*.basics.*'
```

## Recorrido sugerido

1. `Numbers.kt`: conversiones explícitas (`widen`), división entera (`integerDivision`, `realDivision`), overflow
   (`overflowingAdd`, `checkedAdd`), `toIntOrNull` (`parseOrDefault`), `BigDecimal` con operadores (`money`,
   `addTax`, `sameAmount`), `Char` (`nextChar`, `codeOf`) y literales.
2. `Identity.kt`: `==` frente a `===` (`structurallyEqual`, `sameReference`), `Nothing` (`fail`), `when` por tipo
   con smart cast (`describe`), `UInt` (`unsignedMax`).
3. `BasicsTest.kt`: una prueba por trampa.
4. `Main.kt`: la demo.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Numbers.kt` | Números sin conversiones implícitas, overflow, dinero con `BigDecimal` | `widen`, `integerDivision`, `realDivision`, `overflowingAdd`, `checkedAdd`, `parseOrDefault`, `money`, `addTax`, `sameAmount`, `nextChar`, `codeOf` | `BasicsTest` (`integer division…`, `int overflows…`, `toIntOrNull…`, `BigDecimal equals…`, `tax uses banker rounding`, `chars are not numbers`) |
| `Identity.kt` | Igualdad, `Any`/`Unit`/`Nothing`, smart casts | `Point`, `structurallyEqual`, `sameReference`, `fail`, `describe`, `unsignedMax` | `BasicsTest` (`double equals…`, `describe uses smart casts`, `nothing-returning…`) |

## Estado

- **Hecho:** código y 9 pruebas; 25 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 3 (`BigDecimal` suelto → `Money` del Banco); B2 (ejercicios a la escala nueva, 🎤 y ⚡
  calculadora de precisión arbitraria).
