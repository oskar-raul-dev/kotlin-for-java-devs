# 10 · `loom` — Corrutinas frente a hilos virtuales

**Sección:** `02-coroutines` · **Paquete:** `dev.raul.oskar.learning.kotlin.loom` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#02--loom--corrutinas-frente-a-hilos-virtuales) · [ejercicios](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/loom/Exercises.md) · [código](../../../02-coroutines/src/main/kotlin/dev/raul/oskar/learning/kotlin/loom/) · [pruebas](../../../02-coroutines/src/test/kotlin/dev/raul/oskar/learning/kotlin/loom/LoomTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Comparar corrutinas e hilos virtuales del JDK 21 (qué resuelve cada uno, qué cuesta y cuándo conviene mezclarlos) con
mediciones propias, y reconocer el *pinning* de `synchronized` en el JDK 21.

## Teoría

### 1. El mismo problema, dos soluciones

Los dos atacan lo mismo: los hilos de plataforma son caros (pila reservada en el sistema operativo, cambio de contexto
del kernel), así que no se puede tener uno por cada tarea que espera. Las corrutinas lo resuelven en el **lenguaje**: el
código marca con `suspend` dónde puede pausarse, y el compilador lo convierte en una máquina de estados (`internals`).
Los hilos virtuales (JEP 444, finales en el JDK 21) lo resuelven en la **JVM**: un hilo virtual es barato y, cuando
bloquea (`sleep`, E/S de sockets), el JDK lo desmonta de su hilo portador y guarda su pila en el heap. El código
bloqueante de siempre funciona sin cambios. → [JEP 444][jep444], [Virtual Threads][oracle-vt]

### 2. Lo que se ve en los números

`Models.kt` hace lo mismo (entregar un precio a N clientes, cada entrega espera 50 ms) en cuatro modelos. La demo, con
10 000 clientes en una máquina de 8 núcleos, da del orden de 2.7 s para un pool fijo de 200 hilos de plataforma (50
tandas) y unos 70–90 ms para hilos virtuales, corrutinas, y corrutinas sobre hilos virtuales. Para esperar, los tres
modelos baratos se parecen: lo que cambia es todo lo demás. La demo es una sola corrida; el ⚡ pide calentamiento,
repeticiones y p99.

### 3. Lo que traen las corrutinas y Loom no

- **Cancelación estructurada**: un fan-out con hilos virtuales donde una tarea falla no cancela a las demás; hay que
  hacerlo a mano (`Future.cancel(true)` e interrupciones), o usar `StructuredTaskScope`, que en el JDK 21 está en vista
  previa (JEP 453) y lo siguió estando en las versiones siguientes.
- **`Flow`, canales y backpressure**: Loom no tiene un modelo de flujos; para eso sigue habiendo `BlockingQueue` o
  Reactor.
- **Contexto que viaja**: `CoroutineContext` frente a `ScopedValue` (vista previa en el 21, JEP 446).
- **Multiplataforma**: las corrutinas corren en JS, Native y Android (con su `Dispatchers.Main`); los hilos virtuales,
  solo en la JVM 21+.
- **Tiempo virtual en pruebas**: no existe para hilos virtuales (`testing`).

### 4. Lo que trae Loom y las corrutinas no

- **El código bloqueante sin cambios**: JDBC, clientes HTTP síncronos, librerías Java que nunca van a tener una API
  `suspend`. Con corrutinas, cada llamada bloqueante hay que moverla a `Dispatchers.IO` (`dispatchers`).
- **Sin colores de función**: no hay `suspend` que contagie las firmas; cualquier método puede bloquear.
- **Herramientas de siempre**: volcados de hilos, depurador y perfiles que ya conocen los hilos.

### 5. *Pinning*: la trampa del JDK 21

Un hilo virtual que bloquea **dentro de `synchronized`** (o en `Object.wait()`) no se puede desmontar en el JDK 21:
queda fijado a su hilo portador mientras espera, y el paralelismo cae al número de portadores (tantos como núcleos).
`blockingInsideLocks` lo mide con tareas que duermen 50 ms, cada una con su propio lock (sin competencia): con
`synchronized`, la demo tarda unos 435 ms para 64 tareas en 8 portadores (8 tandas); con `ReentrantLock`, unos 55 ms
(`blocking inside synchronized pins the carrier on JDK 21`). El JDK 24 lo corrigió (JEP 491); el curso usa el 21, y la
prueba se salta sola con un JDK nuevo. Las corrutinas no tienen este problema con `Mutex`, que suspende en vez de
bloquear. → [JEP 491][jep491]

### 6. Mezclarlos: corrutinas sobre hilos virtuales

`Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()` da un dispatcher donde cada corrutina corre en un
hilo virtual (`CoroutinesOnVirtualThreadsModel`): la estructura y la cancelación de las corrutinas, y llamadas
bloqueantes sin agotar los 64 hilos de `IO`. Tiene sentido cuando un servicio con corrutinas depende de librerías
bloqueantes. Mezclar sin necesidad duplica las herramientas de diagnóstico (volcados de hilos y de corrutinas) y
esconde errores de diseño: código que bloquea "funciona" en hilos virtuales y se rompe al cambiar de dispatcher.

