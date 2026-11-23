# 04 · `backpressure` — Backpressure en los tres modelos

**Sección:** `03-reactive` · **Paquete:** `dev.raul.oskar.learning.kotlin.backpressure` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#03--backpressure--backpressure-en-los-tres-modelos) · [ejercicios](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/backpressure/Exercises.md) · [código](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/backpressure/) · [pruebas](../../../03-reactive/src/test/kotlin/dev/raul/oskar/learning/kotlin/backpressure/BackpressureTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Comparar cómo manejan un productor más rápido que el consumidor Reactor (`request(n)`), RxJava (`Flowable`) y `Flow`
(suspensión), y elegir la estrategia (buffer acotado, descarte, último valor) según el dato: los precios se pueden
descartar; las ejecuciones, nunca.

## Teoría

### 1. El problema

Un feed de mercado emite en ráfagas; una pantalla o una base de datos consumen a su ritmo. Si nadie frena al
productor, los elementos se acumulan en alguna cola hasta que se acaba la memoria. **Backpressure** es el mecanismo
por el que el consumidor le dice al productor cuánto puede recibir. Un buffer sin límite **no** es backpressure: solo
cambia cuándo explota (y medir unos segundos no lo muestra; la memoria crece de a poco).

### 2. Tres modelos

- **Reactive Streams (Reactor, `Flowable`)**: el suscriptor pide con `request(n)` y el productor no debe emitir más de
  lo pedido. Si la fuente no puede esperar (un reloj, un socket, un feed), un operador `onBackpressure…` decide qué
  hacer con lo que sobra. → [Reactive Streams][rs-spec]
- **`Flow`**: no hay `request(n)`. `emit` suspende hasta que el colector termina, así que un productor normal se frena
  solo. Las estrategias aparecen al poner un buffer entre los dos (`buffer`, `conflate`). → [Asynchronous Flow][flow-docs]
- **`Observable` de RxJava**: no tiene backpressure. Por eso RxJava 2 separó `Flowable`; al pasar de uno a otro,
  `toFlowable(estrategia)` obliga a elegir. → [Backpressure (2.0)][rx-backpressure]

### 3. Las estrategias, medidas

La lección usa una fuente que empuja 100 precios de golpe sin mirar la demanda (`eagerReactorSource`, `eagerRxSource`)
y un consumidor que pide 5 y después 10 (`consumeSlowly`). En `Flow`, 20 precios con un consumidor de 10 ms
(`flowWithStrategy`). Resultados medidos (las pruebas los fijan):

| Estrategia | Reactor | RxJava (`Flowable`) | `Flow` |
|---|---|---|---|
| Buffer acotado (4) | 1..5, luego lo del buffer (6..9) y **después** `OverflowException` | 1..5 y `MissingBackpressureException` **enseguida** | Los 20: el productor espera |
| Descartar lo nuevo | 1..5 | 1..5 | `buffer(4, DROP_LATEST)`: 1..5 (uno en curso + 4) |
| Quedarse con el último | 1..5 y 100 | 1..5 y 100 | `conflate()`: 1 y 20 |

La diferencia entre Reactor y RxJava ante el desborde no estaba en el temario y salió al medir: Reactor entrega primero
lo que tenía guardado y recién después el error; RxJava corta en el acto (con `onBackpressureBuffer(n, true)`, el
parámetro `delayError`, se comporta como Reactor). Y la diferencia de fondo con `Flow`: ahí el buffer acotado nunca
falla, porque el productor puede esperar.

### 4. Una fuente que no puede esperar

`Flux.interval` y `Flowable.interval` son relojes: no pueden "esperar" a que les pidan. Sin demanda, fallan al primer
tick (`Reactor interval without demand overflows`, `RxJava interval without demand fails too`, en tiempo virtual). Es
el caso típico donde hay que elegir explícitamente una estrategia antes del consumidor.

### 5. Qué estrategia para qué dato

- **Precios para una pantalla**: el último valor (`onBackpressureLatest`, `conflate`): los intermedios no le importan a
  nadie.
- **Ejecuciones, órdenes, asientos**: nunca descartar. Un buffer **acotado** que falla de forma visible (Reactor,
  RxJava) o una fuente que espera (`Flow`, o una fuente de Reactor que respeta la demanda), y procesamiento por lotes
  hacia la base.
- **Métricas o eventos de diagnóstico**: descartar lo nuevo con un contador de descartados suele ser aceptable.

