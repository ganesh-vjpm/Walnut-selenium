package com.walnut.automation.pages;

import com.walnut.automation.actions.SeleniumActions;
import org.openqa.selenium.By;

/**
 * Page Object for the Login page.
 * Contains locators and actions performed on the login screen.
 */
public class LoginPage {

    private final SeleniumActions actions;

    // Locators
    private final By logo = By.xpath("//h1[text()='WalnutAI']");
    private final By signInText = By.xpath("//h3[text()='Sign In']");
    private final By emailInput = By.xpath("//input[@type='email']");
    private final By continueButton = By.xpath("//button[@type='submit']");

    public LoginPage(SeleniumActions actions) {
        this.actions = actions;
    }

    public boolean isLogoDisplayed() {
        return actions.isDisplayed(logo);
    }

    public boolean isSignInTextDisplayed() {
        return actions.isDisplayed(signInText);
    }

    public void enterEmail(String email) {
        actions.type(emailInput, email);
    }

    public void clickContinue() {
        actions.click(continueButton);
    }

    public void loginWithEmail(String email) {
        enterEmail(email);
        clickContinue();
    }
}
