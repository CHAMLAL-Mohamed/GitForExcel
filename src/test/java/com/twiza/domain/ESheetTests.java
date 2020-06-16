package com.twiza.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class ESheetTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    List<ERow> rows;
    ERow headers;
    Integer[] keyColumns;
    String sheetName = "Sheet";
    int cellsSize = 6;

    @Before
    public void setup() {
        headers = mock(ERow.class);
        keyColumns = new Integer[]{0};
        rows = new ArrayList<>();
    }

    @Test
    public void createESheetWithDiffRowsAndDiffKeysTest() {
        for (int i = 0; i < 5; i++) {
            ERow row = mock(ERow.class);
            rows.add(mockERow(cellsSize, true, "key" + i));
        }
        ESheet sheet = new ExcelSheet(sheetName, headers, rows, keyColumns);
        Assert.assertNotNull(sheetName);
    }

    @Test
    public void createESheetWithDiffRowsWithSameKey() {

        exceptionRule.expect(UnsupportedOperationException.class);

        for (int i = 0; i < 5; i++) {
            ERow row = mock(ERow.class);
            rows.add(mockERow(cellsSize, true, "key" + i));
        }

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        rows.add(firstRow);
        ERow secondRow = mockERow(cellsSize, true, "SameKey");
        rows.add(secondRow);
        when(firstRow.equals(secondRow)).thenReturn(false);
        when(secondRow.equals(firstRow)).thenReturn(false);
        ESheet sheet = new ExcelSheet(sheetName, headers, rows, keyColumns);

    }

    @Test
    public void createESheetWithSameRowsWithSameKeysTest() {
//        List<ECell> cells;
//        ERow eRow;
//        ECell cell1 = mock(ECell.class);
//        ECell cell2 = mock(ECell.class);
//        ECell cell3 = mock(ECell.class);
//        ECell cell4 = mock(ECell.class);
//        ECell cell5 = mock(ECell.class);
//        when(cell1.getValue()).thenReturn("C1");
//        when(cell2.getValue()).thenReturn("C2");
//        when(cell3.getValue()).thenReturn("C3");
//        when(cell4.getValue()).thenReturn("C4");
//        when(cell5.getValue()).thenReturn("C5");
//
//        cells = Arrays.asList(cell1, cell2, cell3, cell4, cell5);

//        rows.clear();
//        ERow firstRow = new ExcelRow(cells);
//        rows.add(firstRow);
//        ERow secondRow = new ExcelRow(cells);
//        rows.add(secondRow);

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        rows.add(firstRow);
        ERow secondRow = mockERow(cellsSize, true, "SameKey");
        rows.add(secondRow);
        when(firstRow.equals(secondRow)).thenReturn(true);
        when(secondRow.equals(firstRow)).thenReturn(true);
        System.out.println("Rows size is: " + rows.size());
        System.out.println(firstRow.equals(secondRow));
        System.out.println(secondRow.equals(firstRow));
        ESheet sheet = new ExcelSheet(sheetName, headers, rows, keyColumns);
        System.out.println(sheet.getData().size());
        Assert.assertEquals(sheet.getData().size(), 1);

    }


    public ERow mockERow(int size, boolean containsCells, String key) {
        ERow row = mock(ERow.class);
        when(row.containsCells()).thenReturn(containsCells);
        when(row.getSize()).thenReturn(size);
        when(row.getKey(keyColumns)).thenReturn(key);

        return row;
    }


}
