# 02 · `rxjava` — RxJava desde Kotlin

**Sección:** `03-reactive` · **Paquete:** `dev.raul.oskar.learning.kotlin.rxjava` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#03--rxjava--rxjava-desde-kotlin) · [ejercicios](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/rxjava/Exercises.md) · [código](../../../03-reactive/src/main/kotlin/dev/raul/oskar/learning/kotlin/rxjava/) · [pruebas](../../../03-reactive/src/test/kotlin/dev/raul/oskar/learning/kotlin/rxjava/RxJavaTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Mantener código RxJava 3 (muy común en Android y en servicios viejos) desde Kotlin sin caer en sus trampas
(errores sin manejar, suscripciones que nadie libera, hilos equivocados) y migrarlo a corrutinas por partes.

## Teoría

### 1. Los cinco tipos

RxJava 3 separa por cardinalidad y por backpressure: `Observable` (0..N, **sin** backpressure), `Flowable` (0..N, con
backpressure, implementa Reactive Streams), `Single` (exactamente 1), `Maybe` (0 o 1) y `Completable` (solo termina o
falla). Reactor junta los tres últimos en `Mono`. Elegir mal el tipo es parte del problema: un `Observable` con un
productor rápido llena la memoria (`backpressure`). → [RxJava][rxjava], [What's different in 3.0][rx3-diff]

### 2. Errores: siempre `onError`

Un `subscribe { }` con solo el callback de valores no tiene a dónde mandar un error: RxJava lo envuelve en
`OnErrorNotImplementedException` y lo manda al manejador global (`RxJavaPlugins`), que por defecto lo imprime y lo
relanza en el hilo; en Android eso cierra la app. `errorWithoutOnError` lo captura con `RxJavaPlugins.setErrorHandler`
(`an error without onError goes to the global handler`). RxKotlin agrega `subscribeBy(onNext = …, onError = …)` con
argumentos con nombre, que evita confundir los callbacks de la sobrecarga de Java (`PriceWatcher`, `subscribeBy routes
errors to the named callback`). → [Error Handling][rx-errors], [RxKotlin][rxkotlin]

### 3. `Disposable`: liberar lo que se suscribe

Cada `subscribe` devuelve un `Disposable`. Si nadie lo libera, la suscripción sigue viva y retiene todo lo que capturó
(en Android, la pantalla entera: la fuga clásica). `CompositeDisposable` junta varias y `addTo` de RxKotlin las
agrega; `clear()` las libera y deja reusar el contenedor, `dispose()` lo cierra para siempre. `a TestScheduler gives
RxJava virtual time and disposing stops the stream` comprueba que después de `stop()` no llega nada más. Es lo que
structured concurrency hace sola con las corrutinas: un scope que se cancela cancela todo lo que lanzó.

### 4. `subscribeOn` y `observeOn`

`subscribeOn` decide en qué hilo corre la **fuente**, sin importar dónde se escriba en la cadena (si hay varios, gana el
más cercano a la fuente); `observeOn` cambia el hilo de todo lo que viene **después** de él. `threadsWith` lo muestra:
fuente en `RxCachedThreadScheduler` (`Schedulers.io()`), suscriptor en `RxComputationThreadPool`
(`Schedulers.computation()`). Mezclarlos sin entender cuál afecta a qué deja el trabajo bloqueante en el hilo de la
interfaz o el cálculo en el de E/S.

### 5. `null`: Kotlin lo ve, salvo en los platform types

RxJava tampoco admite `null`. Kotlin reconoce las anotaciones de nulabilidad de RxJava 3 (medido con el compilador):
`Observable.just(valorNullable)` y `map { null }` no compilan. Lo que sigue pasando es el `null` que viene de una API
Java sin anotar, como `System.getProperty`: compila (es un platform type) y falla en ejecución con
`NullPointerException("The mapper function returned a null value.")` (`nullFromJava`, y la prueba `a null from an
unannotated Java API passes the compiler and fails at runtime`). La misma lección que `reactor`.

### 6. Tiempo virtual: `TestScheduler`

`TestScheduler` es el reloj virtual de RxJava: los operadores con tiempo (`interval`, `delay`, `timeout`) reciben el
scheduler como parámetro, y la prueba avanza con `advanceTimeBy`. `pricesEverySecond(scheduler)` da tres precios en tres
segundos virtuales. `test()` devuelve un `TestObserver` con `assertValues`, `assertComplete`, `assertError`: el
`StepVerifier` de RxJava. → [RxJava][rxjava]

### 7. El mismo pipeline

La limpieza, las velas y los cruces, otra vez: `Observable.defer` para el estado por suscripción, `groupBy` por
(ticker, ventana) con `toList()` para las velas (RxJava no tiene `bufferUntilChanged`; sirve para un feed finito, en uno
infinito los grupos de ventanas viejas no se cerrarían) y `concatMap` con `just`/`empty` para los cruces. Las pruebas
comparan contra la referencia y contra los resultados de Reactor (`candles equal the reference and the Reactor
version`, `crossings match the Reactor version`).

### 8. Migrar a corrutinas sin reescribir todo

`kotlinx-coroutines-rx3` trae los puentes, y con ellos la migración va por partes, de abajo hacia arriba:

1. El código nuevo es `suspend` y consume la API vieja con `await()` (`quoteNew`): la suscripción se cancela si la
   corrutina se cancela.
2. Los flujos viejos entran al código nuevo con `asFlow()` (`candlesAsFlow`).
3. Lo que ya se reescribió se sigue ofreciendo a los llamadores viejos con `rxSingle { }` / `rxObservable { }`
   (`quoteForLegacyCallers`) hasta que todos migren.

Las pruebas `await and asFlow bring RxJava into coroutines` y `rxSingle offers suspend code to old callers` cubren las
dos direcciones; el ⚡ hace la migración completa con la misma batería en cada paso. →
[kotlinx-coroutines-rx3][rx3-api]

## Cómo correrla

```bash
./gradlew -p 03-reactive run -Plesson=rxjava
./gradlew -p 03-reactive test --tests '*.rxjava.*'
```

## Recorrido sugerido

1. `Semantics.kt`: el error sin `onError` (`errorWithoutOnError`), `PriceWatcher` con `subscribeBy` y `addTo`, el reloj
   virtual (`pricesEverySecond`), los hilos (`threadsWith`) y el `null` desde Java (`nullFromJava`).
2. `Pipeline.kt`: `cleaned`, `candles` y `crossings` en RxJava.
3. `Migration.kt`: `LegacyQuoteClient`, `quoteNew`, `candlesAsFlow` y `quoteForLegacyCallers`.
4. `RxJavaTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Semantics.kt` | Errores, suscripciones, hilos y `null` | `errorWithoutOnError`, `PriceWatcher`, `pricesEverySecond`, `threadsWith`, `nullFromJava`, `io` | `RxJavaTest` (`an error without onError goes to the global handler`, `a null from an unannotated Java API…`, `subscribeOn picks the source thread…`, `a TestScheduler gives RxJava virtual time…`, `subscribeBy routes errors to the named callback`) |
| `Pipeline.kt` | El pipeline del feed en RxJava | `cleaned`, `candles`, `crossings` | `RxJavaTest` (`TestObserver checks the pipeline`, `candles equal the reference and the Reactor version`, `crossings match the Reactor version`) |
| `Migration.kt` | Migración incremental | `LegacyQuoteClient`, `quoteNew`, `candlesAsFlow`, `quoteForLegacyCallers` | `RxJavaTest` (`await and asFlow bring RxJava into coroutines`, `rxSingle offers suspend code to old callers`) |

## Para profundizar

**Documentación oficial**

- [RxJava][rxjava] (README y wiki): los tipos, los schedulers y los operadores.
- [What's different in 3.0][rx3-diff]: lo que cambió desde RxJava 2 (paquetes, `null`, tipos).
- [Error Handling][rx-errors]: `onError`, `RxJavaPlugins` y `OnErrorNotImplementedException`.
- [RxKotlin][rxkotlin]: `subscribeBy`, `addTo`, `toObservable` y demás extensiones.
- [kotlinx-coroutines-rx3][rx3-api]: `await`, `asFlow`, `rxSingle`, `rxObservable`, `asCoroutineDispatcher`.

**Lecturas**

- [Backpressure (2.0)][rx-backpressure] (wiki de RxJava): por qué existen `Observable` y `Flowable` por separado; se
  retoma en `backpressure`.

**Videos**

- Sin video verificado para RxJava 3 desde Kotlin; la wiki oficial es la referencia.

## Estado

- **Hecho:** código y 10 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[rxjava]: https://github.com/ReactiveX/RxJava
[rx3-diff]: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-3.0
[rx-errors]: https://github.com/ReactiveX/RxJava/wiki/Error-Handling
[rxkotlin]: https://github.com/ReactiveX/RxKotlin
[rx3-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-rx3/
[rx-backpressure]: https://github.com/ReactiveX/RxJava/wiki/Backpressure-%282.0%29
