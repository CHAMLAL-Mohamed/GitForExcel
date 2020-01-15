package com.twiza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ERow {
    final String id;
    List<String> elements;
    Map<Integer, String> changedElements;

    public ERow(String id,List<String> elements) {
        this.id = id;
        this.elements = elements;
        this.changedElements = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public Map<Integer, String> getChangedElements() {
        return changedElements;
    }

    public void setChangedElements(Map<Integer, String> changedElements) {
        this.changedElements = changedElements;
    }
}
