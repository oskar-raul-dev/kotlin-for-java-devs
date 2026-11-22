# NN · `<subpaquete>` — <Título del temario>

**Sección:** `<sección>` · **Paquete:** `dev.raul.oskar.learning.kotlin.<subpaquete>` · **Caso:** <Banco | Mercado | Ambos>
**Enlaces:** [temario](../../Temario.md#<ancla>) · [ejercicios](../../../<sección>/src/main/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/Exercises.md) · [código](../../../<sección>/src/main/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/) · [pruebas](../../../<sección>/src/test/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/)

> Enlaces revisados el AAAA-MM-DD.

## Qué vas a aprender

<2 o 3 líneas, del objetivo del temario.>

## Teoría

### 1. <Concepto>

<El concepto, el contraste con Java 21 (qué se escribía allá y por qué), el **por qué** del diseño de Kotlin, el
símbolo de la lección que lo muestra (`simbolo` en `Archivo.kt`) y la fuente.> → [Fuente oficial][ref]

<De 4 a 8 apartados. Más profundidad y más comparación con Java donde el modelo cambia de verdad (oop, io,
corrutinas, reactivo, JPA); menos donde Kotlin solo cambia la sintaxis. Lo que la teoría afirma y no está en las
fuentes, se mide antes de escribirlo.>

## Cómo correrla

```bash
./gradlew -p <sección> run -Plesson=<subpaquete>
./gradlew -p <sección> test --tests '*.<subpaquete>.*'
```

## Recorrido sugerido

1. `<Archivo>.kt`: qué mirar primero y por qué (símbolos entre comillas invertidas).
2. …
3. `<Lección>Test.kt`: cada trampa del temario con la prueba que la demuestra.
4. `Main.kt`: la demo que imprime todo junto.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `<Archivo>.kt` | … | `simbolo1`, `simbolo2` | `<Lección>Test` (`nombre de la prueba`) |

## Para profundizar

**Documentación oficial**

- [Título][ref] (kotlinlang.org): qué mirar ahí.

**Lecturas**

- [Título][ref2] (autor o sitio): qué aporta. Las de español se marcan *(es)*.

**Videos**

- [Título][ref3] (orador, evento y año): qué cubre. Los de español se marcan *(es)*.

## Estado

- **Hecho:** código y N pruebas; N ejercicios (N 🎯); N resueltos en `practice/`.
- **Falta:** <pasos del plan que tocan esta lección>.

[ref]: https://kotlinlang.org/docs/…

<!--
Reglas (docs/lessons/README.md):
- Se citan símbolos, no números de línea; cada fila del mapa apunta a una prueba que existe.
- Fuentes: primero lo oficial (en la versión fijada), luego lecturas y videos; inglés primero, español cuando hay
  material de la misma calidad, marcado (es). Cada enlace comprobado con scripts/check-guide-links.sh.
- "Estado" habla solo de esta lección; el total vive en CHECKLIST.md.
-->
