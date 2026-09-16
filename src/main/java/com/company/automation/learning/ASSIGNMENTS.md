# Java learning lab (maps to this SDET framework)

These classes live under `com.company.automation.learning`. They do **not** run with `mvn test`. In Cursor/IntelliJ, open any `Lesson0XMain.java` and press Run on `main`.

| Lesson | Run this class | Framework idea it explains |
|---|---|---|
| 1 | `lesson01.Lesson01Main` | What a class is; `new` creates an object |
| 2 | `lesson02.Lesson02Main` | Why `User` is a type; wrapping many Strings |
| 3 | `lesson03.Lesson03Main` | `extends` + `super(...)` like `LoginPage` / `BasePage` |
| 4 | `lesson04.Lesson04Main` | Factory + helper like `UserFactory` / `UserApiHelper` |
| 5 | `lesson05.Lesson05Main` | The one-liner: `new LoginPage(driver).loginAs(user)` |

Do the assignments **in order**. Write answers in a scratch file or comments; do not change production pages/tests unless a later ticket asks.

---

## Assignment A — Lesson 1 (objects)

1. In `Student.java`, add a method `introduce()` that returns `"Hi, I am " + name`.
2. In `Lesson01Main`, create a second student (`name = "Sam", age = 30`) and print both.
3. Change only the **object** `ada` (set age to 37). Confirm `sam` does not change. That proves two objects, not one shared blob.

**Check:** you can explain: class = blueprint; object = one actual student in memory; variable = the name that points at it.

---

## Assignment B — Lesson 2 (`User` wrapping)

Look at `LoginTest`:

```java
User user = UserApiHelper.createActiveUser();
new LoginPage(getDriver()).loginAs(user);
```

1. Rewrite `loginAs` in your head as four Strings (`firstName`, `lastName`, `username`, `password`). Why is that worse?
2. In `Lesson02Main`, call `FakeLoginForm.submit(account)` instead of four arguments.
3. Add a method on `Account` named `withPassword(String newPassword)` that returns a **new** Account (copy of names, new password). Same idea as `User.withPassword`.

**Check:** `User` is not a Java built-in. It is a class in `data/User.java`. The type of the variable `user` is `User`.

---

## Assignment C — Lesson 3 (`super`)

1. Draw two boxes: `Screen` (has `title`) and `LoginScreen` (has `usernameField`). Arrow: LoginScreen **is a** Screen.
2. Temporarily remove `super(title)` from `LoginScreen` and read the compiler error. Put it back.
3. Open real code: `LoginPage(WebDriver driver) { super(driver); }` — write one sentence: what does `BasePage` store that `LoginPage` reuses?

**Check:** child constructor must call parent constructor first. `super(driver)` hands the browser to `BasePage`.

---

## Assignment D — Lesson 4 (factory / helper)

1. Call `StudentFactory.unique()` twice. Print both usernames. They must differ (like `ada.` + random token).
2. `StudentDirectory.register(...)` is a stand-in for HTTP. Add a comment in that class: “In the real project this is RestAssured POST /api/users”.
3. Why does the test call `createActiveUser()` **before** Selenium types? Write two bullets.

**Check:** Factory builds data. Helper (or API) **saves** it and returns the same type (`User` / `Student`).

---

## Assignment E — Lesson 5 (the login one-liner)

Read `Lesson05Main` then `LoginTest.validUserCanLogIn`.

1. Match each piece:

   | Mini lab | Real framework |
   |---|---|
   | `new FakeBrowser()` | `DriverFactory` + `getDriver()` |
   | `new LoginScreen(browser)` | `new LoginPage(getDriver())` |
   | `.signIn(account)` | `.loginAs(user)` |
   | return `HomeScreen` | return `HomePage` |

2. Why does `signIn` return `HomeScreen` instead of `void`? (SDET-22 / journey pattern)
3. Why does `signIn` **not** call `browser.open(...)`? Where does open happen in the real project?

**Check:** you can read `HomePage home = new LoginPage(getDriver()).loginAs(user);` out loud as: “create a login page for this browser, log in as this user, get the home page object back.”
