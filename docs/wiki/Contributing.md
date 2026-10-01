# Contributing

## Workflow

1. Pick an issue from the [Roadmap](Roadmap.md) and assign yourself.
2. Branch from `main`: `feat/<issue#>-short-name` or `fix/<issue#>-…`.
3. Use Conventional Commits: `feat(admin): sermon MP3 upload with progress (#14)`.
4. Open a PR that says `Closes #<n>`. CI must be green and one approval is needed. Use squash merge.

## Labels

| Label | Meaning |
|---|---|
| `epic` | Parent issue |
| `api` | forge-api (Java / Spring Boot) |
| `admin` | forge-admin (React) |
| `infra` | CI, Docker, hosting |
| `sync` | Forge ↔ Heritage contract. Needs a matching change in heritage-website |
| `privacy` | Touches member or personal data. Review against [POPIA](Data-Privacy-POPIA.md) |
| `design` | Brand and UI |
| `good first issue` | Small and well-scoped |

## Conventions

- **Java:** feature-first packages, `record` DTOs, constructor injection, `@PreAuthorize` on admin services, MockMvc + Testcontainers tests, and a security test for every new endpoint.
- **React:** use the `brand.css` tokens, accessible forms, and never trust the UI for authorization.
- **Personal data:** never log it, and never add it to a sync payload.
