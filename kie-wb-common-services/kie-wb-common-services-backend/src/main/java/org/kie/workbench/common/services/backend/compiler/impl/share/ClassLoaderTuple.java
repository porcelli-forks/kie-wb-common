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

import java.util.*;

import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

class ClassLoaderTuple {

    private Map<String, byte[]> declaredTypes;

    private List<String> targetDeps;

    private MapClassLoader targetClassloader;

    private ClassLoader dependenciesClassloader;

    public ClassLoaderTuple() {
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

    public void removeTargetClassloader(Path project) {
        this.targetClassloader = null;
    }

    public void removeDependenciesClassloader(Path project) {
        this.dependenciesClassloader = null;
    }

    public void addDependenciesClassloader(ClassLoader dependenciesClassloader) {
        this.dependenciesClassloader = dependenciesClassloader;
    }

    public void replaceTargetDeps(List<String> newTargetDeps) {
        this.targetDeps = newTargetDeps;
    }

    public void addDeclaredTypes(Map<String, byte[]> declaredTypes){
        this.declaredTypes = declaredTypes;
    }

    public Map<String, byte[]> getDeclaredTypes(){
        return Collections.unmodifiableMap(declaredTypes);
    }
}
