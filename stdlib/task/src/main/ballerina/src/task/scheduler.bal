// Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

# Represents a ballerina task Scheduler, which can be used to run jobs periodically, using the given configurations.
public type Scheduler object {
    private Listener taskListener;

    # Initializes a `task:Scheduler` object. This may panic if the initialization causes any error due to
    # a configuration error.
    #
    # + configurations - A `task:TimerConfiguration` or `task:AppointmentConfiguration` record to define the
    # `task:Sceduler` behavior
    public function __init(TimerConfiguration|AppointmentConfiguration configurations) {
        self.taskListener = new(configurations);
    }

    # Attaches the provided `service` to the task.
    #
    # + serviceToAttach - Ballerina `service` object which needs to be attached to the task.
    # + attachments - Set of optional parameters which needs to passed inside the resources.
    # + return - Returns `task:SchedulerError` if the process failed due to any reason, nil otherwise.
    public function attach(service serviceToAttach, any... attachments) returns SchedulerError? {
        var result = attachExternal(self.taskListener, serviceToAttach, ...attachments);
        if (result is ListenerError) {
            string message = "Failed to attach the service to the scheduler";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Detach the provided `service` from the task.
    #
    # + attachedService - ballerina `service` object which needs to be detached from the task.
    # + return - Returns `task:SchedulerError` if the process failed due to any reason, nil otherwise.
    public function detach(service attachedService) returns SchedulerError? {
        var result = detachExternal(self.taskListener, attachedService);
        if (result is ListenerError) {
            string message = "Scheduler failed to detach the service";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Starts running the task. Task Scheduler will not run until this has been called.
    #
    # + return - Returns `task:SchedulerError` if the process failed due to any reason, nil otherwise.
    public function start() returns SchedulerError? {
        var result = startExternal(self.taskListener);
        if (result is ListenerError) {
            string message = "Scheduler failed to start";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Stops the task. This will stop, after finish running the existing jobs.
    #
    # + return - Returns `task:SchedulerError` if the process failed due to any reason, nil otherwise.
    public function stop() returns SchedulerError? {
        var result = stopExternal(self.taskListener);
        if (result is ListenerError) {
            string message = "Scheduler failed to stop";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Pauses the task.
    #
    # + return - Returns `task:SchedulerError` if an error is occurred while resuming, nil Otherwise.
    public function pause() returns SchedulerError? {
        var result = pauseExternal(self.taskListener);
        if (result is ListenerError) {
            string message = "Scheduler failed to pause";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Resumes a paused task.
    #
    # + return - Returns `task:SchedulerError` when an error occurred while pausing, nil Otherwise.
    public function resume() returns SchedulerError? {
        var result = resumeExternal(self.taskListener);
        if (result is ListenerError) {
            string message = "Scheduler failed to resume";
            return error(SCHEDULER_ERROR_REASON, message = message, cause = result);
        }
    }

    # Checks whether the task listener is started or not.
    #
    # + return - Returns `true` if the Scheduler is already started, `false` if the Scheduler is
    #               not started yet or stopped calling the `Scheduler.stop()` function.
    public function isStarted() returns boolean {
        return self.taskListener.isStarted();
    }
};