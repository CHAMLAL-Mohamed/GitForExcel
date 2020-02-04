package com.twiza.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

final class ESheet {
    private final String name;
    private Map<String, ERow> data;
    private Status status;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;

    ESheet(String name) {
        this.name = name;
        this.data = new LinkedHashMap<>();
    }


    String getName() {
        return name;
    }

    Map<String, ERow> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    void setStatus(Status status) {
        this.status = status;
    }

    Status getStatus() {
        return status;
    }

    void createSheet(Sheet sheet) throws IllegalArgumentException {
        this.dataFormatter = new DataFormatter();
        this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) sheet.getWorkbook());
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            List<String> list = this.readRow((Row) rows.next());
            ERow row = new ERow(list.get(0), list.subList(1, list.size()));
            this.data.putIfAbsent(row.getId(), row);
        }
    }

    private void addRow(ERow row, Status status) {
        row.setStatus(status);
        this.data.putIfAbsent(row.getId(), row);
    }

    ESheet compare(ESheet sheet) {
        Set<String> allElements = new HashSet<>(sheet.getData().keySet());
        allElements.addAll(this.getData().keySet());
        ESheet compareSheet = new ESheet(this.getName());

        //or common between the two.
        Consumer<String> assignElementStatus = elementId -> {
            if (!this.getData().containsKey(elementId)) {
                compareSheet.addRow(sheet.getData().get(elementId), Status.DELETED);
            } else if (!sheet.getData().containsKey(elementId)) {
                compareSheet.addRow(this.getData().get(elementId), Status.ADDED);
            } else {
                compareSheet.addRow(this.getData().get(elementId)
                        .compare(sheet.getData().get(elementId)), Status.COMMON);
            }
            //Display Row status
            System.out.println("Key: " + elementId + " status: "
                    + compareSheet.getData().get(elementId).getStatus());
        };
        allElements.forEach(assignElementStatus);  //TODO: Implement the logic for Row Comparing

        return compareSheet;
    }

    private List<String> readRow(Row row) {
        if (row == null) {
            return null;
        } else {
            List<String> rowList = new ArrayList<>();
            Iterator cells = row.cellIterator();

            while (cells.hasNext()) {
                rowList.add(this.readCell((Cell) cells.next()));
            }

            return rowList;
        }
    }

    public void writeToSheet(Sheet sheet) {
        AtomicInteger rowCounter = new AtomicInteger();
        this.data.forEach((key, value) -> value.writeToExcel(sheet.createRow((rowCounter.getAndIncrement()))));
    }


    private String readCell(Cell cell) {
        this.formulaEvaluator.evaluate(cell);
        return this.dataFormatter.formatCellValue(cell, this.formulaEvaluator);
    }




}
