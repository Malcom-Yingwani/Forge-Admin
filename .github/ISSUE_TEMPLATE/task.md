---
name: Task
about: A step in the build order. Fill every section so a developer can pick it up without asking.
title: "NN · [Area] Short description"
labels: []
---

> **Build order: Step NN of 60** · <repo>
> ⬅ Step NN-1: <owner/repo#n> · ➡ Step NN+1: <owner/repo#n>
> ⇄ **Paired change** (only if needed): build this together with Step X (<owner/repo#n>). Open the PRs together and merge them together.

## Context
Why this exists and where it fits. Link the wiki pages a newcomer should read first.

## What to build
The concrete deliverables: endpoints, tables, screens, files.

## How to build it
Step-by-step approach: packages and classes to create, libraries to use, the patterns to follow, and the gotchas.

## Example
A request/response, a JSON shape, a UI sketch or a code snippet.

## Acceptance criteria
- [ ] Checkable statements. The PR is done when all of these are true.

## How to test
The commands to run, and what to check by hand.

## Depends on
Earlier steps (with issue links) that must be merged first.

## Out of scope
What not to do here, and which step handles it.
