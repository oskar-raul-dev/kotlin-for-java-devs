# 01 · `jdbc` — JDBC idiomático, tres motores (SQLite, PostgreSQL, MySQL)

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jdbc` (+ `.sqlite`, `.postgres`, `.mysql`) · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#04--jdbc--jdbc-idiomático-tres-motores-sqlite-postgresql-mysql) · [ejercicios](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/Exercises.md) · [código](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/) · [pruebas](../../../04-data/src/test/kotlin/dev/raul/oskar/learning/kotlin/jdbc/)

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

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 5) y 39 pruebas (13 × 3 motores); 25 ejercicios compartidos por los
  tres motores, etiquetados `[todos]`/`[SQLite]`/`[PG]`/`[MySQL]`; 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 6 (enunciados que todavía crean tablas de clientes y pedidos); B2 (⚡ ledger concurrente con
  `FOR UPDATE`).
