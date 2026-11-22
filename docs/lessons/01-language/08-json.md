# 08 · `json` — kotlinx-serialization

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.json` · **Caso:** pedidos (provisional) → Mercado
**Enlaces:** [temario](../../Temario.md#01--json--kotlinx-serialization) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/json/JsonTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=json
./gradlew -p 01-language test --tests '*.json.*'
```

## Recorrido sugerido

1. `Model.kt`: `@SerialName` para claves con guiones, nullable sin default (`OrderEvent`, `Address`), valores por
   defecto (`Settings`) y jerarquía sellada con discriminador (`Payment`).
2. `Codec.kt`: un `Json` estricto, uno tolerante (`ignoreUnknownKeys`, `explicitNulls = false`, `encodeDefaults`,
   `coerceInputValues`) y uno con `prettyPrint`; árbol `JsonElement` y el DSL `buildJsonObject`.
3. `Serializers.kt`: `KSerializer` propios para `BigDecimal` y `LocalDate` (`Invoice`).
4. `JsonTest.kt` y `Main.kt` (leen `data/orders.json`).

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Model.kt` | Modelo serializable | `OrderEvent`, `Address`, `Settings`, `Payment`, `CardPayment`, `TransferPayment` | `JsonTest` (`lenient json reads order events`, `missing nullable key…`, `sealed polymorphism round trip`) |
| `Codec.kt` | Configuraciones de `Json` y árbol | `strictJson`, `lenientJson`, `prettyJson`, `loadOrderEvents`, `toJson`, `settingsFrom`, `encodePayments`, `decodePayments`, `sumOrderTotals`, `statesIn`, `buildSummary`, `countOf` | `JsonTest` (`strict json rejects unknown keys`, `defaults are skipped…`, `json tree navigation`) |
| `Serializers.kt` | Serializadores para tipos de Java | `BigDecimalAsString`, `LocalDateIso`, `Invoice` | `JsonTest` (`custom serializers for java types`) |

## Estado

- **Hecho:** código y 7 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 2 (`OrderEvent`, `Address`, `Payment` → `Tick` y `MarketEvent` sobre `ticks.json`) y tanda 3
  (serializador de `Money`); B2 (⚡ serializador binario).
