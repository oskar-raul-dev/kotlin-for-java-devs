# 02 · `cancellation` — Cooperativa, `withTimeout`, excepciones, `SupervisorJob`

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.cancellation` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#02--cancellation--cooperativa-withtimeout-excepciones-supervisorjob) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/cancellation/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/cancellation/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/cancellation/CancellationTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Cancelar sin dejar trabajo a medias, poner tiempos límite que se conviertan en errores de dominio, limpiar recursos
aunque la corrutina esté cancelada, y decidir cuándo una falla debe tumbar a las corrutinas hermanas y cuándo no.

## Teoría

### 1. La cancelación es cooperativa

En Java, `Thread.interrupt()` no detiene un hilo: marca una bandera, y el código tiene que mirarla (`isInterrupted`) o
estar en una llamada que la mire (`sleep`, `wait`, que lanzan `InterruptedException`). Las corrutinas funcionan igual:
`job.cancel()` pasa el `Job` a *Cancelling*, y la corrutina se detiene **la próxima vez que mira**. Todas las funciones
`suspend` de kotlinx.coroutines (`delay`, `await`, `withContext`, `yield`…) miran, así que el código normal se cancela
solo. Un bucle de cálculo sin puntos de suspensión, en cambio, no se entera: `processBatch` con `cooperative = false`
procesa el lote entero aunque lo cancelaron en el tercer elemento.

Para hacer cooperativo un bucle hay tres herramientas: `ensureActive()` (lanza `CancellationException` si ya la
cancelaron: la de `processBatch`), `isActive` (devuelve `false`, para salir del bucle a mano) y `yield()` (además cede
el hilo a otras corrutinas). → [Cancellation and timeouts][cancellation], [Cancellation in Kotlin Coroutines][kt-cancellation]

### 2. `CancellationException` no es un error

Una corrutina cancelada termina lanzando `CancellationException` desde su punto de suspensión. Esa excepción es el
**mecanismo** normal de cancelar, no una falla: quien espera la corrutina la ve terminar como "cancelada", y el padre
no se cancela porque un hijo fue cancelado (sí cuando un hijo falla). En Kotlin, `CancellationException` es un
`typealias` de `java.util.concurrent.CancellationException`, una `IllegalStateException`: por eso cae en cualquier
`catch (e: Exception)`.

La trampa más común al portar código: un `catch (e: Exception)` o un `runCatching` alrededor de una llamada `suspend`
atrapa la cancelación, y la corrutina **sigue trabajando** después de cancelada. `swallowingCancellation` lo muestra:
anota "siguió trabajando" y solo se detiene en el siguiente `delay`, que vuelve a lanzar porque el `Job` ya está
cancelado. Con un bucle de sondeo, eso es un bucle que gira sin esperar. La regla: relanzar siempre la cancelación
(`rethrowingCancellation`) o usar una variante de `runCatching` que la deje pasar (`runCatchingCancellable`, la misma
de `01-language/exceptions`). → [Cancellation in coroutines][vivo-cancellation]

### 3. Tiempos límite: `withTimeout` y `withTimeoutOrNull`

`withTimeoutOrNull(limit) { }` devuelve `null` si el bloque no termina a tiempo, como un `future.get(timeout)` que no
lanza (`rateOrNull`). `withTimeout(limit) { }` lanza `TimeoutCancellationException`, y aquí está la sutileza: esa
excepción **es** una `CancellationException`. Si nadie la convierte, la corrutina que la recibe termina "cancelada"
en silencio en vez de fallar (`an uncaught timeout ends a launch silently`: el `launch` queda cancelado y la prueba ni
se entera). Por eso `rateOrFail` la traduce a un error de dominio con causa (`FxTimeout`), igual que en `exceptions` se
envolvía la `IOException` del cliente de tasas.

En tiempo virtual, el límite salta en el instante exacto: `withTimeoutOrNull returns null exactly at the limit`
comprueba que `currentTime` es 100. → [`withTimeout`][withtimeout-api]

### 4. `finally` y `NonCancellable`: limpiar aunque te cancelen

Un `try/finally` funciona en una corrutina cancelada: el `finally` corre. Pero si el `finally` necesita **suspender**
(cerrar una conexión remota, revertir un débito con otra llamada), cualquier función `suspend` que mire la cancelación
lanza de inmediato, porque la corrutina sigue cancelada. La solución es `withContext(NonCancellable) { }`: un `Job` que
nunca está cancelado, solo para ese bloque.

`CompensatingTransfer` es el caso del Banco: débito, tasa con tiempo límite, crédito; si algo falla **o** la cancelan
después del débito, el `finally` revierte. Con `nonCancellable = false`, la prueba `without NonCancellable the
compensation never runs` muestra que el dinero desaparece: el débito quedó y la reversión nunca corrió. Una regla de
uso: `NonCancellable` solo va en limpieza; envolver trabajo normal en él rompe la cancelación del padre. → [Coroutines &
patterns for work that shouldn't be cancelled][vivo-noncancellable], [`NonCancellable`][noncancellable-api]

### 5. Cómo se propagan las excepciones

La regla de `fundamentals`, completa:

| Lo que pasa en un hijo | Qué le pasa al padre | Qué les pasa a los hermanos |
|---|---|---|
| Termina bien | Nada | Nada |
| Lo cancelan | Nada (la cancelación no es falla) | Nada |
| Falla con una excepción | Se cancela y relanza la excepción | Se cancelan |

Cuando fallan varios hijos, la primera excepción gana y las demás se agregan como `suppressed` (las cancelaciones de los
hermanos no se agregan). → [Coroutine exceptions handling][exceptions], [Exception handling in Kotlin
Coroutines][kt-exceptions]

### 6. `supervisorScope` y `SupervisorJob`: que una falla no tumbe a los demás

A veces la falla de un hijo no debería cancelar a los hermanos: el Mercado sigue a cada ticker por separado, y la caída
del feed de LUMA no tiene por qué cortar los otros cuatro. `supervisorScope { }` es un `coroutineScope` cuyos hijos
fallan solos (`followEach` devuelve un `Result` por ticker; `followAllOrNothing`, con `coroutineScope`, corta en la
primera falla). `SupervisorJob()` es lo mismo como `Job`, para un scope de larga vida:
`CoroutineScope(SupervisorJob() + …)`.

La trampa: `launch(SupervisorJob()) { }` **no** hace supervisada a esa corrutina. El `Job` que se pasa pasa a ser su
padre, así que deja de ser hija del scope: `coroutineScope` vuelve sin esperarla y nadie la cancela
(`detachedBySupervisorJob`). En kotlinx.coroutines 1.11.0 esa sobrecarga de `launch` ya da un warning de deprecación
("Passing a Job to coroutine builders breaks structured concurrency", medido), que anuncia que pasará a error.

### 7. `CoroutineExceptionHandler`: solo en la raíz

Es el `UncaughtExceptionHandler` de las corrutinas: recibe las excepciones que nadie más maneja, y solo se consulta en
la corrutina **raíz** (un `launch` directo de un scope sin padre que la maneje). `launchWithHandler` lo usa bien; en
`handlerOnChild`, el handler puesto en un hijo nunca corre, porque el hijo le pasa la excepción a su padre. En un
`async`, tampoco sirve: la excepción espera al `await`. → [`CoroutineExceptionHandler`][handler-api]

### 8. Cancelación de punta a punta

Lo que hace funcionar todo esto en un servidor: si el cliente corta la petición, el framework cancela la corrutina de
la petición, y la cancelación baja por cada `coroutineScope`, cada `withContext` y cada `await` hasta la llamada remota
que estaba esperando. Basta con que una capa intermedia se trague la `CancellationException` (o lance la llamada en
`GlobalScope`) para que la cadena se corte y el trabajo siga corriendo para nadie. Se retoma en `bridges` (Reactor),
`09-spring` y `10-ktor`.

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=cancellation
./gradlew -p 02-coroutines test --tests '*.cancellation.*'
```

