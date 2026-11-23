# Ejercicios: corrutinas frente a hilos virtuales

> **Lección:** `loom` · **Dónde se resuelven:** `loom/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../loom/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 02-coroutines test --tests '*.loom.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Aquí casi todo es con hilos y
> tiempo **reales** (los hilos virtuales no saben nada del tiempo virtual de `runTest`): las pruebas comprueban órdenes
> de magnitud con márgenes amplios, y las mediciones finas van en tablas dentro de comentarios, con calentamiento y
> repeticiones. El curso usa el JDK 21: lo que cambió después (JEP 491 en el 24) se marca. Las APIs en vista previa del
> 21 (`StructuredTaskScope`, `ScopedValue`) piden `--enable-preview`; donde aparecen, son opcionales. El ⚡ es el 41.

**Prioritarios (🎯):** ninguno todavía (la ruta corta no pasa por esta sección). **Entrevista (🎤):** 01, 05, 13, 18, 27, 31.

## 🟢 Fáciles

**01 🟢 🎤 ¿Los hilos virtuales hacen innecesarias las corrutinas?** El mismo fan-out de cotizaciones con los dos.
- Firma: `fun quotesWithVirtualThreads(tickers: List<String>): Map<String, Money>` (cada fuente bloquea con
  `Thread.sleep`) y `suspend fun quotesWithCoroutines(tickers: List<String>): Map<String, Money>` (con `delay`)
- Casos: los dos tardan lo que la fuente más lenta. Explica en un comentario qué trae cada modelo que el otro no
  (corrutinas: cancelación estructurada, `Flow`, multiplataforma; hilos virtuales: el código bloqueante de siempre sin
  cambios, las librerías Java).

**02 🟢 Un hilo virtual a mano.** `Thread.ofVirtual()` y `Thread.startVirtualThread`.
- Firma: `fun runOnVirtual(block: () -> Unit): Thread`
- Casos: `isVirtual` es `true`; el nombre está vacío salvo que se le ponga uno (`ofVirtual().name("quote-", 0)`);
  `join()` espera.

**03 🟢 10 000 hilos virtuales.** Lo que con hilos de plataforma cuesta caro.
- Firma: `fun manyVirtualThreads(n: Int): Duration` (cada uno duerme 100 ms)
- Casos: 10 000 terminan en bastante menos de un segundo; anota en un comentario lo que pasó al intentar lo mismo con
  10 000 hilos de plataforma (tiempo, memoria o error).

**04 🟢 El executor de hilos virtuales con `use`.** Cerrar espera a las tareas.
- Firma: `fun processAll(items: List<String>, work: (String) -> Unit): Int`
- Casos: al salir del `use`, todas terminaron (sin `awaitTermination` a mano); explica en un comentario que
  `ExecutorService` es `AutoCloseable` desde el JDK 19.

**05 🟢 🎤 ¿Qué es el *pinning*?** Reproducirlo.
- Firma: usa `blockingInsideLocks` de la lección
- Casos: con `synchronized` en el JDK 21, el tiempo es de `tareas / núcleos` tandas; con `ReentrantLock`, una. Explica
  en un comentario qué es un hilo portador, por qué `synchronized` lo fija en el 21 y qué cambió el JEP 491 (JDK 24).

**06 🟢 Detectar el pinning.** `-Djdk.tracePinnedThreads=full`.
- Firma: un `main` propio que provoca pinning
- Casos: corrido con `-Djdk.tracePinnedThreads=full` (agrégalo en un `JavaExec` o con `java -D…`), imprime el stack del
  hilo fijado; pega la salida relevante en un comentario. (En el JDK 21 la opción existe; desde el 24 cambió el
  mecanismo: anota qué encontraste.)

**07 🟢 Corrutinas sobre hilos virtuales.** El dispatcher de `dispatchers`.
- Firma: `suspend fun <T> onVirtualThreads(block: () -> T): T` (con un dispatcher de hilos virtuales compartido)
- Casos: el bloque corre en un hilo virtual; 1 000 llamadas bloqueantes de 50 ms terminan en un poco más de 50 ms;
  compáralo con `Dispatchers.IO` (64 a la vez).

**08 🟢 `ThreadLocal` en hilos virtuales.** Funciona, pero cuesta.
- Firma: `fun threadLocalsPerVirtualThread(n: Int): Long` (memoria aproximada con un `ThreadLocal<ByteArray>` de 10 KB)
- Casos: con 10 000 hilos virtuales, la memoria crece en proporción (anota la medida); explica en un comentario por qué
  los `ThreadLocal` pesados (cachés por hilo) son un problema con millones de hilos.

**09 🟢 Hilos de plataforma frente a virtuales: identidad.** Lo que cambia y lo que no.
- Firma: `fun describe(thread: Thread): String` (virtual, daemon, grupo, prioridad)
- Casos: un hilo virtual es siempre daemon y tiene prioridad normal (intentar cambiarla no tiene efecto: compruébalo).

**10 🟢 JDBC bloqueante en un hilo virtual.** El repositorio simulado de `dispatchers`.
- Firma: `fun balancesOnVirtual(repo: BlockingLedgerRepository, accounts: List<String>): List<Money>`
- Casos: 500 cuentas con 10 ms cada una terminan en mucho menos que 5 s; el pico de concurrencia es cercano a 500
  (sin límite: explica en un comentario por qué eso puede agotar el pool de conexiones de la base).

**11 🟢 Limitar la concurrencia con hilos virtuales.** El `Semaphore` de Java vuelve a ser útil.
- Firma: `fun limitedOnVirtual(tasks: Int, permits: Int): Int` (pico de concurrencia)
- Casos: con 1 000 tareas y 10 permisos, el pico es 10; explica en un comentario por qué con hilos virtuales no se
  limita con el tamaño del pool (no hay pool) y se usa un semáforo.

**12 🟢 `CompletableFuture` sobre hilos virtuales.** El estilo asíncrono ya no hace falta.
- Firma: una versión con `supplyAsync(…, virtualExecutor).thenCombine(…)` y otra con código bloqueante directo en un
  hilo virtual
- Casos: mismo resultado y mismo tiempo; explica en un comentario por qué con hilos virtuales vuelve a ser razonable
  escribir código bloqueante.

## 🟡 Medios

**13 🟡 🎤 ¿Cuándo usarías cada uno en un backend Spring?** Un servicio de transferencias en dos versiones.
- Firma: `class BlockingTransferService` (JDBC simulado, pensado para Spring MVC con hilos virtuales:
  `spring.threads.virtual.enabled=true`) y `class SuspendingTransferService` (para WebFlux con corrutinas)
- Casos: los dos atienden 1 000 transferencias concurrentes (simuladas) en un tiempo parecido; explica en un comentario
  qué elegirías según el resto del sistema (librerías bloqueantes, streaming, equipo) y que se retoma en `09-spring` y
  `13-spring-api`.

**14 🟡 Cancelación: lo que los hilos virtuales no traen.** Cancelar un fan-out.
- Firma: el fan-out de cinco fuentes con hilos virtuales donde una falla, y la misma con corrutinas
- Casos: con corrutinas, las otras cuatro se cancelan solas; con hilos virtuales hay que cancelarlas a mano
  (`Future.cancel(true)` + interrupciones): escribe ese código y cuenta las líneas de más.

**15 🟡 `StructuredTaskScope` (vista previa del 21, opcional).** El mismo fan-out con la API de Java.
- Firma: `fun quotesWithScope(tickers: List<String>): Map<String, Money>` con `StructuredTaskScope.ShutdownOnFailure`
- Casos: si una falla, las demás se interrumpen; requiere `--enable-preview` en la compilación y en las pruebas (o
  explica en un comentario lo que daría y compáralo con `coroutineScope`).

**16 🟡 `ScopedValue` frente a `CoroutineContext`.** Un id de correlación con los dos.
- Firma: `fun withScopedRequestId(id: String, block: () -> String): String` (vista previa del 21, opcional) y
  `suspend fun withContextRequestId(id: String, block: suspend () -> String): String` (con `QuoteRequestId` de
  `internals`)
- Casos: los dos llegan a funciones anidadas; los dos se heredan por los hijos (subtareas o corrutinas hijas); explica
  en un comentario las diferencias (inmutable y acotado en los dos; uno por hilo, otro por corrutina).

**17 🟡 Throughput de un servidor simulado.** Clientes que piden un precio y esperan 20 ms de "red".
- Firma: `fun serve(model: BroadcastModel, clients: Int): Pair<Duration, Int>` usando los modelos de la lección
- Casos: con 1 000, 5 000 y 10 000 clientes, tabla de tiempos de los cuatro modelos (con calentamiento) en un
  comentario; la prueba solo comprueba que los virtuales y las corrutinas terminan en menos de 1 s con 5 000.

**18 🟡 🎤 Mezclar los dos modelos sin necesidad.** Qué cuesta.
- Firma: un servicio que lanza una corrutina por pedido dentro de un hilo virtual por pedido
- Casos: funciona, pero el volcado de hilos muestra los dos niveles (pega un fragmento de `jcmd <pid> Thread.dump_to_file`
  y de un volcado de corrutinas); explica en un comentario por qué duplica las herramientas de diagnóstico y cuándo sí
  vale la pena mezclar (corrutinas que llaman a librerías bloqueantes).

**19 🟡 El carrier y el `ForkJoinPool`.** De dónde salen los hilos portadores.
- Firma: `fun carrierNames(): Set<String>` (desde el `toString()` de los hilos virtuales, que incluye el portador)
- Casos: los portadores se llaman `ForkJoinPool-1-worker-N`; hay tantos como núcleos (o lo que diga
  `-Djdk.virtualThreadScheduler.parallelism`); anota lo que observaste.

**20 🟡 `synchronized` sin bloqueo no fija.** El pinning solo importa si se bloquea adentro.
- Firma: `fun cpuInsideSynchronized(tasks: Int): Duration` (cálculo corto dentro de `synchronized`, sin `sleep`)
- Casos: con o sin `synchronized`, el tiempo es parecido; explica en un comentario por qué el problema es bloquear
  (`sleep`, E/S) dentro del monitor, no el monitor en sí.

**21 🟡 `Object.wait()` en un hilo virtual.** Otra fuente de pinning en el 21.
- Firma: un productor-consumidor con `wait`/`notify` sobre hilos virtuales, y la versión con `ReentrantLock` +
  `Condition`
- Casos: con muchos pares, la versión con `wait` se degrada en el JDK 21 (mide); la de `Condition`, no. Anota qué cambió
  en el 24.

**22 🟡 El `Mutex` de kotlinx dentro de corrutinas sobre hilos virtuales.** Sin pinning.
- Firma: corrutinas sobre el dispatcher de hilos virtuales que toman un `Mutex` y hacen `delay` adentro
- Casos: el tiempo no se degrada con el número de tareas (el `Mutex` suspende, no bloquea el hilo); compáralo con
  `synchronized` + `Thread.sleep` en el mismo dispatcher.

**23 🟡 Memoria: hilos de plataforma, virtuales y corrutinas.** Medir con 10 000 de cada uno esperando.
- Firma: `fun memoryWhileWaiting(model: String, n: Int): Long` (bytes aproximados con `Runtime` y `System.gc()`)
- Casos: tabla en un comentario; explica por qué la medida es aproximada y qué guarda cada uno (pila reservada del SO,
  pila en el heap que crece, objeto de continuación con las variables vivas).

**24 🟡 Hilos virtuales en las pruebas.** Por qué no hay tiempo virtual.
- Firma: un servicio que usa `Thread.sleep` en un hilo virtual y su prueba
- Casos: la prueba tarda el tiempo real; explica en un comentario qué perdiste frente a `runTest` y cómo se diseña para
  probar igual (inyectar un `Sleeper` o un reloj).

**25 🟡 Interrupción de hilos virtuales.** La cancelación de Loom.
- Firma: `fun cancellableVirtual(work: () -> Unit): Thread` y una prueba que lo interrumpe durante un `sleep`
- Casos: el `InterruptedException` llega y el hilo termina; uno que se traga la interrupción sigue (demuéstralo);
  compáralo en un comentario con `cancellation` (las tres equivalencias).

**26 🟡 Un pool de conexiones con hilos virtuales.** El cuello de botella se mueve.
- Firma: `FakeConnectionPool` de `dispatchers` (ejercicio 27) con 10 conexiones y 1 000 hilos virtuales
- Casos: 990 hilos esperan conexión (cuéntalos); el throughput lo marca el pool, no los hilos; explica en un comentario
  por qué Loom no hace más rápida a la base de datos.

## 🟠 Difíciles

**27 🟠 🎤 El servidor de difusión con backpressure.** Un cliente lento con cada modelo.
- Firma: una difusión de 100 actualizaciones por segundo a 1 000 clientes, uno de ellos lento
- Casos: con corrutinas y `SharedFlow` + `DROP_OLDEST` el lento no frena a los demás; con hilos virtuales y una
  `BlockingQueue` por cliente hay que elegir a mano qué descartar (escríbelo); explica qué trae cada modelo para la
  backpressure (`Flow` sí, Loom no).

**28 🟠 Benchmark con JMH (o arnés propio).** La latencia p99 de un pedido con los tres modelos.
- Firma: un benchmark que mide pedidos con 20 ms de espera bajo carga sostenida de 30 s
- Casos: tabla de p50, p99 y throughput en un comentario, con calentamiento y 5 repeticiones; explica por qué un
  benchmark de una sola corrida mide el arranque (JIT, GC) y no el modelo.

**29 🟠 Pinning y deadlock.** Cuando todos los portadores quedan fijados.
- Firma: una prueba con más tareas que núcleos donde cada una toma un `synchronized` y espera algo que solo otra tarea
  (que no consigue portador) puede darle
- Casos: en el JDK 21 se cuelga (detectado con un tiempo límite y una prueba `@Disabled` si molesta); con
  `ReentrantLock`, termina. Explica el mecanismo en un comentario.

**30 🟠 Migrar un servicio con `CompletableFuture` a hilos virtuales.** Paso a paso.
- Firma: un servicio con una cadena de `thenCompose` (dado por ti) migrado a código bloqueante en hilos virtuales
- Casos: la misma batería de pruebas pasa antes y después; el código queda más corto (cuenta las líneas); el manejo de
  errores pasa de `exceptionally` a `try/catch`.

**31 🟠 🎤 Migrar el mismo servicio a corrutinas.** Comparar con el 30.
- Firma: el servicio del 30 en corrutinas (`await()` de `kotlinx.coroutines.future` para lo que siga siendo future)
- Casos: misma batería; compara en un comentario los dos caminos de migración (qué cambia en las firmas, quién llama,
  cancelación, pruebas).

**32 🟠 Un `ThreadContextElement` para hilos virtuales.** Propagar el MDC a corrutinas que corren en hilos virtuales.
- Firma: el `MdcElement` de `dispatchers` (ejercicio 33) usado sobre el dispatcher de hilos virtuales
- Casos: el MDC aparece en cada línea de log aunque la corrutina salte entre hilos virtuales; explica qué pasa con un
  `ThreadLocal` común en ese caso.

**33 🟠 El JDK 24 y el JEP 491 (opcional).** Correr la prueba de pinning en un JDK nuevo.
- Firma: la prueba de `loom` corrida con un toolchain 24 o posterior (en un proyecto aparte, sin cambiar el del curso)
- Casos: el pinning de `synchronized` desaparece (la prueba de la lección se salta sola con `assumeTrue`); anota los
  tiempos de los dos JDK en un comentario.

**34 🟠 Corrutinas que bloquean sin querer en hilos virtuales.** Lo que se esconde.
- Firma: un servicio con corrutinas en `Dispatchers.Default` que llama a JDBC sin `withContext`, y la misma idea en el
  dispatcher de hilos virtuales
- Casos: en `Default` se nota (el pico es de núcleos); en hilos virtuales "funciona"; explica en un comentario por qué
  eso puede esconder un error de diseño que aparece al cambiar de dispatcher.

**35 🟠 Carga sostenida y memoria.** Diez mil clientes conectados durante un minuto.
- Firma: el servidor de difusión con cada modelo y clientes que se conectan y desconectan
- Casos: la memoria máxima (con `MemoryMXBean`) de cada modelo en una tabla; ninguna fuga (al desconectarse todos, la
  memoria vuelve después de un GC).

**36 🟠 Un `Executor` virtual con nombre y límite.** Útil en producción.
- Firma: `fun namedLimitedVirtualExecutor(prefix: String, maxConcurrent: Int): ExecutorService`
- Casos: los hilos se llaman `prefix-N`; nunca hay más de `maxConcurrent` corriendo; cerrar espera a las tareas.

## 🔴 Muy difíciles

**37 🔴 Corrutinas *stackful* de juguete con hilos virtuales.** Implementar `yield` entre dos tareas con hilos
virtuales y un `SynchronousQueue`.
- Firma: `class VirtualGenerator<T>(block: (yield: (T) -> Unit) -> Unit) : Iterator<T>`
- Casos: genera precios como `sequence` de `internals`; compara en un comentario el costo (un hilo virtual por
  generador frente a un objeto de continuación) y la diferencia *stackful*/*stackless*.

**38 🔴 Un servidor TCP de precios con hilos virtuales.** Un hilo virtual por conexión, código bloqueante.
- Firma: `class VirtualPriceServer(port: Int, board: PriceBoard)` con `ServerSocket` y un hilo virtual por cliente
- Casos: 1 000 clientes conectados reciben actualizaciones; la memoria se mantiene razonable; cerrar el servidor cierra
  todas las conexiones.

**39 🔴 El mismo servidor con corrutinas y NIO.** Sin un hilo por conexión.
- Firma: el servidor del 38 con `AsynchronousSocketChannel` envuelto en `suspendCancellableCoroutine` (o Ktor raw
  sockets, adelanto de `10-ktor`)
- Casos: mismo comportamiento que el 38; tabla comparativa de memoria y throughput.

**40 🔴 Informe de decisión.** Corrutinas, hilos virtuales o los dos, para tres sistemas del curso.
- Firma: un archivo `.md` en tu `practice/` con la recomendación para el feed del Mercado, la API de transferencias y
  una app Android, cada una justificada con mediciones de los ejercicios anteriores
- Casos: cada afirmación tiene su número y su ejercicio de origen; incluye lo que **no** se midió.

## ⚡ Saiyajin

**41 ⚡ Servidor de difusión con tres modelos.** El mismo servidor de suscripciones (tipo chat) con hilos de
plataforma, hilos virtuales y corrutinas, bajo 10 000 clientes simulados.
- Firma: `interface BroadcastServer { fun subscribe(client: String, tickers: Set<String>); fun publish(tick: Tick); fun close() }`
  con tres implementaciones
- Reglas: cada cliente recibe solo sus tickers; un cliente lento no frena a los demás (decide la política y
  documéntala); medición con calentamiento y repeticiones.
- **Se verifica con:** una tabla de throughput, latencia p99 y memoria de cada modelo guardada en la lección (con
  calentamiento y repeticiones), y una prueba que reproduce el *pinning* en JDK 21 con un `synchronized` y lo resuelve
  con `ReentrantLock` o `Mutex`.
