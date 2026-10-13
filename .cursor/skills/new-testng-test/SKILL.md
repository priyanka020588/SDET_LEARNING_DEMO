---
name: new-testng-test
description: >-
  Generate a TestNG test that matches LoginTest and this framework: extends
  BaseTest, API-first data, assertions only in the test, smoke or regression
  groups, existing page objects only, no locators in tests. Use when the user
  asks for a new automated test or TestNG class. Stop if the page object is
  missing — use the new-page-object Skill first.
---

# New TestNG test

**The test describes intent. The page already knows how.**

Gold standard: `src/test/java/com/company/automation/tests/login/LoginTest.java`

```java
User user = UserApiHelper.createActiveUser();
HomePage home = new LoginPage(getDriver()).loginAs(user);
assertThat(home.isLoaded()).isTrue();
```

Also read: `BaseTest.java`, `src/test/resources/suites/smoke.xml`, `src/test/resources/suites/regression.xml`.

## Prerequisites / required inputs

| Input | Required | Notes |
|-------|----------|-------|
| **Page objects** | Yes | Which classes/methods exist (e.g. `LoginPage.loginAs`). Do not invent pages. |
| **Acceptance criteria** | Yes | What success/failure looks like — from story, Jira, or user paste. |
| **Suite group** | Yes | `smoke` or `regression` — must match how suites filter groups. |

Optional: test class name, package under `com.company.automation.tests.*`, negative path expectations (`AppConstants`, JSON test data).

**Human approval:** Present the draft test for review before commit unless the user explicitly asks you to commit.

## Workflow

```
Task Progress:
- [ ] Step 1: Confirm inputs and that page objects exist
- [ ] Step 2: Read LoginTest and BaseTest patterns
- [ ] Step 3: Draft test class (v1 — simple like LoginTest)
- [ ] Step 4: Run Phase 0 evaluation checklist on the draft
- [ ] Step 5: Tell user to run smoke (README command)
```

### Step 1: Confirm inputs

- If the **page class or journey method does not exist**, **STOP**. Do not add locators or pages in the test. Point to `.cursor/skills/new-page-object/SKILL.md` and wait until the page exists.
- If acceptance criteria are vague, ask before generating.

### Step 2: Read references

- `LoginTest` — structure, groups, AssertJ, API data, journey returns.
- `BaseTest` — `getDriver()`, listeners, suite lifecycle.
- Suites — `smoke` includes only `groups = {"smoke"}`; regression uses `regression`.

### Step 3: Draft the test

- **Package:** under `src/test/java/com/company/automation/tests/` (mirror feature, e.g. `login/`).
- **Class:** `extends BaseTest`.
- **Annotation:** `@Test(groups = {"smoke"})` or `@Test(groups = {"regression"})`.
- **Pattern:** create data → page action returning next page → **assertions in the test only**.
- **Data:** `UserApiHelper` (or other API helpers in `com.company.automation.api`) when the app provides them — not UI signup.
- **v1 scope:** one scenario per method, like `LoginTest`. Do **not** add `@DataProvider` unless the user explicitly requires data-driven tests.

### Step 4: Phase 0 evaluation checklist

Apply every item to the draft before calling it done:

| # | Check | Pass? |
|---|--------|-------|
| 1 | Class extends `BaseTest` | |
| 2 | Every `@Test` has `groups` (`smoke` or `regression`) | |
| 3 | **No** `By` / `driver.findElement` / CSS in the test | |
| 4 | **No** assertions inside page classes (only in this test) | |
| 5 | Test data via API helper where available (e.g. `UserApiHelper`) | |
| 6 | **No** `Thread.sleep` | |
| 7 | Page methods return the next page (or `this`) — journey style | |
| 8 | Assertions use AssertJ (`assertThat`) or TestNG as in `LoginTest` | |
| 9 | No hard-coded URLs/credentials — use framework config/pages | |
| 10 | A draft is not a pass — user reviewed the checklist | |

Reference: framework rules in `.cursor/rules/` and `sdet-project-structure.md` (tests vs pages).

Return the completed checklist table to the user with pass/fail notes.

### Step 5: Run smoke after test changes

After the user approves the test, instruct them to run (from `README.md`):

```bash
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml
```

If the new test is **regression-only**, say so and give:

```bash
./mvnw test -DsuiteXmlFile=src/test/resources/suites/regression.xml
```

Do not skip this step in your summary.

## Rules

- **Assertions** only in test classes under `src/test/java/**`.
- **No locators** in tests — use page objects under `src/main/java/**/pages/**`.
- **Data via API** helpers when the framework provides them.
- **Forbid** `Thread.sleep` in generated tests.
- **Require** a TestNG **group** on every new test method.
- Do not generate a test for a **missing** page — hand off to **new-page-object** Skill.
- Keep **v1** simple; no `DataProvider` unless explicitly requested.

## Verification (Skill complete)

- Draft test path stated (e.g. `src/test/java/.../MyFeatureTest.java`).
- Phase 0 checklist returned with all rows addressed.
- Smoke (or regression) Maven command included.
- No invented page objects or locators.

## Error handling

| Situation | Action |
|-----------|--------|
| Page object missing | Stop; link `new-page-object` Skill |
| AC unclear | Ask user; do not guess product behaviour |
| User wants only regression | Use `groups = {"regression"}` and regression suite command |
| User asks for data-driven v1 | Confirm first; default remains single-method tests like `LoginTest` |

## Mistakes to avoid

- Clicking through UI to create users when `UserApiHelper` exists.
- Putting assertions or locators in page classes while “fixing” the test.
- Omitting `groups` so the test never runs in smoke/regression suites.
- Adding retry or sleep to make a bad test pass — use **triage-test-failure** Skill on failures instead.
