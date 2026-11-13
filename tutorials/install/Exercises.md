# Ejercicios: instalar y preparar el entorno

> **Tutorial:** [`install`](README.md) · **Dónde se resuelven:** `tutorials/install/practice/` (un `NOTAS.md` con los
> comandos, las salidas y las respuestas; y los proyectos de prueba que armes) · **Verificar:** cada ejercicio trae
> el comando y lo que debe salir.
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil · 🎤 pregunta típica de entrevista ·
> **🎯 = prioritario** (ruta mínima).

**Prioritarios (🎯):** 01, 03, 06, 08.

## 🟢 Fáciles

**01 🟢 🎯 Tu entorno.** Corre `sdk env` en la raíz y luego `tutorials/install/check.sh`. Copia el resultado en
`NOTAS.md`; si algo falla, anota qué instalaste para arreglarlo.

**02 🟢 `current` frente a `sdk env`.** Abre dos terminales. En una entra a la raíz del repo y haz `sdk env`; en la
otra, no. Compara `java -version`, `kotlinc -version` y `sdk current` en las dos.
- Casos: la diferencia entre lo que activa `sdk env` y la versión por defecto (`current`).

**03 🟢 🎯 El wrapper.** Responde en `NOTAS.md` con comandos que lo demuestren: dónde guarda Gradle la distribución
que baja el wrapper, qué versión usa la raíz y cuál el track 19, y qué pasa si borras esa carpeta y vuelves a correr
`./gradlew --version`.

**04 🟢 Dos Kotlin.** Explica la línea `Kotlin: …` de `./gradlew --version` y por qué no coincide con la del plugin
del proyecto. Demuéstralo con la versión que reporta `KotlinVersion.CURRENT` en un `Main` de `01-language`.

## 🟡 Medios

**05 🟡 Auto-env.** Activa `sdkman_auto_env=true`, entra y sal de la raíz del repo y de `22-legacy-kotlin19/`, y anota
qué versiones quedan activas en cada paso. Al terminar, déjalo como estaba si no lo quieres.

**06 🟡 🎯 Toolchain de otra versión.** En `practice/toolchain17/`, un proyecto Gradle mínimo (copia la estructura de
`check.sh`) con `jvmToolchain(17)`.
- Casos: imprime en un `main` la versión de Java con la que corre (`System.getProperty("java.version")`) usando
  `./gradlew run`; saca el `major` del bytecode con `javap -v`; anota con qué JDK corre Gradle mientras tanto.

**07 🟡 El JDK que falta.** En el mismo proyecto pide `jvmToolchain(19)` (o una versión que no tengas) y copia el
error. Arréglalo de dos formas: instalando ese JDK con SDKMAN, y agregando el plugin foojay en una copia del proyecto.
- Casos: dónde quedó cada JDK (`./gradlew -q javaToolchains`, `~/.gradle/jdks`); cuánto bajó foojay.

**08 🟡 🎯 🎤 `jvmToolchain` frente a `jvmTarget`.** Explica en `NOTAS.md` la diferencia entre el JDK con que corre
Gradle, el JDK del toolchain y el `jvmTarget` del bytecode, con una tabla de tres proyectos de ejemplo (por ejemplo,
Gradle en 21, toolchain 17, target 11) y el `major` que sale en cada uno.
- Pista: `kotlin { jvmToolchain(17); compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }`, y el error que da si el
  target de Java y el de Kotlin no coinciden.

**09 🟡 IntelliJ.** Abre el repo en IntelliJ, corre una prueba de `01-language` desde el IDE y luego cambia
*Gradle JVM* a un JDK 17. Copia lo que pasa al sincronizar y al correr la prueba, y vuelve a dejarlo en 21.

## 🟠 Difíciles

**10 🟠 Sin SDKMAN.** En un contenedor limpio (`docker run --rm -it -v "$PWD":/work -w /work eclipse-temurin:21-jdk
bash`), consigue correr `./gradlew -p 01-language test` y un `kotlinc -version` 2.4.21 instalando solo con `curl` y
`unzip`.
- Casos: los comandos exactos en `NOTAS.md`; cuánto bajó cada cosa; por qué las pruebas de PostgreSQL salen `SKIPPED`.

**11 🟠 Dos JDK en el mismo build.** Haz que las pruebas de `practice/toolchain17/` corran con JDK 21 mientras el
código compila con 17 (`tasks.test { javaLauncher.set(javaToolchains.launcherFor { … }) }`).
- Casos: una prueba que imprima la versión con que corre; demuestra que el código sigue siendo bytecode 61.

## 🔴 Muy difíciles

**12 🔴 Instalador del curso.** `practice/setup.sh`: deja una máquina lista para el curso desde cero (instala SDKMAN si
no está, hace `sdk env install` en la raíz y en el track 19, levanta las bases de Docker si Docker existe y corre
todos los `check.sh` de los tutoriales). Debe poder correrse dos veces sin romper nada (idempotente).
- Casos: segunda corrida sin descargas; mensaje claro si falta Docker; código de salida distinto de 0 si algún
  `check.sh` falla.
