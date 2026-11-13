# Tutorial: Kotlin desde la línea de comandos

`kotlinc` y `kotlin` sin Gradle ni IDE: qué genera el compilador, por qué un jar de Kotlin no corre solo con
`java -jar`, cómo se arma un jar ejecutable y cómo lo consume código Java. Es lo que hace Gradle por debajo; verlo a
mano evita sorpresas cuando un build falla o cuando hay que correr algo en un servidor sin herramientas.

> **Versión:** Kotlin **2.4.21** sobre JDK 21. Todas las salidas de este tutorial están medidas con esa versión, y
> [`check.sh`](check.sh) las vuelve a verificar. **Ejercicios:** [`Exercises.md`](Exercises.md).

**Salto rápido:** [Preparar](#preparar) · [Compilar y correr](#1-compilar-y-correr) · [Jars](#2-jars) ·
[Paquetes y classpath](#3-paquetes-librerías-y-classpath) · [Desde Java](#4-java-consumiendo-kotlin) ·
[Opciones](#5-opciones-del-compilador) · [Chuleta](#chuleta-java--kotlin) · [Trampas](#trampas)

## Preparar

```bash
sdk env install          # en la raíz del repo: JDK 21 y Kotlin 2.4.21 (lee .sdkmanrc)
kotlinc -version         # info: kotlinc-jvm 2.4.21 (JRE 21.0.8+9-LTS)
cd tutorials/cli/examples
```

Sin SDKMAN: `brew install kotlin` (instala la última, no necesariamente la 2.4.21) o el zip de la página de
*releases* de Kotlin en GitHub, descomprimido y con su `bin/` en el `PATH`. Gradle no necesita nada de esto: baja su
propio compilador. El compilador de línea de comandos solo hace falta para este tutorial y para los scripts.

| Comando | Equivale en Java a | Para qué |
|---|---|---|
| `kotlinc` | `javac` | Compilar `.kt` a `.class` o a un `.jar`; también corre scripts (`-script`) y expresiones (`-e`) |
| `kotlin` | `java` | Correr una clase o un jar **agregando la stdlib de Kotlin** al classpath; también scripts `.main.kts` |

## 1. Compilar y correr

[`examples/Hello.kt`](examples/Hello.kt) es un `main` suelto, sin clase:

```kotlin
fun main(args: Array<String>) {
    val name = args.firstOrNull() ?: "mundo"
    println("Hola, $name")
}
```

```bash
kotlinc Hello.kt -d out          # ~2.5 s: arrancar el compilador es lo que más tarda
ls out                           # HelloKt.class  META-INF/main.kotlin_module
kotlin -cp out HelloKt Ana       # Hola, Ana
```

- **`HelloKt`**: las funciones de nivel superior de `Hello.kt` van a una clase que se llama como el archivo más
  `Kt`. Con `@file:JvmName("…")` se le pone otro nombre (sección 3).
- `META-INF/main.kotlin_module` es metadato del compilador (lista las clases de fachada por paquete). No lo borres de
  un jar.

`javap` muestra lo que vería Java:

```text
$ javap out/HelloKt.class
public final class HelloKt {
  public static final void main(java.lang.String[]);
}
```

Una clase `final` con un `main` estático: lo mismo que escribirías en Java. Ahora, la diferencia importante:

```text
$ java -cp out HelloKt Ana
Exception in thread "main" java.lang.NoClassDefFoundError: kotlin/jvm/internal/Intrinsics
	at HelloKt.main(Hello.kt)

$ java -cp out:$KOTLIN_HOME/lib/kotlin-stdlib.jar HelloKt Ana
Hola, Ana
```

El bytecode de Kotlin llama a la **stdlib de Kotlin** (aquí, `Intrinsics`, que revisa que `args` no sea `null`).
`kotlin` la agrega sola al classpath; `java` no. Con SDKMAN, `$KOTLIN_HOME` es
`~/.sdkman/candidates/kotlin/current`; con Gradle, la stdlib llega como dependencia.

## 2. Jars

```bash
kotlinc Hello.kt -d hello.jar                      # -d con .jar: empaqueta en vez de dejar clases sueltas
unzip -p hello.jar META-INF/MANIFEST.MF            # Main-Class: HelloKt
java -jar hello.jar Ana                            # NoClassDefFoundError: kotlin/jvm/internal/Intrinsics

kotlinc Hello.kt -include-runtime -d hello-fat.jar
java -jar hello-fat.jar Ana                        # Hola, Ana
kotlin hello-fat.jar Luis                          # Hola, Luis (kotlin también corre jars)
```

| Jar | Tamaño | ¿`java -jar` funciona? |
|---|---|---|
| `hello.jar` | 1.3 KB | No: falta la stdlib |
| `hello-fat.jar` (`-include-runtime`) | 5.7 MB | Sí: trae la stdlib adentro |

- `kotlinc` escribe `Main-Class` en el manifiesto cuando encuentra **un único** `main`. Con dos (`A.kt` y `B.kt`,
  cada uno con su `main`) el manifiesto queda sin `Main-Class` y `java -jar` no sabe qué correr.
- `-include-runtime` es el "fat jar" más simple. En un proyecto real eso lo hace Gradle (plugin `application` o
  Shadow), que además mete las demás dependencias; se ve en `08-build`.
- ¿Por qué 5.7 MB? `-include-runtime` mete la stdlib (1.85 MB) **y también `kotlin-reflect`** (3.8 MB), aunque el
  programa no use reflexión. Nada se recorta: eso es trabajo de R8/ProGuard en Android. Gradle, en cambio, solo
  empaqueta `kotlin-reflect` si lo declaras como dependencia.

## 3. Paquetes, librerías y classpath

[`examples/bank/`](examples/bank/) es una mini librería del caso Banco ([`docs/Casos.md`](../../docs/Casos.md)) y una
app que la usa:

```text
examples/bank/
├── money/Money.kt      package …tutorials.cli.money · data class Money, fun usd(…), @file:JvmName("Monies")
└── app/Main.kt         package …tutorials.cli.app   · suma montos con la librería
```

```bash
kotlinc bank/money -d money.jar                    # una carpeta entera: compila todos sus .kt
kotlinc bank/app -cp money.jar -d app.jar          # la app compila contra la librería
kotlin -cp money.jar:app.jar dev.raul.oskar.learning.kotlin.tutorials.cli.app.MainKt
# total = USD 14.75
```

- Sin `-cp money.jar`, la app no compila: `unresolved reference`.
- **A diferencia de `javac`, la carpeta no tiene que imitar el paquete.** `money/Money.kt` declara
  `package dev.raul.oskar.learning.kotlin.tutorials.cli.money` y compila igual; el `.class` sí queda en la ruta del
  paquete. La convención (carpetas = paquete) se sigue igual en el curso, porque las herramientas la esperan.
- `@file:JvmName("Monies")` hace que las funciones de nivel superior de `Money.kt` vivan en `Monies.class` en lugar de
  `MoneyKt.class`. Importa sobre todo para Java (sección 4).

Todo junto en un jar ejecutable:

```bash
kotlinc bank -include-runtime -d bank.jar
unzip -p bank.jar META-INF/MANIFEST.MF             # Main-Class: dev.raul.oskar.learning.kotlin.tutorials.cli.app.MainKt
java -jar bank.jar                                 # total = USD 14.75
```

## 4. Java consumiendo Kotlin

[`examples/java/UseMoney.java`](examples/java/UseMoney.java):

```java
Money total = Monies.usd("99.99").plus(Monies.usd("0.01"));   // operator plus = método plus()
System.out.println("desde Java: " + total + " (amount=" + total.getAmount() + ")");
```

```bash
javac -cp money.jar -d javaout java/UseMoney.java
java -cp javaout:money.jar:$KOTLIN_HOME/lib/kotlin-stdlib.jar UseMoney
# desde Java: USD 100.00 (amount=100.00)
```

- `fun usd(…)` de nivel superior → `Monies.usd(…)`, un método estático de la clase de fachada.
- `operator fun plus` → `plus(…)`: los operadores son métodos con nombre fijo.
- `val amount` → `getAmount()`: las propiedades son getters (y setters si son `var`).
- Para **compilar** basta `money.jar`; para **correr** hace falta además `kotlin-stdlib.jar` (sin ella,
  `NoClassDefFoundError`). Una librería Kotlin publicada en Maven declara la stdlib como dependencia, así que Maven o
  Gradle la traen solos.

Lo demás de la interoperabilidad (`@JvmStatic`, `@JvmOverloads`, `@Throws`, tipos de plataforma) va en la lección
`interop` de `01-language`.

## 5. Opciones del compilador

```bash
kotlinc -help          # opciones estándar
kotlinc -X             # opciones avanzadas (-X…), que pueden cambiar entre versiones
```

| Opción | Qué hace | Medido con 2.4.21 |
|---|---|---|
| `-d <carpeta o .jar>` | Destino | Si termina en `.jar`, empaqueta |
| `-cp <rutas>` | Classpath de compilación | Separador `:` (en Windows, `;`) |
| `-include-runtime` | Mete la stdlib en el jar | 5.7 MB más |
| `-jvm-target <v>` | Versión del bytecode | **Por defecto 1.8** (major 52) aunque corras JDK 21; `-jvm-target 21` da major 65. `1.6` ya no existe |
| `-language-version <v>` | Versión del lenguaje | **Mínimo 2.0**, y deprecado: `1.9` da `language version 1.9 is no longer supported` |
| `-api-version <v>` | Hasta qué API de la stdlib se puede usar | Igual: `API version 1.9 is no longer supported` |
| `-Werror` | Avisos = errores | `error: warnings found and -Werror specified` |
| `-e '<código>'` | Evalúa una expresión | `kotlin -e 'println((1..5).sum())'` → `15` |
| `-script <archivo.kts>` | Corre un script | Ver el tutorial [`repl`](../repl/README.md) |

El dato de `-language-version` importa para los tracks legacy: un compilador 2.4 **no puede** compilar "como 1.9".
Por eso el track `22-legacy-kotlin19` usa el compilador 1.9.22 de verdad
([`docs/LegacyKotlin.md`](../../docs/LegacyKotlin.md)).

Un aviso no detiene la compilación, salvo con `-Werror`:

```text
$ kotlinc Warn.kt -d out
Warn.kt:3:17: warning: unnecessary non-null assertion (!!) on a non-null receiver of type 'String'.
    println(name!!.length)
                ^^
```

## Chuleta Java → Kotlin

| Java | Kotlin |
|---|---|
| `javac -d out Hello.java` | `kotlinc Hello.kt -d out` |
| `java -cp out Hello` | `kotlin -cp out HelloKt` |
| `jar cfe app.jar Main -C out .` | `kotlinc src -d app.jar` (el manifiesto lo escribe solo) |
| — (un jar de Java corre con el JDK) | `-include-runtime`, porque hace falta la stdlib |
| `javac --release 17` | `kotlinc -jvm-target 17 -Xjdk-release=17` (`-jvm-target` fija el bytecode; `-Xjdk-release`, la API del JDK visible) |
| `jshell` | No hay REPL estable en 2.4: scripts `.main.kts` ([`repl`](../repl/README.md)) |

## Trampas

- **`java -jar` de un jar de Kotlin sin `-include-runtime`:** `NoClassDefFoundError: kotlin/jvm/internal/Intrinsics`.
  No es un error de tu código; falta la stdlib.
- **El bytecode por defecto es Java 8.** Si quieres usar algo del JDK más nuevo desde el bytecode (records, por
  ejemplo, con `@JvmRecord`), sube `-jvm-target`.
- **`HelloKt`, no `Hello`.** Al correr con `kotlin` o `java` se usa el nombre de la clase generada.
- **La carpeta no define el paquete.** Kotlin no se queja si no coinciden; las herramientas y tus compañeros sí.
- **Arrancar `kotlinc` cuesta ~2.5 s** aunque el archivo sea mínimo. Gradle mantiene un *daemon* del compilador para
  no pagarlo en cada build.
