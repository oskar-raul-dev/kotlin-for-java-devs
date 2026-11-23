# Ejercicios: fundamentos de corrutinas

> **Lección:** `fundamentals` · **Dónde se resuelven:** `fundamentals/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../fundamentals/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.fundamentals.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Salvo que el enunciado diga otra
> cosa, las pruebas usan `runTest` (tiempo virtual: `delay` no espera) y comprueban el tiempo con `currentTime`, que
> pide `@OptIn(ExperimentalCoroutinesApi::class)` en kotlinx-coroutines-test 1.11.0. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 12, 20, 24, 31, 32.

## 🟢 Fáciles

**01 🟢 🎤 Tu primera función `suspend`.** Un precio que tarda en llegar.
- Firma: `suspend fun delayedPrice(ticker: String, latency: Duration): Money` (con `REFERENCE_PRICES` de `cases`)
- Casos: devuelve el precio de referencia; `currentTime` avanza exactamente `latency`; con `Thread.sleep` en vez de
  `delay` la prueba tarda de verdad. Explica en un comentario si una corrutina es un hilo y qué pasa con el hilo
  mientras la corrutina espera.

**02 🟢 `runBlocking` en `main`.** Un `main` que imprime el valor del portafolio de la lección.
- Firma: `fun main()` en `Ex02Main.kt` que llama a `portfolioValue` con `runBlocking`
- Casos: la prueba llama a una función `fun portfolioReport(): String` que el `main` imprime. Explica en un comentario
  por qué `runBlocking` va en `main` y en pruebas, y nunca dentro de una corrutina.

**03 🟢 Orden de `launch`.** Tres corrutinas que anotan su nombre después de esperar 300, 100 y 200 ms.
- Firma: `suspend fun finishOrder(): List<String>`
- Casos: el orden es el de las esperas, no el de los `launch`; `currentTime` es 300, no 600.

**04 🟢 `async` y `await`.** El valor de dos posiciones consultadas a la vez.
- Firma: `suspend fun pairValue(a: Pair<String, Long>, b: Pair<String, Long>, source: PriceSource): Money`
- Casos: ACME×10 + ORBX×25 = 2062.50; tarda lo que la fuente más lenta de las dos; el mismo ticker dos veces.

**05 🟢 `awaitAll`.** Todas las cotizaciones de una lista con `awaitAll`.
- Firma: `suspend fun quoteAll(tickers: List<String>, source: PriceSource): List<Money>`
- Casos: conserva el orden de la lista de entrada aunque terminen en otro orden; lista vacía; un ticker que falla
  hace fallar todo con `QuoteUnavailable`.

**06 🟢 `joinAll`.** Guardar las cotizaciones en un mapa compartido con un `launch` por ticker.
- Firma: `suspend fun quoteInto(tickers: List<String>, source: PriceSource, into: MutableMap<String, Money>)`
- Casos: al volver, el mapa tiene los cinco; `joinAll` frente a un `forEach { it.join() }`: mismo resultado. Explica
  en un comentario por qué aquí no hay carrera aunque el mapa no sea concurrente (en `runTest` todo corre en un hilo)
  y qué cambiaría con `Dispatchers.Default`.

**07 🟢 `coroutineScope` espera.** Una función que lanza dos hijos y no espera a ninguno a mano.
- Firma: `suspend fun fireAndWait(log: MutableList<String>)`
- Casos: al volver, el log tiene lo de los dos hijos; `currentTime` es el del hijo más lento.

**08 🟢 Estados de un `Job`.** `isActive`, `isCompleted` e `isCancelled` en cada momento.
- Firma: `suspend fun jobStates(): Map<String, Triple<Boolean, Boolean, Boolean>>` con las claves `"nuevo (LAZY)"`,
  `"corriendo"`, `"terminado"`, `"cancelado"`
- Casos: los cuatro estados con sus tres banderas; un `Job` cancelado también está completado.

**09 🟢 `async` perezoso.** Una cotización cara que solo se pide si hace falta.
- Firma: `suspend fun priceIfNeeded(ticker: String, needed: Boolean, source: SimulatedPriceSource): Money?` con
  `async(start = CoroutineStart.LAZY)`
- Casos: con `needed = false` la fuente nunca empezó (`started` vacío) y no hay que esperar a nadie; con `true`,
  devuelve el precio. Ojo: un `async` perezoso que nadie arranca deja colgado al `coroutineScope`: cancélalo.

**10 🟢 `delay` frente a `Thread.sleep`.** El mismo bucle de 5 esperas de 100 ms, con cada una.
- Firma: `suspend fun waitWithDelay(times: Int)` y `fun waitWithSleep(times: Int)`
- Casos: en `runTest`, la primera avanza `currentTime` en 500 sin tardar; la segunda no mueve `currentTime` y tarda
  medio segundo real (mídelo con `measureTime`).

**11 🟢 `suspend` sin suspender.** Una función `suspend` que no llama a ninguna otra.
- Firma: `suspend fun sumPrices(prices: List<Money>): Money`
- Casos: la suma; corre en el hilo de quien llama. Explica en un comentario el aviso del IDE ("redundant suspend
  modifier") y por qué `suspend` es un permiso para suspender, no una orden.

**12 🟢 🎤 `launch` frente a `async`.** La misma falla en cada uno.
- Firma: `suspend fun failInLaunch()` y `suspend fun failInAsync(): Int`, cada una con un hijo que lanza
- Casos: en los dos casos el `coroutineScope` falla con la misma excepción aunque nadie llame a `await`. Explica en un
  comentario cuándo usar cada uno y qué devuelve cada uno.

## 🟡 Medios

**13 🟡 Por tandas.** Cotizar en tandas de N: cada tanda en paralelo, las tandas una tras otra.
- Firma: `suspend fun quoteInBatches(tickers: List<String>, batchSize: Int, source: PriceSource): Map<String, Money>`
- Casos: con tandas de 2 sobre los cinco tickers, `currentTime` es la suma de la latencia máxima de cada tanda
  (calcúlala a mano); tanda de 1 = en serie; tanda de 5 = todo en paralelo; `batchSize` ≤ 0 → `IllegalArgumentException`.

**14 🟡 La respuesta más rápida.** El mismo ticker pedido a varias fuentes: gana la primera y las demás se cancelan.
- Firma: `suspend fun fastestPrice(ticker: String, sources: List<PriceSource>): Money`
- Casos: con latencias 300, 50 y 200 gana la de 50 y `currentTime` es 50; las otras dos quedan canceladas (empezaron,
  no terminaron); si la más rápida falla, gana la siguiente; si fallan todas, la excepción de la última.

**15 🟡 Pedido de cobertura (*hedged request*).** Si la fuente principal no contesta en 100 ms, se pide también a la
secundaria y se usa la que conteste primero.
- Firma: `suspend fun hedgedPrice(ticker: String, primary: PriceSource, secondary: PriceSource, hedgeAfter: Duration): Money`
- Casos: la principal contesta en 80 → la secundaria nunca empieza; la principal tarda 500 y la secundaria 50 → gana
  la secundaria en 150; las dos lentas → la que termine primero; la perdedora queda cancelada.

**16 🟡 Portafolio en dos monedas.** Posiciones en USD y en EUR, con los precios y la tasa pedidos a la vez.
- Firma: `suspend fun portfolioInUsd(holdings: Map<String, Long>, eurTickers: Set<String>, source: PriceSource, fx: FxRateClient): Money`
- Casos: el total con una tasa fija; el tiempo es el máximo entre la fuente más lenta y la tasa; si la tasa falla,
  las cotizaciones quedan canceladas.

**17 🟡 Tres llamadas antes de transferir.** `prepareTransfer` con una tercera consulta: la comisión.
- Firma: `suspend fun prepareTransferWithFee(account: String, to: Currency, balances: BalanceClient, fx: FxRateClient, fee: suspend () -> Money): Triple<Money, BigDecimal, Money>`
- Casos: las tres en paralelo (tiempo = la más lenta); si la comisión falla a los 10 ms, saldo y tasa quedan
  cancelados y `currentTime` es 10.

**18 🟡 El árbol de `Job`.** Describir la jerarquía de una corrutina con sus hijos y nietos.
- Firma: `fun describeTree(job: Job, indent: Int = 0): String` (recorre `job.children`)
- Casos: un padre con dos hijos, uno con un nieto, impreso con sangría; después de que un hijo termina, ya no aparece.

**19 🟡 `invokeOnCompletion`.** Saber cómo terminó cada hijo.
- Firma: `suspend fun completionCauses(): Map<String, String>` con tres hijos: uno termina bien, uno se cancela y uno
  falla (dentro de un `supervisorScope`, para que la falla no cancele a los otros)
- Casos: la causa es `null`, una `CancellationException` y la excepción del fallo, respectivamente.

**20 🟡 🎤 Structured concurrency.** La misma consulta con `GlobalScope.launch` y con un scope propio, y lo que pasa al
cancelar.
- Firma: `class LeakyQuoter` y `class ScopedQuoter(scope: CoroutineScope)`, cada uno con `fun start(ticker: String)` y
  `fun stop()`
- Casos: después de `stop()`, el de scope propio no vuelve a consultar (avanza el tiempo y cuenta las consultas); el
  de `GlobalScope` sigue. Explica en un comentario qué es structured concurrency y qué tres problemas resuelve
  (fugas, cancelación, errores que se pierden).

**21 🟡 Un sondeo con su propio scope.** Pedir el precio cada segundo hasta que se detenga.
- Firma: `class QuotePoller(scope: CoroutineScope, source: PriceSource, every: Duration)` con `fun start(ticker: String)`,
  `fun stop()` y `val history: List<Money>`
- Casos: con `backgroundScope` de `runTest`, 5 s de tiempo virtual dan 5 precios; después de `stop()` no llegan más;
  `start` dos veces no lanza dos sondeos.

**22 🟡 Nombres de corrutina.** `CoroutineName` y el modo debug.
- Firma: `suspend fun namedThreads(): List<String>` con tres corrutinas nombradas `quote-ACME`, `quote-ORBX`, `quote-LUMA`
- Casos: con el modo debug activo (las pruebas de Gradle corren con `-ea`), el nombre del hilo termina en
  `@quote-ACME#N`; explica en un comentario cómo se activa en producción (`-Dkotlinx.coroutines.debug`) y qué cuesta.

