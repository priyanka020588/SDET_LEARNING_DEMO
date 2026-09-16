---
name: jira-create-tickets
description: >-
  Create Jira Task tickets via the Jira MCP from user-provided summary, description,
  labels, and Epic. Use when the user asks to create Jira tickets, add tasks to an
  Epic, bulk-create programming-practice tickets, or provides ticket details with
  labels and an Epic name or key.
---

# Jira Create Tickets

Create Jira issues in the connected Atlassian instance using the `user-jira` MCP namespace.

## Prerequisites

- Jira MCP must be configured in `~/.cursor/mcp.json` (namespace: `user-jira`)
- Default project: **SDET** (from `JIRA_PROJECTS_FILTER` unless the user specifies another project key)
- Default issue type: **Task** (unless the user specifies Story, Bug, etc.)

## Input the user provides

Accept any of these formats:

| Field | Required | Notes |
|-------|----------|-------|
| **Summary** | Yes | Ticket title. May be labeled "Title" or "Summary". |
| **Description** | Yes | Problem statement. Preserve test cases and input/output values **verbatim**. |
| **Labels** | Yes | Comma-separated or listed. Always include all labels the user gives. |
| **Epic** | Yes | Epic **key** (e.g. `SDET-2`) or Epic **summary/name** (e.g. `prog`). |
| **Project** | No | Defaults to `SDET`. |
| **Issue type** | No | Defaults to `Task`. |

For **batch requests**, the user may provide a numbered list of tickets. Parse each item independently.

## Workflow

Copy and track progress:

```
Task Progress:
- [ ] Step 1: Parse ticket(s) from user input
- [ ] Step 2: Resolve Epic key (if name given)
- [ ] Step 3: Inspect jira_create_issue schema
- [ ] Step 4: Create ticket(s)
- [ ] Step 5: Summarize created issue keys grouped by Epic
```

### Step 1: Parse ticket(s)

Extract for each ticket:
- `summary`
- `description` (keep test-case lines exactly as written)
- `labels` (array of strings, lowercase with hyphens)
- `epic` (name or key)
- optional `project_key`, `issue_type`

If summary is missing but a title is present, use the title as summary.

### Step 2: Resolve Epic key

**If Epic looks like a key** (pattern `PROJ-123`): use it directly.

**If Epic is a name** (e.g. `prog`):
1. Call `GetDynamicTools` for `user-jira` / `jira_search`
2. Search: `project = SDET AND type = Epic AND summary ~ "prog"`
3. Use the returned `key`. If multiple matches, pick exact summary match; otherwise ask the user.

If Epic creation is requested instead, call `jira_create_issue` with `issue_type: Epic` first, then use the returned key for child tickets.

### Step 3: Inspect tool schema

Always call `GetDynamicTools` with `namespace: user-jira`, `toolName: jira_create_issue` before the first create in the session.

### Step 4: Create ticket(s)

Use `CallDynamicTool`:

```json
{
  "namespace": "user-jira",
  "toolName": "jira_create_issue",
  "arguments": {
    "project_key": "SDET",
    "summary": "<title>",
    "issue_type": "Task",
    "description": "<description with verbatim test cases>",
    "additional_fields": "{\"labels\": [\"label-one\", \"label-two\"], \"epicKey\": \"SDET-2\"}"
  }
}
```

Rules:
- `additional_fields` must be a **JSON string**, not a nested object
- Link to Epic via `"epicKey": "<EPIC-KEY>"` inside `additional_fields`
- Create multiple tickets in parallel when the batch is independent (up to ~15 at once)
- Do not set assignee unless the user asks

### Step 5: Summarize results

After creation, return a verification table:

```markdown
## Epic: <EPIC-KEY> — <Epic summary>

| # | Key | Summary | Labels |
|---|-----|---------|--------|
| 1 | SDET-3 | Linear Search in an Array | topic-array, level-beginner, programming-practice |

**All ticket IDs:** SDET-3, SDET-4, ...
```

Include a Jira browse link when possible: `https://etltester.atlassian.net/browse/<KEY>`

## Error handling

| Error | Action |
|-------|--------|
| Epic not found | Search again with broader query, or offer to create the Epic |
| Invalid project key | Ask user or read `JIRA_PROJECTS_FILTER` from mcp.json |
| Label rejected | Retry without invalid labels; report which failed |
| Epic link failed on create | Create the issue, then call `jira_link_to_epic` with `issue_key` and `epic_key` |

## Description format

When the user includes test cases, structure the description as:

```
<problem statement>

Test case: <input/output verbatim>
```

For multiple test cases, include each on its own line exactly as the user wrote them.

## Additional resources

- Batch and single-ticket examples: [examples.md](examples.md)
