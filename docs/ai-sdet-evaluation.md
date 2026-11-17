# AI SDET evaluation (SDET-66)

CCAF-style evaluation for the **AI Adoption in SDET** epic on `sdet_learning_demo`.

> **One line:** If you cannot say what still needs a human, you did not evaluate — you only generated.

**Sources:** [SDET-65](https://etltester.atlassian.net/browse/SDET-65) capstone (checkout), [ai-sdet-playbook.md](ai-sdet-playbook.md), [ai-adoption-in-sdet.md](ai-adoption-in-sdet.md).  
**Capstone PR:** [GitHub PR #36](https://github.com/priyanka020588/SDET_LEARNING_DEMO/pull/36).

---

## Is the toolchain worth using on the next feature?

**Yes — with guardrails.** Planning Skills, framework Rules, and read-only review saved drafting time on [SDET-65](https://etltester.atlassian.net/browse/SDET-65). Review time and explicit delegation are non-optional; without them, output looks fine but misses scope (e.g. subagent reviewing without `git diff`).

---

## What the agent drafted well (real wins)

| Example | Evidence |
|---------|----------|
| **Test plan before code** | Checkout capstone: test table (smoke #1, regression #2) aligned with `DemoShopServer` + `AppConstants.EMPTY_CART_MESSAGE`; **human approved** before edits to `CheckoutTest.java`. |
| **Framework-shaped tests** | Added `checkoutPageShowsProductInSummary` (`groups = {"smoke"}`) and `emptyCartShowsEmptySummaryOnCheckout` (`regression`): `UserApiHelper`, page journey only, AssertJ in test — see `src/test/java/com/company/automation/tests/checkout/CheckoutTest.java`. |
| **Phase 0 checklist** | Applied [new-testng-test](../.cursor/skills/new-testng-test/SKILL.md) checks (no `By` in test, groups set, no `Thread.sleep`). |
| **Green verification** | `./mvnw test -Dtest=CheckoutTest` (3/3) and `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml` — **PASS** (recorded in playbook). |
| **Failure classification** | [SDET-63](https://etltester.atlassian.net/browse/SDET-63) dry run: **failure-investigator** on `LoginTest.validUserCanLogIn:25` → category **wrong test assertion**, paths under `target/surefire-reports/`, `logs/`, `reports/screenshots/`. |
| **Repo map** | [SDET-64](https://etltester.atlassian.net/browse/SDET-64): **Explore** returned `DriverFactory.java`, `smoke.xml`, `ScreenshotUtil` / listeners — verified by opening files. |

---

## Failure modes we saw (real misses)

| Miss | What happened | What we did |
|------|----------------|-------------|
| **Reviewer without diff** | **test-reviewer** subagent on checkout capstone: called new tests “regression only” (missed **smoke** on `checkoutPageShowsProductInSummary`); **Should fix** on `ScreenshotUtil` in `userCanCheckoutACatalogItem` — **not in the diff**. | Parent chat re-reviewed with `git diff`; **Verdict: Approve** on added lines only. **Change:** always attach diff or ask delegate to run `git diff` first. |
| **Subagent vs main chat** | `/test-reviewer` in main chat sometimes **inline** review, not isolated subagent — same rules, different mechanism. | Explicit prompt: “Delegate to test-reviewer subagent (readonly).” |
| **Learning failure vs product bug** | [SDET-57](https://etltester.atlassian.net/browse/SDET-57): inverted `assertThat(home.isLoaded()).isFalse()` — triage correctly **wrong assertion**, not locator/wait. | Did not file product Jira; fixed or reverted after classification. |
| **Regression not in capstone comment** | Capstone recorded smoke; **regression.xml** not always run locally when only `src/test/` changed. | **Next time:** run regression suite (or at least new regression methods) when adding `regression` group tests — see working agreement below. |

We did **not** see invented `data-test` locators or assertions inside page classes on the capstone diff.

---

## Time: saved vs spent (honest estimate)

| Activity | Rough effect |
|----------|----------------|
| **Saved** | Drafting two TestNG methods and a structured test list; summarizing triage categories and evidence paths; explore-style path lists. |
| **Spent** | Approving test list; reading diff and subagent output; re-running smoke (and should run regression); choosing **Command** vs **agent**; PR description and Jira comments; git branch/PR ([SDET-39](https://etltester.atlassian.net/browse/SDET-39) workflow). |

**Net:** Time saved on boilerplate **if** review budget is built in. Skipping review or diff → net loss (merge risk).

*Edit the table above with your own hours if your team tracks them.*

---

## Risks that remain yours

- **Product truth** — AC and demo-app behaviour (e.g. empty cart copy in `AppConstants` / `DemoShopServer`).
- **Secrets** — never paste credentials into chat; MCP config stays local.
- **Merge decision** — green smoke ≠ skip review; CI on PR is the team gate.
- **Jira** — create bugs only with evidence and explicit approval ([jira-create-tickets](../.cursor/skills/jira-create-tickets/SKILL.md)).
- **Delegation** — agents do not replace approving the test list or opening files Explore returned.

---

## Keep / change / drop

| Artifact | Verdict | Rationale |
|----------|---------|-----------|
| **Rules** | **Keep** | `sdet-project-structure.md`, `.cursor/rules/browser-verification.mdc` — tests vs pages, smoke after UI/test changes. |
| **Rules** | **Change** | Finish populating `AGENTS.md` when team norms stabilize. |
| **Skills** | **Keep** | `story-to-testcase` (stop before code), `new-testng-test`, `triage-test-failure`. |
| **Skills** | **Change** | When Jira MCP omits AC, paste into chat early ([Fields MCP may miss](../.cursor/skills/story-to-testcase/SKILL.md)). |
| **Commands** | **Keep** | `/review-test-pr`, `/triage-ci`, `/new-test` (+ `/new-page` when needed). |
| **Commands** | **Change** | Pair `/review-test-pr` with **attached `git diff`** in the same message. |
| **Agents (project)** | **Keep** | `test-reviewer`, `failure-investigator` with `readonly: true`. |
| **Agents (project)** | **Change** | Document “delegate + diff” in [test-reviewer-agent.md](test-reviewer-agent.md) if repeat misses occur. |
| **Agents (built-in)** | **Keep** | **Explore** for “where is X?” before edits ([SDET-64](https://etltester.atlassian.net/browse/SDET-64)). |
| **Agents (built-in)** | **Drop** as default | Explore to **write** tests — use Skills/Commands instead. |
| **MCP** | **Keep** | **user-jira** for tickets and comments. |
| **MCP** | **Change** | Use `gh` CLI when GitHub MCP errors; browser MCP when layout changes matter. |
| **MCP** | **Drop** as blocker | MY-CEL for Selenium specifics — thin; use repo docs first. |

---

## Working agreement — next story (5 lines)

1. Paste or link **Jira AC**; run **story-to-testcase** and **approve the table** before any `src/test` or `src/main` edits.  
2. If the page object is missing → **`/new-page`** first; never invent locators in tests.  
3. Implement via **`/new-test`** / **new-testng-test**; apply **Phase 0 checklist** on the draft.  
4. Review with **`git diff`** + **test-reviewer** subagent or **`/review-test-pr`**; run **smoke** and **regression** when test code changes.  
5. If red → **`/triage-ci`** or **failure-investigator** first; no Jira bug and no “fix” until **I** agree with the category.

---

## Links

| Item | Location |
|------|----------|
| Capstone loop | [ai-sdet-playbook.md](ai-sdet-playbook.md) |
| Epic map | [ai-adoption-in-sdet.md](ai-adoption-in-sdet.md) |
| When to use agents | [when-to-use-agents.md](when-to-use-agents.md) |
| Jira evaluation ticket | [SDET-66](https://etltester.atlassian.net/browse/SDET-66) |