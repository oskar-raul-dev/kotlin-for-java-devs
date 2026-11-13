# Prompt maestro: una tanda del temario

Prompt para escribir una tanda de [`docs/Temario.md`](../Temario.md) según el [plan de trabajo](../Temario-Plan.md).
Se copia, se rellenan los parámetros con la fila de la tanda y se usa en una sesión nueva (o se le pasa a un
subagente). Así cada tanda sale con las mismas reglas aunque se escriba en días distintos.

## Parámetros

| Parámetro | Qué poner | Ejemplo (tanda 4) |
|---|---|---|
| `{TANDA}` | Número de la tanda | `4` |
| `{ENTRADAS}` | Sección y lecciones, tal como están en la tabla de tandas | `02-coroutines: fundamentals, cancellation, dispatchers, channels, flow, hotflows, sync, internals, testing, loom` |
| `{CASO}` | Caso que domina, si hay uno, según `docs/Casos.md` | `Mercado (feed como Flow, libro como actor); Banco en sync (transferencias concurrentes)` |
| `{FOCO}` | Lo que esta tanda tiene que cuidar en especial | `estructura vs. hilos de Java, cancelación cooperativa, comparación con Loom` |
| `{MEDIR}` | Construcciones cuya etiqueta de versión hay que medir porque no están en la referencia (o "ninguna") | `ninguna (coroutines es librería: versión de Versions.md)` |
| `{EXTENSIÓN}` | Largo orientativo por lección | `30–45 líneas` |

## Prompt

```text
Trabajas en el repo kotlin-for-java-devs (curso de Kotlin para un dev Java senior; repo público).
Tu tarea: escribir la tanda {TANDA} del temario, docs/Temario.md, siguiendo docs/Temario-Plan.md.

Entradas de esta tanda: {ENTRADAS}
Caso que domina: {CASO}
Foco especial: {FOCO}
Construcciones que hay que medir: {MEDIR}
Extensión orientativa por lección: {EXTENSIÓN}

Antes de escribir, lee:
1. docs/Temario-Plan.md completo: la plantilla de lección, las reglas y la referencia de versiones medida.
2. Plan-Migracion-Monorepo.md, sección 5 (qué cubre cada lección y el ⚡ de su sección) y sección 6 (escala de
   ejercicios: 🟢 🟡 🟠 🔴 ⚡ 🐉 🎤 🎯, etiquetas [x.y]).
3. docs/Casos.md: entidades, invariantes y qué lección usa qué caso. Usa sus nombres (Money, Tick, OrderBook,
   JournalEntry, Transfer…).
4. docs/Versions.md: versiones fijadas. Las librerías se citan con su versión, no con etiqueta de lenguaje.
5. docs/Temario.md: los bloques "⬜️ pendiente" de esta tanda. Si la lección ya existe, lo que decía el temario
   anterior está en docs/archive/Temario-v1.md (tablas, trampas y la sección "Huecos", que dice qué faltaba).
6. Si alguna lección ya tiene código (01-language, 04-data, tutorials/), su Main.kt, sus .kt y su Exercises.md:
   el temario describe lo que hay y lo que falta, no lo contradice.

Reglas:
- Una entrada por lección, con la plantilla de docs/Temario-Plan.md. Reemplaza solo los bloques "⬜️ pendiente" de
  esta tanda; no toques los de otras.
- Texto en español; identificadores, nombres de lección y código en inglés. Nada de empresas, clientes ni procesos
  de selección; entidades y datos ficticios.
- Etiquetas de versión [x.y] solo de la referencia medida de docs/Temario-Plan.md. Para cada construcción de
  {MEDIR}: agrega una línea (nombre|código) al bloque PROBES de scripts/version-probes.sh, córrelo con
  `scripts/version-probes.sh <nombre>` (usa los kotlinc 1.9 a 2.4 instalados con SDKMAN, sin cambiar el default) y suma
  la fila a la tabla de referencia. Lo que no puedas medir lleva ⚠️.
- Cada ⚡ tiene un criterio verificable (una prueba, un benchmark, una salida concreta), no "entender X".
- Cada tabla Java → Kotlin compara con Java 21 (el JDK del curso) cuando Java tiene algo equivalente.
- No escribas código de lecciones ni ejercicios: eso es de B2 en adelante.

Al terminar:
- Revisa que los enlaces relativos y las anclas de docs/Temario.md resuelvan.
- Escanea el diff con el patrón de referencias externas del repo (está en la memoria del proyecto y en CLAUDE.md:
  "scan for external references"); el resultado debe ser vacío.
- Actualiza la fila de la tanda en la tabla "Estado" de docs/Temario-Plan.md y agrega una entrada a la bitácora de
  CHECKLIST.md.
- Resume en la respuesta: qué entradas escribiste, qué etiquetas mediste (con su resultado), qué dejaste con ⚠️ y
  qué decisiones quedan para el usuario. No hagas commit salvo que te lo pidan.
```

## Cómo se usa

1. Elegir la tanda en la tabla de [`Temario-Plan.md`](../Temario-Plan.md#tandas) (respetando "Depende de").
2. Copiar el bloque *Prompt* y reemplazar los seis parámetros.
3. Correrlo en una sesión nueva, o pasárselo a un subagente si se sacan varias tandas independientes a la vez.
4. Revisar el resultado (ver el diff de `docs/Temario.md`), ajustar, y hacer commit de la tanda.
