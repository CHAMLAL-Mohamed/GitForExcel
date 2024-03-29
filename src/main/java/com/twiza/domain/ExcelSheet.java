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

package com.twiza.domain;

import com.twiza.exceptions.HeaderNotMatchingException;
import com.twiza.exceptions.SheetWithInconsistentDataException;
import com.twiza.exceptions.UnsupportedStatusChangeException;

import java.util.*;
import java.util.stream.Collectors;

public class ExcelSheet implements ESheet {
    private final static String DELETING_KEY_COLUMN_EXCEPTION_MESSAGE = "You cannot delete a column, that compose the key please change keyColumns first. position is: ";
    private final static String DIFFERENT_ROWS_WITH_SAME_KEY_EXCEPTION_MESSAGE = "This key is already inserted with a different Row";
    private final static String HEADERS_INCORRECT_SIZE_EXCEPTION_MESSAGE = "Headers size is different than inserted rows size";
    /**
     * Default status of the instance,
     * the ExcelSheet is considered new during construction.
     */
    private final static Status DEFAULT_STATUS = Status.NEW;

    /**
     * used as default value for key creation,
     * it considers that the first column is the key's column.
     */
    private static final int[] DEFAULT_KEY_INDEXES = {0};

    /**
     * The name of the sheet.
     */
    private String name;
    private int columnsNumber;
    private int rowsNumber;
    private int[] keyIndexes;
    /**
     * Status of the Sheet.
     */
    private Status status;

    /**
     * Headers of sheet, it can be null in case sheet has no headers.
     */
    private List<String> headers;

    private final List<ERow> rows;

    private Map<String, ERow> uniqueRows;


    public ExcelSheet(String name) {
        this(name, new ArrayList<>(), null, DEFAULT_KEY_INDEXES);
    }

    public ExcelSheet(String name, List<String> headers) {
        this(name, new ArrayList<>(), headers, DEFAULT_KEY_INDEXES);
    }

    public ExcelSheet(String name, List<ERow> rows, List<String> headers) {
        this(name, rows, headers, DEFAULT_KEY_INDEXES);
    }

    /**
     * @param name       the name of the sheet.
     * @param headers    headers of the sheet if exist, otherwise null.
     * @param rows       the rows that constitute the sheet, headers are not included
     * @param keyIndexes the positions of columns that are used to construct the key for each row
     * @throws UnsupportedOperationException if the provided headers have different size than rows
     * @throws NullPointerException          if the name provided is null
     */
    public ExcelSheet(String name, List<ERow> rows, List<String> headers, int... keyIndexes) throws UnsupportedOperationException {
        this.name = Objects.requireNonNull(name, "Name of the sheet cannot be null").toLowerCase();
        Objects.requireNonNull(rows);
        this.rows = new ArrayList<>();
        setHeaders(headers);
        rows.forEach(this::addRow);//avoid passing external reference of lists.
        setKeyIndexes(keyIndexes);
        this.status = DEFAULT_STATUS;
    }

    public ExcelSheet(ESheet sheet) {
        this.name = sheet.getName();
        this.headers = new ArrayList<>(sheet.getHeaders());
        this.rows = new ArrayList<>(sheet.getData().size());
        sheet.getData().forEach(row -> rows.add(new ExcelRow(row)));//avoid passing external reference of lists.
        setKeyIndexes(sheet.getKeysIndexes());
        this.status = sheet.getStatus();
    }


    /**
     * Appends a row at the end of this ESheet.
     *
     * @param row the row to be added to the end of this sheet
     * @return this {@link ESheet} with the implemented modifications
     * @throws NullPointerException          if the <code>row</code> is null
     * @throws UnsupportedOperationException if the row's size is different than {@code columnsNumber}
     */
    @Override
    public ESheet addRow(ERow row) {
        Objects.requireNonNull(row);
        checkRowSize(row.getSize());
        rows.add(row);
        columnsNumber = row.getSize();
        rowsNumber = rows.size();
        return this;
    }


    /**
     * Adds a new row to the provided <code>position</code>.
     *
     * @param position the position where the row will be added to
     * @param row      the row to be added
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException     if the <code>position</code> provided is out of range
     * @throws NullPointerException          if the <code>row</code> is null
     * @throws UnsupportedOperationException if the row's size is different than {@code columnsNumber}
     */
    @Override
    public ESheet addRow(int position, ERow row) {
        Objects.requireNonNull(row);
        checkRowSize(row.getSize());
        rows.add(position, row);
        columnsNumber = row.getSize();
        rowsNumber = rows.size();
        return this;
    }

