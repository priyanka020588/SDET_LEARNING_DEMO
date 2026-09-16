package com.company.automation.learning.lesson05;

/** Like {@code HomePage}: a different screen, same browser instance. */
public class HomeScreen extends BaseScreen {

    public HomeScreen(FakeBrowser browser) {
        super(browser);
    }

    public boolean isLoaded() {
        return "/home".equals(browser.currentPage());
    }

    public String welcome(String displayName) {
        return "Hello, " + displayName;
    }
}
