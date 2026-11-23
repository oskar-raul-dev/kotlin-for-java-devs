# 02 · `jpa` — Hibernate 7 sin Spring

**Sección:** `04-data` · **Paquete:** `dev.raul.oskar.learning.kotlin.jpa` · **Caso:** Banco
**Enlaces:** [temario](../../Temario.md#04--jpa--hibernate-7-sin-spring) · [ejercicios](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/Exercises.md) · [código](../../../04-data/src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/) · [pruebas](../../../04-data/src/test/kotlin/dev/raul/oskar/learning/kotlin/jpa/JpaTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Usar JPA desde Kotlin sin caer en sus trampas (clases `final`, `data class`, ids nulos), entender el contexto de
persistencia (qué se guarda solo, qué se carga tarde y por qué) y medir el N+1.

## Teoría

### 1. JPA espera Java: tres choques con Kotlin y sus plugins

JPA (y Hibernate debajo) se diseñó para clases Java con constructor vacío, no `final` y con campos mutables. Kotlin choca
en tres puntos, y cada uno tiene su solución en el build (`04-data/build.gradle.kts`):

- **Constructor sin argumentos:** Hibernate instancia las entidades por reflexión. El plugin `kotlin("plugin.jpa")`
  genera uno (invisible desde Kotlin) para las clases `@Entity`, `@Embeddable` y `@MappedSuperclass`.
- **Clases `final`:** Hibernate crea subclases en runtime (proxies) para la carga perezosa; si la clase es `final`, el
  `LAZY` no funciona. El plugin `allopen`, configurado para esas anotaciones, las abre. El plugin `jpa` **no** hace esto:
  hay que configurar `allopen` aparte.
- **`val` y nulabilidad:** las propiedades que Hibernate escribe son `var`, el id es `Long? = null` (nulo hasta que se
  persiste) y un `String` no nullable no impide que Hibernate cargue un `null` si la columna lo permite: la columna
  tiene que ser `NOT NULL` de verdad. → [Plugin kotlin-jpa][kotlin-jpa], [pitfalls de JPA con Kotlin][jetbrains-pitfalls]

### 2. Nunca `data class` para una entidad

Una `data class` calcula `equals` y `hashCode` con **todas** las propiedades del constructor: incluye el id, que cambia
de `null` a un número al persistir (la entidad "cambia de identidad" dentro de un `HashSet`), y puede incluir
colecciones perezosas (comparar dos entidades dispara consultas o una `LazyInitializationException`). Su `toString`
también recorre las colecciones. La lección usa clases normales con `equals` por id y `hashCode` constante por clase
(`Account`, `JournalEntry`, `Posting`), el patrón recomendado para Hibernate, y lo prueba en
`entities use id based equality`. → [Hibernate: equals y hashCode][hibernate-guide]

### 3. El contexto de persistencia

Un `EntityManager` mantiene un **contexto de persistencia**: cada entidad cargada queda registrada una sola vez, y al hacer
*flush* (al confirmar la transacción) Hibernate compara cada una con su foto inicial y escribe los `UPDATE` necesarios.
De ahí salen tres comportamientos que la lección prueba:

- **Dirty checking:** cambiar `status` dentro de la transacción basta; no hay `save` (`dirty checking writes on
  commit`).
- **Carga perezosa:** una colección `LAZY` se carga al primer acceso, mientras la sesión está abierta; fuera, lanza
  `LazyInitializationException` (`lazy collection outside transaction throws`).
- **UPDATE masivo:** un `UPDATE` en JPQL va directo a la base y **no** actualiza las entidades ya cargadas; hace falta
  `refresh` o limpiar el contexto (`bulk update bypasses the persistence context`).

### 4. Relaciones: dueño, cascada y huérfanos

En una relación bidireccional, el lado con la foreign key (`Posting.entry`, `@ManyToOne`) es el **dueño**; el otro
(`JournalEntry.postings`, `@OneToMany(mappedBy = "entry")`) es solo un espejo: si se agrega una línea solo a la lista,
Hibernate la ignora. Por eso `post(…)` actualiza los dos lados. `cascade = [CascadeType.ALL]` hace que persistir el
asiento persista sus líneas, y `orphanRemoval = true` borra una línea que se saca de la lista. `@ManyToOne` es `EAGER`
por defecto en JPA; casi siempre conviene `LAZY`.

### 5. El N+1

`select e from JournalEntry e` trae los asientos en una consulta; recorrer `e.postings` de cada uno dispara **una
consulta más por asiento**: N+1. Con pocos datos no se nota; con miles es la causa más común de lentitud en JPA. La
lección lo mide contando sentencias con las estadísticas de Hibernate (`statementCount`): 3 sentencias sin fetch frente
a 1 con `join fetch` (`join fetch avoids N+1`). Las otras salidas son `@BatchSize`/`SUBSELECT` (mitigan) y las
proyecciones a DTO (`netMovementByAccount`). → [Hibernate: fetching][hibernate-fetching], [N+1][vlad-n1]

### 6. Bloqueo optimista con `@Version`

`@Version var version: Long?` hace que cada `UPDATE` lleve `WHERE version = ?` y la incremente. Si otra transacción
modificó la cuenta entre la lectura y la escritura, no se actualiza ninguna fila y JPA lanza
`OptimisticLockException` (`optimistic locking detects concurrent update`): es la versión JPA del arreglo 3 de la
actualización perdida de `01 · jdbc`. Lo que sigue (reintentar o avisar al usuario) es decisión de la aplicación.

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

## Para profundizar

**Documentación oficial**

- [Plugin kotlin-jpa][kotlin-jpa] (kotlinlang.org): qué genera el plugin y qué no.
- [Hibernate ORM 7.4: guía de usuario][hibernate-guide]: entidades, contexto de persistencia, `equals`/`hashCode`, bloqueos.
- [Hibernate ORM 7.4: fetching][hibernate-fetching]: estrategias de carga, `join fetch`, `@BatchSize`, N+1.
- [A Short Guide to Hibernate 7.4][hibernate-short]: la introducción oficial corta, con recomendaciones.

**Lecturas**

- [How to avoid common pitfalls with JPA and Kotlin][jetbrains-pitfalls] (blog de JetBrains): `data class`, plugins,
  nulabilidad y API.
- [N+1 query problem with JPA and Hibernate][vlad-n1] (Vlad Mihalcea): cómo detectarlo y las formas de evitarlo.
- [JPA Buddy: entidades JPA en Kotlin][jpa-buddy] (GitHub): ejemplos de entidades bien escritas y las trampas de cada
  forma.

**Videos**

- [High-Performance Hibernate][vlad-hibernate] (Vlad Mihalcea, JDK IO 2018): el N+1, lotes, caché y transacciones.
- [Transactions and Concurrency Control Patterns][vlad-transactions] (Vlad Mihalcea): bloqueo optimista y pesimista.

## Estado

- **Hecho:** código en el caso Banco (B0′ tanda 5) y 11 pruebas; 40 ejercicios más el ⚡ (6 🎤; B2, tanda 4); 0 resueltos en `practice/`.
- **Falta:** nada de B2.

[kotlin-jpa]: https://kotlinlang.org/docs/kotlin-jpa-plugin.html
[hibernate-guide]: https://docs.hibernate.org/orm/7.4/userguide/html_single/
[hibernate-fetching]: https://docs.hibernate.org/orm/7.4/userguide/html_single/#fetching
[hibernate-short]: https://docs.hibernate.org/orm/7.4/introduction/html_single/
[jetbrains-pitfalls]: https://blog.jetbrains.com/idea/2026/01/how-to-avoid-common-pitfalls-with-jpa-and-kotlin
[vlad-n1]: https://vladmihalcea.com/n-plus-1-query-problem/
[jpa-buddy]: https://github.com/jpa-buddy/kotlin-entities
[vlad-hibernate]: https://www.youtube.com/watch?v=bQWUyp_9SDc
[vlad-transactions]: https://www.youtube.com/watch?v=onYjxRcToto
