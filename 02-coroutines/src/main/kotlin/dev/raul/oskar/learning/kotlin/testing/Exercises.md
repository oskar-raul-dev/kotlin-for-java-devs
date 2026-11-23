# Ejercicios: `runTest`, tiempo virtual y Turbine

> **Lección:** `testing` · **Dónde se resuelven:** `testing/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../testing/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.testing.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Aquí lo que se escribe es sobre
> todo la **prueba**: el código de producción suele ser corto. En kotlinx-coroutines-test 1.11.0, `currentTime`,
> `advanceTimeBy`, `advanceUntilIdle`, `runCurrent`, `UnconfinedTestDispatcher`, `setMain` y `resetMain` piden
> `@OptIn(ExperimentalCoroutinesApi::class)` (medido). Regla de la casa: ninguna prueba usa `Thread.sleep` ni depende
> del reloj real, salvo que el enunciado lo pida para comparar. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 19, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 Probar un `delay(5.minutes)`.** Sin esperar.
- Firma: `suspend fun settleAfterCooldown(): String` (espera 5 minutos y devuelve `"liquidado"`)
- Casos: con `runTest`, la prueba tarda milisegundos de reloj real (`measureTime`) y `currentTime` es 300 000.
  Explica en un comentario cómo funciona el tiempo virtual (un planificador que salta al siguiente evento).

**02 🟢 `currentTime` en cada paso.** Seguir un flujo de pasos con tiempos.
- Firma: `suspend fun transferSteps(log: MutableList<Pair<String, Long>>, clock: () -> Long)` (débito 20 ms, tasa
  100 ms, crédito 20 ms)
- Casos: el log tiene `("débito", 20)`, `("tasa", 120)`, `("crédito", 140)` usando `currentTime` como reloj.

**03 🟢 `advanceTimeBy` y `runCurrent`.** Ver el estado justo antes y justo después.
- Firma: usa `RateMonitor` de la lección
- Casos: a los 99 ms no hay tasa; a los 100 ms, después de `runCurrent`, sí; a los 60 100 ms, dos sondeos. Explica en
  un comentario por qué `advanceTimeBy(n)` no corre lo que está programado exactamente en `n`.

**04 🟢 `advanceUntilIdle`.** Correr todo lo pendiente.
- Firma: `fun CoroutineScope.scheduleReminders(log: MutableList<String>)` (tres `launch` con `delay` de 1, 2 y 3 horas)
- Casos: después de `advanceUntilIdle`, los tres corrieron y `currentTime` es 3 horas; con un bucle infinito en
  `backgroundScope`, `advanceUntilIdle` no se cuelga (explica por qué).

**05 🟢 🎤 `StandardTestDispatcher` frente a `UnconfinedTestDispatcher`.** El mismo código, dos órdenes.
- Firma: usa `launchOrder` de la lección
- Casos: los dos órdenes exactos; explica en un comentario cuándo conviene cada uno (Standard para controlar el orden,
  Unconfined para colectores que no deben perder valores).

**06 🟢 `backgroundScope`.** Una corrutina que nunca termina.
- Firma: `fun CoroutineScope.heartbeat(counter: AtomicInteger)` (un `launch` con `while (true) { delay(1.seconds); … }`)
- Casos: en `backgroundScope`, 10 s virtuales dan 10 latidos y la prueba termina; en el scope de la prueba, `runTest`
  falla (anota el mensaje: espera a los hijos y se agota su tiempo límite de reloj real).

**07 🟢 Inyectar el dispatcher.** Pasar de un `Dispatchers.IO` fijo a uno inyectado.
- Firma: `class StatementLoader(io: CoroutineDispatcher = Dispatchers.IO)` con `suspend fun load(): List<String>` (un
  `delay(2.seconds)` simulando el archivo)
- Casos: con `StandardTestDispatcher(testScheduler)`, `currentTime` es 2 000 y la prueba no espera; con `Dispatchers.IO`
  fijo, espera 2 s reales (mídelo una vez y explica).

**08 🟢 `runTest` devuelve un resultado de prueba.** No lo descartes en JUnit 5.
- Firma: ninguna: una prueba escrita como función de bloque (`= runTest { }`) y otra que llama a `runTest` dentro de
  `{ }` sin devolverlo
- Casos: en la JVM las dos corren; explica en un comentario por qué en Kotlin/JS el resultado de `runTest` tiene que
  devolverse (es una promesa) y por qué conviene el estilo `= runTest { }` siempre.

**09 🟢 Turbine: `awaitItem` y `awaitComplete`.** Un flujo finito.
- Firma: `fun tickerPrices(ticks: List<Tick>, ticker: String): Flow<Money>`
- Casos: con `test { }`, un `awaitItem` por precio y `awaitComplete` al final; un `awaitItem` de más falla con un
  mensaje claro (mira el mensaje de Turbine).

**10 🟢 Turbine: errores.** Un flujo que falla.
- Firma: `fun failingFeed(): Flow<Tick>` (dos ticks y una `IOException`)
- Casos: `awaitItem` dos veces y `awaitError()` devuelve la `IOException`.

**11 🟢 Turbine: `expectNoEvents`.** Que no pase nada por un rato.
- Firma: `class AlertGate(threshold: Money)` con `val alerts: SharedFlow<String>` y `suspend fun onPrice(p: Money)`
- Casos: precios bajo el umbral → `expectNoEvents()`; uno encima → `awaitItem()`.

**12 🟢 `runBlocking` usa tiempo real.** Por qué no sirve para probar tiempos.
- Firma: la misma prueba del 01 con `runBlocking`
- Casos: tarda los 5 minutos… así que redúcelo a 200 ms y mídelo; explica en un comentario por qué la prueba con
  `runBlocking` es lenta y frágil.

## 🟡 Medios

**13 🟡 🎤 Probar un `Flow` de punta a punta.** El pipeline de `flow` (limpieza + velas + alertas) con Turbine.
- Firma: pruebas sobre `TickFeed(...).flow().cleaned().candles()` de la lección `flow`
- Casos: cada vela llega en el `currentTime` correcto; `expectNoEvents` entre velas; las alertas en su instante.
  Explica en un comentario las tres formas de probar un `Flow` (`toList`, `take`/`first`, Turbine) y cuándo cada una.

**14 🟡 Dos flujos a la vez con Turbine.** `turbineScope` y `testIn`.
- Firma: pruebas sobre `PriceBoard` de `hotflows`: el precio de ACME y las alertas, recolectados a la vez
- Casos: con `turbineScope { val prices = …testIn(backgroundScope); val alerts = …testIn(backgroundScope) }`, el orden
  relativo de precios y alertas es el esperado; se cancelan los dos al final.

**15 🟡 Un `StateFlow` con Turbine sin perder valores.** El orden de las emisiones de un ViewModel de juguete.
- Firma: `class TransferViewModel(scope: CoroutineScope, service: suspend (Money) -> String)` con
  `val state: StateFlow<UiState>` (`Idle`, `Sending`, `Done`, `Error`) y `fun send(amount: Money)`
- Casos: con un servicio que tarda 1 s, Turbine ve `Idle`, `Sending`, `Done`; con un servicio que contesta sin
  suspender, `Sending` puede no verse (explica por qué y cómo lo resolverías si importara).

**16 🟡 `Dispatchers.setMain`.** Un ViewModel que usa `Dispatchers.Main`.
- Firma: el mismo ViewModel del 15 con `viewModelScope` simulado como `CoroutineScope(Dispatchers.Main + SupervisorJob())`
- Casos: con `setMain(StandardTestDispatcher(testScheduler))` en `@BeforeTest` y `resetMain()` en `@AfterTest`, todo
  queda en tiempo virtual; sin `setMain`, falla con "Module with the Main dispatcher is missing" (medido). Se retoma en
  `12-android`.

**17 🟡 Una regla reutilizable.** Una extensión de JUnit 5 que hace `setMain`/`resetMain`.
- Firma: `class MainDispatcherExtension(val dispatcher: TestDispatcher = StandardTestDispatcher()) : BeforeEachCallback, AfterEachCallback`
- Casos: dos clases de prueba la usan con `@RegisterExtension`; las pruebas comparten el planificador con
  `runTest(extension.dispatcher)`.

**18 🟡 El tiempo límite de una transferencia.** `withTransferDeadline` de la lección en todos sus bordes.
- Firma: pruebas sobre `withTransferDeadline`
- Casos: termina a los 29 999 ms; falla a los 30 000 exactos (¿o pasa? mídelo y explica el empate); la causa es una
  `TimeoutCancellationException`; la cancelación del llamador no se convierte en `TransferTimedOut`.

**19 🟡 🎤 ¿Por qué mi prueba se cuelga?** Tres pruebas que no terminan y su arreglo.
- Firma: tres pruebas mal escritas (un `collect` sobre un `StateFlow` en el cuerpo de la prueba, un `launch` con bucle
  infinito sin `backgroundScope`, un `CompletableDeferred` que nadie completa) y sus versiones corregidas
- Casos: las rotas se marcan con `@Disabled` y un comentario con el síntoma (el mensaje de `runTest` tras su tiempo
  límite); las corregidas pasan. Explica en un comentario la regla de cada una.

**20 🟡 El tiempo límite de `runTest`.** Qué pasa con una prueba lenta de verdad.
- Firma: una prueba con `runTest(timeout = 1.seconds)` que bloquea con `Thread.sleep(2_000)` dentro
- Casos: falla por tiempo (anota el mensaje); explica en un comentario que ese límite es de reloj real (el virtual no
  cuenta) y cuál es el valor por defecto en 1.11.0 (búscalo en la documentación de `runTest`).

**21 🟡 Probar reintentos con espera exponencial.** Los tiempos exactos.
- Firma: `suspend fun <T> retrying(times: Int, initial: Duration, block: suspend () -> T): T` (de `fundamentals`, ejercicio 28)
- Casos: con una fuente que falla tres veces, los intentos ocurren en 0, 100, 300 y 700 ms (anótalos con
  `currentTime`); sin tiempo virtual, la prueba tardaría casi un segundo.

**22 🟡 Un reloj inyectado y el tiempo virtual.** `TestScope` como fuente de `Clock`.
- Firma: `fun TestScope.virtualClock(start: Instant): java.time.Clock` (con `currentTime` sumado a `start`)
- Casos: un servicio que registra `Instant.now(clock)` ve el tiempo avanzar con `advanceTimeBy`; útil para el corte de
  fecha de `datetime` y el libro con instantes.

**23 🟡 Probar la cancelación.** Que un servicio libere todo al cancelarse.
- Firma: pruebas sobre `CompensatingTransfer` de `cancellation`
- Casos: cancelar en cada fase (antes del débito, durante la tasa, durante el crédito) con `advanceTimeBy` al instante
  exacto; en todas, el saldo final es correcto y el log no tiene operaciones duplicadas.

**24 🟡 Pruebas parametrizadas en tiempo virtual.** Una tabla de latencias y resultados.
- Firma: `@ParameterizedTest` con `@MethodSource` y `runTest` adentro, sobre `quoteInParallel` de `fundamentals`
- Casos: cinco filas (latencias → tiempo total esperado) que pasan todas; explica en un comentario cómo combinar
  `runTest` con pruebas parametrizadas de JUnit 5.

**25 🟡 Probar un canal.** El actor de `channels` paso a paso.
- Firma: pruebas sobre `MatchingEngine`
- Casos: con `StandardTestDispatcher`, una orden enviada no se procesa hasta `runCurrent()`; el canal de ejecuciones se
  prueba con Turbine (`executions.consumeAsFlow().test { }`) o con `receive` y tiempos límite.

**26 🟡 Fakes en vez de mocks.** Un `FxRateClient` falso con guion.
- Firma: `class ScriptedFx(vararg steps: Step)` con `Step.Answer(rate, after)` y `Step.Fail(error, after)`
- Casos: una prueba de `CompensatingTransfer` usa el guion para cubrir éxito, falla y lentitud; explica en un
  comentario por qué un fake con tiempos es mejor que un mock para código con corrutinas (se retoma en `05-testing`
  con MockK y `coEvery`).

## 🟠 Difíciles

**27 🟠 🎤 Probar código con varios dispatchers.** Un servicio con `io` y `cpu` inyectados.
- Firma: `class Settlement(io: CoroutineDispatcher, cpu: CoroutineDispatcher)` con `suspend fun settleDay(): Report`
- Casos: en la prueba, los dos son `StandardTestDispatcher(testScheduler)` del mismo planificador y los tiempos cuadran;
  con dos planificadores distintos, el `runTest` falla o se cuelga (demuéstralo con un tiempo límite y explica por qué
  todos los dispatchers de prueba deben compartir planificador).

**28 🟠 Pruebas deterministas con hilos de verdad.** Cuando hay que usar `Dispatchers.Default`.
- Firma: una prueba de `OrderedLockBank` de `sync` que corre 100 veces seguidas con semillas distintas
- Casos: todas pasan; si una falla, imprime la semilla para reproducirla; explica en un comentario qué cosas solo se
  pueden probar con hilos de verdad (carreras de datos) y cuáles en tiempo virtual (intercalaciones, tiempos).

**29 🟠 Un `TestDispatcher` propio que registra.** Saber qué se despachó y cuándo.
- Firma: un envoltorio de `CoroutineDispatcher` que delega en un `StandardTestDispatcher` y anota cada `dispatch`
- Casos: cuenta cuántas veces se despachó en un flujo con `flowOn` (los cambios de contexto se ven); útil para
  depurar pruebas.

**30 🟠 Probar `WhileSubscribed` sin Turbine y con Turbine.** Las dos formas, comparadas.
- Firma: pruebas sobre `SharedLatest` de `hotflows`
- Casos: con colectores en `backgroundScope` y `advanceTimeBy`, y con Turbine (`test { }` y `cancelAndIgnoreRemainingEvents`);
  las dos comprueban la parada a los 5 000 ms exactos. Discute cuál se lee mejor.

**31 🟠 🎤 Una prueba que fallaba de vez en cuando.** Convertir una prueba inestable en determinista.
- Firma: una prueba "inestable" dada (con `Thread.sleep(100)` esperando a que una corrutina en `Default` termine) y su
  versión con tiempo virtual y dispatcher inyectado
- Casos: la inestable falla en un bucle de 1 000 corridas al menos una vez si bajas el `sleep` (demuéstralo); la nueva
  pasa siempre. Explica las tres causas típicas de pruebas inestables con corrutinas.

**32 🟠 Probar `debounce` y `sample` al milisegundo.** Operadores de tiempo de `flow`.
- Firma: pruebas sobre `settled` y `everySecond` (ejercicio 16 de `flow`)
- Casos: emisiones en instantes exactos (0, 100, 250, 900, 1 300…) y salidas exactas; con Turbine y `currentTime` en
  cada `awaitItem`.

**33 🟠 La sesión con alertas, como propiedad.** Pruebas basadas en propiedades sobre el resumen de la sesión.
- Firma: pruebas sobre `summarize` con 100 semillas
- Casos: para toda semilla, `candles` = 5 × minutos; el cierre de cada ticker es el último tick del feed; las alertas no
  dependen del orden de los tickers. (Con Kotest property testing, se retoma en `05-testing`; aquí, un bucle de semillas.)

**34 🟠 Medir la cobertura de los caminos de cancelación.** Que cada `finally` y cada `invokeOnCancellation` corra en
alguna prueba.
- Firma: pruebas sobre `quote` de `internals` y `CompensatingTransfer`
- Casos: un contador por camino (éxito, error, cancelación) y una prueba final que verifica que todos se ejecutaron al
  menos una vez; opcional: Kover (`08-build`) para la cobertura de líneas.

**35 🟠 Un *watchdog* de hilos bloqueados en pruebas.** Detectar código que bloquea un dispatcher.
- Firma: `suspend fun <T> assertNoBlocking(maxBlock: Duration, block: suspend () -> T): T` (corre el bloque en un
  dispatcher de un hilo y un hilo vigía mide cuánto tarda en responder un *ping*)
- Casos: un servicio *main-safe* pasa; uno que hace JDBC simulado sin `withContext` falla con el tiempo bloqueado.

**36 🟠 Probar el libro de órdenes contra un modelo.** *Model-based testing*: el actor contra una implementación simple.
- Firma: un modelo trivial (listas ordenadas a mano, lento pero obvio) y el `MatchingEngine` de `channels`
- Casos: 1 000 secuencias aleatorias de órdenes con semilla dan las mismas ejecuciones en los dos; al primer
  desacuerdo, la prueba imprime la secuencia mínima que lo reproduce (achica la lista a mano).

## 🔴 Muy difíciles

**37 🔴 Un `runTest` de juguete.** Tiempo virtual propio sobre el bucle de eventos de `internals` (ejercicio 37).
- Firma: `fun miniRunTest(block: suspend MiniTestScope.() -> Unit)` con `currentTime` y un `delay` que salta el reloj
- Casos: `delay(1.hours)` vuelve al instante; varias corrutinas con `delay` terminan en el orden del reloj virtual; una
  corrutina que nunca termina hace fallar la prueba con un mensaje claro.

**38 🔴 Una sesión de mercado reproducible.** Grabar y reproducir.
- Firma: un `RecordingFxClient` que graba pedidos y respuestas con sus instantes virtuales, y un `ReplayFxClient` que
  los reproduce
- Casos: una sesión grabada y reproducida da el mismo resumen; un pedido que no estaba en la grabación falla con un
  mensaje que dice cuál.

**39 🔴 Pruebas de concurrencia con Lincheck (opcional).** Verificar un contador o una cola concurrente.
- Firma: un test de Lincheck (librería de JetBrains) sobre el `BalanceActor` de `channels` (ejercicio 18) o sobre una
  cola propia
- Casos: Lincheck no encuentra ejecuciones no linealizables en la versión correcta y sí en una versión rota a propósito;
  explica en un comentario qué es la linealizabilidad.

**40 🔴 Un arnés de pruebas de rendimiento con tiempo virtual y real.** Comparar el mismo pipeline en los dos.
- Firma: libre
- Casos: el pipeline de `flow` con tiempo virtual (lógica: tiempos exactos) y con tiempo real (rendimiento: throughput
  con calentamiento); explica en un comentario por qué cada uno responde una pregunta distinta y no se mezclan.

## ⚡ Saiyajin

**41 ⚡ Una sesión de mercado en un segundo.** La sesión completa (6.5 h de ticks simulados) procesada en tiempo
virtual.
- Firma: `class SessionRunner(feed: Flow<Tick>, io: CoroutineDispatcher, cpu: CoroutineDispatcher)` con
  `suspend fun run(): SessionReport` (velas de un minuto, alertas y cierres, como `summarize` de la lección y el
  pipeline de `flow`)
- Reglas: dispatchers inyectados; ningún `Thread.sleep`; mismo resultado en cada corrida (semilla fija); sin
  `runBlocking` en las pruebas.
- **Se verifica con:** `measureTime` que muestra menos de 1 s de reloj real, el mismo resultado en 100 corridas
  seguidas, y pruebas con Turbine del orden de las velas y las alertas.
