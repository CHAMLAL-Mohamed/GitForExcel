package com.twiza.domain;

public interface ECell {


    /**
     * get the status of Cell( changed, deleted, added, new)
     *
     * @return the {@link Status}
     */
    Status getStatus();

    String getValue();

    String getOldValue();

    void setStatus(Status newStatus);

    void setValue(String value);

    void setOldValue(String oldValue);

    String updateValue(String newValue);
}
