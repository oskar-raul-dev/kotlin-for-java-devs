# 03 · `bridges` — `kotlinx-coroutines-reactor`, `awaitSingle`, `asFlow`, `asFlux`

**Sección:** `03-reactive` · **Paquete:** `dev.raul.oskar.learning.kotlin.bridges` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#03--bridges--kotlinx-coroutines-reactor-awaitsingle-asflow-asflux) · [ejercicios](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/bridges/Exercises.md) · [código](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/bridges/) · [pruebas](../../../03-reactive/src/test/kotlin/dev/raul/oskar/learning/kotlin/bridges/BridgesTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Cruzar entre corrutinas y Reactor (y RxJava) sin perder la cancelación, el contexto ni la backpressure: lo que pide un
backend WebFlux escrito en Kotlin, donde el framework habla Reactor y el código propio habla `suspend` y `Flow`.

## Teoría

### 1. Por qué hacen falta puentes

En un backend Spring WebFlux, el framework, `WebClient`, R2DBC y Spring Security hablan Reactor; el código que conviene
escribir en Kotlin es `suspend` y `Flow`. Los puentes de `kotlinx-coroutines-reactor` (y de `-reactive`, que viene con
él, y de `-rx3` para RxJava) convierten en las dos direcciones, y lo importante es que conservan las tres cosas que
hacen funcionar a cada modelo: la **cancelación**, el **contexto** y la **backpressure**. Spring los usa por detrás
cuando un controlador es `suspend` o devuelve `Flow`. → [Coroutines en Spring][spring-coroutines],
[kotlinx-coroutines-reactor][reactor-api]

### 2. De Reactor a corrutinas: `await…` y `asFlow`

| Reactor | Corrutinas | Nota |
|---|---|---|
| `mono.block()` | `mono.awaitSingle()` | Suspende en vez de bloquear |
| `Mono` que puede estar vacío | `awaitSingleOrNull()` | El vacío se vuelve `null` |
| `flux.blockFirst()` | `flux.awaitFirst()` / `awaitFirstOrNull()` | |
| `flux.toIterable()` | `flux.asFlow()` | La demanda de Reactor se vuelve suspensión |

`awaitSingle()` sobre un `Mono` vacío lanza `NoSuchElementException` (`awaitSingle suspends for the value and fails on
empty`): para "puede no haber" está `awaitSingleOrNull()`. `feedAsFlow` convierte el feed y `a Flux becomes a Flow`
comprueba que llega entero.

### 3. De corrutinas a Reactor: `mono { }`, `flux { }`, `asFlux`

`mono { … }` corre una corrutina **por suscripción** y emite su resultado (vacío si devuelve `null`); `flux { send(…) }`
es un productor con corrutinas; `flow.asFlux()` convierte un `Flow`. Las tres son perezosas, como todo en Reactor:
nada corre hasta la suscripción (`mono and asFlux offer coroutine code to Reactor`). La corrutina de `mono { }` corre por
defecto en `Dispatchers.Default`; se le puede pasar otro contexto.

### 4. La cancelación cruza en las dos direcciones

Cancelar la corrutina que recolecta un `flux.asFlow()` cancela la suscripción de Reactor (`cancelling the Flow cancels
the Reactor subscription`: un `take(3)` sobre un `Flux.interval` infinito dispara su `doOnCancel`). Y cancelar la
suscripción de un `flux { }` cancela su corrutina (`cancelling the subscription cancels the coroutine`: corre el `finally`).
Eso es lo que permite que, si un cliente HTTP corta la conexión, WebFlux cancele la suscripción y la cancelación llegue
hasta la consulta que estaba esperando. Lo que rompe la cadena: un `GlobalScope.launch` en el medio, o un `subscribe()`
suelto. → [Cancellation in coroutines][vivo-cancellation]

### 5. El contexto cruza en las dos direcciones

Reactor tiene su propio contexto (`contextWrite`, `deferContextual`), que viaja **de abajo hacia arriba** (del
suscriptor a la fuente). Dentro de un `mono { }`, ese contexto está en `coroutineContext[ReactorContext]`
(`correlationIdSeenByCoroutine`). Al revés, una corrutina le pasa contexto a una cadena de Reactor con
`Context.of(…).asCoroutineContext()` (`correlationIdSeenByReactor`). Así llega el `SecurityContext` de Spring Security a
una función `suspend` de un controlador. Lo que lo pierde: lanzar una corrutina aparte (en otro scope) que ya no
hereda el contexto. → [Context][reactor-context]

### 6. La backpressure cruza el puente

`flow.asFlux()` solo hace avanzar al `Flow` cuando Reactor pide: `countingFlowAsFlux` produjo exactamente 25 de 1 000
elementos cuando se pidieron 25 con `limitRate(10).take(25)`, y 5 cuando la demanda fue 5 (medido; `backpressure crosses
the bridge`). En la otra dirección, `flux.asFlow()` pide a Reactor de a lotes y deja de pedir si el colector no avanza.
Ningún puente agrega un buffer sin límite.

