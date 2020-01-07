//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelFile {
    private Map<String, ESheet> sheets = new HashMap();

    public ExcelFile() {
    }

    public void AddSheet(Sheet sheet) {
        this.sheets.computeIfAbsent(sheet.getSheetName(), (k) -> {
            ESheet eSheet = new ESheet(sheet.getSheetName());
            eSheet.createSheet(sheet);
            return eSheet;
        });
    }

    public Map<String, List<String>> getSheetData(String sheetName) {
        return Collections.unmodifiableMap(((ESheet)this.sheets.getOrDefault(sheetName,  null)).getData());
    }
}
