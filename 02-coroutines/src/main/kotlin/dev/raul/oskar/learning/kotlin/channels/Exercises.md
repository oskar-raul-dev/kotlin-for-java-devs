# Ejercicios: canales, `select` y actores

> **Lección:** `channels` · **Dónde se resuelven:** `channels/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../channels/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.channels.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `runTest`
> (tiempo virtual) salvo que el enunciado pida hilos de verdad; `produce` y `onTimeout` piden
> `@OptIn(ExperimentalCoroutinesApi::class)` en 1.11.0. Los ticks salen de `tickFeed` y las órdenes son `LimitOrder`
> de la lección. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 07, 13, 18, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 `Channel` frente a `BlockingQueue`.** El mismo productor-consumidor con los dos.
- Firma: `fun withBlockingQueue(items: List<Int>): List<Int>` (dos hilos) y `suspend fun withChannel(items: List<Int>): List<Int>`
- Casos: los dos entregan todo en orden; mide cuántos hilos usa cada uno (`Thread.activeCount` o nombres). Explica
  en un comentario qué cambia cuando `put` bloquea y cuando `send` suspende.

**02 🟢 Cerrar para terminar.** Un consumidor con `for` que termina solo.
- Firma: `suspend fun consumeAll(prices: List<Money>): Money` (suma lo recibido)
- Casos: el productor cierra y el `for` termina; si el productor no cierra, la prueba lo detecta con
  `withTimeoutOrNull` (devuelve `null`); explica en un comentario qué es el *poison pill* y por qué aquí no hace falta.

**03 🟢 `produce`.** Un productor de ticks con `produce`.
- Firma: `fun CoroutineScope.ticksOf(ticker: String, ticks: List<Tick>): ReceiveChannel<Tick>`
- Casos: solo emite los del ticker; el canal se cierra solo; si el bloque falla, el consumidor recibe la excepción en
  el `receive`.

**04 🟢 Capacidades.** Qué hace `send` con cada tipo de canal.
- Firma: `suspend fun sendsBeforeBlock(capacity: Int): Int`
- Casos: `RENDEZVOUS` → 0; `Channel(5)` → 5; `BUFFERED` → 64 (el valor por defecto, medido); `UNLIMITED` → todos;
  `CONFLATED` → todos, pero el consumidor solo ve el último.

**05 🟢 `trySend` y `tryReceive`.** Operaciones que no suspenden.
- Firma: `fun offerAll(channel: Channel<Int>, items: List<Int>): List<Int>` (los que no entraron)
- Casos: canal de 2 y 5 ítems → 3 rechazados; `trySend` sobre un canal cerrado da `isClosed`; `tryReceive` sobre
  uno vacío da `isFailure` sin lanzar.

**06 🟢 `receiveCatching`.** Recibir sin excepción cuando el canal se cierra.
- Firma: `suspend fun receiveAllSafely(channel: ReceiveChannel<String>): Pair<List<String>, Throwable?>`
- Casos: cerrado normalmente → lista y `null`; cerrado con causa (`close(IOException("feed"))`) → lista y la causa.

**07 🟢 🎤 *Fan-out*: se reparten.** Tres trabajadores sobre un canal.
- Firma: `suspend fun distribute(ticks: List<Tick>, workers: Int): Map<Int, Int>` (cuántos procesó cada uno)
- Casos: la suma es el total; ningún tick se procesa dos veces; explica en un comentario por qué para que **todos**
  reciban **todo** hace falta otra cosa (`SharedFlow`).

**08 🟢 *Fan-in*.** Cinco feeds de tickers en un solo canal.
- Firma: `suspend fun mergeFeeds(feeds: Map<String, List<Tick>>): List<Tick>`
- Casos: llegan todos; el canal se cierra cuando terminan los cinco productores (no antes); el orden respeta los
  tiempos de cada feed.

**09 🟢 Canal de ejecuciones.** Del libro a la liquidación.
- Firma: `suspend fun settle(executions: ReceiveChannel<Execution>): Money` (suma de precio × cantidad)
- Casos: tres ejecuciones; canal vacío y cerrado → cero; los precios en centavos se convierten bien.

**10 🟢 `CONFLATED`.** El último precio y nada más.
- Firma: `suspend fun lastPriceSeen(prices: List<Money>, consumerDelay: Duration): List<Money>`
- Casos: con un consumidor lento, recibe el primero y luego solo los últimos; nunca suspende el productor.

**11 🟢 `consumeEach` y la cancelación.** Consumir y cancelar el productor al salir.
- Firma: `suspend fun firstAbove(channel: ReceiveChannel<Money>, limit: Money): Money?` con `consume { }`
- Casos: devuelve el primero mayor al límite y el canal queda cancelado (el productor recibe la cancelación); si
  ninguno pasa, `null`.

**12 🟢 *Pipeline* de tres etapas.** Ticks → precios en dólares → redondeados.
- Firma: `fun CoroutineScope.stage1(ticks: List<Tick>): ReceiveChannel<Tick>`, `stage2`, `stage3`
- Casos: el resultado final es el esperado; cancelar el scope cancela las tres etapas (ninguna queda viva).

## 🟡 Medios

**13 🟡 🎤 `select` con dos fuentes.** La cotización principal o la de respaldo, la que llegue primero.
- Firma: `suspend fun quoteFromEither(primary: ReceiveChannel<Money>, backup: ReceiveChannel<Money>): Pair<String, Money>`
- Casos: gana la más rápida; si las dos están listas, gana la principal (sesgo de `select`); con `selectUnbiased`, en
  1 000 corridas cada una gana alguna vez. Explica en un comentario qué hace `select`.

**14 🟡 `select` para enviar.** Mandar al primer trabajador libre.
- Firma: `suspend fun dispatchToFree(order: LimitOrder, workers: List<SendChannel<LimitOrder>>): Int` con `onSend`
- Casos: si el trabajador 0 está ocupado y el 1 libre, va al 1; si todos están ocupados, espera al primero que se
  libere.

**15 🟡 Tiempo límite con `select`.** `onTimeout` frente a `withTimeoutOrNull`.
- Firma: `suspend fun receiveOrDefault(channel: ReceiveChannel<Money>, limit: Duration, default: Money): Money` en las dos
  versiones
- Casos: las dos dan lo mismo en tiempo virtual; la de `onTimeout` pide opt-in experimental (medido en 1.11.0).

**16 🟡 Un *ticker* sin `ticker`.** Una señal cada segundo con `Flow` o `produce`.
- Firma: `fun CoroutineScope.everySecond(): ReceiveChannel<Long>`
- Casos: en 5 s virtuales, 5 señales; explica en un comentario por qué `ticker()` es `@ObsoleteCoroutinesApi` en 1.11.0
  (medido) y qué pasa con un consumidor lento en cada versión.

**17 🟡 Lotes desde un canal.** Agrupar órdenes de a N o cada T, lo que pase primero.
- Firma: `fun CoroutineScope.batches(source: ReceiveChannel<LimitOrder>, size: Int, every: Duration): ReceiveChannel<List<LimitOrder>>`
- Casos: 10 órdenes rápidas y tamaño 4 → lotes 4, 4, 2; órdenes lentas → lotes por tiempo; el último lote sale al
  cerrarse la fuente.

**18 🟡 🎤 Un actor de saldos.** El estado de las cuentas detrás de un canal de comandos.
- Firma: `class BalanceActor(scope: CoroutineScope)` con `suspend fun deposit(account: String, amount: Money)`,
  `suspend fun balance(account: String): Money` y `fun close()`
- Casos: 100 corrutinas en `Dispatchers.Default` depositan 1 000 veces cada una y el saldo es exacto; `balance` responde
  con `CompletableDeferred`. Explica en un comentario por qué el actor no necesita locks.

**19 🟡 Pedido-respuesta con canales.** Un servicio de tasas que responde por un canal de respuesta.
- Firma: `data class RateRequest(val pair: String, val reply: SendChannel<BigDecimal>)` y
  `fun CoroutineScope.rateServer(requests: ReceiveChannel<RateRequest>): Job`
- Casos: dos pedidos concurrentes reciben cada uno su respuesta; un par desconocido cierra el canal de respuesta con
  una excepción.

**20 🟡 Cancelar un libro.** El comando `Cancel` del actor de la lección, de punta a punta.
- Firma: usa `MatchingEngine`
- Casos: cancelar una orden parcialmente ejecutada saca solo lo que quedaba; cancelar una que ya no existe devuelve
  `false`; un `Place` y un `Cancel` enviados uno tras otro se procesan en ese orden.

**21 🟡 Descarte con `onUndeliveredElement`.** Liberar lo que nunca se entregó.
- Firma: `fun reservations(log: MutableList<String>): Channel<String>` con `Channel(2, onUndeliveredElement = { … })`
- Casos: al cancelar el canal con elementos dentro, cada uno pasa por el callback (se registra "liberada"); los
  entregados no.

**22 🟡 `BufferOverflow` en canales.** `DROP_OLDEST` y `DROP_LATEST`.
- Firma: `fun boundedFeed(capacity: Int, overflow: BufferOverflow): Channel<Int>`
- Casos: con capacidad 3 y 10 envíos sin consumidor, cada política deja los elementos esperados (los últimos o los
  primeros); `send` nunca suspende con ninguna de las dos.

**23 🟡 Detectar un `send` colgado.** El error del canal *rendezvous* sin receptor.
- Firma: `suspend fun sendWithWatchdog(channel: SendChannel<Int>, value: Int, limit: Duration): Boolean`
- Casos: con receptor → `true`; sin receptor → `false` a tiempo (el `send` se cancela); explica en un comentario cómo
  se diagnostica en producción (volcado de corrutinas con `kotlinx-coroutines-debug`).

**24 🟡 Trabajadores con resultados ordenados.** Procesar en paralelo y devolver en el orden original.
- Firma: `suspend fun <T, R> processOrdered(items: List<T>, workers: Int, f: suspend (T) -> R): List<R>`
- Casos: con latencias distintas, el resultado respeta el orden de entrada; con un trabajador, igual a un `map`.

**25 🟡 Libro de varios tickers.** Un actor por ticker y un enrutador.
- Firma: `class MultiBook(scope: CoroutineScope, tickers: List<String>)` con `suspend fun place(ticker: String, order: LimitOrder)`
- Casos: órdenes de ACME no tocan el libro de ORBX; las ejecuciones salen por un solo canal con el ticker; cerrar el
  enrutador cierra los cinco actores.

**26 🟡 Mezcla de dos feeds con prioridad.** Las órdenes de cancelación primero.
- Firma: `suspend fun prioritized(cancels: ReceiveChannel<String>, orders: ReceiveChannel<LimitOrder>): List<String>`
- Casos: si hay cancelaciones pendientes, siempre salen antes que las órdenes (orden de los `onReceive` en el
  `select`); cuando se cierran las dos, termina.

## 🟠 Difíciles

**27 🟠 🎤 ¿Qué es un actor?** El actor de saldos frente a la versión con `Mutex` y la versión con `synchronized`.
- Firma: tres clases con la misma interfaz `BalanceStore`
- Casos: las tres dan el mismo resultado con 100 corrutinas concurrentes; mide el tiempo de cada una con
  calentamiento y anótalo en una tabla en un comentario. Explica qué gana el actor (sin locks, orden de llegada) y qué
  pierde (latencia, una corrutina dueña).

**28 🟠 Productor con backpressure.** Un productor rápido y un consumidor lento sin perder nada ni agotar la memoria.
- Firma: `suspend fun ingest(ticks: List<Tick>, capacity: Int, consumerDelay: Duration): Int` (máximo de elementos en
  el canal)
- Casos: el máximo nunca pasa de la capacidad; el productor se frena (sus tiempos de envío lo muestran); con
  `UNLIMITED`, el máximo es todo el feed. Explica la diferencia.

**29 🟠 *Rate limiter* con un canal.** Un *token bucket* de N permisos por segundo.
- Firma: `class TokenBucket(scope: CoroutineScope, perSecond: Int, burst: Int)` con `suspend fun acquire()`
- Casos: 10 pedidos con 2 por segundo y ráfaga 2 → los dos primeros al instante y luego uno cada 500 ms (tiempo
  virtual exacto); los permisos no se acumulan más allá de la ráfaga.

**30 🟠 Supervisor de trabajadores.** Un *pool* de trabajadores que reinicia al que falla.
- Firma: `class WorkerPool(scope: CoroutineScope, size: Int, work: suspend (LimitOrder) -> Unit)` con `suspend fun submit(order: LimitOrder)`
- Casos: un trabajador que falla con una orden se reemplaza y los demás siguen; la orden que falló va a un canal de
  "cartas muertas"; el pool nunca baja de N trabajadores.

**31 🟠 🎤 `select` a fondo.** Un *multiplexor* de tres feeds con tiempo límite total y cierre.
- Firma: `suspend fun multiplex(feeds: List<ReceiveChannel<Tick>>, total: Duration): List<Tick>`
- Casos: recibe de todos hasta que se cierran o vence el tiempo; un feed cerrado se deja de escuchar (sin girar en
  vacío); explica en un comentario cómo funciona `select` por dentro (cláusulas registradas y la primera que se
  completa gana).

**32 🟠 Emparejamiento con órdenes de mercado.** Agregar órdenes sin precio al libro de la lección.
- Firma: `data class MarketOrder(val id: String, val side: Side, val quantity: Long)` y
  `fun MatchingBook.placeMarket(order: MarketOrder): List<Execution>` (escríbelo en tu propio libro)
- Casos: consume niveles hasta llenar; si no alcanza, lo que falta se cancela (no queda en el libro); libro vacío →
  ninguna ejecución.

**33 🟠 Snapshot consistente.** Una foto del libro mientras llegan órdenes.
- Firma: `suspend fun MatchingEngine.snapshotEvery(every: Duration, count: Int): List<BookSnapshot>`
- Casos: con 100 corrutinas enviando órdenes, ninguna foto está cruzada y la cantidad total de cada foto cuadra con
  las órdenes procesadas hasta ese momento (lleva un contador en el actor).

**34 🟠 Canal con prioridad.** Un `SendChannel` que entrega primero lo de mayor prioridad.
- Firma: `class PriorityChannel<T>(scope: CoroutineScope, comparator: Comparator<T>)` con `send`, `receive` y `close`
- Casos: si se envían 5 y luego se recibe, salen ordenados; un receptor esperando recibe el primero que llega;
  cerrar entrega lo pendiente y luego termina.

**35 🟠 Pipeline con errores por elemento.** Que un tick malo no tumbe la etapa.
- Firma: `fun CoroutineScope.safeStage(input: ReceiveChannel<String>, parse: (String) -> Tick, errors: SendChannel<String>): ReceiveChannel<Tick>`
- Casos: los buenos pasan y los malos van a `errors`; una falla del propio canal (no del elemento) sí cierra la etapa
  con la causa.

**36 🟠 Medir canales.** Throughput de `RENDEZVOUS`, `BUFFERED` y `UNLIMITED` con un millón de enteros.
- Firma: `fun channelThroughput(capacity: Int, items: Int): Duration` (hilos de verdad, `Dispatchers.Default`)
- Casos: tabla de tiempos con calentamiento; explica en un comentario por qué el *rendezvous* es el más lento.

## 🔴 Muy difíciles

**37 🔴 Un `Channel` propio.** Un canal acotado con `send`/`receive` que suspenden, escrito con
`suspendCancellableCoroutine` y una cola.
- Firma: `class TinyChannel<T>(capacity: Int)` con `suspend fun send(x: T)`, `suspend fun receive(): T` y `fun close()`
- Casos: pasa las pruebas de capacidad y cierre de la lección; la cancelación de un `send` suspendido lo saca de la
  cola de espera; 100 productores y 100 consumidores no pierden ni duplican.

**38 🔴 Libro de órdenes con *stop orders*.** Órdenes que se activan cuando el precio de la última ejecución cruza un
nivel.
- Firma: amplía tu motor con `data class StopOrder(val id: String, val side: Side, val trigger: Long, val quantity: Long)`
- Casos: una ejecución que cruza el disparo activa la orden como de mercado; varias activadas a la vez se procesan
  en orden de llegada; una activación que provoca otra (cascada) termina.

**39 🔴 Simulación de mercado con agentes.** Diez agentes (corrutinas) con estrategias distintas que mandan órdenes
al motor durante una sesión simulada.
- Firma: libre; los agentes reciben las ejecuciones por un canal de difusión que tú escribes
- Casos: con semilla fija, la sesión da siempre las mismas ejecuciones; la cantidad total se conserva; el libro nunca
  queda cruzado; tiempo virtual de una sesión de 6.5 h en menos de 1 s real.

**40 🔴 CSP: el problema de los filósofos.** Cinco filósofos y cinco tenedores como actores, sin *deadlock*.
- Firma: libre (cada tenedor es un actor con `Acquire`/`Release`)
- Casos: 1 000 comidas en total sin bloqueo (la prueba termina dentro de un `withTimeout`); ningún filósofo pasa
  hambre (cada uno come al menos 150 veces); explica en un comentario qué estrategia evita el deadlock.

## ⚡ Saiyajin

**41 ⚡ Motor de emparejamiento como actor.** El libro de órdenes de `collections` detrás de un canal de comandos,
con precio-tiempo y ejecuciones parciales.
- Firma: `class OrderBookActor(scope: CoroutineScope)` con `suspend fun place(order: LimitOrder)`,
  `suspend fun cancel(id: String): Boolean`, `suspend fun snapshot(): BookSnapshot`, `val executions: ReceiveChannel<Execution>`
  y `fun close()`
- Reglas: una sola corrutina dueña del libro; ningún lock; las respuestas por `CompletableDeferred`; cerrar procesa lo
  pendiente y cierra el canal de ejecuciones.
- **Se verifica con:** la misma secuencia de órdenes que el ⚡ de `control` da las mismas ejecuciones, y una prueba
  con 100 corrutinas que envían órdenes a la vez donde el libro nunca queda cruzado y la cantidad total se conserva.
