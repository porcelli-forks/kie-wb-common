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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.jgit.api.Git;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class DefaultCompilerMapsHolder implements CompilerMapsHolder {

    private Map<JGitFileSystem, Git> gitMap;
    private Map<Path, KieAFBuilder> buildersMap;

    public DefaultCompilerMapsHolder() {
        gitMap = new ConcurrentHashMap<>();
        buildersMap = new ConcurrentHashMap<>();
    }

    /**
     * GIT
     */

    public Git getGit(JGitFileSystem key) {
        return gitMap.get(key);
    }

    public boolean addGit(JGitFileSystem key, Git git) { return gitMap.put(key, git) != null; }

    public Git removeGit(JGitFileSystem key) {
        return gitMap.remove(key);
    }

    public boolean containsGit(JGitFileSystem key) {
        return gitMap.containsKey(key);
    }

    public void clearGitMap() {
        gitMap.clear();
    }

    /**
     * BUILDER
     */

    public KieAFBuilder getBuilder(Path projectRootPath) {
        return buildersMap.get(projectRootPath);
    }

    public boolean addBuilder(final Path projectRootPath, final KieAFBuilder builder) {
        return buildersMap.put(projectRootPath, builder) != null;
    }

    public KieAFBuilder removeBuilder(Path projectRootPath) {
        return buildersMap.remove(projectRootPath);
    }

    public boolean containsBuilder(Path projectRootPath) {
        return buildersMap.containsKey(projectRootPath);
    }

    public void clearBuilderMap() {
        buildersMap.clear();
    }

}