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

import java.util.*;
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
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;

import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.ClassLoadersResourcesHolder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter;
import org.kie.workbench.common.services.backend.compiler.impl.utils.PathConverter;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceChange;

@Service
@ApplicationScoped
public class BuildServiceImpl implements BuildService {

    private KieProjectService projectService;

    private GuvnorM2Repository guvnorM2Repository;

    private ClassLoadersResourcesHolder classloadersResourcesHolder;

    private CompilerMapsHolder compilerMapsHolder;

    private User user;

    public BuildServiceImpl( ) {
        //Empty constructor for Weld
    }

    @Inject
    public BuildServiceImpl(final KieProjectService projectService,
                            final GuvnorM2Repository guvnorM2Repository,
                            final CompilerMapsHolder compilerMapsHolder,
                            final ClassLoadersResourcesHolder classloadersResourcesHolder,
                            final User user) {
        this.projectService = projectService;
        this.compilerMapsHolder = compilerMapsHolder;
        this.guvnorM2Repository = guvnorM2Repository;
        this.classloadersResourcesHolder = classloadersResourcesHolder;
        this.user = user;
    }

    @Override
    public BuildResults build( final Project project ) {
        String username = user.getIdentifier();
        return buildInternal(project);
    }

    private BuildResults buildAndDeployInternal(final Project project){
        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(PathConverter.getNioPath(project),
                                                                compilerMapsHolder,
                                                                guvnorM2Repository);
        KieCompilationResponse res = kieAfBuilder.buildAndInstall(project.getRootPath().toString(),guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME));
        return MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
    }

    private BuildResults buildInternal(final Project project){
            //@TODO build senwithout classloader creation ot without classloader creation ?
        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(PathConverter.getNioPath(project),
                                                                compilerMapsHolder,
                                                                guvnorM2Repository);

        KieCompilationResponse res = kieAfBuilder.build(Boolean.TRUE, Boolean.FALSE);
        return MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
    }


    private IncrementalBuildResults buildIncrementallyInternal(final Project project){

        KieAFBuilder kieAfBuilder = KieAFBuilderUtil.getKieAFBuilder(PathConverter.getNioPath(project),
                                                                     compilerMapsHolder,
                                                                     guvnorM2Repository);
        KieCompilationResponse res = kieAfBuilder.build(Boolean.TRUE, Boolean.FALSE);
        return MavenOutputConverter.convertIntoIncrementalBuildResults(res.getMavenOutput().get());
    }

    @Override
    public BuildResults buildAndDeploy(final Project project) {
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
        org.uberfire.java.nio.file.Path path = Paths.convert(project.getRootPath());
        return compilerMapsHolder.getBuilder(path) != null;//@TODO check if could be better the classloaderHolder
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