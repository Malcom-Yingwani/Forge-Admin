# Contributing

## Workflow

1. Take the **next step** in the [Build Order](Roadmap.md): steps run 1 → 60 across both repos. Assign yourself. For a **⇄ paired** step, also take its partner step in the other repo.
2. Branch from `main`: `feat/<issue#>-short-name` or `fix/<issue#>-…`.
3. Use Conventional Commits: `feat(admin): sermon MP3 upload with progress (#14)`.
4. Open a PR that says `Closes #<n>` (for a paired step, link the partner PR in the other repo). CI must be green and one approval is needed. Use squash merge.

## Labels

| Label | Meaning |
|---|---|
| `paired` | Must be built together with a step in the other repo |
| `epic` | Parent issue |
| `api` | forge-api (Java / Spring Boot) |
| `admin` | forge-admin (React) |
| `infra` | CI, Docker, hosting |
| `sync` | Forge ↔ Heritage contract. Needs a matching change in heritage-website |
| `privacy` | Touches personal data (staff accounts, contact messages). Review against [POPIA](Data-Privacy-POPIA.md) |
| `design` | Brand and UI |
| `good first issue` | Small and well-scoped |

## Conventions

- **Java:** feature-first packages, `record` DTOs, constructor injection, `@PreAuthorize` on admin services, MockMvc + Testcontainers tests, and a security test for every new endpoint.
- **React:** use the `brand.css` tokens, accessible forms, and never trust the UI for authorization.
- **Personal data:** never log it, and never add it to a sync payload.
