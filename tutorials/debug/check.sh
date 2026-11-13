#!/usr/bin/env bash
# Verifica lo que el tutorial debug (README.md) dice: información de depuración, jdb, JDWP y corrutinas.
# Trabaja en una carpeta temporal. La primera corrida baja kotlinx-coroutines-debug de Maven Central (necesita red).
# La sesión de jdb se alimenta con pausas (jdb no espera al punto de parada): en una máquina lenta, subir PAUSE.
#
#   tutorials/debug/check.sh        (con kotlinc 2.4.x en el PATH: `sdk env` en la raíz del repo)
set -euo pipefail

here="$(cd "$(dirname "$0")" && pwd)"
root="$(cd "$here/../.." && pwd)"
ex="$here/examples"
work="$(mktemp -d)"
pids=()
cleanup() { for p in "${pids[@]:-}"; do [[ -n "$p" ]] && kill "$p" 2>/dev/null || true; done; rm -rf "$work"; }
trap cleanup EXIT
pause="${PAUSE:-1.5}"

pass=0
ok()   { pass=$((pass + 1)); echo "  ✓ $1"; }
fail() { echo "  ✗ $1"; echo "    esperado: $2"; echo "    obtenido: $3"; exit 1; }
has()  { [[ "$3" == *"$2"* ]] && ok "$1" || fail "$1" "$2" "$(echo "$3" | head -25)"; }
# wait_for <archivo> <texto> <segundos>: espera a que el archivo contenga el texto
wait_for() { for _ in $(seq 1 "$3"); do grep -q "$2" "$1" 2>/dev/null && return 0; sleep 1; done; return 1; }
free_port() { python3 -c 'import socket; s=socket.socket(); s.bind(("127.0.0.1",0)); print(s.getsockname()[1]); s.close()'; }

command -v kotlinc >/dev/null || { echo "kotlinc no está en el PATH (en la raíz del repo: sdk env)"; exit 1; }
version="$(kotlinc -version 2>&1)"; echo "$version"
[[ "$version" == *"kotlinc-jvm 2.4."* ]] || { echo "Este tutorial está escrito para Kotlin 2.4.x (sdk env en la raíz)"; exit 1; }
stdlib="$(dirname "$(dirname "$(readlink -f "$(command -v kotlinc)")")")/lib/kotlin-stdlib.jar"
[[ -f "$stdlib" ]] || { echo "no encuentro kotlin-stdlib.jar junto a kotlinc"; exit 1; }

echo "1. Información de depuración"
kotlinc "$ex/Fees.kt" -d "$work/fees" 2>/dev/null
cls="$work/fees/FeesKt.class"
has "kotlinc deja LineNumberTable" "LineNumberTable" "$(javap -l -p "$cls")"
has "kotlinc deja LocalVariableTable (variables con nombre)" "raw   Ljava/math/BigDecimal;" "$(javap -l -p "$cls")"
has "map inline: SMAP en SourceDebugExtension" "SourceDebugExtension" "$(javap -v "$cls")"
has "el SMAP apunta a _Collections.kt" "_Collections.kt" "$(javap -v "$cls")"
has "el SMAP: líneas 1-20 propias y la 21 es de _Collections.kt" '1#1,20:1\n1745#2:21' "$(javap -v "$cls")"
has "marcador de función inline: \$i\$f\$map" "\$i\$f\$map" "$(javap -l -p "$cls")"
has "marcador de lambda inline: \$i\$a\$-map-…" "\$i\$a\$-map-FeesKt\$main\$fees\$1" "$(javap -l -p "$cls")"
has "-Xdebug existe y guarda todas las variables al suspender" "spilling all variables in a suspending context" "$(kotlinc -X 2>&1)"

