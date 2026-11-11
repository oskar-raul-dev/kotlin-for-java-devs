# Ejercicios: JDBC con SQLite, PostgreSQL y MySQL

> **Lecciones:** `jdbc.sqlite`, `jdbc.postgres`, `jdbc.mysql` (y lo común en `jdbc/`) · **Dónde se resuelven:**
> `jdbc/practice/ExNN<Nombre>.kt`, con pruebas en `src/test/kotlin/.../jdbc/practice/`. Si el ejercicio vale para los tres motores,
> escribe la prueba como **contrato** abstracto (igual que `ProductRepositoryContract`) y una subclase por motor.
> **Correr:** `./gradlew test --tests '*.jdbc.practice.*'` (con los compose arriba para PG y MySQL; si no, esas pruebas salen `SKIPPED`).
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **Motor:** `[todos]`, `[SQLite]`, `[PG]`, `[MySQL]`.

**Prioritarios (🎯):** ninguno; esta lección va después de las de lenguaje.

## 🟢 Fáciles

**01 🟢 Conectar y preguntar la versión.** `[todos]` Con `DriverManager`, `metaData.databaseProductVersion` y `SELECT version()`
(o `sqlite_version()`). Cierra todo con `use`.

**02 🟢 Crear e insertar.** `[todos]` Una tabla `customer(id, name, email UNIQUE)` con el autoincremento propio de cada motor e
inserción con `PreparedStatement` devolviendo la clave generada.

**03 🟢 Leer a objetos.** `[todos]` `findAll()` mapeando con la extensión `mapRows` a una `data class`.

**04 🟢 Nulos en JDBC.** `[todos]` Una columna `phone` nullable: escribir con `setNull(i, Types.VARCHAR)` y leer con `getString` + `wasNull()`
para un `INT` nullable (¿qué devuelve `getInt` si es NULL?).

**05 🟢 Contar y existir.** `[todos]` `count()` y `exists(email)` con `SELECT 1 … LIMIT 1` (¿`FETCH FIRST` en los tres?).

**06 🟢 Base en archivo.** `[SQLite]` Crear la base en `build/`, cerrarla, reabrirla y comprobar que los datos siguen; luego `jdbc:sqlite::memory:` y ver que no.

**07 🟢 Inyección SQL.** `[todos]` Escribe a propósito un `findByName` concatenando y una prueba que lo explote con `' OR '1'='1`; luego arréglalo.

## 🟡 Medios

**08 🟡 Paginación.** `[todos]` `page(n, size)` con `LIMIT/OFFSET` y luego con *keyset* (`WHERE id > ? ORDER BY id LIMIT ?`). ¿Por qué el segundo escala mejor?

**09 🟡 Transacción con rollback.** `[todos]` Transferir saldo entre cuentas con `inTransaction`; una prueba que falle a la mitad y
compruebe que no quedó nada a medias.

**10 🟡 Lote grande.** `[todos]` Insertar 50 000 filas fila a fila contra `addBatch` cada 1000, midiendo tiempos. En MySQL prueba
`rewriteBatchedStatements=true` en la URL y en PG `reWriteBatchedInserts=true`.

**11 🟡 Fechas por JDBC.** `[PG]` `[MySQL]` Guardar y leer `LocalDate`, `LocalDateTime` e `Instant` con `setObject`/`getObject(col, Clase::class.java)`.

**12 🟡 Upsert devolviendo si insertó o actualizó.** `[PG]` Con `RETURNING (xmax = 0) AS inserted`. `[MySQL]` Con el valor de `executeUpdate` (1 o 2).

**13 🟡 Claves foráneas.** `[todos]` `orders → customer` con `ON DELETE CASCADE`. En SQLite, comprueba que sin `PRAGMA foreign_keys = ON` no se aplica.

**14 🟡 Pool de conexiones.** `[todos]` Agrega HikariCP en un experimento y mide abrir 200 conexiones con `DriverManager` frente al pool.

**15 🟡 Metadatos.** `[todos]` Listar tablas y columnas con `DatabaseMetaData.getTables`/`getColumns` y generar una `data class` en texto.

## 🟠 Difíciles

**16 🟠 Niveles de aislamiento.** `[PG]` `[MySQL]` Con dos conexiones, reproducir una *non-repeatable read* en `READ COMMITTED` y
mostrar que desaparece en `REPEATABLE READ`. ¿Por qué el resultado por defecto difiere entre PG y MySQL?

**17 🟠 Actualización perdida.** `[todos]` Dos hilos leen el stock, restan y escriben: muestra la pérdida, y arréglala de tres formas:
`UPDATE … SET stock = stock - ?`, `SELECT … FOR UPDATE` (no en SQLite) y versión optimista (`WHERE version = ?`).

**18 🟠 Deadlock.** `[PG]` `[MySQL]` Provocar un deadlock con dos transacciones que bloquean filas en orden inverso, capturar el error
(SQLState `40P01` / `40001`) y reintentar.

**19 🟠 `jsonb`.** `[PG]` Guardar `OrderEvent` como `jsonb`, consultar por `data->'address'->>'state'` con un índice GIN, y comparar el plan con `EXPLAIN`.

**20 🟠 Migraciones a mano.** `[todos]` Una tabla `schema_version` y una lista de scripts numerados que se aplican una sola vez, en transacción.

**21 🟠 Mapeo de errores.** `[todos]` Traducir `SQLException` por SQLState (`23505`, `23000`, `SQLITE_CONSTRAINT`…) a excepciones de dominio
(`DuplicateEmail`, `ForeignKeyViolation`) iguales para los tres motores.

## 🔴 Muy difíciles

**22 🔴 Mini-ORM.** `[todos]` `insert(entity)` y `findById<T>(id)` genéricos por reflexión de Java sobre `data class` con una anotación `@Table`.

**23 🔴 Outbox transaccional.** `[PG]` Escribir el pedido y un mensaje en `outbox` en la misma transacción, y un *poller* que publica con
`SELECT … FOR UPDATE SKIP LOCKED` en dos hilos sin duplicar.

**24 🔴 Ventas acumuladas en SQL.** `[PG]` Cargar los pedidos a una tabla y calcular ventas por estado y día y la suma de los
7 días anteriores con funciones de ventana (`SUM(...) OVER (PARTITION BY … ORDER BY day RANGE BETWEEN INTERVAL '7 days' PRECEDING AND INTERVAL '1 day' PRECEDING)`).
Compara con el resultado en Kotlin del ejercicio 21 de `collections`.

**25 🔴 Mismo ejercicio, tres motores.** `[todos]` Repite el 24 en MySQL 8 (funciones de ventana) y en SQLite (también las tiene desde 3.25)
y documenta las diferencias de sintaxis de intervalos.
