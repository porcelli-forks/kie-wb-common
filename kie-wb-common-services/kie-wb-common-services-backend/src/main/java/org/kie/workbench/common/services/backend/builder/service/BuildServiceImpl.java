/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.service;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;

import org.kie.workbench.common.services.backend.compiler.impl.classloader.ClassLoaderProviderImpl;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassloadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl implements BuildService {

    private BuildServiceHelper buildServiceHelper;

    private KieProjectService projectService;

    private KieAFBuilder kieAfBuilder;

    private GuvnorM2Repository guvnorM2Repository;

    private ClassloadersResourcesHolder classloadersResourcesHolder;

    private CompilerMapsHolder compilerMapsHolder;

    private ClassLoaderProviderImpl provider ;

    public BuildServiceImpl( ) {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl(final KieProjectService projectService,
                            final BuildServiceHelper buildServiceHelper,
                            final GuvnorM2Repository guvnorM2Repository,
                            final CompilerMapsHolder compilerMapsHolder,
                            final ClassloadersResourcesHolder classloadersResourcesHolder) {
        this.projectService = projectService;
        this.buildServiceHelper = buildServiceHelper;
        //this.kieAfBuilder = new DefaultKieAFBuilder("", guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),compilerMapsHolder);
        this.guvnorM2Repository = guvnorM2Repository;
        this.classloadersResourcesHolder = classloadersResourcesHolder;
        provider = new ClassLoaderProviderImpl();
    }

    @Override
    public BuildResults build( final Project project ) {
        return buildInternal(project);
    }

    private BuildResults buildAndDeployInternal(final Project project){
        KieCompilationResponse res = kieAfBuilder.buildAndInstall(project.getRootPath().toString(),guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));
        return MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
    }

    private BuildResults buildInternal(final Project project){
        if(kieAfBuilder == null){
            this.kieAfBuilder = new DefaultKieAFBuilder(org.uberfire.java.nio.file.Paths.get("file://"+project.getRootPath().toURI().toString()), guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME),compilerMapsHolder);
        }
        KieCompilationResponse res = kieAfBuilder.build(project.getRootPath().toString(),guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));
        return MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
    }

    private IncrementalBuildResults buildIncrementallyInternal(final Project project){
        //@TODO check if is correct this conversion
        org.uberfire.java.nio.file.Path nioPath = Paths.convert(project.getRootPath());
        KieCompilationResponse res;

        if(classloadersResourcesHolder.containsPomDependencies(nioPath)){ //the pom deps are present we refresh the target deps
            res = kieAfBuilder.build(project.getRootPath().toString(),guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));
            readAndSetResourcesFromTargetFolders(nioPath);
        }else{
            res = kieAfBuilder.build(project.getRootPath().toString(),guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME), Boolean.FALSE);
            if(res.getProjectDependenciesAsURI().isPresent()){
                List<String> urisTOStrings = res.getProjectDependenciesAsURI().get().stream()
                        .map(URI::toString)
                        .collect(Collectors.toList());
                classloadersResourcesHolder.addPomDependencies(nioPath, urisTOStrings);
            }
            readAndSetResourcesFromTargetFolders(nioPath);
        }
        return MavenOutputConverter.convertIntoIncrementalBuildResults(res.getMavenOutput().get());
    }

    private void readAndSetResourcesFromTargetFolders(org.uberfire.java.nio.file.Path nioPath) {
        Optional<List<String>> targetResources = provider.getStringFromTargets(nioPath);
        if(targetResources.isPresent()) {
            classloadersResourcesHolder.replaceTargetDependencies(nioPath, targetResources.get());
        }
    }

    @Override
    public BuildResults buildAndDeploy( final Project project ) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final DeploymentMode mode ) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers ) {
        return buildAndDeployInternal(project);
    }

    @Override
    public BuildResults buildAndDeploy( final Project project,
                                        final boolean suppressHandlers,
                                        final DeploymentMode mode ) {
        return buildAndDeployInternal(project);
    }

    @Override
    public boolean isBuilt( final Project project ) {
        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(URI.create(project.toString()));
        return classloadersResourcesHolder.containsPomDependencies( path);
    }

    @Override
    public IncrementalBuildResults addPackageResource( final Path resource ) {
        Project project = projectService.resolveProject( resource );
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults deletePackageResource( final Path resource ) {
        Project project = projectService.resolveProject( resource );
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults updatePackageResource( final Path resource ) {
        Project project = projectService.resolveProject( resource );
        return buildIncrementallyInternal(project);
    }

    @Override
    public IncrementalBuildResults applyBatchResourceChanges( final Project project,
                                                              final Map< Path, Collection< ResourceChange > > changes ) {
        if ( project == null ) {
            return new IncrementalBuildResults( );
        }
        return buildIncrementallyInternal(project);
    }

}