package com.twiza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Templates {

    private static Templates INSTANCE;
    Map<String, List<String>> sheetsTemplates;

    private Templates(String path) {
        sheetsTemplates = new HashMap<>();

    }

    public static Templates getInstance(String path) {
        if (INSTANCE == null) {
            INSTANCE = new Templates(path);
        }
        return INSTANCE;
    }

    /**
     * get the template of sheet or return null if it has no predefined template.
     *
     * @param sheetName the name of the sheet, it can be the absolute name or the short name,
     * @return null if no template exist for this sheetName, or a list of headers
     */
    public List<String> getSheetTemplate(String sheetName) {

        return sheetsTemplates.getOrDefault(sheetName, null);
    }
}
