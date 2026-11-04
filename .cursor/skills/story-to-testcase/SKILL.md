---
name: story-to-testcase
description: >-
  Turn a Jira story into an approved test-case list (positive, negative, boundary;
  smoke vs regression). Read AC via Jira MCP when the user gives an issue key.
  No automation code and no new Jira tickets until the user approves the list.
---

## Planning step of SDLC

Create test cases from the story: positive, negative, and boundary. Mark smoke vs regression and automate vs manual.

**No automation until the test list is approved.**

## Prerequisites

- Jira MCP (`user-jira`) configured, or user pastes AC manually
- Jira issue key or pasted acceptance criteria

## Input the user provides

| Input | Required | Notes |
|-------|----------|--------|
| **Issue key** | Preferred | e.g. `SDET-24` — read ticket via MCP |
| **Pasted AC** | If MCP lacks fields | Use when AC is not in the API response (see **Fields MCP may miss**) |

## Pull AC from Jira (MCP)

1. User gives issue key (e.g. `SDET-24`).
2. Call **`jira_get_issue`** on namespace **`user-jira`** (summary + description at minimum).
3. Extract **acceptance criteria** in your own words from the description (look for **Acceptance criteria**, **Done when**, or requirement bullets).
4. Include **summary**, **status**, and link: `https://etltester.atlassian.net/browse/<KEY>`.
5. If AC is not visible in the API response, **stop and ask** the user to paste AC — do not invent requirements.
6. **Ask before** any `jira_create_issue` or batch create. This Skill produces a **test list only**, not new tickets.

If description looks incomplete, retry once with `fields: "*all"` and `use_display_names: true`, then check **Fields MCP may miss** before asking for a paste.

## Fields MCP may miss

**Default `jira_get_issue` returns:** summary, description, status, issuetype, priority, assignee, labels, created, updated, reporter (enough when AC is written in **Description** — e.g. SDET learning Tasks).

**Often needs extra request or still incomplete:**

| Where AC lives in Jira UI | MCP gap | Action |
|---------------------------|---------|--------|
| **Description** (markdown bullets) | None if text is in API | Use default pull |
| **Acceptance criteria / DoD custom field** | Omitted unless `fields` lists that field or `*all`; field may be empty on some issue types | Paste AC, or fetch `*all` and read the named field |
| **Proforma / request-type forms** | Not in description | `jira_get_issue_proforma_forms` if needed; else paste |
| **Comments only** | Not always in default payload | `include: comments` or paste the comment |
| **Attachments** (PDF, screenshot) | Not interpreted as AC automatically | User pastes text or agent uses `jira_download_attachments` and reads file |
| **Confluence / remote link** | Link metadata only, not page body | User pastes criteria or link + bullets |
| **Parent Epic body** | Parent returns key + summary, not full epic description | Fetch epic issue key separately or paste AC |
| **Permissions / auth** | Empty description in API while UI shows text | Fix MCP auth; paste until fixed |

**Paste AC by hand when:** after `jira_get_issue` (and one `*all` retry if needed), you still cannot state acceptance criteria from the response — do not guess.

## Workflow

```
Task Progress:
- [ ] Step 1: Read Jira ticket (MCP) and state AC in plain language
- [ ] Step 2: List test cases — Positive, Negative, Edge
- [ ] Step 3: Mark each row — smoke vs regression
- [ ] Step 4: Mark automate vs manual
- [ ] Step 5: STOP — wait for human approval (no Java, no new Jira issues)
```

### Step 5: Summarize results

Table columns: **Test Scenario | Category | Suite | Automate/Manual**

## Rules

- **STOP** for human approval before any framework code or new Jira issues.
- Do **not** add test classes under `src/test/` in this Skill.
- Do **not** invoke `new-testng-test`, `new-page-object`, or `jira-create-tickets` without explicit user approval after the list.
- Do **not** commit as part of this planning step unless the user asks separately.

## Verification

- Table includes positive, negative, and boundary-style rows.
- Every row has suite + automate/manual.
- User approved the list.
- No Java files and no new Jira tickets were created.

## Error handling

| Situation | Action |
|-----------|--------|
| MCP auth failure | Report; user fixes Settings → MCP |
| Missing AC in response | Retry `fields: "*all"`; if still missing, ask user to paste AC (see **Fields MCP may miss**) |
| User wants automation next | Hand off to `/new-page` or `/new-test` after approval |
