#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
"$root/scripts/check-prerequisites.sh"
(cd "$root/api" && ./mvnw --batch-mode --no-transfer-progress verify)
(cd "$root/admin" && npm run lint && npm test && npm run build)
