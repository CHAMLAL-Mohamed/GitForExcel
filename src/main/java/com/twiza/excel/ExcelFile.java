//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza.excel;

import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;


public class ExcelFile {
    private Map<String, ESheet> sheets;


    public ExcelFile() {
        sheets = new HashMap<>();
    }

    public void AddSheet(Sheet sheet) {
        this.sheets.computeIfAbsent(sheet.getSheetName(), (k) -> {
            ESheet eSheet = new ESheet(sheet.getSheetName());
            eSheet.createSheet(sheet);
            return eSheet;
        });
    }

    private void addSheet(ESheet eSheet, Status status) {
        eSheet.setStatus(status);
        sheets.putIfAbsent(eSheet.getName(), eSheet);
    }
    //we will compare the sheets first :added, deleted, changed . Take the changed sheets and analyse the added, deleted, and changed elements

    public ExcelFile compare(@Nonnull ExcelFile file) throws NullPointerException {
        // create set with all sheets
        Set<String> allSheets = new HashSet<>(file.sheets.keySet());
        allSheets.addAll(this.sheets.keySet());
        ExcelFile compareFile = new ExcelFile();

        //Assign the status of each sheet based on if it was deleted in the new file, added,
        //or common between the two.
        Consumer<String> assignSheetStatus = sheetName -> {
            if (!this.sheets.containsKey(sheetName)) {
                compareFile.addSheet(file.sheets.get(sheetName), Status.DELETED);
            } else if (!file.sheets.containsKey(sheetName)) {
                compareFile.addSheet(this.sheets.get(sheetName), Status.ADDED);
            } else {
                compareFile.addSheet(this.sheets.get(sheetName)
                        .compare(file.sheets.get(sheetName)), Status.COMMON);
            }
            //Display Sheet Status
            System.out.println("Key: " + sheetName + " value: "
                    + compareFile.sheets.get(sheetName).getStatus());
        };

        allSheets.forEach(assignSheetStatus);
        return compareFile;
    }


    public Map<String, ERow> getSheetData(@Nonnull String sheetName) {
        return Collections.unmodifiableMap(this.sheets.getOrDefault(sheetName, null).getData());
    }
}
