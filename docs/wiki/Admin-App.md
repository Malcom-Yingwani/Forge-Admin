# Admin App

`admin/`: React + Vite, deployed separately at `admin.heritagebaptist.co.za`. It's a private app, not indexed by search engines.

## Screens

| Screen | Role | Notes |
|---|---|---|
| Login, forgot and reset password | ADMIN, EDITOR | `.login-page` wallpaper with white overlay, dark header, gold ornament divider, scripture quote, show/hide password |
| Dashboard | ADMIN, EDITOR | Stat cards (sermons, events, unread messages, sync health), next services and upcoming events |
| Sermons, Series, Preachers | ADMIN, EDITOR | Table with filters, a 2-column form grid, MP3 upload with progress and preview player, publish toggle |
| Events | ADMIN, EDITOR | Upcoming and past, recurrence |
| Pages | ADMIN, EDITOR | Markdown editor with preview (History, 1689 Confession, Potchefstroom, Bible Hour, Do you need a lift?) |
| People (Leadership, Office Staff) | ADMIN, EDITOR | Photo upload, ordering, category, public email |
| Ministries, Growth Groups | ADMIN, EDITOR | Rich description, image, public-contact flag |
| Blog | ADMIN, EDITOR | Posts with markdown editor, cover image, tags, schedule or publish |
| Creeds and Confessions | ADMIN, EDITOR | Documents with text and/or PDF upload, category, ordering |
| Giving funds | ADMIN | Bank details, reference, SnapScan link/QR, target and amount raised (progress bar) |
| Service times, Site settings | ADMIN, EDITOR | Hero, address, socials |
| My account | ADMIN, EDITOR | Change own name, email and password |
| Inbox | ADMIN, EDITOR | Contact messages, **lift requests** (separate tab) and issue reports |
| Sync | ADMIN | Outbox status, failed deliveries, retry, "Resync all" |
| Users | ADMIN | Invite, change role, disable |

## Shared components

Toast stack, lightbox, modal (dark header with Mustard border), confirm dialog, admin card, data table, empty state, and the "Report issue" button in the navbar. Styling and class names come from [Brand and Design System](Brand-and-Design-System.md).

## Auth flow

The access token is kept **in memory**, and the refresh token in an httpOnly `Secure` `SameSite=Strict` cookie. On a 401 the app calls `/api/auth/refresh` once and retries the request, and if that fails it sends you to login. Routes are guarded by role, and **the server enforces every rule regardless of the UI**.
