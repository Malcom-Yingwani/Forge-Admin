#!/usr/bin/env bash
# Foreground app processes keep Ctrl+C reliable; use one WSL terminal per app.
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
if [[ -f .env ]]; then
  set -a
  source <(sed 's/\r$//' .env)
  set +a
fi
mkdir -p .local/logs
case "${1:-help}" in
  up)
    ./scripts/check-prerequisites.sh
    docker compose config --quiet
    # Check all host ports before starting any containers. Never stop other stacks.
    ports=()
    for mapping in "postgres:5432:${POSTGRES_PORT:-5433}" "mailpit:1025:${MAIL_PORT:-1026}" "mailpit:8025:${MAIL_UI_PORT:-8026}" "minio:9000:${MINIO_PORT:-9000}" "minio:9001:${MINIO_CONSOLE_PORT:-9001}"; do
      IFS=: read -r service internal external <<< "$mapping"
      if [[ $(docker compose port "$service" "$internal" 2>/dev/null || true) != "127.0.0.1:$external" ]]; then
        ports+=("$external")
      fi
    done
    node --input-type=module - "${ports[@]}" <<'JS'
import net from 'node:net';
for (const port of process.argv.slice(2).map(Number)) {
  const server = net.createServer();
  await new Promise((resolve, reject) => {
    server.once('error', () => reject(new Error(`Port ${port} is occupied. If Forge is already running, use docker compose ps; otherwise set a free port in .env (see local development docs).`)));
    server.listen(port, '127.0.0.1', () => server.close(resolve));
  });
}
JS
    docker compose up -d
    docker compose wait minio-init
    docker compose exec -T postgres pg_isready -U forge -d forge
    ;;
  api)
    exec > >(tee -a .local/logs/api.log) 2>&1
    exec ./scripts/run-api.sh
    ;;
  admin)
    [[ -x admin/node_modules/.bin/vite ]] || { echo 'Frontend dependencies missing; run scripts/setup.sh first.' >&2; exit 1; }
    if [[ -n ${PORT:-} && -z ${VITE_API_URL:-} ]]; then
      export VITE_API_URL="http://localhost:$PORT"
    fi
    exec > >(tee -a .local/logs/admin.log) 2>&1
    cd admin
    exec npm run dev -- --port "${ADMIN_PORT:-5174}"
    ;;
  stop) docker compose stop ;;
  status) docker compose ps -a ;;
  *) echo 'Usage: scripts/dev.sh {up|api|admin|stop|status}'; exit 1 ;;
esac
