/*
 * Copyright  2020  Chamlal.Mohamed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twiza.domain;

import com.twiza.exceptions.HeaderNotMatchingException;
import com.twiza.exceptions.SheetWithInconsistentDataException;
import com.twiza.exceptions.UnsupportedStatusChangeException;

import java.util.List;
import java.util.Map;

public interface ESheet {


    /**
     * this enum is used to specify which mode will be used to match sheet against a headers
     * the headers can be the concatenation of unique headers of 2 sheets, matched headers
     * between 2 sheets, or a specific headers.
     */
    enum TemplateMode {
        MATCH, CONCAT, SPECIFIC;
    }


    /**
     * Appends a row at the end of this ESheet.
     *
     * @param row the row to be added to the end of this sheet
     * @return this {@link ESheet} with the implemented modifications
     * @throws NullPointerException          if the <code>row</code> is null
     * @throws UnsupportedOperationException if the row's size is different than the size of the sheet columns
     */
    ESheet addRow(ERow row);

    /**
     * Adds a new row to the provided <code>position</code>.
     *
     * @param position the position where the row will be added to
     * @param row      the row to be added
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException     if the <code>position</code> provided is out of range
     * @throws NullPointerException          if the <code>row</code> is null
     * @throws UnsupportedOperationException if the row's size is different than the size of the sheet columns
     */
    ESheet addRow(int position, ERow row);


    /**
     * Adds a new row to the provided <code>position</code>, with the possibility
     * to append blanks to the end of the row if its size is less than
     * the columns number of this sheet.
     *
     * @param position     the position where the row will be added to
     * @param row          the row to be added
     * @param appendBlanks wether to add blanks or not
     * @return this {@link ESheet} with the implemented modifications
     * @throws UnsupportedOperationException if the row's size is different than the size of the sheet columns
     */
    ESheet addRow(int position, ERow row, boolean appendBlanks);

    /**
     * Deletes the rows in the provided positions.
     *
     * @param positions the position where the row will be deleted from
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if one of the provided positions  is out of range
     */
    ESheet deleteRows(int... positions);

    /**
     * Deletes the provided row from this sheet
     * (optional operation).
     *
     * @param row the row to be deleted from this sheet
     * @return this {@link ESheet} with the implemented modifications.
     * @throws NullPointerException if the <code>row</code> is null
     */
    ESheet deleteRow(ERow row);


    /**
     * Deletes a range of columns from this {@link ESheet}
     *
     * @param beginIndex the index of the first row to delete, inclusive
     * @param endIndex   the index of the last row to delete, inclusive
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if the
     *                                   {@code beginIndex} is negative, or
     *                                   {@code endIndex} is larger than the length of
     *                                   this {@code ESheet} rows, or
     *                                   {@code beginIndex} is larger than
     *                                   {@code endIndex}.
     */
    ESheet deleteRowsRange(int beginIndex, int endIndex);

    /**
     * Deletes all the empty rows(optional operation).
     * a row is considered to be empty, if all the cells in the row are blank
     * (Empty or contains only white spaces).
     *
     * @return this {@link ESheet} with the implemented modifications.
     */
    ESheet deleteEmptyRows();

    /**
     * Appends a column at the end of this ESheet,
     * in other words, append  each <code>cell(i)</code> in column to the end of <code>row(i)</code>
     *
     * @param column the column to be added to the end of this sheet
     * @return this {@link ESheet} with the implemented modifications
     * @throws NullPointerException if the <code>row</code> is null
     */
    ESheet addColumn(EColumn column);

    /**
     * Adds a new column to the provided <code>position</code>.
     * In other words add  each <code>cell(i)</code> in column
     * to the provided position in the row <code>row(i).addCell(position, cell(i))</code>
     *
     * @param position the position where the column will be added to
     * @param column   the column to be added
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if the <code>position</code> provided is out of range
     * @throws NullPointerException      if the <code>column</code> is null
     */
    ESheet addColumn(int position, EColumn column);

    /**
     * Adds a new column to the provided <code>position</code>, with the possibility
     * to append blanks to the end of the column if its size is less than
     * the rows number of this sheet.
     *
     * @param position     the position where the column will be inserted
     * @param column       the column to be inserted
     * @param appendBlanks wether to add blanks or not
     * @return this {@link ESheet} with the implemented modifications
     * @throws UnsupportedOperationException if the column's size is different than the sheet's rows size
     */
    ESheet addColumn(int position, EColumn column, boolean appendBlanks);

    /**
     * Deletes the columns in the provided positions,
     * In other words, from each row in the sheet,
     * delete the cells at <code>positions</code>.
     *
     * @param positions an array of position from where to delete the columns
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if one of the provided positions  is out of range
     */
    ESheet deleteColumns(int... positions);

    /**
     * Deletes a range of columns from this {@link ESheet}.
     *
     * @param beginIndex the index of the first column to delete, inclusive
     * @param endIndex   the index of the last column to delete, inclusive
     * @return this {@link ESheet} with the implemented modifications
     * @throws IndexOutOfBoundsException if the
     *                                   {@code beginIndex} is negative, or
     *                                   {@code endIndex} is larger than the length of
     *                                   this {@code ESheet} columns, or
     *                                   {@code beginIndex} is larger than
     *                                   {@code endIndex}.
     */
    ESheet deleteColumnRange(int beginIndex, int endIndex);

    /**
     * Deletes all the empty columns(optional operation).
     * a column is considered to be empty, if all the cells in the column are blank,
     * (Empty or contains only white spaces).
     *
     * @return this {@link ESheet} with the implemented modifications.
     */
    ESheet deleteEmptyColumns();

    /**
     * adjust the current sheet to match the provided template headers, and the mode
     * In other words, eliminate extra columns, and add missing columns to match the template provided.
     *
     * @param template template the headers to match against
     * @param mode     check {@link TemplateMode}
     * @return this {@link ESheet} with the implemented modifications.
     */
    ESheet matchWithTemplate(List<String> template, TemplateMode mode);

    /**
     * Sets the  indexes of the key, in case of simple key it will be one index, otherwise an array of indexes,
     * the indexes will be used to compose the key in each row.
     *
     * @param keyIndexes the indexes that will be used to construct the key in each {@link ERow},
     * @return this {@link ESheet} with the implemented modifications.
     * @throws IndexOutOfBoundsException if any of the provided indexes is out of range
     *                                   in any row instance in this sheet
     */
    ESheet setKeyIndexes(int... keyIndexes);

    /**
     * Assigns a list of headers to the sheet,
     *
     * @param newHeaders the headers to assign to the sheet, the {@code newHeaders} size
     *                   should be equal to the columns number in this sheet
     * @return this {@link ESheet} with the implemented modifications.
     */
    ESheet setHeaders(List<String> newHeaders);

    /**
     * takes the first row in the sheet and set it as headers, if isFirstRowHeaders is{@code true},
     * otherwise does nothing.
     *
     * @param isFirstRowHeaders if the first row should be considered as headers.
     * @return this {@link ESheet} with the implemented modifications.
     */
    ESheet adoptFirstRowAsHeaders(boolean isFirstRowHeaders);

    /**
     * set a new Status for this element, if it's applicable
     *
     * @param newStatus the new Status to be applied to this element
     * @return this {@link ESheet} with the implemented modifications.
     * @throws UnsupportedStatusChangeException if the new status cannot be applied
     *                                          to the element
     */
    ESheet setStatus(Status newStatus);

    /**
     * Returns the name of the sheet.
     *
     * @return the name of the Sheet.
     */
    String getName();

    /**
     * Seet sheetName
     */
    void setName(String name);

    /**
     * Returns the current status of this sheet.
     *
     * @return the {@code Status} of this sheet, default value is {@code Status.NEW}
     */
    Status getStatus();

    /**
     * Returns the a <code>List</code> that contains the headers of this sheet.
     *
     * @return the list of headers of this sheet
     */
    List<String> getHeaders();

    /**
     * Returns the number of columns in this sheet.
     *
     * @return the number of Columns in this sheet
     */
    int getColumnsNumber();

    /**
     * Returns the number of rows in this sheet.
     *
     * @return the number of rows in this sheet
     */
    int getRowsNumber();

    /**
     * Returns array of integers that contains the positions columns that are used to construct the key on each row.
     *
     * @return the positions of columns used to construct the key on each row
     */
    int[] getKeysIndexes();

    /**
     * Get the {@link ERow} based on index of the row.
     *
     * @param position of the row in the sheet.
     * @return the ERow in the index position.
     * @throws IndexOutOfBoundsException if any of the provided indexes is out of range
     */
    ERow getRow(int position);

    /**
     * retrieve the row associated to the provided key,
     * get the first row in this sheet where<code>ERow.getKey().equals(key)==true</code>,
     * null if not found.
     *
     * @param key the unique key used to get the associated row
     * @return a row with a key equals provided key, null if no row is found
     */
    ERow getRow(String key);

    /**
     * Get the {@link EColumn} based on index of the column.
     *
     * @param position of the column in the sheet.
     * @return the EColumn in the provided position.
     * @throws IndexOutOfBoundsException if any of the provided position is out of range
     *                                   in any row instance in this sheet
     */
    EColumn getColumn(int position);

    /**
     * Returns a column based on header's name, <code>null</code> otherwise.
     *
     * @param headerName the name of the header
     * @return the associated column to the provided header,
     * return <code>null</code> if the header is not found.
     */
    EColumn getColumn(String headerName);

    /**
     * Returns the rows of this sheet, without any check,
     * rows can be duplicate, not consistent(2 rows with same key, but not equal, or row with empty key).
     *
     * @return the list of rows in this sheet.
     * this is not a safe method to be used for comparison,
     * @see ESheet#getUniqueData() instead.
     */
    List<ERow> getData();


    /**
     * Returns a map with key that represents the row's key, and the associated row to each key.
     * This method was defined to fail fast if the data is inconsistent
     * (2 rows with same key, but not equal, or row with empty key), and remove duplicate rows
     *
     * @return a map of row's keys as keys, and the rows as value
     * @throws SheetWithInconsistentDataException if the data is inconsistent,
     *                                            data is considered to be inconsistent if 2 rows with same key but not equal, or row with empty key
     */
    Map<String, ERow> getUniqueData();

    /**
     * Compare this sheet with anther one, and returns a new sheet that contains
     * the details of what elements have been changed, added or deleted.
     *
     * @param old the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     * @throws HeaderNotMatchingException if the 2 sheets headers are not matching,
     *                                    2 headers are matching if they are equal
     */
    ESheet compare(ESheet old);

    /**
     * Compares the specified object with this sheet for equality.  Returns
     * {@code true} if and only if the specified object is also a {@link ESheet}, both
     * sheets have the same unique rows number, and all corresponding pairs of rows in the two sheets
     * are <i>equal</i>
     * In other words, two sheets are defined to be equals if they contains
     * the same unique rows. This definition ensures that
     * the equals method works properly across
     * different implementations of the {@link ESheet} interface.
     *
     * @param o the object to be compared for equality with this sheet
     * @return {@code true} if the specified object is equal to this sheet
     */
    boolean equals(Object o);

    //TODO: improve documentations of equals and hashcode

    /**
     * Returns the hash code value for this sheet.  The hash code of a sheet
     * is defined to be the result of the hashcode of unique rows of this sheet .
     * This ensures that {@code sheet1.equals(rows)} implies that
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
