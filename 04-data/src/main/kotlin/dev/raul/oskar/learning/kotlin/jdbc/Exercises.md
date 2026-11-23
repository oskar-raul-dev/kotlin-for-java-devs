# Ejercicios: JDBC con SQLite, PostgreSQL y MySQL

> **Lecciones:** `jdbc.sqlite`, `jdbc.postgres`, `jdbc.mysql` (y lo común en `jdbc/`) · **Dónde se resuelven:**
> `jdbc/practice/ExNN<Nombre>.kt`, con pruebas en `src/test/kotlin/.../jdbc/practice/`. Si el ejercicio vale para los tres motores,
> escribe la prueba como **contrato** abstracto (igual que `LedgerRepositoryContract`) y una subclase por motor.
> **Correr:** `./gradlew -p 04-data test --tests '*.jdbc.practice.*'` (con los compose arriba para PG y MySQL; si no, esas pruebas salen `SKIPPED`).
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **Motor:** `[todos]`, `[SQLite]`, `[PG]`, `[MySQL]`. **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que
> cierra la lección. Los números 26 a 40 se agregaron después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** ninguno; esta lección va después de las de lenguaje. **Entrevista (🎤):** 07, 08, 16, 17, 26, 33.

## 🟢 Fáciles

**01 🟢 Conectar y preguntar la versión.** `[todos]` Con `DriverManager`, `metaData.databaseProductVersion` y `SELECT version()`
(o `sqlite_version()`). Cierra todo con `use`.

**02 🟢 Crear e insertar.** `[todos]` Una tabla `holder(id, name, email UNIQUE)` (titulares de cuentas) con el autoincremento propio de cada motor e
inserción con `PreparedStatement` devolviendo la clave generada.

**03 🟢 Leer a objetos.** `[todos]` `findAll()` mapeando con la extensión `mapRows` a una `data class`.

**04 🟢 Nulos en JDBC.** `[todos]` Una columna `phone` nullable: escribir con `setNull(i, Types.VARCHAR)` y leer con `getString` + `wasNull()`
para un `INT` nullable (¿qué devuelve `getInt` si es NULL?).

**05 🟢 Contar y existir.** `[todos]` `count()` y `exists(email)` con `SELECT 1 … LIMIT 1` (¿`FETCH FIRST` en los tres?).

**06 🟢 Base en archivo.** `[SQLite]` Crear la base en `build/`, cerrarla, reabrirla y comprobar que los datos siguen; luego `jdbc:sqlite::memory:` y ver que no.

**07 🟢 🎤 Inyección SQL.** `[todos]` Escribe a propósito un `findByName` concatenando y una prueba que lo explote con `' OR '1'='1`; luego arréglalo.

**26 🟢 🎤 Un `PreparedStatement` reutilizado.** `[todos]` Insertar 1000 tasas de cambio con un solo
`PreparedStatement` (parámetros nuevos en cada vuelta) y con un `Statement` que concatena.
- Firmas: `fun insertPrepared(conn: Connection, rates: Map<String, BigDecimal>)` y `fun insertConcatenated(…)`
- Casos: las dos dejan las mismas filas; una moneda con comilla (`"X'Y"`) rompe la concatenada; la prueba imprime los
  tiempos. Explica en un comentario las tres razones para preferir `PreparedStatement` (inyección, tipos y plan
  reutilizado en el servidor).

**27 🟢 `SUM` de nada.** `[todos]` El total de una moneda sin cuentas: `SUM` sobre cero filas es `NULL`, no `0`.
- Firmas: `fun sumNaive(conn: Connection, currency: String): BigDecimal?` y `fun sumOrZero(conn: Connection, currency: String): BigDecimal`
- Casos: moneda con cuentas, moneda sin cuentas (`null` en la ingenua, `0.00` con `COALESCE`); con `getInt` en vez
  de `getBigDecimal` el `NULL` parece `0` (la trampa de `wasNull()`).

**28 🟢 El monto que vuelve como `Double`.** `[SQLite]` Guardar `0.10` en una columna `NUMERIC` y leerla con
`getObject`: la afinidad de SQLite la devuelve como `Double`.
- Firma: `fun roundTrip(conn: Connection, amount: BigDecimal, columnType: String): Any?` (`"NUMERIC"`, `"TEXT"`,
  `"INTEGER"` en centavos)
