# 04 · `oop` — Clases, herencia, `sealed`, enums, objetos

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.oop` · **Caso:** pedidos (provisional) → Ambos
**Enlaces:** [temario](../../Temario.md#01--oop--clases-herencia-sealed-enums-objetos) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/oop/OopTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=oop
./gradlew -p 01-language test --tests '*.oop.*'
```

## Recorrido sugerido

1. `Accounts.kt`: constructor primario, `init` con `require`, `private set`, propiedad calculada, `abstract`/`open`,
   `protected`, constructor secundario (`LockedAccount`) y polimorfismo (`transfer`).
2. `Shapes.kt`: interfaz con método por defecto (`Named`), `sealed interface Shape` y `when` exhaustivo
   (`perimeterHint`).
3. `Enums.kt`: enum con propiedades, cuerpo por constante y fábrica en `companion` (`ShippingMethod.of`).
4. `Objects.kt`: `object`, `data class`, constructor privado con fábrica, `value class`, operadores, delegación
   `by`, `lazy` y genéricos con cota.
5. `OopTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Accounts.kt` | Herencia y visibilidad | `Account`, `SavingsAccount`, `CheckingAccount`, `LockedAccount`, `transfer` | `OopTest` (`init block validates…`, `overridden overdraft…`, `polymorphic transfer`) |
| `Shapes.kt` | `sealed` + `when` exhaustivo | `Named`, `Shape`, `Circle`, `Rectangle`, `Triangle`, `perimeterHint`, `totalArea` | `OopTest` (`sealed hierarchy and default methods`) |
| `Enums.kt` | Enums con `companion` | `ShippingMethod`, `Direction`, `label` | `OopTest` (`enum factory in companion`) |
| `Objects.kt` | `object`, `data`, `value`, operadores, `by` | `IdGenerator`, `Customer`, `Email`, `Cents`, `Vector2`, `CountingList`, `Config`, `maxOf3` | `OopTest` (`data class equality…`, `object singleton…`, `operators and value classes`, `delegation with by and lazy`, `generic with upper bound`) |

## Estado

- **Hecho:** código y 10 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 3 (`ShippingMethod`, `Customer` y las formas → `Order` sellado, `AccountType`, `Money`);
  B2 (⚡ evaluador de expresiones con `sealed`).
