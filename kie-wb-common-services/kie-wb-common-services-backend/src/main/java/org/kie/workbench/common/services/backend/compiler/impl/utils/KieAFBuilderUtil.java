/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.net.URI;
import java.util.UUID;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;

import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieDefaultMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

public class KieAFBuilderUtil {


    public static KieAFBuilder getKieAFBuilder(org.uberfire.java.nio.file.Path nioPath,
                                               CompilerMapsHolder compilerMapsHolder,
                                               GuvnorM2Repository guvnorM2Repository, String user) {

        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        if (builder == null) {
            if (nioPath.getFileSystem() instanceof JGitFileSystem) {
                String folderName = getFolderName(nioPath, user);
                Git repo = JGitUtils.tempClone((JGitFileSystem) nioPath.getFileSystem(), folderName);
                compilerMapsHolder.addGit((JGitFileSystem) nioPath.getFileSystem(), repo);
                org.uberfire.java.nio.file.Path prj = org.uberfire.java.nio.file.Paths.get(URI.create(repo.getRepository().getDirectory().toPath().getParent().toAbsolutePath().toUri().toString() + nioPath.toString()));
                builder = new DefaultKieAFBuilder(prj,
                        MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                        getCompiler(compilerMapsHolder),
                        compilerMapsHolder);
                compilerMapsHolder.addBuilder(nioPath, builder);
            }
        }
        return builder;
    }

    public static String getFolderName(Path nioPath, String user) {
        //@TODO currently the only way to understand if is a imported prj
        return nioPath.toUri().toString().contains("@myrepo") ? UUID.randomUUID().toString() : user +"-" + UUID.randomUUID().toString();
    }

    public static AFCompiler getCompiler(CompilerMapsHolder compilerMapsHolder) {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator,
                compilerMapsHolder);
        return outerDecorator;
    }

    public static Path getFSPath(KieProject project,
                                 CompilerMapsHolder compilerMapsHolder,
                                 GuvnorM2Repository guvnorM2Repository, String user) {

        Path nioPath = Paths.convert(project.getRootPath());
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(nioPath,
                compilerMapsHolder, guvnorM2Repository, user);
        Path prjPath = ((DefaultKieAFBuilder) builder).getInfo().getPrjPath();
        return prjPath;
    }

    public static String getIdentifier(Instance<User> identity) {
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
