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
import org.kie.scanner.KieModuleMetaData;
import org.kie.scanner.KieModuleMetaDataImpl;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.BuilderCache;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassLoadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.DependenciesCache;
import org.kie.workbench.common.services.backend.compiler.impl.share.KieModuleMetaDataCache;
import org.kie.workbench.common.services.backend.compiler.impl.utils.BuilderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
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


public class ProjectDataModelOracleBuilderProvider {

    private static final Logger log = LoggerFactory.getLogger(ProjectDataModelOracleBuilderProvider.class);

    private ProjectImportsService importsService;
    private PackageNameWhiteListService packageNameWhiteListService;
    private BuilderUtils builderUtils;
    private ClassLoadersResourcesHolder resourcesHolder;
    private BuilderCache builderCache;
    private KieModuleMetaDataCache kieModuleMetaDataCache;
    private DependenciesCache dependenciesCache;

    public ProjectDataModelOracleBuilderProvider() {
        //CDI proxy
    }

    @Inject
    public ProjectDataModelOracleBuilderProvider(final PackageNameWhiteListService packageNameWhiteListService,
                                                 final ProjectImportsService importsService, final ClassLoadersResourcesHolder resourcesHolder,
                                                 final BuilderUtils builderUtils, final BuilderCache builderCache,
                                                 final KieModuleMetaDataCache kieModuleMetaDataCache,
                                                 final DependenciesCache dependenciesCache) {
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.importsService = importsService;
        this.resourcesHolder = resourcesHolder;
        this.builderUtils = builderUtils;
        this.builderCache = builderCache;
        this.kieModuleMetaDataCache = kieModuleMetaDataCache;
        this.dependenciesCache = dependenciesCache;
    }

    public InnerBuilder newBuilder( final KieProject project) {
        Path nioPath = Paths.convert(project.getRootPath());
        Optional<KieAFBuilder> builder =  builderUtils.getBuilder(project.getRootPath().toURI(),nioPath);

        if(!builder.isPresent()){
            throw new RuntimeException("Isn't possible create a Builder"+ project.toString());
        }
        DefaultKieAFBuilder defaultKieBuilder = (DefaultKieAFBuilder)builder.get();
        Path workingDir = defaultKieBuilder.getInfo().getPrjPath();
        KieModuleMetaData kieModuleMetaData =kieModuleMetaDataCache.getMetadata(workingDir);

        if(kieModuleMetaData != null){
            return getInnerBuilderWithAlreadyPresentData(project, kieModuleMetaData);
        }else {
            return runNewBuild(project, builder);
        }
    }


    private InnerBuilder getInnerBuilderWithAlreadyPresentData(KieProject project,KieModuleMetaData kieModuleMetaData) {
        KieAFBuilder builder = builderCache.getBuilder(project.getRootPath().toURI().toString());
        Path workingDir = ((DefaultKieAFBuilder)builder).getInfo().getPrjPath();
        final Set<String> javaResources = new HashSet<String>(dependenciesCache.getDependenciesRaw(workingDir));
        final TypeSourceResolver typeSourceResolver = new TypeSourceResolver(kieModuleMetaData, javaResources);
        return new InnerBuilder(project, kieModuleMetaData, typeSourceResolver, resourcesHolder);
    }

    private InnerBuilder runNewBuild(KieProject project, Optional<KieAFBuilder> builder) {
        KieModuleMetaData kieModuleMetaData;
        KieCompilationResponse res = builder.get().build(Boolean.TRUE, Boolean.FALSE);// this could be readed from the ui
        if (res.isSuccessful() && res.getKieModule().isPresent() && res.getWorkingDir().isPresent()) {
            kieModuleMetaData = new KieModuleMetaDataImpl((InternalKieModule) res.getKieModule().get(),
                                                          res.getProjectDependenciesAsURI().get());
            kieModuleMetaDataCache.addKieMetaData(res.getWorkingDir().get(), kieModuleMetaData);
            if (res.getProjectDependenciesRaw().isPresent()) {
                final Set<String> javaResources = new HashSet<String>(res.getProjectDependenciesRaw().get());
                final TypeSourceResolver typeSourceResolver = new TypeSourceResolver(kieModuleMetaData,
                                                                                     javaResources);

                return new InnerBuilder(project, kieModuleMetaData, typeSourceResolver, resourcesHolder);
            } else {
                throw new RuntimeException("Failed to build correctly the project:" + project.toString());
            }
        } else {
            throw new RuntimeException("Failed to build correctly the project:" + project.toString());
        }
    }


    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

        private final KieProject project;
        private final KieModuleMetaData kieModuleMetaData;
        private final TypeSourceResolver typeSourceResolver;
        private final ClassLoadersResourcesHolder classloadersResourcesHolder;

        private InnerBuilder(final KieProject project,
                             final KieModuleMetaData kieModuleMetaData,
                             final TypeSourceResolver typeSourceResolver,
                             final ClassLoadersResourcesHolder classloadersResourcesHolder) {
            this.project = project;
            this.kieModuleMetaData = kieModuleMetaData;
            this.typeSourceResolver = typeSourceResolver;
            this.classloadersResourcesHolder = classloadersResourcesHolder;
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
            Path prjRoot =  builderCache.getProjectRoot(project.getRootPath().toURI().toString());
            WhiteList whiteList = getFilteredPackageNames();
            for (final String packageName : whiteList) {
                pdBuilder.addPackage(packageName);
                addClasses(packageName, kieModuleMetaData.getClasses(packageName), prjRoot);
                addClasses(packageName, classloadersResourcesHolder.getTargetsProjectDependenciesFiltered(prjRoot, packageName), prjRoot);
            }
        }

        /**
         * @return A "white list" of package names that are available for authoring
         */
        private WhiteList getFilteredPackageNames() {
            DefaultKieAFBuilder builder = (DefaultKieAFBuilder) builderCache.getBuilder(project.getRootPath().toURI().toString());
            Collection<String> pkgs = kieModuleMetaData.getPackages();
            //@TODO change /global with guvnor repo
            Set<String> filtered = CompilerClassloaderUtils.filterPathClasses(classloadersResourcesHolder.getTargetsProjectDependencies(builder.getInfo().getPrjPath()), "global/");
            pkgs.addAll(filtered);
            return packageNameWhiteListService.filterPackageNames(project, pkgs);
        }

        private void addClasses(final String packageName, final Collection<String> classes, Path projectPath) {
            for (final String className : classes) {
                addClass(packageName, className, projectPath);
            }
        }

        private void addClass(final Import item) {
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(item.getType());//@ŢODO is it correct again the use of this classloader instead of the prj classloader ?
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

        private void addClass(final String packageName, final String className, Path project) {
            try {
                Optional<MapClassLoader> prjClassloaderOpt = classloadersResourcesHolder.getTargetClassLoader(project);
                if(prjClassloaderOpt.isPresent()) {

                    final Class clazz = CompilerClassloaderUtils.getClass(packageName,
                                                                          className,
                                                                          (MapClassLoader) prjClassloaderOpt.get());

                    if(clazz != null) {
                        pdBuilder.addClass(clazz,
                                           kieModuleMetaData.getTypeMetaInfo(clazz).isEvent(),
                                           typeSourceResolver.getTypeSource(clazz));
                    }
                }
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
