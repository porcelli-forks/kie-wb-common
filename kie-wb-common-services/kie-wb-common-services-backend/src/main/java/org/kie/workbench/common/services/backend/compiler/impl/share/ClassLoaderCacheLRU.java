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
import java.util.Map;
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
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getTargetDeps();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized void removeTargetClassloader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeTargetClassloader(projectRootPath);
        }
    }

    @Override
    public synchronized List<String> getTargetsProjectDependenciesFiltered(Path projectRootPath, String packageName) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            List<String> allTargetDeps = tuple.getTargetDeps();
            return CompilerClassloaderUtils.filterClassesByPackage(allTargetDeps, packageName);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized void removeDependenciesClassloader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.removeDependenciesClassloader(projectRootPath);
        }
    }

    @Override
    public synchronized void replaceTargetDependencies(Path projectRootPath,
                                                       List<String> uris) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.replaceTargetDeps(uris);
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
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addTargetDeps(uris);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetDeps(uris);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized void addTargetClassLoader(Path projectRootPath, MapClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addTargetClassloader(classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addTargetClassloader(classLoader);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized void addDependenciesClassLoader(Path projectRootPath, ClassLoader classLoader) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addDependenciesClassloader(classLoader);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDependenciesClassloader(classLoader);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized Optional<MapClassLoader> getTargetClassLoader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getTargetClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<ClassLoader> getDependenciesClassLoader(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return tuple.getDependenciesClassloader();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public synchronized void addDeclaredTypes(Path projectRootPath, Map<String, byte[]> store) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            tuple.addDeclaredTypes(store);
        } else {
            tuple = new ClassLoaderTuple();
            tuple.addDeclaredTypes(store);
            setEntry(projectRootPath, tuple);
        }
    }

    @Override
    public synchronized Optional<Map<String, byte[]>> getDeclaredTypes(Path projectRootPath) {
        ClassLoaderTuple tuple = getEntry(projectRootPath);
        if (tuple != null) {
            return Optional.ofNullable(tuple.getDeclaredTypes());
        } else {
            return Optional.empty();
        }
    }
}
