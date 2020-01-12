//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza;

import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;

public class ExcelFile {
    private Map<String, ESheet> sheets;

    public ExcelFile() {
        this.sheets = new LinkedHashMap<>();
    }


    public void AddSheet(Sheet sheet) {
        this.sheets.computeIfAbsent(sheet.getSheetName(), (k) -> {
            ESheet eSheet = new ESheet(sheet.getSheetName());
            eSheet.createSheet(sheet);
            return eSheet;
        });
    }


    private void addAll(Map<String, ESheet> excelFileData, String separator) {
        excelFileData.keySet().forEach(key -> this.sheets.putIfAbsent(key + separator, excelFileData.get(key)));
    }

    //add this comments to a new Sheet
    static ExcelFile getFilesDiff(ExcelFile newFile, ExcelFile oldFile) {
        ExcelFile diffFile = new ExcelFile();
        diffFile.addAll(newFile.getData(), "/+");
        diffFile.addAll(oldFile.getData(), "/-");



//        diffFile.getData().keySet().forEach(System.out::println);
        return null;
    }

    public ESheet getESheet(String sheetName) {
        return this.sheets.getOrDefault(sheetName, null);
    }

    Set<String> getSheetsNames() {
        return this.sheets.keySet();
    }

    public Map<String, ESheet> getData() {

        return Collections.unmodifiableMap(sheets);
    }

    private String getSheetName(String name) {

        return name.substring(0, name.indexOf("/"));
    }

}
