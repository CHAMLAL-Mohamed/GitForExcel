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


    boolean equals(Object o);

    int hashCode();


}
