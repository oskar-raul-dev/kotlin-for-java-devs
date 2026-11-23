# Ejercicios: CPS, máquina de estados y `Continuation` a mano

> **Lección:** `internals` · **Dónde se resuelven:** `internals/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../internals/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.internals.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Varios ejercicios usan solo la
> stdlib (`kotlin.coroutines`: `Continuation`, `startCoroutine`, `createCoroutine`, `suspendCoroutine`, `sequence`), sin
> kotlinx; el enunciado lo dice. Para mirar el bytecode, el tutorial `04-decompile` (`javap -c -p`, CFR). El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 18, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 La firma real de una función `suspend`.** Verla en el bytecode.
- Firma: `suspend fun quotePrice(ticker: String): Money` (con un `delay`) en tu `practice/`
- Casos: con `javap -p` sobre la clase compilada, la firma tiene un parámetro `Continuation` extra y devuelve `Object`;
  pega la línea en un comentario de la prueba y compruébalo por reflexión (`parameterTypes.last()` es
  `Continuation::class.java`). Explica en un comentario qué es *continuation-passing style*.

**02 🟢 `COROUTINE_SUSPENDED`.** Cuándo una función `suspend` devuelve la marca y cuándo el valor.
- Firma: usa `callWithContinuation` de la lección con tres lambdas: una que no suspende, una que suspende (`Parking`) y
  una que lanza
- Casos: valor directo, `COROUTINE_SUSPENDED` y la excepción (¿se lanza o llega a `resumeWith`? pruébalo y explica).

**03 🟢 `suspendCoroutine` con un callback.** Envolver un cliente de tasas de callbacks (sin kotlinx).
- Firma: `suspend fun CallbackFx.rate(pair: String): BigDecimal` con `interface CallbackFx { fun request(pair: String, cb: (BigDecimal) -> Unit) }`
- Casos: el callback síncrono (dentro de `request`) y el asíncrono (desde otro hilo) dan lo mismo; explica en un
  comentario por qué el síncrono no suspende de verdad.

**04 🟢 Errores por la continuación.** `resumeWithException`.
- Firma: amplía el 03 con `onError: (Throwable) -> Unit`
- Casos: el error llega como excepción en quien llamó, con su tipo y mensaje; el stack trace (míralo) empieza en el
  callback, no en quien llamó: anota en un comentario qué se pierde.

**05 🟢 🎤 ¿Qué es una `Continuation`?** Escribir una a mano.
- Firma: `class LoggingContinuation<T>(val log: MutableList<String>) : Continuation<T>`
- Casos: arranca con `startCoroutine` un bloque que suspende dos veces (con `Parking`) y reanúdalo a mano: el log
  muestra el resultado final una sola vez; el `context` es `EmptyCoroutineContext`. Explica en un comentario qué
  representa una continuación ("el resto del programa").

**06 🟢 `createCoroutine` frente a `startCoroutine`.** Crear sin arrancar.
- Firma: `fun <T> lazyCoroutine(block: suspend () -> T, done: Continuation<T>): Continuation<Unit>`
- Casos: crear no ejecuta nada (un contador lo muestra); `resume(Unit)` sobre lo que devuelve la arranca; arrancarla
  dos veces lanza `IllegalStateException`.

**07 🟢 `sequence` de velas.** Generar velas de un minuto a partir de ticks con `yield`.
- Firma: `fun candles(ticks: Iterator<Tick>): Sequence<Candle>`
- Casos: es perezosa (pedir la primera vela consume solo los ticks de ese minuto más uno: cuéntalos); da lo mismo que
  `flow.candles` de la lección `flow` sobre la misma lista.

**08 🟢 `yieldAll`.** Concatenar generadores.
- Firma: `fun allTickers(feeds: Map<String, List<Tick>>): Sequence<Tick>`
- Casos: recorre los cinco en orden; `take(3)` no toca el segundo ticker si el primero tiene tres o más.

**09 🟢 No se puede `delay` en `sequence`.** La suspensión restringida.
- Firma: ninguna: un comentario en la prueba
- Casos: escribe el error del compilador al llamar a `delay` dentro de `sequence { }` (mídelo) y explica qué hace
  `@RestrictsSuspension`.

**10 🟢 Elementos del contexto.** Un `CoroutineName` y un id propio, juntos.
- Firma: `suspend fun describe(): String` que lee `QuoteRequestId` (de la lección) y `CoroutineName`
- Casos: `withContext(QuoteRequestId("r1") + CoroutineName("quote"))` los da a los dos; `+` reemplaza un elemento con
  la misma clave; `minusKey` lo quita.

**11 🟢 El contexto es un mapa.** Recorrerlo.
- Firma: `suspend fun contextKeys(): List<String>` con `coroutineContext.fold`
- Casos: dentro de `runTest` aparecen el `Job`, el dispatcher de prueba, el `CoroutineExceptionHandler` de `runTest` y
  lo que agregues; anota la lista en un comentario.

**12 🟢 `suspend` lambdas como valores.** Guardarlas y llamarlas después.
- Firma: `class RetryPolicy(val attempts: Int)` con `suspend fun <T> run(block: suspend () -> T): T`
- Casos: un bloque que falla dos veces y luego anda; el bloque se pasa como valor (`val b: suspend () -> Money`);
  explica en un comentario qué tipo de la JVM tiene una lambda `suspend` (`Function1<Continuation, Any?>`).

## 🟡 Medios

**13 🟡 🎤 La máquina de estados decompilada.** Contar estados.
- Firma: `suspend fun threeSteps(source: PriceSource): Money` con tres llamadas `suspend` en secuencia
- Casos: decompila con CFR o `javap -c` (tutorial `04-decompile`) y guarda el resultado como recurso de prueba; la
  prueba cuenta las ramas del `switch` sobre `label` (tantas como puntos de suspensión más uno). Explica en un
  comentario cómo compila Kotlin una función `suspend`.

**14 🟡 Variables que sobreviven.** Qué locales se vuelven campos.
- Firma: tres lambdas `suspend` con distintas variables vivas antes y después de un `delay`
- Casos: con `stateMachineFields` de la lección, las variables usadas después del `delay` aparecen como campos
  (`I$0`, `L$0`…), las que no, no; explica en un comentario qué cuesta en memoria una corrutina suspendida.

**15 🟡 `suspendCancellableCoroutine` con un cliente de Java.** Envolver un `CompletableFuture` a mano.
- Firma: `suspend fun <T> CompletableFuture<T>.awaitByHand(): T`
- Casos: el valor llega; la excepción llega desenvuelta (sin `CompletionException`); cancelar la corrutina cancela el
  future; compáralo con `await()` de `kotlinx.coroutines.future`.

**16 🟡 Callbacks que llegan dos veces.** Proteger la continuación.
- Firma: `suspend fun CallbackQuoteClient.quoteOnce(ticker: String): Money` que tolera que el cliente llame al callback
  dos veces
- Casos: con `suspendCancellableCoroutine`, la segunda llamada a `resume` lanza (mídelo: ¿qué excepción?); con
  `cont.isActive` o `tryResume`, se ignora; explica cuál conviene.

**17 🟡 Un `delay` propio sin kotlinx.** Con un `ScheduledExecutorService`.
- Firma: `suspend fun myDelay(scheduler: ScheduledExecutorService, millis: Long)` con `suspendCoroutine`
- Casos: arranca con `startCoroutine` un bloque que hace dos `myDelay` y anota los hilos: después del primer
  `myDelay`, la corrutina sigue en el hilo del scheduler (no hay dispatcher que la devuelva). Explica qué agrega
  kotlinx (el `ContinuationInterceptor`).

**18 🟡 🎤 ¿Por qué las corrutinas son baratas?** Medir el objeto de una corrutina suspendida.
- Firma: `fun memoryPerSuspendedCoroutine(n: Int): Long` (bytes por corrutina, con `Runtime` antes y después de
  suspender n corrutinas en `Parking`)
- Casos: la medida está en el orden de cientos de bytes (no de MB como la pila de un hilo); anota la medición y el
  método en un comentario, y por qué es aproximada (GC).

**19 🟡 Un `ContinuationInterceptor` propio.** Despachar cada reanudación a un executor.
- Firma: `class ExecutorInterceptor(executor: Executor) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor`
- Casos: con `startCoroutine` y este contexto, el bloque corre en los hilos del executor después de cada suspensión
  (anota los nombres); es lo que hace `CoroutineDispatcher` por dentro.

**20 🟡 Generador con estado: el libro paso a paso.** Una simulación de órdenes como `sequence`.
- Firma: `fun simulate(orders: List<LimitOrder>): Sequence<BookSnapshot>` (usa `MatchingBook` de `channels`)
- Casos: una foto por orden; la sexta foto se calcula sin calcular la séptima; el estado vive en la secuencia.

**21 🟡 `iterator { }`.** Un iterador de ticks sin clase.
- Firma: `fun ticksIterator(ticks: List<Tick>, onlyTicker: String): Iterator<Tick>`
- Casos: `hasNext`/`next` sin crear una clase; `next` sin `hasNext` funciona; al final lanza `NoSuchElementException`.

**22 🟡 Un elemento de contexto con clave polimórfica.** Un `Clock` en el contexto.
- Firma: `class ClockElement(val clock: java.time.Clock) : AbstractCoroutineContextElement(Key)` y
  `suspend fun now(): Instant` que usa el del contexto o el del sistema
- Casos: en la prueba, un reloj fijo en el contexto; los hijos lo heredan; útil para el corte de fecha de `datetime`.

**23 🟡 La corrutina como objeto.** Las propiedades de una `Continuation` de kotlinx.
- Firma: `suspend fun peekContinuation(): String` con `suspendCoroutineUninterceptedOrReturn` que mira
  `continuation.javaClass.name` y `continuation.context`
- Casos: el nombre de la clase delata la función (`…$peekContinuation$1` o similar); el contexto trae el `Job`.
  Explica en un comentario qué ves y por qué no se usa en código normal.

**24 🟡 `startCoroutine` con resultado síncrono.** Un `runSuspend` mínimo, sin kotlinx.
- Firma: `fun <T> runSuspend(block: suspend () -> T): T` que funciona si el bloque nunca suspende y lanza si suspende
- Casos: un bloque sin suspensión devuelve el valor; uno que suspende lanza `IllegalStateException` con un mensaje
  claro; explica qué haría falta para soportar suspensión (un bucle de eventos: el 37).

**25 🟡 Pilas de excepciones cortadas.** Lo que `kotlinx-coroutines-debug` reconstruye.
- Firma: `suspend fun deepFailure(): Nothing` (tres niveles de `suspend` con `delay` entre medio)
- Casos: el stack trace de la excepción no muestra a todos los que llamaron (anótalo); con el modo debug activo (las
  pruebas corren con `-ea`), aparecen marcas de "recuperación" (`kotlinx.coroutines.debug` stack trace recovery): compara
  las dos en un comentario. Retoma el tutorial `05-debug`.

**26 🟡 Llamar a una función `suspend` desde Java.** Lo incómodo y la solución.
- Firma: una clase Java que llama a `quote` con una `Continuation` escrita a mano, y un adaptador Kotlin
  `fun quoteFuture(ticker: String): CompletableFuture<Money>` (con `future { }` de kotlinx)
- Casos: las dos funcionan; explica en un comentario por qué la API para Java debería ser la del adaptador.

## 🟠 Difíciles

**27 🟠 🎤 CPS a mano.** Reescribir una función `suspend` como lo hace el compilador.
- Firma: `fun transferCps(from: String, to: String, amount: Money, cont: Continuation<Money>): Any?` con una clase
  máquina de estados propia (`label`, campos para las variables vivas) que llama a funciones `suspend` vía
  `callWithContinuation`
- Casos: da el mismo resultado que la versión `suspend` escrita normalmente, suspendiendo y sin suspender; explica en
  un comentario cada parte (el `when (label)`, el chequeo de `COROUTINE_SUSPENDED`, el `resumeWith`).

**28 🟠 Un generador asíncrono propio.** `sequence` permite solo `yield`; escribe uno que además permita esperar a un
callback.
- Firma: `class AsyncGenerator<T>(block: suspend GeneratorScope<T>.() -> Unit)` con `yield(value)` y `await(callback)`
- Casos: genera precios que llegan por callback; el consumidor pide de a uno; no usa kotlinx.

**29 🟠 Cancelación en un adaptador con recursos.** Un cliente que abre un recurso por pedido.
- Firma: `suspend fun ResourcefulClient.quote(ticker: String): Money` donde el pedido abre una "conexión" que hay que
  cerrar siempre
- Casos: éxito, error y cancelación cierran la conexión exactamente una vez (cuéntalo); un valor que llega después de
  cancelar se descarta y su recurso también se cierra (`resume(value) { onCancellation }` de kotlinx).

**30 🟠 Elementos de contexto que se combinan.** Un contexto de "presupuesto de tiempo" que se reduce en los hijos.
- Firma: `class Budget(val remaining: Duration) : AbstractCoroutineContextElement(Key)` y
  `suspend fun <T> withBudget(spend: Duration, block: suspend () -> T): T` (el hijo recibe el menor entre el resto del
  padre y lo pedido)
- Casos: presupuestos anidados toman el menor; un bloque que se pasa falla con un error que dice cuánto se excedió.

**31 🟠 🎤 Corrutinas sin pila (*stackless*).** Por qué una función normal no puede suspender.
- Firma: una función `suspend` que llama a `list.forEach { }` con una llamada `suspend` adentro (compila, porque
  `forEach` es `inline`) y otra con `list.asSequence().map { }` donde no compila (la lambda de `Sequence.map` no es
  `inline` ni `suspend`)
- Casos: escribe en un comentario el error del compilador y explica qué significa que las corrutinas de Kotlin sean
  *stackless* (cada función `suspend` guarda su propio estado) frente a las *stackful* (Loom guarda toda la pila).

**32 🟠 `Result` y `resumeWith`.** La continuación recibe un `Result<T>`.
- Firma: `class ResultCollector<T> : Continuation<T>` que junta los resultados de varias corrutinas arrancadas con
  `startCoroutine`
- Casos: éxitos y fallas en la misma colección; explica en un comentario por qué la API usa `Result` y no dos métodos
  (`resume`, `resumeWithException` son extensiones sobre `resumeWith`).

**33 🟠 Un `yield` cooperativo propio.** Dos corrutinas que se turnan en un solo hilo, sin kotlinx.
- Firma: `class PingPong` con dos bloques `suspend` que llaman a `pass()`, y un planificador que alterna
- Casos: el log alterna `ping, pong, ping, pong…`; cuando uno termina, el otro sigue solo hasta terminar.

**34 🟠 Medir el costo de una suspensión.** Cuánto tarda suspender y reanudar.
- Firma: `fun suspendResumeCost(iterations: Int): Duration` (con `Parking` y reanudación inmediata, sin kotlinx)
- Casos: tabla con calentamiento en un comentario, comparada con una llamada normal y con un `withContext` de kotlinx;
  explica de dónde sale cada costo.

**35 🟠 `sequence` frente a `Flow`.** El mismo pipeline de limpieza y velas en las dos.
- Firma: dos funciones sobre `tickFeed(steps = 10_000)`
- Casos: mismo resultado; la `sequence` no puede esperar a una fuente remota (muéstralo con un comentario y un intento
  que no compila); compara tiempos.

**36 🟠 El `Continuation` del `main`.** `suspend fun main()`.
- Firma: un archivo con `suspend fun main()` que llama a `delay`
- Casos: funciona sin `runBlocking`; decompílalo y explica en un comentario qué genera el compilador (un `main` que
  llama a `runSuspend` con su propia continuación) y en qué hilo corre después del `delay`.

## 🔴 Muy difíciles

**37 🔴 Un bucle de eventos.** `runBlocking` de juguete con una cola, sin kotlinx.
- Firma: `fun <T> miniRunBlocking(block: suspend MiniScope.() -> T): T` con `MiniScope.launch`, `delay` y `yield`
  propios
- Casos: tres corrutinas con `delay` distintos terminan en el orden correcto; el hilo de la prueba hace todo; el tiempo
  real es el máximo, no la suma.

**38 🔴 `async`/`await` propios.** Sobre el bucle del 37.
- Firma: `fun <T> MiniScope.async(block: suspend () -> T): MiniDeferred<T>` con `suspend fun await(): T`
- Casos: cinco cotizaciones en paralelo tardan lo que la más lenta; una que falla relanza en el `await`; esperar dos
  veces el mismo devuelve el mismo valor.

**39 🔴 Structured concurrency de juguete.** Agregar jerarquía y cancelación al 37–38.
- Firma: `MiniJob` con padre e hijos, `cancel()` que se propaga y un scope que espera a sus hijos
- Casos: cancelar al padre cancela a los hijos; una falla en un hijo cancela a los hermanos; el scope no vuelve hasta
  que terminan todos. Es la mitad del 🐉 1.

**40 🔴 Decompilar una lambda `suspend` con captura.** Leer el bytecode completo.
- Firma: una lambda `suspend` que captura dos variables externas y tiene dos puntos de suspensión dentro de un `try`
- Casos: decompila y anota en un archivo `.md` de la prueba cada parte: la clase `SuspendLambda`, `create`,
  `invokeSuspend`, los campos `L$`/`I$`, el `label`, cómo se restaura el estado y dónde queda el `try`.

## ⚡ Saiyajin

**41 ⚡ Scheduler de corrutinas propio sobre `createCoroutine`.** Un planificador de un solo hilo, con su propio
`yield` y `delay`, sin kotlinx.
- Firma: `class TinyScheduler` con `fun launch(name: String, block: suspend TinyScope.() -> Unit)`, `fun run()` y, en
  `TinyScope`, `suspend fun yield()` y `suspend fun delay(millis: Long)` (con reloj virtual propio)
- Reglas: solo `kotlin.coroutines` de la stdlib (`createCoroutine`, `suspendCoroutine`, `Continuation`); una cola de
  listas para correr y otra de dormidas ordenada por instante; `run()` vuelve cuando no queda nada.
- **Se verifica con:** una prueba del intercalado exacto de N corrutinas (salida esperada fija), y la máquina de
  estados decompilada (tutorial `decompile`) guardada en la lección, con tantos estados como puntos de suspensión más
  uno.
