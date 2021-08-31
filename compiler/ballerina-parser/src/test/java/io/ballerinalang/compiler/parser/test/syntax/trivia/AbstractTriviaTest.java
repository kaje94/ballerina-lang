/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerinalang.compiler.parser.test.syntax.trivia;

import io.ballerina.compiler.parser.ParserRuleContext;
import io.ballerinalang.compiler.parser.test.ParserTestUtils;

import java.nio.file.Paths;

/**
 * Test capturing trivia.
 */
public abstract class AbstractTriviaTest {

    void test(String sourceFilePath, String filePath) {
        ParserTestUtils.test(Paths.get("trivia", sourceFilePath), ParserRuleContext.COMP_UNIT,
                Paths.get("trivia", filePath));
    }
}
