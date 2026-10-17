# Local Development

## Prerequisites

- Full JDK 21 (including `javac`), preferably Temurin. `api/mvnw` supplies checksum-verified Maven 3.9.16.
- Node 22.12+; `.nvmrc` selects Node 24 LTS locally and CI uses Node 22.
- Docker Desktop with WSL integration enabled for your Ubuntu distro and Compose v2. Do not install a competing Linux daemon. Tests start their own PostgreSQL 16 and Mailpit containers.
- Go 1.24+ (a genuine Go toolchain, not the GNU Go game), curl, tar, sha256sum and Linux CA certificates for the local MinIO/mc source builds.

## Install and start

From the repository root:

```bash
./scripts/setup.sh
./scripts/dev.sh up
```

Setup uses `npm ci`, validates Compose, and runs `./mvnw verify`. It preserves existing environment files. Forge builds pinned local MinIO and mc images when absent; module checksums and source archive checksums remain enabled. The one-shot init service creates `forge-media` idempotently.

In separate terminals, from the repository root:

```bash
./scripts/dev.sh api
```

```bash
./scripts/dev.sh admin
```

| Service | Local port |
|---|---|
| API | 8081 |
| Frontend | 5174 |
| PostgreSQL | 5433 |
| Mailpit SMTP / UI | 1026 / 8026 |
| MinIO S3 / console | 9000 / 9001 |

Development ports bind to loopback, with strict frontend port checking. Both stacks run simultaneously without port collisions. Each database has its own persistent `pgdata` volume and db/user/password `forge` (local examples only). The frontend contains title-only route placeholders, not real screens.

## Configuration

Optionally copy `.env.example` to `.env`, and `admin/.env.example` to `admin/.env`, only if those files do not already exist. The API helper exports the root `.env`; Compose reads it too. A manual Java/Maven command does not load `.env` automatically. Vite reads the frontend file.

Base configuration reads settings from environment variables; the `dev` profile supplies documented local values and includes `db/dev` for later seed migrations. There are no seeded users or content. Set production credentials securely; never reuse the local examples. `POSTGRES_PASSWORD` and `DB_PASSWORD` must agree. Initialization settings only affect a fresh volume and do not rotate an existing database password.

Forge temporarily denies application routes until the auth task replaces this policy. Health endpoints are public. Swagger is available at /swagger-ui.html in dev; OPENAPI_ENABLED overrides documentation availability and defaults to false outside dev.

The API client scaffold uses `VITE_API_URL`; requests include cookies and an in-memory access token, time out after 30s, and refresh/retry once after a 401. This is a client stub, not a login implementation.

`shared/brand.css` must remain byte-for-byte identical across the repos. Vite aliases `@brand` to it. Global styles use its tokens and Google Fonts; the temporary geometric favicon is not a church-logo recreation.

## Checks

```bash
curl --fail http://127.0.0.1:8081/actuator/health
cd api && ./mvnw --batch-mode --no-transfer-progress verify
```

Health must report `UP`, including database and mail connectivity. API tests use isolated PostgreSQL and Mailpit containers and are not skipped without Docker. Flyway establishes version 1 without creating domain tables; JPA validates rather than generating schema.

From the root:

```bash
cd admin
npm run lint
npm test
npm run build
```

Lint uses oxlint and ESLint. Vitest mocks fetch to test the API client; real auth/content endpoints remain out of scope. All site-map/screen routes render only their titles, with a 404 fallback. Google Fonts may require working Internet access; local fallbacks remain available.

Build the non-root API image from the root with `docker build -t forge-api api/`. In the Codex cloud, source `/workspace/.tools/activate.sh`; for image builds requiring the supplied egress proxy, pass BuildKit secrets `--secret id=maven_settings,src=/workspace/.cache/maven/settings.xml --secret id=java_truststore,src=/etc/ssl/certs/java/cacerts`. These mounts are not retained in image layers. API images bind to all container interfaces; the host application defaults to loopback.

The independent `api.yml` and `admin.yml` CI workflows filter paths and run verification on PRs and pushes to main; API failures upload Surefire reports. The repo owner configures main protection and required `api` / `admin` checks after they exist.

## Stop and restart

Stop API/frontend terminals with Ctrl+C. App output is also appended to ignored `.local/logs/api.log` and `.local/logs/admin.log`. Restart with `./scripts/dev.sh api` and `./scripts/dev.sh admin`. Use `./scripts/dev.sh status` to inspect infrastructure and `./scripts/dev.sh stop` to stop only Forge containers. `docker compose stop` and `docker compose up -d` retain data; `docker compose down` retains named volumes. Do not use `down --volumes` unless deleting local data intentionally. Older scaffold `postgres-data` volumes are preserved separately when switching to the issue-required PostgreSQL 16 `pgdata` volumes.

