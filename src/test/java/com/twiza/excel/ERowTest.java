package com.twiza.excel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.dom.",
        "com.sun.org.apache.xalan.", "javax.activation.*", "jdk.internal.reflect.*"})
@PrepareForTest(ERow.class)
public class ERowTest {
    int idColumn = 0;

    Row row;

    DataFormatter dataFormatter;

    FormulaEvaluator formulaEvaluator;

    Cell idCell;

    @Before
    public void setup() {
        row = mock(Row.class);
        idCell = mock(Cell.class);
        dataFormatter = mock(DataFormatter.class);
        formulaEvaluator = mock(FormulaEvaluator.class);
    }

    @Test
    public void getIdShouldNotBeNull() {

        when(row.getLastCellNum()).thenReturn((short) 3);
        when(row.getCell(0)).thenReturn(idCell);
        formulaEvaluator.evaluate(idCell);
        when(dataFormatter.formatCellValue(idCell, formulaEvaluator)).thenReturn("ID");
        System.out.println(idCell.getStringCellValue());
        ERow eRow = new ERow(row, dataFormatter, formulaEvaluator);

        Assert.assertNotNull("Id is not null", eRow.getId());
    }

    @Ignore
    @Test
    public void getIdShouldBeNull() {


    }

    @Ignore
    @Test
    public void getElementsShouldNotBeNull() {

    }

    @Ignore
    @Test
    public void getElementsShouldBeNull() {

    }


}


