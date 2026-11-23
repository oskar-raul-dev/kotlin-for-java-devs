# Ejercicios: `Mutex`, `Semaphore`, atómicos y el modelo de memoria

> **Lección:** `sync` · **Dónde se resuelven:** `sync/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../sync/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.sync.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable · `[exp 2.4]` = experimental en 2.4.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las carreras de datos solo se ven
> con hilos de verdad (`withContext(Dispatchers.Default)` dentro de `runBlocking`); las intercalaciones en puntos de
> suspensión y los deadlocks se reproducen de forma determinista en `runTest` con `yield()`. Una prueba que "a veces
> falla" se repite en un bucle hasta verla fallar o se rediseña. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 `Mutex` frente a `synchronized`.** El mismo contador con los dos.
- Firma: `class SyncCounter` (con `synchronized`) y `class MutexCounter2` (con `Mutex`), cada uno con `increment()` y
  `count`
- Casos: los dos dan 100 000 con 100 corrutinas × 1 000 en `Default`; dentro del `synchronized` no se puede llamar a
  `delay` (escribe en un comentario el error exacto del compilador: "The 'delay' suspension point is inside a critical
  section", medido en 2.4.21). Explica qué hace cada uno con el hilo mientras espera.

**02 🟢 `@Volatile` para una bandera.** Detener un trabajo desde otro hilo.
- Firma: `class StopFlag` con `@Volatile var stopped` y `fun runUntilStopped(): Long` (un bucle en un hilo)
- Casos: con `@Volatile`, el hilo ve el cambio y termina (dentro de un tiempo límite real); explica en un comentario
  por qué sin `@Volatile` el JIT podría dejar el bucle sin terminar (no siempre se reproduce).

**03 🟢 `AtomicLong` para un contador de órdenes.** El reemplazo directo de Java.
- Firma: `class OrderIds` con `fun next(): String` (`ORD-000001`, …)
- Casos: 100 corrutinas piden 1 000 ids cada una y no hay repetidos (un `Set` de 100 000).

**04 🟢 atomicfu.** El mismo contador con `atomic(0L)`.
- Firma: `class FuCounter` con `private val n = atomic(0L)`
- Casos: 100 000 exactos; explica en un comentario qué hace el plugin de Gradle con `atomic()` en la JVM y por qué
  atomicfu exige que el campo sea `private val` (el plugin lo reescribe).

**05 🟢 🎤 ¿Por qué `@Volatile` no basta para un contador?** Demostrarlo.
- Firma: `fun lostIncrements(threads: Int, each: Int): Long` (cuántos se perdieron con `@Volatile var count++`)
- Casos: con 8 hilos × 100 000, se pierden incrementos (repite hasta verlo, como la prueba de la lección). Explica en
  un comentario la diferencia entre visibilidad y atomicidad.

**06 🟢 `withLock` en vez de `lock`/`unlock`.** Liberar el lock aunque falle.
- Firma: `suspend fun guardedDebit(mutex: Mutex, balances: MutableMap<String, Money>, account: String, amount: Money)`
- Casos: un débito sin fondos lanza y el `Mutex` queda libre (`isLocked == false`); con `lock()`/`unlock()` a mano y
  sin `try/finally`, queda tomado (demuéstralo).

**07 🟢 `tryLock`.** No esperar si está ocupado.
- Firma: `fun tryReconcile(mutex: Mutex, work: () -> Unit): Boolean`
- Casos: libre → corre y devuelve `true`; tomado por otra corrutina → devuelve `false` sin esperar.

**08 🟢 `Semaphore` con `withPermit`.** Limitar las consultas al servicio de tasas.
- Firma: usa `LimitedFxClient` de la lección con 2 permisos
- Casos: 6 llamadas de 100 ms tardan 300 ms virtuales; el pico es 2; una llamada que falla devuelve su permiso.

**09 🟢 `ConcurrentHashMap.merge`.** Sumar volúmenes por ticker desde muchas corrutinas.
- Firma: `suspend fun volumes(executions: List<Execution>): Map<String, Long>` (con `merge(ticker, qty, Long::plus)`)
- Casos: el total coincide con la suma de las ejecuciones; con un `HashMap` común y hilos de verdad, el total puede
  salir mal (repite hasta verlo o explica por qué no lo viste).

**10 🟢 Estado inmutable compartido.** Publicar una tabla de tasas sin locks.
- Firma: `class RateTable` con `@Volatile private var rates: Map<String, BigDecimal>` y `fun publish(new: Map<…>)`
- Casos: los lectores ven siempre una tabla completa (nunca a medias); explica en un comentario por qué un mapa
  inmutable más una referencia `@Volatile` es seguro (publicación segura del JMM).

**11 🟢 `limitedParallelism(1)` como lock.** Confinar el estado de un contador de alertas.
- Firma: `class ConfinedAlerts(base: CoroutineDispatcher)` con `suspend fun record(ticker: String)` y `suspend fun counts()`
- Casos: 100 000 registros desde 100 corrutinas, exactos; sin `Mutex` ni atómicos.

**12 🟢 `Mutex` no reentrante.** Ver el cuelgue.
- Firma: `suspend fun reentrant(mutex: Mutex): Unit` que llama a sí misma bajo el lock una vez
- Casos: con `withTimeoutOrNull`, nunca termina; explica en un comentario cómo se reestructura el código (una función
  privada que asume el lock tomado) en vez de buscar un `Mutex` reentrante.

## 🟡 Medios

**13 🟡 🎤 Deadlock al transferir.** Reproducirlo y arreglarlo de dos formas.
- Firma: `class TwoLockBank` con `transferNaive` (origen y luego destino) y `transferOrdered` (por id)
- Casos: A→B y B→A a la vez cuelgan con la ingenua (en `runTest`, con `yield()` entre los dos locks, detectado con
  `withTimeoutOrNull`); terminan con la ordenada. Explica en un comentario las cuatro condiciones de Coffman y cuál
  rompe el orden.

**14 🟡 Locks con tiempo límite.** Otra forma de salir del deadlock: rendirse.
- Firma: `suspend fun transferWithTimeout(from: String, to: String, amount: Money, wait: Duration): Boolean` (si no
  consigue el segundo lock a tiempo, suelta el primero y devuelve `false`)
- Casos: con transferencias cruzadas, al menos una termina y la otra devuelve `false` sin colgar; reintentar con espera
  aleatoria termina las dos. Discute en un comentario por qué el orden fijo es mejor que el tiempo límite.

**15 🟡 Un `Mutex` por cuenta, creado al vuelo.** Las cuentas no se conocen de antemano.
- Firma: `class LockRegistry` con `fun lockFor(account: String): Mutex` sobre un `ConcurrentHashMap`
- Casos: dos corrutinas que piden el lock de la misma cuenta reciben **el mismo** `Mutex` (`computeIfAbsent`, no
  `getOrPut`: explica por qué aquí importa); 10 000 cuentas no dejan locks duplicados.

**16 🟡 Leer mucho, escribir poco.** Un `ReentrantReadWriteLock` para la tabla de tasas.
- Firma: `class RatesWithRwLock` con `fun read(pair: String): BigDecimal` y `fun write(pair: String, rate: BigDecimal)`
- Casos: varios lectores a la vez (cuéntalos), un escritor solo; explica en un comentario por qué este lock no sirve
  dentro de una corrutina que suspende (es de hilos) y qué alternativa hay (un `StateFlow` de mapa inmutable).

**17 🟡 Semáforo justo.** Que nadie espere para siempre.
- Firma: `suspend fun fairness(permits: Int, waiters: Int): List<Int>` (orden en que consiguieron el permiso)
- Casos: el `Semaphore` de kotlinx atiende en orden de llegada (FIFO): compruébalo en `runTest`; compáralo en un
  comentario con `java.util.concurrent.Semaphore(n, fair = true)`.

**18 🟡 `AtomicReference` para un estado compuesto.** El último precio y su instante, siempre juntos.
- Firma: `class LastQuote` con `AtomicReference<Pair<Money, Long>>` y `fun offer(price: Money, at: Long)` que solo
  acepta instantes más nuevos (`updateAndGet` o bucle con `compareAndSet`)
- Casos: con 100 corrutinas ofreciendo en desorden, queda el de mayor instante; nunca un precio con el instante de
  otro.

**19 🟡 El ABA del `compareAndSet`.** Cuándo un CAS no alcanza.
- Firma: una pila sin locks de órdenes pendientes con `AtomicReference<Node?>`, y la prueba que muestra el problema ABA
  (con un nodo reutilizado)
- Casos: describe el escenario paso a paso en un comentario y una versión que lo evita (`AtomicStampedReference` o no
  reutilizar nodos).

**20 🟡 🎤 Confinar en vez de bloquear.** El libro de órdenes de `channels` con `limitedParallelism(1)` en vez de un
actor.
- Firma: `class ConfinedBook(base: CoroutineDispatcher)` con `suspend fun place(order: LimitOrder): List<Execution>`
- Casos: 100 corrutinas, libro nunca cruzado, cantidad conservada; explica en un comentario la trampa de la lección (un
  punto de suspensión dentro de la sección crítica rompe el confinamiento) y por qué `place` no debe suspender por
  dentro.

**21 🟡 Intercalación en un solo hilo.** El doble gasto sin paralelismo.
- Firma: `suspend fun doubleSpend(withMutex: Boolean): Money` (saldo final de una cuenta de 100 después de dos retiros
  de 100 concurrentes, con un `yield()` entre validar y escribir)
- Casos: en `runTest` (un hilo), sin `Mutex` queda en −100; con `Mutex` alrededor de todo, en 0 y el segundo retiro
  falla. Explica por qué no hace falta un segundo hilo para tener una carrera lógica.

**22 🟡 `Mutex` con dueño.** Detectar quién lo tiene.
- Firma: `suspend fun ownerCheck(): Boolean` con `mutex.lock(owner)` y `mutex.holdsLock(owner)`
- Casos: `holdsLock` es `true` para el dueño y `false` para otro; `unlock(otro)` lanza `IllegalStateException`;
  intentar `lock(owner)` dos veces con el mismo dueño también lanza (en vez de colgarse): úsalo para detectar la
  reentrada.

**23 🟡 Contadores por cuenta sin lock global.** *Striping*: N locks para M cuentas.
- Firma: `class StripedBank(stripes: Int)` donde la cuenta va al lock `abs(hash) % stripes`
- Casos: dos cuentas del mismo *stripe* se serializan; de distintos, en paralelo; transferencias entre stripes toman
  los dos en orden de índice (sin deadlock).

**24 🟡 `ThreadLocal` y corrutinas.** Por qué un lock de hilos y una corrutina no se llevan.
- Firma: `suspend fun reentrantLockAcrossSuspension(): Throwable?` (toma un `ReentrantLock`, hace `delay` en otro
  dispatcher, intenta `unlock`)
- Casos: si la corrutina vuelve en otro hilo, `unlock` lanza `IllegalMonitorStateException` (el lock es del hilo, no de
  la corrutina); con `Mutex`, no pasa.

**25 🟡 `@Synchronized` en Kotlin.** La anotación que reemplaza a la palabra clave de Java.
- Firma: `class LegacyLedger` con `@Synchronized fun post(entry: String)` y `fun entries(): List<String>`
- Casos: 100 hilos de plataforma registran 1 000 cada uno sin perder; explica en un comentario que `@Synchronized` en
  una función `suspend` sincroniza solo hasta el primer punto de suspensión (por qué es una trampa).

**26 🟡 `Semaphore` como límite de memoria.** No procesar más de N lotes de ticks a la vez.
- Firma: `suspend fun processBatches(batches: List<List<Tick>>, maxInFlight: Int, work: suspend (List<Tick>) -> Unit)`
- Casos: con 100 lotes y límite 4, nunca hay más de 4 en curso; el tiempo total en tiempo virtual es el esperado;
  una falla cancela el resto y libera los permisos.

## 🟠 Difíciles

**27 🟠 🎤 Transferencias concurrentes, tres diseños.** `Mutex` por cuenta, actor y confinamiento, con la misma batería
de pruebas.
- Firma: tres implementaciones de `Bank` de la lección
- Casos: las tres conservan el total con 10 000 transferencias desde 100 corrutinas; mide el tiempo de cada una con
  calentamiento y anótalo. Explica cuál elegirías en un servicio con base de datos (pista: ninguno de los tres; el lock
  va en la base, como en `04-data/jdbc`).

**28 🟠 Detector de deadlocks.** Un grafo de espera entre corrutinas.
- Firma: `class TrackedMutex(name: String)` que registra quién tiene y quién espera, y `fun detectCycle(): List<String>?`
- Casos: con A→B y B→A colgadas, detecta el ciclo `[A, B]`; sin ciclo, `null`; el registro no altera el
  comportamiento del lock.

**29 🟠 Lote con un solo commit.** Aplicar 1 000 asientos con un lock por lote y no por asiento.
- Firma: `class BatchingLedger(scope: CoroutineScope, maxBatch: Int, maxWait: Duration)` con `suspend fun post(entry: Posting)`
- Casos: 1 000 asientos concurrentes se aplican en pocos lotes (cuéntalos) con el mismo resultado que uno por uno;
  cada `post` vuelve cuando su lote se aplicó; la latencia máxima es `maxWait`.

**30 🟠 El JMM en un ejemplo.** *Double-checked locking* bien y mal.
- Firma: un `lazy` hecho a mano en dos versiones (con y sin `@Volatile`), y `by lazy` con sus tres modos
  (`SYNCHRONIZED`, `PUBLICATION`, `NONE`)
- Casos: 100 hilos inicializan una sola vez con `SYNCHRONIZED`; con `PUBLICATION` el inicializador puede correr varias
  veces pero queda un solo valor (cuéntalo); explica en un comentario por qué la versión sin `@Volatile` está rota
  aunque casi nunca falle.

**31 🟠 🎤 *Lock-free* frente a lock.** Un contador de alto tráfico con `AtomicLong`, `LongAdder` y `Mutex`.
- Firma: `fun throughput(counter: String, threads: Int, ops: Int): Duration`
- Casos: tabla con 1, 4 y 16 hilos y calentamiento; explica en un comentario por qué `LongAdder` gana con mucha
  competencia y qué pierde (`sum()` no es instantáneo).

**32 🟠 Idempotencia concurrente.** Dos peticiones con la misma clave de idempotencia al mismo tiempo.
- Firma: `class IdempotentTransfers(bank: Bank)` con `suspend fun transfer(key: String, from: String, to: String, amount: Money): String`
- Casos: 10 corrutinas con la misma clave → una sola transferencia y todas reciben el mismo recibo (un
  `CompletableDeferred` por clave en un `ConcurrentHashMap` con `computeIfAbsent`); claves distintas no se bloquean
  entre sí.

**33 🟠 Reserva de fondos.** Reservar, confirmar o liberar, sin que dos reservas usen el mismo saldo.
- Firma: `class Reservations` con `suspend fun reserve(account: String, amount: Money): String?`,
  `suspend fun confirm(id: String)` y `suspend fun release(id: String)`
- Casos: 100 reservas concurrentes de 10 sobre 500 → exactamente 50 aceptadas; liberar devuelve el saldo disponible;
  confirmar dos veces no descuenta dos veces.

**34 🟠 `Mutex` y cancelación.** Que cancelar no deje el lock tomado ni el estado a medias.
- Firma: `suspend fun cancellableTransfer(bank: OrderedLockBank, …)` cancelada mientras espera el lock y mientras lo tiene
- Casos: cancelada esperando → nunca tomó el lock; cancelada adentro → `withLock` lo suelta y el estado no queda a
  medias (diseña la transferencia para que la parte crítica no suspenda o compense).

**35 🟠 Visibilidad sin `@Volatile`.** Un experimento con hilos de plataforma y el JIT.
- Firma: `fun spinWithoutVolatile(): Boolean` (¿terminó el bucle en 2 s?)
- Casos: documenta en un comentario lo que observaste en tu máquina y JDK, con y sin `@Volatile`, y por qué el
  resultado sin `@Volatile` no está garantizado en ningún sentido (puede terminar o no). La prueba solo comprueba la
  versión con `@Volatile`.

**36 🟠 jcstress (opcional).** Probar una carrera con la herramienta de OpenJDK.
- Firma: un test de jcstress (proyecto aparte o plugin de Gradle) sobre el contador `@Volatile` de la lección
- Casos: los resultados muestran estados "interesantes" (incrementos perdidos); explica en un comentario por qué una
  prueba unitaria no es la herramienta para esto.

## 🔴 Muy difíciles

**37 🔴 Un `Mutex` propio.** Con una cola de continuaciones y un atómico, sin usar el de kotlinx.
- Firma: `class TinyMutex` con `suspend fun lock()`, `fun unlock()` y `suspend fun <T> withLock(block: suspend () -> T): T`
- Casos: 100 corrutinas × 1 000 incrementos exactos; orden FIFO de espera; una corrutina cancelada mientras espera
  sale de la cola y no se queda con el lock.

**38 🔴 Un `Semaphore` propio.** Igual que el anterior, con N permisos.
- Firma: `class TinySemaphore(permits: Int)` con `acquire`, `release` y `withPermit`
- Casos: el pico nunca pasa de N; `release` de más lanza; la cancelación de quien espera no pierde permisos.

**39 🔴 STM de juguete.** Transacciones optimistas sobre varias cuentas con reintento.
- Firma: `class TVar<T>(initial: T)` y `suspend fun <R> atomically(block: Tx.() -> R): R` (lee versiones, valida y
  confirma, o reintenta)
- Casos: transferencias concurrentes conservan el total; una transacción que lee un valor cambiado reintenta (cuenta
  los reintentos); sin deadlocks posibles (explica por qué).

**40 🔴 El ledger de `04-data` con concurrencia en la base.** Comparar locks en memoria con `SELECT … FOR UPDATE`.
- Firma: un proyecto propio (o en `practice/` con H2 en memoria agregado a tus pruebas) que implementa la transferencia
  con bloqueo pesimista en la base y la llama desde 100 corrutinas sobre `Dispatchers.IO.limitedParallelism(pool)`
- Casos: total conservado, sin deadlocks (orden por id también en la base); explica en un comentario por qué con varias
  instancias del servicio los locks en memoria no sirven.

## ⚡ Saiyajin

**41 ⚡ Transferencias concurrentes sin perder saldo.** 10 000 transferencias aleatorias desde 100 corrutinas en
`Dispatchers.Default` sobre 50 cuentas.
- Firma: `class ConcurrentBank(accounts: Map<String, Money>)` con `suspend fun transfer(from: String, to: String, amount: Money)`
  y una variante `class ActorBank` con el mismo estado confinado a un actor (`channels`)
- Reglas: un `Mutex` por cuenta tomados en orden de id; ninguna transferencia deja un saldo negativo (se rechaza); la
  parte crítica no suspende fuera del lock.
- **Se verifica con:** una prueba de que el total de dinero se conserva y ningún saldo queda negativo, otra con
  transferencias cruzadas A→B y B→A que termina dentro de un `withTimeout` (sin deadlock), y una variante con el mismo
  estado confinado a un actor que da el mismo resultado.
