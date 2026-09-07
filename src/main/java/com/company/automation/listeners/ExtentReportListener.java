package com.company.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.company.automation.base.DriverFactory;
import com.company.automation.utils.ScreenshotUtil;
import java.nio.file.Path;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            Path report = Path.of("reports", "extent-report.html");
            report.toFile().getParentFile().mkdirs();
            ExtentSparkReporter spark = new ExtentSparkReporter(report.toString());
            spark.config().setDocumentTitle("SDET Selenium Demo");
            spark.config().setReportName("Demo Shop regression");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        TEST.set(extent.createTest(result.getMethod().getMethodName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST.get().log(Status.PASS, "Passed");
        TEST.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String screenshot = ScreenshotUtil.capture(DriverFactory.getDriver(), result.getName() + "-extent");
        ExtentTest test = TEST.get();
        test.log(Status.FAIL, result.getThrowable());
        if (!screenshot.isBlank()) {
            test.fail(MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
        }
        TEST.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST.get().log(Status.SKIP, "Skipped");
        TEST.remove();
    }
}
