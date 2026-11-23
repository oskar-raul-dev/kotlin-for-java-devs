# Ejercicios: RxJava desde Kotlin y su migración a corrutinas

> **Lección:** `rxjava` · **Dónde se resuelven:** `rxjava/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../rxjava/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 03-reactive test --tests '*.rxjava.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `test()`
> (`TestObserver`/`TestSubscriber`) y `TestScheduler` para el tiempo virtual de RxJava. Kotlin reconoce las anotaciones
> de nulabilidad de RxJava 3 (medido): un `null` explícito no compila; se cuela por platform types de Java. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 `Observable` frente a `Flowable`.** El mismo feed con los dos.
- Firma: `fun feedObservable(ticks: List<Tick>): Observable<Tick>` y `fun feedFlowable(ticks: List<Tick>): Flowable<Tick>`
- Casos: los dos emiten lo mismo; `Flowable` respeta `test(3)` (pide 3 y recibe 3) y `Observable` no tiene demanda.
  Explica en un comentario cuándo usar cada uno.

**02 🟢 `Single`, `Maybe`, `Completable`.** Los tipos de un solo resultado.
- Firma: `fun quote(t: String): Single<Money>`, `fun lastPrice(t: String): Maybe<Money>` y `fun settle(): Completable`
- Casos: `Maybe` vacío completa sin valor; `Single` vacío no existe (`Single.fromCallable { null }` falla:
  compruébalo); `Completable` solo termina o falla. Compáralos con `Mono` de Reactor.

**03 🟢 `subscribeBy`.** Argumentos con nombre de RxKotlin.
- Firma: `fun watch(source: Observable<Money>, log: MutableList<String>): Disposable` con `subscribeBy(onNext = …, onError = …, onComplete = …)`
- Casos: valores, error y fin quedan en el log en orden; explica por qué los argumentos con nombre evitan confundir
  callbacks (`subscribe(a, b)` en Java).

**04 🟢 `CompositeDisposable` y `addTo`.** Liberar todo de una vez.
- Firma: `class TickerScreen` con `fun open(sources: List<Observable<Money>>)` y `fun close()`
- Casos: después de `close()`, ninguna fuente sigue emitiendo (con `TestScheduler`); `clear()` frente a `dispose()`.

**05 🟢 🎤 Un error sin `onError`.** Qué pasa y cómo se evita.
- Firma: usa `errorWithoutOnError` de la lección
- Casos: el manejador global recibe `OnErrorNotImplementedException`; con `RxJavaPlugins.setErrorHandler` lo capturas;
  explica en un comentario por qué en Android eso cierra la app y la regla (siempre `onError`).

**06 🟢 `TestObserver`.** Comprobar un flujo.
- Firma: pruebas sobre `cleaned()` de la lección
- Casos: `assertValues`, `assertValueCount`, `assertComplete`, `assertNoErrors`, `assertError`.

**07 🟢 `TestScheduler`.** Tiempo virtual en RxJava.
- Firma: `fun quotesEvery(period: Long, scheduler: Scheduler): Observable<Money>`
- Casos: `advanceTimeBy(5, SECONDS)` da 5 valores; `triggerActions()` corre lo pendiente; compáralo con `runTest`.

**08 🟢 `map`, `filter`, `distinctUntilChanged`.** Cambios de precio de ACME.
- Firma: `fun Observable<Tick>.acmeChanges(): Observable<Money>`
- Casos: con un feed chico, solo los cambios de ACME en orden.

**09 🟢 `zip` y `combineLatest`.** Precio y tasa.
- Firma: `fun priceInEur(prices: Observable<Money>, rates: Observable<BigDecimal>, latest: Boolean): Observable<Money>`
- Casos: con `TestScheduler` y tiempos distintos, `zip` empareja de a uno y `combineLatest` combina con el último del
  otro.

**10 🟢 `onErrorReturnItem` y `onErrorResumeNext`.** Fuente de respaldo.
- Firma: `fun withBackup(primary: Single<Money>, backup: Single<Money>): Single<Money>`
- Casos: como en `reactor` (01–08): la principal, el respaldo, el error del respaldo.

**11 🟢 `toObservable` y `toFlowable` de RxKotlin.** Desde colecciones.
- Firma: `fun tickers(): Observable<String> = TICKERS.toObservable()`
- Casos: emite los cinco; `toFlowable()` con `test(2)` recibe dos.

**12 🟢 `blockingGet` en pruebas y en ningún otro lado.** Lo que bloquea.
- Firma: `fun quoteBlocking(t: String): Money = quote(t).blockingGet()` (solo para una prueba)
- Casos: funciona en una prueba; explica en un comentario por qué en el hilo principal de Android (o en un hilo de un
  servidor reactivo) es un error.

## 🟡 Medios

**13 🟡 🎤 `subscribeOn` y `observeOn`.** Cuál afecta a qué.
- Firma: `fun threadsOf(chain: String): List<String>` con tres cadenas distintas (dos `subscribeOn`, `observeOn` en el
  medio, `observeOn` al final)
- Casos: el primer `subscribeOn` gana (el de más arriba en la cadena); cada `observeOn` cambia lo que sigue. Explica la
  regla en un comentario.

**14 🟡 La suscripción que nadie libera.** Fugas de memoria.
- Firma: `class LeakyPresenter(source: Observable<Money>)` que se suscribe en el constructor y nunca libera
- Casos: con una referencia débil (`WeakReference`) y `System.gc()`, el presentador no se libera mientras la
  suscripción vive (explica que el resultado del GC no está garantizado y cómo lo verificas); con `dispose()`, sí.

**15 🟡 `flatMap`, `concatMap`, `switchMap`.** Cotizaciones del ticker seleccionado.
- Firma: `fun Observable<String>.quotes(mode: String, quote: (String) -> Observable<Money>): Observable<Money>`
- Casos: con `TestScheduler`, `concatMap` en orden, `flatMap` intercalado y `switchMap` cancela el anterior al cambiar.

**16 🟡 `retryWhen` con espera.** Reconectar el feed.
- Firma: `fun <T : Any> Observable<T>.retryWithBackoff(times: Int, initialMillis: Long, scheduler: Scheduler): Observable<T>`
- Casos: con `TestScheduler`, los reintentos en los instantes exactos; al agotarlos, el error original.

**17 🟡 `timeout`.** Cotización con plazo.
- Firma: `fun Single<Money>.within(millis: Long, scheduler: Scheduler, fallback: Money): Single<Money>`
- Casos: a tiempo y fuera de tiempo, con `TestScheduler`.

**18 🟡 `Subject`s.** El tablero con `BehaviorSubject` y las alertas con `PublishSubject`.
- Firma: `class RxBoard` con `val price: BehaviorSubject<Money>` y `val alerts: PublishSubject<String>`
- Casos: el suscriptor tardío recibe el precio actual y no las alertas viejas; compáralo en un comentario con
  `StateFlow` y `SharedFlow` de `02 · hotflows`.

**19 🟡 `share` y `replay`.** Compartir una fuente cara.
- Firma: `fun sharedFeed(source: Observable<Tick>): Observable<Tick>` con `publish().refCount()` o `share()`
- Casos: dos suscriptores, una sola suscripción a la fuente (cuéntala con `doOnSubscribe`); `replay(1).refCount()` le
  da el último al que llega tarde.

**20 🟡 🎤 `null` en RxJava desde Kotlin.** Por dónde se cuela.
- Firma: `fun Observable<String>.withConfig(): Observable<String>` que hace `map { System.getProperty(it) }`
- Casos: compila y falla con `NullPointerException("The mapper function returned a null value.")` (medido en la
  lección); arréglalo con `mapOptional` o filtrando con `flatMapMaybe { Maybe.fromCallable { … } }`. Explica en un
  comentario por qué `map { null }` sí lo detecta el compilador y esto no.

**21 🟡 Probar con `TestScheduler` y `TestObserver` juntos.** Velas de un feed temporizado.
- Firma: `fun timedFeed(ticks: List<Tick>, scheduler: Scheduler): Observable<Tick>` (cada tick en su instante)
- Casos: avanzar el reloj minuto a minuto y comprobar cuántas velas salieron.

**22 🟡 `Flowable.generate`.** Una fuente que respeta la demanda.
- Firma: `fun priceWalk(start: Money): Flowable<Money>` con `generate` (un valor por pedido)
- Casos: `test(3)` recibe 3 y la fuente generó 3 (cuéntalo); `take(10)` termina.

**23 🟡 `Completable` en cadena.** Liquidar, registrar y notificar.
- Firma: `fun settlement(steps: List<Completable>): Completable` con `concat`
- Casos: en orden; si uno falla, los siguientes no corren.

**24 🟡 `groupBy` en RxJava.** El último precio por ticker.
- Firma: `fun Observable<Tick>.lastPerTicker(): Single<Map<String, Money>>`
- Casos: un mapa con los cinco; con `toMap` o `groupBy` + `lastElement`.

**25 🟡 RxJava en una API de Java.** Consumir una librería Java que devuelve `Observable` y un callback.
- Firma: un adaptador que convierte un callback de Java en `Observable.create { emitter -> … }` con `setCancellable`
- Casos: cancelar desregistra; un error del callback llega como `onError`.

**26 🟡 `Disposable` en Kotlin con `use`.** Liberar al salir de un bloque.
- Firma: `inline fun <T> Disposable.useAndDispose(block: () -> T): T`
- Casos: al salir (bien o con excepción), `isDisposed` es verdadero.

## 🟠 Difíciles

**27 🟠 🎤 Migrar RxJava a corrutinas sin reescribir todo.** Un servicio con tres capas, migrado de abajo hacia arriba.
- Firma: un `QuoteRepository` (RxJava), un `QuoteService` y un `QuotePresenter`; primero el repositorio pasa a
  `suspend` y la capa de arriba lo consume con `rxSingle { }`, después el servicio, después el presentador
- Casos: la misma batería de pruebas pasa en cada paso (guarda cada paso en un archivo o en tu historial); al final, el
  núcleo no importa nada de RxJava. Explica en un comentario el orden elegido y por qué.

**28 🟠 `asFlow` y `asObservable`.** Las dos direcciones.
- Firma: `fun Observable<Tick>.cleanedFlow(): Flow<Tick>` y `fun Flow<Candle>.toObservable(): Observable<Candle>`
  (con `asObservable()` de kotlinx-coroutines-rx3)
- Casos: ida y vuelta da lo mismo; cancelar el colector del `Flow` libera la suscripción de RxJava (cuéntalo).

**29 🟠 `await` en todas sus formas.** `Single.await`, `Maybe.awaitSingleOrNull`, `Completable.await`, `Observable.awaitFirst`.
- Firma: un servicio que usa los cuatro desde corrutinas
- Casos: vacíos y errores de cada uno; la cancelación de la corrutina hace `dispose` de la suscripción.

**30 🟠 Schedulers de RxJava y dispatchers.** Que los dos mundos usen los mismos hilos.
- Firma: `fun Scheduler.asDispatcher()` de kotlinx-coroutines-rx3 y su inverso `asScheduler()`
- Casos: una corrutina en `Schedulers.io().asCoroutineDispatcher()` corre en hilos `RxCachedThreadScheduler`; una cadena
  RxJava en `Dispatchers.IO.asScheduler()` corre en `DefaultDispatcher-worker`.

**31 🟠 🎤 Un `Observable` rápido sin backpressure.** Lo que hace con la memoria.
- Firma: `fun floodObservable(n: Int): Observable<Int>` con un `observeOn` y un consumidor lento
- Casos: mide la memoria o la longitud de la cola interna (con un contador de emitidos menos consumidos) con 1 000 000
  elementos; con `Flowable` y `onBackpressureDrop`, se mantiene acotada. Explica por qué `Observable` no la tiene.

**32 🟠 RxJava y el ciclo de vida de una pantalla.** Suscribir al abrir y liberar al cerrar, con reintentos.
- Firma: `class ScreenModel(source: Observable<Money>, scheduler: Scheduler)` con `onStart`/`onStop`
- Casos: abrir, cerrar y volver a abrir; ninguna suscripción duplicada ni perdida; compáralo con `viewModelScope` y
  `repeatOnLifecycle` (se retoma en `12-android`).

**33 🟠 Errores en `flatMap`.** Uno falla: ¿cae todo?
- Firma: `fun quoteAllSafely(tickers: Observable<String>, quote: (String) -> Single<Money>): Observable<Result<Money>>`
- Casos: sin protección, el primer error termina todo; con `onErrorReturn` dentro del `flatMap`, cada uno tiene su
  resultado; compáralo con `supervisorScope` de `02 · cancellation`.

**34 🟠 RxJava 2 a RxJava 3.** Qué cambió (lectura y código).
- Firma: un archivo `.md` en tu práctica con los cambios que afectan a código Kotlin (paquetes, `null`, `Single` vacío,
  `Maybe.toSingle`, `test()`), cada uno con un ejemplo que compila en RxJava 3
- Casos: los ejemplos tienen su prueba.

**35 🟠 Medir RxJava frente a `Flow`.** El pipeline del feed.
- Firma: el pipeline de limpieza y velas en los dos sobre `tickFeed(steps = 10_000)`
- Casos: mismo resultado; tabla de tiempos con calentamiento en un comentario.

**36 🟠 `Observable.create` con recursos.** Un feed que abre una conexión simulada.
- Firma: `fun connectionFeed(log: MutableList<String>): Observable<Tick>` con `emitter.setCancellable { cerrar }`
- Casos: `take(3)`, error y `dispose` cierran la conexión exactamente una vez.

## 🔴 Muy difíciles

**37 🔴 Un operador propio de RxJava.** `lift` con un `Observer` intermedio.
- Firma: `fun <T : Any> Observable<T>.everyNth(n: Int): Observable<T>` con `lift`
- Casos: cumple el contrato (un solo `onError` o `onComplete`, nada después); `dispose` llega a la fuente.

**38 🔴 Migración completa de un módulo.** El pipeline del feed en RxJava pasado entero a `Flow`, con puentes en las
fronteras mientras dura.
- Firma: libre, en pasos
- Casos: la misma batería en cada paso; al final, ninguna dependencia de RxJava en el núcleo (lo verifica una prueba que
  revisa los imports de los archivos del núcleo).

**39 🔴 Un `TestScheduler` compartido con `runTest`.** Sincronizar los dos relojes virtuales.
- Firma: un adaptador que avanza el `TestScheduler` de RxJava cuando avanza el reloj de `runTest`
- Casos: una cadena mixta (corrutinas y RxJava) se prueba en tiempo virtual con los instantes exactos.

**40 🔴 Comparación final.** Reactor, RxJava y corrutinas para el mismo servicio de cotizaciones.
- Firma: tres implementaciones con la misma interfaz y la misma batería de pruebas
- Casos: un informe en un comentario con líneas de código, tiempos y facilidad de prueba de cada una.

## ⚡ Saiyajin

**41 ⚡ Migración incremental.** El pipeline en RxJava migrado a `Flow` por partes, con puentes en las fronteras.
- Firma: `RxFeedPipeline` (el punto de partida, en RxJava) y `FlowFeedPipeline` (el final), con pasos intermedios
  que mezclan los dos con `asFlow`, `asObservable`, `await` y `rxSingle`
- Reglas: la misma interfaz pública en cada paso; sin `blockingGet` fuera de las pruebas.
- **Se verifica con:** la misma batería de pruebas en verde en cada paso de la migración (un commit por paso), y una
  prueba final sin dependencias de RxJava en el núcleo.
