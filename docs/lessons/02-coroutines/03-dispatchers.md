# 03 · `dispatchers` — `withContext`, `limitedParallelism`, bloquear hilos

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.dispatchers` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#02--dispatchers--withcontext-limitedparallelism-bloquear-hilos) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/dispatchers/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/dispatchers/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/dispatchers/DispatchersTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Elegir en qué hilos corre cada cosa (cálculo, E/S bloqueante), limitar el paralelismo sin crear pools a mano, no
agotar los hilos con llamadas bloqueantes y hacer que el contexto (un id de correlación) viaje con la corrutina.

## Teoría

### 1. Qué es un dispatcher

Una corrutina no es un hilo, pero corre en uno. El **dispatcher** es el elemento del `CoroutineContext` que decide en
qué hilo (o hilos) corre: cada vez que la corrutina arranca o se reanuda después de suspender, el dispatcher la pone en
la cola de un hilo. Es el equivalente del `Executor` de Java, y de hecho cualquier `Executor` se convierte en uno con
`asCoroutineDispatcher()`. Si no se dice nada, la corrutina hereda el dispatcher de su padre; los constructores sin
padre (`GlobalScope`, un `CoroutineScope` sin dispatcher) usan `Dispatchers.Default`. → [Coroutine context and
dispatchers][context], [Kotlin Coroutines dispatchers][kt-dispatchers]

### 2. `Default` para cálculo, `IO` para bloquear

- `Dispatchers.Default`: tantos hilos como núcleos (mínimo dos). Es el `ForkJoinPool.commonPool()` de las corrutinas,
  para cálculo: las velas del Mercado (`candles`) corren ahí.
- `Dispatchers.IO`: para llamadas que **bloquean** (JDBC, archivos, librerías Java síncronas). Tiene hasta 64 hilos (o
  tantos como núcleos, si son más), y los **comparte** con `Default`: los dos usan el mismo pool, por eso los hilos de
  los dos se llaman `DefaultDispatcher-worker-N` (`IO and Default share the same worker threads`). Pasar de `Default`
  a `IO` muchas veces ni siquiera cambia de hilo; solo cambia cuántos hilos pueden estar bloqueados a la vez.

Bloquear en `Default` es el error caro: cada `Thread.sleep` o consulta JDBC ocupa un núcleo entero mientras espera, y
con tantas llamadas como núcleos, todo lo demás que use `Default` se frena (`blocking calls on Default are capped by
the number of cores`: el pico nunca pasa de los núcleos; la demo muestra 200 llamadas de 10 ms en ~330 ms en
`Default` contra ~50 en `IO`). → [`Dispatchers.IO`][io-api], [Blocking threads, suspending coroutines][elizarov-blocking]

### 3. `withContext`: cambiar de hilo y volver

`withContext(Dispatchers.IO) { repository.balance(id) }` muda la corrutina a un hilo de `IO`, corre el bloque y vuelve
al dispatcher original con el resultado; para quien llama es una función `suspend` más. Es lo que `executor.submit(…)`
+ `future.get()` intentaba, sin bloquear al que espera. La convención que conviene seguir (Android la llama
*main-safety*): **una función `suspend` no bloquea a quien la llama**. Si adentro hay algo bloqueante, la propia
función hace el `withContext`, como `LedgerService.balance`; así, ningún llamador puede olvidarse. → [Best practices
for coroutines][android-best]

### 4. `limitedParallelism`: el pool dedicado sin pool

La base de datos tiene un pool de, por ejemplo, 10 conexiones. Si se lanzan 200 consultas en `IO`, 64 hilos quedan
bloqueados y 54 de ellos esperan una conexión. Lo correcto es limitar las consultas simultáneas al tamaño del pool:
`Dispatchers.IO.limitedParallelism(10)` es una **vista** de `IO` que nunca corre más de 10 tareas a la vez
(`dbDispatcher`, y la prueba `limitedParallelism never exceeds the pool size` con un contador de llamadas simultáneas).
Equivale a un `Executors.newFixedThreadPool(10)` dedicado, pero sin crear, nombrar ni cerrar un pool.

Dos detalles medidos en 1.11.0:

- Sobre `IO`, la vista es **elástica**: sus hilos se suman a los 64 de `IO`, así que varias vistas no se pisan. Sobre
  `Default`, no agrega hilos, solo limita; con un límite mayor que el de `Default`, devuelve el mismo `Default`.
- El nombre opcional (`limitedParallelism(4, "db")`) compila sin opt-in y aparece en el `toString()` del dispatcher y
  del contexto, pero **no** en el nombre del hilo, que sigue siendo `DefaultDispatcher-worker-N` (`the db name shows
  in toString but not in the thread name`). → [`limitedParallelism`][limited-api]

### 5. Hilos virtuales como dispatcher

`Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()` (`virtualThreadDispatcher`) corre cada corrutina
en un hilo virtual del JDK 21: ahí bloquear es barato, y la prueba `virtual threads run every blocking call at once`
muestra las 100 llamadas a la vez. Es un `ExecutorService`, así que hay que cerrarlo (`use`). Los hilos virtuales no
tienen nombre por defecto (la demo imprime `''`). La comparación a fondo, con el *pinning* de `synchronized`, está en
`loom`. → [JEP 444][jep444]

### 6. `ThreadLocal` y el contexto que viaja

Un `ThreadLocal` vive en el hilo; una corrutina salta de hilo en hilo. Por eso el MDC del log (un `ThreadLocal`) aparece
vacío después de un `withContext`: `correlationIdAfterSwitch` con `propagate = false` devuelve `null`. La solución es
convertir el `ThreadLocal` en un elemento del contexto con `asContextElement(valor)`: kotlinx.coroutines pone el valor en
cada hilo donde la corrutina corre y restaura el anterior cuando se va. `MDCContext` de `kotlinx-coroutines-slf4j` hace
exactamente eso para SLF4J. Para valores que solo lee código Kotlin, es mejor un elemento de contexto propio, sin
`ThreadLocal` (ejercicio 17). → [`asContextElement`][context-element-api]

### 7. `Unconfined` y el modo debug

`Dispatchers.Unconfined` no despacha: la corrutina arranca en el hilo de quien la lanza y, después de suspender, sigue
en el hilo que la reanudó (`unconfinedThreads`: `main` antes del `delay`, `kotlinx.coroutines.DefaultExecutor`
después). Sirve en casos muy puntuales; en código de aplicación hace impredecible el orden y el hilo. → [`Dispatchers.Unconfined`][unconfined-api]

Un detalle que aparece en las pruebas: con las aserciones de la JVM activas (`-ea`, como en las pruebas de Gradle) o con
`-Dkotlinx.coroutines.debug`, kotlinx.coroutines entra en modo debug y agrega ` @coroutine#N` (o el `CoroutineName`) al
nombre del hilo mientras la corrutina corre. Por eso la lección compara con `threadName()`, que quita ese sufijo.

