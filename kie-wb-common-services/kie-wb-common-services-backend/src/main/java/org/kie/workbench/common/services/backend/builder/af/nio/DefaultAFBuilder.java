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
package org.kie.workbench.common.services.backend.builder.af.nio;

import java.nio.file.Paths;
import java.util.HashMap;

import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOMavenCompilerFactory;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOWorkspaceCompilationInfo;

public class DefaultAFBuilder implements AFBuilder {

    private NIOMavenCompiler compiler;
    private NIOWorkspaceCompilationInfo info;
    private NIOCompilationRequest req;
    private String mavenRepo;
    private String projectRepo;

    /***
     *Constructor to define the default behaviour called with the build method
     * @param projectRepo
     * @param mavenRepo
     * @param args maven cli args
     */
    public DefaultAFBuilder(String projectRepo,
                            String mavenRepo,
                            String[] args) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.projectRepo = projectRepo;
        compiler = NIOMavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new NIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               args,
                                               new HashMap(),
                                               Boolean.TRUE);
    }

    public DefaultAFBuilder(String projectRepo,
                            String mavenRepo) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.projectRepo = projectRepo;
        compiler = NIOMavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new NIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new NIODefaultCompilationRequest(mavenRepo,
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
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               new String[]{MavenCLIArgs.PACKAGE},
                                               new HashMap(),
                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall() {
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               new String[]{MavenCLIArgs.INSTALL},
                                               new HashMap(),
                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String mavenRepo) {
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String projectPath,
                                     String mavenRepo) {
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndPackage(String projectPath,
                                               String mavenRepo) {
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     new String[]{MavenCLIArgs.PACKAGE},
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall(String projectPath,
                                               String mavenRepo) {
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
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
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
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

        NIOMavenCompiler compiler = NIOMavenCompilerFactory.getCompiler(decorator);
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     args,
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }
}