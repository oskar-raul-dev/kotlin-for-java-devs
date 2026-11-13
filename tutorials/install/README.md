# Tutorial: instalar y preparar el entorno

Qué hay que instalar para el curso y qué no: el JDK, el Kotlin de línea de comandos, Gradle (que no se instala), los
toolchains de Gradle y el IDE. Con SDKMAN y el `.sdkmanrc` del repo, cada carpeta pide sus versiones y una terminal no
pisa a la otra.

> **Versiones:** JDK 21, Kotlin 2.4.21 de línea de comandos, Gradle 9.9.1 (wrapper). [`check.sh`](check.sh) revisa tu
> entorno y demuestra los toolchains en un proyecto temporal (14 comprobaciones). **Ejercicios:**
> [`Exercises.md`](Exercises.md).

**Salto rápido:** [Qué hace falta](#qué-hace-falta) · [SDKMAN](#1-sdkman) · [Sin SDKMAN](#2-sin-sdkman) ·
[Gradle](#3-gradle-no-se-instala) · [Toolchains](#4-toolchains-de-gradle) · [IDE](#5-el-ide) · [Docker](#6-docker) ·
[Trampas](#trampas)

## Qué hace falta

| Pieza | ¿Instalar? | Para qué |
|---|---|---|
| **JDK 21** | Sí | Correr Gradle, IntelliJ y todo lo demás |
| **Kotlin de línea de comandos** | Solo para los tutoriales | `kotlinc`, `kotlin`, scripts `.main.kts` |
| **Gradle** | **No** | Cada proyecto trae su wrapper (`./gradlew`), que baja la versión correcta |
| **Compilador de Kotlin para los proyectos** | **No** | Lo baja Gradle según el plugin (`kotlin("jvm") version "2.4.21"`) |
| IntelliJ IDEA | Recomendado | Kotlin es de JetBrains: el soporte del IDE es el de referencia |
| Docker | Opcional | PostgreSQL, MySQL y los JDK de los tracks legacy |

## 1. SDKMAN

[SDKMAN](https://sdkman.io) instala varias versiones de JDK, Kotlin, Gradle y Maven lado a lado, y cambia entre ellas
por terminal o por carpeta. Es lo que usa el curso.

```bash
curl -s "https://get.sdkman.io" | bash        # instalar SDKMAN (una vez); abrir otra terminal
sdk install java 21.0.8-zulu                  # el JDK del curso
sdk install kotlin 2.4.21                     # el Kotlin de línea de comandos
```

Al instalar, SDKMAN pregunta si la versión pasa a ser la **por defecto** (`current`). No hace falta: el repo trae un
[`.sdkmanrc`](../../.sdkmanrc) en la raíz y `sdk env` activa esas versiones solo en esa terminal:

```bash
cd kotlin-for-java-devs
cat .sdkmanrc                 # java=21.0.8-zulu · kotlin=2.4.21
sdk env install               # instala lo que falte y lo activa
sdk env                       # (las siguientes veces) solo activa
kotlinc -version              # info: kotlinc-jvm 2.4.21 (JRE 21.0.8+9-LTS)
sdk current                   # qué versión hay activa de cada candidato
```

- Con `sdkman_auto_env=true` en `~/.sdkman/etc/config`, el `sdk env` ocurre solo al entrar a la carpeta y se deshace
  al salir.
- **Cada track legacy trae su propio `.sdkmanrc`**: al entrar a `22-legacy-kotlin19/`, `sdk env` activa lo de ese
  track ([`docs/LegacyKotlin.md`](../../docs/LegacyKotlin.md)).
- `sdk list java` muestra las distribuciones (Temurin, Zulu, Corretto, GraalVM…). El curso usa Zulu 21; cualquier JDK
  21 sirve para compilar y correr.
- Con SDKMAN, `KOTLIN_HOME` apunta a `~/.sdkman/candidates/kotlin/current` y `JAVA_HOME` al JDK activo.

## 2. Sin SDKMAN

| Sistema | JDK | Kotlin de línea de comandos |
|---|---|---|
| macOS (Homebrew) | `brew install --cask zulu@21` o `brew install openjdk@21` | `brew install kotlin` (instala la última: hoy 2.4.21) |
| Windows | El instalador `.msi` de Zulu o Temurin; `winget` | El zip de la página de *releases* de Kotlin en GitHub; agregar su `bin\` al `PATH` |
| Linux | El paquete de la distribución (`openjdk-21-jdk`) o el `.tar.gz` | El zip de *releases*, o SDKMAN |

Homebrew instala una sola versión a la vez; para tener 1.9.22 y 2.4.21 juntos (track legacy y base), SDKMAN es más
cómodo.

## 3. Gradle no se instala

```bash
./gradlew --version           # Gradle 9.9.1 · Launcher JVM: 21.0.8
```

- `./gradlew` lee `gradle/wrapper/gradle-wrapper.properties`, baja esa versión de Gradle a `~/.gradle/wrapper/dists` la
  primera vez y la reutiliza. Todos los que clonan el repo usan la misma.
- El track `22-legacy-kotlin19/` trae **su propio wrapper** (Gradle 9.2.1): se corre con el `./gradlew` de esa
  carpeta.
- La línea `Kotlin: 2.2.20` de `./gradlew --version` es el Kotlin con el que Gradle corre sus scripts
  (`build.gradle.kts`), **no** el de tu proyecto. El del proyecto lo fija el plugin: 2.4.21 en la base, 1.9.22 en el
  track 19.
- Gradle corre sobre el JDK de `JAVA_HOME` (o el del `PATH`). Con `sdk env`, es el 21.

## 4. Toolchains de Gradle

`kotlin { jvmToolchain(21) }` en cada `build.gradle.kts` le dice a Gradle **con qué JDK compilar y correr las
pruebas**, independientemente del JDK con que corre Gradle. Gradle busca ese JDK en la máquina:

```bash
./gradlew -p 01-language -q javaToolchains     # lista los JDK que encontró, y cómo
```

```text
 + Options
     | Auto-detection:     Enabled
     | Auto-download:      Enabled

 + Azul Zulu JDK 17 (17.0.16+8-LTS)
     | Location:           /Users/…/.sdkman/candidates/java/17.0.16-zulu
     | Language Version:   17
     | Detected by:        SDKMAN!
```

En esta máquina los encontró por tres vías: `SDKMAN!` (nueve JDK), `environment variable 'JAVA_HOME'` y `Current JVM`.
Gradle también busca en las ubicaciones estándar de cada sistema operativo y en otros gestores; la lista completa está
en la documentación de *toolchains* de Gradle. Lo que demuestra `check.sh` en un proyecto temporal:

| Prueba | Resultado medido |
|---|---|
| Gradle corriendo con JDK 21 y `jvmToolchain(17)` | Compila con el 17: bytecode **major 61** (Java 17), no 52 como `kotlinc` a mano |
| `jvmToolchain(19)` sin un JDK 19 instalado | `Cannot find a Java installation on your machine … matching: {languageVersion=19 …}. Toolchain download repositories have not been configured.` |

"Auto-download: Enabled" no alcanza: hace falta un **repositorio de descarga**. El más usado es el plugin foojay, en
`settings.gradle.kts`:

```kotlin
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
```

Con él, Gradle baja el JDK que falte a `~/.gradle/jdks`. El curso **no** lo usa (todo corre con el JDK 21 que ya
tienes); comprobado que aplica en Gradle 9.9.1, pero la descarga en sí no se probó aquí.

`jvmToolchain(n)` también fija el `jvmTarget` del bytecode a `n`: por eso un proyecto Gradle con `jvmToolchain(21)`
genera bytecode Java 21, mientras que `kotlinc` a mano genera Java 8 por defecto ([`cli`](../cli/README.md)).

## 5. El IDE

**IntelliJ IDEA** (la versión gratuita alcanza para el curso):

- *File → Open* sobre la carpeta raíz del repo. Importa el *composite build*: `01-language` y `04-data` como builds
  incluidos.
- *Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JVM*: el JDK 21. El SDK del proyecto también
  en 21 (*Project Structure → SDK*).
- El plugin de Kotlin viene incluido y su versión va con la del IDE; para compilar no importa (lo hace Gradle con
  2.4.21), pero un IDE viejo puede no entender sintaxis nueva del lenguaje.
- Los tracks legacy se abren **aparte**, cada uno como su propio proyecto (*File → Open* sobre `22-legacy-kotlin19/`).
- `.idea/` está en `.gitignore`: la configuración del IDE es personal.

**VS Code:** el repo no versiona un `.code-workspace` (es personal). El soporte de Kotlin fuera de IntelliJ es más
limitado; JetBrains publica un servidor de lenguaje oficial (Kotlin LSP) ⚠️ (en desarrollo, revisar su estado). Para
correr y probar, la terminal con `./gradlew` funciona igual.

## 6. Docker

Docker Desktop (o Colima, o OrbStack en macOS). Se usa para dos cosas, ninguna obligatoria:

```bash
docker compose -f docker/postgres/compose.yaml up -d --wait   # PostgreSQL para 04-data
./scripts/legacy.sh 19                                        # el track 1.9 dentro de su JDK
```

Sin Docker, las pruebas de PostgreSQL y MySQL salen `SKIPPED` y los tracks legacy se corren en nativo.

## Comprobar todo

```bash
sdk env
tutorials/install/check.sh
```

## Trampas

- **`kotlinc -version` dice 1.9.22** aunque instalaste 2.4.21: tu `current` es otra versión y no hiciste `sdk env` en
  esta terminal.
- **"Toolchain download repositories have not been configured"**: el proyecto pide un JDK que no tienes. Instálalo
  con SDKMAN (Gradle lo detecta) o agrega foojay.
- **Instalar Gradle con brew o SDKMAN no cambia nada**: los proyectos usan su wrapper. Solo sirve para crear
  proyectos nuevos (`gradle init`).
- **`Kotlin: 2.2.20` en `./gradlew --version`** no es la versión de tu proyecto.
- **El JDK de IntelliJ y el de la terminal pueden ser distintos**: si algo compila en uno y no en el otro, revisa
  *Gradle JVM*.