## Recorrido sugerido

1. `Cooperative.kt`: el lote que ignora la cancelación y el que la mira (`processBatch`), la cancelación tragada y la
   relanzada (`swallowingCancellation`, `rethrowingCancellation`), y `runCatchingCancellable`.
2. `Timeouts.kt`: `rateOrNull` y `rateOrFail` con su error de dominio (`FxTimeout`).
3. `Compensation.kt`: las cuentas remotas simuladas (`RemoteAccounts`) y la transferencia con compensación
   (`CompensatingTransfer`), con y sin `NonCancellable`.
4. `Supervision.kt`: `followEach` frente a `followAllOrNothing`, el handler en la raíz y en un hijo
   (`launchWithHandler`, `handlerOnChild`) y la trampa de `launch(SupervisorJob())` (`detachedBySupervisorJob`).
5. `CancellationTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Cooperative.kt` | Cancelación cooperativa y la trampa de atraparla | `processBatch`, `swallowingCancellation`, `rethrowingCancellation`, `runCatchingCancellable` | `CancellationTest` (`a loop without checks ignores cancellation`, `ensureActive makes the loop cooperative`, `catching Exception swallows…`, `rethrowing keeps cancellation working`, `runCatching swallows cancellation…`) |
| `Timeouts.kt` | Tiempos límite y su traducción a dominio | `FxTimeout`, `rateOrNull`, `rateOrFail` | `CancellationTest` (`withTimeoutOrNull returns null exactly at the limit`, `withTimeout is a cancellation…`, `an uncaught timeout ends a launch silently`) |
| `Compensation.kt` | Limpieza que suspende | `RemoteAccounts`, `CompensatingTransfer` | `CancellationTest` (`a successful transfer debits and credits`, `a timeout reverts the debit`, `cancellation after the debit is compensated under NonCancellable`, `without NonCancellable the compensation never runs`) |
| `Supervision.kt` | Supervisión y handlers | `followEach`, `followAllOrNothing`, `launchWithHandler`, `handlerOnChild`, `detachedBySupervisorJob` | `CancellationTest` (`supervisorScope keeps the siblings alive`, `coroutineScope is all or nothing`, `the exception handler only works on the root coroutine`, `launch with a SupervisorJob leaves the hierarchy`) |