    private void checkRowSize(int size) {
        if (columnsNumber > 0 && size != columnsNumber) {
            throw new UnsupportedOperationException("The size of this row is different than the other rows size");
        }
        if (columnsNumber == 0) {
            columnsNumber = size;
        }
    }

    /**
     * Adds a new row to the provided <code>position</code>, with the possibility
     * to append blanks to the end of the row if its size is less than
     * the columns number of this sheet.
     *
     * @param position     the position where the row will be added to
     * @param row          the row to be added
     * @param appendBlanks wether to add blanks or not
     * @return this {@link ESheet} with the implemented modifications
     * @throws UnsupportedOperationException if the row's size is different than the size of the sheet columns
     */
    @Override
    public ESheet addRow(int position, ERow row, boolean appendBlanks) {
        int rowSize = row.getSize();
        if (!appendBlanks || rowSize == columnsNumber) {
            return addRow(position, row);
        }
        if (rowSize > columnsNumber) {
            throw new UnsupportedOperationException("The size of this row is bigger than the other rows size");
        }
        int blankCells = columnsNumber - rowSize;
        for (int i = 0; i < blankCells; i++) {
            row.addCell(new ExcelCell(""));
        }
        return addRow(position, row);
    }

    /**
     * Deletes the rows in the provided positions.
     *
     * @param positions the position where the row will be deleted from
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if one of the provided positions  is out of range
     */
    @Override
    public ESheet deleteRows(int... positions) {
        Arrays.sort(positions);
        for (int position = positions.length - 1; position >= 0; position--) {
            deleteRow(positions[position]);
        }
        return this;
    }

    /**
     * Deletes the row in the provided position.
     *
     * @param position the position where the row will be deleted from
     * @throws IndexOutOfBoundsException if the <code>position</code> provided is out of range
     */

    private void deleteRow(int position) {
        rows.remove(position);
        rowsNumber = rows.size();
    }


    /**
     * Deletes the provided row from this sheet
     * (optional operation).
     *
     * @param row the row to be deleted from this sheet
     * @return this {@link ESheet} with the implemented modifications.
     * @throws NullPointerException if the <code>row</code> is null
     */
    @Override
    public ESheet deleteRow(ERow row) {
        Objects.requireNonNull(row);
        rows.remove(row);
        rowsNumber = rows.size();

        return this;
    }

    @Override
    public ESheet deleteRowsRange(int beginIndex, int endIndex) {
        checkBoundsBeginEnd(beginIndex, endIndex, rowsNumber);
        for (int i = endIndex; i >= beginIndex; i--) {
            deleteRow(i);
        }
        return this;
    }

    private void checkBoundsBeginEnd(int begin, int end, int length) {
        if (begin < 0 || end >= length || begin > end) {
            throw new IndexOutOfBoundsException("The provided range is not supported, " +
                                                        "begin " + begin + ", end " +
                                                        end + ", length " + length);
        }
    }

    /**
     * Deletes all the empty rows(optional operation).
     * a row is considered to be empty, if all the cells in the row are blank
     * (Empty or contains only white spaces).
     *
     * @return this {@link ESheet} with the implemented modifications.
     * TODO: 21/09/2020 deleteEmptyRows functionality will be added later
     */

    @Override
    public ESheet deleteEmptyRows() {
        return this;
    }


