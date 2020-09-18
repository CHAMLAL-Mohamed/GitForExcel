package com.twiza.domain;

import org.junit.Before;
import org.junit.Rule;
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
     * list of mocked list
     */
    List<ECell> cells = new ArrayList<>();
    int cellsListSize = 5;

    @Before
    public void Setup() {

        for (int i = 0; i < cellsListSize; i++) {
            ECell cell = mock(ECell.class);
            when(cell.getValue()).thenReturn("cellValue" + (i + 1));
            cells.add(cell);
        }
    }

















}
