# 08 · `json` — kotlinx-serialization

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.json` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--json--kotlinx-serialization) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/json/JsonTest.kt)

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=json
./gradlew -p 01-language test --tests '*.json.*'
```

## Recorrido sugerido

1. `Model.kt`: `Tick` con `@SerialName` para `ts`, `bid-price` y `ask-price`, nullables sin default (obligatorios
   salvo con `explicitNulls = false`) y precios `BigDecimal` con serializador; valores por defecto (`Settings`);
   jerarquía sellada con discriminador `"type"` (`MarketEvent`: `Trade`, `Quote`, `Halt`).
2. `Codec.kt`: un `Json` estricto, uno tolerante (`ignoreUnknownKeys`, `explicitNulls = false`, `encodeDefaults`,
   `coerceInputValues`) y uno con `prettyPrint`; `loadTicks` sobre el feed; árbol `JsonElement` (`totalVolume`,
   `tickersIn`) y el DSL `buildJsonObject`.
3. `Serializers.kt`: `KSerializer` propios para `BigDecimal` (como cadena, nunca como número), `LocalDate`
   (`DailyClose`) y `Money` de `basics` como `"1.00 USD"`, que al leer exige la escala de la moneda (`Fee`).
4. `JsonTest.kt` (una muestra de tres ticks con cada imperfección, y el feed completo) y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Model.kt` | Modelo serializable | `Tick`, `Settings`, `MarketEvent`, `Trade`, `Quote`, `Halt` | `JsonTest` (`lenient json reads ticks with gaps`, `missing nullable key…`, `sealed polymorphism round trip`) |
| `Codec.kt` | Configuraciones de `Json` y árbol | `strictJson`, `lenientJson`, `prettyJson`, `loadTicks`, `toJson`, `settingsFrom`, `encodeEvents`, `decodeEvents`, `totalVolume`, `tickersIn`, `buildSummary`, `countOf` | `JsonTest` (`strict json rejects unknown keys`, `defaults are skipped…`, `json tree navigation`, `the generated feed only loads…`) |
| `Serializers.kt` | Serializadores para tipos de Java y para `Money` | `BigDecimalAsString`, `LocalDateIso`, `DailyClose`, `MoneyAsString`, `Fee` | `JsonTest` (`custom serializers for java types`, `money travels as a string…`) |

Datos: `data/ticks.json`, generado por `datagen` (ver [`Casos.md`](../../Casos.md#datos)).

## Estado

- **Hecho:** código en el caso Mercado (B0′ tandas 2 y 3) y 9 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 6 (reescribir los enunciados que todavía hablan de
  pedidos); B2 (⚡ serializador binario).