**23 🟡 Leer el contexto.** Lo que hay dentro de `coroutineContext`.
- Firma: `suspend fun describeContext(): String` con el `Job`, el `CoroutineName` (o `"sin nombre"`) y el dispatcher
- Casos: dentro de `launch(CoroutineName("x"))` sale el nombre; dentro de `withContext(Dispatchers.IO)` sale `IO`; un
  hijo hereda el nombre del padre si no pone otro.

**24 🟡 🎤 El `async` que no era paralelo.** La trampa de esperar dentro del `map`.
- Firma: `suspend fun trapSequential(tickers: List<String>, source: PriceSource): List<Money>` con
  `tickers.map { async { … }.await() }` y `suspend fun trulyParallel(…)` con dos pasadas
- Casos: la primera tarda la suma (700 ms) y la segunda la máxima (300 ms), con el mismo resultado. Explica en un
  comentario por qué.

**25 🟡 `runBlocking` dentro de una corrutina.** El cuelgue de un dispatcher de un solo hilo.
- Firma: `fun nestedRunBlocking(dispatcher: CoroutineDispatcher): String`
- Casos: con un dispatcher de un hilo, una corrutina que hace `runBlocking { withContext(dispatcher) { … } }` se
  cuelga (la prueba lo detecta con un tiempo límite real y cierra el dispatcher); la versión con `withContext`
  directo funciona. Explica en un comentario por qué.

