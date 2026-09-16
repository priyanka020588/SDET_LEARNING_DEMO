# AI Adoption in SDET

North-star document for the Jira epic **AI Adoption in SDET**.

This repo already has a working Selenium + TestNG framework (Demo Shop) and a Java learning path. This epic sits beside that work. It does not replace writing tests by hand. It teaches you to use Claude in Cursor as a **repeatable testing toolchain**.

**One line to remember:** AI drafts. You own the verdict.

---

## Objective

Become an SDET who can run the testing SDLC with Cursor — from a Jira story to a reviewed test, a CI failure, and a defect — using **Rules, Skills, Commands, Agents, and MCP connectors**.

When this epic is done, you should be able to:

1. Point the agent at a Jira story and get a **test list you trust enough to approve**
2. Generate a **page object + TestNG test that matches this framework** and pass smoke
3. Point it at a **red local or CI run** and get a classified cause (flake vs product bug vs wait vs locator)
4. File a **Jira bug with evidence** through a Skill (you already have `jira-create-tickets`)
5. Explain **which artifact owns each step** — Rule, Skill, Command, Agent, or MCP

CCAF (Claude Certified AI Fluency) is the thinking model. This epic is the practice: collaboration, context, evaluation, and responsible use, applied to real testing work on this project.

---

## What this is not

- Replacing Page Object Model, TestNG, or CI with “the agent just tests the UI”
- Training or fine-tuning a model
- Merging unreviewed tests because they “look right”
- Inventing locators or product behaviour the app does not have

---

## CCAF → Cursor mapping

| CCAF idea | What you will build here | Why it matters for testing |
|---|---|---|
| You stay accountable | Rules + review Command + evaluation checklist | Stops shipping unreviewed tests |
| Prompting | Skills + Commands | Encodes a good prompt once |
| Context | Rules, this repo, MCP | Agent reads pages, Jira AC, and CI logs instead of guessing |
| Evaluation | Checklist in every Skill | “Looks like a test” is not a pass |
| Responsible use | Guardrails in Rules | No secrets in prompts, no fake product behaviour |

---

## The five building blocks

| Artifact | What it is | Analogy |
|---|---|---|
| **Rules** | Always-on project memory (`.cursor/rules`, `AGENTS.md`) | House style the agent must follow |
| **Skills** | Playbooks the agent follows for one job (`.cursor/skills/`) | A runbook: inputs, steps, checks |
| **Commands** | Slash triggers that start a Skill (`.cursor/commands/`) | A button that starts the runbook |
| **MCP** | Connectors to Jira, GitHub, browser, knowledge | Hands: the agent can *read/write systems*, not only files |
| **Agents** | Specialists for review, explore, or failure investigation | A teammate with a narrow job |

You already have one Skill: `.cursor/skills/jira-create-tickets/`. Phase 2 starts by taking that apart.

---

## How the testing SDLC maps to this epic

```text
Jira story (MCP)
    → test ideas (Skill, you approve)
        → page object + test (Skill / Command)
            → review against framework rules (Command / Agent)
                → smoke on PR (CI + GitHub MCP)
                    → if red: classify failure (Skill / Agent)
                        → Jira bug with evidence (existing Jira Skill)
```

---

## How to work the tickets

Same Git rule as the framework learning epic:

- One Jira ticket = one branch = one Pull Request (when the ticket adds files)
- Branch: `learning/SDET-XX-short-name`
- Commit: `SDET-XX: description`
- PR includes: Summary, Jira link, What I learned, Test plan, Files changed

Phase 0 tickets are mostly reading and notes. Later phases add files under `.cursor/` and, for the capstone, a small Demo Shop test change.

Do tickets **in phase order**. A Skill that generates tests will fight you if Rules do not exist yet.

---

## Epic and tickets

