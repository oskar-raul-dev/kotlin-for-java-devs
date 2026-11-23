# Ejercicios: cadenas y expresiones regulares

> **Lección:** `strings` · **Dónde se resuelven:** `strings/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../strings/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.strings.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[2.2]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 08, 09. **Entrevista (🎤):** 08, 17, 23, 25, 26, 33.

## 🟢 Fáciles

**01 🟢 🎯 Fecha MM/dd/yyyy.** Partir `"03/14/1985"` en mes, día y año **sin regex** y sin la trampa de
`split("\\/")`. Devolver `null` si no hay tres partes numéricas.
- Firma: `fun mdy(text: String?): Triple<Int, Int, Int>?`
- Casos: correcta, `"3/14/1985"`, `"03-14-1985"`, `null`, `""`.

**02 🟢 Iniciales.** `"oskar raúl cahuenas"` → `"ORC"`.
- Firma: `fun initials(name: String): String`
- Casos: espacios múltiples, una sola palabra, cadena vacía.

**03 🟢 Contar vocales.** Incluyendo tildes (`á é í ó ú ü`).
- Firma: `fun vowels(text: String): Int`

**04 🟢 Plantilla de reporte.** Raw string con `trimIndent` y una lista con `joinToString` dentro de la plantilla.
- Firma: `fun receipt(account: String, items: List<Pair<String, Int>>): String` (cada ítem: ticker y cantidad)

**05 🟢 Truncar con elipsis.** Si pasa de `max`, cortar y terminar en `…` sin superar `max` caracteres.
- Firma: `fun ellipsize(text: String, max: Int): String`

**06 🟢 Slug.** `"Hola, Mundo Kotlin!"` → `"hola-mundo-kotlin"`.
- Firma: `fun slug(text: String): String`
- Casos: tildes (`"Canción"` → `"cancion"`, con `Normalizer`), guiones repetidos, bordes.

**07 🟢 Comparar sin mayúsculas.** Ordenar una lista de nombres sin importar mayúsculas ni tildes.
- Firma: `fun sortNames(names: List<String>): List<String>`
- Pista: `Collator.getInstance(Locale("es"))`.

**25 🟢 🎤 `[2.2]` Dólares literales.** Un `$` suelto en una plantilla interpola: escaparlo de dos formas.
- Firmas: `fun priceLine(ticker: String, price: String): String` (con `\$`) y `fun quoteJson(ticker: String): String`
  (con un raw string `$$"""…"""`, donde `$$ticker` interpola y `$` es literal)
- Casos: `priceLine("ACME", "100.00") == "ACME: $100.00"`; `quoteJson("ACME")` →
  `{"ticker": "ACME", "currency": "$"}`. Explica en un comentario cómo se hacía antes de 2.2 en un raw string
  (`${'$'}`).

**26 🟢 🎤 `[1.5]` Mayúsculas sin locale.** Normalizar tickers con `uppercase()` y demostrar el bug de la "i" turca.
- Firma: `fun normalizeTicker(raw: String): String` (sin espacios alrededor y en mayúsculas)
- Casos: `" acme "` → `"ACME"`; con `Locale.setDefault(Locale("tr"))`, `normalizeTicker("visa")` sigue dando `"VISA"`
  mientras que `"visa".uppercase(Locale("tr"))` da `"VİSA"` (restaura el locale en un `finally`). Explica en un
  comentario por qué `toUpperCase()` sin argumentos ya no compila desde 2.1.

**27 🟢 Fila del extracto.** Columnas de ancho fijo con `padEnd`/`padStart` y monto con separador de miles.
- Firma: `fun statementRow(date: String, description: String, amountCents: Long): String` (fecha de 10, descripción
  de 20 cortada si es más larga, monto de 12 alineado a la derecha)
- Casos: `("2026-11-03", "Transferencia", 123456)` → `"2026-11-03Transferencia           1,234.56"`, descripción larga,
  monto negativo (`-12.50`), cero. Pista: `String.format(Locale.US, "%,.2f", …)` o hazlo a mano con `Long`.

**28 🟢 Clave y valor.** Una línea de configuración `clave = valor` con `substringBefore`/`substringAfter`.
- Firma: `fun keyValue(line: String): Pair<String, String>?`
- Casos: `"currency = USD"` → `("currency", "USD")`, `"a=b=c"` → `("a", "b=c")`, sin `=` → `null`, clave vacía →
  `null`, comentario `"# x=1"` → `null`.

**29 🟢 Barras de volumen.** Un histograma de texto con `buildString`, escalado al mayor volumen.
- Firma: `fun volumeBars(volumes: List<Pair<String, Long>>, width: Int): String`
- Casos: `[("ACME", 1200), ("GLOBX", 600)]` con ancho 10 → `"ACME  ########## 1200\nGLOBX #####       600"` (ticker alineado
  al más largo, espacio, barra de `width`, espacio, volumen alineado a la derecha), todos en cero, lista vacía → `""`.

## 🟡 Medios

**08 🟡 🎯 🎤 Mensaje FIX.** Parsear `35=D|55=ACME|54=1|38=10|44=100.00` a una `data class NewOrder(ticker, side, quantity, price)`,
validando cada tag (54: 1 compra, 2 venta; 38: entero positivo; 44: precio con dos decimales); `null` si falta o falla uno.
- Firma: `fun parseNewOrder(raw: String?): NewOrder?`
- Casos: tags en otro orden, `54=3`, `38=0`, `44=abc`, partir con `split("\\|")` (la trampa) y con `split("|")`, `null`.

**09 🟡 🎯 Dígitos de control de la cuenta.** Validar `ACC-00001234-82` con control = 98 − (número × 100 mod 97), como el
IBAN, y generar el número completo a partir de los 8 dígitos.
- Firmas: `fun isValidAccount(s: String): Boolean`, `fun accountFor(number: Int): String`
- Casos: `ACC-00001234-82` y `ACC-00005678-39` válidos, un dígito cambiado, sin guiones, `ACC-1234-82`.

**10 🟡 Compresión RLE.** `"aaabccdddd"` ↔ `"a3b1c2d4"`.
- Firmas: `fun rle(s: String): String`, `fun unrle(s: String): String`
- Casos: números de dos dígitos (`"a12"`), cadena vacía, ida y vuelta.

**11 🟡 Palabras más frecuentes.** Las `k` palabras más frecuentes, ignorando mayúsculas y signos, con desempate alfabético.
- Firma: `fun topWords(text: String, k: Int): List<Pair<String, Int>>`

**12 🟡 Camel y snake.** `"bidPrice"` ↔ `"bid_price"` ↔ `"bid-price"`.
- Firmas: `fun toSnake(s: String): String`, `fun toCamel(s: String): String`
- Casos: acrónimos (`"parseURLValue"`), números.

**13 🟡 Plantillas `{{nombre}}`.** Reemplazar con un mapa usando `Regex.replace` con lambda; las claves que faltan quedan intactas.
- Firma: `fun render(template: String, values: Map<String, String>): String`

**14 🟡 Log de acceso.** Parsear líneas `127.0.0.1 - - [03/Nov/2026:10:00:00 -0500] "GET /api HTTP/1.1" 200 512`
con grupos con nombre.
- Firma: `fun parseLog(line: String): LogEntry?`
- Casos: línea válida, código no numérico, línea truncada.

**15 🟡 Enmascarar datos personales.** Ocultar emails (`a***@dominio.com`), teléfonos y números de tarjeta en un texto libre.
- Firma: `fun redact(text: String): String`

**30 🟡 `[1.7]` Tokenizer con `matchAt`.** Partir un filtro de órdenes (`price >= 100.50 and side == BUY`) en tokens,
probando en cada posición una regex anclada con `Regex.matchAt(text, i)`.
- Firma: `fun tokens(filter: String): List<String>`
- Casos: el ejemplo → `["price", ">=", "100.50", "and", "side", "==", "BUY"]`, sin espacios (`price>=1`), cadena
  vacía, un carácter desconocido (`price # 1`) → `IllegalArgumentException` con la posición (6) en el mensaje.

