# 03 · `strings` — Plantillas, extensiones de `String`, regex

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.strings` · **Caso:** genérico → Ambos
**Enlaces:** [temario](../../Temario.md#01--strings--plantillas-extensiones-de-string-regex) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/strings/StringsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=strings
./gradlew -p 01-language test --tests '*.strings.*'
```

## Recorrido sugerido

1. `Strings.kt`: plantillas (`greeting`), raw strings (`report`), `padEnd`/`padStart` (`row`),
   `substringBefore`/`After` (`domainOf`, `userOf`), `buildString` (`reverseWords`) y **la trampa de `split`
   literal** (`splitLiteral` frente a `splitRegex`).
2. `Regexes.kt`: `matches` frente a `containsMatchIn`, `findAll`, grupos con nombre y `destructured`
   (`parseUsDate`, `yearByName`), `replace` con lambda (`maskDigits`, `maskCard`), `parseKeyValues`.
3. `StringsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Strings.kt` | Extensiones de `String` y la trampa de `split` | `greeting`, `report`, `row`, `domainOf`, `userOf`, `reverseWords`, `isPalindrome`, `charFrequency`, `capitalizeWords`, `sameIgnoringCase`, `splitLiteral`, `splitRegex` | `StringsTest` (`templates and raw strings`, `substring helpers`, `string algorithms`, `split with string is literal, not regex`) |
| `Regexes.kt` | `Regex` de Kotlin | `isEmail`, `hasZip`, `allZips`, `parseUsDate`, `yearByName`, `maskDigits`, `maskCard`, `normalizeSpaces`, `parseKeyValues` | `StringsTest` (`regex validation and extraction`, `named groups and destructuring`, `replace with regex`) |

## Estado

- **Hecho:** código y 7 pruebas; 24 ejercicios (3 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 4 (zip y fecha → cuentas `ACC-…` y mensajes estilo FIX; la trampa de `split("\\/")` se queda);
  B2 (⚡ códec estilo FIX).