Epic: [SDET-40 — AI Adoption in SDET](https://etltester.atlassian.net/browse/SDET-40)

Board: [SDET timeline](https://etltester.atlassian.net/jira/software/projects/SDET/boards/2/timeline)

Work tickets **in phase order**.

### Phase 0 — Fluency and guardrails

Decide *when* to use AI and *how* to judge the output before you generate code.

| Key | Summary |
|---|---|
| [SDET-41](https://etltester.atlassian.net/browse/SDET-41) | When AI helps vs hurts in testing — decide before you generate |
| [SDET-42](https://etltester.atlassian.net/browse/SDET-42) | Evaluation checklist for AI-generated tests — a draft is not a pass |
| [SDET-43](https://etltester.atlassian.net/browse/SDET-43) | Context beats one-shot prompts — why Rules and Skills exist |

### Phase 1 — Rules (project memory)

Teach the agent this framework: assertions in tests, locators in pages, API-first data, run smoke after test changes.

| Key | Summary |
|---|---|
| [SDET-44](https://etltester.atlassian.net/browse/SDET-44) | Framework Rules — teach the agent how this repo tests |
| [SDET-45](https://etltester.atlassian.net/browse/SDET-45) | Operating Rules — read first, run smoke, never invent locators |
| [SDET-46](https://etltester.atlassian.net/browse/SDET-46) | AGENTS.md — project briefing the agent reads every time |

### Phase 2 — Skills (playbooks)

Reusable instructions for page objects, tests, flake triage, and story-to-test-cases.

| Key | Summary |
|---|---|
| [SDET-47](https://etltester.atlassian.net/browse/SDET-47) | Anatomy of a Skill — reverse-engineer jira-create-tickets |
| [SDET-48](https://etltester.atlassian.net/browse/SDET-48) | Skill: new Page Object — generate pages that match LoginPage |
| [SDET-49](https://etltester.atlassian.net/browse/SDET-49) | Skill: new TestNG test — generate tests that match this framework |
| [SDET-50](https://etltester.atlassian.net/browse/SDET-50) | Skill: flake and failure triage — classify before you fix |
| [SDET-51](https://etltester.atlassian.net/browse/SDET-51) | Skill: story to test cases — Jira AC to a list you approve |

### Phase 3 — Commands (triggers)

Slash commands so you do not re-explain the same job every time.

| Key | Summary |
|---|---|
| [SDET-52](https://etltester.atlassian.net/browse/SDET-52) | Command /new-page — one slash to generate a page object |
| [SDET-53](https://etltester.atlassian.net/browse/SDET-53) | Command /new-test — one slash to generate a TestNG test |
| [SDET-54](https://etltester.atlassian.net/browse/SDET-54) | Command /triage-ci — one slash to classify a red run |
| [SDET-55](https://etltester.atlassian.net/browse/SDET-55) | Command /review-test-pr — audit a diff against framework rules |

### Phase 4 — MCP (systems)

Jira for planning, GitHub for PR/CI, browser to verify UI, knowledge lookup, and a wishlist of connectors you would want next.

| Key | Summary |
|---|---|
| [SDET-56](https://etltester.atlassian.net/browse/SDET-56) | Jira MCP for test planning — story to tests, no ticket spam |
| [SDET-57](https://etltester.atlassian.net/browse/SDET-57) | GitHub MCP for PR and CI — explain a failing check with evidence |
| [SDET-58](https://etltester.atlassian.net/browse/SDET-58) | Browser verification — prove UI and test changes like a user would |
| [SDET-59](https://etltester.atlassian.net/browse/SDET-59) | Testing MCP wishlist — design connectors you would want next |
| [SDET-60](https://etltester.atlassian.net/browse/SDET-60) | Knowledge MCP — look up testing standards without leaving Cursor |

### Phase 5 — Agents (specialists)

When to stay in the main chat vs spawn a reviewer, investigator, or explore agent.

| Key | Summary |
|---|---|
| [SDET-61](https://etltester.atlassian.net/browse/SDET-61) | When to spawn an agent — main chat vs specialist |
| [SDET-62](https://etltester.atlassian.net/browse/SDET-62) | Test-reviewer agent — catch locator and assertion mistakes |
| [SDET-63](https://etltester.atlassian.net/browse/SDET-63) | Failure-investigator agent — verdict from logs and screenshots |
| [SDET-64](https://etltester.atlassian.net/browse/SDET-64) | Explore agent — find the right file before changing it |

### Phase 6 — Capstone

Run the full loop on Demo Shop, then write what AI got right, wrong, and still needs you.

| Key | Summary |
|---|---|
| [SDET-65](https://etltester.atlassian.net/browse/SDET-65) | Capstone — full AI-assisted SDLC loop on Demo Shop |
| [SDET-66](https://etltester.atlassian.net/browse/SDET-66) | Evaluation write-up — what AI got right, wrong, and still needs you |

---

## Definition of done for the epic

You can, without a long prompt:

- Use `/new-test` (or the Skill) on a Demo Shop case and pass `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml`
- Use `/triage-ci` (or the Skill) on a failure and agree with the classification
- Open a Jira story, get test ideas, **approve them**, then implement
- Tell someone which file to edit to change agent behaviour (Rule vs Skill vs Command)

---

## Related work in this repo

| Epic | What it covers |
|---|---|
| [SDET-18](https://etltester.atlassian.net/browse/SDET-18) | Java / Selenium / TestNG framework learning |
| [SDET-2](https://etltester.atlassian.net/browse/SDET-2) | Programming practice |
| **AI Adoption in SDET** (this epic) | Cursor + Claude as the testing toolchain |

Do not duplicate SDET-18 lessons here. Use that framework as the lab.
