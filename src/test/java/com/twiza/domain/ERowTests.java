package com.twiza.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class ERowTests {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    List<ECell> cells;
    ERow eRow;

    @Before
    public void Setup() {
        ECell cell1 = mock(ECell.class);
        ECell cell2 = mock(ECell.class);
        ECell cell3 = mock(ECell.class);
        ECell cell4 = mock(ECell.class);
        ECell cell5 = mock(ECell.class);
        when(cell1.getValue()).thenReturn("C1");
        when(cell2.getValue()).thenReturn("C2");
        when(cell3.getValue()).thenReturn("C3");
        when(cell4.getValue()).thenReturn("C4");
        when(cell5.getValue()).thenReturn("C5");

        cells = Arrays.asList(cell1, cell2, cell3, cell4, cell5);
        eRow = new ExcelRow(cells);
    }

    @Test
    public void getSimpleIdTest() {

        int index = 1;
        Assert.assertEquals("C2", eRow.getKey(index));
    }

    @Test
    public void getCompositeIdTest() {
        Integer[] idColumns = {0, 1, 2};
        Assert.assertEquals("C1/C2/C3", eRow.getKey(idColumns));
    }

    @Test
    public void getKeyThrowOutOfBoundsExceptionWithNegativeIndexTest() {
        exceptionRule.expect(ArrayIndexOutOfBoundsException.class);
        Integer[] idColumns = {0, 1, -1};
        eRow.getKey(idColumns);
    }

    @Test
    public void getKeyThrowArrayOutOfBoundsExceptionWithBiggerIndexTest() {
        exceptionRule.expect(ArrayIndexOutOfBoundsException.class);
        Integer[] idColumns = {0, 1, cells.size()};
        eRow.getKey(idColumns);
    }


    @Test
    public void getCellWithinBoundsTest() {
        Assert.assertNotNull(eRow.getCell(1));
    }

    @Test
    public void getCellOutsideBoundsTest() {
        exceptionRule.expect(ArrayIndexOutOfBoundsException.class);
        eRow.getCell(cells.size());
    }

    @Test
    public void updateExistingCellValueWithDifferentValueTest() {
        int testIndex = 1;
        ECell cell = cells.get(testIndex);
        String oldValue = cell.getValue();
        when(cell.updateValue("DiffValue"))
                .thenReturn(oldValue);
        boolean isUpdated = eRow.updateCellValue(testIndex, "DiffValue");
        Assert.assertTrue(isUpdated);
    }

    @Test
    public void updateExistingCellValueWithTheSameValueTest() {
        int testIndex = 1;
        ECell cell = cells.get(testIndex);
        String oldValue = cell.getValue();
        when(cell.updateValue(oldValue))
                .thenReturn(null);
        boolean isUpdated = eRow.updateCellValue(testIndex, "DiffValue");
        Assert.assertFalse(isUpdated);
    }


    @Test
    public void updateCellValueThrowOutOfBoundsExceptionTest() {
        exceptionRule.expect(ArrayIndexOutOfBoundsException.class);
        eRow.updateCellValue(cells.size(), "DiffValue");

    }

    @Test
    public void equalsRowsTest() {
        ERow row1 = new ExcelRow(cells);
        ERow row2 = new ExcelRow(cells);

        Assert.assertEquals(row1, row2);
    }

    @Test
    public void notEqualsRowsTest() {
        ERow row1 = new ExcelRow(cells);
        ERow row2 = new ExcelRow(cells.subList(0, cells.size() - 1));
        Assert.assertNotEquals(row1, row2);
    }

    @Test
    public void diffRowsObjectWithSameContentTest() {
        ERow row1 = new ExcelRow(cells);
        ERow row2 = new ExcelRow(cells);
        Assert.assertTrue(row1.equals(row2) && row2.equals(row1));
    }


}