**26 🟡 Saldos de todo el Banco.** Los saldos de N cuentas en paralelo, con el total y la cuenta más lenta.
- Firma: `suspend fun bankTotal(accounts: List<String>, client: BalanceClient): Pair<Money, String>`
- Casos: 50 cuentas con latencias distintas: el total es la suma y el tiempo el de la más lenta; la cuenta más lenta
  es la que tarda más (anota el orden de llegada); lista vacía → cero.

## 🟠 Difíciles

**27 🟠 Resultados parciales con plazo.** Lo que llegue en 200 ms; lo demás queda `null` y se cancela.
- Firma: `suspend fun quotesWithin(tickers: List<String>, source: SimulatedPriceSource, deadline: Duration): Map<String, Money?>`
- Casos: con 200 ms llegan ACME, ORBX, KAPA y NUBE y LUMA queda `null` y cancelada (en `started`, no en `finished`);
  `currentTime` es 200; con 0 ms, todos `null`.

**28 🟠 Reintentos con espera exponencial.** Un reintento genérico para las llamadas que fallan de vez en cuando.
- Firma: `suspend fun <T> retrying(times: Int, initialDelay: Duration, factor: Double, block: suspend (attempt: Int) -> T): T`
- Casos: una fuente que falla dos veces y luego contesta: tres intentos y `currentTime` = 100 + 200 con 100 ms y
  factor 2; si falla siempre, se relanza la última excepción con las anteriores como suprimidas; la cancelación no
  se reintenta.

