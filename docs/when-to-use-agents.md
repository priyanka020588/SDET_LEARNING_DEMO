# When to use agents (SDET-61)

Decision guide for **sdet_learning_demo**: stay in the **main chat** vs spawn a **specialist agent**. Phase 5 aligns with [SDET-62](https://etltester.atlassian.net/browse/SDET-62) (test-reviewer), [SDET-63](https://etltester.atlassian.net/browse/SDET-63) (failure-investigator), and [SDET-64](https://etltester.atlassian.net/browse/SDET-64) (explore).

> **One line:** An agent is for a **narrow job** with a clear **hand-back**. The main chat is for the **SDLC conversation** (plan → implement → approve → commit).

## Main chat vs specialists (quick map)

| Specialist | Narrow job | Fallback in this repo (no subagent) |
|------------|------------|-------------------------------------|
| **Main chat** | Skills, Commands, Q&A, edits you approve | `/new-page`, `/new-test`, `/triage-ci`, `/review-test-pr` |
| **Explore** | Find the right **file/path** before changing code | Thorough repo search in main chat |
| **Test-reviewer** | **Read-only** diff audit vs Rules | `/review-test-pr` |
| **Failure-investigator** | **Verdict + evidence paths** from logs/reports only | `/triage-ci` + triage Skill |

---

## Decision table

| Situation | Use | Why |
|-----------|-----|-----|
| Discuss or ask questions about the **overall framework** (POM, suites, where assertions live, interview-style walkthrough) | **Main chat** | General SDLC conversation; pulls from Rules, `sdet-project-structure.md`, and many areas — not a single narrow search or read-only review. |
| **Implement** a page or TestNG test with a known Skill (verified locators, AC, smoke/regression group) | **Main chat** + **`/new-page`** or **`/new-test`** | Commands trigger Skills with gates and checklists; you stay accountable for approval and local smoke. |
| Plan tests from a **Jira story** (AC → test list, no code yet) | **Main chat** + **story-to-testcase** Skill (+ Jira MCP) | Planning and approval belong in one thread; no separate agent needed. |
| **Find** where something is configured or wired (“Where is retry?”, “Who opens the browser?”, “Where is smoke listed?”, “Where are screenshots saved?”) | **Explore agent** (or explore-style subagent) | Map the repo **first**; implement the change later in main chat with a Skill. Explore does **not** write the test for you ([SDET-64](https://etltester.atlassian.net/browse/SDET-64)). |
| **Review** a PR or local diff for framework violations (locators in tests, asserts in pages, `Thread.sleep`, missing `@Test` groups, isolation) | **Test-reviewer agent** (read-only) | Focused audit with **Blocking / Should fix / Nit**; reviewer does **not** implement fixes unless you ask after the report. Same intent as **`/review-test-pr`** ([SDET-62](https://etltester.atlassian.net/browse/SDET-62)). |
| **Classify** a failed run (local or CI): read Surefire, `logs/`, `reports/screenshots/`; category + next step; **no fix in the same pass** | **Failure-investigator agent** | Keeps “why did it fail?” separate from “rewrite the code.” Same categories as **triage-test-failure** Skill; use **`/triage-ci`** in main chat if you are not spawning a subagent ([SDET-63](https://etltester.atlassian.net/browse/SDET-63)). |
| **Triage CI on a PR** (check name + log snippet, then classify) | **Main chat** + **`/triage-ci`** (GitHub/`gh` for check, then triage Skill) | One handoff: evidence from CI → same verdict format; investigator subagent optional if you want logs-only focus. |
| Small **doc or rule** change (e.g. browser verification rule, MCP wishlist) | **Main chat** | Single-file, human-reviewed; no specialist required. |

---

## Anti-pattern (explicit)

**Do not spawn a specialist agent to do a job a Skill + Command already defines.**

Examples:

- Spawning an agent to **write `LoginTest`** when **`/new-test`** → **new-testng-test** Skill already covers inputs, groups, checklist, and smoke reminder.
- Spawning **explore** to “generate the checkout test” — explore is for **finding paths**, not implementing tests.
- Spawning **investigator** and letting it **patch `WaitUtils` or delete a test** in the same pass — investigation is **verdict + evidence**; you approve fixes in main chat.
- Spawning **reviewer** that **edits files** during the review — reviewer is **read-only** until you ask for a follow-up fix.

If the Command exists, use the Command in main chat unless you need **extra isolation** (long diff review, deep repo search, log-only tunnel vision).

---

## Hand-back rule

After any specialist finishes:

1. Read the report (paths, categories, findings).
2. Return to **main chat** for decisions: approve test list, apply fix, commit, open PR.
3. Do **not** leave implementation split across multiple agents without you reconciling context.

---

## Related docs

- North star: [`docs/ai-adoption-in-sdet.md`](ai-adoption-in-sdet.md) — five building blocks (Rules, Skills, Commands, MCP, Agents)
- Triage categories: `.cursor/skills/triage-test-failure/SKILL.md`
- MCP wishlist (future connectors): [`docs/testing-mcp-wishlist.md`](testing-mcp-wishlist.md)

**Out of scope for this guide:** inventing many agent types you never run, or using agents to skip reading the Skill.
