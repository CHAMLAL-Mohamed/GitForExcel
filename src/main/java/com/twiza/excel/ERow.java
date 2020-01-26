package com.twiza.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ERow {
    private final String id;
    private List<String> elements;
    private Status status;
    private Map<Integer, String> changedElements;

    public ERow(String id, List<String> elements) {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    ERow compare(ERow row) {
        for (int i = 0; i < this.getElements().size(); i++) {
            if (!this.getElements().get(i).equals(row.getElements().get(i))) {
                this.changedElements.putIfAbsent(i, row.getElements().get(i));
                System.out.println("The element " + this.getId() + " has changed from " + row.getElements().get(i)
                        + " to: " + this.getElements().get(i));
            }
        }
        return this;
    }
}
