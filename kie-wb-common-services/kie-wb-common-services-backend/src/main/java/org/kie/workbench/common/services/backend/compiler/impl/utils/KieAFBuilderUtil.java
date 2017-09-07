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

import org.eclipse.jgit.api.Git;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.nio.DefaultKieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieDefaultMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.share.CompilerMapsHolder;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

import java.net.URI;
import java.util.UUID;


public class KieAFBuilderUtil {

    public static KieAFBuilder getKieAFBuilder(org.uberfire.java.nio.file.Path nioPath, CompilerMapsHolder compilerMapsHolder, GuvnorM2Repository guvnorM2Repository) {
        KieAFBuilder builder = compilerMapsHolder.getBuilder(nioPath);
        if (builder == null) {
            if (nioPath.getFileSystem() instanceof JGitFileSystem) {
                Git repo = JGitUtils.tempClone((JGitFileSystem) nioPath.getFileSystem(), UUID.randomUUID().toString());
                compilerMapsHolder.addGit((JGitFileSystem) nioPath.getFileSystem(), repo);
                org.uberfire.java.nio.file.Path prj = org.uberfire.java.nio.file.Paths.get(URI.create(repo.getRepository().getDirectory().toPath().getParent().toAbsolutePath().toUri().toString() + nioPath.toString()));
                builder = new DefaultKieAFBuilder(prj, MavenUtils.getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)), getCompiler(compilerMapsHolder), compilerMapsHolder);
                compilerMapsHolder.addBuilder(nioPath, builder);
            }
        }
        return builder;
    }

    private static AFCompiler getCompiler(CompilerMapsHolder compilerMapsHolder) {
        // we create the compiler in this weird mode to use the gitMap used internally
        AFCompiler innerDecorator = new KieAfterDecorator(new OutputLogAfterDecorator(new KieDefaultMavenCompiler()));
        AFCompiler outerDecorator = new JGITCompilerBeforeDecorator(innerDecorator,
                compilerMapsHolder);
        return outerDecorator;
    }
}
