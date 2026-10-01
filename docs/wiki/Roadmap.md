# Roadmap

All work is tracked as GitHub issues, grouped under four epics. Suggested build order: **Setup → API core → Sync → Admin screens → Ops**.

Issues: <https://github.com/Malcom-Yingwani/Forge-Admin/issues>

## Epic #1: Project setup

| # | Issue |
|---|---|
| #5 | Repo layout & local stack (docker compose: Postgres, MinIO, Mailpit) |
| #6 | Scaffold forge-api (Java 21, Spring Boot 3, Maven) |
| #7 | Scaffold forge-admin (React + Vite) with brand tokens |
| #8 | GitHub Actions CI for api and admin |

## Epic #2: forge-api

| # | Issue |
|---|---|
| #9 | Domain model, JPA entities & Flyway migrations |
| #10 | Authentication & role-based authorization (JWT + refresh cookie) |
| #11 | Admin content CRUD endpoints |
| #12 | Media uploads & storage (sermon MP3s, images, PDFs) |
| #13 | **Sync to Heritage:** transactional outbox, signed webhooks & retry dispatcher |
| #14 | Sync status, retry & full resync endpoints |
| #15 | Contact inbox: contact messages & lift requests from heritage-api |
| #17 | "Report an issue" endpoint |
| #18 | OpenAPI docs, ProblemDetail errors, audit log & conventions |

## Epic #3: forge-admin

| # | Issue |
|---|---|
| #19 | Login, password reset, auth guard & app shell |
| #20 | Dashboard: stat cards, sync health & upcoming items |
| #21 | Sermon management: sermons, series, preachers & MP3 upload |
| #22 | Events management |
| #23 | Pages, service times & site settings |
| #24 | People (Leadership & Office Staff), Ministries & Growth Groups |
| #26 | User & role management |
| #27 | Inbox (contact messages & lift requests) & "Report an issue" modal |
| #28 | Sync screen: outbox status, failed deliveries, retry & resync |
| #29 | Shared UI components |
| #33 | Blog management |
| #34 | Creeds and Confessions (documents) management |
| #35 | Giving funds management (ADMIN only) |

## Epic #4: Deployment, operations & data privacy

| # | Issue |
|---|---|
| #30 | Containerize & deploy forge-api and forge-admin |
| #31 | Backups, monitoring & alerting |
| #32 | POPIA: contact messages, staff accounts & published people |

## Milestones (suggested)

1. **M1: Skeleton.** #5–#8, #9, #10, #19
2. **M2: Sermons end-to-end.** #11, #12, #13, #21. A sermon published in Forge plays on the Heritage site, including during a Forge outage
3. **M3: All website content.** #14, #15, #20, #22–#24, #27–#29, #33–#35
4. **M4: Production.** #17, #18, #26, #30–#32

_#16 and #25 (member directory) were closed: there's no directory in this project._
