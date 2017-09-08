/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.project;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 */
@ApplicationScoped
public class ProjectClassLoaderHelper {

    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    /*@Inject
    @Named("LRUProjectDependenciesClassLoaderCache")
    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;*/

    @Inject
    private CompilerMapsHolder compilerMapsHolder;

    public ClassLoader getProjectClassLoader(KieProject project) {
        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(nioPath, compilerMapsHolder, guvnorM2Repository);
        KieCompilationResponse res = builder.build();
        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {
            ClassLoader projectClassLoader;
            final KieModule module = res.getKieModule().get();
            if (module instanceof InternalKieModule) {
                //The integration works with CompilerClassloaderUtils.getMapClasses
                ClassLoader dependenciesClassLoader = new URLClassLoader(res.getProjectDependenciesAsURL().get().toArray(new URL[res.getProjectDependenciesAsURL().get().size()]));
                projectClassLoader = new MapClassLoader(CompilerClassloaderUtils.getMapClasses(res.getWorkingDir().get().toString()), dependenciesClassLoader);
            } else {
                projectClassLoader = KieModuleMetaData.Factory.newKieModuleMetaData(module).getClassLoader();
            }
            return projectClassLoader;
        } else {
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: " + project.toString());
        }
    }


}