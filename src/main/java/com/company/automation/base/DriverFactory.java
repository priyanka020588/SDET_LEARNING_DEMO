package com.company.automation.base;

import com.company.automation.config.ConfigReader;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void createDriver(String browser) {
        WebDriver driver = switch (browser.toLowerCase()) {
            case "firefox" -> new FirefoxDriver(firefoxOptions());
            default -> new ChromeDriver(chromeOptions());
        };
        // deliberately 0 - never mix implicit and explicit waits
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().window().maximize();
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1400,1000");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (headless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static boolean headless() {
        String override = System.getProperty("headless");
        if (override != null && !override.isBlank()) {
            return Boolean.parseBoolean(override);
        }
        return ConfigReader.getBoolean("headless");
    }
}
