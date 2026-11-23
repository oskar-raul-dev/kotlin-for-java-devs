# 09 · `testing` — `runTest`, tiempo virtual, Turbine

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.testing` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#02--testing--runtest-tiempo-virtual-turbine) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/testing/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/testing/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/testing/TestingTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Probar código con corrutinas, tiempos y flujos de forma determinista y en milisegundos: tiempo virtual, dispatchers
de prueba, `backgroundScope`, Turbine y cómo diseñar el código (dispatchers inyectados) para que se pueda probar.

## Teoría

### 1. El problema: `Thread.sleep` en las pruebas

En Java, probar algo que espera 5 minutos o reintenta con espera exponencial termina en `Thread.sleep`, Awaitility o
relojes falsos hechos a mano: pruebas lentas, y frágiles cuando la máquina está cargada. Con corrutinas, el tiempo pasa
por `delay`, y `delay` lo controla el dispatcher. `kotlinx-coroutines-test` aprovecha eso con un planificador de
**tiempo virtual**. → [kotlinx-coroutines-test][test-readme], [Testing Kotlin Coroutines][kt-testing]

### 2. `runTest` y el tiempo virtual

`runTest { }` corre la prueba con un `TestCoroutineScheduler`: cuando todas las corrutinas están esperando, el
planificador salta el reloj al siguiente evento en vez de esperar. `delay(5.minutes)` vuelve al instante y
`currentTime` dice 300 000 (`runTest skips delays with virtual time`, que mide menos de un segundo de reloj real). Por
contraste, `runBlocking` usa tiempo real (`runBlocking uses real time`). `runTest` tiene además su propio tiempo límite
**de reloj real** para la prueba entera (el virtual no cuenta), para que una prueba colgada no cuelgue la build.

En 1.11.0, varias funciones de uso diario siguen marcadas: `currentTime`, `runCurrent`, `advanceTimeBy`,
`advanceUntilIdle`, `UnconfinedTestDispatcher`, `setMain` y `resetMain` piden `@OptIn(ExperimentalCoroutinesApi::class)`
(medido); `runTest` y `StandardTestDispatcher`, no. Las pruebas de la sección lo declaran a nivel de archivo. →
[`runTest`][runtest-api]

### 3. Controlar el reloj a mano

- `advanceTimeBy(n)`: avanza n ms y corre lo programado **antes** de ese instante (lo que cae exactamente en n queda
  pendiente).
- `runCurrent()`: corre lo que está listo en el instante actual.
- `advanceUntilIdle()`: corre todo hasta que no quede nada pendiente.

`advanceTimeBy stops just before the instant and runCurrent runs what is due` usa los tres para ver la tasa del
monitor ausente a los 99 ms y presente a los 100.

### 4. Inyectar el dispatcher, o el tiempo virtual no llega

El reloj virtual solo controla las corrutinas que corren en dispatchers de prueba que comparten el planificador. Un
`withContext(Dispatchers.IO)` fijo dentro del código saca al `delay` del reloj de la prueba: `HardcodedIoQuoter` hace
que la prueba espere 300 ms reales mientras `currentTime` sigue en 0 (`a hardcoded IO dispatcher escapes virtual
time`). Por eso `RateMonitor` recibe el scope **y** el dispatcher: en la prueba, `StandardTestDispatcher(testScheduler)`,
y una hora de sondeo cada minuto se prueba en milisegundos (`an injected test dispatcher keeps the service in virtual
time`). Todos los dispatchers de prueba de una prueba deben compartir el mismo `testScheduler`. → [Testing Kotlin
coroutines on Android][android-test]

### 5. `StandardTestDispatcher` frente a `UnconfinedTestDispatcher`

`StandardTestDispatcher` encola las corrutinas y no corre nada hasta que la prueba cede (avanza el tiempo, llama a
`runCurrent` o suspende); `UnconfinedTestDispatcher` las arranca en el acto, dentro del `launch`. `standard and unconfined
test dispatchers order work differently` muestra los dos órdenes con el mismo `launchOrder`. Regla práctica: Standard por
defecto (el orden se controla), Unconfined para colectores que tienen que ver cada valor al momento.

### 6. `backgroundScope`

Una corrutina que nunca termina (un sondeo, un colector de un `StateFlow`) lanzada en el scope de la prueba la deja
esperando hasta su tiempo límite. `backgroundScope` es un scope que `runTest` cancela al terminar: ahí van esos trabajos
(`backgroundScope is cancelled when the test ends`).

### 7. Turbine y los flujos

Turbine recolecta un `Flow` dentro de `test { }` y deja comprobar emisión por emisión: `awaitItem()`, `awaitError()`,
`awaitComplete()`, `expectNoEvents()`, `cancelAndIgnoreRemainingEvents()`, con el tiempo virtual
(`Turbine checks emissions one by one`). Un detalle medido que corrige una idea común: Turbine recolecta **sin
confinar**, así que ve cada valor que la prueba asigna a un `StateFlow`, aunque no suspenda entre medio. La conflación
aparece con un colector que no está corriendo: uno sobre `StandardTestDispatcher` que no recibe turno entre dos
asignaciones pierde la intermedia (`a StateFlow conflates for a collector that is not running`). → [Turbine][turbine]

### 8. `Dispatchers.Main` en pruebas

En la JVM no hay `Dispatchers.Main` (usarlo falla con "Module with the Main dispatcher is missing", medido en
`dispatchers`). `Dispatchers.setMain(StandardTestDispatcher(testScheduler))` lo reemplaza por uno de prueba en el reloj
virtual, y `resetMain()` lo restaura (`setMain replaces Dispatchers Main in tests`). Es lo que se usa para probar
`ViewModel`s en Android (`12-android`).

### 9. Determinismo: la misma sesión, siempre el mismo resultado

Con datos generados con semilla y tiempo virtual, una prueba es reproducible por construcción: `an hour of market
session in milliseconds and always the same result` procesa una hora de feed (18 000 ticks) tres veces y obtiene un solo
resultado, en una fracción de segundo. Lo que **no** se puede probar así son las carreras de datos entre hilos de verdad
(`sync`, `dispatchers`): esas pruebas usan hilos reales, márgenes amplios y repeticiones.

## Cómo correrla

```bash
./gradlew -p 02-coroutines test --tests '*.testing.*'
./gradlew -p 02-coroutines run -Plesson=testing      # la misma función con tiempo real, para comparar
```

## Recorrido sugerido

1. `Services.kt`: lo que se prueba: el monitor con dispatcher inyectado (`RateMonitor`), la versión que no se puede
   probar (`HardcodedIoQuoter`), el tiempo límite de una transferencia (`withTransferDeadline`, `TransferTimedOut`) y
   el orden de los dispatchers de prueba (`launchOrder`).
2. `Session.kt`: el feed de una sesión (`sessionFeed`) y su resumen (`summarize`, `SessionSummary`).
3. `TestingTest.kt`: el centro de la lección; cada técnica en una prueba.
4. `Main.kt`: un minuto de sesión con tiempo real.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Services.kt` | Código diseñado para probarse (y uno que no) | `RateMonitor`, `HardcodedIoQuoter`, `TransferTimedOut`, `withTransferDeadline`, `launchOrder` | `TestingTest` (`an injected test dispatcher keeps the service in virtual time`, `advanceTimeBy stops just before the instant…`, `a hardcoded IO dispatcher escapes virtual time`, `the transfer deadline fires at the exact instant`, `standard and unconfined test dispatchers order work differently`, `Turbine checks emissions one by one`) |
| `Session.kt` | Una sesión de mercado en tiempo virtual | `SessionSummary`, `sessionFeed`, `summarize` | `TestingTest` (`an hour of market session in milliseconds and always the same result`) |
| (solo pruebas) | Reloj virtual y real, `backgroundScope`, conflación, `setMain` | — | `TestingTest` (`runTest skips delays with virtual time`, `runBlocking uses real time`, `backgroundScope is cancelled when the test ends`, `a StateFlow conflates for a collector that is not running`, `setMain replaces Dispatchers Main in tests`) |

