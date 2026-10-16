#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
if [[ -f "$root/.env" ]]; then
  set -a
  source "$root/.env"
  set +a
fi
cd "$root/api"
exec ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
