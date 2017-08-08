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

import org.kie.workbench.common.services.backend.builder.af.AFBuilder;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.nio.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.MavenCompilerFactory;
import org.uberfire.java.nio.file.Paths;

public class DefaultAFBuilder implements AFBuilder {

    private AFCompiler compiler;
    private WorkspaceCompilationInfo info;
    private CompilationRequest req;
    private String mavenRepo;

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
        compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            args,
                                            Boolean.TRUE);
    }

    public DefaultAFBuilder(String projectRepo,
                            String mavenRepo) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = MavenCompilerFactory.getCompiler(Decorator.LOG_OUTPUT_AFTER);
        info = new WorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE);
    }


    public DefaultAFBuilder(String projectRepo,
                            String mavenRepo, AFCompiler compiler) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects ill be created in the compileSync */
        this.mavenRepo = mavenRepo;
        this.compiler = compiler;
        info = new WorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.COMPILE},
                                            Boolean.TRUE);
    }

    @Override
    public CompilationResponse build() {
        req.getKieCliRequest().getMap().clear();
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndPackage() {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.PACKAGE},
                                            Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall() {
        req = new DefaultCompilationRequest(mavenRepo,
                                            info,
                                            new String[]{MavenCLIArgs.INSTALL},
                                            Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String mavenRepo) {
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse build(String projectPath,
                                     String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.COMPILE},
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndPackage(String projectPath,
                                               String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.PACKAGE},
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildAndInstall(String projectPath,
                                               String mavenRepo) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{MavenCLIArgs.INSTALL},
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildSpecialized(String projectPath,
                                                String mavenRepo,
                                                String[] args) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public CompilationResponse buildSpecialized(String projectPath,
                                                String mavenRepo,
                                                String[] args,
                                                Decorator decorator) {

        AFCompiler compiler = MavenCompilerFactory.getCompiler(decorator);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }
}