# Kotlin para quien viene de Java

Ejemplos que corren, con pruebas, y ejercicios graduados para aprender Kotlin desde Java. Varios ejercicios
siguen un caso de punta a punta con pedidos de una tienda: leer JSON, limpiar nulos y fechas, agrupar, contar por
día, ventanas de tiempo y un poco de estadística.

**Salto rápido:** [Versiones](#versiones) · [Cómo se usa](#cómo-se-usa) · [Lecciones](#lecciones) ·
[Ejercicios](#ejercicios) · [Ruta prioritaria](#ruta-prioritaria) · [Bases de datos con Docker](#bases-de-datos-con-docker) ·
[Estructura](#estructura) · [Trampas encontradas](#trampas-encontradas-al-armarlo) · [Agregar una lección](#agregar-una-lección)

## Versiones

Fijadas a propósito (la migración a Kotlin 2.x va en un paso aparte):

| Pieza | Versión |
|---|---|
| Kotlin (plugins `jvm`, `serialization`, `jpa`, `allopen`) | 1.9.22 |
| kotlinx-serialization-json | 1.6.2 |
| Kermit (logging) | 2.0.4 |
| JDK (toolchain) | 21 |
| Gradle (wrapper) | 9.2.1 |

Agregado para las lecciones de bases de datos y pruebas: sqlite-jdbc 3.46.1.3, PostgreSQL JDBC 42.7.4,
mysql-connector-j 9.1.0, Hibernate ORM 6.6.3.Final, H2 2.3.232, slf4j-simple 2.0.16 y el BOM de JUnit 5.10.3.

## Cómo se usa

```bash
./gradlew test                                  # todas las pruebas
./gradlew test --tests '*.strings.*'            # las de una lección (incluye su práctica)
./gradlew test --tests '*.strings.practice.*'   # solo tus ejercicios de esa lección
./gradlew run -Plesson=basics                   # el Main de una lección
./gradlew run -Plesson=jdbc.postgres
./gradlew run -Plesson=jpa --args=postgres
./gradlew run -Plesson=io --console=plain       # la parte de consola lee de la terminal
```

- `-Plesson=<subpaquete>` elige qué `Main.kt` corre: el build arma `dev.raul.oskar.learning.kotlin.<lesson>.MainKt`.
  Sin `-Plesson`, corre `basics`.
- `--tests` filtra por nombre de clase con comodines.
- `--args` pasa argumentos al `main(args)`.
- `--console=plain` quita la barra de progreso de Gradle, que se mezcla con lo que se escribe en la consola.

Estado actual: **123 pruebas**, todas en verde. Las 18 de PostgreSQL y MySQL salen `SKIPPED` si sus
contenedores no están arriba.

## Lecciones

Paquete raíz: `dev.raul.oskar.learning.kotlin`. Cada subpaquete tiene su `Main.kt` y su `Exercises.md` en
`src/main/kotlin/…/<subpaquete>/`, y sus pruebas en el mismo paquete bajo `src/test/kotlin`.

| # | Subpaquete | Qué repasa | Ejercicios | 🎯 |
|---|---|---|---|---|
| 1 | `basics` | Tipos numéricos, conversiones explícitas, overflow, `BigDecimal` con operadores, `Char`, `==` frente a `===`, `Any`/`Unit`/`Nothing`, smart casts | [25](src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/Exercises.md) | 5 |
| 2 | `control` | `if`/`when` como expresiones, rangos (`..`, `until`, `downTo`, `step`), bucles, etiquetas `break@`, algoritmos de pizarra (gcd, primos, búsqueda binaria, two-sum, paréntesis) | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/control/Exercises.md) | 4 |
| 3 | `strings` | Plantillas, raw strings, `buildString`, `substringBefore/After`, regex con grupos con nombre, `replace` con lambda. **La trampa de `split("\\/")`** | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/strings/Exercises.md) | 3 |
| 4 | `oop` | Constructores, `init`, setters privados, `open`/`abstract`, interfaces con default, `sealed` + `when` exhaustivo, enums con `companion` y `of`, `object`, `data class`, `value class`, operadores, delegación `by`, `lazy` | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/Exercises.md) | 4 |
| 5 | `io` | `Path` + `kotlin.io.path`, `useLines`, `use {}`, recursos del classpath, consola testeable, Kermit | [23](src/main/kotlin/dev/raul/oskar/learning/kotlin/io/Exercises.md) | 3 |
| 6 | `collections` | Solo lectura frente a mutable, listas, sets, mapas (`getOrPut`), `TreeMap`, `ArrayDeque`, `PriorityQueue`, arrays | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/collections/Exercises.md) | 5 |
| 7 | `functional` | Tipos función, closures, composición, lambdas con receptor, `reified`, `Result`, scope functions, `fun interface`, pipelines (`groupBy`, `groupingBy`, `partition`), secuencias perezosas | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/functional/Exercises.md) | 6 |
| 8 | `json` | kotlinx-serialization: `@SerialName`, `explicitNulls`, `ignoreUnknownKeys`, defaults, sealed polimórfico, serializadores para `BigDecimal`/`LocalDate`, árbol `JsonElement` | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/json/Exercises.md) | 4 |
| 9 | `datetime` | `java.time`: parseo estricto `MM/dd/uuuu`, edad con `Period`, `Clock` inyectado, epoch millis y zonas, ventanas de días, horario de verano | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/Exercises.md) | 5 |
| 10 | `jdbc.sqlite` | Repositorio JDBC común + dialecto SQLite (sin servidor) | [25, compartidos](src/main/kotlin/dev/raul/oskar/learning/kotlin/jdbc/Exercises.md) | — |
| 11 | `jdbc.postgres` | Mismo repositorio en PostgreSQL + `RETURNING`, `= ANY(array)`, `jsonb` | ↑ | — |
| 12 | `jdbc.mysql` | Mismo repositorio en MySQL + `ON DUPLICATE KEY`, `LAST_INSERT_ID()`, aislamiento por defecto | ↑ | — |
| 13 | `jpa` | Hibernate 6 sin Spring: entidades en Kotlin, cascade, `orphanRemoval`, N+1 y `join fetch`, dirty checking, `LazyInitializationException`, `@Version`, UPDATE masivo | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/jpa/Exercises.md) | — |
| 14 | `exceptions` | Sin checked exceptions, `try`/`throw` como expresiones, `require`/`check`/`error`, sin multi-catch (`when` en el catch), jerarquía sellada con `cause`, `Result`/`runCatching` (y su trampa con `CancellationException`), validación que acumula errores, `use` y suppressed, `@Throws` para Java | [24](src/main/kotlin/dev/raul/oskar/learning/kotlin/exceptions/Exercises.md) | 4 |

