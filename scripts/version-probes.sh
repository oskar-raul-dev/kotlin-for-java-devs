#!/usr/bin/env bash
# Mide en qué versión de Kotlin es estable cada construcción: compila un ejemplo mínimo con cada kotlinc instalado
# con SDKMAN y clasifica el resultado. Es la fuente de la "Referencia de versiones" de docs/Temario-Plan.md.
#
#   scripts/version-probes.sh                      # todas las pruebas, con todas las versiones instaladas
#   scripts/version-probes.sh guard multidollar    # solo esas
#   VERSIONS="2.1.21 2.4.21" scripts/version-probes.sh
#
# ok  = compila sin avisos · exp = pide habilitarla (-X…), opt-in, o "solo desde la versión N" · no = no compila
# Para agregar una construcción: una línea más en el bloque PROBES (nombre|código en una línea; \n para saltos).
# Compila en una carpeta temporal; no deja nada en el repo.
set -euo pipefail

candidates="${SDKMAN_DIR:-$HOME/.sdkman}/candidates/kotlin"
versions="${VERSIONS:-$(ls "$candidates" 2>/dev/null | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tr '\n' ' ')}"
[[ -n "$versions" ]] || { echo "No hay kotlinc instalados con SDKMAN en $candidates"; exit 1; }

PROBES='
data-object|sealed interface S; data object A : S
entries|enum class L { A, B }; fun f() = L.entries
range-until|fun f() = (0..<3).toList()
definitely-non-null|fun <T> f(x: T & Any) = x
subclass-opt-in|@RequiresOptIn annotation class Internal\n@SubclassOptInRequired(Internal::class) open class Base
enum-entries-fun|enum class L { A, B }\nfun f() = kotlin.enums.enumEntries<L>()
path-walk|import kotlin.io.path.walk\nfun f(p: java.nio.file.Path) = p.walk().toList()
non-local-break|fun f() { for (i in 1..3) { listOf(1).forEach { if (it == i) break } } }
guard|fun f(x: Any) = when (x) { is Int if x > 0 -> 1; else -> 0 }
multi-dollar|fun f(x: Int) = $$"precio: $${x} USD, literal: $x"
base64|fun f(b: ByteArray) = kotlin.io.encoding.Base64.encode(b)
hex-format|fun f(n: Int) = n.toHexString()
nested-typealias|class Market { typealias Ticker = String; fun t(): Ticker = "ACME" }
instant-clock|fun f() = kotlin.time.Clock.System.now()
context-parameters|context(log: StringBuilder) fun hi() { log.append("x") }
explicit-backing-fields|class Book { val orders: List<Int> field = mutableListOf(); fun add(o: Int) { orders.add(o) } }
uuid|fun f() = kotlin.uuid.Uuid.random()
context-receivers|class Log { fun i(s: String) = println(s) }\ncontext(Log) fun hi() { i("x") }
stdlib-atomics|fun f() = kotlin.concurrent.atomics.AtomicInt(0).incrementAndFetch()
jvm-expose-boxed|@JvmInline value class Cents @JvmExposeBoxed constructor(val v: Long)
'

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

printf '%-26s' "construcción"
for v in $versions; do printf '%-9s' "$v"; done
echo

while IFS='|' read -r name code; do
  [[ -z "$name" ]] && continue
  if [[ $# -gt 0 ]] && [[ " $* " != *" $name "* ]]; then continue; fi
  printf '%b\n' "$code" > "$work/$name.kt"
  printf '%-26s' "$name"
  for v in $versions; do
    out="$("$candidates/$v/bin/kotlinc" "$work/$name.kt" -d "$work/out-$v" 2>&1 | grep -E 'error:|warning:' | head -1 || true)"
    if [[ -z "$out" ]]; then r=ok
    elif echo "$out" | grep -qiE 'opt-in|experimental|enabled explicitly|only available since'; then r=exp
    else r=no; fi
    printf '%-9s' "$r"
  done
  echo
done <<< "$PROBES"
