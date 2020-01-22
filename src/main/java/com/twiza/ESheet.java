package com.twiza;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    ESheet(String name, Status status) {
        this.name = name;
        this.status = status;
        this.data = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, ERow> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void createSheet(Sheet sheet) throws IllegalArgumentException {
        this.dataFormatter = new DataFormatter();
        this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) sheet.getWorkbook());
        Iterator rows = sheet.rowIterator();
        while (rows.hasNext()) {
            List<String> list = this.readRow((Row) rows.next());
            ERow row = new ERow(list.get(0), list.subList(1, list.size()));
            this.data.putIfAbsent(row.getId(), row);
        }
    }

    private void addRow(ERow row, Status status) {
        ERow newRow = new ERow(row.getId(), row.getElements());
        newRow.setStatus(status);
        this.data.putIfAbsent(newRow.getId(), newRow);
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
                compareSheet.addRow(sheet.getData().get(elementId), Status.COMMON);
            }
        };

        Consumer<String> displayElementStatus = elementId -> System.out.println(
                "Key: " + elementId + " status: "
                        + compareSheet.getData().get(elementId).getStatus());


        Predicate<String> filterCommonElements = elementId ->
                compareSheet.getData().get(elementId).getStatus().equals(Status.COMMON);


        allElements.stream()
                .peek(assignElementStatus)
                .peek(displayElementStatus)
                .filter(filterCommonElements)
                .forEach(elementId -> System.out.println("commun elements kept: " + elementId));  //TODO: Implement the logic for Row Comparing


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

    private String readCell(Cell cell) {
        this.formulaEvaluator.evaluate(cell);
        return this.dataFormatter.formatCellValue(cell, this.formulaEvaluator);
    }

    public void addComment(Cell cell, String author, String commentText) {
        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        //i found it useful to show the comment box at the bottom right corner
        anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
        anchor.setCol2(cell.getColumnIndex() + 3); //...and ends at that given column
        anchor.setRow1(cell.getRowIndex() + 1); //one row below the cell...
        anchor.setRow2(cell.getRowIndex() + 5); //...and 4 rows high
        Drawing drawing = cell.getSheet().createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor(author);
        cell.setCellComment(comment);
    }


}
