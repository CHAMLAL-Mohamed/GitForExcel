package com.twiza.excel;


import org.apache.poi.ss.usermodel.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class ESheet {
    /** Read sheet mode*/
    /**
     * no constrains on Headers, usually used for sheets without defined templates/Const Number of columns
     */
    private static final int READ_MODE_DEFAULT = 0;
    /**
     * fixed number of columns, any extra columns will be dropped
     */
    private static final int READ_MODE_FIX_COLUMNS_NUMBER = 1;
    /**
     * using a defined template to read sheet, any columns that is not part of template will be dropped
     */
    private static final int READ_MODE_TEMPLATE = 2;
    /***/

    public static final int READ_MODE_SAME_HEADERS = 3;

    private static final Status DEFAULT_ESHEET_STATUS = Status.NEW;
    /**
     * original sheet from where data is extracted
     */
    Sheet sheet;
    DataFormatter dataFormatter;
    FormulaEvaluator formulaEvaluator;

    /**
     *
     */
    private List<String> sheetTemplate;
    private int columnsNumber;
    private int readMode;
    private static final int HEADERS_ROW = 0;
    private String name;
    private boolean hasHeader;
    private List<String> eSheetHeader;
    private Map<String, ERow> eSheetData;
    private Status status;

    /**
     * --------------------------------------------------------------------------Constructors--------------------------------------
     */
    /*-------------------------------------------------------------------------------------------------------------------------------*/

    ESheet(String name) {
        this.name = name;
    }


    ESheet(Sheet sheet, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator) {
        this(sheet, dataFormatter, formulaEvaluator, true, READ_MODE_DEFAULT, null, 0);
    }

    ESheet(Sheet sheet, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, boolean hasHeader) {
        this(sheet, dataFormatter, formulaEvaluator, hasHeader, READ_MODE_DEFAULT, null, 0);
    }

    ESheet(Sheet sheet, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, boolean hasHeader, int columnsNumber) {
        this(sheet, dataFormatter, formulaEvaluator, hasHeader, READ_MODE_FIX_COLUMNS_NUMBER, null, columnsNumber);
    }

    ESheet(Sheet sheet, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, boolean hasHeader,
           List<String> sheetTemplate) {
        this(sheet, dataFormatter, formulaEvaluator, hasHeader, READ_MODE_TEMPLATE, sheetTemplate, 0);
    }

    ESheet(Sheet sheet, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, boolean hasHeader, int readMode,
           List<String> sheetTemplate, int columnsNumber) {
        this.sheet = sheet;
        this.dataFormatter = dataFormatter;
        this.formulaEvaluator = formulaEvaluator;
        this.hasHeader = hasHeader;
        this.readMode = readMode;
        this.sheetTemplate = sheetTemplate;
        this.name = sheet.getSheetName();
        this.eSheetHeader = assignHeader(sheet, hasHeader);
        this.eSheetData = assignDataToESheet(sheet, hasHeader);
        this.columnsNumber = columnsNumber;
        status = DEFAULT_ESHEET_STATUS;
    }

/**
 * --------------------------------------------------------------------------Logic Functions--------------------------------------
 */
    /*-------------------------------------------------------------------------------------------------------------------------------*/


    /**
     * extract headers from the sheet in case it has one.
     *
     * @param sheet     the origin sheet of data.
     * @param hasHeader true if the sheet has headers.
     * @return true if the headers where assigned, false otherwise.
     */
    private List<String> assignHeader(Sheet sheet, boolean hasHeader) {
        List<String> headers = new ArrayList<>();
        if (hasHeader && sheet != null) {
            headers = new ArrayList<>(readRowToList(sheet.getRow(HEADERS_ROW)));
        }
        return headers;
    }

    /**
     * reads data from the sheet and return the number of rows
     *
     * @param sheet     the  Sheet from where data should be read
     * @param hasHeader if
     * @return
     */
    private Map<String, ERow> assignDataToESheet(Sheet sheet, boolean hasHeader) {
        Map<String, ERow> data = new LinkedHashMap<>();
        int firstRow = hasHeader ? HEADERS_ROW + 1 : HEADERS_ROW;

        int lastRowNumber = sheet.getLastRowNum();
        for (int i = firstRow; i < lastRowNumber; i++) {
            ERow row = readRowToERow(sheet.getRow(i));
            if (data.containsKey(row.getId())) {
                throw new IllegalArgumentException("Sheet contains duplicate Id");
            }
            data.put(row.getId(), row);
        }
        return data;
    }


    private void addERow(ERow eRow, Status status) {
        eRow.setStatus(status);
        this.eSheetData.putIfAbsent(eRow.getId(), eRow);
    }

    ESheet compare(ESheet eSheet) {
        Set<String> allElements = new HashSet<>(eSheet.getESheetData().keySet());
        allElements.addAll(this.getESheetData().keySet());
        ESheet compareSheet = new ESheet(this.getName());

        //or common between the two.
        Consumer<String> assignElementStatus = elementId -> {
            if (!this.getESheetData().containsKey(elementId)) {
                compareSheet.addERow(eSheet.getESheetData().get(elementId), Status.DELETED);
            } else if (!eSheet.getESheetData().containsKey(elementId)) {
                compareSheet.addERow(this.getESheetData().get(elementId), Status.ADDED);
            } else {
                compareSheet.addERow(this.getESheetData().get(elementId)
                        .compare(eSheet.getESheetData().get(elementId)), Status.COMMON);
            }
            //Display Row status
            System.out.println("Key: " + elementId + " status: "
                    + compareSheet.getESheetData().get(elementId).getStatus());
        };
        allElements.forEach(assignElementStatus);  //TODO: Implement the logic for Row Comparing

        return compareSheet;
    }

    private List<String> readRowToList(Row row) {
        if (row == null) {
            return null;
        } else {
            List<String> rowList = new ArrayList<>();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()) {
                rowList.add(readCell(cells.next(), formulaEvaluator, dataFormatter));
            }
            return rowList;
        }
    }

    private ERow readRowToERow(Row row) {
        return new ERow(row, dataFormatter, formulaEvaluator, Status.NEW);
    }

    private String readCell(Cell cell, FormulaEvaluator formulaEvaluator, DataFormatter dataFormatter) {
        formulaEvaluator.evaluate(cell);
        return dataFormatter.formatCellValue(cell, formulaEvaluator);
    }

    public void writeToSheet(Sheet sheet) {
        AtomicInteger rowCounter = new AtomicInteger();
        this.eSheetData.forEach((key, value) -> value.writeToExcel(sheet.createRow((rowCounter.getAndIncrement()))));
    }


/**
 * --------------------------------------------------------------------------Getters & Setters-------------------------------------
 */
    /*------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * get the name of the sheet
     *
     * @return the sheet's name
     */
    String getName() {
        return name;
    }

    /**
     * get the Map that contains all the data of the sheet
     *
     * @return sheet's data
     */
    public Map<String, ERow> getESheetData() {
        return Collections.unmodifiableMap(this.eSheetData);
    }


    public int getColumnsNumber() {
        return columnsNumber;
    }

    public int getReadMode() {
        return readMode;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public List<String> geteSheetHeader() {
        return eSheetHeader;
    }

    /**
     * set the status of the sheet, if it's ADDED, DELETED, or CHANGED
     *
     * @param status
     */
    void setStatus(Status status) {
        this.status = status;
    }

    /**
     * return the status of the sheet
     *
     * @return the status of the Sheet
     */
    Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append("\n");
        eSheetData.values().forEach(k -> stringBuilder.append(k).append("\n"));

        return stringBuilder.toString();
    }
}
