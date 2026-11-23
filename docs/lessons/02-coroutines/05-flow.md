# 05 · `flow` — Operadores, `flowOn`, buffer, conflate

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.flow` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#02--flow--operadores-flowon-buffer-conflate) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/flow/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/flow/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/flow/FlowTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Procesar flujos de datos asíncronos con `Flow` (frío, con backpressure por suspensión), escribir operadores propios
con estado, cambiar de contexto bien (`flowOn` y no `withContext`) y elegir entre `buffer` y `conflate` según el dato.

## Teoría

### 1. Qué es un `Flow`

Un `Flow<T>` es una secuencia de valores que llegan **de a poco y de forma asíncrona**: el `Stream` de Java, pero cada
paso puede suspender. Su interfaz es mínima: `suspend fun collect(collector: FlowCollector<T>)`. Un `flow { }` es un
bloque que llama a `emit(x)`; cada `emit` llama al colector y **espera a que termine**. Todo lo demás (`map`, `filter`,
`take`…) son extensiones que envuelven un `Flow` en otro.

Esa simpleza es a propósito: Roman Elizarov la explica comparándola con Reactor y RxJava, donde un `Publisher`, un
`Subscriber` y una `Subscription` negocian la demanda con `request(n)`. En `Flow`, la negociación la hace la suspensión.
→ [Asynchronous Flow][flow-docs], [Simple design of Kotlin Flow][elizarov-simple]

### 2. Frío: nada pasa hasta `collect`, y cada `collect` empieza de cero

`TickFeed.flow()` no hace nada al crearse; cada `collect` vuelve a correr el bloque desde el principio y abre la fuente
de nuevo (`a cold flow runs again for every collector` cuenta dos aperturas). Es como un `Stream` que se puede recorrer
muchas veces, o como un `Flux` de Reactor sin `share()`. La consecuencia práctica: dos `collect` del mismo `Flow` que lee
un archivo o abre una conexión leen dos veces. Para compartir una sola fuente entre varios colectores hace falta un
flujo **caliente** (`shareIn`, `stateIn`: `hotflows`). → [Cold flows, hot channels][elizarov-cold]

### 3. Backpressure sin `request(n)`

Como `emit` espera a que el colector termine, un productor rápido con un consumidor lento **se frena solo**: no hay
cola que crezca ni elementos perdidos. `slowConsumer` con `Strategy.NONE` lo muestra: 10 precios cada 10 ms con un
consumidor de 30 ms tardan 400 ms (los dos tiempos se suman), y ningún precio se pierde. En la especificación de
Reactive Streams (`java.util.concurrent.Flow` en el JDK), lo mismo exige que el suscriptor pida `request(n)` y que el
publicador cuente; en `Flow`, la suspensión hace ese trabajo. Se compara a fondo en `03-reactive/backpressure`. →
[Reactive Streams and Kotlin Flows][elizarov-rs], [`java.util.concurrent.Flow`][jdk-flow]

### 4. `buffer` y `conflate`: desacoplar productor y consumidor

Con productor y consumidor en la misma corrutina, sus tiempos se suman. `buffer(n)` los separa: el productor corre en
otra corrutina y deja hasta `n` elementos en un canal; el consumidor marca el ritmo y se recibe todo
(`a buffer decouples producer and consumer`: 310 ms en vez de 400). `conflate()` es un buffer de uno que se queda solo
con **el último**: el consumidor salta los intermedios (`conflate keeps only the latest value`). Sirve para el último
precio de una pantalla, **nunca** para ejecuciones que hay que liquidar todas. `collectLatest` va más lejos: cancela el
trabajo del valor anterior cuando llega uno nuevo. → [`buffer`][buffer-api]

### 5. Operadores propios con estado

Un operador propio es una extensión que devuelve `flow { collect { … emit(…) } }`. El estado (lo último visto por
ticker, la vela abierta) se declara **dentro** del `flow { }`, así cada colección tiene el suyo:

- `cleaned` descarta duplicados exactos y ticks que llegan fuera de orden dentro de su ticker (el feed con defectos sale
  de `withFlaws`).
- `candles` arma velas por ventana y emite cada una cuando llega el primer tick de la ventana siguiente (o al terminar
  el flujo).
- `alerts` emite cuando el precio cruza un umbral. La primera versión de la lección tenía el estado afuera del
  `flow { }` y dos colecciones se lo compartían: `alerts fire on each crossing and keep state per collector` cuida
  justamente eso.

→ [Understanding Flow][kt-understanding], [Flow building][kt-building]

### 6. Contexto: `flowOn` y la invariante de contexto

Un `Flow` corre en el contexto de quien llama a `collect`. Para mover el productor a otro dispatcher se usa
`flowOn(Dispatchers.IO)`, que cambia el contexto de todo lo que está **aguas arriba** del operador
(`upstreamAndCollectorThreads`: el productor en `DefaultDispatcher-worker-…`, el colector en el hilo de la prueba). Es el
`subscribeOn` de Reactor, pero por tramos. Por debajo, `flowOn` mete un canal entre los dos contextos (con buffer de
64), así que el productor puede adelantarse.

Lo que **no** se puede: llamar a `emit` desde un `withContext` dentro de `flow { }`. Eso haría que el colector recibiera
el valor en otro hilo que el suyo, y la librería lo impide: `brokenFlow` falla con `IllegalStateException` "Flow
invariant is violated" (medido). Si el productor de verdad necesita varias corrutinas o contextos, se usa
`channelFlow { }`. → [Execution context of Kotlin Flows][elizarov-context], [`flowOn`][flowon-api]

### 7. Errores: `catch` es aguas arriba, y la cancelación

`catch { }` atrapa las excepciones de lo que está **antes** que él (el productor y los operadores anteriores) y puede
convertirlas en un valor (`catchUpstreamOnly`). Una excepción que lanza el `collect` sigue de largo: `catch` no la ve
(lo comprueba `catch only sees the upstream`). Para hacer algo al final pase lo que pase, `onCompletion { causa -> }`. Y
como un `Flow` corre dentro de una corrutina, se cancela con ella: `take(3)` corta al productor apenas recibe el tercero
(por dentro, con una excepción de cancelación que el productor no debe atrapar).

### 8. `callbackFlow`: de listeners a `Flow`

Muchas APIs (clientes de precios, sensores en Android, websockets) entregan datos por callback. `callbackFlow { }`
registra el listener, manda cada valor con `trySend` y termina con `awaitClose { quitarListener() }`, que corre cuando el
colector se cancela o el flujo se cierra (`PriceListenerClient.prices`). Sin `awaitClose`, el listener quedaría colgado y
la librería lanza una excepción para avisar. La prueba `callbackFlow unregisters when the collector goes away` usa
`take(2)` y verifica que el listener se quitó. En 1.11.0 compila sin opt-in (medido). → [`callbackFlow`][callbackflow-api]

### 9. Operadores con opt-in

Varios operadores útiles siguen marcados en 1.11.0 (medido con el compilador): `debounce`, `sample` y `timeout` piden
`@FlowPreview`; `flatMapMerge`, `flatMapConcat`, `flatMapLatest` y `chunked` piden `@ExperimentalCoroutinesApi`. Se
pueden usar, pero su firma puede cambiar entre versiones de la librería: conviene concentrar su uso en pocas funciones.

### 10. Probar un `Flow` con Turbine

`flow.test { awaitItem(); expectNoEvents(); awaitComplete() }` de Turbine recorre las emisiones una por una dentro de
`runTest`, con el tiempo virtual: `the feed emits each tick at its instant` comprueba que el tick de 1 s llega cuando
`currentTime` es 1 000. Se profundiza en `testing`. → [Turbine][turbine]

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=flow
./gradlew -p 02-coroutines test --tests '*.flow.*'
```

