package com.company.automation.pages;

import com.company.automation.base.BasePage;
import com.company.automation.pages.components.CartWidget;
import com.company.automation.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final By heading = By.cssSelector("[data-test='home-heading']");
    private final By welcome = By.cssSelector("[data-test='welcome-text']");
    private final HeaderComponent header;
    private final CartWidget cart;

    public HomePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        this.cart = new CartWidget(driver);
    }

    public boolean isLoaded() {
        return isVisible(heading);
    }

    public String welcomeText() {
        return readText(welcome);
    }

    public HeaderComponent header() {
        return header;
    }

    public CartWidget cart() {
        return cart;
    }

    public HomePage addProductToCart(String productName) {
        click(By.cssSelector("[data-test='add-" + toSlug(productName) + "']"));
        return this;
    }

    public CheckoutPage goToCheckout() {
        return cart.goToCheckout();
    }

    private static String toSlug(String productName) {
        return productName.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
