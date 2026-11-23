# Ejercicios: dispatchers, `withContext` y llamadas bloqueantes

> **Lección:** `dispatchers` · **Dónde se resuelven:** `dispatchers/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../dispatchers/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.dispatchers.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Aquí muchas pruebas miden hilos
> de verdad, así que van con `runBlocking` y latencias chicas (10–30 ms); cuando el enunciado pide tiempo virtual, el
> dispatcher se inyecta y la prueba pasa un `StandardTestDispatcher(testScheduler)`. En las pruebas, el nombre del
> hilo trae el sufijo ` @coroutine#N` (modo debug, activo con `-ea`): compara con `threadName()` de la lección. El ⚡
> es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 26, 31.

## 🟢 Fáciles

**01 🟢 🎤 ¿`Default` o `IO`?** Clasificar tareas del Banco y del Mercado.
- Firma: `enum class Work { CPU, BLOCKING_IO, SUSPENDING }` y `fun dispatcherFor(work: Work): CoroutineDispatcher`
- Casos: velas (CPU) → `Default`; JDBC (bloqueante) → `IO`; una llamada `suspend` a un cliente HTTP no bloqueante →
  no hace falta cambiar (decide qué devuelves y explícalo). Explica en un comentario cuántos hilos tiene cada uno y
  por qué comparten los mismos.

**02 🟢 `withContext` devuelve el resultado.** Leer un archivo del ledger fuera del hilo de quien llama.
- Firma: `suspend fun readLedger(path: Path): List<String>` con `withContext(Dispatchers.IO)`
- Casos: devuelve las líneas; el bloque corre en un hilo `DefaultDispatcher-worker-…` y al volver se sigue en el hilo
  original (compruébalo dentro de `runBlocking`).

**03 🟢 Nombre de hilo.** En qué hilo corre cada dispatcher.
- Firma: `suspend fun threadsOf(dispatchers: Map<String, CoroutineDispatcher>): Map<String, String>`
- Casos: `Default` e `IO` dan `DefaultDispatcher-worker-N`; `Unconfined` da el hilo de quien llama; uno de hilos
  virtuales da un nombre vacío. Explica en un comentario por qué `IO` y `Default` muestran el mismo prefijo.

**04 🟢 `limitedParallelism` con nombre.** Un dispatcher para la base y otro para el servicio de tasas.
- Firma: `val db: CoroutineDispatcher` y `val fx: CoroutineDispatcher` sobre `Dispatchers.IO`, con nombre
- Casos: `toString()` da el nombre; el hilo no lo lleva (medido en 1.11.0: sigue siendo `DefaultDispatcher-worker-N`);
  los dos comparten hilos con `IO` pero cada uno respeta su límite.

**05 🟢 🎤 JDBC desde `Default`.** Demostrar el daño.
- Firma: `fun blockingOnDefault(calls: Int): Duration` con `Thread.sleep(20)` en `Default`
- Casos: con tantas llamadas como núcleos más una, el tiempo es de dos tandas; mientras tanto, una tarea de CPU chica
  lanzada en `Default` espera a que se libere un núcleo (mide su latencia). Explica en un comentario qué pasa en un
  servidor.

**06 🟢 Contar llamadas simultáneas.** Usar `ConcurrencyMeter` de la lección con un repositorio propio.
- Firma: `class SlowRatesRepository(meter: ConcurrencyMeter)` con `fun rate(pair: String): BigDecimal` (bloquea 10 ms)
- Casos: 50 llamadas sobre `limitedParallelism(5)` → máximo 5; sobre `IO` → más de 5.

**07 🟢 Inyectar el dispatcher.** Un servicio que no fija su dispatcher.
- Firma: `class CandleService(private val cpu: CoroutineDispatcher = Dispatchers.Default)`
- Casos: en producción usa `Default`; en la prueba, `StandardTestDispatcher(testScheduler)` y todo queda en tiempo
  virtual. Explica en un comentario por qué un `Dispatchers.Default` fijo dentro de la clase complica las pruebas.

