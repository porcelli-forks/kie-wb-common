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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.tools.ant.taskdefs.Classloader;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.workbench.common.services.backend.builder.af.KieAfBuilderClassloaderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassloadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<Path, ClassLoader> {

    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;
    private ClassloadersResourcesHolder classloadersResourcesHolder;

    public LRUProjectDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache(GuvnorM2Repository guvnorM2Repository,
                                                  CompilerMapsHolder compilerMapsHolder,
                                                  ClassloadersResourcesHolder classloadersResourcesHolder) {
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
        this.classloadersResourcesHolder = classloadersResourcesHolder;
    }

    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
       // Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder);
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository);
        ClassLoader classLoader = getEntry(nioFsPAth);
        if (classLoader == null) {
            classLoader = buildClassLoader(project);
            setEntry(nioFsPAth, classLoader);
        }
        return classLoader;
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader) {
        Path nioFsPAth = KieAFBuilderUtil.getFSPath(project, compilerMapsHolder, guvnorM2Repository);
        setEntry(nioFsPAth, classLoader);
    }

    protected ClassLoader buildClassLoader(final KieProject project) {
        Path nioPath = Paths.convert(project.getRootPath());
        ClassLoader classLoader =KieAfBuilderClassloaderUtil.getProjectClassloader(nioPath,
                                                                 compilerMapsHolder,
                                                                 guvnorM2Repository,classloadersResourcesHolder);
        return classLoader;
    }

    /*class ClassloaderTuple{

        private ClassLoader classLoader;
        private Path fsPath;

        public ClassloaderTuple(ClassLoader classLoader,
                                Path fsPath) {
            this.classLoader = classLoader;
            this.fsPath = fsPath;
        }

        public ClassLoader getClassLoader() {
            return classLoader;
        }

        public Path getFsPath() {
            return fsPath;
        }
    }*/



    /**
     * This method and the subsequent caching was added for performance reasons, since the dependencies calculation and
     * project class loader calculation tends to be time consuming when we manage project with transitives dependencies.
     * Since the project ClassLoader may change with ever incremental build it's better to store in the cache the
     * ClassLoader part that has the project dependencies. And the project ClassLoader can be easily calculated using
     * this ClassLoader as parent. Since current project classes are quickly calculated on each incremental build, etc.
     */
   /* public static ClassLoader buildClassLoader(final KieProject project,
                                               final KieModuleMetaData kieModuleMetaData) {
        //By construction the parent class loader for the KieModuleMetadata.getClassLoader() is an URLClass loader
        //that has the project dependencies. So this implementation relies on this. BUT can easily be changed to
        //calculate this URL class loader given that we have the pom.xml and we can use maven libraries classes
        //to calculate project maven dependencies. This is basically what the KieModuleMetaData already does. The
        //optimization was added to avoid the maven transitive calculation on complex projects.
        final ClassLoader classLoader = kieModuleMetaData.getClassLoader().getParent();
        if (classLoader instanceof URLClassLoader) {
            return classLoader;
        } else {
            //this case should never happen. But if ProjectClassLoader calculation for KieModuleMetadata changes at
            //the error will be notified for implementation review.
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: "
                                               + project.getKModuleXMLPath());
        }
    }*/
/*
    protected AFCompiler getCompiler() {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator,
                                                                    compilerMapsHolder);
        return outerDecorator;
    }*/
}