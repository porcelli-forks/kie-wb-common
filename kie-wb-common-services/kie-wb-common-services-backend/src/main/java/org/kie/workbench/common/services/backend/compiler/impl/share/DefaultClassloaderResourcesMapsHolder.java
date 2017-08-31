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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class DefaultClassloaderResourcesMapsHolder implements ClassloadersResourcesHolder {

    private Map<Path, Tuple> internalMap;

    public DefaultClassloaderResourcesMapsHolder() {
        internalMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<String> getPomDependencies(Path projectRootPath) {
        return internalMap.get(projectRootPath).projectsDeps;
    }

    @Override
    public List<String> getTargetsProjectDependencies(Path projectRootPath) {
        return internalMap.get(projectRootPath).targetDeps;
    }

    @Override
    public void replacePomDependencies(Path projectRootPath,
                                       List<String> uris) {
        internalMap.get(projectRootPath).replaceProjectsDeps(uris);
    }

    @Override
    public void replaceTargetDependencies(Path projectRootPath,
                                          List<String> uris) {
        internalMap.get(projectRootPath).replaceTargetDeps(uris);
    }

    @Override
    public boolean containsPomDependencies(Path projectRootPath) {
        return internalMap.get(projectRootPath) != null;
    }

    @Override
    public void clearClassloaderResourcesMap() {
        internalMap.clear();
    }

    @Override
    public Boolean removeProjectDeps(Path projectRootPath) {
        return internalMap.remove(projectRootPath) != null;
    }

    @Override
    public void addPomDependencies(Path projectRootPath,
                                   List<String> uris) {
        internalMap.get(projectRootPath).addProjectDeps(uris);
    }

    @Override
    public void addTargetProjectDependencies(Path projectRootPath,
                                             List<String> uris) {
        internalMap.get(projectRootPath).addTargetDeps(uris);
    }

    class Tuple {

        private List<String> targetDeps;
        private List<String> projectsDeps;

        public Tuple() {
            targetDeps = new ArrayList<>();
            projectsDeps = new ArrayList<>();
        }

        public List<String> getTargetDeps() {
            return Collections.unmodifiableList(targetDeps);
        }

        public List<String> getProjectDeps() {
            return Collections.unmodifiableList(projectsDeps);
        }

        public void addTargetDep(String resource) {
            targetDeps.add(resource);
        }

        public void addTargetDeps(List<String> resources) {
            targetDeps.addAll(resources);
        }

        public void addProjectDep(String resource) {
            projectsDeps.add(resource);
        }

        public void addProjectDeps(List<String> resources) {
            projectsDeps.addAll(resources);
        }

        public void replaceTargetDeps(List<String> newTargetDeps) {
            this.targetDeps = newTargetDeps;
        }

        public void replaceProjectsDeps(List<String> newProjectDeps) {
            this.projectsDeps = newProjectDeps;
        }
    }
}
