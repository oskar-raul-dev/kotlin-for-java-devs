# 10 · `exceptions` — Sin checked exceptions, errores como valores

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.exceptions` · **Caso:** pedidos (provisional) → Banco
**Enlaces:** [temario](../../Temario.md#01--exceptions--sin-checked-exceptions-errores-como-valores) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/exceptions/ExceptionsTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=exceptions
./gradlew -p 01-language test --tests '*.exceptions.*'
```

## Recorrido sugerido

1. `Basics.kt`: `try` y `throw` como expresiones (`readOrNull`, `portFrom`), `require`/`check`/`error` (`Wallet`),
   `requireNotNull` con smart cast (`parseAge`), sin multi-catch (`classify`), `finally` (`withFinally`).
2. `Domain.kt`: jerarquía sellada (`OrderException`), traducción de una excepción de bajo nivel conservando la causa
   (`OrderService.canPlace`), `when` exhaustivo a HTTP (`httpStatusFor`), cadena de causas.
3. `Results.kt`: `Result` y `runCatching`, la trampa con `CancellationException` (`runCatchingCancellable`) y
   validación que acumula errores (`Validation`, `validate`).
4. `Resources.kt`: `use` con dos recursos y suprimidas; `@Throws` para Java.
5. `ExceptionsTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Basics.kt` | Sin checked exceptions; precondiciones | `readOrNull`, `portFrom`, `Wallet`, `parseAge`, `classify`, `withFinally`, `divide` | `ExceptionsTest` (`try is an expression…`, `throw fits in an elvis`, `require check and error…`, `requireNotNull smart casts`, `catch the parent…`, `finally always runs…`) |
| `Domain.kt` | Excepciones de dominio | `OrderException`, `InvalidOrderRequest`, `OrderNotFound`, `InventoryUnavailable`, `InventoryClient`, `OrderService`, `httpStatusFor`, `causeChain`, `rootCause` | `ExceptionsTest` (`domain exceptions keep the cause`, `sealed hierarchy maps to status codes`) |
| `Results.kt` | Errores como valores | `parsePort`, `portOrDefault`, `portOrExplain`, `portRecoveringFormat`, `doubledPort`, `runCatchingCancellable`, `Validation`, `Applicant`, `validate`, `describe` | `ExceptionsTest` (`result helpers`, `runCatching swallows cancellation…`, `validation accumulates every error`) |
| `Resources.kt` | `use` y `@Throws` | `FakeResource`, `useTwo`, `loadConfigForJava` | `ExceptionsTest` (`use closes in reverse order…`, `close failure alone propagates`, `throws annotation…`) |

## Estado

- **Hecho:** código y 14 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 3 (`OrderException` y `OrderService` → `BankException` y `TransferService`; `Applicant` →
  validar una transferencia); B2 (⚡ `TransferService` completo).
