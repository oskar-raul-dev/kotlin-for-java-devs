#!/usr/bin/env bash
# Mide en qué versión de Kotlin es estable cada construcción: compila un ejemplo mínimo con cada kotlinc instalado
# con SDKMAN y clasifica el resultado. Es la fuente de la "Referencia de versiones" de docs/Temario-Plan.md.
#
#   scripts/version-probes.sh                      # todas las pruebas, con todas las versiones instaladas
#   scripts/version-probes.sh guard multidollar    # solo esas
#   VERSIONS="2.1.21 2.4.21" scripts/version-probes.sh
#   VERBOSE=1 scripts/version-probes.sh guard       # además, el primer aviso o error de cada versión
#
# ok  = compila sin avisos · exp = pide habilitarla (-X…), opt-in, o "solo desde la versión N"
# dep = compila con aviso de deprecación · dep! = deprecación con error (ya no compila) · no = no compila (u otro aviso)
# Para agregar una construcción: una línea más en el bloque PROBES (nombre|código en una línea; \n para saltos), con
# un tercer campo opcional de opciones de kotlinc (nombre|código|-jvm-target 16).
# Los kotlinc 1.4.x no arrancan en JDK 21: se corren con el primer JDK 11 de SDKMAN (o JAVA11_HOME).
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
fun-interface|fun interface Rule { fun ok(x: Int): Boolean }\nval positive = Rule { it > 0 }
array-deque|fun f() = ArrayDeque<Int>().apply { addLast(1) }.removeFirstOrNull()
sum-of|fun f(xs: List<Int>) = xs.sumOf { it.toLong() }
running-fold|fun f(xs: List<Int>) = xs.runningFold(0) { acc, x -> acc + x }
unsigned|fun f() = 1u + 2u
int-mod|fun f() = (-3).mod(2)
char-code|fun f(c: Char) = c.code
lowercase|fun f(s: String) = s.lowercase() + s.replaceFirstChar { it.uppercase() }
capitalize|fun f(s: String) = s.capitalize()
to-lower-case|fun f(s: String) = s.toLowerCase()
sealed-interface|sealed interface Order\nclass Limit : Order\nclass Market : Order
value-class|@JvmInline value class Cents(val v: Long)
inline-class|inline class Cents(val v: Long)
jvm-record|@JvmRecord data class Tick(val ticker: String, val price: Long)|-jvm-target 16
result-return|fun f(): Result<Int> = runCatching { 1 }
path-api|import kotlin.io.path.readText\nfun f(p: java.nio.file.Path) = p.readText()
path-delete-recursively|import kotlin.io.path.deleteRecursively\nfun f(p: java.nio.file.Path) = p.deleteRecursively()
readln|fun f() = readln()
build-list|fun f() = buildList { add(1); add(2) }
duration|import kotlin.time.Duration.Companion.minutes\nfun f() = 5.minutes
measure-time|fun f() = kotlin.time.measureTime { }
min-non-null|fun f(xs: List<Int>): Int = xs.min()
sealed-when-statement|sealed class S\nclass A : S()\nclass B : S()\nfun f(s: S) { when (s) { is A -> println() } }
range-until-comparable|fun f(a: java.time.LocalDate, b: java.time.LocalDate) = a..<b
regex-match-at|fun f(r: Regex) = r.matchAt("ACME", 1)
copy-visibility|data class Money private constructor(val cents: Long) { companion object { fun of(c: Long) = Money(c) } }\nfun f(m: Money) = m.copy(cents = 1)
contracts|import kotlin.contracts.*\nfun f(x: Any?) { contract { returns() implies (x != null) } }
'

java11="${JAVA11_HOME:-$(ls -d "${SDKMAN_DIR:-$HOME/.sdkman}"/candidates/java/11* 2>/dev/null | head -1)}"

work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

printf '%-26s' "construcción"
for v in $versions; do printf '%-9s' "$v"; done
echo

while IFS='|' read -r name code flags; do
  [[ -z "$name" ]] && continue
  if [[ $# -gt 0 ]] && [[ " $* " != *" $name "* ]]; then continue; fi
  printf '%b\n' "$code" > "$work/$name.kt"
  printf '%-26s' "$name"
  for v in $versions; do
    jh="${JAVA_HOME:-}"
    [[ "$v" == 1.4.* && -n "$java11" ]] && jh="$java11"
    # shellcheck disable=SC2086  # $flags se separa a propósito
    out="$(JAVA_HOME="$jh" "$candidates/$v/bin/kotlinc" "$work/$name.kt" -d "$work/out-$v" $flags 2>&1 | grep -E 'error:|warning:' | head -1 || true)"
    if [[ -z "$out" ]]; then r=ok
    elif echo "$out" | grep -qiE 'unresolved reference'; then r=no
    elif echo "$out" | grep -qiE 'opt-in|experimental|enabled explicitly|only available since'; then r=exp
    elif echo "$out" | grep -qiE 'deprecat'; then
      if echo "$out" | grep -q 'error:'; then r='dep!'; else r=dep; fi
    else r=no; fi
    [[ -n "${VERBOSE:-}" && -n "$out" ]] && r="$r($out)"
    printf '%-9s' "$r"
  done
  echo
done <<< "$PROBES"
