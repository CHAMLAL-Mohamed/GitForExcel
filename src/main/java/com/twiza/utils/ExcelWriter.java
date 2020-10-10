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

package com.twiza.utils;

import com.twiza.domain.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriter {
    /**
     * a static instance of this class to ensure Singleton.
     */
    private static ExcelWriter INSTANCE;
    private CellStyle cellStyle;
    private CellStyle headerCellStyle;
    private CellStyle changedCellStyle;
    private CellStyle addedRowStyle;
    private CellStyle deletedRowStyle;
    private CellStyle changedRowStyle;
    private CellStyle defaultRowStyle;

    private IndexedColorMap colorMap;

    private ExcelWriter() {
    }

    public static ExcelWriter getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ExcelWriter();
        return INSTANCE;
    }

    public void writeToWorkbook(String workbookPath, EWorkbook eWorkbook) throws IOException {
        Objects.requireNonNull(eWorkbook, "cannot write a null workbook to Excel");
        XSSFWorkbook workbook = new XSSFWorkbook();
        colorMap = workbook.getStylesSource().getIndexedColors();
        setUpStyles(workbook);
        for (ESheet eSheet : eWorkbook.getSheets().values()) {
            XSSFSheet sheet = workbook.createSheet(eSheet.getName());
            setSheetTabColor(sheet, eSheet);
            AtomicInteger rowIndex = new AtomicInteger(0);
            writeHeadersToSheet(eSheet.getHeaders(), sheet, rowIndex.getAndIncrement());
            eSheet.getUniqueData().forEach((key, value) -> writeERowToSheet(value, sheet, rowIndex.getAndIncrement()));
        }
        FileOutputStream outputStream = new FileOutputStream(workbookPath);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    private void setUpStyles(Workbook workbook) {
        headerCellStyle = buildCellStyle(workbook, true, IndexedColors.GREY_40_PERCENT.getIndex());
        changedCellStyle = buildCellStyle(workbook, true, IndexedColors.LIGHT_ORANGE.getIndex());
        addedRowStyle = buildCellStyle(workbook, true, IndexedColors.LIGHT_GREEN.getIndex());
        deletedRowStyle = buildCellStyle(workbook, true, IndexedColors.RED.getIndex());
        changedRowStyle = buildCellStyle(workbook, true, IndexedColors.LIGHT_YELLOW.getIndex());
        defaultRowStyle = buildCellStyle(workbook, true, IndexedColors.WHITE.getIndex());


    }

    private CellStyle buildCellStyle(Workbook workbook, boolean isHasBorders, short colorIndex) {
        CellStyle cellStyle = workbook.createCellStyle();
        if (isHasBorders) {
            addCellBorders(cellStyle);
        }
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(colorIndex);

        return cellStyle;
    }

    private void addCellBorders(CellStyle cellStyle) {
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
    }


    private void setSheetTabColor(XSSFSheet sheet, ESheet eSheet) {
        switch (eSheet.getStatus()) {
            case ADDED:
                XSSFColor color = new XSSFColor(Color.GREEN, colorMap);
                sheet.setTabColor(color);
                break;
            case DELETED:
                color = new XSSFColor(Color.RED, colorMap);
                sheet.setTabColor(color);
                break;
            case CHANGED:
                color = new XSSFColor(Color.YELLOW, colorMap);
                sheet.setTabColor(color);
                break;
            default:
        }
    }

    private void writeHeadersToSheet(List<String> list, Sheet sheet, int rowNumber) {
        Row row = sheet.createRow(rowNumber);
        int columnIndex = 0;
        for (String value : list) {
            Cell cell = writeValueToCell(value, row, columnIndex++);
            cell.setCellStyle(headerCellStyle);
        }
    }

    private void writeERowToSheet(ERow eRow, Sheet sheet, int rowNumber) {
        Row row = sheet.createRow(rowNumber);
        int columnIndex = 0;
        for (ECell eCell : eRow.getCells()) {
            Cell cell = writeValueToCell(eCell.getValue(), row, columnIndex++);
            if (eRow.getStatus().equals(Status.CHANGED) && eCell.getStatus().equals(Status.NEW)) {
                cell.setCellStyle(changedRowStyle);
            }else {
                updateCellColor(cell, eCell);
            }

            assignCommentToCell(cell, eCell);
        }
    }

    private Cell writeValueToCell(String value, Row row, int columnNumber) {
        Cell cell = row.getCell(columnNumber, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
        return cell;
    }

    private void updateCellColor(Cell cell, ECell eCell) {
        switch (eCell.getStatus()) {
            case ADDED:
                cell.setCellStyle(addedRowStyle);
                break;
            case DELETED:
                cell.setCellStyle(deletedRowStyle);
                break;
            case CHANGED:
                cell.setCellStyle(changedCellStyle);
                break;
            default:

        }
    }

    private void assignCommentToCell(Cell cell, ECell eCell) {
        if (eCell.getStatus().equals(Status.CHANGED)) {
            cell.setCellComment(buildComment(cell, eCell.getChangesHistory()));
        }
    }

    public Comment buildComment(Cell cell, String commentText) {
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
        //comment.setAuthor(author);
        cell.setCellComment(comment);
        return comment;
    }
}
