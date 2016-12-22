/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.ballerina.core.runtime.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerina.core.exception.BallerinaException;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.Resource;
import org.wso2.ballerina.core.model.Service;
import org.wso2.ballerina.core.runtime.core.dispatching.ResourceDispatcher;
import org.wso2.ballerina.core.runtime.core.dispatching.ServiceDispatcher;
import org.wso2.ballerina.core.runtime.errors.handler.DefaultErrorHandler;
import org.wso2.ballerina.core.runtime.errors.handler.ErrorHandler;
import org.wso2.ballerina.core.runtime.internal.ServiceContextHolder;
import org.wso2.ballerina.core.runtime.registry.DispatcherRegistry;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * {@code ServerConnectorMessageHandler} is responsible for bridging Ballerina Program and External Server Connector
 *
 * @since 1.0.0
 */
public class ServerConnectorMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerConnectorMessageHandler.class);


    public static void handleInbound(CarbonMessage cMsg, CarbonCallback callback) {
        try {
            String protocol = (String) cMsg.getProperty(org.wso2.carbon.messaging.Constants.PROTOCOL);
            if (protocol == null) {
                throw new BallerinaException("Protocol not defined in the incoming request");
            }

            // Find the Service Dispatcher
            ServiceDispatcher dispatcher = DispatcherRegistry.getInstance().getServiceDispatcher(protocol);
            if (dispatcher == null) {
                throw new BallerinaException("No service dispatcher available to handle protocol : " + protocol);
            }

            // Find the Service
            Service service = dispatcher.findService(cMsg, callback);
            if (service == null) {
                throw new BallerinaException("No Service found to handle the service request");
                // Finer details of the errors are thrown from the dispatcher itself, Ideally we shouldn't get here.
            }

            // Find the Resource Dispatcher
            ResourceDispatcher resourceDispatcher = DispatcherRegistry.getInstance().getResourceDispatcher(protocol);
            if (resourceDispatcher == null) {
                throw new BallerinaException("No resource dispatcher available to handle protocol : " + protocol);
            }

            // Find the Resource
            Resource resource = resourceDispatcher.findResource(service, cMsg, callback);
            if (resource == null) {
                throw new BallerinaException("No Resource found to handle the request to Service : " +
                                             service.getSymbolName().getName());
                // Finer details of the errors are thrown from the dispatcher itself, Ideally we shouldn't get here.
            }

            // Create the Ballerina Context
            Context balContext = new Context(cMsg);
            balContext.setBalCallback(new DefaultBalCallback(callback));

            // Delegate the execution to the BalProgram Executor
            BalProgramExecutor.execute(balContext, resource);

        } catch (Throwable throwable) {
            handleError(cMsg, callback, throwable);
        }
    }

    public static void handleOutbound(CarbonMessage cMsg, CarbonCallback callback) {
        try {
            callback.done(cMsg);
        } catch (Throwable throwable) {
            handleError(cMsg, callback, throwable);
        }
    }

    private static void handleError(CarbonMessage cMsg, CarbonCallback callback, Throwable throwable) {
        log.error("Error while executing ballerina program. " + throwable.getMessage());

        ErrorHandler errorHandler;
        Object protocol = cMsg.getProperty(org.wso2.carbon.messaging.Constants.PROTOCOL);
        if (protocol != null) {
            errorHandler = ServiceContextHolder.getInstance().getErrorHandler((String) protocol);
        } else {
            errorHandler = DefaultErrorHandler.getInstance();
        }
        errorHandler.handleError(new Exception(throwable.getMessage(), throwable.getCause()), cMsg, callback);
    }

}
