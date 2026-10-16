#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
if [[ -f "$root/.env" ]]; then
  set -a
  source <(sed 's/\r$//' "$root/.env")
  set +a
fi
export DB_URL="${DB_URL:-jdbc:postgresql://localhost:${POSTGRES_PORT:-5433}/forge}"
export MEDIA_S3_ENDPOINT="${MEDIA_S3_ENDPOINT:-http://localhost:${MINIO_PORT:-9000}}"
export CORS_ORIGINS="${CORS_ORIGINS:-http://localhost:${ADMIN_PORT:-5174}}"
cd "$root/api"
exec ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
