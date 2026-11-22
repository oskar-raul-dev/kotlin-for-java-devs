# 08 · `json` — kotlinx-serialization

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.json` · **Caso:** Mercado
**Enlaces:** [temario](../../Temario.md#01--json--kotlinx-serialization) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/json/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/json/JsonTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Leer y escribir JSON sin Jackson ni reflexión, con la configuración tolerante que pide un feed real, jerarquías selladas
y serializadores propios; y saber en qué se diferencia el modelo de kotlinx-serialization del de Jackson.

## Teoría

### 1. Serializadores generados al compilar, no reflexión

Jackson descubre los campos en runtime por reflexión. kotlinx-serialization trabaja al revés: el plugin del compilador
(`kotlin("plugin.serialization")`) genera un `KSerializer` para cada clase `@Serializable` mientras compila. Eso tiene
consecuencias concretas: no hace falta un constructor vacío ni `kotlin-reflect`, los errores de modelo (un tipo no
serializable) salen al compilar, el código funciona igual en Android, iOS, JS y en un binario nativo de GraalVM (no hay
reflexión que configurar), y la librería **entiende la nulabilidad y los valores por defecto de Kotlin**, que Jackson
solo entiende con su módulo de Kotlin. La contracara: una clase de Java (`BigDecimal`, `LocalDate`) no se puede anotar y
necesita un serializador escrito a mano (`BigDecimalAsString`, `LocalDateIso`). → [Serialization][serialization],
[guía de kotlinx.serialization][guide]

### 2. Un `Json` por configuración

`Json` es inmutable y caro de crear (arma sus cachés de serializadores), así que se crea uno por configuración y se
reutiliza: la lección tiene `strictJson`, `lenientJson` y `prettyJson` como `val` de nivel superior. Por defecto es
**estricto**: una clave desconocida lanza `SerializationException`. Jackson también falla por defecto
(`FAIL_ON_UNKNOWN_PROPERTIES`), pero Spring Boot lo configura tolerante, así que quien viene de Spring suele esperar lo
contrario. El feed de Mercado trae una clave `venue` que el modelo no conoce, por eso se lee con `ignoreUnknownKeys`
(`loadTicks`). → [Customize the Json instance][json-config]

### 3. Nullable no es opcional

En kotlinx-serialization, un campo `val volume: Int?` **sin** valor por defecto es una clave **obligatoria**: si falta
en el JSON, es un error, aunque el tipo admita `null` (la prueba `missing nullable key…` lo muestra). Hay dos salidas:
darle un valor por defecto (`= null`) o configurar `explicitNulls = false`, que trata la clave ausente como `null`. Y al
escribir, los valores por defecto **no** se emiten salvo `encodeDefaults = true` (`toJson(Settings(…))`): quien consume
el JSON ve campos que faltan. → [`explicitNulls`][explicit-nulls]

### 4. Nombres y tipos de Java

`@SerialName("bid-price")` mapea una clave que no es un identificador válido (el `@JsonProperty` de Jackson); para todo
el modelo existe `namingStrategy` (estable en 1.13). Los montos viajan como **cadena** (`"99.84"`), nunca como número:
muchos clientes JSON parsean números a `Double` y ahí se pierde la exactitud. `MoneyAsString` escribe `"1.00 USD"` y al
leer exige la escala de la moneda: un `"1.5 USD"` es un error, no se redondea en silencio.

### 5. Jerarquías selladas: el polimorfismo sin registrar subtipos

Una `sealed interface MarketEvent` con subclases `@Serializable` se serializa con un **discriminador**: el JSON lleva
`"type": "trade"` (el `@SerialName` de cada subclase) y al leer se elige la clase por ese valor. Como el compilador conoce
todas las subclases, no hay que registrarlas (en Jackson se necesitan `@JsonTypeInfo` y `@JsonSubTypes`). Una propiedad
que se llame `type` choca con el discriminador por defecto. → [Polimorfismo en la guía][polymorphism]

### 6. El árbol `JsonElement`

Cuando el esquema es variable o solo interesa un campo, se navega el árbol sin clases: `parseToJsonElement`,
`jsonArray`, `jsonObject["volume"]?.jsonPrimitive?.longOrNull` (`totalVolume`, `tickersIn`), y se construye con el DSL
`buildJsonObject { put(…); putJsonArray(…) { } }` (`buildSummary`). Es el `JsonNode` de Jackson.

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

## Para profundizar

**Documentación oficial**

- [Serialization][serialization] (kotlinlang.org): qué es, el plugin y los formatos.
- [Customize the Json instance][json-config]: `ignoreUnknownKeys`, `explicitNulls`, `encodeDefaults`,
  `coerceInputValues`, `prettyPrint`, `namingStrategy`.
- [`explicitNulls`][explicit-nulls] (API): el comportamiento exacto de las claves ausentes.
- [Guía de kotlinx.serialization][guide] (GitHub): la guía completa, con [polimorfismo][polymorphism] y serializadores
  propios.
- [`jackson-module-kotlin`][jackson-kotlin]: la alternativa con Jackson (la que usa Spring por defecto, `09-spring`).

**Lecturas**

- [CHANGELOG de kotlinx.serialization][changelog]: qué cambió en cada versión (útil porque muchos ejemplos de internet son
  de versiones viejas, cuando `explicitNulls` pedía opt-in).

**Videos**

- [kotlinx.serialization 1.0][ks-1-0] (Leonid Startsev, JetBrains): el diseño de la librería contado por su autor.

## Estado

- **Hecho:** código en el caso Mercado (B0′ tandas 2 y 3) y 9 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ serializador binario).

[serialization]: https://kotlinlang.org/docs/serialization.html
[json-config]: https://kotlinlang.org/docs/serialization-json-configuration.html
[explicit-nulls]: https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/explicit-nulls.html
[guide]: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
[polymorphism]: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md
[jackson-kotlin]: https://github.com/FasterXML/jackson-module-kotlin
[changelog]: https://github.com/Kotlin/kotlinx.serialization/blob/master/CHANGELOG.md
[ks-1-0]: https://www.youtube.com/watch?v=Azi57n59ICM
