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

package com.twiza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Templates {

    private static Templates INSTANCE;
    Map<String, List<String>> sheetsTemplates;

    private Templates(String path) {
        sheetsTemplates = new HashMap<>();

    }

    public static Templates getInstance(String path) {
        if (INSTANCE == null) {
            INSTANCE = new Templates(path);
        }
        return INSTANCE;
    }

    /**
     * get the template of sheet or return null if it has no predefined template.
     *
     * @param sheetName the name of the sheet, it can be the absolute name or the short name,
     * @return null if no template exist for this sheetName, or a list of headers
     */
    public List<String> getSheetTemplate(String sheetName) {

        return sheetsTemplates.getOrDefault(sheetName, null);
    }
}
