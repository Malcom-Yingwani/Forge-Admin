#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
if [[ -f .env ]]; then
  set -a
  source <(sed 's/\r$//' .env)
  set +a
fi
api="http://127.0.0.1:${PORT:-8081}"
curl --fail --silent --show-error --retry 30 --retry-connrefused --retry-delay 1 --max-time 5 "$api/actuator/health" |
  node -e 'let s=""; for await (const c of process.stdin) s+=c; if(JSON.parse(s).status!=="UP") process.exit(1); console.log("API health: UP")' --input-type=module
[[ $(docker compose exec -T postgres psql -U forge -d forge -Atc "select count(*) from flyway_schema_history where success and version = '1'") == 1 ]]
echo 'Database: migration 1 applied'
for path in /api/admin/users; do
  [[ $(curl --silent --show-error --max-time 5 -o /dev/null -w '%{http_code}' "$api$path") == 403 ]]
done
echo 'Protected routes: expected 403'
if [[ "${OPENAPI_ENABLED:-true}" == true ]]; then
  curl --fail --silent --show-error --max-time 10 "$api/v3/api-docs" >/dev/null
  curl --fail --silent --show-error --max-time 10 "$api/swagger-ui/index.html" >/dev/null
  echo 'OpenAPI: documentation accessible'
fi
curl --fail --silent --show-error --max-time 10 "http://127.0.0.1:${ADMIN_PORT:-5174}" | node -e 'let s=""; for await (const c of process.stdin) s+=c; if(!s.includes("id=\"root\"")) process.exit(1); console.log("Frontend: HTML served (check rendered routes in a browser)")' --input-type=module
curl --fail --silent --show-error --max-time 5 "http://127.0.0.1:${MAIL_UI_PORT:-8026}/api/v1/info" >/dev/null
echo 'Mailpit: accessible'
curl --fail --silent --show-error --max-time 5 "http://127.0.0.1:${MINIO_PORT:-9000}/minio/health/live"
docker compose run --rm --no-deps --entrypoint /bin/sh minio-init -ec 'mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null && mc stat local/forge-media'
echo 'MinIO: forge-media ready'
