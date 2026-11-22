# 06 · `collections` — Solo lectura frente a mutable, estructuras

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.collections` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--collections--solo-lectura-frente-a-mutable-estructuras) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/collections/CollectionsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=collections
./gradlew -p 01-language test --tests '*.collections.*'
```

## Recorrido sugerido

1. `Collections.kt`, de arriba abajo: solo lectura no es inmutable (`readOnlyIsNotImmutable`), listas
   (`listBasics`), sets (`setOps`), mapas (`wordCount`, `indexByFirstLetter`, `describeMap`), `TreeMap` de cierres por fecha
   con consultas de rango (`closesBefore`, `closeAtOrBefore`: el último cierre conocido un sábado), `ArrayDeque` como cola y pila (`queueAndStack`), `PriorityQueue`
   (`topK`) y arreglos (`arrays`).
2. `OrderBook.kt`: el libro de órdenes de un ticker: `TreeMap` por precio en centavos (compras con
   `Comparator.reverseOrder()`), `ArrayDeque` FIFO por nivel con `getOrPut`, `firstEntry()` en vez de `firstKey()`,
   profundidad y cancelación con `removeIf`. Solo guarda y consulta: emparejar es el ⚡ de `control`.
3. `CollectionsTest.kt`: incluye la trampa de `==` entre arreglos (con `@Suppress` a propósito).
4. `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Collections.kt` | Colecciones de Kotlin y estructuras del JDK | `readOnlyIsNotImmutable`, `listBasics`, `setOps`, `wordCount`, `indexByFirstLetter`, `describeMap`, `closesBefore`, `closeAtOrBefore`, `queueAndStack`, `topK`, `arrays` | `CollectionsTest` (una prueba por bloque, de `read-only view…` a `arrays need contentEquals`) |
| `OrderBook.kt` | Libro de órdenes con estructuras del JDK | `RestingOrder`, `OrderBook` (`addBid`, `addAsk`, `bestBid`, `bestAsk`, `firstAt`, `isCrossed`, `depth`, `cancel`) | `CollectionsTest` (`order book keeps price priority and arrival order`) |

## Estado

- **Hecho:** código en el caso Mercado (B0′ tanda 4) y 9 pruebas; 24 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ `HashMap` propio).
