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

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.List;

/**
 * Template class store are configuration needed to setup a sheet
 */
public class Template {
    /**
     * Pattern that will be matched with sheets for
     */
    private final String name;
    private List<String> matchingHeaders;
    private MatchingPolicy matchingPolicy;
    private boolean isFirstRowHeader;
    private int[] deleteRows;
    private int[] deleteColumns;
    private int[] keyColumns;

    public Template(String name) {
        FileSystems.getDefault().getPathMatcher("").matches(Paths.get("fff"));
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getMatchingHeaders() {
        return matchingHeaders;
    }

    public void setMatchingHeaders(List<String> matchingHeaders) {
        this.matchingHeaders = matchingHeaders;
    }

    public MatchingPolicy getMatchingPolicy() {
        return matchingPolicy;
    }

    public void setMatchingPolicy(MatchingPolicy matchingPolicy) {
        this.matchingPolicy = matchingPolicy;
    }

    public boolean isFirstRowHeader() {
        return isFirstRowHeader;
    }

    public void setFirstRowHeader(boolean firstRowHeader) {
        isFirstRowHeader = firstRowHeader;
    }

    public int[] getDeleteRows() {
        return deleteRows;
    }

    public void setDeleteRows(int[] deleteRows) {
        this.deleteRows = deleteRows;
    }

    public int[] getDeleteColumns() {
        return deleteColumns;
    }

    public void setDeleteColumns(int[] deleteColumns) {
        this.deleteColumns = deleteColumns;
    }

    public int[] getKeyColumns() {
        return keyColumns;
    }

    public void setKeyColumns(int[] keyColumns) {
        this.keyColumns = keyColumns;
    }
}
