# 08 · `internals` — CPS, máquina de estados decompilada, `Continuation` a mano

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.internals` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#02--internals--cps-máquina-de-estados-decompilada-continuation-a-mano) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/internals/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/internals/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/internals/InternalsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Qué genera el compilador para una función `suspend`, cómo se arranca y se reanuda una corrutina con la stdlib sola,
cómo adaptar una API de callbacks a `suspend` sin perder la cancelación, y qué cuesta cada punto de suspensión.

## Teoría

### 1. Dos capas: el lenguaje y la librería

Las corrutinas de Kotlin tienen dos partes. El **lenguaje** y la **stdlib** (`kotlin.coroutines`) aportan muy poco: la
palabra `suspend`, la interfaz `Continuation`, `createCoroutine`/`startCoroutine`, `suspendCoroutine`, `sequence` y el
`CoroutineContext`. Todo lo demás (`launch`, `async`, dispatchers, `Job`, `Flow`, `Channel`) está en la **librería**
`kotlinx.coroutines`, escrita sobre esas piezas. Es una decisión de diseño explícita del KEEP: el compilador no sabe
nada de hilos ni de schedulers. → [KEEP: Kotlin Coroutines][keep], [`kotlin.coroutines`][stdlib-coroutines]

### 2. *Continuation-passing style*

Para la JVM, `suspend fun quote(ticker: String): Money` es `quote(String, Continuation<Money>): Object`. El compilador
agrega el último parámetro: la **continuación**, el "resto del programa" que tiene que correr cuando llegue el
resultado. El valor de retorno es el resultado **o** la marca `COROUTINE_SUSPENDED`, que significa "me suspendí; te
aviso por la continuación". `callWithContinuation` lo hace visible con un cast de la lambda `suspend` a función de un
parámetro: sin suspensión devuelve el valor; con `Parking`, devuelve la marca, y el resultado llega cuando alguien llama
a `resumeWith` sobre la continuación guardada (`a suspend function returns its value or the suspended marker`). Por eso
desde Java una función `suspend` pide un `Continuation` como último parámetro. → [How does suspension work in Kotlin
coroutines?][kt-suspension]

### 3. La máquina de estados

Una función `suspend` con N puntos de suspensión se compila a una clase con un campo `label` y un `switch` de N + 1
ramas: cada vez que se reanuda, salta a la rama donde quedó. Las variables locales que siguen vivas después de un punto
de suspensión no pueden quedar en la pila del hilo (el hilo se va), así que pasan a ser **campos** del objeto:
`stateMachineFields` muestra `I$0` (el `Int` que sobrevive al `delay`) y `label` (`live locals become fields of the state
machine`). Para una lambda `suspend`, la propia lambda es la máquina de estados. Para verla entera, el tutorial
`04-decompile` (`javap -c -p`, CFR) y el ⚡ de esta lección. → [Coroutines under the hood][kt-under-hood], [Deep Dive into
Coroutines on JVM][video-deep]

### 4. Por qué son "baratas"

Una corrutina suspendida es ese objeto de estado más la cadena de continuaciones de quienes la llamaron: unos cientos
de bytes en el heap. Un hilo de plataforma reserva una pila de alrededor de un megabyte en el sistema operativo. Esa es
toda la diferencia, y también el costo: cada punto de suspensión crea o actualiza objetos, y las variables vivas ocupan
memoria mientras la corrutina espera. Son corrutinas *stackless*: cada función guarda su propio estado, por eso solo una
función `suspend` (o una lambda `inline` dentro de una) puede suspender. Los hilos virtuales de Loom son *stackful*:
guardan la pila entera y pueden suspender en cualquier llamada bloqueante (`loom`).

### 5. Arrancar una corrutina sin kotlinx

`block.startCoroutine(continuacion)` arranca una corrutina con la stdlib sola (`startBare`): no hay dispatcher, ni
`Job`, ni scope. Corre en el hilo que la arranca hasta su primer punto de suspensión y sigue en el hilo de quien la
reanude. `RecordingContinuation` es todo lo que hace falta del otro lado: un `context` y un `resumeWith(Result<T>)` que
recibe el resultado o la excepción. `createCoroutine` crea sin arrancar. Sobre esto se construye `runBlocking`, `launch` y
lo demás: el ⚡ (un planificador propio) y el 🐉 1 lo hacen a mano.

### 6. Callbacks → `suspend`

`suspendCancellableCoroutine { cont -> … }` es la herramienta para envolver cualquier API asíncrona: se registra el
callback, el callback llama a `cont.resume(valor)` o `cont.resumeWithException(error)`, y `cont.invokeOnCancellation {
}` cancela el pedido si la corrutina se cancela mientras espera (`CallbackQuoteClient.quote`, y la prueba `cancelling the
coroutine cancels the request`). La versión de la stdlib, `suspendCoroutine`, no se entera de la cancelación: el pedido
sigue vivo en el cliente y la corrutina cancelada queda en *Cancelling* hasta que el callback llega (`suspendCoroutine
does not hear about cancellation`). Y una continuación se reanuda una sola vez: la segunda vez lanza
`IllegalStateException("Already resumed")` (medido), así que un callback que puede llegar dos veces hay que protegerlo.
→ [`suspendCancellableCoroutine`][scc-api]

### 7. `sequence`: corrutinas restringidas

`sequence { yield(x) }` es una corrutina de la stdlib que solo puede suspender con `yield` (está marcada con
`@RestrictsSuspension`): no necesita hilos ni dispatcher y corre en el hilo de quien pide el siguiente elemento. Es el
generador que Java no tiene (`priceWalk`: perezosa, genera solo lo que se pide). Sirve también para escribir máquinas de
estados como código secuencial (`orderLifecycle`). → [`sequence`][sequence-api], [Sequences][sequences]

### 8. El contexto viaja con la continuación

El `CoroutineContext` es un mapa inmutable indexado por clave (`Job`, dispatcher, `CoroutineName`, elementos propios)
que va dentro de la continuación, no en el hilo. Por eso un elemento propio (`QuoteRequestId`) lo lee cualquier función
`suspend` de la corrutina y lo heredan los hijos (`context elements travel with the coroutine`), como un `ThreadLocal` que
no se pierde al cambiar de hilo. El dispatcher es un elemento más: un `ContinuationInterceptor` que envuelve cada
continuación para reanudarla en su hilo. → [Coroutine context and scope][elizarov-context]

### 9. Las pilas de excepciones

Como el código después de un punto de suspensión corre desde otra pila (la de quien reanudó), el stack trace de una
excepción solo muestra el último tramo. kotlinx.coroutines, en modo debug (activo con `-ea`, como en las pruebas),
"recupera" parte de la pila copiando la excepción en cada `await`; `kotlinx-coroutines-debug` (tutorial `05-debug`) da
volcados completos de corrutinas. → [Debug coroutines using IntelliJ IDEA][debug-idea]

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=internals
./gradlew -p 02-coroutines test --tests '*.internals.*'
```

