package com.walnut.automation.tests;

import com.walnut.automation.base.BaseTest;
import com.walnut.automation.config.ConfigManager;
import com.walnut.automation.pages.LoginPage;
import com.walnut.automation.pages.OrganizationPage;
import com.walnut.automation.pages.PasswordPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for the WalnutAI sign-in flow.
 */
public class LoginTest extends BaseTest {

    /**
     * Verifies that the Sign In page loads correctly with all expected elements.
     */
    @Test
    public void verifySignInPageLoads() {
        LoginPage loginPage = new LoginPage(actions);

        Assert.assertTrue(loginPage.isLogoDisplayed(), "WalnutAI logo is not displayed");
        Assert.assertTrue(loginPage.isSignInTextDisplayed(), "Sign In text is not displayed");
        Assert.assertTrue(loginPage.isEmailLabelDisplayed(), "Email label is not displayed");
        Assert.assertTrue(loginPage.isEmailInputDisplayed(), "Email input is not displayed");
    }

    /**
     * Enters the configured email and clicks Continue.
     * Verifies that the Welcome toast appears after successful sign-in.
     */
    @Test
    public void signInWithValidEmail() {
        LoginPage loginPage = new LoginPage(actions);
        String email = ConfigManager.get("login.email");

        Assert.assertNotNull(email, "login.email must be set in the properties file");

        loginPage.loginWithEmail(email);

        Assert.assertTrue(loginPage.isWelcomeToastDisplayed(),
                "Welcome toast is not displayed after sign-in");
        Assert.assertTrue(loginPage.getWelcomeToastText().contains("Welcome to"),
                "Welcome toast text does not contain 'Welcome to'");
    }

    /**
     * Performs the complete end-to-end login flow:
     * 1. Enter email and click Continue.
     * 2. Select organization if the organization selection page appears.
     * 3. Enter password and click Sign In.
     * 4. Verify the Welcome back heading is displayed.
     */
    @Test
    public void completeLoginFlow() {
        String email = ConfigManager.get("login.email");
        String password = ConfigManager.get("login.password");
        String organization = ConfigManager.get("login.organization");

        Assert.assertNotNull(email, "login.email must be set");
        Assert.assertNotNull(password, "login.password must be set");
        Assert.assertNotNull(organization, "login.organization must be set");

        LoginPage loginPage = new LoginPage(actions);
        loginPage.loginWithEmail(email);

        OrganizationPage organizationPage = new OrganizationPage(actions);
        if (organizationPage.isAtOrganizationPage()) {
            organizationPage.selectOrganization(organization);
        }

        PasswordPage passwordPage = new PasswordPage(actions);
        Assert.assertTrue(passwordPage.isAtPasswordPage(), "Password page is not displayed");
        Assert.assertTrue(passwordPage.isOrganizationNameDisplayed(organization),
                "Organization name is not displayed on password page");
        Assert.assertTrue(passwordPage.isEmailDisplayed(email),
                "Email is not displayed on password page");

        passwordPage.signInWithPassword(password);

        Assert.assertTrue(passwordPage.isWelcomeBackDisplayed(),
                "Welcome back heading is not displayed after login");
        Assert.assertTrue(passwordPage.getWelcomeBackText().contains("Welcome back"),
                "Welcome back text does not contain 'Welcome back'");
    }
}
