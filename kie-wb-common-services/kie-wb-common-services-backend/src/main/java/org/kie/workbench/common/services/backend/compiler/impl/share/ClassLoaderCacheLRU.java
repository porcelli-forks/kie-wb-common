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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.kie.workbench.common.services.backend.project.MapClassLoader;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Named("LRUClassLoaderCache")
public class ClassLoaderCacheLRU extends LRUCache<Path, ClassLoaderTuple> implements ClassLoaderCache {

    @Override
    public synchronized List<String> getTargetsProjectDependencies(Path projectRootPath) {
        if (getEntry(projectRootPath) != null) {
            return getEntry(projectRootPath).getTargetDeps();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized void removeTargetClassloader(Path projectRootPath) {
        if (getEntry(projectRootPath) != null) {
            getEntry(projectRootPath).removeTargetClassloader(projectRootPath);
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName) {
        if (getEntry(projectRootPath) != null) {
            List<String> allTargetDeps = getEntry(projectRootPath).getTargetDeps();
            return CompilerClassloaderUtils.filterClassesByPackage(allTargetDeps, packageName);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized void removeDependenciesClassloader(Path projectRootPath) {
        if (getEntry(projectRootPath) != null) {
            getEntry(projectRootPath).removeDependenciesClassloader(projectRootPath);
        }
    }

    @Override
    public synchronized void replaceTargetDependencies(Path projectRootPath,
                                                       List<String> uris) {
        if (getEntry(projectRootPath) != null) {
            getEntry(projectRootPath).replaceTargetDeps(uris);
        }
    }

    @Override
    public synchronized boolean containsPomDependencies(Path projectRootPath) {
        return getEntry(projectRootPath) != null;
    }

    @Override
    public synchronized void clearClassloaderResourcesMap() {
        invalidateCache();
    }

    @Override
    public synchronized void removeProjectDeps(Path projectRootPath) {
        invalidateCache(projectRootPath);
    }

    @Override
    public synchronized void addTargetProjectDependencies(Path projectRootPath,
                                                          List<String> uris) {
        if (getEntry(projectRootPath) != null) {
            getEntry(projectRootPath).addTargetDeps(uris);
        } else {
            ClassLoaderTuple tuple = new ClassLoaderTuple();
            tuple.addTargetDeps(uris);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized void addTargetClassLoader(Path project,
                                                  MapClassLoader classLoader) {
        if (getEntry(project) != null) {
            getEntry(project).addTargetClassloader(classLoader);
        } else {
            ClassLoaderTuple tuple = new ClassLoaderTuple();
            tuple.addTargetClassloader(classLoader);
            setEntry(project, tuple);
        }
    }

    @Override
    public synchronized void addDependenciesClassLoader(Path project,
                                                        ClassLoader classLoader) {

        if (getEntry(project) != null) {
            getEntry(project).addDependenciesClassloader(classLoader);
        } else {
            ClassLoaderTuple tuple = new ClassLoaderTuple();
            tuple.addDependenciesClassloader(classLoader);
            setEntry(project, tuple);
        }
    }

    @Override
    public synchronized Optional<MapClassLoader> getTargetClassLoader(Path project) {

        if (getEntry(project) != null) {
            return getEntry(project).getTargetClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<ClassLoader> getDependenciesClassLoader(Path project) {
        if (getEntry(project) != null) {
            return getEntry(project).getDependenciesClassloader();
        } else {
            return Optional.empty();
        }
    }
}
