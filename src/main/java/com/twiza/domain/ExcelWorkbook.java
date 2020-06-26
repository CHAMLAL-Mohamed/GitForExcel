package com.twiza.domain;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelWorkbook implements EWorkbook {
    private final static String SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE = "EWorkbook cannot contain 2 ESheets with the same Name";

    private final String path;
    private final Map<String, ESheet> sheets;


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
        if (sheets.putIfAbsent(sheet.getName(), sheet) != null) {
            throw new UnsupportedOperationException(SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public ESheet getSheet(String name) {
        return sheets.getOrDefault(name, null);
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
        return !(sheets.remove(sheetName) == null);
    }


    @Override
    public String toString() {
        return sheets.values()
                     .stream()
                     .map(sheet -> ("------\n" + sheet.getName() + "\n\n" + sheet.toString()))
                     .collect(Collectors.joining());
    }
}
