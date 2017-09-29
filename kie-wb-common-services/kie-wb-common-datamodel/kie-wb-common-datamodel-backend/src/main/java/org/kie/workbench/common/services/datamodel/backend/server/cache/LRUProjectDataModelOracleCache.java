/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.backend.compiler.impl.utils.KieAFBuilderUtil;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A simple LRU cache for Project DataModelOracles
 */
@ApplicationScoped
@Named("ProjectDataModelOracleCache")
public class LRUProjectDataModelOracleCache extends LRUCache<org.uberfire.java.nio.file.Path, ProjectDataModelOracle> {

    private ProjectDataModelOracleBuilderProvider builderProvider;
    private KieProjectService projectService;
    private CompilerMapsHolder compilerMapsHolder;
    private GuvnorM2Repository guvnorM2Repository;

    public LRUProjectDataModelOracleCache() {
    }

    @Inject
    public LRUProjectDataModelOracleCache( final ProjectDataModelOracleBuilderProvider builderProvider,
                                           final KieProjectService projectService,
                                           final CompilerMapsHolder compilerMapsHolder,
                                           final GuvnorM2Repository guvnorM2Repository) {
        this.builderProvider = builderProvider;
        this.projectService = projectService;
        this.compilerMapsHolder = compilerMapsHolder;
        this.guvnorM2Repository = guvnorM2Repository;
    }

    public synchronized void invalidateProjectCache( @Observes final InvalidateDMOProjectCacheEvent event ) {
        PortablePreconditions.checkNotNull( "event",
                                            event );
        final Path resourcePath = event.getResourcePath();
        final KieProject project = projectService.resolveProject( resourcePath );
        org.uberfire.java.nio.file.Path workingDir = compilerMapsHolder.getProjectRoot(project.getRootPath());
        //If resource was not within a Project there's nothing to invalidate
        if ( project != null ) {
            invalidateCache( workingDir );//@TOdo AFTER THE STARTUP INDEXING THE FIRST IMPORT CAUSE THE INVALIDATION CACHE OF THE PRJ INDEXED DURING THE STARTUP
        }
    }



    //Check the ProjectOracle for the Project has been created, otherwise create one!
    public synchronized ProjectDataModelOracle assertProjectDataModelOracle( final KieProject project) {
        ProjectDataModelOracle  projectOracle;
        org.uberfire.java.nio.file.Path workingDir = compilerMapsHolder.getProjectRoot(project.getRootPath());
        if(workingDir == null){
            projectOracle = buildAndSetEntry(project);
        }else{
            projectOracle = getEntry( workingDir );
            if ( projectOracle == null ) {
                projectOracle = buildAndSetEntry(project);
            }
        }
        return projectOracle;

    }

    private ProjectDataModelOracle buildAndSetEntry(KieProject project) {
        ProjectDataModelOracle projectOracle;
        org.uberfire.java.nio.file.Path workingDir;
        projectOracle = makeProjectOracle(project );
        workingDir = compilerMapsHolder.getProjectRoot(project.getRootPath());
        setEntry( workingDir, projectOracle );
        return projectOracle;
    }

    private ProjectDataModelOracle makeProjectOracle( final KieProject project ) {
        return builderProvider.newBuilder(project).build();
    }

}