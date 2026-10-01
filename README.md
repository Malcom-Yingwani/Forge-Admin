# Forge Admin

**Forge** is the content-management and member-directory platform for **Heritage Baptist Church, Johannesburg**. It owns the church's data and publishes website content to the [Heritage Website](https://github.com/Malcom-Yingwani/heritage-website).

| App | Folder | Stack |
|---|---|---|
| **forge-api**: source of truth, covering auth, content, media, member directory and sync to Heritage | `api/` | Java 21 · Spring Boot 3 · PostgreSQL |
| **forge-admin**: staff admin app and members-only directory | `admin/` | React · Vite |

## Documentation

The project wiki lives in [`docs/wiki/`](docs/wiki/Home.md):

- [System Architecture](docs/wiki/System-Architecture.md) · [Sync Protocol](docs/wiki/Sync-Protocol.md) · [Domain Model](docs/wiki/Domain-Model.md)
- [Forge API](docs/wiki/Forge-API.md) · [Admin App](docs/wiki/Admin-App.md)
- [Brand and Design System](docs/wiki/Brand-and-Design-System.md) · [Data Privacy (POPIA)](docs/wiki/Data-Privacy-POPIA.md)
- [Local Development](docs/wiki/Local-Development.md) · [Contributing](docs/wiki/Contributing.md) · [Deployment](docs/wiki/Deployment.md)
- [Roadmap](docs/wiki/Roadmap.md)

To publish these pages to the GitHub Wiki tab, run `scripts/publish-wiki.sh`.

## Status

Planning. The work is tracked in the [issues](https://github.com/Malcom-Yingwani/forge-admin/issues), grouped under epics.

> This repository is public. Never commit member data, real credentials or secrets. See [Data Privacy (POPIA)](docs/wiki/Data-Privacy-POPIA.md).
