package com.company.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import com.company.automation.config.ConfigReader;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigReader.getInt("retryCount");
        if (attempts < max) {
            attempts++;
            return true;
        }
        return false;
    }
}
