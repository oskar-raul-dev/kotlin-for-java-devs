#!/usr/bin/env bash
# Verifica el entorno descrito en el tutorial install (README.md): JDK, Kotlin, Gradle y toolchains.
# Crea un proyecto Gradle temporal para demostrar los toolchains; no deja nada dentro del repo.
#
#   tutorials/install/check.sh      (después de `sdk env` en la raíz del repo)
set -euo pipefail

root="$(cd "$(dirname "$0")/../.." && pwd)"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

pass=0
ok()   { pass=$((pass + 1)); echo "  ✓ $1"; }
skip() { echo "  - $1"; }
fail() { echo "  ✗ $1"; echo "    esperado: $2"; echo "    obtenido: $3"; exit 1; }
expect() {
  local desc="$1" want="$2"; shift 2
  local out
  out="$("$@" 2>&1)" || fail "$desc" "$want (y salida 0)" "$out"
  [[ "$out" == *"$want"* ]] && ok "$desc" || fail "$desc" "$want" "$out"
}

want_java="$(sed -n 's/^java=//p' "$root/.sdkmanrc")"
want_kotlin="$(sed -n 's/^kotlin=//p' "$root/.sdkmanrc")"

echo "1. SDKMAN y .sdkmanrc"
[[ -n "$want_java" && -n "$want_kotlin" ]] && ok ".sdkmanrc pide java=$want_java y kotlin=$want_kotlin" \
  || fail ".sdkmanrc" "java= y kotlin=" "$(cat "$root/.sdkmanrc")"
sdkman="${SDKMAN_DIR:-$HOME/.sdkman}"
if [[ -d "$sdkman/candidates" ]]; then
  [[ -d "$sdkman/candidates/java/$want_java" ]] && ok "SDKMAN tiene java $want_java" \
    || fail "java $want_java instalado" "sdk install java $want_java" "no está"
  [[ -d "$sdkman/candidates/kotlin/$want_kotlin" ]] && ok "SDKMAN tiene kotlin $want_kotlin" \
    || fail "kotlin $want_kotlin instalado" "sdk install kotlin $want_kotlin" "no está"
else
  skip "SDKMAN no está instalado: se verifica solo lo que hay en el PATH"
fi

echo "2. Lo que está activo en esta terminal"
expect "java del PATH es 21" 'version "21' java -version
expect "kotlinc del PATH es $want_kotlin" "kotlinc-jvm $want_kotlin" kotlinc -version

echo "3. Gradle"
expect "el wrapper de la raíz es Gradle 9.9.1" "Gradle 9.9.1" "$root/gradlew" --version
expect "el track 19 trae su wrapper 9.2.1" "gradle-9.2.1-bin.zip" cat "$root/22-legacy-kotlin19/gradle/wrapper/gradle-wrapper.properties"
for s in 01-language 04-data 22-legacy-kotlin19; do
  expect "$s pide jvmToolchain(21)" "jvmToolchain(21)" cat "$root/$s/build.gradle.kts"
done
expect "Gradle detecta un JDK 21" "Language Version:   21" "$root/gradlew" -p "$root/01-language" -q javaToolchains --console=plain

echo "4. Toolchains en un proyecto temporal"
mkdir -p "$work/src/main/kotlin"
echo 'rootProject.name = "toolchains"' > "$work/settings.gradle.kts"
cat > "$work/build.gradle.kts" <<'EOF'
plugins { kotlin("jvm") version "2.4.21" }
repositories { mavenCentral() }
kotlin { jvmToolchain((findProperty("jdk") as String?)?.toInt() ?: 21) }
tasks.register("gradleJvm") { doLast { println("Gradle corre en Java " + System.getProperty("java.version")) } }
EOF
echo 'fun main() = println("hola")' > "$work/src/main/kotlin/Main.kt"
tool="$("$root/gradlew" -p "$work" -q javaToolchains --console=plain 2>&1)"
expect "Gradle corre sobre el JDK del PATH (21)" "Gradle corre en Java 21" "$root/gradlew" -p "$work" -q gradleJvm --console=plain
if [[ "$tool" == *"Language Version:   17"* ]]; then
  "$root/gradlew" -p "$work" -q compileKotlin -Pjdk=17 --console=plain >/dev/null 2>&1
  expect "jvmToolchain(17) compila con el 17: bytecode major 61" "major version: 61" javap -v "$work/build/classes/kotlin/main/MainKt.class"
else
  skip "no hay JDK 17 instalado: se salta la prueba de jvmToolchain(17)"
fi
if [[ "$tool" != *"Language Version:   19"* ]]; then
  out="$("$root/gradlew" -p "$work" -q compileKotlin -Pjdk=19 --console=plain 2>&1 || true)"
  [[ "$out" == *"Toolchain download repositories have not been configured"* ]] \
    && ok "sin JDK 19 ni repositorio de descarga: el build falla y lo dice" \
    || fail "JDK faltante" "Toolchain download repositories have not been configured" "$out"
else
  skip "hay un JDK 19 instalado: se salta la prueba del JDK faltante"
fi

echo
echo "Todo en orden: $pass verificaciones."
