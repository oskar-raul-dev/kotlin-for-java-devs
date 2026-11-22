# Tutoriales

Guías de la terminal y del entorno: nivel 0, sin build de Gradle. Cada tutorial tiene su `README.md`, ejemplos que
corren, un `Exercises.md` y un `check.sh` que vuelve a ejecutar todos los comandos del texto y compara sus salidas,
para que el tutorial no quede desactualizado sin que se note.

## Preparar

```bash
sdk env install          # en la raíz del repo: JDK 21 y Kotlin 2.4.21 de línea de comandos (.sdkmanrc)
kotlinc -version         # info: kotlinc-jvm 2.4.21 (JRE 21.0.8+9-LTS)
```

Las secciones con Gradle no necesitan el compilador instalado (lo baja Gradle); los tutoriales sí.

## Índice

El número de la carpeta es el orden sugerido. Los paquetes de Kotlin de los ejemplos no llevan el número
(`…tutorials.cli`, porque un paquete no puede empezar con un dígito).

| Tutorial | Qué cubre | Ejercicios | Verificar | Estado |
|---|---|---|---|---|
| [`01-install/`](01-install/README.md) | Qué instalar y qué no, SDKMAN y `.sdkmanrc`, Homebrew, el wrapper de Gradle, toolchains (`jvmToolchain`, foojay), IntelliJ, Docker | [12](01-install/Exercises.md) | `tutorials/01-install/check.sh` (14) | ✅ |
| [`02-cli/`](02-cli/README.md) | `kotlinc` y `kotlin`: qué genera el compilador, la stdlib en el classpath, jars ejecutables, librerías y `-cp`, Java consumiendo Kotlin, opciones (`-jvm-target`, `-language-version`, `-Werror`) | [15](02-cli/Exercises.md) | `tutorials/02-cli/check.sh` (30) | ✅ |
| [`03-repl/`](03-repl/README.md) | Qué pasó con el REPL (1.9 → 2.1 → 2.4), `-e`, scripts `.kts` y `.main.kts` con dependencias, `@file:Import`, caché, shebang, scratch files y Kotlin Notebook | [15](03-repl/Exercises.md) | `tutorials/03-repl/check.sh` (17) | ✅ |
| [`04-decompile/`](04-decompile/README.md) | `javap`, CFR, *Show Kotlin Bytecode* y `@Metadata`: qué genera `kotlinc` para clases, funciones, lambdas, `inline`, `value class`, `suspend` y `when`; cómo cambia con el `jvmTarget` | [16, con ⚡](04-decompile/Exercises.md) | `tutorials/04-decompile/check.sh` (47) | ✅ |
| [`05-debug/`](05-debug/README.md) | Información de depuración, `jdb`, JDWP y `--debug-jvm`, SMAP de las funciones `inline`, corrutinas (`-Dkotlinx.coroutines.debug`, `DebugProbes` como agente, `-Xdebug`), IntelliJ | [14](05-debug/Exercises.md) | `tutorials/05-debug/check.sh` (28) | ✅ |

```bash
for t in tutorials/0*/check.sh; do "$t" || break; done   # todos, en orden (136 comprobaciones)
```

## Convenciones

- Las soluciones van en `tutorials/<tutorial>/practice/` (código y un `NOTAS.md` con comandos y respuestas).
- Los ejemplos usan los casos Banco y Mercado ([`docs/Casos.md`](../docs/Casos.md)), con datos ficticios.
- Todo comando que aparezca en un `README.md` tiene su comprobación en el `check.sh` del tutorial. Si cambias uno,
  cambias el otro.