- Casos: la clase de lo que vuelve en cada tipo, y que solo `TEXT` y `INTEGER` en centavos devuelven el monto exacto
  para `0.10`, `19.99` y `12345678901234.56`. Explica en un comentario qué hace el `Dialect` de SQLite de la lección.

**29 🟢 Saldos por moneda.** `[todos]` Un `GROUP BY` a `Map<String, BigDecimal>` con `mapRows`, en orden
determinista.
- Firma: `fun balancesByCurrency(conn: Connection): Map<String, BigDecimal>`
- Casos: dos monedas, una sola, sin cuentas → mapa vacío; el orden de las claves es alfabético (`ORDER BY`), igual
  en los tres motores.

**30 🟢 Movimientos entre fechas.** `[todos]` Los movimientos de una cuenta en un rango de fechas, con `LocalDate`
como parámetro.
- Firma: `fun movementsBetween(conn: Connection, account: String, from: LocalDate, toInclusive: LocalDate): List<MovementRow>`
- Casos: los dos bordes entran, un rango vacío, `from > to` → `IllegalArgumentException`; en SQLite las fechas son
  `TEXT` ISO y la comparación funciona porque el orden lexicográfico coincide (explícalo en un comentario).

## 🟡 Medios

**08 🟡 🎤 Paginación.** `[todos]` `page(n, size)` con `LIMIT/OFFSET` y luego con *keyset* (`WHERE id > ? ORDER BY id LIMIT ?`). ¿Por qué el segundo escala mejor?

**09 🟡 Transacción con rollback.** `[todos]` Transferir saldo entre cuentas con `inTransaction`; una prueba que falle a la mitad y
compruebe que no quedó nada a medias.

**10 🟡 Lote grande.** `[todos]` Insertar 50 000 filas fila a fila contra `addBatch` cada 1000, midiendo tiempos. En MySQL prueba
`rewriteBatchedStatements=true` en la URL y en PG `reWriteBatchedInserts=true`.

**11 🟡 Fechas por JDBC.** `[PG]` `[MySQL]` Guardar y leer `LocalDate`, `LocalDateTime` e `Instant` con `setObject`/`getObject(col, Clase::class.java)`.

**12 🟡 Upsert devolviendo si insertó o actualizó.** `[PG]` Con `RETURNING (xmax = 0) AS inserted`. `[MySQL]` Con el valor de `executeUpdate` (1 o 2).

**13 🟡 Claves foráneas.** `[todos]` `account → holder` con `ON DELETE CASCADE`. En SQLite, comprueba que sin `PRAGMA foreign_keys = ON` no se aplica.

**14 🟡 Pool de conexiones.** `[todos]` Agrega HikariCP en un experimento y mide abrir 200 conexiones con `DriverManager` frente al pool.

**15 🟡 Metadatos.** `[todos]` Listar tablas y columnas con `DatabaseMetaData.getTables`/`getColumns` y generar una `data class` en texto.

**31 🟡 Extracto con saldo acumulado.** `[todos]` Los movimientos de una cuenta con el saldo después de cada uno,
calculado en SQL con `SUM(…) OVER (ORDER BY …)` (SQLite tiene funciones de ventana desde 3.25).
- Firma: `fun statement(conn: Connection, account: String, opening: BigDecimal): List<StatementRow>`
- Casos: tres transferencias de entrada y salida, una cuenta sin movimientos, el último saldo acumulado coincide con
  el de `findAccount`; el mismo resultado calculado en Kotlin con `runningFold`.

**32 🟡 Una sola vez por clave.** `[todos]` Repetir una transferencia con la misma clave de idempotencia devuelve el
asiento que ya existe en vez de fallar.
- Firma: `fun transferOnce(repo: LedgerRepository, conn: Connection, key: String, from: String, to: String, amount: BigDecimal): Long`
- Casos: la primera crea el asiento, la segunda devuelve el mismo id y no mueve saldos; la violación de unicidad se
  reconoce por motor (el `SQLState` o el código de SQLite, en el `Dialect` y no en el repositorio); claves distintas
  crean asientos distintos.

**33 🟡 🎤 Índices y `EXPLAIN`.** `[PG]` Buscar los movimientos de una cuenta con 100 000 postings, con y sin índice
en la columna de la cuenta.
- Firma: `fun planFor(conn: Connection, sql: String, vararg params: Any): String` (el tipo de nodo de
  `EXPLAIN (FORMAT JSON)`)
