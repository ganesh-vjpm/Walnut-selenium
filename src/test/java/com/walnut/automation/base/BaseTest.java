package com.walnut.automation.base;

import com.walnut.automation.actions.SeleniumActions;
import com.walnut.automation.config.ConfigManager;
import com.walnut.automation.factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Base test class that handles driver lifecycle for every test method.
 * All test classes should extend this class.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected SeleniumActions actions;

    @BeforeMethod
    @Parameters({"browser", "headless"})
    public void setUp(
            @Optional("chrome") String browser,
            @Optional("false") String headless) {

        driver = DriverFactory.createDriver(browser, Boolean.parseBoolean(headless));
        actions = new SeleniumActions(driver);
        actions.maximizeWindow();
        actions.navigateTo(ConfigManager.get("base.url"));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && actions != null) {
            actions.takeScreenshot(result.getName() + "_FAILED");
        }
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    public SeleniumActions getActions() {
        return actions;
    }
}
