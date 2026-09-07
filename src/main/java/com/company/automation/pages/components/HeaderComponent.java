package com.company.automation.pages.components;

import com.company.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HeaderComponent extends BasePage {
    private final By root = By.cssSelector("[data-test='app-header']");
    private final By userLabel = By.cssSelector("[data-test='header-user']");
    private final By logout = By.cssSelector("[data-test='logout']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(root);
    }

    public String userLabel() {
        return readText(userLabel);
    }

    public void logOut() {
        click(logout);
    }
}
