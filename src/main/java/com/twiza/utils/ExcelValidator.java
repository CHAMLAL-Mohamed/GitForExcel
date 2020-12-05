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

/**
 * @author Mohamed.Chamlal, 25/11/2020
 */

    //BRAINSTORMING: this class id responsible for matching sheets against a template
    // mainly, it handles: data cleaning(delete specific rows, columns),
    // eliminating unnecessary columns and ordering the remaining based on a template
    // And with the usage of policies, we can define the necessary actions in case a rule was not satisfied
    // ( missing column, keys are not unique, rows with same keys...)
    //TODO(1):define Template format
    //TODO(2):define policies
    //TODO(3):define logic of reading most specific match of template pattern
    //TODO(4):
public class ExcelValidator {

}