## Para profundizar

**Documentación oficial**

- [kotlinx-coroutines-test][test-readme] (README de la librería): `runTest`, dispatchers de prueba, `backgroundScope`,
  `setMain` y la migración desde las APIs viejas (`runBlockingTest`).
- [`runTest`][runtest-api] en la API: el tiempo límite y cómo se comporta con hijos que no terminan.
- [Testing Kotlin coroutines on Android][android-test] (Android Developers): inyectar dispatchers, `setMain` y probar
  `ViewModel`s; vale fuera de Android.

**Lecturas**

- [Testing Kotlin Coroutines][kt-testing] (Marcin Moskała, Kt. Academy): tiempo virtual, dispatchers de prueba y los
  errores típicos.
- [Turbine][turbine] (Cash App): la API completa, `turbineScope` y `testIn`.

**Videos**

- [Kotlin Flows in practice][video-flows-practice] (Android Developers): incluye cómo probar flujos.

## Estado

- **Hecho:** código y 12 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 3); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[test-readme]: https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/README.md
[runtest-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/run-test.html
[android-test]: https://developer.android.com/kotlin/coroutines/test
[kt-testing]: https://kt.academy/article/cc-testing
[turbine]: https://github.com/cashapp/turbine
[video-flows-practice]: https://www.youtube.com/watch?v=fSB6_KE95bU
