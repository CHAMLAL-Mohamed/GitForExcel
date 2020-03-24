/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.twiza;

import com.twiza.excel.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class App {


    static int[] indexes = new int[]{0};
    static EBuilder builder;
    static final String ROOT = "C:\\data\\TestFiles\\";
    static final String PATH1 = ROOT + "test1.xlsx";
    static final String PATH2 = ROOT + "test2.xlsx";
    static final String diffPath = ROOT + "diffResult.xlsx";


    public static void main(String[] args) throws IOException {
        builder = ExcelBuilder.getInstance();
        ExcelFile excelFile1 = new ExcelFile(builder.buildExcelFileParams(PATH1));
        ExcelFile excelFile2 = new ExcelFile(builder.buildExcelFileParams(PATH2));


//
//        try (Workbook diffWorkbook = new XSSFWorkbook()) {
//            excelFile2.compare(excelFile1).writeToExcel(diffWorkbook);
//            FileOutputStream outputStream = new FileOutputStream(diffPath);
//            diffWorkbook.write(outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            // Map<String, ERow> map = excelFile1.getSheetData(excelFile1.getExcelFileParams().getWorkbook().getSheetName(0));
            ESheet eSheet = excelFile1.getESheet(excelFile1.getExcelFileParams().getWorkbook().getSheetName(0));
            //   excelFile.getSheetData(workbook.getSheetName(indexes[0]));
            System.out.println("-----------------------Print Sheet1--------------------------");
            printEsheet(eSheet);
            // printMap(map);
        } catch (NullPointerException var5) {
            System.out.println(" the ExcelFile doesn't contain this sheet");
        } catch (IllegalArgumentException var6) {
            System.out.println("No sheet at that Index");
        }

    }

    private static void printEsheet(ESheet eSheet) {
        System.out.println(eSheet);

    }

    private static void printMap(Map<String, ERow> map) {
        Iterator<String> rows = map.keySet().iterator();
        while (rows.hasNext()) {
            String key = (String) rows.next();
            System.out.print(key + " :");
            Iterator rowsElements = ((List) map.get(key).getElements()).iterator();

            while (rowsElements.hasNext()) {
                String value = (String) rowsElements.next();
                System.out.print(value + "-");
            }

            System.out.println();
        }

    }
}
