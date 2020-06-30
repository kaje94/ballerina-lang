/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerinalang.compiler.syntax.tree;

import io.ballerinalang.compiler.internal.parser.tree.STNode;

import java.util.Objects;

/**
 * This is a generated syntax tree node.
 *
 * @since 2.0.0
 */
public class ParameterDocumentationLineNode extends DocumentationNode {

    public ParameterDocumentationLineNode(STNode internalNode, int position, NonTerminalNode parent) {
        super(internalNode, position, parent);
    }

    public Token hashToken() {
        return childInBucket(0);
    }

    public Token plusToken() {
        return childInBucket(1);
    }

    public Token parameterName() {
        return childInBucket(2);
    }

    public Token minusToken() {
        return childInBucket(3);
    }

    public Token description() {
        return childInBucket(4);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(NodeTransformer<T> visitor) {
        return visitor.transform(this);
    }

    @Override
    protected String[] childNames() {
        return new String[]{
                "hashToken",
                "plusToken",
                "parameterName",
                "minusToken",
                "description"};
    }

    public ParameterDocumentationLineNode modify(
            SyntaxKind kind,
            Token hashToken,
            Token plusToken,
            Token parameterName,
            Token minusToken,
            Token description) {
        if (checkForReferenceEquality(
                hashToken,
                plusToken,
                parameterName,
                minusToken,
                description)) {
            return this;
        }

        return NodeFactory.createParameterDocumentationLineNode(
                kind,
                hashToken,
                plusToken,
                parameterName,
                minusToken,
                description);
    }

    public ParameterDocumentationLineNodeModifier modify() {
        return new ParameterDocumentationLineNodeModifier(this);
    }

    /**
     * This is a generated tree node modifier utility.
     *
     * @since 2.0.0
     */
    public static class ParameterDocumentationLineNodeModifier {
        private final ParameterDocumentationLineNode oldNode;
        private Token hashToken;
        private Token plusToken;
        private Token parameterName;
        private Token minusToken;
        private Token description;

        public ParameterDocumentationLineNodeModifier(ParameterDocumentationLineNode oldNode) {
            this.oldNode = oldNode;
            this.hashToken = oldNode.hashToken();
            this.plusToken = oldNode.plusToken();
            this.parameterName = oldNode.parameterName();
            this.minusToken = oldNode.minusToken();
            this.description = oldNode.description();
        }

        public ParameterDocumentationLineNodeModifier withHashToken(
                Token hashToken) {
            Objects.requireNonNull(hashToken, "hashToken must not be null");
            this.hashToken = hashToken;
            return this;
        }

        public ParameterDocumentationLineNodeModifier withPlusToken(
                Token plusToken) {
            Objects.requireNonNull(plusToken, "plusToken must not be null");
            this.plusToken = plusToken;
            return this;
        }

        public ParameterDocumentationLineNodeModifier withParameterName(
                Token parameterName) {
            Objects.requireNonNull(parameterName, "parameterName must not be null");
            this.parameterName = parameterName;
            return this;
        }

        public ParameterDocumentationLineNodeModifier withMinusToken(
                Token minusToken) {
            Objects.requireNonNull(minusToken, "minusToken must not be null");
            this.minusToken = minusToken;
            return this;
        }

        public ParameterDocumentationLineNodeModifier withDescription(
                Token description) {
            Objects.requireNonNull(description, "description must not be null");
            this.description = description;
            return this;
        }

        public ParameterDocumentationLineNode apply() {
            return oldNode.modify(
                    oldNode.kind(),
                    hashToken,
                    plusToken,
                    parameterName,
                    minusToken,
                    description);
        }
    }
}
