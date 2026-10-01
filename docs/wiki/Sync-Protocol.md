# Sync Protocol (Forge → Heritage)

This is the contract between **forge-api** (sender) and **heritage-api** (receiver). Both repos keep an identical copy of this page, and any change to it needs a matching PR in **both** repos.

## Endpoints on heritage-api

All endpoints are under `/internal/sync/**`. They aren't exposed through the public CDN path and require a valid signature.

| Method | Path | Body | Purpose |
|---|---|---|---|
| `POST` | `/internal/sync/events` | `SyncEvent` (JSON) | Upsert or delete one content entity |
| `PUT` | `/internal/sync/media/{key}` | raw bytes, `Content-Type`, `X-Content-SHA256` | Mirror one media file |
| `DELETE` | `/internal/sync/media/{key}` | — | Remove a mirrored file |
| `POST` | `/internal/sync/snapshot` | `{ "type": "sermon", "items": [ ... ] }` | Full replace of one content type (used by "Resync all") |
| `GET` | `/internal/sync/status` | — | Counts and last-applied event per type (shown in Forge Admin) |

## SyncEvent

```json
{
  "eventId": "0b9a3c8e-5d1f-4c1e-9f0a-2f6d2a1e7c11",
  "type": "sermon",
  "op": "UPSERT",
  "id": "8f2d…",
  "version": 7,
  "occurredAt": "2026-10-01T09:30:00+02:00",
  "payload": { "id": "8f2d…", "slug": "a-well-ordered-church", "title": "…", "published": true }
}
```

- `type` is one of `sermon`, `sermon-series`, `preacher`, `event`, `page`, `person`, `ministry`, `growth-group`, `blog-post`, `document`, `giving-fund`, `service-time`, `site-settings`.
- `op` is `UPSERT` or `DELETE`. **Unpublishing is sent as `DELETE`**, because Heritage only stores published content.
- **Idempotent:** Heritage records every applied `eventId` and returns `200` for duplicates without re-applying them.
- **Ordering:** Heritage ignores an event whose `version` is lower than the stored version for that `id`, and still returns `200`.
- `payload` is the **public DTO**, never the Forge entity. It never contains user accounts, contact messages or internal notes.

## Signing

Every request carries:

```
X-Forge-Timestamp: 1759304400
X-Forge-Signature: v1=<hex HMAC-SHA256(secret, timestamp + "." + rawBody)>
```

Heritage rejects a request with `401` if the signature doesn't match or the timestamp is more than 5 minutes away from its own clock. The secret is `SYNC_SHARED_SECRET`, set identically on both sides. To rotate it, Heritage accepts the current *and* the previous secret during the rollover.

## Sender guarantees (forge-api)

1. A content write and its `sync_outbox` row are committed in the **same database transaction** (transactional outbox).
2. A scheduled dispatcher delivers pending rows in order per entity, with exponential backoff (1s, 2s, 4s… capped at 15 min) and **no maximum attempt count**. Nothing is ever dropped.
3. Media is uploaded to Forge storage first, then a `media` outbox row is queued. Content referencing that media is only sent **after** the media delivery succeeds.
4. Forge Admin shows the queue (pending, failing, last error) and has a **Resync all** button that sends snapshots for every type.

## Receiver guarantees (heritage-api)

1. Each event is applied in its own transaction.
2. Responses: `2xx` = applied or already applied. `4xx` = permanently bad (Forge marks it *dead* and shows it in admin). `5xx` = retry.
3. heritage-api never calls forge-api to serve a public request.

## Reverse direction: contact messages and lift requests (Heritage → Forge)

`POST forge-api/internal/inbox/contact` with `category` = `GENERAL` or `LIFT_REQUEST`. Same signing scheme, with the header prefix `X-Heritage-`. Heritage writes the message to its own outbox and retries the same way until Forge accepts it, then purges its copy after the retention period.

## Local development

| Variable | heritage-api | forge-api |
|---|---|---|
| `SYNC_SHARED_SECRET` | `dev-secret-change-me` | `dev-secret-change-me` |
| `HERITAGE_SYNC_URL` | — | `http://localhost:8080/internal/sync` |
| `FORGE_INBOX_URL` | `http://localhost:8081/internal/inbox` | — |
