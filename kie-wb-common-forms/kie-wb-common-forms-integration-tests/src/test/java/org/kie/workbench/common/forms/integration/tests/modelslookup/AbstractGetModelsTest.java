/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.integration.tests.modelslookup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.workbench.common.services.backend.builder.ModuleBuildInfo;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public abstract class AbstractGetModelsTest {

    protected static final SimpleFileSystemProvider FS = new SimpleFileSystemProvider();

    protected static final String
            PROJECT_ROOT = "project",
            PERSON = "Person",
            ITEM = "Item",
            ADDRESS = "Address",
            DO_PACKAGE = "com.myteam.modelslookup.",
            ORDER_FQN = DO_PACKAGE + "Order",
            PERSON_FQN = DO_PACKAGE + PERSON,
            ITEM_FQN = DO_PACKAGE + ITEM,
            ADDRESS_FQN = DO_PACKAGE + ADDRESS;

    protected static final URL ROOT_URL = AbstractGetModelsTest.class.getResource(PROJECT_ROOT);

    protected static Path rootPath;

    protected static WeldContainer weldContainer;

    protected static KieModuleService moduleService;
    protected static ModuleBuildInfo moduleBuildInfo;

    @BeforeClass
    public static void containerSetup() throws Exception {
        weldContainer = new Weld().initialize();
        moduleService = weldContainer.select(KieModuleService.class).get();
        moduleBuildInfo = weldContainer.select(ModuleBuildInfo.class).get();

        rootPath = getRootPath(PROJECT_ROOT);
    }

    @AfterClass
    public static void containerTearDown() {
        if (weldContainer != null) {
            weldContainer.shutdown();
        }
    }

    protected java.nio.file.Path copyResource(String resourcePath, String newName) throws IOException {
        final java.nio.file.Path sourcePath = getNioPath(resourcePath);
        final java.nio.file.Path targetPath = sourcePath.resolveSibling(newName);
        return Files.copy(sourcePath, targetPath);
    }

    protected java.nio.file.Path renameResource(String resourcePath, String newName) throws IOException {
        final java.nio.file.Path sourcePath = getNioPath(resourcePath);
        final java.nio.file.Path targetPath = sourcePath.resolveSibling(newName);
        return Files.move(sourcePath, targetPath);
    }

    protected void deleteResource(String resourcePath) throws URISyntaxException, IOException {
        Files.delete(getNioPath(resourcePath));
    }

    protected java.nio.file.Path getNioPath(String pathToConvert) {
        return java.nio.file.Paths.get(getClass().getResource(pathToConvert).getPath());
    }

    private static Path getRootPath(String rootFolder) throws URISyntaxException {
        URL packageUrl = GetDataObjectModelsTest.class.getResource(rootFolder);
        org.uberfire.java.nio.file.Path nioPackagePath = FS.getPath(packageUrl.toURI());
        return Paths.convert(nioPackagePath);
    }
}
