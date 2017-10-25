/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.appformer.maven.integration.Aether;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.backend.builder.cache.ModuleCache;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService.GLOBAL_M2_REPO_NAME;

public abstract class AbstractDataModelerServiceWeldTest {

    private WeldContainer weldContainer;
    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected KieModuleService moduleService;
    protected DataModelerService dataModelService;
    protected Map<String, AnnotationDefinition> systemAnnotations = null;
    protected Paths paths;
    protected ModuleCache moduleCache;
    protected BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");
        System.setProperty(GLOBAL_M2_REPO_NAME, Aether.getAether().getLocalRepository().getUrl());

        //Bootstrap WELD container
        weldContainer = new Weld().initialize();
        beanManager = weldContainer.getBeanManager();
        dataModelService = weldContainer.select(DataModelerService.class).get();
        moduleService = weldContainer.select(KieModuleService.class).get();
        moduleCache = weldContainer.select(ModuleCache.class).get();
        paths = weldContainer.select(Paths.class).get();

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
        systemAnnotations = dataModelService.getAnnotationDefinitions();
    }

    @After
    public void tearDown() {
        if (weldContainer != null) {
            weldContainer.shutdown();
        }
    }

    protected KieModule loadProjectFromResources(String resourcesDir) throws URISyntaxException {
        final URL packageUrl = getClass().getResource(resourcesDir);
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        return moduleService.resolveModule(packagePath);
    }
}
