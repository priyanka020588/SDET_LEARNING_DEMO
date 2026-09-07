package com.company.automation.listeners;

import com.company.automation.base.DriverFactory;
import com.company.automation.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("Starting {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("Passed {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("Failed {}", result.getMethod().getMethodName(), result.getThrowable());
        ScreenshotUtil.capture(DriverFactory.getDriver(), result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("Skipped {}", result.getMethod().getMethodName());
    }
}