**08 🟢 Hilos virtuales como dispatcher.** Cerrar lo que se abre.
- Firma: `fun <T> withVirtualThreads(block: suspend CoroutineScope.() -> T): T`
- Casos: el bloque corre en hilos virtuales (`Thread.currentThread().isVirtual`); el dispatcher queda cerrado al
  salir (un `launch` posterior sobre él falla con `RejectedExecutionException`).

**09 🟢 `newSingleThreadContext` y su cierre.** El hilo que no deja terminar la JVM.
- Firma: `fun singleThreadName(): String` con `newSingleThreadContext("ledger")` dentro de `use`
- Casos: el hilo se llama `ledger`; pide `@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)`
  (medido en 1.11.0); explica en un comentario qué pasa si no se cierra y por qué `limitedParallelism(1)` suele ser
  mejor.

**10 🟢 `Unconfined`.** Predecir el hilo antes y después de suspender.
- Firma: `suspend fun unconfinedTrace(resumeOn: CoroutineDispatcher): List<String>`
- Casos: antes del primer punto de suspensión, el hilo de quien llama; después de un `withContext(resumeOn)` interno,
  el hilo de `resumeOn`, y sigue ahí al volver. Explica en un comentario por qué no se usa en código de aplicación.

**11 🟢 `asCoroutineDispatcher` de un executor propio.** Un pool fijo de Java como dispatcher.
- Firma: `fun fixedPoolDispatcher(n: Int): ExecutorCoroutineDispatcher`
- Casos: nunca corre más de n a la vez; los hilos se llaman como diga tu `ThreadFactory`; hay que cerrarlo. Compáralo
  en un comentario con `limitedParallelism(n)`.

**12 🟢 Dónde vuelve `withContext`.** El hilo después de volver.
- Firma: `suspend fun hopAndReturn(log: MutableList<String>)` que anota el hilo antes, dentro y después de
  `withContext(Dispatchers.IO)`, corriendo en `limitedParallelism(1)` de `Default`
- Casos: antes y después son del dispatcher original (no necesariamente el mismo hilo físico: explícalo); adentro, uno
  de `IO`.

## 🟡 Medios

**13 🟡 🎤 Limitar a N las llamadas a la base.** El dispatcher del tamaño del pool de conexiones.
- Firma: `class LedgerGateway(poolSize: Int, repository: BlockingLedgerRepository)` con
  `suspend fun balances(accounts: List<String>): List<Money>`
- Casos: con pool 10 y 200 cuentas, el máximo simultáneo es 10 y el tiempo ≈ 20 tandas; con pool 0 →
  `IllegalArgumentException`. Explica en un comentario por qué el límite tiene que coincidir con el pool (y qué pasa
  si es mayor).

**14 🟡 Dos límites a la vez.** La base aguanta 10 y el servicio de tasas 3.
- Firma: `class TransferPreparer(db: CoroutineDispatcher, fx: CoroutineDispatcher)` con
  `suspend fun prepare(accounts: List<String>): List<Pair<Money, BigDecimal>>`
- Casos: con 50 cuentas, cada recurso respeta su límite (dos medidores); los dos trabajan en paralelo entre sí.

**15 🟡 `limitedParallelism` sobre `Default` frente a sobre `IO`.** La vista elástica.
- Firma: `fun peakThreads(base: CoroutineDispatcher, limit: Int, calls: Int): Int` (hilos distintos usados)
- Casos: `IO.limitedParallelism(100)` llega a más de 64 llamadas a la vez (puede sumar hilos); `Default.limitedParallelism(100)`
  nunca pasa de los núcleos (y su `toString()` es `Dispatchers.Default`: con un límite mayor que el suyo devuelve el
  mismo dispatcher, medido en 1.11.0). Explica en un comentario la diferencia documentada.

**16 🟡 El MDC que se pierde.** Un id de correlación en cada línea de log.
- Firma: `class RequestLog` con un `ThreadLocal<String?>` y `suspend fun handle(requestId: String, work: suspend () -> Unit): List<String>`
  (las líneas registradas, cada una con el id)
- Casos: sin propagar, las líneas escritas después de un `withContext(IO)` salen sin id; con `asContextElement`, todas
  lo llevan; el id no queda en el hilo al terminar.

