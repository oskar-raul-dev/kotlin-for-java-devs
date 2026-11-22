# 01 · `basics` — Tipos, números, identidad

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.basics` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#01--basics--tipos-números-identidad) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/basics/BasicsTest.kt)

> **Guía con teoría (muestra de la propuesta de la sección 13 del plan).** Primero la teoría, después el mapa del
> código y al final las fuentes para profundizar. Enlaces revisados el 22 de noviembre de 2026.

## Qué vas a aprender

Dónde quedaron los primitivos de Java y cuándo hay boxing, por qué no hay conversiones implícitas, qué significan
`==` y `===`, para qué sirven `Any`, `Unit` y `Nothing`, y cómo manejar dinero con `BigDecimal` sin las trampas de
escala, redondeo y `Double`.

## Teoría

### 1. Todo parece objeto, pero hay primitivos

En Kotlin no se escribe `int`: todo es `Int`, `Long`, `Double`… con métodos (`42.toLong()`). Pero el compilador baja
un `Int` **no nullable** a un `int` de la JVM. Hay boxing (un `java.lang.Integer`) solo cuando el valor es nullable
(`Int?`) o argumento de un genérico (`List<Int>` guarda `Integer`). Por eso no hay que elegir entre `int` e `Integer`
como en Java: lo decide el tipo, y la nulabilidad es parte del tipo.

Consecuencia práctica: la caché de `Integer` de la JVM (−128 a 127) sigue existiendo, así que `===` entre dos `Int?`
iguales da `true` o `false` según el valor. Los números se comparan siempre con `==`. → [Numbers][numbers],
[Types overview][types]

### 2. Sin conversiones implícitas

Java convierte `int` a `long` sin pedir permiso; Kotlin no: `val l: Long = i` no compila, hace falta `i.toLong()`
(`widen` en `Numbers.kt`). La razón es que una conversión implícita entre tipos de caja rompería `equals`
(`Long(1) != Integer(1)`). Lo demás es igual que en Java: la división entera trunca hacia cero, `%` conserva el
signo del dividendo (`mod` da siempre un resto positivo) y el overflow de `Int` es **silencioso**; para detectarlo
está `Math.addExact` (`checkedAdd`). → [Numbers: conversiones explícitas][numbers]

### 3. `==` es `equals`; `===` es identidad

`a == b` se traduce a `a?.equals(b) ?: (b === null)`: llama a `equals` y además maneja `null` sin
`NullPointerException`. La identidad de Java (`==` entre referencias) se escribe `===`. Una `data class` genera
`equals` por contenido; una clase normal hereda el de `Any`, que es identidad (`Point` en `Identity.kt`).
→ [Equality][equality]

### 4. `Any`, `Unit` y `Nothing`

- `Any` es la raíz de los tipos no nullables (el `Object` de Java); `Any?` incluye a `null`.
- `Unit` es el `void` de Java pero es un valor de verdad (un objeto único), así que funciona como argumento genérico
  (`() -> Unit`).
- `Nothing` es el tipo **sin valores**: el de una expresión que nunca termina normalmente (`throw`, `TODO()`, un
  bucle infinito). Es subtipo de todos los tipos, y por eso `val x: String = y ?: fail("…")` compila: la rama de
  `fail` no produce un valor que pueda contradecir a `String` (`fail` en `Identity.kt`). → [`Nothing`][nothing],
  [jerarquía de tipos de la especificación][spec-types]

### 5. Dinero: nunca `Double`

`0.1 + 0.2` no da `0.3` porque `0.1` no tiene representación exacta en binario; eso no es un bug de la JVM sino del
formato IEEE 754. El dinero va en `BigDecimal`, y `BigDecimal` se construye desde `String` (o desde centavos), nunca
desde `Double`: `BigDecimal(0.1)` copia el error binario (`fromDouble` en `Numbers.kt`). → [Goldberg][goldberg]

Dos trampas que Kotlin hereda de Java:

- **Escala.** `BigDecimal.equals` compara valor **y escala**: `1.0` y `1.00` no son iguales con `==`; `compareTo`
  sí los considera iguales (`sameAmount`). → [Javadoc de `BigDecimal`][bigdecimal], [Baeldung: `equals` frente a
  `compareTo`][baeldung-bd]
- **Redondeo.** Cada operación que puede producir más decimales necesita un `RoundingMode`. `HALF_EVEN` (redondeo
  bancario) reparte los empates hacia el par y no acumula sesgo en muchas operaciones; `HALF_UP` es el "de la
  escuela" (`commission`).

Y una que es **solo de Kotlin**: los operadores aritméticos de la stdlib sobre `BigDecimal` (`+`, `-`, `*`, `/`)
son extensiones, y la división `a / b` es `a.divide(b, RoundingMode.HALF_EVEN)` con la **escala de `a`**. Medido con
2.4.21: `BigDecimal("10") / BigDecimal("3")` da `3`, `BigDecimal("10.00") / BigDecimal("3")` da `3.33`, y
`BigDecimal("1.25") / BigDecimal("10")` da `0.12`. En Java, `divide` sin modo de redondeo lanza
`ArithmeticException` si el resultado no termina; en Kotlin no lanza, redondea en silencio.

### 6. `Money`: monto y moneda juntos

`Money.kt` aplica lo anterior al Banco: una `data class` (no `value class`, porque son dos propiedades) con la escala
fija de la moneda (`Currency.defaultFractionDigits`), validada en el `init` y normalizada en las fábricas
(`Money.of`, `ofCents`). Con la escala fija, el `equals` generado deja de tener la trampa de `1.0` frente a `1.00`, y
los operadores exigen la misma moneda con `require`. → [`Casos.md`](../../Casos.md#money)

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

## Para profundizar

**Documentación oficial**

- [Numbers][numbers] (kotlinlang.org): tipos enteros y de punto flotante, literales, representación en la JVM y
  boxing, conversiones explícitas, operaciones. La base de las secciones 1 y 2.
- [Types overview][types]: qué tipos tienen representación primitiva en runtime.
- [Equality][equality]: `==`, `===` y el caso de los números de punto flotante (IEEE 754).
- [`Nothing`][nothing] (API de la stdlib) y el [sistema de tipos de la especificación][spec-types]: `Any`, `Nothing`
  y la jerarquía completa, con la definición formal.
- [Unsigned integer types][unsigned]: `UInt`, `ULong` y cuándo usarlos.
- [Comparison to Java][comparison]: la lista oficial de qué cambia respecto de Java.
- [Javadoc de `BigDecimal`][bigdecimal] (Java SE 21): escala, `equals` frente a `compareTo`, `MathContext` y
  `RoundingMode`.

**Lecturas**

- [What Every Computer Scientist Should Know About Floating-Point Arithmetic][goldberg] (D. Goldberg): por qué
  `0.1 + 0.2 != 0.3`. Largo; con la introducción y la sección de errores de redondeo alcanza.
- [BigDecimal: `equals` frente a `compareTo`][baeldung-bd] (Baeldung): la trampa de la escala, con ejemplos.
- [Kotlin's `Nothing`: its usefulness in generics][nothing-generics] (Kotlin Academy): `Nothing` como tipo de
  `emptyList()` y de las ramas que lanzan.
- *Effective Java*, 3.ª edición, ítem 60 ("Avoid float and double if exact answers are required"), de J. Bloch: el
  argumento clásico para `BigDecimal` o enteros en centavos.

**Videos**

- [Exploring the Kotlin Type Hierarchy from Top to Bottom][pryce] (Nat Pryce, KotlinConf 2018): `Any`, `Nothing`,
  nulabilidad y subtipos explicados para quien viene de Java.
- [New Type Inference and Related Language Features][isakova] (Svetlana Isakova, KotlinConf 2018): inferencia de
  tipos y smart casts (base de la sección 3 y del `describe` de la lección).
- [Kotlin for Java Developers Complete Course][course] (curso completo en YouTube, no oficial): las partes "From Java
  to Kotlin" y "Basics" repasan esta lección a ritmo de video.

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 3) y 12 pruebas; 25 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (ejercicios a la escala nueva, 🎤 y ⚡ calculadora de precisión arbitraria).

[numbers]: https://kotlinlang.org/docs/numbers.html
[types]: https://kotlinlang.org/docs/types-overview.html
[equality]: https://kotlinlang.org/docs/equality.html
[nothing]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-nothing/
[spec-types]: https://kotlinlang.org/spec/type-system.html
[unsigned]: https://kotlinlang.org/docs/unsigned-integer-types.html
[comparison]: https://kotlinlang.org/docs/comparison-to-java.html
[bigdecimal]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html
[goldberg]: https://docs.oracle.com/cd/E19957-01/806-3568/ncg_goldberg.html
[baeldung-bd]: https://www.baeldung.com/java-bigdecimal-equals-compareto-difference
[nothing-generics]: https://www.codementor.io/@kotlin_academy/kotlin-s-nothing-its-usefulness-in-generics-j4wcu5taa
[pryce]: https://www.youtube.com/watch?v=juFkdMv4B9s
[isakova]: https://www.youtube.com/watch?v=MyljSWm0Y_k
[course]: https://www.youtube.com/watch?v=dMyRywABp_c
