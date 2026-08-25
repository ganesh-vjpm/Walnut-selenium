package com.walnut.automation.config;

import com.walnut.automation.utils.ExcelConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads environment-specific configuration.
 *
 * Value resolution priority (highest to lowest):
 * 1. Java system property (-Dkey=value)
 * 2. OS environment variable (KEY=value or key=value)
 * 3. Environment properties file (config/qa.properties, config/uat.properties, etc.)
 * 4. Excel workbook (testdata/EnvironmentConfig.xlsx) - fallback for credentials/URLs per environment
 *
 * The active environment is selected via the "environment" system property or
 * the "ENVIRONMENT" / "environment" OS environment variable (default: qa).
 */
public class ConfigManager {

    private static final String DEFAULT_ENV = "qa";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String environment = resolveActiveEnvironment();
        String fileName = "config/" + environment + ".properties";

        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Configuration file not found: " + fileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + fileName, e);
        }
    }

    /**
     * Resolves the active environment from system property, env variable, or default.
     */
    private static String resolveActiveEnvironment() {
        String env = System.getProperty("environment");
        if (env != null && !env.isEmpty()) {
            return env;
        }

        env = System.getenv("ENVIRONMENT");
        if (env != null && !env.isEmpty()) {
            return env;
        }

        env = System.getenv("environment");
        if (env != null && !env.isEmpty()) {
            return env;
        }

        return DEFAULT_ENV;
    }

    /**
     * Returns the value for a key using the priority:
     * system property > environment variable > properties file > Excel fallback.
     */
    public static String get(String key) {
        // 1. Java system property (-Dkey=value)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // 2. OS environment variable (key=value or KEY=value)
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        value = System.getenv(key.toUpperCase().replace(".", "_"));
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // 3. Properties file
        value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // 4. Excel fallback (per-environment sheet)
        return ExcelConfigManager.get(key);
    }

    /**
     * Returns the value or a default if the key is not found anywhere.
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Returns the value parsed as an integer.
     */
    public static int getInt(String key) {
        String value = get(key);
        if (value == null) {
            throw new RuntimeException("Missing integer config key: " + key);
        }
        return Integer.parseInt(value);
    }

    /**
     * Returns the active environment name (qa, uat, prod, etc.).
     */
    public static String getEnvironment() {
        return resolveActiveEnvironment();
    }
}
