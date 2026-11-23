# Ejercicios: `Flow`, operadores, contexto y backpressure

> **Lección:** `flow` · **Dónde se resuelven:** `flow/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../flow/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.flow.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `runTest` y,
> cuando conviene, Turbine (`flow.test { awaitItem() }`). Los ticks salen de `tickFeed` (y de `withFlaws` para los
> defectos). En 1.11.0, `debounce`, `sample` y `timeout` piden `@OptIn(FlowPreview::class)`, y `flatMapMerge`,
> `flatMapConcat`, `flatMapLatest` y `chunked` piden `@OptIn(ExperimentalCoroutinesApi::class)` (medido). El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 06, 13, 19, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 Frío.** Demostrar que un `Flow` no hace nada hasta `collect` y que cada `collect` lo vuelve a correr.
- Firma: `fun countedFlow(counter: AtomicInteger): Flow<Int>` (incrementa al empezar)
- Casos: crear el `Flow` deja el contador en 0; dos `toList()` lo dejan en 2. Explica en un comentario la diferencia
  con un `Stream` de Java (de un solo uso) y con un flujo caliente.

**02 🟢 `flowOf`, `asFlow` y `flow { }`.** Tres formas de crear el feed.
- Firma: `fun ticksA(ticks: List<Tick>): Flow<Tick>`, `ticksB` y `ticksC`, uno con cada constructor
- Casos: los tres emiten lo mismo; solo el de `flow { }` puede hacer `delay` entre ticks.

**03 🟢 `map`, `filter`, `take`.** Los precios de ACME por encima de 100.
- Firma: `fun Flow<Tick>.acmeAbove(limit: Money): Flow<Money>`
- Casos: con `tickFeed`, solo ACME y solo los mayores; `take(3)` corta el productor después del tercero (cuenta
  cuántos ticks se generaron).

**04 🟢 Operadores terminales.** `first`, `single`, `fold`, `count`, `toList`.
- Firma: `suspend fun summary(ticks: Flow<Tick>): Summary` con conteo, precio máximo de ACME y primer tick de LUMA
- Casos: con un feed chico conocido; `single` sobre un flujo de dos lanza; `first` sobre uno vacío lanza
  `NoSuchElementException`.

**05 🟢 `onEach` y `onStart`/`onCompletion`.** Registrar el ciclo de vida del flujo.
- Firma: `fun Flow<Tick>.logged(log: MutableList<String>): Flow<Tick>`
- Casos: el log tiene "inicio", cada tick y "fin"; si el colector cancela, `onCompletion` recibe la causa.

**06 🟢 🎤 Backpressure por suspensión.** Ver al productor esperar.
- Firma: `suspend fun producerWaits(): List<Long>` (instantes de cada `emit` con un consumidor de 100 ms)
- Casos: los `emit` salen cada 100 ms aunque el productor no tiene `delay`; explica en un comentario por qué en
  `Flow` no hace falta `request(n)`.

**07 🟢 `catch` y `onCompletion`.** Un feed que falla a mitad.
- Firma: `fun Flow<Tick>.failingAfter(n: Int): Flow<Tick>` y `fun Flow<Tick>.recoverWithLast(): Flow<Tick>`
- Casos: `catch` emite el último tick bueno otra vez y termina; una excepción en el `collect` no la atrapa `catch`.

**08 🟢 `distinctUntilChanged`.** Solo los cambios de precio.
- Firma: `fun Flow<Money>.changes(): Flow<Money>`
- Casos: `100, 100, 101, 101, 100` → `100, 101, 100`; con `distinctUntilChangedBy` sobre el ticker.

**09 🟢 `transform`.** Emitir cero, uno o varios por elemento.
- Firma: `fun Flow<Execution>.legs(): Flow<String>` (cada ejecución da dos líneas: comprador y vendedor)
- Casos: tres ejecuciones → seis líneas en orden.

**10 🟢 `zip` y `combine`.** Precio y tasa de cambio.
- Firma: `fun pricesInEur(prices: Flow<Money>, rates: Flow<BigDecimal>, combineLatest: Boolean): Flow<Money>`
- Casos: con `zip`, uno a uno (el más corto manda); con `combine`, cada vez que cambia cualquiera de los dos (tiempo
  virtual con instantes distintos).

**11 🟢 `runningFold`.** El saldo acumulado de una cuenta.
- Firma: `fun Flow<Money>.runningBalance(start: Money): Flow<Money>`
- Casos: depósitos y retiros dan los saldos intermedios; flujo vacío → solo el saldo inicial.

**12 🟢 `flowOn`.** Leer un archivo de ticks en `IO`.
- Firma: `fun ticksFromFile(path: Path, io: CoroutineDispatcher): Flow<String>`
- Casos: el productor corre en el dispatcher dado y el colector no; en la prueba, `io` es un
  `StandardTestDispatcher(testScheduler)`.

## 🟡 Medios

**13 🟡 🎤 `withContext` dentro de `flow { }`.** La excepción y las dos soluciones.
- Firma: `fun brokenTicks(io: CoroutineDispatcher): Flow<Tick>`, `fun fixedWithFlowOn(…)` y `fun fixedWithChannelFlow(…)`
- Casos: el roto lanza "Flow invariant is violated"; los dos arreglos emiten bien. Explica en un comentario qué
  invariante protege (el contexto del colector) y por qué.

**14 🟡 `buffer`, `conflate` y `collectLatest`.** Un consumidor lento con las tres.
- Firma: `suspend fun consume(strategy: String, prices: Flow<Money>, work: Duration): List<Money>`
- Casos: en tiempo virtual, `buffer` recibe todo, `conflate` salta intermedios y `collectLatest` cancela el trabajo del
  anterior cuando llega uno nuevo (solo el último termina); anota los tiempos totales.

**15 🟡 Velas de un minuto con el feed real.** Usar `cleaned` y `candles` de la lección sobre `withFlaws(tickFeed())`.
- Firma: `suspend fun minuteCandles(seed: Long): Map<String, List<Candle>>`
- Casos: dos minutos de feed dan dos velas por ticker; `high ≥ open, close ≥ low` en todas; el número de ticks de las
  velas suma lo que dejó la limpieza.

**16 🟡 `debounce` y `sample`.** Una ráfaga de órdenes de un usuario y un tablero que se actualiza cada segundo.
- Firma: `fun Flow<String>.settled(quiet: Duration): Flow<String>` y `fun Flow<Money>.everySecond(): Flow<Money>`
- Casos: con instantes exactos, `debounce(300)` deja pasar solo el último de cada ráfaga; `sample(1000)` da uno por
  segundo; los dos piden `@FlowPreview` (medido en 1.11.0).

**17 🟡 `flatMapConcat`, `flatMapMerge` y `flatMapLatest`.** Las cotizaciones de cada ticker que el usuario selecciona.
- Firma: `fun Flow<String>.quotes(source: (String) -> Flow<Money>, mode: String): Flow<Money>`
- Casos: con `concat`, uno tras otro; con `merge`, intercalados; con `latest`, al cambiar de ticker se cancela el
  anterior. Instantes exactos en tiempo virtual.

**18 🟡 `channelFlow`.** Un flujo que mezcla dos fuentes concurrentes.
- Firma: `fun mergedFeeds(a: Flow<Tick>, b: Flow<Tick>): Flow<Tick>` con `channelFlow { launch { … } }`
- Casos: llegan todos los de las dos, intercalados por tiempo; si una falla, el flujo falla y la otra se cancela;
  compáralo con `merge(a, b)` de la librería.

**19 🟡 🎤 `callbackFlow` de un cliente de precios.** Adaptar una API de listeners.
- Firma: `fun PriceListenerClient.pricesWithErrors(ticker: String): Flow<String>` (agrega `onError` a la interfaz de la
  lección)
- Casos: el error del cliente cierra el flujo con la causa; cancelar el colector desregistra (cuéntalo); sin
  `awaitClose` lanza `IllegalStateException` (medido en tu prueba). Explica en un comentario para qué sirve
  `awaitClose`.

**20 🟡 `retry` y `retryWhen`.** Reconectar el feed con espera exponencial.
- Firma: `fun Flow<Tick>.resilient(maxAttempts: Int, initial: Duration): Flow<Tick>`
- Casos: una fuente que falla dos veces y luego anda: los tiempos de reintento son 100 y 200 ms exactos; al pasar el
  máximo se propaga la excepción; los ticks emitidos antes de la falla se vuelven a emitir (explica por qué: el flujo
  es frío y se vuelve a correr entero).

**21 🟡 `stateIn` sin `stateIn`: ventana deslizante.** El promedio móvil de los últimos N precios.
- Firma: `fun Flow<Money>.movingAverage(n: Int): Flow<BigDecimal>`
- Casos: con n = 3, el primer valor sale con el tercer precio; el estado vive dentro del `flow { }` (dos colecciones no
  se pisan).

**22 🟡 Alertas con histéresis.** Una alerta por cruce, sin repetir mientras el precio oscila cerca del umbral.
- Firma: `fun Flow<Tick>.alertsWithBand(ticker: String, threshold: Money, band: Money): Flow<PriceAlert>`
- Casos: oscilaciones dentro de la banda no generan alertas; un cruce real sí; comparar con `alerts` de la lección
  sobre la misma secuencia.

**23 🟡 `timeout` del flujo.** Detectar que el feed se quedó callado.
- Firma: `fun Flow<Tick>.silentAfter(limit: Duration): Flow<Tick>` con el operador `timeout` (pide `@FlowPreview`, medido)
- Casos: un hueco de 5 s con límite de 3 s lanza `TimeoutCancellationException` a los 3 s del último tick; con
  `catch`, se convierte en una alerta "feed caído".

**24 🟡 Unir el feed con un catálogo.** Enriquecer cada tick con el nombre del instrumento, cargado una vez.
- Firma: `fun Flow<Tick>.enriched(loadCatalog: suspend () -> Map<String, String>): Flow<Pair<Tick, String>>`
- Casos: el catálogo se carga una sola vez por colección (cuéntalo); un ticker desconocido sale con `"?"`.

**25 🟡 Probar con Turbine.** Las velas emitidas en el instante correcto.
- Firma: prueba sobre `TickFeed(...).flow().cleaned().candles(windowMillis = 1_000)`
- Casos: con `test { }`, la primera vela llega en `currentTime == 1_000` (al llegar el primer tick de la ventana
  siguiente); `expectNoEvents` entre velas; `awaitComplete` al final.

**26 🟡 Partir el feed por ticker.** Un `Flow` por ticker a partir de uno solo, sin leer la fuente cinco veces.
- Firma: `fun Flow<Tick>.splitBy(scope: CoroutineScope, tickers: List<String>): Map<String, Flow<Tick>>` (con
  `shareIn`, adelanto de `hotflows`)
- Casos: la fuente se abre una vez para cinco colectores (cuéntalo con `TickFeed.opened`); cada flujo solo trae su
  ticker.

## 🟠 Difíciles

**27 🟠 🎤 Backpressure en `Flow` frente a Reactor.** El mismo productor rápido con `Flow` y con un
`java.util.concurrent.Flow.Publisher` escrito a mano (con `request(n)`).
- Firma: dos implementaciones del mismo productor de 1 000 precios
- Casos: en las dos, el productor nunca está más de N elementos adelante; explica en un comentario por qué la versión
  `Flow` no necesita `request(n)` ni `Subscription`, y qué hace la suspensión en su lugar.

**28 🟠 VWAP por ventana.** El precio promedio ponderado por volumen de cada minuto.
- Firma: `fun Flow<Pair<Tick, Long>>.vwapPerMinute(): Flow<Pair<Long, BigDecimal>>` (tick y volumen)
- Casos: ventanas con datos conocidos; ventana sin volumen se omite; resultado exacto con `BigDecimal` y redondeo
  `HALF_EVEN` a 4 decimales.

**29 🟠 Reordenar con tolerancia.** Un feed que llega desordenado hasta 2 s: reordenarlo en vez de descartar.
- Firma: `fun Flow<Tick>.reordered(tolerance: Duration): Flow<Tick>` (un buffer ordenado por instante)
- Casos: con `withFlaws`, el resultado queda ordenado por ticker y no pierde ninguno (a diferencia de `cleaned`); un
  tick que llega más tarde que la tolerancia se descarta y se cuenta.

**30 🟠 `buffer` con política de descarte.** Ráfagas del feed con un consumidor lento: precios y ejecuciones.
- Firma: `fun Flow<Money>.latestPrices(): Flow<Money>` y `fun Flow<Execution>.allExecutions(capacity: Int): Flow<Execution>`
- Casos: los precios usan `buffer(1, DROP_OLDEST)` o `conflate` y pierden intermedios; las ejecuciones usan buffer
  acotado y llegan todas aunque el productor tenga que esperar. Explica por qué no se puede usar lo mismo para las dos.

**31 🟠 🎤 `flowOn` y sus buffers.** Cuántos elementos puede adelantarse el productor con `flowOn`.
- Firma: `suspend fun aheadWithFlowOn(io: CoroutineDispatcher): Int`
- Casos: con `flowOn`, el productor se adelanta hasta 64 elementos (el buffer por defecto, medido); con
  `flowOn` + `buffer(0)`, ninguno… o el mínimo (pruébalo). Explica en un comentario por qué `flowOn` agrega un canal.

**32 🟠 Un operador `windowed` por tiempo.** Ventanas deslizantes de 5 s que avanzan cada segundo.
- Firma: `fun <T> Flow<T>.windowedByTime(size: Duration, step: Duration, clock: () -> Long): Flow<List<T>>`
- Casos: instantes exactos en tiempo virtual; cada elemento aparece en `size / step` ventanas; flujo vacío → ninguna
  ventana.

**33 🟠 Detección de huecos.** Un ticker que deja de cotizar más de N segundos dentro de la sesión.
- Firma: `fun Flow<Tick>.gaps(max: Duration): Flow<Pair<String, LongRange>>`
- Casos: un hueco de 30 s en LUMA se detecta con su rango; los huecos fuera de sesión no cuentan (decide cómo se
  define la sesión).

**34 🟠 Conciliación en streaming.** Dos flujos (ejecuciones del Mercado y asientos del Banco) que deben cuadrar.
- Firma: `fun reconcile(executions: Flow<Execution>, postings: Flow<String>, within: Duration): Flow<String>`
  (diferencias)
- Casos: cada ejecución debe tener su asiento dentro del plazo; un asiento sin ejecución y una ejecución sin asiento
  salen como diferencias.

**35 🟠 Flujo cancelable con limpieza.** Un feed que abre una conexión simulada y la cierra siempre.
- Firma: `fun connectionFeed(log: MutableList<String>): Flow<Tick>` (con `try/finally` dentro del `flow { }`)
- Casos: `take(3)` cierra la conexión; una excepción en el colector también; la cancelación del scope también.

**36 🟠 Comparar con `Sequence` y `Stream`.** El mismo pipeline de limpieza y velas en las tres.
- Firma: tres funciones con el mismo resultado sobre `tickFeed(steps = 10_000)`
- Casos: resultados iguales; tiempos con calentamiento en una tabla en un comentario; explica cuándo cada una (sin
  suspensión → `Sequence`; asíncrono → `Flow`).

## 🔴 Muy difíciles

**37 🔴 `Flow` desde cero.** Una interfaz `MiniFlow<T>` con `collect`, constructores y cinco operadores.
- Firma: `fun interface MiniFlow<T> { suspend fun collect(collector: suspend (T) -> Unit) }` con `map`, `filter`, `take`,
  `onEach` y `buffer`
- Casos: pasa las pruebas de frío, `take` (que corta al productor) y backpressure de la lección; explica en un
  comentario cómo `take` detiene al productor sin un canal (una excepción interna).

**38 🔴 Libro de órdenes reactivo.** El motor de `channels` expuesto como flujos: entrada `Flow<Command>` y salida
`Flow<Execution>`.
- Firma: `fun Flow<Command>.matched(): Flow<Execution>`
- Casos: la misma secuencia que en `channels` da las mismas ejecuciones; la cancelación del colector detiene el
  motor; el estado no se comparte entre colecciones.

**39 🔴 *Replay* de una sesión a velocidad variable.** Reproducir la sesión de ticks a 1×, 10× o 100×, con pausa y
reanudación.
- Firma: `class SessionReplay(ticks: List<Tick>)` con `fun flow(speed: StateFlow<Double>, paused: StateFlow<Boolean>): Flow<Tick>`
- Casos: a 10×, una sesión de 60 s dura 6 s virtuales; pausar detiene la emisión y reanudar sigue donde iba; cambiar
  la velocidad a mitad cambia el ritmo desde ese punto.

**40 🔴 Un motor de reglas sobre el feed.** Reglas declarativas (`precio > x durante 5 s`, `cruce de medias`) que se
evalúan sobre el flujo.
- Firma: un pequeño DSL `rule("ACME alto") { price("ACME") above Money.of("101.00") during 5.seconds }` y
  `fun Flow<Tick>.evaluate(rules: List<Rule>): Flow<String>`
- Casos: cada regla con su secuencia de ticks y sus disparos exactos en tiempo virtual; reglas independientes no se
  afectan.

## ⚡ Saiyajin

**41 ⚡ Operadores propios con backpressure.** `windowedByTime`, `throttleLatest` y `batch(size, timeout)` escritos
con `flow { }` y `channelFlow { }`.
- Firma: `fun <T> Flow<T>.windowedByTime(window: Duration): Flow<List<T>>`, `fun <T> Flow<T>.throttleLatest(period: Duration): Flow<T>`
  y `fun <T> Flow<T>.batch(size: Int, timeout: Duration): Flow<List<T>>`
- Reglas: sin `GlobalScope`; el estado vive dentro de cada colección; la cancelación del colector detiene al
  productor; ningún buffer sin límite.
- **Se verifica con:** pruebas en tiempo virtual con las emisiones exactas en los instantes exactos, una prueba con un
  consumidor lento que demuestra que el productor se frena (buffer acotado), y la comparación con `sample` de la
  librería donde hacen lo mismo.
