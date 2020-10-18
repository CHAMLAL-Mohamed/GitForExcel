/*
 * Copyright  2020  Chamlal.Mohamed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twiza.domain;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelWorkbook implements EWorkbook {
    private final static String SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE = "EWorkbook cannot contain 2 getSheets() with the same Name";

    private String path;
    private final Map<String, ESheet> sheets;

    public ExcelWorkbook() {
        this.sheets = new HashMap<>();
    }

    public ExcelWorkbook(String path, List<ESheet> sheetsList) {
        this.path = path;
        this.sheets = assignSheets(sheetsList);
    }

    public ExcelWorkbook(EWorkbook workbook) {
        this.path = workbook.getWorkbookPath();
        this.sheets = new HashMap<>(workbook.getSheets().size());
        workbook.getSheets().forEach((key, value) -> this.sheets.putIfAbsent(key, new ExcelSheet(value)));
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
        Objects.requireNonNull(oldWorkbook, "the oldWorkbook cannot be null");
        Set<String> allSheets = assembleTwoSets(oldWorkbook.getSheets().keySet(), getSheets().keySet());
        EWorkbook diffWorkbook = new ExcelWorkbook();
        allSheets.forEach(sheetName -> {
//            System.out.println("Sheet to compare is" + "\t" + sheetName);
            assignSheetToCompareWorkbook(oldWorkbook.getSheet(sheetName),
                                         getSheet(sheetName),
                                         diffWorkbook);
        });
        return diffWorkbook;
    }

    private Set<String> assembleTwoSets(Set<String> set1, Set<String> set2) {
        Set<String> sumSet = new LinkedHashSet<>(set1);
        sumSet.addAll(set2);
        return sumSet;
    }

    private void assignSheetToCompareWorkbook(ESheet oldSheet, ESheet currentSheet, EWorkbook workbook) {
        if (currentSheet == null) {
            //if current is null than old is not because the key is extracted from one of them
            oldSheet.setStatus(Status.DELETED);
            workbook.addSheet(oldSheet);
//            System.out.println("Sheet " + oldSheet.getName() + "\t" + oldSheet.getStatus());
        } else if (oldSheet == null) {
            currentSheet.setStatus(Status.ADDED);
            workbook.addSheet(currentSheet);
//            System.out.println("Sheet " + currentSheet.getName() + "\t" + currentSheet.getStatus());
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
