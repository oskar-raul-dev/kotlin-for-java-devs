# 07 · `sync` — `Mutex`, `Semaphore`, atomicfu, `@Volatile` y el JMM

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.sync` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#02--sync--mutex-semaphore-atomicfu-volatile-y-el-jmm) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/sync/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/sync/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/sync/SyncTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Proteger estado compartido entre corrutinas sin bloquear hilos, evitar el deadlock clásico de las transferencias, y
saber cuándo un lock sobra porque el estado se puede confinar, y cuándo confinar no alcanza.

## Teoría

### 1. Dos problemas distintos: carreras de datos e intercalaciones

Con corrutinas aparecen dos clases de error que en Java suelen verse juntas:

- **Carrera de datos**: dos hilos tocan la misma memoria a la vez. Solo pasa si hay paralelismo real
  (`Dispatchers.Default`, `IO`). `VolatileCounter` pierde incrementos así.
- **Intercalación en un punto de suspensión**: una corrutina valida un saldo, suspende (una llamada remota, un
  `yield`) y otra corrutina gasta el mismo saldo antes de que la primera escriba. Pasa **aunque haya un solo hilo**:
  `without protection money appears or disappears` lo reproduce dentro de `runTest`.

Los locks resuelven las dos; el confinamiento (más abajo) resuelve solo la primera. → [Shared mutable state and
concurrency][shared-state]

### 2. El modelo de memoria es el de Java

Kotlin en la JVM usa el JMM tal cual: `@Volatile` compila a un campo `volatile` (visibilidad y orden, **no**
atomicidad: `count++` sigue siendo leer-sumar-escribir), `synchronized` y los `java.util.concurrent` funcionan igual,
y la publicación segura de un objeto inmutable mediante una referencia `volatile` sigue siendo la forma más barata de
compartir. Lo que Kotlin no tiene es la palabra clave: `synchronized(lock) { }` es una función `inline` de la stdlib, y
`@Synchronized` es la anotación para métodos. → [JLS §17][jls17], [JMM Pragmatics][shipilev]

### 3. `Mutex`: el lock que suspende

`mutex.withLock { }` es el `synchronized` de las corrutinas: si el lock está tomado, la corrutina **suspende** (el hilo
queda libre), y `withLock` lo suelta aunque el bloque falle o la corrutina se cancele. Tres diferencias con
`synchronized`:

- Se puede suspender adentro. Dentro de `synchronized`, en cambio, llamar a una función `suspend` no compila: "The
  'delay' suspension point is inside a critical section" (medido en 2.4.21). Un monitor de la JVM pertenece a un hilo, y
  la corrutina podría volver en otro.
- **No es reentrante**: un `withLock` anidado sobre el mismo `Mutex` en la misma corrutina espera para siempre
  (`Mutex is not reentrant`). Se reestructura con una función privada que asume el lock tomado.
- Es justo: atiende en orden de llegada.

`MutexCounter` y `OrderedLockBank` lo usan. → [`Mutex`][mutex-api]

### 4. El deadlock de las transferencias

A→B toma el lock de A y espera el de B; B→A toma el de B y espera el de A: ninguna avanza. `NaiveLockBank` lo
reproduce de forma determinista (un `yield()` entre los dos locks) y la prueba `crossed transfers deadlock with naive
lock order` lo detecta con `withTimeoutOrNull`. La solución clásica es romper la espera circular: tomar los locks
**siempre en el mismo orden** (por id de cuenta), como hace `OrderedLockBank`. Con eso, las mismas transferencias
cruzadas terminan.

### 5. `Semaphore`: limitar cuántos entran

`semaphore.withPermit { }` deja entrar a N corrutinas a la vez y suspende a las demás: el límite de llamadas
simultáneas al servicio de tasas (`LimitedFxClient`; con 3 permisos, 10 llamadas de 100 ms tardan 400 ms virtuales).
Es la herramienta cuando el recurso no es un dispatcher (un servicio externo, la memoria); para limitar llamadas
bloqueantes, `limitedParallelism` (`dispatchers`). → [`Semaphore`][semaphore-api]

### 6. Atómicos: Java, atomicfu y la stdlib

Para un contador o una referencia, un atómico es más barato que un lock. Tres opciones, las tres en `Counters.kt`:

- `java.util.concurrent.atomic.AtomicLong` (`JavaAtomicCounter`): lo de siempre, solo en la JVM.
- **atomicfu** (`AtomicfuCounter`): `atomic(0L)` con la misma API en JVM, JS y Native. En la JVM, su plugin de Gradle
  reescribe el campo a un `volatile long` con `AtomicLongFieldUpdater`, sin objeto extra. Importa en multiplataforma
  (`11-multiplatform`).
- **Los atómicos de la stdlib** (`kotlin.concurrent.atomics`, `StdlibAtomicCounter`): experimentales en 2.4.21, y el
  opt-in es **obligatorio** (error, no warning; medido). En la JVM, `kotlin.concurrent.atomics.AtomicLong` es la misma
  clase de Java, con extensiones como `incrementAndFetch` que hay que importar.

`update { }` de `StateFlow` (`hotflows`) es el mismo ciclo de `compareAndSet`. → [atomicfu][atomicfu],
[`kotlin.concurrent.atomics`][stdlib-atomics]

### 7. Confinar en vez de bloquear, y su límite

A veces el lock sobra: si todo el acceso al estado pasa por un dispatcher de un solo hilo lógico
(`limitedParallelism(1)`, `ConfinedBank`) o por un actor (`channels`), no hay paralelismo y no hay carreras de datos.
Tampoco puede haber deadlock.

Pero confinar evita el **paralelismo**, no la **intercalación**: si la sección crítica tiene un punto de suspensión
entre validar y escribir, otra corrutina del mismo dispatcher entra ahí. `confinement does not protect across a
suspension point` lo muestra: un solo hilo lógico y el mismo saldo gastado dos veces. Lo descubrimos al escribir la
lección (la primera versión de `ConfinedBank` dejaba saldos negativos). La regla: con confinamiento o con actor, la
parte crítica no suspende; si tiene que suspender, se necesita un `Mutex`, que sí se mantiene tomado a través de la
suspensión.

### 8. `ConcurrentHashMap` desde Kotlin: `getOrPut` frente a `computeIfAbsent`

`map.getOrPut(k) { calcular() }` de Kotlin sobre un `ConcurrentHashMap` no pisa un valor existente, pero puede
**calcular dos veces**: lee, no encuentra, calcula y recién ahí hace `putIfAbsent`. La prueba `getOrPut on a
ConcurrentHashMap may compute twice` lo fuerza con un `CountDownLatch`. `computeIfAbsent` de Java calcula una sola vez
con la entrada bloqueada (`computeIfAbsent computes once`). Si la lambda cobra una comisión o abre una conexión, la
diferencia importa; si es pura y barata, da igual.

### 9. En un servicio real, el lock está en la base

Todo esto protege memoria de **un** proceso. Con varias instancias del servicio, o con el saldo en una base de datos,
el lock va donde está el dato: transacciones, `SELECT … FOR UPDATE`, versiones optimistas (`04-data/jdbc` y `jpa`). Los
locks en memoria sirven para cachés, contadores y estado local.

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=sync
./gradlew -p 02-coroutines test --tests '*.sync.*'
```

