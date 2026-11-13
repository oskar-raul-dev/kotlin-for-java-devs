#!/usr/bin/env bash
# Verifica que los comandos del tutorial cli (README.md) hagan lo que el texto dice.
# Compila en una carpeta temporal: no deja nada dentro del repo.
#
#   tutorials/cli/check.sh          (con kotlinc 2.4.x en el PATH: `sdk env` en la raíz del repo)
set -euo pipefail

here="$(cd "$(dirname "$0")" && pwd)"
ex="$here/examples"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
cd "$work"

pass=0
ok()   { pass=$((pass + 1)); echo "  ✓ $1"; }
fail() { echo "  ✗ $1"; echo "    esperado: $2"; echo "    obtenido: $3"; exit 1; }

# expect <descripción> <texto esperado (subcadena)> <comando…>: el comando termina bien y su salida contiene el texto
expect() {
  local desc="$1" want="$2"; shift 2
  local out
  out="$("$@" 2>&1)" || fail "$desc" "$want (y salida 0)" "$out"
  [[ "$out" == *"$want"* ]] && ok "$desc" || fail "$desc" "$want" "$out"
}

# expect_fail <descripción> <texto esperado> <comando…>: el comando falla y su salida contiene el texto
expect_fail() {
  local desc="$1" want="$2"; shift 2
  local out
  if out="$("$@" 2>&1)"; then fail "$desc" "un error con: $want" "$out"; fi
  [[ "$out" == *"$want"* ]] && ok "$desc" || fail "$desc" "$want" "$out"
}

command -v kotlinc >/dev/null || { echo "kotlinc no está en el PATH (en la raíz del repo: sdk env)"; exit 1; }
version="$(kotlinc -version 2>&1)"
echo "$version"
[[ "$version" == *"kotlinc-jvm 2.4."* ]] || { echo "Este tutorial está escrito para Kotlin 2.4.x (sdk env en la raíz)"; exit 1; }
stdlib="$(dirname "$(dirname "$(command -v kotlinc)")")/lib/kotlin-stdlib.jar"
# Con SDKMAN, kotlinc es un enlace en candidates/kotlin/<versión>/bin: resolverlo si hace falta.
[[ -f "$stdlib" ]] || stdlib="$(dirname "$(dirname "$(readlink -f "$(command -v kotlinc)")")")/lib/kotlin-stdlib.jar"
[[ -f "$stdlib" ]] || { echo "no encuentro kotlin-stdlib.jar junto a kotlinc"; exit 1; }

echo "1. Compilar y correr"
kotlinc "$ex/Hello.kt" -d out 2>/dev/null
[[ -f out/HelloKt.class ]] && ok "Hello.kt compila a out/HelloKt.class" || fail "clase HelloKt" "out/HelloKt.class" "$(ls -R out)"
expect "kotlin -cp out HelloKt Ana" "Hola, Ana" kotlin -cp out HelloKt Ana
expect "sin argumentos usa el valor por defecto" "Hola, mundo" kotlin -cp out HelloKt
expect_fail "java sin la stdlib falla" "NoClassDefFoundError: kotlin/jvm/internal/Intrinsics" java -cp out HelloKt Ana
expect "java con kotlin-stdlib.jar en el classpath" "Hola, Ana" java -cp "out:$stdlib" HelloKt Ana
expect "javap: clase final con main estático" "public static final void main(java.lang.String[])" javap out/HelloKt.class
expect "bytecode por defecto: Java 8 (major 52)" "major version: 52" javap -v out/HelloKt.class

echo "2. Jars"
kotlinc "$ex/Hello.kt" -d hello.jar 2>/dev/null
expect "el manifiesto trae Main-Class" "Main-Class: HelloKt" unzip -p hello.jar META-INF/MANIFEST.MF
expect_fail "java -jar sin -include-runtime falla" "NoClassDefFoundError: kotlin/jvm/internal/Intrinsics" java -jar hello.jar Ana
kotlinc "$ex/Hello.kt" -include-runtime -d hello-fat.jar 2>/dev/null
expect "java -jar con -include-runtime" "Hola, Ana" java -jar hello-fat.jar Ana
expect "kotlin también corre un jar" "Hola, Luis" kotlin hello-fat.jar Luis
small=$(wc -c < hello.jar); fat=$(wc -c < hello-fat.jar)
(( fat > 4000000 && small < 5000 )) && ok "tamaños: hello.jar ${small} B, hello-fat.jar ${fat} B" \
  || fail "tamaños de los jars" "chico < 5 KB y gordo > 4 MB" "$small / $fat"
