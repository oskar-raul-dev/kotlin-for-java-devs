# 04 · `oop` — Clases, herencia, `sealed`, enums, objetos

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.oop` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--oop--clases-herencia-sealed-enums-objetos) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/oop/OopTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=oop
./gradlew -p 01-language test --tests '*.oop.*'
```

## Recorrido sugerido

1. `Accounts.kt`: cuentas del Banco con `Money` (de `basics`): constructor primario, `init` con `require`,
   `private set` (`balance`, `status`), propiedad calculada, `abstract`/`open`, `protected` (`overdraftLimit`),
   constructor secundario (`EscrowAccount`) y polimorfismo (`transfer`).
2. `Enums.kt`: `AccountType` con propiedades, cuerpo por constante (`closesAtMonthEnd`) y fábrica en `companion`
   (`of`, `ofOrThrow`); `AccountStatus` con propiedades calculadas; `Side` con `when` exhaustivo.
3. `Orders.kt`: interfaz con método por defecto (`Described`), `sealed interface Order` (`LimitOrder`,
   `MarketOrder`) y `when` exhaustivo (`priceHint`).
4. `Objects.kt`: `object` (`IdGenerator`), `data class` con propiedad en el cuerpo (`Position`), constructor privado
   con fábrica (`Ticker`), `value class` con `init` (`TransferId`), operadores (`Vector2`), delegación `by`,
   `lazy` y genéricos con cota.
5. `OopTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Accounts.kt` | Herencia y visibilidad | `Account`, `SavingsAccount`, `CheckingAccount`, `EscrowAccount`, `transfer` | `OopTest` (`init block validates…`, `overridden overdraft…`, `polymorphic transfer and account status`) |
| `Enums.kt` | Enums con `companion` y cuerpo por constante | `AccountType`, `AccountStatus`, `Side`, `label` | `OopTest` (`enum factory in companion and bodies per constant`) |
| `Orders.kt` | `sealed` + `when` exhaustivo | `Described`, `Order`, `LimitOrder`, `MarketOrder`, `priceHint`, `committed` | `OopTest` (`sealed hierarchy and default methods`) |
| `Objects.kt` | `object`, `data`, `value`, operadores, `by` | `IdGenerator`, `Position`, `Ticker`, `TransferId`, `Vector2`, `CountingList`, `Config`, `maxOf3` | `OopTest` (`data class equality…`, `object singleton…`, `operators and value classes`, `delegation with by and lazy`, `generic with upper bound`) |

## Estado

- **Hecho:** código en los casos (B0′ tanda 3) y 10 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 6 (enunciados que todavía hablan de envíos y clientes); B2 (⚡ evaluador de expresiones con
  `sealed`).
