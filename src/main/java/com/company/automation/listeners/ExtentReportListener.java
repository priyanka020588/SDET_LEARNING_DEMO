package com.company.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.company.automation.base.DriverFactory;
import com.company.automation.utils.ScreenshotUtil;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
    private static final DateTimeFormatter REPORT_STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            Path reportsDir = Path.of("reports");
            reportsDir.toFile().mkdirs();
            String stamp = LocalDateTime.now().format(REPORT_STAMP);
            Path archived = reportsDir.resolve("extent-report-" + stamp + ".html");
            Path latest = reportsDir.resolve("extent-report.html");
            ExtentSparkReporter archivedReporter = new ExtentSparkReporter(archived.toString());
            ExtentSparkReporter latestReporter = new ExtentSparkReporter(latest.toString());
            archivedReporter.config().setDocumentTitle("SDET Selenium Demo");
            archivedReporter.config().setReportName("Demo Shop regression");
            latestReporter.config().setDocumentTitle("SDET Selenium Demo");
            latestReporter.config().setReportName("Demo Shop regression");
            extent = new ExtentReports();
            extent.attachReporter(archivedReporter, latestReporter);
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
