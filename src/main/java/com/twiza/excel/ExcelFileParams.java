package com.twiza.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileParams {

    Workbook workbook;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;

    ExcelFileParams(Workbook workbook) {
        this.workbook = workbook;
        dataFormatter = new DataFormatter();
        formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
    }


    public Workbook getWorkbook() {
        return workbook;
    }

    public DataFormatter getDataFormatter() {
        return dataFormatter;
    }

    public FormulaEvaluator getFormulaEvaluator() {
        return formulaEvaluator;
    }
}