## MinIO maintenance

MinIO community edition is archived and source-only; its former community container images are unavailable. `scripts/build-minio.sh` builds the pinned upstream releases for server and mc with four workers, checks source archive SHA-256 values and preserves Go module verification. It is a local compatibility service, not a production storage recommendation. The init service uses local sample credentials `forge` / `forge-secret` and creates `forge-media` safely on repeated starts. Check readiness with `curl --fail http://127.0.0.1:9000/minio/health/live`.

## Windows / WSL setup

Run commands in WSL, as your normal user, from the repository root. Install missing system packages using Ubuntu's signed repositories:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk curl unzip ca-certificates
# If needed, Ubuntu's golang-go package must supply Go 1.24 or later.
# Alternatively install an official https://go.dev/dl/ Linux archive in your
# user directory after checking its SHA-256 against the official metadata.
source "$HOME/.nvm/nvm.sh"
nvm install
nvm use
./scripts/check-prerequisites.sh
```

Use the existing nvm installation; never run npm or project scripts with sudo. For a new nvm installation, follow the official https://github.com/nvm-sh/nvm instructions. Keep TLS validation enabled for all downloads. The Maven wrapper, MinIO archives, and Go modules already retain checksum verification. `setup.sh` installs locked npm dependencies, builds missing MinIO images, and verifies the API. `./scripts/verify.sh` repeats Maven verification plus frontend lint, tests, and production build.

Docker Desktop must be running on Windows. In Settings → Resources → WSL Integration, enable this distro. `docker info` should identify Docker Desktop and `docker compose version` should succeed. A sandboxed agent may need approval to access the Docker socket; that does not imply Docker needs reinstalling.

## Port conflicts and local overrides

`dev.sh up` checks infrastructure ports before starting containers and reports conflicts without stopping other services. It also accepts containers already owned by this Compose project. Preserve Heritage's PostgreSQL mapping at **15432**. Forge defaults to **5433** and its other defaults are separate from Heritage. Check Windows listeners too if Docker reports a bind failure (`Get-NetTCPConnection -State Listen` in PowerShell).

Keep local overrides in the ignored root `.env`; edit only needed values and preserve existing files. Supported host port settings are `POSTGRES_PORT`, `MAIL_PORT`, `MAIL_UI_PORT`, `MINIO_PORT`, and `MINIO_CONSOLE_PORT`. `PORT` changes the API port and `ADMIN_PORT` changes the frontend port. For example, to use PostgreSQL 5434, add `POSTGRES_PORT=5434`. The API helper derives `DB_URL` from this port unless an explicit `DB_URL` is already set; update an existing URL to match. Similarly it derives the S3 endpoint from `MINIO_PORT` and CORS origin from `ADMIN_PORT` unless explicitly configured. When `PORT` is set, the frontend helper defaults `VITE_API_URL` to that API port. Otherwise Vite retains its own environment configuration. Explicit root environment values take precedence over Vite environment files, so update any existing URLs consistently. Restart the apps after changes. Windows CRLF environment files are supported by the shell helpers.

Do not commit `.env`, local tools, logs, build outputs, or credentials. `dev.sh up` requires the local MinIO images produced by setup. Run `./scripts/smoke.sh` after both apps start to check infrastructure, migrations, API health, frontend HTML, and expected protected-route 401 responses. It does not substitute for a browser rendering check. Bucket initialization must exit zero; inspect it with `docker compose logs minio-init`. Check the bucket without exposing credentials:

```bash
docker compose run --rm --no-deps --entrypoint /bin/sh minio-init -ec \
  'mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD" >/dev/null && mc stat local/forge-media'
curl --fail http://localhost:8081/actuator/health
curl --fail http://localhost:8026/api/v1/info
curl --fail http://localhost:9000/minio/health/live
```

The public API health response intentionally summarizes status. Maven integration tests additionally query PostgreSQL's migration history. Protected application routes return **401** until staff authenticate. POST email/password to `/api/auth/login` using the bootstrap account configured in your environment. An EDITOR receives **403** on administrator-only routes. Swagger is accessible in dev at http://localhost:8081/swagger-ui.html. Open http://localhost:5174 on Windows to see the title-only scaffold. MinIO's console is http://localhost:9001 and Mailpit is http://localhost:8026.

## Media integration tests

Maven verification now requires the pinned local MinIO image. Run `./scripts/build-minio.sh server-only` if the image is missing; normal setup builds both server and client images. CI builds the same checksum-verified server source and runs the full suite, including a 100 MB HTTP upload with a 256 MB application heap. Testcontainers owns and removes its test buckets, database and containers; these tests do not use the development stack's media bucket.
