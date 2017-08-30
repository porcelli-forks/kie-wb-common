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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.builder.core.LRUProjectDependenciesClassLoaderCache;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import java.net.URI;
import java.util.UUID;

/**
 *
 */
@ApplicationScoped
public class ProjectClassLoaderHelper {

    protected String FILE_URI = "file://";

    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    @Inject
    @Named("LRUProjectDependenciesClassLoaderCache")
    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;

    @Inject
    private CompilerMapsHolder compilerMapsHolder;

    public ClassLoader getProjectClassLoader( KieProject project ) {
        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        if(builder == null) {
            if(nioPath.getFileSystem() instanceof JGitFileSystem ){
                Git repo = JGitUtils.tempClone((JGitFileSystem)nioPath.getFileSystem(), UUID.randomUUID().toString());
                compilerMapsHolder.addGit((JGitFileSystem) nioPath.getFileSystem(), repo);
                Path prj = org.uberfire.java.nio.file.Paths.get(URI.create(repo.getRepository().getDirectory().toPath().getParent().toAbsolutePath().toUri().toString()+ nioPath.toString()));
                builder = new DefaultKieAFBuilder(prj, guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),compilerMapsHolder);
            }
        }
        KieCompilationResponse res = builder.build();

        if(res.isSuccessful() && res.getKieModule().isPresent()) {
            final KieModule module = res.getKieModule().get();
            ClassLoader dependenciesClassLoader = dependenciesClassLoaderCache.assertDependenciesClassLoader(project);
            ClassLoader projectClassLoader;
            if (module instanceof InternalKieModule) {
                //will always be an internal kie module
                InternalKieModule internalModule = (InternalKieModule) module;
                projectClassLoader = new MapClassLoader(internalModule.getClassesMap(true),
                                                        dependenciesClassLoader);
            } else {
                projectClassLoader = KieModuleMetaData.Factory.newKieModuleMetaData(module).getClassLoader();
            }
            return projectClassLoader;
        }else{
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: " + project.toString());
        }
    }

}