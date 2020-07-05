package com.twiza.data;

import java.util.ArrayList;
import java.util.List;

public class Ignore {

    private static Ignore INSTANCE;
    private static List<String> folders;
    private static List<String> workbooks;
    private static List<String> sheets;

    private Ignore() {
        folders = new ArrayList<>();
        workbooks = new ArrayList<>();
        sheets = new ArrayList<>();
    }

    public static Ignore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ignore();
        }
        return INSTANCE;
    }

    public Ignore setFoldersToIgnore(List<String> foldersToIgnore) {
        folders = foldersToIgnore;
        return INSTANCE;
    }

    public Ignore setWorkbooksToIgnore(List<String> workbooksToIgnore) {
        workbooks = workbooksToIgnore;
        return INSTANCE;
    }

    public Ignore setSheetsToIgnore(List<String> sheetsToIgnore) {
        sheets = sheetsToIgnore;
        return INSTANCE;
    }

    public static List<String> getFolders() {
        return folders;
    }

    public static List<String> getWorkbooks() {
        return workbooks;
    }

    public static List<String> getSheets() {
        return sheets;
    }
}
