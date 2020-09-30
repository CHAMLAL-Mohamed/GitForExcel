package com.twiza.domain;

import com.twiza.exceptions.UnsupportedStatusChangeException;

import java.util.List;

/**
 *
 */
public interface ERow {

    /**
     * Get current status of this row.
     *
     * @return the {@code Status} of this row, default value is {@code Status.NEW}
     */
    Status getStatus();

    /**
     * Get the List of cells in this row
     *
     * @return the list of Cells
     */
    List<ECell> getCells();

    /**
     * Get the List of cells values in this row
     *
     * @return the list of Cells values
     */
    List<String> getCellsValues();

    /**
     * Returns the cell at the specified position in this row's list.
     *
     * @param position position of cell to return
     * @return the cell at the specified position in this row's list
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    ECell getCell(int position);

    /**
     * build the key of this row based on the position of columns that compose the key.
     *
     * @param keyColumnsPosition the positions of columns  one position if simple key,
     *                           multiple if composed key
     * @return the key of the row
     * @throws IndexOutOfBoundsException     if any column's position is out of this row's range
     * @throws UnsupportedOperationException if the result key is empty or contains only white space,
     *                                       {@code key.isBlank()=true}
     */
    String getKey(int... keyColumnsPosition);

    /**
     * Returns the size of cells list in the row.
     *
     * @return the size of cells list.
     */
    int getSize();

    /**
     * set a new Status for this element, if it's applicable
     *
     * @param newStatus the new Status to be applied to this row
     * @throws UnsupportedStatusChangeException if the new status cannot be applied
     *                                          to the row
     */
    void setStatus(Status newStatus);

    /**
     * Remove cell from the given position
     *
     * @param position the position from which to remove the cell
     * @return the deleted cell, nut if no cell was deleted
     * @throws IndexOutOfBoundsException, if the position is out of the range
     */
    ECell removeCell(int position) throws IndexOutOfBoundsException;

    /**
     * append a {@code ECell} to end of the cells list.
     *
     * @param cell the ECell to be appended to the cells list.
     * @return {@code true}
     * @throws NullPointerException if the {@code ECell} was null.
     */
    boolean addCell(ECell cell);

    /**
     * Add the {@code ECell} to the provided position in the List.
     *
     * @param position position at which the specified cell to be inserted
     * @param cell     the ECell to be inserted
     * @throws IndexOutOfBoundsException if the position is out of range
     * @throws NullPointerException      if the {@code ECell} is null.
     */
    void addCell(int position, ECell cell);

    /**
     * Replaces the cell at the specified position in this row with the
     * specified cell.
     *
     * @param position the position of the cell to replace
     * @param cell     cell to be stored at the specified position
     * @return the cell previously at the specified position
     * @throws IndexOutOfBoundsException if the position is out of range{@inheritDoc}
     */
    ECell replaceCell(int position, ECell cell);

    /**
     * Compare this row with anther one, and returns a new {@code ERow} that contains
     * the details of what elements have been changed.
     *
     * @param oldRow the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     */
    ERow compare(ERow oldRow);

    /**
     * Compares the specified object with this row for equality.  Returns
     * {@code true} if and only if the specified object is also a {@link ERow}, both
     * rows have the same size, and all corresponding pairs of cells in the two lists
     * are <i>equal</i>
     * In other words, two rows are defined to be equals if they contains
     * the same elements at the same order. This definition ensures that
     * the equals method works properly across
     * different implementations of the {@code ERow} interface.
     *
     * @param o the object to be compared for equality with this row
     * @return {@code true} if the specified object is equal to this row
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this row.  The hash code of a row
     * is defined to be the result of the hashcode of cells list of this row .
     * This ensures that {@code row1.equals(rows)} implies that
     * {@code row1.hashCode()==row2.hashCode()} for any two rows,
     * {@code row1} and {@code row2}, as required by the general
     * contract of {@link Object#hashCode}.
     *
     * @return the hash code value for this row
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();


}
