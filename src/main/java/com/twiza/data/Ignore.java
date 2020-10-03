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

package com.twiza.data;

import java.util.ArrayList;
import java.util.List;

public class Ignore {

    private static Ignore INSTANCE;
    private static List<String> folders;
    private static List<String> workbooks;
    private static List<String> sheets;

    private Ignore() {
        folders = new ArrayList<>();
        workbooks = new ArrayList<>();
        sheets = new ArrayList<>();
    }

    public static Ignore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ignore();
        }
        return INSTANCE;
    }

    public Ignore setFoldersToIgnore(List<String> foldersToIgnore) {
        folders = foldersToIgnore;
        return INSTANCE;
    }

    public Ignore setWorkbooksToIgnore(List<String> workbooksToIgnore) {
        workbooks = workbooksToIgnore;
        return INSTANCE;
    }

    public Ignore setSheetsToIgnore(List<String> sheetsToIgnore) {
        sheets = sheetsToIgnore;
        return INSTANCE;
    }

    public static List<String> getFolders() {
        return folders;
    }

    public static List<String> getWorkbooks() {
        return workbooks;
    }

    public static List<String> getSheets() {
        return sheets;
    }
}
