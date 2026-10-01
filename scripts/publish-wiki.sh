#!/usr/bin/env bash
# Publishes docs/wiki/*.md to this repo's GitHub wiki.
# One-time setup: open the repo's Wiki tab on GitHub and create any first page,
# which initialises <repo>.wiki.git. Then run this from the repo root.
set -euo pipefail
remote="$(git remote get-url origin | sed -E 's/\.git$//').wiki.git"
tmp="$(mktemp -d)"
git clone "$remote" "$tmp"
find "$tmp" -maxdepth 1 -name '*.md' -delete
for f in docs/wiki/*.md; do
  # GitHub wiki links have no .md extension: (Page-Name.md) -> (Page-Name), keeping #anchors
  sed -E 's/\]\(([A-Za-z0-9_()-]+)\.md(#[^)]*)?\)/](\1\2)/g' "$f" > "$tmp/$(basename "$f")"
done
cd "$tmp"
git add -A
git commit -m "Sync wiki from docs/wiki ($(date -u +%Y-%m-%dT%H:%MZ))" || { echo "Wiki already up to date"; exit 0; }
git push
