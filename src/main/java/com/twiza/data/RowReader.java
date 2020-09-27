package com.twiza.data;

import com.twiza.domain.ERow;
import com.twiza.domain.ExcelRow;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

public class RowReader {
    private static RowReader INSTANCE;
    private Integer firstCellPosition;
    private Integer lastCellPosition;
    /**
     * an Instance of dataFormatter to be used in formatting all Cells.
     */
    private static DataFormatter dataFormatterInstance;

    private RowReader() {
    }

    public static RowReader getInstance(DataFormatter dataFormatter) {

        if (INSTANCE == null) {
            INSTANCE = new RowReader();
            dataFormatterInstance = dataFormatter;
        }
        return INSTANCE;
    }

    public ERow read(Row row) {
        Objects.requireNonNull(row);
        ERow eRow = new ExcelRow();
        CellReader reader = CellReader.getInstance(dataFormatterInstance);
        //firstCellPosition = firstCellPosition == null ? Integer.valueOf(row.getFirstCellNum()) : firstCellPosition;
        firstCellPosition = 0;
        // lastCellPosition = lastCellPosition == null ? Integer.valueOf(row.getLastCellNum()) : lastCellPosition;
        lastCellPosition = (int) row.getLastCellNum();
        System.out.println("last position is " + lastCellPosition);
        for (int i = firstCellPosition; i < lastCellPosition; i++) {
            System.out.println("Cell value is " + reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
            eRow.addCell(reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
        }
        return eRow;
    }

    public ERow read(Row row, int maxCellsNumber) {
        Objects.requireNonNull(row);
        ERow eRow = new ExcelRow();
        CellReader reader = CellReader.getInstance(dataFormatterInstance);
        int firstCellPosition = 0;
        for (int i = firstCellPosition; i < maxCellsNumber; i++) {
            // System.out.println("Cell value is " + reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
            eRow.addCell(reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
        }
        return eRow;
    }

    public static void releaseResources() {
        CellReader.releaseResources();
    }
}
