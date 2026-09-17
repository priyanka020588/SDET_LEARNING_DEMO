# Selenium automation framework (Java + Maven + TestNG)

Working implementation of the structure in `sdet-project-structure.md`. Tests run against an **embedded Demo Shop** started automatically in `@BeforeSuite`, so you do not need an external website.

## Prerequisites

- Java 17+
- Google Chrome (Firefox is supported via `browser=firefox`)

Maven is included as `./mvnw`.

## CI

**Local Jenkins** (this Mac): http://localhost:8080/job/sdet-demo/

The `sdet-demo` job is a multibranch pipeline. After you **commit** on any branch that has `Jenkinsfile`, Jenkins scans the repo every 2 minutes and runs:

- feature branches → smoke
- `main` → regression

Build with Parameters if you want to force `smoke` or `regression`. On the build page: **Test Result** (JUnit), **Extent Report** (HTML Publisher), and archived `reports/` / `logs/`.

**GitHub Actions** (hosted): `.github/workflows/ci.yml` — smoke on PRs, regression nightly. See `docs/ci.md`.

## Run tests

```bash
# all tests (testng.xml)
./mvnw test

# smoke only (fast set)
./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml

# full regression, 2 parallel methods
./mvnw test -DsuiteXmlFile=src/test/resources/suites/regression.xml

# headed Chrome
./mvnw test -Dheadless=false
```

## What you get after a run

- `reports/extent-report.html` — HTML report
- `reports/screenshots/` — failure screenshots
- `logs/automation.log` — run log
- `target/surefire-reports/` — TestNG/Surefire output

## Layout

`src/main/java` is the reusable framework (pages, driver factory, API helpers, listeners).  
`src/test/java` holds only tests and assertions.

Test data is created through `UserApiHelper.createActiveUser()` (HTTP POST), not by clicking a signup form. Catalog names and prices come from `src/test/resources/testdata/products.xlsx`.

Added Comment from branch login

