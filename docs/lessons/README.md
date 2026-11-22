# Guías de lección

Una guía por lección **con código**: es el mapa de la lección. Dice qué archivo leer primero, qué muestra cada uno,
qué prueba lo demuestra, cómo correrlo y qué está hecho y qué falta. No repite el temario.

| Documento | Responde |
|---|---|
| [`docs/Temario.md`](../Temario.md) | **Qué** se aprende: mapeo Java → Kotlin, trampas, caso, 🎤 y ⚡ |
| `<lección>/Exercises.md` | **Qué** se practica: enunciados graduados |
| `docs/lessons/…` (esto) | **Dónde** está y **por dónde** leerlo: archivos, símbolos, pruebas, comandos, estado |
| [`CHECKLIST.md`](../../CHECKLIST.md) | **Cuánto** falta, en una tabla para todo el curso |

## Reglas

- **Una carpeta por sección**, con el nombre exacto del directorio de la raíz (`01-language`, `04-data`).
- **Un archivo por lección, `NN-<subpaquete>.md`**, con `NN` en el orden del temario dentro de la sección. El número
  solo vive en `docs/`: paquetes, directorios de código y nombres de prueba no cambian.
- Se citan **símbolos, no números de línea**, y cada fila del mapa apunta a una prueba que existe.
- La guía nace con el código, no con el temario: una lección sin código no tiene guía. Los tutoriales tampoco: su
  `README.md` ya es el recorrido.
- Cuando cambia el código de una lección (archivo nuevo, renombrado o borrado, símbolos que cambian), su guía se
  actualiza en el mismo cambio. Lección nueva → guía nueva y fila en este índice.
- Plantilla: [`_template.md`](_template.md).

## Índice

### `tutorials` (sin guía: el README de cada tutorial es el recorrido)

[`install`](../../tutorials/install/README.md) · [`cli`](../../tutorials/cli/README.md) ·
[`repl`](../../tutorials/repl/README.md) · [`decompile`](../../tutorials/decompile/README.md) ·
[`debug`](../../tutorials/debug/README.md)

### `01-language`

| # | Guía | Caso | Pruebas | Ejercicios |
|---|---|---|---|---|
| 01 | [`basics`](01-language/01-basics.md) — Tipos, números, identidad | Banco | 12 | 25 |
| 02 | [`control`](01-language/02-control.md) — `if`/`when` como expresiones, rangos, bucles | Ambos | 6 | 24 |
| 03 | [`strings`](01-language/03-strings.md) — Plantillas, extensiones de `String`, regex | Ambos | 7 | 24 |
| 04 | [`oop`](01-language/04-oop.md) — Clases, herencia, `sealed`, enums, objetos | Ambos | 10 | 24 |
| 05 | [`io`](01-language/05-io.md) — Archivos, recursos, consola, logging | Banco | 10 | 23 |
| 06 | [`collections`](01-language/06-collections.md) — Solo lectura frente a mutable, estructuras | Mercado | 9 | 24 |
| 07 | [`functional`](01-language/07-functional.md) — Funciones, lambdas, scope functions, pipelines | Mercado | 9 | 24 |
| 08 | [`json`](01-language/08-json.md) — kotlinx-serialization | Mercado | 9 | 24 |
| 09 | [`datetime`](01-language/09-datetime.md) — `java.time` correcto | Ambos | 8 | 24 |
| 10 | [`exceptions`](01-language/10-exceptions.md) — Sin checked exceptions, errores como valores | Banco | 16 | 24 |

`datagen` no es una lección y no tiene guía: genera los datos de los casos y las órdenes del libro (`orderStream`) (ver
[`Casos.md`](../Casos.md#datos)).

### `04-data`

| # | Guía | Caso | Pruebas | Ejercicios |
|---|---|---|---|---|
| 01 | [`jdbc`](04-data/01-jdbc.md) — JDBC idiomático, tres motores | Banco | 39 (13 × 3 motores) | 25 |
| 02 | [`jpa`](04-data/02-jpa.md) — Hibernate 7 sin Spring | Banco | 11 | 24 |

El caso es el **destino** según [`Casos.md`](../Casos.md); mientras dure el reemplazo (paso B0′), cada guía dice en
"Estado" si su código ya pasó al caso o sigue con el dominio provisional de pedidos.
