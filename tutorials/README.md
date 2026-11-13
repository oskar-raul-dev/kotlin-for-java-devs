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

Orden sugerido: `install` → `cli` → `repl` → `decompile` → `debug`.

| Tutorial | Qué cubre | Ejercicios | Verificar | Estado |
|---|---|---|---|---|
| [`cli/`](cli/README.md) | `kotlinc` y `kotlin`: qué genera el compilador, la stdlib en el classpath, jars ejecutables, librerías y `-cp`, Java consumiendo Kotlin, opciones (`-jvm-target`, `-language-version`, `-Werror`) | [15](cli/Exercises.md) | `tutorials/cli/check.sh` (30) | ✅ |
| [`repl/`](repl/README.md) | Qué pasó con el REPL (1.9 → 2.1 → 2.4), `-e`, scripts `.kts` y `.main.kts` con dependencias, `@file:Import`, caché, shebang, scratch files y Kotlin Notebook | [15](repl/Exercises.md) | `tutorials/repl/check.sh` (17) | ✅ |
| [`install/`](install/README.md) | Qué instalar y qué no, SDKMAN y `.sdkmanrc`, Homebrew, el wrapper de Gradle, toolchains (`jvmToolchain`, foojay), IntelliJ, Docker | [12](install/Exercises.md) | `tutorials/install/check.sh` (14) | ✅ |
| [`decompile/`](decompile/README.md) | `javap`, CFR, *Show Kotlin Bytecode* y `@Metadata`: qué genera `kotlinc` para clases, funciones, lambdas, `inline`, `value class`, `suspend` y `when`; cómo cambia con el `jvmTarget` | [16, con ⚡](decompile/Exercises.md) | `tutorials/decompile/check.sh` (47) | ✅ |
| [`debug/`](debug/README.md) | Información de depuración, `jdb`, JDWP y `--debug-jvm`, SMAP de las funciones `inline`, corrutinas (`-Dkotlinx.coroutines.debug`, `DebugProbes` como agente, `-Xdebug`), IntelliJ | [14](debug/Exercises.md) | `tutorials/debug/check.sh` (28) | ✅ |

```bash
for t in install cli repl decompile debug; do tutorials/$t/check.sh || break; done   # todos (136 comprobaciones)
```

## Convenciones

- Las soluciones van en `tutorials/<tutorial>/practice/` (código y un `NOTAS.md` con comandos y respuestas).
- Los ejemplos usan los casos Banco y Mercado ([`docs/Casos.md`](../docs/Casos.md)), con datos ficticios.
- Todo comando que aparezca en un `README.md` tiene su comprobación en el `check.sh` del tutorial. Si cambias uno,
  cambias el otro.
