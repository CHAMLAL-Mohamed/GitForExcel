package com.twiza.domain;

import java.util.ArrayList;
import java.util.List;

public class ExcelSheet implements ESheet {
    /**
     * Default status of the instance,
     * in case it wasn't provided during construction.
     */
    private final static Status DEFAULT_STATUS = Status.NEW;

    private static final Integer[] DEFAULT_ID_COLUMNS = {0};


    private Status status;
    private final String name;

    private ERow headers;

    private List<ERow> rows;

    private Integer[] idColumns;

    public ExcelSheet(String name) {
        this(name, null, new ArrayList<>());
    }

    public ExcelSheet(String name, ERow headers) {
        this(name, headers, new ArrayList<>());
    }

    public ExcelSheet(String name, ERow headers, List<ERow> rows) {
        this.name = name;
        this.headers = headers;
        this.rows = rows;
    }


    @Override
    public String getName() {
        return null;
    }

    @Override
    public ERow getERow(int index) {
        return null;
    }

    @Override
    public ERow getERow(String key) {
        return null;
    }

    @Override
    public void setIdColumns(Integer[] idColumns) {
        this.idColumns = idColumns;
    }

    @Override
    public void setStatus(Status newStatus) {

    }

    @Override
    public boolean addRow(ERow row) {
        return false;
    }

    @Override
    public boolean removeRow(ERow row) {
        return false;
    }
}
