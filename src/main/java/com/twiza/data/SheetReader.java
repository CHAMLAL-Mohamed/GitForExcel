package com.twiza.data;

import com.twiza.domain.ESheet;
import com.twiza.domain.ExcelSheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

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
        Iterable<Row> rowsIterable = sheet::rowIterator;
        int maxCellsNumber = StreamSupport.stream(rowsIterable.spliterator(), false)
                                          .mapToInt(Row::getLastCellNum)
                                          .max().orElseThrow(NoSuchElementException::new);
        Iterator<Row> rows = sheet.rowIterator();
        //System.out.println(maxCellsNumber);
        //System.out.println(sheet.getLastRowNum());
        rows.forEachRemaining(row -> {
            //System.out.println("row number " + row.getRowNum() + " is added");
            eSheet.addRow(reader.read(row, maxCellsNumber));
        });
        return eSheet;
    }

    public static void releaseResources() {
        RowReader.releaseResources();
    }
}
