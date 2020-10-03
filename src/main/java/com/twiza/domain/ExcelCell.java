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

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ExcelCell implements ECell {
    /**
     * Default initial {@code Status}.
     */
    private static final Status DEFAULT_STATUS = Status.NEW;
    /**
     * Date and time format to be used in {@code changeHistory}
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * the value of the cell
     */
    private String value;
    /**
     * The changeHistory of this cell(contains the oldValue, the date&time of the change, and the modifier user).
     */
    private final StringBuilder changesHistory;
    /**
     * The current status of the cell(check {@link Status}).
     */
    private Status status;

    /**
     * Constructs a cell containing a value, and default status.
     *
     * @param value the value of the cell
     * @throws NullPointerException if the value is null
     */
    public ExcelCell(String value) {
        this(value, null);
    }

    /**
     * Constructs a cell containing the value, the changes history, and the status of the cell
     *
     * @param value          the value of the cell
     * @param changesHistory the changes history of this cell
     * @throws NullPointerException if the {@code value} is null
     */
    public ExcelCell(String value, String changesHistory) {
        Objects.requireNonNull(value);
        this.value = value;
        this.status = DEFAULT_STATUS;
        this.changesHistory = new StringBuilder();
        if (changesHistory != null) {
            //TODO(1): history should obey to the standard or ignore it:
            // date&Time+"\t"+"oldValue"+"\t"+user
            //TODO(2): check the necessity to remove adding initial History during construction
            // and use a setter.
            setInitialChangesHistory(changesHistory);
        }
    }

    /**
     * Return the current {@code status } of this cell.
     *
     * @return the current {@code status } of this cell
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the {@code value} of this cell.
     *
     * @return the {@code value} of this cell
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Returns all the changes made in this cell.
     *
     * @return the {@code changeHistory} of this cell
     */
    @Override
    public String getChangesHistory() {
        return changesHistory.toString();

    }

    private void setInitialChangesHistory(String initialChangesHistory) {
        String[] changes = initialChangesHistory.split("\n");
        for (String change : changes) {
            String[] elements = change.split("\t");
            int dateTimeIndex = 0;
            try {
                DATE_TIME_FORMATTER.parse(elements[dateTimeIndex]);
            } catch (DateTimeException e) {
                return;
            }
            if (elements.length < 3) {
                return;
            }
        }
        this.changesHistory.append(initialChangesHistory);
    }

    /**
     * Update the value of this cell if it is applicable, and return oldValue.
     * Otherwise returns {@code null}.
     *
     * @param newValue the cell's new value, {@code null} values are not considered
     * @return the old value of this cell if it was updated, {@code null} if not
     */
    @Override
    public String updateValue(String newValue) {
        if (newValue == null || getValue().equals(newValue)) {
            return null;
        }
        String oldValue = value;
        setValue(newValue);
        setStatus(Status.CHANGED);
        UpdateChangesHistory(oldValue);
        return oldValue;
    }

    private void setValue(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    /**
     * Set the {@code status} of this cell if it is possible,
     *
     * @param newStatus the new Status of the cell.
     * @throws UnsupportedStatusChangeException if the new status cannot be applicable.
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
     * Update the changes history of this cell by adding a new line that contains
     * the time& date of change, the old value of this cell, and the user who made the changes.
     *
     * @param oldValue the old value of this cell
     */
    private void UpdateChangesHistory(String oldValue) {
        this.changesHistory.append(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                           .append("  ")
                           .append(oldValue)
                           .append("  ")
                           .append("User");
    }


    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ECell) {
            return ((ECell) obj).getValue().equals(getValue());
        }
        return false;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
