package com.company.automation.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtil {
    private static final Logger LOG = LogManager.getLogger(ScreenshotUtil.class);
    private static final Path SCREENSHOT_DIR = Path.of("reports", "screenshots");

    private ScreenshotUtil() {
    }

    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return "";
        }
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            String fileName = safe(testName) + "-"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"))
                    + ".png";
            Path destination = SCREENSHOT_DIR.resolve(fileName);
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination);
            LOG.info("Saved screenshot {}", destination);
            return destination.toAbsolutePath().toString();
        } catch (IOException exception) {
            LOG.error("Could not save screenshot for {}", testName, exception);
            return "";
        }
    }

    private static String safe(String testName) {
        return testName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
