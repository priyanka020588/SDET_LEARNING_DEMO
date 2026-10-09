package com.company.automation.tests;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AddActionCameraToCartTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void addDjiOsmoPocketToCart() {
        driver.get("https://www.amazon.in/");
        dismissInterstitialIfPresent();

        openAllMenu();
        clickMenuItem("TV, Audio");
        clickMenuItem("Cameras");
        clickLink("Action Cameras");
        openProduct("DJI Osmo Pocket 4P");
        addToCart();

        Assert.assertTrue(isAddedToCart(), "Product was not added to the cart");
    }

    private void openAllMenu() {
        click(By.id("nav-hamburger-menu"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("hmenu-content")));
    }

    /** Clicks a row in the open All menu. Matches partial text so a longer label still works. */
    private void clickMenuItem(String visibleText) {
        By item = By.xpath(
                "//div[@id='hmenu-content']//a[contains(@class,'hmenu-item')]"
                        + "[contains(normalize-space(.),\"" + visibleText + "\")]");
        WebElement menuItem = wait.until(ExpectedConditions.elementToBeClickable(item));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'})", menuItem);
        menuItem.click();
    }

    /** Used once we leave the side menu and land on a category page. */
    private void clickLink(String visibleText) {
        By link = By.xpath("//a[contains(normalize-space(.),\"" + visibleText + "\")]");
        click(link);
    }

    private void openProduct(String titleFragment) {
        By product = By.xpath(
                "//span[contains(normalize-space(.),\"" + titleFragment + "\")]");
        click(product);
    }

    private void addToCart() {
        click(By.id("add-to-cart-button"));
        dismissProtectionPlanIfPresent();
    }

    private boolean isAddedToCart() {
        By confirmation = By.xpath(
                "//*[contains(translate(normalize-space(.),'ADDED TO CART','added to cart'),'added to cart')]"
                        + " | //*[@id='nav-cart-count' and number(text()) > 0]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmation)).isDisplayed();
    }

    private void dismissInterstitialIfPresent() {
        By continueShopping = By.xpath("//button[normalize-space()='Continue shopping']");
        if (!driver.findElements(continueShopping).isEmpty()) {
            click(continueShopping);
        }
    }

    private void dismissProtectionPlanIfPresent() {
        By noThanks = By.id("attachSiNoCoverage");
        if (!driver.findElements(noThanks).isEmpty()) {
            click(noThanks);
        }
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}