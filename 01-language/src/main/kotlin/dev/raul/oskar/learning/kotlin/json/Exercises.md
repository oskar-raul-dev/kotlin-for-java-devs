# Ejercicios: serialización JSON con kotlinx-serialization

> **Lección:** `json` · **Dónde se resuelven:** `json/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../json/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.json.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41. Lo que diga "experimental" se refiere a kotlinx-serialization 1.13.0.

**Prioritarios (🎯):** 01, 02, 03, 08. **Entrevista (🎤):** 03, 16, 23, 25, 26, 36.

> Recordatorio: `Json` se construye una vez y se reutiliza. Los datos de ejemplo están en `src/main/resources/data/ticks.json`.

## 🟢 Fáciles

**01 🟢 🎯 Ida y vuelta.** `@Serializable data class Account(val id: String, val currency: String, val alias: String?)`: serializar,
deserializar y comprobar igualdad.

**02 🟢 🎯 Claves con guiones.** Un JSON con `"bid-price"` y `"ask-price"` mapeado con `@SerialName`. ¿Qué error da si lo olvidas?
Captura la `SerializationException` en una prueba y revisa el mensaje.

**03 🟢 🎯 🎤 Campo que falta.** Con el `Json` por defecto, un `String?` sin valor por defecto que falta en el JSON, ¿falla o da `null`?
Pruébalo, luego con `explicitNulls = false`, luego con `= null` como default. Deja la tabla de resultados en un comentario.

**04 🟢 Claves desconocidas.** Deserializar un JSON con un campo extra con y sin `ignoreUnknownKeys`.

**05 🟢 Listas y mapas.** `List<Account>` y `Map<String, Int>` directo a JSON y de vuelta.

**06 🟢 Pretty print.** Un `Json` con `prettyPrint = true` y `prettyPrintIndent = "  "` (era experimental en 1.6.x y pedía `@OptIn`; en 1.13 ya no).

**07 🟢 Enums.** Un `enum class Status` serializado con `@SerialName` en minúsculas por constante.

**25 🟢 🎤 Plugin, no reflexión.** Lo que genera el plugin del compilador se ve en el `descriptor` del serializador.
- Firmas: `fun serialName(): String` (de `Tick.serializer().descriptor`) y `fun fieldNames(): List<String>`
- Casos: los nombres de los campos de `Tick` incluyen `"bid-price"` (el de `@SerialName`, no el de la propiedad);
  cuáles son opcionales (`isElementOptional`). Explica en un comentario qué clase genera el plugin (búscala con
  `javap` en `build/classes`) y qué cambia frente a la reflexión de Jackson (arranque, ProGuard/R8, GraalVM).

**26 🟢 🎤 Un `Json` por configuración.** Dos configuraciones derivadas de una base, creadas una sola vez.
- Firmas: `val feedJson: Json` (`ignoreUnknownKeys`, `explicitNulls = false`) y `val reportJson: Json`
  (`Json(from = feedJson) { prettyPrint = true }`)
- Casos: `reportJson` hereda `ignoreUnknownKeys` (pruébalo con un campo extra) y además indenta; `feedJson` no
  indenta. Explica en un comentario por qué crear un `Json { … }` dentro de cada llamada es un error de rendimiento.

**27 🟢 `@Required`.** Un campo con valor por defecto que igual debe venir en el JSON.
- Firma: `@Serializable data class TransferRequest(val from: String, val to: String, val amount: String, @Required val currency: String = "USD")`
- Casos: sin `currency` → `MissingFieldException` (con el nombre del campo en el mensaje); con `currency` funciona;
  al serializar, `currency` siempre se escribe aunque tenga el valor por defecto.

**28 🟢 `Money` en JSON.** Usar el `MoneyAsString` de la lección en un modelo propio, por campo y para todo el archivo.
- Firmas: `@Serializable data class Balance(val account: String, @Serializable(with = MoneyAsString::class) val amount: Money)`
  y la misma clase en otro archivo con `@file:UseSerializers(MoneyAsString::class)`
- Casos: ida y vuelta de las dos (la prueba fija el texto que produce `MoneyAsString`), un monto con escala
  equivocada en el JSON → excepción, una lista de `Balance`.

**29 🟢 Precio como texto o como número.** En el árbol JSON, `"99.84"` y `99.84` son primitivos distintos pero los dos
tienen `content`.
- Firma: `fun priceOf(obj: JsonObject): BigDecimal?`
- Casos: `{"price": "99.84"}`, `{"price": 99.84}`, sin `price` → `null`, `{"price": null}` → `null` (`JsonNull`),
  `{"price": "abc"}` → `null`. Compara `jsonPrimitive.isString` en los dos primeros.

## 🟡 Medios

**08 🟡 🎯 Cargar con tolerancia.** Leer `ticks.json` y devolver los ticks buenos y, para cada uno malo, el índice y el error.
Pista: parsear primero a `JsonArray` y decodificar cada elemento con `decodeFromJsonElement`.
- Firma: `fun loadTolerant(text: String): Pair<List<Tick>, List<Pair<Int, String>>>`

**09 🟡 Defaults y `encodeDefaults`.** Una clase con defaults: ¿qué se escribe con y sin `encodeDefaults`? ¿Y con `@EncodeDefault`?

**10 🟡 `coerceInputValues`.** Un `null` o un enum desconocido en un campo no nulo con default: pruébalo con y sin la opción.

**11 🟡 Serializador de `Instant`.** Como `epochMillis` (`Long`) en un caso y como texto ISO-8601 en otro.

**12 🟡 Serializador de fecha `MM/dd/yyyy`.** Para que un campo `"settle-date": "10/26/2026"` llegue ya como `LocalDate?`, devolviendo `null` en fechas inválidas
**sin** romper todo el documento. ¿Es buena idea tragarse el error? Argumenta en un comentario como si fuera un comentario de PR.

**13 🟡 Árbol JSON.** Sin clases: sumar `volume` por `ticker` en `ticks.json`, ignorando los que no tienen volumen.

**14 🟡 Construir con el DSL.** Un reporte `{"generatedAt": …, "alerts": [{…}], "summary": {…}}` con `buildJsonObject`.

**15 🟡 `@Transient` y propiedades calculadas.** Una clase con un campo que no se debe serializar y una propiedad calculada en el cuerpo:
¿cuál aparece en el JSON?

**30 🟡 Serializador flexible.** Un `KSerializer<BigDecimal>` que acepta el precio como texto o como número y siempre
lo escribe como texto.
- Firma: `object FlexibleBigDecimal : KSerializer<BigDecimal>` (con `JsonDecoder.decodeJsonElement()` al leer)
- Casos: los dos formatos dan el mismo `BigDecimal` (sin pasar por `Double`: `0.1` llega exacto), un booleano →
  `SerializationException`, usarlo con un formato que no es JSON → excepción con un mensaje claro.

**31 🟡 `value class` serializable.** `@Serializable @JvmInline value class Ticker(val symbol: String)` con validación
en `init` (de 1 a 5 letras mayúsculas).
- Casos: dentro de una `data class` se escribe como `"ACME"`, no como `{"symbol": "ACME"}`; un ticker inválido en el
  JSON falla al leer (la prueba fija qué excepción sale); una `List<Ticker>`.

**32 🟡 `data object` en una jerarquía.** `sealed interface SessionEvent` con `data object Open`, `data object Close`
y `data class Halt(val reason: String)`, cada uno con `@SerialName` en minúsculas.
- Casos: `Open` se escribe `{"type":"open"}`, `Halt` lleva su campo; ida y vuelta de una lista mezclada; un `type`
  desconocido → `SerializationException`.

**33 🟡 Qué se escribe.** La tabla de lo que se serializa según `explicitNulls` y `encodeDefaults`, para un campo
`String? = null`, otro `String = "x"` y otro `String?` sin default.
- Firma: `fun encodeWith(explicitNulls: Boolean, encodeDefaults: Boolean, value: Sample): String`
- Casos: las cuatro combinaciones con los campos en `null`/default y con valores; la prueba fija la tabla completa.
  Explica en un comentario qué combinación usarías para una API pública y cuál para guardar en disco.

**34 🟡 JSON Lines.** Un tick por línea (NDJSON), como lo mandaría un feed.
- Firmas: `fun writeJsonLines(path: Path, ticks: List<Tick>)` y `fun <T> readJsonLines(path: Path, block: (Sequence<Tick>) -> T): T`
- Casos: ida y vuelta de `ticks.json`; líneas en blanco se saltan; una línea mala da una excepción con su número de
  línea; el archivo se cierra aunque el bloque lance (cuidado con la trampa de `useLines` de `io`).

**35 🟡 Claves que no son texto.** Cierres por fecha (`Map<LocalDate, BigDecimal>`) y un mapa con clave compuesta.
- Firmas: `fun closesJson(closes: Map<LocalDate, BigDecimal>): String` y
  `fun volumesJson(volumes: Map<TickerDay, Long>): String` con `@Serializable data class TickerDay(val ticker: String, val day: Int)`
- Casos: las fechas quedan como claves de objeto (`"2026-11-03"`); la clave compuesta falla con el `Json` por defecto
  y con `allowStructuredMapKeys = true` se escribe como arreglo `[clave, valor, …]`; ida y vuelta de las dos.

## 🟠 Difíciles

**16 🟠 🎤 Polimorfismo con discriminador propio.** Una `sealed class Event` con `"kind"` como discriminador (`classDiscriminator = "kind"`)
y una subclase con `@SerialName`.

**17 🟠 Polimorfismo sin discriminador.** Elegir la subclase por la forma del JSON con un `JsonContentPolymorphicSerializer`
(si tiene `"iban"` es transferencia, si tiene `"last4"` es tarjeta).

**18 🟠 Renombrado global.** `namingStrategy = JsonNamingStrategy.SnakeCase` (experimental en 1.6.x) frente a `@SerialName` por campo:
¿quién gana? Pruébalo.

**19 🟠 Migración de versiones.** El JSON v1 tiene `"name"`; el v2 tiene `"firstName"` y `"lastName"`. Leer los dos con un
`JsonTransformingSerializer`.

**20 🟠 Streaming.** Decodificar un arreglo JSON enorme (generado en la prueba, 500 000 elementos) elemento a elemento con
`Json.decodeToSequence` desde un `InputStream`, sin cargarlo entero.

**36 🟠 🎤 Sobre genérico.** Una respuesta de API con versión y datos de cualquier tipo.
- Firma: `@Serializable data class Envelope<T>(val version: Int, val data: T, val errors: List<String> = emptyList())`
- Casos: `Envelope<List<Tick>>` y `Envelope<MarketEvent>` (polimórfico dentro del genérico) con `serializer<…>()` y
  con `Envelope.serializer(Tick.serializer())`; `Envelope<Money>` con `MoneyAsString`. Explica en un comentario
  cómo resuelve el plugin el serializador de `T` sin reflexión (y qué hace Jackson con el borrado de tipos).

**37 🟠 Polimorfismo abierto.** Comandos de una interfaz **no** sellada, registrados en un `SerializersModule`, con un
deserializador por defecto para tipos desconocidos.
- Firma: `interface Command`, dos implementaciones `@Serializable`, y `val commandJson: Json` con
  `polymorphic(Command::class) { subclass(…); defaultDeserializer { UnknownCommand.serializer() } }`
- Casos: ida y vuelta de los dos conocidos; un `type` desconocido se lee como `UnknownCommand` sin fallar; sin
  registrar una subclase, serializarla falla con un mensaje que lo dice.

**38 🟠 Diff de JSON.** Las diferencias entre dos documentos, con la ruta de cada una.
- Firma: `fun jsonDiff(a: JsonElement, b: JsonElement): List<String>` (`"$.summary.count: 3 -> 4"`,
  `"+ $.alerts[2]"`, `"- $.venue"`)
- Casos: iguales (vacío), un valor cambiado, una clave agregada y otra quitada, arreglos de distinto largo, un cambio
  de tipo (objeto por arreglo), el orden de las claves no importa.

**39 🟠 JSONPath mínimo.** Seleccionar nodos con rutas `$.a.b`, `$.a[2]` y `$.a[*].b`.
- Firma: `fun select(root: JsonElement, path: String): List<JsonElement>`
- Casos: sobre `ticks.json`, `$[*].ticker` da todos los tickers y `$[0].price` el primero; una ruta que no existe →
  vacía; índice fuera de rango → vacía; ruta mal formada → `IllegalArgumentException` con la posición.

**40 🟠 Describir el modelo.** Un resumen tipo JSON Schema generado desde el `SerialDescriptor`, sin reflexión.
- Firma: `fun describe(descriptor: SerialDescriptor): JsonObject` (tipo de cada campo, cuáles son obligatorios,
  listas, enums con sus valores, clases anidadas y jerarquías selladas como `oneOf`)
- Casos: `Tick` (campos opcionales y nombres de `@SerialName`), `MarketEvent` (tres subtipos), un enum, una clase
  recursiva (un árbol: no debe entrar en un bucle infinito).

## 🔴 Muy difíciles

**21 🔴 Serializador genérico para `Result`.** `ResultSerializer<T>(dataSerializer: KSerializer<T>)` que escriba `{"ok": …}` o `{"error": "…"}`.

**22 🔴 Validación al deserializar.** Un `KSerializer` para `Email` (value class) que rechace formatos inválidos con un mensaje que
incluya la ruta (`$[3].email`).

**23 🔴 🎤 Comparar con Jackson.** Agrega `jackson-module-kotlin` en un experimento aparte y compara: nulos, defaults, clases sin
anotar y rendimiento con 1 millón de objetos. ¿Qué elegirías en un servicio Spring Boot y por qué?

**24 🔴 JSON canónico.** Serializar con claves ordenadas y números normalizados para poder firmar el contenido (HMAC) y que la firma
no cambie al reordenar campos.

## ⚡ Saiyajin

**41 ⚡ Serializador para un formato binario.** Un `Encoder`/`Decoder` propio para un formato compacto que sirve para
cualquier clase `@Serializable`: enteros con *varint* (y *zigzag* para los negativos), cadenas UTF-8 con prefijo de
longitud, booleanos en un byte, un byte de marca para los nullables, colecciones y mapas con prefijo de tamaño, y sin
nombres de campo (el orden lo da el descriptor). Las jerarquías selladas escriben el `serialName` antes del valor.
- Firma: `object Compact { fun <T> encode(serializer: SerializationStrategy<T>, value: T): ByteArray; fun <T> decode(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T }`,
  con un `AbstractEncoder` y un `AbstractDecoder` (piden `@OptIn(ExperimentalSerializationApi::class)`).
- **Se verifica con:** ida y vuelta de `Tick`, de una lista de `MarketEvent` y de un mapa; los bytes exactos de un caso
  fijo escrito a mano en la prueba (un `Tick` con un nullable en `null`); datos truncados o un varint de más de 10
  bytes → `SerializationException`; y una prueba que mide que `ticks.json` en binario ocupa menos que en JSON e
  imprime la proporción.
