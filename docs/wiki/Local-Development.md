# Local Development

## Prerequisites

- Full JDK 21 (including `javac`), preferably Temurin. A Java runtime alone cannot compile the API.
- Node 24 LTS, pinned in `.nvmrc`; Node 22.12+ is supported.
- Docker Engine and Docker Compose v2, with a working daemon. API tests use real PostgreSQL through Testcontainers.
- Maven is supplied by `api/mvnw` / `api/mvnw.cmd`, pinned to 3.9.16 with distribution checksum verification.

## First setup

From the repository root, run `./scripts/setup.sh`. It installs frontend dependencies with `npm ci`, validates Compose, and runs the API tests and package build. No real credentials or external services are needed. It leaves existing environment files unchanged.

The API uses Java 21 and Spring Boot 3.5.16, PostgreSQL 17, JPA, Flyway, validation, Spring Security, Actuator, and springdoc. The frontend uses React, TypeScript, and Vite. Both repositories carry identical `brand.css` tokens; update both copies together.

## Run

Run the infrastructure from the repository root:

```bash
docker compose up -d --wait
```

In separate terminals, from the repository root:

```bash
./scripts/run-api.sh
```

```bash
cd admin
npm run dev
```

The API runs on port 8081; the frontend runs on 5174 with strict port checking. Services bind to loopback by default. No login or content UI is implemented yet.

| Service | Local port | Purpose |
|---|---|---|
| PostgreSQL | 5433 | Separate `forge` database and persistent named volume |
| Mailpit SMTP | 1026 | Local mail capture for later mail features |
| Mailpit UI | 8026 | Inspect local messages |

Forge and Heritage can run together without port collisions. Neither API needs the other to start.

## Configuration

Defaults in `application-dev.yml` match Compose and are **local development only**. Base configuration requires explicit database credentials, leaves API documentation disabled, and denies application routes until their security is implemented. Health is public; Swagger and OpenAPI are available in the `dev` profile only. There is no seeded admin or content yet.

Optional overrides:

```bash
# Run only when these files do not already exist.
cp .env.example .env
cp admin/.env.example admin/.env
```

`run-api.sh` loads the root `.env`; Compose also reads it. A manually invoked Maven or Java process does not automatically load it. Vite loads `admin/.env`. `VITE_API_URL` reserves the API base URL for future client requests; the placeholder page makes no API calls.

When changing database credentials, keep `POSTGRES_PASSWORD` and `DB_PASSWORD` aligned. PostgreSQL initialization variables apply only to a fresh volume: changing them does not rotate an existing database password. Never use the sample values in production. Mail settings are reserved for the later mail implementation.

## Readiness and checks

```bash
curl --fail http://127.0.0.1:8081/actuator/health
curl --fail http://127.0.0.1:8081/v3/api-docs
cd api && ./mvnw --batch-mode --no-transfer-progress verify
```

The health response must have `status: UP`, including a working database connection. API tests run against an isolated PostgreSQL container and verify Flyway baseline history, health, OpenAPI, denial of unimplemented routes, and allowed/rejected CORS origins. They fail if Docker or the database is unavailable; they are not silently skipped. The baseline migration deliberately creates no domain tables; those belong to later roadmap steps.

From the repository root:

```bash
cd admin
npm run lint
npm run build
```

CI runs the same API and frontend checks, plus Compose validation on pull requests and pushes to `main`. GitHub-hosted runners supply Docker for Testcontainers. The workflow has read-only repository permissions.

## Stop and restart

Stop frontend and API terminal processes with Ctrl+C. From the root, `docker compose stop` retains data; `docker compose up -d --wait` restarts services. `docker compose down` removes containers and networking but retains named volumes. Avoid `down --volumes` unless you intend to delete all local data.

## Running both repositories

Use their existing sibling checkouts. The future sync sender/receiver, HMAC secrets, content models, auth, and media features are not implemented by the scaffold. Do not supply production secrets or claim sync/outage tests pass yet. Once those roadmap tasks land, configure the same `SYNC_SHARED_SECRET` on both APIs and the documented sync endpoints.

## Optional local S3 service

The default scaffold uses no object storage. Later media work can choose local disk or S3 as documented in the architecture. MinIO is behind the `s3` Compose profile so its build is not required for API/frontend work.

MinIO community edition is archived and now source-only; former public container images are unavailable. The helper builds release `RELEASE.2025-09-07T16-13-09Z` from its upstream source archive, checks its pinned SHA-256, preserves Go module checksum verification, and builds a local static container. It requires a real Go 1.24+ toolchain, curl, tar, sha256sum, Linux CA certificates, and Docker. Build parallelism defaults to four workers. It is a local compatibility service, not a production storage recommendation.

```bash
./scripts/build-minio.sh
docker compose --profile s3 up -d --wait
curl --fail http://127.0.0.1:9000/minio/health/live
```

The S3 API is on 9000, the console on 9001, both loopback-only, with the sample local credentials in `.env.example`. The helper image persists; its container must be restarted after a cloud snapshot. `docker compose --profile s3 down` removes this optional container as well.
