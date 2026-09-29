---
name: new-page-object
description: >-
  Generate a page class under src/main/java/com/company/automation/pages/ that
  matches LoginPage and BasePage style. Use when the user asks for a new page
  object, POM for a screen, or page class with verified locators. Refuse to
  invent data-test or CSS selectors.
---

# New Page Object

Create page classes in this framework’s style — not a generic Selenium tutorial.

## Prerequisites

- Read operating and framework rules (locators in pages only, no assertions in pages).
- Gold standard: `LoginPage.loginAs(User)` returns `HomePage`; failure path returns `LoginPage` (`return this`).

## Required user input

| Field | Required | Notes |
|-------|----------|-------|
| **Screen name** | Yes | Class name basis (e.g. `CheckoutPage`). |
| **Navigation** | Yes | URL path, or how the user reaches the screen (from which page/action). |
| **Locators** | Yes | **Verified** `data-test` or CSS from the real app. If missing, **stop and ask** — do not guess. |
| **Actions** | Yes | What the user can do on the screen (click, type, navigate). |
| **Next page types** | Yes | Return type for each journey action (e.g. success → `HomePage`, stay on screen → `this`). |

Optional: component reuse (`HeaderComponent`, `CartWidget`) when the screen shares chrome with existing pages.

## Workflow

```
Task Progress:
- [ ] Step 1: Confirm inputs (especially verified locators)
- [ ] Step 2: Read reference pages
- [ ] Step 3: Draft page class (human review before commit)
- [ ] Step 4: Self-check verification checklist
```

### Step 1: Confirm inputs

- If locators are not verified against the Demo Shop UI, **refuse** to generate the class. Ask the user to inspect the app or provide selectors.
- Do not invent `[data-test='...']` values.

### Step 2: Read reference pages

Read end to end before writing:

1. `src/main/java/com/company/automation/pages/LoginPage.java`
2. `src/main/java/com/company/automation/pages/HomePage.java`
3. `src/main/java/com/company/automation/base/BasePage.java`
4. `src/main/java/com/company/automation/pages/components/CartWidget.java`

Match: `private final By` locators, constructor `super(driver)`, actions use `click` / `type` / `readText` / `isVisible` from `BasePage`.

### Step 3: Draft page class

- **Package:** `com.company.automation.pages` (or `...pages.components` for widgets).
- **Path:** `src/main/java/com/company/automation/pages/<Name>Page.java`
- **Pattern:** actions return the **next page** in the journey; failure or “still on this screen” returns `this`.
- **No** TestNG, AssertJ, or `assert*` in the page class.

Present the draft to the user for **approval before commit** (same as Jira/test generation guardrail).

### Step 4: Verification checklist

Before finishing, confirm:

| Check | Required |
|-------|----------|
| Locators are `private` | Yes |
| Extends `BasePage` | Yes |
| Locators only from user-verified list | Yes |
| No assertions in page | Yes |
| Actions return a page type (`HomePage`, `this`, etc.) | Yes |
| No `Thread.sleep`; use `BasePage` waits | Yes |

Return this table to the user with pass/fail notes.

## Error handling

| Situation | Action |
|-----------|--------|
| User asks for locators not provided | Stop; ask for verified selectors or UI inspection |
| User wants assertions in the page | Refuse; explain assertions belong in `src/test/java/**` |
| Action return type unclear | Ask which page the user lands on after the action |
| New screen shares header/cart | Suggest `HeaderComponent` / `CartWidget` like `HomePage` |

## Reference snippets

**Journey (success):** `LoginPage.loginAs` → `HomePage`

**Journey (failure on same screen):** `LoginPage.loginExpectingFailure` → `return this`

**Paths:**

- `src/main/java/com/company/automation/pages/LoginPage.java`
- `src/main/java/com/company/automation/pages/HomePage.java`
