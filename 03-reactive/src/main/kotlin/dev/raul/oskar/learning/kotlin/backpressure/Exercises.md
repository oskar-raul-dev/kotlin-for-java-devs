# Ejercicios: backpressure en Reactor, RxJava y `Flow`

> **Lección:** `backpressure` · **Dónde se resuelven:** `backpressure/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../backpressure/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 03-reactive test --tests '*.backpressure.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Para que las pruebas sean
> deterministas, la demanda se controla a mano (`consumeSlowly` de la lección, `test(n)` de RxJava, `StepVerifier` con
> demanda inicial) o el tiempo es virtual (`runTest`, `TestScheduler`, `withVirtualTime`); las mediciones de memoria y
> throughput van en tablas dentro de comentarios. Regla del caso: los precios se pueden descartar; las ejecuciones y las
> órdenes, nunca. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 ¿Qué es la backpressure?** El mismo productor rápido en los tres modelos, sin ninguna estrategia.
- Firma: un productor de 1 000 precios y un consumidor de 1 ms por elemento en `Flow`, en `Flowable` y en `Flux`
- Casos: en `Flow`, el productor espera (sus instantes de emisión lo muestran); en `Flowable` y `Flux` con `request`
  normal, también; explica en un comentario qué es la backpressure y cómo la resuelve cada modelo (suspensión frente a
  `request(n)`).

**02 🟢 `request(n)` a mano.** Un suscriptor de Reactor que pide de a 2.
- Firma: `class TwoAtATime : BaseSubscriber<Int>()` que pide 2 al suscribirse y 2 más cada dos recibidos
- Casos: con `Flux.range(1, 10).doOnRequest { … }`, las demandas anotadas son todas de 2; recibe los 10.

**03 🟢 `test(n)` en RxJava.** La demanda inicial del `TestSubscriber`.
- Firma: pruebas sobre `Flowable.range(1, 10)`
- Casos: `test(3)` recibe 3; `requestMore(2)`, dos más; `test()` (sin número), todo.

**04 🟢 `onBackpressureDrop` con contador.** Cuántos se perdieron.
- Firma: `fun Flux<Int>.droppingCounted(dropped: AtomicInteger): Flux<Int>` con `onBackpressureDrop { dropped++ }`
- Casos: con `eagerReactorSource(100)` y `consumeSlowly(5, 10)`, se perdieron 95.

**05 🟢 🎤 ¿Cuándo descartas datos y cuándo no?** Precios frente a ejecuciones.
- Firma: `fun Flux<Money>.forScreen(): Flux<Money>` (con `onBackpressureLatest`) y `fun Flux<Execution>.forSettlement(): Flux<Execution>`
  (sin descarte: buffer acotado que falla o una fuente que respeta la demanda)
- Casos: la pantalla recibe el último precio; la liquidación recibe todas las ejecuciones o falla de forma visible.
  Explica la regla en un comentario.

**06 🟢 `conflate` en `Flow`.** El último precio para la pantalla.
- Firma: usa `flowWithStrategy` de la lección con `LATEST`
- Casos: con 20 precios y un consumidor de 10 ms, recibe el primero y el último (medido); con un consumidor de 0 ms,
  todos.

**07 🟢 `buffer(n)` en `Flow`.** No pierde, espera.
- Firma: `flowWithStrategy` con `BUFFER` y distintos tamaños
- Casos: con buffer 0, 4 y 64, recibe los 20 siempre; el tiempo total es el del consumidor; los instantes de emisión
  muestran cuánto se adelanta el productor en cada caso.

**08 🟢 `buffer(n, DROP_OLDEST)`.** Otra política.
- Firma: amplía `Strategy` con `DROP_OLDEST` en tu práctica
- Casos: con 20 precios y buffer 4, recibe el primero y los últimos 4 (pruébalo y anota el resultado exacto).

**09 🟢 `Observable.toFlowable`.** Elegir la estrategia al cruzar.
- Firma: usa `toFlowableWith` de la lección
- Casos: `LATEST` y `DROP` con `consumeSlowly(3, 1)` dan lo medido en la lección; `BUFFER` (sin límite) no pierde.

**10 🟢 `limitRate`.** Prefetch acotado en Reactor.
- Firma: `fun Flux<Int>.prefetched(n: Int): Flux<Int>` con `limitRate(n)`
- Casos: con `doOnRequest` sobre `Flux.range(1, 100)`, la primera demanda es n y las siguientes, 75 % de n (anota lo
  medido); compáralo con `buffer(n)` + `flowOn` de `Flow`.

**11 🟢 Un reloj sin demanda.** `Flux.interval` y `Flowable.interval`.
- Firma: pruebas en tiempo virtual con demanda 0
- Casos: los dos fallan al primer tick (medido en la lección); con `onBackpressureDrop()` antes, no fallan y descartan.

**12 🟢 Un buffer sin límite no es backpressure.** Demostrarlo.
- Firma: `fun unboundedBufferSize(produced: Int, consumed: Int): Int` (cuántos quedan esperando)
- Casos: con `onBackpressureBuffer()` sin tamaño y un consumidor que pidió 10 de 100 000, hay 99 990 en memoria (cuenta
  con un contador). Explica en un comentario por qué solo cambia cuándo se acaba la memoria.

## 🟡 Medios

**13 🟡 🎤 Reactor y RxJava ante el desborde.** La diferencia medida en la lección.
- Firma: pruebas sobre `withStrategy(BUFFER)` en los dos
- Casos: Reactor entrega lo que tenía en el buffer y después falla (`OverflowException`); RxJava falla enseguida
  (`MissingBackpressureException`); con `onBackpressureBuffer(n, true)` en RxJava (`delayError`), se comporta como
  Reactor. Explica en un comentario cuál preferirías para ejecuciones y por qué.

**14 🟡 Estrategias de desborde de Reactor.** `BufferOverflowStrategy`.
- Firma: `fun Flux<Int>.bufferWith(size: Int, strategy: BufferOverflowStrategy): Flux<Int>`
- Casos: `ERROR`, `DROP_LATEST` y `DROP_OLDEST` con `consumeSlowly(5, 10)` dan lo esperado (anótalo); el callback de
  descarte recibe cada elemento perdido.

**15 🟡 Estrategias de RxJava.** `BackpressureOverflowStrategy`.
- Firma: el equivalente de 14 en `Flowable`
- Casos: las mismas tres; compara las salidas con las de Reactor.

**16 🟡 Ráfagas del feed.** Diez veces el ritmo normal durante 5 s.
- Firma: `fun burstFeed(scheduler: Scheduler): Flowable<Tick>` (un tick cada 100 ms, y entre los segundos 10 y 15, uno
  cada 10 ms)
- Casos: un consumidor de 50 ms con `onBackpressureLatest` nunca se atrasa más de un elemento; con buffer acotado de
  64, el atraso máximo es 64 (mídelo en tiempo virtual).

**17 🟡 Precios con `conflate` y ejecuciones con `buffer`.** El caso de la lección, en `Flow`.
- Firma: `fun splitFeed(source: Flow<MarketEvent>): Pair<Flow<Money>, Flow<Execution>>` (con un `sealed interface
  MarketEvent` propio)
- Casos: el flujo de precios pierde intermedios; el de ejecuciones no pierde ninguna; en tiempo virtual.

**18 🟡 `onBackpressureLatest` frente a `sample`.** No es lo mismo.
- Firma: dos versiones del flujo de la pantalla: una que se queda con el último cuando el consumidor no pide, otra que
  muestrea cada 100 ms
- Casos: con un consumidor rápido, la primera entrega todo y la segunda no (explica por qué); en tiempo virtual.

**19 🟡 La backpressure de `SharedFlow`.** Un suscriptor lento frena a todos.
- Firma: un `MutableSharedFlow` con dos suscriptores (uno lento) y su versión con `DROP_OLDEST`
- Casos: con `SUSPEND`, el rápido también se atrasa (medido en `02 · hotflows`); con `DROP_OLDEST`, no.

**20 🟡 🎤 ¿Por qué `Observable` no tiene backpressure?** Y qué hace con un productor rápido.
- Firma: `fun floodWithObserveOn(n: Int): Int` (máximo de elementos en la cola de `observeOn`, con un contador)
- Casos: con 1 000 000 elementos y un consumidor lento, la cola crece sin límite; con `Flowable` y `observeOn` (que
  tiene un buffer de 128 y pide de a lotes), se mantiene acotada. Explica la decisión de diseño de RxJava 2/3.

**21 🟡 Prefetch de `flowOn`.** Cuánto se adelanta el productor.
- Firma: `suspend fun aheadWithFlowOn(capacity: Int): Int` (máximo de emitidos menos recibidos)
- Casos: con `flowOn` solo, hasta 64 (el buffer por defecto); con `buffer(8).flowOn(…)`, hasta 8; anota lo medido.

**22 🟡 Backpressure a través del puente.** `Flux` → `Flow` → `Flux`.
- Firma: una cadena que cruza dos veces
- Casos: la demanda del suscriptor final llega a la fuente (con `doOnRequest` en la fuente); anota los tamaños de lote
  en cada cruce.

**23 🟡 Demanda por lotes en un consumidor de base de datos.** Insertar de a 100.
- Firma: `fun Flux<Execution>.persistInBatches(save: (List<Execution>) -> Mono<Void>): Mono<Void>` con `buffer(100)` y
  `concatMap`
- Casos: 1 050 ejecuciones → 11 lotes; nunca hay más de un lote guardándose a la vez; ninguna ejecución se pierde.

**24 🟡 El mismo consumidor por lotes en `Flow`.** `chunked` (experimental) o a mano.
- Firma: `fun Flow<Execution>.persistInBatches(size: Int, save: suspend (List<Execution>) -> Unit)`
- Casos: los mismos que 23; anota en un comentario que `chunked` pide `@ExperimentalCoroutinesApi` en 1.11.0 (medido en
  `02 · flow`) y una versión sin opt-in.

**25 🟡 *Rate limiting* frente a backpressure.** No son lo mismo.
- Firma: un productor limitado a 10 por segundo (`delayElements` o `sample`) frente a un consumidor que pide de a 10
- Casos: el primero limita aunque el consumidor sea rápido; el segundo, solo si el consumidor es lento; en tiempo
  virtual. Explica la diferencia.

**26 🟡 Descarte con registro.** Saber qué se perdió.
- Firma: `fun Flux<Money>.latestWithLog(dropped: MutableList<Money>): Flux<Money>` con `onBackpressureLatest` y
  `doOnDiscard`
- Casos: la lista de descartados más los recibidos son todos los emitidos.

## 🟠 Difíciles

**27 🟠 🎤 El pipeline del feed bajo carga.** Los tres modelos con la misma ráfaga y la misma estrategia por dato.
- Firma: el pipeline (limpieza, precios con último valor, ejecuciones con buffer acotado) en Reactor, RxJava y `Flow`
- Casos: con la misma ráfaga en tiempo virtual, los tres entregan todas las ejecuciones y el último precio; explica en
  un comentario qué tuviste que decidir en cada modelo y dónde.

**28 🟠 Backpressure adaptativa.** Un consumidor que pide más cuando va rápido y menos cuando va lento.
- Firma: `class AdaptiveSubscriber(min: Long, max: Long) : BaseSubscriber<Int>()` que ajusta el tamaño de la demanda
  según la latencia de procesamiento
- Casos: con un procesamiento que se vuelve lento a mitad, la demanda baja; con uno que se acelera, sube; nunca sale
  de [min, max].

**29 🟠 Medir memoria máxima.** Un minuto de carga sostenida con cada estrategia.
- Firma: `fun peakMemory(model: String, strategy: Strategy): Long` (con `MemoryMXBean` y muestreo cada 100 ms)
- Casos: tabla en un comentario para los tres modelos y las tres estrategias; el buffer sin límite crece y los demás
  no.

**30 🟠 Un operador de backpressure propio en Reactor.** `onBackpressureLatestWithin(window)`: el último, pero solo si
no es más viejo que la ventana.
- Firma: `fun <T : Any> Flux<T>.latestWithin(window: Duration): Flux<T>`
- Casos: en tiempo virtual, un valor viejo se descarta aunque sea el último; uno fresco pasa.

**31 🟠 🎤 Diseñar la backpressure de un servicio.** Un servicio que recibe órdenes por HTTP, las valida y las manda al
libro.
- Firma: un diseño con código: dónde hay colas, de qué tamaño, qué se hace al llenarse (responder 429, esperar, fallar)
- Casos: una prueba con una ráfaga de 10 000 órdenes muestra que ninguna se pierde en silencio (o se aceptan o se
  rechazan explícitamente) y la memoria queda acotada. Explica las decisiones en un comentario.

**32 🟠 Backpressure entre servicios.** Del productor al consumidor a través de un canal "de red" simulado.
- Firma: un productor y un consumidor conectados por un `Channel` acotado que simula la red, con demanda propagada
- Casos: si el consumidor se detiene, el productor se detiene en N elementos; al reanudar, sigue sin pérdidas.

**33 🟠 RSocket o *request-n* por la red (lectura y diseño).** Cómo viaja la demanda entre procesos.
- Firma: un archivo `.md` en tu práctica que explica cómo RSocket transporta `request(n)` y qué haría falta para lo
  mismo con WebSockets
- Casos: un ejemplo con código (simulado) que muestra la demanda cruzando dos procesos ficticios.

**34 🟠 Pruebas de propiedades de las estrategias.** Para cualquier secuencia y cualquier demanda.
- Firma: un bucle de 100 casos aleatorios con semilla sobre `consumeSlowly`
- Casos: con `DROP`, lo recibido es un prefijo de lo emitido; con `LATEST`, lo recibido es una subsecuencia que termina
  en el último; con `BUFFER`, o se recibe todo o hay error.

**35 🟠 Backpressure y cancelación.** Que cancelar libere los buffers.
- Firma: pruebas en los tres modelos con un buffer lleno y una cancelación
- Casos: los elementos en el buffer se descartan (con `doOnDiscard` en Reactor, `onUndeliveredElement` en canales); la
  memoria vuelve.

**36 🟠 El costo de `request(1)`.** Pedir de a uno frente a pedir de a lotes.
- Firma: `fun throughputByBatch(batch: Long, items: Int): Duration`
- Casos: tabla con lotes de 1, 16, 128 y 1 024; explica por qué el prefetch existe.

## 🔴 Muy difíciles

**37 🔴 Un `onBackpressureBuffer` propio.** Con cola acotada, demanda de abajo y desborde configurable.
- Firma: `fun <T : Any> Publisher<T>.myBackpressureBuffer(capacity: Int, onOverflow: OverflowPolicy): Publisher<T>`
- Casos: pasa las mismas pruebas que la lección para las tres políticas; respeta la regla de una señal a la vez de
  Reactive Streams.

**38 🔴 Un `conflate` propio en `Flow`.** Sin usar el de la librería.
- Firma: `fun <T> Flow<T>.myConflate(): Flow<T>` con `channelFlow` o con dos corrutinas y un `AtomicReference`
- Casos: mismo resultado que `conflate()` en tiempo virtual; la cancelación detiene al productor.

**39 🔴 Simulación de una sesión con ráfagas reales.** Una sesión de 6.5 h con aperturas y cierres agitados.
- Firma: libre, con los tres modelos
- Casos: en tiempo virtual, cuántos precios descartó cada modelo y que ninguno descartó ejecuciones; menos de 5 s de
  reloj real por modelo.

**40 🔴 Informe de backpressure.** Qué modelo y qué estrategia para el feed, la liquidación y una app móvil.
- Firma: un archivo `.md` con la recomendación para cada caso, respaldada por las mediciones de los ejercicios
  anteriores
- Casos: cada afirmación con su número y su ejercicio de origen.

## ⚡ Saiyajin

**41 ⚡ El mismo pipeline en tres modelos.** El pipeline del feed en Reactor, RxJava y `Flow`, bajo la misma carga.
- Firma: `interface FeedPipeline { fun run(load: Load): Report }` con tres implementaciones y un `Load` con ráfagas
- Reglas: precios con último valor, ejecuciones sin pérdida (buffer acotado y demanda respetada); medición con
  calentamiento y carga sostenida.
- **Se verifica con:** una tabla de throughput y memoria máxima de cada uno (con calentamiento, carga sostenida de un
  minuto) guardada en la lección, y una prueba por estrategia que muestra qué elementos llegan (todos, los últimos, los
  primeros).
