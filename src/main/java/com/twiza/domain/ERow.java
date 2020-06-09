package com.twiza.domain;

import java.util.List;

public interface ERow {

    Status getStatus();

    List<ECell> getCells();

    String getId(Integer[] idColumns);


    void setStatus(Status newStatus);
}
