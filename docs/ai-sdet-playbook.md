# AI SDET playbook — capstone run (SDET-65)

**One real loop** on Demo Shop **checkout**, using this repo’s Cursor artifacts. Not a new framework.

> **One line:** Plan → implement with Skills → review → smoke → document what you used.

**Jira:** [SDET-65](https://etltester.atlassian.net/browse/SDET-65)  
**Feature:** Checkout step (summary with/without cart items)  
**Code change:** `CheckoutTest.java` — two new methods; existing pages unchanged.

---

## What we shipped

| Test | Group | Intent |
|------|-------|--------|
| `checkoutPageShowsProductInSummary` | smoke | Login → add product → checkout → heading + summary contains product |
| `emptyCartShowsEmptySummaryOnCheckout` | regression | Login → checkout with empty cart → summary = `AppConstants.EMPTY_CART_MESSAGE` |
| `userCanCheckoutACatalogItem` | regression | *(unchanged)* full place-order E2E |

**Smoke result (local):** `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml` — **PASS** (includes new smoke test).  
**Class run:** `./mvnw test -Dtest=CheckoutTest` — **PASS** (3/3).

---

## The loop (in order)

Use this sequence for the next story; skip a step only when it does not apply and note why.

| Step | What we did | Artifact |
|------|-------------|----------|
| 1. **Pick scope** | Checkout capstone; small PR, no new screen | Jira **MCP** (`jira_get_issue` on SDET-65) + main chat |
| 2. **Test list from AC** | Positive/negative rows; smoke vs regression; **human approved before code** | **Skill:** [story-to-testcase](../.cursor/skills/story-to-testcase/SKILL.md) |
| 3. **Page object** | **Skipped** — `CheckoutPage` / `ConfirmationPage` already exist | **Command:** `/new-page` *not used* (would be step 3 if page missing) |
| 4. **Implement tests** | Two methods in `CheckoutTest`; API user, AssertJ, groups | **Skill:** [new-testng-test](../.cursor/skills/new-testng-test/SKILL.md) (same rules as **Command:** `/new-test`) |
| 5. **Phase 0 evaluation** | Checklist in Skill § Step 4 (no locators in test, groups, no sleep, API data) | **Skill** checklist + `sdet-project-structure.md` |
| 6. **Review diff** | Blocking / Should fix / Nit on **git diff only** | **Agent:** `test-reviewer` subagent (`.cursor/agents/test-reviewer.md`); fallback **Command:** `/review-test-pr` |
| 7. **UI / smoke check** | Maven smoke suite (substitute when no browser walk in session) | **Rule:** [browser-verification.mdc](../.cursor/rules/browser-verification.mdc) + README smoke command |
| 8. **If red** | Classify from Surefire/logs/screenshots; no fix in same pass | **Command:** `/triage-ci` → **Skill:** triage-test-failure; optional **Agent:** `failure-investigator` |
| 9. **Product bug** | Only with evidence; **you approve** before create | **Skill:** jira-create-tickets + **MCP:** user-jira |
| 10. **Find wiring first** *(optional)* | Paths for driver, smoke XML, Extent, etc. | **Built-in Agent:** Explore (not used on this run; used in SDET-64) |

---

## Artifact cheat sheet (this capstone)

| Type | Used on checkout capstone? | Path / entry |
|------|----------------------------|--------------|
| **Rule** | Yes (review + smoke substitute) | `.cursor/rules/`, `sdet-project-structure.md`, `AGENTS.md` |
| **Skill** | Yes | `story-to-testcase`, `new-testng-test` |
| **Command** | Yes (review); `/new-test` equivalent via Skill workflow | `review-test-pr.md`, `new-test.md` |
| **MCP** | Yes (ticket context) | **user-jira** |
| **MCP** | No on this run | user-github (CI), cursor-ide-browser (walk), user-MY-CEL |
| **Project agent** | Yes | `test-reviewer` |
| **Project agent** | No on this run | `failure-investigator` |
| **Built-in agent** | No on this run | **Explore**, **Browser** |

---

## When to use what (short)

See [when-to-use-agents.md](when-to-use-agents.md).

- **Main chat** — planning, approval, implement after test list OK, commit/PR conversation.
- **Explore** — “where is X?” before editing; **does not** write tests.
- **test-reviewer** — read-only diff audit before merge.
- **failure-investigator** — logs/Surefire/screenshots after a red run.

**Anti-pattern we avoided:** Generating checkout tests before approving the test table; inventing locators; merging without review because smoke passed.

---

## Commands to keep handy

```bash
# After test changes (smoke)
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml

# One class
./mvnw test -Dtest=CheckoutTest

# Regression-only new test
./mvnw test -Dtest=CheckoutTest#emptyCartShowsEmptySummaryOnCheckout
```

---

## Related docs

- [AI Adoption in SDET](ai-adoption-in-sdet.md) — epic map  
- [test-reviewer-agent.md](test-reviewer-agent.md) — reviewer subagent  
- [SDET-65](https://etltester.atlassian.net/browse/SDET-65) — capstone acceptance criteria  

---

*Playbook records the **SDET-65 checkout** run. Update the table in § “The loop” if you repeat the capstone on another flow.*