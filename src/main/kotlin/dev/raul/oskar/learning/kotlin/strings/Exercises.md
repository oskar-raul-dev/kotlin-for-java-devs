# Ejercicios: cadenas y expresiones regulares

> **Lección:** `strings` · **Dónde se resuelven:** `strings/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../strings/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew test --tests '*.strings.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 08, 09.

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
- Firma: `fun receipt(customer: String, items: List<Pair<String, Int>>): String`

**05 🟢 Truncar con elipsis.** Si pasa de `max`, cortar y terminar en `…` sin superar `max` caracteres.
- Firma: `fun ellipsize(text: String, max: Int): String`

**06 🟢 Slug.** `"Hola, Mundo Kotlin!"` → `"hola-mundo-kotlin"`.
- Firma: `fun slug(text: String): String`
- Casos: tildes (`"Canción"` → `"cancion"`, con `Normalizer`), guiones repetidos, bordes.

**07 🟢 Comparar sin mayúsculas.** Ordenar una lista de nombres sin importar mayúsculas ni tildes.
- Firma: `fun sortNames(names: List<String>): List<String>`
- Pista: `Collator.getInstance(Locale("es"))`.

## 🟡 Medios

**08 🟡 🎯 Código de estado.** Validar estados de EE. UU. de dos letras (`"CA"`, `"ca"` se normaliza) contra un `Set`;
devolver `null` si no es válido.
- Firma: `fun normalizeState(raw: String?): String?`
- Casos: espacios, minúsculas, `"C A"`, `"XX"`, `null`.

**09 🟡 🎯 Zip de EE. UU.** Regex para `"12345"` y `"12345-6789"`; extraer el zip de 5 dígitos de una dirección libre.
- Firmas: `fun isZip(s: String): Boolean`, `fun zip5(address: String): String?`

**10 🟡 Compresión RLE.** `"aaabccdddd"` ↔ `"a3b1c2d4"`.
- Firmas: `fun rle(s: String): String`, `fun unrle(s: String): String`
- Casos: números de dos dígitos (`"a12"`), cadena vacía, ida y vuelta.

**11 🟡 Palabras más frecuentes.** Las `k` palabras más frecuentes, ignorando mayúsculas y signos, con desempate alfabético.
- Firma: `fun topWords(text: String, k: Int): List<Pair<String, Int>>`

**12 🟡 Camel y snake.** `"orderTotal"` ↔ `"order_total"` ↔ `"order-total"`.
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

## 🟠 Difíciles

**16 🟠 CSV con comillas.** Partir una línea CSV que admite campos entre comillas con comas y comillas dobladas (`""`).
- Firma: `fun parseCsvLine(line: String): List<String>`
- Casos: `a,"b,c",d`, `"di ""hola"""`, campo vacío al final.

**17 🟠 Subcadena más larga sin repetir.** Ventana deslizante con un mapa de últimas posiciones.
- Firma: `fun longestUnique(s: String): String`

**18 🟠 Validar contraseña con lookaheads.** Una sola regex: 12+ caracteres, mayúscula, minúscula, dígito, símbolo, sin espacios.
Luego reescríbela con reglas separadas que digan **cuál** falla. ¿Cuál preferirías en un PR?
- Firmas: `fun strongRegex(p: String): Boolean`, `fun weaknesses(p: String): List<String>`

**19 🟠 Distancia de Levenshtein.** Con DP en dos filas; usarla para sugerir la ciudad más parecida de una lista.
- Firmas: `fun levenshtein(a: String, b: String): Int`, `fun suggest(input: String, options: List<String>): String?`

**20 🟠 Expansión de rangos.** `"1-3,5,7-9"` ↔ `[1, 2, 3, 5, 7, 8, 9]`.
- Firmas: `fun expand(spec: String): List<Int>`, `fun compress(xs: List<Int>): String`

## 🔴 Muy difíciles

**21 🔴 KMP.** Buscar todas las ocurrencias de un patrón con Knuth-Morris-Pratt y comparar contra `Regex.findAll`
con 10 000 cadenas aleatorias.
- Firma: `fun kmp(text: String, pattern: String): List<Int>`

**22 🔴 Mini-JSON tokenizer.** Tokenizar `{"a": [1, 2.5, "x\"y"], "b": null}` en tokens tipados, con posiciones para los errores.
- Firma: `fun tokenize(json: String): List<Token>` (`sealed interface Token`)

**23 🔴 Regex catastrófica.** Demostrar con una prueba con tiempo límite (`assertTimeoutPreemptively`) que `(a+)+$` contra
`"aaaa…a!"` explota, y reescribir el patrón para que sea lineal. Explicar el backtracking en un comentario.

**24 🔴 Plantillas con bucles.** Extender el ejercicio 13 con `{{#items}}…{{/items}}` sobre listas de mapas, anidable.
- Firma: `fun renderBlocks(template: String, model: Map<String, Any>): String`
