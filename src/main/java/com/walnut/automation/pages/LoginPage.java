package com.walnut.automation.pages;

import com.walnut.automation.actions.SeleniumActions;
import org.openqa.selenium.By;

/**
 * Page Object for the WalnutAI Sign In page.
 * Contains real locators and actions performed on the login screen.
 */
public class LoginPage {

    private final SeleniumActions actions;

    // Locators
    private final By logo = By.xpath("//h1[text()='WalnutAI']");
    private final By signInText = By.xpath("//h3[text()='Sign In']");
    private final By emailLabel = By.xpath("//label[text()='Email']");
    private final By emailInput = By.xpath("//input[@placeholder='you@company.com']");
    private final By continueButton = By.xpath("//div[text()='Continue']/parent::button");
    private final By welcomeToast = By.xpath("//div[contains(text(),'Welcome to')]");

    public LoginPage(SeleniumActions actions) {
        this.actions = actions;
    }

    /**
     * Returns true if the WalnutAI logo is displayed.
     */
    public boolean isLogoDisplayed() {
        return actions.isDisplayed(logo);
    }

    /**
     * Returns true if the Sign In heading is displayed.
     */
    public boolean isSignInTextDisplayed() {
        return actions.isDisplayed(signInText);
    }

    /**
     * Returns true if the Email label is displayed.
     */
    public boolean isEmailLabelDisplayed() {
        return actions.isDisplayed(emailLabel);
    }

    /**
     * Returns true if the email input field is displayed.
     */
    public boolean isEmailInputDisplayed() {
        return actions.isDisplayed(emailInput);
    }

    /**
     * Types the given email into the email input field.
     */
    public void enterEmail(String email) {
        actions.type(emailInput, email);
    }

    /**
     * Clicks the Continue button.
     */
    public void clickContinue() {
        actions.click(continueButton);
    }

    /**
     * Performs the full login step: enter email and click Continue.
     */
    public void loginWithEmail(String email) {
        enterEmail(email);
        clickContinue();
    }

    /**
     * Waits up to the configured timeout for the Welcome toast to appear,
     * then returns true if it is visible.
     */
    public boolean isWelcomeToastDisplayed() {
        try {
            actions.waitForVisible(welcomeToast);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for the Welcome toast to be visible and returns its text.
     */
    public String getWelcomeToastText() {
        return actions.waitForVisible(welcomeToast).getText();
    }
}
