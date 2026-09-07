package com.company.automation.pages;

import com.company.automation.base.BasePage;
import com.company.automation.data.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private final By username = By.cssSelector("[data-test='username']");
    private final By password = By.cssSelector("[data-test='password']");
    private final By loginButton = By.cssSelector("[data-test='login-submit']");
    private final By errorBanner = By.cssSelector("[data-test='login-error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public HomePage loginAs(User user) {
        type(username, user.getUsername());
        type(password, user.getPassword());
        click(loginButton);
        return new HomePage(driver);
    }

    public LoginPage loginExpectingFailure(User user) {
        type(username, user.getUsername());
        type(password, user.getPassword());
        click(loginButton);
        return this;
    }

    public String errorMessage() {
        return readText(errorBanner);
    }
}
