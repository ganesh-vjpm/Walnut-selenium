package com.walnut.automation.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class SeleniumActions {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumActions.class);
    private static final int DEFAULT_TIMEOUT = 10;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;
    private final JavascriptExecutor jsExecutor;

    public SeleniumActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public SeleniumActions(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    // ===================== CONSTRUCTOR HELPERS =====================

    public void setImplicitWait(long seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    public void resetImplicitWait() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    // ===================== CLICK ACTIONS =====================

    public void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        logger.info("Clicked on element: {}", locator);
    }

    public void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        logger.info("Clicked on element: {}", element);
    }

    public void clickWithOffset(By locator, int x, int y) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.moveToElement(element, x, y).click().perform();
        logger.info("Clicked on element with offset x={}, y={}: {}", x, y, locator);
    }

    public void doubleClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.doubleClick(element).perform();
        logger.info("Double clicked on element: {}", locator);
    }

    public void rightClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.contextClick(element).perform();
        logger.info("Right clicked on element: {}", locator);
    }

    public void hover(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        actions.moveToElement(element).perform();
        logger.info("Hovered on element: {}", locator);
    }

    public void hover(WebElement element) {
        actions.moveToElement(element).perform();
        logger.info("Hovered on element: {}", element);
    }

    public void clickUsingJavaScript(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].click();", element);
        logger.info("Clicked using JavaScript on element: {}", locator);
    }

    public void clickUsingJavaScript(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
        logger.info("Clicked using JavaScript on element: {}", element);
    }

    public void safeClick(By locator) {
        try {
            click(locator);
        } catch (Exception e) {
            logger.warn("Normal click failed, trying JavaScript click for: {}", locator);
            clickUsingJavaScript(locator);
        }
    }

    // ===================== DRAG AND DROP =====================

    public void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = wait.until(ExpectedConditions.elementToBeClickable(sourceLocator));
        WebElement target = wait.until(ExpectedConditions.elementToBeClickable(targetLocator));
        actions.dragAndDrop(source, target).perform();
        logger.info("Dragged element {} and dropped on {}", sourceLocator, targetLocator);
    }

    public void dragAndDropByOffset(By sourceLocator, int xOffset, int yOffset) {
        WebElement source = wait.until(ExpectedConditions.elementToBeClickable(sourceLocator));
        actions.dragAndDropBy(source, xOffset, yOffset).perform();
        logger.info("Dragged element {} by offset x={}, y={}", sourceLocator, xOffset, yOffset);
    }

    public void clickAndHold(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.clickAndHold(element).perform();
        logger.info("Clicked and held element: {}", locator);
    }

    public void release(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.release(element).perform();
        logger.info("Released element: {}", locator);
    }

    public void moveToLocation(int x, int y) {
        actions.moveByOffset(x, y).perform();
        logger.info("Moved mouse to location x={}, y={}", x, y);
    }

    public void moveToElement(By locator, int x, int y) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        actions.moveToElement(element, x, y).perform();
        logger.info("Moved mouse to element with offset x={}, y={}: {}", x, y, locator);
    }

    // ===================== SLIDER ACTIONS =====================

    public void moveSlider(By sliderLocator, int xOffset) {
        WebElement slider = wait.until(ExpectedConditions.elementToBeClickable(sliderLocator));
        actions.clickAndHold(slider).moveByOffset(xOffset, 0).release().perform();
        logger.info("Moved slider {} by xOffset={}", sliderLocator, xOffset);
    }

    public void moveSliderTo(By sliderLocator, By trackLocator, double percentage) {
        WebElement slider = wait.until(ExpectedConditions.presenceOfElementLocated(sliderLocator));
        WebElement track = wait.until(ExpectedConditions.presenceOfElementLocated(trackLocator));
        int trackWidth = track.getSize().getWidth();
        int xOffset = (int) (trackWidth * (percentage / 100));
        actions.clickAndHold(slider).moveByOffset(xOffset, 0).release().perform();
        logger.info("Moved slider {} to {}% on track {}", sliderLocator, percentage, trackLocator);
    }

    // ===================== TYPE ACTIONS =====================

    public void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        logger.info("Typed '{}' into element: {}", text, locator);
    }

    public void type(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        logger.info("Typed '{}' into element: {}", text, element);
    }

    public void typeWithoutClear(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.sendKeys(text);
        logger.info("Typed '{}' without clearing element: {}", text, locator);
    }

    public void clear(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        logger.info("Cleared element: {}", locator);
    }

    public void pressKey(By locator, Keys key) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.sendKeys(key);
        logger.info("Pressed key '{}' on element: {}", key, locator);
    }

    public void pressEnter(By locator) {
        pressKey(locator, Keys.ENTER);
    }

    public void pressTab(By locator) {
        pressKey(locator, Keys.TAB);
    }

    public void pressEscape() {
        actions.sendKeys(Keys.ESCAPE).perform();
        logger.info("Pressed Escape key");
    }

    public void pressKeyCombination(Keys... keys) {
        actions.keyDown(keys[0]);
        for (int i = 1; i < keys.length - 1; i++) {
            actions.keyDown(keys[i]);
        }
        actions.sendKeys(keys[keys.length - 1]);
        for (int i = keys.length - 2; i >= 0; i--) {
            actions.keyUp(keys[i]);
        }
        actions.perform();
        logger.info("Pressed key combination: {}", (Object[]) keys);
    }

    public void selectAllAndCopy(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        actions.click(element)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL)
                .perform();
        logger.info("Selected all and copied from element: {}", locator);
    }

    public void selectAllAndPaste(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        actions.click(element)
                .keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL)
                .keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL)
                .perform();
        logger.info("Selected all and pasted into element: {}", locator);
    }

    public void ctrlClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.keyDown(Keys.CONTROL).click(element).keyUp(Keys.CONTROL).perform();
        logger.info("Ctrl+Clicked element: {}", locator);
    }

    public void shiftClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        actions.keyDown(Keys.SHIFT).click(element).keyUp(Keys.SHIFT).perform();
        logger.info("Shift+Clicked element: {}", locator);
    }

    // ===================== GET VALUES =====================

    public String getText(By locator) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        logger.info("Retrieved text '{}' from element: {}", text, locator);
        return text;
    }

    public String getText(WebElement element) {
        String text = wait.until(ExpectedConditions.visibilityOf(element)).getText();
        logger.info("Retrieved text '{}'", text);
        return text;
    }

    
    public String getAttribute(By locator, String attributeName) {
        String value = wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getAttribute(attributeName);
        logger.info("Retrieved attribute '{}' = '{}' from element: {}", attributeName, value, locator);
        return value;
    }

    public String getDomProperty(By locator, String propertyName) {
        String value = wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getDomProperty(propertyName);
        logger.info("Retrieved DOM property '{}' = '{}' from element: {}", propertyName, value, locator);
        return value;
    }

    public String getCssValue(By locator, String propertyName) {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getCssValue(propertyName);
        logger.info("Retrieved CSS value '{}' = '{}' from element: {}", propertyName, value, locator);
        return value;
    }

    public String getTagName(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getTagName();
    }

    public Rectangle getRect(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getRect();
    }

    // ===================== STATE CHECKS =====================

    public boolean isDisplayed(By locator) {
        try {
            return  wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEnabled(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSelected(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPresent(By locator) {
        return driver.findElements(locator).size() > 0;
    }

    // ===================== CHECKBOX / RADIO ACTIONS =====================

    public void check(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        if (!element.isSelected()) {
            element.click();
            logger.info("Checked element: {}", locator);
        } else {
            logger.info("Element already checked: {}", locator);
        }
    }

    public void uncheck(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        if (element.isSelected()) {
            element.click();
            logger.info("Unchecked element: {}", locator);
        } else {
            logger.info("Element already unchecked: {}", locator);
        }
    }

    public void toggle(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        logger.info("Toggled element: {}", locator);
    }

    // ===================== DROPDOWN ACTIONS =====================

    public void selectByVisibleText(By locator, String visibleText) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).selectByVisibleText(visibleText);
        logger.info("Selected '{}' by visible text from dropdown: {}", visibleText, locator);
    }

    public void selectByValue(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).selectByValue(value);
        logger.info("Selected '{}' by value from dropdown: {}", value, locator);
    }

    public void selectByIndex(By locator, int index) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).selectByIndex(index);
        logger.info("Selected index '{}' from dropdown: {}", index, locator);
    }

    public String getSelectedOptionText(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return new Select(element).getFirstSelectedOption().getText();
    }

    public List<WebElement> getAllOptions(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return new Select(element).getOptions();
    }

    public void deselectByVisibleText(By locator, String visibleText) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).deselectByVisibleText(visibleText);
        logger.info("Deselected '{}' from dropdown: {}", visibleText, locator);
    }

    public void deselectByValue(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).deselectByValue(value);
        logger.info("Deselected '{}' by value from dropdown: {}", value, locator);
    }

    public void deselectByIndex(By locator, int index) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).deselectByIndex(index);
        logger.info("Deselected index '{}' from dropdown: {}", index, locator);
    }

    public void deselectAll(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(element).deselectAll();
        logger.info("Deselected all options from dropdown: {}", locator);
    }

    // ===================== SCROLL ACTIONS =====================

    public void scrollToElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        logger.info("Scrolled to element: {}", locator);
    }

    public void scrollByAmount(int x, int y) {
        jsExecutor.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
        logger.info("Scrolled by x={}, y={}", x, y);
    }

    public void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom of page");
    }

    public void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        logger.info("Scrolled to top of page");
    }

    public void scrollToElementActions(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        actions.scrollToElement(element).perform();
        logger.info("Scrolled to element using Actions: {}", locator);
    }

    public void scrollByAmountActions(int deltaX, int deltaY) {
        actions.scrollByAmount(deltaX, deltaY).perform();
        logger.info("Scrolled by x={}, y={} using Actions", deltaX, deltaY);
    }

    // ===================== WAIT ACTIONS =====================

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForTextPresent(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        logger.info("Waited for text '{}' in element: {}", text, locator);
    }

    public void waitForUrlContains(String fraction) {
        wait.until(ExpectedConditions.urlContains(fraction));
        logger.info("Waited for URL containing: {}", fraction);
    }

    public void waitForTitleContains(String title) {
        wait.until(ExpectedConditions.titleContains(title));
        logger.info("Waited for title containing: {}", title);
    }

    public void staticWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Static wait interrupted", e);
        }
    }

    // ===================== ALERT ACTIONS =====================

    public void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        logger.info("Accepted alert");
    }

    public void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
        logger.info("Dismissed alert");
    }

    public String getAlertText() {
        String text = wait.until(ExpectedConditions.alertIsPresent()).getText();
        logger.info("Alert text: {}", text);
        return text;
    }

    public void typeInAlert(String text) {
        wait.until(ExpectedConditions.alertIsPresent()).sendKeys(text);
        logger.info("Typed '{}' in alert", text);
    }

    // ===================== FRAME ACTIONS =====================

    public void switchToFrame(By locator) {
        WebElement frame = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        driver.switchTo().frame(frame);
        logger.info("Switched to frame: {}", locator);
    }

    public void switchToFrame(int index) {
        driver.switchTo().frame(index);
        logger.info("Switched to frame index: {}", index);
    }

    public void switchToFrame(String nameOrId) {
        driver.switchTo().frame(nameOrId);
        logger.info("Switched to frame: {}", nameOrId);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        logger.info("Switched to default content");
    }

    // ===================== WINDOW/TAB ACTIONS =====================

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
        logger.info("Switched to window: {}", windowHandle);
    }

    public void switchToNewWindow() {
        String currentWindow = driver.getWindowHandle();
        for (String window : driver.getWindowHandles()) {
            if (!window.equals(currentWindow)) {
                driver.switchTo().window(window);
                logger.info("Switched to new window");
                break;
            }
        }
    }

    public void openNewTab() {
        driver.switchTo().newWindow(WindowType.TAB);
        logger.info("Opened new tab");
    }

    public void openNewWindow() {
        driver.switchTo().newWindow(WindowType.WINDOW);
        logger.info("Opened new window");
    }

    public void closeCurrentWindow() {
        driver.close();
        logger.info("Closed current window/tab");
    }

    public Set<String> getAllWindowHandles() {
        return driver.getWindowHandles();
    }

    public String getCurrentWindowHandle() {
        return driver.getWindowHandle();
    }

    // ===================== NAVIGATION ACTIONS =====================

    public void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    public void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }

    public void navigateForward() {
        driver.navigate().forward();
        logger.info("Navigated forward");
    }

    public void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: {}", url);
        return url;
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Page title: {}", title);
        return title;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    // ===================== BROWSER WINDOW ACTIONS =====================

    public void maximizeWindow() {
        driver.manage().window().maximize();
        logger.info("Window maximized");
    }

    public void minimizeWindow() {
        driver.manage().window().minimize();
        logger.info("Window minimized");
    }

    public void fullscreenWindow() {
        driver.manage().window().fullscreen();
        logger.info("Window fullscreen");
    }

    public void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
        logger.info("Set window size to {}x{}", width, height);
    }

    // ===================== COOKIE ACTIONS =====================

    public void addCookie(String name, String value) {
        driver.manage().addCookie(new Cookie(name, value));
        logger.info("Added cookie: {}={}", name, value);
    }

    public void addCookie(String name, String value, String domain, String path) {
        Cookie cookie = new Cookie.Builder(name, value)
                .domain(domain)
                .path(path)
                .build();
        driver.manage().addCookie(cookie);
        logger.info("Added cookie: {}={} for domain {}", name, value, domain);
    }

    public String getCookieValue(String name) {
        Cookie cookie = driver.manage().getCookieNamed(name);
        return cookie != null ? cookie.getValue() : null;
    }

    public Set<Cookie> getAllCookies() {
        return driver.manage().getCookies();
    }

    public void deleteCookie(String name) {
        driver.manage().deleteCookieNamed(name);
        logger.info("Deleted cookie: {}", name);
    }

    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        logger.info("Deleted all cookies");
    }

    // ===================== UPLOAD / DOWNLOAD =====================

    public void uploadFile(By locator, String filePath) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        element.sendKeys(filePath);
        logger.info("Uploaded file: {}", filePath);
    }

    public boolean isFileDownloaded(String downloadPath, String fileName, int timeoutSeconds) {
        Path filePath = Paths.get(downloadPath, fileName);
        for (int i = 0; i < timeoutSeconds; i++) {
            if (Files.exists(filePath)) {
                logger.info("File downloaded: {}", filePath);
                return true;
            }
            staticWait(1000);
        }
        logger.error("File not downloaded: {}", filePath);
        return false;
    }

    public boolean isFileDownloaded(String downloadPath, String fileName) {
        return isFileDownloaded(downloadPath, fileName, DEFAULT_TIMEOUT);
    }

    public void deleteDownloadedFile(String downloadPath, String fileName) {
        try {
            Files.deleteIfExists(Paths.get(downloadPath, fileName));
            logger.info("Deleted downloaded file: {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileName, e);
        }
    }

    // ===================== SCREENSHOT =====================

    public String takeScreenshot(String fileName) {
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File("screenshots/" + fileName + ".png");
            FileHandler.copy(source, destination);
            logger.info("Screenshot saved: {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }

    public String takeElementScreenshot(By locator, String fileName) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            File source = element.getScreenshotAs(OutputType.FILE);
            File destination = new File("screenshots/" + fileName + ".png");
            FileHandler.copy(source, destination);
            logger.info("Element screenshot saved: {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (Exception e) {
            logger.error("Failed to take element screenshot", e);
            return null;
        }
    }

    public String getScreenshotAsBase64() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    // ===================== JAVA SCRIPT ACTIONS =====================

    public Object executeJavaScript(String script, Object... args) {
        Object result = jsExecutor.executeScript(script, args);
        logger.info("Executed JavaScript: {}", script);
        return result;
    }

    public Object executeAsyncJavaScript(String script, Object... args) {
        Object result = jsExecutor.executeAsyncScript(script, args);
        logger.info("Executed async JavaScript: {}", script);
        return result;
    }

    public void highlightElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].style.border='3px solid red'; arguments[0].style.backgroundColor='yellow';", element);
        logger.info("Highlighted element: {}", locator);
    }

    public void removeHighlight(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].style.border=''; arguments[0].style.backgroundColor='';", element);
        logger.info("Removed highlight from element: {}", locator);
    }

    public void setValueUsingJavaScript(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].value = arguments[1];", element, value);
        logger.info("Set value '{}' using JavaScript on element: {}", value, locator);
    }

    public void removeElementUsingJavaScript(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].remove();", element);
        logger.info("Removed element using JavaScript: {}", locator);
    }

    public void setAttributeUsingJavaScript(By locator, String attribute, String value) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attribute, value);
        logger.info("Set attribute '{}' = '{}' using JavaScript on element: {}", attribute, value, locator);
    }

    public void removeAttributeUsingJavaScript(By locator, String attribute) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].removeAttribute(arguments[1]);", element, attribute);
        logger.info("Removed attribute '{}' using JavaScript on element: {}", attribute, locator);
    }

    public void changeCssUsingJavaScript(By locator, String property, String value) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        jsExecutor.executeScript("arguments[0].style." + property + " = arguments[1];", element, value);
        logger.info("Changed CSS property '{}' = '{}' using JavaScript on element: {}", property, value, locator);
    }

    // ===================== SHADOW DOM ACTIONS =====================

    public SearchContext getShadowRoot(By hostLocator) {
        WebElement host = wait.until(ExpectedConditions.presenceOfElementLocated(hostLocator));
        return host.getShadowRoot();
    }

    public WebElement findElementInShadowRoot(By hostLocator, By shadowLocator) {
        SearchContext shadowRoot = getShadowRoot(hostLocator);
        return shadowRoot.findElement(shadowLocator);
    }

    public List<WebElement> findElementsInShadowRoot(By hostLocator, By shadowLocator) {
        SearchContext shadowRoot = getShadowRoot(hostLocator);
        return shadowRoot.findElements(shadowLocator);
    }

    public void clickInShadowRoot(By hostLocator, By shadowLocator) {
        findElementInShadowRoot(hostLocator, shadowLocator).click();
        logger.info("Clicked shadow element {} inside host {}", shadowLocator, hostLocator);
    }

    public void typeInShadowRoot(By hostLocator, By shadowLocator, String text) {
        WebElement element = findElementInShadowRoot(hostLocator, shadowLocator);
        element.clear();
        element.sendKeys(text);
        logger.info("Typed '{}' in shadow element {}", text, shadowLocator);
    }

    // ===================== RELATIVE LOCATORS =====================

    public WebElement findAbove(By referenceLocator, By targetLocator) {
        return driver.findElement(RelativeLocator.with(targetLocator).above(referenceLocator));
    }

    public WebElement findBelow(By referenceLocator, By targetLocator) {
        return driver.findElement(RelativeLocator.with(targetLocator).below(referenceLocator));
    }

    public WebElement findLeftOf(By referenceLocator, By targetLocator) {
        return driver.findElement(RelativeLocator.with(targetLocator).toLeftOf(referenceLocator));
    }

    public WebElement findRightOf(By referenceLocator, By targetLocator) {
        return driver.findElement(RelativeLocator.with(targetLocator).toRightOf(referenceLocator));
    }

    public WebElement findNear(By referenceLocator, By targetLocator, int pixelDistance) {
        return driver.findElement(RelativeLocator.with(targetLocator).near(referenceLocator, pixelDistance));
    }

    // ===================== LISTS / TABLES =====================

    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    public int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }

    public void clickElementFromList(By locator, int index) {
        List<WebElement> elements = driver.findElements(locator);
        if (index >= 0 && index < elements.size()) {
            elements.get(index).click();
            logger.info("Clicked element at index {} from list: {}", index, locator);
        } else {
            throw new IndexOutOfBoundsException("Invalid index " + index + " for list " + locator);
        }
    }

    public String getTextFromList(By locator, int index) {
        List<WebElement> elements = driver.findElements(locator);
        if (index >= 0 && index < elements.size()) {
            return elements.get(index).getText();
        }
        throw new IndexOutOfBoundsException("Invalid index " + index + " for list " + locator);
    }

    public WebElement findElementByText(By locator, String text) {
        for (WebElement element : driver.findElements(locator)) {
            if (element.getText().equals(text) || element.getText().contains(text)) {
                return element;
            }
        }
        throw new NoSuchElementException("Element with text '" + text + "' not found using locator " + locator);
    }

    public void clickElementByText(By locator, String text) {
        findElementByText(locator, text).click();
        logger.info("Clicked element with text '{}' using locator: {}", text, locator);
    }

    // ===================== BROWSER LOGS =====================

    public List<String> getBrowserLogs() {
        return driver.manage().logs().get("browser").getAll()
                .stream()
                .map(logEntry -> logEntry.getLevel() + " - " + logEntry.getMessage())
                .toList();
    }

    public List<String> getAvailableLogTypes() {
        return driver.manage().logs().getAvailableLogTypes().stream().toList();
    }

    // ===================== DEVTOOLS ACTIONS =====================

    public org.openqa.selenium.devtools.DevTools getDevTools() {
        if (driver instanceof org.openqa.selenium.devtools.HasDevTools) {
            return ((org.openqa.selenium.devtools.HasDevTools) driver).getDevTools();
        }
        throw new UnsupportedOperationException("DevTools not supported for this driver");
    }

    public void captureConsoleLogs(java.util.function.Consumer<String> consumer) {
        DevTools devTools = getDevTools();
        devTools.createSession();
        devTools.getDomains().events().addConsoleListener(consoleEvent -> consumer.accept(consoleEvent.getType() + ": " + consoleEvent.getMessages()));
        logger.info("Console log listener added");
    }

    public void captureNetworkLogs(java.util.function.Consumer<String> consumer) {
        DevTools devTools = getDevTools();
        devTools.createSession();
        devTools.send(org.openqa.selenium.devtools.v149.network.Network.enable(java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty()));
        devTools.addListener(org.openqa.selenium.devtools.v149.network.Network.responseReceived(), response -> consumer.accept(response.getResponse().getUrl()));
        logger.info("Network log listener added");
    }

    public void clearBrowserCache() {
        DevTools devTools = getDevTools();
        devTools.createSession();
        devTools.send(org.openqa.selenium.devtools.v149.network.Network.clearBrowserCache());
        logger.info("Browser cache cleared");
    }

    // ===================== REMOTE / GRID =====================

    public String getSessionId() {
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }

    public org.openqa.selenium.Capabilities getCapabilities() {
        return ((RemoteWebDriver) driver).getCapabilities();
    }
}