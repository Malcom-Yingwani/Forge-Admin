#!/usr/bin/env bash
set -euo pipefail
fail() { echo "$*" >&2; exit 1; }
for tool in java javac node npm docker curl unzip tar sha256sum; do
  command -v "$tool" >/dev/null || fail "Missing $tool; see docs/wiki/Local-Development.md"
done
[[ $(javac -version 2>&1) == 'javac 21.'* ]] || fail 'Select a full JDK 21 (JAVA_HOME and PATH).'
[[ $(java -version 2>&1 | head -1) == *'"21.'* ]] || fail 'Select JDK 21 for java as well as javac.'
[[ $(node -p 'process.versions.node.split(".")[0]') == 24 ]] || fail 'Run nvm install && nvm use from the repository root (Node 24).'
docker compose version >/dev/null || fail 'Enable Docker Desktop WSL integration and Compose v2.'
docker info >/dev/null 2>&1 || fail 'Docker daemon unavailable. Start Docker Desktop and enable integration for this WSL distro; do not install another daemon.'
if ! docker image inspect forge-minio:RELEASE.2025-09-07T16-13-09Z forge-mc:RELEASE.2025-08-13T08-35-41Z >/dev/null 2>&1; then
  command -v go >/dev/null || fail 'Missing Go 1.24+; add your Go installation to PATH.'
  version=$(go env GOVERSION)
  [[ $version =~ ^go([0-9]+)\.([0-9]+) ]] || fail "Unrecognized Go toolchain: $version"
  (( BASH_REMATCH[1] > 1 || BASH_REMATCH[2] >= 24 )) || fail 'Go 1.24+ is required.'
fi
echo 'Prerequisites ready.'
