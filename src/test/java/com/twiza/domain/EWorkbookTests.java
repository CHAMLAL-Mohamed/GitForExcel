package com.twiza.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ESheet.class)

@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class EWorkbookTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    List<ESheet> sheets;
    String workbookName = "Workbook";
    int sheetsNumber = 5;

    @Before
    public void setup() {
        sheets = new ArrayList<>();
    }

    @Test
    public void EWorkbookWithDifferentSheetsWithDifferentNamesTest() {
        for (int i = 0; i < sheetsNumber; i++) {
            sheets.add(mockESheet("Name" + i));
        }
        EWorkbook workbook = new ExcelWorkbook(workbookName, sheets);
        Assert.assertEquals(sheetsNumber, workbook.getSize());
    }


    @Test
    public void EWorkbookWithDifferentSheetsWithSameNamesTest() {
        for (int i = 0; i < sheetsNumber; i++) {
            sheets.add(mockESheet("SameName"));
        }
        exceptionRule.expect(UnsupportedOperationException.class);
        exceptionRule.expectMessage(EWorkbook.SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE);
        new ExcelWorkbook(workbookName, sheets);
    }


    ESheet mockESheet(String sheetName) {
        ESheet sheet = mock(ESheet.class);
        when(sheet.getName()).thenReturn(sheetName);
        return sheet;
    }
}
