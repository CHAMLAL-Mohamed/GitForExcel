//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelBuilder implements EBuilder {
    private static ExcelBuilder instance;

    private ExcelBuilder() {
    }

    public static ExcelBuilder getInstance() {
        if (instance == null) {
            instance = new ExcelBuilder();
        }

        return instance;
    }

    public Workbook buildWorkbook(String path) throws IOException {
        return new XSSFWorkbook(path);
    }

    public Sheet buildESheet(Workbook workbook, int index) {
        return workbook.getSheetAt(index);
    }
}
