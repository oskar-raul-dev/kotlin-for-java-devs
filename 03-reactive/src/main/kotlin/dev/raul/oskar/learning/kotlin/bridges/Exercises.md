# Ejercicios: puentes entre corrutinas, Reactor y RxJava

> **Lección:** `bridges` · **Dónde se resuelven:** `bridges/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../bridges/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 03-reactive test --tests '*.bridges.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los puentes están en
> `kotlinx-coroutines-reactor` (`awaitSingle`, `asFlux`, `mono { }`, `flux { }`, `ReactorContext`),
> `kotlinx-coroutines-reactive` (`asFlow`, `awaitFirst`, que vienen con el anterior) y `kotlinx-coroutines-rx3`. Las
> pruebas que cruzan a Reactor usan `runBlocking` o `StepVerifier` (los schedulers de Reactor no siguen el reloj virtual
> de `runTest`). El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 Llamar a Reactor desde una corrutina.** `awaitSingle` en vez de `block`.
- Firma: `suspend fun ReactivePriceClient.portfolio(holdings: Map<String, Long>): Money`
- Casos: el valor correcto; las llamadas en paralelo con `async` tardan lo que la más lenta (mídelo); explica en un
  comentario por qué `awaitSingle` no bloquea el hilo y `block()` sí.

**02 🟢 `awaitSingleOrNull`.** El vacío como `null`.
- Firma: `suspend fun ReactivePriceClient.priceOrDefault(ticker: String, default: Money): Money`
- Casos: existe → su precio; no existe → el valor por defecto; con `awaitSingle`, `NoSuchElementException` (medido en la
  lección).

**03 🟢 `awaitFirst` y `awaitLast`.** Del feed, el primero y el último.
- Firma: `suspend fun firstAndLast(feed: Flux<Tick>): Pair<Tick, Tick>`
- Casos: con `tickFeed`, los esperados; feed vacío → `NoSuchElementException`; `awaitFirstOrNull` → `null`.

**04 🟢 `asFlow`.** Un `Flux` en un pipeline de `Flow`.
- Firma: `fun ReactivePriceClient.acmeFlow(steps: Int): Flow<Money>` con `feed(steps).asFlow()` y operadores de `Flow`
- Casos: los precios de ACME en orden; `take(3)` cancela la suscripción de Reactor (cuéntalo con `doOnCancel`).

**05 🟢 🎤 `mono { }`.** Una función `suspend` ofrecida como `Mono`.
- Firma: `fun quoteAsMono(ticker: String, source: suspend (String) -> Money): Mono<Money>`
- Casos: `StepVerifier` recibe el valor; la corrutina corre recién al suscribirse (cuéntalo) y otra vez en cada
  suscripción; si la función lanza, el `Mono` falla. Explica en un comentario qué hace Spring WebFlux con un controlador
  `suspend`.

**06 🟢 `flux { }`.** Un productor con corrutinas.
- Firma: `fun pricesFlux(count: Int): Flux<Money>` con `send` y `delay`
- Casos: emite `count` y termina; la demanda de Reactor frena el `send` (con `StepVerifier.create(f, 0).thenRequest(2)`).

**07 🟢 `asFlux`.** Un `Flow` hacia Reactor.
- Firma: `fun Flow<Candle>.forReactorClients(): Flux<Candle>`
- Casos: los mismos elementos; un error del `Flow` llega como `onError`.

**08 🟢 `Single.await` y `rxSingle`.** Los puentes de RxJava.
- Firma: `suspend fun legacyQuote(c: LegacyQuoteClient, t: String): Money` y `fun newQuoteForRx(t: String): Single<Money>`
- Casos: ida y vuelta da el mismo valor; un error cruza el puente con su tipo.

**09 🟢 `Observable.asFlow`.** El feed de RxJava como `Flow`.
- Firma: `fun Observable<Tick>.cleanedFlow(): Flow<Tick>`
- Casos: los ticks limpios; cancelar el colector libera la suscripción.

**10 🟢 `Mono<Void>` desde una corrutina.** Efectos sin valor.
- Firma: `fun settleMono(settle: suspend () -> Unit): Mono<Void>` (con `mono { settle(); null }` y `then()`)
- Casos: completa sin valor; el efecto corrió una vez; explica en un comentario por qué hace falta el `null` (o
  `Mono<Unit>`) y qué hace Spring con `Unit`.

**11 🟢 `Flux` vacío y `Flow` vacío.** Los bordes.
- Firma: ninguna: pruebas con `Flux.empty<Tick>().asFlow()` y `emptyFlow<Tick>().asFlux()`
- Casos: los dos terminan sin elementos; `toList()` y `collectList()` dan listas vacías.

**12 🟢 El tipo de cada puente.** Una tabla en código.
- Firma: un objeto `BridgeCheatSheet` con un ejemplo de cada conversión (Mono→suspend, Flux→Flow, suspend→Mono,
  Flow→Flux, Single→suspend, Observable→Flow, suspend→Single, Flow→Observable)
- Casos: una prueba por fila.

## 🟡 Medios

**13 🟡 🎤 ¿Se conserva la cancelación al cruzar el puente?** En las cuatro direcciones.
- Firma: pruebas con contadores de cancelación en `Mono`→suspend, `Flux`→`Flow`, suspend→`Mono` y `Flow`→`Flux`
- Casos: en las cuatro, cancelar de un lado cancela el otro (la lección cubre dos; tú las cuatro). Explica en un
  comentario qué rompe la cadena (un `GlobalScope`, un `subscribe()` suelto).

**14 🟡 El puente roto.** `GlobalScope` dentro de un `mono`.
- Firma: `fun brokenMono(work: suspend () -> Money): Mono<Money>` que hace `Mono.create { sink -> GlobalScope.launch { sink.success(work()) } }`
- Casos: cancelar la suscripción no cancela la corrutina (cuéntalo: sigue corriendo); con `mono { }`, sí. Explica.

**15 🟡 El contexto de Reactor en una función `suspend`.** Un usuario autenticado.
- Firma: `suspend fun currentUser(): String?` (lee `coroutineContext[ReactorContext]?.context?.getOrDefault("user", null)`)
- Casos: llamada desde un `mono { }` con `contextWrite(Context.of("user", "ana"))` → `"ana"`; desde un `launch` aparte
  → `null`. Explica por qué así funciona `ReactiveSecurityContextHolder` de Spring con corrutinas.

**16 🟡 Contexto de la corrutina hacia Reactor.** `asCoroutineContext`.
- Firma: `suspend fun quoteWithTenant(tenant: String, client: (String) -> Mono<Money>): Money` (el cliente lee el
  tenant de su contexto)
- Casos: el tenant llega al cliente; dos llamadas concurrentes con tenants distintos no se mezclan.

**17 🟡 Backpressure de `Flux` a `Flow`.** El colector lento frena a Reactor.
- Firma: `suspend fun slowCollect(flux: Flux<Int>, delayMillis: Long, count: Int): Int` (cuántos pidió Reactor en total)
- Casos: con `doOnRequest` anota las demandas; el `asFlow()` pide de a lotes (anota el tamaño medido) y nunca más de lo
  que el colector procesa más ese lote.

**18 🟡 Backpressure de `Flow` a `Flux`.** Reactor frena al `Flow`.
- Firma: usa `countingFlowAsFlux` de la lección con distintas demandas
- Casos: con `limitRate(10)` y `take(25)`, el `Flow` produce 25 (medido en la lección); con `request(Long.MAX_VALUE)`,
  todo. Explica qué hace `limitRate`.

**19 🟡 `asFlow` sobre un `Flux` caliente.** No se vuelve frío.
- Firma: un `Sinks.many().multicast()` convertido a `Flow` y recolectado dos veces
- Casos: los dos colectores comparten la fuente (los elementos emitidos antes de suscribirse se pierden para el
  tardío); compáralo con un `Flux` frío, que se vuelve a correr para cada colector.

**20 🟡 🎤 `runBlocking` en un controlador WebFlux.** Por qué frena todo.
- Firma: un "controlador" de juguete que corre en `Schedulers.parallel()` y llama a `runBlocking { client.priceOf(…) }`
- Casos: con tantos pedidos concurrentes como hilos del scheduler, los demás esperan (mide la latencia); con `mono { }`
  no. Explica en un comentario qué pasa en un servidor Netty real.

**21 🟡 Errores a través del puente.** Que el tipo de excepción no cambie.
- Firma: pruebas de `IOException` y de una excepción de dominio cruzando en las cuatro direcciones
- Casos: llegan con su tipo y su mensaje; la `CancellationException` no se convierte en error del otro lado.

**22 🟡 `Mono.zip` frente a dos `async`.** La misma composición en los dos mundos.
- Firma: `fun quoteAndRateReactor(…): Mono<Pair<Money, BigDecimal>>` y `suspend fun quoteAndRateCoroutines(…): Pair<…>`
- Casos: mismo resultado y mismo tiempo; si uno falla, el otro se cancela en las dos (compruébalo).

**23 🟡 Un repositorio R2DBC simulado.** La capa de datos devuelve `Flux`, el servicio es `suspend`.
- Firma: `interface ReactiveLedger { fun entries(account: String): Flux<String>; fun save(e: String): Mono<Void> }` y
  `class LedgerService(repo: ReactiveLedger)` con funciones `suspend` y `Flow`
- Casos: el servicio no usa `block()` en ningún lado; probado con un repositorio en memoria. Se retoma en `04 · r2dbc`.

**24 🟡 `flux { }` con cierre.** Un feed que libera su recurso al cancelarse.
- Firma: `fun connectionFlux(log: MutableList<String>): Flux<Tick>` con `try/finally` en el `flux { }`
- Casos: `take(3)`, error y `dispose` cierran la conexión una sola vez (cuéntalo).

**25 🟡 Dispatchers en `mono { }`.** Dónde corre la corrutina.
- Firma: `fun threadOfMono(context: CoroutineContext): Mono<String>`
- Casos: sin contexto, en `Dispatchers.Default` (anota el nombre del hilo); con `Dispatchers.IO`, ahí; con
  `Schedulers.boundedElastic().asCoroutineDispatcher()`, en sus hilos.

**26 🟡 `Flow` → `Publisher` genérico.** Para una librería que pide un `Publisher` de Reactive Streams.
- Firma: `fun Flow<Tick>.toPublisher(): Publisher<Tick>` con `asPublisher()` de kotlinx-coroutines-reactive
- Casos: un suscriptor de Reactive Streams escrito a mano pide 3 y recibe 3; cancela y el `Flow` se detiene.

## 🟠 Difíciles

**27 🟠 🎤 Un servicio WebFlux en corrutinas.** Controlador, servicio y cliente, con el puente en las dos fronteras.
- Firma: un `QuoteHandler` (recibe y devuelve tipos de Reactor, como haría Spring), un `QuoteService` (`suspend` y
  `Flow`) y un `QuoteClient` (Reactor, como `WebClient`)
- Casos: la cancelación del "pedido HTTP" llega al cliente; el contexto (un id de correlación) llega del handler al
  cliente pasando por el servicio. Explica en un comentario cómo lo hace Spring en `09-spring`.

**28 🟠 Puentes y `Dispatchers.Unconfined`.** El orden de ejecución.
- Firma: pruebas de `mono(Dispatchers.Unconfined) { }` y del orden de los `doOnNext` respecto de la corrutina
- Casos: anota el orden exacto; explica por qué `Unconfined` puede cambiar el orden respecto de `Default`.

**29 🟠 Contexto que viaja por un `Flow`.** Del `Flux` de origen al colector.
- Firma: un `Flux` que lee su contexto con `deferContextual`, convertido con `asFlow()` y recolectado dentro de
  `withContext(Context.of(…).asCoroutineContext())`
- Casos: el contexto de la corrutina colectora llega a la fuente de Reactor (medido); explica el mecanismo.

**30 🟠 Un puente con *timeout* en los dos lados.** Quién corta primero.
- Firma: `suspend fun quoteWithTimeouts(client: (String) -> Mono<Money>, reactorTimeout: Duration, coroutineTimeout: Duration): Money`
- Casos: el más corto gana; el error que llega dice cuál fue; la suscripción queda cancelada en los dos casos.

**31 🟠 🎤 Reactor, corrutinas o los dos en un backend nuevo.** Un informe con código.
- Firma: el mismo endpoint de cotizaciones en Reactor puro, en corrutinas puras sobre Reactor (con puentes) y en
  corrutinas con un cliente `suspend`
- Casos: misma batería de pruebas; un comentario con líneas, legibilidad, manejo de errores y prueba de cada uno.

**32 🟠 RxJava ↔ Reactor sin corrutinas.** Los dos hablan Reactive Streams.
- Firma: `fun Flowable<Tick>.toFlux(): Flux<Tick> = Flux.from(this)` y la vuelta con `Flowable.fromPublisher`
- Casos: la demanda cruza (pedir 3 en Reactor pide 3 en RxJava); `Observable` necesita `toFlowable(estrategia)` antes
  (explica por qué).

**33 🟠 El libro de órdenes con API reactiva.** El actor de `02 · channels` ofrecido como `Flux<Execution>` y
`Mono<BookSnapshot>`.
- Firma: un adaptador sobre un actor propio (en tu práctica) con `fun executions(): Flux<Execution>` y
  `fun snapshot(): Mono<BookSnapshot>`
- Casos: varios suscriptores de ejecuciones reciben todas (un `SharedFlow` por detrás con `asFlux`); el snapshot es
  consistente.

**34 🟠 Pruebas de puentes en tiempo virtual.** Hasta dónde llega.
- Firma: un `flux { delay(…) }` probado con `StepVerifier.withVirtualTime` y con `runTest`
- Casos: anota qué funciona y qué no (el `delay` de la corrutina no sigue el `VirtualTimeScheduler` de Reactor salvo
  que la corrutina corra en un dispatcher hecho de ese scheduler: pruébalo con `asCoroutineDispatcher()`).

**35 🟠 Medir el costo del puente.** Un millón de elementos de `Flux` a `Flow` y al revés.
- Firma: `fun bridgeCost(items: Int): Map<String, Duration>`
- Casos: tabla con calentamiento (directo en Reactor, directo en `Flow`, cruzando una vez, cruzando dos); explica de
  dónde sale el costo (canales y demanda por lotes).

**36 🟠 Migrar un cliente Reactor a `suspend` sin romper a nadie.** Dos APIs al mismo tiempo.
- Firma: `class DualQuoteClient` con `fun quote(t: String): Mono<Money>` (para los viejos) y `suspend fun quoteSuspend(t: String): Money`
  (para los nuevos), una implementada sobre la otra
- Casos: las dos pasan la misma batería; decide cuál es la implementación real y explica por qué.

## 🔴 Muy difíciles

**37 🔴 Un `asFlow` propio.** Un `Publisher` convertido a `Flow` respetando la demanda.
- Firma: `fun <T : Any> Publisher<T>.myAsFlow(batch: Long): Flow<T>` (con un `Channel` y `request(batch)` cuando se
  vacía la mitad)
- Casos: la demanda nunca pasa de `batch` adelantados; la cancelación del colector cancela la `Subscription`; un error
  del publicador llega al colector.

**38 🔴 Un `mono { }` propio.** Una corrutina como `Mono` con cancelación y contexto.
- Firma: `fun <T : Any> myMono(block: suspend () -> T?): Mono<T>` con `Mono.create` y un `Job` por suscripción
- Casos: cancelar la suscripción cancela la corrutina; el `ReactorContext` llega; vacío si devuelve `null`.

**39 🔴 Un gateway de precios híbrido.** Fuentes en Reactor, RxJava y corrutinas mezcladas en un solo `Flow`.
- Firma: libre: tres fuentes, una de cada mundo, fusionadas con prioridad y deduplicadas
- Casos: la salida es la esperada con tiempos conocidos; cancelar el colector cancela las tres; un error en una se
  aísla (las otras siguen).

**40 🔴 El contexto de seguridad de punta a punta.** Simular lo que hace Spring Security con corrutinas.
- Firma: un filtro (Reactor) que pone un usuario en el contexto, un handler `suspend` que lo lee y un cliente (Reactor)
  que lo necesita para firmar el pedido
- Casos: el usuario llega al cliente; un `launch` en otro scope lo pierde (demuéstralo) y la solución lo arrastra con
  `coroutineContext[ReactorContext]`.

## ⚡ Saiyajin

**41 ⚡ Puente con cancelación y contexto.** Un adaptador `Flow` ↔ `Flux` para el feed, con un id de correlación en el
contexto.
- Firma: `class FeedBridge` con `fun toFlux(feed: Flow<Tick>): Flux<Tick>` y `fun toFlow(feed: Flux<Tick>): Flow<Tick>`,
  más una función `suspend` que lee el id de correlación de cualquiera de los dos lados
- Reglas: sin `GlobalScope`, sin `block()`, sin buffers sin límite; el id viaja en el contexto, no como parámetro.
- **Se verifica con:** pruebas de que cancelar del lado `Flow` cancela la suscripción de Reactor (y al revés), de que
  el id de correlación llega a los dos lados, y de que la backpressure frena al productor.
