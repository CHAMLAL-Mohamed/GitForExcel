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

package com.twiza.domain;

import java.nio.file.Path;
import java.util.Map;

public interface EWorkbook {
    String SHEETS_WITH_SAME_NAME_EXCEPTION_MESSAGE = "EWorkbook cannot contain 2 ESheets with the same Name";

    /**
     * Returns the {@code ESheet} based on the name provided.
     *
     * @param name the name of the sheet
     * @return the sheet with the specified name, null otherwise
     */
    ESheet getSheet(String name);

    ESheet getSheet(int index);

    Path getWorkbookPath();

    Map<String, ESheet> getSheets();

    int getSize();

    void addSheet(ESheet sheet);


    boolean removeSheet(String sheetName);

    /**
     * Compare this sheet with anther one, and returns a new sheet that contains
     * the details of what elements have been changed, added or deleted.
     *
     * @param oldWorkbook the other {@code ESheet} to compare with
     * @return the new {@code ESheet} with all the changes made between this sheet and the new one.
     */
    EWorkbook compare(EWorkbook oldWorkbook);

    boolean equals(Object o);

    int hashCode();


}
