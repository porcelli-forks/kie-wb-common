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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@ApplicationScoped
public class DefaultCompilerMapsHolder implements CompilerMapsHolder {

    private Map<JGitFileSystem, Git> gitMap;
    private Map<String, KieAFBuilder> buildersMap;
    private Map<Path, KieModuleMetaData> kieMetaDataMap;
    private Map<Path, List<String>> depsRawMap;
    private Map<String, Path> alias ;

    @Inject
    private Instance< User > identity;

    public DefaultCompilerMapsHolder() {
        gitMap = new ConcurrentHashMap<>();
        buildersMap = new ConcurrentHashMap<>();
        kieMetaDataMap = new ConcurrentHashMap<>();
        depsRawMap = new ConcurrentHashMap<>();
        alias = new ConcurrentHashMap<>();
    }

    /**
     * GIT
     */

    public Git getGit(JGitFileSystem key) {
        return gitMap.get(key);
    }

    public boolean addGit(JGitFileSystem key,
                          Git git) {
        return gitMap.put(key,
                git) != null;
    }

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

    public KieAFBuilder getBuilder(String uri) {
        return buildersMap.get(uri);
    }

    public boolean addBuilder(final String uri , final KieAFBuilder builder) {
        return buildersMap.put(uri, builder) != null;
    }

    public KieAFBuilder removeBuilder(String uri) {
        return buildersMap.remove(uri);
    }

    public boolean containsBuilder(String uri) {
        return buildersMap.containsKey(uri);
    }

    public void clearBuilderMap() {
        buildersMap.clear();
    }


    /**
     * KIE METADATA
     */

    public KieModuleMetaData getMetadata(Path projectRootPath) {
        return kieMetaDataMap.get(projectRootPath);
    }

    public void addKieMetaData(Path projectRootPath,
                               KieModuleMetaData metadata) {
        kieMetaDataMap.put(projectRootPath, metadata);
    }

    public boolean removeKieModuleMetaData(Path projectRootPath) {
        return kieMetaDataMap.remove(projectRootPath) != null;
    }

    public void replaceKieMetaData(Path projectRootPath,
                                   KieModuleMetaData metadata) {
        kieMetaDataMap.replace(projectRootPath, metadata);
    }

    /**
     * DEPENDENCIES
     */

    public List<String> getDependenciesRaw(Path projectRootPath) {
        return depsRawMap.get(projectRootPath);
    }

    public void addDependenciesRaw(Path projectRootPath,
                                   List<String> depsRaw) {
        depsRawMap.put(projectRootPath, depsRaw);
    }

    public boolean removeDependenciesRaw(Path projectRootPath) {
        return depsRawMap.remove(projectRootPath) != null;
    }

    public void replaceDependenciesRaw(Path projectRootPath,
                                       List<String> depsRaw) {
        depsRawMap.replace(projectRootPath, depsRaw);
    }



    /**
     * ALIAS
     */

    public boolean addAlias(String gitURI, Path workingFolder) {
        return alias.put(gitURI, workingFolder) != null;
    }

    public Path getWorkingFolder(String gitURI) {
        return alias.get(gitURI);
    }

    public void removeAlias(String gitURI) {
        alias.remove(gitURI);
    }

    /**
     * UTILS
     */

    @Override
    public Path getProjectRoot( String uri ) {
        KieAFBuilder builder = getBuilder(uri);
        if (builder != null) {
            Path prjRoot = ((DefaultKieAFBuilder) builder).getInfo().getPrjPath();
            return prjRoot;
        } else {
            return  org.uberfire.java.nio.file.Paths.get(URI.create(uri));
        }
    }

}