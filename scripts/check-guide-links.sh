#!/usr/bin/env bash
# Comprueba los enlaces externos de las guías de lección (docs/lessons) y de cualquier .md que se le pase.
#
#   scripts/check-guide-links.sh                       # todas las guías
#   scripts/check-guide-links.sh docs/lessons/01-language/01-basics.md
#
# 200-399 = ok. 401/403/429 = el sitio bloquea clientes automáticos (Cloudflare y similares): se listan aparte para
# revisarlos a mano en un navegador. Lo demás (404, 410, 5xx, sin respuesta) es un enlace roto: el script sale con 1.
set -uo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
files=("$@")
if [[ $# -eq 0 ]]; then   # sin mapfile: el bash de macOS es 3.2
  while IFS= read -r f; do files+=("$f"); done < <(find "$root/docs/lessons" -name '*.md' ! -name '_template.md' | sort)
fi

broken=0; blocked=0; ok=0
while read -r url; do
  code=$(curl -s -o /dev/null -L -m 20 -A "Mozilla/5.0 (link check)" -w "%{http_code}" "$url")
  case "$code" in
    2??|3??) ok=$((ok + 1)) ;;
    401|403|429) blocked=$((blocked + 1)); echo "BLOQUEADO $code $url" ;;
    *) broken=$((broken + 1)); echo "ROTO      $code $url" ;;
  esac
done < <(grep -ohE 'https?://[^ )>"]+' "${files[@]}" | sed 's/[.,;]$//' | sort -u)

echo "ok: $ok · bloqueados (revisar a mano): $blocked · rotos: $broken"
[[ $broken -eq 0 ]]
