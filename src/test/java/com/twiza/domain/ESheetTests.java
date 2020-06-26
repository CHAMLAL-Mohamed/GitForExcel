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
@PrepareForTest(ERow.class)

@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class ESheetTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    private final static String HEADERS_INCORRECT_SIZE_EXCEPTION_MESSAGE = "Headers size is different than inserted rows size";
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

    //Construction Tests
    @Test
    public void ESheetWithHeadersWithDiffRowsAndDiffKeysTest() {
        for (int i = 0; i < 5; i++) {
            rows.add(mockERow(cellsSize, true, "key" + i));
        }
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        ESheet sheet = new ExcelSheet(sheetName, headers, rows, keyColumns);

        Assert.assertEquals(sheet.getData().size(), rows.size());
    }

    @Test
    public void ESheetWithHeadersWithDiffRowsAndSameKeysTest() {
        exceptionRule.expect(UnsupportedOperationException.class);

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        ERow secondRow = mockERow(cellsSize, true, "SameKey");
        rows.add(firstRow);
        rows.add(secondRow);
        when(firstRow.equals(secondRow)).thenReturn(false);
        when(secondRow.equals(firstRow)).thenReturn(false);
        new ExcelSheet(sheetName, headers, rows, keyColumns);
    }

    @Test
    public void ESheetWithHeadersWithSameRowsAndSameKeysTest() {

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        ERow secondRow = mockERow(cellsSize, true, "SameKey");
        rows.add(firstRow);
        rows.add(secondRow);
        when(firstRow.equals(secondRow)).thenReturn(true);
        when(secondRow.equals(firstRow)).thenReturn(true);
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        ESheet sheet = new ExcelSheet(sheetName, headers, rows, keyColumns);
        Assert.assertEquals(sheet.getData().size(), 1);
    }

    @Test
    public void ESheetWithRowsSizeDiffFromHeadersSizeTest() {
        exceptionRule.expect(UnsupportedOperationException.class);
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        ERow row = mockERow(cellsSize - 1, true, "row1key");
        rows.add(row);
        new ExcelSheet(sheetName, headers, rows);
    }

    @Test
    public void ESheetWithDiffRowsWithDiffSizesTest() {
        exceptionRule.expect(UnsupportedOperationException.class);

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        ERow secondRow = mockERow(cellsSize - 1, true, "secondRowKey");
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        rows.add(firstRow);
        rows.add(secondRow);

        new ExcelSheet(sheetName, headers, rows);
    }

    @Test
    public void ESheetWithoutHeadersAndWithDiffRowsWithDiffSizesTest() {
        exceptionRule.expect(UnsupportedOperationException.class);

        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        ERow secondRow = mockERow(cellsSize - 1, true, "secondRowKey");
        rows.add(firstRow);
        rows.add(secondRow);

        new ExcelSheet(sheetName, null, rows);
    }

    @Test
    public void ESheetWithOnlyHeadersTest() {
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);

        ESheet sheet = new ExcelSheet(sheetName, headers);
        Assert.assertEquals(cellsSize, sheet.getColumnsNumber());
    }


    //Modification

    @Test
    public void setHeadersToEmptySheetTest() {
        ESheet sheet = new ExcelSheet(sheetName);
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        sheet.assignHeaders(headers);
        Assert.assertTrue(headers == sheet.getHeaders()
                                  && sheet.getColumnsNumber() == cellsSize);
    }

    @Test
    public void setHeadersToSheetWithRowsWithSameSizeTest() {
        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        rows.add(firstRow);

        ESheet sheet = new ExcelSheet(sheetName, null, rows);

        sheet.assignHeaders(headers);
        Assert.assertTrue(headers == sheet.getHeaders()
                                  && sheet.getColumnsNumber() == cellsSize);
    }

    @Test
    public void setHeadersToSheetWithRowsWithDifferentSizeTest() {
        ERow firstRow = mockERow(cellsSize - 1, true, "SameKey");
        when(headers.getSize()).thenReturn(cellsSize);
        when(headers.containsCells()).thenReturn(true);
        rows.add(firstRow);

        ESheet sheet = new ExcelSheet(sheetName, null, rows);
        exceptionRule.expect(UnsupportedOperationException.class);
        exceptionRule.expectMessage(HEADERS_INCORRECT_SIZE_EXCEPTION_MESSAGE);
        sheet.assignHeaders(headers);
    }


    @Test
    public void addDiffRowsWithDiffKeysToEmptySheetTest() {
        ESheet sheet = new ExcelSheet(sheetName);
        for (int i = 0; i < 5; i++) {
            rows.add(mockERow(cellsSize, true, "key" + i));
            sheet.addRow(rows.get(i));
        }
        Assert.assertEquals(sheet.getData().size(), 5);
    }

    @Test
    public void addDiffRowsWithSameKeysToEmptySheetTest() {

        ESheet sheet = new ExcelSheet(sheetName);
        ERow firstRow = mockERow(cellsSize, true, "SameKey");
        ERow secondRow = mockERow(cellsSize, true, "SameKey");

        when(firstRow.equals(secondRow)).thenReturn(false);
        when(secondRow.equals(firstRow)).thenReturn(false);
        sheet.addRow(firstRow);

        exceptionRule.expect(UnsupportedOperationException.class);
        sheet.addRow(secondRow);
    }

    @Test
    public void addDifferentRowsWithDifferentSizesTest() {
        ESheet sheet = new ExcelSheet(sheetName);
        ERow firstRow = mockERow(cellsSize, true, "key1");
        ERow secondRow = mockERow(cellsSize - 1, true, "Key2");

        when(firstRow.equals(secondRow)).thenReturn(false);
        when(secondRow.equals(firstRow)).thenReturn(false);
        sheet.addRow(firstRow);

        exceptionRule.expect(UnsupportedOperationException.class);
        sheet.addRow(secondRow);
    }

    @Test
    public void addRowsWithDifferentSizeThanHeadersTest() {
        ERow firstRow = mockERow(cellsSize - 1, true, "SameKey");
        when(headers.getSize()).thenReturn(cellsSize);

        ESheet sheet = new ExcelSheet(sheetName, headers);
        exceptionRule.expect(UnsupportedOperationException.class);
        sheet.addRow(firstRow);
    }


    public ERow mockERow(int size, boolean containsCells, String key) {
        ERow row = mock(ExcelRow.class);
        when(row.containsCells()).thenReturn(containsCells);
        when(row.getSize()).thenReturn(size);
        when(row.getKey(keyColumns)).thenReturn(key);
        return row;
    }


}
