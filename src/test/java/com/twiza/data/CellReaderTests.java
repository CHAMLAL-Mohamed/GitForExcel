package com.twiza.data;

import com.twiza.domain.ECell;
import com.twiza.domain.Status;
import org.apache.poi.ss.usermodel.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
public class CellReaderTests {

    DataFormatter dataFormatter;

    FormulaEvaluator formulaEvaluator;

    @Test
    public void readingCellValueTest() {
        Cell cell = mock(Cell.class);
        Sheet sheet = mock(Sheet.class);
        Workbook workbook = mock(Workbook.class);
        dataFormatter = mock(DataFormatter.class);
        formulaEvaluator = mock(FormulaEvaluator.class);
        CreationHelper creationHelper = mock(CreationHelper.class);
        cell.setCellValue("TestOK");
        when(cell.getSheet()).thenReturn(sheet);
        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.getCreationHelper()).thenReturn(creationHelper);
        when(creationHelper.createFormulaEvaluator()).thenReturn(formulaEvaluator);
        when(dataFormatter.formatCellValue(cell, formulaEvaluator)).thenReturn("TestOK");

        ECell eCell = CellReader.getInstance(dataFormatter).read(cell);

        Assert.assertEquals("TestOK", eCell.getValue());
    }

    @Test
    public void readingCellStatusTest() {
        Cell cell = mock(Cell.class);
        Sheet sheet = mock(Sheet.class);
        Workbook workbook = mock(Workbook.class);
        dataFormatter = mock(DataFormatter.class);
        formulaEvaluator = mock(FormulaEvaluator.class);
        CreationHelper creationHelper = mock(CreationHelper.class);
        cell.setCellValue("TestOK");
        when(cell.getSheet()).thenReturn(sheet);
        when(sheet.getWorkbook()).thenReturn(workbook);
        when(workbook.getCreationHelper()).thenReturn(creationHelper);
        when(creationHelper.createFormulaEvaluator()).thenReturn(formulaEvaluator);
        formulaEvaluator.evaluate(cell);
        when(dataFormatter.formatCellValue(cell, formulaEvaluator)).thenReturn("TestOK");

        ECell eCell = CellReader.getInstance(dataFormatter).read(cell);

        Assert.assertEquals(Status.NEW, eCell.getStatus());
    }
}