## Recorrido sugerido

1. `cases/Feed.kt`: el feed sintético con semilla (`tickFeed`) y sus defectos (`withFlaws`).
2. `Feed.kt`: el feed como `Flow` frío que emite cada tick en su instante (`TickFeed`).
3. `Operators.kt`: `cleaned`, `candles` (`Candle`) y `alerts` (`PriceAlert`), con el estado dentro del `flow { }`.
4. `Context.kt`: `flowOn` (`upstreamAndCollectorThreads`), la invariante (`brokenFlow`), `catch` (`catchUpstreamOnly`),
   `callbackFlow` (`PriceListenerClient`, `prices`) y `describe`.
5. `Backpressure.kt`: `fastPrices` y `slowConsumer` con las tres estrategias (`Strategy`).
6. `FlowTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Feed.kt` | Un `Flow` frío con tiempos | `TickFeed` | `FlowTest` (`a cold flow runs again for every collector`, `the feed emits each tick at its instant`) |
| `Operators.kt` | Operadores propios con estado | `cleaned`, `Candle`, `candles`, `PriceAlert`, `alerts` | `FlowTest` (`cleaning drops duplicates and late ticks per ticker`, `candles close when the next window starts`, `alerts fire on each crossing and keep state per collector`) |
| `Context.kt` | Contexto, errores y callbacks | `upstreamAndCollectorThreads`, `brokenFlow`, `catchUpstreamOnly`, `PriceListenerClient`, `prices`, `describe` | `FlowTest` (`flowOn moves only the upstream`, `withContext inside flow violates the context invariant`, `catch only sees the upstream`, `callbackFlow unregisters when the collector goes away`) |
| `Backpressure.kt` | Productor rápido, consumidor lento | `Strategy`, `fastPrices`, `slowConsumer` | `FlowTest` (`without a buffer the producer waits for the consumer`, `a buffer decouples producer and consumer`, `conflate keeps only the latest value`) |

