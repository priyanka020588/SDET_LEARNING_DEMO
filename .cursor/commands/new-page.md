# New page object (`/new-page`)

Thin trigger for the **new-page-object** Skill. Read and follow `.cursor/skills/new-page-object/SKILL.md` completely — do not duplicate that playbook here.

## Before writing any code

1. **Screen name** — If missing, ask (e.g. `CartPage` for a Demo Shop screen).
2. **Verified locators** — Require `data-test` or CSS the user verified on the real Demo Shop UI. If locators are missing or unverified, **stop and ask**. **Never invent selectors.**
3. **Navigation, actions, return types** — If missing, ask per the Skill’s required-input table.

## Scope (strict)

- Output: **one page class** under `src/main/java/com/company/automation/pages/`, matching `LoginPage` / `BasePage` style.
- **Do not** generate TestNG tests, suites, or locators inside test classes (that is `/new-test`).

## Finish

- Use the Skill’s verification checklist.
- Present the draft for **human review** before commit.