    @Override
    public ESheet addColumn(EColumn column) {
        Objects.requireNonNull(column);
        checkColumnSize(column);
        headers.add(column.getHeaderName());
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).addCell(column.getCells().get(i));
        }
        columnsNumber = headers.size();
        return this;
    }

    /**
     * Adds a new column to the provided <code>position</code>.
     * In other words add  each <code>cell(i)</code> in column
     * to the provided position in the row <code>row(i).addCell(position, cell(i))</code>
     *
     * @param position the position where the column will be added to
     * @param column   the column to be added
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if the <code>position</code> provided is out of range
     * @throws NullPointerException      if the <code>column</code> is null
     */
    @Override
    public ESheet addColumn(int position, EColumn column) {
        Objects.requireNonNull(column);
        checkColumnSize(column);
        headers.add(position, column.getHeaderName());
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).addCell(position, column.getCells().get(i));
        }
        columnsNumber = headers.size();
        return this;
    }

    /**
     * Adds a new column to the provided <code>position</code>, with the possibility
     * to append blanks to the end of the column if its size is less than
     * the rows number of this sheet.
     *
     * @param position     the position where the column will be inserted
     * @param column       the column to be inserted
     * @param appendBlanks wether to add blanks or not
     * @return this {@link ESheet} with the implemented modifications
     * @throws UnsupportedOperationException if the column's size is different than the sheet's rows size
     */
    @Override
    public ESheet addColumn(int position, EColumn column, boolean appendBlanks) {
        int columnSize = column.getSize();
        if (!appendBlanks || columnSize == columnsNumber) {
            return addColumn(position, column);
        }
        if (columnSize > rowsNumber) {
            throw new UnsupportedOperationException("The size of this column is bigger than the other columns size");
        }
        int blankCells = rowsNumber - columnSize;
        for (int i = 0; i < blankCells; i++) {
            column.addCell(new ExcelCell(""));
        }
        return addColumn(position, column);
    }

    private void checkColumnSize(EColumn column) {
        if (rowsNumber > 0 && column.getSize() != rowsNumber) {
            throw new UnsupportedOperationException("The size of this row is different thant the other rows size");
        }
    }

    /**
     * Deletes the columns in the provided positions,
     * In other words, from each row in the sheet,
     * delete the cells at <code>positions</code>.
     *
     * @param positions an array of position from where to delete the columns
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if one of the provided positions  is out of range
     */
    @Override
    public ESheet deleteColumns(int... positions) {
        Arrays.sort(positions);
        Arrays.sort(positions);
        for (int position = positions.length - 1; position >= 0; position--) {
            // System.out.println(positions[position]);
            deleteColumn(positions[position]);
        }
        return this;
    }

    /**
     * Deletes a range of columns from this {@link ESheet}.
     *
     * @param beginIndex the index of the first column to delete, inclusive
     * @param endIndex   the index of the last column to delete, inclusive
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if the
     *                                   {@code beginIndex} is negative, or
     *                                   {@code endIndex} is larger than the length of
     *                                   this {@code ESheet} columns, or
     *                                   {@code beginIndex} is larger than
     *                                   {@code endIndex}.
     */
    @Override
    public ESheet deleteColumnRange(int beginIndex, int endIndex) {
        checkBoundsBeginEnd(beginIndex, endIndex, columnsNumber);
        for (int i = endIndex; i >= beginIndex; i--) {
            deleteColumn(i);
        }
        return this;
    }


    /**
     * Deletes the column in the provided position,
     * In other words, from each row in the sheet,
     * delete the cell at <code>position</code>.
     *
     * @param position the position where the column will be deleted from
     * @throws IndexOutOfBoundsException if the <code>position</code> provided is out of range
     */

    private void deleteColumn(int position) {
        if (headers != null) {
            String removed = headers.remove(position);
            // System.out.println("removing header: " + removed);
        }
        rows.forEach(row -> row.removeCell(position));
        columnsNumber = headers != null ? headers.size() : rows.get(0).getSize();
    }


    /**
     * Deletes all the empty columns(optional operation).
     * a column is considered to be empty, if all the cells in the column are blank,
     * (Empty or contains only white spaces).
     *
     * @return this {@link ESheet} with the implemented modifications.
     * TODO: 21/09/2020 deleteEmptyColumns functionality will be added later
     */
    @Override
    public ESheet deleteEmptyColumns() {
        return this;
    }

    @Override
    public ESheet deleteColumnsWithEmptyHeaders() {
        List<Integer> emptyHeadersPositions=new ArrayList<>();
        for(int i=0;i<headers.size();i++){
            if (headers.get(i).isBlank()){
                emptyHeadersPositions.add(i);
            }
        }
        int[] positions=emptyHeadersPositions.stream().mapToInt(Integer::intValue).toArray();
        deleteColumns(positions);
        return this;
    }

    /**
     * Implemented on: 03/10/2020
     * adjust the current sheet to match the provided template headers, and the mode
     * In other words, eliminate extra columns, and add missing columns to match the template provided.
     *
     * @param headersTemplate the headers to match against
     * @return this {@link ESheet} with the implemented modifications.
     */
    @Override
    public ESheet matchWithTemplate(List<String> headersTemplate, TemplateMode mode) {
        Objects.requireNonNull(headersTemplate);
        if (headers == null) {
            setHeaders(headersTemplate);
            return this;
        }
        switch (mode) {
            case MATCH:
                matchColumnsBasedOnTemplate(headersTemplate);
                break;
            case CONCAT:
                concatColumnsBasedOnTemplate(headersTemplate);
                //System.out.println(TemplateMode.CONCAT);
                break;
            case SPECIFIC:
                specificColumnsBasedOnTemplate(headersTemplate);
            default:
        }
        return this;
    }

    private void specificColumnsBasedOnTemplate(List<String> headersTemplate) {
        concatColumnsBasedOnTemplate(headersTemplate);
        deleteExtraColumnsBasedOnTemplate(headersTemplate);
    }

    private void matchColumnsBasedOnTemplate(List<String> headersTemplate) {
        //get matching headers(duplicate headers are removed)
        List<String> matchedHeaders = headersTemplate.stream()
                                                     .distinct()
                                                     .filter(headers::contains)
                                                     .collect(Collectors.toCollection(ArrayList::new));
        //get the size of matching headers
        int matchedHeadersSize = matchedHeaders.size();
        System.out.println("MatchedHeaders size is: " + matchedHeadersSize);
        //go through the matching headers, and arrange the headers of this sheet to have the same sequence as matching headers
        for (int templateCursor = 0; templateCursor < matchedHeadersSize; templateCursor++) {
            String headerTemplate = matchedHeaders.get(templateCursor);
            int headerPosition = headers.indexOf(headerTemplate);
            if (headerPosition != -1 && headerPosition != templateCursor) {
                EColumn column = getColumn(headerPosition);
                deleteColumn(headerPosition);
                addColumn(templateCursor, column);
            }
        }
        // delete remaining columns that are not part of the matching headers
        deleteExtraColumnsBasedOnTemplate(matchedHeaders);
    }

    private void concatColumnsBasedOnTemplate(List<String> headersTemplate) {
        int templateSize = headersTemplate.size();
        System.out.println("Template size is: " + templateSize);
        for (int templateCursor = 0; templateCursor < templateSize; templateCursor++) {
            String headerTemplate = headersTemplate.get(templateCursor);
            int headerPosition = headers.indexOf(headerTemplate);
            //System.out.println(" header position is: " + headerPosition);
            if (headerPosition < 0) {
                EColumn column = new EColumn(headerTemplate, new ArrayList<>());
                addColumn(templateCursor, column, true);
            } else if (headerPosition != templateCursor) {
                EColumn column = getColumn(headerPosition);
                deleteColumn(headerPosition);
                addColumn(templateCursor, column);
            }
        }
    }

    private void deleteExtraColumnsBasedOnTemplate(List<String> headersTemplate) {
        int templateSize = headersTemplate.size();
        if (templateSize == columnsNumber) {
            return;
        }
        deleteColumnRange(templateSize, columnsNumber - 1);

    }

    /**
     * Sets the  indexes of the key, in case of simple key it will be one index, otherwise an array of indexes,
     * the indexes will be used to compose the key in each row.
     *
     * @param keyIndexes the indexes that will be used to construct the key in each {@link ERow},
     * @return this {@link ESheet} with the implemented modifications.
     * @throws IndexOutOfBoundsException if any of the provided indexes is out of range
     *                                   in any row instance in this sheet
     */
    @Override
    public ESheet setKeyIndexes(int... keyIndexes) {
        Objects.requireNonNull(keyIndexes);
        for (int index : keyIndexes) {
            if (columnsNumber > 0 && index >= columnsNumber) {
                throw new IndexOutOfBoundsException("The provided index is bigger that sheet size(" + columnsNumber + ")");
            }
        }
        this.keyIndexes = keyIndexes;
        return this;
    }

    /**
     * Assigns a list of headers to the sheet,
     *
     * @param newHeaders the headers to assign to the sheet.
     * @return this {@link ESheet} with the implemented modifications.
     */
    @Override
    public ESheet setHeaders(List<String> newHeaders) {
        if (newHeaders == null) {
            headers = null;
            return this;
        }
        checkRowSize(newHeaders.size());
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.clear();
        headers.addAll(newHeaders);
        columnsNumber = headers.size();
        return this;
    }

    /**
     * takes the first row in the sheet and set it as headers, if isFirstRowHeaders is{@code true},
     * otherwise does nothing.
     *
     * @param isFirstRowHeaders if the first row should be considered as headers.
     * @return this {@link ESheet} with the implemented modifications.
     */
    @Override
    public ESheet adoptFirstRowAsHeaders(boolean isFirstRowHeaders) {
        if (isFirstRowHeaders && !rows.isEmpty()) {
            ERow firstRow = rows.get(0);
            setHeaders(firstRow.getCellsValues());
            deleteRow(firstRow);
        }
        return this;
    }

    /**
     * set a new Status for this element, if it's applicable
     *
     * @param newStatus the new Status to be applied to this row
     * @return this {@link ESheet} with the implemented modifications.
     * @throws UnsupportedStatusChangeException if the new status cannot be applied
     *                                          to the row
     */
    @Override
    public ESheet setStatus(Status newStatus) {

        if ((status.equals(Status.ADDED)
                     || status == Status.CHANGED
                     || status == Status.DELETED) && newStatus == Status.NEW) {
            throw new UnsupportedStatusChangeException(status.toString());
        }
        this.status = newStatus;
        return this;
    }

    /**
     * Returns the name of the sheet.
     *
     * @return the name of the Sheet.
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the current status of this sheet.
     *
     * @return the {@code Status} of this sheet, default value is {@code Status.NEW}
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the a <code>List</code> that contains the headers of this sheet.
     *
     * @return the list of headers of this sheet
     */
    @Override
    public List<String> getHeaders() {
        if (headers == null) {
            return null;
        }
        return Collections.unmodifiableList(headers);
    }

    /**
     * Returns the number of columns in this sheet.
     *
     * @return the number of Columns in this sheet
     */
    @Override
    public int getColumnsNumber() {
        return columnsNumber;
    }

    /**
     * Returns the number of rows in this sheet.
     *
     * @return the number of rows in this sheet
     */
    @Override
    public int getRowsNumber() {
        return rowsNumber;
    }

    /**
     * Returns array of integers that contains the positions columns that are used to construct the key on each row.
     *
     * @return the positions of columns used to construct the key on each row
     */
    @Override
    public int[] getKeysIndexes() {
        return keyIndexes;
    }

    /**
     * Get the {@link ERow} based on index of the row.
     *
     * @param position of the row in the sheet.
     * @return the ERow in the provided position.
     * @throws IndexOutOfBoundsException if any of the provided indexes is out of range
     */
    @Override
    public ERow getRow(int position) {
        return rows.get(position);
    }

    /**
     * retrieve the row associated to the provided key,
     * get the first row in this sheet where<code>ERow.getKey().equals(key)==true</code>,
     * null if not found.
     *
     * @param key the unique key used to get the associated row
     * @return a row with a key equals provided key, null if no row is found
     */
    @Override
    public ERow getRow(String key) {
        for (ERow row : rows) {
            if (row.getKey(keyIndexes).equals(key)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Implemented on: 03/10/2020 getColumn functionality will be implemented later
     * Get the {@link EColumn} based on index of the column.
     *
     * @param position of the column in the sheet.
     * @return the EColumn in the provided position.
     * @throws IndexOutOfBoundsException if any of the provided position is out of range
     *                                   in any row instance in this sheet
     */
    @Override
    public EColumn getColumn(int position) {
        EColumn column = new EColumn(headers.get(position), new ArrayList<>());
        rows.forEach(row -> column.addCell(row.getCell(position)));
        return column;
    }

    /**
     * Implemented: 03/10/2020
     * Returns a column based on header's name, <code>null</code> otherwise.
     *
     * @param headerName the name of the header
     * @return the associated column to the provided header,
     * return <code>null</code> if the header is not found.
     */
    @Override
    public EColumn getColumn(String headerName) {
        int columnIndex = headers == null ? -1 : headers.indexOf(headerName);
        if (columnIndex < 0) {
            return null;
        }
        EColumn column = new EColumn(headerName, new ArrayList<>());
        rows.forEach(row -> column.addCell(row.getCell(columnIndex)));
        return column;
    }

    /**
     * Returns the rows of this sheet, without any check,
     * rows can be duplicate, not consistent(2 rows with same key, but not equal, or row with empty key).
     * this is not a safe method to be used for comparison.
     *
     * @return the list of rows in this sheet.
     * @see ESheet#getUniqueData() instead.
     */
    @Override
    public List<ERow> getData() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * Returns a map with key that represents the row's key, and the associated row to each key.
     * <p>
     * this method was defined to fail fast if the data is inconsistent
     * (2 rows with same key, but not equal, or row with empty key), and remove duplicate rows
     *
     * @return a map of row's keys as keys, and the rows as value
     * @throws SheetWithInconsistentDataException if the data is inconsistent,
     *                                            data is considered to be inconsistent if 2 rows with same key but not equal, or row with empty key
     */
    @Override
    public Map<String, ERow> getUniqueData() {
        return generateUniqueRows(rows);
    }

    /**
     * Compare this sheet with anther one, and returns a new sheet that contains
     * the details of what elements have been changed, added or deleted.
     *
     * @param old the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     * @throws HeaderNotMatchingException if the 2 sheets headers are not matching,
     *                                    2 headers are matching if they are equal
     */
    @Override
    public ESheet compare(ESheet old) {
        ESheet diffSheet = new ExcelSheet(this);
        if (!(old instanceof ExcelSheet)) {
            diffSheet.setStatus(Status.ADDED);
            return diffSheet;
        }
        checkIfHeadersMatch(getHeaders(), old.getHeaders());
        Set<String> allRowsKeys = assembleTwoSets(old.getUniqueData().keySet(), getUniqueData().keySet());
        allRowsKeys.forEach(key -> assignRowToCompareSheet(old.getRow(key), diffSheet.getRow(key), diffSheet));
        return diffSheet;
    }

    private void checkIfHeadersMatch(List<String> headers, List<String> otherHeaders) {
        if (!headers.equals(otherHeaders)) {
            throw new HeaderNotMatchingException("Headers are not matching");
        }
    }

    private Set<String> assembleTwoSets(Set<String> set1, Set<String> set2) {
        Set<String> sumSet = new LinkedHashSet<>(set1);
        sumSet.addAll(set2);
        return sumSet;
    }

    private void assignRowToCompareSheet(ERow oldRow, ERow currentRow, ESheet diffSheet) {
        if (currentRow == null) {
            ERow oldRowCopy = new ExcelRow(oldRow);
            //if current is null than old is not because the key is extracted from one of them
            oldRowCopy.setStatus(Status.DELETED);
            oldRowCopy.getCells().forEach(cell -> cell.setStatus(Status.DELETED));
//            System.out.println("row " + oldRow + "\t" + oldRow.getStatus());
            diffSheet.addRow(oldRowCopy);
        } else if (oldRow == null) {
            currentRow.setStatus(Status.ADDED);
            currentRow.getCells().forEach(cell -> cell.setStatus(Status.ADDED));
//            System.out.println("row " + currentRow + "\t" + currentRow.getStatus());

        } else {
            ERow row = currentRow.compare(oldRow);
            if (!row.getStatus().equals(Status.NEW) && diffSheet.getStatus().equals(Status.NEW)) {
                diffSheet.setStatus(Status.CHANGED);
            }
            diffSheet.addRow(row);
        }
    }

    private Map<String, ERow> generateUniqueRows(List<ERow> rowsList) {
        final Map<String, ERow> tempUniqueKeys = new LinkedHashMap<>();
        rowsList.forEach(row -> addRowToUniqueRows(row, tempUniqueKeys));
        return tempUniqueKeys;
    }


    private void addRowToUniqueRows(ERow row, Map<String, ERow> map) {
        if (row.getSize() != columnsNumber) {
            throw new UnsupportedOperationException("The row: " + row.getKey(keyIndexes)
                                                            + " has a size of: " + row.getSize() + ", different than columns number " +
                                                            columnsNumber);
        }
        ERow rowInMap = map.get(getRowKey(row));
        if (rowInMap != null && !rowInMap.equals(row)) {
            throw new SheetWithInconsistentDataException(getRowKey(row));
        } else if (rowInMap == null) {
            map.put(getRowKey(row), row);
        }
    }

    private String getRowKey(ERow row) throws UnsupportedOperationException {
        return row.getKey(keyIndexes);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExcelSheet)) {
            return false;
        }
        ExcelSheet sheet = (ExcelSheet) o;
        return getName().equals(sheet.getName()) &&
                       Objects.equals(headers, sheet.getHeaders()) &&
                       Objects.equals(getUniqueData(), sheet.getUniqueData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, headers, uniqueRows);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (headers!=null){
            builder.append(headers).append("\n");
        }
       rows.forEach(row -> builder.append(row.toString()).append("\n"));
        return builder.toString();
    }
}
