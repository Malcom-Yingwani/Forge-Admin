#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$root"
java -version
javac -version
node --version
docker compose config --quiet
(cd admin && npm ci)
(cd api && ./mvnw --batch-mode --no-transfer-progress verify)
