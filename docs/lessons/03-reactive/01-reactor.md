# 01 · `reactor` — `Mono`/`Flux` desde Kotlin, extensiones

**Sección:** `03-reactive` · **Paquete:** `dev.raul.oskar.learning.kotlin.reactor` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#03--reactor--monoflux-desde-kotlin-extensiones) · [ejercicios](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/reactor/Exercises.md) · [código](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/reactor/) · [pruebas](../../../03-reactive/src/test/kotlin/dev/raul/oskar/learning/kotlin/reactor/ReactorTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Leer y escribir código Reactor desde Kotlin (el de WebFlux y de muchas librerías Java), con las extensiones de
`reactor-kotlin-extensions`, probarlo con `StepVerifier`, y entender cómo Kotlin ve los `null` de Reactor 3.9.

## Teoría

### 1. Reactor en una página

Reactor implementa Reactive Streams (la especificación que el JDK copió como `java.util.concurrent.Flow`): un
`Publisher` emite a un `Subscriber`, que pide elementos con `request(n)` (backpressure, `backpressure`). `Mono<T>`
emite cero o un elemento; `Flux<T>`, de cero a infinitos. Encima de eso hay unos 500 operadores (`map`, `flatMap`,
`zip`, `retryWhen`, `window`…) y *schedulers* para elegir hilos. Spring WebFlux, R2DBC y `WebClient` están hechos con
Reactor: por eso aparece en todo backend reactivo, aunque el código propio se escriba con corrutinas (`bridges`). →
[Reactor Reference][ref], [Reactive Streams][rs-spec]

### 2. *Lazy*: nada pasa hasta `subscribe`

Armar un `Mono` o un `Flux` no ejecuta nada: es una receta. El trabajo corre al suscribirse y **otra vez en cada
suscripción** (como un `Flow` frío). `lazyQuote` cuenta las ejecuciones: 0 al armarlo, 2 después de dos suscripciones
(`nothing happens until subscribe and each subscription runs again`). El error clásico al portar código imperativo es
llamar a una función que devuelve `Mono<Void>` (guardar, enviar) y no suscribirla ni devolverla: no pasa nada, sin
error. El contrario también es un error: un `subscribe()` suelto en medio de una cadena pierde los errores y no espera.

### 3. `null`: prohibido por la especificación, visible para Kotlin

Reactive Streams prohíbe `null` como elemento (regla 2.13). "Puede no haber" se modela con un `Mono` **vacío**
(`Mono.justOrEmpty`, `priceOrEmpty`), que no es lo mismo que `null`. Lo nuevo: Reactor 3.9 trae anotaciones de
nulabilidad JSpecify, y Kotlin las lee. Medido con el compilador:

- `Mono.just(valorNullable)` y un `map { null }` **no compilan**.
- `block()` sobre un vacío devuelve `null` con tipo nullable; `mapNotNull` filtra lo que da `null`.
- El `null` se sigue colando por los **platform types** de APIs Java sin anotar: `Mono.just(System.getProperty("x"))`
  compila y falla en ejecución con `NullPointerException: value` (medido).

`empty is not null` recorre los casos que compilan. → [JSpecify][jspecify]

### 4. Las extensiones de Kotlin

`reactor-kotlin-extensions` (versión alineada por el BOM de Reactor) agrega lo que el temario llama idiomático:
`list.toFlux()`, `valor.toMono()`, `flux.test()` (un `StepVerifier` más corto) y funciones con tipos reificados. Las
lambdas finales fuera del paréntesis hacen que una cadena de Reactor se lea casi igual que en Java, con menos ruido. →
[Kotlin support][ref-kotlin], [reactor-kotlin-extensions][kotlin-ext]

### 5. `map` frente a `flatMap`, y la concurrencia

`map` transforma cada elemento de forma síncrona. `flatMap` transforma cada elemento en otro `Publisher` (una llamada
remota) y mezcla los resultados: se suscribe a varios a la vez (hasta 256 por defecto), así que el orden de salida no
está garantizado. `concatMap` va de a uno y conserva el orden; `flatMapSequential`, en paralelo y en orden. El segundo
parámetro de `flatMap` limita la concurrencia: `quoteAll` cuenta las suscripciones internas activas (5 sin límite, 2
con límite: `flatMap concurrency limits the inner subscriptions`). Una nota medida al escribirlo: el contador baja en
`doOnTerminate`, no en `doFinally`, porque `doFinally` corre después de que `flatMap` ya se suscribió al siguiente. →
[Which operator do I need?][operator-choice]

### 6. Estado por suscripción

Un operador con estado (lo último visto por ticker, de qué lado del umbral estaba el precio) tiene que crear su estado
**por suscripción**: `Flux.defer { val estado = …; filter { … } }`. Si el estado viviera afuera, dos suscriptores se lo
compartirían, la misma trampa que tuvo `alerts` en `02 · flow`. `cleaned` y `crossings` lo hacen así, y `cleaning
matches the reference and keeps state per subscriber` se suscribe dos veces para comprobarlo.

### 7. El pipeline del feed en Reactor

