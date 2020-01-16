//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza;

import org.apache.poi.ss.usermodel.Sheet;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private void addSheet(ESheet eSheet, SheetStatus status) {
        eSheet.setStatus(status);
        sheets.putIfAbsent(eSheet.getName(), eSheet);
    }
    //we will compare the sheets first :added, deleted, changed . Take the changed sheets and analyse the added, deleted, and changed elements

    ExcelFile compare(@Nonnull ExcelFile file) {
        // create set with all sheets
        //TODO : change the set to ESheet instead of Strings and add the status field in ESheet
        Set<String> allSheets = new HashSet<>(file.sheets.keySet());
        allSheets.addAll(this.sheets.keySet());
        ExcelFile compareFile = new ExcelFile();

        //Assign the status of each sheet based on if it was deleted in the new file, added,
        //or common between the two.
        Consumer<String> assignSheetStatus = sheetName -> {
            if (!this.sheets.containsKey(sheetName)) {
                compareFile.addSheet(file.sheets.get(sheetName), SheetStatus.DELETED);
            } else if (!file.sheets.containsKey(sheetName)) {
                compareFile.addSheet(this.sheets.get(sheetName), SheetStatus.ADDED);
            } else {
                compareFile.addSheet(file.sheets.get(sheetName), SheetStatus.COMMON);
            }
        };

        Consumer<String> displaySheetStatus = sheetName ->
                System.out.println(
                        "Key: " + sheetName + " value: "
                                + compareFile.sheets.get(sheetName).getStatus());

        Predicate<String> filterCommonSheets = sheetName ->
                compareFile.sheets.get(sheetName).getStatus().equals(SheetStatus.COMMON);

        //Starting a stream to assign sheetStatus and filter only common sheets
        allSheets.stream()
                .peek(assignSheetStatus)
                .peek(displaySheetStatus)
                .filter(filterCommonSheets)
                .forEach(sheetName -> System.out.println("Sheet kept: " + sheetName));  //TODO: Implement the logic for sheet comparing);
        return null;
    }


    public Map<String, ERow> getSheetData(@Nonnull String sheetName) {
        return Collections.unmodifiableMap(this.sheets.getOrDefault(sheetName, null).getData());
    }
}
