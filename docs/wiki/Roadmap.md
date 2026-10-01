# Build Order

Work through the steps **in order, from 1 to 60**. The order runs across **both repos**, so a step may live in [Heritage-Website](https://github.com/Malcom-Yingwani/Heritage-Website/issues) or [Forge-Admin](https://github.com/Malcom-Yingwani/Forge-Admin/issues). Each issue title starts with its step number (`19 · …`), and each issue links to the previous and next step.

**⇄ Paired steps** need matching changes in both repos at the same time, because one side can't work without the other. Build them together, open both PRs together, and merge them together. They carry the `paired` label.

| Pair | Steps | What has to match |
|---|---|---|
| Sync | 19 ⇄ 20 ⇄ 21 | Forge's outbox/webhook sender, Heritage's receiver and its media mirror ([Sync Protocol](Sync-Protocol.md)) |
| Resync | 26 ⇄ 27 | Forge's resync trigger and Heritage's snapshot endpoint |
| Contact inbox | 45 ⇄ 46 | Heritage's contact/lift-request outbox and Forge's inbox receiver |

Other steps depend on earlier steps in the other repo (e.g. Heritage's read model, step 11, mirrors Forge's domain model, step 9), but they can be built one after the other.

## Phase 1: Foundations (both repos build and run)

| Step | Repo | Issue | Task |
|---|---|---|---|
| **1** | Heritage-Website | [#5](https://github.com/Malcom-Yingwani/Heritage-Website/issues/5) | Repo layout & local stack |
| **2** | Heritage-Website | [#6](https://github.com/Malcom-Yingwani/Heritage-Website/issues/6) | Scaffold heritage-api |
| **3** | Heritage-Website | [#7](https://github.com/Malcom-Yingwani/Heritage-Website/issues/7) | Scaffold heritage-web + brand tokens |
| **4** | Heritage-Website | [#8](https://github.com/Malcom-Yingwani/Heritage-Website/issues/8) | CI for api and web |
| **5** | Forge-Admin | [#5](https://github.com/Malcom-Yingwani/Forge-Admin/issues/5) | Repo layout & local stack |
| **6** | Forge-Admin | [#6](https://github.com/Malcom-Yingwani/Forge-Admin/issues/6) | Scaffold forge-api |
| **7** | Forge-Admin | [#7](https://github.com/Malcom-Yingwani/Forge-Admin/issues/7) | Scaffold forge-admin + brand tokens |
| **8** | Forge-Admin | [#8](https://github.com/Malcom-Yingwani/Forge-Admin/issues/8) | CI for api and admin |

## Phase 2: Data model, conventions, auth & admin shell

| Step | Repo | Issue | Task |
|---|---|---|---|
| **9** | Forge-Admin | [#9](https://github.com/Malcom-Yingwani/Forge-Admin/issues/9) | Domain model & migrations |
| **10** | Forge-Admin | [#18](https://github.com/Malcom-Yingwani/Forge-Admin/issues/18) | API conventions: OpenAPI, errors, audit |
| **11** | Heritage-Website | [#9](https://github.com/Malcom-Yingwani/Heritage-Website/issues/9) | Read-model schema & migrations |
| **12** | Forge-Admin | [#10](https://github.com/Malcom-Yingwani/Forge-Admin/issues/10) | Auth & roles |
| **13** | Forge-Admin | [#29](https://github.com/Malcom-Yingwani/Forge-Admin/issues/29) | Shared UI components |
| **14** | Forge-Admin | [#19](https://github.com/Malcom-Yingwani/Forge-Admin/issues/19) | Login & app shell |

## Phase 3: Sermons end to end. **Milestone:** an MP3 uploaded in Forge plays on Heritage, even with Forge stopped

| Step | Repo | Issue | Task |
|---|---|---|---|
| **15** | Forge-Admin | [#11](https://github.com/Malcom-Yingwani/Forge-Admin/issues/11) | Admin content CRUD endpoints |
| **16** | Forge-Admin | [#12](https://github.com/Malcom-Yingwani/Forge-Admin/issues/12) | Media uploads & storage |
| **17** | Heritage-Website | [#13](https://github.com/Malcom-Yingwani/Heritage-Website/issues/13) | Public read endpoints |
| **18** | Heritage-Website | [#14](https://github.com/Malcom-Yingwani/Heritage-Website/issues/14) | Sermons API, facets & podcast feed |
| **19** ⇄ | Forge-Admin | [#13](https://github.com/Malcom-Yingwani/Forge-Admin/issues/13) | Sync sender: outbox & signed webhooks |
| **20** ⇄ | Heritage-Website | [#10](https://github.com/Malcom-Yingwani/Heritage-Website/issues/10) | Sync receiver: events, signature, idempotency |
| **21** ⇄ | Heritage-Website | [#11](https://github.com/Malcom-Yingwani/Heritage-Website/issues/11) | Media mirror & Range serving |
| **22** | Forge-Admin | [#21](https://github.com/Malcom-Yingwani/Forge-Admin/issues/21) | Sermon management UI |
| **23** | Heritage-Website | [#17](https://github.com/Malcom-Yingwani/Heritage-Website/issues/17) | Layout: navbar, dropdowns, footer |
| **24** | Heritage-Website | [#18](https://github.com/Malcom-Yingwani/Heritage-Website/issues/18) | Brand components |
| **25** | Heritage-Website | [#22](https://github.com/Malcom-Yingwani/Heritage-Website/issues/22) | Sermons pages & audio player |

## Phase 4: Sync operations & the outage guarantee

| Step | Repo | Issue | Task |
|---|---|---|---|
| **26** ⇄ | Forge-Admin | [#14](https://github.com/Malcom-Yingwani/Forge-Admin/issues/14) | Sync status, retry & resync |
| **27** ⇄ | Heritage-Website | [#12](https://github.com/Malcom-Yingwani/Heritage-Website/issues/12) | Snapshot (resync) endpoint |
| **28** | Forge-Admin | [#28](https://github.com/Malcom-Yingwani/Forge-Admin/issues/28) | Sync screen |
| **29** | Heritage-Website | [#16](https://github.com/Malcom-Yingwani/Heritage-Website/issues/16) | API conventions & outage-guarantee test |

## Phase 5: Content types (each admin screen, then its website page)

| Step | Repo | Issue | Task |
|---|---|---|---|
| **30** | Forge-Admin | [#23](https://github.com/Malcom-Yingwani/Forge-Admin/issues/23) | Pages, service times & settings UI |
| **31** | Forge-Admin | [#24](https://github.com/Malcom-Yingwani/Forge-Admin/issues/24) | People, ministries & growth groups UI |
| **32** | Heritage-Website | [#20](https://github.com/Malcom-Yingwani/Heritage-Website/issues/20) | About pages |
| **33** | Heritage-Website | [#23](https://github.com/Malcom-Yingwani/Heritage-Website/issues/23) | Ministries pages |
| **34** | Forge-Admin | [#36](https://github.com/Malcom-Yingwani/Forge-Admin/issues/36) | FAQs management |
| **35** | Heritage-Website | [#34](https://github.com/Malcom-Yingwani/Heritage-Website/issues/34) | FAQs page |
| **36** | Forge-Admin | [#22](https://github.com/Malcom-Yingwani/Forge-Admin/issues/22) | Events management |
| **37** | Heritage-Website | [#24](https://github.com/Malcom-Yingwani/Heritage-Website/issues/24) | Events pages |
| **38** | Forge-Admin | [#33](https://github.com/Malcom-Yingwani/Forge-Admin/issues/33) | Blog management |
| **39** | Heritage-Website | [#32](https://github.com/Malcom-Yingwani/Heritage-Website/issues/32) | Blog pages |
| **40** | Forge-Admin | [#34](https://github.com/Malcom-Yingwani/Forge-Admin/issues/34) | Creeds & Confessions management |
| **41** | Heritage-Website | [#21](https://github.com/Malcom-Yingwani/Heritage-Website/issues/21) | Resources: Creeds & Confessions, Bible Hour |
| **42** | Forge-Admin | [#35](https://github.com/Malcom-Yingwani/Forge-Admin/issues/35) | Giving funds management |
| **43** | Heritage-Website | [#33](https://github.com/Malcom-Yingwani/Heritage-Website/issues/33) | Giving pages |

## Phase 6: Contact, lift requests & inbox

| Step | Repo | Issue | Task |
|---|---|---|---|
| **44** | Forge-Admin | [#17](https://github.com/Malcom-Yingwani/Forge-Admin/issues/17) | Report-an-issue endpoint |
| **45** ⇄ | Forge-Admin | [#15](https://github.com/Malcom-Yingwani/Forge-Admin/issues/15) | Contact inbox receiver |
| **46** ⇄ | Heritage-Website | [#15](https://github.com/Malcom-Yingwani/Heritage-Website/issues/15) | Contact & lift-request forms API |
| **47** | Forge-Admin | [#27](https://github.com/Malcom-Yingwani/Forge-Admin/issues/27) | Inbox UI & report-issue modal |
| **48** | Heritage-Website | [#25](https://github.com/Malcom-Yingwani/Heritage-Website/issues/25) | Contact Us pages |

## Phase 7: Admin finishing & website polish

| Step | Repo | Issue | Task |
|---|---|---|---|
| **49** | Forge-Admin | [#20](https://github.com/Malcom-Yingwani/Forge-Admin/issues/20) | Dashboard |
| **50** | Forge-Admin | [#26](https://github.com/Malcom-Yingwani/Forge-Admin/issues/26) | User management |
| **51** | Heritage-Website | [#19](https://github.com/Malcom-Yingwani/Heritage-Website/issues/19) | Home page |
| **52** | Heritage-Website | [#26](https://github.com/Malcom-Yingwani/Heritage-Website/issues/26) | SEO, accessibility, performance |

## Phase 8: Privacy, hosting, deploy & launch

| Step | Repo | Issue | Task |
|---|---|---|---|
| **53** | Forge-Admin | [#32](https://github.com/Malcom-Yingwani/Forge-Admin/issues/32) | POPIA |
| **54** | Heritage-Website | [#31](https://github.com/Malcom-Yingwani/Heritage-Website/issues/31) | Decide hosting & take control of DNS |
| **55** | Forge-Admin | [#30](https://github.com/Malcom-Yingwani/Forge-Admin/issues/30) | Deploy Forge |
| **56** | Heritage-Website | [#28](https://github.com/Malcom-Yingwani/Heritage-Website/issues/28) | Deploy Heritage |
| **57** | Forge-Admin | [#31](https://github.com/Malcom-Yingwani/Forge-Admin/issues/31) | Forge backups & monitoring |
| **58** | Heritage-Website | [#29](https://github.com/Malcom-Yingwani/Heritage-Website/issues/29) | Heritage backups & monitoring |
| **59** | Heritage-Website | [#27](https://github.com/Malcom-Yingwani/Heritage-Website/issues/27) | Content migration |
| **60** | Heritage-Website | [#30](https://github.com/Malcom-Yingwani/Heritage-Website/issues/30) | Launch: DNS cutover & redirects |

## Closed / not in the order

- Forge-Admin #16 and #25 (member directory): closed, there's no directory in this project.
- The epics (#1–#4 in each repo) group the steps and are closed when all their steps are done.
