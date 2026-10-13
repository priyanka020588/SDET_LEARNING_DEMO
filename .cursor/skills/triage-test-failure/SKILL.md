---
name: triage-test-failure
description: >-
  Use when a failed test run using logs, screenshots, and Surefire output.
  Use when the user asks to triage a failure, explain a red CI run, or
  classify flake vs product bug vs locator vs wait. Do not recommend retry
  or Thread.sleep as the first fix. Triage only — do not edit framework
  code in the same step.
---

# Triage test failure

**One line:** A retry that passes is not a pass. **Classify first.**

Reference: `sdet-project-structure.md` §7 (flaky tests — categories and fixes).

## Prerequisites / inputs

The user should provide (or you should ask for):

| Input | Required | Where to look |
|-------|----------|----------------|
| **What failed** | Yes | Test class/method name, suite (smoke/regression), local vs CI |
| **Automation log** | If exists | `logs/automation.log` (or latest `logs/automation-*.log`) |
| **Screenshots** | If exists | `reports/screenshots/` |
| **Surefire output** | Yes for Maven runs | `target/surefire-reports/` (XML + text dumps) |

Also useful: `reports/extent-report.html`, stack trace pasted in chat, CI job link.

## Workflow
Task Progress:

Step 1: Gather evidence (with file paths)
Step 2: Pick one primary category
Step 3: State what NOT to do
Step 4: Recommend next step (fix or Jira bug)
Step 5: STOP — no code edits in this Skill unless user asks after triage


### Step 1: Gather evidence

Read relevant files. In your output, **always cite paths**, for example:

- `logs/automation.log` — line or excerpt
- `reports/screenshots/<name>.png` — what it shows
- `target/surefire-reports/<suite>.xml` — failure message, stack trace

Note the **failure pattern** when visible: fails only on CI, only in parallel, only in full suite, passes on retry, etc.

### Step 2: Pick a category

Choose **one primary** category (add secondary if needed). Use this project’s language:

| Category | Typical signals |
|----------|-----------------|
| **Product bug** | App behaviour wrong; stable repro; not timing/order |
| **Locator** | `NoSuchElement`, stale element, selector not found after UI change |
| **Wait / timing** | Passes locally, fails on slow CI; element not clickable/visible yet |
| **Test isolation** | Passes alone, fails in suite; shared data; order dependency |
| **Environment** | Many unrelated tests fail; infra/Demo Shop not up; third-party down |
| **Flake (symptom)** | Failed then passed with **no code change** — still classify **root cause** (wait, data, locator, etc.) |

Map causes to fixes using `sdet-project-structure.md` §7 table (bad waiting, shared data, fragile locators, etc.).

### Step 3: What NOT to do

Always include a short **“Do not do first”** list. At minimum:

- Do **not** recommend **RetryAnalyzer** or “add retries” as the **first** fix.
- Do **not** recommend **`Thread.sleep`** as the fix.
- Do **not** edit production page/test code as part of this triage step (triage first, change later).

### Step 4: Recommended next step

Give a **concrete** next step matched to the category, for example:

- **Wait:** explicit wait on condition via `BasePage` / `WaitUtils`; no mixed implicit/explicit waits.
- **Locator:** verify `data-test` in app; update page object only with verified selectors.
- **Isolation:** API setup per test (`UserApiHelper`), no shared state; run test alone vs suite.
- **Environment:** check `@BeforeSuite` / Demo Shop; distinguish infra vs test failure.
- **Product bug:** prepare a Jira bug **draft** for the user — do **not** auto-create.

**Product bug → Jira handoff**

- Point to Skill: `.cursor/skills/jira-create-tickets/`
- **Require human approval** before calling `jira-create-tickets` or creating any issue.
- Bug description must include **evidence**: screenshot path and/or log/Surefire snippet (verbatim).

### Step 5: STOP

Deliver the triage report. Do not open PRs, commit fixes, or generate Java unless the user explicitly asks **after** they agree with the classification.

## Output format

Return a structured report:

```markdown
## Triage: <test name>

**Category:** <primary category>

### Evidence
| Source | Path | What it shows |
|--------|------|----------------|
| Surefire | target/surefire-reports/... | ... |
| Log | logs/automation.log | ... |
| Screenshot | reports/screenshots/... | ... |

### Jira (if product bug)
- Draft summary/description with evidence attached in text
- Awaiting your approval before jira-create-tickets

## Rules (explicit)
- Do not add RetryAnalyzer (or increase retry count) as the first recommendation.
- Do not treat “passed on retry” as a pass without naming the underlying category.
- Do not create Jira tickets without user approval.
- Do not triage and patch code in one breath — classify first.

## Verification
Output includes category + evidence file paths + next step.
“Do not do first” mentions retry and sleep.
No Java files changed and no Jira issues created unless the user approved after triage.

## Error handling
Situation|	Action
Missing Surefire/log files | Ask user to re-run test or paste stack trace
Evidence contradicts category | State uncertainty; list what would confirm each category
User only pastes one line of error | Triage from that; list which files would strengthen the call