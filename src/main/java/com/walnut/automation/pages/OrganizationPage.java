package com.walnut.automation.pages;

import com.walnut.automation.actions.SeleniumActions;
import org.openqa.selenium.By;

/**
 * Page Object for the Select Organization screen.
 * This page appears after entering the email and clicking Continue
 * when the user belongs to more than one organization.
 */
public class OrganizationPage {

    private final SeleniumActions actions;

    private final By selectOrganizationHeading = By.xpath("//h3[text()='Select Organization']");

    public OrganizationPage(SeleniumActions actions) {
        this.actions = actions;
    }

    /**
     * Builds the dynamic locator for an organization button using the organization name.
     */
    private By organizationButton(String orgName) {
        return By.xpath("//div[text()='" + orgName + "' and @class='text-sm text-muted-foreground']/ancestor::button[@type='button']");
    }

    /**
     * Returns true if the Select Organization page is displayed.
     */
    public boolean isAtOrganizationPage() {
        try {
            actions.waitForVisible(selectOrganizationHeading);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Selects the given organization from the list.
     */
    public void selectOrganization(String orgName) {
        actions.click(organizationButton(orgName));
    }
}
