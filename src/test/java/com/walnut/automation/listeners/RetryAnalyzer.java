package com.walnut.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries failed tests up to a configured maximum count.
 * Set maxRetries via system property `max.retries` (default 1).
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int DEFAULT_MAX_RETRIES = 1;
    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        int maxRetries = getMaxRetries();
        if (count < maxRetries) {
            count++;
            System.out.println("Retrying test [" + result.getMethod().getMethodName()
                    + "] - attempt " + count + " of " + maxRetries);
            return true;
        }
        return false;
    }

    private int getMaxRetries() {
        String value = System.getProperty("max.retries");
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid max.retries value: " + value + ". Using default.");
            }
        }
        return DEFAULT_MAX_RETRIES;
    }
}
