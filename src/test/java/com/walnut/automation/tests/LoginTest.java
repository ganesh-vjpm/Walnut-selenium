package com.walnut.automation.tests;

import com.walnut.automation.base.BaseTest;
import com.walnut.automation.config.ConfigManager;
import com.walnut.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Example test class for the login flow.
 */
public class LoginTest extends BaseTest {

    @Test
    public void verifyLoginPageLoads() {
        LoginPage loginPage = new LoginPage(actions);

        Assert.assertTrue(loginPage.isLogoDisplayed(), "Logo is not displayed");
        Assert.assertTrue(loginPage.isSignInTextDisplayed(), "Sign In text is not displayed");
    }

    @Test
    public void loginWithValidEmail() {
        LoginPage loginPage = new LoginPage(actions);
        String email = ConfigManager.get("login.email", "test@example.com");

        loginPage.loginWithEmail(email);

        // Replace with the actual post-login verification for your application
        String expectedFragment = ConfigManager.get("login.success.url.fragment", "verify");
        Assert.assertTrue(actions.getCurrentUrl().contains(expectedFragment),
                "URL did not contain expected fragment after login: " + expectedFragment);
    }
}
