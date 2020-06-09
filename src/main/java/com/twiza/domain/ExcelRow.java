package com.twiza.domain;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**
     * retrieve ERow id, by concatenating a list of columns,
     * separated by a special character<>'/'</>
     * based on the indexes supplied.
     *
     * @param idColumns an array of cells indexes,
     * @return a composite, unique id of the row, the columns are separated b
     */
    @Override
    public String getId(Integer[] idColumns) throws ArrayIndexOutOfBoundsException {
        StringBuilder idBuilder = new StringBuilder();
        int idColumnsLength = idColumns.length;
        for (int i = 0; i < idColumnsLength; i++) {

            idBuilder.append(getIdFromIndex(idColumns[i]));
            if (i < idColumnsLength - 1) {
                idBuilder.append(ID_SEPARATOR);
            }
        }

        return idBuilder.toString();
    }

    /**
     * returns the key from the index provided.
     *
     * @param index of the key.
     * @return a unique key to be considered as the ID.
     * @throws ArrayIndexOutOfBoundsException in case the index is negative,
     *                                        or bigger than the cells List size.
     */

    private String getIdFromIndex(int index) throws ArrayIndexOutOfBoundsException {
        return cells.get(index).getValue();
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
}
