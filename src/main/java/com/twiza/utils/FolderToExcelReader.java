/*
 * Copyright  2020  Chamlal.Mohamed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twiza.utils;

import com.twiza.domain.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mohamed.Chamlal, 01/12/2020
 */
//BRAINSTORMING: reads a folder into excel file (only support sheets with no formulas, styles, or anything of that sort).
// Add standard themes in the future to be applied based on workbook/sheetName
public class FolderToExcelReader {

    /**
     * a static instance of this class to ensure Singleton.
     */
    private static FolderToExcelReader INSTANCE;

    private static final String EXTENSION_REGEX_PATTERN = "([.][^.]+$)";

    private FolderToExcelReader() {
    }

    public static FolderToExcelReader getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new FolderToExcelReader();
        return INSTANCE;
    }

    public void read(Path sheetsFolder) {
        //TODO(1): check if it is a folder
        //TODO(2): check if all children are txt files
        //TODO(3): create a workbook with the name of the folder
        //TODO(4): convert txt files into sheets and add them to workbook
        //TODO(5): save the workbook ( add a suffix to not override the old file)
        Files.isDirectory(sheetsFolder);
        try (Stream<Path> treePaths = Files.walk(sheetsFolder)) {
            List<ESheet> sheets = treePaths.filter(path -> path.toString().endsWith(".txt"))
                                           .map(this::textToESheet)
                                           .collect(Collectors.toCollection(ArrayList::new));
            EWorkbook workbook = new ExcelWorkbook(Paths.get(sheetsFolder.toString() + ".xlsx"), sheets);
            ExcelWriter.getInstance().writeToWorkbook(workbook.getWorkbookPath().toString(), workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ESheet textToESheet(Path sheetPath) {
        ESheet sheet = new ExcelSheet(sheetPath.getFileName().toString().replaceAll(EXTENSION_REGEX_PATTERN,""));
        try {
            Files.lines(sheetPath)
                 .map(this::stringToERow)
                 .forEach(sheet::addRow);
            return sheet.adoptFirstRowAsHeaders(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ERow stringToERow(String line) {
        List<ECell> cells = Arrays.stream(line.split("\t"))
                                  .map(ExcelCell::new)
                                  .collect(Collectors.toCollection(ArrayList::new));
        return new ExcelRow(cells);


    }

}