### 6. Buffers escondidos

`flowOn` agrega un canal (de 64 elementos por defecto) entre los dos contextos; `observeOn` de RxJava, un buffer de 128;
`publishOn` y `limitRate` de Reactor, un prefetch. No son un problema (están acotados), pero explican por qué un
productor "se adelanta" un poco aunque haya backpressure. Un `SharedFlow` con un suscriptor lento frena a todos los
demás si su buffer se llena (`02 · hotflows`).

### 7. Medir bien

La memoria crece de a poco y el GC esconde el problema en corridas cortas: las comparaciones de throughput y memoria
máxima van con calentamiento y carga sostenida (el ⚡ pide un minuto). Las pruebas de la lección no miden: fijan **qué
elementos llegan** con cada estrategia, que es lo determinista.

## Cómo correrla

```bash
./gradlew -p 03-reactive run -Plesson=backpressure
./gradlew -p 03-reactive test --tests '*.backpressure.*'
```

## Recorrido sugerido

1. `Strategies.kt`: `Strategy`, las fuentes que empujan (`eagerReactorSource`, `eagerRxSource`), las estrategias en
   cada modelo (`withStrategy` para `Flux` y para `Flowable`, `toFlowableWith` para `Observable`, `flowWithStrategy`
   para `Flow`) y el consumidor lento (`consumeSlowly`, `Received`).
2. `BackpressureTest.kt`: la tabla de arriba, prueba por prueba.
3. `Main.kt`: la misma tabla impresa.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Strategies.kt` | Reactor | `eagerReactorSource`, `withStrategy`, `consumeSlowly` | `BackpressureTest` (`Reactor bounded buffer delivers what it kept and then fails`, `Reactor drop keeps the first ones`, `Reactor latest keeps the last one`, `Reactor interval without demand overflows`) |
| `Strategies.kt` | RxJava | `eagerRxSource`, `withStrategy`, `toFlowableWith`, `consumeSlowly` | `BackpressureTest` (`RxJava bounded buffer fails right away`, `RxJava drop and latest behave like Reactor`, `an Observable picks its strategy when it becomes a Flowable`, `RxJava interval without demand fails too`) |
| `Strategies.kt` | `Flow` | `Strategy`, `flowWithStrategy`, `Received` | `BackpressureTest` (`Flow buffer waits and loses nothing`, `Flow drop latest keeps what fits`, `Flow conflate keeps the first and the last`, `without a buffer the Flow producer emits only as fast as the consumer`) |

## Para profundizar

**Documentación oficial**

- [Reactive Streams][rs-spec]: las reglas de `request(n)` (sección 3 sobre la `Subscription`).
- [Reactor Reference][reactor-ref]: los operadores `onBackpressure…`, `limitRate` y `Flux.create` con sus modos.
- [Backpressure (2.0)][rx-backpressure] (wiki de RxJava): `Observable` frente a `Flowable` y las estrategias.
- [Asynchronous Flow][flow-docs] (kotlinlang.org): `buffer`, `conflate` y `collectLatest`.
- [`buffer`][buffer-api] en la API de kotlinx.coroutines: capacidad y `BufferOverflow`.

**Lecturas**

- [Reactive Streams and Kotlin Flows][elizarov-rs] (Roman Elizarov): por qué la suspensión reemplaza a `request(n)`.
- [Simple design of Kotlin Flow][elizarov-simple] (Roman Elizarov): el diseño que hace que `emit` sea backpressure.

**Videos**

- [Asynchronous Data Streams with Kotlin Flow][video-flow] (Roman Elizarov, KotlinConf 2019): la backpressure en
  `Flow` comparada con Reactive Streams.

## Estado

- **Hecho:** código y 12 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[rs-spec]: https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md
[reactor-ref]: https://projectreactor.io/docs/core/release/reference/
[rx-backpressure]: https://github.com/ReactiveX/RxJava/wiki/Backpressure-%282.0%29
[flow-docs]: https://kotlinlang.org/docs/flow.html
[buffer-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/buffer.html
[elizarov-rs]: https://elizarov.medium.com/reactive-streams-and-kotlin-flows-bfd12772cda4
[elizarov-simple]: https://elizarov.medium.com/simple-design-of-kotlin-flow-4725e7398c4c
[video-flow]: https://www.youtube.com/watch?v=tYcqn48SMT8
