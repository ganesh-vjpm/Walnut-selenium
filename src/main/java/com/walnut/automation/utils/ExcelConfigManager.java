package com.walnut.automation.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads key-value configuration from an Excel workbook.
 *
 * Expected workbook structure:
 * - File: src/test/resources/testdata/EnvironmentConfig.xlsx
 * - One sheet per environment (qa, uat, prod)
 * - Each sheet has at least two columns: Key, Value
 *
 * Example sheet "qa":
 * | Key                  | Value                        |
 * |----------------------|------------------------------|
 * | base.url             | https://qa.wnut.ai/login     |
 * | login.email          | ganeshavjpm@gmail.com        |
 * | login.password       | Ganesh@2001                  |
 * | login.organization   | walnut-test                  |
 */
public class ExcelConfigManager {

    private static final String DEFAULT_FILE = "testdata/EnvironmentConfig.xlsx";

    private ExcelConfigManager() {
        // utility class
    }

    /**
     * Returns the active environment name from system property or environment variable.
     */
    private static String getActiveEnvironment() {
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

        return "qa";
    }

    /**
     * Reads a value from the active environment sheet in the default Excel file.
     *
     * @param key the configuration key to look up
     * @return the configured value, or null if not found
     */
    public static String get(String key) {
        return get(DEFAULT_FILE, getActiveEnvironment(), key);
    }

    /**
     * Reads a value from the specified sheet in the specified Excel resource file.
     *
     * @param resourcePath path to the Excel file under src/test/resources
     * @param sheetName    environment sheet to read
     * @param key          configuration key to look up
     * @return the configured value, or null if not found
     */
    public static String get(String resourcePath, String sheetName, String key) {
        try (InputStream input = ExcelConfigManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                return null;
            }

            try (Workbook workbook = new XSSFWorkbook(input)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    return null;
                }

                for (Row row : sheet) {
                    if (row == null || row.getCell(0) == null) {
                        continue;
                    }

                    String cellKey = getCellValueAsString(row.getCell(0));
                    if (cellKey == null || !cellKey.trim().equalsIgnoreCase(key.trim())) {
                        continue;
                    }

                    if (row.getCell(1) == null) {
                        return null;
                    }

                    return getCellValueAsString(row.getCell(1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel config: " + resourcePath + ", sheet: " + sheetName, e);
        }

        return null;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return cell.toString();
        }
    }
}
