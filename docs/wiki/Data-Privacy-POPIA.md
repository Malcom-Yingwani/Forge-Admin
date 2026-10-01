# Data Privacy (POPIA)

Forge stores personal information about South African data subjects, so the **Protection of Personal Information Act (POPIA)** applies. Church members' names, phone numbers, birthdays, anniversaries, photos and family relationships are all personal information.

## Principles applied

| POPIA condition | What we do |
|---|---|
| Accountability | The church appoints and registers an **Information Officer** with the Information Regulator |
| Processing limitation | Collect only what the directory needs. Get consent when a member is added (`consent_at`) |
| Purpose specification | Directory data is for church fellowship only. It's never synced to the public website, never sold, never exported in bulk |
| Further processing | No marketing use |
| Information quality | Members can edit their own profile |
| Openness | A privacy notice on the website and in the admin app |
| Security safeguards | TLS everywhere, encrypted backups, BCrypt passwords, role-based access, audit log on member data, login lockout |
| Data subject participation | Members can view, correct and request deletion of their data, which an admin actions within 30 days |

## Technical rules

- Member data is served **only** under `/api/members/**` and `/api/admin/members/**`, and only to signed-in users.
- `directory_visible = false` hides a member from everyone but admins.
- No personal data in logs, error reports (scrub Sentry) or sync payloads.
- Contact messages are deleted 12 months after being handled.
- If there's a breach, notify the Information Regulator and affected members as soon as reasonably possible. The procedure is kept in the church's operations notes.
