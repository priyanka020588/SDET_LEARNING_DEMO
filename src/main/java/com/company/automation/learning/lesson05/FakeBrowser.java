package com.company.automation.learning.lesson05;

/**
 * Tiny stand-in for {@code WebDriver}. Real project: ChromeDriver from DriverFactory.
 */
public class FakeBrowser {
    private String page = "(blank)";

    public void open(String url) {
        page = url;
        System.out.println("browser opened " + url);
    }

    public String currentPage() {
        return page;
    }

    public void goTo(String url) {
        page = url;
        System.out.println("browser now at " + url);
    }
}
