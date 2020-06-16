package com.twiza.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelRow implements ERow {
    /**
     * Default status of the instance,
     * in case it wasn't provided during construction.
     */
    private final static Status DEFAULT_STATUS = Status.NEW;
    /**
     * used to separate key's elements, in case splitting is required.
     */
    private final static char ID_SEPARATOR = '/';

    /**
     * A List of {@link ECell}, represent a row elements.
     */
    private List<ECell> cells;

    /**
     * The current status, it equals <>Status.NEW</> by default.
     */
    private Status status;

    private String key;

    public ExcelRow() {
        this(new ArrayList<>(), DEFAULT_STATUS);
    }

    public ExcelRow(List<ECell> cells) {
        this(cells, DEFAULT_STATUS);
    }

    public ExcelRow(List<ECell> cells, Status status) {
        this.cells = cells;
        this.status = status;
    }


    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public List<ECell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    @Override
    public ECell getCell(int position) {
        return cells.get(position);
    }

    /**
     * retrieve ERow id, by concatenating a list of columns,
     * separated by a special character<>'/'</>
     * based on the indexes supplied.
     *
     * @param idColumns an array of cells indexes,
     * @return a composite, unique id of the row, the columns are separated b
     */
    @Override
    public String getKey(Integer[] idColumns) throws ArrayIndexOutOfBoundsException {
        StringBuilder idBuilder = new StringBuilder();
        int idColumnsLength = idColumns.length;
        for (int i = 0; i < idColumnsLength; i++) {

            idBuilder.append(getKey(idColumns[i]));
            if (i < idColumnsLength - 1) {
                idBuilder.append(ID_SEPARATOR);
            }
        }
        key = idBuilder.toString();
        return key;
    }

    /**
     * returns the key from the index provided.
     *
     * @param index of the key.
     * @return a unique key to be considered as the ID.
     * @throws ArrayIndexOutOfBoundsException in case the index is negative,
     *                                        or bigger than the cells List size.
     */

    public String getKey(int index) throws ArrayIndexOutOfBoundsException {
        return cells.get(index).getValue();
    }

    @Override
    public int getSize() {
        return cells.size();
    }

    @Override
    public boolean containsCells() {
        return !cells.isEmpty();
    }

    /**
     * set the new stat of the {@link ExcelRow}
     *
     * @param newStatus the new stat to be assigned to this instance.
     */

    @Override
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public ECell removeCell(int position) {
        return cells.remove(position);
    }

    @Override
    public boolean addCell(ECell cell) {
        return cells.add(cell);
    }

    @Override
    public void addCell(int position, ECell cell) {
        cells.add(position, cell);
    }


    @Override
    public boolean updateCellValue(int position, String newValue) {
        return cells.get(position).updateValue(newValue) != null;


    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ERow) {
            return ((ERow) obj).toString().equals(toString());
        }
        return false;
    }

    @Override
    public String toString() {
        return cells.stream()
                    .map(ECell::getValue)
                    .collect(Collectors.joining());
    }
}
