package com.walnut.automation.pages;

import com.walnut.automation.actions.SeleniumActions;
import org.openqa.selenium.By;

/**
 * Page Object for the Enter Password screen.
 * Contains locators and actions for the password page and post-login welcome text.
 */
public class PasswordPage {

    private final SeleniumActions actions;

    private final By passwordText = By.xpath("//h3[text()='Enter Password']");
    private final By passwordInput = By.xpath("//input[@type='password']");
    private final By signInButton = By.xpath("//div[text()='Sign In']/parent::button");
    private final By welcomeBackText = By.xpath("//h1[text()='Welcome back, ']");

    public PasswordPage(SeleniumActions actions) {
        this.actions = actions;
    }

    /**
     * Builds the dynamic locator for the organization name shown on the password page.
     */
    private By organizationNameText(String orgName) {
        return By.xpath("//span[text()='" + orgName + "']");
    }

    /**
     * Builds the dynamic locator for the email shown on the password page.
     */
    private By emailText(String email) {
        return By.xpath("//p[text()='" + email + "']");
    }

    /**
     * Returns true if the password page is displayed.
     */
    public boolean isAtPasswordPage() {
        try {
            actions.waitForVisible(passwordText);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the expected organization name is displayed on the password page.
     */
    public boolean isOrganizationNameDisplayed(String orgName) {
        try {
            actions.waitForVisible(organizationNameText(orgName));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the expected email is displayed on the password page.
     */
    public boolean isEmailDisplayed(String email) {
        try {
            actions.waitForVisible(emailText(email));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Types the password into the password field.
     */
    public void enterPassword(String password) {
        actions.type(passwordInput, password);
    }

    /**
     * Clicks the Sign In button.
     */
    public void clickSignIn() {
        actions.click(signInButton);
    }

    /**
     * Performs full password step: type password and click Sign In.
     */
    public void signInWithPassword(String password) {
        enterPassword(password);
        clickSignIn();
    }

    /**
     * Returns true if the Welcome back heading is displayed after successful login.
     */
    public boolean isWelcomeBackDisplayed() {
        try {
            actions.waitForVisible(welcomeBackText);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for the Welcome back heading and returns its text.
     */
    public String getWelcomeBackText() {
        return actions.waitForVisible(welcomeBackText).getText();
    }
}
