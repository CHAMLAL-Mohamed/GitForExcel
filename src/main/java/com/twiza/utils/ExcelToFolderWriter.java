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

import com.twiza.domain.EWorkbook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Mohamed.Chamlal, 25/11/2020
 */
//IDEA: use this class to convert excel files into folder with sheets as text file
// the purpose of this class is to check the  possibility to add excel files into git with no issues
public class ExcelToFolderWriter {
    /**
     * a static instance of this class to ensure Singleton.
     */
    private static ExcelToFolderWriter INSTANCE;

    private static final String EXTENSION_REGEX_PATTERN = "([.][^.]+$)";

    private ExcelToFolderWriter() {
    }

    public static ExcelToFolderWriter getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new ExcelToFolderWriter();
        return INSTANCE;
    }

    public boolean write(EWorkbook workbook) throws IOException {
        Path workbookPath = getWorkbookPath(workbook.getWorkbookPath());
        workbookPath.toFile().mkdirs();
        workbook.getSheets().values().forEach(sheet -> {
            FileWriter writer = null;
            try {
                File output = Paths.get(workbookPath.toString(), sheet.getName() + ".txt").toFile();
                writer = new FileWriter(output);

                if (sheet.getHeaders() != null) {
                    writer.write(String.join("\t", sheet.getHeaders()) + "\n");
                }
                FileWriter finalWriter = writer;
                sheet.getData().forEach(row -> {
                    try {
                        finalWriter.write(String.join("\t", row.getCellsValues()) + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return true;
    }

    private Path getWorkbookPath(Path workbookPath) {
        String workbookName = workbookPath.toString().replaceAll(EXTENSION_REGEX_PATTERN, "");
        return Paths.get(workbookName);
    }
}
