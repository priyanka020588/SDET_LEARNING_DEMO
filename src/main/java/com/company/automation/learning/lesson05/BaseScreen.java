package com.company.automation.learning.lesson05;

/**
 * Like {@code BasePage}: holds the shared browser. Children call {@code super(browser)}.
 */
public abstract class BaseScreen {
    protected final FakeBrowser browser;

    protected BaseScreen(FakeBrowser browser) {
        this.browser = browser;
    }
}
