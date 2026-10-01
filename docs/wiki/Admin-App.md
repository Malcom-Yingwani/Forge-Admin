# Admin App

`admin/`: React + Vite, deployed separately at `admin.heritagebaptist.co.za`. It's a private app, not indexed by search engines.

## Screens

| Screen | Role | Notes |
|---|---|---|
| Login, forgot and reset password | all | `.login-page` wallpaper with white overlay, dark header, gold ornament divider, scripture quote, show/hide password |
| Dashboard | ADMIN, EDITOR | Stat cards (sermons, events, members, families, unread messages, sync health), upcoming birthdays/anniversaries offcanvas |
| Sermons, Series, Preachers | ADMIN, EDITOR | Table with filters, a 2-column form grid, MP3 upload with progress and preview player, publish toggle |
| Events | ADMIN, EDITOR | Upcoming and past, recurrence |
| Pages, FAQs | ADMIN, EDITOR | Markdown editor with preview, drag to reorder FAQs |
| Leaders, Ministries, Growth Groups | ADMIN, EDITOR | Photo upload, ordering, public-contact flag |
| Service times, Site settings | ADMIN, EDITOR | Hero, address, socials |
| Member directory | all | Member cards (1/2/4 per row), family groups, search pill, role badges E/D/M, member modal, lightbox |
| Members & Families admin | ADMIN, EDITOR | Table with avatars, member form with phone country code and camera badge |
| My profile | MEMBER | Edit own details and photo, directory visibility |
| Inbox | ADMIN, EDITOR | Contact messages and issue reports |
| Sync | ADMIN | Outbox status, failed deliveries, retry, "Resync all" |
| Users | ADMIN | Invite, change role, disable |

## Shared components

Toast stack, lightbox, modal (dark header with Mustard border), confirm dialog, phone input, admin card, data table, empty state, and the "Report issue" button in the navbar. Styling and class names come from [Brand and Design System](Brand-and-Design-System.md).

## Auth flow

The access token is kept **in memory**, and the refresh token in an httpOnly `Secure` `SameSite=Strict` cookie. On a 401 the app calls `/api/auth/refresh` once and retries the request, and if that fails it sends you to login. Routes are guarded by role, and **the server enforces every rule regardless of the UI**.