## Recorrido sugerido

1. `Counters.kt`: el contador `@Volatile` que pierde, los tres atómicos y el `Mutex` (`hammer` los pone a prueba con
   100 corrutinas en `Default`).
2. `Transfers.kt`: `Bank`, la versión sin protección (`UnsafeBank`), locks ordenados (`OrderedLockBank`), el deadlock
   (`NaiveLockBank`), el confinamiento y su trampa (`ConfinedBank`) y el `Mutex` no reentrante (`nestedWithLock`).
3. `Limits.kt`: `Semaphore` (`LimitedFxClient`) y `getOrPut` frente a `computeIfAbsent` (`FeeCache`).
4. `SyncTest.kt` y `Main.kt` (la demo muestra los incrementos perdidos y el dinero que aparece sin locks).

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Counters.kt` | Visibilidad, atomicidad, atómicos y `Mutex` | `OperationCounter`, `VolatileCounter`, `JavaAtomicCounter`, `AtomicfuCounter`, `StdlibAtomicCounter`, `MutexCounter`, `hammer` | `SyncTest` (`atomic counters never lose increments`, `a volatile counter is visible but not atomic`) |
| `Transfers.kt` | Transferencias concurrentes del Banco | `InsufficientFunds`, `Bank`, `MapBank`, `UnsafeBank`, `OrderedLockBank`, `NaiveLockBank`, `ConfinedBank`, `nestedWithLock` | `SyncTest` (`ordered locks keep the total…`, `confined state gives the same guarantees without locks`, `confinement does not protect across a suspension point`, `without protection money appears or disappears`, `crossed transfers deadlock with naive lock order`, `the same crossed transfers finish with ordered locks`, `Mutex is not reentrant`) |
| `Limits.kt` | Límites y mapas concurrentes | `LimitedFxClient`, `FeeCache` | `SyncTest` (`a semaphore caps concurrent calls`, `getOrPut on a ConcurrentHashMap may compute twice`, `computeIfAbsent computes once`) |

## Para profundizar

**Documentación oficial**

- [Shared mutable state and concurrency][shared-state] (kotlinlang.org): el mismo contador con atómicos,
  confinamiento, `Mutex` y actor.
- En la API: [`Mutex`][mutex-api] y [`Semaphore`][semaphore-api]; [`kotlin.concurrent.atomics`][stdlib-atomics].
- [atomicfu][atomicfu] (GitHub de Kotlin): qué hace el plugin y sus reglas (`private val`, sin escaparse).
- [JLS, capítulo 17: Threads and Locks][jls17]: el modelo de memoria de Java, que es el de Kotlin en la JVM.

**Lecturas**

- [Java Memory Model Pragmatics][shipilev] (Aleksey Shipilёv): el JMM explicado con ejemplos, sin la notación formal.
- *Java Concurrency in Practice* (B. Goetz y otros, Addison-Wesley): visibilidad, publicación segura y deadlocks; los
  capítulos 3 y 10 son el fondo de esta lección.

**Videos**

- [Kotlin Coroutines in Practice][video-practice] (Roman Elizarov, KotlinConf 2018): estado compartido con
  confinamiento y actores en lugar de locks.

## Estado

- **Hecho:** código y 12 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 3); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[shared-state]: https://kotlinlang.org/docs/shared-mutable-state-and-concurrency.html
[mutex-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-mutex/
[semaphore-api]: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.sync/-semaphore/
[atomicfu]: https://github.com/Kotlin/kotlinx-atomicfu
[stdlib-atomics]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.concurrent.atomics/
[jls17]: https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html
[shipilev]: https://shipilev.net/blog/2014/jmm-pragmatics/
[video-practice]: https://www.youtube.com/watch?v=a3agLJQ6vt8
