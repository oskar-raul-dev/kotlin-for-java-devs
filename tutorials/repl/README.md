# Tutorial: REPL, expresiones y scripts

Cómo probar Kotlin sin crear un proyecto: qué pasó con el REPL de `kotlinc` (que en 2.4 ya no arranca), las
expresiones de una línea con `-e`, y los scripts `.kts` y `.main.kts`, que bajan dependencias de Maven y sirven para
reemplazar scripts de bash o Python con Kotlin tipado.

> **Versión:** Kotlin **2.4.21** sobre JDK 21; las salidas están medidas con esa versión y [`check.sh`](check.sh) las
> vuelve a verificar. **Ejercicios:** [`Exercises.md`](Exercises.md). Antes conviene haber hecho [`cli`](../cli/README.md).

**Salto rápido:** [El REPL](#1-qué-pasó-con-el-repl) · [Expresiones](#2-expresiones-de-una-línea) ·
[Scripts .kts](#3-scripts-kts) · [Scripts .main.kts](#4-scripts-mainkts) · [En el IDE](#5-en-el-ide) ·
[Chuleta](#chuleta-jshell--kotlin) · [Trampas](#trampas)

```bash
sdk env install          # en la raíz del repo: JDK 21 y Kotlin 2.4.21
cd tutorials/repl/examples
```

## 1. Qué pasó con el REPL

Java tiene `jshell` desde Java 9. Kotlin tuvo un REPL en `kotlinc` desde el principio, pero no sobrevivió al cambio de
compilador (K1 → K2). Medido con las tres versiones instaladas con SDKMAN:

| Versión | `kotlinc` sin argumentos |
|---|---|
| **1.9.22** (K1) | Funciona: `Welcome to Kotlin version 1.9.22` |
| **2.1.21** | Arranca, pero avisa `this REPL implementation is deprecated and will be removed soon` y `REPL is not compatible with the Kotlin version 2.1.21, using '-language-version 1.9'`: el REPL corre con el lenguaje de 1.9, no con K2 |
| **2.4.21** | No arranca: `Kotlin REPL is deprecated and should be enabled explicitly for now; please use the '-Xrepl' option`. Con `-Xrepl`: `unable to run REPL, no scripting plugin loaded` |

Si quieres un REPL de verdad en la terminal, el de 1.9.22 sigue funcionando (y es justo la versión del track
`22-legacy-kotlin19`):

```text
$ sdk use kotlin 1.9.22      # solo en esta terminal
$ kotlinc
Welcome to Kotlin version 1.9.22 (JRE 21.0.8+9-LTS)
Type :help for help, :quit for quit
>>> val x = 21
>>> x * 2
res1: kotlin.Int = 42
>>> :quit
```

Pero es Kotlin 1.9: nada de lo que trajo 2.x. Para el Kotlin actual, lo que cumple el papel del REPL es lo que sigue.

## 2. Expresiones de una línea

```bash
kotlin -e 'println((1..5).sum())'               # 15
kotlinc -e 'println("acme".uppercase())'        # ACME
```

`-e` compila y ejecuta el texto como un script. Tarda lo mismo que arrancar el compilador (~2 s), así que sirve para
comprobar algo rápido, no para un bucle de prueba y error.

## 3. Scripts `.kts`

Un `.kts` es Kotlin sin `main`: se ejecuta de arriba abajo, y `args` trae los argumentos.
[`examples/closes.kts`](examples/closes.kts):

```kotlin
val closes = args.map { it.toDouble() }.ifEmpty { listOf(100.0, 101.5, 99.75) }
println("cierres = $closes")
println("promedio = ${closes.average()}")
```

```bash
kotlinc -script closes.kts 100 102      # cierres = [100.0, 102.0]  promedio = 101.0
kotlin closes.kts                       # cierres = [100.0, 101.5, 99.75]  promedio = 100.41666666666667
```

Un `.kts` común no puede traer dependencias: `@file:DependsOn` da `unresolved reference 'DependsOn'`. Para eso está
`.main.kts`. Los `.kts` comunes los vas a ver sobre todo como **`build.gradle.kts`**: Gradle los corre con su propia
definición de script, que agrega `plugins { }`, `dependencies { }` y compañía.

## 4. Scripts `.main.kts`

La extensión `.main.kts` activa una definición de script con tres cosas más: dependencias de Maven, importar otros
scripts y caché de compilación. [`examples/vwap.main.kts`](examples/vwap.main.kts) calcula el VWAP del caso Mercado
([`docs/Casos.md`](../../docs/Casos.md)) con kotlinx-serialization:

```kotlin
#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.13.0")

import kotlinx.serialization.json.Json
// …
println("VWAP ACME = ${notional / volume}")
```

```bash
kotlin vwap.main.kts          # VWAP ACME = 101.5   (primera vez ~4.7 s: baja la dependencia y compila)
kotlin vwap.main.kts          # VWAP ACME = 101.5   (siguientes ~1 s: sale de la caché)
./vwap.main.kts               # lo mismo, por el shebang (necesita `kotlin` 2.x en el PATH y chmod +x)
```

| Anotación | Para qué |
|---|---|
| `@file:DependsOn("grupo:artefacto:versión")` | Baja la dependencia (y las suyas) de Maven Central a `~/.m2/repository` |
| `@file:Repository("https://…")` | Agrega otro repositorio Maven |
| `@file:Import("otro.main.kts")` | Trae las declaraciones de otro script (ruta relativa al script) |

- **Librerías multiplataforma:** se pide el nombre genérico (`kotlinx-serialization-json`) y el resolutor elige la
  variante JVM (`…-json-jvm`) por los metadatos de Gradle; pedir el `-jvm` directo también funciona.
- **Caché:** los scripts compilados quedan en `~/Library/Caches/main.kts.compiled.cache` (macOS). Con
  `KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR=<carpeta>` se elige otra; la carpeta tiene que existir, si no, no se
  usa caché.
- **`@file:Import`:** [`examples/report.main.kts`](examples/report.main.kts) usa la función `usd(…)` de
  [`examples/money.main.kts`](examples/money.main.kts):

```bash
kotlin report.main.kts
# ACC-00001234-82  USD 1234.56
# ACC-00005678-39  USD 9.99
```

**¿Cuándo un `.main.kts` y cuándo un proyecto Gradle?** Script: una tarea de uno o dos archivos que corres a mano
(migrar un CSV, revisar un endpoint, generar datos). Proyecto: cuando necesitas pruebas, varios módulos o un
artefacto que se despliega.

## 5. En el IDE

- **Scratch files de IntelliJ** (*File → New → Scratch File → Kotlin*): un archivo `.kts` fuera del proyecto que se
  ejecuta con un clic y muestra el resultado de cada línea al costado. Es lo más parecido a un REPL con Kotlin 2.x.
- **Kotlin Notebook**: cuadernos estilo Jupyter dentro de IntelliJ (plugin), con celdas, gráficos y dependencias
  de Maven. Útil para explorar datos, por ejemplo el `ticks.json` del caso Mercado ⚠️ (disponibilidad según la
  edición de IntelliJ).
- **Un `.main.kts` abierto en IntelliJ** tiene autocompletado de las dependencias de `@file:DependsOn` después de
  sincronizar el script (aviso arriba del editor).

## Chuleta jshell → Kotlin

| Java | Kotlin 2.4 |
|---|---|
| `jshell` | Sin REPL en la terminal: scratch file en IntelliJ, o `kotlinc` 1.9.22 para lo que exista en 1.9 |
| `jshell> 1 + 1` | `kotlin -e 'println(1 + 1)'` |
| `java Script.java` (un archivo, Java 11+) | `kotlin script.kts` |
| Un script con dependencias: JBang | `.main.kts` con `@file:DependsOn` |
| `#!/usr/bin/java --source 21` | `#!/usr/bin/env kotlin` en un `.main.kts` |

## Trampas

- **`kotlinc` sin argumentos en 2.4 no abre nada útil**: el mensaje dice `-Xrepl`, pero con `-Xrepl` tampoco
  arranca. No es tu instalación.
- **El REPL de 2.1 no es Kotlin 2.1**: corre con `-language-version 1.9`. Lo que pruebes ahí puede comportarse distinto
  en tu proyecto.
- **`.kts` frente a `.main.kts`**: solo el segundo entiende `@file:DependsOn` e `@file:Import`.
- **La primera corrida de un `.main.kts` tarda** (dependencias + compilación); no lo midas por esa.
- **El shebang usa el `kotlin` que esté primero en el `PATH`**: si es el 1.9.22 de SDKMAN, el script corre con 1.9.
