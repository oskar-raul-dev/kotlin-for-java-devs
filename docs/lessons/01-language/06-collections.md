# 06 · `collections` — Solo lectura frente a mutable, estructuras

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.collections` · **Caso:** genérico → Mercado
**Enlaces:** [temario](../../Temario.md#01--collections--solo-lectura-frente-a-mutable-estructuras) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/collections/CollectionsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=collections
./gradlew -p 01-language test --tests '*.collections.*'
```

## Recorrido sugerido

1. `Collections.kt`, de arriba abajo: solo lectura no es inmutable (`readOnlyIsNotImmutable`), listas
   (`listBasics`), sets (`setOps`), mapas (`wordCount`, `indexByFirstLetter`, `describeMap`), `TreeMap` con consultas
   de rango (`eventsBefore`, `lastEventAtOrBefore`), `ArrayDeque` como cola y pila (`queueAndStack`), `PriorityQueue`
   (`topK`) y arreglos (`arrays`).
2. `CollectionsTest.kt`: incluye la trampa de `==` entre arreglos (con `@Suppress` a propósito).
3. `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Collections.kt` | Colecciones de Kotlin y estructuras del JDK | `readOnlyIsNotImmutable`, `listBasics`, `setOps`, `wordCount`, `indexByFirstLetter`, `describeMap`, `eventsBefore`, `lastEventAtOrBefore`, `queueAndStack`, `topK`, `arrays` | `CollectionsTest` (las 8 pruebas, una por bloque) |

## Estado

- **Hecho:** código y 8 pruebas; 24 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 4 (libro de órdenes con `TreeMap` + `ArrayDeque`, cierres por fecha, tickers); B2 (⚡
  `HashMap` propio).
