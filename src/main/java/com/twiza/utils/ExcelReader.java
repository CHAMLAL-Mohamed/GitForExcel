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
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class is a factory responsible for building eWorkbook
 * you can provide sheetsPatterns to be ignored and decide if empty rows should be kept
 */
public final class ExcelReader {
    private static final boolean PARALLEL_PROCESSING = false;
    private static final String GLOB_SYNTAX = "glob:";
    private static final String EXTENSION_REGEX_PATTERN = "([.][^.]+$)";
    private static final boolean WORKBOOK_READ_ONLY_MODE = true;
    private static final Logger logger = Logger.getLogger(String.valueOf(ExcelReader.class));

    private static final boolean FIRST_ROW_IS_NOT_HEADER = false;
    /**
     * a static instance of this class to ensure Singleton.
     */
    private static ExcelReader INSTANCE;
    /**
     * Used to format cells into a String value.
     */
    private final DataFormatter dataFormatterInstance;
//    /**
//     * used to evaluate cells values.
//     */
//    private FormulaEvaluator formulaEvaluator;

    private boolean keepEmptyRows = false;

    private ExcelReader() {
        dataFormatterInstance = new DataFormatter();

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
     * @throws IOException                        if the path provided doesn't exist or is not an excel file.
     * @throws WorkbookWithInvalidFormatException if the input file is corrupted(files with invalid format)
     */
    public EWorkbook read(Path workbookPath) throws IOException {
        //TODO read the workbook provided, and use default values.
        return read(workbookPath, null);
    }

    /**
     * @param workbookPath
     * @param ignoredSheetsPatterns
     * @return
     * @throws IOException
     */
    public EWorkbook read(Path workbookPath, List<String> ignoredSheetsPatterns) throws IOException {
        return read(workbookPath, ignoredSheetsPatterns, false);
    }

    /**
     * @param workbookPath
     * @param ignoredSheetsPatterns
     * @param keepEmptyRows
     * @return
     * @throws IOException
     */
    public EWorkbook read(Path workbookPath, List<String> ignoredSheetsPatterns, boolean keepEmptyRows) throws IOException {
        this.keepEmptyRows = keepEmptyRows;
        EWorkbook eWorkbook = null;
        try (Workbook workbook = createWorkbook(workbookPath)) {
//            formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<ESheet> eSheets = retrieveSheets(workbook.spliterator(), ignoredSheetsPatterns, workbookPath);
            eWorkbook = new ExcelWorkbook(workbookPath, eSheets);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | EncryptedDocumentException e) {
            //add log here
            e.printStackTrace();
            throw e;
        }
        return eWorkbook;
    }

    /**
     * create a workbook in readOnly mode from the specified path
     *
     * @param workbookPath the path of the workbook to create
     * @return an instance of {@link Workbook}
     * @throws IOException if the file wasn't found or was enable to be opened.
     */
    private Workbook createWorkbook(Path workbookPath) throws IOException {
        return WorkbookFactory.create(workbookPath.toFile(), null, WORKBOOK_READ_ONLY_MODE);
    }


    //retrieves all the sheets in workbook, after ignoring unnecessary ones
    private List<ESheet> retrieveSheets(Spliterator<Sheet> sheetSpliterator, List<String> ignoreSheetsPatterns, Path workbookPath) {
        return StreamSupport.stream(sheetSpliterator, PARALLEL_PROCESSING)
                            .peek(sheet -> System.out.println("processing sheet " + sheet.getSheetName()))
                            .filter(sheetShouldBeRead(ignoreSheetsPatterns, workbookPath))
                            .peek(sheet -> System.out.println("not ignoredSheet " + sheet.getSheetName()))
                            .map(this::readSheet)
                            .map(sheet -> sheet.adoptFirstRowAsHeaders(FIRST_ROW_IS_NOT_HEADER))
                            .collect(Collectors.toCollection(ArrayList::new));
    }

    private Predicate<Sheet> sheetShouldBeRead(List<String> ignoreSheetsPatterns, Path workbookPath) {
        return sheetShouldBeIgnored(ignoreSheetsPatterns, workbookPath).negate();
    }

    //idea: is it possible to use git check-ignore to verify if a sheet/workbook/folder should be ignore ??
    private Predicate<Sheet> sheetShouldBeIgnored(List<String> ignoreSheetsPatterns, Path workbookPath) {
        FileSystem defaultFileSystem = FileSystems.getDefault();
        return sheet -> Optional.ofNullable(ignoreSheetsPatterns).orElseGet(ArrayList::new)
                                .stream()
                                .map(patternString -> GLOB_SYNTAX + patternString)// use glob syntax for matching
                                .map(defaultFileSystem::getPathMatcher)
                                .anyMatch(pathMatcher -> pathMatcher.matches(buildSheetPath(workbookPath, sheet.getSheetName())));// patterns will be in the form of workbookPath/sheetName( a global sheet should start with |)
        //using anyMatch and apply negate() is more efficient than using noneMatch, because then it will traverse through the entire stream before returning a result.

    }

    private Path buildSheetPath(Path workbookPath, String sheetName) {
        String workbookName = workbookPath.getFileName().toString().replaceAll(EXTENSION_REGEX_PATTERN, "");
        return Paths.get(workbookName, sheetName);
    }


    /**
     * Reads all sheet's non empty rows and return an instance of {@code ESheet}
     *
     * @param sheet the sheet to be read
     * @return an {@code ESheet} that contains all the rows of the sheet( non empty rows only)
     */
    private ESheet readSheet(Sheet sheet) {
        ESheet eSheet = new ExcelSheet(sheet.getSheetName());
        // get the max number of cells in all rows -> column number
        int maxCellsNumber = StreamSupport.stream(sheet.spliterator(), false)
                                          .mapToInt(Row::getLastCellNum)
                                          .max().orElse(0);
        StreamSupport.stream(sheet.spliterator(), false)
                     .filter(this::isNotEmptyRow)
                     .forEach(row -> eSheet.addRow(readRow(row, maxCellsNumber)));
        return eSheet;
    }

    /**
     * checks if a row contains only null or empty cells, used ti ignore empty rows during reading
     *
     * @param row the row to check
     * @return {@code true} if all cells are null or empty, {@code false} otherwise
     */
    private boolean isNotEmptyRow(Row row) {
        return keepEmptyRows || StreamSupport.stream(row.spliterator(), false)
                                             .anyMatch(cell -> cell != null && cell.getCellType() != CellType.BLANK);
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
        //formulaEvaluator.evaluate(cell);
        //String value = dataFormatterInstance.formatCellValue(cell, formulaEvaluator);
        //String value = dataFormatterInstance.formatCellValue(cell);
        String value;
        if (cell.getCellType() == CellType.FORMULA) {
            switch (cell.getCachedFormulaResultType()) {
                case BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                case STRING:
                    value = String.valueOf(cell.getRichStringCellValue());
                    break;
                case _NONE:
                    value = String.valueOf(cell.getRichStringCellValue());
                    break;
                default:
                    value = dataFormatterInstance.formatCellValue(cell);
            }
        } else {
            value = dataFormatterInstance.formatCellValue(cell);
        }

        return new ExcelCell(value);
    }

}
