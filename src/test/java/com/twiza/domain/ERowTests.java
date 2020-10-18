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
public class ERowTests {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    /**
     * Constant that holds the size of the cells list.
     */
    static final int CELLS_LIST_SIZE = 5;
    /**
     * list of mocked list
     */
    List<ECell> cells = new ArrayList<>();

    @Before
    public void Setup() {
        cells = mockCells(CELLS_LIST_SIZE);
    }

    public List<ECell> mockCells(int cellsSize) {
        List<ECell> cells = new ArrayList<>();
        for (int i = 0; i < cellsSize; i++) {
            ECell cell = mock(ECell.class);
            when(cell.getValue()).thenReturn("cellValue" + (i));
            cells.add(cell);
        }
        return cells;
    }

    //Construction test
    @Test
    public void rowThrowsNullExceptionIfNullIsProvidedDuringConstruction() {
        exceptionRule.expect(NullPointerException.class);
        new ExcelRow((List<ECell>) null);
    }

    @Test
    public void rowIsCreatedWithEmptyInitialListIfEmptyConstructorIsUsed() {
        ERow row = new ExcelRow();
        Assert.assertTrue(row.getCells().isEmpty());
    }

    @Test
    public void rowIsCreatedWithAListOfCellsIfItWasProvidedToConstructorWithArgs() {
        ERow row = new ExcelRow(cells);
        Assert.assertEquals(row.getCells(), cells);
    }


    /**
     * <code>getKey()</code> tests
     */
    @Test
    public void getKeyThrowExceptionIfKeyIsEmpty() {
        exceptionRule.expect(UnsupportedOperationException.class);
        ECell cell = mock(ECell.class);
        when(cell.getValue()).thenReturn("");
        cells.add(0, cell);
        ERow row = new ExcelRow(cells);
        row.getKey(0);
    }

    @Test
    public void getKeyThrowExceptionIfProvidedIndexesAreOutOfRange() {
        exceptionRule.expect(IndexOutOfBoundsException.class);
        ERow row = new ExcelRow(cells);
        row.getKey(cells.size());
    }

    @Test
    public void generateKeyIfOneIndexOfNonEmptyCellIsProvided() {
        exceptionRule.expect(IndexOutOfBoundsException.class);
        ERow row = new ExcelRow(cells);
        row.getKey(cells.size());
    }

    @Test
    public void generateKeyIfAtLeastOneIndexIsOfANonEmptyCell() {
        ERow row = new ExcelRow(cells);
        ECell cell = mock(ECell.class);
        when(cell.getValue()).thenReturn("");
        cells.add(0, cell);
        //index 0 contains empty cell, but index 1 contains a non empty cell
        Assert.assertFalse(row.getKey(0, 1).isEmpty());
    }
    //test replaceCell

    @Test
    public void replaceCellIfProvidedIndexIsInTheRangeAndCellIsNotNull() {
        int positionToReplace = 0;
        ERow row = new ExcelRow(cells);
        ECell cell = mock(ECell.class);
        when(cell.getValue()).thenReturn("anyValue");
        row.replaceCell(positionToReplace, cell);
        Assert.assertEquals(cell, row.getCell(positionToReplace));
    }

    @Test(expected = NullPointerException.class)
    public void replaceCellThrowNullExceptionIfProvidedCellIsNull() {
        int positionToReplace = 0;
        ERow row = new ExcelRow(cells);
        row.replaceCell(positionToReplace, null);
    }

    @Test
    public void compareReturnsTheCurrentRowIfProvidedRowIsNull() {
        ERow row = new ExcelRow(cells);
        ERow diffRow = row.compare(null);
        Assert.assertEquals(row, diffRow);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void compareThrowsUnsupportedExceptionIfProvidedRowSizeDoesNotMatchCurrentRowSize() {
        ERow row = new ExcelRow(cells);
        cells.remove(0);
        ERow oldRow = new ExcelRow(cells);
        row.compare(oldRow);
    }

//    @Test
//    public void compareReturnsRowEqualToTheOldRowIfNoChangesAreMade() {
//        List<ECell> oldCells = mockCells(CELLS_LIST_SIZE);
//        List<ECell> newCells = mockCells(CELLS_LIST_SIZE);
//        newCells.forEach(cell -> when(cell.updateValue(any())).thenReturn(null));
//        ERow oldRow = new ExcelRow(oldCells);
//        ERow newRow = new ExcelRow(oldCells);
//        ERow diffRow = newRow.compare(oldRow);
//        assert
//
//    }

}
