# Forge API

`api/`: Spring Boot 3, Java 21, Spring Security, Spring Data JPA, Flyway, PostgreSQL, springdoc-openapi.

## Package layout

```
za.co.heritagebaptist.forge
├── ForgeApiApplication.java
├── config/     SecurityConfig, CorsConfig, OpenApiConfig, StorageConfig, SchedulingConfig
├── common/     BaseEntity, PageResponse, ProblemDetail handler, SlugService, AuditService
├── auth/       AppUser, AuthController, JwtService, RefreshTokenService, PasswordResetService
├── content/    sermon/, series/, preacher/, event/, page/, faq/, leader/, ministry/, group/, servicetime/, settings/
├── media/      MediaAsset, StorageService (LocalDiskStorage | S3Storage), ImageVariants, AudioMetadata
├── member/     Member, Family, MemberController, UpcomingService
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
/api/admin/leaders  /ministries  /growth-groups  /service-times
GET|PUT /api/admin/settings
POST /api/admin/media (multipart)   GET /api/admin/media   DELETE /api/admin/media/{id}
GET  /api/admin/dashboard
GET  /api/admin/contact-messages    PATCH /{id} (handled)   DELETE /{id}
GET  /api/admin/issues              PATCH /{id} (status)
GET  /api/admin/sync/status         POST /api/admin/sync/resync     POST /api/admin/sync/outbox/{id}/retry
/api/admin/users                    (ADMIN only)
/api/admin/members  /api/admin/families
```
Every collection supports `GET` (paged, `?q=&sort=`), `GET /{id}`, `POST`, `PUT /{id}` (with `version`), `DELETE /{id}`, and `POST /{id}/publish` and `/unpublish` where relevant.

### Members (any signed-in user)
```
GET  /api/members?q=&role=&family=&sort=
GET  /api/members/{id}
GET  /api/members/families
GET  /api/members/upcoming?days=14
GET|PUT /api/members/me      POST /api/members/me/photo
POST /api/issues
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