expect "-include-runtime mete también kotlin-reflect" "kotlin/reflect/jvm/internal/KClassImpl.class" unzip -l hello-fat.jar
mkdir -p two && printf 'fun main() = println("a")\n' > two/A.kt && printf 'fun main() = println("b")\n' > two/B.kt
kotlinc two -d two.jar 2>/dev/null
if unzip -p two.jar META-INF/MANIFEST.MF | grep -q Main-Class; then fail "dos main" "sin Main-Class" "con Main-Class"; else ok "con dos main el manifiesto queda sin Main-Class"; fi

echo "3. Paquetes, librerías y classpath"
kotlinc "$ex/bank/money" -d money.jar 2>/dev/null
kotlinc "$ex/bank/app" -cp money.jar -d app.jar 2>/dev/null
expect "la app usa la librería con -cp" "total = USD 14.75" kotlin -cp money.jar:app.jar dev.raul.oskar.learning.kotlin.tutorials.cli.app.MainKt
expect_fail "sin la librería en -cp no compila" "unresolved reference" kotlinc "$ex/bank/app" -d app2.jar
expect "@file:JvmName genera la clase Monies" "Monies.class" unzip -l money.jar
kotlinc "$ex/bank" -include-runtime -d bank.jar 2>/dev/null
expect "un jar ejecutable con todo: Main-Class detectado" "Main-Class: dev.raul.oskar.learning.kotlin.tutorials.cli.app.MainKt" unzip -p bank.jar META-INF/MANIFEST.MF
expect "java -jar bank.jar" "total = USD 14.75" java -jar bank.jar

echo "4. Java consumiendo Kotlin"
javac -cp money.jar -d javaout "$ex/java/UseMoney.java"
expect "Java llama a Monies.usd y a plus()" "desde Java: USD 100.00 (amount=100.00)" java -cp "javaout:money.jar:$stdlib" UseMoney
expect_fail "Java sin la stdlib al correr falla" "NoClassDefFoundError" java -cp "javaout:money.jar" UseMoney

echo "5. Opciones del compilador"
kotlinc "$ex/Hello.kt" -jvm-target 21 -d out21 2>/dev/null
expect "-jvm-target 21 da major 65" "major version: 65" javap -v out21/HelloKt.class
expect_fail "-jvm-target 1.6 ya no existe" "JVM target 1.6 is no longer supported" kotlinc "$ex/Hello.kt" -jvm-target 1.6 -d outx
expect_fail "-language-version 1.9 ya no se acepta en 2.4" "language version 1.9 is no longer supported" kotlinc "$ex/Hello.kt" -language-version 1.9 -d outx
expect_fail "-api-version 1.9 tampoco" "API version 1.9 is no longer supported" kotlinc "$ex/Hello.kt" -api-version 1.9 -d outx
expect "-language-version 2.0 compila, deprecado" "language version 2.0 is deprecated" kotlinc "$ex/Hello.kt" -language-version 2.0 -d outx
printf 'fun main() {\n    val name: String = "Ana"\n    println(name!!.length)\n}\n' > Warn.kt
expect "un aviso no frena la compilación" "warning: unnecessary non-null assertion (!!)" kotlinc Warn.kt -d outw
expect_fail "-Werror convierte el aviso en error" "warnings found and -Werror specified" kotlinc Warn.kt -Werror -d outw
expect "kotlin -e evalúa una expresión" "15" kotlin -e 'println((1..5).sum())'
expect "-Xjdk-release existe" "-Xjdk-release=<version>" kotlinc -X

echo
echo "Todo en orden: $pass verificaciones."
