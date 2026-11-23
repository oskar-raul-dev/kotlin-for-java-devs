# 01 · `jdbc` — JDBC idiomático, tres motores (SQLite, PostgreSQL, MySQL)

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jdbc` (+ `.sqlite`, `.postgres`, `.mysql`) · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#04--jdbc--jdbc-idiomático-tres-motores-sqlite-postgresql-mysql) · [ejercicios](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/Exercises.md) · [código](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/) · [pruebas](../../../04-data/src/test/kotlin/dev/raul/oskar/learning/kotlin/jdbc/)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Escribir JDBC sin ceremonia con extensiones de Kotlin, entender qué garantiza (y qué no) una transacción en cada motor,
aislar las diferencias entre SQLite, PostgreSQL y MySQL en un dialecto y probarlo todo con una sola batería de contrato.

## Teoría

### 1. JDBC sigue siendo JDBC; Kotlin le quita la ceremonia

No hay un JDBC de Kotlin: son las mismas interfaces de `java.sql` (`Connection`, `PreparedStatement`, `ResultSet`) y
los mismos drivers. Lo que cambia es el código alrededor. El try-with-resources se vuelve `use { }`; el clásico
`while (rs.next()) list.add(map(rs))` se encapsula una vez en una extensión `inline` (`mapRows`); y el
`setAutoCommit(false)` / `commit` / `rollback` / `finally` se escribe una sola vez como función de orden superior con
receptor (`inTransaction`), de modo que dentro del bloque `this` es la conexión. Las reglas de siempre siguen valiendo:
`PreparedStatement` con `?` (nunca concatenar: inyección SQL, que el contrato prueba con `x' OR '1'='1`), claves
generadas con `RETURN_GENERATED_KEYS`, lotes con `addBatch`/`executeBatch`. → [JDBC: transacciones][jdbc-transactions]

### 2. Una transacción es "todo o nada", no "nadie más ve nada"

`transfer` inserta el asiento, sus dos postings y actualiza dos saldos dentro de `inTransaction`: si algo falla (por
ejemplo, el `CHECK (balance >= 0)` de la tabla), `rollback` deshace todo, también el asiento ya insertado (lo prueba
`check constraint failure rolls back the whole transfer`). Eso es la **atomicidad**. Lo que una transacción **no**
garantiza por sí sola es qué ven otras transacciones concurrentes: eso es el **aislamiento**, y cada motor lo
configura distinto.

### 3. Aislamiento: el mismo código, tres comportamientos

El estándar SQL define cuatro niveles; cada motor elige uno por defecto, y la lección lo prueba
(`the default isolation level depends on the engine`): **PostgreSQL** usa `READ COMMITTED` (cada sentencia ve lo
último confirmado), **MySQL/InnoDB** usa `REPEATABLE READ` (la transacción ve una foto tomada al empezar) y **SQLite**
es serializable (un solo escritor a la vez). Ningún motor implementa los niveles igual: el `REPEATABLE READ` de MySQL y
el de PostgreSQL permiten anomalías distintas. → [PostgreSQL][pg-iso], [MySQL/InnoDB][mysql-iso], [SQLite][sqlite-iso],
[Hermitage][hermitage]

### 4. La actualización perdida y cómo se evita

El error clásico de un saldo: dos transacciones leen 1 000, cada una resta en la aplicación y escribe el resultado
absoluto; la segunda pisa a la primera y se pierde una resta. La lección lo reproduce con dos conexiones
(`read-modify-write loses an update…`): **PostgreSQL y MySQL lo dejan pasar** en su nivel por defecto (queda 950 en vez
de 920) y **SQLite lo rechaza** (una de las dos escrituras falla). Hay tres arreglos:

1. **UPDATE relativo:** `SET balance = balance - ?`. La base resta sobre el valor actual con la fila bloqueada; es lo
   que hace `transfer` (`relative updates keep both withdrawals`).
2. **Bloqueo pesimista:** `SELECT … FOR UPDATE` al leer (no existe en SQLite), cuando hay que leer y decidir antes de
   escribir.
3. **Bloqueo optimista:** una columna `version` y `UPDATE … WHERE version = ?`; si no actualiza ninguna fila, alguien se
   adelantó y se reintenta (es lo que hace JPA con `@Version`, `02 · jpa`).

### 5. Lo que cambia por motor va en el dialecto

DDL (autoincremento `IDENTITY` frente a `AUTO_INCREMENT` frente a `INTEGER PRIMARY KEY`, `NUMERIC` frente a `TEXT` en
SQLite), el upsert (`ON CONFLICT` frente a `ON DUPLICATE KEY UPDATE`), el aislamiento por defecto y la preparación de la
sesión (SQLite trae las foreign keys **apagadas** en cada conexión) viven en cada `*Dialect`; el repositorio es uno solo.
Dos trampas de motor: MySQL **ignora** un `REFERENCES` escrito en la columna (la foreign key va a nivel de tabla) y su
upsert consume valores de `AUTO_INCREMENT` aunque actualice.

### 6. El contrato: una batería, tres motores

`LedgerRepositoryContract` es una clase de prueba abstracta con las 13 pruebas; cada motor la hereda y solo dice cómo
conectarse. Si el contenedor no está arriba, `openConnection()` devuelve `null` y la prueba sale `SKIPPED` en vez de
fallar. Así, una diferencia de comportamiento entre motores aparece como una prueba que falla en uno solo, y se resuelve
en el dialecto, no con un `if` en el repositorio.

## Cómo correrla

```bash
./gradlew -p 04-data run -Plesson=jdbc.sqlite                 # sin Docker (por defecto en 04-data)
docker compose -f docker/postgres/compose.yaml up -d --wait   # y/o docker/mysql/compose.yaml
./gradlew -p 04-data run -Plesson=jdbc.postgres               # o jdbc.mysql
./gradlew -p 04-data test --tests '*.jdbc.*'                  # sin contenedores, PG y MySQL salen SKIPPED
```

`jdbc` no tiene `Main`: cada motor tiene el suyo y todos llaman al mismo `runDemo`.

## Recorrido sugerido

1. `Jdbc.kt`: las extensiones que quitan la ceremonia (`mapRows`, `inTransaction`) y `DbConfig` con `fromEnv` e
   `isReachable`.
2. `Ledger.kt` y `Dialect.kt`: las filas del ledger (`AccountRow`, `MovementRow`) y lo único que cambia por motor
   (DDL, upsert, aislamiento por defecto, si el motor rechaza la actualización perdida, `sessionSetup`).
3. `LedgerRepository.kt`: cuentas con saldo materializado y `CHECK (balance >= 0)`; la transferencia como un asiento
   de doble partida (asiento con clave generada, dos postings en lote y dos UPDATE **relativos**) dentro de
   `inTransaction`; la clave de idempotencia `UNIQUE`; el invariante débitos = créditos en SQL
   (`unbalancedEntries`); `COALESCE`, `LIKE` con parámetro, foreign keys y el upsert de tasas.
4. `sqlite/`, `postgres/`, `mysql/`: cada `*Dialect` y su `Main` (`RETURNING`, `= ANY(array)` y `jsonb` en PG;
   `LAST_INSERT_ID()`, `@@transaction_isolation` y `GROUP_CONCAT` en MySQL).
5. `Demo.kt`: el recorrido común.
6. Pruebas: `LedgerRepositoryContract` (abstracta, la batería) y una subclase por motor que da el dialecto y la
   conexión (`null` → `assumeTrue` → `SKIPPED`). La de SQLite usa un archivo temporal porque el contrato abre una
   segunda conexión a la misma base.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Jdbc.kt` | Extensiones de JDBC | `mapRows`, `inTransaction`, `DbConfig` | `LedgerRepositoryContract` (`a transfer writes the entry…`, `check constraint failure rolls back…`) |
