# Testing MCP wishlist (design only)

Future connector ideas for an SDET team using this Demo Shop framework. **Not implemented in SDET-59** — no MCP server code in this ticket.

> **One line:** Write the tools you wish the agent had, with inputs and outputs, before writing code.

## Connectors we use today

| MCP / tool | Role in this project |
|------------|----------------------|
| **Jira** (`user-jira`) | Read issues, AC, comments; create tickets only with human approval (Skills) |
| **GitHub** (`user-github` / `gh`) | PR status, failing **smoke** check, log snippets |
| **MY-CEL** | Knowledge / PRP templates (thin on Selenium specifics today) |
| **Browser** (`cursor-ide-browser`) | Walk UI like a user after layout changes (see `browser-verification.mdc`) |

## Desired tools (4+)

Each tool is a **design spec**: when to call it, inputs, outputs, human confirmation, and risk.

---

### 1. Attach failure screenshot to Jira

**When an SDET would use it:** After **`/triage-ci`** classifies a failure and you have a product bug or need evidence on an existing issue — not before triage.

| Inputs | Outputs |
|--------|---------|
| Jira **issue key** (bug or story) | Screenshot attached or linked on the issue |
| Path under `reports/screenshots/` | Short comment: test name, category, one-line failure |
| Optional: PR URL or run link | |

**Human confirmation required?** **Yes** — never create a new Jira issue or attach without approval. Prefer comment + attachment on a **draft** issue the user confirms.

**Risk if wrong:** Ticket spam; wrong issue; leaking local paths in a public project.

---

### 2. Summarise last smoke run

**When an SDET would use it:** After CI **`smoke`** completes (GitHub Actions on a PR) or after local `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml`.

| Inputs | Outputs |
|--------|---------|
| GitHub **check run URL** or `gh run view` id, **or** local `target/surefire-reports/` | Pass/fail counts |
| Workflow name **CI**, job **`smoke`** | Failed test **class#method** if any |
| | Verbatim **log snippet** (not paraphrase) |

**Human confirmation required?** **Yes** before posting summary to Jira/Slack — agent must read logs first; user approves text.

**Risk if wrong:** “Build failed” with no test name; invented errors (same anti-pattern as SDET-57).

---

### 3. List quarantine / disabled tests

**When an SDET would use it:** Sprint planning, flake cleanup, or before enabling a previously disabled test.

| Inputs | Outputs |
|--------|---------|
| Repo root (this framework) | List of tests with `@Test(enabled = false)` |
| Optional: suite XML paths (`smoke.xml`, `regression.xml`) | Tests in a future **quarantine** group if we add one |
| | File path + method name per row |

**Human confirmation required?** **No** for read-only scan. **Yes** before changing `enabled` or suite membership.

**Risk if wrong:** Stale grep missing DataProvider cases; doc should note “verify in IDE/search”.

*Note: This repo may have zero quarantined tests today — the tool is still useful when the team adds that practice.*

---

### 4. Fetch product acceptance criteria from Jira

**When an SDET would use it:** Start of test planning (`story-to-testcase` Skill) when the user gives an issue key.

| Inputs | Outputs |
|--------|---------|
| Jira issue key (e.g. `SDET-23`) | Summary, status, link |
| Optional: `fields=*all` if AC is in a custom field | **AC in plain language** from description or named field |
| | Note if user must paste AC (see Skill **Fields MCP may miss**) |

**Human confirmation required?** **No** for read. **Yes** before `jira_create_issue` or any write.

**Risk if wrong:** Invented requirements; **partially covered today** by `user-jira` + `.cursor/skills/story-to-testcase/SKILL.md` — a dedicated tool would standardise AC extraction and custom-field handling.

---

## Explicitly out of scope (SDET-59)

- Implementing or deploying a new MCP server
- Vague tools (“make testing better”) without inputs/outputs
- Auto-creating Jira bugs or comments without human approval

## Optional future ideas (not required for SDET-59)

- **Flake rate:** inputs = test name + CI history; outputs = fail/pass ratio over N runs; confirm before quarantining.
- **MY-CEL ingest:** inputs = `sdet-project-structure.md` §7; outputs = searchable distillation for waits/POM; confirm before publishing to KB.