## Recorrido sugerido

1. `Cps.kt`: la firma real (`callWithContinuation`), una suspensión de verdad (`Parking`), una continuación propia
   (`RecordingContinuation`), arrancar sin kotlinx (`startBare`) y los campos de la máquina de estados
   (`stateMachineFields`).
2. `Callbacks.kt`: el cliente de callbacks (`CallbackQuoteClient`, `Cancellable`), su adaptador cancelable (`quote`), el
   que no lo es (`quoteIgnoringCancellation`) y la doble reanudación (`resumeTwice`).
3. `Generators.kt`: `priceWalk` y `orderLifecycle`.
4. `Context.kt`: `QuoteRequestId` y `currentRequestId`.
5. `InternalsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Cps.kt` | CPS, continuaciones y máquina de estados | `callWithContinuation`, `Parking`, `RecordingContinuation`, `startBare`, `stateMachineFields` | `InternalsTest` (`a suspend function returns its value or the suspended marker`, `startCoroutine runs a coroutine without kotlinx`, `live locals become fields of the state machine`) |
| `Callbacks.kt` | Callbacks a `suspend` | `Cancellable`, `CallbackQuoteClient`, `quote`, `quoteIgnoringCancellation`, `resumeTwice` | `InternalsTest` (`a callback API becomes a suspend function`, `callback errors arrive as exceptions`, `cancelling the coroutine cancels the request`, `suspendCoroutine does not hear about cancellation`, `resuming twice throws`) |
| `Generators.kt` | Corrutinas restringidas | `priceWalk`, `orderLifecycle` | `InternalsTest` (`sequence is lazy and needs no threads`) |
| `Context.kt` | Elementos propios del contexto | `QuoteRequestId`, `currentRequestId` | `InternalsTest` (`context elements travel with the coroutine`) |

## Para profundizar

**Documentación oficial**

- [KEEP: Kotlin Coroutines][keep]: el diseño completo (continuaciones, máquina de estados, interceptores, corrutinas
  restringidas). Es largo y es la referencia.
- En la API: [`kotlin.coroutines`][stdlib-coroutines], [`Continuation`][continuation-api], [`sequence`][sequence-api] y
  [`suspendCancellableCoroutine`][scc-api].
- [Sequences][sequences] y [Debug coroutines using IntelliJ IDEA][debug-idea] (kotlinlang.org).

**Lecturas**

- [How does suspension work in Kotlin coroutines?][kt-suspension] y [Coroutines under the hood][kt-under-hood] (Marcin
  Moskała, Kt. Academy): la máquina de estados paso a paso, con el código equivalente.
- [Coroutine context and scope][elizarov-context] y [Explicit concurrency][elizarov-explicit] (Roman Elizarov): qué es el
  contexto y por qué `suspend` marca las funciones que pueden suspender.

**Videos**

- [Deep Dive into Coroutines on JVM][video-deep] (Roman Elizarov, KotlinConf 2017): CPS, máquina de estados e
  interceptores, por quien los diseñó.

## Estado

- **Hecho:** código y 10 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 3); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[keep]: https://github.com/Kotlin/KEEP/blob/master/proposals/coroutines.md
[stdlib-coroutines]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.coroutines/
[continuation-api]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.coroutines/-continuation/
[sequence-api]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/sequence.html
[scc-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/suspend-cancellable-coroutine.html
[sequences]: https://kotlinlang.org/docs/sequences.html
[debug-idea]: https://kotlinlang.org/docs/debug-coroutines-with-idea.html
[kt-suspension]: https://kt.academy/article/cc-suspension
[kt-under-hood]: https://kt.academy/article/cc-under-the-hood
[elizarov-context]: https://elizarov.medium.com/coroutine-context-and-scope-c8b255d59055
[elizarov-explicit]: https://elizarov.medium.com/explicit-concurrency-67a8e8fd9b25
[video-deep]: https://www.youtube.com/watch?v=YrrUCSi72E8
