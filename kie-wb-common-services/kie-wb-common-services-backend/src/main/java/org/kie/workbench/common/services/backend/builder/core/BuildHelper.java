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

package org.kie.workbench.common.services.backend.builder.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;

import org.guvnor.common.services.project.model.GAV;

import org.guvnor.common.services.project.model.Project;

import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;

import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.MavenOutputConverter;

import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class BuildHelper {

    //@MAXWasHere

    private static final Logger logger = LoggerFactory.getLogger( BuildHelper.class );

    private POMService pomService;

    private ExtendedM2RepoService m2RepoService;

    private KieProjectService projectService;

    private Instance< User > identity;

    private GuvnorM2Repository guvnorM2Repository;

    private CompilerMapsHolder compilerMapsHolder;

    public BuildHelper( ) {
    }

    @Inject
    public BuildHelper( final POMService pomService,
                        final ExtendedM2RepoService m2RepoService,
                        final KieProjectService projectService,
                        final Instance< User > identity,
                        final GuvnorM2Repository guvnorM2Repository, final CompilerMapsHolder compilerMapsHolder) {
        this.pomService = pomService;
        this.m2RepoService = m2RepoService;
        this.projectService = projectService;
        this.identity = identity;
        this.guvnorM2Repository = guvnorM2Repository;
        this.compilerMapsHolder = compilerMapsHolder;
    }

    public BuildResult build( final Project project ) {
        try {
            //@TODO AFBuilder from the CompilerMapsHolder
            KieAFBuilder builder = new DefaultKieAFBuilder(project.getRootPath().toURI().toString(), guvnorM2Repository.getM2RepositoryRootDir(ArtifactRepositoryService.LOCAL_M2_REPO_NAME), compilerMapsHolder);
            KieCompilationResponse res = builder.build();
            if(res.isSuccessful()) {
                final BuildResults results = MavenOutputConverter.convertIntoBuildResults(res.getMavenOutput().get());
                BuildMessage infoMsg = new BuildMessage();
                infoMsg.setLevel(Level.INFO);
                infoMsg.setText(buildResultMessage(project,
                                                   results).toString());

                results.addBuildMessage(0,
                                        infoMsg);

                return new BuildResult(builder,
                                       results);
            }else{
                return new BuildResult(builder, new BuildResults());
            }

        } catch ( Exception e ) {
            logger.error( e.getMessage( ),
                    e );
            return new BuildResult( null, buildExceptionResults( e, project.getPom( ).getGav( ) ) );
        }
    }

    private StringBuffer buildResultMessage( final Project project,
                                             final BuildResults results ) {
        StringBuffer message = new StringBuffer( );

        message.append( "Build of project '" );
        message.append( project.getProjectName( ) );
        message.append( "' (requested by " );
        message.append( getIdentifier( ) );
        message.append( ") completed.\n" );
        message.append( " Build: " );
        message.append( results.getErrorMessages( ).isEmpty( ) ? "SUCCESSFUL" : "FAILURE" );

        return message;
    }

    /**
     * When an exception is produced by the builder service, this method is uses to generate an instance of
     * <code>org.guvnor.common.services.project.builder.model.BuildResults</code> in generated with the exception details.
     * @param e The error exception.
     * @param gav
     * @return An instance of BuildResults with the exception details.
     */
    public BuildResults buildExceptionResults( Exception e,
                                               GAV gav ) {
        BuildResults exceptionResults = new BuildResults( gav );
        BuildMessage exceptionMessage = new BuildMessage( );
        exceptionMessage.setLevel( Level.ERROR );
        exceptionMessage.setText( e.getMessage( ) );
        exceptionResults.addBuildMessage( exceptionMessage );

        return exceptionResults;
    }


    public class BuildResult {

        private KieAFBuilder builder;

        private BuildResults buildResults;

        private IncrementalBuildResults incrementalBuildResults;

        public BuildResult( KieAFBuilder builder, BuildResults buildResults ) {
            this.builder = builder;
            this.buildResults = buildResults;
        }

        public BuildResult( KieAFBuilder builder, IncrementalBuildResults incrementalBuildResults ) {
            this.builder = builder;
            this.incrementalBuildResults = incrementalBuildResults;
        }

        public KieAFBuilder getBuilder( ) {
            return builder;
        }

        public BuildResults getBuildResults( ) {
            return buildResults;
        }

        public IncrementalBuildResults getIncrementalBuildResults( ) {
            return incrementalBuildResults;
        }
    }
    
    private String getIdentifier( ) {
        if ( identity.isUnsatisfied( ) ) {
            return "system";
        }
        try {
            return identity.get( ).getIdentifier( );
        } catch ( ContextNotActiveException e ) {
            return "system";
        }
    }
}