# Brand and Design System

Sources: the **Heritage Baptist Church Style Guide** (Sharon Martin Design) and the `site.css` from the earlier HBC Directory project, reused here for its brand tokens and generic components only. Where the two disagree, the [decisions](#decisions) below say which one wins.

> The style guide's own advice: *"These are guidelines, not rules. Feel free to be creative, but not so creative that what you create no longer merges well with our other messages."*

## Colour palette

| Token | Name (style guide) | HEX | RGB | Pantone | UI role |
|---|---|---|---|---|---|
| `--color-primary` | Heritage **Mustard** | `#c69760` | 198 151 96 | P 22-4 C | **Primary accent**: buttons, active nav, focus rings, badges, borders under dark headers |
| `--color-primary-dark` | (derived) | `#a87d4a` | | | Hover/pressed state of primary |
| `--color-gold` | Heritage Gold | `#9a865f` | 154 134 95 | P 13-9 C | Links, subtitles, divider lines, ornaments |
| `--color-dark-gold` | Heritage Dark Gold | `#847153` | 132 113 83 | P 13-11 C | Small gold text on light backgrounds (passes contrast), badge text |
| `--color-beige` | Heritage Beige | `#c6b08d` | 198 176 141 | P 16-11 C | Secondary buttons, soft fills, login title |
| `--color-dark` | Heritage Dark Grey | `#202222` | 33 35 34 | 419 C | Navbar, footer, headings, body text, modal headers |
| `--color-olive` | Heritage Olive Green | `#808e6b` | 128 142 107 | P 178-8 C | Success states, accent backgrounds |
| `--color-slate` | Heritage Slate Blue | `#899cad` | 137 156 173 | P 174-4 C | Info states, accent backgrounds |
| `--color-light` | (derived) | `#f5f1eb` | | | Warm off-white for cards and sections |
| `--color-light-2` | (derived) | `#ede8df` | | | Borders, dividers |
| `--color-danger` | (UI only) | `#b91c1c` | | | Destructive actions, errors |

### Decisions

1. **Mustard is the primary UI colour.** The style guide makes Gold, Beige and Dark Grey the main colours for *graphics* and treats Mustard, Olive and Slate as extras. For the *websites*, Mustard works better as the interactive accent: it's warmer and more legible on the dark navbar. Gold, Beige and Dark Grey still carry the brand everywhere else: headings, dividers, textures and imagery.
2. **Contrast:** Mustard `#c69760` on white is about 2.5:1, which fails WCAG AA for text. Use Mustard for **fills, borders and text on dark backgrounds**. Use `--color-dark-gold` or `--color-dark` for small text on white. Buttons use Mustard fill with `--color-dark` text (≈ 7:1 ✔).
3. Olive and Slate are used sparingly (success and info states, occasional section backgrounds), as the style guide suggests.

## Typography

| Role | Font | Weights | Notes |
|---|---|---|---|
| Display / headings / nav / buttons / form labels | **Montserrat** (primary typeface) | 300, 400, 600, 700, 800 | Headings 700. Nav, buttons and labels are UPPERCASE with `letter-spacing: 0.06em`. Large hero and series titles use wide tracking (≈ 0.1–0.15em). |
| Body copy, long text | **Open Sans** (secondary typeface) | 300, 400, 600, 700 | `line-height: 1.6`. The style guide: "Open Sans is our preferred typeface for large bodies of text." |

```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400;600;700;800&family=Open+Sans:wght@300;400;600;700&display=swap" rel="stylesheet">
```

## Logo

- The tree logo: *"The tree of life points us to Christ who alone can satisfy and give everlasting life."*
- **Variations available:** black on white, white on black, gold on white, white on gold, black/white/gold on transparent, **tree ring** (circle), **tree tab** (gold tab hanging from the top edge, used on sermon-series graphics), Heritage Simplified, Heritage wordmark.
- **Clear space:** one x-height (the height of the "e" in "Heritage") on every side.
- **Misuse, do not:** rearrange or resize parts, pixelate, stretch, recreate, add effects (shadows or glows), recolour, put a dark logo on a dark background, or place it on busy or too-light images. Use the **white logo** on almost anything other than a white background.
- **Web usage:** white or gold logo in the dark navbar, tree ring as the favicon and app icon, and gold on white in the footer of light pages.
- Ask the church for the original SVG/PNG logo files. **Never** redraw the logo in CSS or SVG.

## Sub-ministry wordmarks

Montserrat Bold, UPPERCASE, wide tracking, two lines, with a short underline beneath (left-aligned or centred):
**Adult Bible Hour · School of the Bible · Heritage Ladies · Iron Men · Young Adults · Heritage Prayer Group · Heritage Baptist Lifts**

On the website, build these as a CSS component (`.ministry-mark`) rather than images, so they stay crisp and editable.

## Textures and patterns

- **Textures:** Stone, Gold, Subtle Marble (dark), plus light stone/marble. Use them as subtle section backgrounds (hero overlays, ministry cards, login page).
- **Patterns:** African-inspired borders and circular motifs, "a hint of African flavour as a nod to our location in South Africa". Use at **10–50% opacity** only, as subtle enhancement. Ship as SVG for the web: section dividers and ministry card backgrounds.

## Graphic elements

- Thin gold **line elements** to separate and frame content: a short underline under titles, and a full-width gold rule.
- **Frame boxes:** a thin gold outline rectangle inset over an image (used for event and conference graphics).
- **Sermon-series card (web):** full-bleed image → dark bottom gradient → UPPERCASE Montserrat title in white with wide tracking → short white or gold underline → gold **tree tab** in the top-right corner.

## Imagery

- Photos of places and elements: landscapes, mountains, sheep, fields, the church spire and building, Bibles, the congregation.
- Prefer **high-resolution images with large areas of open space**, which suit text overlays.
- Service graphics already exist: *Morning Service* (sunrise landscape), *Evening Service* (night sky), *Bible Hour* (bright landscape). Reuse them as hero and section images.

## Components (from `site.css`)

| Component | Classes | Used in |
|---|---|---|
| Fixed dark navbar with 3px Mustard bottom border, uppercase links, animated hamburger | `.navbar`, `.nav-link`, `.hamburger` | web, admin |
| Buttons | `.btn-primary` (Mustard fill, dark text), `.btn-outline-primary`, `.btn-outline-secondary`, `.btn-danger`, `.btn-logout` | web, admin |
| Pill search bar | `.search-bar-wrap`, `.search-icon-btn` | sermons archive, admin lists |
| Select with chevron | `.select-wrap` | filters |
| Warm and dark sections | `.section-warm`, `.section-dark` | web |
| Forms | `.form-control`, `.form-select`, `.form-label` (uppercase Montserrat) | web, admin |
| Alerts | `.alert-success` (olive tint), `.alert-danger` | web, admin |
| Modal with dark header and Mustard border | `.modal-header` | web, admin |
| Toasts | `.app-toast-*` | admin |
| Lightbox | `.app-lightbox` | web (galleries), admin (image previews) |
| Login page | `.login-page`, `.login-card`, `.login-header`, `.login-divider` | admin |
| Admin cards and stats | `.admin-card`, `.admin-stat-card`, `.admin-form-grid`, `.admin-list-item` | admin |
| Tables | `.table`, `.table-avatar` | admin |
| Empty state | `.empty-state` | web, admin |

> **Not carried over:** the member-directory styles in `site.css` (`.member-card`, `.badge-role-circle--*`, `.badge-family`, `.family-group*`, `.upcoming-*`, `.ring`) belong to the HBC Directory project. This platform has no member directory.

### Bugs in `site.css` to fix when porting

- `--shadow-xl` is used by `.login-card` but never defined.
- The toast styles use the undefined `--dir-mustard`, `--dir-dark` and `--dir-grey`. Map them to `--color-primary`, `--color-dark` and a grey token.
- `.report-issue-category-btn` uses `--color-light2`, a typo for `--color-light-2`.
- `header { height: 50px }` disagrees with the comment saying the header reserves 70px. Make the navbar height a single token, `--nav-height`.

## Tokens file

Both frontends import the same `brand.css`:

```css
:root {
  --color-primary: #c69760;  --color-primary-dark: #a87d4a;
  --color-gold: #9a865f;     --color-dark-gold: #847153;
  --color-beige: #c6b08d;    --color-dark: #202222;
  --color-olive: #808e6b;    --color-slate: #899cad;
  --color-white: #ffffff;    --color-light: #f5f1eb;  --color-light-2: #ede8df;
  --color-grey: #6b6f6f;     --color-danger: #b91c1c;
  --color-secondary: var(--color-beige); --color-accent: var(--color-gold);
  --color-success: var(--color-olive);   --color-info: var(--color-slate);
  --font-display: "Montserrat", sans-serif;
  --font-body: "Open Sans", sans-serif;
  --shadow-sm: 0 1px 4px rgba(32,34,34,.07);
  --shadow-md: 0 4px 16px rgba(32,34,34,.11);
  --shadow-lg: 0 10px 30px rgba(32,34,34,.16);
  --shadow-xl: 0 20px 50px rgba(32,34,34,.22);
  --nav-height: 64px;
  --transition: all .28s cubic-bezier(.4,0,.2,1);
}
```

The copies in `heritage-website` and `forge-admin` must stay identical. A change to one is a change to both.
