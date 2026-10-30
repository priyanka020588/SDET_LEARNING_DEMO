# Triage test failure (`/triage-ci`)

Thin trigger for the **triage-test-failure** Skill. Read and follow `.cursor/skills/triage-test-failure/SKILL.md` completely — do not duplicate that playbook here.

**One line:** Classify first. Do not fix in the same breath.

## Before triage

If missing, ask: **which test/suite** failed and **local** run vs **CI** (failing check name/URL when CI).

## Command gates (on top of the Skill)

1. **Read evidence from disk** — use file tools on `target/surefire-reports/`, `logs/`, and `reports/screenshots/` when they exist. Do **not** ask the user to paste logs if those folders already have output from a local run.
2. **CI** — if `user-github` MCP is available, use a failing check as input; if not, pasted CI text is **fallback only** when disk evidence is missing or CI-only.
3. **Do not do first** — no RetryAnalyzer/retry count and no `Thread.sleep` as the first fix (Skill has the rest).
4. **Triage only** — no code edits until the user **confirms the category**. **STOP** after the Skill’s report unless they ask for a fix next.

## Finish

Deliver the Skill’s structured triage output (category, evidence paths, next step). Jira only after user approval.
