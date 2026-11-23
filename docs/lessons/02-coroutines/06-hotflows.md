# 06 · `hotflows` — `StateFlow`, `SharedFlow`

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.hotflows` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#02--hotflows--stateflow-sharedflow) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/hotflows/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/hotflows/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/hotflows/HotFlowsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Publicar estado y eventos a varios suscriptores con flujos calientes, convertir uno frío en caliente sin fugas
(`stateIn`, `shareIn`, `WhileSubscribed`) y saber exactamente qué recibe un suscriptor que llega tarde.

## Teoría

### 1. Caliente frente a frío

Un `Flow` frío (`flow`) no existe hasta que alguien lo recolecta, y cada colector tiene su propia ejecución. Un flujo
**caliente** existe por su cuenta: tiene estado o emite aunque nadie escuche, y todos los suscriptores comparten la
misma fuente. Es la diferencia entre llamar a un servicio cada vez que alguien pregunta y tener un tablero que todos
miran. En RxJava y Reactor esto son los `Subject` y los `Sinks`; en kotlinx.coroutines, `SharedFlow` y su caso
particular `StateFlow`, que reemplazaron a `BroadcastChannel` y `ConflatedBroadcastChannel`. → [StateFlow and
SharedFlow][android-state], [Hot and cold data sources][kt-hot-cold], [Shared flows, broadcast channels][elizarov-shared]

### 2. `StateFlow`: un valor que se puede observar

`MutableStateFlow(inicial)` siempre tiene un `value`; quien se suscribe recibe **el valor actual** en el acto y después
cada cambio (`a late subscriber gets the current price at once`). Es un `AtomicReference` con listeners, o el
`BehaviorSubject` de RxJava. En `PriceBoard`, el último precio de cada ticker es un `StateFlow<Money?>` (`null` hasta el
primer tick), expuesto de solo lectura con `asStateFlow()` para que nadie de afuera lo modifique.

Dos detalles que definen su comportamiento:

- **Compara con `equals`**: asignar un valor igual al actual no emite (`StateFlow only emits when the value changes`).
  La trampa: mutar un objeto (una `MutableList`) y volver a asignar **la misma referencia** no avisa a nadie, porque es
  igual a sí misma (`mutating the same list in place notifies nobody`). Con `StateFlow` se usan valores inmutables y se
  asigna uno nuevo (`copy`, `+`).
- **Es *conflated***: un suscriptor lento ve el último valor, no todos los intermedios. Para estado de pantalla es lo
  correcto; para una secuencia de eventos que hay que procesar completa, no.

→ [`StateFlow`][stateflow-api]

### 3. `update`: leer y escribir sin perder cambios

`state.value = state.value + 1` desde varias corrutinas pierde incrementos: dos leen el mismo valor y escriben el mismo
resultado (con 100 corrutinas en `Default`, la demo pierde la mayoría). `state.update { it + 1 }` hace el ciclo
leer-calcular-`compareAndSet` en un bucle, como `AtomicReference.updateAndGet` (`update is atomic across threads`).
`LiveBalances.post` lo usa para que dos asientos concurrentes sobre la misma cuenta no se pisen. La función que se le
pasa puede correr más de una vez si hay competencia: no debe tener efectos.

### 4. `SharedFlow`: eventos para muchos

`MutableSharedFlow<T>(replay, extraBufferCapacity, onBufferOverflow)` no tiene "valor actual": emite eventos a quienes
estén suscritos en ese momento. `replay` decide cuántos de los últimos eventos recibe quien llega tarde (0 por defecto:
ninguno). Las alertas del tablero son un `SharedFlow` sin replay: quien se suscribe después no ve las alertas viejas
(`alerts are events that late subscribers do not see`). Es el `PublishSubject` de RxJava, o un bus de eventos.

La trampa: con `replay = 0` y sin suscriptores, lo emitido **se pierde** sin error (`tryEmit` devuelve `true` y el valor
no queda en ningún lado: `a SharedFlow without subscribers and replay loses its events`). → [`SharedFlow`][sharedflow-api]

### 5. Backpressure en un flujo caliente

Un `SharedFlow` tiene un buffer (replay más `extraBufferCapacity`). Si un suscriptor es lento y el buffer se llena,
`emit` **suspende** a quien emite hasta que el lento avance (`a slow subscriber with a full buffer suspends the
emitter`), y con él a todos los demás suscriptores, que dejan de recibir. Para no frenar al feed se elige qué descartar
con `onBufferOverflow = DROP_OLDEST` o `DROP_LATEST` (entonces `emit` nunca suspende). Precios: se puede descartar.
Ejecuciones: no; ahí conviene un canal o un buffer grande y vigilado.

### 6. De frío a caliente: `stateIn` y `shareIn`

Lo más común en una aplicación es tener un `Flow` frío y caro (el feed, una consulta que observa la base) y varios
consumidores. `stateIn(scope, started, inicial)` lo convierte en un `StateFlow` compartido; `shareIn(scope, started,
replay)`, en un `SharedFlow`. La fuente corre **una sola vez**, en el `scope` que se pasa, y el parámetro `started`
decide cuándo:

| `SharingStarted` | Arranca | Se detiene |
|---|---|---|
| `Eagerly` | Ya | Nunca (hasta que se cancela el scope) |
| `Lazily` | Con el primer suscriptor | Nunca |
| `WhileSubscribed(stopTimeoutMillis)` | Con el primer suscriptor | `stopTimeoutMillis` después del último |

`WhileSubscribed(5000)` es la recomendación habitual: el período de gracia de 5 s evita reiniciar la fuente cuando una
pantalla se recrea (una rotación en Android) o un cliente se reconecta. `SharedLatest` lo implementa, y la prueba
`WhileSubscribed stops the source exactly five seconds after the last subscriber` lo comprueba con tiempo virtual: la
fuente sigue viva a los 4 999 ms y se detiene a los 5 000. La trampa opuesta: `stateIn(appScope, Eagerly, …)` en un
scope de larga vida deja la fuente corriendo para siempre aunque nadie mire. → [`stateIn`][statein-api],
[`SharingStarted`][sharingstarted-api], [Things to know about Flow's shareIn and stateIn operators][vivo-sharein]

### 7. Un solo escritor por estado

El patrón que usan los tres ejemplos de la lección: el `MutableStateFlow` es privado, una sola clase lo modifica (con
`update` cuando hay concurrencia) y todos los demás reciben un `StateFlow` de solo lectura. Si dos piezas de estado
tienen que ser consistentes entre sí (el saldo y la lista de movimientos), van en **un solo** `StateFlow` con un
`data class`, no en dos: con dos, un observador puede ver uno actualizado y el otro no.

### 8. Probar flujos calientes

`collect` sobre un `StateFlow` o un `SharedFlow` nunca termina. En pruebas se usa Turbine (`test { awaitItem() }`, que
cancela al salir), `first { }`, `take(n)`, o un colector lanzado en `backgroundScope`, que `runTest` cancela solo al
terminar. Para no perder valores de un `StateFlow` que se emiten sin suspender, el colector va con
`UnconfinedTestDispatcher` (como en `StateFlow only emits when the value changes`). Se profundiza en `testing`.

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=hotflows
./gradlew -p 02-coroutines test --tests '*.hotflows.*'
```

