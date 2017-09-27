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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class DefaultClassLoaderResourcesMapsHolder implements ClassLoadersResourcesHolder {

    private Map<Path, Tuple> internalMap;

    public DefaultClassLoaderResourcesMapsHolder() {
        internalMap = new ConcurrentHashMap<>();
    }

    @Override
    public List<String> getTargetsProjectDependencies(Path projectRootPath) {
        if (internalMap.get(projectRootPath) != null) {
            return internalMap.get(projectRootPath).getTargetDeps();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void removeTargetClassloader(Path projectRootPath) {
        if (internalMap.get(projectRootPath) != null) {
            internalMap.get(projectRootPath).removeTargetClassloader(projectRootPath);
        }
    }

    @Override
    public List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName) {
        if (internalMap.get(projectRootPath) != null) {
            List<String> allTargetDeps = internalMap.get(projectRootPath).getTargetDeps();
            return CompilerClassloaderUtils.filterClassesByPackage(allTargetDeps, packageName);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void removeDependenciesClassloader(Path projectRootPath) {
        if (internalMap.get(projectRootPath) != null) {
            internalMap.get(projectRootPath).removeDependenciesClassloader(projectRootPath);
        }
    }

    @Override
    public void replaceTargetDependencies(Path projectRootPath,
                                          List<String> uris) {
        if (internalMap.get(projectRootPath) != null) {
            internalMap.get(projectRootPath).replaceTargetDeps(uris);
        }
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
    public void addTargetProjectDependencies(Path projectRootPath,
                                             List<String> uris) {
        if (internalMap.get(projectRootPath) != null) {
            internalMap.get(projectRootPath).addTargetDeps(uris);
        } else {
            Tuple tuple = new Tuple();
            tuple.addTargetDeps(uris);
            internalMap.put(projectRootPath, tuple);
        }
    }

    @Override
    public void addTargetClassLoader(Path project,
                                     MapClassLoader classLoader) {
        if (internalMap.get(project) != null) {
            internalMap.get(project).addTargetClassloader(classLoader);
        } else {
            Tuple tuple = new Tuple();
            tuple.addTargetClassloader(classLoader);
            internalMap.put(project, tuple);
        }
    }

    @Override
    public void addDependenciesClassLoader(Path project,
                                           ClassLoader classLoader) {

        if (internalMap.get(project) != null) {
            internalMap.get(project).addDependenciesClassloader(classLoader);
        } else {
            Tuple tuple = new Tuple();
            tuple.addDependenciesClassloader(classLoader);
            internalMap.put(project, tuple);
        }
    }

    @Override
    public Optional<MapClassLoader> getTargetClassLoader(Path project) {

        if (internalMap.get(project) != null) {
            return internalMap.get(project).getTargetClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClassLoader> getDependenciesClassLoader(Path project) {
        if (internalMap.get(project) != null) {
            return internalMap.get(project).getDependenciesClassloader();
        } else {
            return Optional.empty();
        }
    }

    class Tuple {

        private List<String> targetDeps;
        private MapClassLoader targetClassloader;
        private ClassLoader dependenciesClassloader;

        public Tuple() {
            targetDeps = new ArrayList<>();
        }

        public List<String> getTargetDeps() {
            return Collections.unmodifiableList(targetDeps);
        }


        public Optional<MapClassLoader> getTargetClassloader() {
            return Optional.ofNullable(targetClassloader);
        }

        public Optional<ClassLoader> getDependenciesClassloader() {
            return Optional.ofNullable(dependenciesClassloader);
        }

        public void addTargetDeps(List<String> resources) {
            targetDeps.addAll(resources);
        }

        public void addTargetClassloader(MapClassLoader targetClassloader) {
            this.targetClassloader = targetClassloader;
        }

        public void removeTargetClassloader(Path project){
            this.targetClassloader = null;
        }

        public void removeDependenciesClassloader(Path project){
            this.dependenciesClassloader = null;
        }

        public void addDependenciesClassloader(ClassLoader dependenciesClassloader) {
            this.dependenciesClassloader = dependenciesClassloader;
        }

        public void replaceTargetDeps(List<String> newTargetDeps) {
            this.targetDeps = newTargetDeps;
        }

    }
}
