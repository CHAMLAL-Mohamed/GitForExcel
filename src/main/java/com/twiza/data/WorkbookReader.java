package com.twiza.data;

import com.twiza.domain.ESheet;
import com.twiza.domain.EWorkbook;
import com.twiza.domain.ExcelWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WorkbookReader implements Reader<Workbook, EWorkbook> {

    private static WorkbookReader INSTANCE;
    private static DataFormatter dataFormatterInstance;
    private final List<String> ignoredSheets;


    private WorkbookReader() {
        this.ignoredSheets = new ArrayList<>();
    }

    public static WorkbookReader getInstance(DataFormatter dataFormatter) {

        if (INSTANCE == null) {
            INSTANCE = new WorkbookReader();
            dataFormatterInstance = dataFormatter;
        }
        return INSTANCE;
    }

    public WorkbookReader setIgnoreSheets(List<String> ignoredSheets) {
        this.ignoredSheets.clear();
        if (ignoredSheets != null) {
            this.ignoredSheets.addAll(ignoredSheets);
        }
        return INSTANCE;
    }

    @Override
    public EWorkbook read(Workbook workbook) {
        final List<ESheet> tempSheets = new ArrayList<>();
        SheetReader sheetReader = SheetReader.getInstance(dataFormatterInstance);
        List<Sheet> workbookSheets = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            workbookSheets.add(workbook.getSheetAt(i));
        }
        workbookSheets
                .stream()
                .map(Sheet::getSheetName)
                .filter(isNotIgnored)
                .map(workbook::getSheet)
                .forEach(sheet -> {
                    tempSheets.add(sheetReader.read(sheet));
                });
        SheetReader.releaseResources();
        return new ExcelWorkbook(" fd", tempSheets);
    }

    private Predicate<String> isNotIgnored = new Predicate<String>() {
        @Override
        public boolean test(String s) {

            //TODO: implement ignore logic
            return true;
        }
    };


}
