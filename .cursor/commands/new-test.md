# New TestNG test (`/new-test`)

Thin trigger for the **new-testng-test** Skill. Read and follow `.cursor/skills/new-testng-test/SKILL.md` completely — do not duplicate that playbook here.

## Before writing any code

Collect and confirm **all** of the following. If anything is missing, **ask** — do not generate a test yet.

1. **Page object** — Which page class and journey methods the test will use (e.g. `LoginPage.loginAs`, `CheckoutPage.placeOrder`). **Verify the class exists** under `src/main/java/com/company/automation/pages/`.
2. **Acceptance criteria** — What success (and failure, if relevant) looks like — from Jira, story, or user paste.
3. **Suite group** — **`smoke`** or **`regression`** only. Every `@Test` must use `@Test(groups = {"smoke"})` or `@Test(groups = {"regression"})` to match `smoke.xml` / `regression.xml`.

## Page must exist (hard stop)

If the required **page class or journey method does not exist**:

- **STOP.** Do not invent locators, pages, or inline page logic in the test.
- Tell the user to run **`/new-page`** first (`.cursor/commands/new-page.md` → **new-page-object** Skill), with verified locators.
- Do not continue until the page exists in the repo.

## Scope (strict)

- Output: **one TestNG test class** under `src/test/java/com/company/automation/tests/`, matching `LoginTest` / `BaseTest` patterns.
- **Do not** create or edit page objects in this command (that is **`/new-page`**).

## After the draft

1. Apply the Skill’s **Phase 0 evaluation checklist** and return the checklist table to the user.
2. Present the draft for **human review** — a draft is not a pass; **stop for approval** before commit unless the user explicitly asks to commit.
3. After approval, remind the user to **run tests locally** (from `README.md`):
   - Smoke: `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml`
   - If the new test is **regression-only**: `./mvnw test -DsuiteXmlFile=src/test/resources/suites/regression.xml`

Do not skip the Maven run reminder in your summary.
