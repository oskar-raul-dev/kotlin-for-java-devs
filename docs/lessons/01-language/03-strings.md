# 03 · `strings` — Plantillas, extensiones de `String`, regex

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.strings` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--strings--plantillas-extensiones-de-string-regex) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/strings/StringsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=strings
./gradlew -p 01-language test --tests '*.strings.*'
```

## Recorrido sugerido

1. `Strings.kt`: plantillas (`greeting`), raw strings (`report` de un ticker), `padEnd`/`padStart` (`row`),
   `substringBefore`/`After` (`domainOf`, `userOf`), `buildString` (`reverseWords`) y **la trampa de `split`
   literal** (`splitLiteral` frente a `splitRegex`), con la fecha `03/14/1985` y con un mensaje estilo FIX.
2. `Regexes.kt`: cada `Regex` en un `val` de nivel superior; la forma de las cuentas `ACC-…` con `matches` frente a
   `containsMatchIn` (`isAccountFormat`, `mentionsAccount`), `findAll` (`allAccounts`), grupos con nombre y
   `destructured` (`accountParts`, `accountNumber`), `replace` con lambda y lookbehind (`maskDigits`, `maskAccount`) y
   un mensaje estilo FIX a mapa con `findAll` + `associate` (`parseFix`). Los dígitos de control (mod 97) son ejercicio.
3. `StringsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Strings.kt` | Extensiones de `String` y la trampa de `split` | `greeting`, `report`, `row`, `domainOf`, `userOf`, `reverseWords`, `isPalindrome`, `charFrequency`, `capitalizeWords`, `sameIgnoringCase`, `splitLiteral`, `splitRegex` | `StringsTest` (`templates and raw strings`, `substring helpers`, `string algorithms`, `split with string is literal, not regex`) |
| `Regexes.kt` | `Regex` de Kotlin | `isEmail`, `isAccountFormat`, `mentionsAccount`, `allAccounts`, `accountParts`, `accountNumber`, `maskDigits`, `maskAccount`, `normalizeSpaces`, `parseFix` | `StringsTest` (`regex validation and extraction`, `named groups and destructuring`, `replace with regex`) |

## Estado

- **Hecho:** código en los casos (B0′ tanda 4) y 7 pruebas; 24 ejercicios (3 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ códec estilo FIX).
