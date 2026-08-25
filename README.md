# Walnut Selenium Automation Framework

A Java-based Selenium 4 test automation framework built with Maven, TestNG, and Page Object Model (POM).

---

## Project Structure

```
walnut_v2/
├── pom.xml                                     # Maven build configuration
├── README.md                                   # This file
├── .github/workflows/ci.yml                    # GitHub Actions CI pipeline
└── src/
    ├── main/java/com/walnut/automation/
    │   ├── actions/
    │   │   └── SeleniumActions.java            # Reusable Selenium wrapper methods
    │   ├── config/
    │   │   └── ConfigManager.java              # Reads environment properties
    │   ├── factory/
    │   │   └── DriverFactory.java              # Creates Chrome/Firefox/Edge drivers
    │   ├── pages/
    │   │   └── LoginPage.java                  # Page Object for Login page
    │   └── utils/
    │       └── ExcelUtils.java                 # Excel test-data reader example
    ├── test/java/com/walnut/automation/
    │   ├── base/
    │   │   └── BaseTest.java                   # Test base: driver setup & teardown
    │   ├── listeners/
    │   │   ├── AnnotationTransformer.java      # Globally assigns retry analyzer
    │   │   ├── ExtentReportListener.java       # HTML report + screenshots
    │   │   └── RetryAnalyzer.java              # Retries failed tests
    │   └── tests/
    │       └── LoginTest.java                  # Sample TestNG test class
    └── test/resources/
        ├── config/
        │   ├── qa.properties                   # QA environment settings
        │   └── uat.properties                  # UAT environment settings
        ├── logback.xml                         # Logging configuration
        └── testng.xml                          # TestNG suite configuration
```

---

## Folder & File Reference

| Path | Purpose |
|------|---------|
| `pom.xml` | Declares dependencies (Selenium, TestNG, Allure, Extent, REST Assured, etc.) and Maven profiles. |
| `src/main/java/.../actions/SeleniumActions.java` | Wrapper around Selenium WebDriver. Provides click, type, wait, dropdown, shadow DOM, screenshot, and many more reusable actions. |
| `src/test/java/.../base/BaseTest.java` | Every test class extends this. Opens the browser, navigates to base URL, and quits the driver after each test. Captures screenshots on failure. |
| `src/main/java/.../config/ConfigManager.java` | Loads `qa.properties` or `uat.properties` based on `-Denvironment=qa`. System properties override file values. |
| `src/main/java/.../factory/DriverFactory.java` | Returns a WebDriver instance for Chrome, Firefox, or Edge; supports headless mode. |
| `src/main/java/.../pages/LoginPage.java` | Example Page Object: stores locators and page-specific actions (enter email, click continue). |
| `src/main/java/.../utils/ExcelUtils.java` | Utility to read test data from Excel files in `src/test/resources/testdata/`. |
| `src/test/java/.../tests/LoginTest.java` | Example TestNG tests using Page Objects and assertions. |
| `src/test/java/.../listeners/ExtentReportListener.java` | Generates `reports/ExtentReport.html` and attaches failure screenshots. |
| `src/test/java/.../listeners/RetryAnalyzer.java` | Retries failed tests up to `max.retries` times. |
| `src/test/java/.../listeners/AnnotationTransformer.java` | Applies `RetryAnalyzer` to every `@Test` automatically. |
| `src/test/resources/config/*.properties` | Environment configuration: URL, browser, timeouts, credentials. |
| `src/test/resources/logback.xml` | Console and file logging; reduces Selenium noise. |
| `src/test/resources/testng.xml` | TestNG suite file that defines tests and registered listeners. |
| `.github/workflows/ci.yml` | GitHub Actions workflow that runs tests on push/PR and uploads artifacts. |

---

## How to Run

### Default run (Chrome, QA environment, headed)
```bash
mvn test
```

### Run with a specific environment
```bash
mvn test -Denvironment=uat
```

### Run with a different browser
```bash
mvn test -Dbrowser=firefox
```

### Run headless
```bash
mvn test -Dheadless=true
```

### Run a specific Maven profile
```bash
mvn test -Pchrome
mvn test -Pheadless
mvn test -Puat
```

### Run with retry on failure
```bash
mvn test -Dmax.retries=2
```

### Generate ExtentReport only (does not open browser)
The report is created automatically at `reports/ExtentReport.html` after every run.

### View logs
Execution logs are written to `logs/automation.log`.

---

## Adding a New Test

1. Create a Page Object under `src/main/java/com/walnut/automation/pages/`.
2. Create a Test class under `src/test/java/com/walnut/automation/tests/` extending `BaseTest`.
3. Use the injected `actions` field from `BaseTest` to interact with pages.
4. Add the test class to `src/test/resources/testng.xml`.

---

## Adding a New Page

1. Create a class in `pages/`.
2. Accept `SeleniumActions` in the constructor.
3. Declare `By` locators as private fields.
4. Add public methods for each user action on that page.

---

## Notes

- The framework uses **Java 26** and **Selenium 4.47**.
- Browser drivers are managed automatically by Selenium Manager.
- Screenshots on failure are saved to the `screenshots/` folder and attached to ExtentReports.
- Failed tests are automatically retried based on `-Dmax.retries`.
- CI runs on every push/PR to `main` or `develop` via GitHub Actions.
