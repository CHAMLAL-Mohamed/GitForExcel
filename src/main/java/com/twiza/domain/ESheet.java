package com.twiza.domain;

import java.util.Map;

public interface ESheet {

    /**
     * retrieve the name of the sheet.
     *
     * @return the name of the Sheet.
     */
    String getName();


    Status getStatus();

    void setStatus(Status newStatus);

    /**
     * Get the {@link ERow} based on index of the row.
     *
     * @param index of the row in the sheet.
     * @return the ERow in the index position.
     */

    ERow getERow(int index);

    /**
     * retrieve the {@link ERow} based on a unique key, calculated using <code>ERow.getId()</code>
     *
     * @param key the unique key used to get the right ERow.
     * @return the correspondent ERow.
     */

    ERow getERow(String key);

    ERow getHeaders();

    int getColumnsNumber();

    Integer[] getKeyColumns();

    void setKeyColumns(Integer[] idColumns);

    /**
     * assign headers to the sheet,
     * and update ColumnsNumber in case it was 0.
     *
     * @param headers the headers to assign to the sheet.
     * @return false if headers are null, otherwise true,
     * (only if the size is not different than the other columns)
     * @throws UnsupportedOperationException, if the size of headers is different
     *                                        than the size of the already insertedRows
     */
    boolean assignHeaders(ERow headers) throws UnsupportedOperationException;

    boolean addRow(ERow row) throws UnsupportedOperationException;

    boolean removeRow(ERow row);


    boolean addColumn(int position);

    Map<String, ERow> getData();

    boolean removeColumn(int position) throws ArrayIndexOutOfBoundsException;

}
