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
package org.kie.workbench.common.services.backend.whitelist;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

/**
 * Represents a "white list" of permitted package names for use with authoring
 */
@Service
@ApplicationScoped
public class PackageNameWhiteListServiceImpl
        implements PackageNameWhiteListService {

    private IOService         ioService;
    private KieProjectService projectService;
    private PackageNameWhiteListLoader loader;
    private PackageNameWhiteListSaver saver;
    private CompilerMapsHolder compilerMapsHolder;
    private String FILE_URI = "file://";
    private String PACKAGE_NAME_WHITE_LIST = "package-names-white-list";

    public PackageNameWhiteListServiceImpl() {
    }

    @Inject
    public PackageNameWhiteListServiceImpl( final @Named( "ioStrategy" ) IOService ioService,
                                            final KieProjectService projectService,
                                            final PackageNameWhiteListLoader loader,
                                            final PackageNameWhiteListSaver saver,
                                            final CompilerMapsHolder compilerMapsHolder) {
        this.ioService = ioService;
        this.projectService = projectService;
        this.loader = loader;
        this.saver = saver;
        this.compilerMapsHolder = compilerMapsHolder;
    }

    @Override
    public void createProjectWhiteList( final Path packageNamesWhiteListPath ) {
        if ( ioService.exists( Paths.convert( packageNamesWhiteListPath ) ) ) {
            throw new FileAlreadyExistsException( packageNamesWhiteListPath.toString() );
        } else {
            ioService.write( Paths.convert( packageNamesWhiteListPath ),
                             "" );
        }
    }

    /**
     * Filter the provided Package names by the Project's white list
     * @param project Project for which to filter Package names
     * @param packageNames All Package names in the Project
     * @return A filtered collection of Package names
     */
    @Override
    public WhiteList filterPackageNames( final Project project,
                                         final Collection<String> packageNames) {
        if ( packageNames == null ) {
            return new WhiteList();
        } else if ( project instanceof KieProject ) {

            org.uberfire.java.nio.file.Path workingDir = compilerMapsHolder.getProjectRoot(project.getRootPath());
            org.uberfire.java.nio.file.Path pnwl = org.uberfire.java.nio.file.Paths.get(FILE_URI + workingDir.toUri()+"/"+PACKAGE_NAME_WHITE_LIST );
            final WhiteList whiteList = load( Paths.convert(pnwl));

            if ( whiteList.isEmpty() ) {
                return new WhiteList( packageNames );
            } else {
                for ( Package aPackage : projectService.resolvePackages( project ) ) {
                    whiteList.add( aPackage.getPackageName() );
                }

                return new PackageNameWhiteListFilter( packageNames,
                                                       whiteList ).getFilteredPackageNames();
            }
        } else {
            return new WhiteList( packageNames );
        }
    }

    @Override
    public WhiteList load( final Path packageNamesWhiteListPath ) {
        return loader.load( packageNamesWhiteListPath );
    }

    @Override
    public Path save( final Path path,
                      final WhiteList content,
                      final Metadata metadata,
                      final String comment ) {
        return saver.save( path,
                           content,
                           metadata,
                           comment );
    }
}

