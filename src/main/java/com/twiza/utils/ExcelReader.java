/*
 * Copyright  2020  Chamlal.Mohamed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twiza.utils;

import com.twiza.domain.*;
import com.twiza.exceptions.WorkbookWithInvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ExcelReader {
    /**
     * a static instance of this class to ensure Singleton.
     */
    private static ExcelReader INSTANCE;
    /**
     * Used to format cells into a String value.
     */
    private final DataFormatter dataFormatterInstance;
    /**
     * used to evaluate cells values.
     */
    private FormulaEvaluator formulaEvaluator;
    /**
     * List of the sheet's names to be ignored.
     */
    private final List<String> sheetsToIgnorePatterns;


    private ExcelReader() {
        dataFormatterInstance = new DataFormatter();
        this.sheetsToIgnorePatterns = new ArrayList<>();
        this.sheetsToIgnorePatterns.add("^Sheet[\\w]*");//only sheets that don't start with Sheet[digit] will be read.
    }

    public static ExcelReader getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ExcelReader();
        return INSTANCE;
    }

    /**
     * Reads excel workbook and convert it into {@link EWorkbook} instance
     *
     * @param workbookPath the path of the workbook to be read
     * @return an instance of {@link EWorkbook} that contains the workbook's data
     * @throws IOException                        if the path provided does't exist or is not an excel file.
     * @throws WorkbookWithInvalidFormatException if the input file is corrupted(files with invalid format)
     */
    public EWorkbook readWorkbook(String workbookPath) throws IOException {
        try (Workbook workbook = createWorkbook(workbookPath)) {
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<ESheet> tempSheets = StreamSupport.stream(workbook.spliterator(), false)
                                                   .map(Sheet::getSheetName)
                                                   .filter(sheetIsIgnored.negate())
                                                   .map(workbook::getSheet)
                                                   .map(this::readSheet)
                                                   .map(sheet -> sheet.adoptFirstRowAsHeaders(false))
                                                   .collect(Collectors.toCollection(ArrayList::new));
            return new ExcelWorkbook(workbookPath, tempSheets);
        } catch (InvalidFormatException e) {
            throw new WorkbookWithInvalidFormatException("The workbook \"" + workbookPath + "\" is corrupted");
        }
    }

    public EWorkbook readWorkbook(String workbookPath, boolean firstRowIsHeaders) throws IOException {
        try (Workbook workbook = createWorkbook(workbookPath)) {
            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<ESheet> tempSheets = StreamSupport.stream(workbook.spliterator(), false)
                                                   .map(Sheet::getSheetName)
                                                   .filter(sheetIsIgnored.negate())
                                                   .map(workbook::getSheet)
                                                   .map(this::readSheet)
                                                   .map(sheet -> sheet.adoptFirstRowAsHeaders(firstRowIsHeaders))
                                                   .collect(Collectors.toCollection(ArrayList::new));
            return new ExcelWorkbook(workbookPath, tempSheets);
        } catch (InvalidFormatException e) {
            throw new WorkbookWithInvalidFormatException("The workbook \"" + workbookPath + "\" is corrupted");
        }
    }

    private Workbook createWorkbook(String filePath) throws IOException, InvalidFormatException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        return XSSFWorkbookFactory.createWorkbook(file, true);
    }

    private final Predicate<String> sheetIsIgnored = new Predicate<String>() {

        @Override
        public boolean test(String sheetName) {
            return sheetsToIgnorePatterns.stream()
                                         .map(Pattern::compile)
                                         .map(pattern -> pattern.matcher(sheetName))
                                         .anyMatch(Matcher::find);
        }
    };

    private ESheet readSheet(Sheet sheet) {
        ESheet eSheet = new ExcelSheet(sheet.getSheetName());
        int maxCellsNumber = StreamSupport.stream(sheet.spliterator(), false)
                                          .mapToInt(Row::getLastCellNum)
                                          .max().orElseThrow(NoSuchElementException::new);
        sheet.rowIterator().forEachRemaining(row -> eSheet.addRow(readRow(row, maxCellsNumber)));
        return eSheet;
    }

    private ERow readRow(Row row, int maxCellsNumber) {
        Objects.requireNonNull(row);
        ERow eRow = new ExcelRow();
        int firstCellPosition = 0;
        for (int i = firstCellPosition; i < maxCellsNumber; i++) {
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
