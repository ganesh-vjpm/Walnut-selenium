package com.walnut.automation.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates a sample EnvironmentConfig.xlsx file with one sheet per environment.
 * Run this once to create the workbook, then edit the values in Excel as needed.
 */
public class ExcelConfigGenerator {

    public static void main(String[] args) throws IOException {
        Path outputDir = Paths.get("src", "test", "resources", "testdata");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        Path outputFile = outputDir.resolve("EnvironmentConfig.xlsx");

        Map<String, String> qaConfig = new LinkedHashMap<>();
        qaConfig.put("base.url", "https://qa.wnut.ai/login");
        qaConfig.put("browser", "chrome");
        qaConfig.put("headless", "false");
        qaConfig.put("implicit.wait", "10");
        qaConfig.put("explicit.wait", "15");
        qaConfig.put("login.email", "ganeshavjpm@gmail.com");
        qaConfig.put("login.password", "Ganesh@2001");
        qaConfig.put("login.organization", "walnut-test");
        qaConfig.put("login.success.url.fragment", "dashboard");

        Map<String, String> uatConfig = new LinkedHashMap<>();
        uatConfig.put("base.url", "https://qa.wnut.ai/login");
        uatConfig.put("browser", "chrome");
        uatConfig.put("headless", "false");
        uatConfig.put("implicit.wait", "10");
        uatConfig.put("explicit.wait", "15");
        uatConfig.put("login.email", "ganeshavjpm@gmail.com");
        uatConfig.put("login.password", "Ganesh@2001");
        uatConfig.put("login.organization", "walnut-test");
        uatConfig.put("login.success.url.fragment", "dashboard");

        Map<String, String> prodConfig = new LinkedHashMap<>();
        prodConfig.put("base.url", "https://app.walnutai.ai/login");
        prodConfig.put("browser", "chrome");
        prodConfig.put("headless", "false");
        prodConfig.put("implicit.wait", "10");
        prodConfig.put("explicit.wait", "15");
        prodConfig.put("login.email", "ganeshavjpm@gmail.com");
        prodConfig.put("login.password", "Ganesh@2001");
        prodConfig.put("login.organization", "walnut-test");
        prodConfig.put("login.success.url.fragment", "dashboard");

        try (Workbook workbook = new XSSFWorkbook()) {
            createSheet(workbook, "qa", qaConfig);
            createSheet(workbook, "uat", uatConfig);
            createSheet(workbook, "prod", prodConfig);

            try (FileOutputStream fileOut = new FileOutputStream(outputFile.toFile())) {
                workbook.write(fileOut);
            }
        }

        System.out.println("Created Excel config file: " + outputFile.toAbsolutePath());
    }

    private static void createSheet(Workbook workbook, String sheetName, Map<String, String> config) {
        Sheet sheet = workbook.createSheet(sheetName);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Key");
        header.createCell(1).setCellValue("Value");

        int rowIndex = 1;
        for (Map.Entry<String, String> entry : config.entrySet()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}
