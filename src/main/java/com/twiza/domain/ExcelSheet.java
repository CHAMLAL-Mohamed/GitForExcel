package com.twiza.domain;

import java.util.*;

public class ExcelSheet implements ESheet {
    /**
     * Default status of the instance,
     * the ExcelSheet is considered new during construction.
     */
    private final static Status DEFAULT_STATUS = Status.NEW;

    private final static String DELETING_KEY_COLUMN_EXCEPTION_MESSAGE = "You cannot delete a column, that compose the key please change keyColumns first. position is: ";
    private final static String DIFFERENT_ROWS_WITH_SAME_KEY_EXCEPTION_MESSAGE = "This key is already inserted with a different Row";
    private final static String HEADERS_INCORRECT_SIZE_EXCEPTION_MESSAGE = "Headers size is different than inserted rows size";
    /**
     * used as default value for key creation,
     * it considers that the first column is the key's column.
     */
    private static final Integer[] DEFAULT_KEY_COLUMNS = {0};

    /**
     * The name of the sheet.
     */
    //THOUGHT: use absolute or relative name for sheet?
    // -it depends if template is absolute and can support patterns,
    // -Or it can only reference direct sheets.
    private final String name;
    private int columnsNumber;

    /**
     * Status of the Sheet.
     */
    private Status status;

    /**
     * Headers of sheet, it can be null in case sheet has no headers.
     */
    private ERow headers;

    private final List<ERow> rows;

    private final Map<String, ERow> uniqueRows;

    private Integer[] keyColumns;

    public ExcelSheet(String name) {
        this(name, null, new ArrayList<>(), DEFAULT_KEY_COLUMNS);
    }

    public ExcelSheet(String name, ERow headers) {
        this(name, headers, new ArrayList<>(), DEFAULT_KEY_COLUMNS);
    }

    public ExcelSheet(String name, ERow headers, List<ERow> rows) {
        this(name, headers, rows, DEFAULT_KEY_COLUMNS);
    }

    /**
     * @param name       the name of the sheet.
     * @param headers    headers of the sheet if exist, otherwise null.
     * @param rows       the rows that constitute the sheet, except the headers
     * @param keyColumns the position of columns that contains key's elements.
     * @throws UnsupportedOperationException if rows have different sizes,
     *                                       or 2 rows have the same key with different contents
     *                                       <code>row1.getKey()==row2.getKey()
     *                                       && row1.equals(row2)==false</code>
     */
    public ExcelSheet(String name, ERow headers, List<ERow> rows, Integer[] keyColumns) throws UnsupportedOperationException {
        this.name = name;
        this.headers = headers;
        this.rows = new ArrayList<>(rows);//avoid passing external reference of lists.
        this.keyColumns = keyColumns;
        this.status = DEFAULT_STATUS;
        columnsNumber = assignColumnsNumber();
        uniqueRows = generateUniqueRows(rows);
    }

    private int assignColumnsNumber() {
        if (headers != null) {
            return headers.getSize();
        } else if (!rows.isEmpty()) {
            return rows.get(0).getSize();
        }
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public ERow getERow(int index) {
        return rows.get(index);
    }

    @Override
    public ERow getERow(String key) {
        return uniqueRows.get(key);
    }

    private Map<String, ERow> generateUniqueRows(List<ERow> rowsList) throws UnsupportedOperationException {
        final Map<String, ERow> tempUniqueKeys = new HashMap<>();
        rowsList.forEach(row -> addRowToUniqueRows(row, tempUniqueKeys));
        return tempUniqueKeys;
    }

    private void addRowToUniqueRows(ERow row, Map<String, ERow> map) throws UnsupportedOperationException {
        if (row == null) {
            return;
        }
        if (row.getSize() != columnsNumber) {
            throw new UnsupportedOperationException("The row: " + row.getKey(keyColumns)
                                                            + " has a size of: " + row.getSize() + ", different than columns number " +
                                                            columnsNumber);
        }

        ERow rowInMap = map.get(getRowKey(row));
        if (rowInMap != null && !rowInMap.equals(row)) {
            throw new UnsupportedOperationException(DIFFERENT_ROWS_WITH_SAME_KEY_EXCEPTION_MESSAGE);
        } else if (rowInMap == null) {
            map.put(getRowKey(row), row);
        }
    }

    private String getRowKey(ERow row) throws UnsupportedOperationException {
        return row.getKey(keyColumns);
    }

    @Override
    public ERow getHeaders() {
        return headers;
    }

    @Override
    public int getColumnsNumber() {
        return columnsNumber;
    }


    @Override
    public Integer[] getKeyColumns() {
        return keyColumns;
    }

    //TODO: in case the assignment fails put back the old KeyColumns
    // so it can recover again.
    @Override
    public void setKeyColumns(Integer[] keyColumns) {
        this.keyColumns = keyColumns;
        uniqueRows.clear();
        Map<String, ERow> tempUniqueRows = generateUniqueRows(rows);
        uniqueRows.putAll(tempUniqueRows);
    }

    @Override
    public boolean assignHeaders(ERow headers) throws UnsupportedOperationException {
        if (headers == null) {
            return false;
        }
        if (columnsNumber != 0 && columnsNumber != headers.getSize()) {
            throw new UnsupportedOperationException(HEADERS_INCORRECT_SIZE_EXCEPTION_MESSAGE);
        }
        this.headers = headers;
        if (columnsNumber == 0) {
            columnsNumber = assignColumnsNumber();
        }

        return true;
    }

    @Override
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean addRow(ERow row) throws UnsupportedOperationException {
        rows.add(row);
        if (headers == null && rows.size() < 2) {
            columnsNumber = assignColumnsNumber();
        }
        addRowToUniqueRows(row, uniqueRows);
        return true;
    }

    @Override
    public boolean removeRow(ERow row) {
        boolean rowIsRemoved = rows.remove(row);
        if (headers == null && rows.isEmpty()) {
            columnsNumber = assignColumnsNumber();
        }
        return rowIsRemoved;
    }

    @Override
    public boolean addColumn(int position) {
        return false;
    }

    @Override
    public Map<String, ERow> getData() {
        return Collections.unmodifiableMap(uniqueRows);
    }

    @Override
    public boolean removeColumn(int position) throws UnsupportedOperationException {
        if (position >= rows.get(0).getSize()) {
            return false;
        }
        for (int keyColumn : keyColumns) {
            if (position == keyColumn) {
                throw new UnsupportedOperationException(DELETING_KEY_COLUMN_EXCEPTION_MESSAGE + position);
            }
        }
        headers.removeCell(position);
        rows.forEach(row -> row.removeCell(position));
        columnsNumber = assignColumnsNumber();
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(headers.toString()).append("\n");
        uniqueRows.values().forEach(row -> builder.append(row.toString()).append("\n"));
        return builder.toString();
    }
}
