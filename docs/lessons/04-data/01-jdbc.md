# 01 · `jdbc` — JDBC idiomático, tres motores (SQLite, PostgreSQL, MySQL)

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jdbc` (+ `.sqlite`, `.postgres`, `.mysql`) · **Caso:** tienda (provisional) → Banco
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
2. `Dialect.kt`: lo único que cambia por motor (DDL, upsert).
3. `Product.kt` y `ProductRepository.kt`: CRUD con `PreparedStatement`, claves generadas, batch, upsert, agregados y
   una transacción que se revierte por un `CHECK` (`moveStock`).
4. `sqlite/`, `postgres/`, `mysql/`: cada `*Dialect` y su `Main`.
5. `Demo.kt`: el recorrido común.
6. Pruebas: `ProductRepositoryContract` (abstracta, la batería) y una subclase por motor que solo da el dialecto y la
   conexión (`null` → `assumeTrue` → `SKIPPED`).

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Jdbc.kt` | Extensiones de JDBC | `mapRows`, `inTransaction`, `DbConfig` | `ProductRepositoryContract` (`transaction commits both updates`, `check constraint failure rolls back…`) |
| `Dialect.kt`, `*/…Dialect.kt` | Diferencias entre motores | `Dialect`, `SqliteDialect`, `PostgresDialect`, `MysqlDialect`, `postgresConfig`, `mysqlConfig` | `SqliteRepositoryTest`, `PostgresRepositoryTest`, `MysqlRepositoryTest` |
| `Product.kt`, `ProductRepository.kt` | Repositorio JDBC común | `Product`, `ProductRepository` (`insert`, `insertAll`, `findBySku`, `searchByName`, `updatePrice`, `delete`, `upsert`, `count`, `inventoryValue`, `moveStock`) | `ProductRepositoryContract` (las 9 pruebas, en cada motor) |
| `Demo.kt`, `*/Main.kt` | Recorrido común | `runDemo` | Sin prueba propia: es la demo de `run` |

## Estado

- **Hecho:** código y 27 pruebas (9 × 3 motores); 25 ejercicios compartidos por los tres motores, etiquetados
  `[todos]`/`[SQLite]`/`[PG]`/`[MySQL]`; 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 5 (`ProductRepository` → `LedgerRepository` con su contrato: cuentas, asientos,
  transferencia, `FOR UPDATE` y aislamiento por motor); B2.
