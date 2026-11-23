# 01 · `fundamentals` — `suspend`, structured concurrency, `Job`

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.fundamentals` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#02--fundamentals--suspend-structured-concurrency-job) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/fundamentals/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/fundamentals/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/fundamentals/FundamentalsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Escribir código concurrente que se lee como secuencial, con corrutinas que viven dentro de un scope y nunca quedan
huérfanas; saber qué es (y qué no es) una función `suspend`, y cuándo usar `launch`, `async` y `coroutineScope`.

## Teoría

### 1. El problema: hilos caros y callbacks que no se leen

En Java 21 hay tres formas clásicas de hacer cinco llamadas remotas a la vez. Con un hilo por llamada
(`executor.submit` + `future.get()`), el código se lee en orden pero cada llamada ocupa un hilo de plataforma bloqueado
(cerca de 1 MB de pila reservada y un hilo del sistema operativo). Con `CompletableFuture` (`thenCompose`,
`thenCombine`), los hilos quedan libres pero el código se convierte en una cadena de callbacks donde los `try/catch`,
los bucles y los `return` dejan de funcionar como siempre. Y con hilos virtuales (JEP 444, finales en 21), bloquear
vuelve a ser barato, pero la cancelación y el manejo de errores entre tareas siguen siendo cosa de cada uno (se compara
en `loom`).

Las corrutinas de Kotlin resuelven las dos cosas a la vez: el código se escribe en orden, con `try`, `for` y `return`
normales, y mientras espera no ocupa ningún hilo. En la lección, `quoteSequentially` y `quoteInParallel` son el mismo
código; solo cambia que el segundo envuelve cada llamada en `async`. → [Coroutines][overview], [Why using Kotlin
Coroutines?][kt-why]

### 2. Qué es una función `suspend` (y qué no)

`suspend` marca una función que **puede pausarse** sin bloquear el hilo: en un punto de suspensión (`delay`, `await`,
otra función `suspend` que suspende), la corrutina guarda su estado y el hilo queda libre para otra cosa; cuando el
resultado llega, la corrutina sigue donde estaba, quizás en otro hilo. El compilador lo logra reescribiendo la función
(estilo de paso de continuaciones y una máquina de estados: se ve en `internals`).

Lo que `suspend` **no** quiere decir: "corre en otro hilo". Una función `suspend` corre en el hilo de quien la llama
hasta su primer punto de suspensión, y si adentro hay JDBC o `Thread.sleep`, bloquea ese hilo igual
(`currentThreadName` lo demuestra: devuelve el hilo de la prueba). Elegir el hilo es trabajo del dispatcher
(`dispatchers`). Y una función `suspend` solo se puede llamar desde otra función `suspend` o desde un constructor de
corrutinas (`runBlocking`, `launch`, `async`). → [Coroutines basics][basics], [Blocking threads, suspending
coroutines][elizarov-blocking]

### 3. Los constructores: `runBlocking`, `launch`, `async`

- `runBlocking { }` une el mundo bloqueante con el de las corrutinas: **bloquea** el hilo actual hasta que el bloque
  termina. Va en `main` y en pruebas, nunca dentro de una corrutina (bloquearía un hilo que el dispatcher necesita; en
  uno de un solo hilo, se cuelga).
- `launch { }` arranca una corrutina "de efecto" y devuelve un `Job`: se puede esperar (`join`) o cancelar, pero no
  trae resultado.
- `async { }` arranca una corrutina que calcula algo y devuelve un `Deferred<T>` (un `Job` con resultado):
  `await()` **suspende** hasta tenerlo, a diferencia de `future.get()`, que bloquea.

`launchVersusAsync` muestra los dos lado a lado. Una trampa clásica: `tickers.map { async { … }.await() }` es
secuencial, porque espera cada `async` antes de lanzar el siguiente; el paralelismo sale de lanzar todos primero y
esperar después (`awaitAll`, como en `quoteInParallel`). → [Composing suspending functions][composing]

### 4. Structured concurrency: toda corrutina tiene un dueño

La idea central de kotlinx.coroutines: las corrutinas se lanzan **dentro de un scope**, y el scope no termina hasta que
terminan todas sus hijas. `coroutineScope { }` crea uno: la función que lo usa no vuelve mientras quede trabajo
lanzado adentro, así que nada queda corriendo "por detrás" después de que la función devolvió. Eso da tres garantías:

1. **Sin fugas:** cuando `quoteInParallel` vuelve, no queda ninguna consulta viva.
2. **Cancelación en cascada:** cancelar el scope cancela a todas sus hijas.
3. **Errores que no se pierden:** si una hija falla, el scope cancela a las demás y relanza la excepción a quien
   llamó.

Es la misma idea que Nathaniel J. Smith describió como "go statement considered harmful" y que Java está adoptando con
`StructuredTaskScope` (JEP 453, en vista previa en el JDK 21 y todavía en vista previa después). En Kotlin es estable
desde kotlinx.coroutines 1.0 (2018). → [Structured concurrency][elizarov-structured], [Notes on structured
concurrency][go-harmful], [JEP 453][jep453]

### 5. La jerarquía de `Job`

Cada corrutina tiene un `Job`, y el `Job` de una hija es hijo del `Job` de su padre. De ahí salen las reglas:

- El padre espera a sus hijos: `launchVersusAsync` vuelve a los 100 ms aunque nadie hizo `join` del `launch`.
- Cancelar al padre cancela a los hijos (`cancelParentCancelsChildren`): `future.cancel(true)` en Java no sabe nada de
  las tareas que ese future lanzó.
- Si un hijo **falla** (excepción que no es cancelación), cancela al padre, y el padre cancela a los hermanos
  (`one failing source cancels its siblings`: todas las consultas empezaron, solo terminó la de 50 ms). Si un hijo es
  **cancelado**, el padre no se entera como falla (se ve en `cancellation`).

Un `Job` pasa por los estados *New* (con `CoroutineStart.LAZY`), *Active*, *Completing*, *Completed*, *Cancelling* y
*Cancelled*; `isActive`, `isCompleted` e `isCancelled` los resumen. → [Job and children awaiting][kt-job]

### 6. `async` falla aunque nadie llame a `await`

En Java, una excepción dentro de un `CompletableFuture` queda guardada hasta que alguien llama a `get()` o `join()`; si
nadie lo hace, se pierde en silencio. En un `async` hijo, la excepción cancela al scope padre **en cuanto ocurre**,
haya o no `await` (`asyncFailureWithoutAwait`: el hermano queda cancelado y el scope relanza a los 10 ms). Es una
consecuencia de la regla anterior, y es lo que hace que los errores no se pierdan. → [Exception handling][exceptions]

### 7. `GlobalScope`: la corrutina sin dueño

`GlobalScope.launch` crea una corrutina que no es hija de nadie: nadie la espera, nadie la cancela, y sus errores van
al manejador global. Es el equivalente de crear un hilo suelto. En kotlinx.coroutines 1.11.0 usarlo pide
`@OptIn(DelicateCoroutinesApi::class)` (`leakWithGlobalScope`), y la prueba `GlobalScope leaks while an owned scope
does not` muestra la diferencia: el `coroutineScope` terminó y la corrutina global sigue viva. Lo correcto es recibir
un `CoroutineScope` de quien maneja el ciclo de vida (`ownedLaunch`): el de la petición en un servidor, el del
`ViewModel` en Android. → [The reason to avoid GlobalScope][elizarov-globalscope], [`GlobalScope`][globalscope-api]

### 8. Por qué las corrutinas son baratas

Una corrutina suspendida es un objeto en el heap con las variables que necesita para seguir: unos cientos de bytes.
Por eso `manyCoroutines(100_000)` termina en un poco más de un segundo real (la demo lo mide), mientras que cien mil
hilos de plataforma agotarían la memoria o el límite del sistema operativo. No es magia: el trabajo bloqueante sigue
necesitando hilos (`dispatchers`), y una corrutina que nunca suspende ocupa su hilo como cualquier otra tarea.

### 9. Tiempo virtual: probar `delay` sin esperar

Las pruebas de la lección usan `runTest` de `kotlinx-coroutines-test`: dentro de él, `delay` no espera de verdad sino
que avanza un reloj virtual, y `currentTime` dice cuánto avanzó. Así se puede afirmar que el fan-out tarda **exactamente**
300 ms (la fuente más lenta) y no 700 (la suma), sin que la prueba tarde nada. En 1.11.0, `currentTime` pide
`@OptIn(ExperimentalCoroutinesApi::class)` (medido); por eso las pruebas lo declaran a nivel de archivo. Se profundiza
en `testing`. → [kotlinx-coroutines-test][test-api]

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=fundamentals
./gradlew -p 02-coroutines test --tests '*.fundamentals.*'
```

