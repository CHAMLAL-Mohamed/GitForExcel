package com.twiza.domain;

import org.junit.Assert;
import org.junit.Test;

public class ECellTests {

    String expectedValue = "expectedValue";
    String oldValue = "oldValue";

    @Test(expected = NullPointerException.class)
    public void throwNullExceptionDuringConstructionTest() {
        ECell cell = new ExcelCell(null);
    }


    @Test
    public void getValueIsNotNullTest() {
        ECell cell = new ExcelCell(expectedValue);
        Assert.assertEquals(expectedValue, cell.getValue());
    }


    @Test(expected = NullPointerException.class)
    public void updateValueWithNullValueTest() {
        ECell cell = new ExcelCell("anyValue");
        cell.updateValue(null);
    }


    @Test
    public void updateValueWithNonNullValueTest() {
        ECell cell = new ExcelCell(oldValue);
        cell.updateValue(expectedValue);
        Assert.assertEquals(expectedValue, cell.getValue());
    }

    @Test
    public void updateValueWithTheSameValueTest() {
        ECell cell = new ExcelCell(oldValue);
        Assert.assertNull(cell.updateValue(oldValue));
    }

    @Test
    public void updateValueReturnOldValueTest() {
        ECell cell = new ExcelCell(oldValue);
        Assert.assertEquals(oldValue, cell.updateValue("anyValue"));
    }


    @Test
    public void updateValueChangeStatusTest() {
        ECell cell = new ExcelCell(oldValue);
        cell.updateValue(expectedValue);
        Assert.assertEquals(Status.CHANGED, cell.getStatus());
    }


}