La limpieza, las velas y los cruces de `02 · flow`, con operadores de Reactor: `filter` con estado (limpieza),
`groupBy` por ticker + `bufferUntilChanged` por ventana (velas) y `handle` (cruces, que puede emitir cero o un valor por
elemento). `candles equal the plain collection reference` compara contra `candlesReference` de `cases`, el cálculo con
colecciones comunes: la misma referencia sirve para RxJava (`rxjava`) y para el ⚡.

### 8. Hilos: `publishOn`, `subscribeOn` y no bloquear

`subscribeOn` decide en qué hilo corre la fuente; `publishOn` cambia el hilo de lo que viene después (el `flowOn` de
Reactor va al revés: afecta lo de arriba). `Schedulers.parallel()` es para cálculo y sus hilos están marcados como no
bloqueantes: un `block()` ahí lanza `IllegalStateException` ("block()/blockFirst()/blockLast() are blocking, which is not
supported in thread parallel-1", `block inside a non blocking thread is refused`). Lo mismo pasa en los hilos de Netty
de WebFlux. Para llamadas bloqueantes está `Schedulers.boundedElastic()`, el `Dispatchers.IO` de Reactor.

### 9. Probar con `StepVerifier`

`StepVerifier.create(flux).expectNext(…).verifyComplete()` (o `flux.test()`) se suscribe, comprueba cada señal y
falla con un mensaje claro. `StepVerifier.withVirtualTime { … }` reemplaza los schedulers de tiempo por uno virtual:
`the timed feed runs in virtual time` comprueba dos minutos de feed temporizado (`timedFeed`) sin esperar, con
`expectNoEvent` y `thenAwait`. El `Supplier` es importante: el `Flux` tiene que crearse **dentro** de la lambda para usar
el reloj virtual. → [Testing][ref-testing]

## Cómo correrla

```bash
./gradlew -p 03-reactive run -Plesson=reactor
./gradlew -p 03-reactive test --tests '*.reactor.*'
```

## Recorrido sugerido

1. `cases/` (de la sección): `Money` reducido, tickers, `tickFeed` y `withFlaws` (los mismos de `02-coroutines`), y las
   referencias con colecciones (`cleanReference`, `candlesReference`).
2. `Semantics.kt`: *lazy* (`lazyQuote`), vacío frente a `null` (`priceOrEmpty`), `block()` prohibido
   (`blockInsideParallel`), concurrencia de `flatMap` (`quoteAll`) y `Mono<Void>` (`settle`).
3. `Pipeline.kt`: el feed temporizado (`timedFeed`) y el pipeline (`cleaned`, `candles`, `crossings`).
4. `ReactorTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `cases/Money.kt`, `cases/Market.kt` | Copias mínimas y referencias de la sección | `Money`, `TICKERS`, `Tick`, `tickFeed`, `withFlaws`, `Candle`, `cleanReference`, `candlesReference`, `toCandle` | (se usan en todas las pruebas de la sección) |
| `Semantics.kt` | Reglas de Reactor desde Kotlin | `lazyQuote`, `priceOrEmpty`, `blockInsideParallel`, `quoteAll`, `settle` | `ReactorTest` (`nothing happens until subscribe…`, `empty is not null`, `block inside a non blocking thread is refused`, `flatMap concurrency limits the inner subscriptions`, `Mono of Void only signals completion`) |
| `Pipeline.kt` | El pipeline del feed | `timedFeed`, `cleaned`, `candles`, `crossings` | `ReactorTest` (`cleaning matches the reference and keeps state per subscriber`, `candles equal the plain collection reference`, `crossings fire on each crossing`, `the timed feed runs in virtual time`) |

## Para profundizar

**Documentación oficial**

- [Reactor Reference][ref]: el modelo, los operadores, los schedulers y el manejo de errores.
- [Kotlin support][ref-kotlin] y [reactor-kotlin-extensions][kotlin-ext]: extensiones y nulabilidad.
- [Testing][ref-testing]: `StepVerifier`, tiempo virtual y `TestPublisher`.
- [Which operator do I need?][operator-choice]: el apéndice para elegir operador.
- [Reactive Streams][rs-spec]: las reglas que todo `Publisher` cumple (incluida la del `null`).

**Lecturas**

- [JSpecify][jspecify] y [Null safety in Spring apps with JSpecify and NullAway][spring-jspecify] (blog de Spring): las
  anotaciones que hacen que Kotlin vea los `null` de Reactor 3.8+ y de Spring 7.

**Videos**

- Sin video verificado que cubra Reactor desde Kotlin en la versión fijada; la referencia oficial es lo más preciso.

## Estado

- **Hecho:** código y 9 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[ref]: https://projectreactor.io/docs/core/release/reference/
[ref-kotlin]: https://projectreactor.io/docs/core/release/reference/kotlin.html
[ref-testing]: https://projectreactor.io/docs/core/release/reference/testing.html
[operator-choice]: https://projectreactor.io/docs/core/release/reference/apdx-operatorChoice.html
[kotlin-ext]: https://github.com/reactor/reactor-kotlin-extensions
[rs-spec]: https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md
[jspecify]: https://jspecify.dev/
[spring-jspecify]: https://spring.io/blog/2025/03/10/null-safety-in-spring-apps-with-jspecify-and-null-away
