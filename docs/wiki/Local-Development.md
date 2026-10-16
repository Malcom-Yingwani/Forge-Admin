# Local Development

## Prerequisites

- Full JDK 21 (including `javac`), preferably Temurin. `api/mvnw` supplies checksum-verified Maven 3.9.16.
- Node 22.12+; `.nvmrc` selects Node 24 LTS locally and CI uses Node 22.
- Docker Engine with a working daemon and Compose v2. Tests start their own PostgreSQL 16 and Mailpit containers.
- Go 1.24+ (a genuine Go toolchain, not the GNU Go game), curl, tar, sha256sum and Linux CA certificates for the local MinIO/mc source builds.

## Install and start

From the repository root:

```bash
./scripts/setup.sh
docker compose up -d
docker compose wait minio-init
```

Setup uses `npm ci`, validates Compose, and runs `./mvnw verify`. It preserves existing environment files. Forge builds pinned local MinIO and mc images when absent; module checksums and source archive checksums remain enabled. The one-shot init service creates `forge-media` idempotently.

In separate terminals, from the repository root:

```bash
./scripts/run-api.sh
```

```bash
cd admin
npm run dev
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

Forge temporarily denies all requests except GET health endpoints until the auth task replaces this policy. Swagger remains denied even in dev.

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

Stop API/frontend terminals with Ctrl+C. `docker compose stop` and `docker compose up -d` retain data; `docker compose down` retains named volumes. Do not use `down --volumes` unless deleting local data intentionally. Older scaffold `postgres-data` volumes are preserved separately when switching to the issue-required PostgreSQL 16 `pgdata` volumes.

## MinIO maintenance

MinIO community edition is archived and source-only; its former community container images are unavailable. `scripts/build-minio.sh` builds the pinned upstream releases for server and mc with four workers, checks source archive SHA-256 values and preserves Go module verification. It is a local compatibility service, not a production storage recommendation. The init service uses local sample credentials `forge` / `forge-secret` and creates `forge-media` safely on repeated starts. Check readiness with `curl --fail http://127.0.0.1:9000/minio/health/live`.
