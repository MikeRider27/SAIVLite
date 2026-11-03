#!/usr/bin/env bash
# Restaura un respaldo creado con backup.sh. Uso: scripts/restore.sh backups/saiv-XXXX.sql.gz
set -euo pipefail
cd "$(dirname "$0")/.."
[ -f "${1:-}" ] || { echo "Uso: $0 <archivo.sql.gz>"; exit 1; }
read -r -p "Esto reemplaza TODOS los datos actuales. ¿Continuar? (si/no) " r
[ "$r" = "si" ] || exit 1
docker compose stop backend
gunzip -c "$1" | docker compose exec -T db psql -q -U saiv -d saiv
docker compose start backend
echo "Restauración completa."