**29 🟠 Portafolio de portafolios.** Un árbol de carteras (cada una con posiciones y subcarteras) valuado en paralelo.
- Firma: `sealed interface Book` con `Position(ticker, quantity)` y `Folder(name, children)`;
  `suspend fun value(book: Book, source: PriceSource): Money`
- Casos: un árbol de tres niveles: el valor es la suma y el tiempo es el de la cotización más lenta (no por nivel);
  el mismo ticker en dos ramas se pide dos veces (y en el 30 se arregla).

**30 🟠 Una sola consulta por ticker.** Varias corrutinas piden el mismo ticker a la vez: se hace una sola llamada.
- Firma: `class SharedQuotes(scope: CoroutineScope, source: PriceSource)` con `suspend fun price(ticker: String): Money`
  (un `Deferred` por ticker en un `ConcurrentHashMap`)
- Casos: 100 corrutinas piden ACME a la vez y la fuente recibe una sola llamada; si la llamada falla, se saca del
  mapa y el siguiente pedido vuelve a intentar; cancelar a quien pidió no cancela la consulta compartida.

**31 🟠 🎤 ¿Una corrutina es un hilo?** Mídelo.
- Firma: `fun platformThreads(n: Int): Duration` (n hilos que duermen 1 s) y `fun coroutines(n: Int): Duration`
  (n corrutinas que esperan 1 s, con `runBlocking`)
- Casos: 10 000 hilos y 100 000 corrutinas; la prueba solo comprueba que terminan, y los tiempos y la memoria
  (`Runtime.totalMemory - freeMemory`) van en una tabla en un comentario. Explica qué guarda una corrutina
  suspendida y por qué cuesta tan poco.

**32 🟠 🎤 De `CompletableFuture` a `suspend`.** Una cadena de futures reescrita como código secuencial.
- Firma: `fun quoteFuture(ticker: String): CompletableFuture<Money>` (dado, con un `ScheduledExecutorService`),
  `fun transferValueJava(…): CompletableFuture<Money>` con `thenCompose`/`thenCombine`, y
  `suspend fun transferValue(…): Money` con `await()` de `kotlinx.coroutines.future`
- Casos: los dos dan lo mismo; una falla en el medio llega como la misma excepción (sin `CompletionException`
  envolviendo en la versión `suspend`). Explica qué gana la lectura y qué pasa con la cancelación en cada una.

**33 🟠 Camino crítico.** Una transferencia con dependencias: saldo y tasa en paralelo, la comisión necesita los dos,
y la verificación de cumplimiento corre en paralelo con todo.
- Firma: `suspend fun orchestrate(steps: TransferSteps): TransferPlan` (los pasos son funciones `suspend` con `delay`)
- Casos: con latencias 100, 150, 40 y 300, `currentTime` es 300 (cumplimiento) y no 590; si cumplimiento rechaza a
  los 300, lo demás ya terminó pero el resultado es el rechazo; si la tasa falla, cumplimiento queda cancelado.

**34 🟠 Todos los errores, no el primero.** Cotizar todo y juntar todas las fallas en vez de cortar en la primera.
- Firma: `suspend fun quoteCollectingErrors(tickers: List<String>, source: PriceSource): Pair<Map<String, Money>, Map<String, Throwable>>`
- Casos: con dos tickers que fallan, las otras tres cotizaciones llegan y los dos errores también; el tiempo es el
  de la más lenta; compáralo con `quoteInParallel` (que corta) y explica cuándo conviene cada uno.

**35 🟠 Progreso.** Cotizar en paralelo e informar el avance.
- Firma: `suspend fun quoteWithProgress(tickers: List<String>, source: PriceSource, onProgress: (done: Int, total: Int) -> Unit): Map<String, Money>`
- Casos: `onProgress` recibe 1/5, 2/5, … 5/5 en orden de llegada (KAPA primero); con `Dispatchers.Default` en vez de
  `runTest` el contador sigue siendo correcto (usa un `AtomicInteger`).