### 8. Confinar en vez de bloquear

`limitedParallelism(1)` sobre `Default` serializa a todas las corrutinas que lo usan: un `var` común deja de tener
carreras (`confinedCounter`: 100 corrutinas × 1 000 incrementos = 100 000 exactos). Es el `newSingleThreadExecutor()`
sin hilo propio. Su pariente `newSingleThreadContext("nombre")` sí crea un hilo con nombre, pero pide
`@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)` en 1.11.0 (medido) y hay que cerrarlo: un hilo
que nadie cierra impide que la JVM termine. El confinamiento frente a los locks se retoma en `sync`.

### 9. Inyectar el dispatcher

Un `Dispatchers.IO` escrito a fuego dentro de una clase saca al código del tiempo virtual de las pruebas: sus `delay`
duran de verdad. Por eso `candles` y `LedgerService` reciben el dispatcher como parámetro, y la prueba `candles take an
injected dispatcher` les pasa un `StandardTestDispatcher(testScheduler)`. → [Best practices for coroutines][android-best]

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=dispatchers
./gradlew -p 02-coroutines test --tests '*.dispatchers.*'
```

## Recorrido sugerido

1. `Blocking.kt`: el contador de llamadas simultáneas (`ConcurrencyMeter`), el repositorio JDBC simulado
   (`BlockingLedgerRepository`), el dispatcher de la base (`dbDispatcher`) y el servicio *main-safe* (`LedgerService`).
2. `Threads.kt`: en qué hilo corre cada dispatcher (`threadName`, `threadNameOn`), hilos virtuales
   (`virtualThreadDispatcher`), el `ThreadLocal` que se pierde (`correlationId`, `correlationIdAfterSwitch`),
   `Unconfined`, el estado confinado (`confinedCounter`) y las velas en `Default` (`Candle`, `candles`).
3. `DispatchersTest.kt`: cada afirmación con hilos de verdad (salvo las velas, en tiempo virtual).
4. `Main.kt`: la tabla de 200 llamadas bloqueantes en cada dispatcher, con tiempos y picos reales.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Blocking.kt` | Llamadas bloqueantes y dónde ponerlas | `ConcurrencyMeter`, `BlockingLedgerRepository`, `dbDispatcher`, `LedgerService` | `DispatchersTest` (`limitedParallelism never exceeds the pool size`, `blocking calls on Default are capped by the number of cores`, `IO lends more threads than there are cores`, `virtual threads run every blocking call at once`) |
| `Threads.kt` | Hilos, contexto y confinamiento | `threadName`, `threadNameOn`, `virtualThreadDispatcher`, `correlationId`, `correlationIdAfterSwitch`, `unconfinedThreads`, `confinedCounter`, `Candle`, `candles` | `DispatchersTest` (`the db name shows in toString but not in the thread name`, `IO and Default share the same worker threads`, `a ThreadLocal is lost after withContext…`, `Unconfined resumes on whatever thread resumed it`, `limitedParallelism(1) confines state…`, `candles take an injected dispatcher`) |

