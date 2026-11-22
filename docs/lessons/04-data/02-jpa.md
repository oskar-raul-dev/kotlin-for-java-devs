# 02 · `jpa` — Hibernate 7 sin Spring

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jpa` · **Caso:** pedidos (provisional) → Banco
**Enlaces:** [temario](../../Temario.md#04--jpa--hibernate-7-sin-spring) · [ejercicios](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/Exercises.md) · [código](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/) · [pruebas](../../../04-data/src/test/kotlin/dev/raul/oskar/learning/kotlin/jpa/JpaTest.kt)

## Cómo correrla

```bash
./gradlew -p 04-data run -Plesson=jpa                    # H2 en memoria
./gradlew -p 04-data run -Plesson=jpa --args=postgres    # PostgreSQL de Docker (drop-and-create)
./gradlew -p 04-data test --tests '*.jpa.*'              # H2, no necesita Docker
```

## Recorrido sugerido

1. `Entities.kt`: las cinco reglas de JPA en Kotlin (comentario inicial: nada de `data class`, no-arg, all-open, id
   nullable, `equals` por id) y el modelo `Customer` → `PurchaseOrder` → `OrderLine` con `cascade`,
   `orphanRemoval` y `@Version`.
2. `Jpa.kt`: `EntityManagerFactory` a mano desde `META-INF/persistence.xml` (`h2Factory`, `postgresFactory`), la
   extensión `inTransaction` y las estadísticas para contar sentencias (`statementCount`, `resetStatistics`).
3. `OrderRepository.kt`: JPQL, el N+1 (`allOrdersNaive`) frente a `join fetch` (`allOrdersWithCustomer`),
   proyecciones, paginación y UPDATE masivo.
4. `Seed.kt` y `Main.kt`: datos de ejemplo y el recorrido.
5. `JpaTest.kt`: cada test crea su H2 en memoria con `h2Factory(nombre)`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Entities.kt` | Entidades en Kotlin | `Customer`, `PurchaseOrder`, `OrderLine`, `OrderStatus` | `JpaTest` (`cascade persists orders and lines`, `orphan removal deletes lines`, `entities use id based equality`, `optimistic locking…`) |
| `Jpa.kt` | Hibernate sin Spring | `h2Factory`, `postgresFactory`, `inTransaction`, `statementCount`, `resetStatistics` | `JpaTest` (`join fetch avoids N+1`, `dirty checking writes on commit`, `lazy collection outside transaction throws`) |
| `OrderRepository.kt` | Consultas JPQL | `OrderRepository` (`findCustomerByEmail`, `allOrdersNaive`, `allOrdersWithCustomer`, `ordersByStatus`, `totalsByCustomer`, `customersPage`, `cancelAllNew`) | `JpaTest` (`aggregate projection`, `pagination`, `bulk update bypasses…`, `unique constraint rolls back`) |
| `Seed.kt`, `Main.kt` | Datos y recorrido | `seed` | `JpaTest`: todas siembran en el `setUp` |

## Estado

- **Hecho:** código y 11 pruebas; 24 ejercicios; 0 resueltos en `practice/`.
- **Falta:** B0′ tanda 5 (`Customer`/`PurchaseOrder`/`OrderLine` → `Account` con `@Version`, `JournalEntry` con
  `Posting`); B2.
