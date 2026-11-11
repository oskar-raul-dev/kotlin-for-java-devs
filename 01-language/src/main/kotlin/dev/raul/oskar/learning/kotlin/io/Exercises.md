# Ejercicios: entrada y salida: archivos y consola

> **Lección:** `io` · **Dónde se resuelven:** `io/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../io/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.io.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 07, 08.

> Las pruebas usan un directorio temporal propio (`createTempDirectory`) y lo borran al terminar, como `IoTest`.

## 🟢 Fáciles

**01 🟢 🎯 Recurso del classpath.** Leer `/data/orders.json` y devolver la cantidad
de caracteres; si el recurso no existe, `requireNotNull` con un mensaje claro.
- Firma: `fun resourceText(name: String): String`

**02 🟢 Contar líneas, palabras y bytes.** Un `wc` en miniatura.
- Firma: `fun wc(path: Path): Triple<Int, Int, Long>`

**03 🟢 Escribir y releer.** Guardar una lista de líneas y volver a leerla; probar que un archivo inexistente da lista vacía.
- Firmas: `fun save(path: Path, lines: List<String>)`, `fun load(path: Path): List<String>`

**04 🟢 Agregar con fecha.** Un log que agrega `"2026-11-03T10:00:00Z mensaje"` usando un `Clock` inyectado.
- Firma: `fun log(path: Path, message: String, clock: Clock)`

**05 🟢 Listar archivos.** Los `.kt` de un directorio, recursivo, ordenados.
- Firma: `fun kotlinFiles(root: Path): List<Path>`
- Pista: `Files.walk` dentro de `use`, o `root.walk()` (experimental en 1.9).

**06 🟢 Menú de consola.** Leer opciones de un `BufferedReader` hasta `"salir"` y escribir respuestas en un `Appendable`.
- Firma: `fun menu(input: BufferedReader, out: Appendable)`
- Casos: opción válida, inválida, EOF sin `"salir"`.

**07 🟢 🎯 Kermit.** Configurar un `Logger.withTag("orders")` y una prueba que capture los mensajes con un `LogWriter` propio
(guarda en una lista) para comprobar que se registró un aviso.

## 🟡 Medios

**08 🟡 🎯 CSV a objetos.** Leer `data/sales.csv`, descartar líneas malas **contándolas**, y devolver ventas y errores.
- Firma: `fun loadSales(path: Path): Pair<List<Sale>, List<String>>`

**09 🟡 Archivo grande perezoso.** Generar un archivo de 1 000 000 de líneas en la prueba y sumar una columna con `useLines`
sin cargarlo entero. Mide memoria con `Runtime` antes y después.

**10 🟡 Escritura atómica.** Escribir a un temporal en el mismo directorio y `Files.move(..., ATOMIC_MOVE)`, para que un lector
nunca vea un archivo a medias.
- Firma: `fun writeAtomically(path: Path, content: String)`

**11 🟡 Copiar con progreso.** Copiar un archivo por bloques de 8 KiB llamando a `onProgress(bytesCopied)`.
- Firma: `fun copy(from: Path, to: Path, onProgress: (Long) -> Unit)`

**12 🟡 Properties.** Leer un `.properties` con valores por defecto y variables de entorno que lo sobrescriben (`APP_PORT` → `app.port`).
- Firma: `fun config(path: Path, env: Map<String, String>): Map<String, String>`

**13 🟡 Codificación.** Escribir un texto con tildes en ISO-8859-1 y leerlo como UTF-8: muestra el mojibake en una prueba y arréglalo.

**14 🟡 Tail.** Las últimas `n` líneas de un archivo grande sin leerlo entero (`RandomAccessFile` desde el final).
- Firma: `fun tail(path: Path, n: Int): List<String>`

## 🟠 Difíciles

**15 🟠 Vigilar un directorio.** Con `WatchService`, detectar archivos nuevos `.json` y procesarlos; prueba con un hilo que crea archivos.

**16 🟠 Zip.** Comprimir un directorio en un `.zip` y descomprimirlo, protegiéndose del *zip slip* (rutas con `../`).
- Firmas: `fun zip(dir: Path, out: Path)`, `fun unzip(zip: Path, into: Path)`

**17 🟠 Lector de CSV robusto.** Combinar el ejercicio de comillas de `strings` con `useLines`, admitiendo campos con saltos de línea.

**18 🟠 Bloqueo de archivo.** Dos procesos (o dos hilos con `FileChannel.lock`) que agregan a un mismo archivo sin pisarse.

**19 🟠 Binario.** Escribir y leer registros de tamaño fijo con `DataOutputStream`/`DataInputStream` y acceso aleatorio por índice.

## 🔴 Muy difíciles

**20 🔴 Ordenamiento externo.** Ordenar un archivo de 10 millones de enteros con poca memoria: partir en trozos ordenados y mezclar
con una `PriorityQueue`.

**21 🔴 Log rotativo.** Un `Appender` que rota por tamaño (`app.log`, `app.1.log`, …, máximo `k`), seguro entre hilos.

**22 🔴 REPL.** Una consola que evalúa expresiones del ejercicio 24 de `control`, con historial, variables (`x = 3`) y errores que no la tumban.

**23 🔴 Re-procesador idempotente.** Procesar archivos de un directorio de entrada, moverlos a `done/` o `failed/`, y que una caída a mitad
no procese nada dos veces (marcadores y `ATOMIC_MOVE`).
