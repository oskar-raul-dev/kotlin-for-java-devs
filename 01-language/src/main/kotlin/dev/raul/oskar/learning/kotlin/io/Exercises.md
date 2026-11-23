# Ejercicios: entrada y salida: archivos y consola

> **Lección:** `io` · **Dónde se resuelven:** `io/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../io/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.io.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[2.1]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 24 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 07, 08. **Entrevista (🎤):** 06, 09, 10, 13, 24, 30.

> Las pruebas usan un directorio temporal propio (`createTempDirectory`) y lo borran al terminar, como `IoTest`.

## 🟢 Fáciles

**01 🟢 🎯 Recurso del classpath.** Leer `/data/ticks.json` y devolver la cantidad
de caracteres; si el recurso no existe, `requireNotNull` con un mensaje claro.
- Firma: `fun resourceText(name: String): String`

**02 🟢 Contar líneas, palabras y bytes.** Un `wc` en miniatura.
- Firma: `fun wc(path: Path): Triple<Int, Int, Long>`

**03 🟢 Escribir y releer.** Guardar una lista de líneas y volver a leerla; probar que un archivo inexistente da lista vacía.
- Firmas: `fun save(path: Path, lines: List<String>)`, `fun load(path: Path): List<String>`

**04 🟢 Agregar con fecha.** Un log que agrega `"2026-11-03T10:00:00Z mensaje"` usando un `Clock` inyectado.
- Firma: `fun log(path: Path, message: String, clock: Clock)`

**05 🟢 `[2.1]` Listar archivos.** Los `.kt` de un directorio, recursivo, ordenados.
- Firma: `fun kotlinFiles(root: Path): List<Path>`
- Pista: `Files.walk` dentro de `use`, o `root.walk()` (estable desde Kotlin 2.x; en 1.9 pedía `@OptIn(ExperimentalPathApi::class)`).

**06 🟢 🎤 Menú de consola.** Leer opciones de un `BufferedReader` hasta `"salir"` y escribir respuestas en un `Appendable`.
- Firma: `fun menu(input: BufferedReader, out: Appendable)`
- Casos: opción válida, inválida, EOF sin `"salir"`.

**07 🟢 🎯 Kermit.** Configurar un `Logger.withTag("statement")` y una prueba que capture los mensajes con un `LogWriter` propio
(guarda en una lista) para comprobar que se registró un aviso.

**24 🟢 🎤 `use` y las suprimidas.** Un recurso de prueba que puede fallar al usarse y al cerrarse.
- Firma: `class FlakyResource(val failOnUse: Boolean, val failOnClose: Boolean) : AutoCloseable` con `fun read(): String`,
  y `fun readOnce(r: FlakyResource): String = r.use { it.read() }`
- Casos: sin fallas; falla solo `close` → esa excepción; fallan los dos → sale la de `read` con la de `close` en
  `suppressed`; `close` se llama siempre (un contador lo prueba). Explica en un comentario la equivalencia con
  try-with-resources de Java.

**25 🟢 `[1.6]` `readln` frente a `readlnOrNull`.** Sumar números leídos de la entrada estándar hasta el fin.
- Firmas: `fun sumStdin(): Int` (con `readlnOrNull`) y `fun firstStdin(): String` (con `readln`)
- Casos: con `System.setIn(ByteArrayInputStream(…))` (restaurado en un `finally`): `"1\n2\n3\n"` → 6, vacío → 0,
  `firstStdin()` con entrada vacía → `RuntimeException`. Explica en un comentario por qué en el código de la lección
  se prefiere recibir un `BufferedReader`.

**26 🟢 Recurso relativo o absoluto.** Encontrar un recurso de tres formas y explicar cuál falla.
- Firma: `fun resourceUrls(name: String): Triple<URL?, URL?, URL?>` (con `javaClass.getResource(name)`,
  `javaClass.getResource("/$name")` y `javaClass.classLoader.getResource(name)`)
- Casos: con `"data/ticks.json"` la primera es `null` (es relativa al paquete) y las otras dos no; con un nombre
  inexistente, las tres `null`.

**27 🟢 `[2.1]` Archivos por patrón.** Los extractos de un directorio (no recursivo), ordenados por nombre.
- Firma: `fun statementFiles(dir: Path): List<Path>` (con `listDirectoryEntries("statement-*.csv")`)
- Casos: mezcla de `.csv`, `.txt` y subdirectorios, directorio vacío, directorio inexistente → `NoSuchFileException`.

**28 🟢 Escribir el extracto en CSV.** El camino de vuelta de `readStatement`, sin usar `csvField` de la lección:
las descripciones con comas o comillas van entre comillas y las comillas se doblan.
- Firma: `fun writeCsv(path: Path, lines: List<StatementLine>)`
- Casos: ida y vuelta con `readStatement` para el recurso completo, una descripción con `"` y otra con `,`, lista vacía
  (solo el encabezado).