**17 🟡 Un contexto propio.** Un elemento de `CoroutineContext` para el id de correlación, sin `ThreadLocal`.
- Firma: `class CorrelationId(val value: String) : AbstractCoroutineContextElement(Key)` y
  `suspend fun currentCorrelationId(): String?`
- Casos: viaja por `withContext`, `launch` y `async`; un hijo puede reemplazarlo sin cambiar el del padre; explica en
  un comentario cuándo hace falta además `ThreadContextElement` (librerías que leen un `ThreadLocal`).

**18 🟡 Velas en paralelo por ticker.** Calcular las velas de cada ticker en una corrutina de `Default`.
- Firma: `suspend fun candlesByTicker(ticks: List<Tick>, cpu: CoroutineDispatcher): Map<String, List<Candle>>`
- Casos: el mismo resultado que `candles` de la lección agrupado por ticker; en la prueba con un dispatcher de prueba
  y en otra con `Default` real; ticks vacíos → mapa vacío.

**19 🟡 Cálculo pesado cancelable en `Default`.** Velas sobre un millón de ticks que se puede cancelar.
- Firma: `suspend fun heavyCandles(ticks: List<Tick>, cpu: CoroutineDispatcher): List<Candle>` con `ensureActive`
  cada 10 000 ticks
- Casos: termina con el resultado correcto; cancelada a los 5 ms reales, termina en menos de 50 ms (sin
  `ensureActive`, tarda el cálculo entero: demuéstralo).

**20 🟡 🎤 `IO` no es infinito.** Saturar `Dispatchers.IO` con llamadas bloqueantes.
- Firma: `fun saturateIo(calls: Int, latencyMillis: Long): Pair<Int, Duration>` (pico y tiempo)
- Casos: con 640 llamadas de 30 ms, el pico es 64 (o los núcleos, si son más) y el tiempo ≈ 10 tandas; explica en
  un comentario qué le pasa a otra parte de la aplicación que también usa `IO` en ese momento y cómo lo evita
  `limitedParallelism`.

**21 🟡 Aislar un vecino ruidoso.** Un reporte lento no debe frenar las transferencias.
- Firma: `class Isolation` con `val reports: CoroutineDispatcher` y `val transfers: CoroutineDispatcher`
- Casos: 100 reportes bloqueantes en curso y una transferencia que llega: con dispatchers separados sobre `IO`, la
  transferencia empieza enseguida; con todo en un solo `limitedParallelism(4)`, espera (mide su latencia en los dos).

**22 🟡 `runInterruptible`.** Cancelar una llamada bloqueante de verdad.
- Firma: `suspend fun cancellableBalance(repository: BlockingLedgerRepository, account: String): Money`
- Casos: cancelada durante el `sleep`, el hilo se interrumpe y la corrutina termina enseguida (mídelo); con
  `withContext(IO)` común, espera a que termine el `sleep`.

**23 🟡 El dispatcher de la prueba.** El mismo servicio probado en tiempo real y en tiempo virtual.
- Firma: `class RatePoller(scope: CoroutineScope, fx: FxRateClient, every: Duration, io: CoroutineDispatcher)`
- Casos: con el dispatcher de prueba, 1 hora virtual da 60 refrescos en milisegundos; con `Dispatchers.IO` fijo
  dentro de la clase (versión mala), la prueba no puede avanzar el tiempo: demuéstralo y explica.

**24 🟡 Hilos de plataforma frente a virtuales en `limitedParallelism`.** El límite también sirve sobre hilos virtuales.
- Firma: `fun peakOnVirtual(limit: Int, calls: Int): Int`
- Casos: `virtualThreadDispatcher().limitedParallelism(20)` nunca pasa de 20; sin límite, todas a la vez.

**25 🟡 `Dispatchers.Main` sin Android.** Qué pasa en la JVM.
- Firma: `suspend fun tryMain(): String`
- Casos: en la JVM sin `kotlinx-coroutines-swing`/`-javafx`/`-android`, usar `Dispatchers.Main` lanza
  `IllegalStateException` (mensaje medido en tu prueba); con `Dispatchers.setMain(StandardTestDispatcher())` de
  kotlinx-coroutines-test, funciona (y se limpia con `resetMain`). Se retoma en `12-android`.

