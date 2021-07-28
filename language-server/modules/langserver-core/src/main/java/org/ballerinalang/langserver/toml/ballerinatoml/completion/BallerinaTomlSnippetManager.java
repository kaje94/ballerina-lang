/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.langserver.toml.ballerinatoml.completion;

import io.ballerina.toml.validator.schema.Schema;
import org.apache.commons.io.IOUtils;
import org.ballerinalang.langserver.commons.LanguageServerContext;
import org.ballerinalang.langserver.toml.common.completion.AbstractTomlSnippetManager;
import org.ballerinalang.langserver.toml.common.completion.visitor.TomlNode;
import org.ballerinalang.langserver.toml.common.completion.visitor.TomlSchemaVisitor;
import org.eclipse.lsp4j.CompletionItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Maintains all the supported snippets for Ballerina Toml.
 *
 * @since 2.0.0
 */
public class BallerinaTomlSnippetManager extends AbstractTomlSnippetManager {

    private static final LanguageServerContext.Key<BallerinaTomlSnippetManager> BALLERINA_TOML_SNIPPET_MANAGER_KEY =
            new LanguageServerContext.Key<>();
    private final Map<TomlNode, Map<String, CompletionItem>> completions;

    private BallerinaTomlSnippetManager(LanguageServerContext context) {
        Schema c2cRootSchema = Schema.from(getValidationSchema());
        TomlSchemaVisitor visitor = new TomlSchemaVisitor();
        c2cRootSchema.accept(visitor);
        this.completions = visitor.getAllCompletionSnippets();
        context.put(BALLERINA_TOML_SNIPPET_MANAGER_KEY, this);
    }

    @Override
    public Map<TomlNode, Map<String, CompletionItem>> getCompletions() {
        return Collections.unmodifiableMap(completions);
    }

    /**
     * Returns a single instance of concrete CloudTomlSnippetManager.
     *
     * @return {@link AbstractTomlSnippetManager}
     */
    public static BallerinaTomlSnippetManager getInstance(LanguageServerContext context) {
        BallerinaTomlSnippetManager snippetManager = context.get(BALLERINA_TOML_SNIPPET_MANAGER_KEY);
        if (snippetManager == null) {
            snippetManager = new BallerinaTomlSnippetManager(context);
        }
        return snippetManager;
    }

    @Override
    public String getValidationSchema() {
        try {
            return IOUtils.resourceToString("ballerina-toml-schema.json",
                    StandardCharsets.UTF_8, getClass().getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Schema Not found");
        }
    }
}
