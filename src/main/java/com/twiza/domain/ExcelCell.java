package com.twiza.domain;

public class ExcelCell implements ECell {
    private static final Status DEFAULT_STATUS = Status.NEW;
    private String value;
    private String oldValue;
    private Status status;

    public ExcelCell(String value) {
        this(value, DEFAULT_STATUS);
    }

    public ExcelCell(String value, Status status) {
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

}
