# Walnut Selenium Automation Framework - Complete Guide

A production-ready Java Selenium 4 automation framework built with Maven, TestNG,
Page Object Model (POM), ExtentReports, and GitHub Actions CI.

This guide explains every folder, every file, every important method, how to
create the project from scratch, how to run it, how to modify it, and how data
flows from the moment you type `mvn test` until the browser closes and the
report is generated.

---

## Table of Contents

1. [What This Project Does](#1-what-this-project-does)
2. [Complete Project Structure](#2-complete-project-structure)
3. [Folder-by-Folder, File-by-File Explanation](#3-folder-by-folder-file-by-file-explanation)
4. [Step-by-Step: Create This Project From Scratch](#4-step-by-step-create-this-project-from-scratch)
5. [How to Create Each File / Folder](#5-how-to-create-each-file--folder)
6. [How to Run the Project](#6-how-to-run-the-project)
7. [How to Modify / Extend the Project](#7-how-to-modify--extend-the-project)
8. [Method-by-Method Reference](#8-method-by-method-reference)
9. [Data Flow Deep Dive](#9-data-flow-deep-dive)
10. [Configuration Reference](#10-configuration-reference)
11. [Troubleshooting](#11-troubleshooting)

---

## 1. What This Project Does

This framework automates web browser testing. It can:

- Open Chrome, Firefox, or Edge.
- Navigate to a web application.
- Find elements using XPath, CSS selectors, IDs, etc.
- Click, type, select dropdowns, scroll, drag-and-drop, and more.
- Verify page text, URLs, and element states.
- Take screenshots when tests fail.
- Retry failed tests automatically.
- Generate beautiful HTML reports.
- Run on GitHub Actions for CI/CD.

It separates test code from page details using the **Page Object Model**,
making tests easy to maintain when the UI changes.

---

## 2. Complete Project Structure

```
walnut_v2/
│
├── pom.xml                                     # Maven build configuration
├── README.md                                   # Complete project guide (this file)
├── requirements.txt                            # Dependency reference guide
│
├── .github/
│   └── workflows/
│       └── ci.yml                              # GitHub Actions CI pipeline
│
└── src/
    │
    ├── main/java/com/walnut/automation/
    │   │
    │   ├── actions/
    │   │   └── SeleniumActions.java            # Reusable Selenium wrapper methods
    │   │
    │   ├── config/
    │   │   └── ConfigManager.java              # Reads environment properties
    │   │
    │   ├── factory/
    │   │   └── DriverFactory.java              # Creates WebDriver instances
    │   │
    │   ├── pages/
    │   │   └── LoginPage.java                  # Page Object for Login page
    │   │
    │   └── utils/
    │       └── ExcelUtils.java                 # Excel test-data reader example
    │
    ├── test/java/com/walnut/automation/
    │   │
    │   ├── base/
    │   │   └── BaseTest.java                   # Test base: setup & teardown
    │   │
    │   ├── listeners/
    │   │   ├── AnnotationTransformer.java      # Globally assigns retry analyzer
    │   │   ├── ExtentReportListener.java       # HTML report + screenshots
    │   │   └── RetryAnalyzer.java              # Retries failed tests
    │   │
    │   └── tests/
    │       └── LoginTest.java                  # Sample TestNG test class
    │
    └── test/resources/
        │
        ├── config/
        │   ├── qa.properties                   # QA environment settings
        │   └── uat.properties                  # UAT environment settings
        │
        ├── logback.xml                         # Logging configuration
        │
        └── testng.xml                          # TestNG suite configuration
```

### Why this structure?

| Layer | Responsibility |
|-------|----------------|
| `pages/` | Knows WHAT elements are on a page and WHAT actions can be done. |
| `actions/` | Knows HOW to interact with Selenium (click, type, wait, etc.). |
| `tests/` | Knows the TEST FLOW and ASSERTIONS only. |
| `base/` | Provides shared setup/teardown for every test. |
| `config/` | Loads environment-specific values (URL, timeouts, credentials). |
| `factory/` | Creates browser drivers. |
| `listeners/` | Reports and retries. |
| `utils/` | Helper utilities like Excel reading. |
| `resources/` | Configuration files, test data, suite XML. |

This separation means if a button locator changes, you only update `LoginPage.java`,
not every test.

---

## 3. Folder-by-Folder, File-by-File Explanation

### 3.1 Root files

#### `pom.xml`
This is the Maven build file. It tells Maven:
- What Java version to use (Java 26).
- What libraries (dependencies) to download.
- How to compile, test, and package the project.
- What Maven profiles exist (`chrome`, `firefox`, `edge`, `headless`, `uat`, `prod`).

Key sections:
- `<properties>` — version numbers for all libraries.
- `<dependencies>` — Selenium, TestNG, ExtentReports, Logback, etc.
- `<build>` — compiler, Surefire (test runner), Failsafe plugins.
- `<profiles>` — environment/browser presets.

#### `README.md`
This file. It documents the entire project.

#### `requirements.txt`
Human-readable dependency reference. Explains what each library does and how
to add or update dependencies in `pom.xml`.

---

### 3.2 `src/main/java/com/walnut/automation/`

This folder contains reusable framework code. It is compiled first and is
available to both production and test code.

#### `actions/SeleniumActions.java`
The heart of the framework. It wraps Selenium WebDriver commands so tests and
page objects do not deal with raw WebDriver directly.

What it provides:
- Click, type, clear, hover, double-click, right-click.
- Waits for visibility, clickability, invisibility.
- Dropdown selection, checkbox, radio button handling.
- Scroll actions (to element, to top, to bottom, by amount).
- Drag-and-drop, slider, file upload/download checks.
- Screenshots (full page, element, base64).
- JavaScript execution, shadow DOM access, relative locators.
- Window/tab switching, navigation, cookie handling.
- Browser logs and Chrome DevTools access.

Why it matters:
Every page object uses `actions.click(...)`, `actions.type(...)`, etc. If you
want to change the default wait time or add logging, you change it here once.

#### `config/ConfigManager.java`
Loads environment-specific properties from `src/test/resources/config/`.

What it does:
- Reads the `environment` system property (default `qa`).
- Loads the matching file, e.g., `qa.properties`.
- Provides `get(key)`, `get(key, defaultValue)`, and `getInt(key)` methods.
- System properties override file values.

Why it matters:
You can run the same tests against QA, UAT, or Prod just by changing
`-Denvironment=uat`.

#### `factory/DriverFactory.java`
Creates WebDriver instances.

What it does:
- Accepts a browser name (`chrome`, `firefox`, `edge`).
- Accepts a headless flag.
- Returns the correct driver with sensible options.

Why it matters:
Tests do not create drivers manually. `BaseTest` asks `DriverFactory` for a
driver, keeping driver creation in one place.

#### `pages/LoginPage.java`
Example Page Object.

What it contains:
- `By` locators for elements on the login page.
- Methods like `enterEmail(String)`, `clickContinue()`, `loginWithEmail(String)`.
- Verification methods like `isLogoDisplayed()`.

Why it matters:
If the login page UI changes, only this file changes. Tests stay clean.

#### `utils/ExcelUtils.java`
Example utility for reading test data from Excel.

What it does:
- Reads a `.xlsx` file from the classpath.
- Returns the sheet content as a list of rows.

Why it matters:
You can store hundreds of test credentials or data rows in Excel and feed them
to tests through TestNG DataProviders.

---

### 3.3 `src/test/java/com/walnut/automation/`

This folder contains test-only code. It is compiled after `main` and has access
to all `main` classes plus TestNG and other test-scoped dependencies.

#### `base/BaseTest.java`
Every test class extends this.

What it does:
- `@BeforeMethod` — creates driver, creates `SeleniumActions`, navigates to base URL.
- `@AfterMethod` — takes screenshot on failure, quits driver.
- Provides `getDriver()` and `getActions()` getters for listeners.

Why it matters:
Tests only need to extend `BaseTest`. They get setup/teardown and the `actions`
object for free.

#### `listeners/ExtentReportListener.java`
Generates the HTML test report.

What it does:
- `onStart` — creates the report file `reports/ExtentReport.html`.
- `onTestStart` — logs that a test started.
- `onTestSuccess` — marks test as passed.
- `onTestFailure` — marks test as failed, logs exception, attaches screenshot.
- `onTestSkipped` — marks test as skipped.
- `onFinish` — writes the final report.

Why it matters:
After every run you get a visual report showing what passed, failed, and why.

#### `listeners/RetryAnalyzer.java`
Retries failed tests.

What it does:
- Implements `IRetryAnalyzer`.
- Reads `max.retries` system property.
- Returns `true` to retry until the maximum is reached.

Why it matters:
Selenium tests can be flaky due to network or timing. Retry reduces false failures.

#### `listeners/AnnotationTransformer.java`
Applies `RetryAnalyzer` globally.

What it does:
- Implements `IAnnotationTransformer`.
- Sets `RetryAnalyzer.class` on every `@Test` method automatically.

Why it matters:
You do not need to add `retryAnalyzer = RetryAnalyzer.class` to every test.

#### `tests/LoginTest.java`
Example test class.

What it does:
- Extends `BaseTest`.
- Uses `LoginPage` to perform actions.
- Asserts expected outcomes.

Why it matters:
This is the pattern every new test should follow.

---

### 3.4 `src/test/resources/`

This folder holds non-Java files needed during testing.

#### `config/qa.properties`
QA environment values:
- `base.url` — application URL.
- `browser`, `headless` — defaults.
- `implicit.wait`, `explicit.wait` — timeouts.
- `login.email`, `login.success.url.fragment` — test data.

#### `config/uat.properties`
Same keys as `qa.properties` but with UAT values.

#### `logback.xml`
Logging configuration.

What it controls:
- Console output pattern.
- File output to `logs/automation.log`.
- Log levels for Selenium, WebDriverManager, and framework packages.

#### `testng.xml`
TestNG suite file.

What it contains:
- Registered listeners.
- Default parameters (`browser`, `headless`).
- List of test classes to run.

Why it matters:
Maven Surefire reads this file to know which tests to execute.

---

### 3.5 `.github/workflows/ci.yml`

GitHub Actions pipeline.

What it does:
- Triggers on push/PR to `main` or `develop`.
- Sets up JDK 26 and Chrome.
- Runs `mvn test -Dheadless=true -Denvironment=qa -Dmax.retries=1`.
- Uploads ExtentReport, screenshots, and logs as artifacts.

Why it matters:
Every code change is automatically tested in the cloud.

---

## 4. Step-by-Step: Create This Project From Scratch

Follow these steps if you want to build this exact framework manually.

### Step 1: Create the Maven project

Create a folder named `walnut_v2` and inside it create `pom.xml`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.ganesh</groupId>
    <artifactId>selenium-complete-automation-framework</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <properties>
        <java.version>26</java.version>
        <maven.compiler.release>26</maven.compiler.release>
        <selenium.version>4.47.0</selenium.version>
        <testng.version>7.9.0</testng.version>
    </properties>
</project>
```

Then run:

```bash
mvn clean compile
```

Maven will create the standard `src/main/java` and `src/test/java` folders.

### Step 2: Add the folder structure

Create all folders:

```bash
mkdir -p src/main/java/com/walnut/automation/actions
mkdir -p src/main/java/com/walnut/automation/config
mkdir -p src/main/java/com/walnut/automation/factory
mkdir -p src/main/java/com/walnut/automation/pages
mkdir -p src/main/java/com/walnut/automation/utils
mkdir -p src/test/java/com/walnut/automation/base
mkdir -p src/test/java/com/walnut/automation/listeners
mkdir -p src/test/java/com/walnut/automation/tests
mkdir -p src/test/resources/config
mkdir -p .github/workflows
mkdir -p reports
mkdir -p logs
mkdir -p screenshots
```

### Step 3: Add dependencies to `pom.xml`

Open `pom.xml` and add dependencies inside `<dependencies>...</dependencies>`.
See `requirements.txt` for the full list with explanations.

Minimum dependencies for this framework:
- `selenium-java`
- `testng`
- `extentreports`
- `slf4j-api` + `logback-classic`
- `poi-ooxml` (for Excel)
- `jackson-databind` (for JSON)
- `snakeyaml` (for YAML)
- `commons-io` (for file utilities)

### Step 4: Create the reusable action layer

Create `src/main/java/com/walnut/automation/actions/SeleniumActions.java`.
This class wraps all Selenium commands. Start with simple methods like `click`,
`type`, `getText`, and `waitForVisible`. Add more as needed.

### Step 5: Create configuration support

Create `src/main/java/com/walnut/automation/config/ConfigManager.java`.
This reads `.properties` files from `src/test/resources/config/`.

Create `src/test/resources/config/qa.properties` and `uat.properties`.

### Step 6: Create the driver factory

Create `src/main/java/com/walnut/automation/factory/DriverFactory.java`.
This returns `ChromeDriver`, `FirefoxDriver`, or `EdgeDriver` based on input.

### Step 7: Create the test base

Create `src/test/java/com/walnut/automation/base/BaseTest.java`.
It must:
- Have `@BeforeMethod` to open browser and navigate to base URL.
- Have `@AfterMethod` to quit browser.
- Expose `actions` to subclasses.

### Step 8: Create page objects

Create `src/main/java/com/walnut/automation/pages/LoginPage.java`.
Add `By` locators and page-specific methods.

### Step 9: Create tests

Create `src/test/java/com/walnut/automation/tests/LoginTest.java`.
Extend `BaseTest`, use page objects, and add assertions.

### Step 10: Add listeners

Create:
- `ExtentReportListener.java` for HTML reports.
- `RetryAnalyzer.java` for retry logic.
- `AnnotationTransformer.java` to apply retry globally.

Register them in `src/test/resources/testng.xml`.

### Step 11: Add logging

Create `src/test/resources/logback.xml` to control console and file logs.

### Step 12: Add CI/CD

Create `.github/workflows/ci.yml` to run tests on GitHub Actions.

### Step 13: Verify

Run:

```bash
mvn clean compile test-compile
mvn test
```

Check that `reports/ExtentReport.html`, `logs/automation.log`, and
`screenshots/` are created.

---

## 5. How to Create Each File / Folder

### 5.1 Creating a new Page Object

1. Create a file in `src/main/java/com/walnut/automation/pages/`.
2. Name it after the page, e.g., `DashboardPage.java`.
3. Accept `SeleniumActions` in the constructor.
4. Add private `By` fields for each element.
5. Add public methods for actions a user can perform.

Example:

```java
package com.walnut.automation.pages;

import com.walnut.automation.actions.SeleniumActions;
import org.openqa.selenium.By;

public class DashboardPage {
    private final SeleniumActions actions;
    private final By welcomeMessage = By.xpath("//div[@class='welcome']");

    public DashboardPage(SeleniumActions actions) {
        this.actions = actions;
    }

    public String getWelcomeText() {
        return actions.getText(welcomeMessage);
    }
}
```

### 5.2 Creating a new Test class

1. Create a file in `src/test/java/com/walnut/automation/tests/`.
2. Extend `BaseTest`.
3. Use the inherited `actions` field.
4. Add `@Test` methods.
5. Add the class to `testng.xml`.

Example:

```java
package com.walnut.automation.tests;

import com.walnut.automation.base.BaseTest;
import com.walnut.automation.pages.DashboardPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {
    @Test
    public void welcomeMessageIsDisplayed() {
        DashboardPage dashboard = new DashboardPage(actions);
        Assert.assertEquals(dashboard.getWelcomeText(), "Welcome!");
    }
}
```

### 5.3 Creating a new utility

1. Create a file in `src/main/java/com/walnut/automation/utils/`.
2. Make methods `public static`.
3. Add a private constructor to prevent instantiation.

Example:

```java
package com.walnut.automation.utils;

public class StringUtils {
    private StringUtils() {}

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
```

### 5.4 Creating a new environment config

1. Create `src/test/resources/config/prod.properties`.
2. Copy keys from `qa.properties` and change values.
3. Run with `-Denvironment=prod`.

### 5.5 Creating a new Maven profile

Open `pom.xml`, find `<profiles>`, and add:

```xml
<profile>
    <id>prod</id>
    <properties>
        <browser>chrome</browser>
        <headless>true</headless>
        <environment>prod</environment>
    </properties>
</profile>
```

Run with `mvn test -Pprod`.

---

## 6. How to Run the Project

### 6.1 Prerequisites

- Java 26 installed.
- Maven 3.9+ installed.
- Chrome, Firefox, or Edge installed (for headed runs).
- Internet connection to download Maven dependencies on first run.

### 6.2 Default run

```bash
mvn test
```

This uses the default `chrome` profile:
- Browser: Chrome
- Environment: QA
- Headless: false
- Suite: `src/test/resources/testng.xml`

### 6.3 Run against UAT

```bash
mvn test -Denvironment=uat
```

`ConfigManager` loads `src/test/resources/config/uat.properties`.

### 6.4 Run with Firefox

```bash
mvn test -Dbrowser=firefox
```

### 6.5 Run headless

```bash
mvn test -Dheadless=true
```

### 6.6 Run with retry

```bash
mvn test -Dmax.retries=2
```

Failed tests will be retried up to 2 additional times.

### 6.7 Run a specific test class

```bash
mvn test -Dtest=LoginTest
```

### 6.8 Run a specific test method

```bash
mvn test -Dtest=LoginTest#verifyLoginPageLoads
```

### 6.9 Run with Maven profile

```bash
mvn test -Pheadless
mvn test -Puat
mvn test -Pfirefox
```

### 6.10 Compile only (no tests)

```bash
mvn clean compile test-compile
```

### 6.11 View the report

After any run, open:

```
reports/ExtentReport.html
```

in a web browser.

---

## 7. How to Modify / Extend the Project

### 7.1 Change the application URL

Edit `src/test/resources/config/qa.properties`:

```properties
base.url=https://your-app.com
```

### 7.2 Change the default timeout

Edit `qa.properties`:

```properties
explicit.wait=20
```

Then update `SeleniumActions` constructor to use `ConfigManager.getInt("explicit.wait")`.

### 7.3 Add a new browser

Edit `DriverFactory.java`:

```java
case "safari" -> createSafariDriver(headless);
```

Add the `createSafariDriver` method.

### 7.4 Add a new dependency

1. Find coordinates on https://mvnrepository.com/.
2. Add to `pom.xml` inside `<dependencies>`.
3. Optionally add a version property.
4. Run `mvn clean compile test-compile`.

See `requirements.txt` for detailed instructions.

### 7.5 Disable retry

Run with:

```bash
mvn test -Dmax.retries=0
```

Or set `DEFAULT_MAX_RETRIES = 0` in `RetryAnalyzer.java`.

### 7.6 Disable ExtentReports

Remove or comment out the `ExtentReportListener` from `testng.xml`.

### 7.7 Add parallel execution

Edit `testng.xml`:

```xml
<suite name="WalnutAutomationSuite" parallel="methods" thread-count="3">
```

Make sure page objects and actions are thread-safe (they are per-test instances).

### 7.8 Add data-driven tests

Use TestNG `@DataProvider` with `ExcelUtils`:

```java
@DataProvider(name = "loginData")
public Object[][] loginData() {
    List<List<String>> data = ExcelUtils.readSheet("testdata/login.xlsx", "Sheet1");
    Object[][] result = new Object[data.size() - 1][1];
    for (int i = 1; i < data.size(); i++) {
        result[i - 1][0] = data.get(i).get(0);
    }
    return result;
}
```

---

## 8. Method-by-Method Reference

### 8.1 `SeleniumActions` methods

`SeleniumActions` wraps Selenium commands with explicit waits and logging.
Each method waits for the element to be ready before acting.

#### Constructors

| Method | What it does |
|--------|--------------|
| `SeleniumActions(WebDriver driver)` | Creates the wrapper with a 10-second default timeout. |
| `SeleniumActions(WebDriver driver, int timeoutInSeconds)` | Creates the wrapper with a custom timeout. |

#### Wait helpers

| Method | What it does |
|--------|--------------|
| `setImplicitWait(long seconds)` | Sets Selenium implicit wait. |
| `resetImplicitWait()` | Disables implicit wait (sets to 0). |
| `waitForVisible(By locator)` | Waits until element is visible, returns it. |
| `waitForClickable(By locator)` | Waits until element is clickable, returns it. |
| `waitForInvisible(By locator)` | Waits until element disappears. |
| `waitForTextPresent(By locator, String text)` | Waits until text appears inside element. |
| `waitForUrlContains(String fraction)` | Waits until current URL contains the string. |
| `waitForTitleContains(String title)` | Waits until page title contains the string. |
| `staticWait(long milliseconds)` | Hard sleep. Use sparingly. |

#### Click actions

| Method | What it does |
|--------|--------------|
| `click(By locator)` | Waits and clicks an element. |
| `click(WebElement element)` | Waits and clicks an already found element. |
| `clickWithOffset(By locator, int x, int y)` | Clicks at an offset inside the element. |
| `doubleClick(By locator)` | Double-clicks an element. |
| `rightClick(By locator)` | Right-clicks (context click) an element. |
| `hover(By locator)` | Moves mouse over element. |
| `hover(WebElement element)` | Hovers over an element instance. |
| `clickUsingJavaScript(By locator)` | Clicks via JavaScript (useful when normal click is blocked). |
| `safeClick(By locator)` | Tries normal click, falls back to JavaScript click. |
| `ctrlClick(By locator)` | Ctrl+clicks to open in new tab. |
| `shiftClick(By locator)` | Shift+clicks an element. |

#### Type actions

| Method | What it does |
|--------|--------------|
| `type(By locator, String text)` | Clears field and types text. |
| `typeWithoutClear(By locator, String text)` | Types text without clearing. |
| `clear(By locator)` | Clears an input field. |
| `pressKey(By locator, Keys key)` | Presses a single key. |
| `pressEnter(By locator)` | Presses Enter. |
| `pressTab(By locator)` | Presses Tab. |
| `pressEscape()` | Presses Escape anywhere on page. |
| `pressKeyCombination(Keys... keys)` | Presses key combinations like Ctrl+A. |
| `selectAllAndCopy(By locator)` | Selects all and copies. |
| `selectAllAndPaste(By locator)` | Selects all and pastes. |

#### Get values

| Method | What it does |
|--------|--------------|
| `getText(By locator)` | Returns visible text of element. |
| `getAttribute(By locator, String name)` | Returns attribute value. |
| `getDomProperty(By locator, String name)` | Returns DOM property value. |
| `getCssValue(By locator, String name)` | Returns CSS property value. |
| `getTagName(By locator)` | Returns HTML tag name. |
| `getRect(By locator)` | Returns element position and size. |

#### State checks

| Method | What it does |
|--------|--------------|
| `isDisplayed(By locator)` | Returns true if element is visible. |
| `isEnabled(By locator)` | Returns true if element is enabled. |
| `isSelected(By locator)` | Returns true if checkbox/radio is selected. |
| `isPresent(By locator)` | Returns true if element exists in DOM. |

#### Checkbox / radio / dropdown

| Method | What it does |
|--------|--------------|
| `check(By locator)` | Selects checkbox if not already selected. |
| `uncheck(By locator)` | Deselects checkbox if selected. |
| `toggle(By locator)` | Clicks to toggle state. |
| `selectByVisibleText(By locator, String text)` | Selects dropdown option by visible text. |
| `selectByValue(By locator, String value)` | Selects dropdown option by value attribute. |
| `selectByIndex(By locator, int index)` | Selects dropdown option by index. |
| `getSelectedOptionText(By locator)` | Returns text of selected option. |
| `getAllOptions(By locator)` | Returns all dropdown options. |
| `deselectByVisibleText`, `deselectByValue`, `deselectByIndex`, `deselectAll` | Deselect options in multi-select. |

#### Scroll actions

| Method | What it does |
|--------|--------------|
| `scrollToElement(By locator)` | Scrolls element into center view via JavaScript. |
| `scrollByAmount(int x, int y)` | Scrolls window by pixels via JavaScript. |
| `scrollToBottom()` | Scrolls to bottom of page. |
| `scrollToTop()` | Scrolls to top of page. |
| `scrollToElementActions(By locator)` | Scrolls using Actions API. |
| `scrollByAmountActions(int dx, int dy)` | Scrolls using Actions API. |

#### Drag and drop / sliders

| Method | What it does |
|--------|--------------|
| `dragAndDrop(By source, By target)` | Drags source element onto target. |
| `dragAndDropByOffset(By source, int x, int y)` | Drags source by offset. |
| `clickAndHold(By locator)` | Clicks and holds mouse. |
| `release(By locator)` | Releases held mouse. |
| `moveSlider(By locator, int xOffset)` | Moves slider horizontally. |
| `moveSliderTo(By slider, By track, double percentage)` | Moves slider to a percentage of track width. |

#### Window / tab / frame / alert

| Method | What it does |
|--------|--------------|
| `switchToFrame(By locator)` | Switches to iframe. |
| `switchToDefaultContent()` | Switches back to main page. |
| `switchToNewWindow()` | Switches to the newest browser window. |
| `openNewTab()` | Opens a new browser tab. |
| `openNewWindow()` | Opens a new browser window. |
| `closeCurrentWindow()` | Closes active tab/window. |
| `getAllWindowHandles()` | Returns all window handles. |
| `getCurrentWindowHandle()` | Returns current window handle. |
| `acceptAlert()`, `dismissAlert()` | Accepts or dismisses browser alert. |
| `getAlertText()` | Returns alert text. |
| `typeInAlert(String text)` | Types into alert prompt. |

#### Navigation / browser

| Method | What it does |
|--------|--------------|
| `navigateTo(String url)` | Opens a URL. |
| `navigateBack()` | Browser back button. |
| `navigateForward()` | Browser forward button. |
| `refreshPage()` | Refreshes page. |
| `getCurrentUrl()` | Returns current URL. |
| `getPageTitle()` | Returns page title. |
| `getPageSource()` | Returns full HTML source. |
| `maximizeWindow()` | Maximizes browser window. |
| `minimizeWindow()` | Minimizes browser window. |
| `fullscreenWindow()` | Fullscreens browser window. |
| `setWindowSize(int width, int height)` | Sets browser window size. |

#### Cookies

| Method | What it does |
|--------|--------------|
| `addCookie(String name, String value)` | Adds a simple cookie. |
| `addCookie(String name, String value, String domain, String path)` | Adds cookie with domain/path. |
| `getCookieValue(String name)` | Returns cookie value. |
| `getAllCookies()` | Returns all cookies. |
| `deleteCookie(String name)` | Deletes one cookie. |
| `deleteAllCookies()` | Deletes all cookies. |

#### Upload / download / screenshot

| Method | What it does |
|--------|--------------|
| `uploadFile(By locator, String filePath)` | Uploads a file through file input. |
| `isFileDownloaded(String path, String name, int timeout)` | Waits for file to appear in download folder. |
| `deleteDownloadedFile(String path, String name)` | Deletes a downloaded file. |
| `takeScreenshot(String fileName)` | Saves full-page screenshot. |
| `takeElementScreenshot(By locator, String fileName)` | Saves element-only screenshot. |
| `getScreenshotAsBase64()` | Returns screenshot as base64 string. |

#### JavaScript / shadow DOM / relative locators

| Method | What it does |
|--------|--------------|
| `executeJavaScript(String script, Object... args)` | Runs JavaScript and returns result. |
| `executeAsyncJavaScript(String script, Object... args)` | Runs async JavaScript. |
| `highlightElement(By locator)` | Highlights element with red/yellow border. |
| `removeHighlight(By locator)` | Removes highlight. |
| `setValueUsingJavaScript(By locator, String value)` | Sets input value via JavaScript. |
| `setAttributeUsingJavaScript(...)` | Sets any attribute via JavaScript. |
| `removeAttributeUsingJavaScript(...)` | Removes an attribute via JavaScript. |
| `changeCssUsingJavaScript(...)` | Changes a CSS property via JavaScript. |
| `getShadowRoot(By host)` | Returns shadow root of a host element. |
| `findElementInShadowRoot(By host, By target)` | Finds element inside shadow DOM. |
| `clickInShadowRoot`, `typeInShadowRoot` | Interact with shadow DOM elements. |
| `findAbove`, `findBelow`, `findLeftOf`, `findRightOf`, `findNear` | Relative locators. |

#### Lists / tables / logs

| Method | What it does |
|--------|--------------|
| `findElements(By locator)` | Returns list of matching elements. |
| `getElementCount(By locator)` | Returns number of matching elements. |
| `clickElementFromList(By locator, int index)` | Clicks element at index in list. |
| `getTextFromList(By locator, int index)` | Returns text of element at index. |
| `findElementByText(By locator, String text)` | Finds element matching exact/contains text. |
| `clickElementByText(By locator, String text)` | Clicks element matching text. |
| `getBrowserLogs()` | Returns browser console logs. |
| `getAvailableLogTypes()` | Returns available log types. |

#### DevTools / Grid

| Method | What it does |
|--------|--------------|
| `getDevTools()` | Returns Chrome DevTools instance. |
| `captureConsoleLogs(Consumer<String>)` | Listens to console logs. |
| `captureNetworkLogs(Consumer<String>)` | Listens to network responses. |
| `clearBrowserCache()` | Clears browser cache via DevTools. |
| `getSessionId()` | Returns remote/grid session ID. |
| `getCapabilities()` | Returns browser capabilities. |

---

### 8.2 `ConfigManager` methods

| Method | What it does |
|--------|--------------|
| `get(String key)` | Returns property value; system property overrides file value. |
| `get(String key, String defaultValue)` | Returns value or default if missing. |
| `getInt(String key)` | Returns value parsed as integer. |

---

### 8.3 `DriverFactory` methods

| Method | What it does |
|--------|--------------|
| `createDriver(String browser, boolean headless)` | Returns Chrome/Firefox/Edge driver based on inputs. |
| `createChromeDriver(boolean headless)` | Creates Chrome driver with options. |
| `createFirefoxDriver(boolean headless)` | Creates Firefox driver with options. |
| `createEdgeDriver(boolean headless)` | Creates Edge driver with options. |

---

### 8.4 `BaseTest` methods

| Method | What it does |
|--------|--------------|
| `setUp(String browser, String headless)` | `@BeforeMethod`: creates driver, actions, navigates to base URL. |
| `tearDown(ITestResult result)` | `@AfterMethod`: screenshots on failure, quits driver. |
| `getDriver()` | Returns the current WebDriver. |
| `getActions()` | Returns the current SeleniumActions wrapper. |

---

### 8.5 `LoginPage` methods

| Method | What it does |
|--------|--------------|
| `LoginPage(SeleniumActions actions)` | Constructor: receives actions wrapper. |
| `isLogoDisplayed()` | Returns true if logo is visible. |
| `isSignInTextDisplayed()` | Returns true if Sign In text is visible. |
| `enterEmail(String email)` | Types email into email input. |
| `clickContinue()` | Clicks the continue/submit button. |
| `loginWithEmail(String email)` | Types email and clicks continue in one call. |

---

### 8.6 `LoginTest` methods

| Method | What it does |
|--------|--------------|
| `verifyLoginPageLoads()` | Verifies logo and Sign In text are displayed. |
| `loginWithValidEmail()` | Enters email, clicks continue, verifies URL changed. |

---

### 8.7 Listener methods

#### `ExtentReportListener`

| Method | What it does |
|--------|--------------|
| `onStart(ITestContext)` | Initializes ExtentReports and Spark reporter. |
| `onTestStart(ITestResult)` | Creates a new test node in the report. |
| `onTestSuccess(ITestResult)` | Marks the test as passed. |
| `onTestFailure(ITestResult)` | Marks the test as failed, logs exception, attaches screenshot. |
| `onTestSkipped(ITestResult)` | Marks the test as skipped. |
| `onFinish(ITestContext)` | Writes the final HTML report to disk. |
| `attachScreenshot(...)` | Captures screenshot from `BaseTest` and embeds it in report. |

#### `RetryAnalyzer`

| Method | What it does |
|--------|--------------|
| `retry(ITestResult)` | Returns true if the test should be retried; false otherwise. |
| `getMaxRetries()` | Reads `max.retries` property or uses default. |

#### `AnnotationTransformer`

| Method | What it does |
|--------|--------------|
| `transform(ITestAnnotation, ...)` | Sets `RetryAnalyzer` on every `@Test` method. |

---

## 9. Data Flow Deep Dive

This section explains exactly what happens from the moment you run the command
until the final report is produced.

### 9.1 Command execution

You type:

```bash
mvn test -Denvironment=qa -Dbrowser=chrome -Dheadless=false
```

### 9.2 Maven phase

1. Maven reads `pom.xml`.
2. It applies the active profile (`chrome` by default).
3. It resolves dependencies from Maven Central if not cached.
4. It runs `clean`, `compile`, `test-compile`, then `surefire:test`.

### 9.3 Surefire + TestNG

1. Surefire reads the configured suite XML:
   `${project.basedir}/src/test/resources/testng.xml`.
2. TestNG parses the suite.
3. TestNG registers listeners:
   - `ExtentReportListener`
   - `AnnotationTransformer`
4. TestNG loads the test class `LoginTest`.

### 9.4 Listener: AnnotationTransformer

Before running tests, TestNG calls `AnnotationTransformer.transform(...)` for
every `@Test` method. It sets `RetryAnalyzer.class` as the retry analyzer for
that method.

### 9.5 Listener: ExtentReportListener.onStart

The report engine starts:
- Creates `reports/` directory if missing.
- Creates `ExtentSparkReporter` pointing to `reports/ExtentReport.html`.
- Attaches the reporter to `ExtentReports`.
- Sets system info (environment, browser, OS).

### 9.6 Before each test method

For `LoginTest.verifyLoginPageLoads`:

1. TestNG calls `ExtentReportListener.onTestStart`.
   - A new `ExtentTest` node is created.
2. TestNG calls `BaseTest.setUp`.
   - `DriverFactory.createDriver("chrome", false)` is called.
   - ChromeDriver is created.
   - `SeleniumActions` is created with the driver.
   - `actions.maximizeWindow()` is called.
   - `actions.navigateTo(ConfigManager.get("base.url"))` is called.
   - `ConfigManager` reads `src/test/resources/config/qa.properties`.
   - Browser navigates to `https://app.walnutai.com`.

### 9.7 Test body

`LoginTest.verifyLoginPageLoads` runs:

```java
LoginPage loginPage = new LoginPage(actions);
Assert.assertTrue(loginPage.isLogoDisplayed());
```

Flow inside `LoginPage.isLogoDisplayed()`:

1. `LoginPage` holds a reference to `actions`.
2. It calls `actions.isDisplayed(logo)`.
3. `SeleniumActions.isDisplayed(logo)`:
   - Tries `driver.findElement(logo).isDisplayed()`.
   - Returns true/false.
4. The result goes back to the test.
5. `Assert.assertTrue(...)` passes or fails.

### 9.8 After each test method

1. TestNG calls `BaseTest.tearDown(ITestResult)`.
   - If the test failed, `actions.takeScreenshot(...)` is called.
   - Screenshot is saved to `screenshots/<name>_FAILED.png`.
   - `driver.quit()` closes the browser.
2. TestNG calls `ExtentReportListener.onTestSuccess` or `onTestFailure`.
   - On success: test marked passed.
   - On failure:
     - Exception is logged.
     - `attachScreenshot(result, test)` is called.
     - It casts `result.getInstance()` to `BaseTest`.
     - It calls `baseTest.getActions().takeScreenshot(...)`.
     - Screenshot is embedded in the Extent report.

### 9.9 Retry flow

If `LoginTest.loginWithValidEmail` fails:

1. TestNG checks `RetryAnalyzer.retry(result)`.
2. If retry count < `max.retries`, TestNG re-runs the test.
3. `BaseTest.setUp` runs again (fresh browser).
4. Test body runs again.
5. If it still fails after max retries, TestNG marks it final-failed.

### 9.10 Suite finish

1. TestNG calls `ExtentReportListener.onFinish`.
2. `extent.flush()` writes `reports/ExtentReport.html`.
3. Logback finishes writing `logs/automation.log`.
4. Maven prints the test summary.

### 9.11 CI flow (GitHub Actions)

On push or pull request:

1. GitHub Actions runner starts.
2. Checks out the repository.
3. Installs JDK 26.
4. Installs Chrome.
5. Runs `mvn test -Dheadless=true -Denvironment=qa -Dmax.retries=1`.
6. Uploads artifacts:
   - `reports/ExtentReport.html`
   - `screenshots/`
   - `logs/automation.log`

### 9.12 Visual summary

```
Command: mvn test
    |
    v
pom.xml (Maven)
    |
    v
testng.xml (TestNG suite)
    |
    v
AnnotationTransformer -> sets retry analyzer
ExtentReportListener.onStart -> creates report
    |
    v
For each @Test method:
  |
  +-- onTestStart (report node)
  |
  +-- BaseTest.setUp
  |       +-- DriverFactory -> WebDriver
  |       +-- SeleniumActions
  |       +-- ConfigManager -> qa.properties
  |       +-- navigateTo(base.url)
  |
  +-- Test method body
  |       +-- Page Object
  |       +-- SeleniumActions
  |       +-- Assertion
  |
  +-- BaseTest.tearDown
  |       +-- screenshot on failure
  |       +-- driver.quit()
  |
  +-- onTestSuccess / onTestFailure
          +-- attach screenshot to report
          +-- retry if needed
    |
    v
ExtentReportListener.onFinish -> writes HTML report
Logback -> writes automation.log
```

---

## 10. Configuration Reference

### 10.1 Properties files

Each environment file uses the same keys.

| Key | Example | Purpose |
|-----|---------|---------|
| `base.url` | `https://app.walnutai.com` | Application starting URL. |
| `browser` | `chrome` | Default browser. |
| `headless` | `false` | Run without visible window. |
| `implicit.wait` | `10` | Selenium implicit wait in seconds. |
| `explicit.wait` | `15` | Default explicit wait in seconds. |
| `screenshot.folder` | `screenshots` | Folder for screenshots. |
| `login.email` | `test@example.com` | Test email. |
| `login.success.url.fragment` | `verify` | Expected URL fragment after login. |

### 10.2 System property overrides

| Property | How to use |
|----------|------------|
| `environment` | `-Denvironment=uat` |
| `browser` | `-Dbrowser=firefox` |
| `headless` | `-Dheadless=true` |
| `max.retries` | `-Dmax.retries=2` |

### 10.3 Maven profiles

| Profile | Browser | Headless | Environment |
|---------|---------|----------|-------------|
| `chrome` (default) | chrome | false | qa |
| `firefox` | firefox | false | qa |
| `edge` | edge | false | qa |
| `headless` | chrome | true | qa |
| `uat` | chrome | false | uat |
| `prod` | chrome | true | prod |

---

## 11. Troubleshooting

### `package org.testng does not exist`

Make sure listener and base classes are in `src/test/java`, not `src/main/java`.
TestNG is declared with `<scope>test</scope>`.

### Browser does not open

- Check that the browser is installed.
- Try headless mode: `mvn test -Dheadless=true`.
- Check `logs/automation.log` for driver errors.

### `WebDriverException: unknown error: net::ERR_SSL_UNRECOGNIZED_NAME_ALERT`

The application URL has an SSL certificate issue. Either fix the certificate or
temporarily use `http://` for local testing.

### Report is empty or missing

- Ensure `ExtentReportListener` is registered in `testng.xml`.
- Check that the `reports/` folder is writable.

### Screenshot not attached to report

- Ensure the test class extends `BaseTest`.
- Ensure `actions` is not null (test failed during setup before `actions` was created).

### Tests run very slowly

- Reduce timeouts in properties files.
- Remove unnecessary `staticWait` calls.
- Use headless mode in CI.

### How to debug a failing test

1. Run the specific test:
   ```bash
   mvn test -Dtest=LoginTest#verifyLoginPageLoads
   ```
2. Open `reports/ExtentReport.html`.
3. Check `logs/automation.log`.
4. Check `screenshots/` for failure images.

---

## 12. Quick Start Checklist

- [ ] Java 26 installed (`java -version`).
- [ ] Maven installed (`mvn -version`).
- [ ] Chrome installed (or use `-Dheadless=true`).
- [ ] `base.url` updated in `qa.properties`.
- [ ] Real locators added to `LoginPage.java`.
- [ ] Run `mvn clean compile test-compile`.
- [ ] Run `mvn test`.
- [ ] Open `reports/ExtentReport.html`.

