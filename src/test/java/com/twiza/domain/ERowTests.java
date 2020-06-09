package com.twiza.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
    List<ECell> cells;

    @Before
    public void Setup() {
        ECell cell1 = mock(ECell.class);
        ECell cell2 = mock(ECell.class);
        ECell cell3 = mock(ECell.class);
        ECell cell4 = mock(ECell.class);
        ECell cell5 = mock(ECell.class);
        when(cell1.getValue()).thenReturn("E1");
        when(cell2.getValue()).thenReturn("E2");
        when(cell3.getValue()).thenReturn("E3");
        when(cell4.getValue()).thenReturn("E4");
        when(cell5.getValue()).thenReturn("E5");

        cells = Arrays.asList(cell1, cell2, cell3, cell4, cell5);
    }

    @Test
    public void getSimpleIdTest() {
        ERow eRow = new ExcelRow(cells);
        int index = 1;
        Assert.assertEquals("E2", eRow.getId(index));
    }

    @Test
    public void getCompositeIdTest() {
        ERow eRow = new ExcelRow(cells);
        Integer[] idColumns = {0, 1, 2};
        Assert.assertEquals("E1/E2/E3", eRow.getId(idColumns));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ThrowArrayIndexExceptionWithNegativeIndexTest() {
        ERow eRow = new ExcelRow(cells);
        Integer[] idColumns = {0, 1, -1};
        eRow.getId(idColumns);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void ThrowArrayIndexExceptionWithIndexBiggerThanSizeOfListTest() {
        ERow eRow = new ExcelRow(cells);
        Integer[] idColumns = {0, 1, cells.size()};
        eRow.getId(idColumns);
    }
}
