# Data Privacy (POPIA)

There's **no member directory** on this platform, so the personal data Forge holds is small. POPIA (the Protection of Personal Information Act) still applies to it:

| Data | Where | Why we hold it |
|---|---|---|
| Staff accounts | Forge `app_user` | Logging in to Forge Admin |
| Contact form messages (name, email, optional phone, message) | Heritage briefly, then Forge inbox | Replying to enquiries |
| Names, photos and bios of pastors, elders and preachers | Public website | Published with the person's agreement |

## Rules

- **Consent for public people:** get the agreement of every leader or preacher before publishing their photo and bio.
- **Contact messages:** use them only to reply. Delete them from Forge 12 months after they're handled, and from Heritage once they're delivered to Forge (30 days at most).
- **Privacy notice:** a short notice under the contact form and in the site footer.
- **Security:** TLS everywhere, BCrypt passwords, login lockout, encrypted backups, and no personal data in logs or error reports (scrub Sentry).
- **Information Officer:** by default this is the church's head (the senior pastor or chair of the elders). Record who it is.
- **Breach:** notify the Information Regulator and the affected people as soon as reasonably possible.
- **Hosting outside South Africa** is allowed for this data when the provider offers adequate protection (e.g. EU-hosted with GDPR terms). Record the choice in [Deployment](Deployment.md).
