package com.company.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class ExcelReader {
    private ExcelReader() {
    }

    public static List<Map<String, String>> readSheet(String classpathLocation) {
        try (InputStream stream = ExcelReader.class.getClassLoader().getResourceAsStream(classpathLocation)) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing Excel resource: " + classpathLocation);
            }
            try (Workbook workbook = new XSSFWorkbook(stream)) {
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rows = sheet.iterator();
                if (!rows.hasNext()) {
                    return List.of();
                }
                Row headerRow = rows.next();
                List<String> headers = new ArrayList<>();
                DataFormatter formatter = new DataFormatter();
                for (Cell cell : headerRow) {
                    headers.add(formatter.formatCellValue(cell));
                }
                List<Map<String, String>> records = new ArrayList<>();
                while (rows.hasNext()) {
                    Row row = rows.next();
                    Map<String, String> record = new LinkedHashMap<>();
                    for (int index = 0; index < headers.size(); index++) {
                        Cell cell = row.getCell(index);
                        record.put(headers.get(index), formatter.formatCellValue(cell));
                    }
                    records.add(record);
                }
                return records;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + classpathLocation, exception);
        }
    }
}
