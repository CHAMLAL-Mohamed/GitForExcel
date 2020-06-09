package com.twiza.domain;

public interface ESheet {

    /**
     * retrieve the name of the sheet.
     *
     * @return the name of the Sheet.
     */
    String getName();

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

    void setIdColumns(Integer[] idColumns);

    void setStatus(Status newStatus);

    boolean addRow(ERow row);

    boolean removeRow(ERow row);

}
