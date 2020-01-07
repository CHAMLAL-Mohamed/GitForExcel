package com.twiza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

final class ESheet {
    final String name;
    Map<String, List<String>> data;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;

    ESheet(String name) {
        this.name = name;
        this.data = new LinkedHashMap();
    }

    public Map<String, List<String>> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    public void createSheet(Sheet sheet) throws IllegalArgumentException {
        this.dataFormatter = new DataFormatter();
        this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook)sheet.getWorkbook());
        Iterator rows = sheet.rowIterator();

        while(rows.hasNext()) {
            List<String> list = this.readRow((Row)rows.next());
            this.data.computeIfAbsent((String)list.get(0), (k) -> {
                return list.subList(1, list.size());
            });
        }

    }

    private List<String> readRow(Row row) {
        if (row == null) {
            return null;
        } else {
            List<String> rowList = new ArrayList();
            Iterator cells = row.cellIterator();

            while(cells.hasNext()) {
                rowList.add(this.readCell((Cell)cells.next()));
            }

            return rowList;
        }
    }

    private String readCell(Cell cell) {
        this.formulaEvaluator.evaluate(cell);
        return this.dataFormatter.formatCellValue(cell, this.formulaEvaluator);
    }
}
