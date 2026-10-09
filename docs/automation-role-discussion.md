# How I talk about this automation work

Read this before the discussion. Say it in your own pace. The folder map is in `sdet-project-structure.md`. Locator and action syntax is in `docs/selenium-locators-and-actions.md`. This note is the conversation.

The line to land early:

**Tests describe what should happen. The framework knows how to drive the browser.**

---

## Open (about a minute)

I have been working in a Java and Selenium project built the way a team would actually run it, not as one long script.

The application under test is a small shop. The suite logs in, checks the catalog, adds a product, and completes checkout. The framework is Java 17, Maven, TestNG, and Selenium 4. Page objects hold the locators. Tests hold the assertions. A driver factory owns the browser. Config decides the URL and the browser, so the same tests run locally and in CI.

I can pick up a story, find the page class, add the action, and put the check in a TestNG test. I can also tell a product bug from a bad wait, because failures leave a screenshot, a log, and an HTML report.

If they ask “what would you do on our project on day one?”, say: I would learn the page objects and the smoke suite first, run smoke locally, and only then add a test. I would not start by rewriting the framework.

---

## If they say “walk me through the framework”

Use this order. Do not start with tools.

**1. Two folders.**

`src/main/java` is the framework: driver, base classes, pages, config, API helpers, listeners. None of that is a test.

`src/test/java` is only tests. `LoginTest` and `CheckoutTest` call page methods and assert. They do not contain CSS selectors.

That split is what makes it a framework. A new test does not copy driver setup. A UI change is fixed in one page class.

**2. What happens when a test starts.**

`BaseTest` runs before every test. It asks `DriverFactory` for Chrome or Firefox, then opens `baseUrl` from `config.properties`. After the test, if it failed, we take a screenshot and then quit the browser.

The driver sits in a `ThreadLocal`. Regression runs two tests at once (`parallel="methods"`, `thread-count="2"`). A single static `WebDriver` would make those two tests steal each other’s browser. I mention that only if they ask about parallel. It is a real design choice, not a buzzword.

**3. How a page is written.**

`LoginPage` keeps locators as fields and exposes actions:

- `loginAs` types the username and password, clicks submit, and returns a `HomePage`.
- `loginExpectingFailure` stays on the login page so the test can read the error.

Locators on this shop are `data-test` attributes, for example `[data-test='username']`. I prefer that over a CSS class, because classes change for styling and `data-test` is there for automation.

`BasePage` is where click, type, and read live. Every one of them waits first. Click waits until the element is clickable. Type waits until it is visible, clears it, then sends keys. Implicit wait is set to zero on purpose. I do not mix it with explicit waits.

Assertions stay in the test. `LoginPage` can read the error text. `LoginTest` decides that the text must equal the expected message. If the assertion lived in the page, that page could only be used for one check.

**4. One journey I can narrate.**

Checkout is the example. I do not click through signup to create the user.

`UserApiHelper.createActiveUser()` POSTs a new user and returns it. The UI test starts at login with a user that already exists. Catalog name and price come from `products.xlsx`, not from a string I typed in the test.

Then the test reads like the story:

1. Log in, and get a `HomePage`.
2. Add that product. The cart widget should show 1.
3. Open checkout. The summary should contain the product name.
4. Enter shipping and place the order.
5. The confirmation should contain the user’s first name and the price.

Header and cart are components, not copied onto every page. `HomePage` holds a `HeaderComponent` and a `CartWidget`.

**5. How we know a run is healthy.**

Smoke is the small set: the API is up, and a user can reach the home page. It is what I want on a pull request.

Regression is the wider set, including wrong password, empty username, and checkout. It runs with two threads.

Locally:

```bash
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml
./mvnw test -DsuiteXmlFile=src/test/resources/suites/regression.xml
```

GitHub Actions runs smoke on a pull request and regression on a nightly schedule, headless. Jenkins on a feature branch runs smoke, and `main` runs regression. After a run I look at the Extent HTML report, screenshots under `reports/screenshots/`, and `logs/automation.log`.

