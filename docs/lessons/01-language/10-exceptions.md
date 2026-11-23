# 10 · `exceptions` — Sin checked exceptions, errores como valores

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.exceptions` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#01--exceptions--sin-checked-exceptions-errores-como-valores) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/exceptions/ExceptionsTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Manejar errores sin checked exceptions: precondiciones de la stdlib, jerarquías selladas con causa, `Result` y errores
como valores, y saber cuándo usar cada uno; y entender por qué Kotlin quitó las checked exceptions.

## Teoría

### 1. Por qué no hay checked exceptions

En Java, el compilador obliga a capturar o declarar (`throws`) las checked exceptions. Kotlin las quitó a propósito:
la experiencia con Java mostró que, en código grande, terminan atrapadas y tragadas (`catch (IOException e) {}`) o
envueltas mecánicamente en `RuntimeException`, y que no componen bien con lambdas (un `Stream.map` no puede lanzar una
checked). El precio es que el compilador ya no avisa: `Files.readString` compila sin `try` (`readOrNull`), y hay que
saber qué puede fallar. La postura del equipo de Kotlin, según la explica Roman Elizarov: las excepciones son para
errores de lógica y fallas que no se pueden manejar localmente; lo esperable se modela en los tipos. → [Exceptions][exceptions],
[Kotlin and Exceptions][elizarov]

### 2. `try` y `throw` son expresiones

`val x = try { … } catch (e: E) { … }` devuelve un valor, y `throw` es una expresión de tipo `Nothing`, así que cabe a la
derecha de un Elvis: `env["PORT"]?.toIntOrNull() ?: throw IllegalArgumentException(…)` (`portFrom`). No hay multi-catch
(`catch (A | B e)`): se atrapa el padre y se distingue con `when`, relanzando lo que no se sabe manejar (`classify`). El
`finally` corre siempre y su valor no cuenta (`withFinally`).

### 3. `require`, `check`, `error`: de quién es la culpa

Las precondiciones de la stdlib dicen **quién se equivocó**: `require(cond)` lanza `IllegalArgumentException` (el
llamador pasó un argumento inválido: un 400 en una API), `check(cond)` lanza `IllegalStateException` (el objeto está en
un estado inválido, culpa nuestra: un 500), `error("…")` lanza `IllegalStateException` y devuelve `Nothing`.
`requireNotNull(x)` además hace smart cast: después de la línea, `x` ya no es nullable (`parseAmount`). El mensaje va en
una lambda, así que solo se arma si falla. `TransferService` las usa así: `require` para el monto y las cuentas,
`check` para el invariante de que ningún saldo queda negativo. → [`require`][require]

### 4. Excepciones de dominio: jerarquía sellada y causa

`sealed class BankException : RuntimeException` deja la lista de errores del Banco cerrada: un `when` que traduce a HTTP
(`httpStatusFor`) es exhaustivo sin `else`, y agregar un error nuevo hace fallar la compilación donde no se cubra. Al
traducir una falla de bajo nivel (la `IOException` del cliente de tasas) a una del dominio (`FxUnavailable`), la original
va como **causa**: sin ella, el log pierde el motivo real (`causeChain`, `rootCause`).

### 5. Errores como valores: `Result` y tipos sellados

`runCatching { … }` envuelve el resultado en un `Result<T>` (`success` o `failure`) que se transforma con `map`,
`mapCatching`, `recover`, `fold` o `getOrDefault` (`parsePort` y compañía). Sirve en la frontera con código que lanza.
Para errores de **negocio**, un tipo sellado propio es más claro: `Validation.Valid`/`Validation.Invalid` acumula
**todos** los errores de una solicitud, cosa que con excepciones (que cortan en la primera) no se puede (`validate`).
Arrow (`Either`, `Raise`) lleva la idea más lejos (`07 · fp`). → [`Result`][result], [KEEP de `Result`][result-keep]

### 6. La trampa: `runCatching` y la cancelación

`runCatching` atrapa `Throwable`: también la `CancellationException` con la que se cancela una corrutina, y los `Error`
(`OutOfMemoryError`). En código con corrutinas, eso rompe la cancelación estructurada: la corrutina "cancelada" sigue
corriendo. La lección trae la variante segura (`runCatchingCancellable`), que relanza la cancelación; se retoma en
`02 · cancellation`. → [Coroutine exceptions handling][coroutine-exceptions]

### 7. `use` y `@Throws`

`use { }` cierra recursos y conserva la excepción de `close()` como suprimida, igual que try-with-resources (`useTwo`).
Y para quien llama desde Java, `@Throws(IOException::class)` agrega el `throws` al bytecode: sin él, `javac` no deja
atrapar la checked que el método lanza (`loadConfigForJava`).

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=exceptions
./gradlew -p 01-language test --tests '*.exceptions.*'
```

## Recorrido sugerido

