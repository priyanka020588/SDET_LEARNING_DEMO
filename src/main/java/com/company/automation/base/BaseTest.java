package com.company.automation.base;

import com.company.automation.config.ConfigReader;
import com.company.automation.config.Environment;
import com.company.automation.demo.DemoShopServer;
import com.company.automation.utils.ScreenshotUtil;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void startLocalApp() throws IOException {
        if (Environment.current().isLocal()) {
            DemoShopServer.start();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void stopLocalApp() {
        if (Environment.current().isLocal()) {
            DemoShopServer.stop();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.createDriver(ConfigReader.get("browser"));
        getDriver().get(ConfigReader.get("baseUrl"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.capture(getDriver(), result.getName());
        }
        DriverFactory.quitDriver();
    }

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}
