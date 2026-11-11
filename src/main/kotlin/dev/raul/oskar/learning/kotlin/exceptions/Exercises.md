# Ejercicios: excepciones y manejo de errores

> **Lección:** `exceptions` · **Dónde se resuelven:** `exceptions/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../exceptions/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew test --tests '*.exceptions.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 09.

## 🟢 Fáciles

**01 🟢 🎯 `require` y `check`.** `class Order(total, items)` que valida en `init` con `require` y un `ship()` que con `check` exige
estado `PAID`. La prueba verifica el **tipo** de excepción y el **mensaje**.

**02 🟢 🎯 `!!` en un PR.** Toma `val year = date!!.split("/")[2].toInt()` y escribe tres versiones sin `!!`: devolviendo `Int?`, lanzando
una excepción con mensaje claro, y con `Result`. Prueba con `null`, `"03/14/1985"`, `"1985"` y `"03/14/abcd"`.
¿Qué excepciones podía lanzar el original? Lista las tres.

**03 🟢 `try` como expresión.** `fun parseOr(s: String, default: Int): Int` con `try`/`catch` como expresión, sin `var`.

**04 🟢 `finally` y `return`.** ¿Qué devuelve una función con `return 1` en el `try` y `return 2` en el `finally`? Pruébalo y explica por qué no hacerlo.

**05 🟢 `use` con dos recursos.** Comprueba el orden de cierre con un log, como `FakeResource`.

**06 🟢 Excepción propia.** `class InsufficientFunds(val needed: Long, val available: Long)` que extienda `RuntimeException` con un mensaje
calculado, y que una prueba lea sus campos.

**07 🟢 `getOrElse` y `getOrNull`.** Reescribir un `try`/`catch` con `runCatching` de tres formas.

## 🟡 Medios

**08 🟡 🎯 Envolver sin perder la causa.** Un cliente que lanza `IOException`; el servicio lanza `ServiceUnavailable(cause)`. La prueba
comprueba `cause`, la raíz y que el *stack trace* impreso contiene `Caused by:`.

**09 🟡 🎯 Jerarquía sellada a HTTP.** Cinco excepciones de dominio selladas y un `when` sin `else` que las traduce a código y cuerpo
JSON (`{"error": "…", "field": "…"}`).

**10 🟡 Reintentos.** `fun <T> retry(times: Int, delay: Duration, retryOn: (Throwable) -> Boolean, block: () -> T): T` que reintente
solo los errores transitorios y relance el último con los anteriores como `suppressed`.

**11 🟡 Errores acumulados.** Validar un `OrderEvent` completo devolviendo **todos** los errores (`Validation` de la lección), no solo el primero.

**12 🟡 Excepciones en lambdas.** ¿Qué pasa si `map { }` lanza a la mitad? ¿Y dentro de una `Sequence` perezosa: cuándo salta? Pruébalo.

**13 🟡 `runCatching` traga demasiado.** Muestra con una prueba que captura un `StackOverflowError` y una `CancellationException`;
escribe `runCatchingNonFatal` que solo capture lo recuperable.

**14 🟡 `@Throws` e interop.** Un método Kotlin llamado desde una clase Java (en `src/test/java`) que necesita `catch (IOException e)`:
sin `@Throws`, ¿compila el Java? Pruébalo.

**15 🟡 Mensajes útiles.** Reescribe cinco mensajes malos (`"error"`, `"invalid"`) para que digan qué valor, qué esperaba y dónde.
La prueba compara los mensajes exactos.

## 🟠 Difíciles

**16 🟠 Circuit breaker.** Estados `CLOSED → OPEN → HALF_OPEN` con umbral de fallos y tiempo de espera, con un `Clock` inyectado y pruebas de cada transición.

**17 🟠 Errores en hilos.** Una tarea en un `ExecutorService` que lanza: ¿dónde aparece la excepción? (`Future.get` → `ExecutionException`).
Y con `CompletableFuture.exceptionally`/`handle`.

**18 🟠 Excepciones sin pila.** Una excepción de control de flujo con `writableStackTrace = false` (constructor protegido de `Throwable`):
mide la diferencia de costo y explica cuándo se justifica.

**19 🟠 Transacciones y excepciones.** Con la lección JDBC: una excepción no verificada a la mitad de `inTransaction` hace rollback; ¿y si se
captura dentro del bloque? Muestra el bug de "tragarse" la excepción.

**20 🟠 Manejador global.** `Thread.setDefaultUncaughtExceptionHandler` que registre con Kermit y termine el proceso con código distinto de cero;
pruébalo en un hilo aparte.

## 🔴 Muy difíciles

**21 🔴 Either contra excepciones.** Implementa el mismo flujo (parsear → validar → calcular totales → decidir descuento) con excepciones, con `Result`
y con un `Either` propio; compara legibilidad, pruebas y rendimiento con un error en el 10 % de las entradas.

**22 🔴 Supresión en cadena.** Un `use` anidado de tres recursos donde fallan el cuerpo y dos `close`: predice el orden de `suppressed`
antes de correr la prueba, y explica cómo lo implementa `use` (lee su código fuente).

**23 🔴 Bulkhead y timeout.** Ejecutar llamadas a un servicio lento con un límite de concurrencia y un timeout por llamada, cancelando la
tarea y devolviendo un error de dominio; sin dejar hilos colgados (verifícalo al final de la prueba).

**24 🔴 Revisión de PR.** Toma el `main` monolítico del ejercicio 24 de `oop` (o uno propio con `!!`, `runCatching` y `split`) y escribe en un `.md` los comentarios de PR sobre manejo de errores que harías
(qué falla con datos malos, qué se traga, qué mensajes faltan), cada uno con una prueba que lo demuestre.
