# 02 · `jpa` — Hibernate 7 sin Spring

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jpa` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#04--jpa--hibernate-7-sin-spring) · [ejercicios](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/Exercises.md) · [código](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/) · [pruebas](../../../04-data/src/test/kotlin/dev/raul/oskar/learning/kotlin/jpa/JpaTest.kt)

## Cómo correrla

```bash
./gradlew -p 04-data run -Plesson=jpa                    # H2 en memoria
./gradlew -p 04-data run -Plesson=jpa --args=postgres    # PostgreSQL de Docker (drop-and-create)
./gradlew -p 04-data test --tests '*.jpa.*'              # H2, no necesita Docker
```

## Recorrido sugerido

1. `Entities.kt`: las cinco reglas de JPA en Kotlin (comentario inicial: nada de `data class`, no-arg, all-open, id
   nullable, `equals` por id) y el ledger: `Account` con `@Version` y `AccountStatus`, `JournalEntry` con sus
   `Posting` en cascada y `orphanRemoval`, `post` que sincroniza los dos lados de las relaciones, e `isBalanced`.
   `transferEntry` arma el asiento de una transferencia y actualiza los saldos de cuentas gestionadas.
2. `Jpa.kt`: `EntityManagerFactory` a mano desde `META-INF/persistence.xml` (`h2Factory`, `postgresFactory`), la
   extensión `inTransaction` y las estadísticas para contar sentencias (`statementCount`, `resetStatistics`).
3. `LedgerQueries.kt`: JPQL, el N+1 (`entriesNaive`) frente a `join fetch` (`entriesWithPostings`), una proyección
   con agregados (`netMovementByAccount`), paginación y un UPDATE masivo (`freezeEmptyAccounts`).
4. `Seed.kt` y `Main.kt`: tres cuentas y dos transferencias sembradas en código, y el recorrido.
5. `JpaTest.kt`: cada prueba crea su H2 en memoria con `h2Factory(nombre)`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Entities.kt` | Entidades del ledger en Kotlin | `Account`, `AccountStatus`, `JournalEntry`, `Posting`, `Direction`, `transferEntry` | `JpaTest` (`cascade persists entries and postings`, `orphan removal deletes postings`, `entities use id based equality`, `optimistic locking…`) |
| `Jpa.kt` | Hibernate sin Spring | `h2Factory`, `postgresFactory`, `inTransaction`, `statementCount`, `resetStatistics` | `JpaTest` (`join fetch avoids N+1`, `dirty checking writes on commit`, `lazy collection outside transaction throws`) |
| `LedgerQueries.kt` | Consultas JPQL | `LedgerQueries` (`findAccount`, `entriesNaive`, `entriesWithPostings`, `accountsByStatus`, `netMovementByAccount`, `accountsPage`, `freezeEmptyAccounts`) | `JpaTest` (`aggregate projection`, `pagination`, `bulk update bypasses…`, `unique constraint rolls back`) |
| `Seed.kt`, `Main.kt` | Datos y recorrido | `seed`, `ANA`, `BEA`, `CYD` | `JpaTest`: todas siembran en el `setUp` |

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 5) y 11 pruebas; 24 ejercicios; 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ ledger en JPA con reintento ante `OptimisticLockException`).