- Casos: sin índice → `"Seq Scan"`; después de `CREATE INDEX` y `ANALYZE` → `"Index Scan"` o `"Bitmap Heap Scan"`; un
  `LIKE '%texto'` no usa el índice. Explica en un comentario cuándo un índice no se usa aunque exista.

**34 🟡 Huecos en el `AUTO_INCREMENT`.** `[MySQL]` `ON DUPLICATE KEY UPDATE` consume un valor de autoincremento
aunque termine actualizando.
- Firma: `fun upsertAndNextId(conn: Connection, currency: String, rate: BigDecimal): Long` (el id que tendrá la
  siguiente fila nueva)
- Casos: tres upserts de la misma moneda y luego una moneda nueva: su id no es 2 (la prueba fija cuál es); la
  alternativa con `INSERT … SELECT … WHERE NOT EXISTS` + `UPDATE` no deja huecos. Explica en un comentario por qué
  los huecos no deberían importar si los ids no tienen significado.

**35 🟡 Tiempo máximo de consulta.** `[todos]` `setQueryTimeout` corta una consulta lenta con `SQLTimeoutException`.
- Firma: `fun runWithTimeout(conn: Connection, sql: String, seconds: Int): Result<Unit>`
- Casos: una consulta lenta por motor, en el `Dialect` de la prueba (`pg_sleep(5)`, `SLEEP(5)` y en SQLite un CTE
  recursivo pesado), con 1 segundo → falla de tipo `SQLTimeoutException` (o la que dé cada driver: la prueba la fija);
  la conexión sigue sirviendo después.

**36 🟡 Leer sin cargar todo.** `[todos]` Recorrer un millón de postings sin traerlos todos a memoria: cada driver
pide algo distinto.
- Firma: `fun <T> streamPostings(conn: Connection, block: (Sequence<MovementRow>) -> T): T`
- Casos: en PostgreSQL hace falta `autoCommit = false` y `fetchSize`; en MySQL `useCursorFetch=true` (o
  `fetchSize = Int.MIN_VALUE`); en SQLite ya es así. La prueba suma los montos con el heap limitado y comprueba que
  el `ResultSet` se cierra aunque el bloque lance.

## 🟠 Difíciles

**16 🟠 🎤 Niveles de aislamiento.** `[PG]` `[MySQL]` Con dos conexiones, reproducir una *non-repeatable read* en `READ COMMITTED` y
mostrar que desaparece en `REPEATABLE READ`. ¿Por qué el resultado por defecto difiere entre PG y MySQL?

**17 🟠 🎤 Actualización perdida.** `[todos]` Dos hilos leen el saldo de una cuenta, restan y escriben: muestra la pérdida, y arréglala de tres formas:
`UPDATE … SET balance = balance - ?`, `SELECT … FOR UPDATE` (no en SQLite) y versión optimista (`WHERE version = ?`).

**18 🟠 Deadlock.** `[PG]` `[MySQL]` Provocar un deadlock con dos transacciones que bloquean filas en orden inverso, capturar el error
(SQLState `40P01` / `40001`) y reintentar.

**19 🟠 `jsonb`.** `[PG]` Guardar los ticks de `ticks.json` como `jsonb`, consultar por `data->>'ticker'` con un índice GIN, y comparar el plan con `EXPLAIN`.

**20 🟠 Migraciones a mano.** `[todos]` Una tabla `schema_version` y una lista de scripts numerados que se aplican una sola vez, en transacción.

**21 🟠 Mapeo de errores.** `[todos]` Traducir `SQLException` por SQLState (`23505`, `23000`, `SQLITE_CONSTRAINT`…) a excepciones de dominio
(`DuplicateEmail`, `ForeignKeyViolation`) iguales para los tres motores.

**37 🟠 Bloqueos en orden.** `[PG]` `[MySQL]` Evitar el deadlock del ejercicio 18 bloqueando siempre las dos cuentas
en el mismo orden (por id) con `SELECT … FOR UPDATE`.
- Firma: `fun transferLocked(conn: Connection, from: String, to: String, amount: BigDecimal)`
- Casos: 1000 transferencias concurrentes entre dos cuentas en los dos sentidos, en 8 hilos con su propia conexión:
  ningún deadlock, ningún saldo negativo y el total igual al inicial; la misma carga bloqueando en el orden de la
  transferencia produce al menos un deadlock (la prueba lo cuenta).

