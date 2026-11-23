# Ejercicios: excepciones y manejo de errores

> **Lección:** `exceptions` · **Dónde se resuelven:** `exceptions/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../exceptions/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.exceptions.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.5]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 08, 09. **Entrevista (🎤):** 02, 13, 25, 28, 33, 37.

## 🟢 Fáciles

**01 🟢 🎯 `require` y `check`.** `class Transfer(amount, from, to)` que valida en `init` con `require` y un `settle()` que con `check`
exige estado `PENDING`. La prueba verifica el **tipo** de excepción y el **mensaje**.

**02 🟢 🎯 🎤 `!!` en un PR.** Toma `val year = date!!.split("/")[2].toInt()` y escribe tres versiones sin `!!`: devolviendo `Int?`, lanzando
una excepción con mensaje claro, y con `Result`. Prueba con `null`, `"03/14/1985"`, `"1985"` y `"03/14/abcd"`.
¿Qué excepciones podía lanzar el original? Lista las tres.

**03 🟢 `try` como expresión.** `fun parseOr(s: String, default: Int): Int` con `try`/`catch` como expresión, sin `var`.

**04 🟢 `finally` y `return`.** ¿Qué devuelve una función con `return 1` en el `try` y `return 2` en el `finally`? Pruébalo y explica por qué no hacerlo.

**05 🟢 `use` con dos recursos.** Comprueba el orden de cierre con un log, como `FakeResource`.

**06 🟢 Excepción propia.** `class InsufficientFunds(val needed: Long, val available: Long)` que extienda `RuntimeException` con un mensaje
calculado, y que una prueba lea sus campos.

**07 🟢 `getOrElse` y `getOrNull`.** Reescribir un `try`/`catch` con `runCatching` de tres formas.

**25 🟢 🎤 Sin checked exceptions.** Leer el encabezado de un extracto: puede lanzar `IOException` y nadie obliga a
atraparla.
- Firma: `fun statementHeader(path: Path): String`
- Casos: un archivo válido; uno inexistente → `NoSuchFileException` (que es una `IOException`); usar la función
  dentro de un `map { }` sin `try`. Explica en un comentario qué se pierde (el compilador ya no avisa) y qué se gana
  (lambdas, streams y firmas limpias) frente a Java, y cómo se documenta (`@throws` en KDoc).

**26 🟢 `error()` y `TODO()`.** `error` lanza `IllegalStateException`; `TODO` lanza `NotImplementedError`, que es un
`Error` y no una `Exception`.
- Firma: `fun accountTypeFor(code: String): AccountType` (de `oop`, con `error("…")` en la rama que no debería pasar)
- Casos: un código válido, uno inválido → `IllegalStateException` con el código en el mensaje; demuestra que un
  `catch (e: Exception)` **no** atrapa un `TODO()` y explica en un comentario por qué eso es bueno para los stubs de
  los ejercicios.

**27 🟢 `requireNotNull`.** Buscar el saldo de una cuenta y fallar con un mensaje claro si no existe.
- Firma: `fun balanceOf(id: String, balances: Map<String, Money>): Money`
- Casos: existe; no existe → `IllegalArgumentException` con el id en el mensaje; el mensaje se arma en una lambda
  (un contador prueba que no se construye cuando la cuenta existe); después del `requireNotNull` el valor ya no es
  nullable (smart cast, sin `!!`).

**28 🟢 🎤 ¿`require` o `check`?** Una sesión de cajero: `require` para los argumentos, `check` para el estado.
- Firma: `class AtmSession` con `fun open(card: String)`, `fun withdraw(amount: Money)` y `fun close()`
- Casos: monto negativo → `IllegalArgumentException`; retirar sin abrir o después de cerrar → `IllegalStateException`;
  abrir dos veces → `IllegalStateException`. Explica en un comentario por qué la diferencia importa en una API
  (400, culpa del cliente, frente a 500, culpa nuestra).

**29 🟢 Atrapar solo algunas.** Kotlin no tiene multi-catch: un helper que ignora solo los tipos que se le pasan.
- Firma: `fun <T> ignoring(vararg types: KClass<out Throwable>, block: () -> T): T?`
- Casos: `ignoring(NumberFormatException::class) { "x".toInt() }` → `null`; una excepción de otro tipo sale; una
  subclase de un tipo listado también se ignora (`isInstance`); sin tipos, no ignora nada. Compáralo con un
  `catch (e: Exception)` con `when (e)` adentro.

## 🟡 Medios

**08 🟡 🎯 Envolver sin perder la causa.** Un cliente que lanza `IOException`; el servicio lanza `ServiceUnavailable(cause)`. La prueba
comprueba `cause`, la raíz y que el *stack trace* impreso contiene `Caused by:`.

**09 🟡 🎯 Jerarquía sellada a HTTP.** Cinco excepciones de dominio selladas y un `when` sin `else` que las traduce a código y cuerpo
JSON (`{"error": "…", "field": "…"}`).

**10 🟡 Reintentos.** `fun <T> retry(times: Int, delay: Duration, retryOn: (Throwable) -> Boolean, block: () -> T): T` que reintente
solo los errores transitorios y relance el último con los anteriores como `suppressed`.

**11 🟡 Errores acumulados.** Validar una solicitud de transferencia completa (cuentas, monto, moneda, clave de idempotencia) devolviendo **todos** los errores (`Validation` de la lección), no solo el primero.

**12 🟡 Excepciones en lambdas.** ¿Qué pasa si `map { }` lanza a la mitad? ¿Y dentro de una `Sequence` perezosa: cuándo salta? Pruébalo.

**13 🟡 🎤 `runCatching` traga demasiado.** Muestra con una prueba que captura un `StackOverflowError` y una `CancellationException`;
escribe `runCatchingNonFatal` que solo capture lo recuperable.

**14 🟡 `@Throws` e interop.** Un método Kotlin llamado desde una clase Java (en `src/test/java`) que necesita `catch (IOException e)`:
sin `@Throws`, ¿compila el Java? Pruébalo.

**15 🟡 Mensajes útiles.** Reescribe cinco mensajes malos (`"error"`, `"invalid"`) para que digan qué valor, qué esperaba y dónde.
La prueba compara los mensajes exactos.

**30 🟡 Idempotencia.** Ejecutar una operación una sola vez por clave: la segunda llamada con la misma clave devuelve
el mismo resultado, sea éxito o fracaso, sin volver a ejecutar.
- Firma: `class Idempotent<T>` con `fun run(key: String, block: () -> T): T`
- Casos: dos llamadas con la misma clave ejecutan el bloque una vez (un contador lo prueba); si falló, la segunda
  relanza la misma excepción (la misma instancia); claves distintas no se mezclan; decide y documenta si un fallo
  transitorio debería poder reintentarse.

**31 🟡 Errores con su línea.** Parsear un extracto envolviendo cada falla en una excepción que dice la línea.
- Firmas: `fun parseStatement(lines: List<String>): List<StatementLine>` (corta en el primer error) y
  `fun parseStatementAll(lines: List<String>): Pair<List<StatementLine>, List<StatementParseException>>`, con
  `class StatementParseException(val lineNumber: Int, val line: String, cause: Throwable)`
- Casos: todo bien, un monto ilegible en la línea 3 (número, texto y causa `NumberFormatException`), una fecha
  inválida, varias líneas malas en la segunda versión.

**32 🟡 `[1.5]` `Result` encadenado.** Convertir un monto con una tasa de cambio que puede fallar, cayendo a la última
tasa conocida.
- Firma: `fun convert(amount: Money, to: Currency, client: FxRateClient, lastKnown: Map<Currency, BigDecimal>): Result<Money>`
  (con `runCatching`, `mapCatching` y `recoverCatching`)
- Casos: el cliente responde; falla y hay tasa conocida (se usa); falla y no la hay (`Result.failure` con la causa
  original); la tasa es cero o negativa → falla con `IllegalStateException`. Registra cada falla con `onFailure`.

**33 🟡 🎤 ¿Excepción, `Result` o `sealed`?** La misma operación de retiro en tres estilos y el código que la llama.
- Firmas: `fun withdrawOrThrow(...): Money`, `fun withdrawResult(...): Result<Money>` y
  `fun withdrawOutcome(...): WithdrawOutcome` (sellado: `Ok`, `Insufficient`, `Frozen`)
- Casos: los tres estilos con saldo suficiente, insuficiente y cuenta congelada; un `when` exhaustivo sobre el sellado.
  Explica en un comentario cuándo eliges cada uno (¿es un error de programación? ¿el que llama debe decidir? ¿cuántos
  casos hay?).

**34 🟡 Excepciones al inicializar.** Una propiedad `lazy` que falla se reintenta en el siguiente acceso; un `object`
cuyo inicializador falla queda roto para siempre.
- Firmas: `class RateCache(loader: () -> BigDecimal)` con `val rate by lazy { loader() }`, y un `object` de prueba cuyo
  `val` falla al cargarse
- Casos: el primer acceso a `rate` falla y el segundo lo vuelve a intentar (un contador); el primer acceso al
  `object` da `ExceptionInInitializerError` y el segundo `NoClassDefFoundError`. Explica en un comentario por qué no
  conviene hacer trabajo que pueda fallar en un `object` o un `companion`.

**35 🟡 Reintento con backoff.** El ejercicio 10 con espera exponencial, techo y *jitter*, sin dormir en las pruebas.
- Firma: `fun <T> retryWithBackoff(policy: Backoff, sleeper: (Duration) -> Unit, random: Random, block: () -> T): T` con
  `data class Backoff(val attempts: Int, val initial: Duration, val factor: Double, val max: Duration, val jitter: Double)`
- Casos: las esperas registradas por el `sleeper` falso para 5 intentos (inicial 100 ms, factor 2, techo 500 ms → 100, 200, 400, 500), el
  *jitter* con semilla fija queda dentro de su margen, éxito al tercer intento, `attempts = 1` no espera nunca.

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

**36 🟠 Saga con compensación.** Una transferencia en pasos (debitar, acreditar, cobrar comisión): si un paso falla,
se deshacen los anteriores en orden inverso; si una compensación también falla, se agrega como `suppressed`.
- Firma: `class Saga` con `fun step(name: String, action: () -> Unit, compensate: () -> Unit)` y `fun run()`
- Casos: todo bien (ninguna compensación), falla el segundo paso (se compensa solo el primero), falla el tercero
  (se compensan dos, en orden inverso, según un registro), falla una compensación (la excepción original sale con la
  de la compensación en `suppressed` y las demás compensaciones igual corren).

**37 🟠 🎤 Excepciones por capa.** Un repositorio que lanza `SQLException`, un servicio que la traduce a
`BankException` y un controlador que responde un *problem detail* (RFC 9457: `type`, `title`, `status`, `detail`).
- Firmas: `fun translate(e: SQLException): BankException` (por `SQLState`: `23505` → `DuplicateTransfer`, `40001` →
  reintentable, otro → error interno) y `fun problem(e: Throwable): Pair<Int, String>` (código y JSON)
- Casos: cada `SQLState`; el JSON nunca incluye el mensaje SQL ni la pila (pruébalo); la causa original se conserva
  para el log. Explica en un comentario por qué no se deja subir la `SQLException` hasta el controlador.

**38 🟠 Lote que no se detiene.** Procesar 10 000 solicitudes de transferencia: cada una termina en éxito o en un
error de dominio, el lote nunca corta, y al final hay un resumen.
- Firma: `fun processBatch(requests: List<TransferRequest>, service: TransferService): BatchReport` con totales por
  tipo de error y el monto movido
- Casos: un lote con un 10 % de solicitudes inválidas generado con semilla fija; un error inesperado
  (`NullPointerException`) sí corta el lote (no es de dominio); la suma de los montos exitosos es lo que cambió en el
  ledger.

**39 🟠 Pila legible.** Un resumen de una excepción con solo los frames de nuestro paquete, sus causas y sus
suprimidas, sin entrar en un bucle si la cadena de causas tiene un ciclo.
- Firma: `fun conciseTrace(t: Throwable, packagePrefix: String): List<String>`
- Casos: una excepción con dos causas, una con suprimidas, una cadena con ciclo (`a.initCause(b); b.initCause(a)`:
  `initCause` solo impide que una excepción sea su propia causa), frames de la stdlib filtrados.

**40 🟠 Plazo cooperativo.** Un plazo que el código revisa en puntos seguros; los plazos anidados toman el menor.
- Firma: `fun <T> withDeadline(deadline: Instant, clock: Clock, block: Deadline.() -> T): T` con
  `class Deadline` que tiene `fun check()` (lanza `DeadlineExceeded`) y `val remaining: Duration`
- Casos: termina a tiempo; un bucle que llama a `check()` y se pasa (con un reloj que avanza en la prueba); un plazo
  interno más largo que el externo usa el externo; la excepción dice cuánto se pasó.

## 🔴 Muy difíciles

**21 🔴 Either contra excepciones.** Implementa el mismo flujo (parsear → validar → calcular totales → decidir descuento) con excepciones, con `Result`
y con un `Either` propio; compara legibilidad, pruebas y rendimiento con un error en el 10 % de las entradas.

**22 🔴 Supresión en cadena.** Un `use` anidado de tres recursos donde fallan el cuerpo y dos `close`: predice el orden de `suppressed`
antes de correr la prueba, y explica cómo lo implementa `use` (lee su código fuente).

**23 🔴 Bulkhead y timeout.** Ejecutar llamadas a un servicio lento con un límite de concurrencia y un timeout por llamada, cancelando la
tarea y devolviendo un error de dominio; sin dejar hilos colgados (verifícalo al final de la prueba).

**24 🔴 Revisión de PR.** Toma el `main` monolítico del ejercicio 24 de `oop` (o uno propio con `!!`, `runCatching` y `split`) y escribe en un `.md` los comentarios de PR sobre manejo de errores que harías
(qué falla con datos malos, qué se traga, qué mensajes faltan), cada uno con una prueba que lo demuestre.

## ⚡ Saiyajin

**41 ⚡ `TransferService` completo.** Transferencias entre cuentas del Banco con la `BankException` sellada de la
lección, validación que acumula todos los errores de la solicitud, idempotencia por clave (la misma clave devuelve el
mismo resultado, también si falló) y traducción a HTTP.
- Firma: `class CompleteTransferService(accounts: MutableMap<String, Account>, fx: FxRateClient)` con
  `fun transfer(request: RawTransferRequest, idempotencyKey: String): TransferReceipt` y
  `fun respond(request: RawTransferRequest, idempotencyKey: String): Pair<Int, String>` (código y cuerpo JSON);
  `RawTransferRequest` trae todo como texto (cuentas, monto, moneda), como llegaría de una API.
- Reglas: la validación devuelve todos los errores juntos (400); cuenta congelada (423), fondos insuficientes (422),
  moneda distinta con conversión por `FxRateClient` y su falla envuelta con causa (503); cada transferencia deja dos
  asientos que suman cero.
- **Se verifica con:** una prueba parametrizada de tabla (solicitud → recibo o excepción → código HTTP) con al menos
  12 filas; un reintento con la misma clave que no duplica asientos ni vuelve a llamar al servicio de tasas; 1000
  transferencias aleatorias con semilla fija después de las cuales ningún saldo queda negativo y el total del Banco no
  cambió.
