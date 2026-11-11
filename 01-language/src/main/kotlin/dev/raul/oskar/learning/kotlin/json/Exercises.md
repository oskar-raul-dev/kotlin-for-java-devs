# Ejercicios: serialización JSON con kotlinx-serialization

> **Lección:** `json` · **Dónde se resuelven:** `json/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../json/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.json.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 03, 08.

> Recordatorio: `Json` se construye una vez y se reutiliza. Los datos de ejemplo están en `src/main/resources/data/orders.json`.

## 🟢 Fáciles

**01 🟢 🎯 Ida y vuelta.** `@Serializable data class Customer(val id: String, val name: String, val email: String?)`: serializar,
deserializar y comprobar igualdad.

**02 🟢 🎯 Claves con guiones.** Un JSON con `"order-total"` y `"customer-email"` mapeado con `@SerialName`. ¿Qué error da si lo olvidas?
Captura la `SerializationException` en una prueba y revisa el mensaje.

**03 🟢 🎯 Campo que falta.** Con el `Json` por defecto, un `String?` sin valor por defecto que falta en el JSON, ¿falla o da `null`?
Pruébalo, luego con `explicitNulls = false`, luego con `= null` como default. Deja la tabla de resultados en un comentario.

**04 🟢 Claves desconocidas.** Deserializar un JSON con un campo extra con y sin `ignoreUnknownKeys`.

**05 🟢 Listas y mapas.** `List<Customer>` y `Map<String, Int>` directo a JSON y de vuelta.

**06 🟢 Pretty print.** Un `Json` con `prettyPrint = true` y `prettyPrintIndent = "  "` (experimental: requiere `@OptIn`).

**07 🟢 Enums.** Un `enum class Status` serializado con `@SerialName` en minúsculas por constante.

## 🟡 Medios

**08 🟡 🎯 Cargar con tolerancia.** Leer `orders.json` y devolver los eventos buenos y, para cada uno malo, el índice y el error.
Pista: parsear primero a `JsonArray` y decodificar cada elemento con `decodeFromJsonElement`.
- Firma: `fun loadTolerant(text: String): Pair<List<OrderEvent>, List<Pair<Int, String>>>`

**09 🟡 Defaults y `encodeDefaults`.** Una clase con defaults: ¿qué se escribe con y sin `encodeDefaults`? ¿Y con `@EncodeDefault`?

**10 🟡 `coerceInputValues`.** Un `null` o un enum desconocido en un campo no nulo con default: pruébalo con y sin la opción.

**11 🟡 Serializador de `Instant`.** Como `epochMillis` (`Long`) en un caso y como texto ISO-8601 en otro.

**12 🟡 Serializador de fecha `MM/dd/yyyy`.** Para que un campo `"order-date": "03/14/2026"` llegue ya como `LocalDate?`, devolviendo `null` en fechas inválidas
**sin** romper todo el documento. ¿Es buena idea tragarse el error? Argumenta en un comentario como si fuera un comentario de PR.

**13 🟡 Árbol JSON.** Sin clases: sumar `order-total` por `address.state`, ignorando los que no tienen estado.

**14 🟡 Construir con el DSL.** Un reporte `{"generatedAt": …, "alerts": [{…}], "summary": {…}}` con `buildJsonObject`.

**15 🟡 `@Transient` y propiedades calculadas.** Una clase con un campo que no se debe serializar y una propiedad calculada en el cuerpo:
¿cuál aparece en el JSON?

## 🟠 Difíciles

**16 🟠 Polimorfismo con discriminador propio.** Una `sealed class Event` con `"kind"` como discriminador (`classDiscriminator = "kind"`)
y una subclase con `@SerialName`.

**17 🟠 Polimorfismo sin discriminador.** Elegir la subclase por la forma del JSON con un `JsonContentPolymorphicSerializer`
(si tiene `"iban"` es transferencia, si tiene `"last4"` es tarjeta).

**18 🟠 Renombrado global.** `namingStrategy = JsonNamingStrategy.SnakeCase` (experimental en 1.6.x) frente a `@SerialName` por campo:
¿quién gana? Pruébalo.

**19 🟠 Migración de versiones.** El JSON v1 tiene `"name"`; el v2 tiene `"firstName"` y `"lastName"`. Leer los dos con un
`JsonTransformingSerializer`.

**20 🟠 Streaming.** Decodificar un arreglo JSON enorme (generado en la prueba, 500 000 elementos) elemento a elemento con
`Json.decodeToSequence` desde un `InputStream`, sin cargarlo entero.

## 🔴 Muy difíciles

**21 🔴 Serializador genérico para `Result`.** `ResultSerializer<T>(dataSerializer: KSerializer<T>)` que escriba `{"ok": …}` o `{"error": "…"}`.

**22 🔴 Validación al deserializar.** Un `KSerializer` para `Email` (value class) que rechace formatos inválidos con un mensaje que
incluya la ruta (`$[3].email`).

**23 🔴 Comparar con Jackson.** Agrega `jackson-module-kotlin` en un experimento aparte y compara: nulos, defaults, clases sin
anotar y rendimiento con 1 millón de objetos. ¿Qué elegirías en un servicio Spring Boot y por qué?

**24 🔴 JSON canónico.** Serializar con claves ordenadas y números normalizados para poder firmar el contenido (HMAC) y que la firma
no cambie al reordenar campos.
