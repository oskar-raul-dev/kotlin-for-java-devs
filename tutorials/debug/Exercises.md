# Ejercicios: depurar Kotlin

> **Tutorial:** [`debug`](README.md) · **Dónde se resuelven:** `tutorials/debug/practice/` (programas `.kt`, un
> `NOTAS.md` con las sesiones de `jdb` o capturas de IntelliJ y las respuestas) · **Verificar:** cada ejercicio dice
> qué tiene que aparecer en la sesión.
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil · 🎤 pregunta típica de entrevista ·
> **🎯 = prioritario** (ruta mínima).

**Prioritarios (🎯):** 01, 03, 06, 09, 11.

## 🟢 Fáciles

**01 🟢 🎯 Tu primera sesión de jdb.** Con `examples/Fees.kt`, para en `fee`, avanza con `next` línea por línea y
anota el valor de `raw` y `result` en las tres llamadas.
- Casos: `0.500`/`1.00`, `5.000`/`5.000`, `0.25050`/`1.00` (comprueba los tuyos con `print x.toPlainString()`).

**02 🟢 `stop in` frente a `stop at`.** Pon un punto de parada por método (`stop in FeesKt.fee`) y otro por línea.
Anota en qué difieren y qué pasa con `stop in FeesKt.main`: hay dos `main`.

**03 🟢 🎯 Java en `print`.** En la sesión del 01, evalúa con `print`: el monto por dos, si `result` es mayor que
`min`, y la escala de `raw`.
- Casos: los métodos reales que tuviste que usar (`multiply`, `compareTo`, `scale()`), y el error al usar `*` o `>`.

**04 🟢 Un `null` que llega desde Java.** Escribe un `CallFee.java` que llame a `FeesKt.fee(null)`. Córrelo contra
`Fees.kt` compilado normal y compilado con `-Xno-param-assertions`.
- Casos: con chequeos, `NullPointerException: Parameter specified as non-null is null: method FeesKt.fee, parameter
  amount` en la entrada de `fee`; sin ellos, el NPE sale después, en `multiply` (`Fees.kt:8`). Explica en `NOTAS.md`
  cuál de los dos prefieres al depurar y por qué el chequeo existe.

## 🟡 Medios

**05 🟡 Depuración remota.** Arranca `Fees.kt` con JDWP y `suspend=y` en un puerto propio, conéctate con `jdb
-attach`, pon un punto de parada y sigue.
- Casos: la sesión completa; repite con `suspend=n` y explica por qué el punto de parada ya no alcanza a parar (el
  programa termina antes).

**06 🟡 🎯 `--debug-jvm` e IntelliJ.** Corre `./gradlew -p 01-language run -Plesson=strings --debug-jvm` y conéctate
desde IntelliJ con *Remote JVM Debug*. Para dentro de una función de la lección.
- Casos: la configuración que creaste y una captura (o descripción) de la pila y las variables.

**07 🟡 El SMAP a mano.** En `Fees.kt`, agrega una función `inline` tuya (por ejemplo `inline fun <T> logged(block: () ->
T): T`) usada desde `main`. Compila, saca el SMAP con `javap -v` y explica cada línea de la sección `*L`.
- Casos: qué rango de líneas corresponde a tu función copiada, y qué marcadores `$i$f$…` y `$i$a$-…` aparecen.

**08 🟡 Paso a paso por una lambda inline.** En `jdb`, para en la línea del segundo `map` de `main` y usa `step`
varias veces. Anota en qué archivo y línea dice que está en cada paso.
- Casos: el momento en que "entra" a `_Collections.kt` aunque el código esté en `FeesKt`.

**09 🟡 🎯 🎤 Una corrutina que no aparece.** Corre `examples/Feed.kt` con un `delay` más largo y, mientras corre, toma
un `jstack <pid>`. Busca `feed-ACME`.
- Casos: no aparece (explica por qué); luego córrelo con `-Dkotlinx.coroutines.debug` y con el agente y `-Ddump`, y
  muestra dónde sí aparece.

**10 🟡 Nombres en los logs.** Agrega a `Feed.kt` una tercera corrutina en `Dispatchers.Default` y registra en cada
una `Thread.currentThread().name`.
- Casos: los nombres con y sin `-Dkotlinx.coroutines.debug`; por qué la del `Default` corre en otro hilo
  (`DefaultDispatcher-worker-…`).

## 🟠 Difíciles

**11 🟠 🎯 Variables optimizadas.** Escribe una función `suspend` con una variable local que no se usa después de un
`delay`. Depúrala en IntelliJ (o con `jdb`) parada después del `delay` y mira esa variable. Repite compilando con
`-Xdebug`.
- Casos: la variable no disponible sin `-Xdebug` y visible con `-Xdebug`. Anota por qué no se deja en producción.

**12 🟠 `DebugProbes.install()` en tu máquina.** Prueba la instalación dinámica (`DebugProbes.install()` sin agente) en
tu JDK y sistema operativo.
- Casos: si funciona o con qué error falla; si falla como en el tutorial, investiga qué *attachment provider* busca
  ByteBuddy y por qué no lo encuentra (pista: `jdk.attach`, `-Djdk.attach.allowAttachSelf`, el proceso externo).

**13 🟠 Depurar una prueba.** Toma una prueba de `04-data` (JPA con H2) y depúrala con `./gradlew -p 04-data test
--tests '*JpaTest*' --debug-jvm`, parando dentro de una transacción.
- Casos: la pila hasta tu código (cuántos marcos son de Hibernate) y el valor de una entidad antes del `commit`.

## 🔴 Muy difíciles

**14 🔴 Un bug de verdad.** En `practice/bug/`, este programa a veces imprime un saldo que no cuadra:
```kotlin
var balance = 0
fun main() = runBlocking {
    repeat(1_000) { launch(Dispatchers.Default) { balance += 1 } }
    delay(500)
    println(balance)
}
```
Encuentra la causa **solo con el depurador** (puntos de parada condicionales, vista de hilos y corrutinas, sin
agregar `println`) y documenta en `NOTAS.md` qué viste. Arréglalo de dos formas (`Mutex` y `AtomicInteger`) y
demuestra con 20 corridas que siempre da 1000. Es un adelanto de `02-coroutines/sync`.
