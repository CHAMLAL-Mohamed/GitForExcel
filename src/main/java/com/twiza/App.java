/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.twiza;

import com.twiza.excel.*;
import com.twiza.utils.ResourceHelper;

import java.io.File;
import java.io.IOException;

public class App {


    static int[] indexes = new int[]{0};
    static EBuilder builder;

    public static String PATH1 = "test1.xlsx";
    public static String PATH2 = "test2.xlsx";
    public static String diffPath = "diffResult.xlsx";

    public static void main(String[] args) throws IOException {
        builder = ExcelBuilder.getInstance();
        File myExcelFile = ResourceHelper.getResourceFile(PATH1);
        ExcelFileParams excelFileParams = builder.buildExcelFileParams(myExcelFile.getAbsolutePath());
        ExcelFile excelFile1 = new ExcelFile(excelFileParams);
        // ExcelFile excelFile2 = new ExcelFile(builder.buildExcelFileParams(ResourceHelper.getResourceFile(PATH2).getAbsolutePath()));


//
//        try (Workbook diffWorkbook = new XSSFWorkbook()) {
//            excelFile2.compare(excelFile1).writeToExcel(diffWorkbook);
//            FileOutputStream outputStream = new FileOutputStream(diffPath);
//            diffWorkbook.write(outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {

            ESheet eSheet = excelFile1.getESheet(excelFile1.getExcelFileParams().getWorkbook().getSheetName(0));
            System.out.println("-----------------------Print Sheet1--------------------------");
            printEsheet(eSheet);
        } catch (NullPointerException var5) {
            System.out.println(" the ExcelFile doesn't contain this sheet");
        } catch (IllegalArgumentException var6) {
            System.out.println("No sheet at that Index");
        }

    }

    private static void printEsheet(ESheet eSheet) {
        System.out.println(eSheet);
    }

}
