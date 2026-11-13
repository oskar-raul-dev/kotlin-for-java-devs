#!/usr/bin/env bash
# Verifica que los comandos del tutorial repl (README.md) hagan lo que el texto dice.
# Trabaja en una carpeta temporal; la primera corrida baja kotlinx-serialization de Maven Central (necesita red).
#
#   tutorials/repl/check.sh         (con kotlin 2.4.x en el PATH: `sdk env` en la raíz del repo)
set -euo pipefail

here="$(cd "$(dirname "$0")" && pwd)"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
cp "$here"/examples/* "$work"/
cd "$work"

pass=0
ok()   { pass=$((pass + 1)); echo "  ✓ $1"; }
fail() { echo "  ✗ $1"; echo "    esperado: $2"; echo "    obtenido: $3"; exit 1; }
expect() {
  local desc="$1" want="$2"; shift 2
  local out
  out="$("$@" 2>&1)" || fail "$desc" "$want (y salida 0)" "$out"
  [[ "$out" == *"$want"* ]] && ok "$desc" || fail "$desc" "$want" "$out"
}
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

echo "1. El REPL de kotlinc"
expect_fail "2.4: sin -Xrepl, el REPL está deprecado" "Kotlin REPL is deprecated and should be enabled explicitly" \
  bash -c "printf ':quit\n' | kotlinc"
expect_fail "2.4: con -Xrepl tampoco arranca" "unable to run REPL, no scripting plugin loaded" \
  bash -c "printf ':quit\n' | kotlinc -Xrepl"
k19="$HOME/.sdkman/candidates/kotlin/1.9.22/bin/kotlinc"
if [[ -x "$k19" ]]; then
  out="$(printf 'val x = 21\nx * 2\n:quit\n' | "$k19" 2>&1)"
  [[ "$out" == *"Welcome to Kotlin version 1.9.22"* && "$out" == *"res1: kotlin.Int = 42"* ]] \
    && ok "1.9.22: el REPL funciona (res1: kotlin.Int = 42)" || fail "REPL 1.9.22" "res1: kotlin.Int = 42" "$out"
else
  echo "  - 1.9.22 no instalado (sdk install kotlin 1.9.22): se salta"
fi
k21="$HOME/.sdkman/candidates/kotlin/2.1.21/bin/kotlinc"
if [[ -x "$k21" ]]; then
  out="$(printf '1+1\n:quit\n' | "$k21" 2>&1)"
  [[ "$out" == *"this REPL implementation is deprecated"* && "$out" == *"using '-language-version 1.9'"* ]] \
    && ok "2.1.21: deprecado y corre con -language-version 1.9 (K1)" || fail "REPL 2.1.21" "deprecado + 1.9" "$out"
else
  echo "  - 2.1.21 no instalado: se salta"
fi

echo "2. Expresiones"
expect "kotlin -e" "15" kotlin -e 'println((1..5).sum())'
expect "kotlinc -e" "ACME" kotlinc -e 'println("acme".uppercase())'

echo "3. Scripts .kts"
expect "kotlinc -script con argumentos" "promedio = 101.0" kotlinc -script closes.kts 100 102
expect "kotlin con un .kts" "cierres = [100.0, 101.5, 99.75]" kotlin closes.kts
cp vwap.main.kts plain.kts
expect_fail "un .kts común no conoce @file:DependsOn" "unresolved reference 'DependsOn'" kotlin plain.kts

echo "4. Scripts .main.kts"
mkdir -p cache
export KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR="$work/cache"
expect "@file:DependsOn baja la dependencia" "VWAP ACME = 101.5" kotlin vwap.main.kts
[[ -n "$(ls cache)" ]] && ok "la caché queda en KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR" || fail "caché" "un .jar en cache/" "vacía"
start=$(date +%s); kotlin vwap.main.kts >/dev/null 2>&1; secs=$(( $(date +%s) - start ))
(( secs <= 3 )) && ok "segunda corrida desde la caché (${secs} s)" || fail "segunda corrida" "≤ 3 s" "${secs} s"
expect "shebang: ./vwap.main.kts" "VWAP ACME = 101.5" ./vwap.main.kts
expect "@file:Import de otro script" "ACC-00001234-82  USD 1234.56" kotlin report.main.kts
expect "  …y la segunda cuenta" "ACC-00005678-39  USD 9.99" kotlin report.main.kts
sed 's/kotlinx-serialization-json:/kotlinx-serialization-json-jvm:/' vwap.main.kts > jvm.main.kts
expect "pedir el artefacto -jvm directo también funciona" "VWAP ACME = 101.5" kotlin jvm.main.kts
[[ -d "$HOME/.m2/repository/org/jetbrains/kotlinx/kotlinx-serialization-json/1.13.0" ]] \
  && ok "las dependencias quedan en ~/.m2/repository" || fail "dependencias" "~/.m2/repository/…/1.13.0" "no está"

echo
echo "Todo en orden: $pass verificaciones."
