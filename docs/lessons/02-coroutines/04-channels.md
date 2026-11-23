# 04 · `channels` — `Channel`, `select`, productores

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.channels` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#02--channels--channel-select-productores) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/channels/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/channels/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/channels/ChannelsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Comunicar corrutinas con canales (el `BlockingQueue` que suspende en vez de bloquear), esperar varias fuentes a la
vez con `select` y escribir un actor: una corrutina dueña de un estado al que los demás le hablan por mensajes, sin
locks.

## Teoría

### 1. Un canal es una cola que suspende

`Channel<T>` tiene `send` y `receive` como `BlockingQueue` tiene `put` y `take`, con una diferencia: cuando la cola está
llena o vacía, la corrutina **suspende** y el hilo queda libre. Un canal es *caliente*: existe y guarda elementos aunque
nadie esté recibiendo, y cada elemento se entrega **una sola vez** (a diferencia de un `Flow`, que es frío y se vuelve a
producir para cada colector). Es la herramienta de comunicación entre corrutinas; para transformar datos, casi siempre
conviene `Flow` (`flow`). → [Channels][channels], [Cold flows, hot channels][elizarov-cold]

### 2. La capacidad decide quién espera a quién

| Capacidad | Equivale en Java | `send` suspende cuando… |
|---|---|---|
| `RENDEZVOUS` (0, la de por defecto) | `SynchronousQueue` | …no hay nadie recibiendo |
| `Channel(n)` | `ArrayBlockingQueue(n)` | …hay `n` elementos esperando |
| `BUFFERED` | — | …hay 64 (el valor por defecto, medido en 1.11.0) |
| `UNLIMITED` | `LinkedBlockingQueue()` | …nunca (y la memoria es el límite) |
| `CONFLATED` | — | …nunca: guarda solo el último |

`rendezvousLog` muestra el *rendezvous*: el primer `send` no termina hasta que el consumidor recibe.
`sendsBeforeConsumer` cuenta cuántos envíos alcanza a hacer el productor antes de que llegue el consumidor: 0, 3 o
10 según la capacidad. `trySend` no suspende: sobre un canal lleno devuelve un resultado fallido que hay que mirar
(`trySendIntoFull`), no lanza. Además, `BufferOverflow.DROP_OLDEST` o `DROP_LATEST` hacen que un canal acotado descarte
en vez de suspender. → [`Channel`][channel-api]

### 3. Cerrar el canal

`channel.close()` es el *poison pill* de siempre, pero hecho por la librería: los elementos que ya estaban se siguen
entregando, y después el `for (x in channel)` del consumidor termina. Si el productor no cierra, el `for` espera para
siempre. `close(causa)` cierra con un error que el consumidor recibe al llegar al final. `produce { }` lanza una
corrutina productora y cierra su canal solo cuando el bloque termina, o lo cierra con la excepción si falla
(`produceTicks`); en 1.11.0 todavía pide `@OptIn(ExperimentalCoroutinesApi::class)` (medido). → [`produce`][produce-api]

### 4. *Fan-out* y *fan-in*

Varios consumidores de un mismo canal se **reparten** los elementos: cada tick lo recibe uno solo (`fanOut`, y la prueba
`consumers of one channel share the elements` comprueba que no se pierde ni se duplica ninguno). Es el patrón de un
pool de trabajadores. Si se necesita que **todos** reciban **todo** (varias pantallas mirando el mismo precio), el canal
no sirve: hace falta un `SharedFlow` (`hotflows`). Al revés, varios productores pueden escribir en el mismo canal
(*fan-in*, `fanIn`), y el consumidor recibe todo intercalado; el canal se cierra cuando terminaron todos los
productores, no antes.

### 5. `select`: esperar varias cosas a la vez

`select { a.onReceive { }; b.onReceive { } }` registra varias cláusulas y se queda con la primera que se puede
completar: la cotización de la fuente principal o la de respaldo, la que llegue antes (`firstQuote`). Si varias están
listas al mismo tiempo, gana la que se escribió primero (`select` es **sesgado**; `selectUnbiased` elige al azar).
También hay `onSend` (mandar al primero que tenga lugar), `onAwait` (el primer `Deferred` que termine),
`onReceiveCatching` (que no lanza al cerrarse el canal: `mergeUntilClosed`) y `onTimeout`, que en 1.11.0 pide
`@ExperimentalCoroutinesApi` (`quoteOrTimeout`). Es el `Selector` de NIO o el `select` de Go, para corrutinas. →
[Select expression][select], [Select in Kotlin Coroutines][kt-select]

### 6. Actores: el estado tiene un solo dueño

El problema clásico: el libro de órdenes del Mercado lo modifican muchas corrutinas a la vez. Con locks (`synchronized`,
`Mutex`), cada operación toma el lock y lo suelta. Con un **actor**, el libro tiene un solo dueño: una corrutina que lee
comandos de un canal y los aplica uno tras otro. Los demás no tocan el libro: le mandan mensajes. Como solo una
corrutina lo toca, el libro (`MatchingBook`) no necesita ser seguro entre hilos.

`MatchingEngine` es ese actor: `Command` es una jerarquía sellada (`Place`, `Cancel`, `Snapshot`), y las preguntas que
esperan respuesta llevan un `CompletableDeferred` donde el actor la deja (el "sobre de respuesta"). Las ejecuciones
salen por otro canal hacia la liquidación del Banco. La prueba `a hundred senders never leave the book crossed and keep
the quantity` lanza 100 corrutinas en `Dispatchers.Default` que mandan 1 000 órdenes, y el libro nunca queda cruzado.

kotlinx.coroutines tenía un constructor `actor { }`, pero en 1.11.0 sigue marcado `@ObsoleteCoroutinesApi` (medido): el
equipo planea reemplazarlo, así que el patrón se escribe a mano, como en la lección. Lo mismo pasa con `ticker()` (un
canal que emite cada N ms): para eso hoy se usa un `flow { while (true) { emit(…); delay(…) } }`. →
[Shared mutable state and concurrency][shared-state], [Channel in Kotlin Coroutines][kt-channel]

### 7. Emparejamiento por precio-tiempo

La lógica del libro es la de `01-language/collections` con ejecuciones parciales: una orden de compra cruza mientras
el mejor precio de venta sea menor o igual a su límite; consume primero el mejor precio y, dentro de un precio, la orden
más antigua (`ArrayDeque` por nivel en un `TreeMap`); cada ejecución va al precio de la orden que esperaba (*maker*); lo
que no se llena queda en el libro. `the book matches by price then time with partial fills` recorre un caso completo.

### 8. Cuándo no usar canales

Los canales son de bajo nivel. Para procesar un flujo de datos (filtrar, agrupar, cambiar de hilo), `Flow` es más
simple y no se olvida de cerrar nada; para estado compartido observable, `StateFlow`; para limitar concurrencia,
`Semaphore` o `limitedParallelism` (`sync`, `dispatchers`). Un canal `UNLIMITED` esconde la falta de backpressure hasta
que se acaba la memoria. Lo que queda para los canales: comunicación entre corrutinas de larga vida (actores, pools de
trabajadores, pipelines con etapas concurrentes) y lo que `Flow` usa por debajo (`buffer`, `channelFlow`, `flowOn`).

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=channels
./gradlew -p 02-coroutines test --tests '*.channels.*'
```

