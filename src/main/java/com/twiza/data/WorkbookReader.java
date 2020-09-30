package com.twiza.data;

import com.twiza.Templates;
import com.twiza.domain.ESheet;
import com.twiza.domain.EWorkbook;
import com.twiza.domain.ExcelWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkbookReader {

    private static WorkbookReader INSTANCE;
    private static DataFormatter dataFormatterInstance;
    private final List<String> ignoredSheets;
    private final List<Templates> templates;


    private WorkbookReader(DataFormatter dataFormatter) {
        this.ignoredSheets = new ArrayList<>();
        this.ignoredSheets.add("^Sheet[\\w]*");//only sheets that don't start with Sheet[digit] will be read.
        dataFormatterInstance = dataFormatter;
        this.templates = new ArrayList<>();
    }

    public static WorkbookReader getInstance(DataFormatter dataFormatter) {

        if (INSTANCE == null) {
            INSTANCE = new WorkbookReader(dataFormatter);

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

    public WorkbookReader setSheetsTemplates(List<Templates> templates) {
        this.templates.clear();
        if (templates != null) {
            this.templates.addAll(templates);
        }

        return INSTANCE;
    }


    public EWorkbook read(String filePath) throws IOException, InvalidFormatException {
        try (Workbook workbook = createWorkbook(filePath)) {
            DataFormatter dataFormatter = new DataFormatter();
            final List<ESheet> tempSheets = new ArrayList<>();
            SheetReader sheetReader = SheetReader.getInstance(dataFormatter);
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

    }


    private Workbook createWorkbook(String filePath) throws IOException, InvalidFormatException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        return XSSFWorkbookFactory.createWorkbook(file, true);
    }

    private Predicate<String> isNotIgnored = new Predicate<String>() {
        @Override
        public boolean test(String sheetName) {
            return ignoredSheets
                           .stream()
                           .map(Pattern::compile)
                           .map(pattern -> pattern.matcher(sheetName))
                           .noneMatch(Matcher::find);

        }
    };


}
