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

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;

import org.kie.workbench.common.services.backend.compiler.AFClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.impl.ClassLoaderProviderImpl;
import org.kie.workbench.common.services.backend.compiler.nio.impl.MavenUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<KieProject, ClassLoader> {

    private GuvnorM2Repository guvnorM2Repository;

    public LRUProjectDependenciesClassLoaderCache( ) {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache( GuvnorM2Repository guvnorM2Repository ) {
        this.guvnorM2Repository = guvnorM2Repository;
    }


    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
        ClassLoader classLoader = getEntry(project);
        if (classLoader == null) {
            classLoader = buildClassLoader(project);
            setEntry(project,
                     classLoader);
        }
        return classLoader;
    }

    /**When is called @MAXWasHere*/
    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader) {
        setEntry(project,
                 classLoader);
    }

    private ClassLoader buildClassLoader(final KieProject project) {
        KieAFBuilder builder = new DefaultKieAFBuilder(project.getRootPath().toURI().toString(), guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.LOCAL_M2_REPO_NAME));
        KieCompilationResponse res = builder.build();
        if(res.isSuccessful() && res.getKieModule().isPresent()) {
            return buildClassLoader(project);
        } else {
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: "
                                               + project.getKModuleXMLPath());
        }
    }

    /**
     * This method and the subsequent caching was added for performance reasons, since the dependencies calculation and
     * project class loader calculation tends to be time consuming when we manage project with transitives dependencies.
     * Since the project ClassLoader may change with ever incremental build it's better to store in the cache the
     * ClassLoader part that has the project dependencies. And the project ClassLoader can be easily calculated using
     * this ClassLoader as parent. Since current project classes are quickly calculated on each incremental build, etc.
     */
    public static ClassLoader buildClassLoader(final KieProject project,
                                               final KieModuleMetaData kieModuleMetaData) {
        //By construction the parent class loader for the KieModuleMetadata.getClassLoader() is an URLClass loader
        //that has the project dependencies. So this implementation relies on this. BUT can easily be changed to
        //calculate this URL class loader given that we have the pom.xml and we can use maven libraries classes
        //to calculate project maven dependencies. This is basically what the KieModuleMetaData already does. The
        //optimization was added to avoid the maven transitive calculation on complex projects.
        //final ClassLoader classLoader = kieModuleMetaData.getClassLoader().getParent();
        final ClassLoader classLoader = buildClassloaderFromAllProjectDeps(project);
        if (classLoader instanceof URLClassLoader) {
            return classLoader;
        } else {
            //this case should never happen. But if ProjectClassLoader calculation for KieModuleMetadata changes at
            //the error will be notified for implementation review.
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: "
                                               + project.getKModuleXMLPath());
        }
    }


    /** This method create a classloader only from the target folders*/
    private static ClassLoader buildClassloaderFromTargetFolders(final KieProject project){
        AFClassLoaderProvider kieClazzLoaderProvider = new ClassLoaderProviderImpl();
        List<String> pomList = new ArrayList<>();
        MavenUtils.searchPoms(Paths.convert(project.getRootPath()), pomList);
        Optional<ClassLoader> clazzLoader = kieClazzLoaderProvider.getClassloaderFromProjectTargets(pomList,
                                                                                                    Boolean.FALSE);
        if(clazzLoader.isPresent()){
            return clazzLoader.get();
        }else{
            return new URLClassLoader(new URL[0]);
        }
    }

    /** This method creates a classloader only from the deps from poms (transitives included)*/
    private static ClassLoader buildClassloaderFromAllProjectDeps(final KieProject project){
        AFClassLoaderProvider kieClazzLoaderProvider = new ClassLoaderProviderImpl();
        List<String> pomList = new ArrayList<>();
        MavenUtils.searchPoms(Paths.convert(project.getRootPath()), pomList);
        Optional<List<URL>> depsURLs = kieClazzLoaderProvider.getURLSFromAllDependencies(project.getRootPath().toString());
        if(depsURLs.isPresent()){
            List<URL> urls = depsURLs.get();
            return new URLClassLoader(urls.toArray(new URL[urls.size()]));
        }else{
            return new URLClassLoader(new URL[0]);
        }
    }
}