echo "2. jdb"
out="$(
  { sleep 3; for c in "stop at FeesKt:12" "run" "locals" "print result.toPlainString()" "print result.max(min)" "print amount * min" "where" "cont" "cont" "cont"; do
      echo "$c"; sleep "$pause"; done; } | timeout 90 jdb -classpath "$work/fees:$stdlib" FeesKt 2>&1 || true
)"
has "jdb se detiene en FeesKt.fee, línea 12" 'Breakpoint hit: "thread=main", FeesKt.fee(), line=12' "$out"
has "locals: el argumento amount" "amount = instance of java.math.BigDecimal" "$out"
has "locals: raw, min y result con su nombre Kotlin" "result = instance of java.math.BigDecimal" "$out"
has "print evalúa una expresión Java" 'result.toPlainString() = "1.00"' "$out"
has "print llama métodos: result.max(min)" 'result.max(min) = "1.00"' "$out"
has "print no entiende operadores de Kotlin" "Invalid operation '*' on an Object" "$out"
has "where: main en la línea 17" "FeesKt.main (Fees.kt:17)" "$out"
has "where: el main sintético no tiene línea" "FeesKt.main (null)" "$out"
has "el programa termina con las tres comisiones" "[1.00, 5.00, 1.00]" "$out"

echo "3. Depuración remota (JDWP)"
port="$(free_port)"
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=127.0.0.1:"$port" -cp "$work/fees:$stdlib" FeesKt > "$work/jdwp.log" 2>&1 &
pids+=("$!")
wait_for "$work/jdwp.log" "Listening for transport dt_socket" 20 && ok "java -agentlib:jdwp espera en el puerto $port" \
  || fail "JDWP" "Listening for transport dt_socket" "$(cat "$work/jdwp.log")"
attach="$( { sleep 2; echo "cont"; sleep 3; } | timeout 30 jdb -attach "127.0.0.1:$port" 2>&1 || true)"
wait_for "$work/jdwp.log" "1.00, 5.00, 1.00" 15 && ok "jdb -attach lo suelta (cont) y el programa termina" \
  || fail "jdb -attach" "[1.00, 5.00, 1.00] en la salida del programa" "$attach / $(cat "$work/jdwp.log")"
if [[ "${SKIP_GRADLE:-}" == "" ]]; then
  ( cd "$root" && timeout 120 ./gradlew -p 01-language run -Plesson=basics --debug-jvm --console=plain > "$work/gradle.log" 2>&1 ) &
  pids+=("$!")
  if wait_for "$work/gradle.log" "Listening for transport dt_socket at address: 5005" 90; then
    ok "./gradlew run --debug-jvm espera en el 5005"
  else
    fail "--debug-jvm" "Listening for transport dt_socket at address: 5005" "$(tail -5 "$work/gradle.log")"
  fi
  pkill -f "dev.raul.oskar.learning.kotlin.basics.MainKt" 2>/dev/null || true
else
  echo "  - SKIP_GRADLE: se salta ./gradlew run --debug-jvm"
fi

echo "4. Corrutinas"
cp="$(kotlin "$ex/deps.main.kts" 2>/dev/null | tail -1)"
[[ "$cp" == *"kotlinx-coroutines-debug-1.11.0.jar"* ]] && ok "deps.main.kts baja kotlinx-coroutines-debug y arma el classpath" \
  || fail "deps.main.kts" "un classpath con kotlinx-coroutines-debug-1.11.0.jar" "$cp"
debugjar="$(echo "$cp" | tr ':' '\n' | grep kotlinx-coroutines-debug)"
kotlinc "$ex/Feed.kt" -cp "$cp" -d "$work/feed" 2>/dev/null
run() { java "$@" -cp "$work/feed:$cp:$stdlib" FeedKt 2>&1 || true; }
has "sin opciones: el hilo es main" "feed corre en: main" "$(run)"
has "-Dkotlinx.coroutines.debug: nombre y número de la corrutina" "feed corre en: main @feed-ACME#2" "$(run -Dkotlinx.coroutines.debug)"
has "volcado sin el agente: no está instalado" "Debug probes are not installed" "$(run -Ddump)"
out="$(run -javaagent:"$debugjar" -Ddump)"
has "con -javaagent: volcado de corrutinas" "Coroutines dump" "$out"
has "feed suspendida en Feed.kt:14 (el delay)" "FeedKt\$main\$1\$feed\$1.invokeSuspend(Feed.kt:14)" "$out"
has "book suspendida en Feed.kt:17" "FeedKt\$main\$1\$book\$1.invokeSuspend(Feed.kt:17)" "$out"
has "estado SUSPENDED" "state: SUSPENDED" "$out"

echo
echo "Todo en orden: $pass verificaciones."
