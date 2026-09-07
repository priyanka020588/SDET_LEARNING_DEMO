package com.company.automation.pages;

import com.company.automation.base.BasePage;
import com.company.automation.data.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {
    private final By heading = By.cssSelector("[data-test='checkout-heading']");
    private final By summary = By.cssSelector("[data-test='checkout-summary']");
    private final By firstName = By.cssSelector("[data-test='ship-first-name']");
    private final By lastName = By.cssSelector("[data-test='ship-last-name']");
    private final By address = By.cssSelector("[data-test='ship-address']");
    private final By placeOrder = By.cssSelector("[data-test='place-order']");
    private final By confirmation = By.cssSelector("[data-test='confirmation-message']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(heading);
    }

    public String summaryText() {
        return readText(summary);
    }

    public CheckoutPage fillShipping(User user, String shippingAddress) {
        type(firstName, user.getFirstName());
        type(lastName, user.getLastName());
        type(address, shippingAddress);
        return this;
    }

    public CheckoutPage placeOrder() {
        click(placeOrder);
        return this;
    }

    public String confirmationMessage() {
        return readText(confirmation);
    }
}
