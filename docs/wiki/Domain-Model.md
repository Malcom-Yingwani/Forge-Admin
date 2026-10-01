# Domain Model

Forge's PostgreSQL database is the **source of truth**. Every table has `id UUID`, `created_at`, `updated_at` and `version` (optimistic locking plus the sync version).

```mermaid
erDiagram
  PREACHER ||--o{ SERMON : preaches
  SERMON_SERIES ||--o{ SERMON : contains
  MEDIA_ASSET ||--o{ SERMON : "audio/cover"
  APP_USER ||--o{ AUDIT_LOG : writes
  APP_USER ||--o{ ISSUE_REPORT : reports
```

## Website content (synced to Heritage when published)

| Entity | Key fields |
|---|---|
| `Preacher` | name, slug, title (Pastor/Elder/Guest), bio, photo |
| `SermonSeries` | title, slug, description, cover image, start/end date |
| `Sermon` | title, slug, preached_on, service (MORNING/EVENING/BIBLE_HOUR/OTHER), preacher, series, scripture_book (canonical), scripture_ref, summary, audio (MediaAsset), video_url, duration_seconds, published |
| `Event` | title, slug, starts_at, ends_at, location, description (md), cover, recurrence, published |
| `Page` | slug, title, body (md), section (ABOUT/NEW_HERE/MINISTRIES/OTHER), sort_order, published |
| `Faq` | question, answer (md), sort_order, published |
| `Leader` | name, office (PASTOR/ELDER/DEACON), bio, photo, sort_order |
| `Ministry` | name, slug, description, meeting info, wordmark style, sort_order |
| `GrowthGroup` | name, area, host, day_of_week, time, contact, contact_public, active |
| `ServiceTime` | name, day_of_week, time, notes, active |
| `SiteSettings` (singleton) | church name, address, lat/lng, email, phone, socials, hero headline/image/CTA, footer text |
| `MediaAsset` | storage_key, original_name, content_type, size, sha256, width/height, duration, variants |

## Private (never leaves Forge)

| Entity | Key fields |
|---|---|
| `AppUser` | email, password_hash (BCrypt), display_name, role (ADMIN/EDITOR), enabled, last_login_at |
| `RefreshToken` | user, token_hash, expires_at, revoked |
| `ContactMessage` | name, email, phone, subject, message, received_at, handled, handled_by |
| `IssueReport` | reporter, category (BUG/CONTENT/DATA/OTHER), description, page_url, user_agent, status |
| `AuditLog` | actor, action, entity_type, entity_id, at, diff |
| `SyncOutbox` | event_id, kind (EVENT/MEDIA/SNAPSHOT), type, entity_id, payload, attempts, next_attempt_at, last_error, status (PENDING/SENT/DEAD) |

## Rules

- A slug is unique per type, generated from the title and editable.
- Unpublishing or deleting content writes a `DELETE` outbox event. See [Sync Protocol](Sync-Protocol.md).
- Scripture books are stored as canonical names with a Bible-order index for sorting.
