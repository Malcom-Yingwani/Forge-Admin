#!/usr/bin/env bash
# MinIO community images were withdrawn upstream. Build the pinned release locally.
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
command -v go >/dev/null || { echo "Go 1.24+ is required to build the optional S3 service" >&2; exit 1; }
go version >/dev/null
build_dir="$(mktemp -d)"
trap 'rm -rf "$build_dir"' EXIT
curl --fail --silent --show-error --location \
  https://codeload.github.com/minio/minio/tar.gz/refs/tags/RELEASE.2025-09-07T16-13-09Z \
  --output "$build_dir/source.tar.gz"
printf '%s  %s\n' c9598dcce3440977e79f787f2ba0e7e4d92c8d556bd51e7cef3785bafd6635f3 "$build_dir/source.tar.gz" | sha256sum --check --status
mkdir "$build_dir/source" "$build_dir/image"
tar -xzf "$build_dir/source.tar.gz" --strip-components=1 -C "$build_dir/source"
(
  cd "$build_dir/source"
  CGO_ENABLED=0 GOMAXPROCS="${GOMAXPROCS:-4}" go build -p "${GO_BUILD_PARALLELISM:-4}" -trimpath -o "$build_dir/image/minio" .
)
cp /etc/ssl/certs/ca-certificates.crt "$build_dir/image/ca-certificates.crt"
cp "$root/infra/minio/Dockerfile" "$build_dir/image/Dockerfile"
docker build --tag forge-minio:RELEASE.2025-09-07T16-13-09Z "$build_dir/image"
