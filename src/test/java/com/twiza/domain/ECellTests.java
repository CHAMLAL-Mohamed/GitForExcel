package com.twiza.domain;

import com.twiza.exceptions.UnsupportedStatusChangeException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ECellTests {

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String expectedValue = "expectedValue";

    /**
     * Test constructors cases
     */
    @Test(expected = NullPointerException.class)
    public void cellThrowsExceptionIfValueIsNull() {
        new ExcelCell((String) null);
    }

    @Test
    public void cellWithNewStatusIfOnlyValueIsProvided() {
        ECell cell = new ExcelCell(expectedValue);
        Assert.assertEquals(Status.NEW, cell.getStatus());
    }

    @Test
    public void cellWithEmptyHistoryIfOnlyValueIsProvided() {
        ECell cell = new ExcelCell(expectedValue);
        Assert.assertTrue(cell.getChangesHistory().isEmpty());
    }

    @Test
    public void cellWithEmptyHistoryIfProvidedChangesHistoryIsNull() {
        ECell cell = new ExcelCell(expectedValue, null);
        Assert.assertTrue(cell.getChangesHistory().isEmpty());
    }

    @Test
    public void cellWithEmptyHistoryIfProvidedInitialHistoryDoesNotObeysStandard() {
        String history = "20201125 10:15:33" + "\t" + "oldValue" + "\t" + "User";// standard is to have date separated with dashes
        ECell cell = new ExcelCell(expectedValue, history);
        Assert.assertTrue(cell.getChangesHistory().isEmpty());
    }

    @Test
    public void cellWithInitialHistoryIfProvidedInitialHistoryObeysStandard() {
        String history = LocalDateTime.now().format(DATE_TIME_FORMATTER) + "\t" + "oldValue" + "\t" + "User";
        ECell cell = new ExcelCell(expectedValue, history);
        Assert.assertEquals(history, cell.getChangesHistory());
    }

    /**
     * End Constructions tests.
     * Start UpdateValue tests
     */

    @Test
    public void valueStatusAndHistoryAreNotUpdatedIfNewValueIsNull() {
        String value = "value";
        ECell cell = new ExcelCell(value);
        String oldValue = cell.updateValue(null);
        Assert.assertTrue(oldValue == null && cell.getValue().equals(value) && cell.getStatus() == Status.NEW && cell.getChangesHistory().isEmpty());
    }

    @Test
    public void valueStatusAndHistoryAreNotUpdatedIfNewValueEqualsCurrentValue() {
        String value = "value";
        ECell cell = new ExcelCell(value);
        String oldValue = cell.updateValue(value);
        Assert.assertTrue(oldValue == null && cell.getValue().equals(value) && cell.getStatus() == Status.NEW && cell.getChangesHistory().isEmpty());
    }

    @Test
    public void valueStatusAndHistoryAreUpdatedIfNewValueIsDifferentThanCurrentValue() {
        String value = "value";
        String newValue = "newValue";
        ECell cell = new ExcelCell(value);
        String oldValue = cell.updateValue(newValue);
        Assert.assertTrue(oldValue.equals(value) && cell.getValue().equals(newValue) && cell.getStatus() == Status.CHANGED && cell.getChangesHistory().contains(value));
    }

    /**
     * end updateValue tests.
     * start setStatus tests.
     */

    @Test
    public void satusIsChangedIfTheCurrentStatusIsNew() {
        ECell cell = new ExcelCell("value");
        cell.setStatus(Status.CHANGED);
        assertThat(cell.getStatus(), is(equalTo(Status.CHANGED)));
    }

    /**
     * this logic applies if we try to change the  {@link Status} of cell from
     * {@code CHANGED},{@code ADDED}, or {@code DELETED} to {@code NEW}.
     */
    @Test(expected = UnsupportedStatusChangeException.class)
    public void throwExceptionIfSetStatusIsFromChangedToNew() {
        ECell cell = new ExcelCell("value");
        cell.setStatus(Status.CHANGED);
        cell.setStatus(Status.NEW);
    }

    /**
     * end setStatus() tests
     * start equals tests
     */
    @Test
    public void cellEqualsIsTrueIfTheTwoCellsHaveTheSameValue() {
        ECell cell1 = new ExcelCell("value");
        ECell cell2 = new ExcelCell("value");
        Assert.assertTrue(cell1.equals(cell2) && cell2.equals(cell1));
    }

    @Test
    public void cellEqualsIsFalseIfTheTwoCellsHaveDifferentValues() {
        ECell cell1 = new ExcelCell("value1");
        ECell cell2 = new ExcelCell("value2");
        Assert.assertFalse(cell1.equals(cell2) || cell2.equals(cell1));
    }

    /**
     * end equals tests
     * start hashCode tests
     */
    @Test
    public void cellHashCodeEqualsIfTheyHaveTheSameValue() {
        ECell cell1 = new ExcelCell("value");
        ECell cell2 = new ExcelCell("value");
        Assert.assertEquals(cell1.hashCode(), cell2.hashCode());
    }

    @Test
    public void cellHashCodeNotEqualsIfTheyHaveTheSameValue() {
        ECell cell1 = new ExcelCell("value1");
        ECell cell2 = new ExcelCell("value2");
        Assert.assertNotEquals(cell1.hashCode(), cell2.hashCode());
    }

}
