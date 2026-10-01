# Deployment

| Piece | Suggested host | Domain |
|---|---|---|
| forge-admin | Static host or CDN | `admin.heritagebaptist.co.za` |
| forge-api | Container (VPS with Docker + Caddy, or Render / Fly.io) | `forge-api.heritagebaptist.co.za` |
| Database | Managed Postgres, separate from Heritage's | — |
| Media | S3-compatible (R2 / S3 / MinIO) | — |

## Checklist

- [ ] HTTPS, HSTS, CSP; `X-Robots-Tag: noindex` on the admin app
- [ ] `JWT_SECRET`, `SYNC_SHARED_SECRET` and DB credentials in GitHub environment secrets
- [ ] First admin bootstrapped via env vars, then those vars removed
- [ ] Nightly encrypted `pg_dump` off-site, a media bucket with versioning, and a tested restore
- [ ] Uptime and Sentry alerts, with PII scrubbed
- [ ] Sync outbox alert if anything is pending for more than 30 minutes
- [ ] POPIA items in [Data Privacy](Data-Privacy-POPIA.md) done before launch
