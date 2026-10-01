# Forge Admin Wiki

**Forge** is where Heritage Baptist Church, Johannesburg manages everything: website content and sermon audio. It owns the data and pushes published content to the public [Heritage Website](https://github.com/Malcom-Yingwani/heritage-website).

| App | Folder | Stack | Purpose |
|---|---|---|---|
| **forge-api** | `api/` | Java 21 · Spring Boot 3 · PostgreSQL | Source of truth: staff auth, content CRUD, media uploads, sync to Heritage |
| **forge-admin** | `admin/` | React · Vite | Staff admin app |

## Pages

| Read this to… | Page |
|---|---|
| See how both repos fit together | [System Architecture](System-Architecture.md) |
| Understand the Forge → Heritage contract | [Sync Protocol](Sync-Protocol.md) |
| See the database entities | [Domain Model](Domain-Model.md) |
| Call or build forge-api | [Forge API](Forge-API.md) |
| Work on the admin UI | [Admin App](Admin-App.md) |
| Handle personal data correctly | [Data Privacy (POPIA)](Data-Privacy-POPIA.md) |
| Style the app | [Brand and Design System](Brand-and-Design-System.md) |
| Run it locally | [Local Development](Local-Development.md) |
| Contribute | [Contributing](Contributing.md) |
| Deploy | [Deployment](Deployment.md) |
| Find work | [Roadmap](Roadmap.md) |
| Look up a term | [Glossary](Glossary.md) |

## Roles

| Role | Can do |
|---|---|
| `ADMIN` | Everything, including users, roles and settings |
| `EDITOR` | Manage website content: sermons, events, pages, leaders, groups, ministries |
