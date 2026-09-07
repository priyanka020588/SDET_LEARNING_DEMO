package com.company.automation.pages.components;

import com.company.automation.base.BasePage;
import com.company.automation.pages.CheckoutPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartWidget extends BasePage {
    private final By count = By.cssSelector("[data-test='cart-count']");
    private final By checkoutLink = By.cssSelector("[data-test='cart-checkout']");

    public CartWidget(WebDriver driver) {
        super(driver);
    }

    public int itemCount() {
        return Integer.parseInt(readText(count));
    }

    public CheckoutPage goToCheckout() {
        click(checkoutLink);
        return new CheckoutPage(driver);
    }
}
