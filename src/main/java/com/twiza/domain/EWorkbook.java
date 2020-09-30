package com.twiza.domain;

import java.util.Map;

public interface EWorkbook {
    String SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE = "EWorkbook cannot contain 2 ESheets with the same Name";

    /**
     * some documents
     *
     * @param name
     * @return
     */
    ESheet getSheet(String name);

    ESheet getSheet(int index);

    String getWorkbookPath();

    Map<String, ESheet> getSheets();

    int getSize();

    void addSheet(ESheet sheet);


    boolean removeSheet(String sheetName);

    /**
     * Compare this sheet with anther one, and returns a new sheet that contains
     * the details of what elements have been changed, added or deleted.
     *
     * @param oldWorkbook the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     */
    EWorkbook compare(EWorkbook oldWorkbook);

    boolean equals(Object o);

    int hashCode();


}
