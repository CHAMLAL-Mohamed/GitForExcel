package com.twiza.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelFileParams {

    Workbook workbook;
    private DataFormatter dataFormatter;
    private FormulaEvaluator formulaEvaluator;

    ExcelFileParams(Workbook workbook) {
        this.workbook = workbook;
        dataFormatter = new DataFormatter();
        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

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