**36 🟠 Auditoría de un PR.** Encontrar las corrutinas huérfanas de un servicio.
- Firma: reescribe `class LegacyQuoteService` (en el enunciado de tu prueba: usa `GlobalScope.launch` para refrescar
  precios y `GlobalScope.async` para el valor del portafolio) como `class QuoteService(scope: CoroutineScope)`
- Casos: una prueba que demuestra la fuga del original (una corrutina sigue viva después de terminar la prueba) y otra
  que demuestra que el nuevo cancela todo al cancelar el scope. Escribe los comentarios del PR en un comentario.

## 🔴 Muy difíciles

**37 🔴 `parallelMap` con límite, sin `Semaphore`.** N trabajadores que sacan tareas de un índice compartido.
- Firma: `suspend fun <T, R> List<T>.parallelMap(concurrency: Int, transform: suspend (T) -> R): List<R>`
- Casos: conserva el orden; con concurrencia 2 sobre latencias conocidas, `currentTime` es el del reparto (calcúlalo);
  nunca hay más de N en curso (cuéntalo); una falla cancela a los trabajadores; concurrencia ≤ 0 →
  `IllegalArgumentException`.

**38 🔴 Un `StructuredTaskScope` de juguete.** La API de Java 21 (vista previa) imitada sobre corrutinas.
- Firma: `class TaskScope(…)` con `fun <T> fork(block: suspend () -> T): Subtask<T>`, `suspend fun join()`,
  `fun throwIfFailed()` y las dos políticas `ShutdownOnFailure` y `ShutdownOnSuccess`
- Casos: con `ShutdownOnFailure`, una falla cancela a las demás subtareas; con `ShutdownOnSuccess`, la primera que
  termina gana; `get()` de una subtarea sin terminar lanza `IllegalStateException`. Explica en un comentario qué le
  falta a la versión de Java 21 que `coroutineScope` sí tiene.

**39 🔴 Tres versiones del fan-out.** El mismo fan-out de cinco fuentes con `CompletableFuture` + executor, con
`StructuredTaskScope` (JDK 21 con `--enable-preview`) y con corrutinas.
- Firma: tres funciones con la misma firma `(tickers) -> Map<String, Money>`; las fuentes de Java bloquean con
  `Thread.sleep` y la de Kotlin suspende
- Casos: las tres dan lo mismo; ante una falla, comprueba en cada una si las otras tareas se cancelaron (en la de
  `CompletableFuture` no: demuéstralo). Si no quieres activar la vista previa, explica en un comentario lo que daría.

**40 🔴 Planificador de tareas con dependencias.** Un grafo de tareas con duraciones que corre todo con el máximo
paralelismo posible.
- Firma: `data class Task(val id: String, val duration: Duration, val dependsOn: Set<String>)` y
  `suspend fun runAll(tasks: List<Task>): List<Pair<String, Long>>` (id e instante de fin)
- Casos: el tiempo total es el camino crítico; cada tarea empieza en cuanto terminan sus dependencias; un ciclo →
  `IllegalArgumentException` antes de lanzar nada; una tarea que falla cancela a las que dependen de ella y a las que
  siguen corriendo.

## ⚡ Saiyajin

**41 ⚡ Fan-out de cotizaciones.** Cinco fuentes de precios simuladas, con latencias fijas, consultadas en paralelo
dentro de un `coroutineScope` para armar el valor de un portafolio.
- Firma: `class QuoteFanOut(sources: Map<String, PriceSource>)` con `suspend fun portfolio(holdings: Map<String, Long>): Money`
  y `suspend fun snapshot(): Map<String, Money>`
- Reglas: una fuente por ticker; si una falla, el portafolio falla con una excepción de dominio que trae el ticker y la
  causa, y nada queda corriendo; sin `GlobalScope` ni `runBlocking` dentro de la clase.
- **Se verifica con:** una prueba en tiempo virtual (`runTest`) donde el tiempo total es el de la fuente más lenta y
  no la suma, y otra donde una fuente falla y se comprueba que las demás quedaron canceladas (empezaron y no
  terminaron).
