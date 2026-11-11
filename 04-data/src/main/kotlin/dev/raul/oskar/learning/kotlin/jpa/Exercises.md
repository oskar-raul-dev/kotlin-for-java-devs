# Ejercicios: JPA con Hibernate

> **Lección:** `jpa` · **Dónde se resuelven:** `jpa/practice/ExNN<Nombre>.kt` y su prueba en `src/test/kotlin/.../jpa/practice/`.
> Las entidades nuevas se agregan a `META-INF/persistence.xml` (o usa otra unidad de persistencia para la práctica).
> Las pruebas usan `h2Factory("nombre-único")` como `JpaTest`. **Correr:** `./gradlew -p 04-data test --tests '*.jpa.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.

**Prioritarios (🎯):** ninguno; esta lección va después de las de lenguaje.

## 🟢 Fáciles

**01 🟢 Primera entidad.** `Product` con id generado, `@Column(nullable = false, unique = true)` y `BigDecimal` con `precision`/`scale`.
Persistir, buscar con `find` y comprobar el SQL generado con `hibernate.show_sql=true`.

**02 🟢 ¿Por qué no `data class`?** Escribe una entidad como `data class` y demuestra con una prueba el problema de `hashCode` al
guardarla en un `HashSet` antes y después de persistir.

**03 🟢 Enum como texto.** Cambia a `EnumType.ORDINAL`, reordena el enum y muestra el dato corrupto que lee una prueba. Vuelve a `STRING`.

**04 🟢 Consultas JPQL.** Clientes por nombre con `like`, ordenados, con parámetros con nombre.

**05 🟢 `find` contra `getReference`.** ¿Cuándo va cada una a la base? Cuenta sentencias con `statementCount()`.

**06 🟢 Ciclo de vida.** Un listener con `@PrePersist`/`@PreUpdate` que llena `createdAt`/`updatedAt` desde un `Clock`.

**07 🟢 `Embeddable`.** `Address` como `@Embeddable` dentro de `Customer`.

## 🟡 Medios

**08 🟡 Relación bidireccional.** `Author` 1-N `Book`: métodos `addBook`/`removeBook` que mantengan los dos lados; prueba qué pasa si
solo actualizas uno.

**09 🟡 N+1 con colecciones.** Listar clientes y sus pedidos: cuenta sentencias sin y con `join fetch`, y luego con `@BatchSize(size = 10)`.

**10 🟡 Proyección a DTO.** `select new …CustomerSummary(c.name, count(o))` con una clase Kotlin; ¿qué tipos exactos exige el constructor?

**11 🟡 Paginación con `join fetch`.** Combinar paginación con fetch de colecciones: lee el aviso `HHH90003004` y arréglalo con dos consultas (ids, luego fetch).

**12 🟡 `merge` frente a `persist`.** Con entidades separadas (*detached*): qué devuelve `merge`, por qué el objeto original no queda gestionado.

**13 🟡 Borrado en cascada.** `CascadeType.REMOVE` frente a `orphanRemoval`: dos pruebas que muestren la diferencia.

**14 🟡 Criteria API.** Una búsqueda con filtros opcionales (nombre, estado, rango de fechas) armada con `CriteriaBuilder`.

**15 🟡 Consulta nativa.** Una SQL nativa con `createNativeQuery` y mapeo a entidad; luego a un resultado escalar.

## 🟠 Difíciles

**16 🟠 Bloqueo pesimista.** `LockModeType.PESSIMISTIC_WRITE` contra PostgreSQL (compose): dos hilos y un `lock.timeout`.

**17 🟠 Herencia.** `Payment` con `CardPayment` y `TransferPayment` en las tres estrategias (`SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`);
compara el SQL de una consulta polimórfica.

**18 🟠 Caché de segundo nivel.** Activa una caché (Caffeine/JCache) y muestra con estadísticas aciertos y fallos.

**19 🟠 Auditoría.** Historial de cambios de `PurchaseOrder` con un listener que escribe en otra tabla (o Hibernate Envers en un experimento).

**20 🟠 Tipo propio.** Un `AttributeConverter<Money, String>` y luego con dos columnas (`@Embeddable`); ¿cuál permite consultar por monto?

## 🔴 Muy difíciles

**21 🔴 Problema del producto cartesiano.** Dos `join fetch` de colecciones (`MultipleBagFetchException`): reprodúcelo y resuélvelo de dos formas.

**22 🔴 Repositorio genérico.** `JpaRepository<T, ID>` propio con `findById`, `save`, `delete`, `findAll(spec)` usando `reified` y Criteria.

**23 🔴 Persistencia de pedidos y reportes.** Modela pedidos, líneas y reportes diarios; una consulta JPQL que devuelva las ventas diarias por estado
y compárala con la de SQL del ejercicio 24 de JDBC. ¿Dónde pondrías la agregación en producción y por qué?

**24 🔴 Rendimiento de escritura.** Insertar 100 000 entidades: con `IDENTITY` frente a `SEQUENCE` + `hibernate.jdbc.batch_size`,
`flush`/`clear` periódicos y `StatelessSession`. Mide y explica por qué `IDENTITY` desactiva el batching.
