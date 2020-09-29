package com.twiza.utils;

import com.twiza.domain.ESheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriter {
    /**
     * a static instance of this class to ensure Singleton.
     */
    private static ExcelWriter INSTANCE;

    private ExcelWriter() {
    }

    public static ExcelWriter getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ExcelWriter();
        return INSTANCE;
    }

    public void writeToWorkbook(String workbookPath, ESheet eSheet) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(eSheet.getName());
        AtomicInteger rowIndex = new AtomicInteger(0);
        writeToRow(eSheet.getHeaders(), sheet, rowIndex.getAndIncrement());
        eSheet.getUniqueData().forEach((key, value) -> writeToRow(value.getCellsValues(), sheet, rowIndex.getAndIncrement()));
        FileOutputStream outputStream = new FileOutputStream(workbookPath);
        workbook.write(outputStream);
        workbook.close();
    }

    private void writeToRow(List<String> list, Sheet sheet, int rowNumber) {
        Row row = sheet.createRow(rowNumber);
        int columnIndex = 0;
        for (String value : list) {
            writeToCell(value, row, columnIndex++);
        }
    }

    private void writeToCell(String value, Row row, int columnNumber) {
        Cell cell = row.createCell(columnNumber);
        //Add some logic to add CellType
        cell.setCellValue(value);

    }
}
