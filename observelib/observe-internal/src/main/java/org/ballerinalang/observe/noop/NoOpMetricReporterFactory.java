/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.observe.noop;

import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.observability.metrics.spi.MetricReporterFactory;

import static org.ballerinalang.observe.Constants.OBSERVE_INTERNAL_MODULE;

/**
 * Implementation of No-Op {@link MetricReporterFactory}.
 */
public class NoOpMetricReporterFactory implements MetricReporterFactory {
    private static final BObject BOBJECT_INSTANCE =
            ValueCreator.createObjectValue(OBSERVE_INTERNAL_MODULE, "NoOpMetricReporter");

    @Override
    public String getName() {
        return "noop";
    }

    @Override
    public BObject getReporterBObject() {
        return BOBJECT_INSTANCE;
    }
}