**26 🟡 🎤 Dónde poner el `withContext`.** En quien llama o en quien bloquea.
- Firma: dos versiones del mismo repositorio: `BadRepository` (bloquea; cada llamador pone `withContext`) y
  `GoodRepository` (la función `suspend` hace su propio `withContext(io)`)
- Casos: con la mala, un llamador que se olvida bloquea `Default` (demuéstralo); con la buena, es imposible olvidarse.
  Explica en un comentario la convención "las funciones `suspend` no bloquean a quien las llama" (*main-safety*).

## 🟠 Difíciles

**27 🟠 Pool de conexiones y dispatcher alineados.** Un pool de conexiones simulado con N conexiones y un dispatcher
de N.
- Firma: `class FakeConnectionPool(size: Int)` con `fun <T> withConnection(block: (Int) -> T): T` (bloquea si no hay
  libres) y `class PooledLedger(pool: FakeConnectionPool, db: CoroutineDispatcher)`
- Casos: con dispatcher = pool, ningún hilo espera conexión (mide el tiempo de espera); con dispatcher = 2 × pool, la
  mitad de los hilos queda bloqueada esperando conexión; con dispatcher < pool, sobran conexiones.

**28 🟠 Dispatcher con prioridades.** Las transferencias antes que los reportes, sobre el mismo límite.
- Firma: `class PriorityDispatcher(parallelism: Int) : CoroutineDispatcher` (con una `PriorityBlockingQueue` y
  `dispatch` propio) y `fun withPriority(p: Int): CoroutineContext`
- Casos: con 100 reportes en cola, una transferencia de prioridad alta se ejecuta antes que los pendientes; el
  paralelismo nunca pasa del límite.

**29 🟠 Propagar el contexto a un `Executor` de Java.** Una librería Java que recibe un `Executor` y llama de vuelta.
- Firma: `suspend fun <T> callJavaLibrary(lib: JavaQuoteLibrary): T` (la librería, escrita por ti, usa el
  `Executor` que le pasan) usando `coroutineContext[ContinuationInterceptor]`… o `asExecutor()`
- Casos: el callback corre en el dispatcher de la corrutina; el id de correlación (como `ThreadContextElement`) llega
  al callback.

**30 🟠 Medir el costo de cambiar de hilo.** Cuánto cuesta un `withContext`.
- Firma: `fun switchCost(iterations: Int, target: CoroutineDispatcher): Duration`
- Casos: `withContext` al mismo dispatcher (no cambia de hilo: se nota en el tiempo) frente a otro dispatcher, con
  calentamiento; los números van en una tabla en un comentario.

**31 🟠 🎤 Un servicio *main-safe* completo.** Repositorio, servicio y controlador donde ninguna función `suspend`
bloquea a quien llama.
- Firma: tres capas; solo el repositorio sabe que hay JDBC
- Casos: una prueba que corre todo el flujo desde un dispatcher de un solo hilo y comprueba que ese hilo nunca quedó
  bloqueado más de 5 ms (con un *watchdog* que mide); el mismo flujo con un olvido demuestra el bloqueo. Explica la
  regla en un comentario.

**32 🟠 *Starvation* de `Default`.** Tareas de CPU largas que no dejan correr a las cortas.
- Firma: `fun starvation(longTasks: Int, shortTasks: Int, cooperative: Boolean): Duration` (latencia p99 de las cortas)
- Casos: con tantas tareas largas como núcleos y sin `yield`, las cortas esperan a que terminen; con `yield()`
  periódico, entran antes. Tabla en un comentario.

**33 🟠 Un `ThreadContextElement` propio.** El MDC hecho a mano.
- Firma: `class MdcElement(val values: Map<String, String>) : ThreadContextElement<Map<String, String>?>`
- Casos: el `ThreadLocal` tiene los valores mientras la corrutina corre en cualquier hilo y vuelve a su valor
  anterior al suspender; dos corrutinas en el mismo hilo no se mezclan los valores.

**34 🟠 Contrapresión con dispatchers.** Un productor rápido en `Default` y un consumidor bloqueante en `db(2)`.
- Firma: `suspend fun ingest(items: List<String>, db: CoroutineDispatcher, sink: BlockingSink): Int`
- Casos: sin límite, se lanzan todas las corrutinas a la vez y quedan esperando (cuenta las suspendidas: muchas); con
  un límite de en vuelo (lote o `Semaphore`), nunca hay más de N esperando. Explica qué se aprende para `flow` y
  `channels`.

