package com.twiza.utils;

import com.twiza.Templates;
import com.twiza.data.SheetReader;
import com.twiza.domain.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

public final class ExcelReader {
    /**
     * a Map to store  {@link FormulaEvaluator} for each Workbook,
     * this is used to avoid creating a FormulaEvaluator for each Cell.
     */
    private static ExcelReader INSTANCE;
    private final DataFormatter dataFormatterInstance;
    private final List<String> ignoredSheets;
    private final List<Templates> templates;
    private FormulaEvaluator formulaEvaluator;

    private ExcelReader() {
        dataFormatterInstance = new DataFormatter();
        this.ignoredSheets = new ArrayList<>();
        this.ignoredSheets.add("^Sheet[\\w]*");//only sheets that don't start with Sheet[digit] will be read.
        this.templates = new ArrayList<>();
    }

    public static ExcelReader getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ExcelReader();
        return INSTANCE;
    }

    public EWorkbook readWorkbook(String workbookPath) throws IOException, InvalidFormatException {
        try (Workbook workbook = createWorkbook(workbookPath)) {
            //Insert to the map in case the workbook is new.
            if (formulaEvaluator == null) {
                formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            }

            final List<ESheet> tempSheets = new ArrayList<>();
            List<Sheet> workbookSheets = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                workbookSheets.add(workbook.getSheetAt(i));
            }
            workbookSheets
                    .stream()
                    .map(Sheet::getSheetName)
                    .filter(isNotIgnored)
                    .map(workbook::getSheet)
                    .forEach(sheet -> tempSheets.add(readSheet(sheet)));
            SheetReader.releaseResources();
            return new ExcelWorkbook(" fd", tempSheets);
        }

    }

    /**
     *
     * @param filePath
     * @return
     * @throws IOException
     * @throws InvalidFormatException if the file is corrupted
     */
    private Workbook createWorkbook(String filePath) throws IOException, InvalidFormatException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        return XSSFWorkbookFactory.createWorkbook(file, true);
    }

    private final Predicate<String> isNotIgnored = new Predicate<>() {
        @Override
        public boolean test(String sheetName) {
            return ignoredSheets
                           .stream()
                           .map(Pattern::compile)
                           .map(pattern -> pattern.matcher(sheetName))
                           .noneMatch(Matcher::find);

        }
    };

    private ESheet readSheet(Sheet sheet) {
        ESheet eSheet = new ExcelSheet(sheet.getSheetName());
        Iterable<Row> rowsIterable = sheet::rowIterator;
        int maxCellsNumber = StreamSupport.stream(rowsIterable.spliterator(), false)
                                          .mapToInt(Row::getLastCellNum)
                                          .max().orElseThrow(NoSuchElementException::new);
        Iterator<Row> rows = sheet.rowIterator();
        //System.out.println(maxCellsNumber);
        //System.out.println(sheet.getLastRowNum());
        rows.forEachRemaining(row -> {
            //System.out.println("row number " + row.getRowNum() + " is added");
            eSheet.addRow(readRow(row, maxCellsNumber));
        });
        return eSheet;
    }

    private ERow readRow(Row row, int maxCellsNumber) {
        Objects.requireNonNull(row);
        ERow eRow = new ExcelRow();
        int firstCellPosition = 0;
        for (int i = firstCellPosition; i < maxCellsNumber; i++) {
            // System.out.println("Cell value is " + reader.read(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
            eRow.addCell(readCell(row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)));
        }
        return eRow;
    }

    private ECell readCell(Cell cell) {
        Objects.requireNonNull(cell);
        //evaluate the cell value.
        formulaEvaluator.evaluate(cell);
        String value = dataFormatterInstance.formatCellValue(cell, formulaEvaluator);
        return new ExcelCell(value);
    }

}
