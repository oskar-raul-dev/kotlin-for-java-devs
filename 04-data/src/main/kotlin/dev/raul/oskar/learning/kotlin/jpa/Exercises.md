# Ejercicios: JPA con Hibernate

> **Lección:** `jpa` · **Dónde se resuelven:** `jpa/practice/ExNN<Nombre>.kt` y su prueba en `src/test/kotlin/.../jpa/practice/`.
> Las entidades nuevas se agregan a `META-INF/persistence.xml` (o usa otra unidad de persistencia para la práctica).
> Las pruebas usan `h2Factory("nombre-único")` como `JpaTest`. **Correr:** `./gradlew -p 04-data test --tests '*.jpa.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** ninguno; esta lección va después de las de lenguaje. **Entrevista (🎤):** 02, 09, 12, 16, 24, 29.

## 🟢 Fáciles

**01 🟢 Primera entidad.** `FxRate` (moneda y tasa) con id generado, `@Column(nullable = false, unique = true)` y `BigDecimal` con `precision`/`scale`.
Persistir, buscar con `find` y comprobar el SQL generado con `hibernate.show_sql=true`.

**02 🟢 🎤 ¿Por qué no `data class`?** Escribe una entidad como `data class` y demuestra con una prueba el problema de `hashCode` al
guardarla en un `HashSet` antes y después de persistir.

**03 🟢 Enum como texto.** Cambia a `EnumType.ORDINAL`, reordena el enum y muestra el dato corrupto que lee una prueba. Vuelve a `STRING`.

**04 🟢 Consultas JPQL.** Asientos por descripción con `like`, ordenados, con parámetros con nombre.

**05 🟢 `find` contra `getReference`.** ¿Cuándo va cada una a la base? Cuenta sentencias con `statementCount()`.

**06 🟢 Ciclo de vida.** Un listener con `@PrePersist`/`@PreUpdate` que llena `createdAt`/`updatedAt` desde un `Clock`.

**07 🟢 `Embeddable`.** Un monto con moneda como `@Embeddable` (`amount` + `currency`) dentro de `Account`.

**25 🟢 `@Version` en una cuenta.** Cada cambio sube la versión; una copia vieja ya no se puede guardar.
- Firma: `fun renameAlias(emf: EntityManagerFactory, number: String, alias: String)` (o cambia el campo que tenga tu
  `Account`)
- Casos: la versión sube en 1 con cada cambio y no sube si no hay cambios; hacer `merge` de una copia separada con
  una versión vieja → `OptimisticLockException` (envuelta: la prueba busca la causa).

**26 🟢 UPDATE masivo y contexto.** `freezeEmptyAccounts` de la lección cambia la base, pero no las entidades que ya
están cargadas.
- Firma: `fun freezeAndRead(emf: EntityManagerFactory, number: String): Pair<AccountStatus, AccountStatus>` (el estado
  de la entidad cargada antes, sin y con `em.refresh` o `em.clear()`)
- Casos: la entidad cargada antes sigue en `OPEN` después del UPDATE masivo; después de `refresh`, `FROZEN`; una
  consulta nueva en el mismo contexto devuelve la misma instancia vieja (explica por qué en un comentario).

**27 🟢 El `null` que Kotlin no ve.** Una propiedad no nullable en la entidad sobre una columna que sí admite `NULL`.
- Casos: insertar una fila con `NULL` por SQL nativo y cargarla: la propiedad Kotlin queda en `null` (Hibernate la
  llena por reflexión) y el error salta lejos, al usarla; con la columna `NOT NULL` el insert nativo falla en la base.
  Explica en un comentario por qué la nulabilidad de Kotlin no protege a una entidad.

**28 🟢 `toString` seguro.** Un `toString` de entidad que no toca colecciones lazy.
- Casos: llamar a `toString` de una cuenta y de un asiento fuera de la transacción no lanza ni carga nada (cuenta las
  sentencias); el `toString` ingenuo que imprime los postings lanza `LazyInitializationException` fuera de la
  transacción y dispara una consulta dentro.

**29 🟢 🎤 `LazyInitializationException`.** Recorrer los postings de un asiento después de cerrar el `EntityManager`
falla; arreglarlo de tres formas.
- Firmas: `fun postingsNaive(emf: EntityManagerFactory, entryId: Long): JournalEntry` y tres arreglos: `join fetch`,
  un entity graph y una proyección a DTO
- Casos: la ingenua lanza al recorrer fuera; los tres arreglos funcionan y la prueba cuenta sus sentencias. Explica en
  un comentario por qué `open-in-view` (en Spring) esconde el problema en vez de resolverlo.

## 🟡 Medios

**08 🟡 Relación bidireccional.** `Holder` (titular) 1-N `Account`: métodos `addAccount`/`removeAccount` que mantengan los
dos lados; prueba qué pasa si solo actualizas uno.

**09 🟡 🎤 N+1 con colecciones.** Listar cuentas y sus postings: cuenta sentencias sin y con `join fetch`, y luego con `@BatchSize(size = 10)`.

**10 🟡 Proyección a DTO.** `select new …AccountSummary(a.number, count(p))` con una clase Kotlin; ¿qué tipos exactos exige el constructor?

**11 🟡 Paginación con `join fetch`.** Combinar paginación con fetch de colecciones: lee el aviso `HHH90003004` y arréglalo con dos consultas (ids, luego fetch).

**12 🟡 🎤 `merge` frente a `persist`.** Con entidades separadas (*detached*): qué devuelve `merge`, por qué el objeto original no queda gestionado.

**13 🟡 Borrado en cascada.** `CascadeType.REMOVE` frente a `orphanRemoval`: dos pruebas que muestren la diferencia.

**14 🟡 Criteria API.** Una búsqueda con filtros opcionales (nombre, estado, rango de fechas) armada con `CriteriaBuilder`.

**15 🟡 Consulta nativa.** Una SQL nativa con `createNativeQuery` y mapeo a entidad; luego a un resultado escalar.

**30 🟡 Entity graph.** Cargar asientos con sus postings y sus cuentas con un grafo dinámico
(`em.createEntityGraph`) pasado como `jakarta.persistence.fetchgraph`.
- Firma: `fun entriesWithGraph(em: EntityManager): List<JournalEntry>`
- Casos: una sola sentencia para todo (con `statementCount()`); recorrer los postings y sus cuentas fuera de la
  transacción no lanza; la diferencia entre `fetchgraph` y `loadgraph` en un atributo que es `EAGER` por defecto.

**31 🟡 Dirty checking.** Cambiar una entidad gestionada sin llamar a nada genera un UPDATE al confirmar.
- Firma: `fun touchAll(emf: EntityManagerFactory, readOnly: Boolean): Long` (sentencias de UPDATE emitidas)
- Casos: con `readOnly = false`, un UPDATE por cuenta cambiada y ninguno por las que no cambiaron; con la consulta
  marcada `org.hibernate.readOnly`, ninguno aunque se cambien. Explica en un comentario el costo del dirty checking
  en una transacción que carga 10 000 entidades.

**32 🟡 Consultas a `Tuple`.** El saldo por cuenta calculado desde los postings, leído como `jakarta.persistence.Tuple`
con alias.
- Firma: `fun balancesFromPostings(em: EntityManager): Map<String, BigDecimal>` (con `sum(case when … then … else … end)`)
- Casos: el mismo resultado que el saldo guardado en cada cuenta después de varias transferencias del `Seed`; una
  cuenta sin postings aparece con cero (necesita `left join`); leer un alias que no existe → excepción.

**33 🟡 Igualdad de entidades.** `equals`/`hashCode` de una entidad que siguen valiendo antes y después de persistir.
- Casos: la versión por clave de negocio (`number`) y la versión por id con `hashCode` constante por clase; en las
  dos, una cuenta nueva metida en un `HashSet` se sigue encontrando después de `persist`, de `merge` y en otro
  `EntityManager`; la versión por id ingenua (`id.hashCode()`) se pierde (pruébalo).

**34 🟡 Paginación por clave.** Páginas de asientos por `id > :ultimo order by id` en vez de `setFirstResult`.
- Firma: `fun nextPage(em: EntityManager, afterId: Long?, size: Int): List<JournalEntry>`
- Casos: recorrer todas las páginas da todos los asientos una vez; insertar un asiento entre página y página no hace
  que se repita ni que se salte ninguno (con `OFFSET` sí: la prueba lo muestra); la última página está incompleta.

**35 🟡 Asientos por mes.** Filtrar por un rango de `LocalDate` y agrupar por mes en JPQL con `extract`.
- Firma: `fun entriesPerMonth(em: EntityManager, year: Int): Map<Int, Long>`
- Casos: asientos en tres meses, un mes sin asientos (no aparece), los bordes del año; el parámetro va como
  `LocalDate`, no como texto.

## 🟠 Difíciles

**16 🟠 🎤 Bloqueo pesimista.** `LockModeType.PESSIMISTIC_WRITE` contra PostgreSQL (compose): dos hilos y un `lock.timeout`.

**17 🟠 Herencia.** `Payment` con `CardPayment` y `TransferPayment` en las tres estrategias (`SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`);
compara el SQL de una consulta polimórfica.

**18 🟠 Caché de segundo nivel.** Activa una caché (Caffeine/JCache) y muestra con estadísticas aciertos y fallos.

**19 🟠 Auditoría.** Historial de cambios de `Account` con un listener que escribe en otra tabla (o Hibernate Envers en un experimento).

**20 🟠 Tipo propio.** Un `AttributeConverter<Money, String>` y luego con dos columnas (`@Embeddable`); ¿cuál permite consultar por monto?

**36 🟠 Versión de la cuenta al postear.** Agregar un posting no cambia ninguna columna de la cuenta, así que su
`@Version` no sube: dos transferencias concurrentes desde la misma cuenta no chocan.
- Firma: `fun postWithForceIncrement(em: EntityManager, account: Account, amount: BigDecimal)` (con
  `LockModeType.OPTIMISTIC_FORCE_INCREMENT`)
- Casos: sin el bloqueo, dos transacciones que postean en la misma cuenta confirman las dos; con él, la segunda
  falla con `OptimisticLockException`; la versión sube aunque la cuenta no cambió.

**37 🟠 Borrado lógico.** `@SoftDelete` de Hibernate en una entidad de la práctica (por ejemplo, un titular).
- Casos: después de `remove`, `find` devuelve `null` y las consultas JPQL no la ven; una consulta nativa sí ve la fila
  con su marca; una relación hacia la entidad borrada; explica en un comentario qué pasa con las restricciones de
  unicidad.

**38 🟠 Filtros de Hibernate.** Un `@FilterDef`/`@Filter` por moneda que se activa por sesión.
- Firma: `fun accountsIn(emf: EntityManagerFactory, currency: String): List<Account>` (activa el filtro con
  `session.enableFilter`)
- Casos: con el filtro activo solo se ven las cuentas de esa moneda, también al navegar una colección filtrada;
  `find` por id ignora el filtro (pruébalo y explica por qué); sin activar, se ven todas.

**39 🟠 Outbox con JPA.** Al persistir un asiento, escribir un `OutboxMessage` en la misma transacción.
- Casos: confirmar deja los dos; un fallo después de persistir el asiento hace rollback de los dos; un *poller* que
  marca los mensajes como enviados en otra transacción no ve los que no se confirmaron. Compara con hacerlo en un
  `@PostPersist` (¿se puede usar el `EntityManager` ahí?).

**40 🟠 H2 no es PostgreSQL.** `[PG]` Correr las mismas consultas contra `h2Factory` y `postgresFactory` y documentar
las diferencias que aparecen.
- Casos: al menos cuatro diferencias medidas en pruebas (por ejemplo, el orden de los `NULL` al ordenar, `like` con
  mayúsculas, el tipo que devuelve una suma, la generación de ids); las de PostgreSQL salen `SKIPPED` si el contenedor
  está abajo. Explica en un comentario por qué Testcontainers (sección `05-testing`) reemplaza a H2 en pruebas serias.

## 🔴 Muy difíciles

**21 🔴 Problema del producto cartesiano.** Dos `join fetch` de colecciones (`MultipleBagFetchException`): reprodúcelo y resuélvelo de dos formas.

**22 🔴 Repositorio genérico.** `JpaRepository<T, ID>` propio con `findById`, `save`, `delete`, `findAll(spec)` usando `reified` y Criteria.

**23 🔴 Ticks y reportes diarios.** Modela ticks y velas diarias como entidades; una consulta JPQL que devuelva el volumen diario por ticker
y compárala con la de SQL del ejercicio 24 de JDBC. ¿Dónde pondrías la agregación en producción y por qué?

**24 🔴 🎤 Rendimiento de escritura.** Insertar 100 000 entidades: con `IDENTITY` frente a `SEQUENCE` + `hibernate.jdbc.batch_size`,
`flush`/`clear` periódicos y `StatelessSession`. Mide y explica por qué `IDENTITY` desactiva el batching.

## ⚡ Saiyajin

**41 ⚡ Ledger en JPA con concurrencia.** El ledger de la lección con `@Version` en las cuentas, transferencias que
reintentan ante `OptimisticLockException` y consultas sin N+1.
- Firma: `class JpaLedgerService(private val emf: EntityManagerFactory)` con
  `fun transfer(from: String, to: String, amount: BigDecimal, description: String, day: LocalDate, maxAttempts: Int = 5): Long`
  (id del asiento), `fun statement(number: String): List<StatementRow>` y `fun monthlyMovements(month: YearMonth): List<JournalEntry>`
- Reglas: cada intento en su propia transacción; un saldo que quedaría negativo es un error de dominio que no se
  reintenta; agotar los intentos lanza una excepción que dice cuántos hubo.
- **Se verifica con:** una prueba de dos transacciones que modifican la misma cuenta (con un `CountDownLatch` para que
  choquen: una falla y se reintenta); el número exacto de sentencias SQL de `statement` y de `monthlyMovements`
  (`statementCount()`, sin N+1); 1000 transferencias en 8 hilos con semilla fija, después de las cuales la suma de los
  saldos está intacta y todos los asientos están balanceados; en H2 y, si el contenedor está arriba, en PostgreSQL.