## Recorrido sugerido

1. `Basics.kt`: *rendezvous* (`rendezvousLog`), capacidad (`sendsBeforeConsumer`), `trySend` (`trySendIntoFull`),
   `produce` (`produceTicks`), *fan-out* y *fan-in* (`fanOut`, `fanIn`).
2. `Select.kt`: la primera cotización (`firstQuote`), con tiempo límite (`quoteOrTimeout`) y la mezcla de dos canales
   hasta que se cierran (`mergeUntilClosed`).
3. `MatchingEngine.kt`: el libro sin locks (`MatchingBook`), los mensajes (`Command`) y el actor (`MatchingEngine`).
4. `ChannelsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Basics.kt` | Canales, capacidad, cierre, reparto | `rendezvousLog`, `sendsBeforeConsumer`, `trySendIntoFull`, `produceTicks`, `fanOut`, `fanIn` | `ChannelsTest` (`a rendezvous send waits for the receiver`, `capacity decides how far the producer runs ahead`, `trySend on a full channel fails without throwing`, `produce closes the channel when the block ends`, `consumers of one channel share the elements`, `fan-in interleaves several producers`) |
| `Select.kt` | Esperar varias fuentes | `firstQuote`, `quoteOrTimeout`, `mergeUntilClosed` | `ChannelsTest` (`select takes whichever source is ready first`, `select can time out`, `merge reads both channels until both close`) |
| `MatchingEngine.kt` | El libro como actor | `Side`, `LimitOrder`, `Execution`, `BookSnapshot`, `MatchingBook`, `Command`, `MatchingEngine` | `ChannelsTest` (`the book matches by price then time with partial fills`, `the actor answers through CompletableDeferred…`, `a hundred senders never leave the book crossed…`) |
| `cases/Feed.kt` | Feed sintético con semilla (de la sección) | `tickFeed`, `withFlaws` | `ChannelsTest`, `FlowTest` |

## Para profundizar

**Documentación oficial**

- [Channels][channels] (kotlinlang.org): canales, `produce`, pipelines, *fan-out* y *fan-in*, capacidades.
- [Select expression][select]: `onReceive`, `onSend`, `onAwait` y el sesgo.
- [Shared mutable state and concurrency][shared-state]: el contador con confinamiento, `Mutex` y actor, lado a lado.
- En la API: [`Channel`][channel-api], [`produce`][produce-api] y [`select`][select-api].

**Lecturas**

- [Cold flows, hot channels][elizarov-cold] (Roman Elizarov): por qué los canales son calientes y cuándo eso es un
  problema.
- [Channel in Kotlin Coroutines][kt-channel] y [Select in Kotlin Coroutines][kt-select] (Marcin Moskała, Kt. Academy):
  capacidades, `onUndeliveredElement`, pipelines y `select`.

**Videos**

- [Kotlin Coroutines in Practice][video-practice] (Roman Elizarov, KotlinConf 2018): un sistema con canales, `select` y
  un actor de punta a punta (los "CSP" de la charla).

## Estado

- **Hecho:** código y 12 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 2); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[channels]: https://kotlinlang.org/docs/channels.html
[select]: https://kotlinlang.org/docs/select-expression.html
[shared-state]: https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html
[channel-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/
[produce-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/produce.html
[select-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.selects/select.html
[elizarov-cold]: https://elizarov.medium.com/cold-flows-hot-channels-d74769805f9
[kt-channel]: https://kt.academy/article/cc-channel
[kt-select]: https://kt.academy/article/cc-select
[video-practice]: https://www.youtube.com/watch?v=a3agLJQ6vt8
