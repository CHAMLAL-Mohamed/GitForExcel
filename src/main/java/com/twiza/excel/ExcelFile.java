//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;


public class ExcelFile {

    ExcelFileParams excelFileParams;
    List<String> ignoreSheetsPatterns;
    /**
     * Map to store all the created ESheets
     */
    private Map<String, ESheet> eSheets;

    /**
     * --------------------------------------------------------------------------Constructors--------------------------------------
     */
    public ExcelFile(ExcelFileParams excelFileParams) {
        this(excelFileParams, null);
    }

    public ExcelFile() {
        this(null, null);
    }

    /**
     * Constructor that takes the workbook and ignore patterns to create the needed sheets
     *
     * @param excelFileParams      the ExcelFile Parameters (
     * @param ignoreSheetsPatterns list of sheets's patterns to ignore.
     */
    public ExcelFile(ExcelFileParams excelFileParams, List<String> ignoreSheetsPatterns) {
        this.excelFileParams = excelFileParams;
        this.ignoreSheetsPatterns = ignoreSheetsPatterns;
        eSheets = generateESheets(excelFileParams, ignoreSheetsPatterns);
    }


    /**
     * --------------------------------------------------------------------------Logic Functions--------------------------------------
     */
    private Map<String, ESheet> generateESheets(ExcelFileParams excelFileParams, List<String> ignoreSheetsPatterns) {
        Map<String, ESheet> eSheetsMap = new LinkedHashMap<>();
        filterIgnoredSheets(excelFileParams, ignoreSheetsPatterns).forEach(sheet -> eSheetsMap.put(sheet.getSheetName(),
                new ESheet(sheet, excelFileParams.getDataFormatter(), excelFileParams.getFormulaEvaluator())));

        return eSheetsMap;
    }

    /**
     * this methode takes the ExcelFilePrams and the ignore patterns as parameters an returns the considered ESheet.
     *
     * @param excelFileParams      the file parameters(Workbook, DataFormatter, and FormulaEvaluator)
     * @param ignoreSheetsPatterns the patterns of sheets names that should be ignored
     * @return a List of Sheets after removing the unwanted Sheets
     */
    private List<Sheet> filterIgnoredSheets(ExcelFileParams excelFileParams, List<String> ignoreSheetsPatterns) {
        List<Sheet> sheets = new ArrayList<>();
        excelFileParams.getWorkbook().iterator().forEachRemaining(sheet -> {
            if (!shouldBeIgnored(sheet.getSheetName(), ignoreSheetsPatterns)) {
                sheets.add(sheet);
            }
        });
        return sheets;
    }

    /**
     * this method decide if the provided sheet should be considered for further processing or not.
     *
     * @param sheetName            the Sheet's name that should be checked if it matches any patterns
     * @param ignoreSheetsPatterns the patterns to match against
     * @return if the sheet should be considered or no for further processing
     */
    private boolean shouldBeIgnored(String sheetName, List<String> ignoreSheetsPatterns) {
        //TODO(1): check if patterns is not empty and create the logic to check against the patterns.
        if (ignoreSheetsPatterns == null) {
            return false;
        }
        return ignoreSheetsPatterns.stream().anyMatch(pattern -> Pattern.matches(pattern, sheetName));
    }


    /**
     * @param sheetIndex
     * @throws IllegalArgumentException
     */
    public Sheet addSheetWithIndex(int sheetIndex) throws IllegalArgumentException {
        return excelFileParams.getWorkbook().getSheetAt(sheetIndex);
    }

    /**
     * @param sheetName
     */
    public Sheet addSheetWithName(String sheetName) {
        return excelFileParams.getWorkbook().getSheet(sheetName);
    }

    private void addDefaultSheet(Sheet sheet) {
        this.eSheets.computeIfAbsent(sheet.getSheetName(), (k) -> {
            return new ESheet(sheet, excelFileParams.getDataFormatter(), excelFileParams.getFormulaEvaluator());
        });
    }

    private void addESheet(ESheet eSheet, Status status) {
        eSheet.setStatus(status);
        eSheets.putIfAbsent(eSheet.getName(), eSheet);
    }
    //we will compare the sheets first :added, deleted, changed . Take the changed sheets and analyse the added, deleted, and changed elements

    public ExcelFile compare(@Nonnull ExcelFile file) throws NullPointerException {
        // create set with all sheets
        Set<String> allSheets = new HashSet<>(file.eSheets.keySet());
        allSheets.addAll(this.eSheets.keySet());
        ExcelFile compareFile = new ExcelFile();

        //Assign the status of each sheet based on if it was deleted in the new file, added,
        //or common between the two.
        Consumer<String> assignSheetStatus = sheetName -> {
            if (!this.eSheets.containsKey(sheetName)) {
                compareFile.addESheet(file.eSheets.get(sheetName), Status.DELETED);
            } else if (!file.eSheets.containsKey(sheetName)) {
                compareFile.addESheet(this.eSheets.get(sheetName), Status.ADDED);
            } else {
                compareFile.addESheet(this.eSheets.get(sheetName)
                        .compare(file.eSheets.get(sheetName)), Status.COMMON);
            }
            //Display Sheet Status
            System.out.println("Sheet: " + sheetName + " have the status: "
                    + compareFile.eSheets.get(sheetName).getStatus());
        };

        allSheets.forEach(assignSheetStatus);
        return compareFile;
    }

    public void writeToExcel(Workbook workbook) throws NullPointerException {
        try {
            this.eSheets.keySet().forEach(key -> {
                ESheet sheet = eSheets.get(key);
                sheet.writeToSheet(workbook.createSheet(key));
            });
        } catch (NullPointerException e) {
            throw new NullPointerException("This Workbook is null");
        }

    }


    public ExcelFileParams getExcelFileParams() {
        return excelFileParams;
    }

    public Map<String, ERow> getSheetData(@Nonnull String sheetName) {
        return Collections.unmodifiableMap(this.eSheets.getOrDefault(sheetName, null).getESheetData());
    }

    public ESheet getESheet(String eSheetName) {
        return eSheets.get(eSheetName);
    }

}
