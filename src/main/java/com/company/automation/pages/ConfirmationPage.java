package com.company.automation.pages;

import com.company.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmationPage extends BasePage {
    private final By heading = By.cssSelector("[data-test='confirmation-heading']");
    private final By confirmation = By.cssSelector("[data-test='confirmation-message']");

    public ConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(heading);
    }

    public String confirmationMessage() {
        return readText(confirmation);
    }
}
