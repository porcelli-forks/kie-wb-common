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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassLoadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<Path, ClassLoader> {

    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;
    private ClassLoadersResourcesHolder classloadersResourcesHolder;
    private Instance< User > identity;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(GuvnorM2Repository guvnorM2Repository,
                                                  CompilerMapsHolder compilerMapsHolder,
                                                  ClassLoadersResourcesHolder classloadersResourcesHolder, Instance< User > identity) {
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
        this.classloadersResourcesHolder = classloadersResourcesHolder;
        this.identity = identity;
    }


    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project, String identity) {
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository, identity);
        ClassLoader classLoader = getEntry(nioFsPAth);
        if (classLoader == null) {
            Optional<MapClassLoader> opClassloader = buildClassLoader(project, identity);
            if(opClassloader.isPresent()) {
                setEntry(nioFsPAth, opClassloader.get());
                classLoader = opClassloader.get();
            }
        }
        return classLoader;
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader, String identity) {
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository, identity);
        setEntry(nioFsPAth, classLoader);
    }

    protected Optional<MapClassLoader> buildClassLoader(final KieProject project, String identity) {
        Optional<MapClassLoader> classLoader = KieAfBuilderClassloaderUtil.getProjectClassloader(project, compilerMapsHolder, guvnorM2Repository, classloadersResourcesHolder, identity);
        Path workingDir = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository, identity);
        compilerMapsHolder.addAlias(project.getKModuleXMLPath().toURI(), workingDir.toAbsolutePath());
        return  classLoader;
    }

    @Override
    public void invalidateCache(Path path) {
        compilerMapsHolder.removeDependenciesRaw(path);
        compilerMapsHolder.removeKieModuleMetaData(path);
        classloadersResourcesHolder.removeTargetClassloader(path);
        if(path.endsWith("pom.xml")){
            classloadersResourcesHolder.removeDependenciesClassloader(path);
        }
    }

    private String getKey(String projectRootPath){
        return  new StringBuilder().append(projectRootPath.toString()).append("-").append(KieAFBuilderUtil.getIdentifier(identity)).toString();
    }
}