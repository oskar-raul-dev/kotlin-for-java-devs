# Ejercicios: cancelación, tiempos límite y supervisión

> **Lección:** `cancellation` · **Dónde se resuelven:** `cancellation/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../cancellation/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.cancellation.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Las pruebas usan `runTest`
> (tiempo virtual); `currentTime`, `advanceTimeBy` y `runCurrent` piden `@OptIn(ExperimentalCoroutinesApi::class)` en
> kotlinx-coroutines-test 1.11.0. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 20, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 Cancelar y esperar.** Un sondeo de precios que se cancela desde afuera.
- Firma: `fun CoroutineScope.startPolling(source: suspend () -> Money, every: Duration, into: MutableList<Money>): Job`
- Casos: después de 3.5 s virtuales hay 3 precios (o 4, según si el primero es inmediato: decide y documéntalo);
  `cancelAndJoin()` y luego 10 s más: no llega ninguno. Explica en un comentario por qué la cancelación es
  cooperativa y qué la hace funcionar aquí sin escribir nada (cada `delay` la comprueba).

**02 🟢 `isActive` en un bucle de cálculo.** Calcular el VWAP de una lista enorme de ticks, cancelable.
- Firma: `suspend fun vwap(ticks: List<Tick>, volumes: List<Long>): BigDecimal` que mira `isActive` cada 1 000
  elementos (con `currentCoroutineContext().isActive`)
- Casos: el VWAP de una lista chica; cancelada a mitad (desde un gancho, como `processBatch`), deja de calcular antes
  del final. Diferencia en un comentario `isActive` (devuelve `false`) de `ensureActive()` (lanza).

**03 🟢 `ensureActive` frente a `yield`.** Las dos formas de hacer cooperativo un bucle.
- Firma: `suspend fun countWithEnsureActive(n: Int, onStep: (Int) -> Unit): Int` y
  `suspend fun countWithYield(n: Int, onStep: (Int) -> Unit): Int`
- Casos: las dos se detienen al cancelar; `yield` además deja correr a otra corrutina del mismo hilo en cada paso (dos
  corrutinas en `runTest` intercalan sus pasos con `yield` y no con `ensureActive`).

**04 🟢 `withTimeoutOrNull`.** El último precio o `null` si la fuente no contesta.
- Firma: `suspend fun lastPriceOrNull(source: suspend () -> Money, limit: Duration): Money?`
- Casos: contesta a tiempo; no contesta → `null` en el instante exacto del límite; límite cero → `null` sin llamar.

**05 🟢 🎤 `CancellationException` no es un error.** Lo que ve quien espera a una corrutina cancelada.
- Firma: `suspend fun outcomeOf(job: Job): String` (`"completado"`, `"cancelado"` o `"falló: <mensaje>"`, con
  `invokeOnCompletion` o mirando `isCancelled` después de `join`)
- Casos: los tres resultados; explica en un comentario por qué cancelar no se trata como falla (el padre no se
  cancela cuando un hijo es cancelado) y qué pasa si atrapas `CancellationException` y no la relanzas.

**06 🟢 `finally` corre al cancelar.** Liberar un recurso cuando la corrutina se cancela.
- Firma: `suspend fun useConnection(log: MutableList<String>)` con `try { delay(10.seconds) } finally { log += "cerrada" }`
- Casos: cancelada a los 100 ms, el log dice `"cerrada"`; terminada normalmente, también.

**07 🟢 `NonCancellable` en el `finally`.** El cierre que necesita suspender.
- Firma: `suspend fun closeRemote(log: MutableList<String>, nonCancellable: Boolean)` con un `delay(50)` en el
  `finally` antes de anotar `"cerrada"`
- Casos: cancelada, con `NonCancellable` anota `"cerrada"` 50 ms después; sin él, no anota nada.

**08 🟢 `cancel` con causa.** Cancelar con un mensaje que explique por qué.
- Firma: `suspend fun cancelReason(): String?` que cancela un `Job` con `job.cancel("mercado cerrado")` y lee el mensaje
  con `invokeOnCompletion`
- Casos: el mensaje llega; la causa es una `CancellationException`.

**09 🟢 `withTimeout` en el instante exacto.** Probar que el límite salta justo a tiempo.
- Firma: `suspend fun quoteOrTimeout(latency: Duration, limit: Duration): Money`
- Casos: latencia 99 y límite 100 → precio; latencia 100 y límite 100 → `TimeoutCancellationException` (¿o precio?
  pruébalo y explica qué pasa en el empate); `currentTime` igual al límite al fallar.

**10 🟢 `runCatching` y la cancelación.** El error más común al portar código.
- Firma: `suspend fun safeQuote(source: suspend () -> Money): Result<Money>` en dos versiones: con `runCatching` y con
  `runCatchingCancellable` (de la lección)
- Casos: cancelada a mitad, la primera devuelve un `Result.failure` y la corrutina sigue; la segunda se cancela de
  verdad (el código después de la llamada no corre).

**11 🟢 Cancelar a los hijos sin cancelar al padre.** `cancelChildren`.
- Firma: `suspend fun restartWatchers(scope: CoroutineScope, tickers: List<String>, log: MutableList<String>)`
- Casos: `coroutineContext.cancelChildren()` cancela a los cinco vigilantes y el scope sigue aceptando nuevos; con
  `cancel()` ya no acepta (un `launch` posterior no corre).

**12 🟢 `Job()` como interruptor.** Un `Job` propio que corta un grupo de corrutinas de una vez.
- Firma: `class TickerGroup(scope: CoroutineScope)` con `fun watch(ticker: String)` y `fun stopAll()`
- Casos: `stopAll` cancela a todas; el scope de afuera no se cancela; después de `stopAll`, `watch` ya no lanza.

## 🟡 Medios

**13 🟡 🎤 `coroutineScope` frente a `supervisorScope`.** El mismo conjunto de vigilantes con cada uno.
- Firma: `suspend fun watchAll(tickers: List<String>, supervised: Boolean, watch: suspend (String) -> Unit): List<String>`
  (los tickers que terminaron)
- Casos: con un vigilante que falla a los 10 ms, `coroutineScope` cancela a los demás y relanza; `supervisorScope`
  deja terminar a los demás. Explica en un comentario cuándo usar cada uno.

**14 🟡 Transferencia con tiempo límite total.** Todo el flujo (débito, tasa, crédito) dentro de un solo límite.
- Firma: `suspend fun transferWithin(accounts: RemoteAccounts, fx: FxRateClient, from: String, to: String, amount: Money, limit: Duration): Money?`
- Casos: termina a tiempo; la tasa tarda demasiado → `null` y el débito revertido; el límite salta durante el
  crédito → ¿qué queda? Decide la regla, pruébala y explícala.

**15 🟡 `TimeoutCancellationException` silenciosa.** Hacer visible un tiempo límite que se perdía.
- Firma: `fun CoroutineScope.launchRefresh(fetch: suspend () -> Money, limit: Duration, onError: (Throwable) -> Unit): Job`
- Casos: con `withTimeout` sin atrapar, el `launch` termina cancelado y nadie se entera (`onError` no se llama);
  arréglalo para que el tiempo límite llegue a `onError` como un error de dominio con causa.

**16 🟡 Plazo por llamada y plazo total.** Cada fuente tiene 100 ms y el conjunto 250 ms.
- Firma: `suspend fun quotesWithDeadlines(sources: Map<String, suspend () -> Money>, perCall: Duration, total: Duration): Map<String, Money?>`
- Casos: una fuente lenta queda `null` sin afectar a las otras; si el total vence, todo lo pendiente queda `null`;
  `currentTime` nunca pasa de 250.

**17 🟡 `invokeOnCancellation` en un adaptador.** Una API de callbacks que se puede cancelar.
- Firma: `suspend fun quoteFromCallback(client: CallbackQuoteClient, ticker: String): Money` con
  `suspendCancellableCoroutine` (`CallbackQuoteClient` con `fun request(ticker, onPrice): Cancellable`, escrito por ti)
- Casos: el precio llega; al cancelar la corrutina, el cliente recibe `cancel()` (cuéntalo); el callback que llega
  tarde, después de cancelar, no rompe nada.

**18 🟡 `CoroutineExceptionHandler` en un scope de aplicación.** Registrar errores que nadie espera.
- Firma: `class AppScope(log: MutableList<String>) : CoroutineScope` con `SupervisorJob()` y un handler
- Casos: dos `launch` que fallan quedan registrados y el scope sigue vivo; un `async` que falla **no** llega al
  handler (la excepción espera a `await`); explica en un comentario por qué.

**19 🟡 La excepción del hijo y la del padre.** Qué excepción gana cuando fallan varios hijos.
- Firma: `suspend fun twoFailures(): Throwable` con dos hijos que fallan casi a la vez
- Casos: se lanza la primera y la segunda llega como `suppressed`; una `CancellationException` de los hermanos no se
  agrega como suprimida.

**20 🟡 🎤 ¿Qué pasa si atrapas `CancellationException`?** Tres versiones de un bucle de sondeo.
- Firma: `suspend fun pollSwallowing(…)`, `suspend fun pollRethrowing(…)` y `suspend fun pollCheckingActive(…)`, cada
  una con un `try/catch (e: Exception)` alrededor del `delay`
- Casos: la primera, cancelada, entra en un bucle que gira sin esperar (cuenta las vueltas en un tiempo límite
  real); las otras dos terminan. Explica en un comentario la regla.

**21 🟡 Sin fondos o cancelada: dos caminos distintos.** Una transferencia que distingue falla de cancelación en su
compensación.
- Firma: `suspend fun transferAudited(…, audit: MutableList<String>): Money` que anota `"revertida: falla"` o
  `"revertida: cancelada"`
- Casos: un error de la tasa anota falla; la cancelación del padre anota cancelada; las dos revierten el débito una
  sola vez.

**22 🟡 Recursos en orden inverso al cancelar.** Tres recursos abiertos dentro de una corrutina.
- Firma: `suspend fun openThree(log: MutableList<String>)` con `use` anidados o `try/finally`
- Casos: cancelada después de abrir los tres, se cierran en orden inverso; cancelada después de abrir dos, solo se
  cierran esos dos.

**23 🟡 Límite de tiempo con valor por defecto.** Usar la última tasa conocida si el servicio no contesta.
- Firma: `class FxWithFallback(fx: FxRateClient, limit: Duration)` con `suspend fun rate(from, to): BigDecimal`
- Casos: contesta → guarda la tasa; no contesta → devuelve la última guardada; nunca contestó → `FxTimeout`; la
  cancelación del llamador no se convierte en fallback.

**24 🟡 `supervisorScope` con resultados.** Seguir tickers y devolver lo que cada uno logró.
- Firma: `suspend fun followWithReport(tickers: List<String>, follow: suspend (String) -> Int): Report` con
  `data class Report(val ok: Map<String, Int>, val failed: Map<String, String>)`
- Casos: tres bien y dos mal; el reporte tiene los dos lados; la cancelación del padre no se reporta como falla.

**25 🟡 `SupervisorJob` en un scope, bien usado.** La diferencia entre `CoroutineScope(SupervisorJob())` y
`launch(SupervisorJob())`.
- Firma: `class FeedSupervisor` con un scope propio y `fun follow(ticker: String, block: suspend () -> Unit): Job`
- Casos: un `follow` que falla no cancela a los otros; `close()` cancela a todos. Muestra también la versión mal hecha
  con `launch(SupervisorJob())` (en 1.11.0 da un warning de deprecación, medido) y una prueba que demuestra que su
  corrutina ya no es hija.

**26 🟡 `withContext(NonCancellable)` mal usado.** El `NonCancellable` fuera del `finally`.
- Firma: `suspend fun uncancellableWork(log: MutableList<String>)` que envuelve todo el trabajo en `NonCancellable`
- Casos: cancelada, sigue hasta el final (el log lo muestra); explica en un comentario por qué eso rompe la
  cancelación del padre y por qué `NonCancellable` solo va en limpieza.

## 🟠 Difíciles

**27 🟠 🎤 Cancelación de punta a punta.** Una cadena de tres capas (controlador → servicio → cliente) y una
cancelación en la de arriba.
- Firma: tres clases con funciones `suspend`; el cliente simula la llamada con `suspendCancellableCoroutine`
- Casos: cancelar al controlador llega al cliente (cuenta las llamadas canceladas); un `catch (e: Exception)` en el
  servicio rompe la cadena (demuéstralo) y se arregla relanzando. Explica en un comentario qué tiene que hacer cada
  capa.

**28 🟠 Reversión idempotente.** La compensación no debe correr dos veces aunque la cancelación llegue en mal momento.
- Firma: `class SafeTransfer(accounts: RemoteAccounts, fx: FxRateClient)` con `suspend fun transfer(…): Money`
- Casos: cancelación durante la consulta, durante el crédito y durante la propia reversión: en los tres, el saldo de
  origen queda como estaba y la reversión aparece una sola vez en el log.

**29 🟠 Cierre ordenado.** Un servicio que al detenerse deja terminar lo que está en curso hasta un plazo y cancela lo
demás.
- Firma: `class QuoteWorker(scope: CoroutineScope)` con `fun submit(job: suspend () -> Unit)` y
  `suspend fun shutdown(grace: Duration): Int` (cuántos trabajos cortó)
- Casos: trabajos de 100 ms y plazo de 500 → todos terminan; trabajos de 10 s → se cortan al llegar al plazo;
  después de `shutdown` no se aceptan más.

**30 🟠 Tiempo límite que se extiende.** Un *watchdog*: la corrutina se cancela si pasan 2 s sin progreso, pero cada
progreso reinicia el plazo.
- Firma: `suspend fun <T> withIdleTimeout(idle: Duration, block: suspend (heartbeat: () -> Unit) -> T): T`
- Casos: un bloque que late cada segundo durante 10 s termina; uno que deja de latir se cancela 2 s después del último
  latido (en el instante exacto); la excepción dice cuánto llevaba sin latir.

**31 🟠 🎤 Excepciones en `async` y en `launch`, a fondo.** Una tabla de comportamiento armada con pruebas.
- Firma: una prueba por fila: `launch` raíz, `launch` hijo, `async` raíz con y sin `await`, `async` hijo con y sin
  `await`, cada uno dentro de `coroutineScope` y de `supervisorScope`
- Casos: para cada fila, si la excepción llega al handler, al `await`, al padre o a nadie. Escribe la tabla en un
  comentario y explica la regla general.

**32 🟠 Cancelar código bloqueante.** Una llamada JDBC simulada (`Thread.sleep`) que no se entera de la cancelación.
- Firma: `suspend fun <T> interruptible(block: () -> T): T` con `runInterruptible(Dispatchers.IO)`
- Casos: sin `runInterruptible`, cancelar no corta el `sleep` (mídelo con tiempo real); con él, el hilo recibe
  `interrupt()` y la corrutina termina enseguida; el `InterruptedException` se convierte en cancelación.

**33 🟠 Carreras entre cancelar y terminar.** Una corrutina que termina justo cuando la cancelan.
- Firma: `suspend fun raceCancelAndComplete(): String`
- Casos: con el orden de `runTest` controlado (`runCurrent`, `advanceTimeBy`), arma los dos casos: el resultado llega
  antes de la cancelación (gana el resultado) y la cancelación llega antes (gana la cancelación); explica qué
  garantiza `await` en cada caso.

**34 🟠 Supervisión jerárquica.** Un árbol de supervisores: si un ticker falla tres veces, se reinicia; si el feed
completo falla, se para todo.
- Firma: `class FeedTree(scope: CoroutineScope)` con reintentos por hijo y un supervisor raíz
- Casos: un hijo que falla dos veces y luego anda; uno que falla siempre y queda fuera sin tumbar a los demás; una
  falla del nivel raíz cancela a todos.

**35 🟠 Límite de tiempo en pruebas reales.** El mismo `withTimeout` probado en tiempo virtual y en tiempo real.
- Firma: `suspend fun slowRate(fx: FxRateClient, limit: Duration): BigDecimal`
- Casos: en `runTest` salta en el instante exacto sin esperar; con `runBlocking` y un `delay` real, salta en ±20 ms
  del límite. Explica en un comentario por qué el virtual es determinista y el real no.

**36 🟠 Lote con presupuesto.** Procesar movimientos del ledger hasta agotar un presupuesto de tiempo y devolver lo
pendiente.
- Firma: `suspend fun processWithBudget(items: List<String>, each: Duration, budget: Duration): Pair<List<String>, List<String>>`
- Casos: con 10 ítems de 100 ms y 450 de presupuesto, 4 procesados y 6 pendientes (el quinto, a medias, cuenta como
  pendiente y se compensa); presupuesto de sobra → todo procesado.

## 🔴 Muy difíciles

**37 🔴 Saga con compensaciones.** Una transferencia en cuatro pasos remotos (reservar, debitar, convertir,
acreditar), cada uno con su compensación.
- Firma: `class Saga` con `suspend fun step(name: String, action: suspend () -> Unit, compensate: suspend () -> Unit)` y
  `suspend fun run(block: suspend Saga.() -> Unit)`
- Casos: una falla en el paso 3 compensa 2 y 1, en ese orden; una cancelación igual; una compensación que falla no
  impide las siguientes y se reporta como suprimida; todo con tiempos virtuales exactos.

**38 🔴 Circuit breaker con corrutinas.** Abrir el circuito después de N tiempos límite y cerrarlo a prueba después de
un rato.
- Firma: `class CircuitBreaker(failures: Int, openFor: Duration)` con `suspend fun <T> call(limit: Duration, block: suspend () -> T): T`
- Casos: cerrado → abierto tras N fallas (las llamadas fallan al instante sin llamar); medio abierto pasado el plazo;
  una llamada buena lo cierra; la cancelación del llamador no cuenta como falla. Todo en tiempo virtual.

**39 🔴 Cancelación en un árbol grande.** Medir cuánto tarda en propagarse una cancelación.
- Firma: `suspend fun deepTree(depth: Int, fanOut: Int): Int` (cuántas corrutinas lanzó)
- Casos: un árbol de 100 000 corrutinas suspendidas; cancelar la raíz las cancela a todas (cuéntalo con
  `invokeOnCompletion`); el tiempo real de la cancelación va en una tabla en un comentario, para 1 000, 10 000 y
  100 000.

**40 🔴 `Thread.interrupt` frente a cancelación.** El mismo trabajo cancelable en Java y en Kotlin.
- Firma: una versión con hilos (`interrupt()`, `isInterrupted`, `InterruptedException`) y otra con corrutinas
- Casos: los dos se detienen; la versión de hilos que se traga `InterruptedException` sin restaurar la bandera no se
  detiene (demuéstralo); explica en un comentario las tres equivalencias y la diferencia de la jerarquía (un hilo no
  cancela a los que lanzó).

## ⚡ Saiyajin

**41 ⚡ Transferencia con tiempo límite y compensación.** Débito, consulta de tasa con `withTimeout` y crédito, con
reversión si algo falla o se cancela.
- Firma: `class TimedTransfer(accounts: RemoteAccounts, fx: FxRateClient, fxTimeout: Duration)` con
  `suspend fun transfer(from: String, to: String, amount: Money, target: Currency): Money`
- Reglas: el tiempo límite se convierte en un error de dominio con causa (no en una cancelación silenciosa); la
  reversión corre bajo `NonCancellable` y es idempotente; la cancelación del llamador se relanza siempre.
- **Se verifica con:** pruebas en tiempo virtual: el tiempo límite salta en el instante exacto, la compensación corre
  una sola vez aunque la cancelación llegue durante la compensación, y la cancelación del padre se propaga (no se
  traga).
