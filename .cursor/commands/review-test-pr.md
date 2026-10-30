# Review test PR (`/review-test-pr`)

Thin trigger for a **framework rules review** of test/framework changes — not a generic Java style pass.

**One line:** AI test-code review on the **diff**, mapped to **this repo’s rules**.

## Input

Prefer **changed files only**:

- **Local:** `git diff` / `git diff --staged` vs base branch (default `main`), or paths the user names.
- **PR:** PR link or number — use `gh` / GitHub MCP when available; otherwise ask for the diff or changed file list.

Do **not** review the whole repo unless the user explicitly asks.

## Before commenting

Read project rules (in order):

1. `.cursor/rules/` (all rule files present)
2. `AGENTS.md`
3. `sdet-project-structure.md` (tests vs pages, flaky-test guidance)

If `.cursor/rules/` is empty, rely on `sdet-project-structure.md` and patterns in `LoginTest` / `LoginPage`.

## Review scope (this framework)

Check **only what appears in the diff** against:

- Assertions in **tests** only — not in page classes
- Locators / `By` in **pages** only — not in tests
- API-first data (`UserApiHelper`, etc.) — not UI signup when API exists
- No `Thread.sleep`
- Every new `@Test` has a **group** (`smoke` or `regression`)
- No shared mutable test state / order-dependent setup
- Do not nitpick TestNG vs JUnit taste or unrelated files

## Output format

Structured report — **each finding cites `path:line` (or file)**:

| Section | Meaning |
|---------|---------|
| **Blocking** | Must fix before merge (rule violations) |
| **Should fix** | Strong recommendation; not always merge-blocking |
| **Nit** | Optional polish — style/clarity, not framework correctness |

## Finish

**Review only** — do not edit code or open a PR unless the user asks after the report.