**31 🟡 Montos por país.** Formatear un `Money` (de `basics`) con `NumberFormat.getCurrencyInstance(locale)`.
- Firma: `fun formatMoney(m: Money, locale: Locale): String`
- Casos: `1234.56 USD` en `en-US` → `"$1,234.56"`; en `de-DE` y `es-CO` (la prueba fija la cadena exacta del JDK
  21, que lleva espacios duros `\u00A0`: la trampa); `500 JPY` sin decimales. Explica en un comentario por qué
  comparar con un espacio normal falla.

**32 🟡 Ancho fijo.** Partir una línea de un archivo bancario de ancho fijo según un diseño de columnas.
- Firma: `fun parseFixedWidth(line: String, layout: List<Pair<String, IntRange>>): Map<String, String>?` (campos sin
  espacios alrededor)
- Casos: una línea completa, campos con relleno, una línea más corta que el diseño → `null`, rangos que se solapan
  en el diseño → `IllegalArgumentException`.

**33 🟡 🎤 La regex en su lugar.** Buscar cuentas `ACC-…` en muchas líneas con la regex en un `val` de nivel superior,
y la misma función con `Regex(…)` dentro del bucle.
- Firmas: `fun findAccounts(lines: List<String>): List<String>`, `fun findAccountsSlow(lines: List<String>): List<String>`
- Casos: las dos dan lo mismo sobre 100 000 líneas; una prueba mide las dos con `measureTimeMillis` y lo imprime (sin
  afirmar tiempos, que harían la prueba frágil). Explica en un comentario qué cuesta compilar un patrón y dónde va un
  `Regex` en una clase (`companion object`).

