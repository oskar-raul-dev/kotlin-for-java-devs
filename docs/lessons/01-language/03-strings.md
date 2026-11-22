# 03 · `strings` — Plantillas, extensiones de `String`, regex

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.strings` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--strings--plantillas-extensiones-de-string-regex) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/strings/StringsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Reemplazar `String.format`, `StringBuilder`, `StringUtils` y `Pattern` por la stdlib de Kotlin, y parsear texto con
`Regex` sin caer en las diferencias con Java (la más famosa: `split`).

## Teoría

### 1. Es `java.lang.String`, con otra API encima

Un `String` de Kotlin **es** un `java.lang.String`: inmutable, UTF-16, los mismos métodos. Lo que cambia es lo que hay
encima: decenas de funciones de extensión de la stdlib (`substringBefore`, `padStart`, `removePrefix`, `lines`,
`isBlank`…) que en Java vienen de Commons Lang o de código propio. Y una diferencia de lenguaje que hay que tener
presente al leer código: en Kotlin `a == b` compara **contenido** (llama a `equals`), así que el clásico error de Java
de comparar cadenas con `==` no existe. → [Strings][strings], [Strings in Java and Kotlin][j2k-strings]

### 2. Plantillas y raw strings

`"Hola, $name: ${items.size}"` reemplaza la concatenación y casi todo `String.format` (se sigue usando `format` para
decimales y columnas, como en `row`). Los raw strings `"""…"""` son el equivalente de los *text blocks* de Java 15: sin
escapes y en varias líneas, con `trimIndent()` para quitar la sangría (`report`). Dos diferencias con Java: dentro de
un raw string **no** hay secuencias de escape (`\n` es literal) y las plantillas sí funcionan, así que un `$` literal
se escribe `${'$'}` o, desde 2.2, con una plantilla de varios `$` (`$$"""…"""`). → [Strings: templates][strings],
[JEP 378: Text Blocks][jep378]

### 3. `buildString` y las lambdas con receptor

`buildString { append(…) }` crea un `StringBuilder`, ejecuta el bloque con ese builder como `this` y devuelve el
`String` (`reverseWords`). Es el primer contacto con las *lambdas con receptor*, la misma idea de `apply`,
`buildList` y los DSL de `01 · dsl`. El builder no sale del bloque: afuera queda un valor inmutable.

### 4. `Regex`: los mismos patrones, otra API

`Regex` envuelve `java.util.regex.Pattern`: la sintaxis de los patrones es la de Java. Cambian los nombres y los
tipos: `matches` exige que coincida toda la cadena (el `matches()` de Java) y `containsMatchIn` busca en cualquier
parte (el `find()`); `find` devuelve un `MatchResult?` y `findAll` una `Sequence` perezosa; los grupos se desarman
con `destructured` y los grupos con nombre se leen con `groups["number"]` (`accountParts`, `accountNumber`). Con raw
strings no hay que duplicar las barras (`"""\d{8}"""`). Un `Regex` se compila al crearlo: dentro de una función se
recompila en cada llamada, por eso la lección los declara como `val` de nivel superior. → [`Regex`][regex],
[`Pattern`][pattern]

### 5. La trampa: `split(String)` es literal

En Java, `"35=D|55=ACME".split("\\|")` usa una expresión regular. En Kotlin, `split(String)` busca el texto
**literal** `\|`, no lo encuentra y devuelve la cadena entera, sin error (`splitLiteral` frente a `splitRegex`, con
una fecha y con un mensaje FIX). Para usar una regex se pasa un `Regex`: `split(Regex("\\|"))`, o directamente el
separador literal `split("|")`. Es de los bugs más comunes al portar código Java.

### 6. Mayúsculas, minúsculas y *locale*

`toLowerCase()` de Java usa el *locale* por defecto: en turco, `"TITLE".toLowerCase()` produce una `ı` sin punto y una
comparación deja de funcionar. Kotlin lo resolvió con `lowercase()`/`uppercase()` (siempre `Locale.ROOT`) y deprecó
`toLowerCase()` (es error desde 2.1); para comparar sin mayúsculas está `equals(other, ignoreCase = true)`
(`sameIgnoringCase`).

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

## Para profundizar

**Documentación oficial**

- [Strings][strings]: plantillas, raw strings, plantillas con varios `$` y formato.
- [Strings in Java and Kotlin][j2k-strings]: la tabla oficial de equivalencias con Java, caso por caso.
- [API de `Regex`][regex]: `matches`, `find`, `findAll`, `replace`, `split`.
- [`java.util.regex.Pattern`][pattern] (Java SE 21): la sintaxis de los patrones, que es la misma.
- [JEP 378: Text Blocks][jep378]: los raw strings de Java, para comparar.

**Lecturas**

- [Regular expressions in Kotlin][baeldung-regex] (Baeldung): `Regex` con ejemplos de cada operación.
- [String templates in Kotlin][baeldung-templates] (Baeldung): plantillas y cómo escribir un `$` literal.
- [Capacitación de Kotlin para programadores 2: conceptos básicos][bootcamp-basics] (Google) *(es)*: cadenas y
  plantillas en un codelab.

**Videos**

- [These 3 Tips will Change Your Approach to Strings in Kotlin][strings-video]: plantillas, raw strings y funciones
  de la stdlib en un video corto.

## Estado

- **Hecho:** código en los casos (B0′ tanda 4) y 7 pruebas; 24 ejercicios (3 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ códec estilo FIX).

[strings]: https://kotlinlang.org/docs/strings.html
[j2k-strings]: https://kotlinlang.org/docs/java-to-kotlin-idioms-strings.html
[regex]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-regex/
[pattern]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html
[jep378]: https://openjdk.org/jeps/378
[baeldung-regex]: https://www.baeldung.com/kotlin/regular-expressions
[baeldung-templates]: https://www.baeldung.com/kotlin/string-templates
[bootcamp-basics]: https://developers.google.com/codelabs/kotlin-bootcamp-basics?hl=es-419
[strings-video]: https://www.youtube.com/watch?v=IL3RLKvWJF4