**35 🟠 Dispatcher de prueba compartido.** Un servicio con dos dispatchers inyectados, ambos virtuales en la prueba.
- Firma: `class Settlement(io: CoroutineDispatcher, cpu: CoroutineDispatcher)` con `suspend fun settleDay(): Report`
- Casos: en la prueba los dos son `StandardTestDispatcher(testScheduler)` del mismo planificador y los tiempos
  virtuales cuadran; con planificadores distintos, la prueba se cuelga o falla (demuéstralo y explica).

**36 🟠 Hilos virtuales y `synchronized`.** El *pinning* visto desde un dispatcher (adelanto de `loom`).
- Firma: `fun pinnedPeak(calls: Int, useLock: Boolean): Int` con `Thread.sleep` dentro de `synchronized` o de un
  `ReentrantLock`, sobre el dispatcher de hilos virtuales
- Casos: con `synchronized` en JDK 21, el paralelismo efectivo cae al número de hilos portadores; con
  `ReentrantLock`, no. Anota en un comentario la versión de JDK (JEP 491 lo arregló en 24).

## 🔴 Muy difíciles

**37 🔴 Un dispatcher propio desde cero.** Un `CoroutineDispatcher` con N hilos de plataforma y una cola.
- Firma: `class TinyDispatcher(threads: Int, name: String) : CoroutineDispatcher(), AutoCloseable`
- Casos: corre las corrutinas en sus hilos (nombres `name-1`…); `limitedParallelism` funciona sobre él; `close()`
  termina los hilos y rechaza lo nuevo; pasa las pruebas de concurrencia de la lección.

**38 🔴 Ajuste automático del límite.** Un dispatcher que ajusta su paralelismo según la latencia de la base (estilo
*adaptive concurrency*).
- Firma: `class AdaptiveDb(min: Int, max: Int, target: Duration)` con `suspend fun <T> call(block: () -> T): T`
- Casos: si la latencia supera el objetivo, baja el límite; si sobra, lo sube; nunca sale de [min, max]; una prueba
  con una base simulada que se degrada a mitad de la corrida.

**39 🔴 Benchmark con JMH o hecho a mano.** El costo de `withContext(IO)` frente a llamar directo, con calentamiento
y repeticiones.
- Firma: un benchmark (JMH con su plugin de Gradle, o un arnés propio con calentamiento, 10 repeticiones y mediana)
- Casos: tabla de resultados con intervalo; explica en un comentario por qué un `measureTime` de una corrida no sirve.

**40 🔴 Contexto que cruza a Java y vuelve.** Una corrutina llama a una librería Java asíncrona (con
`CompletableFuture` y su propio pool), que llama a un callback que vuelve a lanzar una corrutina.
- Firma: libre, con el id de correlación y la cancelación como requisitos
- Casos: el id llega a la corrutina del callback; cancelar la corrutina original cancela el future de Java (y su
  tarea); ningún hilo queda vivo al terminar (cuéntalos con `Thread.getAllStackTraces`).

## ⚡ Saiyajin

**41 ⚡ Dimensionar dispatchers.** 1 000 llamadas bloqueantes simuladas (10 ms cada una) sobre `Default`, `IO`,
`limitedParallelism(10)` y un dispatcher de hilos virtuales.
- Firma: `fun sizeDispatchers(calls: Int, latencyMillis: Long, warmup: Int, runs: Int): List<DispatcherReport>` con
  `data class DispatcherReport(val name: String, val median: Duration, val peakConcurrency: Int)`
- Reglas: cada variante con calentamiento y varias corridas (mediana); el pico se mide con un contador de llamadas
  simultáneas como `ConcurrencyMeter`; los dispatchers que se crean se cierran.
- **Se verifica con:** una prueba con un contador de llamadas simultáneas que demuestra que `limitedParallelism(10)`
  nunca pasa de 10, y la tabla de tiempos de cada variante guardada junto a la prueba (con calentamiento).