**34 🟡 Separadores escapados.** En un formato donde los valores pueden contener `|`, se escapa como `\|` (y la barra
como `\\`). Partir y unir respetando los escapes.
- Firmas: `fun splitEscaped(s: String, sep: Char = '|'): List<String>`, `fun joinEscaped(parts: List<String>, sep: Char = '|'): String`
- Casos: `"a\|b|c"` → `["a|b", "c"]`, barra al final → `IllegalArgumentException`, partes vacías, ida y vuelta
  para listas con `|` y `\`.

**35 🟡 Comparar versiones.** `"1.9.22"` contra `"2.4.21"` y `"2.0.0-RC1"`: segmentos numéricos y, a igual número,
la preliminar va antes.
- Firma: `fun compareVersions(a: String, b: String): Int`
- Casos: `1.9.22 < 1.10.0` (no alfabético), `2.0.0-RC1 < 2.0.0`, `2.0 == 2.0.0`, `2.0.0-Beta2 < 2.0.0-RC1`, texto
  inválido → `IllegalArgumentException`; ordenar una lista con `sortedWith(::compareVersions)`.

## 🟠 Difíciles

**16 🟠 CSV con comillas.** Partir una línea CSV que admite campos entre comillas con comas y comillas dobladas (`""`).
- Firma: `fun parseCsvLine(line: String): List<String>`
- Casos: `a,"b,c",d`, `"di ""hola"""`, campo vacío al final.

**17 🟠 🎤 Subcadena más larga sin repetir.** Ventana deslizante con un mapa de últimas posiciones.
- Firma: `fun longestUnique(s: String): String`

**18 🟠 Validar contraseña con lookaheads.** Una sola regex: 12+ caracteres, mayúscula, minúscula, dígito, símbolo, sin espacios.
Luego reescríbela con reglas separadas que digan **cuál** falla. ¿Cuál preferirías en un PR?
- Firmas: `fun strongRegex(p: String): Boolean`, `fun weaknesses(p: String): List<String>`

**19 🟠 Distancia de Levenshtein.** Con DP en dos filas; usarla para sugerir la ciudad más parecida de una lista.
- Firmas: `fun levenshtein(a: String, b: String): Int`, `fun suggest(input: String, options: List<String>): String?`

**20 🟠 Expansión de rangos.** `"1-3,5,7-9"` ↔ `[1, 2, 3, 5, 7, 8, 9]`.
- Firmas: `fun expand(spec: String): List<Int>`, `fun compress(xs: List<Int>): String`

**36 🟠 IBAN.** Validar un IBAN de cualquier país: mover los cuatro primeros caracteres al final, cambiar letras por
números (`A` = 10 … `Z` = 35) y comprobar que el resultado mod 97 es 1, **sin `BigInteger`** (mod por tramos).
- Firmas: `fun isValidIban(s: String): Boolean`, `fun formatIban(s: String): String` (grupos de cuatro)
- Casos: `"GB82 WEST 1234 5698 7654 32"` (el ejemplo de la norma) válido, con y sin espacios, en minúsculas, un
  dígito cambiado, dos dígitos permutados, caracteres que no son letras ni dígitos.

**37 🟠 Comodines.** Suscripción a tickers con patrones `*` (cualquier cosa) y `?` (un carácter), sin regex.
- Firma: `fun globMatch(pattern: String, text: String): Boolean`
- Casos: `"AC*"` con `"ACME"`, `"A?ME"`, `"*"` con vacío, `"**X"`, `"A*B*C"` con `"AXBYC"`, sin comodines. Después,
  una segunda versión que traduzca el patrón a `Regex` con `Regex.escape` y compara las dos en 10 000 casos
  aleatorios.

**38 🟠 Diff de extractos.** Las diferencias entre dos versiones de un extracto, línea por línea, con la subsecuencia
común más larga.
- Firma: `fun diffLines(old: List<String>, new: List<String>): List<String>` (cada línea con prefijo `"  "`, `"- "` o `"+ "`)
- Casos: iguales, una línea cambiada, líneas agregadas al inicio y al final, una lista vacía, y que quitando los `"+ "`
  se recupera `old` y quitando los `"- "`, `new`.

**39 🟠 Cortar sin romper Unicode.** `"👍🏽".length` es 4: cortar por `length` puede partir un emoji o separar una tilde
combinada de su letra.
- Firma: `fun safeTruncate(text: String, max: Int): String` (a lo sumo `max` grafemas, con `BreakIterator`)
- Casos: ASCII, `"e\u0301"` (una sola `é` en dos `Char`), `"👍🏽👍"`, una bandera (`"🇨🇴"`), `max = 0`. Compara
  `length`, `codePointCount` y la cantidad de grafemas para cada caso.

**40 🟠 Monto en letras.** Para los cheques del Banco: `1234.56` → `"mil doscientos treinta y cuatro con 56/100"`.
- Firma: `fun inWords(cents: Long): String`
- Casos: 0 → `"cero con 00/100"`, 1, 15, 16 (`"dieciséis"`), 21 (`"veintiuno"`), 100 (`"cien"`), 101 (`"ciento uno"`),
  1 000 000 (`"un millón"`), 2 000 000 (`"dos millones"`), negativos → `IllegalArgumentException`.

## 🔴 Muy difíciles

**21 🔴 KMP.** Buscar todas las ocurrencias de un patrón con Knuth-Morris-Pratt y comparar contra `Regex.findAll`
con 10 000 cadenas aleatorias.
- Firma: `fun kmp(text: String, pattern: String): List<Int>`

**22 🔴 Mini-JSON tokenizer.** Tokenizar `{"a": [1, 2.5, "x\"y"], "b": null}` en tokens tipados, con posiciones para los errores.
- Firma: `fun tokenize(json: String): List<Token>` (`sealed interface Token`)

**23 🔴 🎤 Regex catastrófica.** Demostrar con una prueba con tiempo límite (`assertTimeoutPreemptively`) que `(a+)+$` contra
`"aaaa…a!"` explota, y reescribir el patrón para que sea lineal. Explicar el backtracking en un comentario.

**24 🔴 Plantillas con bucles.** Extender el ejercicio 13 con `{{#items}}…{{/items}}` sobre listas de mapas, anidable.
- Firma: `fun renderBlocks(template: String, model: Map<String, Any>): String`

## ⚡ Saiyajin

**41 ⚡ Códec estilo FIX.** Generar y leer mensajes completos
`8=FIX.4.4|9=…|35=D|55=ACME|54=1|38=10|44=100.00|10=…|`, con su longitud y su checksum. El tag 9 es la cantidad de
caracteres desde el campo siguiente al 9 hasta el separador anterior al 10, incluido; el tag 10 es la suma de los
bytes de todo lo anterior a `10=`, módulo 256, con tres dígitos. En FIX real el separador es `\u0001`; aquí se usa
`|` para leerlo, pero el códec acepta los dos.
- Firma: `class FixCodec(val separator: Char = '|')` con `fun encode(msgType: String, fields: List<Pair<Int, String>>): String`
  y `fun decode(raw: String): FixMessage`; `data class FixMessage(val msgType: String, val fields: List<Pair<Int, String>>)`;
  los errores, como `class FixFormatException(val tag: Int?, val position: Int, message: String) : RuntimeException(message)`.
- Valida: los tres primeros campos son 8, 9 y 35, y el último es 10; tags numéricos; sin tags repetidos (salvo los
  que declares repetibles); los tipos de los tags conocidos (54 en `1`/`2`, 38 entero positivo, 44 decimal).
- **Se verifica con:** ida y vuelta (`decode(encode(m)) == m`) sobre un juego de al menos 20 mensajes válidos, con
  los dos separadores; y mensajes corruptos (checksum malo, tag repetido, `44=abc`, longitud falsa, `10` que no está
  al final, campo sin `=`) que dan exactamente el tag y la posición del primer carácter del campo con el problema.
