package com.twiza.data;

import com.twiza.domain.ERow;
import com.twiza.domain.ExcelRow;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

public class RowReader implements Reader<Row, ERow> {
    private static RowReader INSTANCE;
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

    @Override
    public ERow read(Row row) {
        Objects.requireNonNull(row);
        ERow eRow = new ExcelRow();
        CellReader reader = CellReader.getInstance(dataFormatterInstance);
        short firstCellPosition = row.getFirstCellNum();
        short lastCellPosition = row.getLastCellNum();
        for (int i = firstCellPosition; i < lastCellPosition; i++) {
            eRow.addCell(reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
        }
        return eRow;
    }

    public static void releaseResources() {
        CellReader.releaseResources();
    }
}
