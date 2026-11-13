#!/usr/bin/env bash
# Corre un track legacy de Kotlin dentro del contenedor con el JDK de su época.
#
# Uso:
#   ./scripts/legacy.sh <track> [tareas y opciones de Gradle…]    (sin tareas: test)
#   ./scripts/legacy.sh <track> shell                              (bash dentro del contenedor)
#
#   track: 14 (20-legacy-kotlin14) · 16 (21-legacy-kotlin16) · 19 (22-legacy-kotlin19)
#
# Ejemplos:
#   ./scripts/legacy.sh 19                      # ./gradlew test en 22-legacy-kotlin19, con JDK 21
#   ./scripts/legacy.sh 19 run --console=plain  # su Main
#   ./scripts/legacy.sh 19 shell                # explorar: java -version, ./gradlew --version…
#
# En nativo, sin Docker:  cd 22-legacy-kotlin19 && sdk env && ./gradlew test
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
compose="$root/docker/legacy-kotlin/compose.yaml"

usage() {
  sed -n '2,15p' "$0" | sed 's/^# \{0,1\}//'
  exit 2
}

[ $# -ge 1 ] || usage
track="$1"
shift

case "$track" in
  14) dir="20-legacy-kotlin14" ;;
  16) dir="21-legacy-kotlin16" ;;
  19) dir="22-legacy-kotlin19" ;;
  -h|--help) usage ;;
  *) echo "Track desconocido: '$track' (usa 14, 16 o 19)" >&2; exit 2 ;;
esac

if [ ! -x "$root/$dir/gradlew" ]; then
  echo "$dir todavía no tiene build ni wrapper (se crea en la fase B5 del plan)." >&2
  exit 1
fi

if [ "${1:-}" = "shell" ]; then
  exec docker compose -f "$compose" run --rm "k$track" bash
fi

[ $# -gt 0 ] || set -- test
exec docker compose -f "$compose" run --rm "k$track" ./gradlew "$@"
