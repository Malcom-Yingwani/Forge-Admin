# Foundation setup report

## Scope

The roadmap's foundation steps 1–8 cover the two repositories' local stacks, API scaffolds, frontend scaffolds, and CI: issues #5–#8 in **each** repository. This work implements that documented scope. Domain models, content CRUD, auth, media workflows, webhooks, production hosting, and application screens remain later tasks.

The repositories' live issue bodies could not be read because the environment blocks `api.github.com`. This report maps work to the checked-in roadmap; it does not certify unseen acceptance criteria. No issues have been closed and no pull requests have been created. The feature branch is `chore/foundation-setup` in each repository.

## What was implemented

| Roadmap steps | Issues in each repository | Implementation |
|---|---|---|
| 1 and 5 | #5 | Repo layout, Compose PostgreSQL and Mailpit, example environment configuration, setup/start helpers, runtime pin, development documentation. Forge also has an optional S3 profile and a checksum-verified MinIO source-build helper; its build remains blocked as described below. |
| 2 and 6 | #6 | Java 21 / Spring Boot 3 API; web, validation, JPA, Flyway/Postgres, Security, Actuator, and OpenAPI dependencies; full Maven wrapper; dev profile; baseline migration and database-backed integration tests. |
| 3 and 7 | #7 | React/TypeScript/Vite frontend; minimal placeholder page, identical brand tokens, environment example, strict port allocation, lint, type checking and production build. |
| 4 and 8 | #8 | GitHub Actions API verification, frontend frozen installation/lint/build, Compose validation; read-only permissions and concurrency cancellation. Hosted CI has not yet run because creating a PR requires the blocked GitHub API. |

## Decisions

- Keep Forge's write database and Heritage's read database independent, including distinct volumes. Neither scaffold API calls the other. The full content/media outage guarantee cannot be tested before those features exist.
- Use Java 21, Spring Boot **3.5.16** (remaining on the architecture's Boot 3 line), Maven **3.9.16**, and Node **24 LTS**. Use exact frontend package versions with committed npm lockfiles. Verify the Maven distribution checksum and retain normal TLS/package verification.
- Keep production database credentials explicit. Only the `dev` profile has documented local defaults and public Swagger/OpenAPI. Deny application routes until their auth/signature handling is implemented; do not seed fake users or invent a JWT or HMAC implementation.
- Give the stacks separate database/API/frontend ports as documented. Resolve the original Mailpit collision with Heritage SMTP/UI **1025/8025** and Forge **1026/8026**. Bind development ports to loopback.
- Use a no-op Flyway version 1 baseline to establish migration history without implementing domain schemas. JPA schema generation is disabled in favour of validation.
- Keep both `brand.css` files identical. Use the documented tokens and system font fallbacks; do not fabricate church logos or require external font downloads for startup.
- Keep MinIO optional because the initial API/frontend scaffold does not consume object storage. Upstream MinIO community edition is archived and source-only; the old container images are unavailable. A pinned upstream source archive plus Go's module verification provides a reproducible local compatibility build without disabling verification. This is not a production storage selection.

## Validation

Passed locally in both repositories:

- `scripts/setup.sh`: clean `npm ci`, Compose configuration validation, `./mvnw verify`, and packaged executable API.
- Four API tests each (**8 total, no failures or skips**) against actual PostgreSQL Testcontainers: healthy database and Flyway history, dev OpenAPI, denied unimplemented routes, configured/rejected CORS origins.
- Frontend lint, TypeScript check, and production bundle build.
- Both stacks running simultaneously; live API database-backed health and OpenAPI responses; both Mailpit mailbox APIs responding.
- Both frontend placeholder headings rendered in Chromium through Playwright, with no page errors.
- API restarts through `scripts/run-api.sh`, migration reuse, matching brand tokens, shell syntax checks, and local documentation-link checks.

Base-profile validation also checks health with explicit database credentials and denied documentation/application routes. The cloud instance has a full Temurin JDK extracted from a digest-pinned official image, plus Maven proxy configuration using the existing egress proxy and system Java truststore. Source `/workspace/.tools/activate.sh` in a new cloud shell.

## Remaining external prerequisites

- Save the environment draft's allowed domains `api.github.com` and `storage.googleapis.com`. The first enables issue criteria review, pull requests, and the requested issue closure. The second is the official Go proxy artifact host required by a cached MinIO console dependency, whose source repository is no longer public.
- The MinIO helper passed shell syntax validation and its source archive checksum was verified, but the source build failed at that blocked dependency download. No MinIO service or S3 functional check is claimed as passed. After network access is applied, run `scripts/build-minio.sh` and the S3 readiness steps in Forge's local-development guide.
- Reread issues #5–#8 in both repositories, address any additional acceptance criteria, run hosted CI through PRs, and close only the issues fully satisfied by the available changes.

The environment draft stores installation and startup instructions; saving it does not publish a filesystem snapshot. Review/save the draft and publish the prepared environment after resolving the desired remaining prerequisites.
