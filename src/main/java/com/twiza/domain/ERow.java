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
     * @return a List of Cells
     */
    List<ECell> getCells();

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
     * @throws UnsupportedOperationException if the key is Empty{@code key.isBlank()=true}
     */
    String getKey(Integer... keyColumnsPosition);

    /**
     * number of cells in the row.
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
     * @param position the position frow which to remove the cell
     * @return the deleted cell, nut if no cell was deleted
     * @throws IndexOutOfBoundsException, if the position is out of the range
     */
    ECell removeCell(int position);

    /**
     * Remove a given cell from the row, if already exist
     *
     * @param cell the cell to be deleted
     * @return the deleted cell, nut if no cell was deleted
     */
    ECell removeCell(ECell cell);

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
     * Update the cell's value with the {@code newValue} at the specified position
     * in the list.
     *
     * @param position the position of the cell to be updated
     * @param newValue the new Value of the cell to be updated with
     * @return true if the cell was updated successfully.
     * @throws IndexOutOfBoundsException if the position is out of range
     */
    boolean updateCellValue(int position, String newValue);

    /**
     * Update the cell's value with the {@code newValue}
     *
     * @param cell     the cell to be updated
     * @param newValue the new value of the cell to be updated with
     * @return true if the cell exist in the list and updated, false otherwise.
     */
    boolean updateCellValue(ECell cell, String newValue);


    boolean equals(Object o);

    int hashCode();


}
