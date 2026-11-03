#!/usr/bin/env bash
# Respaldo de la base de datos interna en ./backups/
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p backups
f="backups/saiv-$(date +%Y%m%d-%H%M%S).sql.gz"
docker compose exec -T db pg_dump -U saiv -d saiv --clean --if-exists | gzip > "$f"
echo "Respaldo creado: $f"