| `Ledger.kt`, `Dialect.kt`, `*/…Dialect.kt` | Filas y diferencias entre motores | `AccountRow`, `MovementRow`, `money`, `Dialect`, `SqliteDialect`, `PostgresDialect`, `MysqlDialect`, `postgresConfig`, `mysqlConfig` | `LedgerRepositoryContract` (`the default isolation level depends on the engine`, `read-modify-write loses an update…`) |
| `LedgerRepository.kt` | El ledger con JDBC | `LedgerRepository` (`openAccount`, `openAccounts`, `findAccount`, `accounts`, `totalBalance`, `transfer`, `movements`, `entriesMatching`, `unbalancedEntries`, `freeze`, `deleteAccount`, `upsertRate`, `rate`, `overwriteBalance`) | `LedgerRepositoryContract` (las 13 pruebas, en cada motor) |
| `Demo.kt`, `*/Main.kt` | Recorrido común | `runDemo` | Sin prueba propia: es la demo de `run` |

## Para profundizar

**Documentación oficial**

- [JDBC: Using Transactions][jdbc-transactions] (tutorial de Oracle): `autoCommit`, `commit`, `rollback`, *savepoints*.
- [`java.sql`][java-sql] (Java SE 21): las interfaces que usa la lección.
- [PostgreSQL: Transaction Isolation][pg-iso], [MySQL 8.4: InnoDB isolation levels][mysql-iso] y
  [SQLite: Isolation][sqlite-iso]: qué garantiza cada motor en cada nivel.
- [PostgreSQL: Explicit Locking][pg-locking]: `SELECT … FOR UPDATE` y los modos de bloqueo.

**Lecturas**

- [Hermitage][hermitage] (Martin Kleppmann): pruebas reproducibles de qué anomalías permite cada nivel en cada motor; la
  mejor tabla comparativa que existe.
- *Designing Data-Intensive Applications* (M. Kleppmann, O'Reilly), capítulo 7 "Transactions": aislamiento,
  actualización perdida y *write skew* explicados con claridad.
- [A beginner's guide to database locking and the lost update phenomena][vlad-lost-update] (Vlad Mihalcea): la
  actualización perdida y sus arreglos, con JDBC y JPA.

**Videos**

- [Transactions and Concurrency Control Patterns][vlad-transactions] (Vlad Mihalcea): aislamiento, anomalías y bloqueos
  optimista y pesimista.

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 5) y 39 pruebas (13 × 3 motores); 40 ejercicios más el ⚡, compartidos por los
  tres motores y etiquetados `[todos]`/`[SQLite]`/`[PG]`/`[MySQL]` (6 🎤; B2, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B2.

[jdbc-transactions]: https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
[java-sql]: https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html
[pg-iso]: https://www.postgresql.org/docs/17/transaction-iso.html
[mysql-iso]: https://dev.mysql.com/doc/refman/8.4/en/innodb-transaction-isolation-levels.html
[sqlite-iso]: https://www.sqlite.org/isolation.html
[pg-locking]: https://www.postgresql.org/docs/17/explicit-locking.html
[hermitage]: https://github.com/ept/hermitage
[vlad-lost-update]: https://vladmihalcea.com/a-beginners-guide-to-database-locking-and-the-lost-update-phenomena/
[vlad-transactions]: https://www.youtube.com/watch?v=onYjxRcToto
