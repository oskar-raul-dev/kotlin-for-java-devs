# NN · `<subpaquete>` — <Título del temario>

**Sección:** `<sección>` · **Paquete:** `dev.raul.oskar.learning.kotlin.<subpaquete>` · **Caso:** <Banco | Mercado | Ambos>
**Enlaces:** [temario](../../Temario.md#<ancla>) · [ejercicios](../../../<sección>/src/main/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/Exercises.md) · [código](../../../<sección>/src/main/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/) · [pruebas](../../../<sección>/src/test/kotlin/dev/raul/oskar/learning/kotlin/<subpaquete>/)

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

## Estado

- **Hecho:** código y N pruebas; N ejercicios (N 🎯); N resueltos en `practice/`.
- **Falta:** <pasos del plan que tocan esta lección>.

<!--
Reglas (docs/lessons/README.md):
- Se citan símbolos, no números de línea.
- Cada fila del mapa apunta a una prueba que existe.
- "Estado" habla solo de esta lección; el total vive en CHECKLIST.md.
- Máximo una pantalla y media.
-->
