#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
./scripts/check-prerequisites.sh
docker compose config --quiet
if ! docker image inspect forge-minio:RELEASE.2025-09-07T16-13-09Z forge-mc:RELEASE.2025-08-13T08-35-41Z >/dev/null 2>&1; then
  ./scripts/build-minio.sh
fi
(cd admin && npm ci)
(cd api && ./mvnw --batch-mode --no-transfer-progress verify)
