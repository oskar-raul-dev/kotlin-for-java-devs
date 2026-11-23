# Ejercicios: flujos calientes, `StateFlow` y `SharedFlow`

> **Lección:** `hotflows` · **Dónde se resuelven:** `hotflows/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../hotflows/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.hotflows.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `runTest`,
> `backgroundScope` para los colectores que no terminan y Turbine (`test { awaitItem() }`). Recuerda: `collect` sobre un
> `StateFlow` o `SharedFlow` nunca termina solo. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 18, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 `StateFlow` frente a `SharedFlow`.** El último precio y las alertas, cada uno con su tipo.
- Firma: `class TickerState` con `val price: StateFlow<Money?>` y `val alerts: SharedFlow<String>`
- Casos: un suscriptor tardío recibe el precio actual y ninguna alerta pasada. Explica en un comentario cuándo usar
  cada uno (estado frente a eventos) y su equivalente en RxJava (`BehaviorSubject`, `PublishSubject`).

**02 🟢 Exponer solo lectura.** El patrón `private val _x = MutableStateFlow(…)` y `val x = _x.asStateFlow()`.
- Firma: `class Counter` con `val count: StateFlow<Int>` y `fun increment()`
- Casos: desde afuera no se puede castear a `MutableStateFlow` (`asStateFlow` lo impide: compruébalo con `is`).

**03 🟢 `update`.** Un contador de órdenes por ticker.
- Firma: `class OrderCounts` con `val counts: StateFlow<Map<String, Int>>` y `fun record(ticker: String)`
- Casos: tres órdenes de ACME y una de ORBX; 100 corrutinas en `Dispatchers.Default` registran 1 000 cada una y el
  total es exacto.

**04 🟢 `equals` y conflación.** Qué se emite y qué no.
- Firma: `fun emissions(values: List<Money>): List<Money>` (lo que ve un colector sin confinar)
- Casos: `100, 100, 101` → `100, 101` (con el valor inicial); `Money.of("100")` y `Money.of("100.00")` son iguales (la
  escala se normaliza en `Money.of`).

**05 🟢 🎤 Suscriptor tardío.** Qué recibe cada uno.
- Firma: `suspend fun lateSubscriberSees(replay: Int): List<Int>` sobre un `MutableSharedFlow(replay)` donde se emiten
  1..5 antes de suscribirse
- Casos: replay 0 → nada; replay 2 → `4, 5`; y un `StateFlow` → solo `5`. Explica la diferencia en un comentario.

**06 🟢 `first()` en vez de `collect`.** Esperar a que el saldo llegue a un valor.
- Firma: `suspend fun awaitBalanceAtLeast(balance: StateFlow<Money>, target: Money): Money`
- Casos: si ya alcanza, vuelve al instante; si no, espera a que otra corrutina deposite; con `withTimeout` cuando nunca
  llega.

**07 🟢 `subscriptionCount`.** Saber cuántos escuchan.
- Firma: `class AlertBus` con `val alerts: SharedFlow<String>` y `val listeners: StateFlow<Int>`
- Casos: dos suscriptores → 2; cancelar uno → 1 (en tiempo virtual, después de `runCurrent`).

**08 🟢 `tryEmit` sin buffer.** El evento que se pierde.
- Firma: `fun tryEmitAll(flow: MutableSharedFlow<Int>, values: List<Int>): List<Boolean>`
- Casos: sin suscriptores ni buffer, todos `true` y se pierden; con un suscriptor lento y sin buffer, `tryEmit`
  devuelve `false`; con `extraBufferCapacity = 2`, los dos primeros entran.

**09 🟢 `StateFlow` de un `data class`.** El estado de una pantalla de transferencia.
- Firma: `data class TransferUiState(val amount: String = "", val error: String? = null, val sending: Boolean = false)` y
  `class TransferForm` con `val state: StateFlow<TransferUiState>` y `fun onAmount(text: String)`
- Casos: escribir un monto inválido pone el error; corregirlo lo quita; usa `copy` dentro de `update`.

**10 🟢 `combine` de dos `StateFlow`.** El valor de una posición: precio × cantidad.
- Firma: `fun positionValue(price: StateFlow<Money?>, quantity: StateFlow<Long>): Flow<Money?>`
- Casos: cambia al cambiar cualquiera de los dos; `null` mientras no hay precio.

**11 🟢 `map` sobre un `StateFlow`.** Ya no es un `StateFlow`.
- Firma: `fun StateFlow<Money?>.formatted(): Flow<String>`
- Casos: el resultado es un `Flow` frío (no tiene `value`); explica en un comentario cómo volver a tener un `StateFlow`
  (`stateIn`) y qué hace falta para eso (un scope).

**12 🟢 Saldos de la lección.** Usar `LiveBalances` con varias cuentas.
- Firma: `fun totalOf(balances: LiveBalances, accounts: List<String>): Money`
- Casos: después de tres transferencias, el total de todas las cuentas no cambió (doble partida).

## 🟡 Medios

**13 🟡 🎤 `WhileSubscribed(5000)`.** Compartir el feed caro entre pantallas.
- Firma: `class SharedFeed(scope: CoroutineScope, source: Flow<Tick>)` con `val latest: StateFlow<Tick?>`
- Casos: dos suscriptores abren la fuente una sola vez; al irse los dos, la fuente se detiene a los 5 s exactos (no
  antes); si alguien vuelve a los 3 s, no se reinicia. Explica en un comentario para qué sirve el período de gracia
  (rotar la pantalla en Android, reconectar un WebSocket).

**14 🟡 `Eagerly`, `Lazily` y `WhileSubscribed`.** Las tres políticas de arranque.
- Firma: `fun <T> Flow<T>.sharedWith(scope: CoroutineScope, policy: SharingStarted): StateFlow<T?>`
- Casos: con `Eagerly` la fuente arranca sin suscriptores; con `Lazily`, con el primero y nunca se detiene; con
  `WhileSubscribed`, se detiene. Cuenta arranques y paradas en cada una.

**15 🟡 `shareIn` con `replay`.** Las últimas 10 ejecuciones para quien se conecta.
- Firma: `fun Flow<Execution>.recentExecutions(scope: CoroutineScope): SharedFlow<Execution>`
- Casos: un suscriptor que llega después de 25 ejecuciones recibe las últimas 10 y luego las nuevas; la fuente se abre
  una vez.

**16 🟡 `onBufferOverflow`.** Un suscriptor lento de alertas.
- Firma: `fun alertFlow(policy: BufferOverflow): MutableSharedFlow<String>` con `extraBufferCapacity = 3`
- Casos: con `SUSPEND`, quien emite espera (mide el tiempo virtual); con `DROP_OLDEST`, el lento recibe las últimas;
  con `DROP_LATEST`, las primeras. El emisor nunca espera con las dos últimas.

**17 🟡 Un suscriptor lento frena a todos.** Dos suscriptores de un `SharedFlow`: uno rápido y uno lento.
- Firma: `suspend fun fastAndSlow(buffer: Int): Pair<List<Long>, List<Long>>` (instantes en que cada uno recibió)
- Casos: con buffer chico, el rápido también se retrasa porque quien emite espera al lento; con buffer grande o con
  `DROP_OLDEST`, el rápido no se entera. Explica la trampa.

**18 🟡 🎤 `LiveData` frente a `StateFlow`.** La misma pantalla de saldo con las dos ideas (sin Android: un
`LiveData` de juguete con `observe` y `setValue`).
- Firma: `class ToyLiveData<T>` y `class BalanceScreen(balance: StateFlow<Money>)`
- Casos: los dos entregan el valor actual al suscribirse; `StateFlow` no emite valores iguales; explica en un
  comentario qué aporta `StateFlow` (operadores, corrutinas, multiplataforma) y qué tenía `LiveData` (ciclo de vida).
  Se retoma en `12-android`.

**19 🟡 Eventos de un solo uso.** Mostrar "transferencia enviada" una vez, aunque la pantalla se recree.
- Firma: tres versiones: un `SharedFlow(replay = 0)`, un `Channel` con `receiveAsFlow()` y un `StateFlow` con evento que
  se consume
- Casos: con `SharedFlow`, si no hay nadie suscrito el evento se pierde; con `Channel`, espera al primer colector y lo
  recibe uno solo; con `StateFlow`, se repite hasta que se marca consumido. Discute en un comentario cuál elegirías.

**20 🟡 Estado derivado.** El P&L de un portafolio que se recalcula con cada precio.
- Firma: `fun pnl(positions: Map<String, Pair<Long, Money>>, board: PriceBoard, scope: CoroutineScope): StateFlow<Money>`
  (cantidad y costo promedio por ticker)
- Casos: con precios conocidos, el P&L exacto; un ticker sin precio no suma; se actualiza con cada `onTick`.

**21 🟡 `distinctUntilChanged` sobre un `StateFlow`.** Por qué no hace falta.
- Firma: `fun StateFlow<Int>.redundant(): Flow<Int> = distinctUntilChanged()`
- Casos: emite lo mismo que el `StateFlow` original; explica en un comentario por qué (y por qué el IDE puede avisar
  que es redundante).

**22 🟡 Saldo que no puede quedar negativo.** `update` con validación.
- Firma: `class GuardedBalance(initial: Money)` con `fun withdraw(amount: Money): Boolean` usando `update` o
  `compareAndSet`
- Casos: 100 corrutinas retiran 10 a la vez de 500: exactamente 50 lo logran y el saldo termina en cero; explica en un
  comentario por qué `update { if (…) it else it - x }` no basta para devolver `Boolean` y cómo lo resuelve
  `compareAndSet` en un bucle.

**23 🟡 Alertas con Turbine.** Probar el tablero de la lección paso a paso.
- Firma: pruebas sobre `PriceBoard`
- Casos: `awaitItem` por alerta, `expectNoEvents` cuando el movimiento no llega al umbral, `cancelAndIgnoreRemainingEvents`
  al final; dos tickers no se mezclan.

**24 🟡 Precio por ticker con un solo `StateFlow`.** Un mapa en un `StateFlow` frente a un `StateFlow` por ticker.
- Firma: `class MapBoard` con `val prices: StateFlow<Map<String, Money>>` y `fun price(ticker: String): Flow<Money?>`
- Casos: los suscriptores de ACME no reciben nada cuando cambia ORBX (usa `map` + `distinctUntilChanged`); compara en un
  comentario con la versión de la lección (un `StateFlow` por ticker).

**25 🟡 Reconexión del feed compartido.** El feed se cae y se vuelve a abrir sin que los suscriptores se enteren.
- Firma: `fun Flow<Tick>.sharedResilient(scope: CoroutineScope): SharedFlow<Tick>` (`retry` antes de `shareIn`)
- Casos: la fuente falla dos veces y los suscriptores siguen recibiendo; la fuente se abrió tres veces.

**26 🟡 `MutableStateFlow` como caché.** La última tasa de cambio con fecha.
- Firma: `class RateCache(fx: FxRateClient, ttl: Duration, clock: () -> Long)` con `suspend fun rate(): BigDecimal` y
  `val current: StateFlow<Pair<BigDecimal, Long>?>`
- Casos: dentro del TTL no vuelve a llamar; vencido, llama y actualiza; dos llamadas concurrentes con la caché vencida
  hacen una sola consulta.

## 🟠 Difíciles

**27 🟠 🎤 Tablero con varios orígenes.** Precios de dos feeds (principal y de respaldo) combinados en un solo
`StateFlow` por ticker, con el más reciente ganando.
- Firma: `class MergedBoard(scope: CoroutineScope, primary: Flow<Tick>, backup: Flow<Tick>)`
- Casos: si el principal se calla 3 s, manda el de respaldo; cuando vuelve, vuelve a mandar; ningún precio viejo pisa
  uno nuevo (compara instantes). Explica en un comentario por qué conviene un solo escritor por `StateFlow`.

**28 🟠 Contrapresión en un `SharedFlow`.** Medir cuánto se retrasa el feed con N suscriptores lentos.
- Firma: `suspend fun feedDelay(subscribers: Int, slowness: Duration, buffer: Int): Duration`
- Casos: tabla con 1, 10 y 100 suscriptores y buffers 0, 64 y `DROP_OLDEST`; explica cuándo conviene cada uno.

**29 🟠 Máquina de estados de una transferencia.** `Pending → Debited → Converted → Credited` (o `Reverted`) como
`StateFlow`.
- Firma: `sealed interface TransferState` y `class TransferMachine(scope: CoroutineScope)` con `val state: StateFlow<TransferState>`
  y `fun start(…)`
- Casos: un suscriptor ve todos los estados en orden si suspende entre ellos; un `StateFlow` conflado puede saltarse
  estados intermedios si se emiten sin suspender (demuéstralo); decide cómo garantizar que no se pierdan (un
  `SharedFlow` con replay o un historial en el estado).

**30 🟠 *Debounce* del estado.** Guardar el borrador de una transferencia 500 ms después de la última edición.
- Firma: `class DraftSaver(scope: CoroutineScope, form: StateFlow<TransferUiState>, save: suspend (TransferUiState) -> Unit)`
- Casos: escribir rápido 10 caracteres → un solo guardado; dos ráfagas separadas → dos guardados; cancelar el scope no
  guarda a medias.

**31 🟠 🎤 `stateIn` en un scope de larga vida.** La fuga de `Eagerly`.
- Firma: `class LeakyRepository(appScope: CoroutineScope)` con `val prices = feed.stateIn(appScope, Eagerly, …)` y su
  arreglo
- Casos: la versión con `Eagerly` mantiene la fuente activa sin nadie mirando (cuenta los ticks generados durante 1
  hora virtual); con `WhileSubscribed`, no. Explica cuándo `Eagerly` sí es correcto.

**32 🟠 Snapshots y deltas.** Un suscriptor nuevo recibe la foto completa del libro y luego solo los cambios.
- Firma: `class BookFeed(scope: CoroutineScope)` con `fun subscribe(): Flow<BookMessage>` (`Snapshot` o `Delta`)
- Casos: un suscriptor que llega tarde recibe primero el snapshot actual y luego los deltas sin huecos ni duplicados
  (numera los mensajes y compruébalo).

**33 🟠 Un `SharedFlow` por usuario.** Notificaciones privadas sobre un bus común.
- Firma: `class NotificationHub` con `fun notify(account: String, message: String)` y `fun stream(account: String): Flow<String>`
- Casos: cada cuenta solo recibe lo suyo; mil suscriptores no generan mil filtros sobre el bus (crea un flujo por cuenta
  y límpialo cuando no tiene suscriptores, con `subscriptionCount`).

**34 🟠 Estado consistente entre dos `StateFlow`.** Saldo y lista de movimientos que deben cuadrar siempre.
- Firma: `class Account` con un solo `MutableStateFlow<AccountState>` en vez de dos
- Casos: con dos `StateFlow` separados, un observador puede ver el saldo nuevo con la lista vieja (demuéstralo con
  `combine` y una emisión intermedia); con uno solo, nunca. Explica la regla.

**35 🟠 Pruebas de `WhileSubscribed` con Turbine.** La fuente que se detiene y se reanuda.
- Firma: pruebas sobre `SharedLatest` de la lección
- Casos: dos ciclos de suscripción con más de 5 s entre ellos dan dos arranques; con menos de 5 s, uno solo; el valor
  guardado persiste entre ciclos (o se reinicia con `replayExpiration`: pruébalo).

**36 🟠 `StateFlow` en Java.** Consumir el tablero desde una clase Java.
- Firma: una clase Java que se suscribe a `PriceBoard.price("ACME")` a través de un adaptador Kotlin (`callback` +
  `Closeable`)
- Casos: el callback recibe los cambios; `close()` cancela la suscripción; explica en un comentario por qué no se puede
  llamar a `collect` desde Java directamente.

## 🔴 Muy difíciles

**37 🔴 Un `StateFlow` propio.** Con `value`, `collect` conflado y `compareAndSet`, sin usar el de la librería.
- Firma: `class TinyStateFlow<T>(initial: T)` con `var value`, `fun compareAndSet(expect: T, update: T): Boolean` y
  `suspend fun collect(collector: suspend (T) -> Unit): Nothing`
- Casos: un colector lento ve el último valor, no todos; valores iguales no se emiten; 100 corrutinas con
  `compareAndSet` en bucle no pierden actualizaciones.

**38 🔴 Servidor de precios en vivo.** Un servidor TCP (o WebSocket de juguete con sockets) que publica el tablero a
clientes conectados.
- Firma: libre; cada cliente pide tickers y recibe sus precios
- Casos: diez clientes reciben lo suyo; un cliente lento no frena a los demás (política de descarte); al desconectarse,
  se libera su suscripción (cuéntalo).

**39 🔴 *Event sourcing* con `SharedFlow`.** El ledger del Banco como un flujo de eventos y vistas derivadas.
- Firma: `class EventStore(scope)` con `suspend fun append(event: LedgerEvent)` y vistas `balances`, `history` como
  `StateFlow`
- Casos: una vista nueva se reconstruye desde el principio (replay completo o un log aparte); las vistas siempre
  cuadran entre sí; 1 000 eventos concurrentes no dejan las vistas inconsistentes.

**40 🔴 Comparar con Reactor.** El tablero con `Sinks.many().multicast()` y `Sinks.one()` de Reactor frente a
`SharedFlow` y `StateFlow` (adelanto de `03-reactive`).
- Firma: dos implementaciones con la misma interfaz
- Casos: mismo comportamiento para el suscriptor tardío; diferencias en el manejo de un suscriptor lento (el sink falla
  con `FAIL_OVERFLOW`, el `SharedFlow` suspende). Tabla de diferencias en un comentario.

## ⚡ Saiyajin

**41 ⚡ Tablero de precios en vivo.** Un `StateFlow` por ticker alimentado por el feed y un `SharedFlow` de alertas.
- Firma: `class LivePriceBoard(scope: CoroutineScope, feed: Flow<Tick>)` con `fun price(ticker: String): StateFlow<Money?>`
  y `val alerts: SharedFlow<MoveAlert>`
- Reglas: el feed se abre solo mientras haya suscriptores (`WhileSubscribed(5000)`); un solo escritor por `StateFlow`;
  las alertas no se repiten para quien llega tarde; un suscriptor lento de alertas no frena los precios.
- **Se verifica con:** pruebas con Turbine en tiempo virtual: el suscriptor tardío recibe el último precio en el acto,
  las alertas no se repiten para quien llega tarde, y la fuente se detiene exactamente 5 s después del último
  suscriptor.
