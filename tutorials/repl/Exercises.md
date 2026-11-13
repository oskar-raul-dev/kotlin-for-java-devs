# Ejercicios: REPL, expresiones y scripts

> **Tutorial:** [`repl`](README.md) · **Dónde se resuelven:** `tutorials/repl/practice/` (scripts `.kts` y
> `.main.kts`, y un `NOTAS.md` con los comandos y las respuestas) · **Verificar:** cada ejercicio trae el comando y lo
> que debe salir. No hay pruebas de Gradle: el tutorial es de nivel 0 (sin build).
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil · 🎤 pregunta típica de entrevista ·
> **🎯 = prioritario** (ruta mínima).

**Prioritarios (🎯):** 01, 04, 06, 09, 12.

## 🟢 Fáciles

**01 🟢 🎯 Tres versiones, tres REPL.** Abre `kotlinc` sin argumentos con 1.9.22, 2.1.21 (si la tienes) y 2.4.21
(`sdk use kotlin <versión>` en terminales distintas). Copia en `NOTAS.md` lo que responde cada una.
- Casos: en 1.9.22 evalúa `listOf(3, 1, 2).sorted()` y anota el tipo que muestra (`res…: kotlin.collections.List<kotlin.Int>`).

**02 🟢 Lo que 1.9 no sabe.** En el REPL de 1.9.22, prueba una función de Kotlin 2.x (por ejemplo, una *guard
condition* en `when`: `when (x) { is Int if x > 0 -> … }`). Copia el error.
- Explica en `NOTAS.md` por qué ese REPL no sirve para probar lo nuevo del lenguaje.

**03 🟢 Una línea.** Con `kotlin -e`, imprime: la suma de los cuadrados del 1 al 10, la fecha de hoy en
`America/New_York` y si 2028 es bisiesto.
- Casos: `385`, la fecha con formato `yyyy-MM-dd`, `true`.

**04 🟢 🎯 Tu primer `.kts`.** `practice/fees.kts` recibe montos como argumentos y aplica una comisión del 0.5 % con
mínimo de USD 1.00, en `BigDecimal` y `HALF_EVEN`.
- Comando: `kotlin practice/fees.kts 100 1000 50.10`
- Casos: `100 → 1.00` (aplica el mínimo), `1000 → 5.00`, `50.10 → 1.00`; sin argumentos, un mensaje de uso.

**05 🟢 `.kts` frente a `.main.kts`.** Pon el mismo `@file:DependsOn` en un `.kts` y en un `.main.kts` y anota qué
pasa con cada uno.

## 🟡 Medios

**06 🟡 🎯 Dependencia de Maven.** `practice/ticks.main.kts` lee un JSON de ticks desde un archivo (ruta en `args[0]`)
con kotlinx-serialization y una `data class Tick(val ticker: String, val price: Double, val qty: Int)` con
`@Serializable`.
- Casos: imprime el VWAP por ticker. Pista: en un script, `@Serializable` necesita el plugin del compilador; pruébalo,
  anota el error y resuélvelo con el árbol `JsonElement` como en `vwap.main.kts`, o investiga cómo activar el plugin
  en un `.main.kts` ⚠️.

**07 🟡 Importar.** Divide el ejercicio 04: las reglas de comisión en `practice/fees-rules.main.kts` y el programa en
`practice/fees-cli.main.kts`, unidos con `@file:Import`.
- Casos: mismo resultado que en el 04; anota qué pasa si mueves `fees-cli.main.kts` a otra carpeta.

**08 🟡 Shebang.** Haz ejecutable el script del 07 con `#!/usr/bin/env kotlin` y `chmod +x`. Córrelo con Kotlin 2.4
primero en el `PATH` y luego con 1.9.22 primero.
- Casos: copia el resultado de cada uno y explica la diferencia.

**09 🟡 🎯 La caché.** Mide con `time` la primera y la segunda corrida de `vwap.main.kts`. Luego cambia una línea y
vuelve a medir. Repite con `KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR` apuntando a una carpeta propia.
- Casos: cuatro tiempos en `NOTAS.md`; qué invalida la caché; qué pasa si la carpeta no existe.

**10 🟡 Reemplazar un script de bash.** Reescribe en `.main.kts` este script: lista los `.json` de una carpeta,
cuenta las líneas de cada uno y escribe un resumen ordenado de mayor a menor en `resumen.txt`.
```bash
for f in "$1"/*.json; do echo "$(wc -l < "$f") $f"; done | sort -rn > resumen.txt
```
- Casos: mismo resultado que el de bash sobre `01-language/src/main/resources/data/`; el script falla con un mensaje
  claro si la carpeta no existe (código de salida 1).

**11 🟡 🎤 ¿Script o proyecto?** En `NOTAS.md`, para cada caso decide `.main.kts` o proyecto Gradle y justifica en una
línea: migrar un CSV una vez; una API REST; un generador de datos de prueba que usa el equipo; una herramienta de
línea de comandos que se distribuye; revisar a mano un endpoint.

## 🟠 Difíciles

**12 🟠 🎯 Conciliación en un script.** `practice/reconcile.main.kts` recibe dos CSV (extracto del banco y movimientos
internos, columnas `fecha,monto,descripción`) y lista las líneas sin pareja. Una pareja es mismo monto y fecha con
hasta un día de diferencia.
- Casos: una pareja exacta, una con un día de desfase, una sin pareja de cada lado, y una descripción con comas
  entre comillas. Es un adelanto del caso Banco ([`docs/Casos.md`](../../docs/Casos.md)).

**13 🟠 Un REPL casero.** `practice/mini-repl.main.kts` lee líneas de la entrada estándar y evalúa expresiones
aritméticas simples (`+ - * /`, paréntesis, `BigDecimal`), guardando el último resultado en `res`.
- Casos: `2 * (3 + 4)` → `14`; `res / 4` → `3.5`; una línea vacía termina; un error de sintaxis no corta el bucle.

**14 🟠 Scratch frente a script.** Haz el ejercicio 04 en un scratch file de IntelliJ y compara con el `.kts`: qué
muestra cada uno, cómo se pasan argumentos y qué versión de Kotlin usa el scratch (la del plugin del IDE, no la del
`PATH`). Anota la versión que ves.

## 🔴 Muy difíciles

**15 🔴 Generador de ticks.** `practice/gen-ticks.main.kts` genera un `ticks.json` sintético para el caso Mercado:
cinco tickers inventados, 10 días hábiles de 09:30 a 16:00 en `America/New_York`, paseo aleatorio con **semilla
fija** (`Random(42)`), y las imperfecciones de [`docs/Casos.md`](../../docs/Casos.md#datos) (nulos, duplicados,
desorden, un tick fuera de horario, el cambio de horario de noviembre).
- Casos: dos corridas dan el mismo archivo byte a byte (`shasum`); ningún precio es ≤ 0; el tick fuera de horario
  existe. Es un candidato a generador real del paso B0′.
