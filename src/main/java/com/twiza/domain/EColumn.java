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

import java.util.List;
import java.util.Objects;


public class EColumn {

    String headerName;
    List<ECell> cells;

    public EColumn(String headerName, List<ECell> cells) {
        this.headerName = headerName;
        this.cells = cells;
    }

    public int getSize() {
        return cells.size();
    }

    public String getHeaderName() {
        return headerName;
    }

    public List<ECell> getCells() {
        return cells;
    }

    /**
     * append a {@code ECell} to end of the cells list.
     *
     * @param cell the ECell to be added to the end of the cells list.
     * @return {@code true}
     * @throws NullPointerException if the {@code ECell} was null.
     */

    public boolean addCell(ECell cell) {
        Objects.requireNonNull(cell);
        return cells.add(cell);
    }

    /**
     * Add the {@code ECell} to the provided position in the List.
     *
     * @param position position at which the specified cell to be inserted
     * @param cell     cell to be inserted
     * @throws IndexOutOfBoundsException if the position is out of range
     * @throws NullPointerException      if the {@code ECell} is null.
     */

    public void addCell(int position, ECell cell) {
        Objects.requireNonNull(cell);
        cells.add(position, cell);
    }
}
