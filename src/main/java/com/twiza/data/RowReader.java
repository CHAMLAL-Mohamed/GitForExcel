package com.twiza.data;

import com.twiza.domain.ERow;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

public class RowReader implements Reader<Row, ERow> {
    private static RowReader INSTANCE;

    private RowReader() {
    }

    public static RowReader getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new RowReader();
        }
        return INSTANCE;
    }

    @Override
    public ERow read(Row row) {
        Objects.requireNonNull(row);


        return null;
    }
}
