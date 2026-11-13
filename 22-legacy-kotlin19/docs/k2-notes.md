# Notas de la migración 1.9.22 → 2.4.21 (K1 → K2)

Bitácora del paso A7 (13 de noviembre de 2026): qué cambió al pasar `01-language` y `04-data` de Kotlin 1.9.22
(último compilador K1) a 2.4.21 (K2). Es la materia prima de la lección central de este track: **migrar a K2**.

Resumen: **nada se rompió**. Las 123 pruebas pasaron en cada subpaso, con los mismos nombres, y la salida de los 14
`Main` es la misma salvo los cambios de librería anotados abajo. Lo que sí apareció fueron **avisos nuevos** de K2,
que señalan código que con K1 era necesario o pasaba sin ruido.

## Cómo se hizo

Por subpasos, con todas las pruebas (PostgreSQL y MySQL arriba) después de cada uno, para saber qué rompió qué:

| Subpaso | Cambio | Resultado |
|---|---|---|
| A7.1 | Gradle 9.2.1 → 9.9.1, todavía con Kotlin 1.9.22 | 123 en verde; 4 avisos de deprecación del plugin 1.9.22 |
| A7.2 | Kotlin 1.9.22 → 2.4.21, kotlinx-serialization 1.6.2 → 1.13.0, Kermit 2.0.4 → 2.2.0 | 123 en verde; 4 avisos nuevos del compilador |
| A7.3 | Drivers JDBC, H2, slf4j, BOM de JUnit 5.10.3 → 5.14.4 | 123 en verde |
| A7.4 | Hibernate 6.6.3 → 7.4.15 | 123 en verde |

## Avisos nuevos del compilador K2

| Código | Aviso | Por qué | Arreglo |
|---|---|---|---|
| `assertNotNull(first.id)` y luego `first.id!!` (`ProductRepositoryContract`) | *Unnecessary non-null assertion (!!) on a non-null receiver of type 'Long'* | Después de `assertNotNull(first.id)` (contrato `returns() implies (actual != null)`), K2 hace smart cast de `first.id` (un `val` de una `data class` de `src/main`) a `Long`; K1 no lo hacía y el `!!` compilaba sin aviso | Quitar el `!!` |
| `java.lang.Long::class.java` (`JpaTest`) | *This class is not recommended for use in Kotlin. Use 'kotlin.Long' instead* | K2 desaconseja nombrar los tipos en caja de Java | `Long::class.javaObjectType` (`Long::class.java` sería el primitivo `long`) |
| `intArrayOf(1) == intArrayOf(1)` (`CollectionsTest`) | *'==' on arrays compares only references* | K2 detecta la trampa de comparar arrays con `==` | Aquí se suprime con `@Suppress("ARRAY_EQUALITY_OPERATOR_CAN_BE_REPLACED_WITH_CONTENT_EQUALS")` porque la línea existe para demostrarla; en código real, `contentEquals` |

Verificado sobre el tag `monorepo-1.9.22` con `compileTestKotlin --rerun-tasks`: con 1.9.22 ninguno de los tres
aparece. Es código que compila en ambos, pero que K2 marca.

## Plugin de Gradle 1.9.22 en Gradle 9.9

Funciona, pero con `--warning-mode=all` emite 4 deprecaciones que vienen del plugin, no de los scripts:

- `Usage` con valores antiguos (`java-api-jars`, `java-runtime-jars`): error en Gradle 10.
- `Configuration.setVisible(boolean)`: se quita en Gradle 11.
- `StartParameter.isConfigurationCacheRequested`: se quita en Gradle 10.

Consecuencia para este track: **Kotlin 1.9.22 queda en Gradle 9.x**.

## Cambios de librerías (no del compilador)

- **kotlinx-serialization 1.6.2 → 1.13.0:**
  - `explicitNulls` y `prettyPrintIndent` dejaron de ser experimentales: ya no piden
    `@OptIn(ExperimentalSerializationApi::class)`. En 1.9.22 el opt-in es parte de la trampa.
  - El error de clave desconocida apunta bien: `at path: $[1]` (1.6.2 decía `$[1].item-count`, la clave anterior).
- **Hibernate 6 → 7:** cambió el texto de `LazyInitializationException`
  (`failed to lazily initialize a collection of role: … could not initialize proxy - no Session` →
  `Cannot lazily initialize collection of role '…' with key '1' (no session)`).
- **JUnit:** `kotlin-test-junit5` 1.9.22 traía JUnit 5.6.3 y, sin el BOM ni el launcher explícito, Gradle 9 fallaba
  con `Failed to load JUnit Platform`. 2.4.21 trae 5.10.1 y declara el launcher.

## Lambdas: `invokedynamic` desde 2.0 (medido en A6)

El código del curso no imprimía lambdas, así que la comparación de salidas de A7 no lo mostró. La prueba de humo del
track (`Legacy19SmokeTest`) lo fija:

| | `val inc: (Int) -> Int = { x -> x + 1 }; inc.toString()` |
|---|---|
| Kotlin 1.9.22 (K1, sin `kotlin-reflect`) | `Function1<java.lang.Integer, java.lang.Integer>`: la lambda es una clase que hereda de `kotlin.jvm.internal.Lambda` |
| Kotlin 2.4.21 (K2) | `…MainKt$$Lambda/0x0000007001000400@27c170f0`: la genera `LambdaMetafactory`, como en Java |

Cualquier log, prueba o mensaje que dependa de ese texto cambia al migrar.

## Lo que no se vio aquí y hay que cubrir en la lección

El código del curso v1 era sencillo y no tocó los cambios de comportamiento más conocidos de K2. Pendiente de
ejemplos propios ⚠️:

- Lambdas con `invokedynamic`: la serialización de lambdas (`@JvmSerializableLambda`) y la opción `-Xlambdas=class`.
- Smart casts nuevos de K2 (variables locales capturadas, `||` con tipos comunes, `inline` con contratos).
- Diferencias en la resolución de sobrecargas y en la inferencia de builders.
- Las opciones `-language-version 1.9` / `-api-version` en un compilador 2.x y su límite de versiones hacia atrás.
