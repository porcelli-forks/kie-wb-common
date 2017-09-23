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
package org.kie.workbench.common.services.backend.compiler.impl.share;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

/**
 * Holder of the maps used in the compiler
 **/
public interface CompilerMapsHolder {

    //GIT

    Git getGit(JGitFileSystem key);

    boolean addGit(JGitFileSystem key,
                   Git git);

    Git removeGit(JGitFileSystem key);

    boolean containsGit(JGitFileSystem key);

    void clearGitMap();

    //BUILDER

    KieAFBuilder getBuilder(Path projectRootPath);

    boolean addBuilder(Path projectRootPath,
                       KieAFBuilder builder);

    KieAFBuilder removeBuilder(Path projectRootPath);

    boolean containsBuilder(Path projectRootPath);

    void clearBuilderMap();

    //KieModueleMetaData

    KieModuleMetaData getMetadata(Path projectRootPath);

    void addKieMetaData(Path projectRootPath,
                        KieModuleMetaData metadata);

    boolean removeKieModuleMetaData(Path projectRootPath);

    void replaceKieMetaData(Path projectRootPath,
                            KieModuleMetaData metadata);

    // Dependencies Raw

    List<String> getDependenciesRaw(Path projectRootPath);

    void addDependenciesRaw(Path projectRootPath,
                            List<String> depsRaw);

    boolean removeDependenciesRaw(Path projectRootPath);

    void replaceDependenciesRaw(Path projectRootPath,
                                List<String> depsRaw);
}
