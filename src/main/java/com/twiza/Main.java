//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.twiza;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;

public class Main {
    static int[] indexes = new int[]{0};
    static EBuilder builder;
    static final String PATH = "C:\\Users\\mohamed.chamlal\\Desktop\\YPPFMCG.xlsx";
    static final int INDEX = 0;

    public Main() {
    }

    public static void main(String[] args) throws IOException {
        builder = ExcelBuilder.getInstance();
        Workbook workbook = builder.buildWorkbook("C:\\Users\\mohamed.chamlal\\Desktop\\YPPFMCG.xlsx");
        ExcelFile excelFile = new ExcelFile();

        for(int i = 0; i < indexes.length; ++i) {
            excelFile.AddSheet(builder.buildESheet(workbook, indexes[i]));
        }

        Map map = null;

        try {
            map = excelFile.getSheetData(workbook.getSheetName(indexes[0]));
            printMap(map);
        } catch (NullPointerException var5) {
            System.out.println(" the ExcelFile doesn't contain this sheet");
        } catch (IllegalArgumentException var6) {
            System.out.println("No sheet at that Index");
        }

    }

    private static void printMap(Map<String, List<String>> map) {
        Iterator var1 = map.keySet().iterator();

        while(var1.hasNext()) {
            String key = (String)var1.next();
            System.out.print(key + " :");
            Iterator var3 = ((List)map.get(key)).iterator();

            while(var3.hasNext()) {
                String value = (String)var3.next();
                System.out.print(value + "-");
            }

            System.out.println();
        }

    }
}
