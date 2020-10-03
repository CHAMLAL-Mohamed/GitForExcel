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

import com.twiza.exceptions.UnsupportedStatusChangeException;

/**
 * @author Mohamed.Chamlal
 */
public interface ECell {

    /**
     * get the status of Cell(new, changed, deleted, added)
     *
     * @return the {@link Status}
     */
    Status getStatus();

    /**
     * get the current value of the cell as String.
     *
     * @return the current text in the cell.
     */
    String getValue();

    /**
     * get change history of this {@link ECell} (stored in comment in excel) if exist,
     * if not return null.
     *
     * @return a String containing the change history of this cell, null otherwise.
     */
    String getChangesHistory();

    /**
     * update value of the cell in case it is different from the current value and return oldValue,
     * return null otherwise.
     *
     * @param newValue the cell's new value.
     * @return the old value if the cell was updated, null otherwise.
     */
    String updateValue(String newValue);

    /**
     * update the {@link Status} of the cell.
     *
     * @param newStatus the new Status of the cell.
     * @throws UnsupportedStatusChangeException if the new status cannot be applicable.
     */
    void setStatus(Status newStatus);

    /**
     * Compares the specified object with this cell for equality.  Returns
     * {@code true} if and only if the specified object is also a cell, and both
     * cells have the same value.
     * definition ensures that the equals method works properly across
     * different implementations of the {@code ECell} interface.
     *
     * @param o the object to be compared for equality with this cell
     * @return {@code true} if the specified object is equal to this cell
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this cell.  The hash code of a cell
     * is defined to be the hashcode of the cellValue String.
     * This ensures that {@code cell1.equals(cell2)} implies that
     * {@code cell1.hashCode()==cell2.hashCode()} for any two cells,
     * {@code cell1} and {@code cell2}, as required by the general
     * contract of {@link Object#hashCode}.
     *
     * @return the hash code value for this cell
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();
}
