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

    Integer[] getKeyColumns();

    void setKeyColumns(Integer[] idColumns);

    boolean addRow(ERow row) throws UnsupportedOperationException;

    boolean removeRow(ERow row);

    boolean addColumn(int position);

    Map<String, ERow> getData();

    boolean removeColumn(int position) throws ArrayIndexOutOfBoundsException;

}