## 🟡 Medios

**08 🟡 🎯 CSV a objetos.** Leer `data/bank-statement.csv`, descartar líneas malas **contándolas**, y devolver las líneas
y los errores.
- Firma: `fun loadStatement(path: Path): Pair<List<StatementLine>, List<String>>`

**09 🟡 🎤 Archivo grande perezoso.** Generar un archivo de 1 000 000 de líneas en la prueba y sumar una columna con `useLines`
sin cargarlo entero. Mide memoria con `Runtime` antes y después.

**10 🟡 🎤 Escritura atómica.** Escribir a un temporal en el mismo directorio y `Files.move(..., ATOMIC_MOVE)`, para que un lector
nunca vea un archivo a medias.
- Firma: `fun writeAtomically(path: Path, content: String)`

**11 🟡 Copiar con progreso.** Copiar un archivo por bloques de 8 KiB llamando a `onProgress(bytesCopied)`.
- Firma: `fun copy(from: Path, to: Path, onProgress: (Long) -> Unit)`

**12 🟡 Properties.** Leer un `.properties` con valores por defecto y variables de entorno que lo sobrescriben (`APP_PORT` → `app.port`).
- Firma: `fun config(path: Path, env: Map<String, String>): Map<String, String>`

**13 🟡 🎤 Codificación.** Escribir un texto con tildes en ISO-8859-1 y leerlo como UTF-8: muestra el mojibake en una prueba y arréglalo.

**14 🟡 Tail.** Las últimas `n` líneas de un archivo grande sin leerlo entero (`RandomAccessFile` desde el final).
- Firma: `fun tail(path: Path, n: Int): List<String>`

**29 🟡 Totales por mes.** Ingresos y egresos de un extracto por mes, leyendo en streaming con `useLines`.
- Firma: `fun monthlyTotals(path: Path): Map<YearMonth, Pair<BigDecimal, BigDecimal>>` (ingresos, egresos)
- Casos: un archivo chico escrito en la prueba con dos meses, un mes sin egresos, encabezado solo → mapa vacío; y el
  recurso `bank-statement.csv` (la prueba fija sus totales, calculados una vez).

**30 🟡 🎤 La secuencia que se escapa.** `fun leakyLines(path: Path): Sequence<String> = path.useLines { it }` compila,
pero falla al consumirse.
- Firmas: `fun leakyLines(path: Path): Sequence<String>` y dos arreglos: `fun linesList(path: Path): List<String>`
  y `fun <T> withLines(path: Path, block: (Sequence<String>) -> T): T`
- Casos: consumir `leakyLines` lanza (`IOException` o `IllegalStateException`: la prueba fija cuál); los dos
  arreglos funcionan. Explica en un comentario por qué y cuál prefieres para un archivo de 10 GB.

**31 🟡 Los movimientos más grandes.** Los `k` movimientos de mayor monto absoluto de un extracto enorme, con una
`PriorityQueue` de tamaño `k` mientras se lee (sin ordenar todo).
- Firma: `fun largestMovements(path: Path, k: Int): List<StatementLine>` (de mayor a menor)
- Casos: un archivo de 1 000 000 de líneas generado en la prueba con semilla fija (contra ordenar en memoria un
  archivo chico con la misma semilla), `k = 0`, `k` mayor que el archivo, empates.

