# 02 · `control` — `if`/`when` como expresiones, rangos, bucles

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.control` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--control--ifwhen-como-expresiones-rangos-bucles) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/control/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/control/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/control/ControlTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Escribir el control de flujo como **expresiones** (que devuelven un valor), recorrer rangos sin `for (;;)`, salir de
bucles y lambdas sin sorpresas, y resolver los algoritmos de pizarra en Kotlin idiomático.

## Teoría

### 1. `if` y `when` devuelven un valor

En Java, `if` es una sentencia y el valor sale del ternario `c ? a : b`. En Kotlin `if` **es** una expresión
(`val s = if (n > 0) "positive" else "negative"`), por eso no existe el ternario: sería una segunda forma de escribir
lo mismo. `when` hace lo mismo con el `switch`: devuelve un valor, no tiene *fall-through* ni `break`, y una rama
puede llevar varios valores (`"saturday", "sunday" ->`), un rango (`in 0L..<10_000L`) o un tipo (`is Int`). Usado
como expresión, el compilador exige que sea exhaustivo (`else`, o todas las constantes de un `enum` o subclases de un
`sealed`). → [Conditions and loops][control-flow]

Java 21 se acercó mucho: el `switch` con patrones (JEP 441) también es expresión, exhaustivo sobre `sealed` y con
guardas (`case Integer i when i > 0`). Kotlin tiene guardas desde 2.2 (`is Int if x > 0 ->`). La diferencia que
queda: en Kotlin `when` sin argumento reemplaza una cadena de `if/else if` (`fizzBuzz`), y `if` sigue siendo
expresión en todos lados. → [JEP 441][jep441]

### 2. Rangos en vez de `for (;;)`

No hay `for (int i = 0; i < n; i++)`: se itera sobre algo. `1..n` es un `IntRange` **cerrado** (incluye `n`),
`0..<n` (desde 1.9) y `0 until n` son semiabiertos, `downTo` cuenta hacia atrás y `step` cambia el paso. Los rangos son
objetos con `contains` (`x in 1..9`), así que sirven en `if` y en `when` además de en `for`. El compilador los baja a
un bucle con contador, sin crear el objeto. → [Ranges and progressions][ranges]

Lo que más se rompe al portar: `0..n` en lugar de `0..<n` (un elemento de más), `10..0` es un rango **vacío** (no
cuenta hacia atrás) y `step` no acepta negativos.

### 3. `return`, `break` y `continue` con etiquetas

Un `return` dentro de la lambda de `forEach { }` **sale de la función que contiene el `forEach`**, no de la iteración:
la lambda está en línea (`inline`) y el `return` es *no local*. Para "pasar al siguiente" se escribe
`return@forEach`. Los bucles anidados se cortan con etiquetas (`break@outer` en `firstPairWithSum`), igual que el
`break outer;` de Java. Desde 2.2, `break` y `continue` también funcionan dentro de lambdas `inline`. Y como `return` y
`throw` son expresiones de tipo `Nothing`, caben a la derecha de un Elvis: `?: return 0` (`firstWordLength`).
→ [Returns and jumps][returns]

### 4. Recursión de cola y algoritmos

`tailrec fun gcd(…)` le pide al compilador que convierta la recursión de cola en un bucle: sin `StackOverflowError`
y con el mismo costo que un `while` (Java no lo hace). Si la llamada no está en posición de cola, el compilador avisa.
Los algoritmos de la lección (`binarySearch`, `twoSum`, `balanced`) usan lo mismo que en Java (arreglos primitivos
`IntArray`, `HashMap`, una pila), con una trampa idéntica: `(lo + hi) / 2` desborda con índices grandes y se escribe
`(lo + hi) ushr 1`. → [Tail recursive functions][tailrec]

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=control
./gradlew -p 01-language test --tests '*.control.*'
```

## Recorrido sugerido

1. `Conditionals.kt`: `if` como expresión (`sign`), `when` con rangos `..<` sobre `Long` (`commissionRate`: tramos de comisión por volumen), varios valores por rama
   (`isWeekend`), `when` sin argumento (`fizzBuzz`), Elvis con `return` (`firstWordLength`).
2. `Loops.kt`: `..`, `until`, `downTo`/`step`, `withIndex`, `do-while`, `break@outer` y `repeat`.
3. `Algorithms.kt`: algoritmos de pizarra: `gcd` con `tailrec`, `isPrime`, `fibonacci`, `binarySearch` sin
   overflow (sobre precios en centavos), `insertionSort`, `twoSum`, `balanced` con `ArrayDeque`.
4. `ControlTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Conditionals.kt` | `if`/`when` como expresiones | `sign`, `commissionRate`, `isWeekend`, `fizzBuzz`, `firstWordLength` | `ControlTest` (`when with ranges`, `when without subject`, `elvis return exits early`) |
| `Loops.kt` | Rangos y bucles | `sumTo`, `indicesUntil`, `countdown`, `indexed`, `digits`, `firstPairWithSum`, `stars` | `ControlTest` (`ranges until downTo and step`, `labeled break leaves outer loop`) |
| `Algorithms.kt` | Algoritmos de entrevista | `gcd`, `isPrime`, `fibonacci`, `binarySearch`, `insertionSort`, `twoSum`, `balanced` | `ControlTest` (`classic algorithms`) |

## Para profundizar

**Documentación oficial**

- [Conditions and loops][control-flow]: `if` y `when` como expresiones, `for`, `while`, guardas en `when`.
- [Ranges and progressions][ranges]: rangos cerrados y semiabiertos, `step`, `downTo`, progresiones.
- [Returns and jumps][returns]: etiquetas, `return` no local y `return@label` en lambdas.
- [Tail recursive functions][tailrec]: qué cumple una función `tailrec`.
- [Kotlin tour: control flow][tour]: el recorrido oficial corto, con ejercicios.
- [JEP 441: Pattern Matching for switch][jep441]: el `switch` de Java 21, para comparar.

**Lecturas**

- [Cómo escribir condicionales en Kotlin][android-cond] (Android Developers) *(es)*: `if` y `when` como expresiones,
  con ejercicios.
- [Capacitación de Kotlin para programadores 2: conceptos básicos][bootcamp-basics] (Google) *(es)*: rangos, bucles y
  `when` en un codelab.

**Videos**

- [How to Improve Loops in Kotlin][tips-loops] (*Kotlin Tips*, canal oficial Kotlin by JetBrains): bucles idiomáticos
  con rangos e índices.
- [Kotlin When Expression: Multiple Conditions, Ranges & Type Checks][when-video] (no oficial): `when` con varias
  condiciones, rangos y tipos.

## Estado

- **Hecho:** código en los casos (B0′ tanda 4) y 6 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ matcher por búsqueda binaria).

[control-flow]: https://kotlinlang.org/docs/control-flow.html
[ranges]: https://kotlinlang.org/docs/ranges.html
[returns]: https://kotlinlang.org/docs/returns.html
[tailrec]: https://kotlinlang.org/docs/functions.html#tail-recursive-functions
[tour]: https://kotlinlang.org/docs/kotlin-tour-control-flow.html
[jep441]: https://openjdk.org/jeps/441
[android-cond]: https://developer.android.com/codelabs/basic-android-kotlin-compose-conditionals?hl=es
[bootcamp-basics]: https://developers.google.com/codelabs/kotlin-bootcamp-basics?hl=es-419
[tips-loops]: https://www.youtube.com/watch?v=i-kyPp1qFBA
[when-video]: https://www.youtube.com/watch?v=HQhTd5kMZ94
