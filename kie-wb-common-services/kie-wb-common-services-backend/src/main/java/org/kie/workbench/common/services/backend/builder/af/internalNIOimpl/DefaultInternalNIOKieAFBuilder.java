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

import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOWorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIODefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.kie.InternalNIOKieMavenCompilerFactory;
import org.uberfire.java.nio.file.Paths;

public class DefaultInternalNIOKieAFBuilder implements KieAFBuilder {

    private InternalNIOKieMavenCompiler compiler;
    private InternalNIOWorkspaceCompilationInfo info;
    private InternalNIOCompilationRequest req;
    private String mavenRepo;

    public DefaultInternalNIOKieAFBuilder(String projectRepo,
                                          String mavenRepo) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = InternalNIOKieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.COMPILE},
                                                       Boolean.TRUE);
    }

    public DefaultInternalNIOKieAFBuilder(String projectRepo,
                                          String mavenRepo,
                                          String[] args) {
        /**In the default construct we create the objects ready for a call to the build() without params to reuse all the internal objects,
         * only in the internal maven compilation new objects will be created in the compileSync */
        this.mavenRepo = mavenRepo;
        compiler = InternalNIOKieMavenCompilerFactory.getCompiler(KieDecorator.LOG_OUTPUT_AFTER);
        info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectRepo));
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       args,
                                                       Boolean.TRUE);
    }

    @Override
    public KieCompilationResponse build() {
        req.getKieCliRequest().getMap().clear();
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage() {
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.PACKAGE},
                                                       Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall() {
        req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                       info,
                                                       new String[]{MavenCLIArgs.INSTALL},
                                                       Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String mavenRepo) {
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse build(String projectPath,
                                        String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.COMPILE},
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndPackage(String projectPath,
                                                  String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.PACKAGE},
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildAndInstall(String projectPath,
                                                  String mavenRepo) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     new String[]{MavenCLIArgs.INSTALL},
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args) {
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     args,
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }

    @Override
    public KieCompilationResponse buildSpecialized(String projectPath,
                                                   String mavenRepo,
                                                   String[] args,
                                                   KieDecorator decorator) {
        InternalNIOKieMavenCompiler compiler = InternalNIOKieMavenCompilerFactory.getCompiler(decorator);
        InternalNIOWorkspaceCompilationInfo info = new InternalNIOWorkspaceCompilationInfo(Paths.get(projectPath));
        InternalNIOCompilationRequest req = new InternalNIODefaultCompilationRequest(mavenRepo,
                                                                                     info,
                                                                                     args,
                                                                                     Boolean.TRUE);
        return compiler.compileSync(req);
    }
}