A retry of one (`retryCount=1`) exists for a flake. I would not raise that number to hide a real bug. If it fails twice, I treat it as a failure and read the screenshot.

---

## How I choose a locator when the UI is not ours

This is the part that shows I can work on a real application, not only on `data-test` attributes.

I ask three questions:

1. Is there a stable `id` or a `data-test` / `data-testid`?
2. If not, can CSS point at a stable attribute?
3. Only then XPath, and only as short as the visible text or the parent I trust.

I do not paste the absolute path from the browser. `div[3]/div[1]` breaks when a banner is inserted.

On a public site I practised this with the left **All** menu. The control is the link `nav-hamburger-menu`, not the “All Categories” box in the search bar, and not the first `div` under the nav. The click handler is on the link. After the menu opens I scope the next click inside `hmenu-content`, because the same word can exist in a hidden row. If the row sits inside a panel that scrolls on its own, I scroll that row into view, then click.

`Select` is only for a real `<select>`. A menu made of links is a click, not `selectByVisibleText`.

Syntax I should be able to write without looking it up:

```java
driver.findElement(By.id("nav-hamburger-menu")).click();
driver.findElement(By.cssSelector("[data-test='login-submit']")).click();
driver.findElement(By.xpath("//span[normalize-space()='All']"));
```

`findElement` takes a `By` in parentheses. `By.xpath` takes a quoted string.

---

## What I want them to believe

| They worry that… | What I show |
| --- | --- |
| I only record and playback | Tests call page methods. Locators are not in the test. |
| My tests depend on each other | Each test creates its own user through the API. The browser opens and closes per test. |
| I click the whole setup in the UI | User creation is an HTTP POST. The UI test starts at the behaviour we care about. |
| I use `Thread.sleep` | Waits are explicit, in `BasePage`. Implicit wait is zero. |
| I cannot run this in a pipeline | Smoke and regression are suite XML files. CI already calls them headless. |
| A red test is a mystery | Screenshot, Extent report, and log. I separate “element not ready”, “locator wrong”, and “product wrong”. |
| I would freeze on a page with no test ids | I can justify id, then CSS, then a short XPath, and I can say why I rejected a positional path. |

---

## Questions they may ask, and a short answer

**Why page objects?**
When the login button changes, I edit `LoginPage`. Five tests keep calling `loginAs`.

**Why is the assertion not in the page?**
The page tells me what the screen says. The test decides whether that is correct. The same page method can serve a positive test and a negative test.

**Why create the user by API?**
Signup is a different feature. If signup breaks, every checkout test would fail for the wrong reason. The API gives a clean user in one call.

**How do you handle a flaky test?**
I look at the screenshot first. If the element was late, I fix the wait condition, not the timeout number. If the locator matched the wrong node, I tighten it. Retry once is a safety net, not the fix.

**How do you run only smoke?**
TestNG groups. Smoke suite includes the `smoke` group. Regression includes `smoke` and `regression`.

**Chrome and Selenium versions disagree. Does the suite die?**
A DevTools version warning is not a failed test. Normal click and find still work. I would still plan a Selenium upgrade so the driver matches the browser, but I would not call that warning the root cause of a failed assertion.

**What would you add next on a real team?**
A failing build that blocks merge, a rule that new screens get a `data-test` attribute, and a check that smoke stays short enough to run on every pull request.

---

## What I will not claim

- I will not say the Amazon flow is part of this suite. It was a locator exercise. This framework’s suites run against the Demo Shop.
- I will not say we retry until green.
- I will not say every locator in the world should be XPath.
- I will not describe CI as “it just runs.” I can name smoke on the pull request and regression nightly.

---

## Close

If I join an automation project, I already work in this shape: page objects, explicit waits, test data from an API, a small smoke suite on the change, and a report I can open when something fails. The first week I would learn your pages and your smoke suite, then add tests in that style.
