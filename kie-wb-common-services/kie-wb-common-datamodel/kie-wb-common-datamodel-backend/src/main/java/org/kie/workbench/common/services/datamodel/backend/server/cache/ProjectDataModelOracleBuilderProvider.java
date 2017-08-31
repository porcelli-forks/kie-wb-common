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
package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.util.*;
import javax.inject.Inject;

import org.appformer.project.datamodel.imports.Import;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.builder.service.BuildInfo;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.JGitUtils;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
//import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

public class ProjectDataModelOracleBuilderProvider {

    private static final Logger log = LoggerFactory.getLogger(ProjectDataModelOracleBuilderProvider.class);

    private ProjectImportsService importsService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private GuvnorM2Repository guvnorM2Repository;
    private CompilerMapsHolder compilerMapsHolder;

    public ProjectDataModelOracleBuilderProvider() {
        //CDI proxy
    }

    @Inject
    public ProjectDataModelOracleBuilderProvider(final PackageNameWhiteListService packageNameWhiteListService,
                                                 final ProjectImportsService importsService, final GuvnorM2Repository guvnorM2Repository,
                                                 final CompilerMapsHolder compilerMapsHolder) {
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.importsService = importsService;
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
    }

    public InnerBuilder newBuilder( final KieProject project,
                                    final BuildInfo buildInfo ) {

        final KieModuleMetaData kieModuleMetaData = buildInfo.getKieModuleMetaDataIgnoringErrors();
        final TypeSourceResolver typeSourceResolver = buildInfo.getTypeSourceResolver(kieModuleMetaData);

        return new InnerBuilder(project,
                                kieModuleMetaData,
                                typeSourceResolver);
    }

    public InnerBuilder newBuilder( final KieProject project ) {
        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        /*if(builder == null) {
            if(nioPath.getFileSystem() instanceof JGitFileSystem){
                Git repo = JGitUtils.tempClone((JGitFileSystem)nioPath.getFileSystem(), UUID.randomUUID().toString());
                compilerMapsHolder.addGit((JGitFileSystem) nioPath.getFileSystem(), repo);
                builder = new DefaultKieAFBuilder(nioPath, guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),compilerMapsHolder);
            }

        }*/

        //KieAFBuilder builder = new DefaultKieAFBuilder(project.getRootPath().toURI().toString(), guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.LOCAL_M2_REPO_NAME));
        KieCompilationResponse res = builder.build();
        if(res.isSuccessful() && res.getKieModule().isPresent()) {
            final KieModuleMetaData kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                                                                                  res.getProjectDependencies().get());
            //final TypeSourceResolver typeSourceResolver = buildInfo.getTypeSourceResolver(kieModuleMetaData);
            //@MAXWasHere this javaresources are the result of addJavaClass and remove in the Builder
            final Set<String> javaResources = new HashSet<String>();
            //@TODO this javaresources logic must be moved from the builder addJavaClass,removeJavaClass,getFullyQualifiedClassName
            final TypeSourceResolver typeSourceResolver = new TypeSourceResolver( kieModuleMetaData,
                                                                                  javaResources );

            return new InnerBuilder(project,
                                    kieModuleMetaData,
                                    typeSourceResolver);
        }else{
            throw new RuntimeException("Failed to build correctly the project:"+ project.toString());
        }
    }

    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

        private final KieProject project;
        private final KieModuleMetaData kieModuleMetaData;
        private final TypeSourceResolver typeSourceResolver;

        private InnerBuilder(final KieProject project,
                             final KieModuleMetaData kieModuleMetaData,
                             final TypeSourceResolver typeSourceResolver) {
            this.project = project;
            this.kieModuleMetaData = kieModuleMetaData;
            this.typeSourceResolver = typeSourceResolver;
        }

        public ProjectDataModelOracle build() {

            addFromKieModuleMetadata();

            addExternalImports();

            return pdBuilder.build();
        }

        /**
         * The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
         */
        private void addExternalImports() {
            if (Files.exists(Paths.convert(project.getImportsPath()))) {
                for (final Import item : getImports()) {
                    addClass(item);
                }
            }
        }

        private void addFromKieModuleMetadata() {
            for (final String packageName : getFilteredPackageNames()) {
                pdBuilder.addPackage(packageName);
                addClasses(packageName,
                           kieModuleMetaData.getClasses(packageName));
            }
        }

        /**
         * @return A "white list" of package names that are available for authoring
         */
        private WhiteList getFilteredPackageNames() {
            return packageNameWhiteListService.filterPackageNames(project,
                                                                  kieModuleMetaData.getPackages());
        }

        private void addClasses(final String packageName,
                                final Collection<String> classes) {
            for (final String className : classes) {
                addClass(packageName,
                         className);
            }
        }

        private void addClass(final Import item) {
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(item.getType());
                pdBuilder.addClass(clazz,
                                   false,
                                   TypeSource.JAVA_DEPENDENCY);
            } catch (ClassNotFoundException cnfe) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(cnfe.getMessage());
            } catch (IOException ioe) {
                log.debug(ioe.getMessage());
            }
        }

        private void addClass(final String packageName,
                              final String className) {
            try {
                final Class clazz = kieModuleMetaData.getClass(packageName,
                                                               className);
                pdBuilder.addClass(clazz,
                                   kieModuleMetaData.getTypeMetaInfo(clazz).isEvent(),
                                   typeSourceResolver.getTypeSource(clazz));
            } catch (Throwable e) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug(e.getMessage());
            }
        }

        private List<Import> getImports() {
            return importsService.load(project.getImportsPath()).getImports().getImports();
        }
    }
}
