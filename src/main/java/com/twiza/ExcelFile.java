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
    //we will compare the sheets first :added, deleted, changed . Take the changed sheets and analyse the added, deleted, and changed elements

    ExcelFile compare(@Nonnull ExcelFile file) {
        // create set with all sheets
        //TODO : change the set to ESheet instead of Strings and add the status field in ESheet
        Set<String> allSheets = new HashSet<>(file.sheets.keySet());
        ExcelFile compareFile = new ExcelFile();

        allSheets.addAll(this.sheets.keySet());
        Map<String, SheetStatus> sheetsStatus = new HashMap<>();

        //Assign the status of each sheet based on if it was deleted in the new file, added,
        //or common between the two.
        Consumer<String> assignSheetStatus = sheetName -> {
            if (!this.sheets.containsKey(sheetName)) {
                sheetsStatus.computeIfAbsent(sheetName, value -> SheetStatus.DELETED);
                this.sheets.get(sheetName).setStatus(SheetStatus.DELETED);
            } else if (!file.sheets.containsKey(sheetName)) {
                sheetsStatus.computeIfAbsent(sheetName, value -> SheetStatus.ADDED);
                file.sheets.get(sheetName).setStatus(SheetStatus.ADDED);
            } else {
                sheetsStatus.computeIfAbsent(sheetName, value -> SheetStatus.COMMON);
                this.sheets.get(sheetName).setStatus(SheetStatus.ADDED);
                file.sheets.get(sheetName).setStatus(SheetStatus.ADDED);
            }
        };

        Consumer<String> displaySheetStatus = sheetName -> System.out.println(
                "Key: " + sheetName + " value: " +
                        sheetsStatus.getOrDefault(sheetName, null));
        Predicate<String> filterCommonSheets = sheetName ->
                sheetsStatus.getOrDefault(sheetName, null).equals(SheetStatus.COMMON);
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
