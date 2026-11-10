# Test-reviewer agent (SDET-62)

Read-only specialist for **framework-rules** review of test and page-object diffs in this repo.

> **One line:** Diff in → rules check → **Blocking / Should fix / Nit** out. No code edits unless you ask after the report.

## How to run

| Method | When |
|--------|------|
| **Subagent** | `/test-reviewer` or “use the test-reviewer subagent on this PR” |
| **Command (fallback)** | `/review-test-pr` in main chat — same scope and output if subagents are unavailable |

**Config file:** [`.cursor/agents/test-reviewer.md`](../.cursor/agents/test-reviewer.md) (`readonly: true`).

**Slash command spec:** [`.cursor/commands/review-test-pr.md`](../.cursor/commands/review-test-pr.md).

## Scope

- **In:** `src/test/java` tests, `src/main/java/.../pages/` (and components), framework/test resources touched by the diff.
- **Out:** Generic style review, whole-repo audits (unless requested), implementing fixes in the same pass.

## Rules sources (read order)

1. `.cursor/rules/`
2. `AGENTS.md`
3. `sdet-project-structure.md`

## Checklist (summary)

| Check | Violation |
|-------|-----------|
| Assertions in tests only | Asserts in pages/components |
| Locators in pages only | `By` in tests |
| API-first data | UI-only setup when API helper exists |
| No `Thread.sleep` | Sleep / blind waits in changed code |
| `@Test` groups | Missing `smoke` or `regression` on new tests |
| Isolation | Shared mutable state, order-dependent tests |

See the agent file and `/review-test-pr` for the full list and browser-verification note.

## Output

Every run includes **Blocking**, **Should fix**, and **Nit** (use “none” if empty), each finding with `path:line`, plus a one-line **Verdict**.

## What this agent must not do

- Edit Java or open PRs as part of the review.
- Replace page objects or tests wholesale.
- Give vague “improve comments” feedback instead of rule violations.

## Related

- [When to use agents](when-to-use-agents.md) — reviewer vs main chat + `/review-test-pr`
- [SDET-62](https://etltester.atlassian.net/browse/SDET-62)
