# Java + Selenium revision: locators and UI actions

Use this before a discussion. The project is Java 17 and Selenium 4.27. Official pages worth keeping open:

- [Locators](https://www.selenium.dev/documentation/webdriver/elements/locators/)
- [Element interactions](https://www.selenium.dev/documentation/webdriver/elements/interactions/)
- [Select lists](https://www.selenium.dev/documentation/webdriver/support_features/select_lists/)
- [Actions API](https://www.selenium.dev/documentation/webdriver/actions_api/)
- [Scroll](https://www.selenium.dev/documentation/webdriver/actions_api/wheel/)
- [Waits](https://www.selenium.dev/documentation/webdriver/waits/)

A locator finds the element. An action is what you do after you have found it. In a discussion, say the locator first, then the action, then how you know the action worked.

## How to choose a locator

Prefer the locator that stays the same when the page layout shifts.

| Order | Use this when | Avoid it when |
| --- | --- | --- |
| 1. `id` | The element has a unique `id` | The id is generated, such as `id="input-3841"` |
| 2. `name` | A form field has a stable `name` | Several fields share that name |
| 3. CSS selector | You can point at a stable attribute or a short parent-child path | You need to match visible text |
| 4. Link text | The target is an `<a>` and the link words are stable | The same words appear on more than one link |
| 5. XPath | You must match visible text, or walk up to a parent | You are copying a long path from the browser |

Say this out loud if they ask “which locator is best?”: I take `id` when it is unique and written by the application. I take CSS for structure. I take XPath when the only stable thing is the text the user sees. I do not take the absolute path the browser copies.

A bad locator from DevTools looks like this:

```java
By.xpath("/html/body/div[3]/div[1]/div[2]/a")
```

`div[3]` breaks the day someone inserts a banner. A better version names the thing:

```java
By.id("nav-hamburger-menu")
By.cssSelector("a[aria-label='Open All Categories Menu']")
By.xpath("//a[@id='nav-hamburger-menu']//span[normalize-space()='All']")
```

Two more rules:

- `findElement` returns the first match and throws if nothing matches. `findElements` returns a list, and an empty list means “not on the page.” Use the list when you are checking that something is absent.
- Scope the search. `//a[contains(.,'Cameras')]` can hit a hidden menu row and a footer link. `//div[@id='hmenu-content']//a[normalize-space()='Cameras']` stays inside the open menu.

## Locator syntax

Every locator is a `By`. You pass it to `findElement`.

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

WebElement allMenu = driver.findElement(By.id("nav-hamburger-menu"));
```

### id, name, class, tag, link

```java
By.id("nav-hamburger-menu")
By.name("email")
By.className("hm-icon-label")      // one class token, not "hm-icon nav-sprite"
By.tagName("button")
By.linkText("Action Cameras")      // full visible text of an <a>
By.partialLinkText("Action")       // part of an <a> only
```

`className` fails if you pass two classes. For that, use CSS: `By.cssSelector(".hm-icon.nav-sprite")`.

### CSS

| Goal | Syntax |
| --- | --- |
| id | `#nav-hamburger-menu` |
| class | `.hm-icon-label` |
| tag + class | `a.hmenu-item` |
| attribute equals | `input[name='email']` |
| attribute contains | `a[href*='cameras']` |
| attribute starts with | `button[id^='add-to-cart']` |
| direct child | `#nav-main > div.nav-left` |
| any descendant | `#hmenu-content a.hmenu-item` |
| nth child | `ul.results > li:nth-child(2)` |

```java
By.cssSelector("#hmenu-content a.hmenu-item")
By.cssSelector("button[type='submit']")
```

CSS cannot match text, and it cannot go up to a parent. That is when XPath earns its place.

### XPath

An XPath is a string inside `By.xpath("...")`. In Java the method is `findElement`, and the argument uses parentheses and quotes.

```java
driver.findElement(By.xpath("//*[@id='nav-hamburger-menu']")).click();
```

This does not compile:

```java
driver.findelement(By.xpath[//*[@id="nav-main"]/div[1]).click();
```

`findelement` is the wrong method name. `By.xpath` does not take square brackets. The path is missing quotes, and `div[1)` is not closed.

| Goal | XPath |
| --- | --- |
| Any tag with an id | `//*[@id='nav-hamburger-menu']` |
| Exact visible text | `//span[normalize-space()='All']` |
| Text contains a phrase | `//span[contains(normalize-space(.),'Osmo Pocket')]` |
| Class contains a token | `//a[contains(@class,'hmenu-item')]` |
| Inside a known parent | `//div[@id='hmenu-content']//a[normalize-space()='Cameras']` |
| Parent of a known child | `//span[normalize-space()='All']/ancestor::a` |
| Following control in a form | `//label[normalize-space()='Quantity']/following::select[1]` |

`normalize-space()` collapses extra spaces and line breaks. Use it for labels. Prefer `=` over `contains` when the shorter word is also inside a longer label. `contains(.,'Camera')` matches “Cameras” and “Camera Accessories”. `normalize-space()='Cameras'` matches one row.

`.` means “this element’s text, including children.” `text()` means only the text node on that tag. A label split across a child `span` is safer with `.`.

## What you can do on the element

Find it, then act. Wait until it is ready before the action. This project already does that in `BasePage`: click only after `elementToBeClickable`, type only after the field is visible.

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
WebElement menu = wait.until(
        ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu")));
menu.click();
```

Do not mix this with an implicit wait. Two wait styles on one driver make timeouts hard to explain.

### Read the page

```java
element.getText();                         // visible text
element.getAttribute("value");             // value of an input, or any attribute
element.getDomProperty("value");           // current DOM property, Selenium 4
element.isDisplayed();
element.isEnabled();
element.isSelected();                      // checkbox, radio, selected <option>
```

`getText()` returns what the user can see. A hidden menu row often returns an empty string. If you need the typed value of an input, ask for `value`, not `getText()`.

### Click, type, clear

```java
wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-hamburger-menu"))).click();

WebElement search = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
search.clear();
search.sendKeys("action camera");
search.submit();   // only when the element sits inside a form
```

`clear()` before `sendKeys()` matters. Otherwise you append to text that is already there.

### Dropdowns: the Select class

`Select` works only on a real `<select>`. A menu built from `div` and `a` tags is just a series of clicks. On Amazon.in, the department box beside search is a `<select id="searchDropdownBox">`. The left **All** control is an `<a>`, so `Select` does not apply to it.

Docs: [Working with select list elements](https://www.selenium.dev/documentation/webdriver/support_features/select_lists/).

```java
import org.openqa.selenium.support.ui.Select;

WebElement category = driver.findElement(By.id("searchDropdownBox"));
Select select = new Select(category);

// read
for (WebElement option : select.getOptions()) {
    System.out.println(option.getText());
}
String alreadySelected = select.getFirstSelectedOption().getText();

// choose one
select.selectByVisibleText("Books");
select.selectByValue("search-alias=stripbooks");
select.selectByIndex(3);
```

`selectByIndex` counts from 0, but it uses the option’s `index` attribute, not “the third thing I see.” Prefer visible text in a discussion. For a multi-select, `deselectAll()`, `deselectByVisibleText`, and `getAllSelectedOptions()` are the matching calls.

### Several actions in one go: the Actions class

Build the chain, then call `perform()`. Nothing runs before `perform()`.

Docs: [Actions API](https://www.selenium.dev/documentation/webdriver/actions_api/).

```java
import org.openqa.selenium.interactions.Actions;

WebElement allMenu = driver.findElement(By.id("nav-hamburger-menu"));
new Actions(driver)
        .scrollToElement(allMenu)
        .moveToElement(allMenu)
        .click()
        .perform();
```

| Method | What the user would do |
| --- | --- |
| `click()` / `click(element)` | Left click |
| `doubleClick(element)` | Double click |
| `contextClick(element)` | Right click |
| `moveToElement(element)` | Hover. Use this before a menu that opens on hover |
| `dragAndDrop(source, target)` | Drag one element onto another |
| `clickAndHold(element)` then `release()` | Press, move, let go |
| `keyDown(Keys.SHIFT)` / `keyUp(Keys.SHIFT)` | Hold a modifier |
| `sendKeys("hello")` | Type through the keyboard, focus must already be in the field |
| `scrollToElement(element)` | Bring the element into the window |
| `scrollByAmount(0, 400)` | Scroll the window by pixels. Positive is down and right |
| `pause(Duration.ofMillis(300))` | Short gap inside a chain, when a hover needs a beat to draw |

Scroll details: [Scroll wheel actions](https://www.selenium.dev/documentation/webdriver/actions_api/wheel/).

`scrollToElement` moves the page. It does not scroll a panel that has its own scrollbar. The Amazon **All** menu is that kind of panel. For an inner list, scroll the row itself:

```java
((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block:'center'})", menuItem);
menuItem.click();
```

Say why you used JavaScript: the element was in the DOM, but it sat below the fold inside its own panel.

### Frames, alerts, and windows

The element can be on the page and still be unreachable.

```java
driver.switchTo().frame("iframe-id");     // or a WebElement
driver.switchTo().defaultContent();       // back to the main page

Alert alert = wait.until(ExpectedConditions.alertIsPresent());
alert.accept();                           // OK
alert.dismiss();                          // Cancel
alert.sendKeys("answer");

String original = driver.getWindowHandle();
// after a click that opens a tab
for (String handle : driver.getWindowHandles()) {
    if (!handle.equals(original)) {
        driver.switchTo().window(handle);
    }
}
driver.switchTo().window(original);
```

If a click throws `ElementClickInterceptedException`, something is covering the target: a cookie banner, a “Continue shopping” wall, or a warranty popup. Dismiss that layer, then click again. Do not jump to a JavaScript click as the first fix.

## A short way to talk through one step

“The **All** control is the link `nav-hamburger-menu`. I wait until it is clickable, then I click it. I know the menu opened because `hmenu-content` is visible. Inside that panel I click the row whose text is `Cameras`, after scrolling that row into the panel. I do not click `div[1]` under `nav-main`, because that is the wrapper, and the click handler is on the link.”

That answer has a locator, an action, and a check. That is the shape they are listening for.
