package com.walnut.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.walnut.automation.actions.SeleniumActions;
import com.walnut.automation.base.BaseTest;
import com.walnut.automation.config.ConfigManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestNG listener that generates an ExtentReports HTML report.
 * Attaches a screenshot to the report when a test fails.
 */
public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        String reportDir = "reports/";
        String reportPath = reportDir + "ExtentReport.html";

        try {
            Path path = Paths.get(reportDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.err.println("Failed to create reports directory: " + e.getMessage());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Walnut Automation Report");
        sparkReporter.config().setReportName("Selenium Test Execution");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Project", "Walnut Automation");
        extent.setSystemInfo("Environment", ConfigManager.getEnvironment());
        extent.setSystemInfo("Browser", ConfigManager.get("browser", "chrome"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        testThreadLocal.set(test);
        test.log(Status.INFO, "Test started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testThreadLocal.get().log(Status.PASS, "Test passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testThreadLocal.get();
        test.log(Status.FAIL, "Test failed: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, result.getThrowable());

        attachScreenshot(result, test);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testThreadLocal.get().log(Status.SKIP, "Test skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    private void attachScreenshot(ITestResult result, ExtentTest test) {
        Object testInstance = result.getInstance();
        if (!(testInstance instanceof BaseTest baseTest)) {
            test.log(Status.WARNING, "Could not attach screenshot: test class does not extend BaseTest");
            return;
        }

        SeleniumActions actions = baseTest.getActions();
        if (actions == null) {
            test.log(Status.WARNING, "Could not attach screenshot: SeleniumActions not initialized");
            return;
        }

        try {
            String screenshotPath = actions.takeScreenshot(result.getName() + "_FAILED");
            if (screenshotPath != null) {
                test.fail("Screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                String base64 = actions.getScreenshotAsBase64();
                if (base64 != null) {
                    test.fail("Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
                }
            }
        } catch (Exception e) {
            test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
        }
    }
}
