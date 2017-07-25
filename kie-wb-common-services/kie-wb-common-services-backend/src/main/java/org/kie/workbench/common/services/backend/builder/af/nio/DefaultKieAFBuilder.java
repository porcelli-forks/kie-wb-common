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
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.impl.NIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.nio.impl.kie.NIOKieMavenCompilerFactory;

public class DefaultKieAFBuilder implements KieAFBuilder {

    private NIOKieMavenCompiler compiler;
    private NIOWorkspaceCompilationInfo info;
    private NIOCompilationRequest req;
    private String mavenRepo;

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = NIOKieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new NIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               new String[]{MavenCLIArgs.COMPILE},
                                               new HashMap(),
                                               Boolean.TRUE);
    }

    public DefaultKieAFBuilder(String projectRepo,
                               String mavenRepo,
                               String[] args) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = NIOKieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new NIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               args,
                                               new HashMap(),
                                               Boolean.TRUE);
    }

    @Override
    public KieCompilationResponse build() {
        req.getKieCliRequest().getMap().clear();
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage() {
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               new String[]{MavenCLIArgs.PACKAGE},
                                               new HashMap(),
                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall() {
        req = new NIODefaultCompilationRequest(mavenRepo,
                                               info,
                                               new String[]{MavenCLIArgs.INSTALL},
                                               new HashMap(),
                                               Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String mavenRepo) {
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String projectPath,
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
    public KieCompilationResponse buildAndPackage(String projectPath,
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
    public KieCompilationResponse buildAndInstall(String projectPath,
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
    public KieCompilationResponse buildSpecialized(String projectPath,
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
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator) {
        NIOKieMavenCompiler compiler = NIOKieMavenCompilerFactory.getCompiler(decorator);
        NIOWorkspaceCompilationInfo info = new NIOWorkspaceCompilationInfo(Paths.get(projectPath));
        NIOCompilationRequest req = new NIODefaultCompilationRequest(mavenRepo,
                                                                     info,
                                                                     args,
                                                                     new HashMap(),
                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }
}