## Recorrido sugerido

1. `cases/` (compartido por la sección): la copia mínima de `Money`, los cinco tickers con su precio y latencia de
   referencia (`REFERENCE_PRICES`, `REFERENCE_LATENCIES`) y los clientes `suspend` del Banco (`FxRateClient`,
   `BalanceClient`).
2. `Quotes.kt`: `PriceSource`, la fuente simulada que anota qué empezó y qué terminó (`SimulatedPriceSource`), el mismo
   código en serie y en paralelo (`quoteSequentially`, `quoteInParallel`), `portfolioValue` y la consulta doble del Banco
   (`prepareTransfer`).
3. `Structured.kt`: `suspend` no cambia de hilo (`currentThreadName`), `launch` frente a `async`, la falla de un
   `async` sin `await`, la cancelación del padre, cien mil corrutinas y la fuga de `GlobalScope`.
4. `FundamentalsTest.kt`: cada regla con su tiempo virtual exacto.
5. `Main.kt`: lo mismo con tiempo real (la demo imprime cuánto tardó cada variante).

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `cases/Money.kt`, `cases/Market.kt`, `cases/Bank.kt` | Copias mínimas de los casos para la sección | `Money`, `sum`, `TICKERS`, `REFERENCE_PRICES`, `REFERENCE_LATENCIES`, `Tick`, `FxRateClient`, `BalanceClient` | (se usan en todas las pruebas de la sección) |
| `Quotes.kt` | Fan-out con `async` en un `coroutineScope` | `PriceSource`, `SimulatedPriceSource`, `QuoteUnavailable`, `quoteSequentially`, `quoteInParallel`, `portfolioValue`, `TransferQuote`, `prepareTransfer` | `FundamentalsTest` (`parallel quotes take as long as the slowest source`, `sequential quotes add up every latency`, `portfolio value…`, `one failing source cancels its siblings`, `the bank asks for balance and rate at the same time`) |
| `Structured.kt` | Reglas de structured concurrency | `currentThreadName`, `launchVersusAsync`, `asyncFailureWithoutAwait`, `cancelParentCancelsChildren`, `manyCoroutines`, `leakWithGlobalScope`, `ownedLaunch` | `FundamentalsTest` (`suspend does not mean another thread`, `coroutineScope waits for launch and async`, `an async failure cancels the scope even without await`, `cancelling the parent cancels every child`, `a hundred thousand coroutines are cheap`, `GlobalScope leaks while an owned scope does not`) |

