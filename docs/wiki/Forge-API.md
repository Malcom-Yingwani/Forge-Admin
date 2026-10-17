# Forge API

`api/`: Spring Boot 3, Java 21, Spring Security, Spring Data JPA, Flyway, PostgreSQL, springdoc-openapi.

## Package layout

```
za.co.heritagebaptist.forge
├── ForgeApiApplication.java
├── config/     SecurityConfig, CorsConfig, OpenApiConfig, StorageConfig, SchedulingConfig
├── common/     BaseEntity, PageResponse, ProblemDetail handler, SlugService, AuditService
├── auth/       AppUser, AuthController, JwtService, RefreshTokenService, PasswordResetService
├── content/    sermon/, series/, preacher/, event/, page/, faq/, person/, ministry/, group/, blog/, document/, giving/, servicetime/, settings/
├── media/      MediaAsset, StorageService (LocalDiskStorage | S3Storage), ImageVariants, AudioMetadata
├── inbox/      ContactMessage, InboxController (/internal/inbox/**, signed)
├── issue/      IssueReport
└── sync/       SyncOutbox, OutboxWriter, OutboxDispatcher (@Scheduled), HeritageClient, SnapshotService
```

## Endpoints

### Auth
```
POST /api/auth/login             { email, password } → { accessToken, user } + httpOnly refresh cookie
POST /api/auth/refresh           → new access token
POST /api/auth/logout
GET  /api/auth/me
POST /api/auth/forgot-password   { email }
POST /api/auth/reset-password    { token, password }
```

### Admin (ADMIN, EDITOR)
```
/api/admin/sermons  /sermon-series  /preachers  /events  /pages  /faqs
/api/admin/people  /ministries  /growth-groups  /blog-posts  /documents  /giving-funds  /service-times
GET|PUT /api/admin/settings
POST /api/admin/media (multipart)   GET /api/admin/media   DELETE /api/admin/media/{id}
GET  /api/admin/dashboard
GET  /api/admin/contact-messages?category=GENERAL|LIFT_REQUEST    PATCH /{id} (handled)   DELETE /{id}
GET  /api/admin/issues              PATCH /{id} (status)
GET  /api/admin/sync/status         POST /api/admin/sync/resync     POST /api/admin/sync/outbox/{id}/retry
/api/admin/users                    (ADMIN only)
```
Every collection supports `GET` (paged, `?q=&sort=`), `GET /{id}`, `POST`, `PUT /{id}` (with `version`), `DELETE /{id}`, and `POST /{id}/publish` and `/unpublish` where relevant.

### Any signed-in user
```
GET|PUT /api/auth/me          own name, email, password
POST /api/issues              report an issue
```

### Internal (HMAC-signed, from heritage-api)
```
POST /internal/inbox/contact
```

## Errors

RFC 7807 `ProblemDetail`. Validation errors include `errors: [{ field, message }]`. Version conflicts return `409`.

## Configuration

| Env var | Purpose |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | Database |
| `JWT_SECRET` | Access-token signing |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Bootstraps the first admin when there are no users |
| `MEDIA_STORAGE` = `local`\|`s3`, `MEDIA_DIR`, `MEDIA_S3_*` | Media storage |
| `HERITAGE_SYNC_URL`, `SYNC_SHARED_SECRET` | Push to Heritage |
| `CORS_ORIGINS` | `http://localhost:5174` in dev |
| `MAIL_*` | Password resets, notifications |

## Staff authentication

Access tokens are HS256 JWTs with a 15-minute lifetime. `JWT_SECRET` must contain at least 32 UTF-8 bytes. Refresh tokens are random 256-bit values stored only as SHA-256 hashes. The httpOnly `refresh_token` cookie is scoped to `/api/auth`, lasts seven days, and rotates on every refresh. Reuse of a revoked token commits revocation of every session for that staff account. Concurrent refreshes are serialized using a per-user database lock; clients should share one refresh request.

`REFRESH_COOKIE_SECURE` defaults to true (false in the dev profile). Set `PASSWORD_RESET_URL` to the admin reset page, and `MAIL_FROM` to your sender address. Reset links expire after one hour, work once, and revoke refresh sessions. New passwords must contain 12�72 characters. Profile changes require `currentPassword`. Bootstrap creates the first enabled ADMIN only when the user table is empty and both bootstrap environment values are supplied; remove those values after first production startup.

Failed logins are limited to five per normalized email and twenty per remote IP in fifteen minutes. A blocked request returns 429 and `Retry-After`. The limiter is local to each API instance. Requests do not trust forwarded IP headers. CORS allows credentials only from configured origins. Internal routes are reserved for the separately implemented HMAC endpoints.
