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
package org.kie.workbench.common.services.backend.builder.af.internalNIOimpl;

import java.util.HashMap;

import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIOMavenCompilerFactory;
import org.uberfire.java.nio.file.Paths;

public class DefaultInternalNIOAFBuilder implements AFBuilder {

    private InternalNIOMavenCompiler compiler;
    private InternalNIOWorkspaceCompilationInfo info;
    private InternalNIOCompilationRequest req;
    private String mavenRepo;
    private String projectRepo;

    public DefaultInternalNIOAFBuilder(String projectRepo,
                                       String mavenRepo,
                                       String[] args) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.projectRepo = projectRepo;
        compiler = InternalNIOMavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       args,
                                                       new HashMap(),
                                                       Boolean.TRUE);
    }

    public DefaultInternalNIOAFBuilder(String projectRepo,
                                       String mavenRepo) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.projectRepo = projectRepo;
        compiler = InternalNIOMavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.COMPILE},
                                                       new HashMap(),
                                                       Boolean.TRUE);
    }

    @Override
    public CompilationResponse build() {
        req.getKieCliRequest().getMap().clear();
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndPackage() {
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.PACKAGE},
                                                       new HashMap(),
                                                       Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall() {
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.INSTALL},
                                                       new HashMap(),
                                                       Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String mavenRepo) {
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String projectPath,
                                     String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndPackage(String projectPath,
                                               String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.PACKAGE},
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall(String projectPath,
                                               String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.INSTALL},
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildSpecialized(String projectPath,
                                                String mavenRepo,
                                                String[] args) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     args,
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildSpecialized(String projectPath,
                                                String mavenRepo,
                                                String[] args,
                                                Decorator decorator) {

        InternalNIOMavenCompiler compiler = InternalNIOMavenCompilerFactory.getCompiler(decorator);
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     args,
                                                                                     new HashMap(),
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }
}
