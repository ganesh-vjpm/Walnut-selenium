package com.walnut.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads environment-specific properties from src/test/resources/config.
 * The active environment is selected via the "environment" system property.
 */
public class ConfigManager {

    private static final String DEFAULT_ENV = "qa";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String environment = System.getProperty("environment", DEFAULT_ENV);
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

    public static String get(String key) {
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
