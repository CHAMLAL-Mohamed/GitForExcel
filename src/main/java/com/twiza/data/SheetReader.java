package com.twiza.data;

import com.twiza.domain.ERow;
import com.twiza.domain.ESheet;
import com.twiza.domain.ExcelSheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class SheetReader implements Reader<Sheet, ESheet> {
    private static SheetReader INSTANCE;
    /**
     * an Instance of dataFormatter to be used in formatting all Cells.
     */
    private static DataFormatter dataFormatterInstance;

    private SheetReader() {
    }

    public static SheetReader getInstance(DataFormatter dataFormatter) {

        if (INSTANCE == null) {
            INSTANCE = new SheetReader();
            dataFormatterInstance = dataFormatter;
        }
        return INSTANCE;
    }

    @Override
    public ESheet read(Sheet sheet) {
        ESheet eSheet = new ExcelSheet(sheet.getSheetName());
        RowReader reader = RowReader.getInstance(dataFormatterInstance);
        Iterator<Row> rows = sheet.rowIterator();
        ERow headers = reader.read(rows.next());
        eSheet.assignHeaders(headers);
        rows.forEachRemaining(row -> eSheet.addRow(reader.read(row)));
        return eSheet;
    }

    public static void releaseResources() {
        RowReader.releaseResources();
    }
}