## Para profundizar

**Documentación oficial**

- [Asynchronous Flow][flow-docs] (kotlinlang.org): constructores, operadores, contexto, buffer, `conflate`,
  `collectLatest`, excepciones y cancelación.
- En la API: [`Flow`][flow-api] (las reglas que un `Flow` debe cumplir), [`flowOn`][flowon-api], [`buffer`][buffer-api]
  y [`callbackFlow`][callbackflow-api].
- [Kotlin flows on Android][android-flow] (Android Developers): productores, intermediarios y consumidores, con
  ejemplos prácticos.
- [`java.util.concurrent.Flow`][jdk-flow] y [Reactive Streams][reactive-streams]: el modelo de `request(n)` con el que se
  compara.

**Lecturas**

- [Simple design of Kotlin Flow][elizarov-simple], [Reactive Streams and Kotlin Flows][elizarov-rs] y [Execution
  context of Kotlin Flows][elizarov-context] (Roman Elizarov): el diseño, la relación con Reactive Streams y por qué
  existe la invariante de contexto.
- [Cold flows, hot channels][elizarov-cold] (Roman Elizarov): frío frente a caliente.
- [Introduction to the Flow][kt-intro], [Understanding Flow][kt-understanding], [Flow building][kt-building] y [Flow
  lifecycle operations][kt-lifecycle] (Marcin Moskała, Kt. Academy): cómo funciona por dentro y sus operadores de ciclo
  de vida.
- [Turbine][turbine] (Cash App): la librería de pruebas, con ejemplos.

**Videos**

- [Asynchronous Data Streams with Kotlin Flow][video-flow] (Roman Elizarov, KotlinConf 2019): el diseño de `Flow`
  contado por su autor.
- [Kotlin Flows in practice][video-flows-practice] (Android Developers): `Flow` en una app real, con `callbackFlow` y
  pruebas.

## Estado

- **Hecho:** código y 12 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 2); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[flow-docs]: https://kotlinlang.org/docs/flow.html
[flow-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/
[flowon-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flow-on.html
[buffer-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/buffer.html
[callbackflow-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html
[android-flow]: https://developer.android.com/kotlin/flow
[jdk-flow]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Flow.html
[reactive-streams]: https://www.reactive-streams.org/
[elizarov-simple]: https://elizarov.medium.com/simple-design-of-kotlin-flow-4725e7398c4c
[elizarov-rs]: https://elizarov.medium.com/reactive-streams-and-kotlin-flows-bfd12772cda4
[elizarov-context]: https://elizarov.medium.com/execution-context-of-kotlin-flows-b8c151c9309b
[elizarov-cold]: https://elizarov.medium.com/cold-flows-hot-channels-d74769805f9
[kt-intro]: https://kt.academy/article/cc-flow-introduction
[kt-understanding]: https://kt.academy/article/cc-understanding-flow
[kt-building]: https://kt.academy/article/cc-flow-building
[kt-lifecycle]: https://kt.academy/article/cc-flow-lifecycle
[turbine]: https://github.com/cashapp/turbine
[video-flow]: https://www.youtube.com/watch?v=tYcqn48SMT8
[video-flows-practice]: https://www.youtube.com/watch?v=fSB6_KE95bU