### 7. Las trampas

- `awaitSingle()` sobre vacío lanza: elegir `awaitSingleOrNull()` cuando el vacío es posible.
- `asFlow()` sobre un `Flux` **caliente** (un `Sinks.many()`, un `share()`) no lo vuelve frío: varios colectores
  comparten la fuente.
- `runBlocking` en un controlador WebFlux bloquea un hilo de Netty: con pocos hilos de eventos, todo el servidor se
  frena. Lo correcto es que el controlador sea `suspend` (o devuelva `mono { }`).
- Las pruebas que cruzan a Reactor no pueden usar el reloj virtual de `runTest`: los schedulers de Reactor corren con
  tiempo real. Las de esta lección usan `runBlocking` y esperas de milisegundos, o `StepVerifier`.

### 8. Los mismos puentes para RxJava

`kotlinx-coroutines-rx3` tiene los equivalentes: `Single.await()`, `Maybe.awaitSingleOrNull()`, `Observable.asFlow()`,
`rxSingle { }`, `rxObservable { }`, `Flow.asObservable()` y `Scheduler.asCoroutineDispatcher()`. Se usan en la
migración de `rxjava`. → [kotlinx-coroutines-rx3][rx3-api]

## Cómo correrla

```bash
./gradlew -p 03-reactive run -Plesson=bridges
./gradlew -p 03-reactive test --tests '*.bridges.*'
```

## Recorrido sugerido

1. `Bridges.kt`, en este orden: el cliente con API de Reactor (`ReactivePriceClient`), Reactor → corrutinas (`priceOf`,
   `priceOrNull`, `feedAsFlow`), corrutinas → Reactor (`quoteMono`, `toFlux`), el contexto
   (`correlationIdSeenByCoroutine`, `correlationIdSeenByReactor`), la cancelación (`endlessReactorFeed`,
   `endlessCoroutineFeed`) y la backpressure (`countingFlowAsFlux`).
2. `BridgesTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Bridges.kt` | Reactor → corrutinas | `ReactivePriceClient`, `priceOf`, `priceOrNull`, `feedAsFlow` | `BridgesTest` (`awaitSingle suspends for the value and fails on empty`, `a Flux becomes a Flow`) |
| `Bridges.kt` | Corrutinas → Reactor | `quoteMono`, `toFlux` | `BridgesTest` (`mono and asFlux offer coroutine code to Reactor`) |
| `Bridges.kt` | Contexto, cancelación y backpressure | `correlationIdSeenByCoroutine`, `correlationIdSeenByReactor`, `endlessReactorFeed`, `endlessCoroutineFeed`, `countingFlowAsFlux` | `BridgesTest` (`the Reactor context reaches the coroutine`, `the coroutine context reaches Reactor`, `cancelling the Flow cancels the Reactor subscription`, `cancelling the subscription cancels the coroutine`, `backpressure crosses the bridge`) |

## Para profundizar

**Documentación oficial**

- [kotlinx-coroutines-reactor][reactor-api] y [kotlinx-coroutines-reactive][reactive-api]: cada puente con su
  comportamiento ante vacío, error y cancelación.
- [kotlinx-coroutines-rx3][rx3-api]: los mismos puentes para RxJava 3.
- [Coroutines en Spring Framework][spring-coroutines]: cómo Spring traduce `suspend` y `Flow` a Reactor en WebFlux,
  el contexto y las transacciones.
- [Context][reactor-context] (referencia de Reactor): cómo viaja el contexto y por qué de abajo hacia arriba.

**Lecturas**

- [Reactive Streams and Kotlin Flows][elizarov-rs] (Roman Elizarov): por qué `Flow` y Reactive Streams se convierten sin
  perder la backpressure.
- [Cancellation in coroutines][vivo-cancellation] (Manuel Vivo): la cancelación que los puentes preservan.

**Videos**

- [Asynchronous Data Streams with Kotlin Flow][video-flow] (Roman Elizarov, KotlinConf 2019): incluye la relación de
  `Flow` con Reactive Streams y los puentes.

## Estado

- **Hecho:** código y 8 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[reactor-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactor/
[reactive-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-reactive/
[rx3-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-rx3/
[spring-coroutines]: https://docs.spring.io/spring-framework/reference/languages/kotlin/coroutines.html
[reactor-context]: https://projectreactor.io/docs/core/release/reference/advancedFeatures/context.html
[elizarov-rs]: https://elizarov.medium.com/reactive-streams-and-kotlin-flows-bfd12772cda4
[vivo-cancellation]: https://medium.com/androiddevelopers/cancellation-in-coroutines-aa6b90163629
[video-flow]: https://www.youtube.com/watch?v=tYcqn48SMT8
