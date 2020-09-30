package com.twiza.domain;


import com.twiza.exceptions.UnsupportedStatusChangeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExcelRow implements ERow {


    private static final String EMPTY_KEY_EXCEPTION_MESSAGE = "Key Cannot be empty";
    /**
     * Default status of the instance,
     */
    private final static Status DEFAULT_STATUS = Status.NEW;

    /**
     * A List of {@link ECell}, represent a row elements.
     */
    private final List<ECell> cells;

    /**
     * The current status, it equals <>Status.NEW</> by default.
     */
    private Status status;

    /**
     * Constructs an empty row
     */
    public ExcelRow() {
        this(new ArrayList<>());
    }

    /**
     * Constructs a row with an initial list of cells.
     *
     * @param cells the list of cells that this rows contains
     * @throws NullPointerException if the input is null
     */
    public ExcelRow(List<ECell> cells) {
        Objects.requireNonNull(cells);
        this.cells = cells;
        this.status = DEFAULT_STATUS;
    }

    /**
     * Return the current {@code status } of this row.
     *
     * @return the current {@code status } of this row
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Returns an unmodifiable version of cells list to ensure immutability.
     *
     * @return unmodifiable list of this row's cells
     */
    @Override
    public List<ECell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    /**
     * Get the List of cells values in this row
     *
     * @return the list of Cells values
     */
    @Override
    public List<String> getCellsValues() {
        return cells.stream()
                    .map(ECell::getValue)
                    .collect(Collectors.toList());
    }

    /**
     * Returns the cell at the specified position in this row's list.
     *
     * @param position position of cell to return
     * @return the cell at the specified position in this row's list
     * @throws IndexOutOfBoundsException if the position is out of the cells list range
     */
    @Override
    public ECell getCell(int position) {
        return cells.get(position);
    }


    /**
     * build the key of this row based on the position of columns that compose the key.
     *
     * @param keyColumnsPositions the positions of columns  one position if simple key,
     *                            multiple if composed key
     * @return a unique key of the row based on columns indexes
     * @throws IndexOutOfBoundsException     if any of the provided columns's position is out of range
     * @throws UnsupportedOperationException if the result key is empty or contains only white space,
     *                                       <code>key.isBlank()</code>
     */
    @Override
    public String getKey(int... keyColumnsPositions) {
        StringBuilder keyBuilder = new StringBuilder();
        for (Integer position : keyColumnsPositions) {
            keyBuilder.append(cells.get(position).getValue());
        }
        String key = keyBuilder.toString();
        if (key.isBlank()) {
            throw new UnsupportedOperationException(EMPTY_KEY_EXCEPTION_MESSAGE);
        }
        return key;
    }

    /**
     * Returns the size of cells list in the row.
     *
     * @return the size of cells list.
     */
    @Override
    public int getSize() {
        return cells.size();
    }


    /**
     * set a new Status for this element, if it's applicable
     *
     * @param newStatus the new Status to be applied to this row
     * @throws UnsupportedStatusChangeException if the new status cannot be applied
     *                                          to the row
     */
    @Override
    public void setStatus(Status newStatus) {
        if ((status.equals(Status.ADDED)
                     || status == Status.CHANGED
                     || status == Status.DELETED) && newStatus == Status.NEW) {
            throw new UnsupportedStatusChangeException(status.toString());
        }
        this.status = newStatus;
    }

    /**
     * Remove cell from the given position, and returns the deleted cell.
     *
     * @param position the position from which to remove the cell
     * @return the deleted cell, null if no cell was deleted
     * @throws IndexOutOfBoundsException, if the position is out of the range
     */
    @Override
    public ECell removeCell(int position) throws IndexOutOfBoundsException {
        return cells.remove(position);
    }

    /**
     * append a {@code ECell} to end of the cells list.
     *
     * @param cell the ECell to be added to the end of the cells list.
     * @return {@code true}
     * @throws NullPointerException if the {@code ECell} was null.
     */
    @Override
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
    @Override
    public void addCell(int position, ECell cell) {
        Objects.requireNonNull(cell);
        cells.add(position, cell);
    }

    /**
     * Replaces the cell at the specified position in this row with the
     * specified cell
     *
     * @param position the position of the cell to replace
     * @param cell     cell to be stored at the specified position
     * @return the cell previously at the specified position
     * @throws NullPointerException      if the cell is null
     * @throws IndexOutOfBoundsException if the position is out of range{@inheritDoc}
     */
    @Override
    public ECell replaceCell(int position, ECell cell) {
        Objects.requireNonNull(cell);
        return cells.set(position, cell);
    }

    /**
     * Compare this row with anther one, and returns a new {@code ERow} that contains
     * the details of what elements have been changed.
     *
     * @param oldRow the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     */
    @Override
    public ERow compare(ERow oldRow) {
        if (oldRow == null) {
            ERow diffRow = new ExcelRow(getCells());
            diffRow.setStatus(Status.ADDED);
            System.out.println("row " + diffRow + "\t" + diffRow.getStatus());
            return diffRow;
        }
        ERow diffRow = new ExcelRow(oldRow.getCells());
        for (int i = 0; i < getSize(); i++) {
            String currentValue = getCell(i).getValue();
            String oldValue = diffRow.getCell(i).updateValue(currentValue);
            if (oldValue != null) {
                System.out.println("old value: " + oldValue + "\t" + "new value: " + currentValue);
                System.out.println(diffRow.getCell(i).getStatus());
                System.out.println(diffRow.getCell(i).getChangesHistory());
            }
        }
        diffRow.setStatus(Status.CHANGED);
        return diffRow;
    }

    @Override
    public int hashCode() {
        return cells.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ERow) {
            return ((ERow) obj).getCells().equals(getCells());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        cells.stream()
             .map(ECell::getValue)
             .forEach(value -> builder.append(value).append("\t"));
        return builder.toString();
    }
}
