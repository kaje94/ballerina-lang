/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.debugadapter.variable.types;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import org.ballerinalang.debugadapter.SuspendedContext;
import org.ballerinalang.debugadapter.variable.BCompoundVariable;
import org.ballerinalang.debugadapter.variable.BVariableType;
import org.ballerinalang.debugadapter.variable.VariableUtils;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.ballerinalang.debugadapter.variable.VariableUtils.UNKNOWN_VALUE;
import static org.ballerinalang.debugadapter.variable.VariableUtils.getStringFrom;

/**
 * Ballerina tuple variable type.
 */
public class BTuple extends BCompoundVariable {

    public BTuple(SuspendedContext context, String name, Value value) {
        super(context, name, BVariableType.TUPLE, value);
    }

    @Override
    public String computeValue() {
        try {
            return getTupleType(jvmValue);
        } catch (Exception e) {
            return UNKNOWN_VALUE;
        }
    }

    @Override
    public Either<Map<String, Value>, List<Value>> computeChildVariables() {
        try {
            if (!(jvmValue instanceof ObjectReference)) {
                return Either.forRight(new ArrayList<>());
            }
            ObjectReference jvmValueRef = (ObjectReference) jvmValue;
            Field valueField = jvmValueRef.referenceType().fieldByName("refValues");
            List<Value> valueList = ((ArrayReference) jvmValueRef.getValue(valueField)).getValues();

            // List length is 100 by default. Create a sub list with actual array size.
            List<Value> valueSubList = valueList.subList(0, getTupleSize(jvmValueRef));
            return Either.forRight(valueSubList);
        } catch (Exception ignored) {
            return Either.forRight(new ArrayList<>());
        }
    }

    @Override
    protected Map.Entry<ChildVariableKind, Integer> getChildrenCount() {
        return new AbstractMap.SimpleEntry<>(ChildVariableKind.INDEXED, getTupleSize((ObjectReference) jvmValue));
    }

    /**
     * Returns the tuple type in string form.
     */
    private String getTupleType(Value jvmValue) {
        try {
            Optional<Value> tupleType = VariableUtils.getFieldValue(jvmValue, "tupleType");
            if (tupleType.isEmpty()) {
                return UNKNOWN_VALUE;
            }
            Optional<Value> subTypes = VariableUtils.getFieldValue(tupleType.get(), "tupleTypes");
            if (subTypes.isEmpty()) {
                return UNKNOWN_VALUE;
            }
            Optional<Value> typesArray = VariableUtils.getFieldValue(subTypes.get(), "elementData");
            if (typesArray.isEmpty()) {
                return UNKNOWN_VALUE;
            }
            List<Value> subValues = ((ArrayReference) typesArray.get()).getValues();
            StringJoiner tupleTypes = new StringJoiner(",");
            subValues.forEach(ref -> {
                if (ref instanceof ObjectReference) {
                    Field typeNameField = ((ObjectReference) ref).referenceType().fieldByName("typeName");
                    Value typeNameRef = ((ObjectReference) ref).getValue(typeNameField);
                    tupleTypes.add(getStringFrom(typeNameRef));
                }
            });
            return String.format("tuple[%s]", tupleTypes.toString());
        } catch (Exception e) {
            return UNKNOWN_VALUE;
        }
    }

    /**
     * Returns the size/length of a given ballerina tuple typed variable.
     *
     * @param arrayRef object reference of the tuple instance.
     * @return size of the tuple.
     */
    private int getTupleSize(ObjectReference arrayRef) {
        List<Field> fields = arrayRef.referenceType().allFields();
        Field arraySizeField = arrayRef.getValues(fields).entrySet().stream().filter(fieldValueEntry ->
                fieldValueEntry.getValue() != null &&
                        fieldValueEntry.getKey().toString().endsWith("ArrayValue.size"))
                .map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
        return ((IntegerValue) arrayRef.getValue(arraySizeField)).value();
    }
}