1. `Basics.kt`: `try` y `throw` como expresiones (`readOrNull`, `portFrom`), `require`/`check`/`error` (`Wallet`),
   `requireNotNull` con smart cast (`parseAmount`), sin multi-catch (`classify`), `finally` (`withFinally`).
2. `Domain.kt`: `BankException` sellada, `TransferService.transfer` (`require` para errores del cliente, `check` para
   los nuestros, excepciones de dominio para las reglas del negocio), la falla del servicio de tasas envuelta en
   `FxUnavailable` conservando la causa, el invariante de doble partida (`checkBalanced`) y `when` exhaustivo a HTTP
   (`httpStatusFor`).
3. `Results.kt`: `Result` y `runCatching`, la trampa con `CancellationException` (`runCatchingCancellable`) y
   validación de una solicitud de transferencia que acumula errores (`Validation`, `validate`).
4. `Resources.kt`: `use` con dos recursos y suprimidas; `@Throws` para Java.
5. `ExceptionsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Basics.kt` | Sin checked exceptions; precondiciones | `readOrNull`, `portFrom`, `Wallet`, `parseAmount`, `classify`, `withFinally`, `divide` | `ExceptionsTest` (`try is an expression…`, `throw fits in an elvis`, `require check and error…`, `requireNotNull smart casts`, `catch the parent…`, `finally always runs…`) |
| `Domain.kt` | Excepciones del Banco | `BankException`, `InsufficientFunds`, `AccountFrozen`, `CurrencyMismatch`, `DuplicateTransfer`, `UnbalancedEntry`, `FxUnavailable`, `FxRateClient`, `TransferService`, `checkBalanced`, `httpStatusFor`, `causeChain`, `rootCause` | `ExceptionsTest` (`domain exceptions keep the cause`, `transfer converts currency…`, `sealed hierarchy maps to status codes`, `require and check separate…`) |
| `Results.kt` | Errores como valores | `parsePort`, `portOrDefault`, `portOrExplain`, `portRecoveringFormat`, `doubledPort`, `runCatchingCancellable`, `Validation`, `TransferRequest`, `validate`, `describe` | `ExceptionsTest` (`result helpers`, `runCatching swallows cancellation…`, `validation accumulates every error`) |
| `Resources.kt` | `use` y `@Throws` | `FakeResource`, `useTwo`, `loadConfigForJava` | `ExceptionsTest` (`use closes in reverse order…`, `close failure alone propagates`, `throws annotation…`) |

`Money` viene de `basics`.

## Para profundizar

**Documentación oficial**

- [Exceptions][exceptions] (kotlinlang.org): checked, `try` como expresión, `Nothing`, precondiciones.
- [`require`][require] y la [API de `Result`][result]: precondiciones y errores como valores.
- [KEEP: Result][result-keep]: por qué `Result` existe y por qué al principio no se podía devolver.
- [Coroutine exceptions handling][coroutine-exceptions]: la cancelación y `runCatching` en corrutinas.

**Lecturas**

- [Kotlin and Exceptions][elizarov] (Roman Elizarov): cuándo usar excepciones y cuándo tipos, según quien dirigió el
  diseño de Kotlin.
- [Exception handling in Kotlin][baeldung-exc] (Baeldung): `try` como expresión, `@Throws` y la interoperabilidad.
- *Java to Kotlin* (D. McGregor y N. Pryce, O'Reilly), capítulo 19 "Throwing to Returning": la migración de excepciones a
  errores como valores, paso a paso.

**Videos**

- [Error Handling Strategies for Kotlin Programs][pryce-mcgregor] (Nat Pryce y Duncan McGregor, KotlinConf 2019):
  excepciones, `null`, tipos de error y funciones totales.
- [From Exceptions to Rich Errors: Rethinking Error Handling in Kotlin][rich-errors] (Michail Zarečenskij): cómo
  evolucionó el modelo de Java en Kotlin y hacia dónde va.
- [Coroutines! Gotta catch 'em all!][coroutines-catch] (Florina Muntenescu y Manuel Vivo, KotlinConf 2019): errores en
  corrutinas, incluido `runCatching`.

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 3) y 16 pruebas; 40 ejercicios más el ⚡ (4 🎯, 6 🎤; B2, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B2.

[exceptions]: https://kotlinlang.org/docs/exceptions.html
[require]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/require.html
[result]: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/
[result-keep]: https://github.com/Kotlin/KEEP/blob/master/proposals/stdlib/result.md
[coroutine-exceptions]: https://kotlinlang.org/docs/exception-handling.html
[elizarov]: https://elizarov.medium.com/kotlin-and-exceptions-8062f589d07
[baeldung-exc]: https://www.baeldung.com/kotlin/exception-handling
[pryce-mcgregor]: https://www.youtube.com/watch?v=pvYAQNT4o0I
[rich-errors]: https://www.youtube.com/watch?v=l2FdAobeUjs
[coroutines-catch]: https://www.youtube.com/watch?v=w0kfnydnFWI