## Recorrido sugerido

1. `PriceBoard.kt`: un `StateFlow` por ticker y un `SharedFlow` de alertas (`PriceBoard`, `MoveAlert`).
2. `Semantics.kt`: `equals` y conflación (`sameValueDoesNotEmit`, `mutatingInPlaceDoesNotEmit`), `update`
   (`concurrentIncrements`), eventos perdidos (`emitWithoutSubscribers`), buffer y descarte (`alertsBus`) y
   `WhileSubscribed` (`SharedLatest`).
3. `Balances.kt`: los saldos del Banco como `StateFlow` (`Posting`, `LiveBalances`).
4. `HotFlowsTest.kt` (con Turbine y `backgroundScope`) y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `PriceBoard.kt` | Estado y eventos del Mercado | `MoveAlert`, `PriceBoard` | `HotFlowsTest` (`a late subscriber gets the current price at once`, `alerts are events that late subscribers do not see`) |
| `Semantics.kt` | Reglas de `StateFlow`, `SharedFlow` y `stateIn` | `sameValueDoesNotEmit`, `mutatingInPlaceDoesNotEmit`, `concurrentIncrements`, `emitWithoutSubscribers`, `alertsBus`, `SharedLatest` | `HotFlowsTest` (`StateFlow only emits when the value changes`, `mutating the same list in place notifies nobody`, `update is atomic across threads`, `a SharedFlow without subscribers and replay loses its events`, `a slow subscriber with a full buffer suspends the emitter`, `WhileSubscribed stops the source exactly five seconds after the last subscriber`) |
| `Balances.kt` | Saldos en vivo del Banco | `Posting`, `LiveBalances` | `HotFlowsTest` (`balances update with each posting and keep double entry`) |

## Para profundizar

**Documentación oficial**

- [StateFlow and SharedFlow][android-state] (Android Developers): la guía más clara de los dos tipos, con `stateIn` y
  `shareIn`; vale fuera de Android.
- En la API: [`StateFlow`][stateflow-api], [`SharedFlow`][sharedflow-api] (buffer, replay y overflow, con su
  tabla), [`stateIn`][statein-api] y [`SharingStarted`][sharingstarted-api].

**Lecturas**

- [Shared flows, broadcast channels][elizarov-shared] (Roman Elizarov): por qué se diseñaron y qué reemplazaron.
- [Things to know about Flow's shareIn and stateIn operators][vivo-sharein] (Manuel Vivo, Android Developers):
  `WhileSubscribed(5000)` y los errores típicos.
- [Hot and cold data sources][kt-hot-cold] y [SharedFlow and StateFlow][kt-shared] (Marcin Moskała, Kt. Academy).

**Videos**

- [Kotlin Flows in practice][video-flows-practice] (Android Developers): `StateFlow`, `stateIn` y
  `WhileSubscribed` en una app.

## Estado

- **Hecho:** código y 9 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 2); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[android-state]: https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
[stateflow-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/
[sharedflow-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/
[statein-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/state-in.html
[sharingstarted-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-sharing-started/
[elizarov-shared]: https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c
[vivo-sharein]: https://medium.com/androiddevelopers/things-to-know-about-flows-sharein-and-statein-operators-20e6ccb2bc74
[kt-hot-cold]: https://kt.academy/article/cc-hot-cold
[kt-shared]: https://kt.academy/article/cc-sharedflow-stateflow
[video-flows-practice]: https://www.youtube.com/watch?v=fSB6_KE95bU
