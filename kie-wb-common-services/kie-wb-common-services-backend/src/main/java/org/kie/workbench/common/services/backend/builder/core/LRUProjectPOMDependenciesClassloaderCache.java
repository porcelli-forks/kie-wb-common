/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.builder.core;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
@Named("LRUProjectPOMClassLoaderCache")
public class LRUProjectPOMDependenciesClassloaderCache extends LRUProjectDependenciesClassLoaderCache {
    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;

    public LRUProjectPOMDependenciesClassloaderCache( ) {
    }

    @Inject
    public LRUProjectPOMDependenciesClassloaderCache(GuvnorM2Repository guvnorM2Repository , CompilerMapsHolder compilerMapsHolder) {
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
    }


    public synchronized ClassLoader assertPOMClassLoader(final KieProject project) {
        ClassLoader classLoader = getEntry(project);
        if (classLoader == null) {
            classLoader = buildClassLoader(project);
            /*setEntry(project,
                    classLoader);*/
        }
        return classLoader;
    }

    public synchronized void setPOMClassLoader(final KieProject project,
                                                        ClassLoader classLoader) {
        setEntry(project, classLoader);
    }

    protected ClassLoader buildClassLoader(final KieProject project) {
        Path nioPath = Paths.convert(project.getRootPath());

        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        if(builder == null) {
            AFCompiler compiler = getCompiler();
            builder = new DefaultKieAFBuilder(project.getRootPath().toURI(), guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),
                    new String[]{MavenCLIArgs.COMPILE, MavenCLIArgs.DEBUG},
                    compiler, Boolean.FALSE, compilerMapsHolder);
            compilerMapsHolder.addBuilder(nioPath, builder);
        }
        ClassLoader classLoader = getEntry(project);
        if(classLoader != null) return classLoader;
        KieCompilationResponse res = builder.build();
        if(res.isSuccessful() && res.getProjectDependenciesAsURI().isPresent()) {
            List<URL> pomDeps = res.getProjectDependenciesAsURL().get();
            Optional<ClassLoader> urlClassloader = buildResult(pomDeps);
            if(urlClassloader.isPresent()){
                setEntry(project, urlClassloader.get());
            }
            return urlClassloader.get();
        } else {
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: "
                    + project.getKModuleXMLPath());
        }
    }

    private Optional<ClassLoader> buildResult(List<URL> urls) {
        if (urls.isEmpty()) {
            return Optional.empty();
        } else {
            URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
            return Optional.of(urlClassLoader);
        }
    }

}