## Para profundizar

**Documentación oficial**

- [Cancellation and timeouts][cancellation] (kotlinlang.org): cooperativa, `finally`, `NonCancellable`, tiempos límite.
- [Coroutine exceptions handling][exceptions]: propagación, `CoroutineExceptionHandler`, supervisión y excepciones
  agregadas.
- En la API: [`withTimeout`][withtimeout-api], [`NonCancellable`][noncancellable-api],
  [`supervisorScope`][supervisorscope-api] y [`CoroutineExceptionHandler`][handler-api].

**Lecturas**

- [Cancellation in coroutines][vivo-cancellation], [Exceptions in coroutines][vivo-exceptions] y [Coroutines &
  patterns for work that shouldn't be cancelled][vivo-noncancellable] (Manuel Vivo, Android Developers): la serie más
  citada sobre el tema.
- [Cancellation in Kotlin Coroutines][kt-cancellation] y [Exception handling in Kotlin Coroutines][kt-exceptions]
  (Marcin Moskała, Kt. Academy): los capítulos de *Kotlin Coroutines: Deep Dive*, con los casos raros.

**Videos**

- [Coroutines! Gotta catch 'em all!][video-catch] (Florina Muntenescu y Manuel Vivo, KotlinConf 2019): cancelación y
  excepciones, con los errores típicos.
- [Kotlin Coroutines in Practice][video-practice] (Roman Elizarov, KotlinConf 2018): cancelación y supervisión en un
  ejemplo real.

## Estado

- **Hecho:** código y 16 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 1); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[cancellation]: https://kotlinlang.org/docs/cancellation-and-timeouts.html
[exceptions]: https://kotlinlang.org/docs/exception-handling.html
[withtimeout-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-timeout.html
[noncancellable-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-non-cancellable/
[supervisorscope-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/supervisor-scope.html
[handler-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/
[vivo-cancellation]: https://medium.com/androiddevelopers/cancellation-in-coroutines-aa6b90163629
[vivo-exceptions]: https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c
[vivo-noncancellable]: https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
[kt-cancellation]: https://kt.academy/article/cc-cancellation
[kt-exceptions]: https://kt.academy/article/cc-exception-handling
[video-catch]: https://www.youtube.com/watch?v=w0kfnydnFWI
[video-practice]: https://www.youtube.com/watch?v=a3agLJQ6vt8