## Para profundizar

**Documentación oficial**

- [Coroutine context and dispatchers][context] (kotlinlang.org): dispatchers, `withContext`, herencia del contexto,
  `Unconfined`, modo debug y elementos de `ThreadLocal`.
- En la API: [`Dispatchers.IO`][io-api] (los 64 hilos y la elasticidad), [`limitedParallelism`][limited-api],
  [`asContextElement`][context-element-api], [`runInterruptible`][interruptible-api] (cancelar una llamada bloqueante)
  y [`Dispatchers.Unconfined`][unconfined-api].
- [Best practices for coroutines in Android][android-best] (Android Developers): inyectar dispatchers y *main-safety*;
  vale igual para un backend.

**Lecturas**

- [Kotlin Coroutines dispatchers][kt-dispatchers] (Marcin Moskała, Kt. Academy): cada dispatcher con mediciones,
  `limitedParallelism` y los hilos virtuales.
- [Blocking threads, suspending coroutines][elizarov-blocking] (Roman Elizarov): por qué bloquear en el dispatcher
  equivocado es el error que más cuesta.
- [JEP 444: Virtual Threads][jep444]: qué cambia en el JDK 21 cuando se bloquea en un hilo virtual.

**Videos**

- [Kotlin Coroutines in Practice][video-practice] (Roman Elizarov, KotlinConf 2018): contexto, dispatchers y
  confinamiento en un ejemplo real.
- [Deep Dive into Coroutines on JVM][video-deep] (Roman Elizarov, KotlinConf 2017): cómo se despacha una
  continuación, por debajo.

## Estado

- **Hecho:** código y 10 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 1); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[context]: https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html
[io-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-i-o.html
[limited-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-dispatcher/limited-parallelism.html
[context-element-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/as-context-element.html
[interruptible-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-interruptible.html
[unconfined-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-dispatchers/-unconfined.html
[android-best]: https://developer.android.com/kotlin/coroutines/coroutines-best-practices
[kt-dispatchers]: https://kt.academy/article/cc-dispatchers
[elizarov-blocking]: https://elizarov.medium.com/blocking-threads-suspending-coroutines-d33e11bf4761
[jep444]: https://openjdk.org/jeps/444
[video-practice]: https://www.youtube.com/watch?v=a3agLJQ6vt8
[video-deep]: https://www.youtube.com/watch?v=YrrUCSi72E8
