package com.twiza;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

final class ESheet {
    private final String name;
    private Map<String, ERow> data;
    private SheetStatus status;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;

    ESheet(String name) {
        this.name = name;
        this.data = new LinkedHashMap<>();
    }

    ESheet(String name, SheetStatus status) {
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

    public void setStatus(SheetStatus status) {
        this.status = status;
    }

    public SheetStatus getStatus() {
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

    void compareSheet(ESheet sheet) {


    }

}