`jdbc/` (sin subpaquete) tiene lo compartido: `ProductRepository`, la interfaz `Dialect`, las extensiones
`mapRows` e `inTransaction`, y el recorrido `runDemo`. Las tres bases pasan **la misma batería de pruebas**
(`ProductRepositoryContract`), así que las diferencias entre motores quedan aisladas en cada `*Dialect`. Por eso
los tres motores comparten un solo `jdbc/Exercises.md`, con cada ejercicio etiquetado por motor (`[todos]`,
`[SQLite]`, `[PG]`, `[MySQL]`).

## Ejercicios

**289 ejercicios** en 12 archivos `Exercises.md`, de 23 a 25 por lección, sin soluciones.

**Semáforo:**

| | Nivel | Qué esperar |
|---|---|---|
| 🟢 | Fácil | Una función, una idea de la lección. 5–15 min |
| 🟡 | Medio | Combina dos o tres ideas o tiene bordes delicados. 15–30 min |
| 🟠 | Difícil | Diseño, algoritmo no trivial o comportamiento que hay que demostrar. 30–90 min |
| 🔴 | Muy difícil | Mini-proyecto o experimento con medición. Una sesión o más |
| 🎯 | Prioritario | Ruta recomendada (ver [Ruta prioritaria](#ruta-prioritaria)). El resto queda para después |

Los tiempos son una estimación, no una medición.

**Convención para resolverlos:**

- Código en el subpaquete `practice` de la lección: `src/main/kotlin/…/<lección>/practice/ExNN<Nombre>.kt`.
- Prueba en el espejo: `src/test/kotlin/…/<lección>/practice/ExNN<Nombre>Test.kt`, con `kotlin.test`.
- Cada enunciado trae una **firma sugerida** y los **casos mínimos** que la prueba debe cubrir.
- Si dos ejercicios de la misma lección usan el mismo nombre de función (`describe`, `parse`…), envuelve cada uno
  en un `object ExNN { … }` para que no choquen dentro del paquete `practice`.
- Los ejercicios que dicen "explica en un comentario" se responden en la prueba, junto al caso que lo demuestra.

Ejemplo, para el ejercicio 02 de `basics`:

```kotlin
// src/main/kotlin/dev/raul/oskar/learning/kotlin/basics/practice/Ex02SafeInt.kt
package dev.raul.oskar.learning.kotlin.basics.practice

fun safeInt(raw: String?): Int? = TODO("ejercicio 02")
```

```kotlin
// src/test/kotlin/dev/raul/oskar/learning/kotlin/basics/practice/Ex02SafeIntTest.kt
package dev.raul.oskar.learning.kotlin.basics.practice

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Ex02SafeIntTest {
    @Test
    fun `parses trimmed numbers and rejects garbage`() {
        assertEquals(42, safeInt(" 42 "))
        assertNull(safeInt("4x"))
        assertNull(safeInt(null))
    }
}
```

```bash
./gradlew test --tests '*.basics.practice.*'
```

- Mientras la función tenga `TODO(...)`, la prueba falla con `NotImplementedError`: así se sabe qué falta.
- Gradle no compila los `Exercises.md` aunque estén en `src/main/kotlin`, porque solo toma los `.kt`.

## Ruta prioritaria

43 ejercicios 🎯, ordenados para seguir un caso de punta a punta con pedidos de una tienda: leer el JSON, limpiar
nulos y fechas, agrupar, contar por día, armar una ventana de días, sacar estadísticas y revisar el manejo de
errores. Si el tiempo no alcanza, haz primero los marcados con **★**: son 15 y cubren el recorrido completo.

**Bloque 1: leer los datos**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `json` | 02 | 🟢 Claves con guiones (`@SerialName`) |
| ★ | `json` | 03 | 🟢 Campo que falta (`explicitNulls`) |
| | `json` | 01 | 🟢 Ida y vuelta |
| | `json` | 08 | 🟡 Cargar con tolerancia |
| | `io` | 01 | 🟢 Recurso del classpath |
| | `io` | 07 | 🟢 Kermit |
| | `io` | 08 | 🟡 CSV a objetos |
| | `oop` | 01 | 🟢 `data class` de pedidos |

**Bloque 2: nulos, cadenas y fechas**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `basics` | 09 | 🟡 Cadena de nulos |
| ★ | `strings` | 01 | 🟢 Fecha MM/dd/yyyy (sin la trampa de `split`) |
| ★ | `datetime` | 02 | 🟢 Epoch a fecha |
| ★ | `datetime` | 03 | 🟢 Edad |
| | `basics` | 02 | 🟢 Parseo seguro |
| | `basics` | 05 | 🟢 Dividir la cuenta |
| | `basics` | 14 | 🟡 Modos de redondeo |
| | `basics` | 16 | 🟡 `Nothing` útil |
| | `strings` | 08 | 🟡 Código de estado |
| | `strings` | 09 | 🟡 Zip de EE. UU. |
| | `datetime` | 01 | 🟢 Fecha estricta |

**Bloque 3: categorías y conteos**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `control` | 02 | 🟢 Tramo de edad |
| ★ | `oop` | 02 | 🟢 Enum con `of` |
| ★ | `functional` | 09 | 🟡 `flatMap` de categorías |
| ★ | `collections` | 09 | 🟡 Conteo por categoría y día |
| | `control` | 01 | 🟢 Tramo de monto |
| | `collections` | 01 | 🟢 Contador con mapa |
| | `collections` | 02 | 🟢 Solo lectura no es inmutable |
| | `collections` | 08 | 🟡 Historial por usuario |
| | `functional` | 01 | 🟢 `map`, `filter`, `sumOf` |
| | `functional` | 02 | 🟢 `mapNotNull` |
| | `functional` | 08 | 🟡 `groupBy` + `mapValues` |
| | `functional` | 10 | 🟡 `fold` con acumulador rico |
| | `datetime` | 09 | 🟡 Agrupar por día local |

**Bloque 4: ventanas de tiempo y estadística**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `datetime` | 08 | 🟡 Ventana de 7 días (¿incluye el día dado?) |
| ★ | `collections` | 10 | 🟡 Ventana de 7 días con `TreeMap` |
| ★ | `functional` | 16 | 🟠 Estadística en una pasada |
| | `control` | 08 | 🟡 Primer índice que cumple |
| | `control` | 11 | 🟡 Ventana deslizante |

**Bloque 5: errores y diseño (lo que más se comenta en una revisión de código)**

| ★ | Lección | # | Ejercicio |
|---|---|---|---|
| ★ | `exceptions` | 02 | 🟢 `!!` en un PR |
| ★ | `exceptions` | 01 | 🟢 `require` y `check` |
| | `exceptions` | 08 | 🟡 Envolver sin perder la causa |
| | `exceptions` | 09 | 🟡 Jerarquía sellada a HTTP |
| | `oop` | 08 | 🟡 `sealed` para resultados |
| | `oop` | 09 | 🟡 Reglas polimórficas |

**Después:** todo lo 🟠 y 🔴 salvo la estadística en una pasada, y las lecciones `jdbc` y `jpa`. Entre los difíciles,
los que cierran el caso son `functional` 17 (días atípicos), `collections` 21 (ventana con sumas acumuladas), `jdbc` 24 (la
misma ventana con funciones de ventana SQL), `oop` 24 y `exceptions` 24 (rediseño y revisión de un `main`
monolítico).

## Bases de datos con Docker

```bash
docker compose -f docker/postgres/compose.yaml up -d --wait
docker compose -f docker/mysql/compose.yaml up -d --wait
./gradlew test
docker compose -f docker/postgres/compose.yaml down -v
docker compose -f docker/mysql/compose.yaml down -v
```

- `up -d --wait` arranca en segundo plano y espera a que el `healthcheck` diga que la base acepta conexiones.
- `down -v` borra el contenedor **y su volumen**: la próxima vez la base arranca vacía.
- PostgreSQL 17.2 escucha en `127.0.0.1:15432` y MySQL 8.4.3 en `127.0.0.1:13306`. Son puertos altos para no
  chocar con otras bases en 5432/3306. Base, usuario y clave: `learning`.
- Para usar otra base están las variables `PG_URL`/`PG_USER`/`PG_PASSWORD` y `MYSQL_URL`/`MYSQL_USER`/`MYSQL_PASSWORD`.
- **Si un contenedor no está arriba, sus pruebas salen `SKIPPED`**, no fallidas: `./gradlew test` siempre
  se puede correr. SQLite (en memoria) y JPA (H2 en memoria) no necesitan Docker.
- `./gradlew run -Plesson=jpa --args=postgres` recrea las tablas de JPA en el PostgreSQL del compose
  (`drop-and-create`). No toca la tabla `product` de JDBC.

## Estructura

```text
kotlin-for-java-devs/
├── build.gradle.kts            # versiones, -Plesson, JUnit launcher, plugins jpa/allopen
├── settings.gradle.kts
├── gradlew, gradle/wrapper/    # Gradle 9.2.1
├── docker/
│   ├── postgres/compose.yaml   # PostgreSQL 17.2 en 15432
│   └── mysql/compose.yaml      # MySQL 8.4.3 en 13306
└── src/
    ├── main/
    │   ├── kotlin/dev/raul/oskar/learning/kotlin/
    │   │   ├── <lección>/Main.kt, *.kt, Exercises.md
    │   │   ├── <lección>/practice/          # tus soluciones (se crea al resolver)
    │   │   └── jdbc/{sqlite,postgres,mysql}/
    │   └── resources/
    │       ├── data/sales.csv, data/orders.json
    │       ├── META-INF/persistence.xml     # unidad JPA "learning" (H2 por defecto)
    │       └── simplelogger.properties      # slf4j-simple solo en WARN
    └── test/kotlin/dev/raul/oskar/learning/kotlin/
        ├── <lección>/<Lección>Test.kt
        └── <lección>/practice/              # pruebas de tus soluciones
```

## Trampas encontradas al armarlo

- **Gradle 9 + Kotlin 1.9.22 no corre pruebas tal cual.** `kotlin("test")` trae JUnit 5.6.3 y Gradle 9 ya
  no inyecta el launcher, así que falla con `Failed to load JUnit Platform`. Se arregla con
  `platform("org.junit:junit-bom:5.10.3")` y `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`
  (ver `build.gradle.kts`). Cualquier proyecto con este build sin `src/test` no lo nota hasta que agrega
  su primera prueba.
- **`split(String)` en Kotlin es literal.** `"03/14/1985".split("\\/")` devuelve la cadena entera; en Java,
  `split` siempre usa regex. Es un error fácil al portar código de Java. Ver `strings/Strings.kt` y su prueba.
- **`explicitNulls` es experimental en kotlinx-serialization 1.6.2:** pide `@file:OptIn(ExperimentalSerializationApi::class)`
  (`json/Codec.kt`).
- **Hibernate registra por `java.util.logging`** aunque haya SLF4J: se fuerza con la propiedad de sistema
  `org.jboss.logging.provider=slf4j` (`jpa/Jpa.kt`).
- **Entidades JPA en Kotlin:** nada de `data class`; hacen falta los plugins `jpa` (constructor vacío) y
  `allopen` (proxies lazy).
- **MySQL gasta ids en los upserts:** cada `ON DUPLICATE KEY UPDATE` que termina en UPDATE consume un valor de
  `AUTO_INCREMENT` (`jdbc/mysql/Main.kt`).

## Agregar una lección

1. Crear `src/main/kotlin/dev/raul/oskar/learning/kotlin/<tema>/Main.kt` con `package …<tema>` y `fun main()`.
2. Poner la lógica en funciones o clases aparte (no en `main`) para poder probarla.
3. Crear `src/test/kotlin/dev/raul/oskar/learning/kotlin/<tema>/<Tema>Test.kt` con `kotlin.test`.
4. Escribir `<tema>/Exercises.md` con el mismo encabezado que los demás: 20–30 ejercicios con semáforo, firma
   sugerida y casos mínimos, y 🎯 en los que tocan lo que se está preparando.
5. Correr con `./gradlew run -Plesson=<tema>` y `./gradlew test --tests '*.<tema>.*'`.
6. Agregar la fila a la tabla de lecciones.