## Para profundizar

**Documentación oficial**

- [Coroutines][overview] y [Coroutines basics][basics] (kotlinlang.org): el punto de partida, con `launch`, scopes y
  structured concurrency.
- [Composing suspending functions][composing]: `async`, `await`, `CoroutineStart.LAZY` y el estilo "async" que no hay
  que usar.
- [`coroutineScope`][coroutinescope-api] y [`GlobalScope`][globalscope-api] en la API: lo que garantiza cada uno.
- [KEEP: Kotlin Coroutines][keep]: el diseño completo (continuaciones, `suspend`, por qué no `async`/`await` como
  palabras clave).

**Lecturas**

- [Structured concurrency][elizarov-structured] y [The reason to avoid GlobalScope][elizarov-globalscope] (Roman
  Elizarov): por qué la librería cambió en 2018 para que toda corrutina tenga dueño.
- [Blocking threads, suspending coroutines][elizarov-blocking] (Roman Elizarov): la diferencia exacta entre bloquear
  y suspender.
- [Notes on structured concurrency, or: Go statement considered harmful][go-harmful] (Nathaniel J. Smith): el ensayo
  que dio nombre a la idea.
- [Why using Kotlin Coroutines?][kt-why] y [Job and children awaiting][kt-job] (Marcin Moskała, Kt. Academy): los
  capítulos de *Kotlin Coroutines: Deep Dive* sobre el porqué y la jerarquía de `Job`.
- [Coroutines: first things first][vivo-first] (Manuel Vivo, Android Developers): scopes, `Job` y contexto, en corto.
- [JEP 453: Structured Concurrency (Preview)][jep453]: la versión de Java, para comparar.

**Videos**

- [Kotlin Coroutines in Practice][video-practice] (Roman Elizarov, KotlinConf 2018): structured concurrency con un
  ejemplo real, de la mano de quien la diseñó.
- [Structured concurrency][video-structured] (Roman Elizarov, Hydra 2019): la idea a fondo y su historia.
- [Kotlin Coroutines 101][video-101] (Android Developers): introducción corta y clara.

## Estado

- **Hecho:** código y 11 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 1); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[overview]: https://kotlinlang.org/docs/coroutines-overview.html
[basics]: https://kotlinlang.org/docs/coroutines-basics.html
[composing]: https://kotlinlang.org/docs/composing-suspending-functions.html
[exceptions]: https://kotlinlang.org/docs/exception-handling.html
[coroutinescope-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/coroutine-scope.html
[globalscope-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/
[test-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/
[keep]: https://github.com/Kotlin/KEEP/blob/master/proposals/coroutines.md
[elizarov-structured]: https://elizarov.medium.com/structured-concurrency-722d765aa952
[elizarov-globalscope]: https://elizarov.medium.com/the-reason-to-avoid-globalscope-835337445abc
[elizarov-blocking]: https://elizarov.medium.com/blocking-threads-suspending-coroutines-d33e11bf4761
[go-harmful]: https://vorpus.org/blog/notes-on-structured-concurrency-or-go-statement-considered-harmful/
[kt-why]: https://kt.academy/article/cc-why
[kt-job]: https://kt.academy/article/cc-job
[vivo-first]: https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21
[jep453]: https://openjdk.org/jeps/453
[video-practice]: https://www.youtube.com/watch?v=a3agLJQ6vt8
[video-structured]: https://www.youtube.com/watch?v=Mj5P47F6nJg
[video-101]: https://www.youtube.com/watch?v=ZTDXo0-SKuU
