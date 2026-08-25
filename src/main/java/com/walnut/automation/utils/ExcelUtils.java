package com.walnut.automation.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Example utility to read test data from Excel files.
 * Place .xlsx files under src/test/resources/testdata/.
 */
public class ExcelUtils {

    private ExcelUtils() {
        // utility class
    }

    public static List<List<String>> readSheet(String resourcePath, String sheetName) {
        List<List<String>> data = new ArrayList<>();

        try (InputStream input = ExcelUtils.class.getClassLoader().getResourceAsStream(resourcePath);
             Workbook workbook = new XSSFWorkbook(input)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    rowData.add(cells.next().toString());
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + resourcePath, e);
        }

        return data;
    }
}
