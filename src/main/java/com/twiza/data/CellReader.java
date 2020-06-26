package com.twiza.data;

import com.twiza.domain.ECell;
import com.twiza.domain.ExcelCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Objects;

public class CellReader implements Reader<Cell, ECell> {
    /**
     * a Map to store  {@link FormulaEvaluator} for each Workbook,
     * this is used to avoid creating a FormulaEvaluator for each Cell.
     */
    private static FormulaEvaluator formulaEvaluator;
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
        if (formulaEvaluator == null) {
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        }
        //evaluate the cell value.
        formulaEvaluator.evaluate(cell);
        String value = dataFormatterInstance.formatCellValue(cell, formulaEvaluator);
        return new ExcelCell(value);
    }
    /**
     * this function should be called before closing the {@link Workbook}
     * to release {@link FormulaEvaluator} in order to be able to close Workbook
     * and allow GC to collect it, failing to call this for each Workbook
     * can cause serious issues, with your cellsValues and memory optimization.
     */
    public static void releaseResources() {
        formulaEvaluator = null;
    }

}
