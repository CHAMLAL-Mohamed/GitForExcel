package com.twiza.data;

import com.twiza.domain.ECell;
import com.twiza.domain.Status;
import org.apache.poi.ss.usermodel.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Workbook.class, Sheet.class, Cell.class, DataFormatter.class
        , FormulaEvaluator.class, CreationHelper.class})
@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class CellReaderTests {


    static Sheet sheet;
    static Workbook workbook;
    static DataFormatter dataFormatter;
    static FormulaEvaluator formulaEvaluator;
    static CreationHelper creationHelper;

    @BeforeClass
    public static void setup() {
        sheet = mock(Sheet.class);
        workbook = mock(Workbook.class);
        dataFormatter = mock(DataFormatter.class);
        formulaEvaluator = mock(FormulaEvaluator.class);
        creationHelper = mock(CreationHelper.class);
    }

    @Test
    public void readEmptyCellTest() {
        Cell cell = mock(Cell.class);
        when(cell.getSheet()).thenReturn(sheet);
        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.getCreationHelper()).thenReturn(creationHelper);
        when(creationHelper.createFormulaEvaluator()).thenReturn(formulaEvaluator);
        when(dataFormatter.formatCellValue(cell, formulaEvaluator)).thenReturn("");
        ECell eCell = CellReader.getInstance(dataFormatter).read(cell);

        Assert.assertEquals("", eCell.getValue());
    }

    @Test
    public void readingCellStatusTest() {
        Cell cell = mock(Cell.class);
        when(cell.getSheet()).thenReturn(sheet);
        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.getCreationHelper()).thenReturn(creationHelper);
        when(creationHelper.createFormulaEvaluator()).thenReturn(formulaEvaluator);
        when(dataFormatter.formatCellValue(cell, formulaEvaluator)).thenReturn("TestOK");
        ECell eCell = CellReader.getInstance(dataFormatter).read(cell);

        Assert.assertEquals(Status.NEW, eCell.getStatus());
    }

    @AfterClass
    public static void releaseResources() {
        CellReader.releaseResources();
    }

}
