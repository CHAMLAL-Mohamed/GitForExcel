package com.twiza.excel;

import java.io.IOException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface EBuilder {
    Sheet buildESheet(Workbook var1, int var2);

    Workbook buildWorkbook(String var1) throws IOException;
}