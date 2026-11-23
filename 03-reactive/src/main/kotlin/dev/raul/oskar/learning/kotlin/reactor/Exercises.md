# Ejercicios: Reactor desde Kotlin

> **Lección:** `reactor` · **Dónde se resuelven:** `reactor/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../reactor/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 03-reactive test --tests '*.reactor.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `StepVerifier`
> (o su extensión de Kotlin, `flux.test()`) y, para los tiempos, `StepVerifier.withVirtualTime`. Los ticks salen de
> `tickFeed` y `withFlaws` de `cases`; las velas de referencia, de `candlesReference`. Reactor 3.9 trae anotaciones
> JSpecify: Kotlin ve sus `null` en compilación. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 *Lazy*.** Un `Mono` que no se suscribe no hace nada.
- Firma: `fun recordTrade(log: MutableList<String>): Mono<Void>` (anota `"registrado"` al ejecutarse)
- Casos: llamar a la función no anota nada; suscribirse anota una vez; suscribirse dos veces, dos. Explica en un
  comentario qué significa que Reactor sea *lazy* y el error clásico de portar código imperativo (llamar y no devolver
  el `Mono`).

**02 🟢 `toMono` y `toFlux`.** Las extensiones de `reactor-kotlin-extensions`.
- Firma: `fun pricesOf(tickers: List<String>): Flux<Money>` con `toFlux()` y `fun firstPrice(…): Mono<Money>` con `toMono()`
- Casos: los valores esperados con `test().expectNext(…).verifyComplete()`; lista vacía → `Flux` vacío.

**03 🟢 `map` y `filter`.** Los precios de ACME por encima de 100.
- Firma: `fun Flux<Tick>.acmeAbove(limit: Money): Flux<Money>`
- Casos: con `tickFeed`, solo ACME y solo los mayores; `take(3)` cancela la fuente (cuéntalo con `doOnCancel`).

**04 🟢 Vacío frente a `null`.** El precio de un ticker que puede no existir.
- Firma: `fun priceOf(ticker: String, prices: Map<String, Money>): Mono<Money>` con `Mono.justOrEmpty`
- Casos: existe → valor; no existe → vacío (`verifyComplete` sin elementos); `defaultIfEmpty` da un valor por omisión.
  Anota en un comentario el error de compilación al escribir `Mono.just(prices[ticker])` (medido con Reactor 3.9).

**05 🟢 🎤 `map` frente a `flatMap`.** Un precio por ticker.
- Firma: `fun quotes(tickers: Flux<String>, quote: (String) -> Mono<Money>): Flux<Money>`
- Casos: con `map` el resultado es `Flux<Mono<Money>>` (no compila contra la firma: explica); con `flatMap`, los
  valores; con `concatMap`, en orden. Explica en un comentario la diferencia.

**06 🟢 `StepVerifier` básico.** Valores, conteo y error.
- Firma: pruebas sobre `Flux.just(…)` y `Flux.error(…)`
- Casos: `expectNext`, `expectNextCount`, `expectError(IOException::class.java)`, `verifyComplete`; explica qué hace
  `verify()` (suscribe y espera).

**07 🟢 `doOnNext`, `doOnError`, `doFinally`.** Efectos para registrar sin cambiar el flujo.
- Firma: `fun Flux<Tick>.logged(log: MutableList<String>): Flux<Tick>`
- Casos: el log tiene cada tick, el error si lo hay y `"fin: ON_COMPLETE"` (o `CANCEL`) al final.

**08 🟢 `onErrorReturn` y `onErrorResume`.** Recuperarse de una fuente caída.
- Firma: `fun withFallback(primary: Mono<Money>, backup: Mono<Money>): Mono<Money>`
- Casos: la principal anda → su valor; falla → el respaldo; fallan las dos → el error del respaldo.

**09 🟢 `zip`.** Precio y tasa juntos.
- Firma: `fun priceInEur(price: Mono<Money>, rate: Mono<BigDecimal>): Mono<Money>`
- Casos: los dos llegan → convierte; uno vacío → vacío (explica por qué); uno falla → falla.

**10 🟢 `Mono<Void>` y `then`.** Encadenar efectos.
- Firma: `fun settleThenNotify(settle: Mono<Void>, notify: Mono<Void>): Mono<Void>`
- Casos: `notify` corre solo si `settle` termina bien; si `settle` falla, `notify` no corre. Explica por qué no conviene
  `Mono<Unit>` para esto.

**11 🟢 `reduce` y `collectList`.** El valor de un portafolio.
- Firma: `fun portfolioValue(positions: Flux<Pair<Money, Long>>): Mono<Money>`
- Casos: suma correcta; vacío → cero (`reduce` con semilla) o vacío (sin semilla): muestra las dos.

**12 🟢 `take`, `skip`, `distinctUntilChanged`.** Los primeros cambios de precio.
- Firma: `fun Flux<Money>.firstChanges(n: Long): Flux<Money>`
- Casos: `100, 100, 101, 101, 102` con n = 2 → `100, 101`.

## 🟡 Medios

**13 🟡 🎤 Suscribir en el lugar equivocado.** `subscribe()` dentro de un `map`.
- Firma: una versión rota (`flux.map { save(it).subscribe(); it }`) y la correcta (`flatMap { save(it).thenReturn(it) }`)
- Casos: la rota pierde errores y no espera a que se guarde (demuéstralo con un `save` lento que falla); la correcta
  propaga el error. Explica en un comentario por qué un `subscribe()` en medio de una cadena es casi siempre un error.

**14 🟡 `publishOn` y `subscribeOn`.** Dónde corre cada parte.
- Firma: `fun threads(): List<String>` (el hilo de la fuente, de un `map` después de `publishOn` y del suscriptor)
- Casos: `subscribeOn(Schedulers.boundedElastic())` mueve la fuente; `publishOn(Schedulers.parallel())` mueve lo que
  sigue; compáralo en un comentario con `flowOn` de `02 · flow`.

**15 🟡 Llamadas bloqueantes en Reactor.** JDBC simulado sin bloquear el hilo de eventos.
- Firma: `fun balance(account: String): Mono<Money>` con `Mono.fromCallable { Thread.sleep(…) }.subscribeOn(Schedulers.boundedElastic())`
- Casos: 50 llamadas en paralelo con `flatMap` terminan en paralelo; sin `subscribeOn`, en serie (mídelo); explica qué
  es `boundedElastic` y por qué es el `Dispatchers.IO` de Reactor.

**16 🟡 `flatMap` con concurrencia.** No abrir más de N consultas a la vez.
- Firma: usa `quoteAll` de la lección
- Casos: con 20 tickers y concurrencia 3, el pico es 3; con `flatMapSequential`, el orden de salida es el de entrada
  aunque terminen desordenados.

**17 🟡 Reintentos con `retryWhen`.** Espera exponencial con `Retry.backoff`.
- Firma: `fun Mono<Money>.resilient(): Mono<Money>`
- Casos: una fuente que falla dos veces y luego anda: con `withVirtualTime`, los reintentos ocurren en los instantes
  esperados; al agotar los intentos, `Exceptions.isRetryExhausted` es verdadero y la causa es la original.

**18 🟡 `timeout`.** Una cotización que tarda demasiado.
- Firma: `fun Mono<Money>.within(limit: Duration, fallback: Money): Mono<Money>`
- Casos: en tiempo virtual, llega a tiempo → su valor; no llega → el respaldo en el instante exacto.

**19 🟡 Velas con `window` por tiempo.** Otra forma de armarlas.
- Firma: `fun Flux<Tick>.candlesByWindow(): Flux<Candle>` con `window(Duration.ofMinutes(1))` sobre `timedFeed`
- Casos: con `withVirtualTime`, las mismas velas que la referencia para un ticker; explica la diferencia entre ventanas
  por tiempo de reloj (`window(Duration)`) y por tiempo del dato (el `atMillis` del tick, como hace `candles`).

**20 🟡 🎤 ¿Por qué Reactor no admite `null`?** Y por dónde se cuela igual.
- Firma: `fun configuredUrl(): Mono<String>` que lee `System.getProperty("fx.url")` (un platform type de Java)
- Casos: si la propiedad no existe, `Mono.just(System.getProperty(…))` compila y falla en ejecución con
  `NullPointerException` (medido: el platform type esquiva la anotación); con `Mono.justOrEmpty`, vacío. Explica en un
  comentario la regla de Reactive Streams (§2.13) y qué cambió con JSpecify.

**21 🟡 Estado por suscripción.** Un contador de ticks que no se comparta.
- Firma: dos versiones de `fun Flux<Tick>.numbered(): Flux<Pair<Int, Tick>>`: con el contador afuera y con `Flux.defer`
- Casos: dos suscripciones a la versión rota continúan la numeración de la otra; la de `defer` empieza en 1 cada vez.

**22 🟡 `Sinks`.** Publicar alertas a varios suscriptores.
- Firma: `class AlertHub` con `Sinks.many().multicast().onBackpressureBuffer<String>()` y `fun publish(a: String)`
- Casos: dos suscriptores reciben las mismas alertas; uno que llega tarde no ve las anteriores; `tryEmitNext` devuelve
  un resultado que hay que mirar (como `trySend`). Compáralo con `SharedFlow`.

**23 🟡 `cache` y `share`.** Una tasa cara compartida.
- Firma: `fun sharedRate(fetch: Mono<BigDecimal>, ttl: Duration): Mono<BigDecimal>` con `cache(ttl)`
- Casos: tres suscriptores dentro del TTL → una sola llamada; vencido → otra; en tiempo virtual.

**24 🟡 El contexto de Reactor.** Un id de correlación con `contextWrite` y `deferContextual`.
- Firma: `fun quoteWithCorrelation(ticker: String): Mono<String>` (el id aparece en el resultado)
- Casos: `contextWrite(Context.of("correlationId", "req-1"))` llega a la fuente; el contexto va **de abajo hacia arriba**
  (un `contextWrite` antes del `deferContextual` no lo ve: demuéstralo).

**25 🟡 `groupBy` sin límites.** El peligro con muchos grupos.
- Firma: `fun Flux<Tick>.lastPricePerTicker(): Flux<Pair<String, Money>>`
- Casos: con cinco tickers, anda; explica en un comentario qué pasa con `groupBy` cuando hay más grupos que la
  concurrencia de `flatMap` (se cuelga) y cómo evitarlo.

**26 🟡 `Flux.interval` y `take`.** Un reloj del mercado.
- Firma: `fun marketClock(): Flux<Long>` (un tick por segundo, solo 5)
- Casos: con `withVirtualTime`, 5 valores en 5 s exactos; sin `take`, nunca termina (la prueba usa `thenCancel`).

## 🟠 Difíciles

**27 🟠 🎤 El pipeline del feed, completo.** Limpieza, velas y cruces encadenados, con su prueba de equivalencia.
- Firma: `fun feedReport(ticks: Flux<Tick>): Mono<Report>` (velas por ticker y cruces de ACME)
- Casos: con `withFlaws(tickFeed(steps = 300))`, las velas son iguales a `candlesReference`; los cruces, los mismos que
  calcula una función con colecciones. Explica en un comentario qué operador de Reactor reemplaza a cada operador de
  `Flow` de `02 · flow`.

**28 🟠 Operador propio con `transform`.** Un `throttleLatest` reutilizable.
- Firma: `fun <T> Flux<T>.throttleLatest(period: Duration): Flux<T>` con `transform` y operadores existentes
  (`sample`, `window` o `switchMap`)
- Casos: en tiempo virtual, emisiones exactas; compáralo con el ⚡ de `02 · flow`.

**29 🟠 `handle` con estado.** Detectar huecos en el feed.
- Firma: `fun Flux<Tick>.gaps(max: Long): Flux<String>` con `Flux.defer` y `handle`
- Casos: un hueco de 30 s en LUMA se detecta; los ticks normales no emiten nada.

**30 🟠 Fusionar con prioridad.** Feed principal y de respaldo.
- Firma: `fun preferPrimary(primary: Flux<Tick>, backup: Flux<Tick>, silence: Duration): Flux<Tick>` (si la principal
  calla, se usa el respaldo hasta que vuelva)
- Casos: con `withVirtualTime` y huecos conocidos, la salida exacta; ningún tick repetido.

**31 🟠 🎤 `block()` en el lugar equivocado.** Encontrar y quitar los `block()` de un servicio.
- Firma: un servicio con tres `block()` escondidos (dado por ti) y su versión sin bloqueos
- Casos: corrido en `Schedulers.parallel()`, el original lanza `IllegalStateException` (medido); el nuevo pasa.
  Explica qué pasa si un `block()` corre en un hilo de Netty en WebFlux.

**32 🟠 Paginación reactiva.** Leer el ledger de a páginas con `expand`.
- Firma: `fun allEntries(page: (Int) -> Mono<List<String>>): Flux<String>`
- Casos: tres páginas y una vacía → todos los asientos en orden; solo se pide la página siguiente cuando hace falta
  (cuéntalo con `take`).

**33 🟠 `Flux.create` con un cliente de callbacks.** El adaptador de Reactor (el `callbackFlow` de Reactor).
- Firma: `fun PriceListenerClient.prices(ticker: String): Flux<String>` con `Flux.create` y `sink.onDispose`
- Casos: los precios llegan; cancelar desregistra; el error del cliente termina el flujo. Compáralo con `callbackFlow`.

**34 🟠 Pruebas con `TestPublisher`.** Controlar la fuente desde la prueba.
- Firma: pruebas de `cleaned` con `TestPublisher.create<Tick>()`
- Casos: emitir, completar y fallar a mano; comprobar que `cleaned` cancela la fuente cuando el suscriptor cancela.

**35 🟠 Medir `flatMap` frente a `concatMap`.** Throughput y orden.
- Firma: `fun throughput(operator: String, items: Int, latency: Duration): Duration`
- Casos: tabla con calentamiento en un comentario; explica cuándo el orden importa (ejecuciones) y cuándo no (precios).

**36 🟠 Reactor y `Flow` lado a lado.** El mismo pipeline en las dos APIs.
- Firma: el pipeline de 27 en Reactor y en `Flow` (en tu práctica; `03-reactive` tiene kotlinx-coroutines)
- Casos: mismo resultado; cuenta líneas y operadores; anota en un comentario qué fue más fácil en cada uno.

## 🔴 Muy difíciles

**37 🔴 Un `Publisher` de Reactive Streams a mano.** Sin Reactor.
- Firma: `class RangePublisher(val n: Int) : Publisher<Int>` con su `Subscription` que respeta `request(n)` y `cancel`
- Casos: pasa el TCK de Reactive Streams (opcional, `org.reactivestreams:reactive-streams-tck`) o, como mínimo,
  pruebas de demanda (pedir 3 da 3), cancelación y reentrada (un `request` dentro de `onNext` no desborda la pila).

**38 🔴 Libro de órdenes reactivo.** El motor de `02 · channels` como `Flux<Command>` → `Flux<Execution>`.
- Firma: `fun Flux<Command>.matched(): Flux<Execution>` con estado por suscripción
- Casos: la misma secuencia de órdenes da las mismas ejecuciones que el actor; la cantidad se conserva.

**39 🔴 Backpressure de punta a punta.** Una fuente rápida, una transformación lenta y un sumidero que pide de a 10.
- Firma: libre, con `limitRate`, `onBackpressureBuffer` y métricas de cuánto hay en cada buffer
- Casos: la memoria se mantiene acotada (cuenta elementos en vuelo); con un buffer sin límite, crece (demuéstralo).

**40 🔴 Un mini WebFlux.** Un servidor HTTP de precios con Reactor Netty (opcional; agrega la dependencia en tu
práctica).
- Firma: un endpoint `/prices/{ticker}` que devuelve un `Flux` de precios como *server-sent events*
- Casos: un cliente recibe precios; al cerrar la conexión, el `Flux` se cancela (cuéntalo); se retoma en `09-spring`.

## ⚡ Saiyajin

**41 ⚡ Pipeline del feed en Reactor.** Limpieza, velas y alertas del feed sobre `Flux`.
- Firma: `class ReactorFeedPipeline` con `fun cleaned(ticks: Flux<Tick>): Flux<Tick>`, `fun candles(ticks: Flux<Tick>): Flux<Candle>`
  y `fun alerts(ticks: Flux<Tick>, rules: Map<String, Money>): Flux<String>`
- Reglas: estado por suscripción; sin `block()` en el pipeline; tiempos sobre el feed temporizado (`timedFeed`).
- **Se verifica con:** pruebas con `StepVerifier` en tiempo virtual (`withVirtualTime`) que dan las mismas velas que la
  versión `Flow` de `02 · flow` para el mismo feed (aquí, contra `candlesReference` y tu versión `Flow` del 36).
