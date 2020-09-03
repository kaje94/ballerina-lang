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
package io.ballerina.projects.test;

import io.ballerina.projects.Module;
import io.ballerina.projects.ModuleId;
import io.ballerina.projects.Package;
import io.ballerina.projects.directory.SingleFileProject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Contains cases to test the basic package structure.
 *
 * @since 2.0.0
 */
public class TestSingleFileProject {
    private static final Path RESOURCE_DIRECTORY = Paths.get("src/test/resources/");

    @Test (description = "tests loading a valid standalone Ballerina file")
    public void testLoadSingleFile() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("single-file").resolve("main.bal");
        SingleFileProject project = null;
        try {
            project = SingleFileProject.loadProject(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        // 2) Load the package
        Package currentPackage = project.currentPackage();
        // 3) Load the default module
        Module defaultModule = currentPackage.getDefaultModule();
        Assert.assertEquals(defaultModule.documentIds().size(), 1);

        Collection<ModuleId> moduleIds = currentPackage.moduleIds();
        Assert.assertEquals(moduleIds.size(), 1);
        Assert.assertEquals(moduleIds.iterator().next(), currentPackage.getDefaultModule().moduleId());

    }

    @Test (description = "tests loading an invalid standalone Ballerina file")
    public void testLoadSingleFileNegative() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("myproject").resolve("modules").resolve("services")
                .resolve("svc.bal");
        try {
            SingleFileProject.loadProject(projectPath);
            Assert.fail("expected an invalid project exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("provided path is not a valid Ballerina standalone file"));
        }

        projectPath = RESOURCE_DIRECTORY.resolve("myproject").resolve("main.bal");
        try {
            SingleFileProject.loadProject(projectPath);
            Assert.fail("expected an invalid project exception");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("provided path is not a valid Ballerina standalone file"));
        }
    }

    @Test (description = "tests setting build options to the project")
    public void testSetBuildOptions() {
        Path projectPath = RESOURCE_DIRECTORY.resolve("single-file").resolve("main.bal");
        SingleFileProject project = null;
        try {
            project = SingleFileProject.loadProject(projectPath);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        SingleFileProject.BuildOptions buildOptions = project.getBuildOptions();

        // Verify expected default buildOptions
        Assert.assertFalse(buildOptions.isSkipTests());
        Assert.assertFalse(buildOptions.isOffline());
        Assert.assertFalse(buildOptions.isTestReport());
        Assert.assertFalse(buildOptions.isExperimental());

        buildOptions.setSkipTests(true);
        buildOptions.setSkipTests(true);

        // Update and verify buildOptions
        Assert.assertTrue(project.getBuildOptions().isSkipTests());
        Assert.assertFalse(project.getBuildOptions().isOffline());
        Assert.assertFalse(project.getBuildOptions().isTestReport());
        Assert.assertFalse(project.getBuildOptions().isExperimental());
    }
}