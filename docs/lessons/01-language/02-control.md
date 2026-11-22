# 02 · `control` — `if`/`when` como expresiones, rangos, bucles

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.control` · **Caso:** genérico → Ambos
**Enlaces:** [temario](../../Temario.md#01--control--ifwhen-como-expresiones-rangos-bucles) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/control/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/control/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/control/ControlTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=control
./gradlew -p 01-language test --tests '*.control.*'
```

## Recorrido sugerido

1. `Conditionals.kt`: `if` como expresión (`sign`), `when` con rangos (`ageGroup`), varios valores por rama
   (`isWeekend`), `when` sin argumento (`fizzBuzz`), Elvis con `return` (`firstWordLength`).
2. `Loops.kt`: `..`, `until`, `downTo`/`step`, `withIndex`, `do-while`, `break@outer` y `repeat`.
3. `Algorithms.kt`: algoritmos de pizarra: `gcd` con `tailrec`, `isPrime`, `fibonacci`, `binarySearch` sin
   overflow, `insertionSort`, `twoSum`, `balanced` con `ArrayDeque`.
4. `ControlTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Conditionals.kt` | `if`/`when` como expresiones | `sign`, `ageGroup`, `isWeekend`, `fizzBuzz`, `firstWordLength` | `ControlTest` (`when with ranges`, `when without subject`, `elvis return exits early`) |
| `Loops.kt` | Rangos y bucles | `sumTo`, `indicesUntil`, `countdown`, `indexed`, `digits`, `firstPairWithSum`, `stars` | `ControlTest` (`ranges until downTo and step`, `labeled break leaves outer loop`) |
| `Algorithms.kt` | Algoritmos de entrevista | `gcd`, `isPrime`, `fibonacci`, `binarySearch`, `insertionSort`, `twoSum`, `balanced` | `ControlTest` (`classic algorithms`) |

## Estado

- **Hecho:** código y 6 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 4 (tramos de edad → tramos de comisión; búsqueda binaria sobre precios); B2 (⚡ matcher por
  búsqueda binaria).
