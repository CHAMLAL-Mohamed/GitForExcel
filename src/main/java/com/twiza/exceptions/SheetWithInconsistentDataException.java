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

package com.twiza.exceptions;

public class SheetWithInconsistentDataException extends RuntimeException {
    private final static String ERROR_MESSAGE_1 = "The key ";
    private final static String ERROR_MESSAGE_2 = " is already inserted with a different Row";

    public SheetWithInconsistentDataException(String message) {
        super(ERROR_MESSAGE_1+message+ERROR_MESSAGE_2);
    }
}
