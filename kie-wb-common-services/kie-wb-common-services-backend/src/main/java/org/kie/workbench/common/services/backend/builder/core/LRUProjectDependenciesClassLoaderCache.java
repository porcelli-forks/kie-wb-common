/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassLoadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<Path, ClassLoader> {

    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;
    private ClassLoadersResourcesHolder classloadersResourcesHolder;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(GuvnorM2Repository guvnorM2Repository,
                                                  CompilerMapsHolder compilerMapsHolder,
                                                  ClassLoadersResourcesHolder classloadersResourcesHolder) {
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
        this.classloadersResourcesHolder = classloadersResourcesHolder;
    }

    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository);
        ClassLoader classLoader = getEntry(nioFsPAth);
        if (classLoader == null) {
            Optional<MapClassLoader> opClassloader = buildClassLoader(project);
            if(opClassloader.isPresent()) {
                setEntry(nioFsPAth, opClassloader.get());
                classLoader = opClassloader.get();
            }
        }
        return classLoader;
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader) {
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository);
        setEntry(nioFsPAth, classLoader);
    }

    protected Optional<MapClassLoader> buildClassLoader(final KieProject project) {
        Path nioPath = Paths.convert(project.getRootPath());
        Optional<MapClassLoader> classLoader =KieAfBuilderClassloaderUtil.getProjectClassloader(nioPath,
                                                                                                            compilerMapsHolder,
                                                                                                            guvnorM2Repository, classloadersResourcesHolder);

        return  classLoader;
    }

}