### 7. En un backend Spring

Spring Boot 3.2+ puede atender cada petición MVC en un hilo virtual (`spring.threads.virtual.enabled=true`): código
bloqueante, JPA y JDBC, sin cambiar nada. WebFlux con corrutinas (`suspend fun` en los controladores, `Flow` en las
respuestas) da streaming, backpressure y cancelación de punta a punta, con drivers no bloqueantes (R2DBC). No hay un
ganador: depende de las librerías que ya se usan y de si hace falta streaming. Se retoma en `09-spring` y en los dos
sabores de `13-spring-api`. → [Structured Concurrency: Java Loom vs Kotlin Coroutines][xebia]

### 8. Medir bien

Un benchmark sin calentamiento mide el JIT y el arranque, no el modelo; una sola corrida mide el ruido; sin carga
sostenida no aparecen el GC ni la memoria. Las pruebas de la lección comprueban solo órdenes de magnitud con márgenes
amplios (son con hilos y reloj reales); las comparaciones finas van con calentamiento, repeticiones y percentiles (el
⚡, o JMH).

## Cómo correrla

```bash
./gradlew -p 02-coroutines run -Plesson=loom
./gradlew -p 02-coroutines test --tests '*.loom.*'
```

## Recorrido sugerido

1. `Models.kt`: la interfaz común (`BroadcastModel`) y los cuatro modelos (`PlatformThreadModel`,
   `VirtualThreadModel`, `CoroutineModel`, `CoroutinesOnVirtualThreadsModel`).
2. `Pinning.kt`: `blockingInsideLocks` con `synchronized` y con `ReentrantLock`, y `jdkFeatureVersion`.
3. `LoomTest.kt` y `Main.kt` (la tabla de tiempos y el pinning medido en tu máquina).

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Models.kt` | El mismo trabajo en cuatro modelos | `BroadcastModel`, `PlatformThreadModel`, `VirtualThreadModel`, `CoroutineModel`, `CoroutinesOnVirtualThreadsModel` | `LoomTest` (`virtual threads make blocking cheap`, `coroutines get the same with suspension`, `a fixed platform pool queues what does not fit`, `coroutines can run blocking code on virtual threads`) |
| `Pinning.kt` | El pinning de `synchronized` en el JDK 21 | `blockingInsideLocks`, `jdkFeatureVersion` | `LoomTest` (`blocking inside synchronized pins the carrier on JDK 21`) |

## Para profundizar

**Documentación oficial**

- [JEP 444: Virtual Threads][jep444]: el diseño, las garantías y la sección sobre pinning.
- [Virtual Threads][oracle-vt] (guía de Oracle para el JDK 21): cómo adoptarlos, qué evitar (pools de hilos virtuales,
  `ThreadLocal` pesados, `synchronized` con bloqueo).
- [JEP 491: Synchronize Virtual Threads without Pinning][jep491] (JDK 24) y [JEP 446: Scoped Values (Preview)][jep446].
- [Coroutine context and dispatchers][context] (kotlinlang.org): `asCoroutineDispatcher` y los dispatchers.

**Lecturas**

- [Networking I/O with Virtual Threads – Under the hood][inside-java] (Inside.java): cómo el JDK desmonta un hilo
  virtual que espera un socket.
- [Structured Concurrency: Java Loom vs Kotlin Coroutines][xebia] (Xebia): la comparación con código de los dos lados.
- [Blocking threads, suspending coroutines][elizarov-blocking] (Roman Elizarov): la diferencia de fondo entre bloquear
  y suspender, escrita antes de Loom y vigente.

**Videos**

- Sin video verificado todavía para la comparación directa (no se cita lo que no se pudo comprobar).
  [Deep Dive into Coroutines on JVM][video-deep] (Roman Elizarov, KotlinConf 2017) cubre el lado de las corrutinas.

## Estado

- **Hecho:** código y 5 pruebas; 40 ejercicios más el ⚡ (6 🎤; B3, tanda 3); 0 resueltos en `practice/`.
- **Falta:** nada de B3.

[jep444]: https://openjdk.org/jeps/444
[jep491]: https://openjdk.org/jeps/491
[jep446]: https://openjdk.org/jeps/446
[oracle-vt]: https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html
[context]: https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html
[inside-java]: https://inside.java/2021/05/10/networking-io-with-virtual-threads/
[xebia]: https://xebia.com/blog/structured-concurrency-will-java-loom-beat-kotlins-coroutines-2/
[elizarov-blocking]: https://elizarov.medium.com/blocking-threads-suspending-coroutines-d33e11bf4761
[video-deep]: https://www.youtube.com/watch?v=YrrUCSi72E8
