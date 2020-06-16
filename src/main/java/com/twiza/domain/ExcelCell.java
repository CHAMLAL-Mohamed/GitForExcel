package com.twiza.domain;

import java.util.Objects;

public class ExcelCell implements ECell {
    private static final Status DEFAULT_STATUS = Status.NEW;


    private String value;
    private String oldValue;
    private Status status;

    public ExcelCell(String value) {
        this(value, DEFAULT_STATUS);
    }

    public ExcelCell(String value, Status status) {
        Objects.requireNonNull(value);
        this.value = value;
        this.status = status;
    }


    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }


    @Override
    public String updateValue(String newValue) {
        Objects.requireNonNull(newValue);
        if (getValue().equals(newValue)) {
            return null;
        }
        setStatus(Status.CHANGED);
        setOldValue(value);
        setValue(newValue);
        return getOldValue();
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