**32 🟡 Kermit sin costo.** El mensaje de un log en lambda solo se construye si el nivel está habilitado.
- Firma: `fun auditTransfer(logger: Logger, from: String, to: String, amount: Money)` (registra en `debug` un
  mensaje caro de armar y en `info` uno corto)
- Casos: con el nivel mínimo en `Info`, la lambda del `debug` no se ejecuta (un contador lo prueba) y la del `info`
  sí; con `Debug`, las dos. Usa un `LogWriter` propio como en el ejercicio 07.

**33 🟡 `[exp 2.4]` Directorio temporal que siempre se borra.** Una función que crea un directorio temporal, se lo
pasa a un bloque y lo borra con `deleteRecursively()` aunque el bloque falle.
- Firma: `fun <T> withTempDir(block: (Path) -> T): T`
- Casos: devuelve lo del bloque y el directorio ya no existe; si el bloque lanza, la excepción sale y el directorio
  tampoco existe; con subdirectorios y archivos dentro. Explica en un comentario qué pide el `@OptIn` y por qué.

**34 🟡 El BOM invisible.** Un CSV exportado desde una hoja de cálculo trae `\uFEFF` al inicio y el encabezado
`"date"` ya no coincide.
- Firma: `fun readTextWithoutBom(path: Path): String`
- Casos: archivo con BOM UTF-8 (`EF BB BF`), sin BOM, vacío; demuestra en una prueba que `lines.first() == "date,…"`
  falla con BOM y pasa con la función.

**35 🟡 Fines de línea.** Un extracto con `\r\n` (Windows) partido con `split("\n")` deja un `\r` pegado al último
campo y el monto no se parsea.
- Firma: `fun parseAmounts(text: String): List<BigDecimal>` (la última columna de cada línea)
- Casos: el mismo texto con `\n`, con `\r\n` y mezclado da lo mismo; demuestra el fallo con `split("\n")` y
  compara con `lines()` y con `readLines()`.

## 🟠 Difíciles

**15 🟠 Vigilar un directorio.** Con `WatchService`, detectar archivos nuevos `.json` y procesarlos; prueba con un hilo que crea archivos.

**16 🟠 Zip.** Comprimir un directorio en un `.zip` y descomprimirlo, protegiéndose del *zip slip* (rutas con `../`).
- Firmas: `fun zip(dir: Path, out: Path)`, `fun unzip(zip: Path, into: Path)`

**17 🟠 Lector de CSV robusto.** Combinar el ejercicio de comillas de `strings` con `useLines`, admitiendo campos con saltos de línea.

**18 🟠 Bloqueo de archivo.** Dos procesos (o dos hilos con `FileChannel.lock`) que agregan a un mismo archivo sin pisarse.

**19 🟠 Binario.** Escribir y leer registros de tamaño fijo con `DataOutputStream`/`DataInputStream` y acceso aleatorio por índice.

**36 🟠 Extractos duplicados.** Encontrar archivos con el mismo contenido en un árbol de directorios: primero se
agrupan por tamaño y solo los que coinciden se comparan por SHA-256 calculado en streaming (`DigestInputStream`).
- Firma: `fun duplicates(root: Path): List<List<Path>>` (grupos de dos o más, cada uno ordenado)
- Casos: tres copias de un extracto en carpetas distintas, dos archivos del mismo tamaño y contenido distinto,
  archivos vacíos, un archivo de 50 MB (que no se carga entero: compruébalo con el heap de la prueba).

**37 🟠 Contar líneas con memoria mapeada.** Contar las líneas de un archivo grande con `FileChannel.map` y comparar
con `useLines` y con `Files.lines`.
- Firma: `fun countLinesMapped(path: Path): Long`
- Casos: archivo vacío, sin salto final, con `\r\n`, más de 2 GB (mapear por tramos: un solo `map` no pasa de
  `Int.MAX_VALUE` bytes; la prueba grande puede ir con `@Tag("slow")`). Mide los tres y explica los tiempos en un comentario.

