package com.twiza.domain;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelWorkbook implements EWorkbook {
    private final static String SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE = "EWorkbook cannot contain 2 getSheets() with the same Name";

    private String path;
    private Map<String, ESheet> sheets;

    public ExcelWorkbook() {
    }

    public ExcelWorkbook(String path, List<ESheet> sheetsList) {
        this.path = path;
        this.sheets = assignSheets(sheetsList);
    }

    private Map<String, ESheet> assignSheets(List<ESheet> sheetsList) {
        final Map<String, ESheet> tempSheetsMap = new HashMap<>();
        sheetsList.forEach(sheet -> addSheetToSheetsMap(sheet, tempSheetsMap));
        return tempSheetsMap;
    }

    private void addSheetToSheetsMap(ESheet sheet, Map<String, ESheet> sheets) {
        if (sheets.putIfAbsent(sheet.getName().toLowerCase(), sheet) != null) {
            throw new UnsupportedOperationException(SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public ESheet getSheet(String name) {
        return sheets.getOrDefault(name.toLowerCase(), null);
    }

    @Override
    public ESheet getSheet(int index) {
        return null;
    }

    @Override
    public String getWorkbookPath() {
        return path;
    }

    @Override
    public Map<String, ESheet> getSheets() {
        return Collections.unmodifiableMap(sheets);
    }

    @Override
    public int getSize() {
        return sheets.size();
    }

    @Override
    public void addSheet(ESheet sheet) {
        addSheetToSheetsMap(sheet, sheets);

    }


    @Override
    public boolean removeSheet(String sheetName) {
        return !(sheets.remove(sheetName.toLowerCase()) == null);
    }

    @Override
    public EWorkbook compare(EWorkbook oldWorkbook) {
        if (oldWorkbook == null) {
            throw new NullPointerException("the oldWorkbook cannot be null");
        }
        Set<String> allSheets = new HashSet<>(oldWorkbook.getSheets().keySet());
        allSheets.addAll(getSheets().keySet());
        EWorkbook diffWorkbook = new ExcelWorkbook();
        allSheets.forEach(sheetName -> assignSheetToCompareWorkbook(oldWorkbook.getSheet(sheetName),
                                                                    getSheet(sheetName),
                                                                    diffWorkbook));

        return diffWorkbook;
    }

    private void assignSheetToCompareWorkbook(ESheet oldSheet, ESheet currentSheet, EWorkbook workbook) {
        if (currentSheet == null) {
            //if current is null than old is not because the key is extracted from one of them
            oldSheet.setStatus(Status.DELETED);
            workbook.addSheet(oldSheet);
            System.out.println("Sheet "+oldSheet.getName()+" is deleted");
        } else {
            workbook.addSheet(currentSheet.compare(oldSheet));
        }
    }

    @Override
    public String toString() {
        return sheets.values()
                     .stream()
                     .map(sheet -> ("------\n" + sheet.getName() + "\n\n" + sheet.toString()))
                     .collect(Collectors.joining());
    }
}
