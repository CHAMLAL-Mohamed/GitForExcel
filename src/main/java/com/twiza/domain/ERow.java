package com.twiza.domain;

import java.util.List;

public interface ERow {

    Status getStatus();

    List<ECell> getCells();

    ECell getCell(int position);

    String getKey(Integer[] idColumns) throws ArrayIndexOutOfBoundsException;

    String getKey(int index) throws ArrayIndexOutOfBoundsException;

    int getSize();
    boolean containsCells();

    void setStatus(Status newStatus);


    ECell removeCell(int position);

    boolean addCell(ECell cell);

    void addCell(int position, ECell cell);


    boolean updateCellValue(int position, String newValue);

}
