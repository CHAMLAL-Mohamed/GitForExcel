package com.twiza.domain;

import com.twiza.exceptions.SheetWithInconsistentDataException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ERow.class)

@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class ESheetTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    List<ERow> rows;
    List<String> headers;
    Integer[] keyColumns;
    String sheetName = "Sheet";
    int cellsSize = 6;

    @Before
    public void setup() {
        headers = setupHeaders(cellsSize);
        keyColumns = new Integer[]{0};
        rows = new ArrayList<>();
    }

    public List<String> setupHeaders(int size) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            headers.add("Header" + (i + 1));
        }
        return headers;
    }

    //Construction Tests
    @Test(expected = NullPointerException.class)
    public void sheetThrowsNullExceptionIfNameIsNull() {
        new ExcelSheet(null);
    }

    @Test
    public void sheetIsCreatedIfEmptyRowsAndNullHeader() {
        ESheet sheet = new ExcelSheet(sheetName);
        Assert.assertEquals(sheet.getName(), sheetName.toLowerCase());
        Assert.assertNull(sheet.getHeaders());
        Assert.assertTrue(sheet.getData().isEmpty());
    }

    @Test
    public void sheetIsCreatedIfHeadersAndEmptyRows() {
        ESheet sheet = new ExcelSheet(sheetName, headers);
        Assert.assertEquals(headers, sheet.getHeaders());
    }

    @Test
    public void sheetIsCreatedIfHeaderIsNullAndRows() {
        rows.add(mockERow(cellsSize));
        ESheet sheet = new ExcelSheet(sheetName, rows, null);
        Assert.assertEquals(sheet.getRowsNumber(), rows.size());
        Assert.assertEquals(sheet.getColumnsNumber(), cellsSize);

    }

    @Test(expected = NullPointerException.class)
    public void sheetThrowsNullExceptionIfProvidedRowsAreNull() {
        new ExcelSheet(sheetName, null, null);
    }

    @Test
    public void sheetIsCreatedIfHeaderAndRowsHavingSameSize() {
        rows.add(mockERow(cellsSize));
        ESheet sheet = new ExcelSheet(sheetName, rows, headers);
        Assert.assertEquals(sheet.getColumnsNumber(), cellsSize);
        Assert.assertEquals(sheet.getData(), rows);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void sheetThrowUnsupportedIfRowsSizeBiggerThanHeadersSize() {
        rows.add(mockERow(cellsSize + 1));
        new ExcelSheet(sheetName, rows, headers);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void sheetThrowUnsupportedIfRowsSizeLessThanHeadersSize() {
        rows.add(mockERow(cellsSize - 1));
        new ExcelSheet(sheetName, rows, headers);
    }

    @Test
    public void sheetCreatedIfKeyIndexesInTheRange() {
        rows.add(mockERow(cellsSize));
        rows.add(mockERow(cellsSize));
        rows.add(mockERow(cellsSize));
        ESheet sheet = new ExcelSheet(sheetName, rows, headers, 0, cellsSize - 1);
        Assert.assertEquals(sheet.getKeysIndexes()[0], 0);
        Assert.assertEquals(sheet.getKeysIndexes()[1], cellsSize - 1);

    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void sheetThrowOutOfBoundExceptionIfKeyIndexesAreOutOfRange() {
        rows.add(mockERow(cellsSize));
        rows.add(mockERow(cellsSize));
        rows.add(mockERow(cellsSize));
        new ExcelSheet(sheetName, rows, headers, 0, cellsSize);
    }

    @Test(expected = NullPointerException.class)
    public void addRowThrowsNullExceptionIfProvidedRowIsNull() {
        ESheet sheet = new ExcelSheet(sheetName, headers);
        sheet.addRow(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addRowThrowsUnsupportedOperationExceptionIfProvidedRowSizeIsDifferentThanPreviousRowsSize() {
        rows.add(mockERow(cellsSize));
        ERow row = mockERow(cellsSize - 1);
        ESheet sheet = new ExcelSheet(sheetName, rows, headers);
        sheet.addRow(row);
    }

    /**
     * @note sheet size means the size of previous entered headers/rows
     */
    @Test
    public void addRowSuccessfullyIfProvidedRowIsNotNullAndItsSizeEqualsSheetSize() {
        rows.add(mockERow(cellsSize));
        ERow row = mockERow(cellsSize);
        int rowsSize = rows.size();
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).addRow(row);
        Assert.assertEquals(sheet.getRowsNumber(), rowsSize + 1);
        Assert.assertEquals(sheet.getRow(rowsSize), row);
    }

    @Test
    public void addRowSuccessfullyIfProvidedRowIsNotNullAndItsSizeEqualsSheetSizeAndHeaderIsNull() {
        rows.add(mockERow(cellsSize));
        ERow row = mockERow(cellsSize);
        int rowsSize = rows.size();
        ESheet sheet = new ExcelSheet(sheetName, rows, null).addRow(row);
        Assert.assertEquals(sheet.getRowsNumber(), rowsSize + 1);
        Assert.assertEquals(sheet.getRow(rowsSize), row);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addRowWithPositionThrowsOutOfBoundExceptionIfIndexIsOutOfRange() {
        rows.add(mockERow(cellsSize));
        ERow row = mockERow(cellsSize);
        new ExcelSheet(sheetName, rows, null).addRow(rows.size() + 1, row);
    }

    @Test
    public void addRowWithPositionSuccessfullyIfIndexIsInRange() {
        rows.add(mockERow(cellsSize));
        ERow row = mockERow(cellsSize);
        int newRowPosition = rows.size() - 1;
        ESheet sheet = new ExcelSheet(sheetName, rows, null).addRow(newRowPosition, row);
        Assert.assertEquals(row, sheet.getData().get(newRowPosition));
    }
//TODO: add tests for addRow with blanks

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteRowWithIndexThrowsOutOfBoundExceptionIfTheProvidedIndexIsOutOfRange() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteRows(rowsSize);
    }

    @Test
    public void deleteRowWithIndexSuccessfullyIfTheProvidedIndexIsInOfRange() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteRows(rowsSize - 1);
        Assert.assertEquals(rowsSize - 1, sheet.getRowsNumber());
    }

    @Test(expected = NullPointerException.class)
    public void deleteRowWithRowThrowNullPointExceptionIfTheProvidedRowIsNull() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteRow(null);
    }

    @Test
    public void deleteRowWithRowSuccessfullyIfTheProvidedRowIsNotNullAndNotInSheet() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        ERow row = mockERow(cellsSize);
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteRow(row);
        Assert.assertEquals(rows, sheet.getData());
    }

    @Test
    public void deleteRowWithRowSuccessfullyIfTheProvidedRowIsNotNullAndInSheet() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteRow(rows.get(0));
        Assert.assertNotEquals(rows.get(0), sheet.getData().get(0));
        Assert.assertEquals(rowsSize - 1, sheet.getRowsNumber());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteRowsRangeThrowsOutOfBoundExceptionIfBeginIndexIsBiggerThanEndIndex() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteRowsRange(2, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteRowsRangeThrowsOutOfBoundExceptionIfBeginIndexIsNegative() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteRowsRange(-1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteRowsRangeThrowsOutOfBoundExceptionIfEndIndexIsBiggerOrEqualRowsSize() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteRowsRange(0, rowsSize);
    }

    @Test
    public void deleteRowsRangeSuccessfullyIfIndexesInRange() {
        int rowsSize = 5;
        int beginIndex = 2;
        int endIndex = 3;
        rows = getRealRows(rowsSize);
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteRowsRange(beginIndex, endIndex);
        for (int i = endIndex; i >= beginIndex; i--) {
            rows.remove(i);
        }
        Assert.assertEquals(rows, sheet.getData());
    }

    //TODO: add tests for addColumn (all of them are still not tested)

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteColumnWithHeadersThrowsOutOfBoundExceptionIfProvidedIndexIsOutOfRange() {
        int rowsSize = 5;
        rows.clear();
        int columnsToDelete = cellsSize;
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize, columnsToDelete));
        }
        new ExcelSheet(sheetName, rows, headers).deleteColumns(cellsSize + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteColumnWithoutHeadersThrowsOutOfBoundExceptionIfProvidedIndexIsOutOfRange() {
        int rowsSize = 5;
        int columnsToDelete = cellsSize;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize, columnsToDelete));
        }
        new ExcelSheet(sheetName, rows, null).deleteColumns(cellsSize);
    }

    @Test
    public void deleteColumnWithHeadersSuccessfullyIfProvideIndexIsInRange() {
        int rowsSize = 5;
        int columnsToDelete = cellsSize - 1;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize, columnsToDelete));
        }
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteColumns(columnsToDelete);
        Assert.assertEquals(cellsSize - 1, sheet.getColumnsNumber());
    }

    @Test
    public void deleteColumnWithNullHeadersSuccessfullyIfProvideIndexIsInRange() {
        int rowsSize = 5;
        int columnsToDelete = cellsSize - 1;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize, columnsToDelete));
        }
        ESheet sheet = new ExcelSheet(sheetName, rows, null).deleteColumns(columnsToDelete);
        Assert.assertEquals(cellsSize - 1, sheet.getColumnsNumber());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteColumnsRangeThrowsOutOfBoundExceptionIfBeginIndexIsBiggerThanEndIndex() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteColumnRange(2, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteColumnsRangeThrowsOutOfBoundExceptionIfBeginIndexIsNegative() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteColumnRange(-1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void deleteColumnsRangeThrowsOutOfBoundExceptionIfEndIndexIsBiggerOrEqualColumnsSize() {
        int rowsSize = 5;
        rows.clear();
        for (int i = 0; i < rowsSize; i++) {
            rows.add(mockERow(cellsSize));
        }
        new ExcelSheet(sheetName, rows, headers).deleteColumnRange(0, cellsSize);
    }

    @Test
    public void deleteColumnsRangeSuccessfullyIfIndexesInRange() {
        int rowsSize = 5;
        int beginIndex = 1;
        int endIndex = 3;
        rows = getRealRows(rowsSize);
        List<ERow> checkRows = getRealRows(rowsSize);
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).deleteColumnRange(beginIndex, endIndex);
        checkRows.forEach(row -> {
            for (int i = endIndex; i >= beginIndex; i--) {
                row.removeCell(i);
            }
        });

        Assert.assertEquals(checkRows, sheet.getData());
    }

    @Test(expected = NullPointerException.class)
    public void setKeyIndexesThrowNullPointExceptionIfProvidedArrayIsNull() {
        rows.add(mockERow(cellsSize));
        new ExcelSheet(sheetName, rows, headers).setKeyIndexes((int[]) null);

    }

    @Test
    public void setKeyIndexesThrowOutOfBoundExceptionIfProvidedArrayContainsElementOutOfRange() {
        exceptionRule.expect(IndexOutOfBoundsException.class);
        exceptionRule.expectMessage("The provided index is bigger that sheet size(");
        rows.add(mockERow(cellsSize));
        int[] keyIndexes = new int[]{cellsSize - 1, cellsSize};
        new ExcelSheet(sheetName, rows, headers).setKeyIndexes(keyIndexes);
    }

    @Test
    public void setKeyIndexesSuccessfullyIfProvidedArrayElementsAreInRange() {
        rows.add(mockERow(cellsSize));
        int[] keyIndexes = new int[]{cellsSize - 1, cellsSize - 2};
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).setKeyIndexes(keyIndexes);
        Assert.assertArrayEquals(keyIndexes, sheet.getKeysIndexes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setHeadersThrowUnsupportedExceptionIfTheProvidedWasNullAndHeadersSizeIsDifferentThanSheetSize() {
        rows.add(mockERow(cellsSize));
        List<String> newHeaders = setupHeaders(cellsSize - 1);
        new ExcelSheet(sheetName, rows, null).setHeaders(newHeaders);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setHeadersThrowUnsupportedExceptionIfTheProvidedWasNotNullAndHeadersSizeIsDifferentThanSheetSize() {
        rows.add(mockERow(cellsSize));
        List<String> newHeaders = setupHeaders(cellsSize - 1);
        new ExcelSheet(sheetName, rows, headers).setHeaders(newHeaders);
    }

    @Test
    public void setHeadersSuccessfullyIfTheProvidedWasNullAndHeadersSizeIsTheSameAsSheetSize() {
        rows.add(mockERow(cellsSize));
        List<String> newHeaders = setupHeaders(cellsSize);
        ESheet sheet = new ExcelSheet(sheetName, rows, null).setHeaders(newHeaders);
        Assert.assertEquals(newHeaders, sheet.getHeaders());
    }

    @Test
    public void setHeadersSuccessfullyIfTheProvidedWasNotNullAndHeadersSizeIsTheSameAsSheetSize() {
        rows.add(mockERow(cellsSize));
        List<String> newHeaders = setupHeaders(cellsSize);
        ESheet sheet = new ExcelSheet(sheetName, rows, headers).setHeaders(newHeaders);
        Assert.assertEquals(newHeaders, sheet.getHeaders());
    }

    /**
     * the magic began here
     * Testing all possibility of <code>getUniqueData()</code>
     * use real {@link ERow} for testing equality.
     */
    @Test
    public void getUniqueDataSuccessfullyIfProvidedRowsHaveDifferentKeys() {
        rows.clear();
        int rowsSize = 5;
        Map<String, ERow> keysValues = new LinkedHashMap<>();
        for (int i = 0; i < rowsSize; i++) {
            ERow row = mockERow(cellsSize, "Key" + i);
            rows.add(row);
            keysValues.put(("Key" + i), row);
        }
        Map<String, ERow> uniqueData = new ExcelSheet(sheetName, rows, headers).getUniqueData();
        Assert.assertEquals(keysValues, uniqueData);
    }

    @Test
    public void getUniqueDataSuccessfullyIfProvidedRowsContainsRowsWithSameKeyAndAreEqual() {
        int rowsSize = 5;
        rows.clear();
        rows.addAll(getRealRows(rowsSize));
        Map<String, ERow> keysValues = new LinkedHashMap<>();
        for (int i = 0; i < rowsSize; i++) {
            keysValues.put(rows.get(i).getKey(0), rows.get(i));
        }
        rows.add(rows.get(0));
        Map<String, ERow> uniqueData = new ExcelSheet(sheetName, rows, headers).getUniqueData();
        Assert.assertEquals(keysValues, uniqueData);
    }

    @Test(expected = SheetWithInconsistentDataException.class)
    public void getUniqueDataThrowsInconsistentDataExceptionIfProvidedRowsContainsRowsWithSameKeyAndAreNotEqual() {
        int rowsSize = 5;
        rows.clear();
        rows.addAll(getRealRows(rowsSize));
        List<ECell> cells = new ArrayList<>();
        for (int j = 0; j < cellsSize; j++) {
            cells.add(new ExcelCell("value" + 1 + j));
        }
        ERow row = new ExcelRow(cells);
        row.getCell(2).updateValue("newValue");
        rows.add(row);
        new ExcelSheet(sheetName, rows, headers).setKeyIndexes(0).getUniqueData();
    }


    public ERow mockERow(int size, String key) {
        ERow row = mockERow(size);
        when(row.getKey(anyInt())).thenReturn(key);
        return row;
    }

    public ERow mockERow(int size, int columnToDelete) {
        ERow row = mockERow(size);
        when(row.removeCell(columnToDelete)).then(removeCellAnswer);
        return row;
    }

    //todo improve mocking to include all special cases
    public ERow mockERow(int size) {
        ERow row = mock(ExcelRow.class);
        when(row.getSize()).thenReturn(size);
        return row;
    }

    public List<ERow> getRealRows(int rowsSize) {
        List<ERow> rows = new ArrayList<>();
        for (int i = 0; i < rowsSize; i++) {
            List<ECell> cells = new ArrayList<>();
            for (int j = 0; j < cellsSize; j++) {
                cells.add(new ExcelCell("value" + i + j));
            }
            rows.add(new ExcelRow(cells));
        }
        return rows;
    }


    Answer<Void> removeCellAnswer = invocation -> {
        int position = invocation.getArgument(0);
        ERow row = (ERow) invocation.getMock();
        int current = row.getSize();
        if (position >= row.getSize()) {
            throw new IndexOutOfBoundsException();
        } else {
            when(row.getSize()).thenReturn(current - 1);
        }
        return null;
    };


}
