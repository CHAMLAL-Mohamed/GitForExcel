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

    private List<ERow> rows;

    Map<String, ERow> uniqueRows;

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
     * @param name
     * @param headers
     * @param rows
     * @param keyColumns
     * @throws UnsupportedOperationException
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
        if (headers != null && headers.containsCells()) {
            return headers.getSize();
        } else if (rows.get(0).containsCells()) {
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
        rowsList.forEach(row -> {
            addRowToUniqueRows(row, tempUniqueKeys);
        });
        return tempUniqueKeys;
    }

    private void addRowToUniqueRows(ERow row, Map<String, ERow> map) throws UnsupportedOperationException {
        if (row.getSize() != columnsNumber) {
            throw new UnsupportedOperationException("The row: " + row.getKey(keyColumns)
                                                            + "has a size of: " + row.getSize() + ", less than columns number" +
                                                            columnsNumber);
        }
        ERow rowInMap = map.get(getRowKey(row));
        if (rowInMap != null && !rowInMap.equals(row)) {
            throw new UnsupportedOperationException(DIFFERENT_ROWS_WITH_SAME_KEY_EXCEPTION_MESSAGE);
        } else if (rowInMap == null) {
            map.put(getRowKey(row), row);
        }
    }

    private String getRowKey(ERow row) {
        return row.getKey(keyColumns);
    }

    @Override
    public ERow getHeaders() {
        return headers;
    }


    @Override
    public Integer[] getKeyColumns() {
        return keyColumns;
    }

    @Override
    public void setKeyColumns(Integer[] keyColumns) {
        this.keyColumns = keyColumns;
        uniqueRows.clear();
        uniqueRows.putAll(generateUniqueRows(rows));
    }

    @Override
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean addRow(ERow row) throws UnsupportedOperationException {
        if (columnsNumber == 0) {
            columnsNumber = row.getSize();
        }
        addRowToUniqueRows(row, uniqueRows);
        return rows.add(row);
    }

    @Override
    public boolean removeRow(ERow row) {
        return rows.remove(row);
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
    public boolean removeColumn(int position) throws
            ArrayIndexOutOfBoundsException, UnsupportedOperationException {
        if (position >= rows.get(0).getSize()) {
            throw new ArrayIndexOutOfBoundsException("Cant remove cell from position (" + position + ") bigger then the size of ERow");
        }
        for (Integer keyColumn : keyColumns) {
            if (position == keyColumn) {
                throw new UnsupportedOperationException(DELETING_KEY_COLUMN_EXCEPTION_MESSAGE + position);
            }
        }
        rows.forEach(row -> row.removeCell(position));
        columnsNumber--;
        return false;
    }

}
