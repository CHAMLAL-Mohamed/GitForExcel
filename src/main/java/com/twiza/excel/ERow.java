package com.twiza.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class ERow {
    private final static Status DEFAULT_EROW_STATUS = Status.NEW;
    private final static int DEFAULT_ID_COLUMN = 0;
    private final String id;
    private List<String> elements;
    private int idColumn;

    private Row row;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;
    private Status status;
    private Map<Integer, String> changedElements;

    /**
     * --------------------------------------------------------------------------Constructors--------------------------------------
     */
    /*-------------------------------------------------------------------------------------------------------------------------------*/
    public ERow(String id, List<String> elements) {
        this.id = id;
        this.elements = elements;
        this.changedElements = new HashMap<>();
        this.status = DEFAULT_EROW_STATUS;
    }

    public ERow(Row row, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, Status status) {
        this(row, dataFormatter, formulaEvaluator, status, DEFAULT_ID_COLUMN);

    }

    public ERow(Row row, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator, Status status, int idColumn) {
        this.row = row;
        this.dataFormatter = dataFormatter;
        this.formulaEvaluator = formulaEvaluator;
        this.idColumn = idColumn;
        this.id = assignId(idColumn);
        this.elements = assignElements();
        this.status = status;

    }

    /**
     * used to get the Id from the Row based on the idColumn
     *
     * @return
     */
    private String assignId(int idColumn) {
        if (row == null) {
            return null;
        }
        return readCell(row.getCell(idColumn), dataFormatter, formulaEvaluator);
    }

    private List<String> assignElements() {
        if (row == null) {
            return null;
        } else {
            List<String> rowList = new ArrayList<>();
            Iterator<Cell> cells = row.cellIterator();
            cells.next(); // to ignore the firstColumn(default id column)
            while (cells.hasNext()) {
                rowList.add(readCell(cells.next(), dataFormatter, formulaEvaluator));
            }
            return rowList;
        }
    }


    private List<String> readRowToList(Row row) {
        if (row == null) {
            return null;
        } else {
            List<String> rowList = new ArrayList<>();
            Iterator<Cell> cells = row.cellIterator();
            while (cells.hasNext()) {
                rowList.add(readCell(cells.next(), dataFormatter, formulaEvaluator));
            }
            return rowList;
        }
    }

    private String readCell(Cell cell, DataFormatter dataFormatter, FormulaEvaluator formulaEvaluator) {
        formulaEvaluator.evaluate(cell);
        return dataFormatter.formatCellValue(cell, formulaEvaluator);
    }


    public String getId() {
        return id;
    }

    public List<String> getElements() {
        return elements;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public Map<Integer, String> getChangedElements() {
        return changedElements;
    }

    public void setChangedElements(Map<Integer, String> changedElements) {
        this.changedElements = changedElements;
    }

    ERow compare(ERow row) {
        for (int i = 0; i < this.getElements().size(); i++) {
            if (!this.getElements().get(i).equals(row.getElements().get(i))) {
                this.changedElements.putIfAbsent(i, row.getElements().get(i));
                System.out.println("The element " + this.getId() + " has changed from: " + row.getElements().get(i)
                        + " to: " + this.getElements().get(i));
            }
        }
        if (changedElements.size() > 0) {
            System.out.println("----------------------------------------------------------------------------------------------");
            changedElements.forEach((integer, s) -> System.out.println("The ID: " + this.getId() + " has changed from: " + s
                    + " to: " + elements.get(integer)));
            System.out.println("----------------------------------------------------------------------------------------------");
        }

        return this;
    }

    void writeToExcel(Row row) {
        Cell cell = writeToCell(this.id, row, 0);
        cell.setCellStyle(setCellStyle(cell));

        for (int i = 0; i < this.elements.size(); i++) {
            writeToCell(elements.get(i), row, i + 1);
        }
    }

    private Cell writeToCell(String value, Row row, int columnNumber) {
        Cell cell = row.createCell(columnNumber, CellType.STRING);
        cell.setCellValue(value);
        if (changedElements.get(columnNumber - 1) != null) {
            addComment(cell, "Chamlal", changedElements.get(columnNumber - 1));
        }
        return cell;
    }

    private CellStyle setCellStyle(Cell cell) {
        CellStyle style = cell.getRow().getSheet().getWorkbook().createCellStyle();
        short backgroundColor;

        switch (this.status) {
            case ADDED:
                backgroundColor = IndexedColors.GREEN.getIndex();
                break;
            case DELETED:
                backgroundColor = IndexedColors.RED.getIndex();
                break;
            case COMMON:
                backgroundColor = !changedElements.isEmpty() ?
                        IndexedColors.YELLOW.getIndex() : IndexedColors.WHITE.getIndex();
                break;
            default:
                backgroundColor = IndexedColors.WHITE.getIndex();
        }
        style.setFillForegroundColor(backgroundColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public void addComment(Cell cell, String author, String commentText) {
        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        //i found it useful to show the comment box at the bottom right corner
        anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
        anchor.setCol2(cell.getColumnIndex() + 3); //...and ends at that given column
        anchor.setRow1(cell.getRowIndex() + 1); //one row below the cell...
        anchor.setRow2(cell.getRowIndex() + 5); //...and 4 rows high
        Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor(author);
        cell.setCellComment(comment);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof String)) {
            return false;
        }
        return id.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id).append(": ");
        for (String element : elements) {
            stringBuilder.append(element).append("-\t");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