**38 🟠 Procesos externos.** Correr un comando, leer su salida estándar y de error **a la vez** (si no, un proceso con
mucha salida se bloquea) y cortar por tiempo.
- Firma: `fun runCommand(command: List<String>, timeout: Duration): CommandResult` con
  `data class CommandResult(val exitCode: Int, val stdout: String, val stderr: String)`
- Casos: `java -version` (con la ruta de `System.getProperty("java.home")`, la versión sale por stderr), un comando
  que escribe 10 MB, uno que no termina → `TimeoutException` y el proceso queda destruido, un comando inexistente.

**39 🟠 Leer solo lo nuevo.** El ledger es un archivo al que solo se agregan líneas; leer las nuevas desde la última
vez guardando el desplazamiento en bytes en un archivo de control.
- Firma: `fun readNew(ledger: Path, checkpoint: Path): List<LedgerLine>`
- Casos: primera lectura (todo), sin cambios (nada), líneas agregadas (solo esas), una línea a medio escribir al
  final (se espera a que termine), el archivo truncado o rotado (más chico que el desplazamiento: empieza de nuevo).

**40 🟠 Ancho fijo a CSV.** Convertir en streaming un archivo bancario de ancho fijo (como el ejercicio 32 de
`strings`) a CSV, mandando las líneas malas a un archivo de rechazos con su número de línea.
- Firma: `fun convert(input: Path, output: Path, rejects: Path, layout: List<Pair<String, IntRange>>): Pair<Int, Int>`
  (convertidas, rechazadas)
- Casos: todo bien, líneas cortas, un monto ilegible, archivo vacío; los dos archivos de salida se escriben de forma
  atómica (ejercicio 10) y un fallo a mitad no deja salidas parciales.

## 🔴 Muy difíciles

**20 🔴 Ordenamiento externo.** Ordenar un archivo de 10 millones de enteros con poca memoria: partir en trozos ordenados y mezclar
con una `PriorityQueue`.

**21 🔴 Log rotativo.** Un `Appender` que rota por tamaño (`app.log`, `app.1.log`, …, máximo `k`), seguro entre hilos.

**22 🔴 REPL.** Una consola que evalúa expresiones del ejercicio 24 de `control`, con historial, variables (`x = 3`) y errores que no la tumban.

**23 🔴 Re-procesador idempotente.** Procesar archivos de un directorio de entrada, moverlos a `done/` o `failed/`, y que una caída a mitad
no procese nada dos veces (marcadores y `ATOMIC_MOVE`).

## ⚡ Saiyajin

**41 ⚡ Conciliador de extracto.** Una CLI que lee `bank-statement.csv` y los asientos de `ledger-movements.csv`,
empareja con tolerancias (±1 día, ±0.01 USD; a igual candidato, el más cercano en fecha y luego en monto) y escribe el
reporte de diferencias: conciliadas, solo en el extracto, solo en el ledger y monto distinto.
- Firma: `fun reconcile(statement: Sequence<StatementLine>, ledger: List<LedgerLine>, tolerance: Tolerance): ReconciliationReport`,
  `fun writeReport(report: ReconciliationReport, out: Appendable)` y un `main(args)` que recibe las dos rutas y la
  salida (`run -Plesson=io.practice` o similar); los errores de parseo se registran con Kermit y se cuentan en el reporte.
- **Se verifica con:** la salida idéntica, byte por byte, a un archivo dorado guardado en `src/test/resources`; casos
  chicos escritos a mano para cada categoría y para los empates; y un extracto sintético de un millón de líneas que
  corre con el heap limitado (por ejemplo, una prueba en un `Test` aparte con `maxHeapSize = "64m"`), lo que obliga a
  leer el extracto en streaming con `useLines`.
