package com.twiza.domain;

import java.util.List;
import java.util.Objects;

public class EColumn {

    String headerName;
    List<ECell> cells;

    public EColumn(String headerName, List<ECell> cells) {
        this.headerName = headerName;
        this.cells = cells;
    }

    public int getSize() {
        return cells.size();
    }

    public String getHeaderName() {
        return headerName;
    }

    public List<ECell> getCells() {
        return cells;
    }

    /**
     * append a {@code ECell} to end of the cells list.
     *
     * @param cell the ECell to be added to the end of the cells list.
     * @return {@code true}
     * @throws NullPointerException if the {@code ECell} was null.
     */

    public boolean addCell(ECell cell) {
        Objects.requireNonNull(cell);
        return cells.add(cell);
    }

    /**
     * Add the {@code ECell} to the provided position in the List.
     *
     * @param position position at which the specified cell to be inserted
     * @param cell     cell to be inserted
     * @throws IndexOutOfBoundsException if the position is out of range
     * @throws NullPointerException      if the {@code ECell} is null.
     */

    public void addCell(int position, ECell cell) {
        Objects.requireNonNull(cell);
        cells.add(position, cell);
    }
}
