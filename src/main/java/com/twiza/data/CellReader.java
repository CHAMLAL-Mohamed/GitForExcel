package com.twiza.data;

import com.twiza.domain.ECell;
import com.twiza.domain.ExcelCell;
import org.apache.poi.ss.usermodel.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CellReader implements Reader<Cell, ECell> {
    /**
     * a Map to store  {@link FormulaEvaluator} for each Workbook,
     * this is used to avoid creating a FormulaEvaluator for each Cell.
     */
    private static Map<Workbook, FormulaEvaluator> formulaEvaluators;
    /**
     * an Instance of dataFormatter to be used in formatting all Cells.
     */
    private static DataFormatter dataFormatterInstance;
    /**
     * Instance of this class to ensure singleton pattern.
     */
    private static CellReader INSTANCE;


    /**
     * Create instance of  {@link CellReader}
     *
     * @param dataFormatter responsible for CellDataFormatting.
     * @return an Instance of this class.
     */
    public static CellReader getInstance(DataFormatter dataFormatter) {
        if (INSTANCE == null) {
            INSTANCE = new CellReader();
            dataFormatterInstance = dataFormatter;
            formulaEvaluators = new HashMap<>();
        }
        return INSTANCE;
    }


    /**
     * read value from {@link Cell} and return an instance of {@link ECell}
     *
     * @param cell the input cell to read data from
     * @return an Instance of {@link ECell}.
     */
    @Override
    public ECell read(Cell cell) {
        Objects.requireNonNull(cell);
        Workbook workbook = cell.getSheet().getWorkbook();
        //Insert to the map in case the workbook is new.
        formulaEvaluators.computeIfAbsent(workbook, key -> key.getCreationHelper().createFormulaEvaluator());
        FormulaEvaluator evaluator = formulaEvaluators.get(workbook);
        //evaluate the cell value.
        evaluator.evaluate(cell);
        String value = dataFormatterInstance.formatCellValue(cell, evaluator);
        return new ExcelCell(value);
    }

}
