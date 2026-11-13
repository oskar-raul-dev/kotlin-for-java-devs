# Tutorial: depurar Kotlin

Depurar Kotlin es depurar bytecode de la JVM: sirven `jdb`, JDWP y el depurador de IntelliJ, igual que con Java. Lo
que cambia son tres cosas: las funciones `inline` (el código de otro archivo aparece copiado en el tuyo), las
corrutinas (un hilo ejecuta muchas, y una corrutina suspendida no está en ningún hilo) y algunos nombres sintéticos.
Este tutorial las muestra con herramientas de línea de comandos que se pueden verificar, y luego en IntelliJ.

> **Versión:** Kotlin **2.4.21**, kotlinx-coroutines 1.11.0, JDK 21. Lo de la terminal está verificado en
> [`check.sh`](check.sh) (28 comprobaciones). **Ejercicios:** [`Exercises.md`](Exercises.md). Conviene haber hecho
> [`decompile`](../decompile/README.md).

**Salto rápido:** [Información de depuración](#1-qué-deja-kotlinc-para-el-depurador) · [jdb](#2-jdb) ·
[Remoto](#3-depuración-remota-jdwp) · [Inline](#4-funciones-inline-y-smap) · [Corrutinas](#5-corrutinas) ·
[IntelliJ](#6-intellij) · [Trampas](#trampas)

```bash
sdk env                              # en la raíz del repo: Kotlin 2.4.21
cd tutorials/debug/examples
kotlinc Fees.kt -d out
```

## 1. Qué deja kotlinc para el depurador

[`examples/Fees.kt`](examples/Fees.kt) calcula comisiones del caso Banco (0.5 % con mínimo de USD 1.00). Por defecto,
`kotlinc` deja en cada `.class` lo que el depurador necesita, sin `-g` como en `javac`:

| Atributo | Para qué | Cómo verlo |
|---|---|---|
| `LineNumberTable` | Poner puntos de parada por línea, mostrar la línea en la pila | `javap -l -p FeesKt.class` |
| `LocalVariableTable` | Ver variables con su nombre Kotlin (`raw`, `min`, `result`) | `javap -l -p FeesKt.class` |
| `SourceDebugExtension` (SMAP) | Mapear a su archivo original el código copiado de funciones `inline` | `javap -v FeesKt.class` |

## 2. jdb

`jdb` viene con el JDK y es el depurador mínimo de línea de comandos. Una sesión sobre `Fees.kt` (salida real,
abreviada):

```text
$ jdb -classpath out:$KOTLIN_HOME/lib/kotlin-stdlib.jar FeesKt
> stop at FeesKt:12
Deferring breakpoint FeesKt:12.
> run
Breakpoint hit: "thread=main", FeesKt.fee(), line=12 bci=…

main[1] locals
Method arguments:
amount = instance of java.math.BigDecimal(id=…)
Local variables:
raw = instance of java.math.BigDecimal(id=…)
min = instance of java.math.BigDecimal(id=…)
result = instance of java.math.BigDecimal(id=…)
main[1] print result.toPlainString()
 result.toPlainString() = "1.00"
main[1] where
  [1] FeesKt.fee (Fees.kt:12)
  [2] FeesKt.main (Fees.kt:17)
  [3] FeesKt.main (null)
main[1] cont
…
[1.00, 5.00, 1.00]
```

- **La clase es `FeesKt`** (funciones de nivel superior; [`cli`](../cli/README.md)). `stop at FeesKt:12` pone el
  punto de parada en la línea 12 del archivo.
- **`print` evalúa Java, no Kotlin**: `result.toPlainString()` y `result.max(min)` funcionan porque son métodos;
  `amount * min` (el operador de Kotlin) da `ParseException: Invalid operation '*' on an Object`. El equivalente es
  el método real: `amount.multiply(min)`.
- **`FeesKt.main (null)`**: con `fun main()` sin argumentos, `kotlinc` genera además un `main(String[])` sintético
  que llama al tuyo. No tiene línea, por eso `null`.
- Comandos útiles: `step` (entra), `next` (pasa), `step up` (sale), `stop in FeesKt.fee`, `clear`, `threads`,
  `dump <objeto>`.

## 3. Depuración remota (JDWP)

Para depurar un proceso que ya corre (un servidor, un contenedor) se arranca la JVM con el agente JDWP y el
depurador se conecta por un puerto:

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=127.0.0.1:5005 -cp out:… FeesKt
# Listening for transport dt_socket at address: 5005       ← espera, porque suspend=y
jdb -attach 127.0.0.1:5005                                 # o IntelliJ: Run → Edit Configurations → Remote JVM Debug
```

- `suspend=y` detiene la JVM hasta que se conecte el depurador (para depurar el arranque); `suspend=n` arranca y
  acepta conexiones después.
- `address=127.0.0.1:5005` solo acepta conexiones locales; `*:5005` acepta de cualquier lado: **nunca** en una
  máquina expuesta, JDWP no tiene autenticación.
- Con Gradle no hace falta escribirlo: `./gradlew -p 01-language run -Plesson=basics --debug-jvm` arranca con
  `suspend=y` en el 5005 (`Listening for transport dt_socket at address: 5005`), y `./gradlew test --debug-jvm` hace
  lo mismo con las pruebas.

## 4. Funciones inline y SMAP

`main` usa dos veces `map`, que es `inline`: su código se copia dentro de `main`. Para que el depurador sepa de
dónde viene cada instrucción, `kotlinc` escribe un SMAP:

```text
SMAP
Fees.kt
Kotlin
*S Kotlin
*F
+ 1 Fees.kt
FeesKt
+ 2 _Collections.kt
kotlin/collections/CollectionsKt___CollectionsKt
*L
1#1,20:1     ← las líneas 1 a 20 de FeesKt son las de Fees.kt
1745#2:21    ← la 21 de FeesKt es la 1745 de _Collections.kt (el map copiado)
1820#2,3:22  ← …y así sigue: líneas que no existen en Fees.kt
```

Y en la tabla de variables locales aparecen marcadores del código copiado:

```text
$i$f$map                              ← aquí empieza el cuerpo de la función inline map
$i$a$-map-FeesKt$main$fees$1          ← aquí, el de la lambda que le pasaste
it   Ljava/math/BigDecimal;           ← el it de esa lambda
```

En la práctica: un paso a paso por una línea con `map { … }` puede "saltar" a `_Collections.kt`, y en IntelliJ verás
esas variables `$i$…` (son marcadores, no variables tuyas).

## 5. Corrutinas

[`examples/Feed.kt`](examples/Feed.kt) lanza dos corrutinas del caso Mercado, `feed-ACME` y `libro-ACME`, que quedan
suspendidas en un `delay`. Necesita `kotlinx-coroutines`; [`examples/deps.main.kts`](examples/deps.main.kts) lo baja a
`~/.m2` e imprime el classpath:

```bash
CP=$(kotlin deps.main.kts)                    # core, debug y ByteBuddy, versiones exactas
kotlinc Feed.kt -cp "$CP" -d feed
STD=$KOTLIN_HOME/lib/kotlin-stdlib.jar
```

**El nombre de la corrutina en el hilo:**

```bash
java -cp "feed:$CP:$STD" FeedKt                              # feed corre en: main
java -Dkotlinx.coroutines.debug -cp "feed:$CP:$STD" FeedKt   # feed corre en: main @feed-ACME#2
```

Con `-Dkotlinx.coroutines.debug`, el nombre del hilo incluye la corrutina que lo está usando (`CoroutineName` y un
número). Sirve en logs, en un volcado de hilos y en el depurador.

**El volcado de corrutinas (`DebugProbes`):** un `jstack` muestra hilos; una corrutina suspendida no está en ningún
hilo, así que no aparece. `DebugProbes.dumpCoroutines()` las lista todas, con su estado y dónde quedó cada una:

```text
$ java -javaagent:<kotlinx-coroutines-debug-1.11.0.jar> -Ddump -cp "feed:$CP:$STD" FeedKt
Coroutines dump 2026/11/13 01:28:13

Coroutine BlockingCoroutine{Active}@28d25987, state: RUNNING
	at FeedKt$main$1.invokeSuspend(Feed.kt:20)
Coroutine StandaloneCoroutine{Active}@4501b7af, state: SUSPENDED
	at FeedKt$main$1$feed$1.invokeSuspend(Feed.kt:14)
Coroutine StandaloneCoroutine{Active}@523884b2, state: SUSPENDED
	at FeedKt$main$1$book$1.invokeSuspend(Feed.kt:17)
```

- **Instalarlo como agente (`-javaagent`) es lo que funcionó aquí.** La forma "en código", `DebugProbes.install()`,
  se adjunta a la propia JVM con ByteBuddy y en este entorno (macOS, Zulu 21) falló con `No compatible attachment
  provider is available`, aun con `-XX:+EnableDynamicAgentLoading` y `-Djdk.attach.allowAttachSelf=true`. Dentro de
  un `.main.kts` falla de otra forma (`NoClassDefFoundError: DebugProbesImpl`), por los *classloaders* del script.
- Sin el agente, `dumpCoroutines()` lanza `Debug probes are not installed`.
- Las líneas (`Feed.kt:14`) dicen dónde está suspendida cada corrutina: el `delay` de cada `launch`.

**Variables "optimizadas":** en una función `suspend`, el compilador solo guarda al suspender las variables que se
usan después; en el depurador las demás salen como no disponibles. `kotlinc -Xdebug` lo desactiva (*"spilling all
variables in a suspending context regardless of whether they are alive"*). Es solo para depurar: no lo dejes en un
build de producción.

## 6. IntelliJ

Lo mismo de arriba, con interfaz:

- **Puntos de parada en lambdas:** en una línea con `map { fee(it) }`, IntelliJ ofrece parar en la línea, en la lambda
  o en las dos.
- **Smart Step Into** (*Shift+F7*): en una línea con varias llamadas, elegir a cuál entrar, incluidas las lambdas.
- **Evaluate Expression** (*Alt+F8*) evalúa **Kotlin**, no Java: ahí `amount * BigDecimal("0.005")` sí funciona.
- **Pestaña Coroutines** del depurador: las corrutinas vivas con su estado y su pila, como el volcado de `DebugProbes`.
  IntelliJ conecta solo el agente de corrutinas al depurar un proyecto que usa `kotlinx-coroutines-core` ⚠️ (revisar
  la versión del plugin si no aparece).
- **Depurar una prueba:** el ícono junto a la prueba, *Debug*. Desde la terminal, `./gradlew test --debug-jvm` y
  *Remote JVM Debug* en el 5005.
- **Show Kotlin Bytecode** ([`decompile`](../decompile/README.md)) ayuda a entender por qué el depurador se comporta
  raro en código `inline`.

## Trampas

- **`print` de `jdb` es Java**: los operadores y las extensiones de Kotlin no existen ahí; llama a los métodos reales
  (`plus`, `compareTo`, la fachada `…Kt`).
- **Líneas que no existen en el archivo**: son código `inline` copiado (SMAP).
- **Una corrutina suspendida no sale en `jstack`**: usa `DebugProbes` o la pestaña Coroutines.
- **`DebugProbes.install()` puede fallar al adjuntarse**: usa `-javaagent:kotlinx-coroutines-debug.jar`.
- **JDWP con `address=*:…` expuesto** es ejecución remota de código para cualquiera que llegue al puerto.