**38 🟠 Bloqueos consultivos.** `[PG]` Serializar el procesamiento por cuenta con `pg_advisory_xact_lock` sin tocar
filas, y comparar con `FOR UPDATE`.
- Firma: `fun <T> withAccountLock(conn: Connection, account: String, block: () -> T): T` (la clave del bloqueo sale
  de un hash estable del id)
- Casos: dos hilos con la misma cuenta se turnan (un registro de entrada y salida lo prueba), con cuentas distintas
  corren a la vez; el bloqueo se suelta al terminar la transacción, también si el bloque lanza; dos ids con el mismo
  hash se serializan (explica en un comentario si eso es un problema).

**39 🟠 Conciliación en SQL.** `[todos]` Cargar `bank-statement.csv` en una tabla y conciliar contra el ledger con un
`FULL OUTER JOIN` por fecha y monto.
- Firma: `fun reconcileSql(conn: Connection, statement: List<StatementLine>): Reconciliation` (la de la lección `io`,
  agregando `01-language` como dependencia de prueba o copiando el tipo)
- Casos: el mismo resultado que `reconcileExact` de `io` en los tres motores; MySQL no tiene `FULL OUTER JOIN`
  (se arma con `LEFT JOIN` ∪ `RIGHT JOIN`) y SQLite lo tiene desde 3.39: la diferencia va en el `Dialect`.

**40 🟠 Tu propio contrato.** `[todos]` Un contrato de pruebas abstracto para las tasas de cambio (`upsertRate`,
`rate`, `rateCount`), con una subclase por motor, igual que `LedgerRepositoryContract`.
- Firma: `abstract class RateRepositoryContract` con `abstract val dialect: Dialect` y `abstract fun openConnection(): Connection?`
- Casos: al menos 6 pruebas (insertar, actualizar, moneda que no existe, escala, muchas monedas, y dos conexiones que
  hacen upsert de la misma moneda a la vez sin dejar duplicados); las tres subclases en verde y las de PG y MySQL en
  `SKIPPED` si su contenedor está abajo.

## 🔴 Muy difíciles

**22 🔴 Mini-ORM.** `[todos]` `insert(entity)` y `findById<T>(id)` genéricos por reflexión de Java sobre `data class` con una anotación `@Table`.

**23 🔴 Outbox transaccional.** `[PG]` Escribir el asiento de una transferencia y un mensaje en `outbox` en la misma transacción, y un *poller* que publica con
`SELECT … FOR UPDATE SKIP LOCKED` en dos hilos sin duplicar.

**24 🔴 Volumen acumulado en SQL.** `[PG]` Cargar `ticks.json` a una tabla y calcular el volumen por ticker y día de mercado y la suma
de los 7 días anteriores con funciones de ventana (`SUM(...) OVER (PARTITION BY … ORDER BY day RANGE BETWEEN INTERVAL '7 days' PRECEDING AND INTERVAL '1 day' PRECEDING)`).
Compara con el resultado en Kotlin del ejercicio 21 de `collections`.

**25 🔴 Mismo ejercicio, tres motores.** `[todos]` Repite el 24 en MySQL 8 (funciones de ventana) y en SQLite (también las tiene desde 3.25)
y documenta las diferencias de sintaxis de intervalos.

## ⚡ Saiyajin

**41 ⚡ Ledger concurrente en tres motores.** `[todos]` El `LedgerRepository` de la lección con transferencias
concurrentes: varios hilos, cada uno con su conexión (o un pool), moviendo dinero al azar entre 20 cuentas.
- Firma: `class ConcurrentLedger(dialect: Dialect, connections: () -> Connection)` con
  `fun transfer(from: String, to: String, amount: BigDecimal): Boolean` (rechaza sin saldo) y
  `fun runLoad(threads: Int, transfers: Int, seed: Long): LoadReport` (hechas, rechazadas, reintentadas por conflicto)
- Dos estrategias elegibles: bloqueo pesimista (`SELECT … FOR UPDATE`, en orden) y optimista (columna `version` y
  reintento); en SQLite, que bloquea la base entera, `BEGIN IMMEDIATE` y reintentos ante `SQLITE_BUSY`.
- **Se verifica con:** un contrato abstracto con una subclase por motor; una prueba que muestra la actualización
  perdida sin bloqueo (leer, restar, escribir) y otra que la evita con cada estrategia; después de 5000
  transferencias en 8 hilos, la suma de todos los saldos es la misma que al inicio, ningún saldo es negativo y todos
  los asientos están balanceados (`unbalancedEntries()` vacío).
